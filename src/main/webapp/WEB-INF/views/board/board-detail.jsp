<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 상세</title>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/header.jsp"%>
<div class="container mt-4">
    <h1>게시글 상세</h1>
    
    <div class="card mb-4">
        <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="card-title mb-0">${board.title}</h5>
            <div>
                <small class="text-muted">작성자: ${board.writer}</small>
                <small class="text-muted ms-3">조회수: ${board.viewCnt}</small>
            </div>
        </div>
        <div class="card-body">
            <p class="card-text">${board.content}</p>
        </div>
        <div class="card-footer text-end">
            <small class="text-muted">작성일: ${board.regDate}</small>
        </div>
    </div>
    
    <div class="d-flex justify-content-between">
        <a href="${root}/board?action=list" class="btn btn-secondary">목록으로</a>
        
        <c:if test="${sessionScope.member.id eq board.writer || sessionScope.member.role eq 'admin'}">
            <div>
                <a href="${root}/board?action=modify-form&bno=${board.bno}" class="btn btn-primary">수정</a>
                <a href="#" onclick="deleteBoard(${board.bno})" class="btn btn-danger">삭제</a>
            </div>
        </c:if>
    </div>
</div>

<%@ include file="/fragments/footer.jsp"%>

<script>
function deleteBoard(bno) {
    if (confirm('정말 삭제하시겠습니까?')) {
        location.href = '${root}/board?action=delete&bno=' + bno;
    }
}
</script>
</body>
</html>