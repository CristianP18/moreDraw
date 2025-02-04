<template>
    <q-dialog
        :model-value="show"
        @update:model-value="$emit('update:show', $event)"
    >
        <spk-card>
            <spk-card-section>
                <div class="text-h6">
                    {{ schedulingData.eventSchedulingId ? $t('eventsScheduling.editEvent') : $t('eventsScheduling.newEvent') }}
                </div>
            </spk-card-section>
            <spk-card-section>
                <q-input
                    v-model="schedulingData.orderTransporterId"
                    :label="$t('eventsScheduling.transportIdentifier')"
                    outlined
                    dense
                />

                <q-select
                    :model-value="selectedStartStorlocId"
                    :label="$t('eventsScheduling.sourceStorloc')"
                    outlined
                    dense
                    :options="storlocOptions"
                    option-label="label"
                    option-value="value"
                    emit-value
                    map-options
                    :loading="isLoadingStorloc"
                    @update:model-value="$emit('update:selectedStartStorlocId', $event)"
                    @focus="$emit('fetch-storlocs')"
                />

                <q-select
                    :model-value="selectedDestinatorStorlocId"
                    :label="$t('eventsScheduling.destinationStorloc')"
                    outlined
                    dense
                    :options="storlocOptions"
                    option-label="label"
                    option-value="value"
                    emit-value
                    map-options
                    :loading="isLoadingStorloc"
                    @update:model-value="$emit('update:selectedDestinatorStorlocId', $event)"
                    @focus="$emit('fetch-storlocs')"
                />

                <q-input
                    v-model="schedulingData.licensePlate"
                    :label="$t('eventsScheduling.vehicleLicensePlate')"
                    outlined
                    dense
                    mask="AAA-####"
                    fill-mask
                    :rules="[val => licensePlateRegex.test(val) || $t('eventsScheduling.invalidFormat')]"
                />

                <q-select
                    v-model="schedulingData.state"
                    :options="stateOptions"
                    :label="$t('eventsScheduling.state')"
                    outlined
                    dense
                    emit-value
                    map-options
                    required
                />
                <q-select
                    v-model="schedulingData.category"
                    :options="categoryOptions"
                    :label="$t('eventsScheduling.category')"
                    outlined
                    dense
                    emit-value
                    map-options
                    required
                />
                <q-input
                    v-model="schedulingData.begin"
                    :label="$t('eventsScheduling.start')"
                    type="datetime-local"
                    outlined
                    dense
                />
                <q-input
                    v-model="schedulingData.end"
                    :label="$t('eventsScheduling.end')"
                    type="datetime-local"
                    outlined
                    dense
                />
                <q-input
                    v-model="schedulingData.time"
                    :label="$t('eventsScheduling.duration')"
                    outlined
                    dense
                    mask="## minutes"
                />
            </spk-card-section>
            <spk-card-actions align="right">
                <q-btn
                    flat
                    :label="$t('eventsScheduling.cancel')"
                    @click="$emit('close')"
                />
                <q-btn
                    flat
                    :label="$t('eventsScheduling.save')"
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
    schedulingData: Object,
    isSaving: Boolean,
    storlocOptions: Array,
    isLoadingStorloc: Boolean,
    stateOptions: Array,
    categoryOptions: Array,
    selectedStartStorlocId: {
        type: String,
        default: null,
    },
    selectedDestinatorStorlocId: {
        type: String,
        default: null,
    },
});

// Definição dos eventos emitidos para o componente pai
const emit = defineEmits([
    'close',
    'confirm-scheduling',
    'fetch-storlocs',
    'update:selectedStartStorlocId',
    'update:selectedDestinatorStorlocId',
    'update:show',
]);

// Função para lidar com o clique no botão "Salvar"
function handleSave() {
    emit('confirm-scheduling');
}

// Regex corrigida para validação da placa
const licensePlateRegex = /^[A-Z]{3}-\d{4}$/;

// Computed para validar se todos os campos obrigatórios estão preenchidos
const isValid = computed(() => (
    props.schedulingData.orderTransporterId?.trim()
        && props.selectedStartStorlocId
        && props.selectedDestinatorStorlocId
        && props.schedulingData.licensePlate
        && licensePlateRegex.test(props.schedulingData.licensePlate)
        && props.schedulingData.state
        && props.schedulingData.category
        && props.schedulingData.begin
        && props.schedulingData.end
));
</script>
