<template>
    <!-- Mostra o nome do projeto selecionado -->
    <q-card
        v-if="selectedProjectName"
        class="q-ma-md project-name-box"
    >
        <div class="q-pa-sm">
            <strong>Projeto selecionado:</strong> {{ selectedProjectName }}
        </div>
    </q-card>

    <q-page class="page q-px-lg">
        <q-card class="q-pa-md dropzone-card">
            <!-- Barra de Ferramentas -->
            <ToolbarComponent
                :can-undo="canUndo"
                :can-redo="canRedo"
                :drawing-mode="drawingMode"
                @add-item="addNewItem"
                @set-drawing-mode="setDrawingMode"
                @undo="undo"
                @redo="redo"
                @save-design-cut="openCropModal"
                @save-design="saveDesignAsImage"
                @activate-paint-mode="togglePaintMode"
                @add-image="handleImageUploadInDrawingArea"
                @add-image-background="handleImageUploadInItemBackground"
                @clean-items="cleanItems"
                @save-project="saveProject"
                @open-project="openProject"
                @update-project="updateProject"
            />

            <!-- Área de Desenho -->
            <DrawingAreaComponent
                ref="drawingAreaRef"
                :items="items"
                :current-drawing="currentDrawing"
                :drawing-mode="drawingMode"
                :show-grid="showGrid"
                :grid-size="gridSize"
                :paint-mode="isPaintMode"
                :paint-type="paintMode"
                :selected-color="selectedColor"
                :selected-item="selectedItem"
                :show-controllers="showControllers"
                :controller-mode="controllerMode"
                :controller-count="controllerCount"
                :is-configurable="isConfigurable"
                :background-image-url="imageBackgrondUrl"
                @update-controller-mode="updateControllerMode"
                @update-controller-count="updateControllerCount"
                @update-rounded-line="updateRoundedLine"
                @update-items="updateItems"
                @select-item="selectItem"
                @bring-to-front="handleBringToFront"
                @send-to-back="handleSendToBack"
                @save-state="saveState"
                @set-drawing-mode="setDrawingMode"
                @add-item="addItem"
                @add-image="addImageToCanvas"
                @toggle-controllers="toggleControllers"
                @change-controller-mode="changeControllerMode"
            />

            <!-- Componente de Controladores -->
            <GeometryController
                :selected-item="selectedItem"
                :controller-mode="controllerMode"
                :show-controllers="showControllers"
                @update-item="updateItems"
            />

            <!-- Painel de Controle -->
            <ControlPanelComponent
                :selected-item="selectedItem"
                :show-grid="showGrid"
                @update-item="updateItem"
                @toggle-grid="toggleGrid"
            />
        </q-card>

        <!-- Componente de Notificação -->
        <div
            v-if="notification"
            :class="['notification', notification.type]"
        >
            {{ notification.message }}
        </div>

        <!-- Seletor de Cor para Pintura -->
        <q-dialog
            v-model="isColorPickerOpen"
            persistent
        >
            <q-card>
                <q-card-section class="q-pt-none">
                    <q-color
                        v-model="selectedColor"
                        label="Selecione uma Cor para Pintar"
                    />
                    <!-- Adição de seleção de modo de pintura -->
                    <div class="q-mt-md">
                        <q-option-group
                            v-model="paintMode"
                            :options="[
                                { label: 'Padrão', value: 'standard' },
                                { label: 'Paint', value: 'paint' }
                            ]"
                            type="radio"
                            inline
                        />
                    </div>
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn
                        flat
                        label="Cancelar"
                        color="negative"
                        @click="closeColorPicker"
                    />
                    <q-btn
                        flat
                        label="Confirmar"
                        color="primary"
                        @click="confirmColor"
                    />
                </q-card-actions>
            </q-card>
        </q-dialog>

        <!-- Modal de Crop Image (com pré-visualização opcional) -->
        <q-dialog
            v-model="showCropModal"
            persistent
            maximized
        >
            <q-card class="crop-modal">
                <q-card-section class="crop-modal-content">
                    <!-- Exemplo de pré-visualização no recorte -->
                    <div class="crop-preview">
                        <img
                            v-if="cropPreviewSrc"
                            :src="cropPreviewSrc"
                            alt="Pré-visualização do recorte"
                            class="crop-preview-image"
                        >
                    </div>

                    <vue-draggable-resizable
                        :w="windowWidth"
                        :h="windowHeight"
                        :min-width="100"
                        :min-height="100"
                        class="crop-frame"
                        :handles="['tl','tm','tr','mr','br','bm','bl','ml']"
                        @dragging="onDragging"
                        @resizing="onResizing"
                    />
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn
                        flat
                        label="Cancelar"
                        color="primary"
                        class="q-mr-sm"
                        @click="cancelCrop"
                    />
                    <q-btn
                        flat
                        label="Salvar"
                        color="primary"
                        @click="saveCropImage"
                    />
                </q-card-actions>
            </q-card>
        </q-dialog>
    </q-page>
</template>

<script setup>
/* ===========================================
   IMPORTS
=========================================== */
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue';
import VueDraggableResizable from 'vue-draggable-resizable';
import 'vue-draggable-resizable/style.css';
import html2canvas from 'html2canvas';
import { OBJLoader } from 'three/examples/jsm/loaders/OBJLoader.js';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { useQuasar } from 'quasar';
import { createAxiosInstance } from '../../../api/axiosInstance';

