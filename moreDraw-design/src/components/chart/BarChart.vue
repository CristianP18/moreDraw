<template>
    <div>
        <canvas ref="barChartCanvas" />
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

const props = defineProps(['chartData']);
const barChartCanvas = ref(null);

onMounted(() => {
    new Chart(barChartCanvas.value, {
        type: 'bar',
        data: props.chartData,
    });
});
</script>
