// auth.js - JWT 토큰 관리를 위한 JavaScript 파일
const TOKEN_KEY = 'jwt_token';
const USER_KEY = 'current_user';

// 로그인 함수
function login(credentials) {
    return fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Login failed');
        }
        return response.json();
    })
    .then(data => {
        // 토큰과 사용자 정보 저장
        localStorage.setItem(TOKEN_KEY, data.token);
        localStorage.setItem(USER_KEY, JSON.stringify(data.member));
        return data;
    });
}

// 회원가입 함수
function register(userData) {
    return fetch('/api/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Registration failed');
        }
        return response.json();
    });
}

// 로그아웃 함수
function logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    // 추가적인 로그아웃 처리 (예: 홈페이지로 리다이렉트)
    window.location.href = '/';
}

// 현재 사용자 정보 가져오기
function getCurrentUser() {
    const userStr = localStorage.getItem(USER_KEY);
    if (userStr) {
        return JSON.parse(userStr);
    }
    return null;
}

// 인증 토큰 가져오기
function getAuthToken() {
    return localStorage.getItem(TOKEN_KEY);
}

// 인증 여부 확인
function isLoggedIn() {
    return !!getAuthToken();
}

// 인증이 필요한 API 호출을 위한 fetch 함수
function authenticatedFetch(url, options = {}) {
    const token = getAuthToken();
    if (!token) {
        throw new Error('No authentication token found');
    }
    
    const headers = {
        ...options.headers,
        'Authorization': `Bearer ${token}`
    };
    
    return fetch(url, {
        ...options,
        headers
    });
}

// 사용자 정보 업데이트
function updateUserProfile(userData) {
    const currentUser = getCurrentUser();
    if (!currentUser) {
        throw new Error('No user logged in');
    }
    
    return authenticatedFetch(`/api/members/${currentUser.id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to update profile');
        }
        return response.json();
    })
    .then(updatedUser => {
        // 로컬 스토리지 사용자 정보 업데이트
        localStorage.setItem(USER_KEY, JSON.stringify(updatedUser));
        return updatedUser;
    });
}

// 회원 탈퇴
function deleteAccount(password) {
    const currentUser = getCurrentUser();
    if (!currentUser) {
        throw new Error('No user logged in');
    }
    
    return authenticatedFetch(`/api/members/${currentUser.id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ password })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to delete account');
        }
        // 성공 시 로그아웃 처리
        logout();
        return response.json();
    });
}