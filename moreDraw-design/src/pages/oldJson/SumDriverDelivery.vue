<template>
    <q-page class="q-pa-md">
        <q-card>
            <q-card-section>
                <div class="text-h6">
                    {{ $t('deliveries.searchDriverDeliveriesTitle') }}
                </div>
            </q-card-section>

            <q-card-section>
                <q-input
                    v-model="driverId"
                    :label="$t('deliveries.searchDriverDeliveriesInput')"
                />

                <q-btn
                    :label="$t('button.search')"
                    color="primary"
                    class="q-mt-md"
                    :loading="loading"
                    :disable="loading || !driverId"
                    @click="fetchDriverDeliveries"
                >
                    <template #loading>
                        <q-spinner color="white" />
                    </template>
                </q-btn>
            </q-card-section>

            <q-card-section>
                <div
                    v-if="error"
                    class="q-mt-md text-negative"
                >
                    {{ error }}
                </div>
                <div
                    v-if="shipments.length"
                    class="q-mt-md"
                >
                    <div>{{ $t('deliveries.totalShipments') }}: {{ totalShipments }}</div>
                    <!-- Mostra o total de remessas -->
                    <q-list style="max-height: 400px; overflow-y: auto">
                        <q-item
                            v-for="shipment in shipments"
                            :key="shipment.shipmentId"
                        >
                            <q-item-section>
                                <strong>{{ $t('deliveries.shipmentId') }}:</strong> {{ shipment.shipmentId }}
                            </q-item-section>
                            <q-item-section>
                                <strong>{{ $t('deliveries.boardingOrder') }}:</strong> {{ shipment.boardingOrder }}
                            </q-item-section>
                        </q-item>
                    </q-list>
                </div>
                <div
                    v-else-if="!loading"
                    class="q-mt-md"
                >
                    {{ $t('deliveries.noShipmentsFound') }}
                </div>
            </q-card-section>
        </q-card>
    </q-page>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Notify } from 'quasar';
import { createAxiosInstance } from '../../api/axiosInstance';

const driverId = ref('');
const shipments = ref([]);
const error = ref('');
const loading = ref(false);

const environment = 'local';

const axiosInstance = createAxiosInstance(environment);

const totalShipments = computed(() => shipments.value.length);

const fetchDriverDeliveries = async () => {
    loading.value = true;
    error.value = '';
    shipments.value = [];

    if (!driverId.value) {
        Notify.create({
            message: 'Por favor, insira um ID de motorista válido.',
            color: 'negative',
            position: 'top',
        });
        loading.value = false;
        return;
    }

    try {
        const response = await axiosInstance.get(`driverSumDeliveryOld/${driverId.value}`);
        shipments.value = response.data;

        if (shipments.value.length) {
            Notify.create({
                message: 'Entregas encontradas com sucesso.',
                color: 'positive',
                position: 'top',
            });
        } else {
            Notify.create({
                message: 'Nenhuma entrega encontrada para este motorista.',
                color: 'warning',
                position: 'top',
            });
        }
    } catch (err) {
        error.value = `Erro ao buscar entregas: ${
            err.response?.data?.message || err.message}`;
        Notify.create({
            message: error.value,
            color: 'negative',
            position: 'top',
        });
    } finally {
        loading.value = false;
    }
};
</script>
