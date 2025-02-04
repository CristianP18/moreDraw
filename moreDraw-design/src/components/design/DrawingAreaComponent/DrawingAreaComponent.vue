<template>
    <div
        ref="drawingArea"
        class="drawing-area"
        :style="drawingAreaStyle"
        @mousedown="handleMouseDown"
        @mousemove="handleMouseMove"
        @mouseup="handleMouseUp"
        @mouseleave="handleMouseLeave"
        @wheel.prevent="handleZoom"
        @contextmenu.prevent
        @dragenter="handleDragEnter"
        @dragleave="handleDragLeave"
        @dragover.prevent="handleDragOver"
        @drop="handleDrop"
    >
        <!-- Canvas para desenho (freehand, preview, etc.) -->
        <canvas
            id="drawingCanvas"
            ref="drawingCanvas"
            class="drawing-canvas"
            :style="{ display: isDrawing.value ? 'block' : 'none' }"
        />

        <!-- Controles para Controladores -->
        <div
            v-if="shouldShowConfig"
            class="controls"
        >
            <button @click="toggleControllers">
                {{ showControllers ? 'Esconder Controladores' : 'Mostrar Controladores' }}
            </button>
            <div
                v-if="showControllers"
                class="controller-options"
            >
                <label>
                    Modo de Controladores:
                    <select
                        v-model="localControllerMode"
                        @change="emitControllerMode"
                    >
                        <option value="dimension">Dimensão</option>
                        <option value="curvature">Curvatura</option>
                    </select>
                </label>
                <label>
                    Número de Controladores:
                    <input
                        v-model.number="localControllerCount"
                        type="number"
                        min="4"
                        max="100"
                        @input="emitControllerCount"
                    >
                </label>
                <label v-if="isConfigurable">
                    <input
                        type="checkbox"
                        :checked="selectedItem.roundedLine"
                        @change="toggleRoundedLine"
                    >
                    Arredondar a linha
                </label>
            </div>
        </div>

        <!-- Grade -->
        <GridComponent
            v-if="showGrid"
            :grid-size="gridSize"
        />

        <!-- Renderizar itens -->
        <ItemComponent
            v-for="item in items"
            :key="item.id"
            :item="item"
            :zoom-level="zoomLevel"
            :pan-offset="panOffset"
            :paint-mode="paintMode"
            :selected-color="selectedColor"
            @select-item="selectItem"
            @update-item="updateItem"
            @bring-to-front="bringToFront"
            @bring-to-back="sendToBack"
        />

        <!-- Notificações -->
        <div
            v-if="notification"
            :class="`notification ${notification.type}`"
        >
            {{ notification.message }}
        </div>
    </div>
</template>

<script setup>
/* eslint-disable no-console */
import { ref, computed, onMounted, watch, onUnmounted } from 'vue';
import ItemComponent from '../ItemComponent/ItemComponent.vue';
import GridComponent from '../GridComponent.vue';

/* =====================================================================
     PROPS / EMITS
===================================================================== */
const props = defineProps({
    items: {
        type: Array,
        default: () => [],
    },
    currentDrawing: {
        type: Object,
        default: () => ({}),
    },
    backgroundImageUrl: {
        type: String,
        default: null,
    },
    drawingMode: {
        type: String,
        default: null, // "line", "rectangle", "freehand", "bezier", "squarePoliline", "circlePoliline", etc.
    },
    showGrid: {
        type: Boolean,
        default: true,
    },
    gridSize: {
        type: Number,
        default: 20,
    },
    paintMode: {
        type: Boolean,
        default: false,
    },
    paintType: {
        type: String,
        default: 'standard', // "standard" | "paint"
    },
    selectedColor: {
        type: String,
        default: '#000000',
    },
    selectedItem: {
        type: Object,
        default: null,
    },
    showControllers: {
        type: Boolean,
        default: false,
    },
    controllerMode: {
        type: String,
        default: 'dimension',
    },
    controllerCount: {
        type: Number,
        default: 30, // Inicializar com 30 pontos
    },
});

const emits = defineEmits([
    'update-items',
    'select-item',
    'bring-to-front',
    'send-to-back',
    'save-state',
    'set-drawing-mode',
    'add-item',
    'toggle-controllers',
    'change-controller-mode',
    'update-controller-count',
]);

/* =====================================================================
     REFS E ESTADOS
===================================================================== */
const drawingArea = ref(null);
const drawingCanvas = ref(null);
const ctx = ref(null);

// Zoom e Pan
const zoomLevel = ref(1);
const isPanning = ref(false);
const panStart = ref({ x: 0, y: 0 });
const panOffset = ref({ x: 0, y: 0 });

// Arrastar itens
const isDragging = ref(false);
const dragItemId = ref(null);
const dragStart = ref({ x: 0, y: 0 });

// Desenho
const isDrawing = ref(false);
const startPoint = ref({ x: 0, y: 0 });
const lastMousePosition = ref({ x: 0, y: 0 });

const selectedItemBackground = ref(props.selectedItem);

// Notificações
const notification = ref(null);

// Caminhos temporários (freehand, bezier)
const currentPath = ref([]);

// Controladores
const localControllerMode = ref(props.controllerMode);
const localControllerCount = ref(
    props.selectedItem
    && (props.selectedItem.type === 'polyline'
     || props.selectedItem.type === 'squarePoliline'
     || props.selectedItem.type === 'circlePoliline')
        ? props.selectedItem.path?.length || props.controllerCount
        : props.controllerCount,
);

