<!-- src/pages/YardHome.vue -->
<template>
    <q-page class="page q-px-lg">
        <!-- Barra de configurações -->
        <q-toolbar class="q-px-md q-py-sm text-white">
            <!-- Componente filho que emite o evento 'map-selected' -->
            <create-map-yard @map-selected="handleMapSelected" />

            <!-- Tamanho da Fonte -->
            <q-input
                v-model.number="fontSize"
                type="number"
                label="Tamanho da Fonte"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 200px; margin-left: 20px;"
                step="1"
                min="10"
                max="100"
            />

            <!-- Botão para abrir o seletor de cor da fonte -->
            <q-btn
                flat
                icon="color_lens"
                label="Cor da Fonte"
                color="primary"
                class="q-ml-sm"
                @click="toggleFontColorDialog"
            />

            <!-- Tamanho da Bolinha -->
            <q-input
                v-model.number="dotSize"
                type="number"
                label="Tamanho da Bolinha"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 220px; margin-left: 20px;"
                step="1"
                min="5"
                max="50"
            />

            <!-- Largura do Dropzone -->
            <q-input
                v-model.number="dropzoneWidth"
                type="number"
                label="Largura do Pátio"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 180px; margin-left: 20px;"
                step="10"
                min="500"
                max="2000"
            />

            <!-- Altura do Dropzone -->
            <q-input
                v-model.number="dropzoneHeight"
                type="number"
                label="Altura do Pátio"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 180px;margin-left: 20px;"
                step="10"
                min="300"
            />

            <!-- Controle de Opacidade -->
            <q-input
                v-model.number="backgroundOpacity"
                type="number"
                label="Opacidade do Pátio"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 220px; margin-left: 20px; margin-right: 20px; margin-bottom: -20px"
                step="0.1"
                min="0"
                max="1"
                :rules="[
                    val => (val >= 0 && val <= 1) || 'Opacidade deve estar entre 0 e 1'
                ]"
            />

            <!-- Controle de Zoom -->
            <q-input
                v-model.number="zoom"
                type="number"
                label="Zoom"
                outlined
                dense
                style="width: 100px; margin-right: 20px; margin-bottom: -20px"
                step="0.1"
                min="0.1"
                max="3"
                :rules="[
                    val => (val >= 0.1 && val <= 3) || 'Zoom deve estar entre 0.1 e 3'
                ]"
            />

            <!-- Controle de Rotação -->
            <q-input
                v-model.number="rotation"
                type="number"
                label="Rotação"
                outlined
                dense
                style="width: 150px;"
                step="1"
                min="0"
                max="360"
                suffix="°"
            />

            <!-- Botão para selecionar a imagem de fundo -->
            <q-btn
                flat
                icon="image"
                label="Alterar Imagem de Fundo"
                color="primary"
                class="q-ml-sm"
                @click="triggerImageUpload"
            />

            <!-- Input de arquivo oculto -->
            <input
                ref="imageUploadInput"
                type="file"
                accept="image/*"
                style="display: none;"
                @change="handleImageUpload"
            >

            <!-- Diálogo que contém o seletor de cores para a fonte -->
            <q-dialog v-model="showFontColorDialog">
                <q-card>
                    <q-card-section>
                        <q-color
                            v-model="fontColor"
                            flat
                            color="primary"
                            class="q-ml-sm"
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

            <!-- Controle de Cor do Sobrefundo -->
            <q-btn
                flat
                icon="color_lens"
                label="Cor do Sobrefundo"
                color="primary"
                class="q-ml-sm"
                @click="toggleOverlayColorDialog"
            />

            <q-dialog v-model="showOverlayColorDialog">
                <q-card>
                    <q-card-section>
                        <q-color
                            v-model="overlayColor"
                            flat
                            color="primary"
                            class="q-ml-sm"
                        />
                    </q-card-section>
                    <q-card-actions align="right">
                        <q-btn
                            flat
                            label="Fechar"
                            @click="toggleOverlayColorDialog"
                        />
                    </q-card-actions>
                </q-card>
            </q-dialog>

            <!-- Controle de Opacidade do Sobrefundo -->
            <q-input
                v-model.number="overlayOpacity"
                type="number"
                label="Opacidade do Sobrefundo"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 260px; margin-left: 20px; margin-bottom: -20px"
                step="0.1"
                min="0"
                max="1"
                :rules="[
                    val => (val >= 0 && val <= 1) || 'Opacidade deve estar entre 0 e 1'
                ]"
            />
        </q-toolbar>

        <div
            class="col-9 storloc-dropzone"
            style="margin-left: -25px; margin-right: -25px;"
        >
            <q-card class="q-pa-md dropzone-card">
                <q-toolbar-title>Yard Controller</q-toolbar-title>

                <!-- Área de Drop com Mapa como Background -->
                <div
                    ref="dropzone"
                    class="dropzone"
                    :style="{
                        opacity: backgroundOpacity,
                        transform: `scale(${zoom}) rotate(${rotation}deg)`,
                        backgroundImage: `url(${backgroundImagePath})`,
                        width: dropzoneWidth + 'px',
                        height: dropzoneHeight + 'px'
                    }"
                >
                    <!-- Sobre o fundo com a opacidade e cor configuráveis -->
                    <div
                        class="overlay"
                        :style="{
                            backgroundColor: overlayColor,
                            opacity: overlayOpacity
                        }"
                    />

                    <!-- Storlocs já posicionadas -->
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
                        @dblclick="openDetails(storloc)"
                    >
                        <q-item
                            clickable
                            class="storloc-item"
                        >
                            <q-item-section>
                                <q-item-label>{{ storloc.zoneId.length > 12 ? storloc.zoneId.slice(0, 12) + '...' : storloc.zoneId }}</q-item-label>
                                <div
                                    class="label-circle"
                                    :style="{ backgroundColor: dotColor, width: dotSize + 'px', height: dotSize + 'px' }"
                                />
                            </q-item-section>
                        </q-item>
                    </div>
                </div>

                <!-- Botões para salvar e componente CRUD -->
                <div class="q-mt-md row items-center q-gutter-sm">
                    <q-btn
                        label="Salvar Posições"
                        color="primary"
                        class="q-mr-sm"
                        @click="savePositions"
                    />
                    <q-btn
                        label="Salvar Configurações"
                        color="secondary"
                        class="q-mr-sm"
                        @click="saveMapConfigurations"
                    />
                    <!-- Componente CRUD ao lado -->
                    <crud-component
                        class="crud-component-aligned"
                        style="margin-top: -8px;"
                    />
                </div>
            </q-card>
        </div>

        <!-- Diálogo de Edição -->
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
                        v-model="selectedStorloc.storlocItem"
                        label="Tipo de Veículo"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="selectedStorloc.index"
                        label="Índice"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="selectedStorloc.area"
                        label="Área"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="selectedStorloc.rack"
                        label="Rack"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="selectedStorloc.zoneId"
                        label="Zona"
                        outlined
                        dense
                    />
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn
                        flat
                        label="Editar"
                        color="primary"
                        @click="updateStorloc"
                    />
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
import { onMounted, ref, reactive } from 'vue';
import interact from 'interactjs';
import Estacionamento from 'src/assets/imgVehicle/patio.png';
import { useQuasar } from 'quasar';
import { createAxiosInstance } from '../../api/axiosInstance';
import CreateMapYard from './CreateMapYard.vue';
import CrudComponent from './CreateComponent.vue';

