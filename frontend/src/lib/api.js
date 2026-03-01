const BASE_URL = 'http://localhost:8080';

export function getToken() {
    if (typeof window !== 'undefined') {
        return localStorage.getItem('jwt_token');
    }
    return null;
}

export function getUserId() {
    if (typeof window !== 'undefined') {
        return localStorage.getItem('userId');
    }
    return null;
}

export function setAuthData(token, id) {
    if (typeof window !== 'undefined') {
        localStorage.setItem('jwt_token', token);
        localStorage.setItem('userId', id);
    }
}

export function clearAuthData() {
    if (typeof window !== 'undefined') {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('userId');
    }
}

export async function fetchWithAuth(url, options = {}) {
    const token = getToken();
    const userId = getUserId();

    const headers = new Headers(options.headers || {});

    if (token) {
        headers.append('Authorization', `Bearer ${token}`);
    }

    if (userId) {
        headers.append('X-User-Id', userId);
    }

    if (!headers.has('Content-Type') && !(options.body instanceof FormData)) {
        headers.append('Content-Type', 'application/json');
    }

    const response = await fetch(`${BASE_URL}${url}`, {
        ...options,
        headers,
    });

    if (response.status === 401) {
        clearAuthData();
        if (typeof window !== 'undefined') {
            window.location.href = '/login';
        }
        throw new Error('Session expired. Please log in again.');
    }

    if (response.status === 204) {
        return null;
    }

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || 'An error occurred');
    }

    return data;
}
