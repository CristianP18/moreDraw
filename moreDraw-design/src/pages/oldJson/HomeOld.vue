<template>
    <q-page class="q-pa-md">
        <q-card>
            <q-card-section>
                <div class="text-h6">
                    {{ $t('dataLoad.loadData') }}
                </div>
            </q-card-section>

            <q-card-section>
                <q-radio
                    v-model="selectedOption"
                    val="Todos"
                    :label="$t('dataLoad.allData')"
                />
                <q-icon
                    name="info"
                    class="cursor-pointer q-ml-sm"
                    @click="showInfo($t('dataLoad.allData'))"
                />
                <q-radio
                    v-model="selectedOption"
                    val="Por Data"
                    :label="$t('dataLoad.byDate')"
                />
                <q-icon
                    name="info"
                    class="cursor-pointer q-ml-sm"
                    @click="showInfo($t('dataLoad.byDate'))"
                />
                <div
                    v-if="selectedOption === 'Por Data'"
                    class="q-mt-md"
                >
                    <q-radio
                        v-model="dateType"
                        val="dia"
                        :label="$t('dataLoad.byDay')"
                    />
                    <q-radio
                        v-model="dateType"
                        val="mes"
                        :label="$t('dataLoad.byMonth')"
                    />
                </div>
                <q-input
                    v-if="selectedOption === 'Por Data' && dateType === 'dia'"
                    v-model="date"
                    :label="$t('dataLoad.date')"
                    mask="##/##/####"
                    class="q-mt-md"
                />
                <q-input
                    v-if="selectedOption === 'Por Data' && dateType === 'mes'"
                    v-model="monthYear"
                    :label="$t('dataLoad.monthYear')"
                    mask="##/####"
                    class="q-mt-md"
                />
            </q-card-section>

            <q-card-section>
                <q-radio
                    v-model="dataType"
                    val="Remessas"
                    :label="$t('shipments.shipments')"
                />
                <q-icon
                    name="info"
                    class="cursor-pointer q-ml-sm"
                    @click="showInfo($t('shipments.shipments'))"
                />
                <q-radio
                    v-model="dataType"
                    val="Remessas e Volumes"
                    :label="$t('shipments.shipmentsAndVolumes')"
                />
                <q-icon
                    name="info"
                    class="cursor-pointer q-ml-sm"
                    @click="showInfo($t('shipments.shipmentsAndVolumes'))"
                />
            </q-card-section>

            <q-card-section v-if="selectedOption === 'Todos'">
                <q-radio
                    v-model="searchType"
                    val="search"
                    :label="$t('dataLoad.searchObject')"
                />
                <q-icon
                    name="info"
                    class="cursor-pointer q-ml-sm"
                    @click="showInfo($t('dataLoad.searchObject'))"
                />
                <q-radio
                    v-model="searchType"
                    val="clients"
                    :label="$t('client.clientReport')"
                />
                <q-icon
                    name="info"
                    class="cursor-pointer q-ml-sm"
                    @click="showInfo($t('client.clientReport'))"
                />
            </q-card-section>

            <q-card-section>
                <q-btn
                    :label="$t('button.load')"
                    color="primary"
                    class="q-mt-md"
                    :loading="loading"
                    @click="fetchData"
                />
                <q-btn
                    :label="$t('button.clearData')"
                    color="negative"
                    class="q-mt-md q-ml-md"
                    @click="clearData"
                />
            </q-card-section>

            <q-card-section>
                <div
                    v-if="output && output.length"
                    class="q-mt-md"
                >
                    <q-table
                        :rows="output"
                        :columns="columns"
                        row-key="clientId"
                    />
                </div>
                <div
                    v-else-if="output"
                    class="q-mt-md"
                >
                    <pre>{{ formattedOutput }}</pre>
                </div>
            </q-card-section>
        </q-card>

        <q-dialog v-model="infoDialog">
            <q-card>
                <q-card-section>
                    <div class="text-h6">
                        {{ infoTitle }}
                    </div>
                    <q-btn
                        icon="close"
                        flat
                        round
                        dense
                        class="absolute-top-right"
                        @click="infoDialog = false"
                    />
                </q-card-section>
                <q-card-section>
                    <p>{{ infoText }}</p>
                </q-card-section>
            </q-card>
        </q-dialog>
    </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { createAxiosInstance } from '../../api/axiosInstance';

