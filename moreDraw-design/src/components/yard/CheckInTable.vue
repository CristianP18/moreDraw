<template>
    <q-card
        class="q-ma-md"
        flat
        bordered
    >
        <q-card-section>
            <q-toolbar-title>Check-In de Veículos</q-toolbar-title>
        </q-card-section>

        <q-table
            :rows="checkInList"
            :columns="columns"
            row-key="gatewayId"
            flat
            bordered
            dense
        >
            <template #body-cell-vehiclePicture="props">
                <q-img
                    :src="props.row.vehiclePicture"
                    style="height: 40px; width: 60px;"
                />
            </template>
        </q-table>
    </q-card>
</template>

<script setup>
import { ref } from 'vue';

const checkInList = ref([
    {
        gatewayId: 'CHK001',
        nfE: '123456',
        vehicleContainerNumber: 'VEH12345',
        entryTime: '2024-09-25 08:30',
        driver: 'João da Silva',
        driverFederalTax: '123.456.789-00',
        boardingType: 'Carga Frágil',
        licensePlate: 'XYZ-1234',
        vehiclePicture: 'https://via.placeholder.com/60x40',
    },
    {
        gatewayId: 'CHK002',
        nfE: '7891011',
        vehicleContainerNumber: 'VEH67890',
        entryTime: '2024-09-25 09:15',
        driver: 'Maria Souza',
        driverFederalTax: '987.654.321-00',
        boardingType: 'Carga Pesada',
        licensePlate: 'ABC-9876',
        vehiclePicture: 'https://via.placeholder.com/60x40',
    },
]);

const columns = [
    { name: 'gatewayId', label: 'ID', align: 'left', field: 'gatewayId' },
    { name: 'nfE', label: 'Nota Fiscal', align: 'left', field: 'nfE' },
    { name: 'vehicleContainerNumber', label: 'ID Veículo', align: 'left', field: 'vehicleContainerNumber' },
    { name: 'entryTime', label: 'Hora Entrada', align: 'left', field: 'entryTime' },
    { name: 'driver', label: 'Motorista', align: 'left', field: 'driver' },
    { name: 'licensePlate', label: 'Placa', align: 'left', field: 'licensePlate' },
    { name: 'vehiclePicture', label: 'Foto', align: 'center', field: 'vehiclePicture' },
];
</script>
