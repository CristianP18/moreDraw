<template>
    <q-page class="page">
        <q-toolbar class="q-px-md">
            <q-toolbar-title>{{ $t('logs.pageTitle') }}</q-toolbar-title>
            <q-space />

            <!-- Seletor de usuários para filtrar os logs -->
            <q-select
                v-model="selectedUser"
                filled
                :options="filteredUserNames"
                :label="$t('logs.selectUser')"
                use-input
                input-debounce="0"
                clearable
                @filter="filterUserNames"
                @update:model-value="handleUserSelection"
                @clear="clearFilter"
            />
        </q-toolbar>

        <!-- Mostra um indicador de carregamento enquanto os logs estão sendo carregados -->
        <div v-if="loading">
            <q-spinner
                class="fixed-center"
                :message="$t('logs.loading')"
            />
        </div>

        <!-- Mostra os logs quando o carregamento é concluído -->
        <div v-else>
            <!-- Lista de logs filtrados para o usuário selecionado -->
            <q-list
                v-if="selectedLogs.length"
                bordered
            >
                <!-- Item expansível para cada log selecionado -->
                <q-expansion-item
                    v-for="log in selectedLogs"
                    :key="log.userId"
                    :label="`${log.userName} - ${$t('logs.date')}: ${log.created}`"
                    icon="person"
                >
                    <!-- Mostra os eventos dentro do log -->
                    <div
                        v-for="event in log.events"
                        :key="event.eventType"
                        class="q-ma-md"
                    >
                        <!-- Mostra detalhes do evento -->
                        <div><strong>{{ $t('logs.type') }}:</strong> {{ event.eventType }}</div>
                        <div><strong>{{ $t('logs.title') }}:</strong> {{ event.title }}</div>
                        <div><strong>{{ $t('logs.subtitle') }}:</strong> {{ event.subtitle }}</div>
                        <div><strong>{{ $t('logs.date') }}:</strong> {{ event.created }}</div>
                        <div><strong>{{ $t('logs.description') }}:</strong> {{ event.description }}</div>
                    </div>
                </q-expansion-item>
            </q-list>

            <!-- Lista de todos os logs se nenhum usuário for selecionado -->
            <q-list
                v-if="!selectedUser"
                bordered
            >
                <q-expansion-item
                    v-for="log in mobileLogs"
                    :key="log.userId"
                    :label="`${log.userName} - > ${log.created}`"
                    icon="person"
                >
                    <!-- Mostra os eventos dentro do log -->
                    <div
                        v-for="event in log.events"
                        :key="event.eventType"
                        class="q-ma-md"
                    >
                        <!-- Mostra detalhes do evento -->
                        <div><strong>{{ $t('logs.type') }}:</strong> {{ event.eventType }}</div>
                        <div><strong>{{ $t('logs.title') }}:</strong> {{ event.title }}</div>
                        <div><strong>{{ $t('logs.subtitle') }}:</strong> {{ event.subtitle }}</div>
                        <div><strong>{{ $t('logs.date') }}:</strong> {{ event.created }}</div>
                        <div><strong>{{ $t('logs.description') }}:</strong> {{ event.description }}</div>
                    </div>
                </q-expansion-item>
            </q-list>
        </div>
    </q-page>
</template>

<script>
// Importa o componente 'defineComponent' do Vue e a função para criar instâncias do Axios
import { defineComponent } from 'vue';
import { createAxiosInstance } from '../api/axiosInstance';

const environment = 'V1';
const axiosInstance = createAxiosInstance(environment);

export default defineComponent({
    name: 'LogsPage', // Nome do componente
    data() {
        return {
            loading: false, // Indica se os dados estão sendo carregados
            mobileLogs: [], // Armazena os logs móveis carregados
            allUserNames: [], // Armazena todos os nomes de usuários carregados
            filteredUserNames: [], // Armazena os nomes de usuários filtrados com base na entrada do usuário
            selectedUser: null, // Armazena o usuário selecionado no q-select
            selectedLogs: [], // Armazena os logs filtrados com base no usuário selecionado
        };
    },
    mounted() {
        this.loadMobileLogs();
    },
    methods: {
        // Método assíncrono para carregar os logs móveis
        async loadMobileLogs() {
            this.loading = true;
            try {
                const response = await axiosInstance.get('http/mobile_log'); // Faz uma requisição GET para carregar os logs
                this.mobileLogs = this.transformData(response.data.content.logs); // Transforma os dados recebidos e armazena em 'mobileLogs'
                this.allUserNames = [
                    ...new Set(this.mobileLogs.map((log) => log.userName)), // Extrai os nomes de usuários únicos
                ];
                this.filteredUserNames = this.allUserNames; // Define os nomes de usuários filtrados como todos os nomes de usuários
                this.selectedUser = null;
                this.selectedLogs = [];
            } catch (error) {
                console.error('Failed to load data:', error);
            } finally {
                this.loading = false;
            }
        },
        // Método para limpar o filtro de usuário
        clearFilter() {
            this.selectedUser = null;
            this.selectedLogs = [];
        },
        // Método chamado quando um usuário é selecionado
        handleUserSelection(value) {
            this.selectedUser = value;
            this.selectedLogs = this.mobileLogs.filter(
                (log) => log.userName === value, // Filtra os logs pelo usuário selecionado
            );
        },
        // Método para filtrar os nomes de usuários com base na entrada do usuário
        filterUserNames(val, update) {
            if (val === '') {
                update(() => {
                    this.filteredUserNames = this.allUserNames; // Se o campo estiver vazio, mostra todos os usuários
                });
            } else {
                const needle = val.toLowerCase(); // Converte o valor digitado para minúsculas
                update(() => {
                    this.filteredUserNames = this.allUserNames.filter((name) => name.toLowerCase().includes(needle)); // Filtra os usuários que contêm o texto digitado
                });
            }
        },
        // Método para transformar os dados recebidos da API em um formato adequado
        transformData(logs) {
            return logs.map((user) => {
                const firstEventDate = user.events.length > 0
                    ? Object.entries(user.events[0])[0][1].created // Pega a data do primeiro evento
                    : 'No Date'; // Se não houver eventos, define 'No Date'
                return {
                    userId: user.userId,
                    userName: user.userName,
                    created: firstEventDate,
                    events: user.events.flatMap((event) => Object.entries(event).map(([eventType, details]) => ({
                        eventType,
                        ...details,
                    }))),
                };
            });
        },
    },
});
</script>