const selectedOption = ref('Todos');
const dateType = ref('dia');
const dataType = ref('Remessas');
const searchType = ref('search');
const date = ref('');
const monthYear = ref('');
const output = ref(null);
const loading = ref(false);

const infoDialog = ref(false);
const infoTitle = ref('');
const infoText = ref('');

const environment = 'local';

const axiosInstance = createAxiosInstance(environment);

const columns = [
    {
        name: 'clientId',
        label: 'Client ID',
        field: 'clientId',
        align: 'left',
    },
    {
        name: 'sumShipments',
        label: 'Sum Shipments',
        field: 'sumShipments',
        align: 'left',
    },
    {
        name: 'sumUnitloads',
        label: 'Sum Unitloads',
        field: 'sumUnitloads',
        align: 'left',
    },
];

const fetchData = async () => {
    loading.value = true;
    let response;
    try {
        if (selectedOption.value === 'Todos') {
            response = await axiosInstance.get(
                dataType.value === 'Remessas'
                    ? `shipments/${searchType.value}`
                    : `shipmentsAndUnitloads/${searchType.value}`,
            );
        } else if (selectedOption.value === 'Por Data') {
            if (dateType.value === 'dia' && date.value) {
                const formattedDate = date.value.replace(/\//g, '-');
                response = await axiosInstance.get(
                    dataType.value === 'Remessas'
                        ? `shipments/clients/${formattedDate}`
                        : `shipmentsAndUnitloads/clients/${formattedDate}`,
                );
            } else if (dateType.value === 'mes' && monthYear.value) {
                const [month, year] = monthYear.value.split('/');
                const formattedMonthYear = `${month}-${year}`;
                response = await axiosInstance.get(
                    dataType.value === 'Remessas'
                        ? `shipments/clients/${formattedMonthYear}`
                        : `shipmentsAndUnitloads/clients/${formattedMonthYear}`,
                );
            }
        }
        output.value = response && response.data ? response.data : [];
        localStorage.setItem('outputData', JSON.stringify(output.value)); // Salva dados no localStorage
    } catch (error) {
        output.value = 'Erro ao buscar dados';
    } finally {
        loading.value = false;
    }
};

const clearData = async () => {
    try {
        await axiosInstance.get('clearData');
        output.value = 'Dados limpos com sucesso.';
        localStorage.removeItem('outputData'); // Remove dados do localStorage
    } catch (error) {
        output.value = 'Erro ao limpar dados';
    }
};

const formattedOutput = computed(() => (output.value && typeof output.value !== 'string'
    ? JSON.stringify(output.value, null, 2)
    : output.value));

const showInfo = (option) => {
    infoTitle.value = option;
    switch (option) {
        case 'Todos os Dados':
            infoText.value = 'Carrega todos os dados disponíveis.';
            break;
        case 'Por Data':
            infoText.value = 'Carrega dados de uma data específica.';
            break;
        case 'Remessas':
            infoText.value = 'Carrega apenas dados de remessas.';
            break;
        case 'Remessas e Volumes':
            infoText.value = 'Carrega dados de remessas e volumes.';
            break;
        case 'Pesquisar objeto':
            infoText.value = 'Extrai os dados das Remessa e Volumes permitindo a consulta dos mesmos.';
            break;
        case 'Relatório dos clientes':
            infoText.value = 'Além de extrair os dados, Gera relatório dos clientes.';
            break;
        default:
            infoText.value = 'Informações não disponíveis.';
            break;
    }
    infoDialog.value = true;
};

onMounted(() => {
    const savedData = localStorage.getItem('outputData');
    if (savedData) {
        output.value = JSON.parse(savedData);
    }
});
</script>

<style scoped>
.cursor-pointer {
    cursor: pointer;
}

.absolute-top-right {
    position: absolute;
    top: 10px;
    right: 10px;
}
</style>
