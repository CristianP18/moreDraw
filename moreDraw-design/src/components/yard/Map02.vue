<template>
    <q-page class="page q-px-lg">
        <spk-card
            class="q-pa-md dropzone-card"
            flat
            bordered
        >
            <!-- Área de Drop com Mapa como Background -->
            <div
                ref="dropzone"
                class="dropzone"
                :style="dropzoneStyles"
                @dragover.prevent="handleDragOver"
                @drop="handleDropOutside"
            >
                <!-- Storlocs já posicionadas -->
                <div
                    v-for="storloc in filteredStorlocs"
                    :key="storloc.uniqueId"
                    class="draggable-item"
                    :data-id="storloc.ean"
                    :style="storlocStyle(storloc)"
                    :draggable="storloc.storlocItem !== ''"
                    @dblclick="openDetails(storloc)"
                    @dragstart="dragStart(storloc)"
                    @dragover.prevent="handleDragOver"
                    @drop="handleDrop(storloc)"
                    @mouseenter="showTooltip(storloc, $event)"
                    @mouseleave="hideTooltip"
                >
                    <q-item
                        clickable
                        class="storloc-item"
                    >
                        <q-item-section class="storloc-section">
                            <!-- Label da Storloc com fundo -->
                            <q-item-label :class="['storloc-label', { 'no-background': !showLabelBackground }]">
                                {{ truncatedZoneId(storloc.zoneId) }}
                            </q-item-label>

                            <template v-if="useIcon && storloc.storlocItem">
                                <img
                                    :src="truckIcon"
                                    :alt="$t('yardMap.storlocIconAlt')"
                                    :style="iconComputedStyle(storloc)"
                                    class="storloc-icon"
                                >
                            </template>
                            <template v-else>
                                <div
                                    class="label-circle"
                                    :style="labelCircleStyle(storloc)"
                                />
                            </template>

                            <!-- Label da placa do veículo com fundo -->
                            <q-item-label
                                v-if="storloc.storlocItem"
                                :class="['storloc-label', { 'no-background': !showLabelBackground }]"
                            >
                                {{ storloc.storlocItem }}
                            </q-item-label>
                        </q-item-section>
                    </q-item>
                </div>

                <!-- Tooltip interativo ao passar o mouse -->
                <q-tooltip
                    v-if="tooltip.visible"
                    :target="tooltipAnchor"
                    transition-show="fade"
                    transition-hide="fade"
                >
                    <div><strong>{{ tooltip.name }}</strong></div>
                    <div v-if="tooltip.item">
                        <em>{{ $t('yardMap.item') }}:</em> {{ tooltip.item }}
                    </div>
                </q-tooltip>
            </div>

            <q-toolbar class="q-px-md q-py-sm">
                <!-- Botão para salvar as trocas -->
                <q-btn
                    :label="$t('yardMap.save')"
                    color="primary"
                    class="q-mt-md"
                    @click="savePositions"
                />
                <!-- Tamanho da Fonte -->
                <q-input
                    v-model.number="fontSize"
                    type="number"
                    :label="$t('yardMap.fontSize')"
                    outlined
                    dense
                    color="primary"
                    class="q-ml-sm"
                    style="width: 150px; margin-right: 20px; margin-left: 100px;"
                />

                <!-- Botão para abrir o seletor de cor da fonte -->
                <q-btn
                    flat
                    icon="color_lens"
                    :label="$t('yardMap.fontColor')"
                    color="primary"
                    class="q-ml-sm"
                    @click="toggleFontColorDialog"
                />

                <!-- Diálogo que contém o seletor de cores para a fonte -->
                <q-dialog v-model="showFontColorDialog">
                    <spk-card>
                        <spk-card-section>
                            <q-color
                                v-model="fontColor"
                                flat
                            />
                        </spk-card-section>
                        <spk-card-actions align="right">
                            <q-btn
                                flat
                                :label="$t('yardMap.close')"
                                @click="toggleFontColorDialog"
                            />
                        </spk-card-actions>
                    </spk-card>
                </q-dialog>

                <!-- Tamanho da Bolinha -->
                <q-input
                    v-model.number="dotSize"
                    type="number"
                    :label="$t('yardMap.dotSize')"
                    outlined
                    dense
                    style="width: 150px; margin-right: 20px;"
                />

                <!-- Input de Tamanho do Ícone -->
                <q-input
                    v-if="useIcon"
                    v-model.number="iconSize"
                    type="number"
                    :label="$t('yardMap.iconSize')"
                    outlined
                    dense
                    color="primary"
                    class="q-ml-sm"
                    style="width: 150px; margin-right: 20px; margin-left: 20px;"
                    step="1"
                    min="10"
                    max="350"
                />

                <!-- Toggle para ativar/desativar o uso do ícone -->
                <q-toggle
                    v-model="useIcon"
                    :label="$t('yardMap.useIcon')"
                    color="primary"
                    class="q-ml-sm"
                    style="margin-left: 20px;"
                />
                <!-- Botão para alternar o fundo das labels -->
                <q-btn
                    flat
                    icon="layers_clear"
                    :label="$t('yardMap.toggleLabelBackground')"
                    color="primary"
                    class="q-ml-sm"
                    @click="toggleLabelBackground"
                />

                <!-- Componente para seleção de mapa -->
                <create-map-yard @map-selected="handleMapSelected" />
            </q-toolbar>
        </spk-card>

        <!-- Diálogo de Edição -->
        <q-dialog v-model="showDetailsDialog">
            <spk-card>
                <spk-card-section>
                    <div class="text-h6">
                        {{ $t('yardMap.storlocDetails') }} - {{ selectedStorloc.zoneId || $t('yardMap.notAvailable') }}
                    </div>
                </spk-card-section>
                <spk-card-section>
                    <q-input
                        v-model="selectedStorloc.zoneId"
                        :label="$t('yardMap.zone')"
                        readonly
                        outlined
                        dense
                        color="primary"
                    />
                    <q-input
                        v-model="selectedStorloc.storlocItem"
                        :label="$t('yardMap.storlocItem')"
                        readonly
                        outlined
                        dense
                        color="primary"
                    />
                    <q-input
                        v-model="selectedStorloc.index"
                        :label="$t('yardMap.index')"
                        readonly
                        outlined
                        dense
                        color="primary"
                    />
                    <q-input
                        v-model="selectedStorloc.area"
                        :label="$t('yardMap.area')"
                        readonly
                        outlined
                        dense
                        color="primary"
                    />
                    <q-input
                        v-model="selectedStorloc.rack"
                        :label="$t('yardMap.rack')"
                        readonly
                        outlined
                        dense
                        color="primary"
                    />
                </spk-card-section>
                <spk-card-actions align="right">
                    <q-btn
                        flat
                        :label="$t('yardMap.close')"
                        @click="showDetailsDialog = false"
                    />
                </spk-card-actions>
            </spk-card>
        </q-dialog>

        <!-- Diálogo de Confirmação de Movimentação -->
        <q-dialog v-model="showMoveDialog">
            <spk-card>
                <spk-card-section>
                    <div class="text-h6">
                        {{ $t('yardMap.movementConfirmation') }}
                    </div>
                </spk-card-section>

                <spk-card-section>
                    <div v-if="!isScheduling">
                        <p><strong>{{ $t('yardMap.sourceStorloc') }}:</strong> {{ moveData.source.zoneId }}</p>
                        <p><strong>{{ $t('yardMap.destinationStorloc') }}:</strong> {{ moveData.destination.zoneId }}</p>
                        <p><strong>{{ $t('yardMap.currentTime') }}:</strong> {{ currentTime }}</p>
                    </div>
                    <div v-else>
                        <q-input
                            v-model="scheduleTime"
                            :label="$t('yardMap.scheduleStartTime')"
                            type="datetime-local"
                            outlined
                            dense
                        />
                    </div>
                    <q-select
                        v-model="eventType"
                        :options="eventOptions"
                        :label="$t('yardMap.eventType')"
                        dense
                        outlined
                        emit-value
                        map-options
                    />
                </spk-card-section>

                <spk-card-actions align="right">
                    <q-btn
                        flat
                        :label="$t('yardMap.cancel')"
                        color="negative"
                        @click="handleCancel"
                    />
                    <q-btn
                        v-if="!isScheduling"
                        flat
                        :label="$t('yardMap.confirm')"
                        color="positive"
                        :loading="isLoading"
                        @click="handleConfirm"
                    />
                    <q-btn
                        v-if="!isScheduling"
                        flat
                        :label="$t('yardMap.schedule')"
                        color="primary"
                        @click="enableScheduling"
                    />
                    <q-btn
                        v-if="isScheduling"
                        flat
                        :label="$t('yardMap.scheduleAndConfirm')"
                        color="primary"
                        :disable="!scheduleTime"
                        :loading="isLoading"
                        @click="handleSchedule"
                    />
                </spk-card-actions>
            </spk-card>
        </q-dialog>

        <!-- Diálogo de Confirmação de Vaga Ocupada -->
        <q-dialog v-model="showOccupiedConfirmationDialog">
            <spk-card>
                <spk-card-section>
                    <div class="text-h6">
                        {{ $t('yardMap.occupiedSpot') }}
                    </div>
                </spk-card-section>
                <spk-card-section>
                    <p>{{ $t('yardMap.destinationOccupiedMessage') }}</p>
                </spk-card-section>
                <spk-card-actions align="right">
                    <q-btn
                        flat
                        :label="$t('yardMap.no')"
                        color="negative"
                        @click="cancelOverwrite"
                    />
                    <q-btn
                        flat
                        :label="$t('yardMap.yes')"
                        color="positive"
                        @click="confirmOverwrite"
                    />
                </spk-card-actions>
            </spk-card>
        </q-dialog>
    </q-page>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useQuasar } from 'quasar';
