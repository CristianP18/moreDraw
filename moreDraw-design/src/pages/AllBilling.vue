<template>
    <q-page padding>
        <spk-card-section>
            <div class="row items-center spk-col-gutter-md">
                <!-- Seletor de custo por volume compacto e alinhado -->

                <q-input
                    v-model.number="costPerVolume"
                    :label="$t('clientReport.payByUnitload')"
                    type="number"
                    :rules="[val => val >= 1 && val <= 12 || 'Escolha um valor entre 1 e 12']"
                    outlined
                    dense
                    style="max-width: 120px; margin: 4px; margin-top:21px;"
                />

                <!-- Seletor de data e hora -->
                <q-input
                    v-model="startDate"
                    style="margin: 4px;"
                    :label="$t('searchOptions.selectDate')"
                    type="date"
                    outlined
                    dense
                />
                <q-input
                    v-model="endDate"
                    style="margin: 4px;"
                    :label="$t('searchOptions.selectDate')"
                    type="date"
                    outlined
                    dense
                />
                <q-input
                    v-model="startTime"
                    style="min-width: 120px; margin: 4px;"
                    :label="$t('searchOptions.selectTime')"
                    type="time"
                    outlined
                    dense
                />
                <q-input
                    v-model="endTime"
                    style="min-width: 120px; margin: 4px;"
                    :label="$t('searchOptions.selectTime')"
                    type="time"
                    outlined
                    dense
                />
            </div>
        </spk-card-section>

        <div
            class="q-mb-md"
            style="max-width: 150px;"
        >
            <spk-btn
                :loading="loading"
                :disable="loading"
                :label="$t('button.billing')"
                color="primary"
                class="full-width"
                @click="allBilling"
            >
                <template #loading>
                    <q-spinner color="white" />
                </template>
            </spk-btn>
        </div>

        <div
            class="response"
            style="max-width: 350px;"
        >
            <q-row class="q-pa-md q-gutter-md">
                <q-col
                    v-for="(item, index) in billingData"
                    :key="index"
                    cols="12"
                    sm="6"
                    md="4"
                    lg="3"
                >
                    <q-card class="my-card">
                        <q-card-section>
                            <div class="text-h6">
                                {{ item.client.companyName }}
                            </div>
                            <div>Total de Remessas: {{ item.billing.allShipment }}</div>
                            <div>Total de Volumes: {{ item.billing.allUnitloads }}</div>
                            <div>Federal Tax: {{ item.client.federalTax }}</div>
                            <div
                                v-if="costPerVolume"
                                style="font-size: 20px;"
                            >
                                Valor Ajustado: {{ (item.billing.allUnitloads * (costPerVolume/100)).toFixed(2) }}
                            </div>
                        </q-card-section>
                    </q-card>
                </q-col>
            </q-row>

            <div
                v-if="billingData.length === 0"
                class="q-mt-lg"
            >
                <q-card>
                    <q-card-section>
                        <div class="text-subtitle2">
                            {{ $t('clientReport.noShipment') }}
                        </div>
                    </q-card-section>
                </q-card>
            </div>
        </div>
    </q-page>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { format, subMonths, startOfMonth, endOfMonth } from 'date-fns';
import { useI18n } from 'vue-i18n';
import { useQuasar } from 'quasar';
import { createAxiosInstance } from '../api/axiosInstance';

const { t } = useI18n();
const $q = useQuasar();
const environment = 'local';
const axiosInstance = createAxiosInstance(environment);

// Calcula o início e o fim do mês anterior
const previousMonthStart = startOfMonth(subMonths(new Date(), 1));
const previousMonthEnd = endOfMonth(subMonths(new Date(), 1));

const startDate = ref(format(previousMonthStart, 'yyyy-MM-dd'));
const endDate = ref(format(previousMonthEnd, 'yyyy-MM-dd'));
const startTime = ref('00:10');
const endTime = ref('23:50');
const costPerVolume = '8';
const loading = ref(false);
const billingData = ref([]);

async function allBilling() {
    const beginDate = `${startDate.value}T${startTime.value}:00`;
    const endDateFormatted = `${endDate.value}T${endTime.value}:00`;

    loading.value = true;

    try {
        const response = await axiosInstance.post('allBilling', {
            begin: beginDate,
            end: endDateFormatted,
        });
        console.log('API response content:', response.data);
        billingData.value = response.data;
    } catch (error) {
        console.error('Erro ao buscar faturas:', error);
        $q.notify({
            type: 'negative',
            message: 'Erro ao buscar faturas. Tente novamente.',
        });
    } finally {
        loading.value = false;
    }
}

onMounted(() => {
    allBilling();
});
</script>
