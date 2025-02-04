// src/stores/store.js
import { defineStore } from 'pinia';

export const useStore = defineStore('main', {
    state: () => ({
        apiKey: localStorage.getItem('apiKey') || null,
        jwt: localStorage.getItem('jwt') || null,
        federalTax: localStorage.getItem('federalTax') || null,
        test: localStorage.getItem('test') || null,
    }),
    actions: {
        setApiKey(key) {
            this.apiKey = key;
            localStorage.setItem('apiKey', key);
        },
        setJwt(jwt) {
            this.jwt = jwt;
            localStorage.setItem('jwt', jwt);
        },
        setFederalTax(federalTax) {
            this.federalTax = federalTax;
            localStorage.setItem('federalTax', federalTax);
        },
        setTest(test) {
            this.test = test;
            localStorage.setItem('test', test);
        },
    },
});
