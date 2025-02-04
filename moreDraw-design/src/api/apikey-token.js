import { getCookie } from 'src/api/getCookie';

export const authOptions = () => {
    const token = getCookie('token');
    return {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    };
};
