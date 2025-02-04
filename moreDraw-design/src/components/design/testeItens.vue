<template>
    <div
        class="draggable-item"
        :data-id="item.id"
        :style="itemStyle"
        @mousedown="onMouseDown"
        @click.stop="toggleDimensions"
        @dblclick.stop="bringToFront"
    >
        <!-- Renderização de diferentes tipos de itens -->
        <!-- Caminhão -->
        <img
            v-if="item.type === 'trailer'"
            :src="trailerIcon"
            class="truck-icon"
            alt="Truck"
        >
        <img
            v-if="item.type === 'truck'"
            :src="truckIcon"
            class="truck-icon"
            alt="Caminhão"
        >
        <!-- Yard -->
        <img
            v-if="item.type === 'yard'"
            :src="yardIcon"
            class="container-box"
            alt="Yard"
        >
        <!-- Carreta -->
        <img
            v-if="item.type === 'trailer'"
            :src="trailerIcon"
            class="container-box"
            alt="Carreta"
        >

        <!-- Truck -->
        <img
            v-if="item.type === 'truck'"
            :src="truckIcon"
            class="container-box"
            alt="Caminhão"
        >

        <!-- Tree -->
        <img
            v-if="item.type === 'tree'"
            :src="treeIcon"
            class="container-box"
            alt="Árvore"
        >

        <!-- Wall Dark -->
        <img
            v-if="item.type === 'wallDark'"
            :src="wallDarkIcon"
            class="container-box"
            alt="Parede Escura"
        >

        <!-- Wall Light -->
        <img
            v-if="item.type === 'wallLight'"
            :src="wallLightIcon"
            class="container-box"
            alt="Parede Clara"
        >

        <!-- Arbusto -->
        <img
            v-if="item.type === 'arbusto'"
            :src="arbustoIcon"
            class="container-box"
            alt="Arbusto"
        >

        <!-- Asfalto Vertical -->
        <img
            v-if="item.type === 'asfaltoVertical'"
            :src="asfaltoVerticalIcon"
            class="container-box"
            alt="Asfalto Vertical"
        >

        <!-- Asfalto -->
        <img
            v-if="item.type === 'asfalto'"
            :src="asfaltoIcon"
            class="container-box"
            alt="Asfalto"
        >

        <!-- Calçada Horizontal -->
        <img
            v-if="item.type === 'calcadaHorizontal'"
            :src="calcadaHorizontalIcon"
            class="container-box"
            alt="Calçada Horizontal"
        >

        <!-- Calçada -->
        <img
            v-if="item.type === 'calcada'"
            :src="calcadaIcon"
            class="container-box"
            alt="Calçada"
        >

        <!-- Canteiro Horizontal -->
        <img
            v-if="item.type === 'canteiroHorizontal'"
            :src="canteiroHorizontalIcon"
            class="container-box"
            alt="Canteiro Horizontal"
        >

        <!-- Canteiro -->
        <img
            v-if="item.type === 'canteiro'"
            :src="canteiroIcon"
            class="container-box"
            alt="Canteiro"
        >

        <!-- Céu -->
        <img
            v-if="item.type === 'ceu'"
            :src="ceuIcon"
            class="container-box"
            alt="Céu"
        >

        <!-- Divisão de Vagas -->
        <img
            v-if="item.type === 'divisaoVagas'"
            :src="divisaoVagasIcon"
            class="container-box"
            alt="Divisão de Vagas"
        >

        <!-- Empresa -->
        <img
            v-if="item.type === 'empresa'"
            :src="empresaIcon"
            class="container-box"
            alt="Empresa"
        >

        <!-- Estrada Cinza -->
        <img
            v-if="item.type === 'estradaCinza'"
            :src="estradaCinzaIcon"
            class="container-box"
            alt="Estrada Cinza"
        >

        <!-- Estrada -->
        <img
            v-if="item.type === 'estrada'"
            :src="estradaIcon"
            class="container-box"
            alt="Estrada"
        >

        <!-- Grama -->
        <img
            v-if="item.type === 'grama'"
            :src="gramaIcon"
            class="container-box"
            alt="Grama"
        >

        <!-- Gramado Dark -->
        <img
            v-if="item.type === 'gramadoDark'"
            :src="gramadoDarkIcon"
            class="container-box"
            alt="Gramado Dark"
        >

        <!-- Green Gras -->
        <img
            v-if="item.type === 'greenGras'"
            :src="greenGrasIcon"
            class="container-box"
            alt="Green Gras"
        >

        <!-- Image 2 -->
        <img
            v-if="item.type === 'image2'"
            :src="image2Icon"
            class="container-box"
            alt="Image 2"
        >

        <!-- Image 3 -->
        <img
            v-if="item.type === 'image3'"
            :src="image3Icon"
            class="container-box"
            alt="Image 3"
        >

        <!-- Image 720 -->
        <img
            v-if="item.type === 'image720'"
            :src="image720Icon"
            class="container-box"
            alt="Image 720"
        >

        <!-- Image Final Cleaned 2 -->
        <img
            v-if="item.type === 'imageFinalCleaned2'"
            :src="imageFinalCleaned2Icon"
            class="container-box"
            alt="Image Final Cleaned 2"
        >

        <!-- Montanha -->
        <img
            v-if="item.type === 'montanha'"
            :src="montanhaIcon"
            class="container-box"
            alt="Montanha"
        >

        <!-- Muro -->
        <img
            v-if="item.type === 'muro'"
            :src="muroIcon"
            class="container-box"
            alt="Muro"
        >

        <!-- Object Retry Processed -->
        <img
            v-if="item.type === 'objectRetryProcessed'"
            :src="objectRetryProcessedIcon"
            class="container-box"
            alt="Object Retry Processed"
        >

        <!-- Paper Texture -->
        <img
            v-if="item.type === 'paperTexture'"
            :src="paperTextureIcon"
            class="container-box"
            alt="Paper Texture"
        >

        <!-- Pasto -->
        <img
            v-if="item.type === 'pasto'"
            :src="pastoIcon"
            class="container-box"
            alt="Pasto"
        >

        <!-- Patio 02 -->
        <img
            v-if="item.type === 'patio02'"
            :src="patio02Icon"
            class="container-box"
            alt="Patio 02"
        >

        <!-- Pessegueiro -->
        <img
            v-if="item.type === 'pessegueiro'"
            :src="pessegueiroIcon"
            class="container-box"
            alt="Pessegueiro"
        >

        <!-- Pond 312465 -->
        <img
            v-if="item.type === 'pond312465'"
            :src="pond312465Icon"
            class="container-box"
            alt="Pond 312465"
        >

        <!-- Pond 1346068 -->
        <img
            v-if="item.type === 'pond1346068'"
            :src="pond1346068Icon"
            class="container-box"
            alt="Pond 1346068"
        >

        <!-- Prédio -->
        <img
            v-if="item.type === 'predio'"
            :src="predioIcon"
            class="container-box"
            alt="Prédio"
        >

        <!-- Savana -->
        <img
            v-if="item.type === 'savana'"
            :src="savanaIcon"
            class="container-box"
            alt="Savana"
        >

        <!-- Telhado Zinco Copia -->
        <img
            v-if="item.type === 'telhadoZincoCopia'"
            :src="telhadoZincoCopiaIcon"
            class="container-box"
            alt="Telhado Zinco Copia"
        >

        <!-- Telhado Zinco -->
        <img
            v-if="item.type === 'telhadoZinco'"
            :src="telhadoZincoIcon"
            class="container-box"
            alt="Telhado Zinco"
        >

        <!-- Texture 1033755 -->
        <img
            v-if="item.type === 'texture1033755'"
            :src="texture1033755Icon"
            class="container-box"
            alt="Texture 1033755"
        >

        <!-- Vagas -->
        <img
            v-if="item.type === 'vagas'"
            :src="vagasIcon"
            class="container-box"
            alt="Vagas"
        >

        <!-- Vagas02 -->
        <img
            v-if="item.type === 'vagas02'"
            :src="vagas02Icon"
            class="container-box"
            alt="Vagas02"
        >

        <!-- Vags Trucks Late3ral -->
        <img
            v-if="item.type === 'vagsTrucksLate3ral'"
            :src="vagsTrucksLate3ralIcon"
            class="container-box"
            alt="Vags Trucks Late3ral"
        >

        <!-- Vags Trucks -->
        <img
            v-if="item.type === 'vagsTrucks'"
            :src="vagsTrucksIcon"
            class="container-box"
            alt="Vags Trucks"
        >

        <!-- Vehicle New Retry Processed -->
        <img
            v-if="item.type === 'vehicleNewRetryProcessed'"
            :src="vehicleNewRetryProcessedIcon"
            class="container-box"
            alt="Vehicle New Retry Processed"
        >

        <!-- Yard03 -->
        <img
            v-if="item.type === 'yard03'"
            :src="yard03Icon"
            class="container-box"
            alt="Yard03"
        >

        <!-- Renderizar Imagem -->
        <img
            v-if="item.type === 'image'"
            :src="item.imageUrl"
            class="container-box"
            :style="{
                backgroundColor: item.color,
                border: `${item.borderWidth}px solid ${item.borderColor}`
            }"
        >
        <!-- Contêiner -->
        <div
            v-else-if="item.type === 'container'"
            class="container-box"
            :style="{
                backgroundColor: item.color,
                border: `${item.borderWidth}px solid ${item.borderColor}`
            }"
        />
        <!-- Rótulo -->
        <span
            v-else-if="item.type === 'label'"
            class="item-label"
            :style="{
                color: item.color,
                fontSize: `${item.size}px`,
                border: `${item.borderWidth}px solid ${item.borderColor}`
            }"
        >
            {{ item.label }}
        </span>
        <!-- Círculo -->
        <div
            v-else-if="item.type === 'circle'"
            class="circle-shape"
            :style="{
                backgroundColor: item.color,
                border: `${item.borderWidth}px solid ${item.borderColor}`
            }"
        />
        <!-- Triângulo -->
        <div
            v-else-if="item.type === 'triangle'"
            class="triangle-shape"
        >
            <svg
                :width="item.width"
                :height="item.height"
                xmlns="http://www.w3.org/2000/svg"
            >
                <polygon
                    :points="`0,${item.height} ${item.width / 2},0 ${item.width},${item.height}`"
                    :fill="item.color || 'transparent'"
                    :stroke="item.borderColor || 'black'"
                    :stroke-width="item.borderWidth"
                />
            </svg>
        </div>
        <!-- Linha -->
        <div
            v-else-if="item.type === 'line'"
            class="line-item"
        >
            <svg
                :width="itemWidth"
                :height="itemHeight"
                :style="svgStyle"
            >
                <line
                    :x1="0"
                    :y1="0"
                    :x2="lineDeltaX"
                    :y2="lineDeltaY"
                    :stroke="item.strokeColor || 'black'"
                    :stroke-width="item.strokeWidth || 2"
                />
            </svg>
        </div>

        <!-- Handles de manipulação -->
        <div
            v-if="enableDimensions"
            class="resize-handle top-left"
            @mousedown.stop.prevent="onResizeMouseDown('top-left', $event)"
        />
        <div
            v-if="enableDimensions"
            class="resize-handle top-right"
            @mousedown.stop.prevent="onResizeMouseDown('top-right', $event)"
        />
        <div
            v-if="enableDimensions"
            class="resize-handle bottom-left"
            @mousedown.stop.prevent="onResizeMouseDown('bottom-left', $event)"
        />
        <div
            v-if="enableDimensions"
            class="resize-handle bottom-right"
            @mousedown.stop.prevent="onResizeMouseDown('bottom-right', $event)"
        />
        <div
            v-if="enableDimensions"
            class="resize-handle top"
            @mousedown.stop.prevent="onResizeMouseDown('top', $event)"
        />
        <div
            v-if="enableDimensions"
            class="resize-handle bottom"
            @mousedown.stop.prevent="onResizeMouseDown('bottom', $event)"
        />
        <div
            v-if="enableDimensions"
            class="resize-handle left"
            @mousedown.stop.prevent="onResizeMouseDown('left', $event)"
        />
        <div
            v-if="enableDimensions"
            class="resize-handle right"
            @mousedown.stop.prevent="onResizeMouseDown('right', $event)"
        />

        <!-- Handle de Rotação -->
        <div
            v-if="enableDimensions"
            class="rotate-handle"
            @mousedown.stop.prevent="onRotateMouseDown($event)"
        />
        <!-- Handles de Inclinação (Skew) -->
        <div
            v-if="enableDimensions"
            class="skew-handle skew-x"
            @mousedown.stop.prevent="onSkewMouseDown('skewX', $event)"
        />
        <div
            v-if="enableDimensions"
            class="skew-handle skew-y"
            @mousedown.stop.prevent="onSkewMouseDown('skewY', $event)"
        />

        <!-- Handles de Perspectiva -->
        <div
            v-if="enableDimensions"
            class="perspective-handle top-left"
            @mousedown.stop.prevent="onPerspectiveMouseDown('topLeft', $event)"
        />
        <div
            v-if="enableDimensions"
            class="perspective-handle top-right"
            @mousedown.stop.prevent="onPerspectiveMouseDown('topRight', $event)"
        />
        <div
            v-if="enableDimensions"
            class="perspective-handle bottom-left"
            @mousedown.stop.prevent="onPerspectiveMouseDown('bottomLeft', $event)"
        />
        <div
            v-if="enableDimensions"
            class="perspective-handle bottom-right"
            @mousedown.stop.prevent="onPerspectiveMouseDown('bottomRight', $event)"
        />

        <!-- Floating Configuration Card -->
        <div
            v-if="isSelected"
            class="config-card"
            :style="{ top: cardPosition.y + 'px', left: cardPosition.x + 'px' }"
            @mousedown.stop.prevent="startDragCard"
        >
            <div class="card-header">
                <span>Configurações do Item</span>
                <button @click="closeCard">
                    ×
                </button>
            </div>
            <div class="card-body">
                <!-- Propriedades Gerais -->
                <div class="property-group">
                    <label>Rotação (graus):</label>
                    <input
                        v-model.number="config.rotation"
                        type="number"
                        @input="updateConfig"
                    >
                </div>
                <div class="property-group">
                    <label>Skew X (graus):</label>
                    <input
                        v-model.number="config.skewX"
                        type="number"
                        @input="updateConfig"
                    >
                </div>
                <div class="property-group">
                    <label>Skew Y (graus):</label>
                    <input
                        v-model.number="config.skewY"
                        type="number"
                        @input="updateConfig"
                    >
                </div>
                <div class="property-group">
                    <label>Largura (px):</label>
                    <input
                        v-model.number="config.width"
                        type="number"
                        @input="updateConfig"
                    >
                </div>
                <div class="property-group">
                    <label>Altura (px):</label>
                    <input
                        v-model.number="config.height"
                        type="number"
                        @input="updateConfig"
                    >
                </div>
                <div class="property-group">
                    <label>Cor:</label>
                    <input
                        v-model="config.color"
                        type="color"
                        @input="updateConfig"
                    >
                </div>
                <div class="property-group">
                    <label>Largura da Borda (px):</label>
                    <input
                        v-model.number="config.borderWidth"
                        type="number"
                        @input="updateConfig"
                    >
                </div>
                <div class="property-group">
                    <label>Cor da Borda:</label>
                    <input
                        v-model="config.borderColor"
                        type="color"
                        @input="updateConfig"
                    >
                </div>
                <!-- Propriedades de Perspectiva -->
                <div class="property-group">
                    <label>Rotação X (graus):</label>
                    <input
                        v-model.number="config.rotateX"
                        type="number"
                        @input="updateConfig"
                    >
                </div>
                <div class="property-group">
                    <label>Rotação Y (graus):</label>
                    <input
                        v-model.number="config.rotateY"
                        type="number"
                        @input="updateConfig"
                    >
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import trailerIcon from 'src/assets/objects/truck02.svg';
import truckIcon from 'src/assets/objects/truck01.svg';
import yardIcon from 'src/assets/objects/yardSpaces.png';
import treeIcon from 'src/assets/objects/tree.png';
import wallDarkIcon from 'src/assets/objects/wall_dark.png';
import wallLightIcon from 'src/assets/objects/wall_light.png';
import arbustoIcon from 'src/assets/objects/Arbusto.png';
import asfaltoVerticalIcon from 'src/assets/objects/asfalto - vertical.png';
import asfaltoIcon from 'src/assets/objects/asfalto.png';
import calcadaHorizontalIcon from 'src/assets/objects/calçada_horizontal.png';
import calcadaIcon from 'src/assets/objects/calçada.png';
import canteiroHorizontalIcon from 'src/assets/objects/canteiro_horizontal.png';
import canteiroIcon from 'src/assets/objects/canteiro.png';
import ceuIcon from 'src/assets/objects/céu.jpg';
import divisaoVagasIcon from 'src/assets/objects/divisão de vagas.png';
import empresaIcon from 'src/assets/objects/Empresa.png';
import estradaCinzaIcon from 'src/assets/objects/estrada_cinza.png';
import estradaIcon from 'src/assets/objects/estrada.png';
import gramaIcon from 'src/assets/objects/Grama.png';
import gramadoDarkIcon from 'src/assets/objects/gramado_dark.png';
import greenGrasIcon from 'src/assets/objects/green_gras.png';
import image2Icon from 'src/assets/objects/image (2).png';
import image3Icon from 'src/assets/objects/image (3).png';
import image720Icon from 'src/assets/objects/image_720.png';
import imageFinalCleaned2Icon from 'src/assets/objects/image_final_cleaned_2.png';
import montanhaIcon from 'src/assets/objects/Montanha.png';
import muroIcon from 'src/assets/objects/muro.png';
import objectRetryProcessedIcon from 'src/assets/objects/object_retry_processed.png';
import paperTextureIcon from 'src/assets/objects/papertexture-2061711_1280.jpg';
import pastoIcon from 'src/assets/objects/pasto.png';
import patio02Icon from 'src/assets/objects/patio02.png';
import pessegueiroIcon from 'src/assets/objects/Pessegueiro.png';
import pond312465Icon from 'src/assets/objects/pond-312465_640.png';
import pond1346068Icon from 'src/assets/objects/pond-1346068_640.jpg';
import predioIcon from 'src/assets/objects/Predio.png';
import savanaIcon from 'src/assets/objects/Savana.png';
import telhadoZincoCopiaIcon from 'src/assets/objects/telhado_zinco - Copia.png';
import telhadoZincoIcon from 'src/assets/objects/telhado_zinco.png';
import texture1033755Icon from 'src/assets/objects/texture-1033755_1280.jpg';
import vagasIcon from 'src/assets/objects/vagas.png';
import vagas02Icon from 'src/assets/objects/vagas02.png';
import vagsTrucksLate3ralIcon from 'src/assets/objects/vagsTrucks_Late3ral.png';
import vagsTrucksIcon from 'src/assets/objects/vagsTrucks.png';
import vehicleNewRetryProcessedIcon from 'src/assets/objects/vehicle_new_retry_processed.png';
import yard03Icon from 'src/assets/objects/yard03.png';

