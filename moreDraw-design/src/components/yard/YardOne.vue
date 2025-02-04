<template>
    <q-page class="page">
        <q-toolbar-title>Yard Controller</q-toolbar-title>
        <q-space />

        <!-- EAN Input for fetching storlocs -->
        <div class="row q-gutter-md items-center">
            <q-input
                v-model="ean"
                style="min-width: 40%;"
                filled
                label="Enter EAN"
                @keydown.enter="fetchStorloc"
            />

            <q-btn
                label="Create New Storloc"
                color="primary"
                class="q-ml-md"
                @click="openCreateDialog"
            />
        </div>

        <!-- Loader while fetching data -->
        <div v-if="loading">
            <q-spinner class="fixed-center" />
        </div>

        <!-- Draggable Storloc Cards -->
        <draggable
            v-model="storlocs"
            group="storloc-group"
            class="q-gutter-md row"
            @end="saveNewOrder"
        >
            >
            <template #item="{ element }">
                <q-card
                    :key="element.index"
                    class="q-pa-md storloc-card"
                    :style="getCardStyle()"
                    @click="openDetails(element)"
                >
                    <!-- Upper Section with background -->
                    <div
                        class="storloc-card-upper"
                        :style="getBackgroundStyle(element.area)"
                    />

                    <!-- Lower Section with storloc name -->
                    <div
                        style="max-height: 20px; margin-bottom: -15px;"
                        class="storloc-card-lower"
                    >
                        {{ element.ean || 'N/A' }}

                        <!-- Tooltip (balão com o nome da storloc) -->
                        <q-tooltip
                            anchor="top middle"
                            self="bottom middle"
                        >
                            Storloc: {{ element.ean }}
                        </q-tooltip>
                    </div>
                </q-card>
            </template>
        </draggable>

        <!-- Detailed View / Edit Card -->
        <q-dialog v-model="showDetailsDialog">
            <q-card>
                <q-card-section>
                    <div class="text-h6">
                        Edit Storloc - {{ selectedStorloc.ean || 'N/A' }}
                    </div>
                </q-card-section>

                <q-card-section>
                    <q-input
                        v-model="selectedStorloc.ean"
                        label="EAN"
                    />
                    <q-input
                        v-model="selectedStorloc.type"
                        label="Vehicle Type"
                    />
                    <q-input
                        v-model="selectedStorloc.index"
                        label="Index"
                    />
                    <q-input
                        v-model="selectedStorloc.area"
                        label="Area"
                    />
                    <q-input
                        v-model="selectedStorloc.rack"
                        label="Rack"
                    />
                    <q-input
                        v-model="selectedStorloc.zoneId"
                        label="Zone"
                    />
                </q-card-section>

                <q-card-actions align="right">
                    <q-btn
                        flat
                        label="Cancel"
                        @click="showDetailsDialog = false"
                    />
                    <q-btn
                        flat
                        label="Save"
                        color="primary"
                        @click="saveStorloc"
                    />
                </q-card-actions>
            </q-card>
        </q-dialog>
    </q-page>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import draggable from 'vuedraggable';

// Import the images directly at the top
import trailerImage from 'src/assets/imgVehicle/Trailer.webp';
import truckImage from 'src/assets/imgVehicle/Truck.webp';
import dobloImage from 'src/assets/imgVehicle/Doblo.webp';
import emptyImage from 'src/assets/imgVehicle/Empty.webp';
import { createAxiosInstance } from '../api/axiosInstance';

const axiosInstance = createAxiosInstance('dev2');

const loading = ref(false);
const storlocs = ref([]);
const ean = ref('');
const showDetailsDialog = ref(false);
const selectedStorloc = ref({
    ean: '',
    type: '',
    index: '',
    area: '',
    rack: '',
    zoneId: '',
});

onMounted(() => {
    fetchStorloc();
});

// Fetch storlocs based on EAN or all storlocs
async function fetchStorloc() {
    loading.value = true;
    try {
        const response = await axiosInstance.get('/rest/storloc', {
            params: { ean: ean.value },
        });
        storlocs.value = response.data.content.storlocs;
    } catch (error) {
        console.error('Error fetching storloc data:', error);
    } finally {
        loading.value = false;
    }
}

// Save the new order of storlocs after dragging
async function saveNewOrder() {
    // Aqui você pode adicionar a lógica para salvar a nova ordem dos storlocs
    console.log('Nova ordem salva:', storlocs.value);
}

// Open dialog to view and edit storloc details
function openDetails(storloc) {
    selectedStorloc.value = { ...storloc };
    showDetailsDialog.value = true;
}

// Save the edited storloc (update or create)
async function saveStorloc() {
    try {
        if (selectedStorloc.value.ean) {
            await axiosInstance.put(`/rest/storloc/${selectedStorloc.value.ean}`, selectedStorloc.value);
        } else {
            await axiosInstance.post('/rest/storloc', selectedStorloc.value);
        }
        showDetailsDialog.value = false;
        fetchStorloc();
    } catch (error) {
        console.error('Error saving storloc:', error);
    }
}

// Open dialog for creating a new storloc
function openCreateDialog() {
    selectedStorloc.value = {
        ean: '',
        type: '',
        index: '',
        area: '',
        rack: '',
        zoneId: '',
    };
    showDetailsDialog.value = true;
}

// Function to get the card background based on the 'area' value
function getBackgroundStyle(area) {
    let backgroundImage;
    switch (area) {
        case '1':
            backgroundImage = `url(${trailerImage})`;
            break;
        case '2':
            backgroundImage = `url(${truckImage})`;
            break;
        case '3':
            backgroundImage = `url(${dobloImage})`;
            break;
        default:
            backgroundImage = `url(${emptyImage})`;
    }

    return {
        backgroundImage,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        height: '70%', // Upper section takes 70%
        borderRadius: '10px 10px 0 0',
    };
}

function getCardStyle() {
    return {
        width: '250px',
        height: '200px',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        borderRadius: '10px',
        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)',
    };
}
</script>

<style scoped>
.storloc-card {
    position: relative;
    cursor: pointer;
}

.storloc-card-upper {
    flex: 1;
}

.storloc-card-lower {
    height: 30%;
    background-color: white;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 18px;
    font-weight: bold;
    color: black;
    border-radius: 0 0 10px 10px;
}
</style>
