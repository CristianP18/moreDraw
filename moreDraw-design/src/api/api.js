import { createAxiosInstance } from './axiosInstance';
import { setupInterceptors } from './axiosInterceptors';
import { authOptions } from './apikey-token';

let instance = null;

export function getAxiosInstance() {
    const taxId = localStorage.getItem('taxId');
    if (!instance) {
        instance = createAxiosInstance(taxId);
        setupInterceptors(instance);
    } else if (instance.defaults.baseURL.split('/')[4] !== taxId) {
        instance = createAxiosInstance(taxId);
        setupInterceptors(instance);
    }
    return instance;
}

export const requests = {
    get: (url, config) => getAxiosInstance().get(url, authOptions()),
    post: (url, body) => getAxiosInstance().post(url, body, authOptions()),
    delete: (url) => getAxiosInstance().delete(url, authOptions()),
    put: (url, body) => getAxiosInstance().put(url, body, authOptions()),
    patch: (url, body) => getAxiosInstance().patch(url, body, authOptions()),
};
