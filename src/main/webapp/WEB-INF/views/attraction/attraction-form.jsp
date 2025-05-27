<%@page import="java.util.Map"%>
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
<title>여행지 정보 조회</title>
</head>
<body>
    <h1>여행지 정보 조회하기</h1>
   <form id="search-form" method="GET" action="${root}/attraction/getAttractionList">
        <div class="form-group mb-3">
            <label for="contentTypeName">컨텐츠 선택</label>
            <select name="contentTypeName" id="contentTypeName" class="form-control">
                <option value="">컨텐츠를 선택해주세요</option>
                <c:if test="${empty contentList}">
                    <option>데이터가 없습니다!</option>
                </c:if>
                <c:forEach var="map1" items="${contentList}">
                <c:forEach var="entry1" items="${map1}">
                    <option value="${entry1.key}">${entry1.value}</option>
                </c:forEach>
            </c:forEach>
            </select>
        </div>
        
        <div class="form-group mb-3">
            <label for="sido">시/도 선택</label>
            <select name="sido" id="sido" class="form-control" required>
                <option value="">시/도를 선택해주세요</option>
                
                <c:forEach var="map2" items="${sidoList}">
                <c:forEach var="entry2" items="${map2}">
                    <option value="${entry2.key}">${entry2.value}</option>
                </c:forEach>
                 </c:forEach>
            </select>
        </div>
        
        <div class="form-group mb-3">
            <label for="gugun">구/군 선택</label>
            <select name="gugun" id="gugun" class="form-control" required>
                <option value="">구/군을 선택해주세요</option>
            </select>
        </div>
        
        <button type="submit" class="btn btn-primary">조회하기</button>
    </form>
    
    <!-- 랜덤 여행지 -->
    <div class="container mt-5">
        <h3>추천 여행지</h3>
        <div class="row">
            <c:forEach items="${randomAttractions}" var="attraction">
                <div class="col-md-4 mb-4">
                    <div class="card h-100">
                        <img src="${not empty attraction.firstImage1 ? attraction.firstImage1 : root.concat('/img/no-image.jpg')}" 
                             class="card-img-top" 
                             alt="${attraction.title}" 
                             style="height: 200px; object-fit: cover;" 
                             onerror="this.src='${root}/img/no-image.jpg'">
                        <div class="card-body">
                            <h5 class="card-title">${attraction.title}</h5>
                            <p class="card-text text-muted">${attraction.sido} ${attraction.gugun}</p>
                            <a href="${root}/attraction/randomDetailAttraction?no=${attraction.no}" 
                               class="btn btn-sm btn-primary">상세보기</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
    
    <!-- 페이지에서 발생한 에러를 출력하는 영역-->
    <c:if test="${!empty error }">
        <div class="alert alert-danger" role="alert">${error}</div>
    </c:if>
    
    <%@ include file="/WEB-INF/views/fragments/footer.jsp"%>
    <script>
    document.getElementById('sido').addEventListener('change', function() {
        const sidoValue = this.value;
        console.log("선택한 시도 값:", sidoValue); // 이 로그가 브라우저 콘솔에 표시되는지 확인
        
        if (!sidoValue) {
            return; // 값이 없으면 중단
        }
        
        // AJAX 요청
        fetch('${root}/attraction/getGugun?sido=' + encodeURIComponent(sidoValue))
            .then(response => {
                console.log("응답 상태:", response.status); // 응답 상태 확인
                if (!response.ok) {
                    throw new Error('서버 응답 오류: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log("받은 구군 데이터:", data); // 데이터 확인
                const gugunSelect = document.getElementById('gugun');
                gugunSelect.innerHTML = '<option value="">구/군을 선택해주세요</option>';
                
                if (data && data.length > 0) {
                    data.forEach(item => {
                        const option = document.createElement('option');
                        option.value = item.name;
                        option.text = item.name;
                        gugunSelect.appendChild(option);
                    });
                } else {
                    console.warn("구군 데이터가 비어있습니다");
                }
            })
            .catch(error => {
                console.error('구/군 데이터 조회 오류:', error);
            });
    });
    </script>
</body>
</html>