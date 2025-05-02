<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="root" value="${pageContext.servletContext.contextPath }" />
<%-- 페이지들이 가져갈 공통적인 내용들 위치 --%>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap" rel="stylesheet">
<style>
body, html {
    font-family: 'Noto Sans KR', Arial, sans-serif;
}

header>#logo {
    width: 90px;
    margin-bottom: 8px;
    margin-left: 10px;
}

header>h1 {
    line-height: 50px;
    display: inline-block;
    height: 50px;
}
/* Header 스타일 (myPage.html에서 가져옴) */
header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 50px;
    background-color: #fff;
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
    font-family: 'Noto Sans KR', Arial, sans-serif;
}

.logo {
    font-size: 24px;
    font-weight: bold;
}

.logo span {
    color: #007bff;
}

.logo a {
    text-decoration: none;
    color: black;
}

nav ul {
    list-style: none;
    display: flex;
    gap: 20px;
}

nav ul li {
    display: inline;
}

nav ul li a {
    text-decoration: none;
    color: #333;
    font-weight: bold;
    cursor: pointer;
}

.user-welcome {
    color: #007bff;
    font-weight: bold;
}
.d-flex {
    display: flex !important;
    align-items: center;
}
</style>
<link rel="stylesheet" href="${root}/css/index.css" />
<script src="${root}/js/auth.js"></script>

<div class="container">
    <!-- 헤더 시작 -->
    <header class="d-flex justify-content-center my-5 align-items-center">
        <div class="d-flex justify-content-end">
            <div class="logo">
                <a href="${root}/">Enjoy <span>Trip</span></a>
            </div>
            <nav>
                <ul id="nav-menu">
                    <!-- 메뉴는 JavaScript로 동적 생성됩니다 -->
                </ul>
            </nav>
        </div>
    </header>

<!-- 알림 및 에러 메시지 처리 -->
<script>
    // 알림 메시지 처리
    const alertMsg = `${alertMsg}` || `${param.alertMsg}`;
    if (alertMsg) {
        alert(alertMsg);
    }
    
    // 에러 메시지 처리 
    const errorMsg = `${error}` || `${param.error}`;
    if (errorMsg) {
        alert("오류가 발생했습니다: " + errorMsg);
    }
    
    // 인증 상태에 따라 UI 업데이트
    function updateUIForAuthState() {
        const navMenu = document.getElementById('nav-menu');
        const isAuthenticated = isLoggedIn();
        const currentUser = getCurrentUser();
        
        // 기존 메뉴 항목 삭제
        while (navMenu.firstChild) {
            navMenu.removeChild(navMenu.firstChild);
        }
        
        if (!isAuthenticated) {
            // 로그인하지 않은 경우 메뉴
            navMenu.innerHTML = `
                <li class="nav-signup"><a href="${root}/member/regist" class="mx-3">회원가입</a></li>
                <li class="nav-login"><a href="${root}/member/login" class="mx-3">로그인</a></li>
            `;
        } else {
            // 로그인한 경우 메뉴
            let menuHtml = '';
            
            // 관리자인 경우 추가 메뉴
            if (currentUser.role === 'admin') {
                menuHtml += `<li><a href="${root}/member/list" class="mx-3">멤버목록</a></li>`;
            }
            
            // 일반 메뉴
            menuHtml += `
                <li><a href="${root}/board/list" class="mx-3">게시판</a></li>
                <li><a href="${root}/attraction/get-attraction-form" class="mx-3">여행지 정보</a></li>
                <li class="nav-user-info"><a href="${root}/member/mypage" class="mx-3">마이페이지</a></li>
                <li class="nav-logout"><a href="#" onclick="handleLogout()" class="mx-3">로그아웃</a></li>
            `;
            
            navMenu.innerHTML = menuHtml;
        }
    }
    
    // 페이지 로드 시 UI 업데이트
    document.addEventListener('DOMContentLoaded', function() {
        updateUIForAuthState();
    });
    
    // 로그아웃 처리 함수
    function handleLogout() {
        logout();
        alert('로그아웃 되었습니다.');
        window.location.href = '${root}/';
    }
</script>

<!-- 메시지 세션에서 제거 -->
<c:remove var="alertMsg"/>
<c:remove var="error"/>
</div>