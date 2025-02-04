<template>
    <q-page class="q-pa-md">
        <div class="top-container">
            <spk-card class="flex-row">
                <div class="selection-container">
                    <spk-card-section>
                        <div class="text-h6">
                            {{ $t('clientReport.titleClient') }}
                        </div>
                        <q-input
                            v-model="cnpj"
                            filled
                            :label="$t('clientReport.cnpjLabel')"
                            lazy-rules
                            :rules="[val => val && val.length > 0 || $t('clientReport.emptyCnpjError')]"
                            @input="debouncedSearch"
                        />
                        <q-btn
                            :label="$t('clientReport.searchButton')"
                            color="primary"
                            class="q-mt-md"
                            :loading="loading"
                            :disable="loading || !cnpj"
                            @click="searchByCnpj"
                        >
                            <template #loading>
                                <q-spinner color="white" />
                            </template>
                        </q-btn>
                        <client-details
                            v-if="selectedClient"
                            :client="selectedClient"
                        />
                    </spk-card-section>
                </div>
            </spk-card>
        </div>
    </q-page>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import debounce from 'lodash/debounce';
import { Notify } from 'quasar';
import ClientDetails from './ClientDetails.vue';
import { useStore } from '../../stores/store';
import { createAxiosInstance } from '../../api/axiosInstance';

const cnpj = ref('');
const selectedClient = ref(null);
const loading = ref(false);

const environment = 'V1';

const axiosInstance = createAxiosInstance(environment);

const debouncedSearch = debounce(async () => {
    if (cnpj.value) {
        try {
            loading.value = true;
            const response = await axiosInstance.get(`rest/users/find?federalTax=${cnpj.value}`);
            console.log('API Response:', response.data);
            if (response.data && response.data.content) {
                selectedClient.value = response.data.content;
                const userData = response.data.content;
                localStorage.setItem('tenantId', userData.tenantId);
                localStorage.setItem('clientId', userData.federalTax);
                localStorage.setItem('selectedClient', JSON.stringify(userData));

                const store = useStore();
                store.setFederalTax(userData.federalTax);

                console.log('Client data and clientId set:', userData.clientId, 'Tenant: ', userData.tenantId);
            } else {
                console.warn('No client data found');
                selectedClient.value = null;
                Notify.create({
                    message: 'Nenhum cliente encontrado com este CNPJ.',
                    color: 'warning',
                    position: 'top',
                });
            }
        } catch (error) {
            console.error('Search error:', error);
            selectedClient.value = null;
            Notify.create({
                message: 'Erro ao buscar cliente. Tente novamente.',
                color: 'negative',
                position: 'top',
            });
        } finally {
            loading.value = false;
        }
    } else {
        selectedClient.value = null;
    }
}, 300);

function searchByCnpj() {
    debouncedSearch.cancel();
    debouncedSearch();
}

watch(selectedClient, (newValue) => {
    console.log('selectedClient changed:', newValue);
    if (newValue && newValue.clientId) {
        updateClientId(newValue.clientId);
    }
});

onMounted(() => {
    const savedClient = localStorage.getItem('selectedClient');
    if (savedClient) {
        selectedClient.value = JSON.parse(savedClient);
    }
});
</script>