const props = defineProps({
    item: Object,
    zoomLevel: Number,
    panOffset: Object,
    selectedItem: Object,
    paintMode: Boolean,
    selectedColor: String,
});

const emits = defineEmits(['select-item', 'update-item', 'bring-to-front']);

const isSelected = computed(() => props.selectedItem && props.selectedItem.id === props.item.id);

// Floating Configuration Card
const isCardOpen = ref(false);
const cardPosition = ref({ x: 100, y: 100 });
const isDraggingCard = ref(false);
const dragStartCard = ref({ x: 0, y: 0 });

const config = ref({
    rotation: props.item.rotation || 0,
    skewX: props.item.skewX || 0,
    skewY: props.item.skewY || 0,
    width: props.item.width,
    height: props.item.height,
    color: props.item.color || '#ffffff',
    borderWidth: props.item.borderWidth || 0,
    borderColor: props.item.borderColor || '#000000',
    rotateX: props.item.rotateX || 0,
    rotateY: props.item.rotateY || 0,
});

watch(
    () => props.selectedItem,
    (newVal) => {
        if (newVal && newVal.id === props.item.id) {
            config.value = {
                rotation: newVal.rotation || 0,
                skewX: newVal.skewX || 0,
                skewY: newVal.skewY || 0,
                width: newVal.width,
                height: newVal.height,
                color: newVal.color || '#ffffff',
                borderWidth: newVal.borderWidth || 0,
                borderColor: newVal.borderColor || '#000000',
                rotateX: newVal.rotateX || 0,
                rotateY: newVal.rotateY || 0,
            };
            isCardOpen.value = true;
        }
    },
);

