<template>
    <div class="col-3 storloc-list">
        <q-card class="q-pa-md">
            <q-btn
                label="Cadastrar Novo Storloc"
                color="primary"
                class="q-mt-md"
                @click="openDialog"
            />
        </q-card>

        <!-- Diálogo para criação/edição -->
        <q-dialog v-model="showDialog">
            <q-card>
                <q-card-section>
                    <div class="text-h6">
                        {{ isEditing ? 'Editar Storloc' : 'Cadastrar Novo Storloc' }}
                    </div>
                </q-card-section>
                <q-card-section>
                    <q-input
                        v-model="storloc.ean"
                        label="EAN"
                    />
                    <q-input
                        v-model="storloc.type"
                        label="Tipo de Veículo"
                    />
                    <q-input
                        v-model="storloc.index"
                        label="Índice"
                    />
                    <q-input
                        v-model="storloc.area"
                        label="Área"
                    />
                    <q-input
                        v-model="storloc.rack"
                        label="Rack"
                    />
                    <q-input
                        v-model="storloc.zoneId"
                        label="Zona"
                    />
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn
                        flat
                        label="Cancelar"
                        @click="showDialog = false"
                    />
                    <q-btn
                        flat
                        label="Salvar"
                        color="primary"
                        @click="saveStorloc"
                    />
                </q-card-actions>
            </q-card>
        </q-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, defineEmits } from 'vue';
import { createAxiosInstance } from '../../api/axiosInstance';

const axiosInstance = createAxiosInstance('dev2');
const showDialog = ref(false);
const isEditing = ref(false);
const storloc = reactive({ ean: '', type: '', index: '', area: '', rack: '', zoneId: '' });
const emit = defineEmits(['storloc-saved', 'edit-storloc']);

// Abre o diálogo de cadastro/edição
function openDialog(storlocToEdit = null) {
    if (storlocToEdit) {
        isEditing.value = true;
        Object.assign(storloc, storlocToEdit);
    } else {
        isEditing.value = false;
        Object.assign(storloc, { ean: '', type: '', index: '', area: '', rack: '', zoneId: '' });
    }
    showDialog.value = true;
}

// Salva o storloc
async function saveStorloc() {
    try {
        if (isEditing.value) {
            await axiosInstance.put(`/rest/storloc/${storloc.ean}`, storloc);
        } else {
            await axiosInstance.post('/rest/storloc', storloc);
        }
        showDialog.value = false;
        emit('storloc-saved');
    } catch (error) {
        console.error('Erro ao salvar storloc:', error);
    }
}
</script>
