<!-- YardController.vue -->
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
        show-if-above
        v-model="rightDrawerOpen"
        side="right"
        width="350px"
      >
        <q-list class="drawer-list">
          <q-item-label class="text-bold">Entregas</q-item-label>
          <q-item
            v-for="delivery in deliveries"
            :key="delivery.deliveryId"
            clickable
            style="width: 250px"
            @click="selectDelivery(delivery)"
          >
            <q-item-section>
              {{ delivery.origin }}/{{ delivery.destination }} =>
              {{ delivery.count }} de {{ delivery.total }}
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
          <q-item clickable class="dock-item">
            <q-item-section>
              {{ dock.dockName }}
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
              <!-- Removido o driver-info para evitar duplicidade na representação -->
            </q-item-section>
          </q-item>
        </div>

        <!-- Motoristas Representados como Bolinhas Marrons -->
        <div
          v-for="driver in drivers"
          :key="driver.driverId"
          class="driver-circle"
          :style="driverStyle(driver)"
          draggable="true"
          @dragstart="dragStartDriver(driver, $event)"
        >
          <!-- Apenas a bolinha marrom com o nome do motorista -->
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

        <q-btn
          label="Adicionar Ordem de Teste"
          color="secondary"
          class="q-mt-md q-ml-sm"
          @click="addTestTransportOrder"
        />

        <q-input
          v-model.number="fontSize"
          type="number"
          label="Tamanho da Fonte"
          outlined
          dense
          color="primary"
          class="q-ml-sm"
          style="width: 150px; margin-right: 20px; margin-left: 100px"
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
              <q-color v-model="fontColor" flat />
            </q-card-section>
            <q-card-actions align="right">
              <q-btn flat label="Fechar" @click="toggleFontColorDialog" />
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
      :routePlanner="routePlanner"
      @update-route-planner="updateRoutePlanner"
    />

    <!-- Diálogo de Alerta para Sem Motoristas Disponíveis -->
    <q-dialog v-model="showNoDriverDialog">
      <q-card>
        <q-card-section>
          <div class="text-h6">Atenção</div>
          <div>Não há motoristas disponíveis para mover o trailer.</div>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="Fechar" @click="showNoDriverDialog = false" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted, computed } from "vue";
import axios from "axios";
import RoutePlannerTable from "./RoutePlannerTable.vue"; // Importe o componente da tabela de rotas

// Variáveis de estado para motoristas e entregas
const drivers = ref([]);
const deliveries = ref([]);

// Controle das barras laterais
const leftDrawerOpen = ref(true);
const rightDrawerOpen = ref(true);

// Estado das docks
const placedDocks = ref([]);
const showDetailsDialog = ref(false);
const selectedDock = reactive({
  name: "",
  location: "",
  loadingTrailer: "",
});

// Estado da fonte e bolinhas
const fontSize = ref(12);
const fontColor = ref("#000000");
const showFontColorDialog = ref(false);
const dotSize = ref(10);
const dotColor = ref("#334ab0");

// Dimensões e transformações do dropzone
const dropzoneWidth = ref(1423);
const dropzoneHeight = ref(600);
const zoom = ref(1);
const rotation = ref(0);

// Caminho da imagem de fundo
import backgroundImage from "src/assets/imgVehicle/patio.png";
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
const dockOptions = computed(() => {
  return deliveries.value.map(delivery => ({
    label: `${delivery.origin} -> ${delivery.destination}`,
    value: { origin: delivery.origin, destination: delivery.destination }
  }));
});

