<template>
  <q-page class="page q-px-lg">
    <!-- Barra de navegação -->
    <q-toolbar class="q-px-md q-py-sm bg-primary text-white">
      <q-toolbar-title
        >Yard Controller - Gerenciamento de Pátio</q-toolbar-title
      >

      <!-- Botões de ações principais -->
      <q-btn
        flat
        icon="mdi-truck-check"
        label="Check-In"
        @click="openCheckInDialog"
      />
      <q-btn
        flat
        icon="mdi-truck-check-outline"
        label="Check-Out"
        @click="openCheckOutDialog"
      />
      <q-btn
        flat
        icon="mdi-calendar-clock"
        label="Eventos Agendados"
        @click="openSchedulingDialog"
      />
      <q-btn
        flat
        icon="mdi-map"
        label="Gerenciar Pátio"
        @click="showManagementView = 'patio'"
      />
    </q-toolbar>

    <!-- Gerenciamento de Pátio -->
    <div class="q-mt-md">
      <!-- Renderização condicional dos componentes baseados na seleção -->
      <div v-if="showManagementView === 'patio'">
        <maps />
      </div>
      <div v-if="showManagementView === 'checkin'">
        <CheckInTable />
      </div>
      <div v-if="showManagementView === 'checkout'">
        <CheckOutTable />
      </div>
      <div v-if="showManagementView === 'eventos'">
        <ScheduledEventsTable />
      </div>
    </div>

    <!-- Check-In Dialog -->
    <q-dialog v-model="showCheckInDialog">
      <q-card>
        <q-card-section>
          <div class="text-h6">Check-In de Veículo</div>
        </q-card-section>

        <q-card-section>
          <q-input
            v-model="checkInData.driver"
            label="Nome do Motorista"
            outlined
          />
          <q-input
            v-model="checkInData.driverFederalTax"
            label="CPF do Motorista"
            outlined
          />
          <q-input
            v-model="checkInData.licensePlate"
            label="Placa do Veículo"
            outlined
          />
          <q-input
            v-model="checkInData.boardingType"
            label="Tipo de Carga"
            outlined
          />
          <q-file
            v-model="checkInData.vehiclePicture"
            label="Foto do Veículo"
            outlined
          />
          <q-file
            v-model="checkInData.driverPicture"
            label="Foto do Motorista"
            outlined
          />
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancelar" @click="showCheckInDialog = false" />
          <q-btn
            flat
            label="Confirmar"
            color="primary"
            @click="confirmCheckIn"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- Check-Out Dialog -->
    <q-dialog v-model="showCheckOutDialog">
      <q-card>
        <q-card-section>
          <div class="text-h6">Check-Out de Veículo</div>
        </q-card-section>

        <q-card-section>
          <q-input
            v-model="checkOutData.exitInspection"
            label="Detalhes da Inspeção"
            outlined
          />
          <q-file
            v-model="checkOutData.releaseDocuments"
            label="Documentação de Saída"
            outlined
          />
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancelar" @click="showCheckOutDialog = false" />
          <q-btn
            flat
            label="Confirmar"
            color="primary"
            @click="confirmCheckOut"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- Evento de Agendamento Dialog -->
    <q-dialog v-model="showSchedulingDialog">
      <q-card>
        <q-card-section>
          <div class="text-h6">Agendar Evento</div>
        </q-card-section>

        <q-card-section>
          <q-input
            v-model="schedulingData.orderTransporterId"
            label="Identificador de Transporte"
            outlined
          />
          <q-input
            v-model="schedulingData.startStorloc"
            label="Storloc de Origem"
            outlined
          />
          <q-input
            v-model="schedulingData.destinator"
            label="Storloc de Destino"
            outlined
          />
          <q-input
            v-model="schedulingData.scheduling"
            label="Horário Agendado"
            outlined
            type="datetime"
          />
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Cancelar" @click="showSchedulingDialog = false" />
          <q-btn
            flat
            label="Confirmar"
            color="primary"
            @click="confirmScheduling"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { createAxiosInstance } from "../api/axiosInstance";
import maps from "../components/yard/DockController.vue";
import CheckInTable from "../components/yard/CheckInTable.vue";
import CheckOutTable from "../components/yard/CheckOutTable.vue";
import ScheduledEventsTable from "../components/yard/ScheduledEventsTable.vue";

const axiosInstance = createAxiosInstance("dev2");
const placedStorlocs = ref([]);
const dropzoneWidth = ref(1350);
const dropzoneHeight = ref(600);
const tooltip = reactive({ visible: false, name: "", licensePlate: "" });
const tooltipAnchor = ref(null);

const showCheckInDialog = ref(false);
const showCheckOutDialog = ref(false);
const showSchedulingDialog = ref(false);

const checkInData = reactive({
  driver: "",
  driverFederalTax: "",
  licensePlate: "",
  boardingType: "",
  vehiclePicture: null,
  driverPicture: null,
});
const checkOutData = reactive({ exitInspection: "", releaseDocuments: null });
const schedulingData = reactive({
  orderTransporterId: "",
  startStorloc: "",
  destinator: "",
  scheduling: "",
  begin: "",
  end: "",
  time: "",
});

const showManagementView = ref("patio"); // Controla a visualização dos cards

onMounted(() => {
  fetchStorlocs();
});

function fetchStorlocs() {
  axiosInstance
    .get("/rest/storloc")
    .then((response) => {
      placedStorlocs.value = response.data.content.storlocs || [];
    })
    .catch((error) => console.error("Erro ao buscar storlocs:", error));
}

function openCheckInDialog() {
  showCheckInDialog.value = true;
  showManagementView.value = "checkin";
}

function confirmCheckIn() {
  axiosInstance
    .post("/rest/checkin", checkInData)
    .then(() => alert("Check-in realizado com sucesso!"))
    .catch((error) => console.error("Erro ao realizar check-in:", error));
  showCheckInDialog.value = false;
}

function openCheckOutDialog() {
  showCheckOutDialog.value = true;
  showManagementView.value = "checkout";
}

function confirmCheckOut() {
  axiosInstance
    .post("/rest/checkout", checkOutData)
    .then(() => alert("Check-out realizado com sucesso!"))
    .catch((error) => console.error("Erro ao realizar check-out:", error));
  showCheckOutDialog.value = false;
}

function openSchedulingDialog() {
  showSchedulingDialog.value = true;
  showManagementView.value = "eventos";
}

function confirmScheduling() {
  axiosInstance
    .post("/rest/schedule", schedulingData)
    .then(() => alert("Evento agendado com sucesso!"))
    .catch((error) => console.error("Erro ao agendar evento:", error));
  showSchedulingDialog.value = false;
}
</script>
