<template>
    <spk-card
        flat
        bordered
        class="q-ma-md"
    >
        <spk-card-section>
            <div class="text-h6">
                {{ $t('searchOptions.searchClients') }}
            </div>
        </spk-card-section>

        <spk-card-section>
            <q-option-group
                v-model="selectedOption"
                inline
                :options="localizedSearchOptions"
                option-type="radio"
                size="sm"
                class="q-mb-md"
            />

            <!-- Campo de input para Evento de Remessa -->
            <div
                v-if="selectedOption === 'fetchShipmentEvents'"
                style="width: 300px;"
            >
                <q-input
                    v-model="shipmentEventInput"
                    :label="$t('searchOptions.searchShipmentEvents')"
                />
            </div>

            <!-- Seção de Busca de Remessas -->
            <div v-if="selectedOption === 'fetchShipments'">
                <q-option-group
                    v-model="selectedShipmentOption"
                    inline
                    :options="localizedShipmentOptions"
                    option-type="radio"
                    size="sm"
                    class="q-mb-md"
                />

                <!-- Seletor de data e hora -->
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
                        :label="$t('searchOptions.selectDate')"
                        type="date"
                    />
                    <q-input
                        v-model="endDate"
                        style="max-width: 150px; margin-left: 200px; margin-top: -55px;"
                        :label="$t('searchOptions.selectDate')"
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
        </spk-card-section>

        <!-- Exibição dos Resultados de Eventos de Remessas -->
        <spk-card
            v-if="selectedOption === 'fetchShipmentEvents' && searchResult"
            class="q-mb-md"
        >
            <spk-card-section
                v-for="(event, index) in searchResult"
                :key="index"
                class="q-mb-md"
            >
                <div class="row">
                    <div class="col-6">
                        <div><strong>{{ $t('logs.eventCode') }}:</strong> {{ event.shipmentEventId }}</div>
                        <div><strong>{{ $t('logs.message') }}:</strong> {{ event.transporterMessage }}</div>
                        <div><strong>{{ $t('logs.date') }}:</strong> {{ event.created }}</div>
                    </div>
                    <div class="col-6">
                        <div v-if="event.latitude">
                            <strong>{{ $t('logs.latitude') }}:</strong> {{ event.latitude }}
                        </div>
                        <div v-if="event.longitude">
                            <strong>{{ $t('logs.longitude') }}:</strong> {{ event.longitude }}
                        </div>
                        <div v-if="event.plusCode">
                            <strong>{{ $t('logs.plusCode') }}:</strong> {{ event.plusCode }}
                        </div>
                    </div>
                </div>
            </spk-card-section>
        </spk-card>

        <!-- Exibição dos Resultados de Clientes -->
        <spk-card
            v-if="selectedOption === 'fetchClients' && searchResult && searchResult.content"
            class="q-mb-md"
        >
            <spk-card-section
                v-for="(client, index) in searchResult.content"
                :key="index"
                class="q-mb-md"
            >
                <div class="row">
                    <div class="col-6">
                        <div><strong>{{ $t('logs.name') }}:</strong> {{ client.companyName }}</div>
                        <div><strong>{{ $t('logs.cnpj') }}:</strong> {{ client.federalTax }}</div>
                    </div>
                    <div class="col-6">
                        <div><strong>{{ $t('logs.phone') }}:</strong> {{ client.phone }}</div>
                        <div><strong>{{ $t('logs.email') }}:</strong> {{ client.email }}</div>
                    </div>
                </div>
            </spk-card-section>
        </spk-card>

        <!-- Exibição dos Resultados de Usuários -->
        <spk-card
            v-if="selectedOption === 'fetchUsers' && searchResult && searchResult.content"
            class="q-mb-md"
        >
            <spk-card-section
                v-for="(user, index) in searchResult.content"
                :key="index"
                class="q-mb-md"
            >
                <div class="row">
                    <div class="col-6">
                        <div><strong>{{ $t('logs.name') }}:</strong> {{ user.user.firstName }} {{ user.user.lastName }}</div>
                        <div><strong>{{ $t('logs.email') }}:</strong> {{ user.user.email }}</div>
                    </div>
                    <div class="col-6">
                        <div><strong>{{ $t('logs.phone') }}:</strong> {{ user.user.mobilePhone }}</div>
                        <div><strong>{{ $t('logs.cpf') }}:</strong> {{ user.user.federalTax }}</div>
                    </div>
                </div>
            </spk-card-section>
        </spk-card>

        <!-- Exibição dos Resultados de Veículos -->
        <spk-card
            v-if="selectedOption === 'fetchVehicles' && searchResult && searchResult.content"
            class="q-mb-md"
        >
            <spk-card-section
                v-for="(vehicle, index) in searchResult.content"
                :key="index"
                class="q-mb-md"
            >
                <div class="row">
                    <div class="col-6">
                        <div><strong>{{ $t('logs.licensePlate') }}:</strong> {{ vehicle.licensePlate }}</div>
                        <div><strong>{{ $t('logs.model') }}:</strong> {{ vehicle.model }}</div>
                    </div>
                    <div class="col-6">
                        <div><strong>{{ $t('logs.color') }}:</strong> {{ vehicle.color }}</div>
                        <div><strong>{{ $t('logs.driverName') }}:</strong> {{ vehicle.driverName }}</div>
                    </div>
                </div>
            </spk-card-section>
        </spk-card>

        <!-- Exibição dos Resultados de Remessas -->
        <spk-card
            v-if="selectedOption === 'fetchShipments' && searchResult && searchResult.content"
            class="q-mb-md"
        >
            <spk-card-section
                v-for="(shipment, index) in searchResult.content"
                :key="index"
                class="q-mb-md"
            >
                <div
                    class="row"
                    @click="expandShipment(index)"
                >
                    <div class="col-6">
                        <div><strong>{{ $t('logs.trackingCode') }}:</strong> {{ shipment.trackingCode }}</div>
                        <div><strong>{{ $t('logs.nfeKey') }}:</strong> {{ shipment.nfeKey }}</div>
                    </div>
                    <div class="col-6">
                        <div><strong>{{ $t('logs.volumeQuantity') }}:</strong> {{ shipment.volumeQuantity }}</div>
                        <div><strong>{{ $t('logs.created') }}:</strong> {{ shipment.created }}</div>
                    </div>
                </div>
                <spk-card-section v-if="expandedShipment === index">
                    <div><strong>{{ $t('logs.state') }}:</strong> {{ shipment.state }}</div>
                    <div><strong>{{ $t('logs.distance') }}:</strong> {{ shipment.distance }} km</div>
                    <div><strong>{{ $t('logs.schedMode') }}:</strong> {{ shipment.schedMode }}</div>
                    <div><strong>{{ $t('logs.salesChannel') }}:</strong> {{ shipment.salesChannel }}</div>
                </spk-card-section>
            </spk-card-section>
        </spk-card>
    </spk-card>
