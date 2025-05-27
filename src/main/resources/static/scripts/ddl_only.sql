-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: ssafytrip
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attractions`
--

DROP TABLE IF EXISTS `attractions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attractions` (
  `no` int NOT NULL AUTO_INCREMENT COMMENT '명소코드',
  `content_id` int DEFAULT NULL COMMENT '콘텐츠번호',
  `title` varchar(500) DEFAULT NULL COMMENT '명소이름',
  `content_type_id` int DEFAULT NULL COMMENT '콘텐츠타입',
  `area_code` int DEFAULT NULL COMMENT '시도코드',
  `si_gun_gu_code` int DEFAULT NULL COMMENT '구군코드',
  `first_image1` varchar(100) DEFAULT NULL COMMENT '이미지경로1',
  `first_image2` varchar(100) DEFAULT NULL COMMENT '이미지경로2',
  `map_level` int DEFAULT NULL COMMENT '줌레벨',
  `latitude` decimal(20,17) DEFAULT NULL COMMENT '위도',
  `longitude` decimal(20,17) DEFAULT NULL COMMENT '경도',
  `tel` varchar(20) DEFAULT NULL COMMENT '전화번호',
  `addr1` varchar(100) DEFAULT NULL COMMENT '주소1',
  `addr2` varchar(100) DEFAULT NULL COMMENT '주소2',
  `homepage` varchar(1000) DEFAULT NULL COMMENT '홈페이지',
  `overview` varchar(10000) DEFAULT NULL COMMENT '설명',
  `view_cnt` int DEFAULT '0',
  PRIMARY KEY (`no`),
  KEY `attractions_typeid_to_types_typeid_fk_idx` (`content_type_id`),
  KEY `attractions_sido_to_sidos_code_fk_idx` (`area_code`),
  KEY `attractions_sigungu_to_guguns_gugun_fk_idx` (`si_gun_gu_code`),
  CONSTRAINT `attractions_area_to_sidos_code_fk` FOREIGN KEY (`area_code`) REFERENCES `sidos` (`sido_code`),
  CONSTRAINT `attractions_sigungu_to_guguns_gugun_fk` FOREIGN KEY (`si_gun_gu_code`) REFERENCES `guguns` (`gugun_code`),
  CONSTRAINT `attractions_typeid_to_types_typeid_fk` FOREIGN KEY (`content_type_id`) REFERENCES `contenttypes` (`content_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=107559 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='명소정보테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `board`
--

