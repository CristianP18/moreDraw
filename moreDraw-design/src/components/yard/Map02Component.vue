<template>
  <q-page class="page q-px-lg">
    <q-card class="q-pa-md dropzone-card">
      <q-toolbar-title>
        Yard Controller
        <q-btn
          label="Configuração"
          href="http://192.168.0.248:8086/#/yardconf02"
          flat
          color="primary"
          class="q-ml-sm"
        />
      </q-toolbar-title>

      <!-- Área de Drop com Mapa como Background -->
      <div
        ref="dropzone"
        class="dropzone"
        :style="{ width: dropzoneWidth + 'px', height: dropzoneHeight + 'px' }"
      >
        <div
          v-for="dock in placedDocks"
          :key="dock.uniqueId"
          class="draggable-item"
          :data-id="dock.uniqueId"
          :style="{
            transform: `translate(${dock.position.x}px, ${dock.position.y}px)`,
            color: fontColor,
            fontSize: fontSize + 'px',
          }"
          :draggable="true"
          @dblclick="openDetails(dock)"
          @dragstart="dragStart(dock)"
          @dragover.prevent
          @drop="drop(dock)"
          @mouseenter="showTooltip(dock, $event)"
          @mouseleave="hideTooltip"
        >
          <q-item clickable class="storloc-item">
            <q-item-section style="position: relative">
              <!-- Ícone do Caminhão quando a cor for verde (#008000) -->
              <img
                v-if="dock.color === '#008000'"
                :src="truckIcon"
                alt="Caminhão"
                class="camiao-icone"
                style="left: 12%; height: 80px; transform: rotate(180deg)"
              />

              <!-- Bolinha (Circle) quando a cor não é verde -->
              <div
                v-else
                class="label-circle"
                :style="{
                  backgroundColor: dock.color,
                  width: dotSize + 'px',
                  height: dotSize + 'px',
                }"
              />

              <!-- EAN label displayed over the truck image or circle -->
              <q-item-label
                style="
                  position: absolute;
                  top: 0;
                  left: 0;
                  width: 100%;
                  text-align: center;
                  color: black;
                  z-index: 10;
                "
              >
                {{
                  dock.ean.length > 8 ? dock.ean.slice(0, 8) + "..." : dock.ean
                }}
              </q-item-label>
            </q-item-section>
          </q-item>
        </div>

        <q-tooltip
          v-if="tooltip.visible"
          :target="tooltipAnchor"
          transition-show="fade"
          transition-hide="fade"
        >
          <div>{{ tooltip.name }}</div>
          <div v-if="tooltip.loadingTrailer">
            Placa: {{ tooltip.loadingTrailer }}
          </div>
        </q-tooltip>
      </div>

      <q-toolbar class="q-px-md q-py-sm">
        <!-- Botão para salvar as trocas -->
        <q-btn
          label="Salvar"
          color="primary"
          class="q-mt-md"
          @click="savePositions"
        />
        <!-- Tamanho da Fonte -->
        <q-input
          v-model.number="fontSize"
          type="number"
          label="Tamanho da Fonte"
          outlined
          dense
          color="primary"
          class="q-ml-sm"
          style="width: 150px; margin-right: 20px; margin-left: 100px"
        />

        <!-- Botão para abrir o seletor de cor da fonte -->
        <q-btn
          flat
          icon="color_lens"
          label="Cor da Fonte"
          color="primary"
          class="q-ml-sm"
          @click="toggleFontColorDialog"
        />

        <!-- Diálogo que contém o seletor de cores para a fonte -->
        <q-dialog v-model="showFontColorDialog">
          <q-card>
            <q-card-section>
              <q-color v-model="fontColor" flat />
            </q-card-section>
            <q-card-actions align="right">
              <q-btn flat label="Fechar" @click="toggleFontColorDialog" />
            </q-card-actions>
          </q-card>
        </q-dialog>

        <!-- Tamanho da Bolinha -->
        <q-input
          v-model.number="dotSize"
          type="number"
          label="Tamanho da Bolinha"
          outlined
          dense
          style="width: 150px; margin-right: 20px"
        />
      </q-toolbar>
    </q-card>

    <!-- Diálogo de Edição -->
    <q-dialog v-model="showDetailsDialog">
      <q-card>
        <q-card-section>
          <div class="text-h6">
            Detalhes da Dock - {{ selectedDock.ean || "N/A" }}
          </div>
        </q-card-section>
        <q-card-section>
          <q-input v-model="selectedDock.ean" label="EAN" readonly />
          <q-input
            v-model="selectedDock.loadingTrailer"
            label="Placa do Veículo"
            readonly
          />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="Fechar" @click="showDetailsDialog = false" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { onMounted, ref, reactive } from "vue";