import Estacionamento from 'src/assets/imgVehicle/patio.png';
import truckIcon from 'src/assets/imgVehicle/truckIcon.png';
import { createAxiosInstance } from '../../api/axiosInstance';
import CreateMapYard from './CreateMapYard.vue';

// Reativo para controlar a visibilidade do fundo das labels
const showLabelBackground = ref(true);

// Função para alternar a visibilidade do fundo das labels
function toggleLabelBackground() {
    showLabelBackground.value = !showLabelBackground.value;
}

const $q = useQuasar();
const axiosInstanceyard = createAxiosInstance('yard');
const axiosInstance = createAxiosInstance('dev2');

const placedStorlocs = ref([]);
const selectedStorloc = reactive({
    zoneId: '',
    storlocItem: '',
    index: '',
    area: '',
    rack: '',
});
const showDetailsDialog = ref(false);
const fontSize = ref(12);
const fontColor = ref('#000000');
const showFontColorDialog = ref(false);
const dotSize = ref(10);
const dropzoneWidth = ref(1423);
const dropzoneHeight = ref(600);

// Configurações adicionais
const rotation = ref(0);
const backgroundImagePath = ref(Estacionamento);
const backgroundOpacity = ref(1);
const useIcon = ref(false);

// Ajuste: agora consideramos iconRotation, iconSkew do storloc ou globais (caso o storloc não tenha)
const iconSize = ref(70);
const iconRotation = ref(0);
const iconSkew = ref(0);

