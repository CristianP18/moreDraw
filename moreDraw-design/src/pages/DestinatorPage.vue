<template>
  <q-page class="q-pa-md">
    <div class="top-container">
      <q-card class="flex-row">
        <div class="selection-container">
          <q-card-section>
            <div class="text-h6">Gerenciar Destinatários</div>

            <!-- Botão para abrir formulário de cadastro -->
            <q-btn
              :label="'Cadastrar Novo Destinatário'"
              color="primary"
              class="q-mt-md"
              @click="toggleCreateDestinatorForm"
            />

            <!-- Formulário para Cadastrar Novo Destinatário -->
            <q-card v-if="showCreateDestinatorForm" class="q-mt-md">
              <q-card-section>
                <div class="text-h6">Cadastrar Destinatário</div>
                <q-input
                  v-model="destinator.name"
                  filled
                  label="Nome do Destinatário"
                  class="q-mt-md"
                />
                <q-input
                  v-model="destinator.location"
                  filled
                  label="Localização"
                  class="q-mt-md"
                />
                <q-input
                  v-model="destinator.description"
                  filled
                  label="Descrição"
                  class="q-mt-md"
                />
                <q-input
                  v-model="destinator.receive"
                  filled
                  label="Total de janelas ao dia"
                  class="q-mt-md"
                />
                <q-btn
                  :label="'Cadastrar Destinatário'"
                  color="secondary"
                  class="q-mt-md"
                  :loading="saving"
                  @click="saveDestinator"
                >
                  <template #loading>
                    <q-spinner color="white" />
                  </template>
                </q-btn>
              </q-card-section>
            </q-card>

            <!-- Botão para listar todos os destinatários -->
            <q-btn
              :label="'Listar Todos os Destinatários'"
              color="primary"
              class="q-mt-md"
              :loading="loadingAll"
              @click="fetchAllDestinators"
            />

            <!-- Input para pesquisar Destinatário -->
            <q-input
              v-model="destinatorName"
              filled
              label="Nome do Destinatário"
              lazy-rules
              :rules="[
                (val) => (val && val.length > 0) || 'O Nome não pode ser vazio',
              ]"
              class="q-mt-md"
              @input="debouncedSearch"
            />
            <q-btn
              :label="'Buscar Destinatário'"
              color="primary"
              class="q-mt-md"
              :loading="loading"
              :disable="loading || !destinatorName"
              @click="searchByDestinatorName"
            >
              <template #loading>
                <q-spinner color="white" />
              </template>
            </q-btn>
          </q-card-section>
        </div>
      </q-card>

      <!-- Lista de Todos os Destinatários -->
      <q-card v-if="allDestinators.length > 0" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Lista de Destinatários</div>
          <q-list bordered class="q-mt-md">
            <q-item
              v-for="destinator in allDestinators"
              :key="destinator.id"
              clickable
              @click="selectDestinator(destinator)"
            >
              <q-item-section>
                {{ destinator.name }} - Localização: {{ destinator.location }}
              </q-item-section>
              <q-item-section side>
                <q-btn
                  flat
                  round
                  icon="delete"
                  color="red"
                  @click.stop="deleteDestinator(destinator.id)"
                />
              </q-item-section>
            </q-item>
          </q-list>
        </q-card-section>
      </q-card>

      <!-- Exibir Destinatário Selecionado -->
      <q-card v-if="selectedDestinator" class="q-mt-md">
        <q-card-section>
          <div class="text-h6">Editar Destinatário</div>
          <q-input
            v-model="destinator.name"
            filled
            label="Nome do Destinatário"
            class="q-mt-md"
          />
          <q-input
            v-model="destinator.location"
            filled
            label="Localização"
            class="q-mt-md"
          />
          <q-input
            v-model="destinator.description"
            filled
            label="Descrição"
            class="q-mt-md"
          />
          <q-btn
            :label="'Atualizar Destinatário'"
            color="secondary"
            class="q-mt-md"
            :loading="saving"
            @click="saveDestinator"
          >
            <template #loading>
              <q-spinner color="white" />
            </template>
          </q-btn>
          <q-btn
            :label="'Excluir Destinatário'"
            color="negative"
            class="q-mt-md"
            :loading="saving"
            @click="deleteDestinator(destinator.value.id)"
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

