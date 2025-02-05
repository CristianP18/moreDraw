DesignComponent.vue
<template>
    <q-page class="page q-px-lg">
        <design />
        <view3-d />
    </q-page>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch, computed } from 'vue';
import design from 'components/design/DesignComponent.vue';
import view3D from './View3D.vue';

</script>
