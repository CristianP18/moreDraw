<template>
    <q-dialog
        :model-value="show"
        @update:model-value="$emit('update:show', $event)"
    >
        <spk-card>
            <spk-card-section>
                <div class="text-h6">
                    {{ checkInData.gatewayId ? $t('checkInManagement.editCheckIn') : $t('checkInManagement.newCheckIn') }}
                </div>
            </spk-card-section>
            <spk-card-section>
                <q-input
                    v-model="checkInData.driverName"
                    :label="$t('checkInManagement.driverName')"
                    outlined
                    dense
                />
                <q-input
                    v-model="checkInData.driverFederalTax"
                    :label="$t('checkInManagement.driverCpf')"
                    outlined
                    dense
                    mask="###.###.###-##"
                />
                <q-input
                    v-model="checkInData.licensePlate"
                    :label="$t('checkInManagement.licensePlate')"
                    outlined
                    dense
                    mask="AAA-####"
                    fill-mask="false"
                    :rules="[val => licensePlateRegex.test(val) || $t('checkInManagement.invalidFormat')]"
                />

                <q-input
                    v-model="checkInData.boardingType"
                    :label="$t('checkInManagement.loadType')"
                    outlined
                    dense
                />
                <q-input
                    v-model="checkInData.nfE"
                    :label="$t('checkInManagement.invoiceNumber')"
                    outlined
                    dense
                    mask="##############"
                />
                <q-input
                    v-model="checkInData.mobilePhone"
                    :label="$t('checkInManagement.phoneNumber')"
                    outlined
                    dense
                    mask="+55#############"
                />
                <q-input
                    v-model="checkInData.location"
                    :label="$t('checkInManagement.location')"
                    outlined
                    dense
                />
                <q-input
                    v-model="checkInData.entryTime"
                    :label="$t('checkInManagement.entryTime')"
                    type="datetime-local"
                    outlined
                    dense
                />

                <q-select
                    :model-value="selectedStorlocId"
                    :label="$t('checkInManagement.storlocDock')"
                    outlined
                    dense
                    :options="storlocOptions"
                    option-label="label"
                    option-value="value"
                    emit-value
                    map-options
                    :loading="isLoadingStorloc"
                    @update:model-value="$emit('update:selectedStorlocId', $event)"
                    @focus="$emit('fetch-storlocs')"
                />

                <q-input
                    v-model="checkInData.boardingDocumentation"
                    :label="$t('checkInManagement.boardingDocuments')"
                    outlined
                    dense
                />
                <q-input
                    v-model="checkInData.boardingCondition"
                    :label="$t('checkInManagement.boardingCondition')"
                    outlined
                    dense
                />
            </spk-card-section>
            <spk-card-actions align="right">
                <q-btn
                    flat
                    :label="$t('checkInManagement.cancel')"
                    @click="$emit('close')"
                />
                <q-btn
                    flat
                    :label="$t('checkInManagement.save')"
                    color="primary"
                    :disable="!isValid || isSaving"
                    :loading="isSaving"
                    @click="handleSave"
                />
            </spk-card-actions>
        </spk-card>
    </q-dialog>
</template>

<script setup>
import { defineProps, defineEmits, computed } from 'vue';

// Definição das propriedades recebidas do componente pai
const props = defineProps({
    show: Boolean,
    checkInData: Object,
    isSaving: Boolean,
    storlocOptions: Array,
    isLoadingStorloc: Boolean,
    selectedStorlocId: {
        type: String,
        default: null,
    },
});

// Definição dos eventos emitidos para o componente pai
const emit = defineEmits([
    'close',
    'confirm-checkin',
    'fetch-storlocs',
    'update:selectedStorlocId',
    'update:show',
]);

// Função para lidar com o clique no botão "Salvar"
function handleSave() {
    emit('confirm-checkin');
}

// Regex para validação da placa
const licensePlateRegex = /^[A-Z]{3}-\d{4}$/;

// Computed para validar se todos os campos obrigatórios estão preenchidos
const isValid = computed(() => (
    props.checkInData.driverName?.trim()
        && props.checkInData.driverFederalTax?.trim()
        && props.checkInData.licensePlate
        && licensePlateRegex.test(props.checkInData.licensePlate)
        && props.checkInData.boardingType?.trim()
        && props.checkInData.nfE?.trim()
        && props.checkInData.mobilePhone?.trim()
        && props.checkInData.location?.trim()
        && props.checkInData.entryTime
        && props.selectedStorlocId
        && props.checkInData.boardingDocumentation?.trim()
        && props.checkInData.boardingCondition?.trim()
));
</script>