const supportedBackgroundTypes = [
    'polyline',
    'bezier',
    'squarePoliline',
    'circlePoliline',
    'star',
    'hexagon',
    'pentagon',
    'octagon',
    'diamond',
    'rhombus',
    'parallelogram',
    'trapezoid',
    'chevronLeft',
    'chevronRight',
    'chevronUp',
    'chevronDown',
    'halfStar',
    'circle',
    'container',
    'triangle',
    'ellipse',
    // Adicione outros tipos conforme necessário
];

/* =====================================================================
     WATCHERS
===================================================================== */
// Observa mudança no item selecionado para ajustar número de controladores
watch(
    () => props.selectedItem,
    (newItem) => {
        // Se for polyline, squarePoliline ou circlePoliline, ajusta localControllerCount
        if (
            newItem
          && (newItem.type === 'polyline'
           || newItem.type === 'squarePoliline'
           || newItem.type === 'circlePoliline')
        ) {
            localControllerCount.value = newItem.path?.length || props.controllerCount;
        } else {
            // Se for outro tipo, definimos para controllerCount padrão
            localControllerCount.value = props.controllerCount;
        }
    },
    { immediate: true, deep: true },
);

// Melhoria na função de watch
watch(() => props.backgroundImageUrl, (newUrl, oldUrl) => {
    if (newUrl !== oldUrl) { // Garante que a URL mudou
        console.log(`Item selecionado é: ${selectedItemBackground.value}`);
        if (selectedItemBackground.value) {
            if (supportedBackgroundTypes.includes(selectedItemBackground.value.type)) {
                selectedItemBackground.value.backgroundImage = newUrl;
                console.log('Updated item:', selectedItemBackground.value);
                emits('update-items', { ...selectedItemBackground.value });
                console.log('Items after update:', props.items);

                console.log(`Background da imagem atualizado para o item ID=${selectedItemBackground.value.id}`);
            } else {
                console.error(`Tipo de item ${selectedItemBackground.value.type} não suporta imagem de fundo.`);
            }
        } else {
            console.error('Nenhum item selecionado para definir a imagem de fundo.');
        }
    }
});

// Observa mudança no modo do controller
watch(
    () => props.controllerMode,
    (newVal) => {
        localControllerMode.value = newVal;
    },
);

// Observa mudança na quantidade de controladores (props)
watch(
    () => props.controllerCount,
    (newVal) => {
        localControllerCount.value = newVal;
    },
);

// Observa localControllerMode -> emite
watch(localControllerMode, (newVal) => {
    emits('change-controller-mode', newVal);
});

// Observa localControllerCount -> emite e atualiza polylines
watch(localControllerCount, (newVal) => {
    emits('update-controller-count', newVal);

    // Se o item atual for squarePoliline/circlePoliline, só ajustamos path (adiciona/remove pontos)
    if (props.selectedItem) {
        if (props.selectedItem.type === 'squarePoliline') {
            updateSquarePoliline(newVal);
        } else if (props.selectedItem.type === 'circlePoliline') {
            updateCirclePoliline(newVal);
        }
    }
});

// Observa items -> re-renderiza no canvas
watch(
    () => props.items,
    () => {
        renderCanvas();
    },
    { deep: true },
);

/* =====================================================================
     COMPUTEDS
===================================================================== */
const drawingAreaStyle = computed(() => ({
    width: '100%',
    height: '1000px',
    cursor: props.drawingMode
        ? 'crosshair'
        : props.paintMode
            ? 'url(/path-to-paint-cursor.png), auto'
            : 'default',
}));

/** Verifica se o item selecionado pode exibir configs extras */
const isConfigurable = computed(() => {
    if (!props.selectedItem) return false;
    const types = [
        'line', 'polyline', 'bezier', 'rectangle', 'circle', 'triangle',
        'star', 'hexagon', 'pentagon', 'octagon', 'diamond',
        'chevronLeft', 'chevronRight', 'chevronUp', 'chevronDown',
        'halfStar', 'squarePoliline', 'circlePoliline',
    ];
    return types.includes(props.selectedItem.type);
});
const shouldShowConfig = computed(() => props.selectedItem && isConfigurable.value);

/* =====================================================================
     onMounted
===================================================================== */
onMounted(() => {
    if (drawingCanvas.value) {
        ctx.value = drawingCanvas.value.getContext('2d');
        drawingCanvas.value.width = drawingArea.value.clientWidth;
        drawingCanvas.value.height = drawingArea.value.clientHeight;
        renderCanvas();
    }
});

onUnmounted(() => {
    window.removeEventListener('keydown', handleKeyDown);
    window.removeEventListener('keyup', handleKeyUp);
});

/* =====================================================================
     FUNÇÕES PRINCIPAIS
===================================================================== */
function renderCanvas() {
    if (!ctx.value) return;
    ctx.value.clearRect(0, 0, drawingCanvas.value.width, drawingCanvas.value.height);

    props.items.forEach((item) => {
        switch (item.type) {
            case 'line':
                drawLine(item);
                break;
            case 'rectangle':
            case 'shape':
                drawRectangle(item);
                break;
            case 'circle':
                drawCircle(item);
                break;
            case 'ellipse':
                drawEllipse(item);
                break;
            case 'triangle':
                drawTriangle(item);
                break;
            case 'star':
                drawStar(item);
                break;
            case 'hexagon':
                drawPolygon(item, 6);
                break;
            case 'pentagon':
                drawPolygon(item, 5);
                break;
            case 'octagon':
                drawPolygon(item, 8);
                break;
            case 'polygon':
                drawPolygon(item, item.sides || 5);
                break;
            case 'chevronLeft':
            case 'chevronRight':
            case 'chevronUp':
            case 'chevronDown':
                drawChevron(item);
                break;
            case 'halfStar':
                drawHalfStar(item);
                break;
            case 'arrowUp':
            case 'arrowDown':
            case 'arrowRight':
            case 'arrowLeft':
                drawArrow(item);
                break;
            case 'diamond':
                drawDiamond(item);
                break;
            case 'image':
                drawImage(item);
                break;
            case '3dModel':
                draw3DModel(item);
                break;
            case 'polyline':
            case 'squarePoliline':
            case 'circlePoliline':
                drawPolyline(item);
                break;
            case 'bezier':
                drawBezier(item);
                break;
            case 'label':
                drawLabel(item);
                break;
            default:
                console.warn(`Tipo de item desconhecido: ${item.type}`);
        }
    });
}

