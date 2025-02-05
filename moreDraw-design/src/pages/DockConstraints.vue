<template>
  <q-page class="q-pa-md">
    <div class="top-container">
      <q-card class="flex-row">
        <div class="selection-container">
          <q-card-section>
            <div class="text-h6">Gerenciamento de Restrições de Doca</div>

            <!-- Input para ID de busca -->
            <q-input
              v-model="constraintId"
              filled
              label="ID da Restrição de Doca"
              lazy-rules
              :rules="[
                (val) => (val && val.length > 0) || 'Informe um ID válido',
              ]"
            />

            <!-- Botão para buscar restrição por ID -->
            <q-btn
              label="Buscar Restrição"
              color="primary"
              class="q-mt-md"
              :loading="loading"
              :disable="loading || !constraintId"
              @click="fetchDockConstraint"
            >
              <template #loading>
                <q-spinner color="white" />
              </template>
            </q-btn>

            <!-- Botão para listar todas as restrições -->
            <q-btn
              label="Listar Todas Restrições"
              color="secondary"
              class="q-mt-md"
              @click="fetchAllDockConstraints"
            />

            <!-- Botão para criar nova restrição -->
            <q-btn
              label="Nova Restrição"
              color="positive"
              class="q-mt-md"
              @click="openCreateModal"
            />

            <!-- Tabela para exibir restrições -->
            <q-card v-if="dockConstraints.length > 0" class="q-mt-md">
              <q-card-section>
                <div class="text-h6">Lista de Restrições de Doca</div>
                <q-table
                  :rows="dockConstraints"
                  :columns="columns"
                  row-key="dockConstraintId"
                >
                  <template v-slot:body-cell-action="props">
                    <q-td align="center">
                      <q-btn
                        icon="edit"
                        color="primary"
                        @click="editDockConstraint(props.row)"
                      />
                      <q-btn
                        icon="delete"
                        color="negative"
                        @click="
                          deleteDockConstraint(props.row.dockConstraintId)
                        "
                      />
                    </q-td>
                  </template>
                </q-table>
              </q-card-section>
            </q-card>
          </q-card-section>
        </div>
      </q-card>
    </div>

    <!-- Modal para criar/editar restrição -->
    <q-dialog v-model="showModal">
      <q-card style="min-width: 400px">
        <q-card-section>
          <div class="text-h6">
            {{ editMode ? "Editar" : "Nova" }} Restrição de Doca
          </div>
        </q-card-section>
        <q-card-section>
          <q-input
            v-model="form.dockConstraint"
            label="Nome da Restrição"
            filled
          />
          <q-input
            v-model="form.dateBegin"
            label="Hora de Início"
            filled
            type="time"
          />
          <q-input
            v-model="form.dateEnd"
            label="Hora de Fim"
            filled
            type="time"
          />
          <q-toggle v-model="form.type" label="Tipo" />
          <q-toggle v-model="form.hardConstraints" label="Hard Constraints" />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="Cancelar" color="negative" v-close-popup />
          <q-btn
            flat
            label="Salvar"
            color="primary"
            @click="saveDockConstraint"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { Notify } from "quasar";

const dockConstraints = ref([]);
const constraintId = ref("");
const loading = ref(false);
const showModal = ref(false);
const editMode = ref(false);
const form = ref({
  dockConstraintId: "",
  dockConstraint: "",
  dateBegin: "",
  dateEnd: "",
  type: false,
  hardConstraints: false,
});
const selectedId = ref(null); // Variável reativa para armazenar o ID selecionado

const columns = [
  {
    name: "dockConstraintId",
    label: "ID",
    field: "dockConstraintId",
    align: "left",
  },
  {
    name: "dockConstraint",
    label: "Restrição",
    field: "dockConstraint",
    align: "left",
  },
  {
    name: "dateBegin",
    label: "Hora de Início",
    field: "dateBegin",
    align: "left",
  },
  { name: "dateEnd", label: "Hora de Fim", field: "dateEnd", align: "left" },
  {
    name: "type",
    label: "Tipo",
    field: "type",
    align: "left",
    format: (val) => (val ? "Sim" : "Não"),
  },
  {
    name: "hardConstraints",
    label: "Hard Constraint",
    field: "hardConstraints",
    align: "left",
    format: (val) => (val ? "Sim" : "Não"),
  },
  { name: "action", label: "Ações", align: "center" },
];

// Fetch all constraints on load
onMounted(fetchAllDockConstraints);

// Fetch all constraints
async function fetchAllDockConstraints() {
  loading.value = true;
  try {
    const response = await axios.get("http://localhost:8082/dock-constraints");
    dockConstraints.value = response.data;
  } catch (error) {
    Notify.create({
      message: "Erro ao carregar restrições",
      color: "negative",
    });
  } finally {
    loading.value = false;
  }
}

// Fetch single constraint by ID
async function fetchDockConstraint() {
  loading.value = true;
  try {
    const response = await axios.get(
      `http://localhost:8082/dock-constraints/${constraintId.value}`
    );
    dockConstraints.value = [response.data];
  } catch (error) {
    Notify.create({ message: "Restrições não encontradas", color: "negative" });
  } finally {
    loading.value = false;
  }
}

// Open modal to create a new constraint
function openCreateModal() {
  editMode.value = false;
  showModal.value = true;
  form.value = {
    dockConstraintId: "",
    dockConstraint: "",
    dateBegin: "",
    dateEnd: "",
    type: false,
    hardConstraints: false,
  };
}

// Save constraint (create or update)
async function saveDockConstraint() {
  try {
    if (editMode.value) {
      await axios.put(
        `http://localhost:8082/dock-constraints/${form.value.dockConstraintId}`,
        form.value
      );
      Notify.create({
        message: "Restrição atualizada com sucesso",
        color: "positive",
      });
    } else {
      await axios.post("http://localhost:8082/dock-constraints", form.value);
      Notify.create({
        message: "Restrição criada com sucesso",
        color: "positive",
      });
    }
    await fetchAllDockConstraints();
    showModal.value = false;
  } catch (error) {
    Notify.create({ message: "Erro ao salvar a restrição", color: "negative" });
  }
}

// Edit constraint
function editDockConstraint(constraint) {
  editMode.value = true;
  form.value = { ...constraint };
  showModal.value = true;
}

// Delete constraint
async function deleteDockConstraint(id) {
  try {
    await axios.delete(`http://localhost:8082/dock-constraints/${id}`);
    Notify.create({
      message: "Restrição excluída com sucesso",
      color: "positive",
    });
    await fetchAllDockConstraints();
  } catch (error) {
    Notify.create({
      message: "Erro ao excluir a restrição",
      color: "negative",
    });
  }
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
