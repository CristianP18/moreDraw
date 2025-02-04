import { boot } from 'quasar/wrappers';
import { createI18n } from 'vue-i18n';
import messages from 'src/i18n';
import { getCookie } from 'src/utils/getCookie';

const i18n = createI18n({
    globalInjection: true,
    legacy: false,
    locale: (getCookie('SpkLanguage') || 'pt-BR'), // initial language
    messages,
});

export default boot(({ app }) => {
    // Set i18n instance on app
    app.use(i18n);
});

export { i18n };

// import { createI18n } from 'vue-i18n';

// const messages = {
//     en: {
//         welcomeMessage: 'Welcome',
//     },
//     fr: {
//         welcomeMessage: 'Bienvenue',
//     },
// };

// const i18n = createI18n({
//     locale: 'en',
//     fallbackLocale: 'en',
//     messages,
// });

// export default ({ app }) => {
//     app.use(i18n);
// };
