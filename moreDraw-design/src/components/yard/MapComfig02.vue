<template>
    <q-page class="page q-px-lg">
        <!-- Barra de Configurações -->
        <q-toolbar
            class="q-px-md q-py-sm"
            style="background-color: #f5f5f5; color: #000;"
        >
            <create-map-yard @map-selected="handleMapSelected" />

            <q-toggle
                v-model="useIcon"
                :label="$t('yardSettings.useIcon')"
                color="primary"
                class="q-ml-sm"
                style="margin-left: 20px;"
            />

            <q-input
                v-model.number="fontSize"
                type="number"
                :label="$t('yardSettings.fontSize')"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 200px; margin-left: 20px;"
                step="1"
                min="10"
                max="100"
            />

            <q-btn
                flat
                icon="color_lens"
                :label="$t('yardSettings.fontColor')"
                color="primary"
                class="q-ml-sm"
                @click="toggleFontColorDialog"
            />

            <q-input
                v-model.number="circleSize"
                type="number"
                :label="$t('yardSettings.circleSize')"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 220px; margin-left: 20px;"
                step="1"
                min="5"
                max="50"
            />

            <q-input
                v-if="useIcon"
                v-model.number="iconSize"
                type="number"
                :label="$t('yardSettings.iconSize')"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 220px; margin-left: 20px; margin-top: 20px;"
                step="1"
                min="20"
                max="650"
                :rules="[val => (val >= 20 && val <= 650) || $t('yardSettings.iconSizeRules')]"
            />

            <q-input
                v-if="useIcon"
                v-model.number="iconRotation"
                type="number"
                :label="$t('yardSettings.iconRotation')"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 650px; margin-left: 20px;"
                step="1"
                min="0"
                max="360"
            />

            <q-input
                v-if="useIcon"
                v-model.number="iconSkew"
                type="number"
                :label="$t('yardSettings.iconSkew')"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 180px; margin-left: 20px;"
                step="1"
                min="-45"
                max="45"
            />

            <q-input
                v-model.number="dropzoneWidth"
                type="number"
                :label="$t('yardSettings.dropzoneWidth')"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 180px; margin-left: 20px;"
                step="10"
                min="500"
                max="2000"
            />

            <q-input
                v-model.number="dropzoneHeight"
                type="number"
                :label="$t('yardSettings.dropzoneHeight')"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 180px; margin-left: 20px;"
                step="10"
                min="300"
            />

            <q-input
                v-model.number="backgroundOpacity"
                type="number"
                :label="$t('yardSettings.backgroundOpacity')"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 220px; margin-left: 20px; margin-right: 20px; margin-bottom: -20px"
                step="0.1"
                min="0"
                max="1"
                :rules="[ val => (val >= 0 && val <= 1) || $t('yardSettings.opacityRule') ]"
            />

            <q-input
                v-model.number="zoom"
                type="number"
                :label="$t('yardSettings.zoom')"
                outlined
                dense
                style="width: 100px; margin-right: 20px; margin-bottom: -20px"
                step="0.1"
                min="0.1"
                max="3"
                :rules="[ val => (val >= 0.1 && val <= 3) || $t('yardSettings.zoomRule') ]"
            />

            <q-input
                v-model.number="rotation"
                type="number"
                :label="$t('yardSettings.rotation')"
                outlined
                dense
                style="width: 150px;"
                step="1"
                min="0"
                max="360"
                suffix="°"
            />

            <q-btn
                flat
                icon="image"
                :label="$t('yardSettings.changeBackgroundImage')"
                color="primary"
                class="q-ml-sm"
                @click="triggerImageUpload"
            />

            <input
                ref="imageUploadInput"
                type="file"
                accept="image/*"
                style="display: none;"
                @change="handleImageUpload"
            >

            <q-dialog v-model="showFontColorDialog">
                <spk-card>
                    <spk-card-section>
                        <q-color
                            v-model="fontColor"
                            flat
                            color="primary"
                            class="q-ml-sm"
                        />
                    </spk-card-section>
                    <spk-card-actions align="right">
                        <q-btn
                            flat
                            :label="$t('yardSettings.close')"
                            @click="toggleFontColorDialog"
                        />
                    </spk-card-actions>
                </spk-card>
            </q-dialog>

            <q-btn
                flat
                icon="color_lens"
                :label="$t('yardSettings.overlayColor')"
                color="primary"
                class="q-ml-sm"
                @click="toggleOverlayColorDialog"
            />

            <q-dialog v-model="showOverlayColorDialog">
                <spk-card>
                    <spk-card-section>
                        <q-color
                            v-model="overlayColor"
                            flat
                            color="primary"
                            class="q-ml-sm"
                        />
                    </spk-card-section>
                    <spk-card-actions align="right">
                        <q-btn
                            flat
                            :label="$t('yardSettings.close')"
                            @click="toggleOverlayColorDialog"
                        />
                    </spk-card-actions>
                </spk-card>
            </q-dialog>

            <q-input
                v-model.number="overlayOpacity"
                type="number"
                :label="$t('yardSettings.overlayOpacity')"
                outlined
                dense
                color="primary"
                class="q-ml-sm"
                style="width: 260px; margin-left: 20px; margin-bottom: -20px"
                step="0.1"
                min="0"
                max="1"
                :rules="[ val => (val >= 0 && val <= 1) || $t('yardSettings.opacityRule') ]"
            />

            <!-- Botão no canto superior direito para ativar/desativar edição de ícones -->
            <q-btn
                flat
                :icon="isEditingIconsMode ? 'close' : 'edit'"
                :label="isEditingIconsMode ? $t('yardSettings.cancelIconEditing') : $t('yardSettings.editIcons')"
                color="primary"
                class="q-ml-auto"
                @click="toggleIconsEditingMode"
            />
        </q-toolbar>

        <div
            class="col-9 storloc-dropzone"
            style="margin-left: -25px; margin-right: -25px;"
        >
            <spk-card
                class="q-pa-md dropzone-card"
                style="margin-left: -14px;"
            >
                <q-toolbar-title>Yard Controller</q-toolbar-title>

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
                    <div
                        class="overlay"
                        :style="{
                            backgroundColor: overlayColor,
                            opacity: overlayOpacity
                        }"
                    />
                    <div
                        v-for="storloc in placedStorlocs"
                        :key="storloc.ean"
                        class="draggable-item"
                        :data-id="storloc.ean"
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
                            <q-item-section style="position: relative;">
                                <div
                                    class="icon-wrapper"
                                    :class="{ 'icon-wrapper-active': isEditingIconsMode && useIcon }"
                                >
                                    <img
                                        v-if="useIcon"
                                        :src="truckIcon"
                                        :width="storloc.iconSize || iconSize"
                                        :style="{
                                            transform: `rotate(${storloc.iconRotation !== undefined ? storloc.iconRotation : iconRotation}deg)
                                                        skew(${storloc.iconSkew !== undefined ? storloc.iconSkew : iconSkew}deg)`,
                                            display: 'block'
                                        }"
                                        class="storloc-icon"
                                        :alt="$t('yardSettings.storlocIconAlt')"
                                    >
                                    <div
                                        v-else
                                        class="label-circle"
                                        :style="{ backgroundColor: dotColor, width: circleSize + 'px', height: circleSize + 'px' }"
                                    />
                                    <!-- Handles para edição do ícone se estiver em modo de edição global -->
                                    <div
                                        v-if="isEditingIconsMode && useIcon"
                                        class="icon-edit-handles"
                                    >
                                        <div
                                            class="handle skew-handle top-left"
                                            @mousedown="startIconSkew(storloc, $event, 'top-left')"
                                        />
                                        <div
                                            class="handle skew-handle top-right"
                                            @mousedown="startIconSkew(storloc, $event, 'top-right')"
                                        />
                                        <div
                                            class="handle skew-handle bottom-left"
                                            @mousedown="startIconSkew(storloc, $event, 'bottom-left')"
                                        />
                                        <div
                                            class="handle skew-handle bottom-right"
                                            @mousedown="startIconSkew(storloc, $event, 'bottom-right')"
                                        />
                                        <div
                                            class="handle rotate-handle"
                                            @mousedown="startIconRotation(storloc, $event)"
                                        />
                                    </div>
                                </div>
                                <q-item-label
                                    style="position: absolute; top: 0; left: 0; width: 100%; text-align: center; color: black; z-index: 10;"
                                >
                                    {{ storloc && storloc.zoneId && storloc.zoneId.length > 12 ? storloc.zoneId.slice(0, 12) + '...' : storloc?.zoneId || '' }}
                                </q-item-label>
                            </q-item-section>
                        </q-item>
                    </div>
                </div>

                <div class="q-mt-md row items-center q-gutter-sm">
                    <q-btn
                        :label="$t('yardSettings.savePositions')"
                        color="primary"
                        class="q-mr-sm"
                        :loading="isSavingPositions"
                        :disable="isEditingIconsMode"
                        @click="savePositions"
                    />
                    <q-btn
                        :label="$t('yardSettings.saveConfigurations')"
                        color="secondary"
                        class="q-mr-sm"
                        :loading="isSavingConfigurations"
                        :disable="isEditingIconsMode"
                        @click="saveMapConfigurations"
                    />
                    <crud-component
                        class="crud-component-aligned"
                        style="margin-top: -8px;"
                        :disabled="isEditingIconsMode"
                    />
                </div>
            </spk-card>
        </div>

        <!-- Diálogo de Edição -->
        <q-dialog v-model="showDetailsDialog">
            <spk-card>
                <spk-card-section>
                    <div class="text-h6">
                        {{ $t('yardSettings.storlocDetails') }} - {{ selectedStorloc.ean || 'N/A' }}
                    </div>
                </spk-card-section>
                <spk-card-section>
                    <q-input
                        v-model="selectedStorloc.ean"
                        :label="$t('yardSettings.ean')"
                        :readonly="!isEditing"
                        outlined
                        dense
                        color="primary"
                        autofocus
                    />
                    <q-input
                        v-model="selectedStorloc.storlocItem"
                        :label="$t('yardSettings.vehiclePlate')"
                        :readonly="!isEditing"
                        outlined
                        dense
                        color="primary"
                    />
                    <q-select
                        v-model="selectedStorloc.type"
                        :label="$t('yardSettings.type')"
                        :options="typeOptions"
                        option-value="value"
                        option-label="label"
                        emit-value
                        dense
                    />
                    <q-input
                        v-model="selectedStorloc.index"
                        :label="$t('yardSettings.index')"
                        :readonly="!isEditing"
                        outlined
                        dense
                        color="primary"
                    />
                    <q-input
                        v-model="selectedStorloc.area"
                        :label="$t('yardSettings.area')"
                        :readonly="!isEditing"
                        outlined
                        dense
                        color="primary"
                    />
                    <q-input
                        v-model="selectedStorloc.rack"
                        :label="$t('yardSettings.rack')"
                        :readonly="!isEditing"
                        outlined
                        dense
                        color="primary"
                    />
                    <q-input
                        v-model="selectedStorloc.zoneId"
                        :label="$t('yardSettings.zone')"
                        :readonly="!isEditing"
                        outlined
                        dense
                        color="primary"
                    />
                </spk-card-section>
                <spk-card-actions align="right">
                    <q-btn
                        v-if="!isEditing"
                        flat
                        :label="$t('yardSettings.edit')"
                        color="primary"
                        @click="startEditing"
                    />
                    <q-btn
                        v-if="isEditing"
                        flat
                        :label="$t('yardSettings.save')"
                        color="primary"
                        :loading="isSavingConfigurations"
                        @click="updateStorloc"
                    />
                    <q-btn
                        v-if="isEditing"
                        flat
                        :label="$t('yardSettings.cancel')"
                        color="secondary"
                        @click="cancelEditing"
                    />
                    <q-btn
                        flat
                        :label="$t('yardSettings.close')"
                        @click="closeDetailsDialog"
                    />
                    <q-btn
                        flat
                        :label="$t('yardSettings.delete')"
                        @click="deleteDetailsDialog"
                    />
                </spk-card-actions>
            </spk-card>
        </q-dialog>
    </q-page>
