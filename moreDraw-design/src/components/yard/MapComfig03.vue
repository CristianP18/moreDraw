<template>
    <q-page class="page q-px-lg">
        <!-- Barra de configurações -->
        <q-toolbar class="q-px-md q-py-sm">
            <!-- Botão para alternar a visibilidade dos botões de configuração 3D -->
            <q-btn
                label="Toggle 3D Config Buttons"
                icon="settings"
                flat
                color="primary"
                class="q-ml-auto"
                @click="toggleThreeConfigsVisibility"
            />
        </q-toolbar>

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
                <!-- Renderização das Storlocs -->
                <div
                    v-for="storloc in placedStorlocs"
                    :key="storloc.uniqueId"
                    class="storloc-container"
                    :style="{
                        transform: `translate(${storloc.position.x}px, ${storloc.position.y}px)`,
                        color: fontColor,
                        fontSize: fontSize + 'px'
                    }"
                >
                    <q-item
                        clickable
                        class="storloc-item"
                    >
                        <q-item-section style="position: relative;">
                            <!-- Ícone do Caminhão sempre exibido por trás -->
                            <img
                                :src="truckIcon"
                                alt="Caminhão"
                                class="camiao-icone"
                                style="left: 12%; height: 80px; transform: rotate(180deg); z-index: 1;"
                            >
                            <!-- EAN label exibido por cima do ícone do caminhão -->
                            <q-item-label
                                style="position: absolute; top: 0; left: 0; width: 100%; text-align: center; color: black; z-index: 10;"
                            >
                                {{ storloc.ean.length > 8 ? storloc.ean.slice(0, 8) + '...' : storloc.ean }}
                            </q-item-label>

                            <!-- Bolinha Azul para abrir/fechar a renderização 3D -->
                            <q-btn
                                v-tooltip.bottom="'Abrir/Fechar 3D'"
                                flat
                                icon="circle"
                                color="secondary"
                                size="xs"
                                class="three-icon-btn"
                                style="position: absolute; top: -10px; right: -10px; z-index: 3;"
                                @click.stop="toggle3DView(storloc)"
                            />

                            <!-- Renderização 3D Inline sobre o mapa -->
                            <div
                                v-if="storloc.show3D"
                                class="three-inline-container"
                                :data-id="storloc.uniqueId"
                                :style="threeInlineStyle(storloc)"
                            >
                                <div
                                    class="three-header"
                                    :class="{ 'no-background': !storloc.toolsActive }"
                                >
                                    <span v-if="storloc.toolsActive">3D View</span>
                                    <q-btn
                                        v-if="storloc.toolsActive"
                                        v-tooltip.bottom="'Configurar Ferramentas'"
                                        flat
                                        icon="settings"
                                        color="primary"
                                        size="sm"
                                        class="config-three-btn"
                                        @click="toggleTools(storloc)"
                                    />
                                    <q-btn
                                        v-if="storloc.showCloseButton"
                                        v-tooltip.bottom="'Fechar 3D'"
                                        flat
                                        icon="close"
                                        color="negative"
                                        size="sm"
                                        class="close-three-btn"
                                        @click.stop="handleCloseThree(storloc.uniqueId)"
                                    />
                                </div>
                                <ThreeContainer
                                    :model-url="storloc.modelUrl"
                                    :show-tools="storloc.toolsActive"
                                    :unique-id="storloc.uniqueId"
                                    @notification="handleNotification"
                                    @toggle-tools="handleToggleTools"
                                    @close-three="handleCloseThree"
                                    @update-position="updateStorlocPosition"
                                />
                            </div>
                        </q-item-section>
                    </q-item>
                </div>

                <!-- Tooltip -->
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

            <!-- Toolbar inferior com botões e inputs adicionais -->
            <q-toolbar class="q-px-md q-py-sm">
                <q-btn
                    label="Salvar"
                    color="primary"
                    class="q-mt-md"
                    @click="savePositions"
                />
                <!-- (outros inputs e botões) -->
            </q-toolbar>
        </q-card>

        <!-- Diálogo para detalhes da storloc -->
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
import { onMounted, ref, reactive, nextTick } from 'vue';
import interact from 'interactjs';
import Estacionamento from 'src/assets/imgVehicle/yard_10.png';
import truckIcon from 'src/assets/imgVehicle/truck02.svg';
import ThreeContainer from 'src/components/yard/ThreeContainer.vue'; // Importar o componente ThreeContainer
import { Notify } from 'quasar';
import { useModelStore } from 'src/stores/mode3D';
import { createAxiosInstance } from '../../api/axiosInstance';

