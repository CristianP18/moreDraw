<template>
    <q-page class="page q-px-lg">
        <q-card
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
                    v-for="storloc in placedStorlocs"
                    :key="storloc.uniqueId"
                    class="draggable-item"
                    :data-id="storloc.uniqueId"
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
                        <q-item-section>
                            <q-item-label>
                                {{ truncatedZoneId(storloc.zoneId) }}
                            </q-item-label>
                            <div
                                class="label-circle"
                                :style="labelCircleStyle(storloc)"
                            />
                            <q-item-label v-if="storloc.storlocItem">
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
                        <em>Item:</em> {{ tooltip.item }}
                    </div>
                </q-tooltip>
            </div>

            <q-toolbar class="q-px-md q-py-sm">
                <!-- Botão para salvar as trocas -->
                <q-btn
                    label="Salvar"
                    color="primary"
                    class="q-mt-md"
                    @click="savePositions"
                />
                <!-- Tamanho da Fonte -->
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

                <!-- Botão para abrir o seletor de cor da fonte -->
                <q-btn
                    flat
                    icon="color_lens"
                    label="Cor da Fonte"
                    color="primary"
                    class="q-ml-sm"
                    @click="toggleFontColorDialog"
                />

                <!-- Diálogo que contém o seletor de cores para a fonte -->
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

                <!-- Tamanho da Bolinha -->
                <q-input
                    v-model.number="dotSize"
                    type="number"
                    label="Tamanho da Bolinha"
                    outlined
                    dense
                    style="width: 150px; margin-right: 20px;"
                />
                <!-- Componente para seleção de mapa -->
                <create-map-yard @map-selected="handleMapSelected" />
            </q-toolbar>
        </q-card>

        <!-- Diálogo de Edição -->
        <q-dialog v-model="showDetailsDialog">
            <q-card>
                <q-card-section>
                    <div class="text-h6">
                        Detalhes da Storloc - {{ selectedStorloc.zoneId || 'N/A' }}
                    </div>
                </q-card-section>
                <q-card-section>
                    <q-input
                        v-model="selectedStorloc.zoneId"
                        label="Zona"
                        readonly
                    />
                    <q-input
                        v-model="selectedStorloc.storlocItem"
                        label="Item da Storloc"
                        readonly
                    />
                    <q-input
                        v-model="selectedStorloc.index"
                        label="Índice"
                        readonly
                    />
                    <q-input
                        v-model="selectedStorloc.area"
                        label="Área"
                        readonly
                    />
                    <q-input
                        v-model="selectedStorloc.rack"
                        label="Rack"
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

        <!-- Diálogo de Confirmação de Movimentação -->
        <q-dialog v-model="showMoveDialog">
            <q-card>
                <q-card-section>
                    <div class="text-h6">
                        Confirmação de Movimentação
                    </div>
                </q-card-section>

                <q-card-section>
                    <div v-if="!isScheduling">
                        <p><strong>Storloc de Saída:</strong> {{ moveData.source.zoneId }}</p>
                        <p><strong>Storloc de Destino:</strong> {{ moveData.destination.zoneId }}</p>
                        <p><strong>Horário Atual:</strong> {{ currentTime }}</p>
                    </div>
                    <div v-else>
                        <q-input
                            v-model="scheduleTime"
                            label="Data e Hora de Início"
                            type="datetime-local"
                            outlined
                            dense
                        />
                    </div>
                    <q-select
                        v-model="eventType"
                        :options="eventOptions"
                        label="Tipo de Evento"
                        dense
                        outlined
                        emit-value
                        map-options
                    />
                </q-card-section>

                <q-card-actions align="right">
                    <q-btn
                        flat
                        label="Cancelar"
                        color="negative"
                        @click="handleCancel"
                    />
                    <q-btn
                        v-if="!isScheduling"
                        flat
                        label="Confirmar"
                        color="positive"
                        @click="handleConfirm"
                    />
                    <q-btn
                        v-if="!isScheduling"
                        flat
                        label="Agendar"
                        color="primary"
                        @click="enableScheduling"
                    />
                    <q-btn
                        v-if="isScheduling"
                        flat
                        label="Agendar e Confirmar"
                        color="primary"
                        :disable="!scheduleTime"
                        @click="handleSchedule"
                    />
                </q-card-actions>
            </q-card>
        </q-dialog>
    </q-page>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useQuasar } from 'quasar';
