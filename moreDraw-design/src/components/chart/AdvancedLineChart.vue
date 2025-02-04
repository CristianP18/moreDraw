<template>
    <div>
        <canvas ref="advancedLineChartCanvas" />
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

const props = defineProps(['chartData', 'movingAverageData', 'growthCurveData']);
const advancedLineChartCanvas = ref(null);

onMounted(() => {
    new Chart(advancedLineChartCanvas.value, {
        type: 'line',
        data: {
            labels: props.chartData.labels,
            datasets: [
                {
                    label: 'Original Data',
                    data: props.chartData.datasets[0].data,
                    borderColor: 'rgba(75,192,192,1)',
                    fill: false,
                },
                {
                    label: 'Moving Average',
                    data: props.movingAverageData,
                    borderColor: 'rgba(255,99,132,1)',
                    fill: false,
                    borderDash: [5, 5],
                },
                {
                    label: 'Growth Curve',
                    data: props.growthCurveData,
                    borderColor: 'rgba(54,162,235,1)',
                    fill: false,
                    borderDash: [10, 5],
                },
            ],
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                },
            },
        },
    });
});
</script>
