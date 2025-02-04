<template>
    <spk-card
        class="q-ma-md"
        flat
        bordered
    >
        <spk-card-section>
            <q-toolbar-title>{{ $t('checkInManagement.vehicleCheckOut') }}</q-toolbar-title>
        </spk-card-section>

        <spk-table
            :rows="checkOutList"
            :columns="columns"
            row-key="gatewayId"
            :spk-table
            :pagination="{ page: 1, rowsPerPage: 25 }"
            flat
            bordered
            dense
        >
            <!-- Custom rendering for document button -->
            <template #body-cell-releaseDocuments="props">
                <q-td align="center">
                    <q-btn
                        flat
                        :label="$t('checkInManagement.viewDocument')"
                        @click="viewDocument(props.row.releaseDocuments)"
                    />
                </q-td>
            </template>

            <!-- Custom rendering for actions -->
            <template #body-cell-actions="props">
                <q-td align="center">
                    <q-btn
                        flat
                        icon="mdi-pencil"
                        color="primary"
                        @click="$emit('edit', props.row)"
                    />
                </q-td>
            </template>
        </spk-table>
    </spk-card>
</template>

<script setup>
import { defineProps } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const props = defineProps({
    checkOutList: {
        type: Array,
        required: true,
    },
});

const columns = [
    { name: 'driverName', label: t('checkInManagement.driverName'), align: 'left', field: 'driverName', sortable: true },
    { name: 'nfE', label: t('checkInManagement.invoiceNumber'), align: 'left', field: 'nfE', sortable: true },
    { name: 'licensePlate', label: t('checkInManagement.licensePlate'), align: 'left', field: 'licensePlate', sortable: true },
    { name: 'mobilePhone', label: t('checkInManagement.phoneNumber'), align: 'left', field: 'mobilePhone', sortable: true },
    { name: 'exitTime', label: t('checkInManagement.exitTime'), align: 'left', field: 'exitTime', sortable: true },
    { name: 'exitInspection', label: t('checkInManagement.exitInspection'), align: 'left', field: 'exitInspection', sortable: true },
    { name: 'releaseDocuments', label: t('checkInManagement.document'), align: 'center', field: 'releaseDocuments' },
    { name: 'actions', label: t('checkInManagement.actions'), align: 'center' },
];

function viewDocument(url) {
    if (url) {
        window.open(url, '_blank');
    } else {
        alert($t('checkInManagement.documentNotAvailable'));
    }
}
</script>
