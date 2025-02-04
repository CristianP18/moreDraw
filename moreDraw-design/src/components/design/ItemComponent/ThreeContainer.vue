<template>
    <div class="three-container">
        <!-- Container para a renderização do Three.js -->
        <div
            ref="threeMount"
            class="three-mount"
        />

        <!-- Painel de Ferramentas -->
        <!-- Adicionamos v-if="showTools" para controlar a visibilidade -->
        <div
            v-if="showTools"
            class="tools-panel"
        >
            <!-- Área de Drag-and-Drop -->
            <div
                class="drop-zone"
                @dragover.prevent="handleDragOver"
                @drop.prevent="handleDrop"
                @click="triggerFileInput"
            >
                <p>Arraste e solte um modelo 3D aqui ou clique para selecionar.</p>
                <input
                    ref="fileInput"
                    type="file"
                    accept=".gltf, .glb, .obj, .fbx, .mtl, .jpg, .png"
                    multiple
                    class="file-input"
                    @change="onFileInputChange"
                    @click.stop
                >
                >
            </div>
            <q-btn
                round
                color="secondary"
                icon="fas fa-camera-retro"
                class="tool-btn"
                @click="resetCamera"
            >
                <q-tooltip>Câmera Resetar</q-tooltip>
            </q-btn>

            <q-btn
                round
                color="primary"
                icon="fas fa-project-diagram"
                class="tool-btn"
                @click="toggleWireframe"
            >
                <q-tooltip>Alternar Wireframe</q-tooltip>
            </q-btn>

            <q-btn
                round
                color="accent"
                icon="fas fa-border-all"
                class="tool-btn"
                @click="toggleGrid"
            >
                <q-tooltip>Alternar Grade</q-tooltip>
            </q-btn>

            <!-- Botões para aumentar e diminuir o tamanho do objeto -->
            <q-btn
                round
                color="green"
                icon="fas fa-plus-circle"
                class="tool-btn"
                @click="increaseObjectSize"
            >
                <q-tooltip>Aumentar Objeto</q-tooltip>
            </q-btn>

            <q-btn
                round
                color="red"
                icon="fas fa-minus-circle"
                class="tool-btn"
                @click="decreaseObjectSize"
            >
                <q-tooltip>Diminuir Objeto</q-tooltip>
            </q-btn>
        </div>
        <!-- Botão de Ferramentas, visível dependendo de showBtnTools -->
        <div
            v-if="showBtnTools.showTools"
            class="tools-panel"
        >
            <div class="toggle-tools">
                <q-btn
                    round
                    color="grey-4"
                    icon="fas fa-sliders-h"
                    class="toggle-btn"
                    @click="toggleToolsVisibility"
                >
                    <q-tooltip>Alternar Painel de Ferramentas</q-tooltip>
                </q-btn>
            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue';
import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { OBJLoader } from 'three/examples/jsm/loaders/OBJLoader.js';
import { MTLLoader } from 'three/examples/jsm/loaders/MTLLoader.js';
import { FBXLoader } from 'three/examples/jsm/loaders/FBXLoader.js';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { Notify } from 'quasar';
import { useModelStore } from 'src/stores/mode3D';

// Propriedades recebidas pelo componente
const props = defineProps({
    modelUrl: String,
    position: Object,
    rotation: Object,
    scale: Object,
});

// Eventos emitidos pelo componente
const emits = defineEmits(['notification']);

// Referências aos elementos do DOM
const threeMount = ref(null);
const fileInput = ref(null);

// Variáveis do Three.js
let scene;
let camera;
let renderer;
let controls;
let model;

// Variáveis de controle
let isWireframe = false;
let gridHelper;
let ambientLight;
let directionalLight;

// Variável para controlar a visibilidade dos botões de comando
const showTools = ref(true);

// Instancia a store
const showBtnTools = useModelStore();