/* ===========================================
   IMPORTAÇÕES DE COMPONENTES
=========================================== */
import ToolbarComponent from '../ToolbarComponent.vue';
import DrawingAreaComponent from '../DrawingAreaComponent/DrawingAreaComponent.vue';
import ControlPanelComponent from '../ControlPanelComponent.vue';
import GeometryController from './GeometryController.vue';

/* ===========================================
   CONFIGURAÇÃO DO AXIOS E QUASAR
=========================================== */
const axiosInstance = createAxiosInstance('yard');
const $q = useQuasar();

/* ===========================================
   REATIVIDADE PRINCIPAL
=========================================== */
const items = reactive([]);
let currentId = 0;
let isLoadingProject = false; // evita salvar no watch

// Histórico para undo/redo
const history = reactive({
    past: [],
    present: null,
    future: [],
});

// Estados para controladores
const showControllers = ref(false);
const controllerMode = ref('dimension'); // 'dimension' | 'curvature' | 'none'
const controllerCount = ref(2);

// Referências e estados
const selectedItem = ref(null);
const copiedItem = ref(null);
const currentDrawing = ref(null);
const drawingMode = ref(null);
const showGrid = ref(true);
const gridSize = 20;
const drawingAreaRef = ref(null);
const paintMode = ref('standard'); // Indica qual tipo de pintura (standard ou paint)
const isPaintMode = ref(false);
const isColorPickerOpen = ref(false);
const selectedColor = ref('#000000');
const showCropModal = ref(false);
const notification = ref(null);

// Dimensões da tela
const windowWidth = ref(window.innerWidth);
const windowHeight = ref(window.innerHeight);

// Formulários e estados relacionados a upload/projetos
const dialogCreateVisible = ref(false);
const createForm = ref({ imageName: '', groupBy: '', description: '' });
const createSelectedFile = ref(null);
const previewImage = ref('');
const loadingCreate = ref(false);

// Flags para redimensionamento do crop
const resizeLeft = ref(false);
const resizeRight = ref(false);
const resizeTop = ref(false);
const resizeBottom = ref(false);

const newLeftPosition = ref(null);
const newRightPosition = ref(null);
const newTopPosition = ref(null);
const newBottomPosition = ref(null);

// Projeto selecionado
const selectedProjectName = ref('');
let localStorageSaveTimeout = null;

// Exemplo de pré-visualização do recorte
const cropPreviewSrc = ref('');

// Estado para todas as imagens carregadas
const imagesLoaded = ref(0);
const totalImages = computed(() => items.filter((item) => item.type === 'image').length);
const allImagesLoaded = computed(() => {
    // Se não houver imagens, considere como todas carregadas
    if (totalImages.value === 0) return true;
    return imagesLoaded.value >= totalImages.value;
});

/* =====================================================
   COMPUTED E GETTERS
===================================================== */
const canUndo = computed(() => history.past.length > 0);
const canRedo = computed(() => history.future.length > 0);

const isConfigurable = computed(() => {
    if (!selectedItem.value) return false;
    const types = [
        'line', 'polyline', 'bezier', 'rectangle', 'circle', 'container', 'triangle',
        'star', 'hexagon', 'pentagon', 'octagon', 'ellipse', 'polygon', 'cross',
        'chevronLeft', 'chevronRight', 'chevronUp', 'chevronDown', 'halfStar', 'arc',
        'trapezoid', 'parallelogram', 'rhombus', 'heart', 'plus', 'moon', 'gear',
        'wave', 'waveShape', 'arrowUp', 'arrowDown', 'arrowRight', 'arrowLeft', 'zigzag', 'diamond',
        'cube', 'cylinder', 'sphere', 'cone', 'torus', 'pyramid', 'prism', 'helix',
        'spiral', 'crescent', 'flower', 'sun', 'cloud', 'bolt', 'waveShape', 'archimedeanSpiral',
        'label',
    ];
    return types.includes(selectedItem.value.type);
});

/* =====================================================
   WATCHERS
===================================================== */
// Observa changes em items e salva localmente com debounce
watch(
    items,
    (newVal) => {
        if (isLoadingProject) {
            console.log('watch(items): Ignorado devido ao carregamento de projeto');
            return;
        }
        if (localStorageSaveTimeout) {
            clearTimeout(localStorageSaveTimeout);
        }
        localStorageSaveTimeout = setTimeout(() => {
            localStorage.setItem('drawing', JSON.stringify(newVal));
            console.log('watch(items): items salvo no localStorage (debounce)');
        }, 400); // Ajuste o tempo de debounce se necessário
    },
    { deep: true },
);

// Observa o item selecionado para ajustar modo 'dimension' ou 'none' em polylines
watch(selectedItem, (newVal) => {
    if (newVal) {
        if (newVal.type === 'polyline') {
            console.log('watch(selectedItem): Item é polyline, definindo controllerMode="none"');
            controllerMode.value = 'none';
        } else {
            controllerMode.value = newVal.controllerMode || 'dimension';
        }
        controllerCount.value = newVal.controllerCount || 2;
        console.log(
            `watch(selectedItem): selectedItem (${newVal.id}) -> controllerMode=${controllerMode.value}, controllerCount=${controllerCount.value}`,
        );
    } else {
        console.log('watch(selectedItem): Nenhum item selecionado');
    }
});

// Observa totalImages para resetar imagesLoaded quando o número de imagens mudar
watch(totalImages, (newVal, oldVal) => {
    if (newVal !== oldVal) {
        imagesLoaded.value = 0;
        console.log(`watch(totalImages): totalImages mudou de ${oldVal} para ${newVal}. imagesLoaded resetado para 0.`);
    }
});

