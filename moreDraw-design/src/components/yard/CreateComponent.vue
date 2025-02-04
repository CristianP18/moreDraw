<template>
    <div class="col-3 storloc-list">
        <q-btn
            :label="$t('storlocManagement.newStorloc')"
            color="primary"
            class="q-mt-md"
            @click="openCreateDialog"
        />

        <!-- Diálogo para criação/edição -->
        <q-dialog v-model="showDialog">
            <q-card>
                <q-card-section>
                    <div class="text-h6">
                        {{ isEditing ? $t('storlocManagement.editStorloc') : $t('storlocManagement.newStorloc') }}
                    </div>
                </q-card-section>
                <q-card-section>
                    <q-input
                        v-model="storloc.zoneId"
                        :label="$t('storlocManagement.zone')"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="storloc.riskClass"
                        :label="$t('storlocManagement.riskClass')"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="storloc.area"
                        :label="$t('storlocManagement.area')"
                        :disable="true"
                        outlined
                        dense
                    />
                    <q-select
                        v-model="storloc.type"
                        :label="$t('storlocManagement.type')"
                        :options="typeOptions"
                        option-value="value"
                        option-label="label"
                        emit-value
                        outlined
                        dense
                    />
                    <q-input
                        v-model="storloc.field"
                        :label="$t('storlocManagement.field')"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="storloc.rack"
                        :label="$t('storlocManagement.rack')"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="storloc.street"
                        :label="$t('storlocManagement.street')"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="storloc.index"
                        :label="$t('storlocManagement.index')"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="storloc.xPos"
                        :label="$t('storlocManagement.xPos')"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="storloc.yPos"
                        :label="$t('storlocManagement.yPos')"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="storloc.zPos"
                        :label="$t('storlocManagement.zPos')"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="storloc.description"
                        :label="$t('storlocManagement.description')"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="storloc.classStorloc"
                        :label="$t('storlocManagement.classStorloc')"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="storloc.classStorlocType"
                        :label="$t('storlocManagement.classStorlocType')"
                        outlined
                        dense
                    />
                    <q-input
                        v-model="storloc.operadorLogistico"
                        :label="$t('storlocManagement.operadorLogistico')"
                        outlined
                        dense
                    />
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn
                        flat
                        :label="$t('storlocManagement.cancel')"
                        @click="showDialog = false"
                    />
                    <q-btn
                        flat
                        :label="$t('storlocManagement.save')"
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
const typeOptions = [
    { label: 'Portaria', value: '20' },
    { label: 'Pátio', value: '30' },
    { label: 'Doca', value: '40' },
];
const storloc = reactive({
    zoneId: '',
    riskClass: '',
    area: '{"x":149.296875,"y":-56.40625}',
    type: '',
    field: '',
    rack: '',
    street: '',
    index: '',
    xPos: '',
    yPos: '',
    zPos: '',
    description: '',
    classStorloc: '',
    classStorlocType: '',
    operadorLogistico: '',
});
const emit = defineEmits(['storloc-saved']);

// Abre o diálogo para cadastro
function openCreateDialog() {
    isEditing.value = false;
    resetStorloc(); // Limpa os campos e aplica valores padrão
    showDialog.value = true;
}

// Abre o diálogo para edição
function openEditDialog(storlocToEdit = null) {
    isEditing.value = true;
    if (storlocToEdit) {
        Object.assign(storloc, storlocToEdit);
    }
    showDialog.value = true;
}

// Reseta os campos do formulário
function resetStorloc() {
    Object.assign(storloc, {
        zoneId: '',
        riskClass: '',
        area: '{"x":149.296875,"y":-56.40625}',
        type: '',
        field: '',
        rack: '',
        street: '',
        index: '',
        xPos: '',
        yPos: '',
        zPos: '',
        description: '',
        classStorloc: '',
        classStorlocType: '',
        operadorLogistico: '',
    });
}

// Salva o storloc
async function saveStorloc() {
    try {
        const payload = { storlocs: [storloc] }; // Adapta o JSON ao formato esperado
        if (isEditing.value) {
            await axiosInstance.put(`/rest/storloc/${storloc.index}`, payload);
        } else {
            await axiosInstance.post('/rest/storloc', payload);
        }
        showDialog.value = false;
        emit('storloc-saved');
    } catch (error) {
        console.error('Erro ao salvar storloc:', error);
    }
}
</script>
