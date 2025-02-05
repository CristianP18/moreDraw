import { defineStore } from 'pinia';

export const useBillingStore = defineStore('billing', {
    state: () => ({
        billing: 0.08,
        valueByUnitloads: 0,
        client: '',
    }),
    actions: {

        setBilling(data) {
            this.billing = data;
        },
        setValueByUnitloads(data) {
            this.valueByUnitloads = data;
        },
        setClient(data) {
            this.client = data;
        },
    },
});
