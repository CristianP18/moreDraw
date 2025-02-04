<template>
    <div class="q-pa-md">
        <spk-card
            flat
            bordered
            class="q-ma-md"
        >
            <spk-card-section>
                <div class="text-h6">
                    {{ $t('clientReport.billingByStateTitle') }}
                </div>
            </spk-card-section>

            <spk-card-section>
                <div class="row items-center spk-col-gutter-md">
                    <div class="col-auto">
                        <q-option-group
                            v-model="selectedStates"
                            :options="stateOptions"
                            option-type="checkbox"
                            inline
                            size="sm"
                            class="q-mb-md"
                        />
                    </div>

                    <!-- Seletor de custo por volume compacto e alinhado -->
                    <div
                        class="col-auto"
                        style="flex-shrink: 0;"
                    >
                        <q-input
                            v-model.number="costPerVolume"
                            label="Custo por Volume"
                            type="number"
                            :rules="[val => val >= 1 && val <= 12 || 'Escolha um valor entre 1 e 12']"
                            outlined
                            dense
                            style="max-width: 100px;"
                        />
                    </div>
                </div>

                <q-option-group
                    v-model="selectedDateTimeOption"
                    inline
                    :options="localizedDateTimeOptions"
                    option-type="radio"
                    size="sm"
                    class="q-mb-md"
                />
                <div class="row q-gutter-md">
                    <q-input
                        v-model="dateNew"
                        label="Date"
                        :loading="loading"
                        type="date"
                        class="col"
                        style="color: orange; max-width: 150px;"
                    />
                    <q-input
                        v-model="dateNew"
                        label="Date"
                        :loading="loading"
                        type="date"
                        class="col"
                        style="color: orange; max-width: 150px;"
                    />
                    <q-input
                        v-model="dateNew"
                        label="Date"
                        :loading="loading"
                        type="date"
                        class="col"
                        style="color: orange; max-width: 150px;"
                    />
                </div>

                <!-- Seletor de data e hora -->
                <div v-if="selectedDateTimeOption.includes('data')">
                    <q-input
                        v-model="startDate"
                        :label="$t('searchOptions.selectDate')"
                        type="date"
                    />
                    <q-input
                        v-model="endDate"
                        :label="$t('searchOptions.selectDate')"
                        type="date"
                    />
                </div>

                <div v-if="selectedDateTimeOption.includes('horario')">
                    <q-input
                        v-model="startTime"
                        :label="$t('searchOptions.selectTime')"
                        type="time"
                    />
                    <q-input
                        v-model="endTime"
                        :label="$t('searchOptions.selectTime')"
                        type="time"
                    />
                </div>
            </spk-card-section>
        </spk-card>

        <!-- Botão de Pesquisa Centralizado -->
        <div class="flex-center">
            <q-btn
                :loading="loading"
                :disable="loading"
                :label="$t('button.search')"
                color="primary"
                class="q-mt-md"
                @click="performSearch"
            >
                <template #loading>
                    <q-spinner color="white" />
                </template>
            </q-btn>
        </div>

        <!-- Exibição dos Resultados Agrupados por Estado em Tabela -->
        <spk-table
            v-if="filteredResults.length > 0"
            :rows="filteredResults"
            :columns="columns"
            row-key="state"
            class="q-mt-md"
        />

        <!-- Exibição da Soma Total dos Volumes e Valor da Fatura -->
        <spk-card-section
            v-if="filteredResults.length > 0"
            class="q-mt-md"
        >
            <div class="text-bold">
                {{ $t('clientReport.allUnitloads') }}: {{ totalVolume }}
            </div>
            <div class="text-bold">
                {{ $t('clientReport.invoiceValue') }}: R$ {{ totalBillingValue.toFixed(2) }}
            </div>
        </spk-card-section>

        <spk-card-section
            v-else-if="!loading && searchResult && searchResult.content && searchResult.content.length === 0"
            class="q-mt-md text-negative"
        >
            Nenhum resultado encontrado.
        </spk-card-section>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { format } from 'date-fns';
import { useI18n } from 'vue-i18n';
import { useQuasar } from 'quasar';
import { createAxiosInstance } from '../api/axiosInstance';

const { t } = useI18n();
const $q = useQuasar();

// Opções de filtro por estados
const stateOptions = [
    { label: '2', value: 2 },
    { label: '3', value: 3 },
    { label: '4', value: 4 },
    { label: '5', value: 5 },
    { label: '6', value: 6 },
    { label: '17', value: 17 },
];

