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

th:nth-child(1) {
	width: 80px;
}

th:nth-child(2) {
	width: 50%;
}

th:nth-child(3) {
	
}

th:nth-child(4) {
	width: 75px;
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

	<h1>컨텐츠 목록</h1>
	<br>

	<div id="map"></div>

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
				<c:forEach items="${attrList }" var="item">
					<tr>
						<td>${item.title }</td>
						<td>${item.addr }</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<!-- 현재 페이지를 추적하기 위한 폼 추가 -->
		<form id="search-form" method="GET" action="${root}/attraction">
			<input type="hidden" name="action" value="getAttractionList">
			<input type="hidden" name="contentTypeName"
				value="${param.contentTypeName}"> <input type="hidden"
				name="sido" value="${param.sido}"> <input type="hidden"
				name="gugun" value="${param.gugun}"> <input type="hidden"
				id="currentPage" name="currentPage"
				value="${page.condition.currentPage}">
		</form>
		<nav class="d-flex justify-content-center">
			<ul class="pagination">
				<c:if test="${page.hasPre}">
					<li class="page-item"><a class="page-link" href="#"
						data-page="${ page.startPage-1}">이전</a></li>
				</c:if>
				<c:forEach begin="${page.startPage}" end="${page.endPage}"
					var="item">
					<li
						class="page-item ${page.condition.currentPage == item ? 'active' : ''}"><a
						class="page-link" href="#" data-page="${ item}">${item}</a></li>
				</c:forEach>
				<c:if test="${page.hasNext}">
					<li class="page-item"><a class="page-link" href="#"
						data-page="${ page.endPage+1}">다음</a></li>
				</c:if>
			</ul>
		</nav>
	</div>

</body>

<script>
const map = sop.map("map");
map.mks = []; // marker 정보 관리
const addrInfos = []; // 지도 정보를 저장할 배열
//x, y, title
<c:forEach var="addr" items="${attrList }">

	var address = "${addr.addr }";
	
	
	var utmkXY = new sop.LatLng (${addr.latitude}, ${addr.longitude});

	addrInfos.push({
		no : "${addr.no}",
		title : "${addr.title}",
		x : utmkXY.x,
		y : utmkXY.y,
		addr : "${addr.addr}"
	});
	
	console.log("no : "+${addr.no});
	  
</c:forEach>

const showAddress = (addrInfos)=>{
    const target = document.querySelector("#address-body");
    target.innerHTML = "";
    if(addrInfos.length>0){
        document.querySelector("#map").style.display="block";
        addrInfos.forEach(item =>{
    		var temp = "";
        	temp +=`
                <tr>
                  <td><a href =`;
            temp += `${root }/attraction?action=detailAttraction&no=\${item.no }`
            
            temp += `>\${item.title}</a></td>
                  <td>\${item.addr}</td>
                </tr>
            `;
            
            console.log(temp);
            target.innerHTML += temp;
        })
        updateMap(map, addrInfos);
    }else{
        document.querySelector("#map").style.display="none";
    }
}
showAddress(addrInfos);

const pageLinks = document.querySelectorAll(".pagination a");
pageLinks.forEach(link => {
  link.addEventListener("click", (e) => {
    e.preventDefault();
    document.querySelector("#currentPage").value = link.dataset.page;
    document.querySelector("#search-form").submit();
  })
})

</script>



</html>