/* =====================================================
   LIFECYCLE HOOKS
===================================================== */
onMounted(() => {
    console.log('onMounted: Componente montado');
    const saved = localStorage.getItem('drawing');
    if (saved) {
        try {
            const parsed = JSON.parse(saved);
            items.splice(0, items.length, ...parsed);
            setCurrentId();
            console.log('onMounted: Itens carregados do localStorage');
        } catch (e) {
            console.error('onMounted: Erro ao carregar items do localStorage:', e);
        }
    } else {
        console.log('onMounted: Nenhum item encontrado no localStorage');
    }

    selectedProjectName.value = localStorage.getItem('selectedProjectName') || '';
    console.log(`onMounted: selectedProjectName set to "${selectedProjectName.value}"`);

    window.addEventListener('resize', updateWindowSize);
    window.addEventListener('keydown', handleKeyDown);
    console.log('onMounted: Listeners de resize e keydown adicionados');
});

onUnmounted(() => {
    window.removeEventListener('resize', updateWindowSize);
    window.removeEventListener('keydown', handleKeyDown);
    console.log('onUnmounted: Listeners de resize e keydown removidos');
});

/* =====================================================
   FUNÇÕES DE AJUDA
===================================================== */
function setCurrentId() {
    if (items.length === 0) {
        currentId = 0;
    } else {
        const maxId = Math.max(...items.map((item) => item.id || 0));
        currentId = maxId + 1;
    }
    console.log(`setCurrentId: currentId set to ${currentId}`);
}

function updateWindowSize() {
    windowWidth.value = window.innerWidth;
    windowHeight.value = window.innerHeight;
    console.log(`updateWindowSize: windowWidth=${windowWidth.value}, windowHeight=${windowHeight.value}`);
}

/* =====================================================
   NOTIFICAÇÕES
===================================================== */
function showNotification(message, type = 'success') {
    notification.value = { message, type };
    console.log(`showNotification: [${type}] ${message}`);
    setTimeout(() => {
        notification.value = null;
        console.log('showNotification: Notificação removida após timeout');
    }, 3000);
}

/* =====================================================
   ITENS: ADIÇÃO / ATUALIZAÇÃO / SELEÇÃO
===================================================== */
function addNewItem(type) {
    console.log(`addNewItem: Adicionando novo item do tipo="${type}"`);
    // Adicionando propriedades flexíveis para suportar todas as figuras
    const newItem = {
        id: currentId++,
        type,
        label: type === 'label' ? 'Novo Rótulo' : '',
        position: { x: 100, y: 100 },
        size: 50,
        rotation: 0,
        color: type === 'label' ? '#000000' : 'transparent',
        borderColor: '#000000',
        borderWidth: 1,
        width: 50,
        height: 50,
        points: type === 'polygon' ? [] : undefined, // Exemplo: suporte a polígonos
        // fillColor: type === 'polygon' ? '#000000' : 'transparent', // Preenchimento
        strokeColor: '#000000',
        strokeWidth: 2,
        endPosition: type === 'line' ? { x: 150, y: 150 } : undefined,
        // Melhorias para figuras/linhas
        fillColor: 'transparent', // caso usemos polilinhas fechadas ou shapes
        backgroundImage: null, // caso deseje inserir imagem de fundo
        roundedLine: false, // default
    };

    // Adicionar propriedades específicas para certos tipos
    switch (type) {
        case 'star':
            newItem.points = 5;
            newItem.innerRadius = 20;
            newItem.outerRadius = 40;
            break;
        case 'hexagon':
        case 'pentagon':
        case 'octagon':
            newItem.sides = type === 'hexagon' ? 6 : type === 'pentagon' ? 5 : 8;
            newItem.radius = 30;
            break;
        case 'ellipse':
            newItem.radiusX = 30;
            newItem.radiusY = 20;
            break;
        // Adicione mais casos conforme necessário para outros tipos
        default:
            break;
    }

    items.push(newItem);
    selectedItem.value = newItem;
    console.log(`addNewItem: Novo item adicionado com ID=${newItem.id}`);
    saveState();
}

function addItem(newItem) {
    console.log('addItem: Recebido newItem emitido por DrawingAreaComponent:', newItem);
    items.push(newItem);
    selectedItem.value = newItem;
    showNotification('Figura adicionada com sucesso!', 'success');
    console.log(`addItem: Item adicionado com ID=${newItem.id}`);
    saveState();
}

function selectItem(item) {
    selectedItem.value = item;
    console.log(`selectItem: Item selecionado com ID=${item.id}`);
}

function updateItems(updatedItem) {
    console.log('updateItems: Recebido updatedItem emitido por DrawingAreaComponent:', updatedItem);
    const index = items.findIndex((i) => i.id === updatedItem.id);
    if (index !== -1) {
        items[index] = { ...updatedItem };
        console.log(`updateItems: Item com ID=${updatedItem.id} atualizado`);
        saveState();
    } else {
        console.warn(`updateItems: Item com ID=${updatedItem.id} não encontrado`);
    }
}

function updateItem(updatedItem) {
    console.log('updateItem: Recebido updatedItem emitido por ControlPanelComponent:', updatedItem);
    const index = items.findIndex((i) => i.id === updatedItem.id);
    if (index !== -1) {
        items[index] = { ...updatedItem };
        console.log(`updateItem: Item com ID=${updatedItem.id} atualizado`);
        saveState();
    } else {
        console.warn(`updateItem: Item com ID=${updatedItem.id} não encontrado`);
    }
}