DROP TABLE IF EXISTS `board`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `board` (
  `bno` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `writer` varchar(50) NOT NULL,
  `reg_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `view_cnt` int DEFAULT '0',
  PRIMARY KEY (`bno`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='댓글 테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contenttypes`
--

DROP TABLE IF EXISTS `contenttypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contenttypes` (
  `content_type_id` int NOT NULL COMMENT '콘텐츠타입번호',
  `content_type_name` varchar(45) DEFAULT NULL COMMENT '콘텐츠타입이름',
  PRIMARY KEY (`content_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='콘텐츠타입정보테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guguns`
--

DROP TABLE IF EXISTS `guguns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guguns` (
  `no` int NOT NULL AUTO_INCREMENT COMMENT '구군번호',
  `sido_code` int NOT NULL COMMENT '시도코드',
  `gugun_code` int NOT NULL COMMENT '구군코드',
  `gugun_name` varchar(20) DEFAULT NULL COMMENT '구군이름',
  PRIMARY KEY (`no`),
  KEY `guguns_sido_to_sidos_cdoe_fk_idx` (`sido_code`),
  KEY `gugun_code_idx` (`gugun_code`),
  CONSTRAINT `guguns_sido_to_sidos_cdoe_fk` FOREIGN KEY (`sido_code`) REFERENCES `sidos` (`sido_code`)
) ENGINE=InnoDB AUTO_INCREMENT=469 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='구군정보테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `member`
--

DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member` (
  `mno` int NOT NULL AUTO_INCREMENT,
  `id` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(20) NOT NULL,
  `role` varchar(20) DEFAULT 'member',
  `address` varchar(100) NOT NULL,
  `tel` varchar(13) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mno` (`mno`),
  UNIQUE KEY `tel` (`tel`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notice`
--

DROP TABLE IF EXISTS `notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notice` (
  `nno` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `writer` varchar(50) NOT NULL,
  `reg_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `view_cnt` int DEFAULT '0',
  `is_important` tinyint(1) DEFAULT '0',
  `priority` int DEFAULT '0',
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  PRIMARY KEY (`nno`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `plan_board`
--

DROP TABLE IF EXISTS `plan_board`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plan_board` (
  `pboard_no` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '게시글 제목',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '게시글 내용',
  `writer` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '작성자 (member.name 참조)',
  `user_id` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '작성자 ID (member.id 참조)',
  `reg_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
  `update_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
  `view_cnt` int DEFAULT '0' COMMENT '조회수',
  `plan_id` int DEFAULT NULL COMMENT '연결된 여행 계획 ID (travel_plan 테이블 참조)',
  `travel_title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '여행 제목 (travel_plan.title 복사)',
  `start_date` date DEFAULT NULL COMMENT '여행 시작일 (travel_plan.start_date 복사)',
  `end_date` date DEFAULT NULL COMMENT '여행 종료일 (travel_plan.end_date 복사)',
  `travel_destinations` text COLLATE utf8mb4_unicode_ci COMMENT '주요 여행지 (JSON 또는 콤마 구분 문자열)',
  `travel_theme` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '여행 테마 (가족여행, 혼자여행, 커플여행, 친구여행 등)',
  `estimated_budget` int DEFAULT NULL COMMENT '총 예상 비용',
  `participant_count` int DEFAULT '1' COMMENT '여행 인원수',
  `travel_duration` int DEFAULT NULL COMMENT '여행 일수 (계산된 값)',
  `is_public` tinyint(1) DEFAULT '1' COMMENT '공개 여부',
  `is_featured` tinyint(1) DEFAULT '0' COMMENT '추천 게시글 여부',
  `like_count` int DEFAULT '0' COMMENT '좋아요 수',
  `comment_count` int DEFAULT '0' COMMENT '댓글 수',
  `thumbnail_image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '대표 이미지 URL',
  `attachment_files` text COLLATE utf8mb4_unicode_ci COMMENT '첨부 파일 목록 (JSON 형태)',
  PRIMARY KEY (`pboard_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_writer` (`writer`),
  KEY `idx_reg_date` (`reg_date` DESC),
  KEY `idx_view_cnt` (`view_cnt` DESC),
  KEY `idx_like_count` (`like_count` DESC),
  KEY `idx_travel_theme` (`travel_theme`),
  KEY `idx_is_featured` (`is_featured`),
  KEY `idx_is_public` (`is_public`),
  KEY `idx_start_date` (`start_date`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='여행 공유 게시판';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `plan_board_comment`
--

DROP TABLE IF EXISTS `plan_board_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plan_board_comment` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `pboard_no` int NOT NULL COMMENT '게시글 번호',
  `user_id` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '댓글 작성자 ID',
  `writer` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '댓글 작성자 이름',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '댓글 내용',
  `parent_comment_id` int DEFAULT NULL COMMENT '부모 댓글 ID (대댓글인 경우)',
  `reg_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
  `update_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '삭제 여부',
  PRIMARY KEY (`comment_id`),
  KEY `idx_pboard_no` (`pboard_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_reg_date` (`reg_date`),
  KEY `idx_parent_comment_id` (`parent_comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='여행 게시판 댓글';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`ssafy`@`%`*/ /*!50003 TRIGGER `update_comment_count_after_insert` AFTER INSERT ON `plan_board_comment` FOR EACH ROW BEGIN
    IF NEW.is_deleted = FALSE THEN
        UPDATE plan_board 
        SET comment_count = comment_count + 1 
        WHERE pboard_no = NEW.pboard_no;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`ssafy`@`%`*/ /*!50003 TRIGGER `update_comment_count_after_update` AFTER UPDATE ON `plan_board_comment` FOR EACH ROW BEGIN
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
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `plan_board_like`
--

DROP TABLE IF EXISTS `plan_board_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plan_board_like` (
  `like_id` int NOT NULL AUTO_INCREMENT,
  `pboard_no` int NOT NULL COMMENT '게시글 번호',
  `user_id` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '사용자 ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '좋아요 누른 시간',
  PRIMARY KEY (`like_id`),
  UNIQUE KEY `unique_like` (`pboard_no`,`user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='여행 게시판 좋아요';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`ssafy`@`%`*/ /*!50003 TRIGGER `update_like_count_after_insert` AFTER INSERT ON `plan_board_like` FOR EACH ROW BEGIN
    UPDATE plan_board 
    SET like_count = like_count + 1 
    WHERE pboard_no = NEW.pboard_no;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`ssafy`@`%`*/ /*!50003 TRIGGER `update_like_count_after_delete` AFTER DELETE ON `plan_board_like` FOR EACH ROW BEGIN
    UPDATE plan_board 
    SET like_count = like_count - 1 
    WHERE pboard_no = OLD.pboard_no;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `plan_board_tag`
--

DROP TABLE IF EXISTS `plan_board_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plan_board_tag` (
  `tag_id` int NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '태그명',
  `use_count` int DEFAULT '0' COMMENT '사용 횟수',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `tag_name` (`tag_name`),
  KEY `idx_tag_name` (`tag_name`),
  KEY `idx_use_count` (`use_count` DESC)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='여행 게시판 태그';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `plan_board_tag_relation`
--

DROP TABLE IF EXISTS `plan_board_tag_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plan_board_tag_relation` (
  `relation_id` int NOT NULL AUTO_INCREMENT,
  `pboard_no` int NOT NULL COMMENT '게시글 번호',
  `tag_id` int NOT NULL COMMENT '태그 ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '연결 생성일',
  PRIMARY KEY (`relation_id`),
  UNIQUE KEY `unique_relation` (`pboard_no`,`tag_id`),
  KEY `idx_pboard_no` (`pboard_no`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='여행 게시판-태그 연결';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`ssafy`@`%`*/ /*!50003 TRIGGER `update_tag_use_count_after_insert` AFTER INSERT ON `plan_board_tag_relation` FOR EACH ROW BEGIN
    UPDATE plan_board_tag 
    SET use_count = use_count + 1 
    WHERE tag_id = NEW.tag_id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`ssafy`@`%`*/ /*!50003 TRIGGER `update_tag_use_count_after_delete` AFTER DELETE ON `plan_board_tag_relation` FOR EACH ROW BEGIN
    UPDATE plan_board_tag 
    SET use_count = use_count - 1 
    WHERE tag_id = OLD.tag_id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `plan_detail`
--

DROP TABLE IF EXISTS `plan_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plan_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT,
  `plan_id` int NOT NULL,
  `day_number` int NOT NULL,
  `attraction_id` int DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `description` text,
  `visit_time` time DEFAULT NULL,
  `stay_duration` int DEFAULT NULL,
  `order_no` int NOT NULL,
  PRIMARY KEY (`detail_id`),
  KEY `plan_id` (`plan_id`),
  KEY `attraction_id` (`attraction_id`),
  CONSTRAINT `plan_detail_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `travel_plan` (`plan_id`) ON DELETE CASCADE,
  CONSTRAINT `plan_detail_ibfk_2` FOREIGN KEY (`attraction_id`) REFERENCES `attractions` (`no`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sidos`
--

DROP TABLE IF EXISTS `sidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sidos` (
  `no` int NOT NULL AUTO_INCREMENT COMMENT '시도번호',
  `sido_code` int NOT NULL COMMENT '시도코드',
  `sido_name` varchar(20) DEFAULT NULL COMMENT '시도이름',
  PRIMARY KEY (`no`),
  UNIQUE KEY `sido_code_UNIQUE` (`sido_code`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='시도정보테이블';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `travel_plan`
--

DROP TABLE IF EXISTS `travel_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `travel_plan` (
  `plan_id` int NOT NULL AUTO_INCREMENT,
  `user_id` varchar(50) NOT NULL,
  `title` varchar(100) NOT NULL,
  `description` text,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`plan_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `travel_plan_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-27 16:20:51