import truckIcon from "src/assets/imgVehicle/truck02.svg";

const props = defineProps({
  docks: Array, // Recebe as docks via props
});

const placedDocks = ref([]);
const dropzone = ref(null);
const showDetailsDialog = ref(false);
const selectedDock = reactive({
  ean: "",
  loadingTrailer: "",
});

const fontSize = ref(18);
const fontColor = ref("#000000");
const showFontColorDialog = ref(false);
const dotSize = ref(12);
const dotColor = ref("#334ab0");
const showDotColorDialog = ref(false);
const dropzoneWidth = ref(2455);
const dropzoneHeight = ref(800);

let draggedDock = null;

const tooltip = reactive({
  visible: false,
  name: "",
  loadingTrailer: "",
});
const tooltipAnchor = ref(null);

onMounted(() => {
  setupDocks();
});

function setupDocks() {
  let idCounter = 0;
  placedDocks.value = props.docks.map((dock) => {
    const position = dock.location ? JSON.parse(dock.location) : { x: 0, y: 0 };
    return { ...dock, position, uniqueId: idCounter++ };
  });
}

function openDetails(dock) {
  Object.assign(selectedDock, dock);
  showDetailsDialog.value = true;
}

function dragStart(dock) {
  draggedDock = dock;
}

function drop(targetDock) {
  if (draggedDock && draggedDock !== targetDock && !targetDock.loadingTrailer) {
    targetDock.loadingTrailer = draggedDock.loadingTrailer;
    targetDock.color = getColorByType(targetDock.loadingTrailer);

    draggedDock.loadingTrailer = "";
    draggedDock.color = getColorByType(draggedDock.loadingTrailer);

    draggedDock = null;
  }
}

function getColorByType(loadingTrailer) {
  return loadingTrailer ? "#008000" : "#FF0000"; // Verde para ocupado, vermelho para vazio
}

async function savePositions() {
  try {
    const updatePromises = placedDocks.value.map(async (dock) => {
      dock.location = JSON.stringify(dock.position);
      await axiosInstance.put(`/rest/dock/${dock.ean}`, dock);
    });

    await Promise.all(updatePromises);
    alert("Posições e tipos de veículo atualizados com sucesso!");
  } catch (error) {
    console.error("Erro ao salvar posições:", error);
  }
}

function toggleFontColorDialog() {
  showFontColorDialog.value = !showFontColorDialog.value;
}

// Função para mostrar o tooltip ao passar o mouse
function showTooltip(dock, event) {
  tooltip.visible = true;
  tooltip.name = dock.ean;
  tooltip.loadingTrailer = dock.loadingTrailer || "";
  tooltipAnchor.value = event.currentTarget;
}

// Função para esconder o tooltip ao sair do mouse
function hideTooltip() {
  tooltip.visible = false;
  tooltipAnchor.value = null;
}
</script>

<style scoped>
.dropzone {
  position: relative;
  border: 2px dashed #ddd;
  background-size: cover;
  background-position: center;
  background-size: contain;
  background-size: 100% 100%;
  background-color: #808080;
  background-image: url("src/assets/imgVehicle/desenho.jpg");
}

.draggable-item {
  display: flex;
  align-items: center;
  cursor: move;
  position: absolute;
  user-select: none;
}

.label-circle {
  border-radius: 50%;
  display: inline-block;
  margin-left: 10px;
}

/* Ícone de caminhão quando a cor é verde */
.camiao-icone {
  position: absolute;
  width: 32px;
  height: 32px;
  top: -5px;
  right: -5px;
  z-index: 5;
}
</style>
