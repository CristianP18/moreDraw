<template>
  <div class="q-pa-md flex flex-center" style="min-height: 100vh">
    <q-card class="q-pa-md" style="width: 400px">
      <!-- Logotipo -->
      <div class="text-center">
        <q-img src="~/src/assets/img/homepage.png" style="width: 280px" />
      </div>
      <!-- Imagem ilustrativa -->
      <div class="q-mt-md text-center">
        <q-img
          src="~/src/assets/img/image.svg"
          style="width: 600px; max-width: 100%"
        />
      </div>
      <!-- Formulário de Login -->
      <q-form @submit.prevent="auth" class="q-gutter-md q-mt-lg">
        <q-input v-model="federalTax" label="Federal Tax" outlined required />
        <q-input
          v-model="phone"
          label="Número de Telefone"
          type="tel"
          outlined
          required
        />
        <q-input
          v-model="password"
          label="Senha"
          type="password"
          outlined
          required
        />
        <q-card-actions align="right">
          <q-btn
            label="Entrar"
            color="primary"
            type="submit"
            :loading="loading"
          />
        </q-card-actions>
      </q-form>
    </q-card>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "LoginPage",
  data() {
    return {
      loading: false,
      federalTax: "",
      phone: "",
      password: "",
    };
  },
  methods: {
    async auth() {
      if (!this.federalTax) {
        this.$q.notify({
          type: "negative",
          message: "Federal Tax não fornecido.",
        });
        return;
      }
      if (!this.phone) {
        this.$q.notify({
          type: "negative",
          message: "Número de telefone não fornecido.",
        });
        return;
      }

      this.loading = true;
      const federalTax = this.federalTax;
      const phoneNumber = `+55${this.phone}`;
      const password = this.password;

      try {
        const response = await axios.post(
          "https://ftrrk8b6rb.execute-api.us-east-1.amazonaws.com/users/login",
          {
            federalTax,
            mobilePhone: phoneNumber,
            password,
            type: "web",
          }
        );

        console.log("Resposta da API:", response.data);
        if (
          response.data &&
          response.data.message &&
          response.data.message.type === "positive"
        ) {
          const userData = response.data.content;
          localStorage.setItem("picture", userData.picUser);
          localStorage.setItem("jwt", userData.jwt);
          localStorage.setItem(
            "nameUser",
            `${userData.firstName} ${userData.lastName}`
          );

          // // Notificação de sucesso
          // this.$q.notify({
          //   type: "positive",
          //   message: "Login realizado com sucesso!",
          // });

          // Redireciona para a página de design após um pequeno delay
          setTimeout(() => {
            this.$router.push("/design");
          }, 500);
        } else {
          const errorMsg =
            (response.data &&
              response.data.message &&
              response.data.message.text) ||
            "Login falhou. Verifique suas credenciais.";
          // this.$q.notify({
          //   type: "negative",
          //   message: errorMsg,
          // });
        }
      } catch (error) {
        console.error("Erro ao fazer login", error);
        this.$q.notify({
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

<style scoped>
.flex {
  display: flex;
}
.flex-center {
  align-items: center;
  justify-content: center;
}
</style>
