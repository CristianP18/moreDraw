<template>
    <q-page class="page q-px-lg">
        <q-card class="q-pa-md dropzone-card">
            <!-- Barra de Título com Botão de Configuração -->
            <q-toolbar>
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
            </q-toolbar>

            <!-- Barra Lateral de Entregas -->
            <q-drawer
                v-model="rightDrawerOpen"
                show-if-above
                side="right"
                width="350px"
            >
                <q-list class="drawer-list">
                    <q-item-label class="text-bold">
                        Entregas
                    </q-item-label>
                    <q-item
                        v-for="delivery in deliveries"
                        :key="delivery.deliveryId"
                        clickable
                        style="width: 250px"
                        @click="selectDelivery(delivery)"
                    >
                        <q-item-section>
                            {{ extractFirstPart(delivery.origin) }}/{{
                                extractFirstPart(delivery.destination)
                            }}
                            => {{ delivery.count }} de {{ delivery.total }}
                        </q-item-section>
                    </q-item>
                </q-list>
            </q-drawer>

            <!-- Área de Drop com Mapa como Background -->
            <div
                ref="dropzone"
                class="dropzone"
                :style="dropzoneStyle"
                @dragover.prevent
                @drop="handleDropInDropzone($event)"
            >
                <!-- Docks já Posicionadas -->
                <div
                    v-for="dock in placedDocks"
                    :key="dock.uniqueId"
                    class="draggable-item"
                    :style="dockStyle(dock)"
                    draggable="true"
                    @dragstart="dragStartDock(dock, $event)"
                    @dragover.prevent
                    @drop="handleDropOnDock($event, dock)"
                >
                    <q-item
                        clickable
                        class="dock-item"
                    >
                        <q-item-section>
                            {{ extractFirstPart(dock.dockName) }}
                            <div
                                class="label-circle"
                                :style="{
                                    backgroundColor: getColorByType(dock),
                                    width: dotSize + 'px',
                                    height: dotSize + 'px',
                                }"
                            />
                            <div v-if="dock.loadingTrailer">
                                Carregando: {{ dock.loadingTrailer }}
                            </div>
                            <div v-if="dock.loadedTrailer">
                                Carregado: {{ dock.loadedTrailer }}
                            </div>
                        </q-item-section>
                    </q-item>
                </div>

                <!-- Motoristas Representados como Bolinhas Marrons ou Lilás -->
                <div
                    v-for="driver in drivers"
                    :key="driver.driverId"
                    class="driver-circle"
                    :style="driverStyle(driver)"
                    draggable="true"
                    @dragstart="dragStartDriver(driver, $event)"
                >
                    {{ driver.name }}
                </div>
            </div>

            <!-- Barra de Ferramentas Inferior -->
            <q-toolbar class="q-px-md q-py-sm">
                <q-btn
                    label="Salvar"
                    color="primary"
                    class="q-mt-md"
                    @click="savePositions"
                />

                <q-btn
                    label="Resetar Tabela"
                    color="negative"
                    class="q-mt-md q-ml-sm"
                    @click="resetRoutePlanner"
                />

                <!-- Input para Horário de Início (atualizado para mostrar o currentClockTime) -->
                <q-input
                    v-model="startTime"
                    type="time"
                    label="Horário Atual"
                    outlined
                    dense
                    color="primary"
                    class="q-ml-sm"
                    style="width: 200px; margin-right: 20px"
                    readonly
                />

                <q-input
                    v-model.number="fontSize"
                    type="number"
                    label="Tamanho da Fonte"
                    outlined
                    dense
                    color="primary"
                    class="q-ml-sm"
                    style="width: 150px; margin-right: 20px; margin-left: 20px"
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
                    style="width: 150px; margin-right: 20px"
                />
            </q-toolbar>
        </q-card>

        <!-- Tabela de Planejamento de Rotas -->
        <RoutePlannerTable
            :route-planner="routePlanner"
            @update-route-planner="updateRoutePlanner"
        />

        <!-- Diálogo de Alerta para Sem Motoristas Disponíveis -->
        <q-dialog v-model="showNoDriverDialog">
            <q-card>
                <q-card-section>
                    <div class="text-h6">
                        Atenção
                    </div>
                    <div>Não há motoristas disponíveis para mover o trailer.</div>
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn
                        flat
                        label="Fechar"
                        @click="showNoDriverDialog = false"
                    />
                </q-card-actions>
            </q-card>
        </q-dialog>
    </q-page>
