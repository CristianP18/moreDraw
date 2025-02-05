<template>
  <q-page class="q-pa-md">
    <div class="top-container">
      <q-card class="flex-row">
        <div class="selection-container">
          <q-card-section>
            <div class="text-h6">Roteirizador</div>

            <!-- Input e Botão de Busca de Simulação -->
            <q-input
              v-model="simulationId"
              filled
              :label="'ID da Simulação'"
              lazy-rules
              :rules="[
                (val) =>
                  (val && val.length > 0) ||
                  'clientReport.emptySimulationError',
              ]"
              @input="debouncedSearch"
            />
            <q-btn
              :label="'Buscar Simulação'"
              color="primary"
              class="q-mt-md"
              :loading="loading"
              :disable="loading || !simulationId"
              @click="searchBySimulationId"
            >
              <template #loading>
                <q-spinner color="white" />
              </template>
            </q-btn>

            <!-- Input e Botão de Rank -->
            <q-input
              v-model="rankId"
              filled
              :label="'ID para Ranqueamento'"
              class="q-mt-md"
            />
            <q-btn
              :label="'Rankear Rota'"
              color="secondary"
              class="q-mt-md"
              :loading="loadingRank"
              :disable="loadingRank || !rankId"
              @click="rankRoute"
            >
              <template #loading>
                <q-spinner color="white" />
              </template>
            </q-btn>

            <!-- Botão de Performance -->
            <q-btn
              :label="'Performance'"
              color="accent"
              class="q-mt-md"
              @click="togglePerformanceCard"
              :disable="!selectedRoute"
            />

            <!-- Card de Performance -->
            <q-card v-if="showPerformance" class="q-mt-md">
              <q-card-section>
                <div class="text-h6">Performance</div>
                <q-list bordered class="q-mt-md">
                  <q-item
                    v-for="(option, index) in performanceData.deliveryOptions"
                    :key="index"
                  >
                    <q-item-section>
                      <div>
                        ID da Entrega: {{ option.deliveryId }} -> Contagem:
                        {{ option.count }} | Total:
                        {{ option.total }}
                      </div>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-card-section>
            </q-card>

            <!-- Box de Progresso -->
            <q-card v-show="showProgress" class="q-mt-md">
              <q-card-section>
                <div class="text-h6">Progresso da Roteirização</div>
                <q-linear-progress
                  :value="progress / 100"
                  color="blue"
                  class="q-mt-md"
                  animated
                />
                <div class="text-subtitle2 q-mt-md">
                  {{ progress.toFixed(0) }}% - Rotas Geradas:
                  {{ routesGenerated }}
                </div>
              </q-card-section>
            </q-card>

            <!-- Exibição do Resultado da Simulação -->
            <q-card v-if="simulationResult" class="q-mt-md">
              <q-card-section>
                <div class="text-h6">Resultado da Simulação</div>
                <q-list bordered class="q-mt-md">
                  <q-item
                    v-for="route in simulationResult"
                    :key="route.routePlanningId"
                    clickable
                    @click="selectRoute(route)"
                  >
                    <q-item-section>
                      Entrega Número: {{ route.numDelivery }}
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-card-section>
            </q-card>
          </q-card-section>
        </div>
      </q-card>

      <!-- Tabela com Detalhes da Rota Selecionada -->
      <q-card v-if="selectedRoute" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Detalhes da Rota Selecionada</div>
          <q-input
            v-model="filter"
            filled
            :label="'Filtrar Detalhes da Rota'"
            @input="applyFilter"
            class="q-mb-md"
          />
          <q-table
            :rows="filteredRows"
            :columns="columns"
            row-key="transporterOrderId"
          />
          <!-- Botão para Imprimir em PDF -->
          <q-btn
            :label="'Imprimir em PDF'"
            color="primary"
            class="q-mt-md"
            @click="printPDF"
          />
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import axios from "axios";
import debounce from "lodash/debounce";
import { Notify } from "quasar";

const simulationId = ref("");
const rankId = ref("");
const simulationResult = ref(null);
const loading = ref(false);
const loadingRank = ref(false);
const selectedRoute = ref(null);
const filter = ref("");
const progress = ref(0);
const routesGenerated = ref(0);
const showProgress = ref(false);
let progressInterval = null;

// Performance data
const showPerformance = ref(false);
const performanceData = ref({ deliveryOptions: [] });