/* =====================================================================
     DRAW FUNCTIONS
===================================================================== */
function drawLine(item) {
    ctx.value.beginPath();
    ctx.value.moveTo(item.position.x, item.position.y);
    ctx.value.lineTo(item.endPosition.x, item.endPosition.y);
    ctx.value.strokeStyle = item.strokeColor || '#000';
    ctx.value.lineWidth = item.strokeWidth || 2;
    ctx.value.stroke();
}

function drawRectangle(item) {
    ctx.value.beginPath();
    ctx.value.rect(item.position.x, item.position.y, item.width, item.height);
    ctx.value.fillStyle = item.color || 'transparent';
    ctx.value.fill();
    ctx.value.strokeStyle = item.borderColor || '#000';
    ctx.value.lineWidth = item.borderWidth || 1;
    ctx.value.stroke();
}

function drawCircle(item) {
    ctx.value.beginPath();
    ctx.value.arc(
        item.position.x + item.radius,
        item.position.y + item.radius,
        item.radius,
        0,
        2 * Math.PI,
    );
    ctx.value.fillStyle = item.color || 'transparent';
    ctx.value.fill();
    ctx.value.strokeStyle = item.borderColor || '#000';
    ctx.value.lineWidth = item.borderWidth || 1;
    ctx.value.stroke();
}

function drawEllipse(item) {
    ctx.value.beginPath();
    ctx.value.ellipse(
        item.position.x + item.radiusX,
        item.position.y + item.radiusY,
        item.radiusX,
        item.radiusY,
        item.rotation || 0,
        0,
        2 * Math.PI,
    );
    ctx.value.fillStyle = item.color || 'transparent';
    ctx.value.fill();
    ctx.value.strokeStyle = item.borderColor || '#000';
    ctx.value.lineWidth = item.borderWidth || 1;
    ctx.value.stroke();
}

function drawTriangle(item) {
    ctx.value.beginPath();
    ctx.value.moveTo(item.position.x + item.width / 2, item.position.y);
    ctx.value.lineTo(item.position.x + item.width, item.position.y + item.height);
    ctx.value.lineTo(item.position.x, item.position.y + item.height);
    ctx.value.closePath();
    ctx.value.fillStyle = item.color || 'transparent';
    ctx.value.fill();
    ctx.value.strokeStyle = item.borderColor || '#000';
    ctx.value.lineWidth = item.borderWidth || 1;
    ctx.value.stroke();
}

function drawStar(item) {
    const outerRadius = item.outerRadius;
    const innerRadius = item.innerRadius;
    const points = item.points;
    const rotation = item.rotation || 0;

    ctx.value.beginPath();
    const step = Math.PI / points;
    ctx.value.moveTo(
        item.position.x + outerRadius * Math.cos(rotation),
        item.position.y + outerRadius * Math.sin(rotation),
    );
    for (let i = 1; i < 2 * points; i++) {
        const radius = i % 2 === 0 ? outerRadius : innerRadius;
        ctx.value.lineTo(
            item.position.x + radius * Math.cos(rotation + i * step),
            item.position.y + radius * Math.sin(rotation + i * step),
        );
    }
    ctx.value.closePath();
    ctx.value.fillStyle = item.color || 'transparent';
    ctx.value.fill();
    ctx.value.strokeStyle = item.borderColor || '#000';
    ctx.value.lineWidth = item.borderWidth || 1;
    ctx.value.stroke();
}

function drawPolygon(item, sides = 5) {
    const radius = item.radius;
    const rotation = item.rotation || 0;

    ctx.value.beginPath();
    for (let i = 0; i < sides; i++) {
        const angle = ((2 * Math.PI) / sides) * i + rotation;
        const x = item.position.x + radius * Math.cos(angle);
        const y = item.position.y + radius * Math.sin(angle);
        if (i === 0) {
            ctx.value.moveTo(x, y);
        } else {
            ctx.value.lineTo(x, y);
        }
    }
    ctx.value.closePath();
    ctx.value.fillStyle = item.color || 'transparent';
    ctx.value.fill();
    ctx.value.strokeStyle = item.borderColor || '#000';
    ctx.value.lineWidth = item.borderWidth || 1;
    ctx.value.stroke();
}

function drawChevron(item) {
    const { x, y } = item.position;
    const w = item.width;
    const h = item.height;

    ctx.value.beginPath();
    switch (item.type) {
        case 'chevronLeft':
            ctx.value.moveTo(x + w, y);
            ctx.value.lineTo(x, y + h / 2);
            ctx.value.lineTo(x + w, y + h);
            break;
        case 'chevronRight':
            ctx.value.moveTo(x, y);
            ctx.value.lineTo(x + w, y + h / 2);
            ctx.value.lineTo(x, y + h);
            break;
        case 'chevronUp':
            ctx.value.moveTo(x, y + h);
            ctx.value.lineTo(x + w / 2, y);
            ctx.value.lineTo(x + w, y + h);
            break;
        case 'chevronDown':
            ctx.value.moveTo(x, y);
            ctx.value.lineTo(x + w / 2, y + h);
            ctx.value.lineTo(x + w, y);
            break;
        default:
            break;
    }
    ctx.value.strokeStyle = item.color || '#000';
    ctx.value.lineWidth = item.borderWidth || 1;
    ctx.value.stroke();
}