function updateConfig() {
    emits('update-item', { ...props.item, ...config.value });
}

const itemStyle = computed(() => {
    const x = (props.item.position.x + props.panOffset.x) * props.zoomLevel;
    const y = (props.item.position.y + props.panOffset.y) * props.zoomLevel;
    const rotation = props.item.rotation || 0;
    const skewX = props.item.skewX || 0;
    const skewY = props.item.skewY || 0;
    const rotateX = props.item.rotateX || 0;
    const rotateY = props.item.rotateY || 0;
    const perspective = 1000;

    return {
        transform: `
            translate(${x}px, ${y}px)
            perspective(${perspective}px)
            rotateX(${rotateX}deg)
            rotateY(${rotateY}deg)
            rotate(${rotation}deg)
            skew(${skewX}deg, ${skewY}deg)
        `,
        width: `${props.item.width * props.zoomLevel}px`,
        height: `${props.item.height * props.zoomLevel}px`,
        position: 'absolute',
        userSelect: 'none',
        cursor: 'grab',
        transformStyle: 'preserve-3d',
    };
});

// Cálculos específicos para itens do tipo 'line'
const itemWidth = computed(() => Math.abs(props.item.endPosition.x - props.item.position.x) * props.zoomLevel);

const itemHeight = computed(() => Math.abs(props.item.endPosition.y - props.item.position.y) * props.zoomLevel);

