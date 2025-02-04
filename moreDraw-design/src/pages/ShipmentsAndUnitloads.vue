<template>
    <spk-card
        show-title
        :title="$t('shipments.searchOptionsTitle')"
    >
        <div
            class="text-bold flex flex-center flex-column"
            style="height: 200px"
        >
            <div class="q-mb-md">
                <div
                    class="flex items-center justify-center"
                    style="padding-right: 100px;"
                >
                    <q-option-group
                        v-model="tipoPesquisa"
                        inline
                        :options="[
                            { label: $t('shipments.searchShipment'), value: 'remessa' },
                            { label: $t('unitloads.searchOptionsShipmentVolumes'), value: 'remessa_volumes' }
                        ]"
                        option-type="radio"
                        size="sm"
                    />
                    <q-icon
                        name="info"
                        class="cursor-pointer q-ml-sm"
                        @click="showInfo('tipoPesquisa')"
                    />
                </div>
            </div>

            <div
                class="q-mb-md"
                style="width: 100%; max-width: 600px;"
            >
                <div class="row q-col-gutter-md">
                    <div class="col">
                        <q-input
                            v-model="pesquisaRemessa"
                            filled
                            :placeholder="$t('shipments.searchShipment')"
                        >
                            <template #append>
                                <q-icon
                                    name="info"
                                    class="cursor-pointer"
                                    @click="showInfo('shipments.pesquisaRemessaInfo')"
                                />
                            </template>
                        </q-input>
                        <spk-btn
                            show-icon
                            type="primary"
                            :label="$t('shipments.searchShipment')"
                            icon="mdi-magnify"
                            class="q-mt-sm"
                            :loading="loading"
                            :disable="loading || !pesquisaRemessa"
                            @click="pesquisarRemessa"
                        >
                            <template #loading>
                                <q-spinner color="white" />
                            </template>
                        </spk-btn>
                    </div>

                    <div class="col">
                        <q-input
                            v-model="pesquisaUnitLoads"
                            filled
                            :placeholder="$t('unitloads.searchUnitload')"
                        >
                            <template #append>
                                <q-icon
                                    name="info"
                                    class="cursor-pointer"
                                    @click="showInfo('unitloads.pesquisaUnitLoadsInfo')"
                                />
                            </template>
                        </q-input>
                        <spk-btn
                            show-icon
                            type="primary"
                            :label="$t('unitloads.searchUnitload')"
                            icon="mdi-magnify"
                            class="q-mt-sm"
                            :loading="loading"
                            :disable="loading || !pesquisaUnitLoads"
                            @click="pesquisarUnitLoads"
                        >
                            <template #loading>
                                <q-spinner color="white" />
                            </template>
                        </spk-btn>
                    </div>
                </div>
            </div>
        </div>
    </spk-card>

    <spk-card
        show-title
        :title="$t('shipments.resultsTitle')"
    >
        <div
            v-if="loading"
            class="q-pa-md"
        >
            {{ $t('messages.loading') }}
        </div>
        <div
            v-else-if="erro"
            class="q-pa-md"
        >
            {{ $t('messages.error') }}: {{ erro }}
        </div>
        <div
            v-else-if="resultadosRemessa.length === 0 && resultadosUnitLoads.length === 0"
            class="q-pa-md"
        >
            {{ $t('clientReport.noResultsFound') }}
        </div>
        <div
            v-else
            class="q-pa-md"
        >
            <div
                v-if="resultadosRemessa.length > 0"
                class="q-mb-md"
            >
                <q-card>
                    <q-card-section>
                        <div class="text-h6">
                            {{ $t('shipments.shipmentDetails') }}
                        </div>
                        <q-separator />
                        <q-list>
                            <q-item
                                v-for="(value, key) in resultadosRemessa[0]"
                                :key="key"
                                class="item-break"
                            >
                                <q-item-section>{{ key }}</q-item-section>
                                <q-item-section class="item-value">
                                    {{ value }}
                                </q-item-section>
                            </q-item>
                        </q-list>
                    </q-card-section>
                </q-card>
            </div>

            <div
                v-if="resultadosUnitLoads.length > 0"
                class="q-mb-md"
            >
                <q-card
                    v-for="unitload in resultadosUnitLoads"
                    :key="unitload.unitloadId"
                >
                    <q-card-section>
                        <div class="text-h6">
                            {{ $t('unitloads.unitloadDetails') }}
                        </div>
                        <q-separator />
                        <q-list>
                            <q-item
                                v-for="(value, key) in unitload"
                                :key="key"
                                class="item-break"
                            >
                                <q-item-section>{{ key }}</q-item-section>
                                <q-item-section class="item-value">
                                    {{ value }}
                                </q-item-section>
                            </q-item>
                        </q-list>
                    </q-card-section>
                </q-card>
            </div>
        </div>
    </spk-card>

    <q-dialog v-model="infoDialog">
        <q-card>
            <q-card-section>
                <div class="text-h6">
                    {{ infoTitle }}
                </div>
                <q-btn
                    icon="close"
                    flat
                    round
                    dense
                    class="absolute-top-right"
                    @click="infoDialog = false"
                />
            </q-card-section>
            <q-card-section>
                <p>{{ infoText }}</p>
            </q-card-section>
        </q-card>
    </q-dialog>
