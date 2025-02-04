<template>
    <q-card class="geometry-selector">
        <q-card-section>
            <!-- <div class="text-h6">
                {{ $t('design.selectGeometry') }}
            </div> -->
        </q-card-section>
        <q-card-section class="q-pt-none">
            <div
                class="q-gutter-sm"
                style="display: flex; flex-wrap: wrap; justify-content: center;"
            >
                <q-btn
                    v-for="(geometry, index) in geometries"
                    :key="index"
                    flat
                    :icon="geometry.icon"
                    :label="$t(geometry.label)"
                    class="geometry-button"
                    @click="selectGeometry(geometry.type)"
                />
            </div>
        </q-card-section>
    </q-card>
</template>
<script>
export default {
    name: 'GeometrySelector',
    data() {
        return {
            geometries: [
                { icon: 'crop_square', label: 'design.addContainer', type: 'container' },
                { icon: 'title', label: 'design.addLabel', type: 'label' },
                { icon: 'radio_button_unchecked', label: 'design.addCircle', type: 'circle' },
                { icon: 'change_history', label: 'design.addTriangle', type: 'triangle' },
                { icon: 'star', label: 'design.addStar', type: 'star' },
                { icon: 'hexagon', label: 'design.addHexagon', type: 'hexagon' },
                { icon: 'pentagon', label: 'design.addPentagon', type: 'pentagon' },
                { icon: 'octagon', label: 'design.addOctagon', type: 'octagon' },
                { icon: 'chevron_left', label: 'design.addChevronLeft', type: 'chevronLeft' },
                { icon: 'chevron_right', label: 'design.addChevronRight', type: 'chevronRight' },
                { icon: 'chevron_up', label: 'design.addChevronUp', type: 'chevronUp' },
                { icon: 'chevron_down', label: 'design.addChevronDown', type: 'chevronDown' },
                { icon: 'star_half', label: 'design.addHalfStar', type: 'halfStar' },
                { icon: 'diamond', label: 'design.addDiamond', type: 'diamond' },
                // Adicionando a Elipse
                { icon: 'adjust', label: 'design.addEllipse', type: 'ellipse' },
            ],
        };
    },
    methods: {
        selectGeometry(type) {
            this.$emit('addItem', type);
            this.$emit('close');
        },
    },
};
</script>
<style scoped>
.geometry-selector {
  width: 100%;
  max-width: 600px;
  margin: auto;
}

.geometry-button {
  min-width: 80px;
  text-align: center;
  justify-content: center;
}
</style>
