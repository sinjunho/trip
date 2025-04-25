<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<%@ include file="/WEB-INF/views/fragments/header.jsp"%>

	<h2>${member.name }님의 상세페이지.</h2>
	<hr />
	<ul>
		<li>이름 : ${member.name }</li>
		<li>아이디 : ${member.id }</li>
		<li>역할 : ${member.role }</li>
		<li>주소 : ${member.address }</li>
		<li>전화번호 : ${member.tel }</li>
	</ul>

	<hr />
	<h1>회원 탈퇴 시켜 버리기</h1>
	<p>경고 ! 회원 탈퇴하면 다시 복구 안해줍니다 삭제 위험</p>
	<form action="${root}/member/delete" method="post" class="m-3">
		<input type="hidden" name="id" value="${member.id }" /> 
		<input type="hidden" name="password" value="${member.password }" /> 
		<button type="submit" class="btn btn-primary">회원 탈퇴</button>
	</form>
	<c:if test="${!empty error }">
		<div class="alert alert-danger" role="alert">${error }</div>
	</c:if>
		<%@ include file="/WEB-INF/views/fragments/footer.jsp"%>
</body>
</html>
