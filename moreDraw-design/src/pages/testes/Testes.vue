<template>
    imagem:

    <q-card>
        <div>
            {{ allUnitloads }} * {{ value }} => {{ allUnitloads * value }} $
        </div>
        <div>
            <number-test
                :name="nomeDoPai"
                :idade="idadeDoPai"
            >
                <template #header>
                    <h1>{{ nomeDoPai }}</h1> <!-- Cabeçalho customizado -->
                    <h1>{{ idadeDoPai }}</h1>
                </template>

                <template #default>
                    <p>Conteúdo principal do pai.</p> <!-- Conteúdo do slot padrão -->
                </template>

                <template #footer>
                    <p>Rodapé customizado do Pai</p> <!-- Rodapé customizado -->
                </template>
            </number-test>
        </div>
        <q-card-section>
            <section>
                <!-- Linha para agrupar os inputs -->
                <div class="row q-gutter-md">
                    <q-input
                        v-model="name"
                        label="Name"
                        :loading="loading"
                        class="col"
                    />
                    <q-input
                        v-model="allUnitloads"
                        label="Unitloads"
                        :loading="loading"
                        class="col"
                    />
                    <q-input
                        v-model="value"
                        label="Valor"
                        :loading="loading"
                        class="col"
                    />
                    <q-input
                        v-model="dateNew"
                        label="Date"
                        :loading="loading"
                        type="date"
                        class="col"
                        style="color: orange;"
                    />
                </div>
            </section>
            <div>
                <div>Pinia: {{ store.getTest }}</div>
            </div>
            <q-btn
                :label="$t('Cadastrar')"
                color="primary"
                class="q-mt-md"
                :loading="loading"
                :disable="loading || !cpf"
                @click="cadastrar"
            />
        </q-card-section>

        <q-card-section>
            <div class="row q-gutter-md">
                <div
                    v-for="(item, index) in data"
                    :key="index"
                    style="max-width: 150px; cursor: pointer;"
                    class="col-12 col-md-6 col-lg-3"
                    @click="toggleDetails(index)"
                >
                    <q-card>
                        <q-card-section>
                            <div>Name: {{ item.name }}</div>
                            <div>Fatura: {{ item.allUnitloads * item.value }} $</div>
                            <div v-if="item.showDetails">
                                <div>Volumes: {{ item.allUnitloads }}</div>
                                <div>Valor: {{ item.value }}</div>
                                <div>Date: {{ item.dateNew }}</div>
                            </div>
                        </q-card-section>
                    </q-card>
                </div>
            </div>
        </q-card-section>
    </q-card>

    <q-card>
        <q-card-select>
            <q-option-group
                v-model="tipoPesquisa"
                inline
                :options="localizedSearchOptions"
                option-type="radio"
                size="sm"
                class="q-mb-md"
            />
        </q-card-select>

        <div v-if="tipoPesquisa === 'name'">
            Name: {{ nameView }}
        </div>
        <div v-if="tipoPesquisa === 'cnpj'">
            CNPJ: {{ cnpjView }}
        </div>
        <div v-if="tipoPesquisa === 'mobilePhone'">
            Phone: {{ numberPhoneView }}
        </div>
        <div v-if="tipoPesquisa === 'unitloads'">
            Unitloads: {{ allUnitloads }}
        </div>
    </q-card>
    <q-space />
    <q-card>
        <teste />
    </q-card>
    <q-space />
    <q-space />
    <spk-card>
        <q-card-section>
            <div class="row q-gutter-md">
                <q-input
                    v-model="clientId"
                    label="ClientId"
                    :loading="loading"
                    style="max-width: 150px;"
                    class="col"
                />
                <q-option-group
                    v-model="tipoPesquisaClient"
                    inline
                    :options="searchOptionsClient"
                    option-type="radio"
                    size="sm"
                    :class="['q-mb-md', 'col']"
                />
            </div>
        </q-card-section>
        <test02 />
        spk
        <chart-test />
        <template>
            <div>
                <canvas ref="lineChart" />
            </div>
        </template>
    </spk-card>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue';
import { Chart, registerables } from 'chart.js';
import teste from '../../components/testes';
import ChartTest from '../Chart.vue';
import test02 from './test02.vue';
import NumberTest from './NumberTest.vue';
import { useStore } from '../../stores/store';

const nomeDoPai = ref('João');
const idadeDoPai = ref('30');

const allUnitloads = ref(0);
const loading = ref(false);
const cpf = ref('2');
const name = ref('default');
const value = ref(0.2);
const dateNew = ref('');
const data = ref([]);
const tipoPesquisa = ref();
const tipoPesquisaClient = ref();
const store = useStore();
const storeTest = ref(store.getTest);

const localizedSearchOptions = ref([
    { label: 'Search by Name', value: 'name' },
    { label: 'Search by Federal Tax', value: 'cnpj' },
    { label: 'Search by Phone', value: 'mobilePhone' },
    { label: 'Search by Unitloads', value: 'unitloads' },
]);

const searchOptionsClient = ref([
    { label: 'Search by Name', value: 'name' },
    { label: 'Search by Federal Tax', value: 'cnpj' },
    { label: 'Search by Phone', value: 'mobilePhone' },
    { label: 'Search by transporter', value: 'transporter' },
    { label: 'Search by shipper', value: 'shipper ' },
    { label: 'Search by destinator', value: 'destinator' },
]);

const nameView = computed(() => name.value);
const cnpjView = ref('');
const numberPhoneView = ref('');
const numberPhone = ref('996228545');
const unitloadView = ref(0);
const clientId = ref('Id do client');

Chart.register(...registerables);

const props = defineProps(['chart-data']);
const lineChart = ref(null);

// Observa as mudanças em tipoPesquisa e chama as funções correspondentes
watch(tipoPesquisa, (newValue) => {
    if (newValue === 'name') {
        updateName();
    } else if (newValue === 'cnpj') {
        updateCNPJ();
    } else if (newValue === 'mobilePhone') {
        updateMobilePhone();
    } else if (newValue === 'unitloads') {
        unitloads();
    }
});

function cadastrar() {
    data.value.push({
        name: name.value,
        allUnitloads: allUnitloads.value,
        value: value.value,
        dateNew: dateNew.value,
        showDetails: false,
    });

    // Limpa os campos após cadastrar
    name.value = '';
    allUnitloads.value = 0;
    value.value = 0;
    dateNew.value = '';
}

function toggleDetails(index) {
    data.value[index].showDetails = !data.value[index].showDetails;
}

function updateCNPJ() {
    cnpjView.value = cpf.value;
}

function updateName() {
    nameView.value = name.value;
}

function updateMobilePhone() {
    numberPhoneView.value = numberPhone.value;
}
function unitloads() {
    unitloadView.value = allUnitloads.value;
}

onMounted(() => {
    new Chart(lineChart.value, {
        type: 'line',
        data: props.chartData,
    });
});
</script>
