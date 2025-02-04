<template>
    <q-dialog
        :model-value="show"
        @update:model-value="$emit('update:show', $event)"
    >
        <spk-card>
            <spk-card-section>
                <div class="text-h6">
                    {{ $t('checkInManagement.performCheckOut') }} {{ checkOutData.gatewayId }}
                </div>
            </spk-card-section>
            <spk-card-section>
                <q-input
                    v-model="checkOutData.exitInspection"
                    :label="$t('checkInManagement.exitInspection')"
                    outlined
                    dense
                />
                <q-file
                    v-model="checkOutData.releaseDocuments"
                    :label="$t('checkInManagement.exitDocuments')"
                    outlined
                    dense
                    accept="application/pdf,image/*"
                    counter
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
                    :label="$t('checkInManagement.confirmCheckOut')"
                    color="secondary"
                    :disable="!isValid || isSaving"
                    :loading="isSaving"
                    @click="handleSave"
                />
            </spk-card-actions>
        </spk-card>
    </q-dialog>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

// Definição das propriedades recebidas do componente pai
const props = defineProps({
    show: Boolean,
    checkOutData: Object,
    isSaving: Boolean,
    isValid: Boolean,
});

// Definição dos eventos emitidos para o componente pai
const emit = defineEmits([
    'close',
    'confirm-checkout',
    'update:show',
]);

// Função para lidar com o clique no botão "Confirmar Check-Out"
function handleSave() {
    emit('confirm-checkout');
}
</script>
