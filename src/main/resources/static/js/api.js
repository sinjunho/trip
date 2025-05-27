// /src/main/resources/static/js/api.js

/**
 * API 요청을 위한 기본 설정
 */
const API_BASE_URL = '/api';

/**
 * API 요청 함수
 * @param {string} url - 엔드포인트 URL
 * @param {Object} options - fetch 요청 옵션
 * @returns {Promise} - API 응답
 */
async function fetchApi(url, options = {}) {
    // 기본 헤더 설정
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    // JWT 토큰이 있으면 헤더에 추가
    const token = localStorage.getItem('jwt_token');
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    // 요청 옵션 구성
    const requestOptions = {
        ...options,
        headers
    };

    try {
        const response = await fetch(`${API_BASE_URL}${url}`, requestOptions);

        // JSON으로 파싱 시도
        let data;
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            data = await response.text();
        }

        // 에러 처리
        if (!response.ok) {
            throw {
                status: response.status,
                message: data.message || '오류가 발생했습니다.',
                data
            };
        }

        return data;
    } catch (error) {
        console.error('API 요청 오류:', error);
        throw error;
    }
}

/**
 * 회원 API 호출 함수들
 */
const memberApi = {
    // 회원 목록 조회
    getMembers: (params = {}) => {
        const queryParams = new URLSearchParams(params).toString();
        return fetchApi(`/members?${queryParams}`);
    },

    // 회원 정보 조회
    getMember: (id) => {
        return fetchApi(`/members/${id}`);
    },

    // 회원 가입
    register: (memberData) => {
        return fetchApi('/members/register', {
            method: 'POST',
            body: JSON.stringify(memberData)
        });
    },

    // 로그인
    login: (credentials) => {
        return fetchApi('/auth/login', {
            method: 'POST',
            body: JSON.stringify(credentials)
        });
    },

    // 회원 정보 수정
    updateMember: (id, memberData) => {
        return fetchApi(`/members/${id}`, {
            method: 'PUT',
            body: JSON.stringify(memberData)
        });
    },

    // 회원 탈퇴
    deleteMember: (id, password) => {
        return fetchApi(`/members/${id}`, {
            method: 'DELETE',
            body: JSON.stringify({ password })
        });
    }
};

/**
 * 게시판 API 호출 함수들
 */
const boardApi = {
    // 게시글 목록 조회
    getBoards: (params = {}) => {
        const queryParams = new URLSearchParams(params).toString();
        return fetchApi(`/boards?${queryParams}`);
    },

    // 게시글 상세 조회
    getBoard: (bno) => {
        return fetchApi(`/boards/${bno}`);
    },

    // 게시글 작성
    createBoard: (boardData) => {
        return fetchApi('/boards', {
            method: 'POST',
            body: JSON.stringify(boardData)
        });
    },

    // 게시글 수정
    updateBoard: (bno, boardData) => {
        return fetchApi(`/boards/${bno}`, {
            method: 'PUT',
            body: JSON.stringify(boardData)
        });
    },

    // 게시글 삭제
    deleteBoard: (bno) => {
        return fetchApi(`/boards/${bno}`, {
            method: 'DELETE'
        });
    }
};

/**
 * 관광지 API 호출 함수들
 */
const attractionApi = {
    // 관광지 목록 조회
    getAttractions: (params = {}) => {
        const queryParams = new URLSearchParams(params).toString();
        return fetchApi(`/attractions?${queryParams}`);
    },

    // 관광지 상세 조회
    getAttraction: (id) => {
        return fetchApi(`/attractions/${id}`);
    },

    // 랜덤 관광지 조회
    getRandomAttractions: (count = 6) => {
        return fetchApi(`/attractions/random?count=${count}`);
    },

    // 콘텐츠 타입 조회
    getContentTypes: () => {
        return fetchApi('/attractions/content-types');
    },

    // 시도 목록 조회
    getSido: () => {
        return fetchApi('/attractions/sido');
    },

    // 구군 목록 조회
    getGugun: (code) => {
        return fetchApi(`/attractions/gugun/${code}`);
    },

    // 인기 관광지 조회
    getRankAttractions: () => {
        return fetchApi('/attractions/rank');
    },

    // 주변 관광지 조회
    getNearbyAttractions: (id) => {
        return fetchApi(`/attractions/near/${id}`);
    }
};

// 모듈 내보내기
window.api = {
    member: memberApi,
    board: boardApi,
    attraction: attractionApi
};