</template>

<script setup>
import {
    ref,
    reactive,
    nextTick,
    onMounted,
    computed,
    watch,
} from 'vue';
import axios from 'axios';
import backgroundImage from 'src/assets/imgVehicle/patio.png';
import RoutePlannerTable from './RoutePlannerTable.vue';

// Caminho da imagem de fundo

// Variáveis de estado para motoristas, entregas e tempos
const drivers = ref([]);
const deliveries = ref([]);
const timeForDestinators = ref([]);

// Controle das barras laterais
const leftDrawerOpen = ref(true);
const rightDrawerOpen = ref(true);

// Estado das docks
const placedDocks = ref([]);
const showDetailsDialog = ref(false);
const selectedDock = reactive({
    name: '',
    location: '',
    loadingTrailer: '',
});

// Estado da fonte e bolinhas
const fontSize = ref(12);
const fontColor = ref('#000000');
const showFontColorDialog = ref(false);
const dotSize = ref(10);

// Dimensões e transformações do dropzone
const dropzoneWidth = ref(1423);
const dropzoneHeight = ref(600);
const zoom = ref(1);
const rotation = ref(0);
const backgroundImagePath = ref(backgroundImage);

// Estado para drag & drop dos motoristas e docks
let draggedDriver = null;
let draggedDock = null;

// Estado do planejamento de rotas
const routePlanner = ref([]);

// Seleção de Docas
const selectedOrigin = ref(null);
const selectedDestination = ref(null);

// Opções de Docas para seleção
const dockOptions = computed(() => deliveries.value.map((delivery) => ({
    label: `${extractFirstPart(delivery.origin)} -> ${extractFirstPart(
        delivery.destination,
    )}`,
    value: { origin: delivery.origin, destination: delivery.destination },
})));

// Computed para definir o estilo do dropzone com a imagem de fundo
const dropzoneStyle = computed(() => ({
    width: `${dropzoneWidth.value}px`,
    height: `${dropzoneHeight.value}px`,
    backgroundImage: `url(${backgroundImagePath.value})`,
    backgroundSize: 'contain', // Mantém a proporção da imagem
    backgroundPosition: 'center',
    transform: `scale(${zoom.value}) rotate(${rotation.value}deg)`,
    paddingLeft: '220px', // Espaço para a seção de motoristas
    paddingRight:
    leftDrawerOpen.value || rightDrawerOpen.value
        ? '450px'
        : '0px' /* Garante que o conteúdo principal respeite os drawers à direita */,
    position: 'relative', // Para posicionamento interno
    zIndex: 1, // z-index mais baixo
}));

// Estado para diálogo de alerta
const showNoDriverDialog = ref(false);

// Estado para Horário de Início
const startTime = ref('08:00');

// Variável reativa para o relógio atual
const currentClockTime = ref(parseTime(startTime.value));

// Observa mudanças no currentClockTime e atualiza startTime
watch(currentClockTime, (newVal) => {
    startTime.value = formatTime(newVal);
});

// Contador para ordens de transporte concluídas
const transportOrderCount = ref(0);

// Funções de seleção
function selectDriver(driver) {
    console.log('Motorista selecionado:', driver);
}

function selectDelivery(delivery) {
    console.log('Entrega selecionada:', delivery);
}

// Função para extrair a primeira parte da string antes da primeira '/'
function extractFirstPart(str) {
    if (!str) return '';
    const parts = str.split('/');
    return parts[0].trim().toUpperCase(); // Padroniza para maiúsculas
}

// Validação JSON
function isValidJSON(str) {
    if (!str) return false;
    try {
        JSON.parse(str);
        return true;
    } catch {
        return false;
    }
}

