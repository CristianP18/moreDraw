<template>
    <q-page class="q-pa-md">
        <q-card>
            <q-card-section>
                <div class="text-h6">
                    Buscar Volumes
                </div>
            </q-card-section>

            <q-card-section>
                <q-input
                    v-model="unitloadId"
                    label="ID do Volume"
                />

                <q-btn
                    :label="'Buscar'"
                    color="primary"
                    class="q-mt-md"
                    :loading="loading"
                    :disable="loading || !unitloadId"
                    @click="fetchUnitload"
                >
                    <template #loading>
                        <q-spinner color="white" />
                    </template>
                </q-btn>
            </q-card-section>

            <q-card-section v-if="unitload">
                <q-list>
                    <q-item>
                        <q-item-section>
                            Unitload ID: {{ unitload.unitloadId }}
                        </q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>
                            Created By: {{ unitload.createdBy }}
                        </q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>
                            Shipment ID: {{ unitload.shipmentId }}
                        </q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>
                            Unitload Name: {{ unitload.unitloadName }}
                        </q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>Bar Code: {{ unitload.barCode }}</q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>
                            External Code: {{ unitload.externalCode }}
                        </q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>
                            Risk Class: {{ unitload.riskClass }}
                        </q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>
                            Package Type: {{ unitload.packageType }}
                        </q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>
                            Opened: {{ unitload.opened ? "Yes" : "No" }}
                        </q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>
                            Stacking Weight: {{ unitload.stackingWeight }}
                        </q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>Width: {{ unitload.width }}</q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>Height: {{ unitload.height }}</q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>Depth: {{ unitload.depth }}</q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>Weight: {{ unitload.weight }}</q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>
                            Weight Calculated:
                            {{ unitload.weightCalculated }}
                        </q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>Quantity: {{ unitload.quantity }}</q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>Amount: {{ unitload.amount }}</q-item-section>
                    </q-item>
                    <q-item>
                        <q-item-section>Created: {{ unitload.created }}</q-item-section>
                    </q-item>
                </q-list>
            </q-card-section>

            <q-card-section
                v-else-if="error"
                class="q-mt-md text-negative"
            >
                {{ error }}
            </q-card-section>
        </q-card>
    </q-page>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { Notify } from 'quasar';
import { createAxiosInstance } from '../../api/axiosInstance';

const unitloadId = ref('');
const unitload = ref(null);
const error = ref('');
const loading = ref(false);

const environment = 'local';

const axiosInstance = createAxiosInstance(environment);

const fetchUnitload = async () => {
    unitload.value = null;
    error.value = '';
    loading.value = true;

    if (unitloadId.value) {
        try {
            const response = await axiosInstance.get(`unitloads/${unitloadId.value}`);
            if (response && response.data) {
                unitload.value = response.data;
                Notify.create({
                    message: 'Dados do volume obtidos com sucesso.',
                    color: 'positive',
                    position: 'top',
                });
            } else {
                error.value = 'Nenhum dado encontrado para este ID de volume.';
                Notify.create({
                    message: 'Nenhum dado encontrado para este ID de volume.',
                    color: 'negative',
                    position: 'top',
                });
            }
        } catch (err) {
            console.error('Erro ao buscar volume:', err);
            error.value = 'Erro ao buscar volume.';
            Notify.create({
                message: 'Erro ao buscar volume.',
                color: 'negative',
                position: 'top',
            });
        } finally {
            loading.value = false;
        }
    } else {
        error.value = 'Por favor, insira um ID de volume válido.';
        Notify.create({
            message: 'Por favor, insira um ID de volume válido.',
            color: 'negative',
            position: 'top',
        });
        loading.value = false;
    }
};
</script>
