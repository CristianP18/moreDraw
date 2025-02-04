<template>
    <q-page class="q-pa-md">
        <!-- Main container for the selection and details cards -->
        <div class="top-container">
            <spk-card class="flex-row">
                <!-- Left container for the selection -->
                <div class="selection-container">
                    <spk-card-section>
                        <div class="text-h6">
                            {{ $t('clientReport.title') }}
                        </div>
                        <q-input
                            v-model="mobilePhone"
                            filled
                            :label="$t('clientReport.mobilePhoneLabel')"
                            lazy-rules
                            :rules="[val => val && val.length > 0 || $t('clientReport.phoneValidationMessage')]"
                            @input="debouncedSearch"
                        />
                        <q-btn
                            :label="$t('button.search')"
                            color="primary"
                            class="q-mt-md"
                            :loading="loading"
                            :disable="loading || !mobilePhone"
                            @click="searchByPhone"
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
import { ref, watch } from 'vue';

import debounce from 'lodash/debounce';
import { Notify } from 'quasar';
import ClientDetails from './ClientDetails.vue';
import { useStore } from '../../stores/store';
import { createAxiosInstance } from '../../api/axiosInstance';

const environment = 'V1';

const axiosInstance = createAxiosInstance(environment);

const mobilePhone = ref('');
const selectedClient = ref(null);
const loading = ref(false);

const debouncedSearch = debounce(async () => {
    if (mobilePhone.value) {
        loading.value = true;
        try {
            const response = await axiosInstance.get(
                `rest/users/find?mobilePhone=${mobilePhone.value}`,
            );
            console.log('API Response:', response.data);
            if (response.data && response.data.content) {
                selectedClient.value = response.data.content;
                const userData = response.data.content;
                localStorage.setItem('tenantId', userData.tenantId);
                localStorage.setItem('clientId', userData.federalTax);
                const store = useStore();
                store.setFederalTax(userData.federalTax);
                console.log('Client data and clientId set:', userData.clientId, 'Tenant: ', userData.tenantId);
                Notify.create({
                    message: 'Cliente encontrado com sucesso.',
                    color: 'positive',
                    position: 'top',
                });
            } else {
                console.warn('No client data found');
                selectedClient.value = null;
                Notify.create({
                    message: 'Nenhum cliente encontrado com este número de telefone.',
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

function searchByPhone() {
    debouncedSearch.cancel();
    debouncedSearch();
}

watch(selectedClient, (newValue) => {
    console.log('selectedClient changed:', newValue);
});
</script>