function drawHalfStar(item) {
    const outerRadius = item.outerRadius;
    const innerRadius = item.innerRadius;
    const points = item.points;
    const rotation = item.rotation || 0;

    ctx.value.beginPath();
    const step = Math.PI / points;
    ctx.value.moveTo(
        item.position.x + outerRadius * Math.cos(rotation),
        item.position.y + outerRadius * Math.sin(rotation),
    );
    for (let i = 1; i < points; i++) {
        const angle = rotation + ((2 * Math.PI) / points) * i;
        ctx.value.lineTo(
            item.position.x + outerRadius * Math.cos(angle),
            item.position.y + outerRadius * Math.sin(angle),
        );
    }
    ctx.value.lineTo(
        item.position.x + innerRadius * Math.cos(rotation + Math.PI),
        item.position.y + innerRadius * Math.sin(rotation + Math.PI),
    );
    ctx.value.closePath();
    ctx.value.fillStyle = item.color || 'transparent';
    ctx.value.fill();
    ctx.value.strokeStyle = item.borderColor || '#000';
    ctx.value.lineWidth = item.borderWidth || 1;
    ctx.value.stroke();
}

function drawArrow(item) {
    const { x, y } = item.position;
    const w = item.width;
    const h = item.height;
    const direction = item.direction || 'up';

    ctx.value.beginPath();
    switch (direction) {
        case 'up':
            ctx.value.moveTo(x + w / 2, y);
            ctx.value.lineTo(x + w, y + h);
            ctx.value.lineTo(x + (3 * w) / 4, y + h);
            ctx.value.lineTo(x + (3 * w) / 4, y + h * 1.5);
            ctx.value.lineTo(x + w / 4, y + h * 1.5);
            ctx.value.lineTo(x + w / 4, y + h);
            ctx.value.lineTo(x, y + h);
            ctx.value.lineTo(x + w * 0.5, y + h);
            ctx.value.closePath();
            break;
        case 'down':
            ctx.value.moveTo(x + w / 2, y + h);
            ctx.value.lineTo(x + w, y);
            ctx.value.lineTo(x + (3 * w) / 4, y);
            ctx.value.lineTo(x + (3 * w) / 4, y - h / 2);
            ctx.value.lineTo(x + w / 4, y - h / 2);
            ctx.value.lineTo(x + w / 4, y);
            ctx.value.lineTo(x, y);
            ctx.value.lineTo(x + w * 0.5, y);
            ctx.value.closePath();
            break;
        case 'right':
            ctx.value.moveTo(x + w, y + h / 2);
            ctx.value.lineTo(x, y);
            ctx.value.lineTo(x, y + (3 * h) / 4);
            ctx.value.lineTo(x - w / 2, y + (3 * h) / 4);
            ctx.value.lineTo(x - w / 2, y + h / 4);
            ctx.value.lineTo(x, y + h / 4);
            ctx.value.lineTo(x, y + h);
            ctx.value.closePath();
            break;
        case 'left':
            ctx.value.moveTo(x, y + h / 2);
            ctx.value.lineTo(x + w, y);
            ctx.value.lineTo(x + w, y + (3 * h) / 4);
            ctx.value.lineTo(x + (3 * w) / 2, y + (3 * h) / 4);
            ctx.value.lineTo(x + (3 * w) / 2, y + h / 4);
            ctx.value.lineTo(x + w, y + h / 4);
            ctx.value.lineTo(x + w, y + h);
            ctx.value.closePath();
            break;
        default:
            break;
    }
    ctx.value.fillStyle = item.color || '#000';
    ctx.value.fill();
    ctx.value.strokeStyle = item.borderColor || '#000';
    ctx.value.lineWidth = item.borderWidth || 1;
    ctx.value.stroke();
}

function drawDiamond(item) {
    const { x, y } = item.position;
    const w = item.width;
    const h = item.height;

    ctx.value.beginPath();
    ctx.value.moveTo(x + w / 2, y);
    ctx.value.lineTo(x + w, y + h / 2);
    ctx.value.lineTo(x + w / 2, y + h);
    ctx.value.lineTo(x, y + h / 2);
    ctx.value.closePath();
    ctx.value.fillStyle = item.color || 'transparent';
    ctx.value.fill();
    ctx.value.strokeStyle = item.borderColor || '#000';
    ctx.value.lineWidth = item.borderWidth || 1;
    ctx.value.stroke();
}

function drawImage(item) {
    const img = new Image();
    img.src = item.imageUrl;
    img.onload = () => {
        ctx.value.drawImage(img, item.position.x, item.position.y, item.width, item.height);
    };
}

function draw3DModel(item) {
    // Placeholder
    console.warn('Desenhar modelos 3D não implementado.');
}

function drawPolyline(item) {
    ctx.value.beginPath();
    if (item.path && item.path.length > 0) {
        // Se desejado, pode-se desenhar relativo a position, mas aqui está "absoluto"
        ctx.value.moveTo(item.path[0].x, item.path[0].y);
        item.path.forEach((point) => {
            ctx.value.lineTo(point.x, point.y);
        });
    }
    ctx.value.strokeStyle = item.strokeColor || '#000';
    ctx.value.lineWidth = item.strokeWidth || 2;
    ctx.value.stroke();
}

