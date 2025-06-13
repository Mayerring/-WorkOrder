-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: wos
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `company`
--
use wos;
DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公司ID',
                           `name` varchar(100) NOT NULL COMMENT '公司名称',
                           `code` varchar(50) DEFAULT NULL COMMENT '公司编码',
                           `parent_company_code` varchar(50) DEFAULT NULL COMMENT '上级公司编码',
                           `level` tinyint DEFAULT '1' COMMENT '公司层级（1：总部，2：省公司，3：市公司）',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `name` (`name`),
                           UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公司信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
INSERT INTO `company` VALUES (7,'总公司','91440000HBZRRFFRF2',NULL,0,'2025-06-07 16:32:01','2025-06-07 16:32:40'),(8,'广东省公司','91440000QUW6NMS6V0','91440000HBZRRFFRF2',1,'2025-06-07 16:32:52','2025-06-07 16:32:52'),(9,'广州市公司','9144000086Q3T0VX77','91440000QUW6NMS6V0',2,'2025-06-07 16:33:35','2025-06-07 16:33:35');
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
                              `name` varchar(100) NOT NULL COMMENT '部门名称',
                              `code` varchar(50) DEFAULT NULL COMMENT '部门编码',
                              `parent_department_code` varchar(50) DEFAULT NULL COMMENT '上级部门ID(null表示顶级）',
                              `company_code` varchar(50) NOT NULL COMMENT '所属公司编码',
                              `leader_number` varchar(50) DEFAULT NULL COMMENT '部门主管员工工号',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department`
--

LOCK TABLES `department` WRITE;
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
INSERT INTO `department` VALUES (2,'总务部','D91440000HBZRRFFRF2-0002',NULL,'91440000HBZRRFFRF2','10001','2025-06-07 19:27:47','2025-06-09 22:00:30'),(3,'技术部','D91440000HBZRRFFRF2-0003',NULL,'91440000HBZRRFFRF2','10002','2025-06-07 19:28:13','2025-06-09 22:00:30'),(4,'人力部','D91440000HBZRRFFRF2-0004',NULL,'91440000HBZRRFFRF2','10003','2025-06-07 19:28:43','2025-06-09 22:00:30'),(5,'人力部','D91440000QUW6NMS6V0-0005','D91440000HBZRRFFRF2-0004','91440000QUW6NMS6V0','10004','2025-06-07 19:28:53','2025-06-09 22:00:30'),(6,'总务部','D91440000QUW6NMS6V0-0006','D91440000HBZRRFFRF2-0002','91440000QUW6NMS6V0','10006','2025-06-07 19:28:59','2025-06-13 17:51:24'),(7,'技术部','D91440000QUW6NMS6V0-0007','D91440000HBZRRFFRF2-0003','91440000QUW6NMS6V0','10007','2025-06-07 19:29:04','2025-06-09 22:10:53'),(8,'技术部','D9144000086Q3T0VX77-0008','D91440000QUW6NMS6V0-0007','9144000086Q3T0VX77','10008','2025-06-07 19:29:16','2025-06-13 17:52:38'),(9,'人力部','D9144000086Q3T0VX77-0009','D91440000QUW6NMS6V0-0005','9144000086Q3T0VX77','10009','2025-06-07 19:29:23','2025-06-13 17:52:56'),(10,'总务部','D9144000086Q3T0VX77-0010','D91440000QUW6NMS6V0-0006','9144000086Q3T0VX77','10010','2025-06-07 19:29:28','2025-06-13 17:52:56'),(12,'运维部','D91440000HBZRRFFRF2-0011',NULL,'91440000HBZRRFFRF2','10011','2025-06-13 17:49:22','2025-06-13 17:52:56'),(13,'财务部','D91440000HBZRRFFRF2-0012',NULL,'91440000HBZRRFFRF2','10012','2025-06-13 17:49:22','2025-06-13 17:52:56');
/*!40000 ALTER TABLE `department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staff`
--

DROP TABLE IF EXISTS `staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff` (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                         `staff_number` varchar(128) NOT NULL COMMENT '工号',
                         `password` varchar(100) NOT NULL COMMENT '密码（加密）',
                         `name` varchar(100) NOT NULL COMMENT '姓名',
                         `company_code` varchar(50) NOT NULL COMMENT '所属公司编号',
                         `company` varchar(100) DEFAULT NULL COMMENT '所属公司',
                         `department_code` varchar(50) DEFAULT NULL COMMENT '部门编号',
                         `department` varchar(100) DEFAULT NULL COMMENT '部门',
                         `position` varchar(100) DEFAULT NULL COMMENT '职位',
                         `status` tinyint DEFAULT '0' COMMENT '状态：0 正常，1 休假，2 停职，3 离职',
                         `manager_number` varchar(50) DEFAULT NULL COMMENT '直属领导工号',
                         `manager_name` varchar(100) DEFAULT NULL COMMENT '直属领导姓名',
                         `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
                         `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `phone` (`phone`),
                         UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='职工表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff`
