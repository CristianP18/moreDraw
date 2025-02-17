// axiosInstance.js
import axios from "axios";

export function createAxiosInstance(env = "local") {
  let baseURL;

  // Define o baseURL com base no ambiente
  if (env === "local") {
    baseURL = "https://iqe2tos81e.execute-api.us-east-1.amazonaws.com/";
  } else if (env === "V1") {
    baseURL = "https://iqe2tos81e.execute-api.us-east-1.amazonaws.com/";
  } else if (env === "dev") {
    baseURL = "https://iqe2tos81e.execute-api.us-east-1.amazonaws.com/";
  } else if (env === "dev2") {
    baseURL = "https://iqe2tos81e.execute-api.us-east-1.amazonaws.com/";
  } else {
    // Se o ambiente não corresponder a nenhum caso,
    // use local sem emitir warning
    baseURL = "https://iqe2tos81e.execute-api.us-east-1.amazonaws.com/";
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