function drawBezier(item) {
    ctx.value.beginPath();
    if (item.segments && item.segments.length > 0) {
        ctx.value.moveTo(item.segments[0].x, item.segments[0].y);
        for (let i = 1; i < item.segments.length; i++) {
            const seg = item.segments[i];
            ctx.value.bezierCurveTo(seg.cx1, seg.cy1, seg.cx2, seg.cy2, seg.x, seg.y);
        }
    }
    ctx.value.strokeStyle = item.strokeColor || '#000';
    ctx.value.lineWidth = item.strokeWidth || 2;
    ctx.value.stroke();
}

function drawLabel(item) {
    ctx.value.beginPath();
    ctx.value.font = `${item.fontSize || 16}px Arial`;
    ctx.value.fillStyle = item.color || '#000';
    // Se item.label existir, use-o. Caso contrário, fallback para item.text
    const textContent = item.label || item.text || 'Texto';
    ctx.value.fillText(textContent, item.position.x, item.position.y);
}

/* =====================================================================
     NOTIFICAÇÕES
===================================================================== */
function showNotification(msg, type = 'success') {
    notification.value = { message: msg, type };
    setTimeout(() => {
        notification.value = null;
    }, 2000);
}

/* =====================================================================
     MOUSE E TECLADO (DRAG & DROP, ETC.)
===================================================================== */
let isCtrlPressed = false;
let isExternalDrop = false;

window.addEventListener('keydown', handleKeyDown);
window.addEventListener('keyup', handleKeyUp);

function handleKeyDown(e) {
    if (e.ctrlKey) {
        isCtrlPressed = true;
    }
}
function handleKeyUp(e) {
    if (!e.ctrlKey) {
        isCtrlPressed = false;
    }
}

function handleDragEnter(e) {
    if (e.dataTransfer.types.includes('Files')) {
        isExternalDrop = true;
    }
}
function handleDragLeave() {
    isExternalDrop = false;
}
function handleDragOver(e) {
    e.preventDefault();
}
function handleDrop(e) {
    e.preventDefault();
    if (isExternalDrop && isCtrlPressed) {
        const file = e.dataTransfer.files[0];
        if (!file) return;
        const extension = file.name.split('.').pop().toLowerCase();
        const modelExtensions = ['obj', 'gltf', 'glb'];

        if (file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onload = (ev) => {
                const imgUrl = ev.target.result;
                addImageAsItem(imgUrl);
            };
            reader.readAsDataURL(file);
            showNotification('Imagem adicionada.', 'success');
        } else if (modelExtensions.includes(extension)) {
            const modelUrl = URL.createObjectURL(file);
            add3DModelAsItem(modelUrl);
            showNotification('Modelo 3D adicionado.', 'success');
        } else {
            showNotification('Arquivo não suportado.', 'error');
        }
    } else {
        showNotification('Para soltar arquivo, mantenha CTRL.', 'warning');
    }
    isExternalDrop = false;
}

/** Se paintType="paint", faz flood fill */
function advancedPaintFill(x, y) {
    const clickedItem = getClickedItem({ clientX: x, clientY: y });
    if (clickedItem) {
        clickedItem.color = props.selectedColor;
        emits('update-items', { ...clickedItem });
        emits('save-state');
        showNotification('Pintura avançada aplicada!', 'success');
    } else {
        showNotification('Nenhum item encontrado para pintura.', 'error');
    }
}

/* =====================================================================
     ADD ITEM (IMAGENS, MODELOS)
===================================================================== */
function addImageAsItem(url) {
    const newItem = {
        id: Date.now(),
        type: 'image',
        imageUrl: url,
        position: { x: 100, y: 100 },
        width: 200,
        height: 200,
    };
    emits('add-item', newItem);
    emits('save-state');
}

function add3DModelAsItem(url) {
    const newItem = {
        id: Date.now(),
        type: '3dModel',
        modelUrl: url,
        position: { x: 150, y: 150 },
        scale: { x: 1, y: 1, z: 1 },
        rotation: { x: 0, y: 0, z: 0 },
    };
    emits('add-item', newItem);
    emits('save-state');
}

/* =====================================================================
     DESENHO E PINTURA
===================================================================== */
function handleMouseDown(e) {
    const rect = drawingCanvas.value.getBoundingClientRect();
    const x = (e.clientX - rect.left) / zoomLevel.value - panOffset.value.x;
    const y = (e.clientY - rect.top) / zoomLevel.value - panOffset.value.y;

    // Se paintMode está ativo
    if (props.paintMode) {
        if (props.paintType === 'paint') {
            advancedPaintFill(x, y);
        } else {
            const clicked = getClickedItem(e);
            if (clicked) {
                clicked.color = props.selectedColor;
                emits('update-items', { ...clicked });
                emits('save-state');
            }
        }
        return;
    }

    // Se não é paintMode, fluxo normal
    if (props.drawingMode) {
        isDrawing.value = true;
        startPoint.value = { x, y };
    } else {
        const target = e.target.closest('.draggable-item');
        if (target) {
            const itemId = parseInt(target.dataset.id, 10);
            if (!isNaN(itemId)) {
                isDragging.value = true;
                dragItemId.value = itemId;
                dragStart.value = { x: e.clientX, y: e.clientY };
            }
        } else {
            isPanning.value = true;
            panStart.value = { x: e.clientX, y: e.clientY };
        }
    }
}

