<template>
    <spk-card
        v-if="client"
        class="q-mt-sm"
    >
        <spk-card-section>
            <div class="client-info-header">
                <img
                    :src="client.picUser || 'https://s3.amazonaws.com/docs.tracebox.com.br/picuser/user_gray.png'"
                    :alt="$t('client.pictureAlt')"
                    class="client-pic"
                >
                <div>
                    <h5>{{ client.fullName }}</h5>
                    <p v-if="client.federalTax">
                        <strong>{{ $t('client.taxId') }}:</strong> {{ client.federalTax }}
                    </p>
                    <p v-if="client.mobilePhone">
                        <strong>{{ $t('client.phone') }}:</strong> {{ client.mobilePhone }}
                    </p>
                    <p v-if="client.email">
                        <strong>{{ $t('client.email') }}:</strong> {{ client.email }}
                    </p>
                </div>
            </div>
        </spk-card-section>
        <spk-card-section v-if="client.address">
            <h6>{{ $t('client.addressTitle') }}</h6>
            <p v-if="client.address.street">
                <strong>{{ $t('client.street') }}:</strong> {{ client.address.street }}
            </p>
            <p v-if="client.address.addressNumber">
                <strong>{{ $t('client.number') }}:</strong> {{ client.address.addressNumber }}
            </p>
            <p v-if="client.address.neighborhood">
                <strong>{{ $t('client.neighborhood') }}:</strong> {{ client.address.neighborhood }}
            </p>
            <p v-if="client.address.city && client.address.stateUF">
                <strong>{{ $t('client.city') }}:</strong> {{ client.address.city }} - {{ client.address.stateUF }}
            </p>
            <p v-if="client.address.zip">
                <strong>{{ $t('client.zip') }}:</strong> {{ client.address.zip }}
            </p>
        </spk-card-section>
    </spk-card>
</template>

<script setup>
import { defineProps } from 'vue';

defineProps({
    client: {
        type: Object,
        required: true,
    },
});
</script>
