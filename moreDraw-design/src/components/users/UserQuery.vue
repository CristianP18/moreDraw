<template>
    <div>
        <q-card
            flat
            bordered
            class="q-ma-md"
        >
            <!-- Seção de Pesquisa -->
            <q-card-section>
                <q-option-group
                    v-model="selectedOption"
                    inline
                    :options="localizedSearchOptions"
                    option-type="radio"
                    size="sm"
                    class="q-mb-md"
                />

                <!-- Seção de Busca de Remessas -->
                <div v-if="selectedOption === 'fetchShipments'">
                    <q-option-group
                        v-model="selectedDateTimeOption"
                        inline
                        :options="localizedDateTimeOptions"
                        option-type="radio"
                        size="sm"
                        class="q-mb-md"
                    />

                    <div v-if="selectedDateTimeOption.includes('data')">
                        <q-input
                            v-model="startDate"
                            style="max-width: 150px;"
                            :label="$t('searchOptions.startDate')"
                            type="date"
                        />
                        <q-input
                            v-model="endDate"
                            style="max-width: 150px; margin-left: 200px; margin-top: -55px;"
                            :label="$t('searchOptions.endDate')"
                            type="date"
                        />
                    </div>

                    <div v-if="selectedDateTimeOption.includes('horario')">
                        <q-input
                            v-model="startTime"
                            style="max-width: 150px;"
                            :label="$t('searchOptions.selectTime')"
                            type="time"
                        />
                        <q-input
                            v-model="endTime"
                            style="max-width: 150px; margin-left: 200px; margin-top: -55px;"
                            :label="$t('searchOptions.selectTime')"
                            type="time"
                        />
                    </div>
                </div>
            </q-card-section>

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

            <!-- Exibição dos Resultados -->
            <q-card
                v-if="sumDeliveryBDriver.numberBoarding > 0"
                class="q-mt-md"
            >
                <!-- SumDeliveryBDriver -->
                <q-card-section>
                    <div class="text-h6">
                        {{ $t('delivery.sumDeliveryBDriverTitle') }}
                    </div>
                    <div class="q-mt-sm">
                        <p><strong>{{ $t('delivery.numberBoarding') }}:</strong> {{ sumDeliveryBDriver.numberBoarding }}</p>
                        <p><strong>{{ $t('delivery.numberSumShipments') }}:</strong> {{ sumDeliveryBDriver.numberSumShipments }}</p>
                        <p><strong>{{ $t('delivery.sumVolumeQuantity') }}:</strong> {{ sumDeliveryBDriver.sumVolumeQuantity }}</p>
                    </div>
                </q-card-section>

                <!-- Lista de Boardings -->
                <q-expansion-item
                    v-for="(boarding) in boardings"
                    :key="boarding.boardingId"
                    label="Boarding"
                    expand-separator
                    :header-class="'text-primary'"
                >
                    <template #header>
                        <div class="row items-center">
                            <div class="col-6">
                                <strong>{{ $t('boarding.boardingOrder') }}:</strong> {{ boarding.boardingOrder }}
                            </div>
                            <div class="col-6">
                                <strong>{{ $t('boarding.created') }}:</strong> {{ formatDate(boarding.created) }}
                            </div>
                        </div>
                    </template>

                    <!-- Detalhes do Boarding -->
                    <div class="q-pa-md">
                        <p><strong>{{ $t('boarding.driverId') }}:</strong> {{ boarding.driverId }}</p>
                    </div>

                    <!-- Remessas Associadas ao Boarding -->
                    <q-expansion-item
                        v-for="(shipment) in shipmentsByBoarding(boarding.boardingOrder)"
                        :key="shipment.shipmentId"
                        label="Shipment"
                        expand-separator
                        :header-class="'text-secondary'"
                    >
                        <template #header>
                            <div class="row items-center">
                                <div class="col-6">
                                    <strong>{{ $t('shipments.trackingCode') }}:</strong> {{ shipment.trackingCode }}
                                </div>
                                <div class="col-6">
                                    <strong>{{ $t('shipments.created') }}:</strong> {{ formatDate(shipment.created) }}
                                </div>
                            </div>
                        </template>

                        <!-- Detalhes da Remessa -->
                        <div class="q-pa-md">
                            <p><strong>{{ $t('shipments.nfeKey') }}:</strong> {{ shipment.nfeKey }}</p>
                            <p><strong>{{ $t('shipments.volumeQuantity') }}:</strong> {{ shipment.volumeQuantity }}</p>
                            <p><strong>{{ $t('shipments.state') }}:</strong> {{ shipment.state }}</p>
                            <!-- Adicione mais campos de Shipment conforme necessário -->
                        </div>
                    </q-expansion-item>
                </q-expansion-item>
            </q-card>
        </q-card>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useQuasar } from 'quasar';
