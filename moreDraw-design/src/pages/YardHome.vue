<!-- src/pages/YardHome.vue -->
<template>
    <q-page class="page q-px-lg">
        <!-- Barra de Navegação -->
        <q-toolbar class="q-px-md q-py-sm bg-primary text-white">
            <q-toolbar-title>{{ $t('yardHome.toolbarTitle') }}</q-toolbar-title>

            <!-- Botões de Ações Principais -->
            <q-btn
                flat
                icon="mdi-truck-check"
                :label="$t('yardHome.checkIn')"
                class="q-ml-sm"
                @click="setManagementView('checkin')"
            />
            <q-btn
                flat
                icon="mdi-truck-check-outline"
                :label="$t('yardHome.checkOut')"
                class="q-ml-sm"
                @click="setManagementView('checkout')"
            />
            <q-btn
                flat
                icon="mdi-calendar-clock"
                :label="$t('yardHome.scheduledEvents')"
                class="q-ml-sm"
                @click="setManagementView('eventos')"
            />
            <q-btn
                flat
                icon="mdi-map"
                :label="$t('yardHome.manageYard')"
                class="q-ml-sm"
                @click="setManagementView('patio')"
            />
            <q-btn
                flat
                icon="mdi-cog"
                :label="$t('yardHome.configuration')"
                class="q-ml-sm"
                @click="setManagementView('config')"
            />
        </q-toolbar>

        <!-- Conteúdo Dinâmico -->
        <div class="q-mt-md">
            <div v-if="showManagementView === 'patio'">
                <maps style="margin-left: -20px; margin-right: -25px;" />
            </div>
            <div v-if="showManagementView === 'config'">
                <Configuration />
            </div>
            <div v-if="showManagementView === 'checkin'">
                <CheckInTable
                    :check-in-list="checkInList"
                    @edit="editCheckIn"
                    @checkout="openCheckOutDialog"
                    @edit-image="handleEditImage"
                />
                <q-btn
                    flat
                    :label="$t('yardHome.newCheckIn')"
                    icon="mdi-plus"
                    color="primary"
                    class="q-my-md"
                    @click="openCheckInDialog()"
                />
            </div>
            <div v-if="showManagementView === 'checkout'">
                <CheckOutTable
                    :check-out-list="checkOutList"
                    @edit="editCheckOut"
                />
            </div>
            <div v-if="showManagementView === 'eventos'">
                <ScheduledEventsTable
                    :scheduled-events="scheduledEvents"
                    @edit="editScheduledEvent"
                />
                <q-btn
                    flat
                    :label="$t('yardHome.newEvent')"
                    icon="mdi-plus"
                    color="primary"
                    class="q-my-md"
                    @click="openSchedulingDialog()"
                />
            </div>
        </div>

        <!-- Componente para Gestão de Check-In -->
        <CheckInManagement
            :show="showCheckInDialog"
            :check-in-data="checkInData"
            :is-saving="isSavingCheckIn"
            :is-valid="isCheckInValid"
            :storloc-options="storlocOptions"
            :is-loading-storloc="isLoadingStorloc"
            :selected-storloc-id="selectedStorlocId"
            @update:check-in-data="checkInData = $event"
            @update:show="showCheckInDialog = $event"
            @fetch-storlocs="fetchStorlocs"
            @confirm-checkin="handleConfirmCheckIn"
            @close="closeCheckInDialog"
            @update:selected-storloc-id="val => selectedStorlocId = val"
        />

        <!-- Componente para Gestão de Check-Out -->
        <CheckOutManagement
            :show="showCheckOutDialog"
            :check-out-data="checkOutData"
            :is-saving="isSavingCheckOut"
            :is-valid="isCheckOutValid"
            @update:show="showCheckOutDialog = $event"
            @confirm-checkout="handleConfirmCheckOut"
            @close="closeCheckOutDialog"
        />

        <!-- Componente para Gestão de Eventos Agendados -->
        <EventSchedulingManagement
            :show="showSchedulingDialog"
            :scheduling-data="schedulingData"
            :is-saving="isSavingScheduling"
            :storloc-options="storlocOptions"
            :is-loading-storloc="isLoadingStorloc"
            :state-options="stateOptions"
            :category-options="categoryOptions"
            :selected-start-storloc-id="selectedStartStorlocId"
            :selected-destinator-storloc-id="selectedDestinatorStorlocId"
            @update:show="showSchedulingDialog = $event"
            @close="closeSchedulingDialog"
            @fetch-storlocs="fetchStorlocs"
            @confirm-scheduling="handleConfirmScheduling"
            @update:selected-start-storloc-id="val => selectedStartStorlocId = val"
            @update:selected-destinator-storloc-id="val => selectedDestinatorStorlocId = val"
        />

        <!-- Dialog para Upload de Nova Imagem -->
        <q-dialog v-model="showImageUploadDialog">
            <q-card>
                <q-card-section>
                    <div class="text-h6">
                        {{ $t('yardHome.updateImage') }}
                    </div>
                </q-card-section>
                <q-card-section>
                    <input
                        ref="imageUploadInput"
                        type="file"
                        accept="image/*"
                        style="display: none;"
                        @change="handleImageUpload"
                    >
                    <!-- Botão para abrir o seletor de arquivo -->
                    <q-btn
                        flat
                        icon="image"
                        :label="$t('yardHome.selectImage')"
                        @click="triggerImageUpload"
                    />
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn
                        flat
                        :label="$t('yardHome.cancel')"
                        @click="closeImageUploadDialog"
                    />
                    <q-btn
                        flat
                        :label="$t('yardHome.save')"
                        color="primary"
                        :disable="!newImageFile || isUploadingImage"
                        :loading="isUploadingImage"
                        @click="confirmImageUpload"
                    />
                </q-card-actions>
            </q-card>
        </q-dialog>
    </q-page>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useQuasar } from 'quasar';
