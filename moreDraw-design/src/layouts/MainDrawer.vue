<template>
    <q-drawer
        v-model="drawerView"
        show-if-above
        side="left"
        :overlay="$q.screen.lt.md"
        :behavior="$q.screen.lt.md ? 'mobile' : 'desktop'"
        bordered
        :width="290"
        :breakpoint="500"
        :class="mode ? 'color-dark' : 'color-light'"
    >
        <div class="column fit">
            <div
                v-if="$q.screen.lt.md"
                class="flex items-center"
            >
                <slot name="logotipo" />
            </div>
            <div
                v-if="showFilter"
                class="q-pa-sm"
                style="height: 55px"
            >
                <q-input
                    ref="filterRef"
                    v-model="filter"
                    filled
                    dense
                    :label="translation[locale].search"
                    autofocus
                >
                    <template #prepend>
                        <q-icon name="search" />
                    </template>
                    <template #append>
                        <q-icon
                            v-if="filter !== ''"
                            name="clear"
                            class="cursor-pointer"
                            @click="resetFilter"
                        />
                    </template>
                </q-input>
            </div>
            <q-separator
                v-if="showFilter"
                class="q-mx-sm"
            />
            <q-scroll-area class="col q-pt-sm q-px-sm">
                <q-list>
                    <div
                        v-if="itemsData.length <= 0"
                        class="text-grey-6 text-caption"
                    >
                        {{ translation[locale].noResultLabel }}
                    </div>
                    <div
                        v-for="item in itemsData"
                        :key="item.name"
                    >
                        <ItemMenuDrawer
                            v-if="!item.children || item.children.length === 0"
                            :item="item"
                            icon
                        />
                        <q-expansion-item
                            v-else
                            :key="item.name"
                            clickable
                            dense
                            :default-opened="item.expand || filter.length > 0"
                        >
                            <template #header="{ expanded }">
                                <q-item-section :class="{ 'text-bold': expanded }">
                                    <div class="row items-center">
                                        <div v-if="item.icon">
                                            <q-icon
                                                size="xs"
                                                :name="item.icon"
                                            />
                                        </div>
                                        <div
                                            :class="{'q-ml-md': item.icon}"
                                            class="col"
                                        >
                                            <q-item-label style="word-break: break-all">
                                                {{ item.label }}
                                            </q-item-label>
                                        </div>
                                    </div>
                                </q-item-section>
                            </template>
                            <div
                                v-for="child in item.children"
                                :key="child.name"
                                class="q-ml-lg q-pl-xs"
                                style="border-left: 1px solid #767776;"
                            >
                                <ItemMenuDrawer
                                    v-if="!child.children || child.children.length === 0"
                                    :item="child"
                                    icon
                                />
                                <q-expansion-item
                                    v-else
                                    :key="child.name"
                                    clickable
                                    dense
                                    :default-opened="child.expand || filter.length > 0"
                                >
                                    <template #header="{ expanded }">
                                        <q-item-section :class="{ 'text-bold': expanded }">
                                            <div class="row items-center">
                                                <div v-if="child.icon">
                                                    <q-icon
                                                        size="xs"
                                                        :name="child.icon"
                                                    />
                                                </div>
                                                <div
                                                    :class="{'q-ml-md': child.icon}"
                                                    class="col"
                                                >
                                                    <q-item-label style="word-break: break-all">
                                                        {{ child.label }}
                                                    </q-item-label>
                                                </div>
                                            </div>
                                        </q-item-section>
                                    </template>
                                    <div
                                        v-for="c in child.children"
                                        :key="c.name"
                                        class="q-ml-lg q-pl-xs"
                                        style="border-left: 1px solid #767776;"
                                    >
                                        <ItemMenuDrawer
                                            :item="c"
                                            icon
                                        />
                                    </div>
                                </q-expansion-item>
                            </div>
                        </q-expansion-item>
                    </div>
                </q-list>
            </q-scroll-area>
            <div
                v-if="!hideTenant"
                class="q-pa-sm"
            >
                <q-card
                    class="flex items-center"
                    flat
                    bordered
                    style="padding: 0.1rem 0.3rem 0.1rem 0.3rem"
                >
                    <div>
                        <q-avatar
                            class="q-mr-sm"
                            color="grey"
                            rounded
                            size="md"
                        >
                            <q-icon
                                name="mdi-domain"
                                :color="mode ? 'grey-9' : 'grey-1'"
                            />
                        </q-avatar>
                    </div>
                    <div class="text-uppercase wrap">
                        <!-- Additional details could be placed here -->
                    </div>
                </q-card>
            </div>
        </div>
    </q-drawer>