const lineDeltaX = computed(() => (props.item.endPosition.x - props.item.position.x) * props.zoomLevel);

const lineDeltaY = computed(() => (props.item.endPosition.y - props.item.position.y) * props.zoomLevel);

const svgStyle = {
    overflow: 'visible',
};

// Estado de dragging e funções de manipulação
const isDragging = ref(false);
const dragStart = ref({ x: 0, y: 0 });
const itemStart = ref({});

// Estado de resizing
const isResizing = ref(false);
const resizeDirection = ref('');
const resizeStart = ref({ x: 0, y: 0 });
const resizeItemStart = ref({});

// Estado de rotação
const isRotating = ref(false);
const rotateStart = ref({ x: 0, y: 0 });
const initialRotation = ref(0);

// Estado de inclinação (skew)
const isSkewing = ref(false);
const skewType = ref('');
const skewStart = ref({ x: 0, y: 0 });
const initialSkew = ref(0);

// Estado de perspectiva
const isAdjustingPerspective = ref(false);
const perspectiveHandle = ref('');
const perspectiveStart = ref({ x: 0, y: 0 });
const initialPerspective = ref({ rotateX: 0, rotateY: 0 });

// Estado para controle de ativação/desativação de configurações de dimensão
const enableDimensions = ref(false);

