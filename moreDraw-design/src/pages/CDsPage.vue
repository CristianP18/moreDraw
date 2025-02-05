<template>
  <q-page class="q-pa-md">
    <div class="top-container">
      <q-card class="flex-row">
        <div class="selection-container">
          <q-card-section>
            <div class="text-h6">Gerenciar CDs</div>

            <!-- Botão para abrir formulário de cadastro -->
            <q-btn
              :label="'Cadastrar Novo CD'"
              color="primary"
              class="q-mt-md"
              @click="toggleCreateCDForm"
            />

            <!-- Formulário para Cadastrar Novo CD -->
            <q-card v-if="showCreateCDForm" class="q-mt-md">
              <q-card-section>
                <div class="text-h6">Cadastrar CD</div>
                <q-input
                  v-model="cd.name"
                  filled
                  label="Nome do CD"
                  class="q-mt-md"
                />
                <q-input
                  v-model="cd.location"
                  filled
                  label="Localização"
                  class="q-mt-md"
                />
                <q-input
                  v-model="cd.description"
                  filled
                  label="Descrição"
                  class="q-mt-md"
                />
                <q-input
                  v-model="cd.capacity"
                  filled
                  label="Capacidade de envio"
                  class="q-mt-md"
                />
                <q-input
                  v-model="cd.receive"
                  filled
                  label="Capacidade de recebimento"
                  class="q-mt-md"
                />
                <q-btn
                  :label="'Cadastrar CD'"
                  color="secondary"
                  class="q-mt-md"
                  :loading="saving"
                  @click="saveCD"
                >
                  <template #loading>
                    <q-spinner color="white" />
                  </template>
                </q-btn>
              </q-card-section>
            </q-card>

            <!-- Botão para listar todos os CDs -->
            <q-btn
              :label="'Listar Todos os CDs'"
              color="primary"
              class="q-mt-md"
              :loading="loadingAll"
              @click="fetchAllCDs"
            />

            <!-- Input para pesquisar CD -->
            <q-input
              v-model="cdName"
              filled
              label="Nome do CD"
              lazy-rules
              :rules="[
                (val) => (val && val.length > 0) || 'O Nome não pode ser vazio',
              ]"
              class="q-mt-md"
              @input="debouncedSearch"
            />
            <q-btn
              :label="'Buscar CD'"
              color="primary"
              class="q-mt-md"
              :loading="loading"
              :disable="loading || !cdName"
              @click="searchByCDName"
            >
              <template #loading>
                <q-spinner color="white" />
              </template>
            </q-btn>
          </q-card-section>
        </div>
      </q-card>

      <!-- Lista de Todos os CDs -->
      <q-card v-if="allCDs.length > 0" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Lista de CDs</div>
          <q-list bordered class="q-mt-md">
            <q-item
              v-for="cd in allCDs"
              :key="cd.id"
              clickable
              @click="selectCD(cd)"
            >
              <q-item-section>
                {{ cd.name }} - Localização: {{ cd.location }}
              </q-item-section>
              <q-item-section side>
                <q-btn
                  flat
                  round
                  icon="delete"
                  color="red"
                  @click.stop="deleteCD(cd.id)"
                />
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>
      </q-card>

      <!-- Exibir CD Selecionado -->
      <q-card v-if="selectedCD" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Editar CD</div>
          <q-input
            v-model="cd.name"
            filled
            label="Nome do CD"
            class="q-mt-md"
          />
          <q-input
            v-model="cd.location"
            filled
            label="Localização"
            class="q-mt-md"
          />
          <q-input
            v-model="cd.description"
            filled
            label="Descrição"
            class="q-mt-md"
          />
          <q-input
            v-model="cd.capacity"
            filled
            label="Capacidade de envio"
            class="q-mt-md"
          />
          <q-input
            v-model="cd.receive"
            filled
            label="Capacidade de recebimento"
            class="q-mt-md"
          />
          <q-btn
            :label="'Atualizar CD'"
            color="secondary"
            class="q-mt-md"
            :loading="saving"
            @click="saveCD"
          >
            <template #loading>
              <q-spinner color="white" />
            </template>
          </q-btn>
          <q-btn
            :label="'Excluir CD'"
            color="negative"
            class="q-mt-md"
            :loading="saving"
            @click="deleteCD(selectedCD.id)"
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

const cdName = ref("");
const selectedCD = ref(null);
const showCreateCDForm = ref(false);
const cd = ref({
  name: "",
  location: "",
  description: "",
});
const allCDs = ref([]);
const loading = ref(false);
const loadingAll = ref(false);
const saving = ref(false);
const isEditMode = ref(false);

const toggleCreateCDForm = () => {
  showCreateCDForm.value = !showCreateCDForm.value;
};

const debouncedSearch = debounce(async () => {
  if (cdName.value) {
    try {
      loading.value = true;
      const response = await axios.get(
        `http://localhost:8082/api/cds/findByName/${cdName.value}`
      );
      if (response.data) {
        selectedCD.value = response.data;
        cd.value = { ...response.data }; // Preenche o formulário com os dados recebidos
        isEditMode.value = true;
      } else {
        selectedCD.value = null;
        Notify.create({
          message: "Nenhum CD encontrado com este nome.",
          color: "warning",
          position: "top",
        });
      }
    } catch (error) {
      console.error("Search error:", error);
      Notify.create({
        message: "Erro ao buscar CD. Tente novamente.",
        color: "negative",
        position: "top",
      });
    } finally {
      loading.value = false;
    }
  } else {
    selectedCD.value = null;
  }
}, 300);

function searchByCDName() {
  debouncedSearch.cancel();
  debouncedSearch();
}

async function fetchAllCDs() {
  try {
    loadingAll.value = true;
    const response = await axios.get(`http://localhost:8082/api/cds`);
    allCDs.value = response.data;
  } catch (error) {
    console.error("Error fetching all CDs:", error);
    Notify.create({
      message: "Erro ao buscar todos os CDs. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    loadingAll.value = false;
  }
}

async function saveCD() {
  try {
    saving.value = true;
    if (isEditMode.value) {
      await axios.put(
        `http://localhost:8082/api/cds/${cd.value.cdId}`,
        cd.value
      );
      Notify.create({
        message: "CD atualizado com sucesso!",
        color: "positive",
        position: "top",
      });
    } else {
      await axios.post(`http://localhost:8082/api/cds`, cd.value);
      Notify.create({
        message: "CD cadastrado com sucesso!",
        color: "positive",
        position: "top",
      });
    }
    resetCDForm();
    fetchAllCDs(); // Atualiza a lista de CDs
  } catch (error) {
    console.error("Error saving CD:", error);
    Notify.create({
      message: "Erro ao salvar o CD. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

async function deleteCD() {
  try {
    saving.value = true;
    await axios.delete(`http://localhost:8082/api/cds/${cd.value.cdId}`);
    Notify.create({
      message: "CD excluído com sucesso!",
      color: "positive",
      position: "top",
    });
    resetCDForm();
    fetchAllCDs();
  } catch (error) {
    console.error("Error deleting CD:", error);
    Notify.create({
      message: "Erro ao excluir o CD. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

function selectCD(cdData) {
  selectedCD.value = cdData;
  cd.value = { ...cdData }; // Preenche o formulário com os dados do CD selecionado
  isEditMode.value = true;
}

function resetCDForm() {
  cd.value = {
    name: "",
    location: "",
    description: "",
  };
  selectedCD.value = null;
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
