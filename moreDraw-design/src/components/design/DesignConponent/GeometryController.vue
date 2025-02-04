<template>
    <div
        v-if="shouldShowControllers"
        class="geometry-controller"
    >
        <!-- BOUNDING BOX -->
        <div
            class="bounding-box"
            :style="boundingBoxStyle"
        >
            <!-- Se estivermos em modo dimension, mostrar handles de redimensionamento/rotação -->
            <template v-if="localControllerMode === 'dimension'">
                <!-- Canto Superior Esquerdo -->
                <div
                    class="resize-handle top-left"
                    @mousedown.stop.prevent="startResize($event, 'top-left')"
                />
                <!-- Canto Superior Direito -->
                <div
                    class="resize-handle top-right"
                    @mousedown.stop.prevent="startResize($event, 'top-right')"
                />
                <!-- Canto Inferior Esquerdo -->
                <div
                    class="resize-handle bottom-left"
                    @mousedown.stop.prevent="startResize($event, 'bottom-left')"
                />
                <!-- Canto Inferior Direito -->
                <div
                    class="resize-handle bottom-right"
                    @mousedown.stop.prevent="startResize($event, 'bottom-right')"
                />
                <!-- Lados (top, bottom, left, right) podem ser adicionados se quiser -->
            </template>

            <!-- Se estivermos em modo curvature, mostrar handles de curvatura -->
            <template v-else-if="localControllerMode === 'curvature'">
                <!-- Exemplo: cada ponto do path/segments vira um handle -->
                <div
                    v-for="(pt, idx) in curvePoints"
                    :key="idx"
                    class="curve-handle"
                    :style="curveHandleStyle(pt)"
                    @mousedown.stop.prevent="startCurveDrag(pt, idx, $event)"
                />
            </template>
        </div>

        <!-- OPÇÕES DE ESTILO (linecap, linejoin, strokeDash, etc.) -->
        <div class="style-options">
            <label>
                Line Cap:
                <select
                    v-model="lineCap"
                    @change="emitStyleChange"
                >
                    <option value="butt">Butt</option>
                    <option value="round">Round</option>
                    <option value="square">Square</option>
                </select>
            </label>
            <label>
                Line Join:
                <select
                    v-model="lineJoin"
                    @change="emitStyleChange"
                >
                    <option value="miter">Miter</option>
                    <option value="round">Round</option>
                    <option value="bevel">Bevel</option>
                </select>
            </label>
            <label>
                Stroke Dash:
                <select
                    v-model="lineDash"
                    @change="emitStyleChange"
                >
                    <option value="solid">Solid</option>
                    <option value="dashed">Dashed</option>
                    <option value="dotted">Dotted</option>
                </select>
            </label>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';

/*
    PROPS:
    - selectedItem: objeto do item selecionado (line, polyline, rectangle, etc.)
    - controllerMode: string ('dimension' ou 'curvature' ou 'none')
    - showControllers: boolean (true/false) indicando se deve exibir ou não
  */
const props = defineProps({
    selectedItem: {
        type: Object,
        default: null,
    },
    controllerMode: {
        type: String,
        default: 'dimension',
    },
    showControllers: {
        type: Boolean,
        default: false,
    },
});

/*
    EMITS:
    - update-item: quando algo mudar no item (tamanho, posição, path, etc.)
  */
const emits = defineEmits(['update-item']);

/* ==================== REFS e COMPUTED ==================== */
const localControllerMode = ref(props.controllerMode);

/*
    Estilos de linha (cap, join, dash):
    Você pode armazenar no item ou em variáveis locais e emitir depois.
  */
const lineCap = ref('butt');
const lineJoin = ref('miter');
const lineDash = ref('solid');

/*
    Computed: se deve mostrar controladores (devemos ignorar se selectedItem é null,
    ou se item é line e controllerMode = dimension, etc.)
  */
const shouldShowControllers = computed(() => {
    if (!props.showControllers || !props.selectedItem) return false;

    // Se o item é line e controllerMode for dimension => ignoramos
    if (props.selectedItem.type === 'line' && props.controllerMode === 'dimension') {
        return false;
    }
    // Se for 'none'
    if (props.controllerMode === 'none') {
        return false;
    }

    return true;
});

/*
    Computed do bounding box (minX, maxX, minY, maxY) => style absolute
    para que possamos posicionar o "bounding-box" no lugar certo
  */
