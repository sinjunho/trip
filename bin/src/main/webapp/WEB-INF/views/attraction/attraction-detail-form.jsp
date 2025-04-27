<%@page import="com.ssafy.trip.model.dto.Attraction"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/fragments/header.jsp"%>
<link rel="stylesheet" href="${root}/css/atteaction.css">
<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>map list</title>
<style type="text/css">
#profile {
	max-width: 150px;
}

input[type='text'] {
	width: 100%;
}

#map {
	display: none;
	width: 100%;
	height: 900px;
}
</style>


<script>
	const key_vworld = `${key_vworld}`;
	const key_sgis_service_id = `${key_sgis_service_id}`;
	const key_sgis_security = `${key_sgis_security}`; // 보안 key
	const key_data = `${key_data}`;
</script>
<script
	src="https://sgisapi.kostat.go.kr/OpenAPI3/auth/javascriptAuth?consumer_key=${key_sgis_service_id }"></script>
<script src="${root }/js/common.js"></script>


</head>

<body>

	<h1>컨텐츠 상세 설명</h1>

	<br>
	<div class="card-body">
		<table class="table table-bordered" id="table-user-address">
			<thead>
				<tr>
					<th>이름</th>
					<th>제목</th>
					<th>타입</th>
					<th>시도</th>
					<th>구군</th>
					<th>주소</th>
					<th>전화번호</th>
				</tr>
			</thead>
			<tbody id="address-body">
				<tr>
					<td>${detailAttraction.no }</td>
					<td>${detailAttraction.title }</td>
					<td>${detailAttraction.contentTypeName }</td>
					<td>${detailAttraction.sido }</td>
					<td>${detailAttraction.gugun }</td>
					<td>${detailAttraction.addr }</td>
					<td>${detailAttraction.tel }</td>
				</tr>
			</tbody>
		</table>

		<table class="table table-bordered" id="table-user-address">
			<thead>
				<tr>
					<th>이미지</th>
				</tr>
			</thead>
			<tbody id="address-body">
				<tr>
					<td><img src="${detailAttraction.firstImage1 }"
						alt="관광지에서 별도의 이미지를 제공하지 않습니다." /></td>
				</tr>
			</tbody>
		</table>

		<a href="#" onClick="history.back()"><input type="button"
			value="이전페이지로 돌아가기"></a> <br> <br> <br> <br>

		<h1>주변에 있는 관광지는..?</h1>
		<br>
		<div class="card-body">
			<table class="table table-bordered" id="table-user-address">
				<thead>
					<tr>
						<th>제목</th>
						<th>주소</th>
					</tr>
				</thead>
				<tbody id="address-body">
					<c:forEach items="${nearAttraction }" var="item">
						<tr>
							<td><a href="${root }/attraction?action=detailAttraction&no=${item.no }">${item.title }</a></td>
							<td>${item.addr }</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<!-- 
			현재 페이지를 추적하기 위한 폼 추가 
			<form id="search-form" method="GET" action="${root}/attraction">
				<input type="hidden" name="action" value="getAttractionList">
				<input type="hidden" name="contentTypeName" value="${param.contentTypeName}"> 
				<input type="hidden" name="sido" value="${param.sido}"> 
				<input type="hidden" name="gugun" value="${param.gugun}"> 
				<input type="hidden" id="currentPage" name="currentPage" value="${page.condition.currentPage}">
			</form>
			-->
		</div>
		<br>
		<br>
		<br>
		</div>
</body>

</html>