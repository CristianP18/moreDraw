<template>
    <q-layout :view="!$q.screen.lt.md ? 'lHh LpR fFf' : 'hHh lpR lFf'">
        <main-toolbar
            :config-btn="configBtn"
            :show-user="showUser"
            :user-info="userInfo"
            :mini-drawer="miniDrawer"
            @change-language="changeLanguage"
            @open-config="systemConfig"
            @menu-action="menuAction"
            @logout="logout"
            @reload="componentKey++"
        >
            <template #logotipo>
                <slot name="logotipo" />
            </template>
            <template #toolbar-slot>
                <slot name="toolbar-slot" />
            </template>
            <template #breadcrumbs>
                <slot name="breadcrumbs" />
            </template>
        </main-toolbar>

        <main-drawer
            :routes="routes"
            :show-filter="showFilter"
            :drawer="drawer"
            :tenant="tenant"
            :hide-tenant="hideTenant"
            @close="drawer = false"
        >
            <template #logotipo>
                <slot name="logotipo" />
            </template>
        </main-drawer>

        <q-page-container>
            <q-page :class="mode ? 'color-dark' : 'color-light'">
                <div
                    class="router-config fit q-pa-sm"
                    :class="mode ? 'color-dark' : 'color-light'"
                >
                    <router-view :key="componentKey" />
                </div>
            </q-page>
        </q-page-container>
    </q-layout>
</template>

<script>
import { useQuasar, QPageContainer, QLayout, QPage } from 'quasar';
import MainToolbar from './MainToolbar.vue';
import MainDrawer from './MainDrawer.vue';
import routes from '../router/routes';

export default {
    name: 'MainLayout',
    components: {
        MainDrawer,
        MainToolbar,
        QPageContainer,
        QLayout,
        QPage,
    },
    props: {
        hideTenant: {
            type: Boolean,
            default: false,
        },
        showFilter: {
            type: Boolean,
            default: false,
        },
        configBtn: {
            type: Boolean,
            default: false,
        },
        showUser: {
            type: Boolean,
            default: false,
        },
        userInfo: {
            type: Object,
            default: () => ({}),
        },
        miniDrawer: {
            type: Boolean,
            default: false,
        },
        tenant: {
            type: Object,
            required: true,
        },
    },
    emits: ['systemConfig', 'userConfig', 'logout', 'changeLanguage'],
    data() {
        return {
            drawer: true,
            componentKey: 0,
            routes,
        };
    },
    computed: {
        mode() {
            return this.$q.dark.isActive;
        },
    },
    methods: {
        systemConfig() {
            this.$emit('systemConfig');
        },
        menuAction() {
            this.drawer = !this.drawer;
        },
        logout() {
            this.$emit('logout');
        },
        changeLanguage() {
            this.$emit('changeLanguage');
        },
    },
};
</script>
