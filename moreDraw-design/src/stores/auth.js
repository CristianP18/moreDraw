export function getAuthStatus() {
    const jwt = localStorage.getItem('jwt');
    return !!jwt;
}
