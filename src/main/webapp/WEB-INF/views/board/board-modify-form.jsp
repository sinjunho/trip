<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 수정</title>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/header.jsp"%>
<div class="container mt-4">
    <h1>게시글 수정</h1>
    
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert">
            ${error}
        </div>
    </c:if>
    
    <form action="${root}/board" method="post">
        <input type="hidden" name="action" value="modify">
        <input type="hidden" name="bno" value="${board.bno}">
        
        <div class="mb-3">
            <label for="title" class="form-label">제목</label>
            <input type="text" class="form-control" id="title" name="title" value="${board.title}" required>
        </div>
        
        <div class="mb-3">
            <label for="content" class="form-label">내용</label>
            <textarea class="form-control" id="content" name="content" rows="10" required>${board.content}</textarea>
        </div>
        

        
        <div class="d-flex justify-content-between">
            <a href="${root}/board?action=detail&bno=${board.bno}" class="btn btn-secondary">취소</a>
            <button type="submit" class="btn btn-primary">수정 완료</button>
        </div>
    </form>
</div>
<%@ include file="/fragments/footer.jsp"%>
</body>
</html>