import Estacionamento from 'src/assets/imgVehicle/patio.png'; // Imagem padrão
import { createAxiosInstance } from '../../api/axiosInstance';
import CreateMapYard from './CreateMapYard.vue';

// Inicialização do Quasar
const $q = useQuasar();
const axiosInstanceyard = createAxiosInstance('yard');
const axiosInstance = createAxiosInstance('dev2');

// Estados reativos
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

// Tooltip
const tooltip = reactive({
    visible: false,
    name: '',
    item: '',
});
const tooltipAnchor = ref(null);

// Diálogo de Movimentação
const showMoveDialog = ref(false);
const moveData = reactive({
    source: null,
    destination: null,
});
const eventType = ref('');
const isScheduling = ref(false);
const scheduleTime = ref('');

// Novas opções para Tipo de Evento
const eventOptions = [
    { label: 'Tipo A', value: 'tipo_a' },
    { label: 'Tipo B', value: 'tipo_b' },
    { label: 'Tipo C', value: 'tipo_c' },
];

// Computed Properties
const currentTime = computed(() => new Date().toLocaleString());

const dropzoneStyles = computed(() => ({
    width: `${dropzoneWidth.value}px`,
    height: `${dropzoneHeight.value}px`,
    backgroundImage: `url(${backgroundImagePath.value})`,
    transform: `rotate(${rotation.value}deg)`,
    opacity: backgroundOpacity.value,
}));

// Função para verificar se uma string é um JSON válido
function isValidJSON(str) {
    try {
        JSON.parse(str);
        return true;
    } catch {
        return false;
    }
}

// Função para buscar storlocs associadas ao mapa selecionado
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

        let idCounter = 0;

        placedStorlocs.value = data
            .map((s) => {
                const position = s.area && isValidJSON(s.area) ? JSON.parse(s.area) : null;
                if (position) {
                    const index = parseInt(s.index, 10) || 0;
                    return {
                        ...s,
                        position,
                        uniqueId: idCounter++,
                        index,
                        // Define a cor com base na presença de storlocItem
                        color: s.storlocItem ? '#008000' : '#0000FF', // Verde ou Azul
                    };
                }
                return null; // Exclui storlocs sem posição
            })
            .filter((s) => s !== null); // Remove os itens nulos

        console.log('Storlocs Atualizadas:', placedStorlocs.value);
    } catch (error) {
        console.error('Erro ao buscar storlocs:', error);
        $q.notify({ message: 'Erro ao buscar storlocs.', color: 'negative' });
    }
}

// Função para abrir o diálogo de detalhes da storloc
function openDetails(storloc) {
    Object.assign(selectedStorloc, storloc);
    showDetailsDialog.value = true;
}

// Função para iniciar o drag
let draggedStorloc = null;
function dragStart(storloc) {
    if (storloc.storlocItem) { // Só permite arrastar se houver um veículo
        draggedStorloc = storloc;
        console.log('Iniciando arrasto da storloc:', storloc.zoneId);
    } else {
        draggedStorloc = null;
        console.log('Storloc sem veículo. Arrasto não permitido:', storloc.zoneId);
    }
}

// Função para lidar com o drop em uma storloc alvo
async function handleDrop(targetStorloc) {
    if (draggedStorloc && draggedStorloc !== targetStorloc && !targetStorloc.storlocItem) {
        console.log(`Tentando mover veículo de ${draggedStorloc.zoneId} para ${targetStorloc.zoneId}`);
        // Preparar os dados para movimentação
        moveData.source = { ...draggedStorloc };
        moveData.destination = { ...targetStorloc };
        showMoveDialog.value = true; // Exibe o diálogo de confirmação
        draggedStorloc = null;
    } else {
        console.log('Movimentação inválida ou storloc de destino já possui veículo.');
    }
}