function handleMouseMove(e) {
    const pos = getMousePos(e);
    lastMousePosition.value = pos;

    // Desenhando
    if (isDrawing.value && props.drawingMode) {
        if (props.drawingMode === 'freehand') {
            currentPath.value.push({ x: pos.x, y: pos.y });
            drawFreehandPreview();
        } else if (props.drawingMode === 'bezier') {
            const li = currentPath.value.length - 1;
            if (li >= 0) {
                currentPath.value[li].cx2 = pos.x;
                currentPath.value[li].cy2 = pos.y;
            }
            drawBezierPreview();
        } else {
            drawNormalPreview(pos.x, pos.y);
        }
        return;
    }

    // Arrastando item
    if (isDragging.value && dragItemId.value != null) {
        const dx = (e.clientX - dragStart.value.x) / zoomLevel.value;
        const dy = (e.clientY - dragStart.value.y) / zoomLevel.value;

        const it = props.items.find((i) => i.id === dragItemId.value);
        if (it) {
            switch (it.type) {
                case 'line': {
                    it.position.x += dx;
                    it.position.y += dy;
                    it.endPosition.x += dx;
                    it.endPosition.y += dy;
                    break;
                }
                case 'polyline':
                case 'squarePoliline':
                case 'circlePoliline':
                case 'bezier': {
                    // AO INVÉS de mover ponto a ponto, movemos a position se quisermos permitir dimension controllers
                    it.path = it.path.map((pt) => ({
                        x: pt.x + dx,
                        y: pt.y + dy,
                    }));
                    break;
                }
                case 'image':
                case '3dModel':
                case 'label':
                default: {
                    it.position.x += dx;
                    it.position.y += dy;
                    break;
                }
            }
            emits('update-items', { ...it });
            dragStart.value = { x: e.clientX, y: e.clientY };
        }
        return;
    }

    // Panning
    if (isPanning.value) {
        const dx = e.clientX - panStart.value.x;
        const dy = e.clientY - panStart.value.y;
        panOffset.value.x += dx / zoomLevel.value;
        panOffset.value.y += dy / zoomLevel.value;
        panStart.value = { x: e.clientX, y: e.clientY };
        emits('save-state');
    }
}

function handleMouseUp() {
    if (props.drawingMode && isDrawing.value) {
        if (props.drawingMode === 'freehand') {
            const newItem = {
                id: Date.now(),
                type: 'polyline',
                path: [...currentPath.value],
                strokeColor: props.selectedColor || '#000',
                strokeWidth: 2,
                position: { x: 0, y: 0 },
            };
            emits('add-item', newItem);
            emits('save-state');
            currentPath.value = [];
            isDrawing.value = false;
        } else if (props.drawingMode === 'bezier') {
            const newItem = {
                id: Date.now(),
                type: 'bezier',
                segments: [...currentPath.value],
                strokeColor: props.selectedColor || '#000',
                strokeWidth: 2,
                position: { x: 0, y: 0 },
            };
            emits('add-item', newItem);
            emits('save-state');
            currentPath.value = [];
            isDrawing.value = false;
        } else {
            finalizeDrawingNormal();
            isDrawing.value = false;
        }
    }

    if (isPanning.value) {
        isPanning.value = false;
        emits('save-state');
    }

    if (isDragging.value) {
        isDragging.value = false;
        dragItemId.value = null;
        emits('save-state');
    }
}

/* =====================================================================
   PRÉ-VISUALIZAÇÃO DE FREEHAND / BEZIER / NORMAL
===================================================================== */
function drawFreehandPreview() {
    if (!ctx.value || !isDrawing.value) return;
    ctx.value.clearRect(0, 0, drawingCanvas.value.width, drawingCanvas.value.height);
    ctx.value.beginPath();
    if (currentPath.value.length > 0) {
        ctx.value.moveTo(currentPath.value[0].x, currentPath.value[0].y);
        for (let i = 1; i < currentPath.value.length; i++) {
            ctx.value.lineTo(currentPath.value[i].x, currentPath.value[i].y);
        }
    }
    ctx.value.strokeStyle = props.selectedColor;
    ctx.value.lineWidth = 2;
    ctx.value.stroke();
}

function drawBezierPreview() {
    if (!ctx.value || !isDrawing.value) return;
    ctx.value.clearRect(0, 0, drawingCanvas.value.width, drawingCanvas.value.height);
    ctx.value.beginPath();
    if (currentPath.value.length > 0) {
        ctx.value.moveTo(currentPath.value[0].x, currentPath.value[0].y);
        for (let i = 1; i < currentPath.value.length; i++) {
            const seg = currentPath.value[i];
            ctx.value.bezierCurveTo(seg.cx1, seg.cy1, seg.cx2, seg.cy2, seg.x, seg.y);
        }
    }
    ctx.value.strokeStyle = props.selectedColor;
    ctx.value.lineWidth = 2;
    ctx.value.stroke();
}

function drawNormalPreview(x, y) {
    ctx.value.clearRect(0, 0, drawingCanvas.value.width, drawingCanvas.value.height);
    ctx.value.beginPath();

    switch (props.drawingMode) {
        case 'line': {
            ctx.value.moveTo(startPoint.value.x, startPoint.value.y);
            ctx.value.lineTo(x, y);
            ctx.value.strokeStyle = props.selectedColor;
            ctx.value.lineWidth = 2;
            ctx.value.stroke();
            break;
        }
        case 'shape':
        case 'rectangle': {
            const w = x - startPoint.value.x;
            const h = y - startPoint.value.y;
            ctx.value.rect(startPoint.value.x, startPoint.value.y, w, h);
            ctx.value.fillStyle = props.selectedColor;
            ctx.value.fill();
            ctx.value.strokeStyle = '#000';
            ctx.value.lineWidth = 1;
            ctx.value.stroke();
            break;
        }
        default:
            break;
    }
}