// Função para buscar docks
async function fetchDocks() {
    try {
        const response = await axios.get('http://localhost:8082/api/docks');
        await nextTick();

        placedDocks.value = response.data.map((dock, index) => {
            let position = { x: 52, y: 350 }; // Posição padrão
            if (dock.location && isValidJSON(dock.location)) {
                const parsedLocation = JSON.parse(dock.location);
                position = {
                    x: typeof parsedLocation.x === 'number' ? parsedLocation.x : 52,
                    y: typeof parsedLocation.y === 'number' ? parsedLocation.y : 350,
                }; // Use coordenadas dinâmicas ou padrão
            }
            return { ...dock, position, uniqueId: index };
        });

        console.log('Docks posicionadas:', placedDocks.value);
    } catch (error) {
        console.error('Erro ao buscar docks:', error);
    }
}

// Função para buscar motoristas
async function fetchDrivers() {
    try {
    // Certifique-se de que as docks foram carregadas
        await nextTick();

        const response = await axios.get('http://localhost:8082/api/drivers');
        drivers.value = response.data.map((driver, index) => {
            // Se o motorista estiver atribuído a uma dock, posiciona-o sobre essa dock
            if (driver.assignedDockId) {
                const dock = placedDocks.value.find(
                    (d) => d.dockId === driver.assignedDockId,
                );
                if (dock) {
                    return {
                        ...driver,
                        position: {
                            x: dock.position.x + 50,
                            y: dock.position.y + 50,
                        },
                        status: driver.status || 'ativo',
                        driverAvailableTime: driver.driverAvailableTime
                            ? new Date(driver.driverAvailableTime)
                            : null,
                    };
                }
            }
            // Posição padrão se não estiver atribuído a nenhuma dock
            return {
                ...driver,
                position: {
                    x: 50 + index * 50,
                    y: 50,
                }, // Posiciona os motoristas não atribuídos em fila
                status: driver.status || 'ativo',
                driverAvailableTime: driver.driverAvailableTime
                    ? new Date(driver.driverAvailableTime)
                    : null,
            };
        });
        console.log('Motoristas carregados:', drivers.value);
    } catch (error) {
        console.error('Erro ao buscar motoristas:', error);
    }
}

// Função para buscar TimeForDeliveries
async function fetchTimeForDestinators() {
    try {
        const response = await axios.get(
            'http://localhost:8082/api/timefordeliveries',
        );
        timeForDestinators.value = response.data.map((time) => ({
            ...time,
            origin: time.origin.toUpperCase(),
            destination: time.destination.toUpperCase(),
        }));
        console.log('TimeForDestinators carregados:', timeForDestinators.value);
    } catch (error) {
        console.error('Erro ao buscar TimeForDestinators:', error);
    }
}

// Função para buscar entregas e garantir que todas as combinações existam
async function fetchDeliveries() {
    try {
        const response = await axios.get('http://localhost:8082/api/deliveries');
        deliveries.value = response.data.map((delivery) => ({
            ...delivery,
            count: delivery.count || 0, // Inicializa count para 0 se não existir
            total: delivery.total || 100, // Define um valor total padrão se necessário
        }));

        // Garantir que todas as combinações de timeForDeliveries existam em deliveries
        timeForDestinators.value.forEach((time) => {
            const exists = deliveries.value.some(
                (delivery) => extractFirstPart(delivery.origin) === time.origin
          && extractFirstPart(delivery.destination) === time.destination,
            );
            if (!exists) {
                deliveries.value.push({
                    deliveryId: `auto-${time.origin}-${time.destination}`, // Geração de ID única
                    origin: time.origin,
                    destination: time.destination,
                    count: 0, // Inicializa com 0
                    total: 100, // Pode ajustar conforme necessário
                });
                console.log(
                    `Adicionada nova entrega: Origin=${time.origin}, Destination=${time.destination}`,
                );
            }
        });

        console.log('Entregas carregadas e combinadas:', deliveries.value);
    } catch (error) {
        console.error('Erro ao buscar entregas:', error);
    }
}

// Inicialização
onMounted(async () => {
    await fetchDocks();
    await fetchDrivers(); // Carregar motoristas após as docks
    await fetchTimeForDestinators(); // Carregar tempos primeiro
    await fetchDeliveries(); // Carregar entregas depois
    initializeRoutePlanner(); // Inicializar o Route Planner
    processTransportOrders(); // Processar ordens pendentes
});