// Cria uma instância do Axios
const axiosInstance = createAxiosInstance('dev2');

// Dados
const availableStorlocs = ref([]);
const placedStorlocs = ref([]);
const dropzone = ref(null);
const showDetailsDialog = ref(false);
const selectedStorloc = reactive({
    ean: '',
    type: '',
    index: '',
    zoneId: '',
    rack: '',
    area: '',
});

const fontSize = ref(18);
const fontColor = ref('#000000');
const showFontColorDialog = ref(false);
const dotSize = ref(12);
const dotColor = ref('#334ab0');
const dropzoneWidth = ref(2455);
const dropzoneHeight = ref(800);
const backgroundOpacity = ref(1);
const zoom = ref(1);
const rotation = ref(0);
const backgroundImagePath = ref(Estacionamento);
const overlayOpacity = ref(0.1);
const overlayColor = ref('#FFFFFF');
const showOverlayColorDialog = ref(false);
const truckRotationAngle = ref(0);

// Instancia a store
const modelStore = useModelStore();

// Novo estado para controlar a visibilidade dos botões de configuração nas renderizações 3D
const showThreeConfigs = ref(true);

// Tooltip configurations
const tooltip = reactive({
    visible: false,
    name: '',
    licensePlate: '',
});
const tooltipAnchor = ref(null);

// Fetch storlocs on mount
onMounted(() => {
    fetchStorlocs();
});

// Funções para toggles de diálogos
function toggleFontColorDialog() {
    showFontColorDialog.value = !showFontColorDialog.value;
}

function toggleDotColorDialog() {
    showDotColorDialog.value = !showDotColorDialog.value;
}

function toggleOverlayColorDialog() {
    showOverlayColorDialog.value = !showOverlayColorDialog.value;
}

// Função para alternar a visibilidade dos botões de configuração
function toggleThreeConfigsVisibility() {
    showThreeConfigs.value = !showThreeConfigs.value;
    // Atualiza apenas os botões dentro dos storlocs, não o botão de toggle em si
    placedStorlocs.value.forEach((storloc) => {
        if (storloc.show3D) { // Garante que estamos modificando apenas elementos que estão ativos
            storloc.toolsActive = showThreeConfigs.value;
            storloc.showCloseButton = showThreeConfigs.value;
        }
    });
    Notify.create({
        message: showThreeConfigs.value ? 'Ferramentas de Configuração Ativadas' : 'Ferramentas de Configuração Desativadas',
        color: showThreeConfigs.value ? 'positive' : 'negative',
    });
}

// Função para alternar a visualização 3D de uma storloc
function toggle3DView(storloc) {
    storloc.show3D = !storloc.show3D;
    if (storloc.show3D) {
        nextTick(() => {
            setupDraggableThreeContainer(storloc);
        });
    }
}

// Função para buscar storlocs
async function fetchStorlocs() {
    try {
        const response = await axiosInstance.get('/rest/storloc');
        const data = response.data.content.storlocs || [];

        let idCounter = 0;
        placedStorlocs.value = data.map((s) => {
            const position = s.zoneId && isValidJSON(s.zoneId) ? JSON.parse(s.zoneId) : { x: 0, y: 0 };
            return {
                ...s,
                position,
                uniqueId: idCounter++,
                modelUrl: s.modelUrl || '', // Adiciona modelUrl se existir
                show3D: false, // Estado para renderização 3D
                toolsActive: showThreeConfigs.value, // Estado para ferramentas
                showCloseButton: showThreeConfigs.value, // Estado para botão de fechar
                rotation: s.rotation || { x: 0, y: 0, z: 0 }, // Adiciona rotação, se existir
            };
        });
    } catch (error) {
        console.error('Erro ao buscar storlocs:', error);
        Notify.create({ message: 'Erro ao buscar storlocs.', color: 'negative' });
    }
}

