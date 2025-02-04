<!-- src/components/yard/View3D.vue -->
<template>
    <div id="threejs-app">
        <!-- Área de Drag-and-Drop -->
        <div
            class="drop-zone"
            @dragover.prevent="handleDragOver"
            @drop.prevent="handleDrop"
            @click="triggerFileInput"
        >
            <p>Arraste e solte uma imagem aqui ou clique para selecionar.</p>
            <input
                ref="fileInput"
                type="file"
                accept=".jpg, .jpeg, .png"
                class="file-input"
                @change="onFileChange"
            >
        </div>

        <!-- Container para a renderização do Three.js -->
        <div
            ref="container"
            class="three-container"
        />

        <!-- Painel de Ferramentas -->
        <div class="tools-panel">
            <!-- Seleção de Ícones -->
            <div class="icon-selection q-mb-sm">
                <p>Selecione um ícone para adicionar:</p>
                <div class="icon-list">
                    <q-btn
                        v-for="icon in icons"
                        :key="icon.name"
                        flat
                        class="icon-button q-mr-sm"
                        @click="selectIcon(icon)"
                    >
                        <i :class="icon.class" />
                    </q-btn>
                </div>
            </div>

            <!-- Botões de Ferramentas -->
            <q-btn
                label="Toggle Wireframe"
                color="primary"
                class="q-mb-sm"
                icon="fas fa-project-diagram"
                @click="toggleWireframe"
            />
            <q-btn
                label="Reset Camera"
                color="secondary"
                class="q-mb-sm"
                icon="fas fa-camera-retro"
                @click="resetCamera"
            />
            <q-btn
                label="Toggle Helpers"
                color="accent"
                class="q-mb-sm"
                icon="fas fa-toggle-on"
                @click="toggleHelpers"
            />
            <q-btn
                label="Change Material"
                color="negative"
                class="q-mb-sm"
                icon="fas fa-paint-brush"
                @click="changeMaterial"
            />
            <div class="q-mb-sm">
                <q-input
                    v-model.number="ambientIntensity"
                    filled
                    label="Ambient Light Intensity"
                    type="number"
                    min="0"
                    max="2"
                    step="0.1"
                    @input="updateAmbientLight"
                />
            </div>
            <div class="q-mb-sm">
                <q-input
                    v-model.number="directionalIntensity"
                    filled
                    label="Directional Light Intensity"
                    type="number"
                    min="0"
                    max="2"
                    step="0.1"
                    @input="updateDirectionalLight"
                />
            </div>
            <q-btn
                label="Add Cube"
                color="green"
                class="q-mb-sm"
                icon="fas fa-cube"
                @click="addCube"
            />
            <q-btn
                label="Add Sphere"
                color="teal"
                class="q-mb-sm"
                icon="fas fa-globe"
                @click="addSphere"
            />
            <q-btn
                label="Remove Selected"
                color="negative"
                class="q-mb-sm"
                icon="fas fa-trash-alt"
                @click="removeSelected"
            />
            <q-btn
                label="Export Scene"
                color="purple"
                class="q-mb-sm"
                icon="fas fa-file-export"
                @click="exportScene"
            />
        </div>

        <!-- Painel de Seleção de Ícones -->
        <div
            v-if="selectedIcon"
            class="selected-icon-panel"
        >
            <p>Ícone Selecionado: {{ selectedIcon.name }}</p>
            <q-btn
                label="Cancelar Seleção"
                color="negative"
                @click="deselectIcon"
            />
        </div>
    </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { GLTFExporter } from 'three/examples/jsm/exporters/GLTFExporter.js';
import { Notify } from 'quasar';

