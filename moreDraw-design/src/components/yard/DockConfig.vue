<template>
  <q-page class="page q-px-lg">
    <q-card class="q-pa-md dropzone-card">
      <q-toolbar-title>
        Configuração de Posição
        <q-btn
          label="Voltar"
          flat
          color="primary"
          class="q-ml-sm"
          @click="$emit('toggle-config')"
        />
      </q-toolbar-title>

      <!-- Barra de ferramentas para configurações -->
      <q-toolbar class="q-px-md q-py-sm">
        <q-input
          v-model.number="fontSize"
          type="number"
          label="Tamanho da Fonte"
          outlined
          dense
          color="primary"
          style="width: 150px; margin-right: 20px"
        />
        <q-btn
          flat
          icon="color_lens"
          label="Cor da Fonte"
          color="primary"
          @click="toggleFontColorDialog"
        />
        <q-input
          v-model.number="dotSize"
          type="number"
          label="Tamanho da Bolinha"
          outlined
          dense
          style="width: 150px; margin-right: 20px"
        />
        <q-btn
          flat
          icon="color_lens"
          label="Cor da Bolinha"
          color="primary"
          @click="toggleDotColorDialog"
        />
        <q-input
          v-model.number="dropzoneWidth"
          type="number"
          label="Largura do Pátio"
          outlined
          dense
          style="width: 150px; margin-right: 20px"
        />
        <q-input
          v-model.number="dropzoneHeight"
          type="number"
          label="Altura do Pátio"
          outlined
          dense
          style="width: 150px"
        />
        <q-input
          v-model.number="zoom"
          type="number"
          label="Zoom"
          outlined
          dense
          style="width: 100px; margin-left: 20px"
          :min="0.1"
          :max="3"
          step="0.1"
        />
        <q-input
          v-model.number="rotation"
          type="number"
          label="Rotação"
          outlined
          dense
          style="width: 100px"
          suffix="°"
        />
        <q-input
          v-model="backgroundImagePathInput"
          type="text"
          label="Caminho da Imagem"
          outlined
          dense
          style="width: 300px; margin-left: 20px"
          placeholder="Digite o caminho da imagem"
          @change="updateBackgroundImage"
        />
      </q-toolbar>

      <!-- Diálogo de seleção de cor da fonte -->
      <q-dialog v-model="showFontColorDialog">
        <q-card>
          <q-card-section><q-color v-model="fontColor" flat /></q-card-section>
          <q-card-actions align="right"
            ><q-btn flat label="Fechar" @click="toggleFontColorDialog"
          /></q-card-actions>
        </q-card>
      </q-dialog>

      <!-- Diálogo de seleção de cor da bolinha -->
      <q-dialog v-model="showDotColorDialog">
        <q-card>
          <q-card-section><q-color v-model="dotColor" flat /></q-card-section>
          <q-card-actions align="right"
            ><q-btn flat label="Fechar" @click="toggleDotColorDialog"
          /></q-card-actions>
        </q-card>
      </q-dialog>

      <!-- Área de Drop com Mapa como Background -->
      <div
        ref="dropzone"
        class="dropzone"
        :style="{
          width: dropzoneWidth + 'px',
          height: dropzoneHeight + 'px',
          transform: `scale(${zoom}) rotate(${rotation}deg)`,
          backgroundImage: `url(${backgroundImagePath})`,
        }"
      >
        <!-- Docks já posicionadas -->
        <div
          v-for="dock in placedDocks"
          :key="dock.dockId"
          class="draggable-item"
          :style="{
            transform: `translate(${dock.position.x}px, ${dock.position.y}px)`,
            color: fontColor,
            fontSize: fontSize + 'px',
          }"
          @mousedown="startDrag($event, dock)"
        >
          <q-item clickable class="dock-item">
            <q-item-section>
              <q-item-label>{{ dock.dockName }}</q-item-label>
              <div
                class="label-circle"
                :style="{
                  backgroundColor: dotColor,
                  width: dotSize + 'px',
                  height: dotSize + 'px',
                }"
              />
            </q-item-section>
          </q-item>
        </div>
      </div>

      <!-- Botão para salvar as posições -->
      <q-btn
        label="Salvar Posições"
        color="primary"
        class="q-mt-md"
        @click="savePositions"
      />
    </q-card>
  </q-page>
</template>

<script setup>
import { ref, onMounted, nextTick } from "vue";
import axios from "axios";
import defaultBackgroundImage from "src/assets/imgVehicle/patio.png";

const placedDocks = ref([]);
const dropzone = ref(null);

