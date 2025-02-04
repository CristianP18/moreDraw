<template>
    <div class="map-manager q-pa-md">
        <!-- Barra de Seleção e Botões Alinhados -->
        <div class="row items-center q-gutter-sm">
            <!-- Dropdown para seleção de mapas -->
            <q-select
                v-model="selectedMap"
                :options="mapOptions"
                :label="$t('mapManager.selectMap')"
                option-value="mapYardId"
                option-label="mapName"
                use-input
                dense
                outlined
                class="select-width"
                @update:model-value="onMapSelected"
            />

            <!-- Botões para cadastrar e editar mapa -->
            <q-btn
                :label="$t('mapManager.register')"
                color="primary"
                class="button-spacing"
                @click="openCreateDialog"
            />
            <q-btn
                :label="$t('mapManager.edit')"
                color="secondary"
                class="button-spacing"
                :disable="!selectedMap"
                @click="openEditDialog"
            />
        </div>

        <!-- Diálogo para cadastrar novo mapa -->
        <q-dialog v-model="showCreateDialog">
            <q-card>
                <q-card-section>
                    <div class="text-h6">
                        {{ $t('mapManager.createMap') }}
                    </div>
                </q-card-section>

                <q-card-section>
                    <q-input
                        v-model="newMapName"
                        :label="$t('mapManager.mapName')"
                        outlined
                    />
                </q-card-section>

                <q-card-actions align="right">
                    <q-btn
                        flat
                        :label="$t('mapManager.cancel')"
                        @click="closeCreateDialog"
                    />
                    <q-btn
                        flat
                        :label="$t('mapManager.register')"
                        color="primary"
                        @click="registerMap"
                    />
                </q-card-actions>
            </q-card>
        </q-dialog>

        <!-- Diálogo para editar mapa existente -->
        <q-dialog v-model="showEditDialog">
            <q-card>
                <q-card-section class="row items-center justify-between">
                    <div class="text-h6">
                        {{ $t('mapManager.editMap') }} - {{ editedMap.mapName }}
                    </div>
                    <!-- Ícone de excluir -->
                    <q-btn
                        dense
                        flat
                        round
                        icon="delete"
                        color="negative"
                        @click="confirmDeleteMap"
                    />
                </q-card-section>

                <q-card-section>
                    <q-input
                        v-model="editedMap.mapName"
                        :label="$t('mapManager.mapName')"
                        outlined
                    />
                    <q-input
                        v-model="editedMap.description"
                        :label="$t('mapManager.description')"
                        outlined
                    />
                </q-card-section>

                <q-card-actions align="right">
                    <q-btn
                        flat
                        :label="$t('mapManager.cancel')"
                        @click="closeEditDialog"
                    />
                    <q-btn
                        flat
                        :label="$t('mapManager.save')"
                        color="primary"
                        @click="updateMap"
                    />
                </q-card-actions>
            </q-card>
        </q-dialog>

        <!-- Diálogo de confirmação de exclusão -->
        <q-dialog v-model="showDeleteDialog">
            <q-card>
                <q-card-section class="q-pa-md">
                    <div class="text-h6">
                        {{ $t('mapManager.confirmDelete') }}
                    </div>
                    <div>
                        {{ $t('mapManager.deleteConfirmation', { mapName: editedMap.mapName }) }}
                    </div>
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn
                        flat
                        :label="$t('mapManager.cancel')"
                        @click="closeDeleteDialog"
                    />
                    <q-btn
                        flat
                        :label="$t('mapManager.delete')"
                        color="negative"
                        @click="deleteMap"
                    />
                </q-card-actions>
            </q-card>
        </q-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import { createAxiosInstance } from '../../api/axiosInstance'; // Ajuste o caminho conforme a estrutura do seu projeto

// Cria uma instância do Axios
const axiosInstance = createAxiosInstance('yard');
const axiosInstanceDev2 = createAxiosInstance('dev2'); // Renomeado para evitar conflito

// URL base da API
const apiBaseUrl = '/rest/mapYard';

// Estados reativos
const showCreateDialog = ref(false);
const showEditDialog = ref(false);
const showDeleteDialog = ref(false);
const newMapName = ref('');
const mapOptions = ref([]);
const selectedMap = ref(null);

// Objeto para edição
const editedMap = ref({});

// Importa a função emit para emitir eventos para o componente pai
const emit = defineEmits(['map-selected']);

