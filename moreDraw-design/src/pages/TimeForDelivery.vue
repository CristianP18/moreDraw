<template>
  <q-page class="q-pa-md">
    <div class="top-container">
      <q-card class="flex-row">
        <div class="selection-container">
          <q-card-section>
            <div class="text-h6">Gerenciar Tempo de Entrega</div>

            <!-- Botão para abrir formulário de cadastro -->
            <q-btn
              :label="'Cadastrar Novo Tempo de Entrega'"
              color="primary"
              class="q-mt-md"
              @click="toggleCreateTimeForm"
            />

            <!-- Formulário para Cadastrar Novo Tempo de Entrega -->
            <q-card v-if="showCreateTimeForm" class="q-mt-md">
              <q-card-section>
                <div class="text-h6">Cadastrar Tempo de Entrega</div>
                <q-input
                  v-model="timeToDelivery.origin"
                  filled
                  label="Origem"
                  placeholder="Digite a origem"
                  class="q-mt-md"
                />
                <q-input
                  v-model="timeToDelivery.destination"
                  filled
                  label="Destino"
                  placeholder="Digite o destino"
                  class="q-mt-md"
                />
                <q-input
                  v-model="timeToDelivery.timeInMinutes"
                  filled
                  label="Tempo em Minutos"
                  placeholder="Digite o tempo em minutos"
                  type="number"
                  class="q-mt-md"
                />
                <q-btn
                  :label="'Cadastrar Tempo de Entrega'"
                  color="secondary"
                  class="q-mt-md"
                  :loading="saving"
                  @click="saveTimeToDelivery"
                >
                  <template #loading>
                    <q-spinner color="white" />
                  </template>
                </q-btn>
              </q-card-section>
            </q-card>

            <!-- Botão para listar todos os tempos de entrega -->
            <q-btn
              :label="'Listar Todos os Tempos de Entrega'"
              color="primary"
              class="q-mt-md"
              :loading="loadingAll"
              @click="fetchAllTimes"
            />

            <!-- Input para pesquisar Tempo de Entrega -->
            <q-input
              v-model="timeToDeliverId"
              filled
              label="ID do Tempo de Entrega"
              lazy-rules
              :rules="[
                (val) =>
                  (val && val.length > 0) ||
                  'O ID do Tempo de Entrega não pode ser vazio',
              ]"
              class="q-mt-md"
              @input="debouncedSearch"
            />
            <q-btn
              :label="'Buscar Tempo de Entrega'"
              color="primary"
              class="q-mt-md"
              :loading="loading"
              :disable="loading || !timeToDeliverId"
              @click="searchByTimeToDeliverId"
            >
              <template #loading>
                <q-spinner color="white" />
              </template>
            </q-btn>
          </q-card-section>
        </div>
      </q-card>

      <!-- Lista de Todos os Tempos de Entrega -->
      <q-card v-if="allTimes.length > 0" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Lista de Tempos de Entrega</div>
          <q-list bordered class="q-mt-md">
            <q-item
              v-for="time in allTimes"
              :key="time.timeToDeliverId"
              clickable
              @click="selectTime(time)"
            >
              <q-item-section>
                {{ time.origin }} - {{ time.destination }} (Tempo:
                {{ time.timeInMinutes }} min)
              </q-item-section>
              <q-item-section side>
                <q-btn
                  flat
                  round
                  icon="delete"
                  color="red"
                  @click.stop="deleteTime(time.timeToDeliverId)"
                />
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>
      </q-card>

      <!-- Exibir Tempo de Entrega Selecionado -->
      <q-card v-if="selectedTime && isEditMode" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Editar Tempo de Entrega</div>
          <q-input
            v-model="timeToDelivery.origin"
            filled
            label="Origem"
            :placeholder="selectedTime.origin || 'Origem não disponível'"
            class="q-mt-md"
          />
          <q-input
            v-model="timeToDelivery.destination"
            filled
            label="Destino"
            :placeholder="selectedTime.destination || 'Destino não disponível'"
            class="q-mt-md"
          />
          <q-input
            v-model="timeToDelivery.timeInMinutes"
            filled
            label="Tempo em Minutos"
            type="number"
            :placeholder="selectedTime.timeInMinutes || 'Tempo não disponível'"
            class="q-mt-md"
          />
          <q-btn
            :label="'Atualizar Tempo de Entrega'"
            color="secondary"
            class="q-mt-md"
            :loading="saving"
            @click="saveTimeToDelivery"
          >
            <template #loading>
              <q-spinner color="white" />
            </template>
          </q-btn>
          <q-btn
            :label="'Excluir Tempo de Entrega'"
            color="negative"
            class="q-mt-md"
            :loading="saving"
            @click="deleteTime(timeToDelivery.value.timeToDeliverId)"
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
import { ref, onMounted } from "vue";
import axios from "axios";
import debounce from "lodash/debounce";
import { Notify } from "quasar";

