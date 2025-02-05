<template>
    <q-card
        class="q-ma-md"
        flat
        bordered
    >
        <q-card-section>
            <q-toolbar-title>Check-Out de Veículos</q-toolbar-title>
        </q-card-section>

        <q-table
            :rows="checkOutList"
            :columns="columns"
            row-key="gatewayId"
            flat
            bordered
            dense
        >
            <template #body-cell-releaseDocuments="props">
                <q-btn
                    flat
                    label="Ver Documento"
                    @click="viewDocument(props.row.releaseDocuments)"
                />
            </template>
        </q-table>
    </q-card>
</template>

<script setup>
import { ref } from 'vue';

const checkOutList = ref([
    {
        gatewayId: 'CHK001',
        exitTime: '2024-09-25 11:30',
        exitInspection: 'Sem danos',
        releaseDocuments: 'https://example.com/doc1.pdf',
    },
    {
        gatewayId: 'CHK002',
        exitTime: '2024-09-25 12:00',
        exitInspection: 'Carga intacta',
        releaseDocuments: 'https://example.com/doc2.pdf',
    },
]);

const columns = [
    { name: 'gatewayId', label: 'ID', align: 'left', field: 'gatewayId' },
    { name: 'exitTime', label: 'Hora Saída', align: 'left', field: 'exitTime' },
    { name: 'exitInspection', label: 'Inspeção Saída', align: 'left', field: 'exitInspection' },
    { name: 'releaseDocuments', label: 'Documento', align: 'center', field: 'releaseDocuments' },
];

function viewDocument(url) {
    window.open(url, '_blank');
}
</script>