export default {
    name: 'View3D',
    setup() {
        const container = ref(null);
        const fileInput = ref(null);
        let scene; let camera; let renderer; let controls; let mesh; let
            animationId;
        let ambientLight; let
            directionalLight;
        let axesHelper; let
            gridHelper;
        let isWireframe = false;
        let showHelpers = true;
        let currentMaterial = 'standard'; // ou 'basic'
        let selectedObject = null;
        const selectedIcon = ref(null);
        const isPlacingIcon = ref(false);

        const ambientIntensity = ref(0.6);
        const directionalIntensity = ref(0.8);

        // Lista de Ícones Padrão (Font Awesome classes)
        const icons = [
            { name: 'Info', class: 'fas fa-info-circle' },
            { name: 'Warning', class: 'fas fa-exclamation-triangle' },
            { name: 'Error', class: 'fas fa-times-circle' },
            { name: 'Checkmark', class: 'fas fa-check-circle' },
            { name: 'Star', class: 'fas fa-star' },
            { name: 'Location', class: 'fas fa-map-marker-alt' },
        // Adicione mais ícones conforme necessário
        ];

        // Lista de objetos adicionados como ícones
        const placedIcons = ref([]);

        // Inicialização do Three.js
        const initThree = () => {
        // Configuração da cena
            scene = new THREE.Scene();
            scene.background = new THREE.Color(0xdddddd);

            // Configuração da câmera
            const aspect = container.value.clientWidth / container.value.clientHeight;
            camera = new THREE.PerspectiveCamera(75, aspect, 0.1, 1000);
            camera.position.set(0, 2, 5);

            // Configuração do renderizador
            renderer = new THREE.WebGLRenderer({ antialias: true });
            renderer.setSize(container.value.clientWidth, container.value.clientHeight);
            renderer.setPixelRatio(window.devicePixelRatio);
            renderer.outputEncoding = THREE.sRGBEncoding; // Melhor encoding para cores
            container.value.appendChild(renderer.domElement);

            // Adicionando controles de órbita
            controls = new OrbitControls(camera, renderer.domElement);
            controls.enableDamping = true; // Suaviza os movimentos

            // Adicionando iluminação
            ambientLight = new THREE.AmbientLight(0xffffff, ambientIntensity.value);
            scene.add(ambientLight);

            directionalLight = new THREE.DirectionalLight(0xffffff, directionalIntensity.value);
            directionalLight.position.set(5, 10, 7.5);
            scene.add(directionalLight);

            // Adicionando helpers
            axesHelper = new THREE.AxesHelper(5);
            scene.add(axesHelper);

            gridHelper = new THREE.GridHelper(10, 10);
            scene.add(gridHelper);

            // Criando um plano para aplicar a textura
            const geometry = new THREE.PlaneGeometry(5, 5, 32, 32);
            const material = new THREE.MeshStandardMaterial({
                color: 0xffffff,
                side: THREE.DoubleSide, // Garantir que o plano seja visível de ambos os lados
            });
            mesh = new THREE.Mesh(geometry, material);
            mesh.rotation.x = -Math.PI / 2; // Orientar o plano horizontalmente
            scene.add(mesh);

            // Adicionando um Raycaster para seleção de objetos
            window.addEventListener('click', onClick, false);

            // Iniciando a animação
            animate();
        };

        // Função de animação
        const animate = () => {
            animationId = requestAnimationFrame(animate);

            // Atualizando controles
            controls.update();

            // Renderizando a cena
            renderer.render(scene, camera);
        };

        // Manipulação do carregamento de arquivos
        const onFileChange = (e) => {
            const file = e.target.files[0];
            if (file) {
                // Verifique se o arquivo é uma imagem válida
                const validImageTypes = ['image/jpeg', 'image/png', 'image/jpg'];
                if (!validImageTypes.includes(file.type)) {
                    Notify.create({
                        message: 'Por favor, selecione um arquivo de imagem válido (jpg, jpeg, png).',
                        color: 'negative',
                        position: 'top',
                    });
                    return;
                }

                const url = URL.createObjectURL(file);
                console.log('URL da imagem carregada:', url);

                const textureLoader = new THREE.TextureLoader();
                textureLoader.load(
                    url,
                    (texture) => {
                        console.log('Textura carregada com sucesso:', texture);
                        // Aplicando a textura ao material do plano
                        mesh.material.map = texture;
                        mesh.material.needsUpdate = true;
                        console.log('Textura aplicada ao material do plano.');
                        Notify.create({
                            message: 'Textura carregada com sucesso!',
                            color: 'positive',
                            position: 'top',
                        });
                    },
                    undefined,
                    (err) => {
                        console.error('Erro ao carregar a textura:', err);
                        Notify.create({
                            message: 'Erro ao carregar a textura.',
                            color: 'negative',
                            position: 'top',
                        });
                    },
                );
            }
        };

        // Manipulação de Drag-and-Drop
        const handleDragOver = (e) => {
            e.preventDefault();
            e.stopPropagation();
            e.dataTransfer.dropEffect = 'copy';
        };

        const handleDrop = (e) => {
            e.preventDefault();
            e.stopPropagation();
            const file = e.dataTransfer.files[0];
            if (file) {
                // Simular clique no input de arquivo
                fileInput.value.files = e.dataTransfer.files;
                onFileChange({ target: fileInput.value });
            }
        };

        // Ajuste dinâmico do tamanho do renderizador
        const onWindowResize = () => {
            if (container.value && camera && renderer) {
                const width = container.value.clientWidth;
                const height = container.value.clientHeight;

                camera.aspect = width / height;
                camera.updateProjectionMatrix();

                renderer.setSize(width, height);
            }
        };

        // Toggle Wireframe
        const toggleWireframe = () => {
            isWireframe = !isWireframe;
            mesh.material.wireframe = isWireframe;
            Notify.create({
                message: isWireframe ? 'Wireframe ativado' : 'Wireframe desativado',
                color: isWireframe ? 'primary' : 'secondary',
                position: 'top',
            });
        };

        // Resetar câmera para posição inicial
        const resetCamera = () => {
            camera.position.set(0, 2, 5);
            controls.reset();
            Notify.create({
                message: 'Câmera resetada',
                color: 'info',
                position: 'top',
            });
        };

        // Toggle Helpers
        const toggleHelpers = () => {
            showHelpers = !showHelpers;
            axesHelper.visible = showHelpers;
            gridHelper.visible = showHelpers;
            Notify.create({
                message: showHelpers ? 'Helpers ativados' : 'Helpers desativados',
                color: showHelpers ? 'primary' : 'secondary',
                position: 'top',
            });
        };

        // Change Material
        const changeMaterial = () => {
            if (currentMaterial === 'standard') {
                mesh.material = new THREE.MeshBasicMaterial({
                    color: 0xffffff,
                    map: mesh.material.map,
                    side: THREE.DoubleSide,
                });
                currentMaterial = 'basic';
                Notify.create({
                    message: 'Material alterado para MeshBasicMaterial',
                    color: 'warning',
                    position: 'top',
                });
            } else {
                mesh.material = new THREE.MeshStandardMaterial({
                    color: 0xffffff,
                    map: mesh.material.map,
                    side: THREE.DoubleSide,
                });
                currentMaterial = 'standard';
                Notify.create({
                    message: 'Material alterado para MeshStandardMaterial',
                    color: 'info',
                    position: 'top',
                });
            }
            mesh.material.needsUpdate = true;
        };

        // Atualizar Intensidade da Luz Ambiente
        const updateAmbientLight = () => {
            ambientLight.intensity = ambientIntensity.value;
        };

        // Atualizar Intensidade da Luz Direcional
        const updateDirectionalLight = () => {
            directionalLight.intensity = directionalIntensity.value;
        };

        // Adicionar Cubo
        const addCube = () => {
            const geometry = new THREE.BoxGeometry();
            const material = new THREE.MeshStandardMaterial({ color: Math.random() * 0xffffff });
            const cube = new THREE.Mesh(geometry, material);
            cube.position.set(Math.random() * 5 - 2.5, 0.5, Math.random() * 5 - 2.5);
            scene.add(cube);
            Notify.create({
                message: 'Cubo adicionado',
                color: 'positive',
                position: 'top',
            });
        };

        // Adicionar Esfera
        const addSphere = () => {
            const geometry = new THREE.SphereGeometry(0.5, 32, 32);
            const material = new THREE.MeshStandardMaterial({ color: Math.random() * 0xffffff });
            const sphere = new THREE.Mesh(geometry, material);
            sphere.position.set(Math.random() * 5 - 2.5, 0.5, Math.random() * 5 - 2.5);
            scene.add(sphere);
            Notify.create({
                message: 'Esfera adicionada',
                color: 'positive',
                position: 'top',
            });
        };

        // Seleção de Ícones
        const selectIcon = (icon) => {
            selectedIcon.value = icon;
            isPlacingIcon.value = true;
            Notify.create({
                message: `Ícone selecionado: ${icon.name}. Clique na cena para adicioná-lo.`,
                color: 'info',
                position: 'top',
            });
        };

        // Limpar Seleção de Ícone
        const deselectIcon = () => {
            selectedIcon.value = null;
            isPlacingIcon.value = false;
            Notify.create({
                message: 'Seleção de ícone cancelada.',
                color: 'warning',
                position: 'top',
            });
        };

        // Adicionar Ícone na Cena
        const addIcon = (icon, position) => {
            const spriteMaterial = new THREE.SpriteMaterial({
                map: new THREE.TextureLoader().load(`/icons/${icon.name.toLowerCase()}.png`),
                transparent: true,
            });
            const sprite = new THREE.Sprite(spriteMaterial);
            sprite.scale.set(1, 1, 1); // Ajuste o tamanho conforme necessário
            sprite.position.copy(position);
            sprite.userData = {
                isIcon: true,
                name: icon.name,
            };
            placedIcons.value.push(sprite);
            scene.add(sprite);
            Notify.create({
                message: `Ícone '${icon.name}' adicionado na cena.`,
                color: 'positive',
                position: 'top',
            });
        };

        // Manipulação de Click na Cena
        const raycaster = new THREE.Raycaster();
        const mouse = new THREE.Vector2();

        const onClick = (event) => {
        // Calcular posição do mouse
            const rect = renderer.domElement.getBoundingClientRect();
            mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
            mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;

            raycaster.setFromCamera(mouse, camera);
            const intersects = raycaster.intersectObjects(scene.children, true);

            if (isPlacingIcon.value && selectedIcon.value) {
                if (intersects.length > 0) {
                    const intersect = intersects.find((i) => i.object === mesh);
                    if (intersect) {
                        const position = intersect.point.clone();
                        addIcon(selectedIcon.value, position);
                        isPlacingIcon.value = false;
                        selectedIcon.value = null;
                    }
                }
            } else {
                if (intersects.length > 0) {
                    const firstIntersect = intersects[0].object;

                    // Verifica se o objeto é um ícone
                    if (firstIntersect.userData.isIcon) {
                        if (selectedObject) {
                            selectedObject.material.opacity = 1;
                        }
                        selectedObject = firstIntersect;
                        selectedObject.material.opacity = 0.7; // Destaca o ícone selecionado
                        Notify.create({
                            message: `Ícone selecionado: ${selectedObject.userData.name}`,
                            color: 'info',
                            position: 'top',
                        });
                    } else {
                        // Se clicou em outro objeto, deseleciona o ícone atual
                        if (selectedObject) {
                            selectedObject.material.opacity = 1;
                            selectedObject = null;
                            Notify.create({
                                message: 'Nenhum ícone selecionado.',
                                color: 'warning',
                                position: 'top',
                            });
                        }
                    }
                } else {
                    // Se não houver interseção, deseleciona qualquer objeto selecionado
                    if (selectedObject) {
                        selectedObject.material.opacity = 1;
                        selectedObject = null;
                        Notify.create({
                            message: 'Nenhum ícone selecionado.',
                            color: 'warning',
                            position: 'top',
                        });
                    }
                }
            }
        };

        // Remover Objeto Selecionado
        const removeSelected = () => {
            if (selectedObject && selectedObject !== mesh) {
                scene.remove(selectedObject);
                placedIcons.value = placedIcons.value.filter((obj) => obj !== selectedObject);
                Notify.create({
                    message: 'Ícone removido.',
                    color: 'negative',
                    position: 'top',
                });
                selectedObject = null;
            } else {
                Notify.create({
                    message: 'Nenhum ícone selecionado para remover.',
                    color: 'warning',
                    position: 'top',
                });
            }
        };

        // Exportar Cena
        const exportScene = () => {
            const exporter = new GLTFExporter();
            exporter.parse(
                scene,
                (result) => {
                    const output = JSON.stringify(result, null, 2);
                    const blob = new Blob([output], { type: 'application/json' });
                    const link = document.createElement('a');
                    link.href = URL.createObjectURL(blob);
                    link.download = 'scene.gltf';
                    link.click();
                    Notify.create({
                        message: 'Cena exportada como scene.gltf',
                        color: 'positive',
                        position: 'top',
                    });
                },
                (error) => {
                    console.error('Erro ao exportar a cena:', error);
                    Notify.create({
                        message: 'Erro ao exportar a cena.',
                        color: 'negative',
                        position: 'top',
                    });
                },
            );
        };

        // Trigger do Input de Arquivo
        const triggerFileInput = () => {
            fileInput.value.click();
        };

        onMounted(() => {
            initThree();
            window.addEventListener('resize', onWindowResize);
            window.addEventListener('click', onClick, false);
        });

        onBeforeUnmount(() => {
            cancelAnimationFrame(animationId);
            window.removeEventListener('resize', onWindowResize);
            window.removeEventListener('click', onClick, false);
            if (controls) controls.dispose();
            if (renderer) {
                renderer.dispose();
                container.value.removeChild(renderer.domElement);
            }
        });

        return {
            container,
            fileInput,
            onFileChange,
            handleDragOver,
            handleDrop,
            toggleWireframe,
            resetCamera,
            toggleHelpers,
            changeMaterial,
            ambientIntensity,
            directionalIntensity,
            updateAmbientLight,
            updateDirectionalLight,
            addCube,
            addSphere,
            removeSelected,
            exportScene,
            icons,
            selectedIcon,
            selectIcon,
            deselectIcon,
            triggerFileInput,
        };
    },
};
</script>

  <style scoped>
  #threejs-app {
    position: relative;
    width: 100%;
    height: 100vh;
    overflow: hidden;
  }

  /* Estilo da Área de Drag-and-Drop */
  .drop-zone {
    position: absolute;
    top: 20px;
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
  .three-container {
    margin-top: 15px;
    margin-left: 20px;
    width: 80%;
    height: 75%;
    display: block;
  }

  /* Estilo do Painel de Ferramentas */
  .tools-panel {
    position: absolute;
    top: 20px;
    right: 20px;
    z-index: 10;
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  /* Estilo da Seleção de Ícones */
  .icon-selection p {
    margin: 0 0 5px 0;
    font-weight: bold;
  }

  .icon-list {
    display: flex;
    flex-wrap: wrap;
    gap: 5px;
  }

  .icon-button {
    width: 40px;
    height: 40px;
    padding: 0;
    border: none;
    background: none;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .icon-button i {
    font-size: 24px;
    color: #555;
  }

  .icon-button:hover i {
    color: #000;
  }

  /* Estilo do Painel de Seleção de Ícones */
  .selected-icon-panel {
    position: absolute;
    bottom: 20px;
    right: 20px;
    z-index: 10;
    background: rgba(255, 255, 255, 0.9);
    padding: 10px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    gap: 10px;
  }
  </style>