/* =====================================================
   MODO DE DESENHO / PINTURA
===================================================== */
function setDrawingMode(mode) {
    if (drawingMode.value === mode) {
        drawingMode.value = null;
        console.log(`setDrawingMode: Modo de desenho "${mode}" desativado`);
    } else {
        drawingMode.value = mode;
        isPaintMode.value = false;
        console.log(`setDrawingMode: Modo de desenho alterado para "${mode}"`);
    }
}

function togglePaintMode(active) {
    isPaintMode.value = active;
    console.log(`togglePaintMode: isPaintMode set to ${active}`);
    if (active) {
        openColorPicker();
    } else {
        closeColorPicker();
    }
}

function openColorPicker() {
    isColorPickerOpen.value = true;
    console.log('openColorPicker: Color picker aberto');
}

function closeColorPicker() {
    isColorPickerOpen.value = false;
    console.log('closeColorPicker: Color picker fechado');
}

function confirmColor() {
    isColorPickerOpen.value = false;
    console.log(`confirmColor: Cor confirmada como ${selectedColor.value}`);
}

/* =====================================================
   CONTROLADORES (TOGGLE, MODE, COUNT)
===================================================== */
function toggleControllers() {
    showControllers.value = !showControllers.value;
    console.log(`toggleControllers: showControllers is now ${showControllers.value}`);
}

function changeControllerMode(mode) {
    controllerMode.value = mode;
    console.log(`changeControllerMode: controllerMode set to "${mode}"`);
}

function updateControllerMode(mode) {
    controllerMode.value = mode;
    console.log(`updateControllerMode: controllerMode set to "${mode}"`);
    if (selectedItem.value) {
        selectedItem.value.controllerMode = mode;
        console.log(`updateControllerMode: Updated selectedItem (${selectedItem.value.id}) controllerMode to "${mode}"`);
        saveState();
    }
}

function updateControllerCount(newCount) {
    controllerCount.value = newCount;
    console.log(`updateControllerCount: controllerCount set to ${newCount}`);
    updateItemControlPoints(newCount);
    if (selectedItem.value) {
        selectedItem.value.controllerCount = newCount;
        console.log(`updateControllerCount: Updated selectedItem (${selectedItem.value.id}) controllerCount to ${newCount}`);
        saveState();
    }
}

function updateRoundedLine(isRounded) {
    if (selectedItem.value) {
        selectedItem.value.roundedLine = isRounded;
        console.log(`updateRoundedLine: Updated selectedItem (${selectedItem.value.id}) roundedLine to ${isRounded}`);
        saveState();
    }
}

/* =====================================================
   CONTROLADORES PARA POLYLINE
===================================================== */
function updateItemControlPoints(newCount) {
    if (!selectedItem.value || selectedItem.value.type !== 'polyline') {
        console.log('updateItemControlPoints: Nenhum item selecionado ou item não é polyline.');
        return;
    }

    const currentPath = selectedItem.value.path || [];
    const currentCount = currentPath.length;

    if (newCount > currentCount) {
        const additionalPoints = newCount - currentCount;
        for (let i = 0; i < additionalPoints; i++) {
            currentPath.push({
                x: currentPath[currentCount - 1]?.x || 0,
                y: currentPath[currentCount - 1]?.y || 0,
            });
        }
        console.log(`updateItemControlPoints: Adicionados ${additionalPoints} pontos ao path.`);
    } else if (newCount < currentCount) {
        const removedPoints = currentCount - newCount;
        currentPath.splice(newCount);
        console.log(`updateItemControlPoints: Removidos ${removedPoints} pontos do path.`);
    }

    selectedItem.value.path = currentPath;
    updateItem(selectedItem.value);
    saveState();
    console.log('updateItemControlPoints: Item atualizado com novos pontos de controle:', currentPath);
}

/* =====================================================
   GRADE
===================================================== */
function toggleGrid(value) {
    showGrid.value = value;
    console.log(`toggleGrid: showGrid set to ${value}`);
}

/* =====================================================
   IMAGENS / MODELOS 3D
===================================================== */
function handleImageUploadInDrawingArea(imageUrl) {
    console.log(`handleImageUploadInDrawingArea: Recebido imageUrl=${imageUrl}`);
    addImageToCanvas(imageUrl);
}
const imageBackgrondUrl = ref(null);
function handleImageUploadInItemBackground(imageUrl) {
    console.log(`handleImageUploadInItemBackground: Recebido imageUrl=${imageUrl}`);
    if (selectedItem.value) {
        // Cria uma nova instância de Image
        const img = new Image();
        img.crossOrigin = 'Anonymous'; // Necessário para evitar problemas de CORS
        img.src = imageUrl;

        img.onload = () => {
            // Cria um canvas para desenhar a imagem
            const canvas = document.createElement('canvas');
            canvas.width = img.width;
            canvas.height = img.height;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(img, 0, 0);

            // Converte o canvas para uma URL Base64
            const dataURL = canvas.toDataURL('image/png');

            // Define a URL Base64 como background-image-url
            imageBackgrondUrl.value = dataURL;
            console.log(`Background da imagem definido para o item ID=${selectedItem.value.id}`);
            showNotification('Imagem de fundo adicionada com sucesso!', 'success');
            saveState();
        };

        img.onerror = () => {
            console.error('Erro ao carregar a imagem de fundo.');
            showNotification('Erro ao carregar a imagem de fundo.', 'error');
        };
    } else {
        console.error('Nenhum item selecionado para definir a imagem de fundo.');
    }
}

