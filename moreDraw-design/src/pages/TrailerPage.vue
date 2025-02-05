<template>
  <q-page class="q-pa-md">
    <div class="top-container">
      <q-card class="flex-row">
        <div class="selection-container">
          <q-card-section>
            <div class="text-h6">Gerenciar Carretas</div>

            <!-- Botão para abrir formulário de cadastro -->
            <q-btn
              :label="'Cadastrar Novo Trailer'"
              color="primary"
              class="q-mt-md"
              @click="toggleCreateTrailerForm"
            />

            <!-- Formulário para Cadastrar Novo Trailer -->
            <q-card v-if="showCreateTrailerForm" class="q-mt-md">
              <q-card-section>
                <div class="text-h6">Cadastrar Trailer</div>
                <q-input
                  v-model="trailer.licensePlate"
                  filled
                  label="Placa do Trailer"
                  placeholder="Digite a placa do trailer"
                  class="q-mt-md"
                />
                <q-input
                  v-model="trailer.type"
                  filled
                  label="Tipo"
                  placeholder="Digite o tipo do trailer"
                  class="q-mt-md"
                />
                <q-input
                  v-model="trailer.description"
                  filled
                  label="Descrição"
                  placeholder="Digite a descrição"
                  class="q-mt-md"
                />
                <q-input
                  v-model="trailer.boarding"
                  filled
                  label="Embarque"
                  placeholder="Digite o embarque"
                  class="q-mt-md"
                />
                <q-input
                  v-model="trailer.truckId"
                  filled
                  label="ID do Caminhão"
                  placeholder="Digite o ID do caminhão"
                  class="q-mt-md"
                />
                <q-input
                  v-model="trailer.dockStart"
                  filled
                  label="Início na Doca"
                  placeholder="Digite o início na doca"
                  class="q-mt-md"
                />
                <q-input
                  v-model="trailer.modefield"
                  filled
                  type="datetime-local"
                  label="Campo de Modo"
                  placeholder="Selecione a data e hora"
                  class="q-mt-md"
                />
                <q-input
                  v-model="trailer.deliveryToFree"
                  filled
                  type="datetime-local"
                  label="Entrega para Liberar"
                  placeholder="Selecione a data e hora"
                  class="q-mt-md"
                />
                <q-toggle
                  v-model="trailer.inTransit"
                  label="Em Trânsito"
                  class="q-mt-md"
                />
                <q-input
                  v-model="trailer.destinationDock"
                  filled
                  label="ID da Doca de Destino"
                  placeholder="Digite o ID da doca de destino"
                  type="number"
                  class="q-mt-md"
                />
                <q-btn
                  :label="'Cadastrar Trailer'"
                  color="secondary"
                  class="q-mt-md"
                  :loading="saving"
                  @click="saveTrailer"
                >
                  <template #loading>
                    <q-spinner color="white" />
                  </template>
                </q-btn>
              </q-card-section>
            </q-card>

            <!-- Botão para listar todos os trailers -->
            <q-btn
              :label="'Listar Todos os Trailers'"
              color="primary"
              class="q-mt-md"
              :loading="loadingAll"
              @click="fetchAllTrailers"
            />

            <!-- Input para pesquisar Trailer -->
            <q-input
              v-model="trailerId"
              filled
              label="ID do Trailer"
              lazy-rules
              :rules="[
                (val) =>
                  (val && val.length > 0) ||
                  'O ID do Trailer não pode ser vazio',
              ]"
              class="q-mt-md"
              @input="debouncedSearch"
            />
            <q-btn
              :label="'Buscar Trailer'"
              color="primary"
              class="q-mt-md"
              :loading="loading"
              :disable="loading || !trailerId"
              @click="searchByTrailerId"
            >
              <template #loading>
                <q-spinner color="white" />
              </template>
            </q-btn>
          </q-card-section>
        </div>
      </q-card>

      <!-- Lista de Todos os Trailers -->
      <q-card v-if="allTrailers.length > 0" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Lista de Trailers</div>
          <q-list bordered class="q-mt-md">
            <q-item
              v-for="trailer in allTrailers"
              :key="trailer.trailerId"
              clickable
              @click="selectTrailer(trailer)"
            >
              <q-item-section>
                {{ trailer.licensePlate }} - ID: {{ trailer.trailerId }}
              </q-item-section>
              <q-item-section side>
                <q-btn
                  flat
                  round
                  icon="delete"
                  color="red"
                  @click.stop="deleteTrailer(trailer.trailerId)"
                />
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>
      </q-card>

      <!-- Exibir Trailer Selecionado -->
      <q-card v-if="selectedTrailer && isEditMode" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Editar Trailer</div>
          <q-input
            v-model="trailer.licensePlate"
            filled
            label="Placa do Trailer"
            :placeholder="
              selectedTrailer.licensePlate || 'Placa não disponível'
            "
            class="q-mt-md"
          />
          <q-input
            v-model="trailer.type"
            filled
            label="Tipo"
            :placeholder="selectedTrailer.type || 'N/A'"
            class="q-mt-md"
          />
          <q-input
            v-model="trailer.description"
            filled
            label="Descrição"
            :placeholder="selectedTrailer.description || 'N/A'"
            class="q-mt-md"
          />
          <q-input
            v-model="trailer.boarding"
            filled
            label="Embarque"
            :placeholder="selectedTrailer.boarding || 'N/A'"
            class="q-mt-md"
          />
          <q-input
            v-model="trailer.truck"
            filled
            label="Placa do Caminhão"
            :placeholder="selectedTrailer.truck || 'N/A'"
            class="q-mt-md"
          />
          <q-input
            v-model="trailer.dockStart"
            filled
            label="Início na Doca"
            :placeholder="selectedTrailer.dockStart || 'N/A'"
            class="q-mt-md"
          />
          <q-input
            v-model="trailer.modefield"
            filled
            type="datetime-local"
            label="Atualizado na data: "
            :placeholder="selectedTrailer.modefield || 'N/A'"
            class="q-mt-md"
          />
          <q-input
            v-model="trailer.deliveryToFree"
            filled
            type="datetime-local"
            label="Entrega para Liberar"
            :placeholder="selectedTrailer.deliveryToFree || 'N/A'"
            class="q-mt-md"
          />
          <q-toggle
            v-model="trailer.inTransit"
            label="Em Trânsito"
            :checked="selectedTrailer.inTransit"
            class="q-mt-md"
          />
          <q-input
            v-model="trailer.destinationDock"
            filled
            label="ID da Doca de Destino"
            :placeholder="selectedTrailer.destinationDock || 'N/A'"
            type="number"
            class="q-mt-md"
          />
          <q-btn
            :label="'Atualizar Trailer'"
            color="secondary"
            class="q-mt-md"
            :loading="saving"
            @click="saveTrailer"
          >
            <template #loading>
              <q-spinner color="white" />
            </template>
          </q-btn>
          <q-btn
            :label="'Excluir Trailer'"
            color="negative"
            class="q-mt-md"
            :loading="saving"
            @click="deleteTrailer(trailer.value.trailerId)"
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

