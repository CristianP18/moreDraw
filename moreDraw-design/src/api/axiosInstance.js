// axiosInstance.js
import axios from "axios";

export function createAxiosInstance(env = "local") {
  let baseURL;

  // Define o baseURL com base no ambiente
  if (env === "local") {
    baseURL = "https://yo8jfiqw3e.execute-api.us-east-1.amazonaws.com/DEV/";
  } else if (env === "V1") {
    baseURL = "https://yo8jfiqw3e.execute-api.us-east-1.amazonaws.com/DEV/";
  } else if (env === "dev") {
    baseURL = "https://yo8jfiqw3e.execute-api.us-east-1.amazonaws.com/DEV/";
  } else if (env === "dev2") {
    baseURL = "https://yo8jfiqw3e.execute-api.us-east-1.amazonaws.com/DEV/";
  } else {
    // Se o ambiente não corresponder a nenhum caso,
    // use local sem emitir warning
    baseURL = "https://yo8jfiqw3e.execute-api.us-east-1.amazonaws.com/DEV/";
  }

  console.log("Using baseURL:", baseURL);

  // Retorna a instância do axios configurada
  return axios.create({
    baseURL,
    headers: {
      "Content-Type": "application/json;charset=UTF-8",
      Authorization: `Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJmZWRlcmFsVGF4IjoiMTIzNDU2Nzg5MTAiLCJpc3MiOiJtb3JlRHJhdyIsImV4cCI6MTczOTkyNDcxNiwidXNlcklkIjoiNzM3ZTkxZDQtNjc5Ni00MDVlLWIzMDMtNGRmNjBmN2YzMzY3IiwiaWF0IjoxNzM5ODg4NzE2fQ.GOxMHZ5a_0gVejK4uYBliLOc_oy6Aom1pu7bzXhzba4`,
    },
    crossDomain: true,
  });
}
