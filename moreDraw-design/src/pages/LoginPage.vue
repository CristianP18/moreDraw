<template>
  <q-login-page
    type="default"
    local-system
    type-id="phone-number"
    :loading-btn="loading"
    @sign-in="auth"
  >
    <template #logotipo>
      <q-img src="~/src/assets/img/homepage.png" style="width: 280px" />
    </template>
    <template #svg-image>
      <q-img src="~/src/assets/img/image.svg" style="width: 600px" />
    </template>
  </q-login-page>
</template>

<script>
import axios from "axios";
import { Notify } from "quasar";

export default {
  name: "LoginPage",
  data() {
    return {
      loading: false,
    };
  },
  methods: {
    async auth(data) {
      this.loading = true;
      try {
        const phoneNumber = data["phone-number"]
          ? `55${data["phone-number"]}`
          : null;
        const password = data.password;

        if (!phoneNumber) {
          throw new Error("Número de telefone não fornecido");
        }

        console.log("Número de telefone:", phoneNumber);
        console.log("Senha:", password);

        const response = await axios.post(
          "https://wsr4iquk8l.execute-api.us-east-1.amazonaws.com/users/login",
          {
            mobilePhone: phoneNumber,
            password,
            type: "mob",
          }
        );

        if (response.data.message.type === "positive") {
          console.log("Login bem-sucedido", response.data);
          this.userData = response.data.content;
          localStorage.setItem("picture", this.userData.picUser);
          localStorage.setItem("jwt", this.userData.jwt);
          localStorage.setItem("apiKey", this.userData.clients[0].apiKey);
          localStorage.setItem(
            "nameUser",
            `${response.data.content.firstName} ${response.data.content.lastName}`
          );
          const redirectTo = localStorage.getItem("redirectTo") || "/home";
          localStorage.removeItem("redirectTo");
          this.$router.push(redirectTo);
        } else {
          console.error("Login falhou", response.data.message.text);
          Notify.create({
            type: "negative",
            message: "Login falhou. Verifique suas credenciais.",
          });
        }
      } catch (error) {
        console.error("Erro ao fazer login", error);
        Notify.create({
          type: "negative",
          message: "Erro ao fazer login. Tente novamente mais tarde.",
        });
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>
