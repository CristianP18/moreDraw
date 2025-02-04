<template>
    <spk-card
        class="q-ma-md"
        flat
        bordered
    >
        <spk-card-section>
            <q-toolbar-title>{{ $t('checkInManagement.vehicleCheckIn') }}</q-toolbar-title>
        </spk-card-section>

        <spk-table
            :rows="checkInList"
            :columns="columns"
            row-key="gatewayId"
            :pagination="{ page: 1, rowsPerPage: 25 }"
            flat
            bordered
            dense
        >
            <!-- Custom rendering for Vehicle Picture -->
            <template #body-cell-vehiclePicture="props">
                <q-td align="center">
                    <q-img
                        :src="props.row.vehiclePicture || 'https://via.placeholder.com/60x40?text=No+Photo'"
                        style="height: 40px; width: 60px; cursor: pointer;"
                        spinner-color="grey-3"
                        ratio="3/2"
                        @dblclick="$emit('edit-image', { type: 'vehicle', row: props.row })"
                    />
                </q-td>
            </template>

            <!-- Custom rendering for Driver Picture -->
            <template #body-cell-driverPicture="props">
                <q-td align="center">
                    <q-img
                        :src="props.row.driverPicture || 'https://via.placeholder.com/60x40?text=No+Photo'"
                        style="height: 40px; width: 60px; cursor: pointer;"
                        spinner-color="grey-3"
                        ratio="3/2"
                        @dblclick="$emit('edit-image', { type: 'driver', row: props.row })"
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
                        class="q-mr-sm"
                        @click="$emit('edit', props.row)"
                    />
                    <q-btn
                        flat
                        icon="mdi-exit-run"
                        color="negative"
                        :label="$t('checkInManagement.checkOut')"
                        @click="$emit('checkout', props.row)"
                    />
                </q-td>
            </template>
        </spk-table>
    </spk-card>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
import { useI18n } from 'vue-i18n';

const props = defineProps({
    checkInList: {
        type: Array,
        required: true,
    },
});
const { t } = useI18n();
const columns = [
    { name: 'driverName', label: t('checkInManagement.driver'), align: 'left', field: 'driverName', sortable: true },
    { name: 'licensePlate', label: t('checkInManagement.licensePlate'), align: 'left', field: 'licensePlate', sortable: true },
    { name: 'nfE', label: t('checkInManagement.invoiceNumber'), align: 'left', field: 'nfE', sortable: true },
    { name: 'location', label: t('checkInManagement.location'), align: 'left', field: 'location', sortable: true },
    { name: 'mobilePhone', label: t('checkInManagement.phoneNumber'), align: 'left', field: 'mobilePhone', sortable: true },
    { name: 'entryTime', label: t('checkInManagement.entryTime'), align: 'left', field: 'entryTime', sortable: true },
    { name: 'driverFederalTax', label: t('checkInManagement.driverCpf'), align: 'left', field: 'driverFederalTax', sortable: true },
    { name: 'boardingType', label: t('checkInManagement.loadType'), align: 'left', field: 'boardingType', sortable: true },
    { name: 'boardingDocumentation', label: t('checkInManagement.boardingDocuments'), align: 'left', field: 'boardingDocumentation', sortable: true },
    { name: 'boardingCondition', label: t('checkInManagement.boardingCondition'), align: 'left', field: 'boardingCondition', sortable: true },
    { name: 'vehiclePicture', label: t('checkInManagement.vehiclePicture'), align: 'center', field: 'vehiclePicture' },
    { name: 'driverPicture', label: t('checkInManagement.driverPicture'), align: 'center', field: 'driverPicture' },
    { name: 'actions', label: t('checkInManagement.actions'), align: 'center' },
];

const emit = defineEmits(['edit', 'checkout', 'edit-image']);
</script>
