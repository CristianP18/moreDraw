<template>
    <q-page class="q-pa-md">
        <spk-card>
            <spk-card-section>
                <div class="text-h6">
                    {{ $t('about.title') }}
                </div>
                <p>{{ $t('about.description') }}</p>
            </spk-card-section>

            <spk-card-section>
                <div class="text-subtitle1">
                    {{ $t('about.features') }}
                </div>
                <ul>
                    <li
                        v-for="feature in $t('about.featureList').split(',')"
                        :key="feature"
                    >
                        {{ feature.trim() }}
                    </li>
                </ul>
            </spk-card-section>

            <spk-card-section>
                <div class="text-subtitle1">
                    {{ $t('about.architecture') }}
                </div>
                <p>{{ $t('about.architectureDescription') }}</p>
                <ul>
                    <li
                        v-for="component in $t('about.mainComponents').split('.') "
                        :key="component"
                    >
                        {{ component.trim() }}
                    </li>
                </ul>
            </spk-card-section>

            <spk-card-section>
                <div class="text-subtitle1">
                    {{ $t('about.technologiesUsed') }}
                </div>
                <ul>
                    <li
                        v-for="tech in $t('about.technologyList').split(',') "
                        :key="tech"
                    >
                        {{ tech.trim() }}
                    </li>
                </ul>
            </spk-card-section>

            <spk-card-section>
                <div class="text-subtitle1">
                    {{ $t('about.installation') }}
                </div>
                <p>{{ $t('about.installationInstructions') }}</p>
                <ol>
                    <li
                        v-for="step in $t('about.installationSteps').split('.') "
                        :key="step"
                    >
                        {{ step.trim() }}
                    </li>
                </ol>
            </spk-card-section>

            <spk-card-section>
                <div class="text-subtitle1">
                    {{ $t('about.usage') }}
                </div>
                <p>{{ $t('about.usageInstructions') }}</p>
                <ul>
                    <li
                        v-for="usage in $t('about.usageDescription').split(',') "
                        :key="usage"
                    >
                        {{ usage.trim() }}
                    </li>
                </ul>
            </spk-card-section>
        </spk-card>
    </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const fetchData = async () => {
    // Código para buscar dados iniciais a ser analisado essa possibilidade
};

onMounted(() => {
    fetchData();
});
</script>

<style>
.q-pa-md {
  padding: 16px;
}
.text-subtitle1 {
  font-size: 1.25rem;
  margin-bottom: 8px;
}
</style>