import { createAxiosInstance } from '../api/axiosInstance';
import maps from '../components/yard/Map02.vue';
import CheckInTable from '../components/yard/check-in/CheckInTable.vue';
import CheckOutTable from '../components/yard/check-out/CheckOutTable.vue';
import ScheduledEventsTable from '../components/yard/eventScheduling/ScheduledEventsTable.vue';
import Configuration from '../components/yard/YardConfig02.vue';
import CheckInManagement from '../components/yard/check-in/CheckInManagement.vue';
import CheckOutManagement from '../components/yard/check-out/CheckOutManagement.vue';
import EventSchedulingManagement from '../components/yard/eventScheduling/EventSchedulingManagement.vue';

const $q = useQuasar();

// Instâncias Axios
const axiosInstance = createAxiosInstance('yard');
const axiosInstancedev = createAxiosInstance('dev2');

// Controle de Visualização
const showManagementView = ref('patio');
const showCheckInDialog = ref(false);
const showCheckOutDialog = ref(false);
const showSchedulingDialog = ref(false);

// Dados dos Formulários
const checkInData = reactive({
    gatewayId: null,
    location: '',
    nfE: '',
    mobilePhone: '',
    entryTime: '',
    driverName: '',
    driverFederalTax: '',
    boardingType: '',
    boardingDocumentation: '',
    licensePlate: '',
    boardingCondition: '',
    storlocEanLocation: '',
    vehiclePicture: [], // inicia como array vazio
    driverPicture: [], // inicia como array vazio
    created: '',
    modified: '',
    createdBy: '',
    yardControllerId: '',
});

const checkOutData = reactive({
    gatewayId: null,
    exitTime: '',
    exitInspection: '',
    releaseDocuments: null,
    modified: '',
});

const schedulingData = reactive({
    eventSchedulingId: null,
    orderTransporterId: '',
    yardControllerId: '',
    startStorloc: '',
    destinator: '',
    licensePlate: '',
    state: '',
    category: '',
    begin: '',
    end: '',
    time: '',
    created: '',
    modified: '',
    scheduling: '',
});

// Registro Selecionado para Check-Out
const selectedCheckIn = ref(null);

// Listas de Dados
const checkInList = ref([]);
const checkOutList = ref([]);
const scheduledEvents = ref([]);

// Opções para Selects
const stateOptions = ref([
    { label: 'Scheduled', value: 1 },
    { label: 'In Progress', value: 2 },
    { label: 'Completed', value: 6 },
    { label: 'Cancelled', value: 7 },
]);

const categoryOptions = ref([
    { label: 'Full Load', value: 'Full Load' },
    { label: 'Partial Load', value: 'Partial Load' },
    { label: 'Empty', value: 'Empty' },
]);

