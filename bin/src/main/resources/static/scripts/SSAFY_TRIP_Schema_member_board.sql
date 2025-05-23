DROP TABLE IF EXISTS `ssafytrip`.`member` ;
CREATE TABLE member (
  mno int NOT NULL AUTO_INCREMENT,
  id varchar(20) NOT NULL,
  password varchar(45) NOT NULL,
  name varchar(20) NOT NULL,
  role varchar(20) DEFAULT 'member',
  address varchar(100) NOT NULL,
  tel varchar(13) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mno` (`mno`),
  UNIQUE KEY `tel` (`tel`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `ssafytrip`.`board` ;
CREATE TABLE board (
    bno int NOT NULL AUTO_INCREMENT,
    title varchar(255) NOT NULL,
    content text NOT NULL,
    writer varchar(50) NOT NULL,
    reg_date timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    view_cnt int DEFAULT '0',
    PRIMARY KEY (bno)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 여행 계획 테이블
CREATE TABLE travel_plan (
  plan_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(50) NOT NULL,
  title VARCHAR(100) NOT NULL,
  description TEXT,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES member(id)
);

-- 여행 일정 상세 테이블
CREATE TABLE plan_detail (
  detail_id INT AUTO_INCREMENT PRIMARY KEY,
  plan_id INT NOT NULL,
  day_number INT NOT NULL,
  attraction_id INT,
  title VARCHAR(100),
  description TEXT,
  visit_time TIME,
  stay_duration INT,
  order_no INT NOT NULL,
  FOREIGN KEY (plan_id) REFERENCES travel_plan(plan_id) ON DELETE CASCADE,
  FOREIGN KEY (attraction_id) REFERENCES attractions(no)
);