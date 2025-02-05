// store/federalTax.js
import { defineStore } from 'pinia';

export const useFederalTaxStore = defineStore('federalTax', {
    state: () => ({
        federalTax: localStorage.getItem('federalTax') || '',
        clientData: null,
    }),
    actions: {
        setFederalTax(value) {
            this.federalTax = value;
            localStorage.setItem('federalTax', value);
        },
        setClientData(data) {
            this.clientData = data;
            localStorage.setItem('clientData', JSON.stringify(data));
        },
        loadClientData() {
            const data = localStorage.getItem('clientData');
            if (data) {
                this.clientData = JSON.parse(data);
            }
        },
    },
});
