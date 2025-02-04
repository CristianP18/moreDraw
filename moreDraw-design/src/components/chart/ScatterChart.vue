<template>
    <div>
        <canvas ref="scatterChartCanvas" />
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

const props = defineProps(['chartData']);
const scatterChartCanvas = ref(null);

onMounted(() => {
    new Chart(scatterChartCanvas.value, {
        type: 'scatter',
        data: props.chartData,
    });
});
</script>
