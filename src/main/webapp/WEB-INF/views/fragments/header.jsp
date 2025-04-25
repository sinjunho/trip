<%@ page import="com.ssafy.trip.model.dto.Member"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="root" value="${pageContext.servletContext.contextPath }" />
<%-- 페이지들이 가져갈 공통적인 내용들 위치 --%>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet" />
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<link
	href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap"
	rel="stylesheet">
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
    display: flex !important
;
    align-items: center;
}
</style>
<link rel="stylesheet" href="${root}/css/index.css" />

<div class="container">
	<!-- 헤더 시작 -->
	<header class="d-flex justify-content-center my-5 align-items-center">
		<div class="d-flex justify-content-end">

			<div class="logo">
				<a href="${root}/main">Enjoy <span>Trip</span></a>
			</div>
			<nav>
				<ul>
					<%
					if (session.getAttribute("member") == null) {
					%>
					<!-- 로그인 안됨 -->
					<li class="nav-signup"><a
						href="${root}//member?action=regist-member-form" class="mx-3">
							회원가입</a></li>
					<li class="nav-login"><a
						href="${root}//member?action=login-member-form" class="mx-3">
							로그인</a></li>
					<%
					} else {
					%>
					<!-- 로그인 됨 -->
					<%
					Member member = (Member) session.getAttribute("member");
					if (member.getRole() != null && member.getRole().equals("admin")) {
					%>
					<li><a href="${root}/member?action=member-list&currentPage=1"
						class="mx-3">멤버목록</a></li>
					<%
					}
					%>
					<li><a href="${root}/board?action=list" class="mx-3">게시판</a></li>
					<li><a href="${root}/attraction?action=get-attraction-form">여행지
							정보</a></li>
					<li class="nav-user-info"><a
						href="${root}//member?action=mypage-member-form" class="mx-3">
							마이페이지</a></li>
					<li class="nav-logout"><a
						href="${root}//member?action=logout-member" class="mx-3"> 로그아웃
					</a></li>
					<%
					}
					%>
				</ul>
			</nav>
		</div>
	</header>
	<!-- 에러 띄우기 -->
	<script>
		const msg = `${alertMsg }` || `${param.alertMsg}`
		if (msg) {
			alert(msg)
		}
	</script>


	<script>
		<c:remove var="alertMsg"/>
	</script>