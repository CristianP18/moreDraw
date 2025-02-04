export function getAuthStatus() {
    const clientId = localStorage.getItem('clientId');
    return !!clientId;
}
