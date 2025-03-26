import { createApp } from "vue";
import App from "./App.vue";
import router from "./router"; // ✅ importa corretamente

const app = createApp(App);

app.use(router); // ✅ registra o router
app.mount("#app");
