<template>
  <q-page class="q-pa-md">
    <div class="top-container">
      <q-card class="flex-row">
        <div class="selection-container">
          <q-card-section>
            <div class="text-h6">Gerenciar Docas</div>

            <!-- Botão para abrir formulário de cadastro -->
            <q-btn
              :label="'Cadastrar Nova Doca'"
              color="primary"
              class="q-mt-md"
              @click="toggleCreateDockForm"
            />

            <!-- Formulário para Cadastrar Nova Doca -->
            <q-card v-if="showCreateDockForm" class="q-mt-md">
              <q-card-section>
                <div class="text-h6">Cadastrar Doca</div>
                <q-input
                  v-model="dock.dockName"
                  filled
                  label="Nome da Doca"
                  placeholder="Digite o nome da doca"
                  class="q-mt-md"
                />
                <q-input
                  v-model="dock.location"
                  filled
                  label="Localização"
                  placeholder="Digite a localização da doca"
                  class="q-mt-md"
                />
                <q-input
                  v-model="dock.description"
                  filled
                  label="Descrição"
                  placeholder="Digite a descrição"
                  class="q-mt-md"
                />
                <q-input
                  v-model="dock.loadingTrailer"
                  filled
                  label="Trailer Carregando"
                  placeholder="Digite o ID do trailer que está carregando"
                  class="q-mt-md"
                />
                <q-input
                  v-model="dock.loadedTrailer"
                  filled
                  label="Trailer Carregado"
                  placeholder="Digite o ID do trailer que foi carregado"
                  class="q-mt-md"
                />
                <q-toggle
                  v-model="dock.available"
                  label="Doca Disponível"
                  color="primary"
                  class="q-mt-md"
                />
                <q-input
                  v-model="dock.arrivalTime"
                  filled
                  label="Hora de Chegada"
                  placeholder="Digite a hora de chegada (YYYY-MM-DD HH:mm:ss)"
                  class="q-mt-md"
                />
                <q-input
                  v-model="dock.departureTime"
                  filled
                  label="Hora de Saída"
                  placeholder="Digite a hora de saída (YYYY-MM-DD HH:mm:ss)"
                  class="q-mt-md"
                />
                <q-input
                  v-model="dock.dockFree"
                  filled
                  label="Doca Livre"
                  placeholder="Digite a hora em que a doca estará livre (YYYY-MM-DD HH:mm:ss)"
                  class="q-mt-md"
                />
                <q-btn
                  :label="'Cadastrar Doca'"
                  color="secondary"
                  class="q-mt-md"
                  :loading="saving"
                  @click="createDock"
                >
                  <template #loading>
                    <q-spinner color="white" />
                  </template>
                </q-btn>
              </q-card-section>
            </q-card>

            <!-- Botão para listar todas as docas -->
            <q-btn
              :label="'Listar Todas as Docas'"
              color="primary"
              class="q-mt-md"
              :loading="loadingAll"
              @click="fetchAllDocks"
            />

            <!-- Input para pesquisar Doca -->
            <q-input
              v-model="dockId"
              filled
              label="ID da Doca"
              lazy-rules
              :rules="[
                (val) =>
                  (val && val.length > 0) || 'O ID da Doca não pode ser vazio',
              ]"
              class="q-mt-md"
              @input="debouncedSearch"
            />
            <q-btn
              :label="'Buscar Doca'"
              color="primary"
              class="q-mt-md"
              :loading="loading"
              :disable="loading || !dockId"
              @click="searchByDockId"
            >
              <template #loading>
                <q-spinner color="white" />
              </template>
            </q-btn>
          </q-card-section>
        </div>
      </q-card>

      <!-- Lista de Todas as Docas -->
      <q-card v-if="allDocks.length > 0" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Lista de Docas</div>
          <q-list bordered class="q-mt-md">
            <q-item
              v-for="dock in allDocks"
              :key="dock.dockId"
              clickable
              @click="selectDock(dock)"
            >
              <q-item-section>
                <div class="text-subtitle1">ID: {{ dock.dockId }}</div>
                <div class="text-caption">Nome: {{ dock.dockName }}</div>
              </q-item-section>
              <q-item-section side>
                <q-btn
                  flat
                  round
                  icon="delete"
                  color="red"
                  @click.stop="deleteDock(dock.dockId)"
                />
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>
      </q-card>

      <!-- Exibir Doca Selecionada -->
      <q-card v-if="selectedDock && isEditMode" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Editar Doca</div>
          <q-input
            v-model="dock.dockName"
            filled
            label="Nome da Doca"
            :placeholder="selectedDock.dockName || 'Nome não disponível'"
            class="q-mt-md"
          />
          <q-input
            v-model="dock.location"
            filled
            label="Localização"
            :placeholder="selectedDock.location || 'N/A'"
            class="q-mt-md"
          />
          <q-input
            v-model="dock.description"
            filled
            label="Descrição"
            :placeholder="selectedDock.description || 'N/A'"
            class="q-mt-md"
          />
          <q-input
            v-model="dock.loadingTrailer"
            filled
            label="Trailer Carregando"
            :placeholder="selectedDock.loadingTrailer || 'N/A'"
            class="q-mt-md"
          />
          <q-input
            v-model="dock.loadedTrailer"
            filled
            label="Trailer Carregado"
            :placeholder="selectedDock.loadedTrailer || 'N/A'"
            class="q-mt-md"
          />
          <q-toggle
            v-model="dock.available"
            label="Doca Disponível"
            color="primary"
            class="q-mt-md"
          />
          <q-input
            v-model="dock.arrivalTime"
            filled
            label="Hora de Chegada"
            :placeholder="selectedDock.arrivalTime || 'YYYY-MM-DD HH:mm:ss'"
            class="q-mt-md"
          />
          <q-input
            v-model="dock.departureTime"
            filled
            label="Hora de Saída"
            :placeholder="selectedDock.departureTime || 'YYYY-MM-DD HH:mm:ss'"
            class="q-mt-md"
          />
          <q-input
            v-model="dock.dockFree"
            filled
            label="Doca Livre"
            :placeholder="selectedDock.dockFree || 'YYYY-MM-DD HH:mm:ss'"
            class="q-mt-md"
          />
          <q-btn
            :label="'Atualizar Doca'"
            color="secondary"
            class="q-mt-md"
            :loading="saving"
            @click="updateDock"
          >
            <template #loading>
              <q-spinner color="white" />
            </template>
          </q-btn>
          <q-btn
            :label="'Excluir Doca'"
            color="negative"
            class="q-mt-md"
            :loading="saving"
            @click="deleteDock(selectedDock.dockId)"
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

