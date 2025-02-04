<template>
    <div class="q-pa-md">
        <spk-card
            flat
            bordered
            class="q-ma-md"
        >
            <spk-card-section>
                <div class="text-h6">
                    {{ $t('clientReport.billingTitle') }}
                </div>
            </spk-card-section>

            <spk-card-section>
                <div class="row items-center spk-col-gutter-md">
                    <!-- Seletor de data e hora -->
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

                    <q-input
                        v-model="startTime"
                        style="min-width: 120px;"
                        :label="$t('searchOptions.selectTime')"
                        type="time"
                    />
                    <q-input
                        v-model="endTime"
                        style="min-width: 120px;"
                        :label="$t('searchOptions.selectTime')"
                        type="time"
                    />

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
                            style="max-width: 110px; margin-left: 12px; margin-top: 35px;"
                        />
                    </div>
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

        <!-- Seletor de Estados para Filtrar Resultados -->
        <spk-card
            v-if="searchResult && searchResult.content.length > 0"
            flat
            bordered
            class="q-ma-md q-mt-md"
        >
            <!-- Exibição da Soma Total dos Volumes e Valor da Fatura -->
            <spk-card-section class="q-mt-md">
                <div class="text-bold">
                    {{ $t('clientReport.allUnitloads') }}: {{ totalVolume }}
                </div>
                <div class="text-bold">
                    {{ $t('clientReport.invoiceValue') }}: R$ {{ totalBillingValue.toFixed(2) }}
                </div>
            </spk-card-section>
        </spk-card>

        <spk-card-section
            v-else-if="!loading && searchResult && searchResult.content.length === 0"
            class="q-mt-md text-negative"
        >
            {{ $t('clientReport.noResultsFound') }}
        </spk-card-section>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { format } from 'date-fns';
import { useI18n } from 'vue-i18n';
import { useQuasar } from 'quasar';
import { createAxiosInstance } from '../../api/axiosInstance';

const { t } = useI18n();
const $q = useQuasar();

// Opções de filtro por estados
const stateOptions = Array.from({ length: 18 }, (_, i) => ({ label: i.toString(), value: i }));

// Estado selecionado para filtro
const selectedState = ref(null);

// Custo por volume selecionado
const costPerVolume = ref(1);

const startDate = ref(format(new Date(Date.now() - 2 * 24 * 60 * 60 * 1000), 'yyyy-MM-dd'));
const endDate = ref(format(new Date(), 'yyyy-MM-dd'));
const startTime = ref('00:10');
const endTime = ref('23:50');
const searchResult = ref(null);
const loading = ref(false);

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

    // Reduzindo os dados recebidos para agrupar por estado e somar volumes
    const grouped = searchResult.value.content.reduce((acc, item) => {
        if (!item.state) return acc;
        if (!acc[item.state]) {
            acc[item.state] = { state: item.state, totalVolume: 0 };
        }
        acc[item.state].totalVolume += item.volumeQuantity || 0;
        return acc;
    }, {});

    return Object.values(grouped);
});

// Filtrar resultados baseados no estado selecionado
const filteredResults = computed(() => {
    if (selectedState.value === null) {
        return groupedResults.value;
    }
    return groupedResults.value.filter((result) => result.state === selectedState.value);
});

// Soma total dos volumes de todos os resultados filtrados
const totalVolume = computed(() => filteredResults.value.reduce((sum, result) => sum + result.totalVolume, 0));

// Cálculo do valor da fatura, ajustando costPerVolume para um decimal
const totalBillingValue = computed(() => totalVolume.value
* (costPerVolume.value / 100));

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
                type: 'finished',
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

<style scoped>
.flex-center {
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>