// Função chamada quando o componente é montado
onMounted(() => {
    initThree();
    if (props.modelUrl) {
        loadModel(props.modelUrl);
    }
    window.addEventListener('resize', onWindowResize);
});

// Função chamada quando o componente é desmontado
onUnmounted(() => {
    if (renderer) {
        renderer.dispose();
    }
    window.removeEventListener('resize', onWindowResize);
});

// Inicialização do Three.js
function initThree() {
    // Configuração da cena
    scene = new THREE.Scene();
    scene.background = null; // Fundo transparente por padrão

    // Configuração da câmera
    const aspect = threeMount.value.clientWidth / threeMount.value.clientHeight;
    camera = new THREE.PerspectiveCamera(75, aspect, 0.1, 1000);
    camera.position.set(0, 2, 5);

    // Configuração do renderizador
    renderer = new THREE.WebGLRenderer({ alpha: true, antialias: true });
    renderer.setSize(threeMount.value.clientWidth, threeMount.value.clientHeight);
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.setClearColor(0x000000, 0); // Fundo transparente
    threeMount.value.appendChild(renderer.domElement);

    // Controles de órbita
    controls = new OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true;
    controls.enableZoom = true; // Ativa o zoom com a roda do mouse
    controls.zoomSpeed = 1.0; // Ajuste a velocidade do zoom conforme necessário

    // Adicionando iluminação
    ambientLight = new THREE.AmbientLight(0xffffff, 1);
    scene.add(ambientLight);

    directionalLight = new THREE.DirectionalLight(0xffffff, 1);
    directionalLight.position.set(0, 10, 10);
    scene.add(directionalLight);

    // Adicionando grade
    gridHelper = new THREE.GridHelper(10, 10);
    scene.add(gridHelper);

    // Animação
    animate();
}

// Função de animação
function animate() {
    requestAnimationFrame(animate);
    controls.update();
    renderer.render(scene, camera);
}

// Ajuste dinâmico do tamanho do renderizador
const onWindowResize = () => {
    if (threeMount.value && camera && renderer) {
        const width = threeMount.value.clientWidth;
        const height = threeMount.value.clientHeight;

        camera.aspect = width / height;
        camera.updateProjectionMatrix();
        renderer.setSize(width, height);
    }
};

// Funções de Drag-and-Drop
function handleDragOver(event) {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'copy';
}

function handleDrop(event) {
    event.preventDefault();
    const files = event.dataTransfer.files;
    if (files.length > 0) {
        handleFiles(files);
    }
}

function onFileInputChange(event) {
    const files = event.target.files;
    if (files.length > 0) {
        handleFiles(files);
    }
}

// Função para processar os arquivos selecionados
function handleFiles(files) {
    const fileArray = Array.from(files);
    const fileMap = {};

    // Mapear os arquivos por nome (convertido para minúsculas)
    fileArray.forEach((file) => {
        fileMap[file.name.toLowerCase()] = file;
    });

    // Identificar o arquivo principal (e.g., .obj, .gltf, .glb, .fbx)
    let mainFile;
    const extensions = ['obj', 'gltf', 'glb', 'fbx'];
    for (const ext of extensions) {
        mainFile = fileArray.find((file) => file.name.toLowerCase().endsWith(`.${ext}`));
        if (mainFile) break;
    }

    if (!mainFile) {
        Notify.create({ message: 'Nenhum arquivo de modelo 3D suportado encontrado.', color: 'negative' });
        return;
    }

    const url = URL.createObjectURL(mainFile);
    const extension = mainFile.name.split('.').pop().toLowerCase();

    loadModel(url, extension, fileMap, mainFile);
}