// Função para determinar a cor com base no estado da dock
function getColorByType(dock) {
    if (dock.loadedTrailer) {
        return '#FFD700'; // Dourado se tiver trailer carregado
    } if (dock.loadingTrailer) {
        return '#008000'; // Verde se tiver trailer carregando
    } if (dock.inTransit) {
        return '#FFD700'; // Dourado para trailers em trânsito (amarelo)
    }
    return '#0000FF'; // Azul se não tiver trailer
}

// Funções de drag & drop dos motoristas
function dragStartDriver(driver, event) {
    if (driver.status !== 'ativo') {
        event.preventDefault();
        return;
    }
    draggedDriver = driver;
    event.dataTransfer.setData('driverId', driver.driverId);
    event.dataTransfer.effectAllowed = 'move';
    console.log('Motorista arrastado:', driver);
}

// Funções de drag & drop das docks
function dragStartDock(dock, event) {
    draggedDock = dock;
    event.dataTransfer.setData('dockId', dock.dockId);
    event.dataTransfer.effectAllowed = 'move';
    console.log('Dock arrastada:', dock);
}

// Função para lidar com o drop em uma dock
async function handleDropOnDock(event, targetDock) {
    event.preventDefault();

    const driverId = event.dataTransfer.getData('driverId');
    const dockId = event.dataTransfer.getData('dockId');

    if (driverId) {
    // Se for um motorista
        const driver = drivers.value.find((d) => d.driverId == driverId);
        if (driver) {
            await assignDriverToDock(driver, targetDock);
        }
    } else if (dockId) {
    // Se for um trailer (dock)
        const sourceDock = placedDocks.value.find((d) => d.dockId == dockId);
        if (sourceDock) {
            await moveTrailerToDock(sourceDock, targetDock);
        }
    }

    draggedDriver = null;
    draggedDock = null;
}

// Função para lidar com o drop na área geral (caso precise)
function handleDropInDropzone(event) {
    event.preventDefault();
    // Implementação adicional, se necessário
}

// Função para atribuir um motorista a uma dock
async function assignDriverToDock(driver, dock) {
    // Verifica se a dock já tem um motorista
    if (dock.driverId) {
        alert('A dock já possui um motorista.');
        return;
    }

    // Atualiza a dock anterior do motorista, se houver
    if (driver.assignedDockId) {
        const previousDock = placedDocks.value.find(
            (d) => d.dockId === driver.assignedDockId,
        );
        if (previousDock) {
            previousDock.driverId = null;
            await updateDock(previousDock);
        }
    }

    // Atribui o motorista à nova dock
    driver.assignedDockId = dock.dockId;
    dock.driverId = driver.driverId;

    // Atualiza a posição do motorista
    driver.position = {
        x: dock.position.x + 50,
        y: dock.position.y + 50,
    };

    // Atualiza o status do motorista para 'ativo' (ele ainda não recebeu uma ordem)
    driver.status = 'ativo';

    // Atualiza no backend (opcional)
    await updateDriver(driver);
    await updateDock(dock);

    console.log(
        `Motorista ${driver.name} atribuído à dock ${extractFirstPart(
            dock.dockName,
        )}.`,
    );
}

