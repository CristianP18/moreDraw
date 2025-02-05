<template>
  <q-page class="q-pa-md">
    <div class="top-container">
      <q-card class="flex-row">
        <div class="selection-container">
          <q-card-section>
            <div class="text-h6">Gerenciar Veículos</div>

            <!-- Botão para abrir formulário de cadastro -->
            <q-btn
              :label="'Cadastrar Novo Veículo'"
              color="primary"
              class="q-mt-md"
              @click="toggleCreateVehicleForm"
            />

            <!-- Formulário para Cadastrar Novo Veículo -->
            <q-card v-if="showCreateVehicleForm" class="q-mt-md">
              <q-card-section>
                <div class="text-h6">Cadastrar Veículo</div>
                <q-input
                  v-model="vehicle.licensePlate"
                  filled
                  label="Placa do Veículo"
                  class="q-mt-md"
                />
                <q-input
                  v-model="vehicle.type"
                  filled
                  label="Tipo"
                  class="q-mt-md"
                />
                <q-input
                  v-model="vehicle.description"
                  filled
                  label="Descrição"
                  class="q-mt-md"
                />
                <q-btn
                  :label="'Cadastrar Veículo'"
                  color="secondary"
                  class="q-mt-md"
                  :loading="saving"
                  @click="saveVehicle"
                >
                  <template #loading>
                    <q-spinner color="white" />
                  </template>
                </q-btn>
              </q-card-section>
            </q-card>

            <!-- Botão para listar todos os veículos -->
            <q-btn
              :label="'Listar Todos os Veículos'"
              color="primary"
              class="q-mt-md"
              :loading="loadingAll"
              @click="fetchAllVehicles"
            />

            <!-- Input para pesquisar Veículo -->
            <q-input
              v-model="licensePlate"
              filled
              label="Placa do Veículo"
              lazy-rules
              :rules="[
                (val) =>
                  (val && val.length > 0) || 'A Placa não pode ser vazia',
              ]"
              class="q-mt-md"
              @input="debouncedSearch"
            />
            <q-btn
              :label="'Buscar Veículo'"
              color="primary"
              class="q-mt-md"
              :loading="loading"
              :disable="loading || !licensePlate"
              @click="searchByLicensePlate"
            >
              <template #loading>
                <q-spinner color="white" />
              </template>
            </q-btn>
          </q-card-section>
        </div>
      </q-card>

      <!-- Lista de Todos os Veículos -->
      <q-card v-if="allVehicles.length > 0" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Lista de Veículos</div>
          <q-list bordered class="q-mt-md">
            <q-item
              v-for="vehicle in allVehicles"
              :key="vehicle.id"
              clickable
              @click="selectVehicle(vehicle)"
            >
              <q-item-section>
                {{ vehicle.licensePlate }} - Tipo: {{ vehicle.type }}
              </q-item-section>
              <q-item-section side>
                <q-btn
                  flat
                  round
                  icon="delete"
                  color="red"
                  @click.stop="deleteVehicle(vehicle.id)"
                />
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>
      </q-card>

      <!-- Exibir Veículo Selecionado -->
      <q-card v-if="selectedVehicle" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Editar Veículo</div>
          <q-input
            v-model="vehicle.licensePlate"
            filled
            label="Placa do Veículo"
            class="q-mt-md"
          />
          <q-input v-model="vehicle.type" filled label="Tipo" class="q-mt-md" />
          <q-input
            v-model="vehicle.description"
            filled
            label="Descrição"
            class="q-mt-md"
          />
          <q-input
            v-model="vehicle.boarding"
            filled
            label="Carregado"
            class="q-mt-md"
          />
          <q-input
            v-model="vehicle.driver"
            filled
            label="Motorista"
            class="q-mt-md"
          />
          <q-btn
            :label="'Atualizar Veículo'"
            color="secondary"
            class="q-mt-md"
            :loading="saving"
            @click="saveVehicle"
          >
            <template #loading>
              <q-spinner color="white" />
            </template>
          </q-btn>
          <q-btn
            :label="'Excluir Veículo'"
            color="negative"
            class="q-mt-md"
            :loading="saving"
            @click="deleteVehicle(vehicle.value.vehicleId)"
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

const licensePlate = ref("");
const selectedVehicle = ref(null);
const showCreateVehicleForm = ref(false);
const vehicle = ref({
  licensePlate: "",
  type: "",
  description: "",
});
const allVehicles = ref([]);
const loading = ref(false);
const loadingAll = ref(false);
const saving = ref(false);
const isEditMode = ref(false);

const toggleCreateVehicleForm = () => {
  showCreateVehicleForm.value = !showCreateVehicleForm.value;
};

const debouncedSearch = debounce(async () => {
  if (licensePlate.value) {
    try {
      loading.value = true;
      const response = await axios.get(
        `http://localhost:8082/api/vehicles/findByLicensePlate/${licensePlate.value}`
      );
      if (response.data) {
        selectedVehicle.value = response.data;
        vehicle.value = response.data;
        isEditMode.value = true;
      } else {
        selectedVehicle.value = null;
        Notify.create({
          message: "Nenhum veículo encontrado com esta placa.",
          color: "warning",
          position: "top",
        });
      }
    } catch (error) {
      console.error("Search error:", error);
      Notify.create({
        message: "Erro ao buscar veículo. Tente novamente.",
        color: "negative",
        position: "top",
      });
    } finally {
      loading.value = false;
    }
  } else {
    selectedVehicle.value = null;
  }
}, 300);

function searchByLicensePlate() {
  debouncedSearch.cancel();
  debouncedSearch();
}

async function fetchAllVehicles() {
  try {
    loadingAll.value = true;
    const response = await axios.get(`http://localhost:8082/api/vehicles`);
    allVehicles.value = response.data;
  } catch (error) {
    console.error("Error fetching all vehicles:", error);
    Notify.create({
      message: "Erro ao buscar todos os veículos. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    loadingAll.value = false;
  }
}

async function saveVehicle() {
  try {
    saving.value = true;
    if (isEditMode.value) {
      await axios.put(
        `http://localhost:8082/api/vehicles/${vehicle.value.vehicleId}`,
        vehicle.value
      );
      Notify.create({
        message: "Veículo atualizado com sucesso!",
        color: "positive",
        position: "top",
      });
    } else {
      await axios.post(`http://localhost:8082/api/vehicles`, vehicle.value);
      Notify.create({
        message: "Veículo cadastrado com sucesso!",
        color: "positive",
        position: "top",
      });
    }
    resetVehicleForm();
    fetchAllVehicles();
  } catch (error) {
    console.error("Error saving vehicle:", error);
    Notify.create({
      message: "Erro ao salvar o veículo. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

async function deleteVehicle(vehicleId) {
  try {
    saving.value = true;
    await axios.delete(
      `http://localhost:8082/api/vehicles/${vehicle.value.vehicleId}`
    );
    Notify.create({
      message: "Veículo excluído com sucesso!",
      color: "positive",
      position: "top",
    });
    resetVehicleForm();
    fetchAllVehicles();
  } catch (error) {
    console.error("Error deleting vehicle:", error);
    Notify.create({
      message: "Erro ao excluir o veículo. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

function selectVehicle(vehicleData) {
  selectedVehicle.value = vehicleData;
  vehicle.value = { ...vehicleData };
  isEditMode.value = true;
}

function resetVehicleForm() {
  vehicle.value = {
    licensePlate: "",
    type: "",
    description: "",
  };
  selectedVehicle.value = null;
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