</template>

<script>
import { Notify } from 'quasar';
import { createAxiosInstance } from '../api/axiosInstance';

const environment = 'local';

const axiosInstance = createAxiosInstance(environment);

export default {
    data() {
        return {
            pesquisaRemessa: '',
            pesquisaUnitLoads: '',
            resultadosRemessa: [],
            resultadosUnitLoads: [],
            loading: false,
            erro: null,
            tipoPesquisa: 'remessa',
            infoDialog: false,
            infoTitle: '',
            infoText: '',
        };
    },
    methods: {
        async pesquisarRemessa() {
            this.loading = true;
            this.erro = null;
            this.resultadosRemessa = [];
            this.resultadosUnitLoads = [];

            try {
                if (this.tipoPesquisa === 'remessa') {
                    const response = await axiosInstance.get(`dynamoDB/shipment/${this.pesquisaRemessa}`);
                    this.resultadosRemessa = [response.data];
                    Notify.create({
                        message: 'Remessa encontrada com sucesso.',
                        color: 'positive',
                        position: 'top',
                    });
                } else if (this.tipoPesquisa === 'remessa_volumes') {
                    const response = await axiosInstance.get(`dynamoDB/unitloadsByShipmentId/${this.pesquisaRemessa}`);
                    this.resultadosUnitLoads = response.data;
                    Notify.create({
                        message: 'Volumes da remessa encontrados com sucesso.',
                        color: 'positive',
                        position: 'top',
                    });
                }
                console.log('Resultado Remessa:', this.resultadosRemessa);
            } catch (error) {
                this.erro = this.$t('shipments.apiErrorShipment');
                Notify.create({
                    message: 'Erro ao buscar remessa.',
                    color: 'negative',
                    position: 'top',
                });
                console.error('Erro ao buscar remessa:', error);
            } finally {
                this.loading = false;
            }
        },
        async pesquisarUnitLoads() {
            this.loading = true;
            this.erro = null;
            this.resultadosRemessa = [];
            this.resultadosUnitLoads = [];

            try {
                const response = await axiosInstance.get(`dynamoDB/unitload/${this.pesquisaUnitLoads}`);
                this.resultadosUnitLoads = [response.data];
                Notify.create({
                    message: 'Unitloads encontrados com sucesso.',
                    color: 'positive',
                    position: 'top',
                });
                console.log('Resultado UnitLoads:', response.data);
            } catch (error) {
                this.erro = this.$t('unitloads.apiErrorUnitload');
                Notify.create({
                    message: 'Erro ao buscar unitload.',
                    color: 'negative',
                    position: 'top',
                });
                console.error('Erro ao buscar unitload:', error);
            } finally {
                this.loading = false;
            }
        },
        showInfo(option) {
            this.infoTitle = option;
            switch (option) {
                case 'tipoPesquisa':
                    this.infoText = this.$t('shipments.tipoPesquisaInfo');
                    break;
                case 'pesquisaRemessa':
                    this.infoText = this.$t('shipments.pesquisaRemessaInfo');
                    break;
                case 'pesquisaUnitLoads':
                    this.infoText = this.$t('unitloads.pesquisaUnitLoadsInfo');
                    break;
                default:
                    this.infoText = this.$t('info.default');
                    break;
            }
            this.infoDialog = true;
        },
    },
};
</script>
<style lang="css" scoped>
.item-break {
    margin-bottom: 10px;
    border-bottom: 1px solid #ccc;
    padding-bottom: 5px;
}

</style>
