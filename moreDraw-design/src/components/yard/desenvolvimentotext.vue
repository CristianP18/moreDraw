<template>
    <div class="three-container">
        <!-- Container para a renderização do Three.js -->
        <div
            ref="threeMount"
            class="three-mount"
        />

        <!-- Painel de Ferramentas -->
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
            </div>

            <!-- Botões de Ferramentas -->
            <q-btn
                round
                color="primary"
                icon="send"
                class="tool-btn"
                @click="sendObjectPosition"
            >
                <q-tooltip>Enviar Posição</q-tooltip>
            </q-btn>

            <q-btn
                round
                color="primary"
                icon="mesh_grid"
                class="tool-btn"
                @click="toggleWireframe"
            >
                <q-tooltip>Alternar Wireframe</q-tooltip>
            </q-btn>

            <q-btn
                round
                color="accent"
                icon="grid_on"
                class="tool-btn"
                @click="toggleGrid"
            >
                <q-tooltip>Alternar Grade</q-tooltip>
            </q-btn>

            <!-- Botões para aumentar e diminuir o tamanho do objeto -->
            <q-btn
                round
                color="green"
                icon="add_circle"
                class="tool-btn"
                @click="increaseObjectSize"
            >
                <q-tooltip>Aumentar Objeto</q-tooltip>
            </q-btn>

            <q-btn
                round
                color="red"
                icon="remove_circle"
                class="tool-btn"
                @click="decreaseObjectSize"
            >
                <q-tooltip>Diminuir Objeto</q-tooltip>
            </q-btn>
        </div>

        <!-- Botão para abrir/fechar o card de controladores -->
        <q-btn
            round
            color="blue"
            icon="menu"
            class="toggle-control-btn"
            @click="toggleControlPanel"
        >
            <q-tooltip>Abrir/Fechar Controladores</q-tooltip>
        </q-btn>

        <!-- Card suspenso com os controladores -->
        <div
            v-if="showControlPanel"
            class="control-panel"
        >
            <h4>Controladores</h4>

            <!-- Controlador de Rotação -->
            <div class="rotation-controls">
                <p>Rotação</p>
                <!-- Eixo X -->
                <q-btn
                    round
                    color="blue"
                    icon="rotate_right"
                    class="tool-btn"
                    @click="rotateObject('x', 0.1)"
                >
                    <q-tooltip>Rotacionar Eixo X +</q-tooltip>
                </q-btn>
                <q-btn
                    round
                    color="blue"
                    icon="rotate_left"
                    class="tool-btn"
                    @click="rotateObject('x', -0.1)"
                >
                    <q-tooltip>Rotacionar Eixo X -</q-tooltip>
                </q-btn>
                <!-- Eixo Y -->
                <q-btn
                    round
                    color="blue"
                    icon="rotate_right"
                    class="tool-btn"
                    @click="rotateObject('y', 0.1)"
                >
                    <q-tooltip>Rotacionar Eixo Y +</q-tooltip>
                </q-btn>
                <q-btn
                    round
                    color="blue"
                    icon="rotate_left"
                    class="tool-btn"
                    @click="rotateObject('y', -0.1)"
                >
                    <q-tooltip>Rotacionar Eixo Y -</q-tooltip>
                </q-btn>
                <!-- Eixo Z -->
                <q-btn
                    round
                    color="blue"
                    icon="rotate_right"
                    class="tool-btn"
                    @click="rotateObject('z', 0.1)"
                >
                    <q-tooltip>Rotacionar Eixo Z +</q-tooltip>
                </q-btn>
                <q-btn
                    round
                    color="blue"
                    icon="rotate_left"
                    class="tool-btn"
                    @click="rotateObject('z', -0.1)"
                >
                    <q-tooltip>Rotacionar Eixo Z -</q-tooltip>
                </q-btn>
            </div>

            <!-- Controlador de Posição -->
            <div class="position-controls">
                <p>Posição</p>
                <q-btn
                    round
                    color="orange"
                    icon="arrow_upward"
                    class="tool-btn"
                    @click="moveObject('y', 0.1)"
                >
                    <q-tooltip>Mover para Cima</q-tooltip>
                </q-btn>
                <q-btn
                    round
                    color="orange"
                    icon="arrow_downward"
                    class="tool-btn"
                    @click="moveObject('y', -0.1)"
                >
                    <q-tooltip>Mover para Baixo</q-tooltip>
                </q-btn>
                <q-btn
                    round
                    color="orange"
                    icon="arrow_back"
                    class="tool-btn"
                    @click="moveObject('x', -0.1)"
                >
                    <q-tooltip>Mover para Esquerda</q-tooltip>
                </q-btn>
                <q-btn
                    round
                    color="orange"
                    icon="arrow_forward"
                    class="tool-btn"
                    @click="moveObject('x', 0.1)"
                >
                    <q-tooltip>Mover para Direita</q-tooltip>
                </q-btn>
                <q-btn
                    round
                    color="orange"
                    icon="arrow_upward"
                    class="tool-btn"
                    @click="moveObject('z', 0.1)"
                >
                    <q-tooltip>Mover para Frente</q-tooltip>
                </q-btn>
                <q-btn
                    round
                    color="orange"
                    icon="arrow_downward"
                    class="tool-btn"
                    @click="moveObject('z', -0.1)"
                >
                    <q-tooltip>Mover para Trás</q-tooltip>
                </q-btn>
            </div>

            <!-- Controlador de Escala -->
            <div class="scale-controls">
                <p>Escala</p>
                <q-btn
                    round
                    color="purple"
                    icon="add_circle_outline"
                    class="tool-btn"
                    @click="scaleObject('x', 1.1)"
                >
                    <q-tooltip>Aumentar Escala X</q-tooltip>
                </q-btn>
                <q-btn
                    round
                    color="purple"
                    icon="remove_circle_outline"
                    class="tool-btn"
                    @click="scaleObject('x', 0.9)"
                >
                    <q-tooltip>Diminuir Escala X</q-tooltip>
                </q-btn>
                <q-btn
                    round
                    color="purple"
                    icon="add_circle_outline"
                    class="tool-btn"
                    @click="scaleObject('y', 1.1)"
                >
                    <q-tooltip>Aumentar Escala Y</q-tooltip>
                </q-btn>
                <q-btn
                    round
                    color="purple"
                    icon="remove_circle_outline"
                    class="tool-btn"
                    @click="scaleObject('y', 0.9)"
                >
                    <q-tooltip>Diminuir Escala Y</q-tooltip>
                </q-btn>
                <q-btn
                    round
                    color="purple"
                    icon="add_circle_outline"
                    class="tool-btn"
                    @click="scaleObject('z', 1.1)"
                >
                    <q-tooltip>Aumentar Escala Z</q-tooltip>
                </q-btn>
                <q-btn
                    round
                    color="purple"
                    icon="remove_circle_outline"
                    class="tool-btn"
                    @click="scaleObject('z', 0.9)"
                >
                    <q-tooltip>Diminuir Escala Z</q-tooltip>
                </q-btn>
            </div>
        </div>
    </div>