// Função para mover um trailer para outra dock
async function moveTrailerToDock(sourceDock, targetDock) {
    // Verifica se a dock de origem tem um trailer em loadedTrailer ou loadingTrailer
    if (!sourceDock.loadedTrailer && !sourceDock.loadingTrailer) {
        alert('A dock de origem não possui um trailer para mover.');
        return;
    }

    // Verifica se a dock de origem tem um motorista
    if (!sourceDock.driverId) {
    // Exibe mensagem de alerta
        showNoDriverDialog.value = true;
        return;
    }

    // Verifica se a dock de destino já tem um trailer em loadingTrailer
    if (targetDock.loadingTrailer) {
    // Se já tiver um loadingTrailer, move para loadedTrailer
        if (targetDock.loadedTrailer) {
            alert('A dock de destino já possui trailers em loading e loaded.');
            return;
        }
        targetDock.loadedTrailer = targetDock.loadingTrailer;
        targetDock.loadingTrailer = null;
    }

    // Move o trailer
    let trailerLicensePlate = 'xxx'; // Valor padrão
    if (sourceDock.loadedTrailer) {
        trailerLicensePlate = sourceDock.loadedTrailer;
        targetDock.loadingTrailer = sourceDock.loadedTrailer;
        sourceDock.loadedTrailer = null;
    } else if (sourceDock.loadingTrailer) {
        trailerLicensePlate = sourceDock.loadingTrailer;
        targetDock.loadingTrailer = sourceDock.loadingTrailer;
        sourceDock.loadingTrailer = null;
    }

    // Move o motorista junto
    const driver = drivers.value.find((d) => d.driverId === sourceDock.driverId);
    if (driver) {
        driver.assignedDockId = targetDock.dockId;
        sourceDock.driverId = null;
        targetDock.driverId = driver.driverId;

        // Atualiza a posição do motorista
        driver.position = {
            x: targetDock.position.x + 50,
            y: targetDock.position.y + 50,
        };

        // Atualiza no backend (opcional)
        await updateDriver(driver);
    }

    // Atualiza as docks no backend (opcional)
    await updateDock(sourceDock);
    await updateDock(targetDock);

    console.log(
        `Trailer movido de ${extractFirstPart(
            sourceDock.dockName,
        )} para ${extractFirstPart(
            targetDock.dockName,
        )}. O motorista foi movido junto.`,
    );

    // Gerar a ordem de transporte, incluindo 'cargoType'
    await addTransportOrder({
        dockStart: sourceDock.dockName,
        dockDestination: targetDock.dockName,
        driverName: getDriverName(driver.driverId),
        trailerLicensePlate: trailerLicensePlate || 'xxx',
        cargoType: 'Peça', // Adicionado para garantir o preenchimento
        driverId: driver.driverId, // Passar o ID do motorista
    });
}

// Função para atualizar a dock no backend (opcional)
async function updateDock(dock) {
    try {
        await axios.put(`http://localhost:8082/api/docks/${dock.dockId}`, dock);
        console.log('Dock atualizada com sucesso:', dock);
    } catch (error) {
        console.error('Erro ao atualizar a dock:', error);
    }
}

// Função para atualizar o motorista no backend (opcional)
async function updateDriver(driver) {
    try {
        await axios.put(
            `http://localhost:8082/api/drivers/${driver.driverId}`,
            driver,
        );
        console.log('Motorista atualizado com sucesso:', driver);
    } catch (error) {
        console.error('Erro ao atualizar o motorista:', error);
    }
}

// Função para salvar as posições e atribuições das docks (opcional)
async function savePositions() {
    try {
    // Atualiza as docks
        const dockPromises = placedDocks.value.map((dock) => axios.put(`http://localhost:8082/api/docks/${dock.dockId}`, dock));
        // Atualiza os motoristas
        const driverPromises = drivers.value.map((driver) => axios.put(`http://localhost:8082/api/drivers/${driver.driverId}`, driver));
        // Atualiza as ordens de transporte
        const orderPromises = routePlanner.value.map((order) => axios.post('http://localhost:8082/api/transportOrders', order)); // Supondo que exista um endpoint para ordens de transporte
        await Promise.all([...dockPromises, ...driverPromises, ...orderPromises]);
        alert('Posições salvas com sucesso!');
    } catch (error) {
        console.error('Erro ao salvar posições:', error);
    }
}

// Função para alternar o diálogo de cor da fonte
function toggleFontColorDialog() {
    showFontColorDialog.value = !showFontColorDialog.value;
}

// Função para inicializar o Route Planner
function initializeRoutePlanner() {
    const storedRoutePlanner = localStorage.getItem('routePlanner');
    if (storedRoutePlanner) {
        try {
            const parsed = JSON.parse(storedRoutePlanner);
            routePlanner.value = Array.isArray(parsed) ? parsed : [];
            // Inicializar o contador baseado nas ordens existentes
            transportOrderCount.value = routePlanner.value.length;
        } catch (e) {
            console.error('Erro ao analisar routePlanner do LocalStorage:', e);
            routePlanner.value = [];
            transportOrderCount.value = 0;
        }
    } else {
        routePlanner.value = [];
        transportOrderCount.value = 0;
    }
    console.log('Route Planner Initialized:', routePlanner.value);
}

