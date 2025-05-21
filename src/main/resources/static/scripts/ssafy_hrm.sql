drop database ssafylive;
create database ssafylive;
use ssafylive;

# member table
CREATE TABLE `member` (
  `mno` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL unique,
  `password` VARCHAR(45) NOT NULL,
  `role` VARCHAR(45) NULL,
  `profile` blob,
  PRIMARY KEY (`mno`));
  
# address table
CREATE TABLE `address` (
  `ano` int NOT NULL AUTO_INCREMENT,
  `mno` int NOT NULL,
  `title` varchar(45) DEFAULT NULL,
  `address` varchar(100) NOT NULL,
  `detail_address` varchar(100) DEFAULT NULL,
  `x`  varchar(20) DEFAULT NULL,
  `y`  varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ano`),
  KEY `fk_member_idx` (`mno`),
  CONSTRAINT `fk_member` FOREIGN KEY (`mno`) REFERENCES `member` (`mno`)
);

insert into member (name, email, password, role) values ('관리자','admin@ssafy.com', '1234', 'admin');
insert into address (mno, title, address, detail_address) 
              values(1, '기본', '서울특별시 강남구 역삼동', '멀티캠퍼스'),
                    (1, '집','경상북도 울릉군 울릉읍','독도 안용복길 3' );

DELIMITER //
CREATE PROCEDURE insert_dummy_members()
BEGIN
    DECLARE i INT DEFAULT 1;
    
    WHILE i <= 100 DO
        INSERT INTO member (name, email, password)
        VALUES (
            LPAD(100-i, 3, '0'),  -- 001, 002, ..., 100
            CONCAT(LPAD(i, 3, '0'), '@ssafy.com'),  -- 001@ssafy.com, ...
            '1234'
        );
        SET i = i + 1;
    END WHILE;
END //
DELIMITER ;

-- 프로시저 실행
CALL insert_dummy_members();

-- 프로시저 삭제 (cleanup)
DROP PROCEDURE insert_dummy_members;

select * from member;

select * from address;
