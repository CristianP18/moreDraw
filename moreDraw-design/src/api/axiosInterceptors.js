import { Notify } from 'quasar';
import { getCookie } from 'src/api/getCookie';
import { useAuthStore } from 'stores/auth-store';
import { router } from 'src/router/index.js';
import { jwtDecode } from 'jwt-decode';
import axios from 'axios';
import { i18n } from 'boot/i18n';

export function setupInterceptors(instance) {
    instance.interceptors.request.use(async (config) => {
        const requestConfig = config;
        const token = requestConfig.headers.Authorization?.replace('Bearer ', '');
        const refreshToken = getCookie('refresh-token');

        if (token === 'undefined' || jwtDecode(token).exp < Date.now() / 1000) {
            if (!refreshToken || jwtDecode(refreshToken).exp < Date.now() / 1000) {
                router.push({ name: 'login' });
                return Promise.reject(new axios.Cancel(i18n.global.t('text.sessionExpiredMessage')));
            }
            try {
                const authStore = useAuthStore();
                const res = await authStore.login({
                    federalTax: localStorage.getItem('taxId'),
                    body: { refreshToken },
                });
                requestConfig.headers.Authorization = `Bearer ${res.token}`;
            } catch (error) {
                return Promise.reject(new axios.Cancel(i18n.global.t('drawer.tokenErrorMessage')));
            }
        }
        return requestConfig;
    }, (error) => Promise.reject(error));

    instance.interceptors.response.use(
        (response) => response,
        (error) => {
            if (error.response) {
                Notify.create({
                    message: error.response.data.message,
                    color: 'warning',
                    icon: 'mdi-alert-circle-outline',
                });
            } else {
                Notify.create({
                    message: error.message ?? i18n.global.t('drawer.connectionProblem'),
                    color: 'warning',
                    icon: 'mdi-alert-circle-outline',
                });
            }
            return Promise.reject(error);
        },
    );
}