function onMouseDown(event) {
    // Only respond to left-click dragging
    if (event.button !== 0) return;

    isDragging.value = true;
    dragStart.value = { x: event.clientX, y: event.clientY };
    itemStart.value = { ...props.item };

    emits('select-item', props.item);

    window.addEventListener('mousemove', onMouseMove);
    window.addEventListener('mouseup', onMouseUp);
}

function onMouseMove(event) {
    if (isDragging.value) {
        const dx = (event.clientX - dragStart.value.x) / props.zoomLevel;
        const dy = (event.clientY - dragStart.value.y) / props.zoomLevel;

        if (props.item.type === 'line') {
            const newPosition = {
                x: itemStart.value.position.x + dx,
                y: itemStart.value.position.y + dy,
            };
            const newEndPosition = {
                x: itemStart.value.endPosition.x + dx,
                y: itemStart.value.endPosition.y + dy,
            };
            emits('update-item', { ...props.item, position: newPosition, endPosition: newEndPosition });
        } else {
            const newPosition = {
                x: itemStart.value.position.x + dx,
                y: itemStart.value.position.y + dy,
            };
            emits('update-item', { ...props.item, position: newPosition });
        }
    }

    if (isResizing.value) {
        const dx = (event.clientX - resizeStart.value.x) / props.zoomLevel;
        const dy = (event.clientY - resizeStart.value.y) / props.zoomLevel;

        if (props.item.type === 'line') {
            const newEndPosition = { ...resizeItemStart.value.endPosition };

            switch (resizeDirection.value) {
                case 'bottom-right':
                    newEndPosition.x += dx;
                    newEndPosition.y += dy;
                    break;
                // Add other cases as needed
                default:
                    break;
            }

            emits('update-item', {
                ...props.item,
                endPosition: newEndPosition,
                width: Math.abs(newEndPosition.x - props.item.position.x),
                height: Math.abs(newEndPosition.y - props.item.position.y),
            });
        } else {
            let newWidth = resizeItemStart.value.width;
            let newHeight = resizeItemStart.value.height;
            let newX = props.item.position.x;
            let newY = props.item.position.y;

            switch (resizeDirection.value) {
                case 'top-left':
                    newWidth = resizeItemStart.value.width - dx;
                    newHeight = resizeItemStart.value.height - dy;
                    newX = resizeItemStart.value.x + dx;
                    newY = resizeItemStart.value.y + dy;
                    break;
                case 'top-right':
                    newWidth = resizeItemStart.value.width + dx;
                    newHeight = resizeItemStart.value.height - dy;
                    newY = resizeItemStart.value.y + dy;
                    break;
                case 'bottom-left':
                    newWidth = resizeItemStart.value.width - dx;
                    newHeight = resizeItemStart.value.height + dy;
                    newX = resizeItemStart.value.x + dx;
                    break;
                case 'bottom-right':
                    newWidth = resizeItemStart.value.width + dx;
                    newHeight = resizeItemStart.value.height + dy;
                    break;
                case 'top':
                    newHeight = resizeItemStart.value.height - dy;
                    newY = resizeItemStart.value.y + dy;
                    break;
                case 'bottom':
                    newHeight = resizeItemStart.value.height + dy;
                    break;
                case 'left':
                    newWidth = resizeItemStart.value.width - dx;
                    newX = resizeItemStart.value.x + dx;
                    break;
                case 'right':
                    newWidth = resizeItemStart.value.width + dx;
                    break;
                default:
                    break;
            }

            newWidth = newWidth > 10 ? newWidth : 10;
            newHeight = newHeight > 10 ? newHeight : 10;

            emits('update-item', {
                ...props.item,
                width: newWidth,
                height: newHeight,
                position: { x: newX, y: newY },
            });
        }
    }

    if (isRotating.value) {
        const rect = event.target.getBoundingClientRect();
        const centerX = rect.left + rect.width / 2;
        const centerY = rect.top + rect.height / 2;
        const radians = Math.atan2(event.clientY - centerY, event.clientX - centerX);
        const degrees = radians * (180 / Math.PI);
        const newRotation = degrees - initialRotation.value;

        emits('update-item', { ...props.item, rotation: newRotation });
    }

    if (isSkewing.value) {
        const dx = (event.clientX - skewStart.value.x) / props.zoomLevel;
        const dy = (event.clientY - skewStart.value.y) / props.zoomLevel;
        let newSkew = initialSkew.value;

        if (skewType.value === 'skewX') {
            newSkew = initialSkew.value + dx;
        } else if (skewType.value === 'skewY') {
            newSkew = initialSkew.value + dy;
        }

        emits('update-item', { ...props.item, [skewType.value]: newSkew });
    }

    if (isAdjustingPerspective.value) {
        const dx = (event.clientX - perspectiveStart.value.x) / props.zoomLevel;
        const dy = (event.clientY - perspectiveStart.value.y) / props.zoomLevel;
        let newRotateX = initialPerspective.value.rotateX;
        let newRotateY = initialPerspective.value.rotateY;

        if (perspectiveHandle.value) {
            switch (perspectiveHandle.value) {
                case 'topLeft':
                    newRotateX = initialPerspective.value.rotateX + dy;
                    newRotateY = initialPerspective.value.rotateY - dx;
                    break;
                case 'topRight':
                    newRotateX = initialPerspective.value.rotateX + dy;
                    newRotateY = initialPerspective.value.rotateY + dx;
                    break;
                case 'bottomLeft':
                    newRotateX = initialPerspective.value.rotateX - dy;
                    newRotateY = initialPerspective.value.rotateY - dx;
                    break;
                case 'bottomRight':
                    newRotateX = initialPerspective.value.rotateX - dy;
                    newRotateY = initialPerspective.value.rotateY + dx;
                    break;
                default:
                    break;
            }
        } else {
            newRotateX = initialPerspective.value.rotateX + dy;
            newRotateY = initialPerspective.value.rotateY + dx;
        }

        emits('update-item', { ...props.item, rotateX: newRotateX, rotateY: newRotateY });
    }
}