const trailerId = ref("");
const selectedTrailer = ref(null);
const showCreateTrailerForm = ref(false);
const trailer = ref({
  trailerId: "", // Adicionado para garantir que o ID seja enviado corretamente na atualização
  licensePlate: "",
  type: "",
  description: "",
  boarding: "",
  truckId: "",
  dockStart: "",
  modefield: "", // Campo para datetime-local
  deliveryToFree: "", // Campo para datetime-local
  inTransit: false,
  destinationDock: null, // ID da doca de destino como bigint
});
const allTrailers = ref([]);
const loading = ref(false);
const loadingAll = ref(false);
const saving = ref(false);
const isEditMode = ref(false);

const toggleCreateTrailerForm = () => {
  showCreateTrailerForm.value = !showCreateTrailerForm.value;
};

const debouncedSearch = debounce(async () => {
  if (trailerId.value) {
    try {
      loading.value = true;
      const response = await axios.get(
        `http://localhost:8082/api/trailers/${trailerId.value}`
      );
      if (response.data) {
        selectedTrailer.value = response.data;
        trailer.value = { ...response.data }; // Preenche o formulário com os dados recebidos
        isEditMode.value = true;
      } else {
        selectedTrailer.value = null;
        Notify.create({
          message: "Nenhum trailer encontrado com este ID.",
          color: "warning",
          position: "top",
        });
      }
    } catch (error) {
      console.error("Search error:", error);
      Notify.create({
        message: "Erro ao buscar trailer. Tente novamente.",
        color: "negative",
        position: "top",
      });
    } finally {
      loading.value = false;
    }
  } else {
    selectedTrailer.value = null;
  }
}, 300);

function searchByTrailerId() {
  debouncedSearch.cancel();
  debouncedSearch();
}

async function fetchAllTrailers() {
  try {
    loadingAll.value = true;
    const response = await axios.get(`http://localhost:8082/api/trailers`);
    allTrailers.value = response.data;
  } catch (error) {
    console.error("Error fetching all trailers:", error);
    Notify.create({
      message: "Erro ao buscar todos os trailers. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    loadingAll.value = false;
  }
}

async function saveTrailer() {
  try {
    saving.value = true;
    if (isEditMode.value && trailer.value.trailerId) {
      await axios.put(
        `http://localhost:8082/api/trailers/${trailer.value.trailerId}`,
        trailer.value
      );
      Notify.create({
        message: "Trailer atualizado com sucesso!",
        color: "positive",
        position: "top",
      });
    } else {
      await axios.post(`http://localhost:8082/api/trailers`, trailer.value);
      Notify.create({
        message: "Trailer cadastrado com sucesso!",
        color: "positive",
        position: "top",
      });
    }
    resetTrailerForm();
    fetchAllTrailers(); // Atualiza a lista de trailers
  } catch (error) {
    console.error("Error saving trailer:", error);
    Notify.create({
      message: "Erro ao salvar o trailer. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

async function deleteTrailer(trailerId) {
  try {
    saving.value = true;
    await axios.delete(`http://localhost:8082/api/trailers/${trailerId}`);
    Notify.create({
      message: "Trailer excluído com sucesso!",
      color: "positive",
      position: "top",
    });
    resetTrailerForm();
    fetchAllTrailers(); // Atualiza a lista de trailers
  } catch (error) {
    console.error("Error deleting trailer:", error);
    Notify.create({
      message: "Erro ao excluir o trailer. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

function selectTrailer(trailerData) {
  selectedTrailer.value = trailerData;
  trailer.value = { ...trailerData }; // Preenche o formulário com os dados do trailer selecionado
  isEditMode.value = true;
}

function resetTrailerForm() {
  trailer.value = {
    trailerId: "", // Limpa o ID do trailer ao resetar o formulário
    licensePlate: "",
    type: "",
    description: "",
    boarding: "",
    truckId: "",
    dockStart: "",
    modefield: "",
    deliveryToFree: "",
    inTransit: false,
    destinationDock: null, // ID da doca de destino como bigint
  };
  selectedTrailer.value = null;
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
