import { route } from 'quasar/wrappers';
import { createRouter, createMemoryHistory, createWebHistory, createWebHashHistory } from 'vue-router';
import { getAuthStatus } from 'src/stores/auth';
import routes from './routes';

export default route((/* { store, ssrContext } */) => {
    const createHistory = process.env.SERVER
        ? createMemoryHistory
        : (process.env.VUE_ROUTER_MODE === 'history' ? createWebHistory : createWebHashHistory);

    const Router = createRouter({
        scrollBehavior: () => ({
            left: 0,
            top: 0,
        }),
        routes,
        history: createHistory(process.env.VUE_ROUTER_BASE),
    });

    Router.beforeEach((to, from, next) => {
        const isAuthenticated = getAuthStatus();

        if (!isAuthenticated && to.name !== 'login') {
            // Se não estiver autenticado e tentando acessar uma página diferente de login
            next({ name: 'login' });
        } else if (isAuthenticated && to.name === 'login') {
            // Se estiver autenticado e tentando acessar a página de login
            next({ name: 'home' });
        } else {
            // Permite a navegação
            next();
        }
    });

    return Router;
});
