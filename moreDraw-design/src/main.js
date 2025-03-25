// main.js
import { createApp } from "vue";
import App from "./App.vue";
import { Quasar, Notify } from "quasar";
import { createPinia } from "pinia";
import router from "./router";
import "quasar/dist/quasar.css";

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(router);

// Registra o Quasar e seus plugins (incluindo Notify)
app.use(Quasar, {
  plugins: { Notify },
});

app.mount("#app");