</template>

<script setup>
import { onMounted, ref, reactive, watch, nextTick } from 'vue';
import interact from 'interactjs';
import Estacionamento from 'src/assets/imgVehicle/patio.png';
import truckIcon from 'src/assets/imgVehicle/truckIcon.png';
import { useQuasar } from 'quasar';
import { createAxiosInstance } from '../../api/axiosInstance';
import CreateMapYard from './CreateMapYard.vue';
import CrudComponent from './CreateComponent.vue';

const $q = useQuasar();
const axiosInstance = createAxiosInstance('yard');
const axiosInstancedev = createAxiosInstance('dev');

const isSavingPositions = ref(false);
const isSavingConfigurations = ref(false);

const placedStorlocs = ref([]);
const dropzone = ref(null);
const showDetailsDialog = ref(false);
const selectedStorloc = reactive({
    ean: '',
    storlocItem: '',
    type: '',
    index: '',
    area: '',
    rack: '',
    zoneId: '',
    iconSize: null,
    iconRotation: null,
    iconSkew: null,
});
const originalStorloc = ref({});

const typeOptions = [
    { label: 'Portaria', value: '20' },
    { label: 'Pátio', value: '30' },
    { label: 'Doca', value: '40' },
];

const isEditing = ref(false);

const fontSize = ref(12);
const fontColor = ref('#000000');
const showFontColorDialog = ref(false);
const circleSize = ref(14);
const iconSize = ref(70);
const dotColor = ref('#334ab0');
const dropzoneWidth = ref(1423);
const dropzoneHeight = ref(600);
const backgroundOpacity = ref(1);
const zoom = ref(1);
const rotation = ref(0);
const backgroundImagePath = ref(Estacionamento);
const overlayOpacity = ref(0.1);
const overlayColor = ref('#FFFFFF');
const showOverlayColorDialog = ref(false);
const selectedMapYardId = ref(null);
const selectedMapName = ref('');
const imageUploadInput = ref(null);
const useIcon = ref(false);
const iconRotation = ref(0);
const iconSkew = ref(0);

