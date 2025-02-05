// axiosInstance.js
import axios from "axios";

export function createAxiosInstance(env = "local") {
  let baseURL;

  // Define o baseURL com base no ambiente
  if (env === "local") {
    baseURL = "http://:8080/";
  } else if (env === "V1") {
    baseURL = "https://api..com.br/v1/";
  } else if (env === "dev") {
    baseURL = "https://api..com.br/dev/";
  } else if (env === "dev2") {
    baseURL = "https://api..com.br/dev2/";
  } else {
    // Se o ambiente não corresponder a nenhum caso,
    // use local sem emitir warning
    baseURL = "http://:8080/";
  }

  console.log("Using baseURL:", baseURL);

  // Retorna a instância do axios configurada
  return axios.create({
    baseURL,
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
    crossDomain: true,
  });
}