function onMouseUp() {
    isDragging.value = false;
    isResizing.value = false;
    isRotating.value = false;
    isSkewing.value = false;
    isAdjustingPerspective.value = false;
    resizeDirection.value = '';
    skewType.value = '';
    perspectiveHandle.value = '';

    window.removeEventListener('mousemove', onMouseMove);
    window.removeEventListener('mouseup', onMouseUp);
}

function onResizeMouseDown(direction, event) {
    isResizing.value = true;
    resizeDirection.value = direction;
    resizeStart.value = { x: event.clientX, y: event.clientY };

    if (props.item.type === 'line') {
        resizeItemStart.value = {
            endPosition: { ...props.item.endPosition },
        };
    } else {
        resizeItemStart.value = {
            width: props.item.width,
            height: props.item.height,
            x: props.item.position.x,
            y: props.item.position.y,
        };
    }

    window.addEventListener('mousemove', onMouseMove);
    window.addEventListener('mouseup', onMouseUp);
}

function onRotateMouseDown(event) {
    isRotating.value = true;
    rotateStart.value = { x: event.clientX, y: event.clientY };
    initialRotation.value = props.item.rotation || 0;

    window.addEventListener('mousemove', onMouseMove);
    window.addEventListener('mouseup', onMouseUp);
}

