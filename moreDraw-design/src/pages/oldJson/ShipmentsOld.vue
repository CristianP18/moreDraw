<template>
    <q-page class="q-pa-md">
        <q-card>
            <q-card-section>
                <div class="text-h6">
                    {{ $t('shipments.searchOptionsTitle') }}
                </div>
            </q-card-section>

            <q-card-section>
                <q-input
                    v-model="shipmentId"
                    :label="$t('shipments.searchShipment')"
                />

                <q-btn
                    :label="$t('button.search')"
                    color="primary"
                    class="q-mt-md"
                    :loading="loading"
                    :disable="loading || !shipmentId"
                    @click="fetchShipment"
                >
                    <template #loading>
                        <q-spinner color="white" />
                    </template>
                </q-btn>
            </q-card-section>

            <q-card-section>
                <div
                    v-if="shipment"
                    class="q-mt-md"
                >
                    <q-list>
                        <q-item>
                            <q-item-section>
                                {{ $t('shipments.shipmentDetails') }} ID: {{ shipment.shipmentId }}
                            </q-item-section>
                        </q-item>
                        <q-item>
                            <q-item-section>{{ $t('shipments.nfeKey') }}: {{ shipment.nfeKey }}</q-item-section>
                        </q-item>
                        <q-item>
                            <q-item-section>
                                {{ $t('shipments.createdBy') }}: {{ shipment.createdBy }}
                            </q-item-section>
                        </q-item>
                        <q-item>
                            <q-item-section>
                                {{ $t('shipments.shipperId') }}: {{ shipment.shipperId }}
                            </q-item-section>
                        </q-item>
                        <q-item>
                            <q-item-section>
                                {{ $t('shipments.transporterId') }}: {{ shipment.transporterId }}
                            </q-item-section>
                        </q-item>
                        <q-item>
                            <q-item-section>
                                {{ $t('shipments.destinatorId') }}: {{ shipment.destinatorId }}
                            </q-item-section>
                        </q-item>
                        <q-item>
                            <q-item-section>
                                {{ $t('shipments.orderNumber') }}: {{ shipment.orderNumber }}
                            </q-item-section>
                        </q-item>
                        <q-item>
                            <q-item-section>
                                {{ $t('shipments.trackingCode') }}: {{ shipment.trackingCode }}
                            </q-item-section>
                        </q-item>
                        <q-item>
                            <q-item-section>{{ $t('shipments.priority') }}: {{ shipment.priority }}</q-item-section>
                        </q-item>
                        <q-item>
                            <q-item-section>
                                {{ $t('shipments.volumeQuantity') }}: {{ shipment.volumeQuantity }}
                            </q-item-section>
                        </q-item>
                        <q-item>
                            <q-item-section>{{ $t('shipments.created') }}: {{ shipment.created }}</q-item-section>
                        </q-item>
                    </q-list>
                </div>
                <div
                    v-else-if="error"
                    class="q-mt-md text-negative"
                >
                    {{ $t('shipments.apiErrorShipment') }}: {{ error }}
                </div>
            </q-card-section>
        </q-card>
    </q-page>
</template>

<script setup>
import { ref } from 'vue';
import { Notify } from 'quasar';
import { createAxiosInstance } from '../../api/axiosInstance';

const environment = 'local';

const axiosInstance = createAxiosInstance(environment);

const shipmentId = ref('');
const shipment = ref(null);
const error = ref('');
const loading = ref(false);

const fetchShipment = async () => {
    shipment.value = null;
    error.value = '';
    loading.value = true;

    if (shipmentId.value) {
        try {
            const response = await axiosInstance.get(`shipments/${shipmentId.value}`);
            if (response && response.data) {
                shipment.value = response.data;
                Notify.create({
                    message: 'Dados da remessa obtidos com sucesso.',
                    color: 'positive',
                    position: 'top',
                });
            } else {
                error.value = 'Nenhum dado encontrado para este ID de remessa.';
                Notify.create({
                    message: 'Nenhum dado encontrado para este ID de remessa.',
                    color: 'negative',
                    position: 'top',
                });
            }
        } catch (err) {
            error.value = 'Erro ao buscar dados da remessa.';
            Notify.create({
                message: 'Erro ao buscar dados da remessa.',
                color: 'negative',
                position: 'top',
            });
        } finally {
            loading.value = false;
        }
    } else {
        error.value = 'Por favor, insira um ID de remessa válido.';
        Notify.create({
            message: 'Por favor, insira um ID de remessa válido.',
            color: 'negative',
            position: 'top',
        });
        loading.value = false;
    }
};
</script>
