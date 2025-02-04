<template>
    <div>
        <!-- Barra de Ferramentas de Geometria -->
        <GeometryToolbar @add-geometry="handleAddGeometry" />

        <!-- Seu Canvas ou Área de Desenho -->
        <div class="canvas">
            <!-- Renderização dos Itens Adicionados -->
            <DraggableItem
                v-for="item in items"
                :key="item.id"
                :item="item"
                :zoom-level="zoomLevel"
                :pan-offset="panOffset"
                :selected-item="selectedItem"
                @select-item="selectItem"
                @update-item="updateItem"
                @bring-to-front="bringToFront"
                @send-to-back="sendToBack"
            />
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import GeometryToolbar from './GeometryToolbar.vue';
import DraggableItem from './DraggableItem.vue';

const items = ref([]);
const zoomLevel = ref(1);
const panOffset = ref({ x: 0, y: 0 });
const selectedItem = ref(null);

/**
   * Função para lidar com a adição de uma nova geometria.
   * @param {string} type - Tipo da geometria a ser adicionada.
   */
function handleAddGeometry(type) {
    const newItem = {
        id: Date.now(),
        type,
        position: { x: 100, y: 100 },
        width: 100,
        height: 100,
        color: '#ff0000',
        borderWidth: 2,
        borderColor: '#000000',
        // Adicione outras propriedades conforme necessário
    };
    items.value.push(newItem);
}

function selectItem(item) {
    selectedItem.value = item;
}

function updateItem(updatedItem) {
    const index = items.value.findIndex((item) => item.id === updatedItem.id);
    if (index !== -1) {
        items.value[index] = updatedItem;
    }
}

function bringToFront(item) {
    const index = items.value.findIndex((i) => i.id === item.id);
    if (index !== -1) {
        const [removed] = items.value.splice(index, 1);
        items.value.push(removed);
    }
}

function sendToBack(item) {
    const index = items.value.findIndex((i) => i.id === item.id);
    if (index !== -1) {
        const [removed] = items.value.splice(index, 1);
        items.value.unshift(removed);
    }
}
</script>

  <style scoped>
  .canvas {
    position: relative;
    width: 100%;
    height: 800px;
    border: 1px solid #ccc;
    background-color: #fafafa;
    overflow: hidden;
  }
  </style>
