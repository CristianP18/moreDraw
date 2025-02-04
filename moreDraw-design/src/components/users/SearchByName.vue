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
                    </spk-card-section>

                    <spk-card-section>
                        <div
                            v-if="tipoPesquisa === 'name'"
                            style="width: 300px;"
                        >
                            <q-input
                                v-model="clientName"
                                :label="$t('clientReport.clientNameLabel')"
                                :loading="loading"
                                @input="handleNameInput"
                            />
                            <q-list
                                v-if="clientOptions.length"
                                class="q-mt-md"
                            >
                                <q-item
                                    v-for="(client, index) in clientOptions"
                                    :key="index"
                                    clickable
                                    @click="selectClient(client)"
                                >
                                    <q-item-section>{{ client.fullName }}</q-item-section>
                                </q-item>
                            </q-list>
                        </div>
                        <!-- Right container for the details -->
                        <spk-card-section
                            v-if="selectedClient"
                            class="details-container"
                        >
                            <ClientDetails
                                :client="selectedClient"
                            />
                        </spk-card-section>
                    </spk-card-section>
                </div>
            </spk-card>
        </div>
    </q-page>
</template>

<script setup>
import { ref, watch } from 'vue';
import { Notify } from 'quasar';
import ClientDetails from './ClientDetails.vue';
import { useStore } from '../../stores/store';
import { createAxiosInstance } from '../../api/axiosInstance';

const environment = 'V1';

const axiosInstance = createAxiosInstance(environment);

const clientName = ref('');
const clientOptions = ref([]);
const selectedClient = ref(null);
const loading = ref(false);
const tipoPesquisa = ref('name');

const debounce = (fn, delay) => {
    let timeoutId;
    return function (...args) {
        if (timeoutId) {
            clearTimeout(timeoutId);
        }
        timeoutId = setTimeout(() => {
            fn.apply(this, args);
        }, delay);
    };
};

// Função para buscar clientes por nome
const debouncedFetch = debounce(async (name) => {
    loading.value = true;
    try {
        const response = await axiosInstance.get(`rest/users/find?name=${name}`);

        if (response.data.content && response.data.content.length > 0) {
            clientOptions.value = response.data.content.map((client) => ({
                fullName: client.fullName,
                id: client.tenantId,
                clientId: client.clientId,
                federalTax: client.federalTax,
                mobilePhone: client.mobilePhone,
                address: client.address,
                picUser: client.picUser,
            }));
        } else {
            clientOptions.value = [];
            Notify.create({
                message: 'Nenhum cliente encontrado com este nome.',
                color: 'warning',
                position: 'top',
            });
        }
    } catch (error) {
        console.error('Erro ao buscar clientes por nome:', error);
        Notify.create({
            message: 'Erro ao buscar clientes. Tente novamente.',
            color: 'negative',
            position: 'top',
        });
    } finally {
        loading.value = false;
    }
}, 300);

watch(clientName, (newName) => {
    if (newName.length >= 3) {
        debouncedFetch(newName);
    } else {
        clientOptions.value = [];
    }
});

// Função para selecionar o cliente e atualizar o nome e a API Key
const selectClient = async (client) => {
    selectedClient.value = client;
    clientName.value = client.fullName;

    localStorage.setItem('tenantId', client.id);
    localStorage.setItem('clientId', client.federalTax);
    const store = useStore();
    store.setFederalTax(client.federalTax);
    console.log('Client data and clientId set:', client.clientId, 'Tenant: ', client.tenantId);
};
</script>
