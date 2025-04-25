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
<title>Insert title here</title>
</head>
<body>
    <h1>여행지 정보 조회하기</h1>
    <form action="${root}/attraction" class="m-3">
        <input type="hidden" name="action" value="getAttractionList" />
        
		<label for="contentTypeName">컨텐츠 선택</label>
		<select name="contentTypeName" id="contentTypeName">
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
		
		<label for="sido">시/도 선택</label>
		<select name="sido" id="sido" required>
			<option value="">시/도를 선택해주세요</option>
			<c:forEach var="map2" items="${sidoList}">
		        <c:forEach var="entry2" items="${map2}"> 
		            <option value="${entry2.key}">${entry2.value}</option>
		        </c:forEach>
		    </c:forEach>
		</select>
		
		<label for="gugun">구/군 선택</label>
		<select name="gugun" id="gugun" required>
		<option value="">구/군를 선택해주세요</option>
			<c:forEach var="map3" items="${gugunList}">
		        <c:forEach var="entry3" items="${map3}"> 
		            <option value="${entry3.key}">${entry3.value}</option>
		        </c:forEach>
		    </c:forEach>
		</select>
		
        <button type="submit" class="btn btn-primary">조회하기</button>
    </form>
    
    <!-- 랜덤 여행지  -->
   <!-- 추천 여행지 섹션 -->
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
                        <a href="${root}/attraction?action=randomDetailAttraction&no=${attraction.no}" 
                           class="btn btn-sm btn-primary">상세보기</a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
    
    <!-- 페이지에서 발생한 에러를 출력하는 영역-->
    <c:if test="${!empty error }">
        <div class="alert alert-danger" role="alert">${error }</div>
    </c:if>
	
    <%@ include file="/fragments/footer.jsp"%>
    <script>
	document.getElementById('sido').addEventListener('change', function() {
    const sidoValue = this.value;
    const gugunSelect = document.getElementById('gugun');
    
    // 시/도 선택 시 구/군 셀렉트 초기화
    gugunSelect.innerHTML = '<option value="">구/군을 선택해주세요</option>';
    
    if (sidoValue) {
        // AJAX 요청
        fetch('${root}/attraction?action=getGugun&value=' + sidoValue)
            .then(response => response.json())
            .then(data => {
                // 받은 데이터로 구/군 옵션 추가
                data.forEach(item => {
                    for (let key in item) {
                        const option = document.createElement('option');
                        option.value = key;
                        option.text = item[key];
                        gugunSelect.appendChild(option);
                    }
                });
            })
            .catch(error => {
                console.error('Error:', error);
                gugunSelect.innerHTML = '<option value="">데이터 로드 실패</option>';
            });
    }
});
</script>
</body>
</html>

</html>


   <%--
    <script type="text/javascript">
    document.addEventListener("DOMContentLoaded", () => {
        initSido();
    });

    const updateSelect = (selectElement, placeholder, data) => {
        if (!(selectElement instanceof HTMLElement)) {
            console.error("유효한 DOM 요소가 아닙니다:", selectElement);
            return;
        }

        if (!Array.isArray(data) || data.length === 0) {
            console.error("유효한 데이터 배열이 제공되지 않았습니다.", data);
            return;
        }

        selectElement.innerHTML = "";
        const defaultOption = document.createElement("option");
        defaultOption.value = "";
        defaultOption.textContent = placeholder;
        defaultOption.disabled = true;
        defaultOption.selected = true;
        selectElement.appendChild(defaultOption);

        data.forEach(item => {
            if (item && typeof item === "object" && "key" in item && "label" in item) {
                const option = document.createElement("option");
                option.value = item.key;
                option.textContent = item.label;
                selectElement.appendChild(option);
            }
        });
    };

    const getFetch = async (url, param) => {
        try {
            const queryString = new URLSearchParams(param).toString();
            const response = await fetch(url + "?" + queryString);
            const result = await response.json();
            console.log("요청 URL:", url, param, result);
            return result;
        } catch (e) {
            console.log(e);
            throw e;
        }
    };

    const sido = document.querySelector("#sido");
    const gugun = document.querySelector("#gugun");

    const initSido = async function () {
        try {
            const params = {
                format: "json",
                numOfRows: 20,
                key: "CEAAD8AF-0926-318F-A203-257E88CE0FA5",
                domain: "localhost"
            };

            const json = await getFetch("https://api.vworld.kr/ned/data/admCodeList", params);

            if (!json) {
                console.error("API 응답이 없습니다.");
                return;
            }

            console.log("API 응답:", json);

            const admVOList = json?.admVOList?.admVOList || json?.admVOList;

            if (!Array.isArray(admVOList)) {
                console.error("admVOList가 배열이 아닙니다:", admVOList);
                return;
            }

            const sidoData = admVOList.map(item => ({
                key: item.admCode,
                label: item.lowestAdmCodeNm
            }));
			
            console.log(sidoData);
            updateSelect(sido, "시/도", sidoData);
        } catch (error) {
            console.error("시/도 로드 실패:", error);
        }
    };
    
    sido.addEventListener("change", async () => {
        try {
            const params = {
                admCode: sido.value,
                format: "json",
                numOfRows: 20,
                key: "CEAAD8AF-0926-318F-A203-257E88CE0FA5",
                domain: "localhost"
            };
            
            const json = await getFetch("https://api.vworld.kr/ned/data/admSiList", params);

			if (!json) {
                console.error("API 응답이 없습니다.");
                return;
            }

            console.log("API 응답:", json);

            const admVOList = json?.admVOList?.admVOList || json?.admVOList;

            if (!Array.isArray(admVOList)) {
                console.error("admVOList가 배열이 아닙니다:", admVOList);
                return;
            }

            const gugunData = admVOList.map(item => ({
                key: item.admCode,
                label: item.lowestAdmCodeNm
            }));
           
            updateSelect(gugun, "구/군", gugunData);
        } catch (error) {
            console.error("구/군 로드 실패:", error);
        }
    });
 
</script>
    --%>
