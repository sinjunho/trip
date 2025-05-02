<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Enjoy Trip - 마이페이지</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
	<%@ include file="/WEB-INF/views/fragments/header.jsp"%>
<link rel="stylesheet" href="${root}/css/member.css">
<link rel="stylesheet" href="${root}/css/mypage.css">
</head>
<body>


    <div class="mypage-wrapper">
        <div class="mypage-container">
            <div class="mypage-header">
                <h1>${member.name}님의 마이페이지</h1>
            </div>
            
            <div class="user-info">
                <h2>회원 정보</h2>
                <ul class="info-list">
                    <li>
                        <i class="fas fa-user"></i>
                        <span class="info-label">이름</span>
                        <span>${member.name}</span>
                    </li>
                    <li>
                        <i class="fas fa-id-card"></i>
                        <span class="info-label">아이디</span>
                        <span>${member.id}</span>
                    </li>
                    <li>
                        <i class="fas fa-user-tag"></i>
                        <span class="info-label">역할</span>
                        <span>${member.role}</span>
                    </li>
                    <li>
                        <i class="fas fa-home"></i>
                        <span class="info-label">주소</span>
                        <span>${member.address}</span>
                    </li>
                    <li>
                        <i class="fas fa-phone"></i>
                        <span class="info-label">전화번호</span>
                        <span>${member.tel}</span>
                    </li>
                </ul>
            </div>
            
            <div class="form-section">
                <h2>회원 정보 수정</h2>
                <form action="${root}/member/modify" method="post">
                 
    
                    <div class="form-row">
                        <div class="form-col">
                            <div class="form-input-group">
                                <i class="fas fa-user"></i>
                                <input type="text" name="name" id="name" class="form-input" 
                                    placeholder="변경할 이름" value="${member.name}" required />
                            </div>
                        </div>
                        <div class="form-col">
                            <div class="form-input-group">
                                <i class="fas fa-lock"></i>
                                <input type="password" name="password" id="password" class="form-input"
                                    placeholder="변경할 비밀번호"  required />
                            </div>
                        </div>
                    </div>
    
                    <div class="form-row">
                        <div class="form-col">
                            <div class="form-input-group">
                                <i class="fas fa-home"></i>
                                <input type="text" name="address" id="address" class="form-input"
                                    placeholder="변경할 주소" value="${member.address}" required />
                            </div>
                        </div>
                        <div class="form-col">
                            <div class="form-input-group">
                                <i class="fas fa-phone"></i>
                                <input type="tel" name="tel" id="tel" class="form-input"
                                    placeholder="변경할 전화번호" value="${member.tel}" required />
                            </div>
                        </div>
                    </div>
                    
                    <div style="text-align: right;">
                        <button type="submit" class="btn-update">정보 수정</button>
                    </div>
                </form>
                
                <c:if test="${!empty error}">
                    <div class="alert-error">
                        <i class="fas fa-exclamation-circle"></i> ${error}
                    </div>
                </c:if>
            </div>
            
            <div class="form-section">
                <h2>회원 탈퇴</h2>
                <div class="delete-warning">
                    <i class="fas fa-exclamation-triangle"></i>
                    <strong>경고!</strong> 회원 탈퇴 시 모든 정보가 삭제되며 복구할 수 없습니다.
                </div>
                
                <form action="${root}/member/delete" method="post">
                 
    
                    <div class="form-row">
                        <div class="form-col">
                            <div class="form-input-group">
                                <i class="fas fa-lock"></i>
                                <input type="password" name="password" id="delete-password" class="form-input"
                                    placeholder="비밀번호" required />
                            </div>
                        </div>
                        <div class="form-col">
                            <div class="form-input-group">
                                <i class="fas fa-lock"></i>
                                <input type="password" name="password2" id="password2" class="form-input"
                                    placeholder="비밀번호 확인" required />
                            </div>
                        </div>
                    </div>
                    
                    <div style="text-align: right;">
                        <button type="submit" class="btn-danger">회원 탈퇴</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

	<%@ include file="/WEB-INF/views/fragments/footer.jsp"%>
</body>
</html>