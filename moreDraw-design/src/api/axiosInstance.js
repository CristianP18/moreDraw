// axiosInstance.js
import axios from 'axios';

export function createAxiosInstance(env) {
    let baseURL;

    // Definindo o baseURL com base no ambiente
    if (env === 'local') {
        baseURL = 'http://192.168.0.248:8080/';
    } else if (env === 'V1') {
        baseURL = 'https://api.tracebox.com.br/dev2/';
    } else if (env === 'dev') {
        baseURL = 'https://api.tracebox.com.br/dev2/';
    } else if (env === 'dev2') {
        baseURL = 'https://api.tracebox.com.br/dev2/';
    } else if (env === 'yard') {
        baseURL = 'https://api.tracebox.com.br/yard/';
    } else {
        throw new Error('Environment not specified or unsupported.');
    }

    console.log('Using baseURL:', baseURL);

    // Retorna a instância do axios configurada corretamente
    return axios.create({
        baseURL,
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
            Authorization: `Bearer ${localStorage.getItem('jwt')}`,
            'x-api-key': 'T5URcur2aEaf0NjBz57zO1hPi7S07Z9g9nts2c0d',
        },
        crossDomain: true,
    });
}
