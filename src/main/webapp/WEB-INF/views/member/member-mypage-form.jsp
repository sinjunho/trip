<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
                <h1 id="member-name">마이페이지</h1>
            </div>
            
            <div class="user-info">
                <h2>회원 정보</h2>
                <ul class="info-list">
                    <li>
                        <i class="fas fa-user"></i>
                        <span class="info-label">이름</span>
                        <span id="user-name"></span>
                    </li>
                    <li>
                        <i class="fas fa-id-card"></i>
                        <span class="info-label">아이디</span>
                        <span id="user-id"></span>
                    </li>
                    <li>
                        <i class="fas fa-user-tag"></i>
                        <span class="info-label">역할</span>
                        <span id="user-role"></span>
                    </li>
                    <li>
                        <i class="fas fa-home"></i>
                        <span class="info-label">주소</span>
                        <span id="user-address"></span>
                    </li>
                    <li>
                        <i class="fas fa-phone"></i>
                        <span class="info-label">전화번호</span>
                        <span id="user-tel"></span>
                    </li>
                </ul>
            </div>
            
            <div class="form-section">
                <h2>회원 정보 수정</h2>
                <form id="modify-form">
                    <div class="form-row">
                        <div class="form-col">
                            <div class="form-input-group">
                                <i class="fas fa-user"></i>
                                <input type="text" name="name" id="name" class="form-input" 
                                    placeholder="변경할 이름" required />
                            </div>
                        </div>
                        <div class="form-col">
                            <div class="form-input-group">
                                <i class="fas fa-lock"></i>
                                <input type="password" name="password" id="password" class="form-input"
                                    placeholder="변경할 비밀번호" required />
                            </div>
                        </div>
                    </div>
    
                    <div class="form-row">
                        <div class="form-col">
                            <div class="form-input-group">
                                <i class="fas fa-home"></i>
                                <input type="text" name="address" id="address" class="form-input"
                                    placeholder="변경할 주소" required />
                            </div>
                        </div>
                        <div class="form-col">
                            <div class="form-input-group">
                                <i class="fas fa-phone"></i>
                                <input type="tel" name="tel" id="tel" class="form-input"
                                    placeholder="변경할 전화번호" required />
                            </div>
                        </div>
                    </div>
                    
                    <div style="text-align: right;">
                        <button type="submit" class="btn-update">정보 수정</button>
                    </div>
                </form>
                
                <div id="update-error" class="alert-error" style="display: none">
                    <i class="fas fa-exclamation-circle"></i> <span id="update-error-text"></span>
                </div>
            </div>
            
            <div class="form-section">
                <h2>회원 탈퇴</h2>
                <div class="delete-warning">
                    <i class="fas fa-exclamation-triangle"></i>
                    <strong>경고!</strong> 회원 탈퇴 시 모든 정보가 삭제되며 복구할 수 없습니다.
                </div>
                
                <form id="delete-form">
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
                
                <div id="delete-error" class="alert-error" style="display: none">
                    <i class="fas fa-exclamation-circle"></i> <span id="delete-error-text"></span>
                </div>
            </div>
        </div>
    </div>

    <%@ include file="/WEB-INF/views/fragments/footer.jsp"%>
    
    <script src="${root}/js/auth.js"></script>
    <script>
        // 페이지 로드 시 사용자 정보 표시
        document.addEventListener('DOMContentLoaded', function() {
            // 로그인 상태 확인
            if (!isLoggedIn()) {
                alert('로그인이 필요한 페이지입니다.');
                window.location.href = '${root}/member/login';
                return;
            }
            
            const user = getCurrentUser();
            
            // 사용자 정보 표시
            document.getElementById('member-name').textContent = user.name + '님의 마이페이지';
            document.getElementById('user-name').textContent = user.name;
            document.getElementById('user-id').textContent = user.id;
            document.getElementById('user-role').textContent = user.role || '회원';
            document.getElementById('user-address').textContent = user.address;
            document.getElementById('user-tel').textContent = user.tel;
            
            // 폼 필드 초기값 설정
            document.getElementById('name').value = user.name;
            document.getElementById('address').value = user.address;
            document.getElementById('tel').value = user.tel;
        });
        
        // 회원 정보 수정 폼 제출 처리
        document.getElementById('modify-form').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const userData = {
                name: document.getElementById('name').value,
                password: document.getElementById('password').value,
                address: document.getElementById('address').value,
                tel: document.getElementById('tel').value
            };
            
            updateUserProfile(userData)
                .then(updatedUser => {
                    alert('회원 정보가 수정되었습니다.');
                    window.location.reload();
                })
                .catch(error => {
                    const errorElement = document.getElementById('update-error');
                    const errorText = document.getElementById('update-error-text');
                    errorElement.style.display = 'block';
                    errorText.textContent = '정보 수정에 실패했습니다. 다시 시도해주세요.';
                });
        });
        
        // 회원 탈퇴 폼 제출 처리
        document.getElementById('delete-form').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const password = document.getElementById('delete-password').value;
            const password2 = document.getElementById('password2').value;
            
            if (password !== password2) {
                const errorElement = document.getElementById('delete-error');
                const errorText = document.getElementById('delete-error-text');
                errorElement.style.display = 'block';
                errorText.textContent = '비밀번호가 일치하지 않습니다.';
                return;
            }
            
            if (confirm('정말 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
                deleteAccount(password)
                    .then(response => {
                        alert('회원 탈퇴가 완료되었습니다.');
                        window.location.href = '${root}/';
                    })
                    .catch(error => {
                        const errorElement = document.getElementById('delete-error');
                        const errorText = document.getElementById('delete-error-text');
                        errorElement.style.display = 'block';
                        errorText.textContent = '회원 탈퇴에 실패했습니다. 비밀번호를 확인해주세요.';
                    });
            }
        });
    </script>
</body>
</html>