const tooltip = reactive({
    visible: false,
    name: '',
    item: '',
});
const tooltipAnchor = ref(null);

const showMoveDialog = ref(false);
const moveData = reactive({
    source: null,
    destination: null,
});
const eventType = ref('');
const isScheduling = ref(false);
const scheduleTime = ref('');
const eventOptions = [
    { label: 'Tipo A', value: 'tipo_a' },
    { label: 'Tipo B', value: 'tipo_b' },
    { label: 'Tipo C', value: 'tipo_c' },
];

const isLoading = ref(false);
const userConfirmedOverwrite = ref(false);
const showOccupiedConfirmationDialog = ref(false);

function isValidJSON(str) {
    try {
        JSON.parse(str);
        return true;
    } catch {
        return false;
    }
}

const currentTime = computed(() => new Date().toLocaleString());

const dropzoneStyles = computed(() => ({
    width: `${dropzoneWidth.value}px`,
    height: `${dropzoneHeight.value}px`,
    backgroundImage: `url(${backgroundImagePath.value})`,
    transform: `rotate(${rotation.value}deg)`,
    opacity: backgroundOpacity.value,
}));

function truncatedZoneId(zoneId) {
    return zoneId && zoneId.length > 12 ? `${zoneId.slice(0, 12)}...` : zoneId;
}