const axiosInstance = createAxiosInstance('yard');
const $q = useQuasar();

const availableStorlocs = ref([]);
const placedStorlocs = ref([]);
const dropzone = ref(null);
const showDetailsDialog = ref(false);
const selectedStorloc = reactive({
    ean: '',
    storlocItem: '',
    index: '',
    area: '',
    rack: '',
    zoneId: '',
});

// Variáveis de configuração
const fontSize = ref(12);
const fontColor = ref('#000000');
const showFontColorDialog = ref(false);
const dotSize = ref(10);
const dotColor = ref('#334ab0');
const showDotColorDialog = ref(false);

// Variáveis para controle do tamanho do dropzone
const dropzoneWidth = ref(1423);
const dropzoneHeight = ref(600);

// Variáveis para imagem de fundo
const backgroundOpacity = ref(1);
const zoom = ref(1);
const rotation = ref(0);
const backgroundImagePath = ref(Estacionamento);

// Variáveis para o sobre-fundo
const overlayOpacity = ref(0.1);
const overlayColor = ref('#FFFFFF');
const showOverlayColorDialog = ref(false);

// Variáveis adicionais para configurações do mapa
const overlayRotation = ref(0);
const overlayZoom = ref(1);
const overlayXPosition = ref(0);
const overlayYPosition = ref(0);
const overlayWidth = ref(0);
const overlayHeight = ref(0);
const overlayBlendMode = ref('');

// Variáveis para armazenar o mapa selecionado
const selectedMapYardId = ref(null);
const selectedMapName = ref('');

// Referência ao input de upload de imagem
const imageUploadInput = ref(null);