// Função para adicionar uma ordem de transporte
async function addTransportOrder({
    dockStart,
    dockDestination,
    driverName,
    trailerLicensePlate,
    cargoType,
    driverId, // Recebe o ID do motorista
}) {
    // Verificar se dockStart e dockDestination estão selecionados (para novas ordens)
    if (!dockStart || !dockDestination) {
        alert('Por favor, selecione Doca de Origem e Doca de Destino.');
        return;
    }

    // Extrair partes relevantes das docas e padronizar para maiúsculas
    const dockStartPart = extractFirstPart(dockStart);
    const dockDestinationPart = extractFirstPart(dockDestination);

    // Encontrar a entrega correspondente (independente da ordem)
    let matchingDelivery = deliveries.value.find(
        (delivery) => (extractFirstPart(delivery.origin) === dockStartPart
        && extractFirstPart(delivery.destination) === dockDestinationPart)
      || (extractFirstPart(delivery.origin) === dockDestinationPart
        && extractFirstPart(delivery.destination) === dockStartPart),
    );

    // Se a entrega não existir, adicionar uma nova
    if (!matchingDelivery) {
    // Verificar se a combinação existe em timeForDestinators
        const timeForDelivery = timeForDestinators.value.find(
            (time) => (time.origin === dockStartPart
          && time.destination === dockDestinationPart)
        || (time.origin === dockDestinationPart
          && time.destination === dockStartPart),
        );

        if (timeForDelivery) {
            // Adicionar a nova entrega com count=0
            const newDelivery = {
                deliveryId: `auto-${timeForDelivery.origin}-${timeForDelivery.destination}`,
                origin: timeForDelivery.origin,
                destination: timeForDelivery.destination,
                count: 0,
                total: 100, // Ajuste conforme necessário
            };
            deliveries.value.push(newDelivery);
            console.log(
                `Adicionada nova entrega: Origin=${newDelivery.origin}, Destination=${newDelivery.destination}`,
            );
            matchingDelivery = newDelivery;
        } else {
            // Se não existir em timeForDestinators, não prosseguir
            alert(
                `Tempo de entrega não encontrado para a rota entre Doca de Origem (${dockStartPart}) e Doca de Destino (${dockDestinationPart}).`,
            );
            return;
        }
    }

    // Encontrar o tempo de entrega correspondente (independente da ordem)
    const timeForDelivery = timeForDestinators.value.find(
        (time) => (time.origin === dockStartPart
        && time.destination === dockDestinationPart)
      || (time.origin === dockDestinationPart
        && time.destination === dockStartPart),
    );

    if (!timeForDelivery) {
        alert(
            `Tempo de entrega não encontrado para a rota entre Doca de Origem (${dockStartPart}) e Doca de Destino (${dockDestinationPart}).`,
        );
        return;
    }

    // Obter o motorista (usando o driverId passado)
    const assignedDriver = drivers.value.find((d) => d.driverId === driverId);

    // Verificar se há motoristas disponíveis
    const availableDrivers = drivers.value.filter((d) => d.status === 'ativo');

    // Se não houver motoristas disponíveis, avançar o currentClockTime
    if (availableDrivers.length === 0) {
        const nextAvailableTime = getNextAvailableDriverTime();
        currentClockTime.value = nextAvailableTime;
        console.log(
            `Nenhum motorista disponível. Avançando currentClockTime para: ${formatTime(
                currentClockTime.value,
            )}`,
        );
    }

    // Se o driverAvailableTime do motorista for maior que o currentClockTime, atualiza o currentClockTime
    if (
        assignedDriver.driverAvailableTime
    && assignedDriver.driverAvailableTime > currentClockTime.value
    ) {
        currentClockTime.value = assignedDriver.driverAvailableTime;
    }

    // Calcular o horário de partida
    const departureDate = new Date(currentClockTime.value);
    const departureTime = formatTime(departureDate);

    // Criação da nova ordem
    const newOrder = {
        orderNumber: `ordem ${transportOrderCount.value + 1}`,
        departureTime, // Hora calculada
        driverName: assignedDriver.name,
        vehicleLicensePlate: 'xxx', // Valor padrão ou ajuste conforme necessário
        cavalinho: 'xxx', // Valor padrão ou ajuste conforme necessário
        trailerLicensePlate: trailerLicensePlate || 'xxx',
        dockStart,
        dockDestination,
        cargoType: cargoType || 'Peça', // Garantir que cargoType está definido
        timeInMinutes: timeForDelivery.timeInMinutes, // Uso correto do atributo
        status: 'em trânsito', // Status inicial
        expectedCompletionTime: new Date(
            departureDate.getTime()
        + (timeForDelivery.timeInMinutes + 80) * 60000,
        ), // Hora esperada de conclusão
    };

    console.log('Criando nova ordem de transporte:', newOrder); // Log para depuração

    // Atualizar driverAvailableTime do motorista
    const totalTime = timeForDelivery.timeInMinutes + 80; // Tempo de transporte + 80 minutos de descarga
    assignedDriver.driverAvailableTime = new Date(
        departureDate.getTime() + totalTime * 60000,
    );
    assignedDriver.status = 'ocupado';

    // Atualiza o motorista no backend (opcional)
    await updateDriver(assignedDriver);

    // Incrementar o contador de ordens de transporte
    transportOrderCount.value += 1;

    // Adiciona a ordem ao routePlanner
    routePlanner.value.push(newOrder);

    // Salva no LocalStorage
    localStorage.setItem('routePlanner', JSON.stringify(routePlanner.value));

    // Incrementa o 'count' na entrega correspondente no frontend
    incrementDeliveryCount(dockStart, dockDestination);

    // Processar ordens de transporte após adicionar nova ordem
    processTransportOrders();
}