function storlocStyle(storloc) {
    return {
        transform: `translate(${storloc.position.x}px, ${storloc.position.y}px)`,
        color: fontColor.value,
        fontSize: `${fontSize.value}px`,
    };
}

function labelCircleStyle(storloc) {
    return {
        backgroundColor: storloc.color || '#0000FF',
        width: `${dotSize.value}px`,
        height: `${dotSize.value}px`,
    };
}

// Função para retornar o estilo do ícone considerando rotação e inclinação do storloc
function iconComputedStyle(storloc) {
    const finalRotation = storloc.iconRotation !== undefined ? storloc.iconRotation : iconRotation.value;
    const finalSkew = storloc.iconSkew !== undefined ? storloc.iconSkew : iconSkew.value;
    return {
        width: `${iconSize.value}px`,
        height: `${iconSize.value}px`,
        transform: `rotate(${finalRotation}deg) skew(${finalSkew}deg)`,
        display: 'block',
    };
}

// Computed property para filtrar storlocs
const filteredStorlocs = computed(() => placedStorlocs.value.filter((storloc) => {
    const area = storloc.area;
    let isExcluded = false;

    if (area) {
        if (typeof area === 'string') {
            // Se 'area' for uma string, tenta parsear
            try {
                const parsedArea = JSON.parse(area);
                isExcluded = parsedArea.x === 149.296875 && parsedArea.y === -56.40625;
            } catch (e) {
                console.warn('Formato de area inválido para storloc:', storloc.zoneId, area);
                // Decide se quer excluir ou incluir storlocs com 'area' inválida
                isExcluded = false; // Aqui estamos incluindo
            }
        } else if (typeof area === 'object') {
            // Se 'area' for um objeto, compara diretamente
            isExcluded = area.x === 149.296875 && area.y === -56.40625;
        }
    }

    if (isExcluded) {
        console.log('Storloc não posicionada:', storloc.zoneId);
    }

    return !isExcluded;
}));

async function fetchStorlocs() {
    if (!selectedMapYardId.value) {
        console.log('Nenhum mapa selecionado.');
        return;
    }
    console.log('Buscando storlocs para o mapa:', selectedMapYardId.value);
    try {
        const response = await axiosInstanceyard.get(`/rest/storlocPositionMap/${selectedMapYardId.value}`);
        console.log('Resposta da API:', response.data);
        const data = response.data.content || [];

        const storlocMap = new Map();
        let idCounter = 0;
        for (const s of data) {
            const position = s.area && isValidJSON(s.area) ? JSON.parse(s.area) : null;
            if (position) {
                const index = parseInt(s.index, 10) || 0;
                if (!storlocMap.has(s.ean)) {
                    storlocMap.set(s.ean, {
                        ...s,
                        position,
                        ean: s.ean,
                        uniqueId: idCounter++,
                        index,
                        color: s.storlocItem ? '#008000' : '#0000FF',
                        iconSize: s.iconSize !== undefined ? parseInt(s.iconSize, 10) : undefined,
                        iconRotation: s.iconRotation !== undefined ? parseFloat(s.iconRotation) : undefined,
                        iconSkew: s.iconSkew !== undefined ? parseFloat(s.iconSkew) : undefined,
                    });
                }
            }
        }

        placedStorlocs.value = Array.from(storlocMap.values());
        console.log('Storlocs Atualizadas (sem duplicatas):', placedStorlocs.value);
    } catch (error) {
        console.error('Erro ao buscar storlocs:', error);
        $q.notify({ message: 'Erro ao buscar storlocs.', color: 'negative' });
    }
}

function openDetails(storloc) {
    Object.assign(selectedStorloc, storloc);
    showDetailsDialog.value = true;
}

let draggedStorloc = null;
function dragStart(storloc) {
    if (storloc.storlocItem) {
        draggedStorloc = storloc;
        console.log('Iniciando arrasto da storloc:', storloc.zoneId);
    } else {
        draggedStorloc = null;
        console.log('Storloc sem veículo. Arrasto não permitido:', storloc.zoneId);
    }
}