// Computed para definir o estilo do dropzone com a imagem de fundo
const dropzoneStyle = computed(() => ({
  width: `${dropzoneWidth.value}px`,
  height: `${dropzoneHeight.value}px`,
  backgroundImage: `url(${backgroundImagePath.value})`,
  backgroundSize: "contain", // Mantém a proporção da imagem
  backgroundPosition: "center",
  transform: `scale(${zoom.value}) rotate(${rotation.value}deg)`,
  paddingLeft: "220px", // Espaço para a seção de motoristas
  paddingRight:
    leftDrawerOpen.value || rightDrawerOpen.value
      ? "450px"
      : "0px" /* Garante que o conteúdo principal respeite os drawers à direita */,
  position: "relative", // Para posicionamento interno
  zIndex: 1, // z-index mais baixo
}));

// Estado para diálogo de alerta
const showNoDriverDialog = ref(false);

// Funções de seleção
function selectDriver(driver) {
  console.log("Motorista selecionado:", driver);
}

function selectDelivery(delivery) {
  console.log("Entrega selecionada:", delivery);
}

// Função para extrair a parte da string entre as barras
function extractBetweenSlashes(str) {
  if (!str) return '';
  const regex = /\/(.*?)\//; // Captura o que está entre duas barras
  const match = str.match(regex);
  if (match && match[1]) {
    return match[1].trim();
  }
  // Se houver apenas uma barra, retorna a parte após a barra
  const singleSlashIndex = str.indexOf('/');
  if (singleSlashIndex !== -1) {
    return str.substring(singleSlashIndex + 1).trim();
  }
  // Se não houver barras, retorna a string inteira
  return str.trim();
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
    const response = await axios.get("http://localhost:8082/api/docks");
    await nextTick();

    placedDocks.value = response.data.map((dock, index) => {
      let position = { x: 52, y: 350 }; // Posição padrão
      if (dock.location && isValidJSON(dock.location)) {
        const parsedLocation = JSON.parse(dock.location);
        position = {
          x: typeof parsedLocation.x === "number" ? parsedLocation.x : 52,
          y: typeof parsedLocation.y === "number" ? parsedLocation.y : 350,
        }; // Use coordenadas dinâmicas ou padrão
      }
      return { ...dock, position, uniqueId: index };
    });

    console.log("Docks posicionadas:", placedDocks.value);
  } catch (error) {
    console.error("Erro ao buscar docks:", error);
  }
}

// Função para buscar motoristas
async function fetchDrivers() {
  try {
    const response = await axios.get("http://localhost:8082/api/drivers");
    drivers.value = response.data.map((driver) => {
      // Se o motorista estiver atribuído a uma dock, posiciona-o sobre essa dock
      if (driver.assignedDockId) {
        const dock = placedDocks.value.find(
          (d) => d.dockId === driver.assignedDockId
        );
        if (dock) {
          return {
            ...driver,
            position: {
              x: dock.position.x + 50, // Ajuste conforme necessário
              y: dock.position.y + 50, // Ajuste conforme necessário
            },
          };
        }
      }
      // Posição padrão se não estiver atribuído a nenhuma dock
      return {
        ...driver,
        position: { x: 100, y: 100 }, // Posição inicial padrão
      };
    });
    console.log("Motoristas carregados:", drivers.value);
  } catch (error) {
    console.error("Erro ao buscar motoristas:", error);
  }
}

// Função para buscar entregas
async function fetchDeliveries() {
  try {
    const response = await axios.get("http://localhost:8082/api/deliveries");
    deliveries.value = response.data.map(delivery => ({
      ...delivery,
      count: delivery.count || 0, // Inicializa count para 0 se não existir
    }));
    console.log("Entregas carregadas:", deliveries.value);
  } catch (error) {
    console.error("Erro ao buscar entregas:", error);
  }
}

// Inicialização
onMounted(async () => {
  await fetchDocks();
  await fetchDrivers(); // Carregar motoristas
  await fetchDeliveries(); // Carregar entregas
  initializeRoutePlanner(); // Inicializar o Route Planner
});

// Função para determinar a cor com base no estado da dock
function getColorByType(dock) {
  if (dock.loadedTrailer) {
    return "#FFD700"; // Dourado se tiver trailer carregado
  } else if (dock.loadingTrailer) {
    return "#008000"; // Verde se tiver trailer carregando
  }
  return "#0000FF"; // Azul se não tiver trailer
}