// Função para lidar com o drop fora de uma storloc específica
async function handleDropOutside(event) {
    if (draggedStorloc) {
        const sourceStorloc = { ...draggedStorloc };
        // Permite soltar fora de qualquer storloc para remover o veículo
        draggedStorloc.storlocItem = '';
        draggedStorloc.color = '#0000FF'; // Azul

        console.log(`Removendo veículo da storloc: ${sourceStorloc.zoneId}`);

        try {
            // Atualizar a storloc de origem para remover o veículo
            await axiosInstanceyard.put(`/rest/storloc/${sourceStorloc.ean}`, draggedStorloc);

            $q.notify({
                message: 'Veículo removido com sucesso!',
                color: 'positive',
            });
        } catch (error) {
            console.error('Erro ao remover veículo:', error);
            $q.notify({
                message: 'Erro ao remover veículo.',
                color: 'negative',
            });

            // Reverter as alterações locais em caso de erro
            draggedStorloc.storlocItem = sourceStorloc.storlocItem;
            draggedStorloc.color = '#008000'; // Verde
        }

        draggedStorloc = null;
    }
}

// Função para salvar as posições das storlocs
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

// Funções para exibir e ocultar o diálogo de cor da fonte
function toggleFontColorDialog() {
    showFontColorDialog.value = !showFontColorDialog.value;
}

// Função para mostrar o tooltip ao passar o mouse
function showTooltip(storloc, event) {
    tooltip.visible = true;
    tooltip.name = storloc.zoneId;
    tooltip.item = storloc.storlocItem || '';
    tooltipAnchor.value = event.currentTarget; // Usar event.currentTarget para referência correta
}

// Função para esconder o tooltip ao sair do mouse
function hideTooltip() {
    tooltip.visible = false;
    tooltipAnchor.value = null;
}

// Função para truncar o Zone ID se for muito longo
function truncatedZoneId(zoneId) {
    return zoneId.length > 12 ? `${zoneId.slice(0, 12)}...` : zoneId;
}

// Função para obter o estilo da storloc
function storlocStyle(storloc) {
    return {
        transform: `translate(${storloc.position.x}px, ${storloc.position.y}px)`,
        color: fontColor.value,
        fontSize: `${fontSize.value}px`,
    };
}

// Função para obter o estilo do círculo de label
function labelCircleStyle(storloc) {
    return {
        backgroundColor: storloc.color,
        width: `${dotSize.value}px`,
        height: `${dotSize.value}px`,
    };
}

// Função para lidar com o mapa selecionado
const selectedMapYardId = ref(null);
const selectedMapName = ref('');

async function handleMapSelected(selectedMap) {
    console.log('Mapa selecionado:', selectedMap);
    // Atualiza as configurações com base no mapa selecionado

    // Valores inteiros simples
    fontSize.value = parseInt(selectedMap.fontSize, 10) || 12;
    fontColor.value = selectedMap.fontColor || '#000000';
    showFontColorDialog.value = selectedMap.showFontColorDialog || false;
    dotSize.value = parseInt(selectedMap.dotSize, 10) || 10;
    dropzoneWidth.value = parseInt(selectedMap.dropzoneWidth, 10) || 1423;
    dropzoneHeight.value = parseInt(selectedMap.dropzoneHeight, 10) || 600;
    rotation.value = parseInt(selectedMap.rotation, 10) || 0;
    backgroundImagePath.value = selectedMap.backgroundImagePath || Estacionamento;

    // Valores que precisam ser divididos por 100
    backgroundOpacity.value = parseInt(selectedMap.backgroundOpacity, 10) / 100 || 1;

    selectedMapYardId.value = selectedMap.mapYardId;
    selectedMapName.value = selectedMap.mapName || 'Mapa Sem Nome';

    console.log('ID do Mapa Selecionado:', selectedMapYardId.value);

    // Buscar storlocs associadas ao mapa selecionado
    await fetchStorlocs();
}

// Função para lidar com a confirmação da movimentação (Movimentação Imediata)
async function handleConfirm() {
    console.log('Confirmar movimentação');

    // Atualizar storlocs localmente
    moveData.destination.storlocItem = moveData.source.storlocItem;
    moveData.destination.color = '#008000'; // Verde

    moveData.source.storlocItem = '';
    moveData.source.color = '#0000FF'; // Azul

    // Atualizar as storlocs no servidor
    await updateStorlocs(moveData.source, moveData.destination);

    // Criar eventScheduling sem data e hora específica
    const schedulingData = {
        orderTransporterId: moveData.source.storlocItem, // Supondo que o ID do transporte seja o item da storloc
        yardControllerId: '', // Defina o ID do controlador atual se disponível
        startStorloc: moveData.source.zoneId,
        destinator: moveData.destination.zoneId,
        scheduling: new Date().toISOString(),
        state: 'Scheduled',
        category: eventType.value,
        begin: new Date().toISOString(),
        end: new Date(new Date().getTime() + 3600000).toISOString(), // +1 hora
        time: '', // Adicione conforme necessário
        created: new Date().toISOString(),
        modified: new Date().toISOString(),
    };
    await createEventScheduling(schedulingData);

    // Fechar o diálogo
    showMoveDialog.value = false;
}

