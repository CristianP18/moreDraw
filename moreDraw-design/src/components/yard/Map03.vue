<template>
    <q-page class="page q-px-lg">
        <q-card class="q-pa-md dropzone-card">
            <q-toolbar-title>
                Yard Controller
                <q-btn
                    label="Configuração"
                    href="http://192.168.0.248:8086/#/yardconf02"
                    flat
                    color="primary"
                    class="q-ml-sm"
                />
            </q-toolbar-title>

            <!-- Única Instância do ThreeContainer -->
            <ThreeContainer
                :models="models"
                class="container-3D"
            />

            <div
                ref="dropzone"
                class="dropzone"
                :style="{ width: dropzoneWidth + 'px', height: dropzoneHeight + 'px' }"
            >
                <div
                    v-for="storloc in placedStorlocs"
                    :key="storloc.uniqueId"
                    class="draggable-item"
                    :data-id="storloc.uniqueId"
                    :style="{
                        transform: `translate(${storloc.position.x}px, ${storloc.position.y}px)`,
                        color: fontColor,
                        fontSize: fontSize + 'px'
                    }"
                    :draggable="true"
                    @dblclick="openDetails(storloc)"
                    @dragstart="dragStart(storloc)"
                    @dragover.prevent
                    @drop="drop(storloc)"
                    @mouseenter="showTooltip(storloc, $event)"
                    @mouseleave="hideTooltip"
                >
                    <q-item
                        clickable
                        class="storloc-item"
                    >
                        <q-item-section style="position: relative;">
                            <!-- Ícone do Caminhão quando a cor for verde (#008000) -->
                            <img
                                v-if="storloc.color === '#008000'"
                                :src="truckIcon"
                                alt="Caminhão"
                                class="camiao-icone"
                                style="left: 12%; height: 80px; transform: rotate(180deg);"
                            >

                            <!-- Bolinha (Circle) quando a cor não é verde -->
                            <div
                                v-else
                                class="label-circle"
                                :style="{
                                    backgroundColor: storloc.color,
                                    width: dotSize + 'px',
                                    height: dotSize + 'px'
                                }"
                            />

                            <!-- EAN label exibido sobre o ícone do caminhão ou círculo -->
                            <q-item-label
                                style="position: absolute; top: 0; left: 0; width: 100%; text-align: center; color: black; z-index: 10;"
                            >
                                {{ storloc.ean.length > 8 ? storloc.ean.slice(0, 8) + '...' : storloc.ean }}
                            </q-item-label>
                        </q-item-section>
                    </q-item>
                </div>

                <q-tooltip
                    v-if="tooltip.visible"
                    :target="tooltipAnchor"
                    transition-show="fade"
                    transition-hide="fade"
                >
                    <div>{{ tooltip.name }}</div>
                    <div v-if="tooltip.licensePlate">
                        Placa: {{ tooltip.licensePlate }}
                    </div>
                </q-tooltip>
            </div>

            <q-toolbar class="q-px-md q-py-sm">
                <!-- Botões e inputs adicionais -->
                <q-btn
                    label="Salvar"
                    color="primary"
                    class="q-mt-md"
                    @click="savePositions"
                />
                <q-input
                    v-model.number="fontSize"
                    type="number"
                    label="Tamanho da Fonte"
                    outlined
                    dense
                    color="primary"
                    class="q-ml-sm"
                    style="width: 150px; margin-right: 20px; margin-left: 100px;"
                />
                <q-btn
                    flat
                    icon="color_lens"
                    label="Cor da Fonte"
                    color="primary"
                    class="q-ml-sm"
                    @click="toggleFontColorDialog"
                />
                <q-dialog v-model="showFontColorDialog">
                    <q-card>
                        <q-card-section>
                            <q-color
                                v-model="fontColor"
                                flat
                            />
                        </q-card-section>
                        <q-card-actions align="right">
                            <q-btn
                                flat
                                label="Fechar"
                                @click="toggleFontColorDialog"
                            />
                        </q-card-actions>
                    </q-card>
                </q-dialog>
                <q-input
                    v-model.number="dotSize"
                    type="number"
                    label="Tamanho da Bolinha"
                    outlined
                    dense
                    style="width: 150px; margin-right: 20px;"
                />
            </q-toolbar>
        </q-card>

        <q-dialog v-model="showDetailsDialog">
            <q-card>
                <q-card-section>
                    <div class="text-h6">
                        Detalhes da Storloc - {{ selectedStorloc.ean || 'N/A' }}
                    </div>
                </q-card-section>
                <q-card-section>
                    <q-input
                        v-model="selectedStorloc.ean"
                        label="EAN"
                        readonly
                    />
                    <q-input
                        v-model="selectedStorloc.type"
                        label="Tipo de Veículo"
                        readonly
                    />
                    <q-input
                        v-model="selectedStorloc.index"
                        label="Índice"
                        readonly
                    />
                    <q-input
                        v-model="selectedStorloc.zoneId"
                        label="Área"
                        readonly
                    />
                    <q-input
                        v-model="selectedStorloc.rack"
                        label="Rack"
                        readonly
                    />
                    <q-input
                        v-model="selectedStorloc.zoneId"
                        label="Zona"
                        readonly
                    />
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn
                        flat
                        label="Fechar"
                        @click="showDetailsDialog = false"
                    />
                </q-card-actions>
            </q-card>
        </q-dialog>
    </q-page>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import truckIcon from 'src/assets/imgVehicle/truck02.svg';