function addImageToCanvas(fileUrl) {
    console.log(`addImageToCanvas: Adicionando imagem com URL=${fileUrl}`);
    const modelExtensions = ['.obj', '.gltf', '.glb'];
    const is3DModel = modelExtensions.some((ext) => fileUrl.toLowerCase().endsWith(ext));

    if (is3DModel) {
        showNotification('Modelo 3D adicionado!', 'success');
        add3DModelToCanvas(fileUrl);
    } else {
        const newItem = {
            id: currentId++,
            type: 'image',
            imageUrl: fileUrl,
            position: { x: 100, y: 100 },
            width: 200,
            height: 200,
            rotation: 0,
            skewX: 0,
            skewY: 0,
            fillColor: 'transparent',
            backgroundImage: null,
        };
        items.push(newItem);
        selectedItem.value = newItem;
        console.log(`addImageToCanvas: Imagem 2D adicionada com ID=${newItem.id}`);
        showNotification('Imagem adicionada com sucesso!', 'success');
        saveState();
    }
}

function add3DModelToCanvas(modelUrl) {
    console.log(`add3DModelToCanvas: Carregando modelo 3D de URL=${modelUrl}`);
    const extension = modelUrl.split('.').pop().toLowerCase();
    let loader;

    if (extension === 'obj') {
        loader = new OBJLoader();
    } else if (extension === 'gltf' || extension === 'glb') {
        loader = new GLTFLoader();
    } else {
        showNotification(`Formato de arquivo não suportado: ${extension}`, 'error');
        console.error(`add3DModelToCanvas: Formato de arquivo não suportado: ${extension}`);
        return;
    }

    loader.load(
        modelUrl,
        (object) => {
            const newItem = {
                id: currentId++,
                type: '3dModel',
                model: object.scene || object,
                position: { x: 100, y: 100, z: 0 },
                scale: { x: 1, y: 1, z: 1 },
                rotation: { x: 0, y: 0, z: 0 },
                width: 200,
                height: 200,
                skewX: 0,
                skewY: 0,
                fillColor: 'transparent',
                backgroundImage: null,
            };
            items.push(newItem);
            selectedItem.value = newItem;
            console.log(`add3DModelToCanvas: Modelo 3D adicionado com ID=${newItem.id}`);
            showNotification('Modelo 3D adicionado com sucesso!', 'success');
            saveState();
        },
        (xhr) => {
            console.log(`add3DModelToCanvas: ${((xhr.loaded / xhr.total) * 100).toFixed(2)}% carregado`);
        },
        (error) => {
            console.error('add3DModelToCanvas: Erro ao carregar o modelo 3D:', error);
            showNotification('Erro ao carregar o modelo 3D', 'error');
        },
    );
}

/* =====================================================
   BRING FRONT / SEND BACK
===================================================== */
function handleBringToFront(item) {
    console.log(`handleBringToFront: Trazendo para frente o item com ID=${item.id}`);
    const index = items.findIndex((i) => i.id === item.id);
    if (index !== -1) {
        const [selectedItemRef] = items.splice(index, 1);
        items.push(selectedItemRef);
        console.log(`handleBringToFront: Item com ID=${item.id} trazido para frente`);
        saveState();
    } else {
        console.warn(`handleBringToFront: Item com ID=${item.id} não encontrado`);
    }
}

function handleSendToBack(item) {
    console.log(`handleSendToBack: Enviando para trás o item com ID=${item.id}`);
    const index = items.findIndex((i) => i.id === item.id);
    if (index !== -1) {
        const [selectedItemRef] = items.splice(index, 1);
        items.unshift(selectedItemRef);
        console.log(`handleSendToBack: Item com ID=${item.id} enviado para trás`);
        saveState();
    } else {
        console.warn(`handleSendToBack: Item com ID=${item.id} não encontrado`);
    }
}

/* =====================================================
   HISTÓRICO (UNDO/REDO)
===================================================== */
let changeCounter = 0;

function saveState() {
    changeCounter++;
    console.log(`saveState: changeCounter=${changeCounter}`);
    if (changeCounter >= 30) {
        if (history.past.length >= 50) {
            history.past.shift();
            console.log('saveState: Histórico past excedeu 50, removendo o mais antigo');
        }
        history.past.push(JSON.stringify(items));
        history.present = JSON.stringify(items);
        history.future = [];
        console.log('saveState: Estado salvo no histórico');
        changeCounter = 0;
    }
}

function undo() {
    console.log('undo: Tentando desfazer a última ação');
    if (history.past.length > 0) {
        history.future.unshift(history.present);
        history.present = history.past.pop();
        loadState(history.present);
        console.log('undo: Desfazer realizado');
    } else {
        console.log('undo: Nenhuma ação para desfazer');
    }
}

function redo() {
    console.log('redo: Tentando refazer a última ação desfeita');
    if (history.future.length > 0) {
        history.past.push(history.present);
        history.present = history.future.shift();
        loadState(history.present);
        console.log('redo: Refazer realizado');
    } else {
        console.log('redo: Nenhuma ação para refazer');
    }
}

function loadState(state) {
    console.log('loadState: Carregando estado do histórico');
    const parsedItems = JSON.parse(state);
    items.splice(0, items.length, ...parsedItems);
    setCurrentId();
    console.log('loadState: Estado carregado');
}

/* =====================================================
   LIMPAR ITENS
===================================================== */
function cleanItems() {
    console.log('cleanItems: Limpando todos os itens');
    items.splice(0, items.length);
    localStorage.setItem('drawing', JSON.stringify(items));
    console.log('cleanItems: Itens limpos e localStorage atualizado');
}