// Função para encontrar o próximo driverAvailableTime mais cedo
function getNextAvailableDriverTime() {
    const times = drivers.value
        .filter((driver) => driver.driverAvailableTime)
        .map((driver) => driver.driverAvailableTime.getTime());
    if (times.length > 0) {
        return new Date(Math.min(...times));
    }
    return currentClockTime.value;
}

// Função para formatar hora para string "HH:MM"
function formatTime(date) {
    return date.toLocaleTimeString('en-GB', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false,
    });
}

// Função para parsear hora em formato "HH:MM" para objeto Date
function parseTime(timeStr) {
    const [hours, minutes] = timeStr.split(':').map(Number);
    const now = new Date();
    now.setHours(hours, minutes, 0, 0);
    return now;
}

// Função para resetar o Route Planner
function resetRoutePlanner() {
    if (confirm('Tem certeza que deseja resetar todas as ordens de transporte?')) {
        routePlanner.value = [];
        transportOrderCount.value = 0;
        localStorage.removeItem('routePlanner');
        alert('Ordem de transporte resetada com sucesso!');
    }
}

// Função para atualizar o Route Planner a partir da tabela
function updateRoutePlanner(updatedRoutePlanner) {
    routePlanner.value = updatedRoutePlanner;
    // Atualizar o contador de ordens concluídas
    transportOrderCount.value = routePlanner.value.length;
    localStorage.setItem('routePlanner', JSON.stringify(routePlanner.value));
    console.log('Route Planner Updated from Table:', routePlanner.value);
}

// Função para obter o nome do motorista pelo ID
function getDriverName(driverId) {
    const driver = drivers.value.find((d) => d.driverId === driverId);
    return driver ? driver.name : 'N/A';
}

// Função para estilizar as docks dinamicamente
function dockStyle(dock) {
    return {
        transform: `translate(${dock.position.x}px, ${dock.position.y}px)`,
        color: fontColor.value,
        fontSize: `${fontSize.value}px`,
        zIndex: 5,
    };
}

// Função para estilizar os motoristas dinamicamente
function driverStyle(driver) {
    return {
        transform: `translate(${driver.position.x}px, ${driver.position.y}px)`,
        backgroundColor: driver.status === 'ativo' ? '#8B4513' : '#DA70D6', // Marrom ou Lilás
        width: `${dotSize.value + 10}px`,
        height: `${dotSize.value + 10}px`,
        borderRadius: '50%',
        cursor: driver.status === 'ativo' ? 'grab' : 'not-allowed',
        zIndex: 10,
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        color: 'white',
        fontSize: '10px',
    };
}

