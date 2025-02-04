<template>
    <div>
        <header>
            <slot name="header" /> <!-- Slot nomeado -->
        </header>
        <main>
            <slot>Conteúdo Padrão</slot> <!-- Slot padrão -->
        </main>
        <footer>
            <slot name="footer">
                Rodapé Padrão
            </slot> <!-- Outro slot nomeado -->
        </footer>
    </div>
    <div>
        <q-input
            v-model="peso"
            label="Peso (kg)"
        />
        <q-input
            v-model="altura"
            label="Altura (cm)"
        />
        <q-radio
            v-model="sexo"
            val="M"
            label="Masculino"
        />
        <q-radio
            v-model="sexo"
            val="F"
            label="Feminino"
        />
        Massa Magra: {{ massaMagra.toFixed(2) }} kg
        <q-space />
        Massa Gorda: {{ massaGorda.toFixed(2) }} kg
        <q-btn
            label="lambda"
            color="primary"
            @click="lambda"
        />
        <div>{{ arrayResponse.join(', ') }}</div>
        <div>{{ array }}</div>
        <q-btn
            label="Resposta"
            color="primary"
            @click="toggleResponse"
        />
        <div v-if="resp">
            <div
                v-for="(item, index) in arrayResponse"
                :key="index"
            >
                <div>{{ item }}</div>
            </div>
        </div>
        <q-btn
            label="Filtro"
            color="primary"
            @click="applyFilter"
        />
        <div v-if="filter">
            <div>
                {{ filteredArray.join(', ') }}
            </div>
        </div>
        <div />
    </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useStore } from '../../stores/store';

const store = useStore();

const peso = ref(0);
const altura = ref(0);
const sexo = ref('M');
const array = ref([1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14.0, 15, 16, 17, 18, 19, 20]);
const arrayResponse = ref([]);
const resp = ref(false);
const filter = ref(false);
const filteredArray = ref([]);

const props = defineProps({
    nome: String,
    idade: String,
});

onMounted(() => {
    store.setTest('kjsnkjsnkjncjkdncjdksnckjsd');
});

function applyFilter() {
    filteredArray.value = array.value.filter((n) => n % 2 === 0);

    toggleFilter();
}

function toggleFilter() {
    filter.value = !filter.value;
}

function lambda() {
    arrayResponse.value = array.value.map((n) => n + (n * 200000000));
    console.log(arrayResponse.value);
}

function toggleResponse() {
    resp.value = !resp.value;
}

const massaMagra = computed(() => {
    if (sexo.value === 'M') {
        return (0.32810 * peso.value) + (0.33929 * altura.value) - 29.5336;
    }
    return (0.29569 * peso.value) + (0.41813 * altura.value) - 43.2933;
});

const massaGorda = computed(() => peso.value - massaMagra.value);
</script>