/* =====================================================
   SALVAR COMO IMAGEM
===================================================== */
function saveDesignAsImage() {
    console.log('saveDesignAsImage: Tentando salvar o design como imagem');
    if (drawingAreaRef.value) {
        const drawingAreaElement = drawingAreaRef.value.$el;
        html2canvas(drawingAreaElement, {
            useCORS: true,
            allowTaint: false,
        })
            .then((canvas) => {
                const link = document.createElement('a');
                link.download = 'design-patio.png';
                link.href = canvas.toDataURL();
                link.click();
                console.log('saveDesignAsImage: Design salvo como "design-patio.png"');
            })
            .catch((error) => {
                console.error('saveDesignAsImage: Erro ao salvar o design:', error);
            });
    } else {
        console.error('saveDesignAsImage: drawingAreaRef não está definido.');
    }
}

/* =====================================================
   ATALHOS DE TECLADO
===================================================== */
function handleKeyDown(event) {
    console.log(`handleKeyDown: Tecla pressionada - ${event.key}, ctrlKey=${event.ctrlKey}`);
    if (event.key === 'Delete' && selectedItem.value) {
        console.log(`handleKeyDown: Removendo item com ID=${selectedItem.value.id}`);
        removeItem(selectedItem.value);
    } else if (event.ctrlKey && event.key === 'c') {
        if (selectedItem.value) {
            copyItem(selectedItem.value);
            console.log(`handleKeyDown: Item copiado com ID=${selectedItem.value.id}`);
        }
    } else if (event.ctrlKey && event.key === 'v') {
        pasteItem();
        console.log('handleKeyDown: Colando item copiado');
    } else if (event.ctrlKey && event.key === 'z') {
        event.preventDefault(); // Evita comportamento padrão do navegador
        undo();
        console.log('handleKeyDown: Executando undo');
    }
}

/* =====================================================
   COPIAR / COLAR / REMOVER
===================================================== */
function removeItem(item) {
    console.log(`removeItem: Removendo item com ID=${item.id}`);
    const index = items.findIndex((i) => i.id === item.id);
    if (index !== -1) {
        items.splice(index, 1);
        selectedItem.value = null;
        console.log(`removeItem: Item com ID=${item.id} removido`);
        saveState();
    } else {
        console.warn(`removeItem: Item com ID=${item.id} não encontrado`);
    }
}

function copyItem(item) {
    copiedItem.value = { ...item, id: undefined };
    console.log(`copyItem: Item copiado com ID original=${item.id}`);
}

function pasteItem() {
    if (copiedItem.value) {
        const newItem = {
            ...copiedItem.value,
            id: currentId++,
            position: {
                x: copiedItem.value.position.x + 10,
                y: copiedItem.value.position.y + 10,
            },
            endPosition: copiedItem.value.endPosition
                ? {
                    x: copiedItem.value.endPosition.x + 10,
                    y: copiedItem.value.endPosition.y + 10,
                }
                : undefined,
        };
        items.push(newItem);
        selectedItem.value = newItem;
        console.log(`pasteItem: Item colado com novo ID=${newItem.id}`);
        showNotification('Item colado com sucesso!', 'success');
        saveState();
    } else {
        console.warn('pasteItem: Nenhum item copiado para colar');
    }
}

/* =====================================================
   CROP IMAGE (SEM CHAINED ASSIGNMENT)
===================================================== */
function openCropModal() {
    showCropModal.value = true;
    console.log('openCropModal: Modal de crop aberto');
    generateCropPreview();
}

function generateCropPreview() {
    if (!drawingAreaRef.value) return;
    html2canvas(drawingAreaRef.value.getDrawingArea(), {
        useCORS: true,
        allowTaint: false,
        scale: 0.5, // diminui a resolução da pré-visualização
    })
        .then((canvas) => {
            cropPreviewSrc.value = canvas.toDataURL();
            console.log('generateCropPreview: Pré-visualização do crop gerada');
        })
        .catch((error) => {
            console.error('generateCropPreview: Erro ao gerar preview:', error);
        });
}

function cancelCrop() {
    showCropModal.value = false;
    cropPreviewSrc.value = '';
    console.log('cancelCrop: Modal de crop cancelado');
}

function onDragging(x, y) {
    console.log(`onDragging: Arrastando crop frame para x=${x}, y=${y}`);
}

function onResizing(newRect, oldRect, handle) {
    // Reset flags
    resizeLeft.value = false;
    resizeRight.value = false;
    resizeTop.value = false;
    resizeBottom.value = false;

    newLeftPosition.value = null;
    newRightPosition.value = null;
    newTopPosition.value = null;
    newBottomPosition.value = null;

    if (handle.includes('l')) {
        resizeLeft.value = true;
        newLeftPosition.value = newRect.left;
        console.log('onResizing: resizeLeft ativado');
    }
    if (handle.includes('r')) {
        resizeRight.value = true;
        newRightPosition.value = newRect.right;
        console.log('onResizing: resizeRight ativado');
    }
    if (handle.includes('t')) {
        resizeTop.value = true;
        newTopPosition.value = newRect.top;
        console.log('onResizing: resizeTop ativado');
    }
    if (handle.includes('b')) {
        resizeBottom.value = true;
        newBottomPosition.value = newRect.bottom;
        console.log('onResizing: resizeBottom ativado');
    }
}

