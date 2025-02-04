<template>
    <q-page class="q-pa-md">
        <q-card>
            <q-card-section>
                <div class="text-h6">
                    {{ $t('clientReport.clientReportTitle') }}
                </div>
            </q-card-section>

            <q-card-section>
                <q-input
                    v-model="clientId"
                    :label="$t('clientReport.clientId')"
                />

                <q-btn
                    :label="$t('button.search')"
                    color="primary"
                    class="q-mt-md"
                    :loading="loading"
                    :disable="loading || !clientId"
                    @click="fetchClientReport"
                >
                    <template #loading>
                        <q-spinner color="white" />
                    </template>
                </q-btn>

                <q-btn
                    :label="$t('clientReport.viewDrivers')"
                    color="secondary"
                    class="q-mt-md q-ml-md"
                    :loading="loading"
                    :disable="loading || !clientId"
                    @click="fetchDrivers"
                >
                    <template #loading>
                        <q-spinner color="white" />
                    </template>
                </q-btn>
            </q-card-section>

            <q-card-section>
                <div
                    v-if="client"
                    class="q-mt-md"
                >
                    <q-list>
                        <q-item>
                            <q-item-section>{{ $t('clientReport.clientId') }}: {{ client.clientId }}</q-item-section>
                        </q-item>
                        <q-item>
                            <q-item-section>
                                {{ $t('clientReport.totalShipments') }}: {{ client.sumShipments }}
                            </q-item-section>
                        </q-item>
                        <q-item>
                            <q-item-section>
                                {{ $t('clientReport.totalUnitloads') }}: {{ client.sumUnitloads }}
                            </q-item-section>
                        </q-item>
                    </q-list>

                    <q-btn
                        :label="$t('clientReport.viewShipments')"
                        color="primary"
                        class="q-mt-md"
                        @click="toggleShipments"
                    />
                    <q-btn
                        :label="$t('clientReport.viewUnitloads')"
                        color="primary"
                        class="q-mt-md q-ml-md"
                        @click="toggleUnitloads"
                    />

                    <div
                        v-if="showShipments"
                        class="q-mt-md"
                    >
                        <div class="text-subtitle2">
                            {{ $t('clientReport.consolidatedShipments') }}
                        </div>
                        <q-list style="max-height: 400px; overflow-y: auto">
                            <q-item
                                v-for="shipment in combinedShipments"
                                :key="shipment.shipmentId"
                                clickable
                                @click="toggleShipmentDetails(shipment)"
                            >
                                <q-item-section>{{ shipment.shipmentId }}</q-item-section>
                            </q-item>
                        </q-list>
                    </div>

                    <div
                        v-if="showUnitloads"
                        class="q-mt-md"
                    >
                        <div class="text-subtitle2">
                            {{ $t('clientReport.unitloads') }}
                        </div>
                        <q-list style="max-height: 400px; overflow-y: auto">
                            <q-item
                                v-for="unitload in client.unitloads"
                                :key="unitload.unitloadId"
                                clickable
                                @click="toggleUnitloadDetails(unitload)"
                            >
                                <q-item-section>{{ unitload.unitloadId }}</q-item-section>
                            </q-item>
                        </q-list>
                    </div>
                </div>

                <div
                    v-if="drivers.length"
                    class="q-mt-md"
                >
                    <div class="text-h6">
                        {{ $t('clientReport.driverDetails') }}
                    </div>
                    <q-list>
                        <q-item
                            v-for="driver in drivers"
                            :key="driver.driverId"
                        >
                            <q-item-section>
                                <div>{{ $t('clientReport.driverId') }}: {{ driver.driverId }}</div>
                                <div>{{ $t('clientReport.totalShipments') }}: {{ driver.totalShipments }}</div>
                            </q-item-section>
                        </q-item>
                    </q-list>
                </div>

                <div
                    v-else-if="error"
                    class="q-mt-md text-negative"
                >
                    {{ error }}
                </div>
            </q-card-section>

            <!-- Dialogs for detailed information -->
            <q-dialog
                v-model="isShipmentDialogOpen"
                persistent
            >
                <q-card>
                    <q-card-section class="row items-center">
                        <div class="col">
                            {{ $t('clientReport.shipmentDetailsTitle') }}
                        </div>
                        <q-btn
                            icon="close"
                            flat
                            round
                            dense
                            @click="isShipmentDialogOpen = false"
                        />
                    </q-card-section>

                    <q-card-section v-if="selectedShipment">
                        <!-- Shipment details -->
                        <div>
                            <q-list>
                                <q-item>
                                    <q-item-section>
                                        {{ $t('shipments.shipmentDetails') }} ID: {{ selectedShipment.shipmentId }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>{{ $t('shipments.nfeKey') }}: {{ selectedShipment.nfeKey }}</q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        {{ $t('shipments.createdBy') }}: {{ selectedShipment.createdBy }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        {{ $t('shipments.shipperId') }}: {{ selectedShipment.shipperId }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        {{ $t('shipments.transporterId') }}: {{ selectedShipment.transporterId }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        {{ $t('shipments.destinatorId') }}: {{ selectedShipment.destinatorId }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        {{ $t('shipments.orderNumber') }}: {{ selectedShipment.orderNumber }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        {{ $t('shipments.trackingCode') }}: {{ selectedShipment.trackingCode }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>{{ $t('shipments.priority') }}: {{ selectedShipment.priority }}</q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        {{ $t('shipments.volumeQuantity') }}: {{ selectedShipment.volumeQuantity }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>{{ $t('shipments.created') }}: {{ selectedShipment.created }}</q-item-section>
                                </q-item>
                            </q-list>
                        </div>
                        <!-- Other fields -->
                        <!-- Additional fields can be listed here similar to above -->
                    </q-card-section>
                </q-card>
            </q-dialog>

            <q-dialog
                v-model="isUnitloadDialogOpen"
                persistent
            >
                <q-card>
                    <q-card-section class="row items-center">
                        <div class="col">
                            {{ $t('clientReport.unitloadDetailsTitle') }}
                        </div>
                        <q-btn
                            icon="close"
                            flat
                            round
                            dense
                            @click="isUnitloadDialogOpen = false"
                        />
                    </q-card-section>

                    <q-card-section v-if="selectedUnitload">
                        <!-- Unitload details -->
                        <div>
                            <q-list>
                                <q-item>
                                    <q-item-section>
                                        Unitload ID: {{ selectedUnitload.unitloadId }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        Created By: {{ selectedUnitload.createdBy }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        Shipment ID: {{ selectedUnitload.shipmentId }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        Unitload Name: {{ selectedUnitload.unitloadName }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>Bar Code: {{ selectedUnitload.barCode }}</q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        External Code: {{ selectedUnitload.externalCode }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        Risk Class: {{ selectedUnitload.riskClass }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        Package Type: {{ selectedUnitload.packageType }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        Opened: {{ selectedUnitload.opened ? "Yes" : "No" }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        Stacking Weight: {{ selectedUnitload.stackingWeight }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>Width: {{ selectedUnitload.width }}</q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>Height: {{ selectedUnitload.height }}</q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>Depth: {{ selectedUnitload.depth }}</q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>Weight: {{ selectedUnitload.weight }}</q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>
                                        Weight Calculated:
                                        {{ selectedUnitload.weightCalculated }}
                                    </q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>Quantity: {{ selectedUnitload.quantity }}</q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>Amount: {{ selectedUnitload.amount }}</q-item-section>
                                </q-item>
                                <q-item>
                                    <q-item-section>Created: {{ selectedUnitload.created }}</q-item-section>
                                </q-item>
                            </q-list>
                        </div>
                        <!-- Additional fields similar to above -->
                    </q-card-section>
                </q-card>
            </q-dialog>
        </q-card>
    </q-page>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Notify } from 'quasar';
import { createAxiosInstance } from '../../api/axiosInstance';

const environment = 'local';

const axiosInstance = createAxiosInstance(environment);

const clientId = ref('');
const client = ref(null);
const error = ref('');
const showShipments = ref(false);
const showUnitloads = ref(false);
const selectedShipment = ref(null);
const selectedUnitload = ref(null);
const isShipmentDialogOpen = ref(false);
const isUnitloadDialogOpen = ref(false);
const drivers = ref([]);
const loading = ref(false);

const fetchClientReport = async () => {
    client.value = null;
    error.value = '';
    showShipments.value = false;
    showUnitloads.value = false;
    selectedShipment.value = null;
    selectedUnitload.value = null;
    loading.value = true;
    try {
        const response = await axiosInstance.get(`shipments/client/${clientId.value}`);
        if (response && response.data) {
            client.value = response.data;
            Notify.create({
                message: 'Relatório do cliente obtido com sucesso.',
                color: 'positive',
                position: 'top',
            });
        } else {
            error.value = 'Nenhum dado encontrado para este ID de cliente.';
        }
    } catch (err) {
        console.error('Erro ao buscar relatório do cliente:', err);
        error.value = 'Erro ao buscar relatório do cliente.';
        Notify.create({
            message: 'Erro ao buscar relatório do cliente.',
            color: 'negative',
            position: 'top',
        });
    } finally {
        loading.value = false;
    }
};

const combinedShipments = computed(() => {
    const allShipments = new Set();
    if (client.value) {
        ['shipperShipments', 'destinatorShipments', 'transporterShipments'].forEach(
            (group) => {
                client.value[group]?.forEach((shipment) => {
                    if (!allShipments.has(shipment.shipmentId)) {
                        allShipments.add(shipment);
                    }
                });
            },
        );
    }
    return Array.from(allShipments);
});

const fetchDrivers = async () => {
    if (!clientId.value) {
        error.value = 'Por favor, insira um ID de cliente válido.';
        return;
    }
    loading.value = true;
    drivers.value = [];
    try {
        const response = await axiosInstance.get(`driverByClient/${clientId.value}`);
        await Promise.all(
            data.map(async (driverId) => {
                const response = await axiosInstance.get(`driverSumDeliveryOld/${driverId}`);
                return {
                    driverId,
                    totalShipments: res.data.length,
                };
            }),
        ).then((results) => {
            drivers.value = results;
            Notify.create({
                message: 'Informações dos motoristas obtidas com sucesso.',
                color: 'positive',
                position: 'top',
            });
        });
    } catch (err) {
        console.error('Erro ao buscar informações do motorista:', err);
        error.value = 'Erro ao buscar informações do motorista.';
        Notify.create({
            message: 'Erro ao buscar informações do motorista.',
            color: 'negative',
            position: 'top',
        });
    } finally {
        loading.value = false;
    }
};

const toggleShipments = () => {
    showShipments.value = !showShipments.value;
};

const toggleUnitloads = () => {
    showUnitloads.value = !showUnitloads.value;
};

const toggleShipmentDetails = (shipment) => {
    selectedShipment.value = shipment;
    isShipmentDialogOpen.value = true;
};

const toggleUnitloadDetails = (unitload) => {
    selectedUnitload.value = unitload;
    isUnitloadDialogOpen.value = true;
};
</script>
