import { createApp } from "vue";
import App from "./App.vue";
import { createI18n } from "vue-i18n";
import { language } from "./language/language";

const i18n = createI18n({
  legacy: false, // para Vue 3
  locale: "pt-BR", // idioma padrão
  fallbackLocale: "en-US", // idioma secundário
  messages: language,
});

const app = createApp(App);
app.use(i18n);
app.mount("#app");