// Função para lidar com o mapa selecionado
function handleMapSelected(selectedMap) {
    // Atualiza as configurações com base no mapa selecionado

    // Valores inteiros simples
    fontSize.value = parseInt(selectedMap.fontSize, 10) || 12;
    fontColor.value = selectedMap.fontColor || '#000000';
    showFontColorDialog.value = selectedMap.showFontColorDialog || false;
    dotSize.value = parseInt(selectedMap.dotSize, 10) || 10;
    dotColor.value = selectedMap.dotColor || '#334ab0';
    showDotColorDialog.value = selectedMap.showDotColorDialog || false;
    dropzoneWidth.value = parseInt(selectedMap.dropzoneWidth, 10) || 1423;
    dropzoneHeight.value = parseInt(selectedMap.dropzoneHeight, 10) || 600;
    rotation.value = parseInt(selectedMap.rotation, 10) || 0;
    backgroundImagePath.value = selectedMap.backgroundImagePath || Estacionamento;
    overlayColor.value = selectedMap.overlayColor || '#FFFFFF';
    overlayRotation.value = parseInt(selectedMap.overlayRotation, 10) || 0;
    overlayXPosition.value = parseInt(selectedMap.overlayXPosition, 10) || 0;
    overlayYPosition.value = parseInt(selectedMap.overlayYPosition, 10) || 0;
    overlayWidth.value = parseInt(selectedMap.overlayWidth, 10) || 0;
    overlayHeight.value = parseInt(selectedMap.overlayHeight, 10) || 0;
    overlayBlendMode.value = selectedMap.overlayBlendMode || '';

    // Valores que precisam ser divididos por 100
    backgroundOpacity.value = parseInt(selectedMap.backgroundOpacity, 10) / 100 || 1;
    zoom.value = parseInt(selectedMap.zoom, 10) / 100 || 1;
    overlayOpacity.value = parseInt(selectedMap.overlayOpacity, 10) / 100 || 0.1;
    overlayZoom.value = parseInt(selectedMap.overlayZoom, 10) / 100 || 1;

    selectedMapYardId.value = selectedMap.mapYardId;
    selectedMapName.value = selectedMap.mapName || 'Mapa Sem Nome';

    // Buscar storlocs associadas ao mapa selecionado
    fetchStorlocs();
}

onMounted(() => {
    setupDragAndDrop();
    // Chamar fetchStorlocs ao iniciar a página
    if (selectedMapYardId.value) {
        fetchStorlocs();
    }
});

function toggleFontColorDialog() {
    showFontColorDialog.value = !showFontColorDialog.value;
}

function toggleOverlayColorDialog() {
    showOverlayColorDialog.value = !showOverlayColorDialog.value;
}