function saveCropImage() {
    console.log('saveCropImage: Tentando salvar a imagem recortada');
    if (!drawingAreaRef.value) {
        console.error('saveCropImage: drawingAreaRef não definido.');
        return;
    }
    const element = drawingAreaRef.value.$el;
    const cropElement = document.querySelector('.crop-frame');

    if (!cropElement) {
        console.error('saveCropImage: ".crop-frame" não encontrado.');
        return;
    }

    const rect = cropElement.getBoundingClientRect();
    const containerRect = element.getBoundingClientRect();

    let x = rect.left - containerRect.left;
    let y = rect.top - containerRect.top;
    let width = rect.width;
    let height = rect.height;

    const originalRight = x + width;
    const originalBottom = y + height;
    const originalLeft = x;
    const originalTop = y;

    if (resizeLeft.value && typeof newLeftPosition.value === 'number') {
        x = newLeftPosition.value - containerRect.left;
        width = originalRight - x;
        console.log('saveCropImage: Redimensionando pela esquerda');
    }
    if (resizeRight.value && typeof newRightPosition.value === 'number') {
        width = newRightPosition.value - containerRect.left - originalLeft;
        console.log('saveCropImage: Redimensionando pela direita');
    }
    if (resizeTop.value && typeof newTopPosition.value === 'number') {
        y = newTopPosition.value - containerRect.top;
        height = originalBottom - y;
        console.log('saveCropImage: Redimensionando pelo topo');
    }
    if (resizeBottom.value && typeof newBottomPosition.value === 'number') {
        height = newBottomPosition.value - containerRect.top - originalTop;
        console.log('saveCropImage: Redimensionando pela base');
    }

    if (width <= 0 || height <= 0) {
        console.error('saveCropImage: Área de corte inválida.');
        return;
    }

    html2canvas(element, {
        x,
        y,
        width,
        height,
        useCORS: true,
        allowTaint: false,
    })
        .then((canvas) => {
            const imageUrl = canvas.toDataURL('image/png');
            const link = document.createElement('a');
            link.download = 'cropImage.png';
            link.href = imageUrl;
            link.click();
            console.log('saveCropImage: CropImage capturada e salva como "cropImage.png"');
            showCropModal.value = false;
            cropPreviewSrc.value = '';
        })
        .catch((error) => {
            console.error('saveCropImage: Erro ao capturar a área:', error);
        });
}

/* =====================================================
   CRUD DE PROJETOS
===================================================== */
async function createImageYard() {
    console.log('createImageYard: Iniciando criação de ImageYard');
    loadingCreate.value = true;

    const file = createSelectedFile.value;
    if (!file) {
        $q.notify({ message: 'Selecione uma imagem (upload).', color: 'warning' });
        console.warn('createImageYard: Nenhum arquivo selecionado para upload');
        loadingCreate.value = false;
        return;
    }

    // Exemplo de validação de tamanho de arquivo
    if (!validateFileSize(file)) {
        loadingCreate.value = false;
        return;
    }

    const fd = new FormData();
    fd.append('file', file);
    fd.append(
        'jsonImageYard',
        JSON.stringify({
            imageName: createForm.value.imageName,
            groupBy: createForm.value.groupBy,
            type: createForm.value.type,
            description: createForm.value.description,
            items: JSON.stringify(items),
        }),
    );

    try {
        console.log('createImageYard: Enviando requisição para criar ImageYard');
        await axiosInstance.post('/rest/imageYard', fd, {
            headers: { 'Content-Type': 'multipart/form-data' },
        });
        $q.notify({ message: 'ImageYard criado com sucesso!', color: 'positive' });
        console.log('createImageYard: ImageYard criado com sucesso');
        dialogCreateVisible.value = false;
        createForm.value = { imageName: '', groupBy: '', description: '' };
        createSelectedFile.value = null;
        previewImage.value = '';
        await loadImages();
    } catch (error) {
        console.error('createImageYard: Erro ao criar ImageYard:', error.response ? error.response.data : error);
        $q.notify({ message: 'Erro ao criar ImageYard.', color: 'negative' });
    }

    loadingCreate.value = false;
}

function validateFileSize(file) {
    // Exemplo: máximo de 10 MB
    const maxSizeMB = 10;
    if (file.size > maxSizeMB * 1024 * 1024) {
        $q.notify({ message: `Tamanho máximo de ${maxSizeMB}MB excedido.`, color: 'negative' });
        console.warn(`validateFileSize: Arquivo muito grande (${file.size} bytes).`);
        return false;
    }
    return true;
}

async function saveProject(jsonImageYard) {
    console.log('saveProject: Iniciando salvamento do projeto');
    if (drawingAreaRef.value) {
        try {
            const canvas = await html2canvas(drawingAreaRef.value.$el, {
                useCORS: true,
                allowTaint: false,
            });
            const blob = await new Promise((resolve) => {
                canvas.toBlob((b) => resolve(b), 'image/png');
            });
            const file = new File([blob], 'screenshot.png', { type: 'image/png' });
            createSelectedFile.value = file;
            createForm.value = { ...jsonImageYard };
            console.log('saveProject: Screenshot capturada e preparada para upload');
            await createImageYard();
        } catch (error) {
            console.error('saveProject: Erro ao salvar o projeto:', error);
        }
    } else {
        console.error('saveProject: drawingAreaRef não está definido.');
    }
}

