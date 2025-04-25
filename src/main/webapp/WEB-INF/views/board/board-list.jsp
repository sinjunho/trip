<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 목록</title>
</head>
<body>
    <%@ include file="/WEB-INF/views/fragments/header.jsp"%>
    <h1>게시판 목록</h1>
    <form class="row mb-3" action="${root }/board" method="get" id="search-form">
        <input type="hidden" name="action" value="list" />
        <input type="hidden" id="currentPage" name="currentPage" value="1" />
        <div class="d-flex justify-content-end">
            <select class="form-select w-25" name="key">
                <option disabled ${empty param.key?'selected':'' }>검색항목 선택</option>
                <option value="1" ${param.key=='1'?'selected':'' }>제목</option>
                <option value="2" ${param.key=='2'?'selected':'' }>작성자</option>
                <option value="3" ${param.key=='1'?'selected':'' }>전체</option>
            </select>
            <input type="text" class="form-control w-25" name="word" value="${param.word}">
            <button type="submit" class="btn btn-primary">검색</button>
        </div>
    </form>
    <table class="table">
        <thead>
            <tr>
                <th>번호</th>
                <th>제목</th>
                <th>작성자</th>
                <th>작성일</th>
                <th>조회수</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${page.list}" var="item">
                <tr>
                    <td>${item.bno}</td>
                    <td><a href="${root }/board?action=detail&bno=${item.bno}">${item.title}</a></td>
                    <td>${item.writer}</td>
                    <td>${item.regDate}</td>
                    <td>${item.viewCnt}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <nav class="d-flex justify-content-center">
        <ul class="pagination">
            <c:if test="${page.hasPre}">
                <li class="page-item"><a class="page-link" href="#" data-page="${ page.startPage-1}">이전</a></li>
            </c:if>
            <c:forEach begin="${page.startPage}" end="${page.endPage}" var="item">
                <li class="page-item ${page.condition.currentPage == item ? 'active' : ''}"><a class="page-link" href="#" data-page="${ item}">${item}</a></li>
            </c:forEach>
            <c:if test="${page.hasNext}">
                <li class="page-item"><a class="page-link" href="#" data-page="${ page.endPage+1}">다음</a></li>
            </c:if>
        </ul>
    </nav>
     <!-- 게시글 등록하기 버튼 -->
    <div class="d-flex justify-content-end">
        <a href="${root}/board?action=write-form" class="btn btn-success">게시글 등록</a>
    </div>
    <%@ include file="/fragments/footer.jsp"%>
</body>
<script>
const pageLinks = document.querySelectorAll(".pagination a");
pageLinks.forEach(link =>{
  link.addEventListener("click", (e)=>{
    e.preventDefault();
    document.querySelector("#currentPage").value = link.dataset.page;
    document.querySelector("#search-form").submit();
  })
})
</script>
</html>