// Variáveis para Storloc
const storlocOptions = ref([]);
const selectedStorlocId = ref(null);
const selectedStartStorlocId = ref(null);
const selectedDestinatorStorlocId = ref(null);
const isLoadingStorloc = ref(false);

// Variáveis para Indicadores de Carregamento
const isSavingCheckIn = ref(false);
const isSavingCheckOut = ref(false);
const isSavingScheduling = ref(false);

// Variáveis para Upload de Imagem via Diálogo
const showImageUploadDialog = ref(false);
const newImageFile = ref(null);
const isUploadingImage = ref(false);
const currentGatewayId = ref(null);
const currentImageType = ref(null); // 'vehicle' ou 'driver'

// Função para abrir o seletor de arquivo
function triggerImageUpload() {
    const input = imageUploadInput.value;
    if (input) {
        input.click();
    }
}

// Referência para o input de upload
const imageUploadInput = ref(null);

// Função para lidar com a seleção de arquivo
function handleImageUpload(event) {
    const files = event.target.files;
    if (files && files.length > 0) {
        newImageFile.value = files;
    } else {
        newImageFile.value = null;
    }
}

// Função para confirmar o upload da imagem
async function confirmImageUpload() {
    console.log('confirmImageUpload chamado');
    if (!newImageFile.value || newImageFile.value.length === 0) {
        console.error('Nenhum arquivo selecionado para upload da imagem.');
        $q.notify({
            type: 'negative',
            message: 'Por favor, selecione uma imagem antes de salvar.',
        });
        return;
    }

    const file = newImageFile.value[0];
    console.log('Arquivo selecionado para upload:', file.name);

    try {
        const formData = new FormData();
        formData.append('file', file);

        let apiUrl = '';
        if (currentImageType.value === 'vehicle') {
            apiUrl = `/rest/gateway/picture?vehicle=${currentGatewayId.value}`;
        } else if (currentImageType.value === 'driver') {
            apiUrl = `/rest/gateway/picture?driver=${currentGatewayId.value}`;
        } else {
            throw new Error('Tipo de imagem inválido.');
        }

        console.log('Enviando imagem para API em:', apiUrl);
        isUploadingImage.value = true;
        await axiosInstance.put(apiUrl, formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        });

        console.log('Upload da imagem concluído com sucesso!');
        $q.notify({ type: 'positive', message: 'Imagem atualizada com sucesso!' });
        closeImageUploadDialog();

        // Atualize a lista de Check-Ins para refletir a alteração
        fetchCheckIns();
    } catch (error) {
        console.error('Erro ao fazer upload da imagem:', error);
        $q.notify({ type: 'negative', message: 'Erro ao fazer upload da imagem.' });
    } finally {
        isUploadingImage.value = false;
        // Limpar o valor do input para permitir o upload do mesmo arquivo novamente, se necessário
        if (imageUploadInput.value) {
            imageUploadInput.value.value = '';
        }
    }
}

// Métodos para Buscar Storlocs
async function fetchStorlocs() {
    console.log('fetchStorlocs chamado');
    isLoadingStorloc.value = true;
    try {
        const response = await axiosInstancedev.get('/rest/storloc');
        console.log('fetchStorlocs resposta:', response.data);

        // Ajusta storlocOptions conforme estrutura recebida
        if (response.data && Array.isArray(response.data.storlocs)) {
            storlocOptions.value = response.data.storlocs.map((storloc) => ({
                label: storloc.zoneId,
                value: storloc.zoneId,
                ean: storloc.ean,
            }));
        } else if (
            response.data
        && response.data.content
        && Array.isArray(response.data.content.storlocs)
        ) {
            storlocOptions.value = response.data.content.storlocs.map((storloc) => ({
                label: storloc.zoneId,
                value: storloc.zoneId,
                ean: storloc.ean,
            }));
        } else if (Array.isArray(response.data)) {
            storlocOptions.value = response.data.map((storloc) => ({
                label: storloc.zoneId,
                value: storloc.zoneId,
                ean: storloc.ean,
            }));
        } else {
            storlocOptions.value = [];
            $q.notify({
                type: 'warning',
                message: 'Nenhuma Doca de alocação encontrada.',
            });
        }

        console.log('storlocOptions após fetch:', storlocOptions.value);
    } catch (error) {
        console.error('Erro ao buscar Doca de alocação:', error);
        $q.notify({
            type: 'negative',
            message: 'Erro ao buscar Doca de alocação.',
        });
    } finally {
        isLoadingStorloc.value = false;
    }
}