async function updateProject() {
    console.log('updateProject: Iniciando atualização do projeto');
    if (!createForm.value || !createForm.value.imageId) {
        console.warn('updateProject: Nenhum projeto selecionado para atualizar');
        $q.notify({ message: 'Nenhum projeto selecionado para atualizar.', color: 'warning' });
        return;
    }

    loadingCreate.value = true;
    try {
        let fileBase64 = null;
        if (createSelectedFile.value) {
            fileBase64 = await fileToBase64(createSelectedFile.value);
            console.log('updateProject: Arquivo convertido para base64');
        }

        const payload = {
            imageName: createForm.value.imageName,
            groupBy: createForm.value.groupBy,
            description: createForm.value.description,
            items: JSON.stringify(items),
        };

        console.log(`updateProject: Enviando requisição para atualizar ImageYard com ID=${createForm.value.imageId}`);
        await axiosInstance.put(`/rest/imageYard/${createForm.value.imageId}`, payload, {
            headers: { 'Content-Type': 'application/json' },
        });

        $q.notify({ message: 'ImageYard atualizado com sucesso!', color: 'positive' });
        console.log('updateProject: ImageYard atualizado com sucesso');
    } catch (error) {
        console.error('updateProject: Erro ao atualizar ImageYard:', error.response ? error.response.data : error);
        $q.notify({ message: 'Erro ao atualizar ImageYard.', color: 'negative' });
    } finally {
        loadingCreate.value = false;
    }
}

function openProject(projectData) {
    console.log('openProject: Recebido projeto para abrir', projectData);
    if (projectData.items) {
        try {
            isLoadingProject = true;
            console.log('openProject: Carregando projeto...');

            const parsedItems = JSON.parse(JSON.stringify(JSON.parse(projectData.items)));
            items.splice(0, items.length, ...parsedItems);
            console.log('openProject: Itens clonados e carregados');

            setCurrentId();
            localStorage.setItem('drawing', JSON.stringify(items));
            console.log('openProject: Itens salvos no localStorage');

            if (projectData.imageId) {
                createForm.value.imageId = projectData.imageId;
                console.log(`openProject: imageId set to ${projectData.imageId}`);
            } else {
                delete createForm.value.imageId;
                console.log('openProject: imageId removido');
            }

            if (projectData.imageName) {
                createForm.value.imageName = projectData.imageName;
                console.log(`openProject: imageName set to "${projectData.imageName}"`);
            }
            if (projectData.groupBy) {
                createForm.value.groupBy = projectData.groupBy;
                console.log(`openProject: groupBy set to "${projectData.groupBy}"`);
            }
            if (projectData.description) {
                createForm.value.description = projectData.description;
                console.log(`openProject: description set to "${projectData.description}"`);
            }

            selectedProjectName.value = projectData.imageName || '';
            localStorage.setItem('selectedProjectName', selectedProjectName.value);
            console.log(`openProject: selectedProjectName set to "${selectedProjectName.value}"`);

            $q.notify({ message: 'Projeto carregado com sucesso!', color: 'positive' });
            console.log('openProject: Notificação de sucesso enviada');
        } catch (error) {
            console.error('openProject: Erro ao parsear items do projeto:', error);
            $q.notify({ message: 'Falha ao carregar o projeto.', color: 'negative' });
        } finally {
            isLoadingProject = false;
            console.log('openProject: Carregamento de projeto finalizado');
        }
    } else {
        $q.notify({ message: 'Nenhum item encontrado no projeto.', color: 'warning' });
        console.warn('openProject: Projeto sem itens');
    }
}

function fileToBase64(file) {
    console.log(`fileToBase64: Convertendo arquivo "${file.name}" para base64`);
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => {
            console.log('fileToBase64: Conversão para base64 concluída');
            resolve(reader.result);
        };
        reader.onerror = (err) => {
            console.error('fileToBase64: Erro na conversão para base64:', err);
            reject(err);
        };
        reader.readAsDataURL(file);
    });
}

/* =====================================================
   CARREGAR IMAGENS (FUNÇÃO DE EXEMPLO)
===================================================== */
async function loadImages() {
    // Implemente a lógica para carregar imagens/projetos, se necessário
    console.log('loadImages: Função para carregar imagens/projetos não implementada.');
}

/* =====================================================
   HANDLERS PARA IMAGENS CARREGADAS
===================================================== */
function handleImagesLoaded() {
    imagesLoaded.value++;
    console.log(`handleImagesLoaded: Imagem carregada. Total carregadas: ${imagesLoaded.value}/${totalImages.value}`);
}

function handleImagesError() {
    console.error('handleImagesError: Erro ao carregar uma imagem.');
    showNotification('Erro ao carregar uma imagem.', 'error');
}
</script>

<style scoped>
/* Caixa do nome do projeto */
.project-name-box {
    position: fixed;
    top: 40px;
    right: 80px;
    z-index: 9999;
}

/* Modal de recorte */
.crop-modal {
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0;
    padding: 0;
}

/* Seção central do modal de recorte */
.crop-modal-content {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}

/* Área de arraste do recorte */
.crop-frame {
    border: 2px dashed #42A5F5;
    background-color: rgba(66, 165, 245, 0.1);
    box-sizing: border-box;
    width: 100%;
    height: 100%;
    cursor: move;
}

/* Notificações fixas */
.notification {
    position: fixed;
    bottom: 20px;
    right: 20px;
    padding: 10px 20px;
    color: white;
    background-color: green;
    border-radius: 5px;
    z-index: 10000;
}
.notification.error {
    background-color: red;
}
.notification.warning {
    background-color: orange;
}

/* Ações do modal */
.crop-modal .q-card-actions {
    padding: 10px;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

/* Pré-visualização de recorte (opcional) */
.crop-preview {
    position: absolute;
    top: 20px;
    left: 20px;
    width: 150px;
    height: 150px;
    background: #fff;
    overflow: hidden;
    z-index: 10001;
    border: 1px solid #ccc;
}
.crop-preview-image {
    width: 100%;
    height: auto;
    display: block;
}

/* Outros estilos como handles, rotas, etc. */
/* ... */
</style>