// Funções de drag & drop dos motoristas
function dragStartDriver(driver, event) {
  draggedDriver = driver;
  event.dataTransfer.setData("driverId", driver.driverId);
  event.dataTransfer.effectAllowed = "move";
  console.log("Motorista arrastado:", driver);
}

// Funções de drag & drop das docks
function dragStartDock(dock, event) {
  draggedDock = dock;
  event.dataTransfer.setData("dockId", dock.dockId);
  event.dataTransfer.effectAllowed = "move";
  console.log("Dock arrastada:", dock);
}

// Função para lidar com o drop em uma dock
async function handleDropOnDock(event, targetDock) {
  event.preventDefault();

  const driverId = event.dataTransfer.getData("driverId");
  const dockId = event.dataTransfer.getData("dockId");

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
    alert("A dock já possui um motorista.");
    return;
  }

  // Atualiza a dock anterior do motorista, se houver
  if (driver.assignedDockId) {
    const previousDock = placedDocks.value.find(
      (d) => d.dockId === driver.assignedDockId
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

  // Atualiza no backend (opcional)
  await updateDriver(driver);
  await updateDock(dock);

  console.log(`Motorista ${driver.name} atribuído à dock ${dock.dockName}.`);
}

// Função para mover um trailer para outra dock
async function moveTrailerToDock(sourceDock, targetDock) {
  // Verifica se a dock de origem tem um trailer em loadedTrailer ou loadingTrailer
  if (!sourceDock.loadedTrailer && !sourceDock.loadingTrailer) {
    alert("A dock de origem não possui um trailer para mover.");
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
      alert("A dock de destino já possui trailers em loading e loaded.");
      return;
    } else {
      targetDock.loadedTrailer = targetDock.loadingTrailer;
      targetDock.loadingTrailer = null;
    }
  }

  // Move o trailer
  let trailerLicensePlate = "xxx"; // Valor padrão
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
    `Trailer movido de ${sourceDock.dockName} para ${targetDock.dockName}. O motorista foi movido junto.`
  );

  // Gerar a ordem de transporte, incluindo 'cargoType'
  await addTransportOrder({
    dockStart: sourceDock.dockName,
    dockDestination: targetDock.dockName,
    driverName: getDriverName(driver.driverId),
    trailerLicensePlate: trailerLicensePlate || "xxx",
    cargoType: "Peça", // Adicionado para garantir o preenchimento
  });
}

// Função para atualizar a dock no backend (opcional)
async function updateDock(dock) {
  try {
    await axios.put(`http://localhost:8082/api/docks/${dock.dockId}`, dock);
    console.log("Dock atualizada com sucesso:", dock);
  } catch (error) {
    console.error("Erro ao atualizar a dock:", error);
  }
}

// Função para atualizar o motorista no backend (opcional)
async function updateDriver(driver) {
  try {
    await axios.put(
      `http://localhost:8082/api/drivers/${driver.driverId}`,
      driver
    );
    console.log("Motorista atualizado com sucesso:", driver);
  } catch (error) {
    console.error("Erro ao atualizar o motorista:", error);
  }
}

// Função para salvar as posições e atribuições das docks (opcional)
async function savePositions() {
  try {
    // Atualiza as docks
    const dockPromises = placedDocks.value.map((dock) =>
      axios.put(`http://localhost:8082/api/docks/${dock.dockId}`, dock)
    );
    // Atualiza os motoristas
    const driverPromises = drivers.value.map((driver) =>
      axios.put(`http://localhost:8082/api/drivers/${driver.driverId}`, driver)
    );
    // Atualiza as ordens de transporte
    const orderPromises = routePlanner.value.map((order) =>
      axios.post(`http://localhost:8082/api/transportOrders`, order)
    ); // Supondo que exista um endpoint para ordens de transporte
    await Promise.all([...dockPromises, ...driverPromises, ...orderPromises]);
    alert("Posições salvas com sucesso!");
  } catch (error) {
    console.error("Erro ao salvar posições:", error);
  }
}

