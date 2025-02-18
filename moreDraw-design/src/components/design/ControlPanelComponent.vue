<template>
  <q-toolbar class="q-mt-md">
    <q-space />
    <image-component />
    <!-- Controle de Grade -->
    <q-toggle
      :model-value="showGrid"
      label="Mostrar Grade"
      @update:model-value="$emit('toggle-grid', $event)"
    />
  </q-toolbar>
</template>

<script setup>
import { ref, watch } from "vue";
import imageComponent from "./ImageComponent.vue";

const props = defineProps({
  selectedItem: {
    type: Object,
    default: null,
  },
  showGrid: Boolean,
});

const emits = defineEmits(["update-item", "toggle-grid"]);

// Cria uma cópia local do selectedItem para permitir edição
const localItem = ref(props.selectedItem ? { ...props.selectedItem } : {});

// Observa mudanças no selectedItem passado pelo pai e atualiza a cópia local
watch(
  () => props.selectedItem,
  (newVal) => {
    if (newVal) {
      localItem.value = { ...newVal };
    } else {
      localItem.value = {};
    }
  }
);

// Observa mudanças na cópia local e emite eventos para atualizar o item no pai
watch(localItem, () => {
  if (props.selectedItem) {
    // Evita emitir update-item quando nenhum item está selecionado
    emits("update-item", localItem.value);
  }
});
</script>

<style scoped>
/* Adicione estilos personalizados, se necessário */
</style>