</template>

<script>
import { QDrawer, QInput, QIcon, QSeparator, QScrollArea, QList, QExpansionItem, QItemLabel, QItemSection, QAvatar, useQuasar } from 'quasar';
import { language } from '../language/language';
import ItemMenuDrawer from './ItemMenuDrawer.vue';
import { getCookie } from '../utils/getCookie';
import routes from '../router/routes'; // Importando as rotas diretamente

export default {
    name: 'MainDrawer',
    components: {
        ItemMenuDrawer,
        QDrawer,
        QInput,
        QIcon,
        QSeparator,
        QScrollArea,
        QList,
        QExpansionItem,
        QItemSection,
        QAvatar,
        QItemLabel,
    },
    props: {
        hideTenant: {
            type: Boolean,
            required: true,
            default: false,
        },
        drawer: {
            type: Boolean,
            required: true,
            default: false,
        },
        showFilter: {
            type: Boolean,
            required: false,
            default: false,
        },
        tenant: {
            type: Object,
            required: true,
        },
    },
    emits: ['close'],
    data() {
        return {
            filter: '',
            filterRef: null,
            $q: useQuasar(),
            items: [], // Aqui, `items` será inicializado no mounted
            translation: language,
            languageSystem: 'pt-BR',
            locale: 'pt-BR',
        };
    },
    computed: {
        mode() {
            return this.$q.dark.isActive;
        },
        drawerView: {
            get() {
                return this.drawer;
            },
            set(value) {
                this.$emit('close');
            },
        },
        itemsData() {
            return this.filterByLabel(this.items, this.filter);
        },
    },
    mounted() {
        console.log('Routes received:', routes); // Verificando as rotas importadas
        this.languageSystem = getCookie('language');
        this.locale = this.languageSystem || 'pt-BR';

        if (Array.isArray(routes)) {
            this.items = this.transformData(routes);
        } else {
            console.error('Expected an array for `routes`, but received:', routes);
            this.items = []; // Defina `items` como um array vazio para evitar outros erros
        }
    },
    methods: {
        resetFilter() {
            this.filter = '';
            this.$refs.filterRef.focus();
        },
        transformData(data) {
            if (!Array.isArray(data)) {
                console.error("Expected an array for 'data', but received:", data);
                return [];
            }

            return data.map((item) => {
                // Verifica se a rota tem filhos, o que indica que pode ser um layout com sub-rotas
                if (item.children && item.children.length > 0) {
                    // Chama recursivamente transformData para processar as sub-rotas
                    const transformedChildren = this.transformData(item.children);

                    // Filtra os itens que possuem meta.showDrawer como true
                    const filteredChildren = transformedChildren.filter((child) => child !== null);

                    // Se não houver filhos filtrados, retorna null
                    if (filteredChildren.length === 0) {
                        return null;
                    }

                    return {
                        name: item.name || '', // Adicione o nome ou algum identificador
                        label: item.meta?.title || item.name || '', // Use o título da rota
                        icon: item.meta?.icon || '', // Ícone associado
                        children: filteredChildren, // Insere os filhos transformados
                    };
                }

                // Para rotas sem filhos, aplica a lógica de transformação diretamente
                if (item.meta?.showDrawer) {
                    return {
                        name: item.name || '',
                        label: item.meta?.title || item.name || '',
                        icon: item.meta?.icon || '',
                    };
                }

                return null; // Filtra qualquer rota sem `showDrawer`
            }).filter((item) => item !== null); // Filtra itens nulos
        },

        filterByLabel(data, filter) {
            const filterRecursive = (items) => items.reduce((acc, item) => {
                if (item.label.toLowerCase().includes(filter.toLowerCase())) {
                    if (item.children) {
                        const filteredChildren = filterRecursive(item.children);
                        if (filteredChildren.length > 0) {
                            acc.push({ ...item, children: filteredChildren });
                        } else {
                            acc.push(item);
                        }
                    } else {
                        acc.push(item);
                    }
                } else if (item.children) {
                    const filteredChildren = filterRecursive(item.children);
                    if (filteredChildren.length > 0) {
                        acc.push({ ...item, children: filteredChildren });
                    }
                }
                return acc;
            }, []);
            return filterRecursive(data);
        },
    },
};
</script>

  <style scoped>
  </style>