function handleDrop(targetStorloc) {
    if (draggedStorloc && draggedStorloc !== targetStorloc) {
        console.log(`Tentando mover veículo de ${draggedStorloc.zoneId} para ${targetStorloc.zoneId}`);
        moveData.source = { ...draggedStorloc };
        moveData.destination = { ...targetStorloc };
        showMoveDialog.value = true;
        draggedStorloc = null;
    } else {
        console.log('Movimentação inválida.');
    }
}

async function handleDropOutside(event) {
    if (draggedStorloc) {
        const sourceStorloc = { ...draggedStorloc };
        draggedStorloc.storlocItem = '';
        draggedStorloc.color = '#0000FF';
        console.log(`Removendo veículo da storloc: ${sourceStorloc.zoneId}`);

        try {
            await axiosInstanceyard.put(`/rest/storloc/${sourceStorloc.ean}`, draggedStorloc);
            $q.notify({
                message: 'Veículo removido com sucesso!',
                color: 'positive',
            });
            await fetchStorlocs();
        } catch (error) {
            console.error('Erro ao remover veículo:', error);
            $q.notify({
                message: 'Erro ao remover veículo.',
                color: 'negative',
            });
            draggedStorloc.storlocItem = sourceStorloc.storlocItem;
            draggedStorloc.color = '#008000';
        }

        draggedStorloc = null;
    }
}

async function savePositions() {
    try {
        const updatePromises = placedStorlocs.value.map(async (storloc) => {
            await axiosInstance.put(`/rest/storloc/${storloc.zoneId}`, storloc);
        });

        await Promise.all(updatePromises);
        $q.notify({
            message: 'Posições e itens atualizados com sucesso!',
            color: 'positive',
        });
    } catch (error) {
        console.error('Erro ao salvar posições:', error);
        $q.notify({
            message: 'Erro ao salvar posições.',
            color: 'negative',
        });
    }
}

function toggleFontColorDialog() {
    showFontColorDialog.value = !showFontColorDialog.value;
}

function showTooltip(storloc, event) {
    tooltip.visible = true;
    tooltip.name = storloc.zoneId;
    tooltip.item = storloc.storlocItem || '';
    tooltipAnchor.value = event.currentTarget;
}

function hideTooltip() {
    tooltip.visible = false;
    tooltipAnchor.value = null;
}

const selectedMapYardId = ref(null);
const selectedMapName = ref('');

// Função para selecionar mapa
async function handleMapSelected(selectedMap) {
    console.log('Mapa selecionado:', selectedMap);

    fontSize.value = parseInt(selectedMap.fontSize, 10) || 12;
    fontColor.value = selectedMap.fontColor || '#000000';
    showFontColorDialog.value = selectedMap.showFontColorDialog || false;
    dotSize.value = parseInt(selectedMap.dotSize, 10) || 10;
    iconSize.value = parseInt(selectedMap.iconSize, 10) || 10;
    dropzoneWidth.value = parseInt(selectedMap.dropzoneWidth, 10) || 1423;
    dropzoneHeight.value = parseInt(selectedMap.dropzoneHeight, 10) || 600;
    rotation.value = parseInt(selectedMap.rotation, 10) || 0;
    backgroundImagePath.value = selectedMap.backgroundImagePath || Estacionamento;

    backgroundOpacity.value = parseInt(selectedMap.backgroundOpacity, 10) / 100 || 1;

    selectedMapYardId.value = selectedMap.mapYardId;
    selectedMapName.value = selectedMap.mapName || 'Mapa Sem Nome';

    useIcon.value = selectedMap.typeIcon === 'icon';

    if (useIcon.value) {
        iconRotation.value = parseFloat(selectedMap.iconRotation) || 0;
        iconSkew.value = parseFloat(selectedMap.iconSkew) || 0;
    } else {
        iconRotation.value = 0;
        iconSkew.value = 0;
    }

    await fetchStorlocs();

    // Salvando o mapa selecionado no Local Storage
    try {
        localStorage.setItem('selectedMap', JSON.stringify(selectedMap));
        console.log('Mapa salvo no Local Storage.');
    } catch (error) {
        console.error('Erro ao salvar mapa no Local Storage:', error);
    }
}