// Watcher para selectedStorlocId
watch(
    () => selectedStorlocId.value,
    (newVal) => {
        console.log('Watcher: selectedStorlocId alterado para:', newVal);
        if (newVal) {
            onStorlocSelected(newVal);
        }
    },
);

// Função para atualizar storlocEanLocation
function onStorlocSelected(zoneId) {
    const selectedStorloc = storlocOptions.value.find(
        (storloc) => storloc.value === zoneId,
    );
    checkInData.storlocEanLocation = selectedStorloc ? selectedStorloc.ean : '';
    console.log('onStorlocSelected chamado. zoneId:', zoneId, 'ean setado em checkInData:', checkInData.storlocEanLocation);
}

// Métodos para Buscar Dados
async function fetchCheckIns() {
    console.log('Buscando Check-Ins...');
    try {
        const response = await axiosInstance.get('/rest/gateway/all');
        if (response.data && response.data.content && response.data.content.gateways) {
            checkInList.value = response.data.content.gateways.filter((g) => !g.exitTime);
        } else {
            checkInList.value = [];
            $q.notify({ type: 'warning', message: 'Nenhum Check-In encontrado.' });
        }
        console.log('CheckInList após fetch:', checkInList.value);
    } catch (error) {
        console.error('Erro ao buscar Check-Ins:', error);
        $q.notify({ type: 'negative', message: 'Erro ao buscar Check-Ins.' });
    }
}

async function fetchCheckOuts() {
    console.log('Buscando Check-Outs...');
    try {
        const response = await axiosInstance.get('/rest/gateway/all');
        if (response.data && response.data.content && response.data.content.gateways) {
            checkOutList.value = response.data.content.gateways.filter((g) => g.exitTime);
        } else {
            checkOutList.value = [];
            $q.notify({ type: 'warning', message: 'Nenhum Check-Out encontrado.' });
        }
        console.log('CheckOutList após fetch:', checkOutList.value);
    } catch (error) {
        console.error('Erro ao buscar Check-Outs:', error);
        $q.notify({ type: 'negative', message: 'Erro ao buscar Check-Outs.' });
    }
}

async function fetchScheduledEvents() {
    console.log('Buscando Eventos Agendados...');
    try {
        const response = await axiosInstance.get('/rest/eventScheduling/all');
        if (
            response.data
        && response.data.content
        && response.data.content.eventSchedulings
        ) {
            scheduledEvents.value = response.data.content.eventSchedulings;
        } else {
            scheduledEvents.value = [];
            $q.notify({ type: 'warning', message: 'Nenhum Evento Agendado encontrado.' });
        }
        console.log('ScheduledEvents após fetch:', scheduledEvents.value);
    } catch (error) {
        console.error('Erro ao buscar Eventos Agendados:', error);
        $q.notify({ type: 'negative', message: 'Erro ao buscar Eventos Agendados.' });
    }
}

// Métodos para Abrir Diálogos
function openCheckInDialog(data = null) {
    if (data) {
        console.log('Abrindo CheckInDialog para edição com dados:', data);
        // Garante que as imagens sejam arrays
        if (!data.vehiclePicture || !Array.isArray(data.vehiclePicture)) {
            data.vehiclePicture = [];
        }
        if (!data.driverPicture || !Array.isArray(data.driverPicture)) {
            data.driverPicture = [];
        }
        Object.assign(checkInData, { ...data });
    } else {
        console.log('Abrindo CheckInDialog para novo Check-In.');
        Object.assign(checkInData, {
            gatewayId: null,
            location: '',
            nfE: '',
            mobilePhone: '',
            entryTime: '',
            driverName: '',
            driverFederalTax: '',
            boardingType: '',
            boardingDocumentation: '',
            licensePlate: '',
            boardingCondition: '',
            storlocEanLocation: '',
            vehiclePicture: [], // array vazio
            driverPicture: [], // array vazio
            created: '',
            modified: '',
            createdBy: '',
            yardControllerId: '',
        });
    }
    console.log('Dados do checkInData ao abrir o diálogo:', checkInData);
    selectedStorlocId.value = null;
    storlocOptions.value = [];
    fetchStorlocs();
    showCheckInDialog.value = true;
}

