<template>
    <div class="q-pa-md">
        <spk-card
            flat
            bordered
            class="q-ma-md"
        >
            <spk-card-section>
                <div class="text-h6">
                    {{ $t('clientReport.title') }}
                </div>
            </spk-card-section>

            <spk-card-section>
                <q-option-group
                    v-model="tipoPesquisa"
                    inline
                    :options="localizedSearchOptions"
                    option-type="radio"
                    size="sm"
                    class="q-mb-md"
                />
                <search-by-name
                    v-if="tipoPesquisa === 'name'"
                    @search-result="updateClientData"
                />
                <search-by-federaltax
                    v-if="tipoPesquisa === 'cnpj'"
                    @search-result="updateClientData"
                />
                <search-by-phone
                    v-if="tipoPesquisa === 'mobilePhone'"
                    @search-result="updateClientData"
                />
            </spk-card-section>
        </spk-card>
        <spk-card
            flat
            bordered
            class="q-ma-md q-mt-md"
        >
            <spk-card-section>
                <component :is="showClientSearchOptions ? 'UserQuery' : 'ClientSearchOptions'" />
            </spk-card-section>
        </spk-card>
    </div>
</template>

<script>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useStore } from '../stores/store';
import SearchByName from '../components/users/SearchByName.vue';
import SearchByFederaltax from '../components/users/SearchByCPF.vue';
import SearchByPhone from '../components/users/SearchByPhone.vue';
import UserQuery from '../components/users/UserQuery.vue';
import ClientSearchOptions from '../components/clients/ClientSearchOptions.vue';

export default {
    components: {
        SearchByName,
        SearchByFederaltax,
        SearchByPhone,
        ClientSearchOptions,
        UserQuery,
    },
    setup() {
        const { t } = useI18n();
        const store = useStore();
        const tipoPesquisa = ref('cnpj');
        const showClientSearchOptions = ref(true);

        const localizedSearchOptions = [
            { label: t('clientReport.cpfLabel'), value: 'cnpj' },
            { label: t('clientReport.clientNameLabel'), value: 'name' },
            { label: t('clientReport.mobilePhoneLabel'), value: 'mobilePhone' },
        ];

        function updateClientData(client) {
            store.setFederalTax(client.federalTax);
        }

        function evaluateSearchComponent() {
            showClientSearchOptions.value = store.federalTax.length > 0;
        }

        return {
            tipoPesquisa,
            localizedSearchOptions,
            showClientSearchOptions,
            federalTax: store.federalTax,
            updateClientData,
            evaluateSearchComponent,
        };
    },
};
</script>