import { createAxiosInstance } from '../../api/axiosInstance';
import ThreeContainer from './ThreeContainer.vue';

const axiosInstance = createAxiosInstance('dev2');

const availableStorlocs = ref([]);
const placedStorlocs = ref([]);
const dropzone = ref(null);
const showDetailsDialog = ref(false);
const selectedStorloc = reactive({
    ean: '',
    type: '',
    index: '',
    area: '',
    rack: '',
    zoneId: '',
});

const fontSize = ref(18);
const fontColor = ref('#000000');
const showFontColorDialog = ref(false);
const dotSize = ref(12);
const dotColor = ref('#334ab0');
const showDotColorDialog = ref(false);

const dropzoneWidth = ref(2455);
const dropzoneHeight = ref(800);

let draggedStorloc = null;

const tooltip = reactive({
    visible: false,
    name: '',
    licensePlate: '',
});
const tooltipAnchor = ref(null);

onMounted(() => {
    fetchStorlocs();
});

async function fetchStorlocs() {
    try {
        const response = await axiosInstance.get('/rest/storloc');
        const data = response.data.content.storlocs || [];

        let idCounter = 0;
        availableStorlocs.value = data.filter((s) => !s.zoneId || !isValidJSON(s.zoneId));

        placedStorlocs.value = data
            .filter((s) => s.zoneId && isValidJSON(s.zoneId))
            .map((s) => {
                const position = JSON.parse(s.zoneId);
                const index = parseInt(s.index, 10) || 0;
                return {
                    ...s,
                    position,
                    uniqueId: idCounter++,
                    index,
                    color: getColorByIndex(index),
                    rotation: { x: 0, y: 0, z: 0 }, // Defina conforme necessário
                    scale: { x: 1, y: 1, z: 1 }, // Defina conforme necessário
                    modelUrl: s.modelUrl || 'path/to/default/model.glb', // Assegure-se que modelUrl esteja definido
                };
            });
    } catch (error) {
        console.error('Erro ao buscar storlocs:', error);
    }
}

function isValidJSON(str) {
    try {
        JSON.parse(str);
        return true;
    } catch {
        return false;
    }
}

function openDetails(storloc) {
    Object.assign(selectedStorloc, storloc);
    showDetailsDialog.value = true;
}

function dragStart(storloc) {
    draggedStorloc = storloc;
}

function drop(targetStorloc) {
    if (draggedStorloc && draggedStorloc !== targetStorloc && !targetStorloc.type) {
        targetStorloc.type = draggedStorloc.type;
        targetStorloc.index = 1;
        targetStorloc.color = getColorByIndex(targetStorloc.index);

        draggedStorloc.type = '';
        draggedStorloc.index = 0;
        draggedStorloc.color = getColorByIndex(draggedStorloc.index);

        draggedStorloc = null;
    }
}

function getColorByIndex(index) {
    index = parseInt(index, 10);
    switch (index) {
        case 1:
            return '#008000'; // Verde para ocupado, exibe ícone de caminhão
        case 2:
            return '#FFFF00'; // Amarelo para ocioso
        case 0:
            return '#0000FF'; // Azul para livre
        case 3:
            return '#FFA500'; // Laranja para alertas
        case 4:
            return '#FF0000'; // Vermelho para problema ocorrido
        default:
            return '#0000FF'; // Azul padrão
    }
}

async function savePositions() {
    try {
        const updatePromises = placedStorlocs.value.map(async (storloc) => {
            await axiosInstance.put(`/rest/storloc/${storloc.ean}`, storloc);
        });

        await Promise.all(updatePromises);
        alert('Posições e tipos de veículo atualizados com sucesso!');
    } catch (error) {
        console.error('Erro ao salvar posições:', error);
    }
}

function toggleFontColorDialog() {
    showFontColorDialog.value = !showFontColorDialog.value;
}

function toggleDotColorDialog() {
    showDotColorDialog.value = !showDotColorDialog.value;
}

function showTooltip(storloc, event) {
    tooltip.visible = true;
    tooltip.name = storloc.ean;
    tooltip.licensePlate = storloc.type || '';
    tooltipAnchor.value = event.currentTarget; // Referência correta
}

function hideTooltip() {
    tooltip.visible = false;
    tooltipAnchor.value = null;
}

// Computed property para os modelos a serem passados para o ThreeContainer
const models = computed(() => placedStorlocs.value.map((storloc) => ({
    modelUrl: storloc.modelUrl,
    position: storloc.position,
    rotation: storloc.rotation,
    scale: storloc.scale,
    uniqueId: storloc.uniqueId,
})));
</script>

<style scoped>
.dropzone {
position: relative;
border: 2px dashed #ddd;
background-size: cover;
background-position: center;
background-size: contain;
background-size: 100% 100%;
background-color: #808080;
background-image: url('src/assets/imgVehicle/yard_10.png');
}

.draggable-item {
display: flex;
align-items: center;
cursor: move;
position: absolute;
user-select: none;
}

.label-circle {
border-radius: 50%;
display: inline-block;
margin-left: 10px;
}

/* Ícone de caminhão quando a cor é verde */
.camiao-icone {
position: absolute;
width: 32px; /* Defina o tamanho do ícone */
height: 32px;
top: -5px; /* Ajusta a posição */
right: -5px;
z-index: 5; /* Garante que o ícone fique na camada superior */
}
</style>
