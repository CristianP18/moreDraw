export function getCookie(name) {
    const cookie = {};
    document.cookie.split(';').forEach((el) => {
        const split = el.split('=');
        cookie[split[0].trim()] = split.slice(1).join('=');
    });
    return cookie[name];
}
