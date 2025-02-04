<template>
    <spk-card
        class="q-ma-md"
        flat
        bordered
    >
        <spk-card-section>
            <q-toolbar-title>{{ $t('eventsScheduling.eventsScheduling') }}</q-toolbar-title>
        </spk-card-section>

        <spk-table
            :rows="mappedScheduledEvents"
            :columns="columns"
            row-key="eventsSchedulingId"
            :pagination="{ page: 1, rowsPerPage: 25 }"
            flat
            bordered
            dense
        >
            <!-- Custom formatting for date columns -->
            <template #body-cell-scheduling="props">
                <q-td align="left">
                    {{ formatDateTime(props.row.scheduling) }}
                </q-td>
            </template>
            <template #body-cell-begin="props">
                <q-td align="left">
                    {{ formatDateTime(props.row.begin) }}
                </q-td>
            </template>
            <template #body-cell-end="props">
                <q-td align="left">
                    {{ formatDateTime(props.row.end) }}
                </q-td>
            </template>

            <!-- Actions column -->
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
import { ref, onMounted, defineProps } from 'vue';
import { date } from 'quasar';
import { useI18n } from 'vue-i18n';
import { createAxiosInstance } from '../../../api/axiosInstance';

const axiosInstance = createAxiosInstance('dev2');
const { t } = useI18n();
// Define props
const props = defineProps({
    scheduledEvents: {
        type: Array,
        required: true,
    },
});

// Columns for the table
const columns = [
    { name: 'orderTransporterId', label: t('eventsScheduling.transportIdentifier'), align: 'left', field: 'orderTransporterId', sortable: true },
    { name: 'licensePlate', label: t('eventsScheduling.vehicleLicensePlate'), align: 'left', field: 'licensePlate', sortable: true },
    { name: 'startStorloc', label: t('eventsScheduling.sourceStorloc'), align: 'left', field: 'startStorloc', sortable: true },
    { name: 'destinator', label: t('eventsScheduling.destinationStorloc'), align: 'left', field: 'destinator', sortable: true },
    { name: 'scheduling', label: t('eventsScheduling.scheduledTime'), align: 'left', field: 'scheduling', sortable: true },
    { name: 'state', label: t('eventsScheduling.state'), align: 'left', field: 'state', sortable: true },
    { name: 'category', label: t('eventsScheduling.category'), align: 'left', field: 'category', sortable: true },
    { name: 'begin', label: t('eventsScheduling.start'), align: 'left', field: 'begin', sortable: true },
    { name: 'end', label: t('eventsScheduling.end'), align: 'left', field: 'end', sortable: true },
    { name: 'time', label: t('eventsScheduling.duration'), align: 'left', field: 'time', sortable: true },
    { name: 'actions', label: t('eventsScheduling.actions'), align: 'center' },
];

// Map scheduled events with zoneId instead of storloc ean
const mappedScheduledEvents = ref([]);

// Function to format date and time
function formatDateTime(dateTime) {
    if (!dateTime) return 'N/A';
    return date.formatDate(dateTime, 'DD/MM/YYYY HH:mm');
}

// Function to fetch storloc data from the API
async function fetchStorlocMappings() {
    try {
        const response = await axiosInstance.get('/rest/storloc');
        if (response.data?.content?.storlocs) {
            return response.data.content.storlocs.reduce((map, storloc) => {
                map[storloc.ean] = storloc.zoneId;
                return map;
            }, {});
        }
        return {};
    } catch (error) {
        console.error('Error fetching storloc mappings:', error);
        return {};
    }
}

// Maps the scheduled events data
async function mapScheduledEvents() {
    const storlocMapping = await fetchStorlocMappings();

    mappedScheduledEvents.value = props.scheduledEvents.map((event) => ({
        ...event,
        startStorloc: storlocMapping[event.startStorloc] || event.startStorloc,
        destinator: storlocMapping[event.destinator] || event.destinator,
    }));
}

// Run on mounted
onMounted(() => {
    mapScheduledEvents();
});
</script>
