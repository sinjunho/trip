<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Enjoy Trip - 회원가입</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <%@ include file="/WEB-INF/views/fragments/header.jsp"%>
<link rel="stylesheet" href="${root}/css/member.css">
</head>
<body>


    <div class="auth-wrapper">
        <div class="auth-box">
            <div class="auth-image">
                <div class="image-text">
                    <h2>환영합니다!</h2>
                    <p>회원가입하고 나만의 여행 계획을 만들어보세요. Enjoy Trip과 함께라면 특별한 여행이 기다립니다.</p>
                </div>
            </div>
            
            <div class="auth-form">
                <div class="auth-header">
                    <h1>회원가입</h1>
                    <p>Enjoy Trip 서비스를 이용하기 위한 계정을 만들어보세요</p>
                </div>
                
                <form action="${root}/member/regist" method="post">                   
                    <div class="form-row">
                        <div class="form-col">
                            <div class="form-group">
                                <label for="name" class="form-label">이름</label>
                                <div class="form-input-group">
                                    <i class="fas fa-user"></i>
                                    <input type="text" name="name" id="name" class="form-input" placeholder="이름을 입력하세요" required />
                                </div>
                            </div>
                        </div>
                        
                        <div class="form-col">
                            <div class="form-group">
                                <label for="id" class="form-label">아이디</label>
                                <div class="form-input-group">
                                    <i class="fas fa-id-card"></i>
                                    <input type="text" name="id" id="id" class="form-input" placeholder="사용할 아이디를 입력하세요" required />
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="password" class="form-label">비밀번호</label>
                        <div class="form-input-group">
                            <i class="fas fa-lock"></i>
                            <input type="password" name="password" id="password" class="form-input" placeholder="비밀번호를 입력하세요" required />
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="address" class="form-label">주소</label>
                        <div class="form-input-group">
                            <i class="fas fa-home"></i>
                            <input type="text" name="address" id="address" class="form-input" placeholder="주소를 입력하세요" required />
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="tel" class="form-label">전화번호</label>
                        <div class="form-input-group">
                            <i class="fas fa-phone"></i>
                            <input type="tel" name="tel" id="tel" class="form-input" placeholder="전화번호를 입력하세요 (예: 010-1234-5678)" required />
                        </div>
                    </div>
                    
                    <button type="submit" class="auth-btn">회원가입</button>
                </form>
                
                <div class="divider">
                    <span>또는</span>
                </div>
                
                <div class="social-login">
                    <a href="#" class="social-btn"><i class="fab fa-google"></i></a>
                    <a href="#" class="social-btn"><i class="fab fa-facebook-f"></i></a>
                    <a href="#" class="social-btn"><i class="fab fa-kakao">K</i></a>
                </div>
                
                <c:if test="${!empty error}">
                    <div class="alert-error">
                        <i class="fas fa-exclamation-circle"></i> ${error}
                    </div>
                </c:if>
                
                <div class="auth-footer">
                    <p>이미 계정이 있으신가요? <a href="${root}/member?action=login-member-form">로그인</a></p>
                </div>
            </div>
        </div>
    </div>
	<%@ include file="/WEB-INF/views/fragments/footer.jsp"%>
</body>
</html>