import { createAxiosInstance } from '../../api/axiosInstance';

// Inicialização de i18n e Quasar
const { t } = useI18n();
const $q = useQuasar();

// Definição das opções de busca
const localizedSearchOptions = ref([
    { label: t('searchOptions.searchVehicles'), value: 'fetchVehicles' },
    { label: t('searchOptions.searchShipments'), value: 'fetchShipments' },
    { label: t('searchOptions.searchClients'), value: 'fetchCompanies' },
]);

const localizedDateTimeOptions = ref([
    { label: t('searchOptions.selectDate'), value: 'data' },
    { label: t('searchOptions.selectTime'), value: 'horario' },
]);

// Variáveis reativas para o formulário de busca
const selectedOption = ref('');
const selectedDateTimeOption = ref([]);
const startDate = ref(new Date().toISOString().substring(0, 10));
const endDate = ref(new Date().toISOString().substring(0, 10));
const startTime = ref('00:00');
const endTime = ref('23:59');
const loading = ref(false);
const searchResult = ref(null);

// Instância do Axios
const environment = 'local';
const axiosInstance = createAxiosInstance(environment);

// Estruturas para armazenar os dados recebidos
const sumDeliveryBDriver = ref({
    numberBoarding: 0,
    numberSumShipments: 0,
    sumVolumeQuantity: 0,
});

const boardings = ref([]);
const shipments = ref([]);

// Função para formatar datas
function formatDate(dateString) {
    const options = { year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit' };
    return new Date(dateString).toLocaleDateString(undefined, options);
}

// Função para filtrar remessas por boardingOrder
function shipmentsByBoarding(boardingOrder) {
    return shipments.value.filter((shipment) => shipment.boardingOrder === boardingOrder);
}

// Função para realizar a busca
async function performSearch() {
    loading.value = true;

    const token = localStorage.getItem('jwt');
    const tenantId = localStorage.getItem('userId');

    if (!token || !tenantId) {
        notifyError('Erro: Token ou Tenant ID não estão disponíveis.');
        loading.value = false;
        return;
    }

    try {
        let response;

        switch (selectedOption.value) {
            case 'fetchVehicles':
                response = await fetchVehicles(token, tenantId);
                break;
            case 'fetchShipments':
                response = await fetchShipments(token, tenantId);
                break;
            case 'fetchCompanies':
                response = await fetchCompanies(token, tenantId);
                break;
            default:
                notifyError('Opção de pesquisa inválida.');
                loading.value = false;
                return;
        }

        if (response && response.sumDeliveryBDriver.numberBoarding === 0) {
            notifyError('Nenhum resultado encontrado.');
        } else {
        // Atualiza as variáveis reativas com os dados recebidos
            sumDeliveryBDriver.value = response.sumDeliveryBDriver;
            boardings.value = response.boardings;
            shipments.value = response.shipments;
        }
    } catch (error) {
        console.error('Erro ao realizar a pesquisa:', error);
        notifyError(`Erro ao realizar a pesquisa: ${error.message}`);
    } finally {
        loading.value = false;
    }
}

// Função para notificar erro
function notifyError(message) {
    $q.notify({
        type: 'negative',
        message,
        position: 'top',
        timeout: 3000,
    });
}

// Funções para buscar dados do backend
async function fetchVehicles(token, tenantId) {
    try {
        const response = await axiosInstance.get(`userVehicles/${tenantId}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        // Supondo que a resposta contém sumDeliveryBDriver, boardings e shipments
        return response.data;
    } catch (error) {
        console.error('Erro ao buscar veículos:', error);
        notifyError('Erro ao buscar veículos.');
        throw error;
    }
}

async function fetchShipments(token, tenantId) {
    const beginDate = `${startDate.value}T${startTime.value}:00`;
    const endDateValue = `${endDate.value}T${endTime.value}:00`;

    try {
        const response = await axiosInstance.post(
            `usersByShipments/${tenantId}`,
            {
                begin: beginDate,
                end: endDateValue,
                type: 'driverId', // Aqui você está buscando por driverId
            },
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            },
        );

        // Supondo que a resposta contém sumDeliveryBDriver, boardings e shipments
        return response.data;
    } catch (error) {
        console.error('Erro ao buscar remessas enviadas:', error);
        notifyError('Erro ao buscar remessas enviadas.');
        throw error;
    }
}

async function fetchCompanies(token, tenantId) {
    try {
        const response = await axiosInstance.get(`userByCompanies/${tenantId}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        // Supondo que a resposta contém sumDeliveryBDriver, boardings e shipments
        return response.data;
    } catch (error) {
        console.error('Erro ao buscar empresas:', error);
        notifyError('Erro ao buscar empresas.');
        throw error;
    }
}
</script>

  <style scoped>
  /* Adicione estilos personalizados aqui, se necessário */
  </style>