--

LOCK TABLES `staff` WRITE;
/*!40000 ALTER TABLE `staff` DISABLE KEYS */;
INSERT INTO `staff` VALUES (1,'10001','123456','张三','91440000HBZRRFFRF2','总公司','D91440000HBZRRFFRF2-0002','总务部','总经理',0,NULL,NULL,'13430066122','6122@example.com','2025-06-09 11:16:15','2025-06-09 21:15:32'),(4,'10002','123456','李四','91440000HBZRRFFRF2','总公司','D91440000HBZRRFFRF2-0003','技术部','技术主管',0,NULL,NULL,'12345678910','kong@example.com','2025-06-09 21:15:32','2025-06-09 21:15:32'),(5,'10003','123456','王五','91440000HBZRRFFRF2','总公司','D91440000HBZRRFFRF2-0004','人力部','人力总监',0,NULL,NULL,'32145632100','@example.com','2025-06-09 21:15:32','2025-06-09 21:15:32'),(6,'10004','123456','alice','91440000QUW6NMS6V0','广东省公司','D91440000QUW6NMS6V0-0005','人力部','人力总监',0,NULL,NULL,'12312312341','122@example.com','2025-06-09 21:18:00','2025-06-13 18:57:18'),(7,'10006','123456','BOB','91440000QUW6NMS6V0','广东省公司','D91440000QUW6NMS6V0-0006','总务部','总经理',0,NULL,NULL,'12312312342','22@example.com','2025-06-09 21:19:20','2025-06-13 18:57:18'),(8,'10007','123456','candy','91440000QUW6NMS6V0','广东省公司','D91440000QUW6NMS6V0-0007','技术部','技术主管',0,NULL,NULL,'12312312343','2@example.com','2025-06-09 21:20:21','2025-06-13 18:57:18'),(9,'10008','123456','123','9144000086Q3T0VX77','广州市公司','D9144000086Q3T0VX77-0008','技术部','技术主管',0,NULL,NULL,'12123123456','aaaaaa','2025-06-09 21:22:15','2025-06-13 18:57:18'),(10,'10009','123456','234','9144000086Q3T0VX77','广州市公司','D9144000086Q3T0VX77-0009','人力部','人力总监',0,NULL,NULL,'14725836912','bbbbbb','2025-06-09 21:23:25','2025-06-09 21:23:28'),(11,'10010','123456','345','9144000086Q3T0VX77','广州市公司','D9144000086Q3T0VX77-0010','总务部','总经理',0,NULL,NULL,'14725836913','ccccccc','2025-06-09 21:24:22','2025-06-13 18:57:18'),(12,'10011','123456','王六','91440000HBZRRFFRF2','总公司','D91440000HBZRRFFRF2-0011','运维部','维修部长',0,NULL,NULL,'14526363214','fix@example.com','2025-06-13 17:48:09','2025-06-13 17:48:09'),(13,'10012','123456','王七','91440000HBZRRFFRF2','总公司','D91440000HBZRRFFRF2-0012','财务部','财务部长',0,NULL,NULL,'14578541236','fortune@example.com','2025-06-13 17:48:09','2025-06-13 17:48:09');
/*!40000 ALTER TABLE `staff` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-13 19:00:12
