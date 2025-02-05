<template>
    <q-breadcrumbs
        :class="mode ? 'color-dark' : 'color-light'"
        class="q-ml-sm"
        active-color="none"
        gutter="xs"
    >
        <template #separator>
            <q-icon
                size="1.5em"
                name="chevron_right"
                :class="mode ? 'q-text-light' : 'q-text-dark'"
            />
        </template>
        <q-breadcrumbs-el
            v-if="$route.name === 'home'"
            :to="'/'"
        >
            <span class="text-body1 text-bold">
                Home
            </span>
        </q-breadcrumbs-el>
        <q-breadcrumbs-el
            v-for="(breadcrumb, idx) in $route.matched"
            :key="idx"
            :to="breadcrumb.components !== undefined ? breadcrumb.path : ''"
        >
            <span
                class="text-body1"
                :class="{ 'text-bold' : breadcrumb.name === $route.name}"
            >
                <span v-if="breadcrumb.name === 'layout'">Home</span>
                <span v-else-if="breadcrumb.name !== 'home'">{{ breadcrumb.meta.title }}</span>
            </span>
        </q-breadcrumbs-el>
    </q-breadcrumbs>
</template>

<script>
import { useQuasar, QBreadcrumbs, QBreadcrumbsEl, QIcon } from 'quasar';

export default {
    name: 'BreadcrumbNavigation',
    components: { QBreadcrumbs, QBreadcrumbsEl, QIcon },
    computed: {
        mode() {
            return this.$q.dark.isActive;
        },
    },
};
</script>