/* =====================================================================
   FINALIZAR DESENHO NORMAL
===================================================================== */
function finalizeDrawingNormal() {
    const endX = lastMousePosition.value.x;
    const endY = lastMousePosition.value.y;
    const startX = startPoint.value.x;
    const startY = startPoint.value.y;

    const width = endX - startX;
    const height = endY - startY;

    const position = {
        x: width < 0 ? endX : startX,
        y: height < 0 ? endY : startY,
    };

    const absWidth = Math.abs(width);
    const absHeight = Math.abs(height);

    let newItem = null;

    switch (props.drawingMode) {
        case 'line': {
            newItem = {
                id: Date.now(),
                type: 'line',
                position: { x: startX, y: startY },
                endPosition: { x: endX, y: endY },
                strokeColor: props.selectedColor,
                strokeWidth: 2,
                width: absWidth,
                height: absHeight,
            };
            break;
        }
        case 'rectangle': {
            newItem = {
                id: Date.now(),
                type: 'rectangle',
                position,
                width: absWidth,
                height: absHeight,
                color: props.selectedColor,
                borderColor: '#000',
                borderWidth: 1,
            };
            break;
        }
        case 'circle': {
            const radius = Math.min(absWidth, absHeight) / 2;
            newItem = {
                id: Date.now(),
                type: 'circle',
                position: {
                    x: position.x + radius,
                    y: position.y + radius,
                },
                radius,
                color: props.selectedColor,
                borderColor: '#000',
                borderWidth: 1,
            };
            break;
        }
        /* Quadrado poliline */
        case 'squarePoliline': {
            const w = endX - startX;
            const h = endY - startY;
            const posX = w < 0 ? endX : startX;
            const posY = h < 0 ? endY : startY;
            const absW = Math.abs(w);
            const absH = Math.abs(h);
            const side = Math.min(absW, absH);

            // Iniciar com 30 pontos (ou props.controllerCount)
            const initialNumPoints = 30;

            // Gera path
            const adjustedNumPoints = Math.max(4, initialNumPoints);
            const pointsPerSide = Math.floor(adjustedNumPoints / 4);
            const remainder = adjustedNumPoints % 4;

            const path = [];
            // Superior
            for (let i = 0; i < pointsPerSide + (remainder > 0 ? 1 : 0); i++) {
                const t = i / (pointsPerSide + (remainder > 0 ? 1 : 0));
                const x = posX + t * side;
                const y = posY;
                path.push({ x, y });
            }
            // Direito
            for (let i = 0; i < pointsPerSide + (remainder > 1 ? 1 : 0); i++) {
                const t = i / (pointsPerSide + (remainder > 1 ? 1 : 0));
                const x = posX + side;
                const y = posY + t * side;
                path.push({ x, y });
            }
            // Inferior
            for (let i = 0; i < pointsPerSide + (remainder > 2 ? 1 : 0); i++) {
                const t = i / (pointsPerSide + (remainder > 2 ? 1 : 0));
                const x = posX + side - t * side;
                const y = posY + side;
                path.push({ x, y });
            }
            // Esquerdo
            for (let i = 0; i < pointsPerSide; i++) {
                const t = i / pointsPerSide;
                const x = posX;
                const y = posY + side - t * side;
                path.push({ x, y });
            }
            // Fecha caminho
            path.push(path[0]);

            newItem = {
                id: Date.now(),
                type: 'squarePoliline',
                path,
                position: { x: 0, y: 0 },
                strokeColor: '#000',
                strokeWidth: 2,
                width: side,
                height: side,
            };
            break;
        }
        /* Círculo poliline */
        case 'circlePoliline': {
            const w = endX - startX;
            const h = endY - startY;
            const posX = w < 0 ? endX : startX;
            const posY = h < 0 ? endY : startY;
            const absW = Math.abs(w);
            const absH = Math.abs(h);
            const radius = Math.min(absW, absH) / 2;
            const centerX = posX + radius;
            const centerY = posY + radius;

            // Iniciar com 30 pontos
            const initialNumPoints = 30;

            const path = [];
            for (let i = 0; i < initialNumPoints; i++) {
                const angle = (i / initialNumPoints) * 2 * Math.PI;
                const px = centerX + radius * Math.cos(angle);
                const py = centerY + radius * Math.sin(angle);
                path.push({ x: px, y: py });
            }
            path.push({ x: path[0].x, y: path[0].y });

            newItem = {
                id: Date.now(),
                type: 'circlePoliline',
                path,
                position: { x: 0, y: 0 },
                strokeColor: '#000',
                strokeWidth: 2,
                width: radius * 2,
                height: radius * 2,
            };
            break;
        }
        default:
            break;
    }

    if (newItem) {
        emits('add-item', newItem);
        emits('save-state');
    }

    if (ctx.value && drawingCanvas.value) {
        ctx.value.clearRect(0, 0, drawingCanvas.value.width, drawingCanvas.value.height);
    }
}

/* =====================================================================
   Funções updateSquarePoliline / updateCirclePoliline
   => Apenas ADICIONAM/REMOVEM pontos sem resetar props de posicionamento
===================================================================== */
function updateSquarePoliline(newCount) {
    // Se não for squarePoliline ou count <4, sai
    if (!props.selectedItem || props.selectedItem.type !== 'squarePoliline' || newCount < 4) return;

    // Mantém position, width, height
    const sideLength = Math.min(props.selectedItem.width, props.selectedItem.height);

    // Precisamos apenas adicionar/remover pontos do array path
    // sem mudar a "posição" base da figura
    const path = [...props.selectedItem.path];
    const totalPoints = path.length;

    // Se o path atual tiver <4, não é nem quadrado, mas OK
    // Ajusta newCount para múltiplo de 4 se quiser rigidez
    // ou não, se quiser permitir qualquer valor
    const adjustedCount = Math.max(4, newCount);

    // Diferença
    if (adjustedCount > totalPoints) {
        // Adicionar
        for (let i = totalPoints; i < adjustedCount; i++) {
            // Adiciona ponto no final, por ex. duplicando o último
            const last = path[path.length - 1] || { x: 0, y: 0 };
            path.push({ x: last.x, y: last.y });
        }
    } else if (adjustedCount < totalPoints) {
        // Remover
        path.splice(adjustedCount, totalPoints - adjustedCount);
    }

    // Garante que o path feche
    path[path.length - 1] = { ...path[0] };

    const updatedItem = {
        ...props.selectedItem,
        path,
    };
    emits('update-items', updatedItem);
}