// Configurações de estilo
const fontSize = ref(12);
const fontColor = ref("#000000");
const showFontColorDialog = ref(false);
const dotSize = ref(10);
const dotColor = ref("#334ab0");
const showDotColorDialog = ref(false);
const dropzoneWidth = ref(1423);
const dropzoneHeight = ref(600);
const zoom = ref(1);
const rotation = ref(0);
const backgroundImagePath = ref(defaultBackgroundImage);
const backgroundImagePathInput = ref("");

// Variáveis para gerenciamento de drag & drop
let selectedDock = null;
let initialMousePosition = { x: 0, y: 0 };
let initialDockPosition = { x: 0, y: 0 };

// Funções auxiliares
function isValidJSON(str) {
  if (!str) return false;
  try {
    JSON.parse(str);
    return true;
  } catch {
    return false;
  }
}

onMounted(async () => {
  await fetchDocks(); // Agora chamamos a API para obter as docks diretamente
  console.log("Docks recebidas da API:", placedDocks.value);
});

async function fetchDocks() {
  try {
    const response = await axios.get("http://localhost:8082/api/docks");
    await nextTick();

    placedDocks.value = response.data.map((dock) => {
      let position = { x: 52, y: 350 }; // Posição padrão
      if (dock.location && isValidJSON(dock.location)) {
        position = JSON.parse(dock.location); // Se `location` for um JSON válido, usar suas coordenadas
      }
      return { ...dock, position };
    });

    console.log("Docks posicionadas:", placedDocks.value);
  } catch (error) {
    console.error("Erro ao buscar docks:", error);
  }
}

function startDrag(event, dock) {
  selectedDock = dock;
  initialMousePosition = { x: event.clientX, y: event.clientY };
  initialDockPosition = { ...dock.position };

  window.addEventListener("mousemove", onDrag);
  window.addEventListener("mouseup", endDrag);
}

function onDrag(event) {
  if (!selectedDock) return;

  const deltaX = event.clientX - initialMousePosition.x;
  const deltaY = event.clientY - initialMousePosition.y;

  selectedDock.position.x = initialDockPosition.x + deltaX / zoom.value;
  selectedDock.position.y = initialDockPosition.y + deltaY / zoom.value;

  // Atualiza o campo location com a nova posição
  selectedDock.location = JSON.stringify(selectedDock.position);
}

function endDrag() {
  window.removeEventListener("mousemove", onDrag);
  window.removeEventListener("mouseup", endDrag);

  // Chama a função para salvar a posição da dock após o drag & drop
  updateDockLocation(selectedDock);

  selectedDock = null;
}

async function updateDockLocation(dock) {
  try {
    // Envia a nova posição da dock para o servidor
    await axios.put(`http://localhost:8082/api/docks/${dock.dockId}`, dock);
    console.log("Posição atualizada com sucesso:", dock.location);
  } catch (error) {
    console.error("Erro ao atualizar a posição da dock:", error);
  }
}

function toggleFontColorDialog() {
  showFontColorDialog.value = !showFontColorDialog.value;
}

function toggleDotColorDialog() {
  showDotColorDialog.value = !showDotColorDialog.value;
}

function updateBackgroundImage() {
  backgroundImagePath.value =
    backgroundImagePathInput.value || defaultBackgroundImage;
}

async function savePositions() {
  try {
    // Faz a requisição PUT para salvar as posições atualizadas
    const updatedDocks = placedDocks.value.map((dock) => ({
      dockId: dock.dockId,
      dockName: dock.dockName,
      location: dock.location, // Nova localização em formato JSON
      description: dock.description,
      loadingTrailer: dock.loadingTrailer,
      loadedTrailer: dock.loadedTrailer,
      available: dock.available,
      arrivalTime: dock.arrivalTime,
      departureTime: dock.departureTime,
      dockFree: dock.dockFree,
    }));

    await axios.put("http://localhost:8082/api/docks", updatedDocks);
    alert("Posições salvas com sucesso!");
  } catch (error) {
    console.error("Erro ao salvar posições:", error);
  }
}
</script>

<style scoped>
.dropzone {
  position: relative;
  border: 2px dashed #ddd;
  background-size: cover;
  background-position: center;
  background-size: 100% 100%;
}

.draggable-item {
  position: absolute;
  cursor: grab;
  user-select: none;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: transparent; /* Fundo transparente */
  border-radius: 50%;
}

.label-circle {
  border-radius: 50%;
  display: inline-block;
  margin-left: 10px;
  border: 1px solid #000;
}
</style>