// Função para validar JSON
function isValidJSON(str) {
    try {
        JSON.parse(str);
        return true;
    } catch {
        return false;
    }
}

// Função para abrir detalhes
function openDetails(storloc) {
    Object.assign(selectedStorloc, storloc);
    showDetailsDialog.value = true;
}

// Função para salvar posições
async function savePositions() {
    try {
        const updatePromises = placedStorlocs.value.map(async (storloc) => {
            storloc.zoneId = JSON.stringify(storloc.position);
            storloc.rack = truckRotationAngle.value;
            await axiosInstance.put(`/rest/storloc/${storloc.ean}`, storloc);
        });

        await Promise.all(updatePromises);
        Notify.create({ message: 'Posições salvas com sucesso!', color: 'positive' });
    } catch (error) {
        console.error('Erro ao salvar posições:', error);
        Notify.create({ message: 'Erro ao salvar posições.', color: 'negative' });
    }
}

// Função para atualizar a posição do ThreeContainer
function updateStorlocPosition({ uniqueId, position, rotation }) {
    const storloc = placedStorlocs.value.find((s) => s.uniqueId === uniqueId);
    if (storloc) {
        storloc.position = position;
        storloc.rotation = rotation;

        // Exibir mensagem de sucesso com os valores salvos
        Notify.create({
            message: `Posição e rotação atualizadas para Storloc ${storloc.ean}:
            Posição - x: ${position.x.toFixed(2)}, y: ${position.y.toFixed(2)}, z: ${position.z.toFixed(2)}
            Rotação - x: ${rotation.x.toFixed(2)}, y: ${rotation.y.toFixed(2)}, z: ${rotation.z.toFixed(2)}`,
            color: 'positive',
            position: 'top',
            timeout: 5000,
        });
    } else {
        Notify.create({
            message: `Storloc com uniqueId ${uniqueId} não encontrado.`,
            color: 'negative',
            position: 'top',
            timeout: 3000,
        });
    }
}

// Função para calcular o estilo de posicionamento da renderização 3D inline
function threeInlineStyle(storloc) {
    return {
        position: 'absolute',
        top: 'calc(50% - 25px)', // Centraliza verticalmente dentro do contêiner
        left: 'calc(50% + 25px)', // Centraliza horizontalmente e desloca 35px para a direita
        width: '300px',
        height: '300px',
        zIndex: '3',
        transform: 'translate(-50%, -50%) translate(20px, 0)', // Centraliza e desloca 20px para a direita
        background: 'transparent', // Fundo transparente para ver o mapa
        border: storloc.toolsActive ? '1px solid #ccc' : 'none',
        borderRadius: '8px',
        boxShadow: storloc.toolsActive ? '0 2px 10px rgba(0,0,0,0.2)' : 'none',
        cursor: 'grab', // Indica que o elemento pode ser arrastado
    };
}

// Função para mostrar o tooltip
function showTooltip(storloc, event) {
    tooltip.visible = true;
    tooltip.name = storloc.ean || '';
    tooltip.licensePlate = storloc.type || '';
    tooltipAnchor.value = event.currentTarget;
}

// Função para esconder o tooltip
function hideTooltip() {
    tooltip.visible = false;
    tooltipAnchor.value = null;
}

