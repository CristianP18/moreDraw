<template>
  <q-page class="q-pa-md">
    <div class="top-container">
      <q-card class="flex-row">
        <div class="selection-container">
          <q-card-section>
            <div class="text-h6">Gerenciar Motoristas</div>

            <!-- Botão para abrir formulário de cadastro -->
            <q-btn
              :label="'Cadastrar Novo Motorista'"
              color="primary"
              class="q-mt-md"
              @click="toggleCreateDriverForm"
            />

            <!-- Formulário para Cadastrar Novo Motorista -->
            <q-card v-if="showCreateDriverForm" class="q-mt-md">
              <q-card-section>
                <div class="text-h6">Cadastrar Motorista</div>
                <q-input
                  v-model="driver.name"
                  filled
                  label="Nome do Motorista"
                  placeholder="Digite o nome do motorista"
                  class="q-mt-md"
                />
                <q-input
                  v-model="driver.vehicleId"
                  filled
                  label="ID do Veículo"
                  placeholder="Digite o ID do veículo"
                  class="q-mt-md"
                />
                <q-input
                  v-model="driver.trailerLicensePlate"
                  filled
                  label="Placa do Reboque"
                  placeholder="Digite a placa do reboque"
                  class="q-mt-md"
                />
                <q-input
                  v-model="driver.dockStart"
                  filled
                  label="Doca de Departida"
                  placeholder="Digite a doca de saida"
                  class="q-mt-md"
                />
                <q-input
                  v-model="driver.destinationDock"
                  filled
                  label="Doca de Destino"
                  placeholder="Digite a doca de destino"
                  class="q-mt-md"
                />
                <q-toggle
                  v-model="driver.available"
                  label="Disponível"
                  class="q-mt-md"
                />
                <q-input
                  v-model="driver.arrivalTime"
                  filled
                  label="Hora de Chegada"
                  placeholder="Digite a hora de chegada"
                  class="q-mt-md"
                />
                <q-input
                  v-model="driver.endDelivery"
                  filled
                  label="Fim da Entrega"
                  placeholder="Digite a hora do fim da entrega"
                  class="q-mt-md"
                />
                <q-input
                  v-model="driver.departureTime"
                  filled
                  label="Hora de Partida"
                  placeholder="Digite a hora de partida"
                  class="q-mt-md"
                />

                <q-btn
                  :label="'Cadastrar Motorista'"
                  color="secondary"
                  class="q-mt-md"
                  :loading="saving"
                  @click="saveDriver"
                >
                  <template #loading>
                    <q-spinner color="white" />
                  </template>
                </q-btn>
              </q-card-section>
            </q-card>

            <!-- Botão para listar todos os motoristas -->
            <q-btn
              :label="'Listar Todos os Motoristas'"
              color="primary"
              class="q-mt-md"
              :loading="loadingAll"
              @click="fetchAllDrivers"
            />

            <!-- Input para pesquisar Motorista -->
            <q-input
              v-model="driverId"
              filled
              label="ID do Motorista"
              lazy-rules
              :rules="[
                (val) =>
                  (val && val.length > 0) ||
                  'O ID do Motorista não pode ser vazio',
              ]"
              class="q-mt-md"
              @input="debouncedSearch"
            />
            <q-btn
              :label="'Buscar Motorista'"
              color="primary"
              class="q-mt-md"
              :loading="loading"
              :disable="loading || !driverId"
              @click="searchByDriverId"
            >
              <template #loading>
                <q-spinner color="white" />
              </template>
            </q-btn>
          </q-card-section>
        </div>
      </q-card>

      <!-- Lista de Todos os Motoristas -->
      <q-card v-if="allDrivers.length > 0" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Lista de Motoristas</div>
          <q-list bordered class="q-mt-md">
            <q-item
              v-for="driver in allDrivers"
              :key="driver.driverId"
              clickable
              @click="selectDriver(driver)"
            >
              <q-item-section>
                {{ driver.name }} - ID: {{ driver.driverId }}
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>
      </q-card>

      <!-- Exibir Motorista Selecionado -->
      <q-card v-if="selectedDriver && isEditMode" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Editar Motorista</div>
          <q-input
            v-model="driver.name"
            filled
            label="Nome do Motorista"
            :placeholder="selectedDriver.name || 'Nome não disponível'"
            class="q-mt-md"
          />
          <q-input
            v-model="driver.vehicleId"
            filled
            label="ID do Veículo"
            :placeholder="selectedDriver.vehicleId || 'N/A'"
            class="q-mt-md"
          />
          <q-input
            v-model="driver.trailerLicensePlate"
            filled
            label="Placa do Reboque"
            :placeholder="selectedDriver.trailerLicensePlate || 'N/A'"
            class="q-mt-md"
          />
          <q-input
            v-model="driver.destinationDock"
            filled
            label="Doca de Destino"
            :placeholder="selectedDriver.destinationDock || 'N/A'"
            class="q-mt-md"
          />
          <q-toggle
            v-model="driver.available"
            label="Disponível"
            :checked="selectedDriver.available"
            class="q-mt-md"
          />
          <q-input
            v-model="driver.arrivalTime"
            filled
            label="Hora de Chegada"
            :placeholder="selectedDriver.arrivalTime || 'N/A'"
            class="q-mt-md"
          />
          <q-input
            v-model="driver.endDelivery"
            filled
            label="Fim da Entrega"
            :placeholder="selectedDriver.endDelivery || 'N/A'"
            class="q-mt-md"
          />
          <q-input
            v-model="driver.departureTime"
            filled
            label="Hora de Partida"
            :placeholder="selectedDriver.departureTime || 'N/A'"
            class="q-mt-md"
          />

          <q-btn
            :label="'Atualizar Motorista'"
            color="secondary"
            class="q-mt-md"
            :loading="saving"
            @click="saveDriver"
          >
            <template #loading>
              <q-spinner color="white" />
            </template>
          </q-btn>

          <!-- Botão para excluir o motorista -->
          <q-btn
            :label="'Excluir Motorista'"
            color="negative"
            class="q-mt-md"
            :loading="deleting"
            @click="deleteDriver"
          >
            <template #loading>
              <q-spinner color="white" />
            </template>
          </q-btn>
        </q-card-section>
      </q-card>
    </div>
  </q-page>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";
