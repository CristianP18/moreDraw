<template>
    <card-select>
        <div>
            test02
        </div>
        Teste de Array:
        {{ objectTest[1] }}
        Array de Objetos:
        {{ name }}
        <div
            class="row"
            style="max-width:100%;"
        >
            <div class="col-lg-4 col-md-6 col-sm-12">
                Semestres:
                <bar-chart :chart-data="barChart" />
            </div>
            <div class="col-lg-4 col-md-6 col-sm-12">
                Meses:
                <bar-chart :chart-data="barChartData" />
            </div>
            <div class="col-lg-4 col-md-6 col-sm-12">
                DoughnutChart:
                <doughnut-chart
                    :chart-data="doughnutChartData"
                    style="height: 350px;"
                />
            </div>
        </div>
    </card-select>
</template>
<script setup>
import { ref } from 'vue';
import BarChart from '../../components/chart/BarChart.vue';
import DoughnutChart from '../../components/chart/DoughnutChart.vue';

const objectTest = ref([20, 10, 13, 14, 18]);
const arrayObject = ref([
    {
        Name: 'Pedro', Idade: '24', Peso: '85',
    },
    {
        Name: 'Adão', Idade: '24', Peso: '85',
    },
    {
        Name: 'Henrique', Idade: '24', Peso: '85',
    },
]);

const secondObject = arrayObject.value[1];
const name = secondObject.Name;

const doughnutChartData = ref({
    labels: ['Red', 'Blue', 'Yellow', 'Green', 'black', 'morron'],
    datasets: [{
        label: 'Colors',
        data: [10, 12, 3, 5, 4, 6],
        backgroundColor: [
            'rgba(255, 99, 132, 0.6)',
            'rgba(54, 162, 235, 0.6)',
            'rgba(255, 206, 86, 0.6)',
            'rgba(75, 192, 192, 0.6)',
            'rgba(0, 0, 0, 1)',
            'rgba(165, 42, 42, 1)',
        ],
    }],
});

const barChart = ref({
    labels: ['first semester', 'second semester', 'third semester', 'time four semester'],
    datasets: [
        {
            label: 'Growth',
            data: [50, 63, 45, 76],
            backgroundColor: 'rgba(75,192,192,0.6)',
        },
    ],
});
const barChartData = ref({
    labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'november', 'december'],
    datasets: [
        {
            label: 'Growth',
            data: [10, 20, 30, 40, 42, 54, 38, 56, 88, 44, 55, 47],
            backgroundColor: 'rgba(75,192,192,0.6)',
        },

    ],
});

</script>
