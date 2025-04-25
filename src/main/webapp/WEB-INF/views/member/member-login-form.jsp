<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Enjoy Trip - 로그인</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
	<%@ include file="/WEB-INF/views/fragments/header.jsp"%>
<link rel="stylesheet" href="${root}/css/member.css">
</head>
<body>


    <div class="auth-wrapper">
        <div class="auth-box login-form">
            <div class="auth-image">
                <div class="image-text">
                    <h2>어서오세요!</h2>
                    <p>로그인하여 Enjoy Trip에서 제공하는 다양한 여행 정보와 맞춤형 서비스를 이용해보세요.</p>
                </div>
            </div>
            
            <div class="auth-form">
                <div class="auth-header">
                    <h1>로그인</h1>
                    <p>계정에 로그인하여 Enjoy Trip을 시작하세요</p>
                </div>
                
                <form action="${root}/member/login" method="post">
                    
                    <div class="form-input-group">
                        <i class="fas fa-user"></i>
                        <input type="text" name="id" id="id" class="form-input" placeholder="아이디" required value="${cookie.rememberMe.value}" />
                    </div>
                    
                    <div class="form-input-group">
                        <i class="fas fa-lock"></i>
                        <input type="password" name="password" id="password" class="form-input" placeholder="비밀번호" required />
                    </div>
                    
                    <div class="form-options">
                        <div class="remember-me">
                            <input type="checkbox" value="on" name="remember-me" id="remember-me" ${cookie.rememberMe.value!=null?'checked':''} />
                            <label for="remember-me">아이디 기억하기</label>
                        </div>
                        <a href="member/member-password-select.jsp" class="forgot-password">비밀번호 찾기</a>
                    </div>
                    
                    <button type="submit" class="auth-btn">로그인</button>
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
                    <p>계정이 없으신가요? <a href="${root}/member/regist">회원가입</a></p>
                </div>
            </div>
        </div>
    </div>

	<%@ include file="/WEB-INF/views/fragments/footer.jsp"%>
</body>
</html>