<template>
    <div class="row">
        <div class="col-3 q-mr-md">
            <q-input
                v-model="upValueUnitloads"
                label="Total de Volumes"
                outlined
                class="q-mb-md"
            />
        </div>
        <div class="col-3">
            <q-input
                v-model="upBilling"
                label="Preço por Volume"
                outlined
                class="q-mb-md"
            />
        </div>
    </div>
    Billing: {{ billing }}
    Cliente: {{ store.client }}
</template>

<script setup>
import { computed } from 'vue';
import { useBillingStore } from '../../stores/billing';

const store = useBillingStore();

const billing = computed(() => store.billing);

const upBilling = computed({
    get: () => store.billing,
    set: (value) => store.setBilling(value),
});

const upValueUnitloads = computed({
    get: () => store.valueByUnitloads,
    set: (value) => store.setValueByUnitloads(value),
});
</script>
