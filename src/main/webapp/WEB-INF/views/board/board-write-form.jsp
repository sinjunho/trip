<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 작성</title>
</head>
<body>
    <%@ include file="/WEB-INF/views/fragments/header.jsp"%>

    <h1>게시글 작성</h1>
    <form action="${root}/board" method="post" class="m-3">
        <input type="hidden" name="action" value="write" />

        <!-- 제목 입력 -->
        <div class="mb-3 row">
            <label for="title" class="col-sm-2 col-form-label">제목</label>
            <div class="col-sm-10">
                <input type="text" name="title" id="title" class="form-control" required />
            </div>
        </div>

        <!-- 내용 입력 -->
        <div class="mb-3 row">
            <label for="content" class="col-sm-2 col-form-label">내용</label>
            <div class="col-sm-10">
                <textarea name="content" id="content" class="form-control" rows="5" required></textarea>
            </div>
        </div>


        <button type="submit" class="btn btn-primary">작성 완료</button>
    </form>

    <!-- 오류 메시지 출력 -->
    <c:if test="${!empty error }">
        <div class="alert alert-danger" role="alert">${error }</div>
    </c:if>

    <%@ include file="/fragments/footer.jsp"%>
</body>
</html>
