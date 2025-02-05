<template>
    <q-card
        class="q-ma-md"
        flat
        bordered
    >
        <q-card-section>
            <q-toolbar-title>Eventos Agendados</q-toolbar-title>
        </q-card-section>

        <q-table
            :rows="scheduledEvents"
            :columns="columns"
            row-key="eventId"
            flat
            bordered
            dense
        />
    </q-card>
</template>

<script setup>
import { ref } from 'vue';

const scheduledEvents = ref([
    {
        eventId: 'EV001',
        orderTransporterId: 'OT123',
        startStorloc: 'SL001',
        destinationStorloc: 'SL005',
        scheduling: '2024-09-26 14:00',
        state: 'Pendente',
        category: 'Movimentação Interna',
    },
    {
        eventId: 'EV002',
        orderTransporterId: 'OT456',
        startStorloc: 'SL002',
        destinationStorloc: 'SL006',
        scheduling: '2024-09-26 15:30',
        state: 'Concluído',
        category: 'Descarregamento',
    },
]);

const columns = [
    { name: 'eventId', label: 'ID Evento', align: 'left', field: 'eventId' },
    { name: 'orderTransporterId', label: 'ID Transporte', align: 'left', field: 'orderTransporterId' },
    { name: 'startStorloc', label: 'Origem', align: 'left', field: 'startStorloc' },
    { name: 'destinationStorloc', label: 'Destino', align: 'left', field: 'destinationStorloc' },
    { name: 'scheduling', label: 'Data Agendada', align: 'left', field: 'scheduling' },
    { name: 'state', label: 'Estado', align: 'left', field: 'state' },
    { name: 'category', label: 'Categoria', align: 'left', field: 'category' },
];
</script>
