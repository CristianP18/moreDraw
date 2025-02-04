<template>
    <div>
        <canvas ref="doughnutChartCanvas" />
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

const props = defineProps(['chartData']);
const doughnutChartCanvas = ref(null);

onMounted(() => {
    new Chart(doughnutChartCanvas.value, {
        type: 'doughnut',
        data: props.chartData,
    });
});
</script>