async function fetchStorlocs() {
    if (!selectedMapYardId.value) {
        return;
    }
    try {
        const response = await axiosInstance.get(`/rest/storlocPositionMap/${selectedMapYardId.value}`);
        const data = response.data.content || [];

        let idCounter = 0;

        placedStorlocs.value = data.map((s) => {
            const position = s.area && isValidJSON(s.area) ? JSON.parse(s.area) : null;
            if (position) {
                return { ...s, position, uniqueId: idCounter++ };
            }
            return null; // Exclui storlocs sem posição
        }).filter((s) => s !== null); // Remove os itens nulos
    } catch (error) {
        console.error('Erro ao buscar storlocs:', error);
        $q.notify({ message: 'Erro ao buscar storlocs.', color: 'negative' });
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

async function updateStorloc() {
    try {
        const axiosInstance = createAxiosInstance('dev');
        await axiosInstance.put(`/rest/storloc/${selectedStorloc.ean}`, selectedStorloc);
        $q.notify({ message: 'Storloc atualizada com sucesso!', color: 'positive' });
        showDetailsDialog.value = false;
        fetchStorlocs(); // Atualiza os dados após a atualização
    } catch (error) {
        console.error('Erro ao atualizar storloc:', error);
        $q.notify({ message: 'Erro ao atualizar storloc.', color: 'negative' });
    }
    const axiosInstance = createAxiosInstance('yard');
}

async function savePositions() {
    if (!selectedMapYardId.value) {
        $q.notify({ message: 'Nenhum mapa selecionado.', color: 'warning' });
        return;
    }
    try {
        const storlocPositionMapList = placedStorlocs.value.map((storloc) => ({
            ean: storloc.ean,
            positionMap: JSON.stringify(storloc.position),
        }));

        const payload = { storlocPositionMapList };

        await axiosInstance.put(`/rest/storlocPositionMap/${selectedMapYardId.value}`, payload);

        $q.notify({ message: 'Posições salvas com sucesso!', color: 'positive' });
    } catch (error) {
        console.error('Erro ao salvar posições:', error);
        $q.notify({ message: 'Erro ao salvar posições.', color: 'negative' });
    }
}

async function saveMapConfigurations() {
    if (!selectedMapYardId.value) {
        $q.notify({ message: 'Nenhum mapa selecionado.', color: 'warning' });
        return;
    }

    try {
        const payload = {
            mapName: selectedMapName.value,
            fontSize: parseInt(fontSize.value, 10),
            fontColor: fontColor.value,
            showFontColorDialog: showFontColorDialog.value,
            dotSize: parseInt(dotSize.value, 10),
            dotColor: dotColor.value,
            showDotColorDialog: showDotColorDialog.value,
            dropzoneWidth: parseInt(dropzoneWidth.value, 10),
            dropzoneHeight: parseInt(dropzoneHeight.value, 10),
            backgroundOpacity: parseInt(backgroundOpacity.value * 100, 10), // Convertendo para inteiro
            zoom: parseInt(zoom.value * 100, 10), // Convertendo para inteiro
            rotation: parseInt(rotation.value, 10),
            backgroundImagePath: backgroundImagePath.value,
            overlayColor: overlayColor.value,
            overlayOpacity: parseInt(overlayOpacity.value * 100, 10), // Convertendo para inteiro
            overlayRotation: parseInt(overlayRotation.value, 10),
            overlayZoom: parseInt(overlayZoom.value * 100, 10), // Convertendo para inteiro
            overlayXPosition: parseInt(overlayXPosition.value, 10),
            overlayYPosition: parseInt(overlayYPosition.value, 10),
            overlayWidth: parseInt(overlayWidth.value, 10),
            overlayHeight: parseInt(overlayHeight.value, 10),
            overlayBlendMode: overlayBlendMode.value,
            modified: new Date().toISOString(),
        };

        await axiosInstance.put(`/rest/mapYard/${selectedMapYardId.value}`, payload);

        $q.notify({ message: 'Configurações do mapa salvas com sucesso!', color: 'positive' });
    } catch (error) {
        console.error('Erro ao salvar configurações do mapa:', error);
        $q.notify({ message: 'Erro ao salvar configurações do mapa.', color: 'negative' });
    }
}

function triggerImageUpload() {
    if (!selectedMapYardId.value) {
        $q.notify({ message: 'Nenhum mapa selecionado.', color: 'warning' });
        return;
    }
    imageUploadInput.value.click();
}

async function handleImageUpload(event) {
    const file = event.target.files[0];
    if (!file) {
        return;
    }

    const formData = new FormData();
    formData.append('picMapYard', file);

    try {
        const response = await axiosInstance.put(`/rest/mapYard/picture/${selectedMapYardId.value}`, formData, {
            headers: {
                'Content-storlocItem': 'multipart/form-data',
            },
        });

        backgroundImagePath.value = response.data.content.backgroundImagePath;

        $q.notify({ message: 'Imagem de fundo atualizada com sucesso!', color: 'positive' });
    } catch (error) {
        console.error('Erro ao atualizar a imagem de fundo:', error);
        $q.notify({ message: 'Erro ao atualizar a imagem de fundo.', color: 'negative' });
    } finally {
        event.target.value = '';
    }
}

function setupDragAndDrop() {
    interact('.draggable-item').draggable({
        inertia: true,
        listeners: {
            start(event) {
                event.target.style.zIndex = '9999';
            },
            move(event) {
                updatePosition(event);
            },
            end(event) {
                updatePosition(event);
                event.target.style.zIndex = '';
            },
        },
    });

    interact(dropzone.value).dropzone({
        accept: '.draggable-item',
        overlap: 0.75,
    });
}

function updatePosition(event) {
    const storlocId = parseInt(event.target.getAttribute('data-id'), 10);
    const storlocIndex = placedStorlocs.value.findIndex((s) => s.uniqueId === storlocId);

    if (storlocIndex !== -1) {
        const position = getRelativePosition(event);
        placedStorlocs.value[storlocIndex].position = position;
    }

    const target = event.target;
    const x = (parseFloat(target.getAttribute('data-x')) || 0) + event.dx;
    const y = (parseFloat(target.getAttribute('data-y')) || 0) + event.dy;

    target.style.transform = `translate(${x}px, ${y}px)`;
    target.setAttribute('data-x', x);
    target.setAttribute('data-y', y);
}

function getRelativePosition(event) {
    const zoneRect = dropzone.value.getBoundingClientRect();
    const x = event.pageX - zoneRect.left;
    const y = event.pageY - zoneRect.top;
    return { x, y };
}
</script>

  <style scoped>
  .dropzone {
    position: relative;
    border: 2px dashed #ddd;
    background-size: cover;
    background-position: center;
    background-size: contain;
    background-size: 100% 100%;
  }

  .overlay {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    z-index: 1;
  }

  .draggable-item {
    position: absolute;
    z-index: 2;
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
  </style>