// Modo global de edição de ícones
const isEditingIconsMode = ref(false);

function handleMapSelected(selectedMap) {
    fontSize.value = parseInt(selectedMap.fontSize, 10) || 12;
    fontColor.value = selectedMap.fontColor || '#000000';
    circleSize.value = parseInt(selectedMap.circleSize, 10) || 14;
    iconSize.value = parseInt(selectedMap.iconSize, 10) || 70;
    dotColor.value = selectedMap.dotColor || '#334ab0';
    dropzoneWidth.value = parseInt(selectedMap.dropzoneWidth, 10) || 1423;
    dropzoneHeight.value = parseInt(selectedMap.dropzoneHeight, 10) || 600;
    rotation.value = parseInt(selectedMap.rotation, 10) || 0;
    backgroundImagePath.value = selectedMap.backgroundImagePath || Estacionamento;
    overlayColor.value = selectedMap.overlayColor || '#FFFFFF';
    backgroundOpacity.value = parseFloat(selectedMap.backgroundOpacity) / 100 || 1;
    zoom.value = parseFloat(selectedMap.zoom) / 100 || 1;
    overlayOpacity.value = parseFloat(selectedMap.overlayOpacity) / 100 || 0.1;
    useIcon.value = selectedMap.typeIcon === 'icon';

    if (useIcon.value) {
        iconSize.value = parseFloat(selectedMap.iconSize) || 70;
        iconRotation.value = parseFloat(selectedMap.iconRotation) || 0;
        iconSkew.value = parseFloat(selectedMap.iconSkew) || 0;
    } else {
        iconRotation.value = 0;
        iconSkew.value = 0;
    }

    selectedMapYardId.value = selectedMap.mapYardId;
    selectedMapName.value = selectedMap.mapName || 'Mapa Sem Nome';

    fetchStorlocs();

    // Salvando o mapa selecionado no Local Storage
    try {
        localStorage.setItem('selectedMap', JSON.stringify(selectedMap));
        console.log('Mapa salvo no Local Storage.');
    } catch (error) {
        console.error('Erro ao salvar mapa no Local Storage:', error);
    }
}