function handleCancel() {
    console.log('Movimentação cancelada.');
    showMoveDialog.value = false;
    isScheduling.value = false;
    scheduleTime.value = '';
    userConfirmedOverwrite.value = false;
}

async function handleConfirm() {
    console.log('Confirmar movimentação');
    if (moveData.destination.storlocItem && !userConfirmedOverwrite.value) {
        showOccupiedConfirmationDialog.value = true;
        return;
    }

    isLoading.value = true;

    const body = {
        orderTransporterId: moveData.source.storlocItem,
        originStorlocEan: moveData.source.ean,
        destinatorStorlocEan: moveData.destination.ean,
        licensePlate: moveData.source.storlocItem || '',
    };

    try {
        await axiosInstanceyard.put('/rest/eventScheduling/exe', body);
        $q.notify({
            message: 'Movimentação realizada com sucesso!',
            color: 'positive',
        });
        await fetchStorlocs();
    } catch (error) {
        console.error('Erro ao realizar movimentação:', error);
        $q.notify({
            message: 'Erro ao realizar movimentação.',
            color: 'negative',
        });
    }

    isLoading.value = false;
    userConfirmedOverwrite.value = false;
    showMoveDialog.value = false;
}

function enableScheduling() {
    isScheduling.value = true;
}

async function handleSchedule() {
    console.log('Agendar movimentação');
    if (!scheduleTime.value) {
        $q.notify({
            message: 'Por favor, selecione a data e hora para o agendamento.',
            color: 'negative',
        });
        return;
    }

    isLoading.value = true;

    const schedulingData = {
        orderTransporterId: moveData.source.storlocItem,
        yardControllerId: '',
        startStorloc: moveData.source.ean,
        destinator: moveData.destination.ean,
        scheduling: new Date(scheduleTime.value).toISOString(),
        state: 1,
        licensePlate: moveData.source.storlocItem,
        category: eventType.value,
        begin: new Date(scheduleTime.value).toISOString(),
        end: new Date(new Date(scheduleTime.value).getTime() + 3600000).toISOString(),
        time: '',
        created: new Date().toISOString(),
        modified: new Date().toISOString(),
    };
    await createEventScheduling(schedulingData);

    isLoading.value = false;
    showMoveDialog.value = false;
    isScheduling.value = false;
    scheduleTime.value = '';

    $q.notify({
        message: 'Movimentação agendada com sucesso!',
        color: 'positive',
    });
}

async function createEventScheduling(data) {
    try {
        await axiosInstanceyard.post('/rest/eventScheduling', data);
    } catch (error) {
        console.error('Erro ao criar evento:', error);
        $q.notify({
            message: 'Erro ao criar evento.',
            color: 'negative',
        });
    }
}

function cancelOverwrite() {
    showOccupiedConfirmationDialog.value = false;
}

function confirmOverwrite() {
    userConfirmedOverwrite.value = true;
    showOccupiedConfirmationDialog.value = false;
    handleConfirm();
}

// Função para carregar o mapa salvo do Local Storage ao montar o componente
onMounted(() => {
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
</script>

<style scoped>
.dropzone {
    position: relative;
    border: 2px dashed #ddd;
    background-size: 100% 100%;
    background-position: center;
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
    margin-top: -30px;
    margin-left: -20px;

    /* Mantém a proporção 1:1 para evitar distorções */
    aspect-ratio: 1 / 1;

    /* Garante que a imagem se ajuste sem distorção */
    object-fit: contain;

    /* Controla a largura com base nos atributos inline ou CSS */
    width: 100%;

    /* Define a altura automaticamente para manter a proporção */
    height: auto;

    /* Remove possíveis espaços em torno da imagem */
    display: block;

    /* Define transformações padrão */
    transform: rotate(0deg) skew(0deg);

    /* Adiciona transições suaves para transformações */
    transition: transform 0.2s ease-in-out;
}

/* Adicionalmente, você pode ajustar outros elementos conforme necessário */

/* Exemplo de outras classes permanecendo inalteradas */
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

.handle.skew-handle {
    width: 10px;
    height: 10px;
    background: #66bb6a;
    border: 1px solid #000;
    cursor: pointer;
    position: absolute;
}

/* Restante do seu CSS */
.three-container {
    width: 100%;
    height: 400px;
}
</style>