</template>

<script setup>
import { watch, onMounted, onUnmounted, ref } from 'vue';
import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { OBJLoader } from 'three/examples/jsm/loaders/OBJLoader.js';
import { MTLLoader } from 'three/examples/jsm/loaders/MTLLoader.js';
import { FBXLoader } from 'three/examples/jsm/loaders/FBXLoader.js';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { Notify } from 'quasar';

// Propriedades recebidas pelo componente
const props = defineProps({
    uniqueId: {
        type: Number, // ou String, conforme sua implementação
        required: true,
    },
    modelUrl: {
        type: String,
        default: '',
    },
    showTools: {
        type: Boolean,
        default: false,
    },
});

// Eventos emitidos pelo componente
const emits = defineEmits(['notification', 'toggle-tools', 'update-position']);

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

// Estado para mostrar/ocultar o painel de controladores
const showControlPanel = ref(false);

// Observador para habilitar/desabilitar os controles com base na prop `showTools`
watch(
    () => props.showTools,
    (newVal) => {
        if (controls) {
            controls.enabled = newVal;
            Notify.create({
                message: newVal ? 'Ferramentas ativadas' : 'Ferramentas desativadas',
                color: newVal ? 'positive' : 'negative',
            });
        }
    },
    { immediate: true },
);

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
    controls.enabled = props.showTools; // Controlado pela prop

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
function onWindowResize() {
    if (threeMount.value && camera && renderer) {
        const width = threeMount.value.clientWidth;
        const height = threeMount.value.clientHeight;

        camera.aspect = width / height;
        camera.updateProjectionMatrix();
        renderer.setSize(width, height);
    }
}

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
                    sendObjectPosition(); // Atualizar a posição após carregar
                    Notify.create({ message: 'Modelo 3D carregado com sucesso!', color: 'positive' });
                },
                undefined,
                (error) => {
                    Notify.create({ message: `Falha ao carregar o modelo 3D: ${error.message}`, color: 'negative' });
                    console.error(`Falha ao carregar o modelo 3D: ${error.message}`);
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
                                sendObjectPosition(); // Atualizar a posição após carregar
                                Notify.create({ message: 'Modelo 3D carregado com sucesso!', color: 'positive' });
                            },
                            undefined,
                            (error) => {
                                Notify.create({ message: `Falha ao carregar o modelo 3D: ${error.message}`, color: 'negative' });
                                console.error(`Falha ao carregar o modelo 3D: ${error.message}`);
                            },
                        );
                    },
                    undefined,
                    (error) => {
                        Notify.create({ message: `Falha ao carregar o material: ${error.message}`, color: 'negative' });
                        console.error(`Falha ao carregar o material: ${error.message}`);
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
                        sendObjectPosition(); // Atualizar a posição após carregar
                        Notify.create({ message: 'Modelo 3D carregado com sucesso!', color: 'positive' });
                    },
                    undefined,
                    (error) => {
                        Notify.create({ message: `Falha ao carregar o modelo 3D: ${error.message}`, color: 'negative' });
                        console.error(`Falha ao carregar o modelo 3D: ${error.message}`);
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
                    sendObjectPosition(); // Atualizar a posição após carregar
                    Notify.create({ message: 'Modelo 3D carregado com sucesso!', color: 'positive' });
                },
                undefined,
                (error) => {
                    Notify.create({ message: `Falha ao carregar o modelo 3D: ${error.message}`, color: 'negative' });
                    console.error(`Falha ao carregar o modelo 3D: ${error.message}`);
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

// Função para enviar a posição do objeto 3D
function sendObjectPosition() {
    if (model) {
        const positionData = {
            position: {
                x: model.position.x,
                y: model.position.y,
                z: model.position.z,
            },
            rotation: {
                x: THREE.MathUtils.radToDeg(model.rotation.x),
                y: THREE.MathUtils.radToDeg(model.rotation.y),
                z: THREE.MathUtils.radToDeg(model.rotation.z),
            },
            scale: {
                x: model.scale.x,
                y: model.scale.y,
                z: model.scale.z,
            },
        };
        console.log('Dados da posição enviados:', positionData);
        emits('update-position', positionData);
        Notify.create({ message: 'Dados da posição enviados com sucesso!', color: 'info' });
    } else {
        Notify.create({ message: 'Nenhum modelo 3D carregado.', color: 'negative' });
    }
}

// Funções para controle do objeto

// Função para rotacionar o objeto em um eixo específico
function rotateObject(axis, angle) {
    if (model) {
        if (axis === 'x') model.rotation.x += angle;
        if (axis === 'y') model.rotation.y += angle;
        if (axis === 'z') model.rotation.z += angle;

        // Atualizar a rotação no console para referência
        console.log(`Rotação atual: X:${model.rotation.x}, Y:${model.rotation.y}, Z:${model.rotation.z}`);
        sendObjectPosition(); // Emitir a nova posição/rotação
    }
}

// Função para mover o objeto em um eixo específico
function moveObject(axis, distance) {
    if (model) {
        model.position[axis] += distance;
        sendObjectPosition();
    }
}

// Função para escalar o objeto em um eixo específico
function scaleObject(axis, scale) {
    if (model) {
        model.scale[axis] *= scale;
        sendObjectPosition();
    }
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
        sendObjectPosition();
        Notify.create({ message: 'Objeto aumentado', color: 'green' });
    }
}