// Função para incrementar o 'count' na entrega correspondente no frontend
function incrementDeliveryCount(origin, destination) {
    // Extrair partes relevantes das docas e padronizar para maiúsculas
    const originPart = extractFirstPart(origin);
    const destinationPart = extractFirstPart(destination);

    // Encontrar a entrega correspondente (independente da ordem)
    const matchingDelivery = deliveries.value.find(
        (delivery) => (extractFirstPart(delivery.origin) === originPart
        && extractFirstPart(delivery.destination) === destinationPart)
      || (extractFirstPart(delivery.origin) === destinationPart
        && extractFirstPart(delivery.destination) === originPart),
    );

    if (matchingDelivery) {
    // Incrementar o count
        matchingDelivery.count += 1;
        console.log(
            `Count incrementado para a entrega ${matchingDelivery.deliveryId}:`,
            matchingDelivery,
        );
    } else {
        console.warn(
            `Entrega não encontrada para origem: ${originPart}, destino: ${destinationPart}`,
        );
    }
}

// Função para processar ordens de transporte
async function processTransportOrders() {
    const currentTime = currentClockTime.value;

    for (const order of routePlanner.value) {
        const expectedCompletionTime = order.expectedCompletionTime;

        if (currentTime >= expectedCompletionTime && order.status !== 'concluída') {
            order.status = 'concluída';
            // Atualizar a dock para refletir que o trailer foi descarregado
            const targetDock = placedDocks.value.find(
                (dock) => extractFirstPart(dock.dockName)
          === extractFirstPart(order.dockDestination),
            );
            if (targetDock) {
                targetDock.loadedTrailer = order.trailerLicensePlate;
                targetDock.loadingTrailer = null;
                await updateDock(targetDock);
            }
            // Atualizar o status do motorista para 'ativo'
            const driver = drivers.value.find((d) => d.name === order.driverName);
            if (driver) {
                driver.status = 'ativo';
                driver.driverAvailableTime = currentTime; // O motorista está disponível agora
                await updateDriver(driver);
            }
            console.log(`Ordem ${order.orderNumber} concluída.`);
        }
    }
}
</script>

<style scoped>
.dropzone {
  position: relative;
  border: 2px dashed #ddd;
  background-size: contain;
  background-position: center;
  transform-origin: top left;
  z-index: 1; /* z-index mais baixo */
}

.draggable-item {
  display: flex;
  align-items: center;
  cursor: grab;
  position: absolute;
  user-select: none;
  z-index: 5; /* z-index intermediário para as docks */
}

.draggable-item:active {
  cursor: grabbing;
}

.label-circle {
  border-radius: 50%;
  display: inline-block;
  margin-left: 10px;
}

.dock-item {
  display: flex;
  align-items: center;
}

.q-item-label {
  color: #000; /* Ajuste da cor do texto para visibilidade */
  font-weight: bold;
  margin-left: 10px; /* Espaçamento entre a bolinha e o texto */
}

/* Estilos para os Drawers */
.drawer-list {
  padding: 10px;
}

/* Ajustes de z-index para os Drawers */
.q-drawer {
  z-index: 100; /* z-index alto para os Drawers */
}

/* Classe adicional para garantir que os componentes da tabela fiquem acima das docks, se necessário */
.route-planner-table {
  position: relative;
  z-index: 10; /* z-index mais alto para a tabela */
}

/* Estilos para os Motoristas */
.driver-circle {
  position: absolute;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 10px;
  user-select: none;
}

.driver-circle:active {
  cursor: grabbing;
}

.driver-name {
  pointer-events: none; /* Evita que o texto interfira no drag */
}

/* Estilos para Informação do Motorista na Dock */
.driver-info {
  display: flex;
  align-items: center;
  margin-top: 5px;
}

.driver-info .driver-name {
  margin-left: 5px;
  font-size: 12px;
}

/* Ajustes Responsivos */
@media (max-width: 768px) {
  .dropzone {
    width: 100%;
    height: 400px;
  }
}

/* Barra de Seleção de Doca */
.dock-selection-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.dock-origin,
.dock-destination {
  width: 45%;
}
</style>