function updateCirclePoliline(newCount) {
    if (!props.selectedItem || props.selectedItem.type !== 'circlePoliline') return;
    if (newCount < 6) return; // Mínimo 6 p/ "circle"

    // Mantém pos e width/height
    const radius = Math.min(props.selectedItem.width, props.selectedItem.height) / 2;
    const centerX = props.selectedItem.position.x + radius;
    const centerY = props.selectedItem.position.y + radius;

    const path = [...props.selectedItem.path];
    const currentCount = path.length;

    // Precisamos apenas "ajustar" total
    if (newCount > currentCount) {
        // Adiciona pontos
        for (let i = currentCount; i < newCount; i++) {
            // Insere novos pontos próximo ao final
            // Uma forma simples é "espalhar" ou duplicar o último
            const angle = (i / newCount) * 2 * Math.PI;
            const px = centerX + radius * Math.cos(angle);
            const py = centerY + radius * Math.sin(angle);
            path.splice(path.length - 1, 0, { x: px, y: py });
        }
    } else if (newCount < currentCount) {
        // Remove
        path.splice(newCount, currentCount - newCount);
    }
    // Fecha
    path[path.length - 1] = { x: path[0].x, y: path[0].y };

    const updatedItem = {
        ...props.selectedItem,
        path,
    };
    emits('update-items', updatedItem);
}

/* =====================================================================
   CONTROLADORES
===================================================================== */
function toggleControllers() {
    emits('toggle-controllers');
}

function emitControllerMode() {
    emits('change-controller-mode', localControllerMode.value);
}

function emitControllerCount() {
    emits('update-controller-count', localControllerCount.value);
}

function toggleRoundedLine() {
    if (props.selectedItem) {
        props.selectedItem.roundedLine = !props.selectedItem.roundedLine;
        emits('update-items', { ...props.selectedItem });
    }
}

/* =====================================================================
   AUXILIARES
===================================================================== */
function getMousePos(e) {
    const rect = drawingArea.value.getBoundingClientRect();
    const x = (e.clientX - rect.left) / zoomLevel.value - panOffset.value.x;
    const y = (e.clientY - rect.top) / zoomLevel.value - panOffset.value.y;
    return { x, y };
}

function handleMouseLeave() {
    if (props.drawingMode && isDrawing.value) {
        isDrawing.value = false;
    }
    if (isPanning.value) {
        isPanning.value = false;
        emits('save-state');
    }
    if (isDragging.value) {
        isDragging.value = false;
        dragItemId.value = null;
        emits('save-state');
    }
}

function handleZoom(e) {
    const delta = e.deltaY > 0 ? -0.1 : 0.1;
    zoomLevel.value = Math.min(Math.max(zoomLevel.value + delta, 0.5), 2);
    renderCanvas();
}

function selectItem(item) {
    selectedItemBackground.value = item;
    emits('select-item', item);
}

function updateItem(updatedItem) {
    emits('update-items', updatedItem);
}

function bringToFront(item) {
    emits('bring-to-front', item);
}

function sendToBack(item) {
    emits('send-to-back', item);
}

function getClickedItem(e) {
    const target = e.target.closest('.draggable-item');
    if (target) {
        const itemId = parseInt(target.dataset.id, 10);
        if (!isNaN(itemId)) {
            return props.items.find((i) => i.id === itemId);
        }
    }
    return null;
}
</script>

<style scoped>
.drawing-area {
    position: relative;
    background-color: #f8f8f8;
    overflow: hidden;
    user-select: none;
}

/* Estilos para os controles */
.controls {
    position: absolute;
    top: 10px;
    left: 10px;
    background: rgba(255, 255, 255, 0.9);
    padding: 10px;
    border-radius: 6px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.3);
    z-index: 100;
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.controls button {
    padding: 6px 12px;
    font-size: 14px;
    cursor: pointer;
    background-color: #42A5F5;
    color: white;
    border: none;
    border-radius: 4px;
}

.controller-options {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.controls label {
    display: flex;
    align-items: center;
    font-size: 14px;
    cursor: pointer;
}

.controls input[type="checkbox"],
.controls input[type="number"],
.controls select {
    margin-right: 6px;
    cursor: pointer;
}

.controls input[type="number"] {
    width: 60px;
}

/* Canvas de desenho */
.drawing-canvas {
    position: absolute;
    top: 0;
    left: 0;
    pointer-events: none;
    /* z-index: 10; */
    width: 100%;
    height: 100%;
}

/* Notificações */
.notification {
    position: fixed;
    bottom: 20px;
    right: 20px;
    padding: 10px 20px;
    color: white;
    background-color: green;
    border-radius: 5px;
}

.notification.error {
    background-color: red;
}

.notification.warning {
    background-color: orange;
}

/* Responsividade */
@media (max-width: 600px) {
    .controls {
        top: 5px;
        left: 5px;
        padding: 8px;
    }

    .controls button {
        padding: 4px 8px;
        font-size: 12px;
    }

    .controller-options label {
        font-size: 12px;
    }

    .controller-options input[type="number"] {
        width: 50px;
    }

    .notification {
        bottom: 10px;
        right: 10px;
        padding: 8px 16px;
        font-size: 12px;
    }
}
</style>