function onSkewMouseDown(type, event) {
    isSkewing.value = true;
    skewType.value = type;
    skewStart.value = { x: event.clientX, y: event.clientY };
    initialSkew.value = props.item[type] || 0;

    window.addEventListener('mousemove', onMouseMove);
    window.addEventListener('mouseup', onMouseUp);
}

function onPerspectiveMouseDown(handle, event) {
    isAdjustingPerspective.value = true;
    perspectiveHandle.value = handle;
    perspectiveStart.value = { x: event.clientX, y: event.clientY };
    initialPerspective.value = {
        rotateX: props.item.rotateX || 0,
        rotateY: props.item.rotateY || 0,
    };

    window.addEventListener('mousemove', onMouseMove);
    window.addEventListener('mouseup', onMouseUp);
}

function onRightClick(event) {
    isAdjustingPerspective.value = true;
    perspectiveHandle.value = '';
    perspectiveStart.value = { x: event.clientX, y: event.clientY };
    initialPerspective.value = {
        rotateX: props.item.rotateX || 0,
        rotateY: props.item.rotateY || 0,
    };

    window.addEventListener('mousemove', onMouseMove);
    window.addEventListener('mouseup', onMouseUp);
}

function toggleDimensions() {
    enableDimensions.value = !enableDimensions.value;
    emits('select-item', props.item);
}