// Função para carregar o modelo 3D
function loadModel(url, extension, fileMap, mainFile) {
    const manager = new THREE.LoadingManager();
    const blobURLs = [];

    // Objeto para rastrear o status de cada arquivo
    const fileStatus = {};

    // Modificar o URLModifier para lidar com caminhos complexos
    manager.setURLModifier((requestedURL) => {
        // Registrar o URL original
        console.log(`Solicitado URL original: ${requestedURL}`);

        // Extrair o nome do arquivo ignorando qualquer caminho
        const urlParts = requestedURL.split(/[\\/]/);
        const fileName = urlParts[urlParts.length - 1].toLowerCase();

        // Registrar o nome do arquivo extraído
        console.log(`Nome do arquivo extraído: ${fileName}`);

        // Inicializar o status do arquivo como "carregando"
        fileStatus[fileName] = 'carregando';
        Notify.create({ message: `Carregando arquivo: ${fileName}`, color: 'info' });

        const file = fileMap[fileName];
        if (file) {
            const blobUrl = URL.createObjectURL(file);
            blobURLs.push(blobUrl);
            console.log(`Blob URL para ${fileName}: ${blobUrl}`);
            return blobUrl;
        }

        // Se o arquivo não for encontrado no fileMap
        Notify.create({ message: `Arquivo não encontrado: ${fileName}`, color: 'warning' });
        console.warn(`Arquivo não encontrado no fileMap: ${fileName}`);

        return requestedURL;
    });

    // Adicionar callbacks do LoadingManager
    manager.onLoad = function () {
        // Atualizar o status de todos os arquivos para "carregado"
        for (const fileName in fileStatus) {
            if (fileStatus[fileName] === 'carregando') {
                fileStatus[fileName] = 'carregado';
                Notify.create({ message: `Arquivo carregado com sucesso: ${fileName}`, color: 'positive' });
            }
        }
        // Limpeza após o carregamento
        cleanup();
    };

    manager.onError = function (requestedURL) {
        const urlParts = requestedURL.split(/[\\/]/);
        const fileName = urlParts[urlParts.length - 1].toLowerCase();
        fileStatus[fileName] = 'erro';
        Notify.create({ message: `Erro ao carregar arquivo: ${fileName}`, color: 'negative' });
        console.error(`Erro ao carregar arquivo: ${fileName}`);
    };

    let loader;
    switch (extension) {
        case 'gltf':
        case 'glb':
            loader = new GLTFLoader(manager);
            loader.load(
                url,
                (gltf) => {
                    if (model) {
                        scene.remove(model);
                    }
                    model = gltf.scene;
                    scene.add(model);
                    centerModel(model);
                    emits('notification', { message: 'Modelo 3D carregado com sucesso!', type: 'success' });
                    Notify.create({ message: 'Modelo 3D carregado com sucesso!', color: 'positive' });
                },
                undefined,
                (error) => {
                    emits('notification', { message: `Falha ao carregar o modelo 3D: ${error.message}`, type: 'error' });
                    Notify.create({ message: `Falha ao carregar o modelo 3D: ${error.message}`, color: 'negative' });
                },
            );
            break;
        case 'obj': {
            loader = new OBJLoader(manager);
            const mtlFileName = mainFile.name.replace(/\.obj$/i, '.mtl').toLowerCase();
            const mtlFile = fileMap[mtlFileName];
            if (mtlFile) {
                const mtlLoader = new MTLLoader(manager);
                const mtlUrl = URL.createObjectURL(mtlFile);
                blobURLs.push(mtlUrl);

                // Definir o resourcePath como string vazia para ignorar caminhos adicionais
                mtlLoader.setResourcePath('');

                mtlLoader.load(
                    mtlUrl,
                    (materials) => {
                        materials.preload();
                        loader.setMaterials(materials);

                        // Definir o path do OBJLoader como string vazia para ignorar caminhos adicionais
                        loader.setPath('');

                        loader.load(
                            url,
                            (object) => {
                                if (model) {
                                    scene.remove(model);
                                }
                                model = object;
                                scene.add(model);
                                centerModel(model);
                                emits('notification', { message: 'Modelo 3D carregado com sucesso!', type: 'success' });
                                Notify.create({ message: 'Modelo 3D carregado com sucesso!', color: 'positive' });
                            },
                            undefined,
                            (error) => {
                                emits('notification', { message: `Falha ao carregar o modelo 3D: ${error.message}`, type: 'error' });
                                Notify.create({ message: `Falha ao carregar o modelo 3D: ${error.message}`, color: 'negative' });
                            },
                        );
                    },
                    undefined,
                    (error) => {
                        emits('notification', { message: `Falha ao carregar o material: ${error.message}`, type: 'error' });
                        Notify.create({ message: `Falha ao carregar o material: ${error.message}`, color: 'negative' });
                    },
                );
            } else {
                loader.load(
                    url,
                    (object) => {
                        if (model) {
                            scene.remove(model);
                        }
                        model = object;
                        scene.add(model);
                        centerModel(model);
                        emits('notification', { message: 'Modelo 3D carregado com sucesso!', type: 'success' });
                        Notify.create({ message: 'Modelo 3D carregado com sucesso!', color: 'positive' });
                    },
                    undefined,
                    (error) => {
                        emits('notification', { message: `Falha ao carregar o modelo 3D: ${error.message}`, type: 'error' });
                        Notify.create({ message: `Falha ao carregar o modelo 3D: ${error.message}`, color: 'negative' });
                    },
                );
            }
            break;
        }
        case 'fbx':
            loader = new FBXLoader(manager);
            loader.load(
                url,
                (object) => {
                    if (model) {
                        scene.remove(model);
                    }
                    model = object;
                    scene.add(model);
                    centerModel(model);
                    emits('notification', { message: 'Modelo 3D carregado com sucesso!', type: 'success' });
                    Notify.create({ message: 'Modelo 3D carregado com sucesso!', color: 'positive' });
                },
                undefined,
                (error) => {
                    emits('notification', { message: `Falha ao carregar o modelo 3D: ${error.message}`, type: 'error' });
                    Notify.create({ message: `Falha ao carregar o modelo 3D: ${error.message}`, color: 'negative' });
                },
            );
            break;
        default:
            Notify.create({ message: 'Formato de arquivo não suportado', color: 'negative' });
            cleanup();
    }

    function cleanup() {
        // Revogar todos os Blob URLs criados
        blobURLs.forEach((blobUrl) => URL.revokeObjectURL(blobUrl));
        URL.revokeObjectURL(url); // Revogar o URL do arquivo principal
        blobURLs.length = 0; // Limpar o array
    }
}