function openCheckOutDialog(row = null) {
    if (row) {
        console.log('Abrindo CheckOutDialog com row:', row);
        selectedCheckIn.value = row;
        checkOutData.gatewayId = row.gatewayId;
        checkOutData.exitTime = '';
        checkOutData.exitInspection = '';
        checkOutData.releaseDocuments = null;
        showCheckOutDialog.value = true;
    }
}

function openSchedulingDialog(data = null) {
    if (data) {
        console.log('Abrindo SchedulingDialog para edição com dados:', data);
        Object.assign(schedulingData, { ...data });
    } else {
        console.log('Abrindo SchedulingDialog para novo evento.');
        Object.assign(schedulingData, {
            eventSchedulingId: null,
            orderTransporterId: '',
            yardControllerId: '',
            startStorloc: '',
            destinator: '',
            scheduling: '',
            state: '',
            category: '',
            begin: '',
            end: '',
            time: '',
            created: '',
            modified: '',
        });
    }
    console.log('Dados do schedulingData ao abrir o diálogo:', schedulingData);
    selectedStartStorlocId.value = null;
    selectedDestinatorStorlocId.value = null;
    storlocOptions.value = [];
    fetchStorlocs();
    showSchedulingDialog.value = true;
}

// Fechar Diálogos
function closeCheckInDialog() {
    console.log('Fechando CheckInDialog');
    showCheckInDialog.value = false;
}

function closeCheckOutDialog() {
    console.log('Fechando CheckOutDialog');
    showCheckOutDialog.value = false;
}

function closeSchedulingDialog() {
    console.log('Fechando SchedulingDialog');
    showSchedulingDialog.value = false;
}

// Funções de Upload de Imagem (Check-In)
async function uploadVehiclePicture(gatewayId, file) {
    console.log('Chamando uploadVehiclePicture - gatewayId:', gatewayId, 'file:', file);
    if (!file || !gatewayId) {
        console.log('uploadVehiclePicture: gatewayId ou file inválido.');
        return;
    }
    const formData = new FormData();
    formData.append('file', file);
    try {
        await axiosInstance.put(`/rest/gateway/picture?vehicle=${gatewayId}`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        });
        console.log('uploadVehiclePicture concluído');
        $q.notify({ type: 'positive', message: 'Imagem do veículo atualizada com sucesso!' });
    } catch (error) {
        console.error('Erro ao fazer upload da imagem do veículo:', error);
        $q.notify({ type: 'negative', message: 'Erro ao fazer upload da imagem do veículo.' });
    }
}

async function uploadDriverPicture(gatewayId, file) {
    console.log('Chamando uploadDriverPicture - gatewayId:', gatewayId, 'file:', file);
    if (!file || !gatewayId) {
        console.log('uploadDriverPicture: gatewayId ou file inválido.');
        return;
    }
    const formData = new FormData();
    formData.append('file', file);
    try {
        await axiosInstance.put(`/rest/gateway/picture?driver=${gatewayId}`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        });
        console.log('uploadDriverPicture concluído');
        $q.notify({ type: 'positive', message: 'Imagem do motorista atualizada com sucesso!' });
    } catch (error) {
        console.error('Erro ao fazer upload da imagem do motorista:', error);
        $q.notify({ type: 'negative', message: 'Erro ao fazer upload da imagem do motorista.' });
    }
}

