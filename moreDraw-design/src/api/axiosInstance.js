// axiosInstance.js
import axios from "axios";

export function createAxiosInstance(env = "local") {
  let baseURL;

  // Define o baseURL com base no ambiente
  if (env === "local") {
    baseURL = "https://04lg3w3swi.execute-api.us-east-1.amazonaws.com/DEV/";
  } else if (env === "V1") {
    baseURL = "https://04lg3w3swi.execute-api.us-east-1.amazonaws.com/DEV/";
  } else if (env === "dev") {
    baseURL = "https://04lg3w3swi.execute-api.us-east-1.amazonaws.com/DEV/";
  } else if (env === "dev2") {
    baseURL = "https://04lg3w3swi.execute-api.us-east-1.amazonaws.com/DEV/";
  } else {
    // Se o ambiente não corresponder a nenhum caso, usa local
    baseURL = "https://04lg3w3swi.execute-api.us-east-1.amazonaws.com/DEV/";
  }

  console.log("Using baseURL:", baseURL);

  // Cria a instância do axios sem header Authorization fixo
  const instance = axios.create({
    baseURL,
    crossDomain: true,
  });

  // Interceptor para configurar o header Authorization a cada requisição
  instance.interceptors.request.use((config) => {
    // Se os dados forem do tipo FormData, remove o Content-Type para que o navegador defina o boundary correto
    if (config.data instanceof FormData) {
      delete config.headers["Content-Type"];
    }
    // Busca o token atualizado do localStorage e, se existir, define o header Authorization
    const token = localStorage.getItem("jwt");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  });

  return instance;
}
