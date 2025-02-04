import axios from 'axios';

export function createAxiosInstance() {
    const taxId = localStorage.getItem('taxId');
    return axios.create({
        baseURL: `${process.env.BASE_URL}${taxId}/v1`,
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'x-api-key': 'yb9qulxRUY2G3lL2wdXWI4HgWSQXjYZW4fwt3jX3',
        },
    });
}
