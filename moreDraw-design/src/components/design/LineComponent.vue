<!-- LineComponent.vue -->
<template>
    <svg
        :style="lineStyle"
        xmlns="http://www.w3.org/2000/svg"
    >
        <line
            :x1="item.start.x"
            :y1="item.start.y"
            :x2="item.end.x"
            :y2="item.end.y"
            :stroke="item.strokeColor || 'black'"
            :stroke-width="item.strokeWidth || 2"
        />
    </svg>
</template>

<script setup>
const props = defineProps({
    item: Object,
});

const lineStyle = {
    position: 'absolute',
    top: '0',
    left: '0',
    width: '100%',
    height: '100%',
};
</script>

  <style scoped>
  /* Adicione estilos se necessário */
  </style>