</template>

<script setup>
import { ref } from 'vue';
import { format } from 'date-fns';
import { useI18n } from 'vue-i18n';
import { useQuasar } from 'quasar';
import { createAxiosInstance } from '../../api/axiosInstance';

const axiosInstance = createAxiosInstance(environment);

const { t } = useI18n();
const $q = useQuasar();

const localizedSearchOptions = ref([
    { label: t('searchOptions.searchClients'), value: 'fetchClients' },
    { label: t('searchOptions.searchUsers'), value: 'fetchUsers' },
    { label: t('searchOptions.searchVehicles'), value: 'fetchVehicles' },
    { label: t('searchOptions.searchShipmentEvents'), value: 'fetchShipmentEvents' },
    { label: t('searchOptions.searchShipments'), value: 'fetchShipments' },
]);

const localizedShipmentOptions = ref([
    { label: t('searchOptions.sentShipments'), value: 'fetchSentShipments' },
    { label: t('searchOptions.transportedShipments'), value: 'fetchTransportedShipments' },
    { label: t('searchOptions.shipmentsToReceive'), value: 'fetchDestinator' },
]);

const localizedDateTimeOptions = ref([
    { label: t('searchOptions.selectDate'), value: 'data' },
    { label: t('searchOptions.selectTime'), value: 'horario' },
]);

const selectedOption = ref('');
const selectedShipmentOption = ref('');
const selectedDateTimeOption = ref([]);
const shipmentEventInput = ref('');
const startDate = ref(format(new Date(Date.now() - 2 * 24 * 60 * 60 * 1000), 'yyyy-MM-dd'));
const endDate = ref(format(new Date(), 'yyyy-MM-dd'));
const startTime = ref('00:10');
const endTime = ref('23:50');
const searchResult = ref(null);
const loading = ref(false);
const expandedShipment = ref(null);
const environment = 'local';

