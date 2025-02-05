<template>
  <q-card flat bordered class="q-ma-md">
    <div class="col-3 q-mr-md">
      <q-input
        v-model="upBilling"
        label="Preço por Volume"
        outlined
        class="q-mb-md"
      />
    </div>
    <div class="col-3">
      <q-input v-model="client" outlined class="q-mb-md" />
    </div>
  </q-card>

  <q-card flat bordered class="q-ma-md">
    <div class="col-3 q-mr-md">
      <q-input
        v-model="upBilling"
        label="Preço por Volume"
        outlined
        class="q-mb-md"
      />
    </div>
    <div class="col-3">
      <q-input v-model="client" label="Client" outlined class="q-mb-md" />
    </div>
  </q-card>

  <q-page class="flex flex-center">
    <img
      alt="Quasar logo"
      src="~assets/img/image.svg"
      style="width: 200px; height: 200px"
    />
    <q-card flat bordered class="q-ma-md">
      <div class="col-3 q-mr-md">
        <q-input
          v-model="upBilling"
          label="Preço por Volume"
          outlined
          class="q-mb-md"
        />
      </div>
      <div class="col-3">
        <q-input v-model="client" label="Client" outlined class="q-mb-md" />
      </div>
    </q-card>
    <q-card flat bordered class="q-ma-md">
      <teste02 />
    </q-card>
  </q-page>
</template>

<script setup>
import { computed } from "vue";
import { useFederalTaxStore } from "../stores/federalTax";
import teste02 from "../components/test/TesteTwo.vue";
import { useBillingStore } from "../stores/billing";

defineOptions({
  name: "IndexPage",
});

const store = useFederalTaxStore();
const storeBilling = useBillingStore();

const computedOi = computed({
  get: () => store.oi,
  set: (value) => store.updateOi(value),
});
const upValueUnitloads = computed({
  get: () => storeBilling.valueByUnitloads,
  set: (value) => storeBilling.setValueByUnitloads(value),
});
const upBilling = computed({
  get: () => storeBilling.billing,
  set: (value) => storeBilling.setBilling(value),
});

const client = computed({
  get: () => storeBilling.client,
  set: (value) => storeBilling.setClient(value),
});
</script>
