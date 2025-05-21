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