const timeToDeliverId = ref("");
const selectedTime = ref(null);
const showCreateTimeForm = ref(false);
const timeToDelivery = ref({
  timeToDeliverId: "", // Adicionado para garantir que o ID seja enviado corretamente na atualização
  origin: "",
  destination: "",
  timeInMinutes: 0,
});
const allTimes = ref([]);
const loading = ref(false);
const loadingAll = ref(false);
const saving = ref(false);
const isEditMode = ref(false);

const toggleCreateTimeForm = () => {
  showCreateTimeForm.value = !showCreateTimeForm.value;
};

const debouncedSearch = debounce(async () => {
  if (timeToDeliverId.value) {
    try {
      loading.value = true;
      const response = await axios.get(
        `http://localhost:8082/api/timefordeliveries/${timeToDeliverId.value}`
      );
      if (response.data) {
        selectedTime.value = response.data;
        timeToDelivery.value = { ...response.data }; // Preenche o formulário com os dados recebidos
        isEditMode.value = true;
      } else {
        selectedTime.value = null;
        Notify.create({
          message: "Nenhum tempo de entrega encontrado com este ID.",
          color: "warning",
          position: "top",
        });
      }
    } catch (error) {
      console.error("Search error:", error);
      Notify.create({
        message: "Erro ao buscar tempo de entrega. Tente novamente.",
        color: "negative",
        position: "top",
      });
    } finally {
      loading.value = false;
    }
  } else {
    selectedTime.value = null;
  }
}, 300);

function searchByTimeToDeliverId() {
  debouncedSearch.cancel();
  debouncedSearch();
}

async function fetchAllTimes() {
  try {
    loadingAll.value = true;
    const response = await axios.get(
      `http://localhost:8082/api/timefordeliveries`
    );
    allTimes.value = response.data;
  } catch (error) {
    console.error("Error fetching all times:", error);
    Notify.create({
      message: "Erro ao buscar todos os tempos de entrega. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    loadingAll.value = false;
  }
}

async function saveTimeToDelivery() {
  try {
    saving.value = true;
    if (isEditMode.value && timeToDelivery.value.timeToDeliverId) {
      await axios.put(
        `http://localhost:8082/api/timefordeliveries/${timeToDelivery.value.timeToDeliverId}`,
        timeToDelivery.value
      );
      Notify.create({
        message: "Tempo de entrega atualizado com sucesso!",
        color: "positive",
        position: "top",
      });
    } else {
      await axios.post(
        `http://localhost:8082/api/timefordeliveries`,
        timeToDelivery.value
      );
      Notify.create({
        message: "Tempo de entrega cadastrado com sucesso!",
        color: "positive",
        position: "top",
      });
    }
    resetTimeForm();
    fetchAllTimes(); // Atualiza a lista de tempos de entrega
  } catch (error) {
    console.error("Error saving time to delivery:", error);
    Notify.create({
      message: "Erro ao salvar o tempo de entrega. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

async function deleteTime(timeToDeliverId) {
  try {
    saving.value = true;
    await axios.delete(
      `http://localhost:8082/api/timefordeliveries/${timeToDeliverId}`
    );
    Notify.create({
      message: "Tempo de entrega excluído com sucesso!",
      color: "positive",
      position: "top",
    });
    resetTimeForm();
    fetchAllTimes(); // Atualiza a lista de tempos de entrega
  } catch (error) {
    console.error("Error deleting time to delivery:", error);
    Notify.create({
      message: "Erro ao excluir o tempo de entrega. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

function selectTime(timeData) {
  selectedTime.value = timeData;
  timeToDelivery.value = { ...timeData }; // Preenche o formulário com os dados do tempo de entrega selecionado
  isEditMode.value = true;
}

function resetTimeForm() {
  timeToDelivery.value = {
    timeToDeliverId: "", // Limpa o ID do tempo de entrega ao resetar o formulário
    origin: "",
    destination: "",
    timeInMinutes: 0,
  };
  selectedTime.value = null;
  isEditMode.value = false;
}

// Carrega todos os tempos de entrega ao montar o componente
onMounted(() => {
  fetchAllTimes();
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