import debounce from "lodash/debounce";
import { Notify } from "quasar";

const driverId = ref("");
const selectedDriver = ref(null);
const showCreateDriverForm = ref(false);
const driver = ref({
  driverId: "", // Adicionado para garantir que o ID seja enviado corretamente na atualização
  name: "",
  vehicleId: "",
  trailerLicensePlate: "",
  destinationDock: "",
  available: false,
  arrivalTime: "",
  endDelivery: "",
  departureTime: "",
});
const allDrivers = ref([]);
const loading = ref(false);
const loadingAll = ref(false);
const saving = ref(false);
const deleting = ref(false);
const isEditMode = ref(false);

const toggleCreateDriverForm = () => {
  showCreateDriverForm.value = !showCreateDriverForm.value;
};

const debouncedSearch = debounce(async () => {
  if (driverId.value) {
    try {
      loading.value = true;
      const response = await axios.get(
        `http://localhost:8082/api/drivers/${driverId.value}`
      );
      if (response.data) {
        selectedDriver.value = response.data;
        driver.value = { ...response.data }; // Preenche o formulário com os dados recebidos
        isEditMode.value = true;
      } else {
        selectedDriver.value = null;
        Notify.create({
          message: "Nenhum motorista encontrado com este ID.",
          color: "warning",
          position: "top",
        });
      }
    } catch (error) {
      console.error("Search error:", error);
      Notify.create({
        message: "Erro ao buscar motorista. Tente novamente.",
        color: "negative",
        position: "top",
      });
    } finally {
      loading.value = false;
    }
  } else {
    selectedDriver.value = null;
  }
}, 300);

function searchByDriverId() {
  debouncedSearch.cancel();
  debouncedSearch();
}

async function fetchAllDrivers() {
  try {
    loadingAll.value = true;
    const response = await axios.get(`http://localhost:8082/api/drivers`);
    allDrivers.value = response.data;
  } catch (error) {
    console.error("Error fetching all drivers:", error);
    Notify.create({
      message: "Erro ao buscar todos os motoristas. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    loadingAll.value = false;
  }
}

async function saveDriver() {
  try {
    saving.value = true;
    if (isEditMode.value && driver.value.driverId) {
      await axios.put(
        `http://localhost:8082/api/drivers/${driver.value.driverId}`,
        driver.value
      );
      Notify.create({
        message: "Motorista atualizado com sucesso!",
        color: "positive",
        position: "top",
      });
    } else {
      await axios.post(`http://localhost:8082/api/drivers`, driver.value);
      Notify.create({
        message: "Motorista cadastrado com sucesso!",
        color: "positive",
        position: "top",
      });
    }
    resetDriverForm();
    fetchAllDrivers(); // Atualiza a lista de motoristas
  } catch (error) {
    console.error("Error saving driver:", error);
    Notify.create({
      message: "Erro ao salvar o motorista. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

async function deleteDriver() {
  try {
    deleting.value = true;
    if (selectedDriver.value && selectedDriver.value.driverId) {
      await axios.delete(
        `http://localhost:8082/api/drivers/${selectedDriver.value.driverId}`
      );
      Notify.create({
        message: "Motorista excluído com sucesso!",
        color: "positive",
        position: "top",
      });
      resetDriverForm();
      fetchAllDrivers(); // Atualiza a lista de motoristas
    }
  } catch (error) {
    console.error("Error deleting driver:", error);
    Notify.create({
      message: "Erro ao excluir o motorista. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    deleting.value = false;
  }
}

function selectDriver(driverData) {
  selectedDriver.value = driverData;
  driver.value = { ...driverData }; // Preenche o formulário com os dados do motorista selecionado
  isEditMode.value = true;
}

function resetDriverForm() {
  driver.value = {
    driverId: "", // Limpa o ID do motorista ao resetar o formulário
    name: "",
    vehicleId: "",
    trailerLicensePlate: "",
    destinationDock: "",
    available: false,
    arrivalTime: "",
    endDelivery: "",
    departureTime: "",
  };
  selectedDriver.value = null;
  isEditMode.value = false;
}
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