const dockId = ref("");
const selectedDock = ref(null);
const showCreateDockForm = ref(false);
const dock = ref({
  dockId: "",
  dockName: "",
  location: "",
  description: "",
  loadingTrailer: "",
  loadedTrailer: "",
  available: false,
  arrivalTime: "",
  departureTime: "",
  dockFree: "",
});
const allDocks = ref([]);
const loading = ref(false);
const loadingAll = ref(false);
const saving = ref(false);
const isEditMode = ref(false);

const toggleCreateDockForm = () => {
  showCreateDockForm.value = !showCreateDockForm.value;
};

const debouncedSearch = debounce(async () => {
  if (dockId.value) {
    try {
      loading.value = true;
      const response = await axios.get(
        `http://localhost:8082/api/docks/${dockId.value}`
      );
      if (response.data) {
        selectedDock.value = response.data;
        dock.value = { ...response.data };
        isEditMode.value = true;
      } else {
        selectedDock.value = null;
        Notify.create({
          message: "Nenhuma doca encontrada com este ID.",
          color: "warning",
          position: "top",
        });
      }
    } catch (error) {
      console.error("Search error:", error);
      Notify.create({
        message: "Erro ao buscar doca. Tente novamente.",
        color: "negative",
        position: "top",
      });
    } finally {
      loading.value = false;
    }
  } else {
    selectedDock.value = null;
  }
}, 300);

function searchByDockId() {
  debouncedSearch.cancel();
  debouncedSearch();
}

async function fetchAllDocks() {
  try {
    loadingAll.value = true;
    const response = await axios.get(`http://localhost:8082/api/docks`);
    allDocks.value = response.data;
  } catch (error) {
    console.error("Error fetching all docks:", error);
    Notify.create({
      message: "Erro ao buscar todas as docas. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    loadingAll.value = false;
  }
}

async function createDock() {
  try {
    saving.value = true;
    await axios.post(`http://localhost:8082/api/docks`, dock.value);
    Notify.create({
      message: "Doca cadastrada com sucesso!",
      color: "positive",
      position: "top",
    });
    resetDockForm();
    fetchAllDocks();
  } catch (error) {
    console.error("Error saving dock:", error);
    Notify.create({
      message: "Erro ao salvar a doca. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

async function updateDock() {
  try {
    saving.value = true;
    if (isEditMode.value && dock.value.dockId) {
      await axios.put(
        `http://localhost:8082/api/docks/${dock.value.dockId}`,
        dock.value
      );
      Notify.create({
        message: "Doca atualizada com sucesso!",
        color: "positive",
        position: "top",
      });
      resetDockForm();
      fetchAllDocks();
    }
  } catch (error) {
    console.error("Error updating dock:", error);
    Notify.create({
      message: "Erro ao atualizar a doca. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

async function deleteDock(dockId) {
  try {
    saving.value = true;
    await axios.delete(`http://localhost:8082/api/docks/${dockId}`);
    Notify.create({
      message: "Doca excluída com sucesso!",
      color: "positive",
      position: "top",
    });
    resetDockForm();
    fetchAllDocks();
  } catch (error) {
    console.error("Error deleting dock:", error);
    Notify.create({
      message: "Erro ao excluir a doca. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

function selectDock(dockData) {
  selectedDock.value = dockData;
  dock.value = { ...dockData };
  isEditMode.value = true;
}

function resetDockForm() {
  dock.value = {
    dockId: "",
    dockName: "",
    location: "",
    description: "",
    loadingTrailer: "",
    loadedTrailer: "",
    available: false,
    arrivalTime: "",
    departureTime: "",
    dockFree: "",
  };
  selectedDock.value = null;
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
.text-subtitle1 {
  font-weight: bold;
}
.text-caption {
  font-size: 0.875rem;
}
</style>