const destinatorName = ref("");
const selectedDestinator = ref(null);
const showCreateDestinatorForm = ref(false);
const destinator = ref({
  id: null,
  name: "",
  location: "",
  description: "",
});
const allDestinators = ref([]);
const loading = ref(false);
const loadingAll = ref(false);
const saving = ref(false);
const isEditMode = ref(false);

const toggleCreateDestinatorForm = () => {
  showCreateDestinatorForm.value = !showCreateDestinatorForm.value;
};

const debouncedSearch = debounce(async () => {
  if (destinatorName.value) {
    try {
      loading.value = true;
      const response = await axios.get(
        `http://localhost:8082/api/destinators/findByName/${destinatorName.value}`
      );
      if (response.data) {
        selectedDestinator.value = response.data;
        destinator.value = { ...response.data }; // Garante que o ID também seja copiado
        isEditMode.value = true;
      } else {
        selectedDestinator.value = null;
        Notify.create({
          message: "Nenhum destinatário encontrado com este nome.",
          color: "warning",
          position: "top",
        });
      }
    } catch (error) {
      console.error("Search error:", error);
      Notify.create({
        message: "Erro ao buscar destinatário. Tente novamente.",
        color: "negative",
        position: "top",
      });
    } finally {
      loading.value = false;
    }
  } else {
    selectedDestinator.value = null;
  }
}, 300);

function searchByDestinatorName() {
  debouncedSearch.cancel();
  debouncedSearch();
}

async function fetchAllDestinators() {
  try {
    loadingAll.value = true;
    const response = await axios.get(`http://localhost:8082/api/destinators`);
    allDestinators.value = response.data;
  } catch (error) {
    console.error("Error fetching all destinatários:", error);
    Notify.create({
      message: "Erro ao buscar todos os destinatários. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    loadingAll.value = false;
  }
}

async function saveDestinator() {
  try {
    saving.value = true;
    if (isEditMode.value) {
      // Garante que o ID seja passado como parte da URL na requisição PUT
      await axios.put(
        `http://localhost:8082/api/destinators/${destinator.value.id}`,
        destinator.value
      );
      Notify.create({
        message: "Destinatário atualizado com sucesso!",
        color: "positive",
        position: "top",
      });
    } else {
      await axios.post(
        `http://localhost:8082/api/destinators`,
        destinator.value
      );
      Notify.create({
        message: "Destinatário cadastrado com sucesso!",
        color: "positive",
        position: "top",
      });
    }
    resetDestinatorForm();
    fetchAllDestinators(); // Atualiza a lista de destinatários
  } catch (error) {
    console.error("Error saving destinatário:", error);
    Notify.create({
      message: "Erro ao salvar o destinatário. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

async function deleteDestinator(destinatorId) {
  try {
    saving.value = true;
    await axios.delete(`http://localhost:8082/api/destinators/${destinatorId}`);
    Notify.create({
      message: "Destinatário excluído com sucesso!",
      color: "positive",
      position: "top",
    });
    resetDestinatorForm();
    fetchAllDestinators(); // Atualiza a lista de destinatários
  } catch (error) {
    console.error("Error deleting destinatário:", error);
    Notify.create({
      message: "Erro ao excluir o destinatário. Tente novamente.",
      color: "negative",
      position: "top",
    });
  } finally {
    saving.value = false;
  }
}

function selectDestinator(destinatorData) {
  selectedDestinator.value = destinatorData;
  destinator.value = { ...destinatorData }; // Preenche o formulário com os dados do destinatário selecionado, incluindo o ID
  isEditMode.value = true;
}

function resetDestinatorForm() {
  destinator.value = {
    id: null,
    name: "",
    location: "",
    description: "",
  };
  selectedDestinator.value = null;
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