async function uploadFile(file) {
    console.log('Chamando uploadFile - file:', file);
    if (!file) {
        console.log('uploadFile: nenhum arquivo para enviar.');
        return null;
    }
    const formData = new FormData();
    formData.append('file', file);
    try {
        const response = await axiosInstance.post('/rest/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        });
        console.log('uploadFile concluído. URL recebida:', response.data.url);
        return response.data.url;
    } catch (error) {
        console.error('Erro ao fazer upload do arquivo:', error);
        $q.notify({ type: 'negative', message: 'Erro ao fazer upload do arquivo.' });
        return null;
    }
}

// Confirmar Check-In
async function handleConfirmCheckIn() {
    console.log('handleConfirmCheckIn chamado');
    console.log('Dados atuais do checkInData:', checkInData);
    console.log('vehiclePicture:', checkInData.vehiclePicture, 'driverPicture:', checkInData.driverPicture);
    isSavingCheckIn.value = true;
    try {
        let gatewayId = checkInData.gatewayId;

        const payload = { ...checkInData };
        delete payload.vehiclePicture;
        delete payload.driverPicture;

        if (gatewayId) {
            console.log('Atualizando Check-In existente. GatewayId:', gatewayId);
            await axiosInstance.put(`/rest/gateway/${gatewayId}`, payload);
            $q.notify({ type: 'positive', message: 'Check-In atualizado com sucesso!' });
        } else {
            console.log('Criando novo Check-In.');
            const response = await axiosInstance.post('/rest/gateway', payload);

            if (response.data && response.data.content && response.data.content.gatewayId) {
                gatewayId = response.data.content.gatewayId;
                checkInData.gatewayId = gatewayId;
                console.log('Novo Check-In criado. GatewayId:', gatewayId);
                $q.notify({ type: 'positive', message: 'Check-In criado com sucesso!' });
            } else {
                throw new Error('Falha ao obter gatewayId após criação do Check-In.');
            }
        }

        console.log('Verificando imagens para upload após criar/atualizar Check-In (gatewayId:', gatewayId, ')');

        if (gatewayId) {
            if (checkInData.vehiclePicture && checkInData.vehiclePicture.length > 0) {
                const vehicleFile = checkInData.vehiclePicture[0];
                console.log('Imagem de veículo encontrada para upload:', vehicleFile);
                if (vehicleFile) {
                    await uploadVehiclePicture(gatewayId, vehicleFile);
                }
            } else {
                console.log('Nenhuma imagem de veículo para enviar.');
            }

            if (checkInData.driverPicture && checkInData.driverPicture.length > 0) {
                const driverFile = checkInData.driverPicture[0];
                console.log('Imagem de motorista encontrada para upload:', driverFile);
                if (driverFile) {
                    await uploadDriverPicture(gatewayId, driverFile);
                }
            } else {
                console.log('Nenhuma imagem de motorista para enviar.');
            }
        }

        fetchCheckIns();
        closeCheckInDialog();
    } catch (error) {
        console.error('Erro ao salvar Check-In:', error);
        $q.notify({ type: 'negative', message: 'Erro ao salvar Check-In.' });
    } finally {
        isSavingCheckIn.value = false;
    }
}

// Confirmar Check-Out
async function handleConfirmCheckOut() {
    console.log('handleConfirmCheckOut chamado');
    console.log('Dados atuais do checkOutData:', checkOutData);
    isSavingCheckOut.value = true;
    try {
        if (checkOutData.releaseDocuments && checkOutData.releaseDocuments.length > 0) {
            console.log('Documento de saída encontrado para upload:', checkOutData.releaseDocuments[0]);
            const releaseDocURL = await uploadFile(checkOutData.releaseDocuments[0]);
            if (releaseDocURL) {
                checkOutData.releaseDocuments = releaseDocURL;
            } else {
                throw new Error('Falha no upload dos documentos de saída.');
            }
        } else {
            console.log('Nenhum documento de saída para upload.');
            checkOutData.releaseDocuments = null;
        }

        checkOutData.exitTime = new Date().toISOString();
        checkOutData.modified = new Date().toISOString();
        console.log('Realizando PUT para Check-Out com gatewayId:', checkOutData.gatewayId);
        await axiosInstance.put(`/rest/gateway/${checkOutData.gatewayId}`, checkOutData);
        $q.notify({ type: 'positive', message: 'Check-Out realizado com sucesso!' });

        checkInList.value = checkInList.value.filter(
            (item) => item.gatewayId !== checkOutData.gatewayId,
        );
        checkOutList.value.push({
            gatewayId: checkOutData.gatewayId,
            exitTime: checkOutData.exitTime,
            exitInspection: checkOutData.exitInspection,
            releaseDocuments: checkOutData.releaseDocuments,
            modified: checkOutData.modified,
        });

        closeCheckOutDialog();
    } catch (error) {
        console.error('Erro ao realizar Check-Out:', error);
        $q.notify({ type: 'negative', message: 'Erro ao realizar Check-Out.' });
    } finally {
        isSavingCheckOut.value = false;
    }
}

// Confirmar Scheduling
async function handleConfirmScheduling() {
    console.log('handleConfirmScheduling chamado');
    console.log('Dados atuais do schedulingData:', schedulingData);
    isSavingScheduling.value = true;
    try {
        const payload = {
            orderTransporterId: schedulingData.orderTransporterId,
            yardControllerId: schedulingData.yardControllerId,
            startStorloc: schedulingData.startStorloc,
            destinator: schedulingData.destinator,
            licensePlate: schedulingData.licensePlate,
            state: schedulingData.state,
            category: schedulingData.category,
            begin: schedulingData.begin,
            end: schedulingData.end,
            time: schedulingData.time,
            created: new Date().toISOString(),
            modified: new Date().toISOString(),
        };

        if (schedulingData.eventSchedulingId) {
            console.log(
                'Atualizando evento agendado existente. ID:',
                schedulingData.eventSchedulingId,
            );
            await axiosInstance.put(`/rest/eventScheduling/${schedulingData.eventSchedulingId}`, payload);
            $q.notify({ type: 'positive', message: 'Evento Agendado atualizado com sucesso!' });
        } else {
            console.log('Criando novo evento agendado.');
            await axiosInstance.post('/rest/eventScheduling', payload);
            $q.notify({ type: 'positive', message: 'Evento Agendado criado com sucesso!' });
        }

        fetchScheduledEvents();
        closeSchedulingDialog();
    } catch (error) {
        console.error('Erro ao salvar Evento Agendado:', error);
        $q.notify({ type: 'negative', message: 'Erro ao salvar Evento Agendado.' });
    } finally {
        isSavingScheduling.value = false;
    }
}

// Editar Check-In, Check-Out e Evento
function editCheckIn(row) {
    console.log('editCheckIn chamado com row:', row);
    openCheckInDialog(row);
}

function editCheckOut(row) {
    console.log('editCheckOut chamado com row:', row);
    openCheckOutDialog(row);
}

function editScheduledEvent(row) {
    console.log('editScheduledEvent chamado com row:', row);
    openSchedulingDialog(row);
}

// Trocar a visualização
function setManagementView(view) {
    console.log('Mudando visualização para:', view);
    showManagementView.value = view;
    if (view === 'checkin') {
        fetchCheckIns();
    } else if (view === 'checkout') {
        fetchCheckOuts();
    } else if (view === 'eventos') {
        fetchScheduledEvents();
    }
}

// Funções para Upload da Imagem após duplo clique na imagem na tabela
function handleEditImage({ type, row }) {
    console.log('handleEditImage chamado. Tipo:', type, 'Row:', row);
    currentImageType.value = type; // 'vehicle' ou 'driver'
    currentGatewayId.value = row.gatewayId;
    newImageFile.value = null;
    console.log(
        'Abrindo diálogo de upload de imagem. currentGatewayId:',
        currentGatewayId.value,
        'currentImageType:',
        currentImageType.value,
    );
    showImageUploadDialog.value = true;
}

function closeImageUploadDialog() {
    console.log('Fechando diálogo de upload de imagem.');
    showImageUploadDialog.value = false;
    currentGatewayId.value = null;
    currentImageType.value = null;
    newImageFile.value = null;
}

// Validações
const isCheckInValid = computed(() => (
    checkInData.driverName
    && checkInData.driverFederalTax
    && checkInData.licensePlate
    && checkInData.boardingType
    && checkInData.nfE
    && checkInData.mobilePhone
    && checkInData.location
    && checkInData.entryTime
    && checkInData.boardingDocumentation
    && checkInData.boardingCondition
    && checkInData.storlocEanLocation
));

const isCheckOutValid = computed(() => (
    checkOutData.exitInspection
));

const isSchedulingValid = computed(() => (
    schedulingData.orderTransporterId
    && schedulingData.startStorloc
    && schedulingData.destinator
    && schedulingData.scheduling
));

// Inicialização
onMounted(() => {
    console.log('YardHome montado. Buscando dados iniciais...');
    fetchCheckIns();
    fetchCheckOuts();
    fetchScheduledEvents();
});
</script>

  <style scoped>
  .q-toolbar {
    display: flex;
    align-items: center;
  }

  .q-toolbar-title {
    flex-grow: 1;
  }

  .q-btn {
    margin-left: 10px;
  }

  .q-my-md {
    margin-top: 16px;
    margin-bottom: 16px;
  }

  .text-h6 {
    font-weight: bold;
  }

  .map-manager {
    display: flex;
    flex-direction: column;
  }

  .button-margin {
    min-width: 40px;
    margin-left: 8px;
  }
  </style>