function decreaseObjectSize() {
    if (model) {
        model.scale.multiplyScalar(0.9); // Diminui o tamanho do objeto
        sendObjectPosition();
        Notify.create({ message: 'Objeto diminuído', color: 'red' });
    }
}

// Função para abrir o seletor de arquivos
function triggerFileInput() {
    fileInput.value.click();
}

// Função para alternar a visibilidade do card de controladores
function toggleControlPanel() {
    showControlPanel.value = !showControlPanel.value;
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
  }

  /* Estilo do Container Three.js */
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
    background: rgba(255, 255, 255, 0.8);
    padding: 10px;
    border-radius: 8px;
  }

  /* Estilo dos botões redondos */
  .tool-btn {
    width: 40px; /* Aumentar o tamanho do botão para melhor visualização */
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
    width: 16px; /* Tamanho do ícone */
    height: 16px; /* Tamanho do ícone */
  }

  /* Estilo do botão de abrir/fechar o card de controladores */
  .toggle-control-btn {
    position: absolute;
    top: 20px;
    left: 20px; /* Posicionado à esquerda */
    z-index: 10;
  }

  /* Estilo do card de controladores */
  .control-panel {
    position: absolute;
    top: 100px;
    left: 260px; /* Posicionado à esquerda da cena, ajustando conforme necessário */
    z-index: 10;
    width: 220px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 8px;
    padding: 15px;
    box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.2);
    display: flex;
    flex-direction: column;
    gap: 15px;
  }

  /* Título do card de controladores */
  .control-panel h4 {
    margin: 0 0 15px 0;
    font-size: 18px;
    text-align: center;
  }

  /* Estilo das seções dentro do card */
  .rotation-controls,
  .position-controls,
  .scale-controls {
    display: grid;
    grid-template-columns: repeat(2, 1fr); /* Duas colunas */
    gap: 10px;
    align-items: center;
    justify-items: center;
  }

  /* Estilo dos parágrafos nas seções */
  .rotation-controls p,
  .position-controls p,
  .scale-controls p {
    grid-column: 1 / -1; /* Span full width */
    margin: 0 0 5px 0;
    font-size: 14px;
    text-align: center;
    font-weight: bold;
  }

  /* Responsividade */
  @media (max-width: 800px) {
    .control-panel {
      left: 20px; /* Ajustar para telas menores */
      width: 180px;
    }

    .tool-btn {
      width: 35px;
      height: 35px;
    }

    .q-icon {
      width: 14px;
      height: 14px;
    }
  }

  @media (max-width: 500px) {
    .control-panel {
      left: 10px;
      width: 160px;
    }

    .tool-btn {
      width: 30px;
      height: 30px;
    }

    .q-icon {
      width: 12px;
      height: 12px;
    }
  }
  </style>