const boundingBoxStyle = computed(() => {
    if (!props.selectedItem) {
        return { display: 'none' };
    }

    // Cálculo genérico do bounding box
    let minX = props.selectedItem.position?.x || 0;
    let minY = props.selectedItem.position?.y || 0;
    let maxX = minX + (props.selectedItem.width || 0);
    let maxY = minY + (props.selectedItem.height || 0);

    // Se for polyline ou bezier, calculamos min/max dos pontos
    if (props.selectedItem.type === 'polyline' && props.selectedItem.path) {
        const xs = props.selectedItem.path.map((p) => p.x);
        const ys = props.selectedItem.path.map((p) => p.y);
        minX = Math.min(...xs);
        maxX = Math.max(...xs);
        minY = Math.min(...ys);
        maxY = Math.max(...ys);
    } else if (props.selectedItem.type === 'bezier' && props.selectedItem.segments) {
        // Pegar todos os anchors e control points
        const allX = [];
        const allY = [];
        props.selectedItem.segments.forEach((seg) => {
            allX.push(seg.x, seg.cx1, seg.cx2);
            allY.push(seg.y, seg.cy1, seg.cy2);
        });
        minX = Math.min(...allX);
        maxX = Math.max(...allX);
        minY = Math.min(...allY);
        maxY = Math.max(...allY);
    }

    const width = maxX - minX;
    const height = maxY - minY;

    // Exemplo de style para posicionar bounding-box com absolute
    return {
        position: 'absolute',
        left: `${minX}px`,
        top: `${minY}px`,
        width: `${width}px`,
        height: `${height}px`,
        border: '1px dashed #00F', /* Para debug do bounding box */
    };
});

/*
    Curve points (para polylines ou beziers) => "curvature" handles
  */
const curvePoints = computed(() => {
    if (!props.selectedItem || localControllerMode.value !== 'curvature') {
        return [];
    }

    if (props.selectedItem.type === 'polyline') {
        return props.selectedItem.path || [];
    } if (props.selectedItem.type === 'bezier') {
        // Se for bezier, cada segment tem x,y e cx1,cy1 e cx2,cy2
        // podemos retornar um array de todos esses pontos se quiser handles
        const result = [];
        props.selectedItem.segments.forEach((seg, sIdx) => {
        // Anchor
            result.push({ x: seg.x, y: seg.y, type: 'anchor', segIndex: sIdx });
            // Control points
            result.push({ x: seg.cx1, y: seg.cy1, type: 'control1', segIndex: sIdx });
            result.push({ x: seg.cx2, y: seg.cy2, type: 'control2', segIndex: sIdx });
        });
        return result;
    }
    return [];
});

/* ==================== FUNÇÕES DE DRAG ==================== */
function startResize(event, direction) {
    console.log('startResize => direction:', direction);

    // Previne comportamento padrão
    event.preventDefault();

    // Pega o item selecionado
    const selectedItem = props.selectedItem;
    if (!selectedItem) {
        console.warn('Nenhum item selecionado para redimensionar.');
        return;
    }

    // Posição inicial do mouse
    const startMousePos = { x: event.clientX, y: event.clientY };

    // Dimensões e posição iniciais do item
    const startDimensions = {
        width: selectedItem.width || 0,
        height: selectedItem.height || 0,
        x: selectedItem.position?.x || 0,
        y: selectedItem.position?.y || 0,
    };

    // Função para calcular as novas dimensões com base no movimento do mouse
    function resizeMoveHandler(moveEvent) {
        const dx = moveEvent.clientX - startMousePos.x;
        const dy = moveEvent.clientY - startMousePos.y;

        const newDimensions = { ...startDimensions };

        if (direction.includes('right')) {
            newDimensions.width = startDimensions.width + dx;
        }
        if (direction.includes('left')) {
            newDimensions.width = Math.max(startDimensions.width - dx, 0);
            newDimensions.x = startDimensions.x + dx;
        }
        if (direction.includes('bottom')) {
            newDimensions.height = startDimensions.height + dy;
        }
        if (direction.includes('top')) {
            newDimensions.height = Math.max(startDimensions.height - dy, 0);
            newDimensions.y = startDimensions.y + dy;
        }

        console.log('Atualizando dimensões durante resize:', newDimensions);

        // Atualiza visualmente as dimensões do item (opcional)
        selectedItem.width = newDimensions.width;
        selectedItem.height = newDimensions.height;
        selectedItem.position.x = newDimensions.x;
        selectedItem.position.y = newDimensions.y;

        // Emite a atualização do item para o componente pai (opcional em tempo real)
        emits('update-item', { ...selectedItem });
    }

    // Finaliza o redimensionamento
    function resizeEndHandler() {
        console.log('Finalizando resize.');
        // Remove os listeners
        window.removeEventListener('mousemove', resizeMoveHandler);
        window.removeEventListener('mouseup', resizeEndHandler);

        // Emite o estado final do item
        emits('update-item', { ...selectedItem });
    }

    // Adiciona os listeners de movimento e finalização
    window.addEventListener('mousemove', resizeMoveHandler);
    window.addEventListener('mouseup', resizeEndHandler);
}