// Função para centralizar o modelo na cena
function centerModel(model) {
    // Calcular os limites do modelo
    const box = new THREE.Box3().setFromObject(model);
    const center = box.getCenter(new THREE.Vector3());
    const size = box.getSize(new THREE.Vector3());

    // Centralizar o modelo na origem
    model.position.x += model.position.x - center.x;
    model.position.y += model.position.y - center.y;
    model.position.z += model.position.z - center.z;

    // Ajustar a câmera para enquadrar o modelo
    const maxDim = Math.max(size.x, size.y, size.z);
    const fov = camera.fov * (Math.PI / 180);
    let cameraZ = Math.abs(maxDim / 2 / Math.tan(fov / 2));

    cameraZ *= 2; // Adicionar espaço extra ao redor do modelo
    camera.position.z = cameraZ;

    // Atualizar planos de corte da câmera
    camera.near = cameraZ / 100;
    camera.far = cameraZ * 100;
    camera.updateProjectionMatrix();

    // Atualizar controles
    controls.maxDistance = cameraZ * 10;
    controls.target.set(0, 0, 0);
    controls.update();
}

// Funções de controle

// Função para resetar a câmera
function resetCamera() {
    camera.position.set(0, 2, 5);
    controls.reset();
    Notify.create({ message: 'Câmera resetada', color: 'info' });
}