const columns = [
  { name: "date", label: "Data", field: "date", align: "left" },
  { name: "driver", label: "Motorista", field: "driver", align: "left" },
  { name: "truck", label: "Caminhão", field: "truck", align: "left" },
  { name: "trailer", label: "Reboque", field: "trailer", align: "left" },
  { name: "dock", label: "Doca de Origem", field: "dock", align: "left" },
  { name: "carregado", label: "Carregado", field: "carregado", align: "left" },
  {
    name: "destinationDock",
    label: "Doca de Destino",
    field: "destinationDock",
    align: "left",
  },
  {
    name: "timeOfDelivery",
    label: "Tempo de Entrega",
    field: "timeOfDelivery",
    align: "left",
  },
  {
    name: "numberDriver",
    label: "Número do Motorista",
    field: "numberDriver",
    align: "left",
  },
];

const filteredRows = computed(() => {
  if (!filter.value) {
    return selectedRoute.value.transporterOrders;
  }
  return selectedRoute.value.transporterOrders.filter((order) => {
    return Object.keys(order).some((key) =>
      String(order[key]).toLowerCase().includes(filter.value.toLowerCase())
    );
  });
});

const debouncedSearch = debounce(async () => {
  if (simulationId.value && simulationId.value != simulationResult.value) {
    try {
      loading.value = true;
      showProgress.value = true;
      startProgressCheck();

      const response = await axios.get(
        `http://localhost:8080/simulation/${simulationId.value}`
      );
      simulationResult.value = response.data;
      performanceData.value = response.data; // Store performance data
      selectedRoute.value = null;
    } catch (error) {
      simulationResult.value = null;
      Notify.create({
        message: "Erro ao buscar simulação. Tente novamente.",
        color: "negative",
        position: "top",
      });
      stopProgressCheck();
    } finally {
      loading.value = false;
    }
  } else {
    simulationResult.value = null;
  }
}, 300);

function startProgressCheck() {
  progressInterval = setInterval(async () => {
    try {
      const response = await axios.get(`http://localhost:8080/progress`);
      routesGenerated.value = parseInt(response.data);
      progress.value = (routesGenerated.value * 100) / simulationId.value;

      if (routesGenerated.value >= simulationId.value) {
        stopProgressCheck();
        Notify.create({
          message: "Roteirização concluída com sucesso!",
          color: "positive",
          position: "top",
        });
      }
    } catch (error) {
      console.error("Error fetching progress:", error);
    }
  }, 500);
}

function stopProgressCheck() {
  if (progressInterval) {
    clearInterval(progressInterval);
    progressInterval = null;
  }
}
async function rankRoute() {
  if (rankId.value) {
    try {
      loadingRank.value = true;
      showProgress.value = true;
      startProgressCheck();
      const response = await axios.get(
        `http://localhost:8080/rankRoute/${rankId.value}`
      );
      simulationResult.value = response.data;
      performanceData.value = response.data;
      selectedRoute.value = null;
    } catch (error) {
      simulationResult.value = null;
      Notify.create({
        message: "Erro ao ranquear rota. Tente novamente.",
        color: "negative",
        position: "top",
      });
    } finally {
      loadingRank.value = false;
      stopProgressCheck();
    }
  }
}

function searchBySimulationId() {
  debouncedSearch.cancel();
  debouncedSearch();
}

function selectRoute(route) {
  selectedRoute.value = route;
  performanceData.value = route;
}

function applyFilter() {
  // A filtragem é feita automaticamente na propriedade computed "filteredRows"
}

async function printPDF() {
  if (selectedRoute.value) {
    try {
      const response = await axios.get(
        `http://localhost:8080/showPDF/${selectedRoute.value.routePlanningId}`,
        { responseType: "blob" }
      );
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "route-details.pdf");
      document.body.appendChild(link);
      link.click();
      Notify.create({
        message: "PDF gerado e baixado com sucesso.",
        color: "positive",
        position: "top",
      });
    } catch (error) {
      Notify.create({
        message: "Erro ao gerar PDF. Tente novamente.",
        color: "negative",
        position: "top",
      });
    }
  } else {
    Notify.create({
      message: "Nenhuma rota selecionada para imprimir.",
      color: "warning",
      position: "top",
    });
  }
}

function togglePerformanceCard() {
  showPerformance.value = !showPerformance.value;
}

onMounted(() => {
  stopProgressCheck(); // Limpa o intervalo ao montar o componente
});

onBeforeUnmount(() => {
  stopProgressCheck(); // Limpa o intervalo ao desmontar o componente
});
</script>

<style scoped>
.flex-row {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: start;
}
.top-container {
  width: 100%;
}
.selection-container {
  flex: 1;
}
.q-card {
  margin-top: 20px;
}
.text-h6 {
  font-weight: bold;
}
</style>