onMounted(() => {
    setupDragAndDrop();
    if (selectedMapYardId.value) {
        fetchStorlocs();
    }
    const savedMap = localStorage.getItem('selectedMap');
    if (savedMap) {
        try {
            const parsedMap = JSON.parse(savedMap);
            console.log('Carregando mapa do Local Storage:', parsedMap);
            handleMapSelected(parsedMap);
        } catch (error) {
            console.error('Erro ao carregar mapa do Local Storage:', error);
            // Opcional: remover o item inválido do Local Storage
            localStorage.removeItem('selectedMap');
        }
    } else {
        console.log('Nenhum mapa salvo no Local Storage.');
    }
});

watch(iconSize, (newValue) => {
    if (newValue < 20 || newValue > 650) {
        $q.notify({ message: 'Tamanho do ícone deve estar entre 20 e 650.', color: 'negative' });
        iconSize.value = Math.min(650, Math.max(20, newValue)); // Corrigido para ajustar ao limite máximo correto
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

        // Filtra duplicatas por EAN para evitar duas instâncias do mesmo storloc
        const storlocMap = new Map();
        for (const s of data) {
            const position = s.area && isValidJSON(s.area) ? JSON.parse(s.area) : { x: 0, y: 0 };

            // Caso já tenha um storloc com o mesmo EAN, ignoramos o duplicado
            if (!storlocMap.has(s.ean)) {
                storlocMap.set(s.ean, {
                    ...s,
                    position,
                    iconSize: s.iconSize !== undefined ? parseInt(s.iconSize, 10) : undefined,
                    iconRotation: s.iconRotation !== undefined ? parseFloat(s.iconRotation) : undefined,
                    iconSkew: s.iconSkew !== undefined ? parseFloat(s.iconSkew) : undefined,
                });
            }
        }

        placedStorlocs.value = Array.from(storlocMap.values());

        await nextTick();
        setupDragAndDrop();
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

    if (selectedStorloc.iconSize == null) selectedStorloc.iconSize = iconSize.value;
    if (selectedStorloc.iconRotation == null) selectedStorloc.iconRotation = iconRotation.value;
    if (selectedStorloc.iconSkew == null) selectedStorloc.iconSkew = iconSkew.value;

    originalStorloc.value = { ...storloc };
    isEditing.value = false;
    showDetailsDialog.value = true;
}

function startEditing() {
    isEditing.value = true;
}

function cancelEditing() {
    Object.assign(selectedStorloc, originalStorloc.value);
    isEditing.value = false;
}

function closeDetailsDialog() {
    isEditing.value = false;
    showDetailsDialog.value = false;
}

async function deleteDetailsDialog() {
    try {
        console.log(`Id do mapa selecionado: ${selectedMapYardId.value}`);
        await axiosInstance.delete(`/rest/deleteStorlocPositionMap/${selectedStorloc.ean}`, {
            params: { mapYardId: selectedMapYardId.value },
        });

        $q.notify({ message: 'Storloc atualizada com sucesso!', color: 'positive' });
        isEditing.value = false;
        showDetailsDialog.value = false;
        fetchStorlocs();
    } catch (error) {
        console.error('Erro ao atualizar storloc:', error);
        $q.notify({ message: 'Erro ao atualizar storloc.', color: 'negative' });
    }
}

async function updateStorloc() {
    try {
        await axiosInstancedev.put(`/rest/storloc/${selectedStorloc.ean}`, selectedStorloc);
        $q.notify({ message: 'Storloc atualizada com sucesso!', color: 'positive' });
        isEditing.value = false;
        showDetailsDialog.value = false;
        fetchStorlocs();
    } catch (error) {
        console.error('Erro ao atualizar storloc:', error);
        $q.notify({ message: 'Erro ao atualizar storloc.', color: 'negative' });
    }
}

async function savePositions() {
    if (!selectedMapYardId.value) {
        $q.notify({ message: 'Nenhum mapa selecionado.', color: 'warning' });
        return;
    }
    isSavingPositions.value = true;
    try {
        const storlocPositionMapList = placedStorlocs.value.map((storloc) => ({
            ean: storloc.ean,
            positionMap: JSON.stringify(storloc.position),
            iconSize: storloc.iconSize !== undefined ? storloc.iconSize : null,
            iconRotation: storloc.iconRotation !== undefined ? storloc.iconRotation : null,
            iconSkew: storloc.iconSkew !== undefined ? storloc.iconSkew : null,
        }));

        const payload = { storlocPositionMapList };

        await axiosInstance.put(`/rest/storlocPositionMap/${selectedMapYardId.value}`, payload);

        $q.notify({ message: 'Posições salvas com sucesso!', color: 'positive' });
    } catch (error) {
        console.error('Erro ao salvar posições:', error);
        $q.notify({ message: 'Erro ao salvar posições.', color: 'negative' });
    } finally {
        isSavingPositions.value = false;
    }
}

async function saveMapConfigurations() {
    if (!selectedMapYardId.value) {
        $q.notify({ message: 'Nenhum mapa selecionado.', color: 'warning' });
        return;
    }

    isSavingConfigurations.value = true;

    try {
        const payload = {
            mapName: selectedMapName.value,
            fontSize: parseInt(fontSize.value, 10),
            fontColor: fontColor.value,
            circleSize: parseInt(circleSize.value, 10),
            iconSize: parseInt(iconSize.value, 10),
            dotColor: dotColor.value,
            dropzoneWidth: parseInt(dropzoneWidth.value, 10),
            dropzoneHeight: parseInt(dropzoneHeight.value, 10),
            backgroundOpacity: parseFloat(backgroundOpacity.value) * 100,
            zoom: parseFloat(zoom.value) * 100,
            rotation: parseInt(rotation.value, 10),
            backgroundImagePath: backgroundImagePath.value,
            overlayColor: overlayColor.value,
            overlayOpacity: parseFloat(overlayOpacity.value) * 100,
            typeIcon: useIcon.value ? 'icon' : 'circle',
            iconRotation: useIcon.value ? parseFloat(iconRotation.value) : 0,
            iconSkew: useIcon.value ? parseFloat(iconSkew.value) : 0,
            modified: new Date().toISOString(),
        };

        await axiosInstance.put(`/rest/mapYard/${selectedMapYardId.value}`, payload);

        $q.notify({ message: 'Configurações do mapa salvas com sucesso!', color: 'positive' });
    } catch (error) {
        console.error('Erro ao salvar configurações do mapa:', error);
        $q.notify({ message: 'Erro ao salvar configurações do mapa.', color: 'negative' });
    } finally {
        isSavingConfigurations.value = false;
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
                'Content-Type': 'multipart/form-data',
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
    interact('.draggable-item').unset();

    interact('.draggable-item').draggable({
        inertia: true,
        listeners: {
            start(event) {
                if (!isEditingIconsMode.value) {
                    event.target.style.zIndex = '9999';
                }
            },
            move(event) {
                if (!isEditingIconsMode.value) {
                    updatePosition(event);
                }
            },
            end(event) {
                if (!isEditingIconsMode.value) {
                    event.target.style.zIndex = '';
                }
            },
        },
    });
}

function updatePosition(event) {
    if (isEditingIconsMode.value) return;

    const storlocId = event.target.getAttribute('data-id');
    const storloc = placedStorlocs.value.find((s) => s.ean === storlocId);

    if (storloc) {
        storloc.position.x += event.dx;
        storloc.position.y += event.dy;
    }
}

// Alterna o modo global de edição de ícones
function toggleIconsEditingMode() {
    isEditingIconsMode.value = !isEditingIconsMode.value;
}

// Lógica para rotação e inclinação do ícone via mouse
let currentStorlocBeingTransformed = null;
let initialMouseX = 0;
let initialMouseY = 0;
let initialRotationVal = 0;
let initialSkewVal = 0;
let rotatingIcon = false;
let skewingIcon = false;
let currentSkewDirection = '';

function startIconRotation(storloc, event) {
    if (!isEditingIconsMode.value) return;
    event.preventDefault();
    event.stopPropagation();
    currentStorlocBeingTransformed = storloc;
    initialMouseX = event.clientX;
    initialMouseY = event.clientY;
    initialRotationVal = storloc.iconRotation ?? iconRotation.value;
    rotatingIcon = true;
    skewingIcon = false;
    currentSkewDirection = '';
    window.addEventListener('mousemove', doIconTransform);
    window.addEventListener('mouseup', stopIconTransform);
}

function startIconSkew(storloc, event, direction) {
    if (!isEditingIconsMode.value) return;
    event.preventDefault();
    event.stopPropagation();
    currentStorlocBeingTransformed = storloc;
    initialMouseX = event.clientX;
    initialMouseY = event.clientY;
    initialSkewVal = storloc.iconSkew ?? iconSkew.value;
    rotatingIcon = false;
    skewingIcon = true;
    currentSkewDirection = direction;
    window.addEventListener('mousemove', doIconTransform);
    window.addEventListener('mouseup', stopIconTransform);
}

function doIconTransform(e) {
    if (!currentStorlocBeingTransformed) return;
    const dx = e.clientX - initialMouseX;

    if (rotatingIcon) {
        const newRotation = initialRotationVal + dx / 2;
        currentStorlocBeingTransformed.iconRotation = newRotation;
    }

    if (skewingIcon) {
        let newSkew = initialSkewVal + dx / 5;
        if (newSkew < -45) newSkew = -45;
        if (newSkew > 45) newSkew = 45;
        currentStorlocBeingTransformed.iconSkew = newSkew;
    }
}

function stopIconTransform() {
    rotatingIcon = false;
    skewingIcon = false;
    currentSkewDirection = '';
    window.removeEventListener('mousemove', doIconTransform);
    window.removeEventListener('mouseup', stopIconTransform);
}
</script>

<style scoped>
.dropzone {
    position: relative;
    border: 2px dashed #ddd;
    background-size: cover;
    background-position: center;
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
    display: flex;
    align-items: center;
    user-select: none;
}

.label-circle {
    border-radius: 50%;
    display: inline-block;
    margin-left: 10px;
}

.text-h6 {
    font-weight: bold;
}

.dropzone-card {
    margin-bottom: 20px;
}

.storloc-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative; /* Mantém o posicionamento relativo */
}

/* Classe para a label da Storloc e da Placa do Veículo */
.storloc-label {
    background: rgba(255, 255, 255, 0.7); /* Fundo semi-transparente */
    padding: 2px 6px; /* Espaçamento interno para melhor cobertura */
    display: block; /* Permite empilhamento vertical */
    white-space: nowrap; /* Evita que o texto quebre linha */
    border-radius: 4px; /* Opcional: arredonda os cantos do fundo */
    pointer-events: none; /* Evita que a label interfira em interações de mouse */
    margin-bottom: 2px; /* Espaçamento entre as labels */
    z-index: 10; /* Garante que a label fique acima do ícone ou bolinha */
}

.storloc-label.no-background {
    background: transparent; /* Remove o fundo */
}

.storloc-icon {
    /* Define o ponto de origem para transformações */
    transform-origin: center center;

    /* Ajusta a margem para posicionamento */
    margin-top: -10px; /* Ajuste conforme necessário */
    margin-left: -20px; /* Ajuste conforme necessário */

    /* Mantém a proporção 1:1 para evitar distorções */
    aspect-ratio: 1 / 1;

    /* Garante que a imagem se ajuste sem distorção */
    object-fit: contain;

    /* Remove a largura fixa para permitir controle via inline ou CSS */
    /* width: 100%; */ /* REMOVIDO */

    /* Define a altura automaticamente para manter a proporção */
    /* height: auto; */ /* REMOVIDO */

    /* Remove possíveis espaços em torno da imagem */
    display: block;

    /* Define transformações padrão */
    transform: rotate(0deg) skew(0deg);

    /* Adiciona transições suaves para transformações */
    transition: transform 0.2s ease-in-out;
}

/* Handles para edição do ícone */
.handle.skew-handle {
    width: 10px;
    height: 10px;
    background: #66bb6a;
    border: 1px solid #000;
    cursor: pointer;
    position: absolute;
}

.skew-handle.top-left {
    top: -5px;
    left: -5px;
}

.skew-handle.top-right {
    top: -5px;
    right: -5px;
}

.skew-handle.bottom-left {
    bottom: -5px;
    left: -5px;
}

.skew-handle.bottom-right {
    bottom: -5px;
    right: -5px;
}

.handle.rotate-handle {
    width: 15px;
    height: 15px;
    background: #ef5350;
    border: 1px solid #000;
    border-radius: 50%;
    cursor: pointer;
    position: absolute;
    top: -25px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 100;
}

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

.perspective-handle {
    position: absolute;
    width: 10px;
    height: 10px;
    background-color: #ff0;
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

.three-container {
    width: 100%;
    height: 400px;
}
</style>
