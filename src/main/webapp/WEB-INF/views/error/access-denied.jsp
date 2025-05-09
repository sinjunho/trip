<!-- /WEB-INF/views/error/access-denied.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>접근 거부</title>
</head>
<body>
    <%@ include file="/WEB-INF/views/fragments/header.jsp"%>
    <div class="container">
        <div class="text-center mt-5">
            <h1 class="text-danger">접근 거부</h1>
            <p>이 페이지에 접근할 권한이 없습니다. 로그인이 필요합니다.</p>
            <a href="${root}/member/login" class="btn btn-primary">로그인 페이지로 이동</a>
        </div>
    </div>
    <%@ include file="/WEB-INF/views/fragments/footer.jsp"%>
</body>
</html>