// Acesso ao Quasar para usar o Notify
const $q = useQuasar();

// Carregar mapas da API
const fetchMaps = async () => {
    try {
        const response = await axiosInstance.get(`${apiBaseUrl}/all`);
        // Ajuste conforme a estrutura dos dados retornados
        mapOptions.value = response.data.content.mapYards;
        console.log('Mapas carregados:', mapOptions.value);
    } catch (error) {
        console.error('Erro ao carregar mapas:', error);
        $q.notify({ message: 'Erro ao carregar mapas.', color: 'negative' });
    }
};

// Registrar um novo mapa
const registerMap = async () => {
    if (!newMapName.value) {
        $q.notify({ message: 'O nome do mapa é obrigatório.', color: 'warning' });
        return;
    }

    const payload = { mapName: newMapName.value };

    try {
        await axiosInstance.post(apiBaseUrl, payload);
        $q.notify({ message: 'Mapa cadastrado com sucesso!', color: 'positive' });
        closeCreateDialog();
        fetchMaps();
    } catch (error) {
        console.error('Erro ao cadastrar mapa:', error);
        $q.notify({ message: 'Erro ao cadastrar mapa. Tente novamente.', color: 'negative' });
    }
};

// Função chamada quando um mapa é selecionado
const onMapSelected = () => {
    // Emite o evento 'map-selected' para o componente pai, passando o objeto completo
    emit('map-selected', selectedMap.value);
};

// Abrir diálogo de criação
const openCreateDialog = () => {
    newMapName.value = '';
    showCreateDialog.value = true;
};

// Fechar diálogo de criação
const closeCreateDialog = () => {
    showCreateDialog.value = false;
};

// Abrir diálogo de edição
const openEditDialog = () => {
    if (!selectedMap.value) {
        $q.notify({ message: 'Nenhum mapa selecionado para editar.', color: 'warning' });
        return;
    }
    // Cria uma cópia do mapa selecionado para edição
    editedMap.value = { ...selectedMap.value };
    showEditDialog.value = true;
};

// Fechar diálogo de edição
const closeEditDialog = () => {
    showEditDialog.value = false;
};

// Atualizar mapa
const updateMap = async () => {
    if (!editedMap.value.mapName) {
        $q.notify({ message: 'O nome do mapa é obrigatório.', color: 'warning' });
        return;
    }

    try {
        await axiosInstance.put(`${apiBaseUrl}/${editedMap.value.mapYardId}`, editedMap.value);
        $q.notify({ message: 'Mapa atualizado com sucesso!', color: 'positive' });
        closeEditDialog();
        fetchMaps();
        // Atualiza o mapa selecionado atual
        selectedMap.value = editedMap.value;
        emit('map-selected', selectedMap.value);
    } catch (error) {
        console.error('Erro ao atualizar mapa:', error);
        $q.notify({ message: 'Erro ao atualizar mapa. Tente novamente.', color: 'negative' });
    }
};

// Abrir diálogo de confirmação de exclusão
const confirmDeleteMap = () => {
    showDeleteDialog.value = true;
};

// Fechar diálogo de confirmação de exclusão
const closeDeleteDialog = () => {
    showDeleteDialog.value = false;
};

// Excluir mapa
const deleteMap = async () => {
    try {
        await axiosInstance.delete(`${apiBaseUrl}/${editedMap.value.mapYardId}`);
        $q.notify({ message: 'Mapa excluído com sucesso!', color: 'positive' });
        closeDeleteDialog();
        closeEditDialog();
        fetchMaps();
        // Limpa o mapa selecionado
        selectedMap.value = null;
        emit('map-selected', null);
    } catch (error) {
        console.error('Erro ao excluir mapa:', error);
        $q.notify({ message: 'Erro ao excluir mapa. Tente novamente.', color: 'negative' });
    }
};

// Carregar mapas ao montar o componente
onMounted(() => {
    fetchMaps();
});
</script>

<style scoped>
.map-manager {
    /* Utiliza flexbox para organizar os elementos verticalmente */
    display: flex;
    flex-direction: column;
}

@media (min-width: 600px) {
    .map-manager .row {
        /* Ajusta o alinhamento para telas maiores */
        flex-wrap: nowrap;
    }
}

.full-width {
    width: 100%;
}

.full-width-md {
    @media (min-width: 600px) {
        width: auto;
    }
}
</style>
