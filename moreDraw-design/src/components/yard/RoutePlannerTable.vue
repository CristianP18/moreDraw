<!-- RoutePlannerTable.vue -->
<template>
  <div class="route-planner-table">
    <q-table
      :rows="routePlanner || []"
      :columns="columns"
      row-key="orderNumber"
      bordered
      flat
      class="q-mt-md"
    >
      <!-- Slot para a coluna Tipo da Carga com Radio Buttons -->
      <template v-slot:body-cell-cargoType="props">
        <q-radio
          v-model="props.row.cargoType"
          :options="cargoOptions"
          color="primary"
          @update:model-value="updateCargoType(props.row, $event)"
        />
      </template>
    </q-table>
  </div>
</template>

<script setup>
import { defineProps, watch } from "vue";

const props = defineProps({
  routePlanner: {
    type: Array,
    required: true,
  },
});

// Emitir evento para atualizar o routePlanner no componente pai
const emit = defineEmits(["update-route-planner"]);

// Definir as colunas da tabela alinhadas com os campos do objeto de ordem
const columns = [
  {
    name: "orderNumber",
    required: true,
    label: "Ordem",
    align: "left",
    field: "orderNumber",
    sortable: true,
  },
  {
    name: "departureTime",
    align: "left",
    label: "Hora de Saída",
    field: "departureTime",
    sortable: true,
  },
  {
    name: "driverName",
    align: "left",
    label: "Motorista",
    field: "driverName",
    sortable: true,
  },
  {
    name: "vehicleLicensePlate",
    align: "left",
    label: "Placa do Veículo",
    field: "vehicleLicensePlate",
    sortable: true,
  },
  {
    name: "cavalinho",
    align: "left",
    label: "Cavalinho",
    field: "cavalinho",
    sortable: true,
  },
  {
    name: "trailerLicensePlate",
    align: "left",
    label: "Placa do Trailer",
    field: "trailerLicensePlate",
    sortable: true,
  },
  {
    name: "dockStart",
    align: "left",
    label: "Dock de Origem",
    field: "dockStart",
    sortable: true,
  },
  {
    name: "dockDestination",
    align: "left",
    label: "Dock de Destino",
    field: "dockDestination",
    sortable: true,
  },
  {
    name: "cargoType",
    align: "left",
    label: "Tipo da Carga",
    field: "cargoType",
    sortable: false,
  },
];

// Definir as opções para Tipo da Carga
const cargoOptions = [
  { label: "Peça", value: "Peça" },
  { label: "Sacolas", value: "Sacolas" },
  { label: "Teste", value: "Teste" },
];

// Monitorar mudanças no routePlanner e emitir atualizações
watch(
  () => props.routePlanner,
  (newVal) => {
    emit("update-route-planner", newVal);
  },
  { deep: true }
);

// Função para atualizar o tipo da carga
function updateCargoType(row, newCargoType) {
  // Crie uma cópia do routePlanner
  const updatedRoutePlanner = [...props.routePlanner];

  // Encontre o índice do row a ser atualizado usando 'orderNumber'
  const index = updatedRoutePlanner.findIndex(
    (entry) => entry.orderNumber === row.orderNumber
  );

  if (index !== -1) {
    // Atualize o cargoType
    updatedRoutePlanner[index].cargoType = newCargoType;

    // Emitir a atualização para o componente pai
    emit("update-route-planner", updatedRoutePlanner);
  }
}
</script>

<style scoped>
.route-planner-table {
  margin-top: 20px;
}
</style>