// Inicialização para garantir que `selectedStates` seja sempre um array
const selectedStates = ref([]);

// Watch para garantir que `selectedStates` seja sempre um array
watch(selectedStates, (newValue) => {
    if (!Array.isArray(newValue)) {
        console.error('selectedStates was assigned a non-array value:', newValue);
        selectedStates.value = Array.isArray(newValue) ? newValue : [newValue]; // Corrige se não for array
    }
});

// Custo por volume selecionado
const costPerVolume = ref(1);

const localizedDateTimeOptions = ref([
    { label: t('searchOptions.selectDate'), value: 'data' },
    { label: t('searchOptions.selectTime'), value: 'horario' },
]);

const selectedDateTimeOption = ref([]);
const startDate = ref(format(new Date(Date.now() - 2 * 24 * 60 * 60 * 1000), 'yyyy-MM-dd'));
const endDate = ref(format(new Date(), 'yyyy-MM-dd'));
const startTime = ref('00:10');
const endTime = ref('23:50');
const searchResult = ref(null);
const loading = ref(false);
const dateNew = ref();
const environment = 'local';

const axiosInstance = createAxiosInstance(environment);

// Configuração das colunas da tabela
const columns = [
    { name: 'state', label: 'Estado', align: 'left', field: 'state' },
    { name: 'totalVolume', label: 'Volume Total', align: 'right', field: 'totalVolume' },
];

// Computed para agrupar, somar volumes e aplicar o filtro de estado
const groupedResults = computed(() => {
    if (!searchResult.value || !searchResult.value.content) return [];

    const grouped = searchResult.value.content.reduce((acc, item) => {
        if (!item.state) return acc; // Ignorar se o estado não estiver presente
        if (!acc[item.state]) {
            acc[item.state] = { state: item.state, totalVolume: 0 };
        }
        acc[item.state].totalVolume += item.volumeQuantity || 0;
        return acc;
    }, {});

    return Object.values(grouped);
});

// Filtrar resultados baseados nos estados selecionados
const filteredResults = computed(() => {
    if (!Array.isArray(selectedStates.value)) {
        console.error('selectedStates is not an array:', selectedStates.value);
        return [];
    }

    const results = groupedResults.value.filter((result) => result.state != null && selectedStates.value.includes(result.state));

    console.log('Updated filtered results:', results);
    return results;
});

// Soma total dos volumes de todos os resultados filtrados
const totalVolume = computed(() => filteredResults.value.reduce((sum, result) => sum + result.totalVolume, 0));

// Cálculo do valor da fatura, ajustando costPerVolume para um decimal
const totalBillingValue = computed(() => totalVolume.value * (costPerVolume.value / 100));

async function performSearch() {
    loading.value = true;
    searchResult.value = null; // Reset searchResult before the request
    const tenantId = localStorage.getItem('tenantId');
    const token = localStorage.getItem('jwt');

    if (!token || !tenantId) {
        notifyError('Erro: Token ou tenantId não estão disponíveis.');
        loading.value = false;
        return;
    }

    try {
        const response = await fetchTransportedShipments(token, tenantId);
        searchResult.value = { content: response.data || [] }; // Assegura que o content seja sempre um array

        if (!searchResult.value.content.length) {
            notifyError('Nenhum resultado encontrado.');
        }
    } catch (error) {
        notifyError(`Erro ao realizar a pesquisa: ${error.message}`);
    } finally {
        loading.value = false; // Ensure loading is stopped
    }
}

function notifyError(message) {
    $q.notify({
        type: 'negative',
        message,
        position: 'top',
        timeout: 3000,
    });
}

async function fetchTransportedShipments(token, tenantId) {
    const beginDate = `${startDate.value}T${startTime.value}:00`;
    const endDateValue = `${endDate.value}T${endTime.value}:00`;

    try {
        const response = await axiosInstance.post(
            `shipmentsByEntitiByDate/${tenantId}`,
            {
                begin: beginDate,
                end: endDateValue,
                type: 'transporterId',
            },
        );
        console.log('API response content:', response.data);
        return response;
    } catch (error) {
        notifyError('Erro ao buscar remessas transportadas.');
        throw error; // Repassa o erro para que `performSearch` possa capturá-lo
    }
}
</script>
