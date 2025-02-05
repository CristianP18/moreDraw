<template>
  <q-page class="q-pa-md">
    <div class="top-container">
      <q-card class="flex-row">
        <div class="selection-container">
          <q-card-section>
            <div class="text-h6">Gerenciar Entregas</div>

            <!-- Botão para abrir formulário de cadastro -->
            <q-btn
              :label="'Cadastrar Nova Entrega'"
              color="primary"
              class="q-mt-md"
              @click="toggleCreateDeliveryForm"
            />

            <!-- Formulário para Cadastrar Nova Entrega -->
            <q-card v-if="showCreateDeliveryForm" class="q-mt-md">
              <q-card-section>
                <div class="text-h6">Cadastrar Entrega</div>
                <q-input
                  v-model="delivery.origin"
                  filled
                  label="Origem"
                  placeholder="Digite a origem da entrega"
                  class="q-mt-md"
                />
                <q-input
                  v-model="delivery.destination"
                  filled
                  label="Destino"
                  placeholder="Digite o destino da entrega"
                  class="q-mt-md"
                />
                <q-input
                  v-model="delivery.total"
                  filled
                  type="number"
                  label="Total"
                  placeholder="Digite o total"
                  class="q-mt-md"
                />
                <q-input
                  v-model="delivery.count"
                  filled
                  type="number"
                  label="Contagem"
                  placeholder="Digite a contagem"
                  class="q-mt-md"
                />
                <q-btn
                  :label="'Cadastrar Entrega'"
                  color="secondary"
                  class="q-mt-md"
                  :loading="saving"
                  @click="saveDelivery"
                >
                  <template #loading>
                    <q-spinner color="white" />
                  </template>
                </q-btn>
              </q-card-section>
            </q-card>

            <!-- Botão para listar todas as entregas -->
            <q-btn
              :label="'Listar Todas as Entregas'"
              color="primary"
              class="q-mt-md"
              :loading="loadingAll"
              @click="fetchAllDeliveries"
            />

            <!-- Input para pesquisar Entrega -->
            <q-input
              v-model="deliveryId"
              filled
              label="ID da Entrega"
              lazy-rules
              :rules="[
                (val) =>
                  (val && val.length > 0) ||
                  'O ID da Entrega não pode ser vazio',
              ]"
              class="q-mt-md"
              @input="debouncedSearch"
            />
            <q-btn
              :label="'Buscar Entrega'"
              color="primary"
              class="q-mt-md"
              :loading="loading"
              :disable="loading || !deliveryId"
              @click="searchByDeliveryId"
            >
              <template #loading>
                <q-spinner color="white" />
              </template>
            </q-btn>
          </q-card-section>
        </div>
      </q-card>

      <!-- Lista de Todas as Entregas -->
      <q-card v-if="allDeliveries.length > 0" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Lista de Entregas</div>
          <q-list bordered class="q-mt-md">
            <q-item
              v-for="delivery in allDeliveries"
              :key="delivery.deliveryId"
              clickable
              @click="selectDelivery(delivery)"
            >
              <q-item-section>
                {{ delivery.origin }} -> {{ delivery.destination }} - ID:
                {{ delivery.deliveryId }}
              </q-item-section>
              <q-item-section side>
                <q-btn
                  flat
                  round
                  icon="delete"
                  color="red"
                  @click.stop="deleteDelivery(delivery.deliveryId)"
                />
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>
      </q-card>

      <!-- Exibir Entrega Selecionada -->
      <q-card v-if="selectedDelivery && isEditMode" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Editar Entrega</div>
          <q-input
            v-model="delivery.origin"
            filled
            label="Origem"
            :placeholder="selectedDelivery.origin || 'Origem não disponível'"
            class="q-mt-md"
          />
          <q-input
            v-model="delivery.destination"
            filled
            label="Destino"
            :placeholder="
              selectedDelivery.destination || 'Destino não disponível'
            "
            class="q-mt-md"
          />
          <q-input
            v-model="delivery.total"
            filled
            type="number"
            label="Total"
            :placeholder="selectedDelivery.total || 'Total não disponível'"
            class="q-mt-md"
          />
          <q-input
            v-model="delivery.count"
            filled
            type="number"
            label="Contagem"
            :placeholder="selectedDelivery.count || 'Contagem não disponível'"
            class="q-mt-md"
          />
          <q-btn
            :label="'Atualizar Entrega'"
            color="secondary"
            class="q-mt-md"
            :loading="saving"
            @click="saveDelivery"
          >
            <template #loading>
              <q-spinner color="white" />
            </template>
          </q-btn>
          <q-btn
            :label="'Excluir Entrega'"
            color="negative"
            class="q-mt-md"
            :loading="saving"
            @click="deleteDelivery(delivery.deliveryId)"
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