async function performSearch() {
    loading.value = true;

    const tenantId = localStorage.getItem('tenantId');
    const token = localStorage.getItem('jwt');

    if (!token || !tenantId) {
        notifyError('Erro: Token ou tenantId não estão disponíveis.');
        loading.value = false;
        return;
    }

    try {
        switch (selectedOption.value) {
            case 'fetchClients':
                await fetchClients(token, tenantId);
                break;
            case 'fetchUsers':
                await fetchUsers(token, tenantId);
                break;
            case 'fetchVehicles':
                await fetchVehicles(token, tenantId);
                break;
            case 'fetchShipmentEvents':
                await fetchShipmentEvents(token, shipmentEventInput.value);
                break;
            case 'fetchShipments':
                await performShipmentSearch(token, tenantId);
                break;
            default:
                notifyError('Opção de pesquisa inválida.');
        }

        if (!searchResult.value || !searchResult.value.content || searchResult.value.content.length === 0) {
            notifyError('Nenhum resultado encontrado.');
        }
    } catch (error) {
        notifyError(`Erro ao realizar a pesquisa: ${error.message}`);
    } finally {
        loading.value = false;
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

// Funções de busca para cada caso

async function fetchClients(token, tenantId) {
    try {
        const response = await axiosInstance.get(`clientByClient/${tenantId}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        searchResult.value = response.data;
    } catch (error) {
        notifyError('Erro ao buscar clientes.');
    }
}

async function fetchUsers(token, tenantId) {
    try {
        const response = await axiosInstance.get(`clientByUsers/${tenantId}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        searchResult.value = response.data;
    } catch (error) {
        notifyError('Erro ao buscar usuários.');
    }
}

async function fetchVehicles(token, tenantId) {
    try {
        const response = await axiosInstance.get(`clientByVehicle/${tenantId}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        searchResult.value = response.data;
    } catch (error) {
        notifyError('Erro ao buscar veículos.');
    }
}

async function fetchShipmentEvents(token, eventCode) {
    try {
        const tenantId = localStorage.getItem('tenantId');

        if (!tenantId) {
            throw new Error('tenantId não está definido no localStorage');
        }

        if (!eventCode) {
            throw new Error('Código de evento não está definido');
        }

        const response = await axiosInstance.get(
            `eventsByShipment/${tenantId}`,
            {
                shipmentId: eventCode,
            },
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            },
        );

        searchResult.value = response.data.content || [];
    } catch (error) {
        notifyError(`Erro ao buscar eventos de remessa: ${error.response ? error.response.data : error.message}`);
    }
}

async function performShipmentSearch(token, tenantId) {
    const beginDate = selectedDateTimeOption.value.includes('data')
        ? `${startDate.value}T${startTime.value}:00`
        : `${format(new Date(Date.now() - 2 * 24 * 60 * 60 * 1000), 'yyyy-MM-dd')}T00:10:00`;

    const endDateValue = selectedDateTimeOption.value.includes('data')
        ? `${endDate.value}T${endTime.value}:00`
        : `${format(new Date(), 'yyyy-MM-dd')}T23:50:00`;

    const finalBeginDate = selectedDateTimeOption.value.includes('horario')
        ? `${startDate.value}T${startTime.value}:00`
        : beginDate;

    const finalEndDate = selectedDateTimeOption.value.includes('horario')
        ? `${endDate.value}T${endTime.value}:00`
        : endDateValue;

    switch (selectedShipmentOption.value) {
        case 'fetchSentShipments':
            await fetchSentShipments(token, tenantId, finalBeginDate, finalEndDate);
            break;
        case 'fetchTransportedShipments':
            await fetchTransportedShipments(token, tenantId, finalBeginDate, finalEndDate);
            break;
        case 'fetchDestinator':
            await fetchDestinator(token, tenantId, finalBeginDate, finalEndDate);
            break;
        default:
            notifyError('Opção de remessa inválida.');
    }
}

// Funções de busca para remessas
async function fetchSentShipments(token, tenantId, beginDate, endDate) {
    try {
        const response = await axiosInstance.post(
            `shipmentsByEntitiByDate/${tenantId}`,
            {
                begin: `${beginDate}`,
                end: `${endDate}`,
                type: 'shipperId',
            },
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            },
        );
        searchResult.value = { content: response.data };
    } catch (error) {
        notifyError('Erro ao buscar remessas enviadas.');
    }
}

async function fetchTransportedShipments(token, tenantId, beginDate, endDate) {
    try {
        const response = await axiosInstance.post(
            `/shipmentsByEntitiByDate/${tenantId}`,
            {
                begin: `${beginDate}`,
                end: `${endDate}`,
                type: 'transporterId',
            },
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            },
        );
        searchResult.value = { content: response.data };
    } catch (error) {
        notifyError('Erro ao buscar remessas transportadas.');
    }
}

async function fetchDestinator(token, tenantId, beginDate, endDate) {
    try {
        const response = await axiosInstance.post(
            `shipmentsByEntitiByDate/${tenantId}`,
            {
                begin: `${beginDate}`,
                end: `${endDate}`,
                type: 'destinatorId',
            },
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            },
        );
        searchResult.value = { content: response.data };
    } catch (error) {
        notifyError('Erro ao buscar remessas destinadas.');
    }
}

function expandShipment(index) {
    expandedShipment.value = expandedShipment.value === index ? null : index;
}
</script>