// Função para configurar o draggable para o ThreeContainer
function setupDraggableThreeContainer(storloc) {
    const containerSelector = `.three-inline-container[data-id="${storloc.uniqueId}"]`;
    const headerSelector = `.three-inline-container[data-id="${storloc.uniqueId}"] .three-header`;

    interact(containerSelector).draggable({
        // Define o handle como o header
        allowFrom: headerSelector,
        listeners: {
            move(event) {
                const target = event.target;

                // Mantém a posição atual
                const x = (parseFloat(target.getAttribute('data-x')) || 0) + event.dx;
                const y = (parseFloat(target.getAttribute('data-y')) || 0) + event.dy;

                // Aplica a transformação
                target.style.transform = `translate(${x}px, ${y}px) translate(-50%, -50%)`;
                target.setAttribute('data-x', x);
                target.setAttribute('data-y', y);

                // Atualiza a posição no storloc
                storloc.position.x += event.dx;
                storloc.position.y += event.dy;
            },
        },
    });
}

// Função para togglar ferramentas do ThreeContainer
function toggleTools(storloc) {
    storloc.toolsActive = !storloc.toolsActive;
    Notify.create({
        message: storloc.toolsActive ? 'Ferramentas ativadas' : 'Ferramentas desativadas',
        color: storloc.toolsActive ? 'positive' : 'negative',
    });
}

// Função para lidar com o evento 'toggle-tools' emitido pelo ThreeContainer
function handleToggleTools(event) {
    const { uniqueId } = event;
    const storloc = placedStorlocs.value.find((s) => s.uniqueId === uniqueId);
    if (storloc) {
        storloc.toolsActive = !storloc.toolsActive;
        Notify.create({
            message: storloc.toolsActive ? 'Ferramentas ativadas' : 'Ferramentas desativadas',
            color: storloc.toolsActive ? 'positive' : 'negative',
        });
    }
}

const handleCloseThree = (uniqueId) => {
    const storloc = placedStorlocs.value.find((s) => s.uniqueId === uniqueId);
    if (storloc) {
        storloc.show3D = false; // Fecha a visualização 3D
        storloc.toolsActive = false; // Desativa as ferramentas
        storloc.showCloseButton = false; // Oculta o botão de fechar
        // Notifique o usuário sobre o fechamento
        Notify.create({
            message: '3D View fechado',
            color: 'info',
            position: 'top',
            timeout: 3000,
        });
    }
};

// Função para lidar com notificações (opcional, caso queira centralizar)
function handleNotification(notification) {
    Notify.create(notification);
}
</script>

<style scoped>
.dropzone {
    position: relative;
    border: 2px dashed #ddd;
    background-size: contain;
    background-position: center;
    background-size: 100% 100%;
}

.storloc-container {
    position: absolute;
    display: flex;
    align-items: center;
}

.storloc-item {
    position: relative;
}

.camiao-icone {
    position: absolute;
    width: 32px;
    height: 32px;
    top: -5px;
    right: -5px;
    z-index: 1; /* Z-index ajustado para ficar atrás do texto */
}

.three-icon-btn {
    /* Estilização adicional para o botão 3D */
    background-color: rgba(255, 255, 255, 0.8);
    border-radius: 50%;
    padding: 2px;
}

.three-inline-container {
    /* Estilização da renderização 3D inline */
    /* O estilo é definido dinamicamente via função threeInlineStyle(storloc) */
}

.three-header {
    width: 100%;
    height: 30px;
    background-color: rgba(0, 0, 0, 0.1);
    cursor: grab; /* Indica que o elemento pode ser arrastado */
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 5px;
    border-top-left-radius: 8px;
    border-top-right-radius: 8px;
    transition: background-color 0.3s ease;
}

.three-header.no-background {
    background-color: transparent;
}

.close-three-btn {
    /* Estilização do botão de fechar na renderização 3D */
    background-color: rgba(255, 255, 255, 0.7);
    border-radius: 50%;
    cursor: pointer; /* Alterado para pointer */
}

.config-three-btn {
    /* Estilização do botão de configuração na renderização 3D */
    background-color: rgba(255, 255, 255, 0.8);
    border-radius: 50%;
    cursor: pointer;
    margin-right: 5px;
}

.tooltip-content {
    /* Estilização adicional para o tooltip, se necessário */
}

.dropzone-card {
    /* Estilização do card que contém a dropzone */
    position: relative;
}

.q-toolbar-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
