<template>
    <q-page class="page q-px-lg">
        <map-component
            :placed-storlocs="placedStorlocs"
            :available-storlocs="availableStorlocs"
            @save-positions="savePositions"
        />
    </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import MapComponent from './MapComfig02.vue';
import { createAxiosInstance } from '../../api/axiosInstance';

const axiosInstance = createAxiosInstance('dev2');

// Estado compartilhado entre os componentes
const storlocs = ref([]);
const availableStorlocs = ref([]);
const placedStorlocs = ref([]);

// Buscar os dados iniciais dos storlocs
async function fetchStorlocs() {
    try {
        const response = await axiosInstance.get('/rest/storloc');
        const data = response.data.content.storlocs || [];

        storlocs.value = data;
        availableStorlocs.value = data.filter((s) => !s.area || !isValidJSON(s.area));
        placedStorlocs.value = data
            .filter((s) => s.area && isValidJSON(s.area))
            .map((s) => {
                const position = JSON.parse(s.area);
                return { ...s, position };
            });
    } catch (error) {
        console.error('Erro ao buscar storlocs:', error);
    }
}

// Salvar posições dos storlocs
async function savePositions(updatedStorlocs) {
    try {
        const updatePromises = updatedStorlocs.map(async (storloc) => {
            storloc.area = JSON.stringify(storloc.position);
            await axiosInstance.put(`/rest/storloc/${storloc.ean}`, storloc);
        });

        await Promise.all(updatePromises);
        alert('Posições salvas com sucesso!');
        fetchStorlocs(); // Atualiza os storlocs após salvar
    } catch (error) {
        console.error('Erro ao salvar posições:', error);
    }
}

function isValidJSON(str) {
    try {
        JSON.parse(str);
        return true;
    } catch {
        return false;
    }
}

// Chamar a função fetchStorlocs no início
onMounted(() => {
    fetchStorlocs();
});
</script>