function bringToFront() {
    emits('bring-to-front', props.item);
}

// Floating Configuration Card Functions
function startDragCard(event) {
    isDraggingCard.value = true;
    dragStartCard.value = { x: event.clientX - cardPosition.value.x, y: event.clientY - cardPosition.value.y };
    window.addEventListener('mousemove', onDragCard);
    window.addEventListener('mouseup', stopDragCard);
}

function onDragCard(event) {
    if (isDraggingCard.value) {
        cardPosition.value = {
            x: event.clientX - dragStartCard.value.x,
            y: event.clientY - dragStartCard.value.y,
        };
    }
}

function stopDragCard() {
    isDraggingCard.value = false;
    window.removeEventListener('mousemove', onDragCard);
    window.removeEventListener('mouseup', stopDragCard);
}

function closeCard() {
    isCardOpen.value = false;
}
</script>

<style scoped>
.item-icon {
    width: 100%;
    height: 100%;
    object-fit: contain;
}

.draggable-item {
    display: flex;
    align-items: center;
    justify-content: center;
    position: absolute;
    cursor: grab;
}

.truck-icon {
    width: 100%;
    height: 100%;
    object-fit: contain;
}

.container-box {
    width: 100%;
    height: 100%;
}

.item-label {
    font-size: 12px;
}

.circle-shape {
    width: 100%;
    height: 100%;
    border-radius: 50%;
}

.line-item svg {
    overflow: visible;
}

/* Estilos para Handles de Redimensionamento */
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

.resize-handle.top {
    top: -5px;
    left: 50%;
    transform: translateX(-50%);
    cursor: ns-resize;
}

.resize-handle.bottom {
    bottom: -5px;
    left: 50%;
    transform: translateX(-50%);
    cursor: ns-resize;
}

.resize-handle.left {
    left: -5px;
    top: 50%;
    transform: translateY(-50%);
    cursor: ew-resize;
}

.resize-handle.right {
    right: -5px;
    top: 50%;
    transform: translateY(-50%);
    cursor: ew-resize;
}

.rotate-handle {
    position: absolute;
    top: -20px;
    left: 50%;
    transform: translateX(-50%);
    width: 15px;
    height: 15px;
    background-color: #fff;
    border: 1px solid #000;
    border-radius: 50%;
    cursor: grab;
    z-index: 10;
}

.skew-handle {
    position: absolute;
    width: 10px;
    height: 10px;
    background-color: #fff;
    border: 1px solid #000;
    box-sizing: border-box;
    cursor: pointer;
    z-index: 10;
}

.skew-handle.skew-x {
    top: 50%;
    left: -15px;
    transform: translateY(-50%);
    cursor: ew-resize;
}

.skew-handle.skew-y {
    top: -15px;
    left: 50%;
    transform: translateX(-50%);
    cursor: ns-resize;
}

.resize-handle:hover,
.rotate-handle:hover,
.skew-handle:hover {
    background-color: #000;
}

/* Estilos para Handles de Perspectiva */
.perspective-handle {
    position: absolute;
    width: 10px;
    height: 10px;
    background-color: #ff0; /* Amarelo para distinguir */
    border: 1px solid #000;
    box-sizing: border-box;
    cursor: pointer;
    z-index: 10;
}

.perspective-handle.top-left {
    top: -10px;
    left: -10px;
    cursor: nwse-resize;
}

.perspective-handle.top-right {
    top: -10px;
    right: -10px;
    cursor: nesw-resize;
}

.perspective-handle.bottom-left {
    bottom: -10px;
    left: -10px;
    cursor: nesw-resize;
}

.perspective-handle.bottom-right {
    bottom: -10px;
    right: -10px;
    cursor: nwse-resize;
}

.perspective-handle:hover {
    background-color: #000;
}

/* Estilos para o Card de Configuração */
.config-card {
    position: fixed;
    width: 300px;
    background-color: #f9f9f9;
    border: 1px solid #ddd;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    z-index: 1000;
    cursor: move;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #eee;
    padding: 10px;
    cursor: grab;
}

.card-header button {
    background: none;
    border: none;
    font-size: 16px;
    cursor: pointer;
}

.card-body {
    padding: 10px;
}

.property-group {
    margin-bottom: 10px;
    display: flex;
    flex-direction: column;
}

.property-group label {
    margin-bottom: 5px;
    font-size: 14px;
}

.property-group input[type="number"],
.property-group input[type="color"] {
    padding: 5px;
    font-size: 14px;
}
</style>
