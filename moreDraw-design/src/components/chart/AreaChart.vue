<template>
    <div>
        <canvas ref="areaChartCanvas" />
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

const props = defineProps(['chartData']);
const areaChartCanvas = ref(null);

onMounted(() => {
    new Chart(areaChartCanvas.value, {
        type: 'line',
        data: props.chartData,
        options: {
            plugins: {
                filler: {
                    propagate: true,
                },
            },
            elements: {
                line: {
                    tension: 0.4,
                },
            },
        },
    });
});
</script>