// Função para alternar o diálogo de cor da fonte
function toggleFontColorDialog() {
  showFontColorDialog.value = !showFontColorDialog.value;
}

// Estado e funções para o Planejador de Rotas
// Inicializar Route Planner a partir do LocalStorage
const transportOrderCount = ref(1);

function initializeRoutePlanner() {
  const storedRoutePlanner = localStorage.getItem("routePlanner");
  if (storedRoutePlanner) {
    try {
      const parsed = JSON.parse(storedRoutePlanner);
      routePlanner.value = Array.isArray(parsed) ? parsed : [];
      // Inicializar o contador baseado nas ordens existentes
      transportOrderCount.value = parsed.length > 0 ? parsed.length + 1 : 1;
    } catch (e) {
      console.error("Erro ao analisar routePlanner do LocalStorage:", e);
      routePlanner.value = [];
      transportOrderCount.value = 1;
    }
  } else {
    routePlanner.value = [];
    transportOrderCount.value = 1;
  }
  console.log("Route Planner Initialized:", routePlanner.value);
}

// Função para adicionar uma ordem de transporte
async function addTransportOrder({ dockStart, dockDestination, driverName, trailerLicensePlate, cargoType }) {
  // Verificar se dockStart e dockDestination estão selecionados
  if (!dockStart || !dockDestination) {
    alert("Por favor, selecione Doca de Origem e Doca de Destino.");
    return;
  }

  // Extrair partes relevantes das docas
  const dockStartPart = extractBetweenSlashes(dockStart);
  const dockDestinationPart = extractBetweenSlashes(dockDestination);

  // Encontrar a entrega correspondente
  const matchingDelivery = deliveries.value.find(delivery => 
    extractBetweenSlashes(delivery.origin) === dockStartPart &&
    extractBetweenSlashes(delivery.destination) === dockDestinationPart
  );

  if (!matchingDelivery) {
    alert("A combinação de Doca de Origem e Doca de Destino não corresponde a nenhuma entrega existente.");
    return;
  }

  // Criação da nova ordem
  const newOrder = {
    orderNumber: `ordem ${transportOrderCount.value}`,
    departureTime: new Date().toLocaleTimeString(), // Atualiza para a hora atual
    driverName: driverName || "N/A",
    vehicleLicensePlate: "xxx", // Valor padrão ou ajuste conforme necessário
    cavalinho: "xxx", // Valor padrão ou ajuste conforme necessário
    trailerLicensePlate: trailerLicensePlate || "xxx",
    dockStart: dockStart,
    dockDestination: dockDestination,
    cargoType: cargoType || "Peça", // Garantir que cargoType está definido
  };

  console.log("Criando nova ordem de transporte:", newOrder); // Log para depuração

  // Adiciona a ordem ao routePlanner
  routePlanner.value.push(newOrder);
  transportOrderCount.value += 1;

  // Salva no LocalStorage
  localStorage.setItem("routePlanner", JSON.stringify(routePlanner.value));

  // Incrementa o 'count' na entrega correspondente no frontend
  incrementDeliveryCount(dockStart, dockDestination);

  // Opcional: Salvar no backend (removido conforme solicitado)
  /*
  try {
    await axios.post("http://localhost:8082/api/transportOrders", newOrder);
    console.log("Ordem de transporte adicionada:", newOrder);
  } catch (error) {
    console.error("Erro ao adicionar ordem de transporte:", error);
  }
  */
}

// Função para resetar o Route Planner
function resetRoutePlanner() {
  if (confirm("Tem certeza que deseja resetar todas as ordens de transporte?")) {
    routePlanner.value = [];
    transportOrderCount.value = 1;
    localStorage.removeItem("routePlanner");
    alert("Ordem de transporte resetada com sucesso!");
  }
}

