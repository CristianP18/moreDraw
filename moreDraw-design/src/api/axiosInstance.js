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
    // Se o ambiente não corresponder a nenhum caso,
    // use local sem emitir warning
    baseURL = "https://04lg3w3swi.execute-api.us-east-1.amazonaws.com/DEV/";
  }

  console.log("Using baseURL:", baseURL);

  // Retorna a instância do axios configurada
  const instance = axios.create({
    baseURL,
    headers: {
      // "Content-Type": "application/json;charset=UTF-8",
      Authorization: `Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJmZWRlcmFsVGF4IjoiMTIzNDU2Nzg5MTAiLCJpc3MiOiJtb3JlRHJhdyIsImV4cCI6MTc0Mjg1NzMwMCwidXNlcklkIjoiNzM3ZTkxZDQtNjc5Ni00MDVlLWIzMDMtNGRmNjBmN2YzMzY3IiwiaWF0IjoxNzQyODIxMzAwfQ.kImxwXCWnlnq-Yx8HmdCK0MUGkWui1K5Kj7V87nHBQQ`,
    },
    crossDomain: true,
  });

  // Função para remover o cabeçalho Content-Type para requisições multipart/form-data
  instance.interceptors.request.use((config) => {
    if (config.data instanceof FormData) {
      // FormData precisa que o navegador defina o Content-Type com o boundary correto
      delete config.headers["Content-Type"];
    }
    return config;
  });

  return instance;
}
