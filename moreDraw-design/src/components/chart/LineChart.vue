<template>
    <div>
        <canvas ref="lineChartCanvas" />
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

const props = defineProps(['chartData']);
const lineChartCanvas = ref(null);

onMounted(() => {
    new Chart(lineChartCanvas.value, {
        type: 'line',
        data: props.chartData,
    });
});
</script>