// Função para adicionar uma ordem de transporte de teste
function addTestTransportOrder() {
  // Certifique-se de que "A" e "DPU" correspondem a uma entrega existente
  const testOrder = {
    orderNumber: `ordem ${transportOrderCount.value}`,
    departureTime: "12:00:00",
    driverName: "Test Driver",
    vehicleLicensePlate: "ABC123",
    cavalinho: "Cav-001",
    trailerLicensePlate: "TRAILER1",
    dockStart: "A", // Ajuste conforme necessário
    dockDestination: "DPU", // Ajuste conforme necessário
    cargoType: "Teste", // Certifique-se de que 'Teste' está nas cargoOptions
  };

  console.log("Adicionando ordem de transporte de teste:", testOrder);

  // Extrair partes relevantes das docas
  const dockStartPart = extractBetweenSlashes(testOrder.dockStart);
  const dockDestinationPart = extractBetweenSlashes(testOrder.dockDestination);

  // Verificar se a combinação de docas existe
  const matchingDelivery = deliveries.value.find(delivery => 
    extractBetweenSlashes(delivery.origin) === dockStartPart &&
    extractBetweenSlashes(delivery.destination) === dockDestinationPart
  );

  if (!matchingDelivery) {
    alert("A combinação de Doca de Origem e Doca de Destino da ordem de teste não corresponde a nenhuma entrega existente.");
    return;
  }

  routePlanner.value.push(testOrder);
  transportOrderCount.value += 1;

  // Salva no LocalStorage
  localStorage.setItem("routePlanner", JSON.stringify(routePlanner.value));

  // Incrementa o 'count' na entrega correspondente no frontend
  incrementDeliveryCount(testOrder.dockStart, testOrder.dockDestination);
}

// Função para atualizar o Route Planner a partir da tabela
function updateRoutePlanner(updatedRoutePlanner) {
  routePlanner.value = updatedRoutePlanner;
  // Atualizar o contador de ordens
  transportOrderCount.value = updatedRoutePlanner.length > 0 ? updatedRoutePlanner.length + 1 : 1;
  localStorage.setItem("routePlanner", JSON.stringify(routePlanner.value));
  console.log("Route Planner Updated from Table:", routePlanner.value);
}

// Função para obter o nome do motorista pelo ID
function getDriverName(driverId) {
  const driver = drivers.value.find((d) => d.driverId === driverId);
  return driver ? driver.name : "N/A";
}

// Função para obter o avatar do motorista pelo ID (opcional)
function getDriverAvatar(driverId) {
  // Implemente a lógica para obter o avatar do motorista, se aplicável
  // Por exemplo, retornar uma URL de imagem baseada no ID
  return "https://via.placeholder.com/24"; // Placeholder
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
    backgroundColor: '#8B4513', // Bolinha marrom
    width: `${dotSize.value + 10}px`,
    height: `${dotSize.value + 10}px`,
    borderRadius: '50%',
    cursor: 'grab',
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
  // Extrair partes relevantes das docas
  const originPart = extractBetweenSlashes(origin);
  const destinationPart = extractBetweenSlashes(destination);

  // Encontrar a entrega correspondente
  const matchingDelivery = deliveries.value.find(delivery => 
    extractBetweenSlashes(delivery.origin) === originPart &&
    extractBetweenSlashes(delivery.destination) === destinationPart
  );

  if (matchingDelivery) {
    // Incrementar o count
    matchingDelivery.count += 1;
    console.log(`Count incrementado para a entrega ${matchingDelivery.deliveryId}:`, matchingDelivery);
  } else {
    console.warn(`Entrega não encontrada para origem: ${originPart}, destino: ${destinationPart}`);
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

.dock-origin, .dock-destination {
  width: 45%;
}
</style>
