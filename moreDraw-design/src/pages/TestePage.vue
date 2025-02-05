<template>
  <div>
    <q-input
      v-model="computedOi"
      :label="translation[locale].updateMessage"
      outlined
      class="q-mb-md"
    />

    <div class="row">
      <div class="col-3 q-mr-md">
        <!-- Margem à direita -->
        <q-input
          v-model="upValueUnitloads"
          :label="translation[locale].totalVolumes"
          outlined
          class="q-mb-md"
        />
      </div>
      <div class="col-3 q-mr-md">
        <q-input
          v-model="upBilling"
          :label="translation[locale].pricePerVolume"
          outlined
          class="q-mb-md"
        />
      </div>
      <div class="col-3">
        <q-input
          v-model="client"
          :label="translation[locale].and"
          outlined
          class="q-mb-md"
        />
      </div>
    </div>

    {{ translation[locale].allUnitloads }}
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useFederalTaxStore } from "../stores/federalTax";
import { useBillingStore } from "../stores/billing";
import { language } from "../language/language";
import { getCookie } from "../utils/getCookie";

const store = useFederalTaxStore();
const storeBilling = useBillingStore();

// Obter o locale do cookie ou usar um padrão
const locale = ref(getCookie("locale") || "pt-BR");

// Traduções acessíveis
const translation = language;

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

function updateOi() {}
</script>
