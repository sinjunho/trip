-- 여행 공유 게시판 테이블 생성 (기존 스키마에 맞춰 수정)
-- member.id: varchar(20), member.name: varchar(20)
-- travel_plan.user_id: varchar(50)

CREATE TABLE IF NOT EXISTS plan_board (
    -- 기본 게시판 정보
    pboard_no INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '게시글 제목',
    content TEXT NOT NULL COMMENT '게시글 내용',
    writer VARCHAR(20) NOT NULL COMMENT '작성자 (member.name 참조)',
    user_id VARCHAR(20) NOT NULL COMMENT '작성자 ID (member.id 참조)',
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    view_cnt INT DEFAULT 0 COMMENT '조회수',
    
    -- 여행 계획 연결 정보
    plan_id INT COMMENT '연결된 여행 계획 ID (travel_plan 테이블 참조)',
    
    -- 여행 정보 요약 (travel_plan에서 가져올 수 있지만 성능을 위해 비정규화)
    travel_title VARCHAR(100) COMMENT '여행 제목 (travel_plan.title 복사)',
    start_date DATE COMMENT '여행 시작일 (travel_plan.start_date 복사)',
    end_date DATE COMMENT '여행 종료일 (travel_plan.end_date 복사)',
    
    -- 추가 여행 정보
    travel_destinations TEXT COMMENT '주요 여행지 (JSON 또는 콤마 구분 문자열)',
    travel_theme VARCHAR(50) COMMENT '여행 테마 (가족여행, 혼자여행, 커플여행, 친구여행 등)',
    estimated_budget INT COMMENT '총 예상 비용',
    participant_count INT DEFAULT 1 COMMENT '여행 인원수',
    travel_duration INT COMMENT '여행 일수 (계산된 값)',
    
    -- 게시판 기능
    is_public BOOLEAN DEFAULT TRUE COMMENT '공개 여부',
    is_featured BOOLEAN DEFAULT FALSE COMMENT '추천 게시글 여부',
    like_count INT DEFAULT 0 COMMENT '좋아요 수',
    comment_count INT DEFAULT 0 COMMENT '댓글 수',
    
    -- 첨부 파일
    thumbnail_image VARCHAR(500) COMMENT '대표 이미지 URL',
    attachment_files TEXT COMMENT '첨부 파일 목록 (JSON 형태)',
    
    -- 인덱스
    INDEX idx_user_id (user_id),
    INDEX idx_writer (writer),
    INDEX idx_reg_date (reg_date DESC),
    INDEX idx_view_cnt (view_cnt DESC),
    INDEX idx_like_count (like_count DESC),
    INDEX idx_travel_theme (travel_theme),
    INDEX idx_is_featured (is_featured),
    INDEX idx_is_public (is_public),
    INDEX idx_start_date (start_date)
    
    -- 외래키는 나중에 추가 (호환성 문제로 인해)
    -- FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE,
    -- FOREIGN KEY (plan_id) REFERENCES travel_plan(plan_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='여행 공유 게시판';

-- 여행 공유 게시판 좋아요 테이블
CREATE TABLE IF NOT EXISTS plan_board_like (
    like_id INT AUTO_INCREMENT PRIMARY KEY,
    pboard_no INT NOT NULL COMMENT '게시글 번호',
    user_id VARCHAR(20) NOT NULL COMMENT '사용자 ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '좋아요 누른 시간',
    
    UNIQUE KEY unique_like (pboard_no, user_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
    
    -- 외래키는 나중에 추가
    -- FOREIGN KEY (pboard_no) REFERENCES plan_board(pboard_no) ON DELETE CASCADE,
    -- FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='여행 게시판 좋아요';

-- 여행 공유 게시판 댓글 테이블
CREATE TABLE IF NOT EXISTS plan_board_comment (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    pboard_no INT NOT NULL COMMENT '게시글 번호',
    user_id VARCHAR(20) NOT NULL COMMENT '댓글 작성자 ID',
    writer VARCHAR(20) NOT NULL COMMENT '댓글 작성자 이름',
    content TEXT NOT NULL COMMENT '댓글 내용',
    parent_comment_id INT DEFAULT NULL COMMENT '부모 댓글 ID (대댓글인 경우)',
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '삭제 여부',
    
    INDEX idx_pboard_no (pboard_no),
    INDEX idx_user_id (user_id),
    INDEX idx_reg_date (reg_date),
    INDEX idx_parent_comment_id (parent_comment_id)
    
    -- 외래키는 나중에 추가
    -- FOREIGN KEY (pboard_no) REFERENCES plan_board(pboard_no) ON DELETE CASCADE,
    -- FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE,
    -- FOREIGN KEY (parent_comment_id) REFERENCES plan_board_comment(comment_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='여행 게시판 댓글';

-- 여행 공유 게시판 태그 테이블
CREATE TABLE plan_board_tag (
    tag_id INT AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(30) NOT NULL UNIQUE COMMENT '태그명',
    use_count INT DEFAULT 0 COMMENT '사용 횟수',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    
    INDEX idx_tag_name (tag_name),
    INDEX idx_use_count (use_count DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='여행 게시판 태그';

-- 여행 공유 게시판-태그 연결 테이블
CREATE TABLE plan_board_tag_relation (
    relation_id INT AUTO_INCREMENT PRIMARY KEY,
    pboard_no INT NOT NULL COMMENT '게시글 번호',
    tag_id INT NOT NULL COMMENT '태그 ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '연결 생성일',
    
    UNIQUE KEY unique_relation (pboard_no, tag_id),
    INDEX idx_pboard_no (pboard_no),
    INDEX idx_tag_id (tag_id)
    
    -- 외래키는 나중에 추가
    -- FOREIGN KEY (pboard_no) REFERENCES plan_board(pboard_no) ON DELETE CASCADE,
    -- FOREIGN KEY (tag_id) REFERENCES plan_board_tag(tag_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='여행 게시판-태그 연결';

-- 댓글 수 업데이트 트리거 (댓글 추가 시)
DELIMITER $$
CREATE TRIGGER update_comment_count_after_insert
AFTER INSERT ON plan_board_comment
FOR EACH ROW
BEGIN
    IF NEW.is_deleted = FALSE THEN
        UPDATE plan_board 
        SET comment_count = comment_count + 1 
        WHERE pboard_no = NEW.pboard_no;
    END IF;
END$$

-- 댓글 수 업데이트 트리거 (댓글 삭제/복구 시)
CREATE TRIGGER update_comment_count_after_update
AFTER UPDATE ON plan_board_comment
FOR EACH ROW
BEGIN
    IF OLD.is_deleted = FALSE AND NEW.is_deleted = TRUE THEN
        -- 댓글 삭제 시
        UPDATE plan_board 
        SET comment_count = comment_count - 1 
        WHERE pboard_no = NEW.pboard_no;
    ELSEIF OLD.is_deleted = TRUE AND NEW.is_deleted = FALSE THEN
        -- 댓글 복구 시
        UPDATE plan_board 
        SET comment_count = comment_count + 1 
        WHERE pboard_no = NEW.pboard_no;
    END IF;
END$$

-- 좋아요 수 업데이트 트리거 (좋아요 추가 시)
CREATE TRIGGER update_like_count_after_insert
AFTER INSERT ON plan_board_like
FOR EACH ROW
BEGIN
    UPDATE plan_board 
    SET like_count = like_count + 1 
    WHERE pboard_no = NEW.pboard_no;
END$$

-- 좋아요 수 업데이트 트리거 (좋아요 삭제 시)
CREATE TRIGGER update_like_count_after_delete
AFTER DELETE ON plan_board_like
FOR EACH ROW
BEGIN
    UPDATE plan_board 
    SET like_count = like_count - 1 
    WHERE pboard_no = OLD.pboard_no;
END$$

-- 태그 사용 횟수 업데이트 트리거 (태그 연결 시)
CREATE TRIGGER update_tag_use_count_after_insert
AFTER INSERT ON plan_board_tag_relation
FOR EACH ROW
BEGIN
    UPDATE plan_board_tag 
    SET use_count = use_count + 1 
    WHERE tag_id = NEW.tag_id;
END$$

-- 태그 사용 횟수 업데이트 트리거 (태그 연결 해제 시)
CREATE TRIGGER update_tag_use_count_after_delete
AFTER DELETE ON plan_board_tag_relation
FOR EACH ROW
BEGIN
    UPDATE plan_board_tag 
    SET use_count = use_count - 1 
    WHERE tag_id = OLD.tag_id;
END$$

DELIMITER ;

-- 외래키 제약조건을 나중에 추가 (테이블 생성 후)
-- 먼저 정확한 데이터 타입과 문자셋을 확인한 후 추가하세요

/*
-- 외래키 추가 예시 (필요시 실행)
ALTER TABLE plan_board 
ADD CONSTRAINT fk_plan_board_user 
FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE;

ALTER TABLE plan_board 
ADD CONSTRAINT fk_plan_board_plan 
FOREIGN KEY (plan_id) REFERENCES travel_plan(plan_id) ON DELETE SET NULL;

ALTER TABLE plan_board_like 
ADD CONSTRAINT fk_like_board 
FOREIGN KEY (pboard_no) REFERENCES plan_board(pboard_no) ON DELETE CASCADE;

ALTER TABLE plan_board_like 
ADD CONSTRAINT fk_like_user 
FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE;

ALTER TABLE plan_board_comment 
ADD CONSTRAINT fk_comment_board 
FOREIGN KEY (pboard_no) REFERENCES plan_board(pboard_no) ON DELETE CASCADE;

ALTER TABLE plan_board_comment 
ADD CONSTRAINT fk_comment_user 
FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE;

ALTER TABLE plan_board_comment 
ADD CONSTRAINT fk_comment_parent 
FOREIGN KEY (parent_comment_id) REFERENCES plan_board_comment(comment_id) ON DELETE CASCADE;

ALTER TABLE plan_board_tag_relation 
ADD CONSTRAINT fk_tag_relation_board 
FOREIGN KEY (pboard_no) REFERENCES plan_board(pboard_no) ON DELETE CASCADE;

ALTER TABLE plan_board_tag_relation 
ADD CONSTRAINT fk_tag_relation_tag 
FOREIGN KEY (tag_id) REFERENCES plan_board_tag(tag_id) ON DELETE CASCADE;
*/

-- 초기 데이터 삽입 (샘플)
INSERT INTO plan_board (
    title, content, writer, user_id, plan_id, travel_title, start_date, end_date,
    travel_destinations, travel_theme, estimated_budget, participant_count, travel_duration,
    thumbnail_image
) VALUES 
(
    '제주도 3박4일 완벽 가이드 - 렌터카 여행',
    '제주도 3박4일 일정을 자세히 공유합니다. 렌터카로 섬 전체를 돌아다니며 숨은 명소들을 발견했어요! 성산일출봉, 한라산, 우도 등 필수 코스부터 로컬 맛집까지 모든 정보를 담았습니다.',
    '여행매니아', 'travel_user1', 1, '제주도 힐링 여행', '2024-03-01', '2024-03-04',
    '성산일출봉,한라산,우도,애월해안도로', '혼자여행', 800000, 1, 4,
    'https://example.com/jeju-thumbnail.jpg'
),
(
    '부산 2박3일 맛집 투어 완전정복',
    '부산 토박이 친구와 함께한 진짜 맛집 투어! 관광객들이 모르는 숨은 맛집들과 유명 맛집들을 비교 리뷰했습니다. 돼지국밥, 밀면, 씨앗호떡 등 부산 대표 음식들을 모두 맛봤어요.',
    '맛집헌터', 'foodie_user', 2, '부산 미식 탐방', '2024-02-15', '2024-02-17',
    '서면,광안리,자갈치시장,국제시장', '친구여행', 600000, 3, 3,
    'https://example.com/busan-food.jpg'
),
(
    '서울 데이트 코스 베스트 추천',
    '연인과 함께한 서울 2박3일 로맨틱 데이트 코스를 공유합니다. 한강 피크닉, 북촌 한옥마을 산책, 홍대 데이트 등 계절별로 추천하는 코스들을 정리했어요.',
    '커플여행러', 'couple_travel', 3, '서울 로맨틱 투어', '2024-02-20', '2024-02-22',
    '명동,홍대,강남,한강공원', '커플여행', 1200000, 2, 3,
    'https://example.com/seoul-couple.jpg'
);

-- 샘플 태그 데이터
INSERT INTO plan_board_tag (tag_name) VALUES 
('제주도'), ('렌터카'), ('힐링'), ('혼자여행'), ('성산일출봉'),
('부산'), ('맛집투어'), ('친구여행'), ('돼지국밥'), ('광안리'),
('서울'), ('데이트코스'), ('커플여행'), ('한강'), ('홍대');

-- 샘플 태그 연결
INSERT INTO plan_board_tag_relation (pboard_no, tag_id) VALUES
-- 제주도 게시글 태그
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
-- 부산 게시글 태그  
(2, 6), (2, 7), (2, 8), (2, 9), (2, 10),
-- 서울 게시글 태그
(3, 11), (3, 12), (3, 13), (3, 14), (3, 15);

-- 샘플 댓글 데이터
INSERT INTO plan_board_comment (pboard_no, user_id, writer, content) VALUES
(1, 'user123', '제주러버', '정말 유용한 정보네요! 다음 달에 제주 가는데 참고할게요 :)'),
(1, 'jeju_local', '제주토박이', '성산일출봉은 일찍 가시는 걸 추천해요. 해돋이가 정말 장관입니다!'),
(2, 'busan_food', '부산댁', '우와 진짜 맛집들만 골라서 가셨네요! 저도 그 돼지국밥집 가봤는데 최고였어요'),
(3, 'dating_pro', '데이트왕', '한강 피크닉 코스 너무 좋아보여요. 이번 주말에 가봐야겠어요!');

-- 샘플 좋아요 데이터
INSERT INTO plan_board_like (pboard_no, user_id) VALUES
(1, 'user123'), (1, 'jeju_local'), (1, 'travel_lover'),
(2, 'busan_food'), (2, 'foodie_kim'), 
(3, 'dating_pro'), (3, 'couple_kim'), (3, 'romance_user');

-- 댓글 테이블
CREATE TABLE `comment` (
  `comment_id` int NOT NULL AUTO_INCREMENT COMMENT '댓글 ID',
  `content_id` int NOT NULL COMMENT '컨텐츠 ID (게시글 번호 또는 관광지 번호)',
  `content_type` varchar(20) NOT NULL DEFAULT 'board' COMMENT '컨텐츠 타입 (board, attraction, plan 등)',
  `content` text NOT NULL COMMENT '댓글 내용',
  `writer` varchar(50) NOT NULL COMMENT '작성자',
  `reg_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
  `parent_id` int DEFAULT NULL COMMENT '부모 댓글 ID (대댓글용)',
  `depth` int NOT NULL DEFAULT '0' COMMENT '댓글 깊이 (0: 댓글, 1: 대댓글)',
  PRIMARY KEY (`comment_id`),
  KEY `idx_content` (`content_id`,`content_type`),
  KEY `fk_comment_parent` (`parent_id`),
  KEY `idx_comment_writer` (`writer`),
  KEY `idx_comment_reg_date` (`reg_date`),
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`comment_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='댓글 테이블'

-- 공지사항 테이블
CREATE TABLE notice (
    nno INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    writer VARCHAR(50) NOT NULL,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    view_cnt INT DEFAULT 0,
    is_important BOOLEAN DEFAULT FALSE,
    priority INT DEFAULT 0,
    start_date DATE,
    end_date DATE
);