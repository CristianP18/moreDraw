<template>
    <q-card-section style="max-width: 250px;">
        <q-input
            v-model="newLabel"
            label="Label"
        />
        <q-input
            v-model="newData"
            label="Data"
            type="number"
        />
        <q-btn
            label="Add Data"
            color="primary"
            @click="addData"
        />
    </q-card-section>
    <div
        class="row"
        style="max-width: 100%;"
    >
        <div
            class="col-lg-4 col-md-6 col-sm-12"
            style="max-width: 400px;"
        >
            <doughnut-chart :chart-data="doughnutChartData" />
        </div>
        <div class="col-lg-4 col-md-6 col-sm-12">
            <line-chart :chart-data="lineChartData" />
        </div>
        <div
            class="col-lg-4 col-md-6 col-sm-12"
        >
            <bar-chart :chart-data="barChartData" />
        </div>
        <div class="col-lg-4 col-md-6 col-sm-12">
            <scatter-chart :chart-data="scatterChartData" />
        </div>
        <div class="col-lg-4 col-md-6 col-sm-12">
            <area-chart :chart-data="areaChartData" />
        </div>
    </div>

    <!-- Gráfico de Linhas Avançado (Médias Móveis e Curva de Crescimento) -->
    <q-card class="col-lg-4 col-md-6 col-sm-12">
        <advanced-line-chart
            :chart-data="lineChartData"
            :moving-average-data="movingAverageData"
            :growth-curve-data="growthCurveData"
        />
    </q-card>
    <div :style="{ backgroundImage: `url(${require('../assets/img/image.svg')})` }">
        ..
        ..
        ..
        ..

        ..
        ..
        ..
        ..
        .
    </div>
</template>

<script setup>
import { ref } from 'vue';
import LineChart from '../components/chart/LineChart.vue';
import BarChart from '../components/chart/BarChart.vue';
import ScatterChart from '../components/chart/ScatterChart.vue';
import AreaChart from '../components/chart/AreaChart.vue';
import DoughnutChart from '../components/chart/DoughnutChart.vue';
import AdvancedLineChart from '../components/chart/AdvancedLineChart.vue';

const newLabel = ref('');
const newData = ref(0);

const lineChartData = ref({
    labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October'],
    datasets: [
        {
            label: 'Growth',
            data: [
                { x: 25, y: 32 },
                { x: 20, y: 30 },
                { x: 30, y: 40 },
                { x: 40, y: 50 },
                { x: 35, y: 45 },
                { x: 60, y: 80 },
                { x: 70, y: 100 },
                { x: 80, y: 120 },
                { x: 90, y: 145 },
                { x: 86, y: 122 },
            ],
            backgroundColor: 'rgba(75,192,192,0.6)',
        },
    ],
});

const barChartData = ref({
    labels: ['January', 'February', 'March', 'April'],
    datasets: [
        {
            label: 'Growth',
            data: [10, 20, 30, 40],
            backgroundColor: 'rgba(75,192,192,0.6)',
        },
    ],
});
const doughnutChartData = ref({
    labels: ['Red', 'Blue', 'Yellow', 'Green'],
    datasets: [{
        label: 'Colors',
        data: [12, 19, 3, 5],
        backgroundColor: [
            'rgba(255, 99, 132, 0.6)',
            'rgba(54, 162, 235, 0.6)',
            'rgba(255, 206, 86, 0.6)',
            'rgba(75, 192, 192, 0.6)',
        ],
    }],
});

const movingAverageData = ref([15, 25, 35, 45, 55, 65, 75, 85, 95, 105]);

const growthCurveData = ref([12, 28, 50, 78, 110, 148, 192, 240, 292, 350]);

const scatterChartData = ref({
    labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October'],
    datasets: [
        {
            label: 'Growth',
            data: [
                { x: 10, y: 20 },
                { x: 20, y: 30 },
                { x: 30, y: 40 },
                { x: 40, y: 50 },
                { x: 50, y: 65 },
                { x: 60, y: 80 },
                { x: 70, y: 100 },
                { x: 80, y: 120 },
                { x: 90, y: 145 },
                { x: 100, y: 170 },
            ],
            backgroundColor: 'rgba(75,192,192,0.6)',
        },
    ],
});

const areaChartData = ref({
    labels: ['January', 'February', 'March', 'April'],
    datasets: [
        {
            label: 'Growth',
            data: [10, 20, 30, 40],
            backgroundColor: 'rgba(75,192,192,0.4)',
            borderColor: 'rgba(75,192,192,1)',
            fill: true,
        },
    ],
});

function addData() {
    const label = newLabel.value;
    const data = newData.value;

    if (label && data !== 0) {
        lineChartData.value.labels.push(label);
        lineChartData.value.datasets[0].data.push(data);

        barChartData.value.labels.push(label);
        barChartData.value.datasets[0].data.push(data);

        scatterChartData.value.labels.push(label);
        scatterChartData.value.datasets[0].data.push({ x: data, y: data + 10 });

        areaChartData.value.labels.push(label);
        areaChartData.value.datasets[0].data.push(data);

        // Limpa os inputs após adicionar os dados
        newLabel.value = '';
        newData.value = 0;
    }
}
</script>

  <style scoped>
  .q-page {
    max-width: 600px;
    margin: auto;
  }
  </style>
