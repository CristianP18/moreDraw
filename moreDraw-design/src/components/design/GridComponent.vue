<template>
    <div
        class="grid"
        :style="gridStyle"
    />
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
    gridSize: {
        type: Number,
        required: true,
        default: 20, // Valor padrão caso não seja passado
    },
});

// Computed property para definir os estilos da grade
const gridStyle = computed(() => ({
    position: 'absolute',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    backgroundSize: `${props.gridSize}px ${props.gridSize}px`,
    backgroundImage:
        'linear-gradient(to right, #ccc 1px, transparent 1px), linear-gradient(to bottom, #ccc 1px, transparent 1px)',
}));
</script>

<style scoped>
.grid {
    pointer-events: none; /* Permite que os cliques passem pela grade */
}
</style>
