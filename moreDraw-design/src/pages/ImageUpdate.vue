<template>
  <div class="add-image-container">
    <h2>Adicionar Nova Imagem</h2>
    <form @submit.prevent="handleSubmit">
      <!-- Título da Imagem -->
      <div class="form-group">
        <label for="title">Título:</label>
        <input
          type="text"
          id="title"
          v-model="form.title"
          required
          placeholder="Digite o título da imagem"
        />
      </div>

      <!-- Descrição da Imagem -->
      <div class="form-group">
        <label for="description">Descrição:</label>
        <textarea
          id="description"
          v-model="form.description"
          required
          placeholder="Digite a descrição detalhada da imagem"
        ></textarea>
      </div>

      <!-- Tipo da Imagem -->
      <div class="form-group">
        <label for="type">Tipo:</label>
        <input
          type="text"
          id="type"
          v-model="form.type"
          required
          placeholder="Digite o tipo de componente gráfico"
        />
      </div>

      <!-- Tags da Imagem -->
      <div class="form-group">
        <label for="tags">Tags:</label>
        <input
          type="text"
          id="tags"
          v-model="form.tags"
          required
          placeholder="Digite as tags separadas por vírgula"
        />
      </div>

      <!-- Idioma dos Dados -->
      <div class="form-group">
        <label for="language">Idioma:</label>
        <input
          type="text"
          id="language"
          v-model="form.language"
          required
          placeholder="Digite o idioma dos dados indexados"
        />
      </div>

      <!-- Upload da Imagem -->
      <div class="form-group">
        <label for="image">Imagem:</label>
        <input
          type="file"
          id="image"
          @change="handleFileChange"
          accept="image/*"
          required
        />
      </div>

      <!-- Botão de Submissão -->
      <button type="submit" :disabled="loading">
        {{ loading ? "Enviando..." : "Adicionar Imagem" }}
      </button>
    </form>

    <!-- Feedback para o Usuário -->
    <div v-if="error" class="error-message">
      {{ error }}
    </div>
    <div v-if="success" class="success-message">
      {{ success }}
    </div>
  </div>
</template>

<script>
import { ref } from "vue";
import axios from "axios";

export default {
  name: "AddImage",
  setup() {
    // Estado do Formulário
    const form = ref({
      title: "",
      description: "",
      type: "",
      tags: "",
      language: "",
      imageFile: null,
    });

    // Estado de Carregamento e Feedback
    const loading = ref(false);
    const error = ref("");
    const success = ref("");

    // Token JWT (Preferencialmente, armazene em variáveis de ambiente ou gerencie via Vuex/Vue Composition API)
    const token = ref(
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJmZWRlcmFsVGF4IjoiMTIzNDU2Nzg5MTAiLCJpc3MiOiJtb3JlRHJhdyIsImV4cCI6MTczMjg3ODI0MiwidXNlcklkIjoiNzM3ZTkxZDQtNjc5Ni00MDVlLWIzMDMtNGRmNjBmN2YzMzY3IiwiaWF0IjoxNzMyODQyMjQyfQ.8HDfVf8s2fUoME3ZEoKhSVI5e0SvriWjtoMyNv3qM8Y"
    );

    // Manipulador de Mudança no Input de Arquivo
    const handleFileChange = (event) => {
      const file = event.target.files[0];
      if (file) {
        form.value.imageFile = file;
      }
    };

    // Método para Submeter o Formulário
    const handleSubmit = async () => {
      // Limpar Feedback Anterior
      error.value = "";
      success.value = "";

      // Validar que um arquivo foi selecionado
      if (!form.value.imageFile) {
        error.value = "Por favor, selecione uma imagem para upload.";
        return;
      }

      loading.value = true;

      try {
        // 1. Criar o Registro da Imagem no Banco de Dados
        const imageData = {
          title: form.value.title,
          description: form.value.description,
          type: form.value.type,
          tags: form.value.tags.split(",").map((tag) => tag.trim()),
          language: form.value.language,
        };

        const createImageResponse = await axios.post(
          "https://7q7xoa9f43.execute-api.us-east-1.amazonaws.com/DEV/image",
          imageData,
          {
            headers: {
              Authorization: `Bearer ${token.value}`,
              "Content-Type": "application/json",
            },
          }
        );

        const imageId = createImageResponse.data.ImageId; // Ajuste conforme a resposta real

        if (!imageId) {
          throw new Error("Falha ao criar o registro da imagem.");
        }

        // 2. Fazer o Upload da Imagem para o S3 via API
        const formData = new FormData();
        formData.append("file", form.value.imageFile);

        await axios.post(
          `https://7q7xoa9f43.execute-api.us-east-1.amazonaws.com/DEV/picture/${imageId}`,
          formData,
          {
            headers: {
              Authorization: `Bearer ${token.value}`,
              "Content-Type": "multipart/form-data",
            },
          }
        );

        // Sucesso
        success.value = "Imagem adicionada e enviada com sucesso!";
        // Resetar o Formulário
        form.value = {
          title: "",
          description: "",
          type: "",
          tags: "",
          language: "",
          imageFile: null,
        };
        // Resetar o Input de Arquivo
        document.getElementById("image").value = "";
      } catch (err) {
        console.error(err);
        error.value =
          err.response?.data?.message ||
          "Ocorreu um erro ao adicionar a imagem. Por favor, tente novamente.";
      } finally {
        loading.value = false;
      }
    };

    return {
      form,
      loading,
      error,
      success,
      handleFileChange,
      handleSubmit,
    };
  },
};
</script>

<style scoped>
.add-image-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}
h2 {
  text-align: center;
  margin-bottom: 20px;
}
form {
  display: flex;
  flex-direction: column;
}
.form-group {
  margin-bottom: 15px;
}
label {
  font-weight: bold;
  margin-bottom: 5px;
  display: block;
}
input[type="text"],
textarea,
input[type="file"] {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}
button {
  padding: 10px;
  background-color: #42b983;
  color: white;
  border: none;
  cursor: pointer;
}
button:disabled {
  background-color: #a5d6a7;
  cursor: not-allowed;
}
.error-message {
  margin-top: 20px;
  color: red;
  text-align: center;
}
.success-message {
  margin-top: 20px;
  color: green;
  text-align: center;
}
</style>
