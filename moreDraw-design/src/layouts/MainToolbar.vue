<template>
    <q-header
        bordered
        :class="mode ? 'card-dark' : 'card-light'"
    >
        <q-toolbar
            class="flex"
            style="height: 55px;"
        >
            <q-btn
                flat
                dense
                class="btn-drawer"
                icon="mdi-menu"
                @click="menuAction"
            />
            <q-separator
                vertical
                inset
            />
            <q-toolbar-title class="flex items-center">
                <slot
                    v-if="!$q.screen.lt.md"
                    name="logotipo"
                />
            </q-toolbar-title>

            <div class="flex items-center">
                <slot
                    name="toolbar-slot"
                />
                <q-btn
                    v-if="configBtn"
                    flat
                    @click="openConfig"
                >
                    <q-icon
                        name="mdi-cog-outline"
                        size="sm"
                        class="cursor-pointer"
                    />
                </q-btn>
                <div class="cursor-pointer">
                    <q-btn
                        flat
                        class="q-pa-sm"
                    >
                        <q-avatar
                            size="sm"
                            color="grey-6"
                        >
                            <img
                                v-if="userInfo && userInfo.picture"
                                :src="userInfo.picture"
                                alt="user-picture"
                            >
                            <div v-else>
                                {{ userInfo && userInfo.name ? userInfo.name.substring(0, 1) : '?' }}
                            </div>
                        </q-avatar>

                        <q-icon
                            name="mdi-chevron-down"
                            size="xs"
                            class="q-ml-xs"
                        />

                        <q-menu
                            flat
                            :offset="[5, 15]"
                            class="flex flex-center card-space"
                        >
                            <user-dialog-config
                                :user-info="userInfo"
                                :show-user="showUser"
                                @logout="logout"
                                @change-language="changeLanguage"
                            />
                        </q-menu>
                    </q-btn>
                </div>
            </div>
        </q-toolbar>
        <q-separator />
        <q-toolbar
            inset
            class="q-pa-none q-px-sm"
            :class="mode ? 'color-dark' : 'color-light'"
            style="min-height: 40px; height: 40px"
        >
            <div
                class="full-width q-pa-xs items-center row"
            >
                <q-btn
                    flat
                    dense
                    class="btn-drawer"
                    icon="mdi-arrow-left"
                    @click="routerBack"
                >
                    <q-tooltip>
                        {{ translation[locale].comeBack }}
                    </q-tooltip>
                </q-btn>
                <q-separator
                    vertical
                    inset
                    class="q-mr-sm"
                />
                <breadcrumbs />
                <div class="col row justify-end">
                    <slot
                        name="breadcrumbs"
                        class="col"
                    />
                    <div class="flex item-center">
                        <q-btn
                            show-icon
                            size="md"
                            class="q-pa-sm"
                            icon="mdi-reload"
                            outline
                            flat
                            @click="$emit('reload')"
                        >
                            <q-tooltip>
                                {{ translation[locale].reload }}
                            </q-tooltip>
                        </q-btn>
                    </div>
                </div>
            </div>
        </q-toolbar>
    </q-header>
</template>

<script>
import { QHeader, QToolbar, QToolbarTitle, QIcon, QMenu, QAvatar, QBtn, QSeparator, QTooltip } from 'quasar';
import UserDialogConfig from './UserDialogConfig.vue';
import Breadcrumbs from './Breadcrumbs.vue';
import { language } from '../language/language';
import { getCookie } from '../utils/getCookie';

export default {
    name: 'MainToolbar',
    components: {
        Breadcrumbs,
        UserDialogConfig,
        QSeparator,
        QHeader,
        QToolbar,
        QToolbarTitle,
        QIcon,
        QMenu,
        QAvatar,
        QBtn,
        QTooltip,
    },
    props: {
        configBtn: {
            type: Boolean,
            required: false,
            default: false,
        },
        showUser: {
            type: Boolean,
            required: false,
            default: false,
        },
        userInfo: {
            type: Object,
            required: false,
            default: () => ({ name: '', picture: '' }),
        },
    },
    emits: ['openConfig', 'menuAction', 'logout', 'changeLanguage', 'reload'],
    data() {
        return {
            translation: language,
            locale: getCookie('Language') || 'pt-BR',
        };
    },
    computed: {
        mode() {
            return this.$q.dark.isActive;
        },
    },
    methods: {
        openConfig() {
            this.$emit('openConfig');
        },
        menuAction() {
            this.$emit('menuAction');
        },
        logout() {
            this.$emit('logout');
        },
        changeLanguage() {
            this.$emit('changeLanguage');
        },
        routerBack() {
            this.$router.back();
        },
    },
};
</script>