const deliveryId = ref("");
const selectedDelivery = ref(null);
const showCreateDeliveryForm = ref(false);
const delivery = ref({
  deliveryId: "", // ID para manter rastreamento
  origin: "",
  destination: "",
  total: 0,
  count: 0,
});
const allDeliveries = ref([]);
const loading = ref(false);
const loadingAll = ref(false);
const saving = ref(false);
const isEditMode = ref(false);

const toggleCreateDeliveryForm = () => {
  showCreateDeliveryForm.value = !showCreateDeliveryForm.value;
};

const debouncedSearch = debounce(async () => {
  if (deliveryId.value) {
    try {
      loading.value = true;
      const response = await axios.get(
        `http://localhost:8082/api/deliveries/${deliveryId.value}`
      );
      if (response.data) {
        selectedDelivery.value = response.data;
        delivery.value = { ...response.data }; // Preenche o formulário com os dados recebidos
        isEditMode.value = true;
      } else {
        selectedDelivery.value = null;
        Notify.create({
          message: "Nenhuma entrega encontrada com este ID.",
          color: "warning",
          position: "top",
        });
      }
    } catch (error) {
      console.error("Search error:", error);
      Notify.create({
        message: "Erro ao buscar entrega. Tente novamente.",
        color: "negative",
        position: "top",
      });
    } finally {
      loading.value = false;
    }
  } else {
    selectedDelivery.value = null;
  }
}, 300);

function searchByDeliveryId() {
  debouncedSearch.cancel();
  debouncedSearch();
}

async function fetchAllDeliveries() {
  try {
    loadingAll.value = true;
    const response = await axios.get(`http://localhost:8082/api/deliveries`);
    allDeliveries.value = response.data;
  } catch (error) {
    console.error("Error fetching all deliveries:", error);
    Notify.create({
      message: "Erro ao buscar todas as entregas. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    loadingAll.value = false;
  }
}

async function saveDelivery() {
  try {
    saving.value = true;
    if (isEditMode.value && delivery.value.deliveryId) {
      await axios.put(
        `http://localhost:8082/api/deliveries/${delivery.value.deliveryId}`,
        delivery.value
      );
      Notify.create({
        message: "Entrega atualizada com sucesso!",
        color: "positive",
        position: "top",
      });
    } else {
      await axios.post(`http://localhost:8082/api/deliveries`, delivery.value);
      Notify.create({
        message: "Entrega cadastrada com sucesso!",
        color: "positive",
        position: "top",
      });
    }
    resetDeliveryForm();
    fetchAllDeliveries(); // Atualiza a lista de entregas
  } catch (error) {
    console.error("Error saving delivery:", error);
    Notify.create({
      message: "Erro ao salvar a entrega. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

async function deleteDelivery(deliveryId) {
  try {
    saving.value = true;
    await axios;

    await axios.delete(`http://localhost:8082/api/deliveries/${deliveryId}`);
    Notify.create({
      message: "Entrega excluída com sucesso!",
      color: "positive",
      position: "top",
    });
    resetDeliveryForm();
    fetchAllDeliveries(); // Atualiza a lista de entregas
  } catch (error) {
    console.error("Error deleting delivery:", error);
    Notify.create({
      message: "Erro ao excluir a entrega. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

function selectDelivery(deliveryData) {
  selectedDelivery.value = deliveryData;
  delivery.value = { ...deliveryData }; // Preenche o formulário com os dados da entrega selecionada
  isEditMode.value = true;
}

function resetDeliveryForm() {
  delivery.value = {
    deliveryId: "", // Limpa o ID da entrega ao resetar o formulário
    origin: "",
    destination: "",
    total: 0,
    count: 0,
  };
  selectedDelivery.value = null;
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
