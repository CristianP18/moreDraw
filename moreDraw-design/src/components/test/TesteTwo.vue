<template>
    <div>
        <!-- <h2>{{ computedOi }}</h2>

        <q-input
            v-model="newOi"
            label="Atualizar mensagem"
            outlined
            class="q-mb-md"
        />

        <q-btn
            label="Atualizar"
            color="primary"
            class="q-mt-md"
            @click="updateOi"
        /> -->

        <div>
            Preço por Volume:  {{ computedBilling }}
        </div>
        <div>
            Total de Unitloads: {{ allUnitloads }}
        </div>

        <div>
            Fatura: {{ computedInvoice }}
        </div>

        <teste3 />
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useFederalTaxStore } from '../../stores/federalTax';
import { useBillingStore } from '../../stores/billing';
import teste3 from './TestThree.vue';

const store = useFederalTaxStore();
const storeBilling = useBillingStore();

const newOi = ref('');

const computedOi = computed(() => store.oi);

const computedBilling = computed(() => storeBilling.billing);
const allUnitloads = computed(() => storeBilling.valueByUnitloads);

const computedInvoice = computed(() => {
    const billing = parseFloat(storeBilling.billing);
    const valueByUnitloads = parseFloat(storeBilling.valueByUnitloads);

    return (valueByUnitloads * billing).toFixed(2);
});

function updateOi() {
    if (newOi.value.trim() !== '') {
        store.updateOi(newOi.value);
        newOi.value = '';
    }
}
</script>