// Função para lidar com o cancelamento da movimentação
function handleCancel() {
    console.log('Movimentação cancelada.');
    showMoveDialog.value = false;

    // Resetar o estado de agendamento se estava ativo
    isScheduling.value = false;
    scheduleTime.value = '';
}

// Função para lidar com o agendamento da movimentação
async function handleSchedule() {
    console.log('Agendar movimentação');

    if (!scheduleTime.value) {
        $q.notify({
            message: 'Por favor, selecione a data e hora para o agendamento.',
            color: 'negative',
        });
        return;
    }

    // Criar eventScheduling com data e hora específica
    const schedulingData = {
        orderTransporterId: moveData.source.storlocItem, // Supondo que o ID do transporte seja o item da storloc
        yardControllerId: '', // Defina o ID do controlador atual se disponível
        startStorloc: moveData.source.zoneId,
        destinator: moveData.destination.zoneId,
        scheduling: new Date(scheduleTime.value).toISOString(),
        state: 'Scheduled',
        category: eventType.value,
        begin: new Date(scheduleTime.value).toISOString(),
        end: new Date(new Date(scheduleTime.value).getTime() + 3600000).toISOString(), // +1 hora
        time: '', // Adicione conforme necessário
        created: new Date().toISOString(),
        modified: new Date().toISOString(),
    };
    await createEventScheduling(schedulingData);

    // Fechar o diálogo
    showMoveDialog.value = false;

    // Resetar agendamento
    isScheduling.value = false;
    scheduleTime.value = '';

    // Notificar o usuário sobre o agendamento
    $q.notify({
        message: 'Movimentação agendada com sucesso!',
        color: 'positive',
    });
}

// Função para ativar o modo de agendamento
function enableScheduling() {
    isScheduling.value = true;
}

// Função para atualizar as storlocs no servidor (Movimentação Imediata)
async function updateStorlocs(source, destination) {
    try {
        // Atualizar a storloc de destino
        await axiosInstance.put(`/rest/storloc/${destination.ean}`, destination);

        // Atualizar a storloc de origem
        await axiosInstance.put(`/rest/storloc/${source.ean}`, source);

        $q.notify({
            message: 'Veículo transferido com sucesso!',
            color: 'positive',
        });

        // Atualizar localmente
        await fetchStorlocs();
    } catch (error) {
        console.error('Erro ao atualizar storlocs:', error);
        $q.notify({
            message: 'Erro ao transferir veículo.',
            color: 'negative',
        });

        // Reverter as alterações locais em caso de erro
        source.storlocItem = moveData.source.storlocItem;
        source.color = '#008000'; // Verde
        destination.storlocItem = '';
        destination.color = '#0000FF'; // Azul
    }
}

// Função para criar um eventScheduling via API
async function createEventScheduling(data) {
    try {
        await axiosInstanceyard.post('/rest/eventScheduling', data);
        // Evitar duplicidade de notificação no agendamento
        if (isScheduling.value) {
            // Notificação já feita no handleSchedule
        } else {
            $q.notify({
                message: 'Evento agendado com sucesso!',
                color: 'positive',
            });
        }
    } catch (error) {
        console.error('Erro ao criar evento:', error);
        $q.notify({
            message: 'Erro ao criar evento.',
            color: 'negative',
        });
    }
}

onMounted(() => {
    // Inicialmente, pode buscar as storlocs de um mapa padrão

});
</script>

<style scoped>
.dropzone {
    position: relative;
    border: 2px dashed #ddd;
    background-size: 100% 100%;
    background-position: center;
    /* background-image é dinâmico */
}

.draggable-item {
    display: flex;
    align-items: center;
    cursor: move;
    position: absolute;
    user-select: none;
    /* Mantém o CSS original das bolinhas */
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
</style>