// Função para alternar o modo wireframe
function toggleWireframe() {
    isWireframe = !isWireframe;
    if (model) {
        model.traverse((child) => {
            if (child.isMesh) {
                child.material.wireframe = isWireframe;
                child.material.needsUpdate = true;
            }
        });
    }
    Notify.create({
        message: isWireframe ? 'Wireframe ativado' : 'Wireframe desativado',
        color: isWireframe ? 'primary' : 'secondary',
    });
}

// Função para alternar a visibilidade da grade
function toggleGrid() {
    gridHelper.visible = !gridHelper.visible;
    Notify.create({
        message: gridHelper.visible ? 'Grade ativada' : 'Grade desativada',
        color: gridHelper.visible ? 'primary' : 'secondary',
    });
}

// Funções para aumentar e diminuir o tamanho do objeto
function increaseObjectSize() {
    if (model) {
        model.scale.multiplyScalar(1.1); // Aumenta o tamanho do objeto
        Notify.create({ message: 'Objeto aumentado', color: 'green' });
    }
}

function decreaseObjectSize() {
    if (model) {
        model.scale.multiplyScalar(0.9); // Diminui o tamanho do objeto
        Notify.create({ message: 'Objeto diminuído', color: 'red' });
    }
}

// Função para abrir o seletor de arquivos
function triggerFileInput() {
    fileInput.value.click();
}

// Função para alternar a visibilidade dos botões de comando
function toggleToolsVisibility() {
    showTools.value = !showTools.value;
    showBtnTools.toggleToolsVisibility(); // Assumindo que esta função está definida na store
    Notify.create({
        message: showTools.value ? 'Painel de Ferramentas Visível' : 'Painel de Ferramentas Oculto',
        color: showTools.value ? 'positive' : 'negative',
    });
}
</script>

<style scoped>
.three-container {
    width: 100%;
    height: 100vh;
    background-color: transparent;
    position: relative;
}

/* Estilo da Área de Drag-and-Drop */
.drop-zone {
    position: absolute;
    top: -50px;
    left: 20px;
    z-index: 5;
    width: 200px;
    height: 50px;
    border: 2px dashed #ccc;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.5);
    cursor: pointer;
}

.drop-zone p {
    margin: 0;
    pointer-events: none;
}

/* Estilo do Input de Arquivo */
.file-input {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    opacity: 0;
    cursor: pointer;
    /* Adicionado para garantir que o input não capture cliques que deveriam ir para a drop-zone */
    /* pointer-events: none; */
}

.three-mount {
    width: 250px;
    height: 280px;
    position: relative;
}

/* Estilo do Painel de Ferramentas */
.tools-panel {
    position: absolute;
    top: 60px;
    left: 0px;
    z-index: 10;
    display: flex;
    flex-direction: column;
    gap: 10px;
}

/* Estilo dos botões redondos */
.tool-btn {
    width: 40px;  /* Aumentar o tamanho do botão para melhor visualização */
    height: 40px; /* Aumentar o tamanho do botão para melhor visualização */
    border-radius: 50%;
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
}

/* Aumentar o tamanho dos ícones dentro dos botões */
.tool-btn i {
    font-size: 16px; /* Aumentar o tamanho do ícone para melhor visualização */
}

/* Ajustes específicos para manter o tamanho do ícone centralizado no botão */
.q-icon {
    width: 16px;  /* Tamanho do ícone */
    height: 16px; /* Tamanho do ícone */
}

/* Estilo do botão de alternar ferramentas */
.toggle-tools {
    position: absolute;
    top: 20px;
    right: 20px;
    z-index: 5;
}

.toggle-btn {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
}

/* Estilo da Notificação */
.notification {
    position: absolute;
    bottom: 10px;
    left: 50%;
    transform: translateX(-50%);
    padding: 10px;
    background: rgba(0, 123, 255, 0.9);
    color: white;
    border-radius: 5px;
    z-index: 10;
}
</style>