function startCurveDrag(pt, idx, event) {
    console.log('startCurveDrag => ponto:', pt, 'index:', idx);
    event.preventDefault();

    const selectedItem = props.selectedItem;
    if (!selectedItem) {
        console.warn('Nenhum item selecionado para manipular curva.');
        return;
    }

    const startMousePos = { x: event.clientX, y: event.clientY };

    // OBS: Se você precisa do zoomLevel e panOffset, você teria que recebê-los por props ou outro meio
    // Como não temos isso definido aqui, comentamos:
    // const rect = ???.value.getBoundingClientRect(); // Se fosse necessário
    // Ajustar para: const dx = (moveEvent.clientX - startMousePos.x) / zoomLevel.value;

    // Cópia do caminho original (para não modificar diretamente)
    const originalPath = selectedItem.type === 'polyline'
        ? [...(selectedItem.path || [])]
        : [...(selectedItem.segments || [])];

    function curveDragMoveHandler(moveEvent) {
        const dx = moveEvent.clientX - startMousePos.x;
        const dy = moveEvent.clientY - startMousePos.y;

        if (selectedItem.type === 'polyline') {
            originalPath[idx].x = pt.x + dx;
            originalPath[idx].y = pt.y + dy;
        } else if (selectedItem.type === 'bezier') {
            if (pt.type === 'anchor') {
                originalPath[idx].x = pt.x + dx;
                originalPath[idx].y = pt.y + dy;
            } else if (pt.type === 'control1') {
                originalPath[idx].cx1 = pt.x + dx;
                originalPath[idx].cy1 = pt.y + dy;
            } else if (pt.type === 'control2') {
                originalPath[idx].cx2 = pt.x + dx;
                originalPath[idx].cy2 = pt.y + dy;
            }
        }

        // Atualiza o item
        if (selectedItem.type === 'polyline') {
            selectedItem.path = [...originalPath];
        } else if (selectedItem.type === 'bezier') {
            selectedItem.segments = [...originalPath];
        }

        emits('update-item', { ...selectedItem });
    }

    function curveDragEndHandler() {
        console.log('Finalizando manipulação da curva.');
        window.removeEventListener('mousemove', curveDragMoveHandler);
        window.removeEventListener('mouseup', curveDragEndHandler);

        emits('update-item', { ...selectedItem });
    }

    window.addEventListener('mousemove', curveDragMoveHandler);
    window.addEventListener('mouseup', curveDragEndHandler);
}

function curveHandleStyle(pt) {
    return {
        position: 'absolute',
        left: `${pt.x - 4}px`,
        top: `${pt.y - 4}px`,
        width: '8px',
        height: '8px',
        backgroundColor: '#F00',
        borderRadius: '50%',
        cursor: 'grab',
    };
}

/* ==================== ESTILOS DE LINHA ==================== */
function emitStyleChange() {
    if (!props.selectedItem) return;
    const updated = { ...props.selectedItem };
    updated.lineCap = lineCap.value;
    updated.lineJoin = lineJoin.value;
    updated.lineDash = lineDash.value;
    emits('update-item', updated);
}
</script>

  <style scoped>
  .geometry-controller {
    position: relative;
    pointer-events: none; /* Garante que não interfira no drag principal */
  }

  /* bounding-box */
  .bounding-box {
    pointer-events: auto; /* Para permitir arrasto das handles dentro dele */
    box-sizing: border-box;
  }

  /* resize-handles (cantos) */
  .resize-handle {
    position: absolute;
    width: 10px;
    height: 10px;
    background-color: #fff;
    border: 1px solid #000;
    box-sizing: border-box;
    cursor: pointer;
    z-index: 10;
  }

  .resize-handle.top-left {
    top: -5px;
    left: -5px;
    cursor: nwse-resize;
  }
  .resize-handle.top-right {
    top: -5px;
    right: -5px;
    cursor: nesw-resize;
  }
  .resize-handle.bottom-left {
    bottom: -5px;
    left: -5px;
    cursor: nesw-resize;
  }
  .resize-handle.bottom-right {
    bottom: -5px;
    right: -5px;
    cursor: nwse-resize;
  }

  /* curve-handle para polylines/bezier */
  .curve-handle {
    position: absolute;
    width: 8px;
    height: 8px;
    background-color: #0F0;
    border: 1px solid #000;
    border-radius: 50%;
    cursor: pointer;
    z-index: 15;
  }

  /* style-options (lineCap, lineJoin, strokeDash, etc.) */
  .style-options {
    position: absolute;
    top: 120%;
    left: 0;
    background: #fafafa;
    padding: 6px;
    border: 1px solid #ccc;
    display: flex;
    gap: 10px;
    font-size: 14px;
  }
  .style-options label {
    display: flex;
    flex-direction: column;
    gap: 3px;
  }
  </style>
