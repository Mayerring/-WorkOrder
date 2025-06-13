-- MySQL dump 10.13  Distrib 9.0.1, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: wos
-- ------------------------------------------------------
-- Server version	9.0.1

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
-- Table structure for table `company`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department`
--

LOCK TABLES `department` WRITE;
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
INSERT INTO `department` VALUES (2,'总务部','D91440000HBZRRFFRF2-0002',NULL,'91440000HBZRRFFRF2','10001','2025-06-07 19:27:47','2025-06-09 22:00:30'),(3,'技术部','D91440000HBZRRFFRF2-0003',NULL,'91440000HBZRRFFRF2','10002','2025-06-07 19:28:13','2025-06-09 22:00:30'),(4,'人力部','D91440000HBZRRFFRF2-0004',NULL,'91440000HBZRRFFRF2','10003','2025-06-07 19:28:43','2025-06-09 22:00:30'),(5,'人力部','D91440000QUW6NMS6V0-0005','D91440000HBZRRFFRF2-0004','91440000QUW6NMS6V0','10004','2025-06-07 19:28:53','2025-06-09 22:00:30'),(6,'总务部','D91440000QUW6NMS6V0-0006','D91440000HBZRRFFRF2-0002','91440000QUW6NMS6V0','10005','2025-06-07 19:28:59','2025-06-09 22:00:30'),(7,'技术部','D91440000QUW6NMS6V0-0007','D91440000HBZRRFFRF2-0003','91440000QUW6NMS6V0','10007','2025-06-07 19:29:04','2025-06-09 22:10:53'),(8,'技术部','D9144000086Q3T0VX77-0008','D91440000QUW6NMS6V0-0007','9144000086Q3T0VX77','10006','2025-06-07 19:29:16','2025-06-09 22:10:53'),(9,'人力部','D9144000086Q3T0VX77-0009','D91440000QUW6NMS6V0-0005','9144000086Q3T0VX77','10008','2025-06-07 19:29:23','2025-06-09 22:00:30'),(10,'总务部','D9144000086Q3T0VX77-0010','D91440000QUW6NMS6V0-0006','9144000086Q3T0VX77','10009','2025-06-07 19:29:28','2025-06-09 22:00:30');
/*!40000 ALTER TABLE `department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `handle_user_info`
--

DROP TABLE IF EXISTS `handle_user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `handle_user_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint NOT NULL COMMENT '工单id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `user_name` varchar(64) NOT NULL COMMENT '用户名',
  `handle_type` tinyint NOT NULL COMMENT '用户类型（1：提交，2：审核，3：派单，4：处理，5：确认）',
  `finished` tinyint NOT NULL COMMENT '已完成操作，0未完成，1已完成',
  `company_code` varchar(64) NOT NULL COMMENT '公司id',
  `company_name` varchar(128) NOT NULL COMMENT '公司名',
  `department_code` varchar(64) NOT NULL COMMENT '部门id',
  `department_name` varchar(128) NOT NULL COMMENT '部门名',
  `handle_time` bigint DEFAULT NULL COMMENT '处理时间',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除位',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工单操作信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `handle_user_info`
--

LOCK TABLES `handle_user_info` WRITE;
/*!40000 ALTER TABLE `handle_user_info` DISABLE KEYS */;
INSERT INTO `handle_user_info` VALUES (21,7,8,'candy',1,1,'91440000QUW6NMS6V0','广东省公司','D91440000QUW6NMS6V0-0007','技术部',1749553193,1749553193,1749553193,'算力不够啊',0),(22,7,4,'李四',3,1,'91440000HBZRRFFRF2','总公司','D91440000HBZRRFFRF2-0003','技术部',1749803961,1749553193,NULL,'小李，此事你牵头处理！',0),(23,7,4,'李四',5,1,'91440000HBZRRFFRF2','总公司','D91440000HBZRRFFRF2-0003','技术部',1749804864,1749553193,NULL,'做得坏！',0),(24,7,4,'李四',2,1,'91440000HBZRRFFRF2','总公司','D91440000HBZRRFFRF2-0003','技术部',1749554429,1749554429,1749554429,'我同意了',0),(25,7,4,'李四',4,1,'91440000HBZRRFFRF2','总公司','D91440000HBZRRFFRF2-0003','技术部',1749804624,1749803961,NULL,'我的部分完成啦！',0),(26,7,9,'123',4,1,'9144000086Q3T0VX77','广州市公司','D9144000086Q3T0VX77-0008','技术部',1749804766,1749804467,NULL,'我也做完啦！',0);
/*!40000 ALTER TABLE `handle_user_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `receiver_id` bigint NOT NULL COMMENT '接收人id',
  `sender_id` bigint NOT NULL COMMENT '发送人id',
  `type` int NOT NULL COMMENT '类型，',
  `content` text NOT NULL COMMENT '内容',
  `send_time` bigint DEFAULT NULL COMMENT '发送时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除位',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (39,2,1,400,'您收到编号为a的工单，请尽快处理。',1749398729,0),(40,1,2,400,'您收到编号为a的工单，请尽快处理。',1749469459,0),(41,1,2,400,'您收到编号为a的工单，请尽快处理。',1749469649,0),(42,1,1,400,'您收到编号为a的工单，请尽快处理。',1749469785,0),(43,1,1,400,'您收到编号为a的工单，请尽快处理。',1749470056,0),(44,1,1,400,'您收到编号为a的工单，请尽快处理。',1749470903,0),(45,1,1,400,'您收到编号为a的工单，请尽快处理。',1749470903,0),(46,1,1,400,'您收到编号为a的工单，请尽快处理。',1749470997,0),(47,1,1,400,'您收到编号为a的工单，请尽快处理。',1749470997,0),(48,1,1,400,'您收到编号为a的工单，请尽快处理。',1749470997,0),(49,1,1,400,'您收到编号为a的工单，请尽快处理。',1749470997,0),(50,1,1,400,'您收到编号为a的工单，请尽快处理。',1749471212,0),(51,1,1,400,'您收到编号为a的工单，请尽快处理。',1749471212,0),(52,2,1,400,'您收到编号为a的工单，请尽快处理。',1749471355,0),(53,1,1,400,'您收到编号为a的工单，请尽快处理。',1749471355,0),(54,2,2,500,'编号为a的工单需要您验收。',1749471833,0),(55,2,2,600,'您完成的编号为a的工单，已经验收成功。',1749471986,0),(56,1,2,600,'您完成的编号为a的工单，已经验收成功。',1749471986,0),(57,2,2,600,'您完成的编号为a的工单，已经验收成功。',1749472201,0),(58,1,2,600,'您完成的编号为a的工单，已经验收成功。',1749472201,0),(59,2,2,600,'您完成的编号为a的工单，已经验收成功。',1749472335,0),(60,1,2,600,'您完成的编号为a的工单，已经验收成功。',1749472335,0),(61,2,2,670,'您完成的编号为a的工单，验收失败，请查看。',1749472413,0),(62,1,2,670,'您完成的编号为a的工单，验收失败，请查看。',1749472413,0),(63,2,2,670,'您完成的编号为a的工单，验收失败，请查看。',1749472892,0),(64,1,2,670,'您完成的编号为a的工单，验收失败，请查看。',1749472892,0),(65,2,2,670,'您完成的编号为a的工单，验收失败，请查看。',1749472993,0),(66,2,1,500,'编号为a的工单需要您验收。',1749473046,0),(67,2,2,600,'您完成的编号为a的工单，已经验收成功。',1749473236,0),(68,1,2,600,'您完成的编号为a的工单，已经验收成功。',1749473236,0),(69,8,8,100,'编号为WO202506101932392268711464960的工单已成功创建，等待审核',1749553193,0),(70,4,1,200,'编号为WO202506101932392268711464960的工单需要您审核',1749554429,0),(71,4,4,400,'您收到编号为WO202506101932392268711464960的工单，请尽快处理。',1749803961,0),(72,9,4,400,'您收到编号为WO202506101932392268711464960的工单，请尽快处理。',1749804467,0),(73,4,4,400,'您收到编号为WO202506101932392268711464960的工单，请尽快处理。',1749804624,0),(74,4,4,400,'您收到编号为WO202506101932392268711464960的工单，请尽快处理。',1749804643,0),(75,9,4,400,'您收到编号为WO202506101932392268711464960的工单，请尽快处理。',1749804643,0),(76,4,9,500,'编号为WO202506101932392268711464960的工单需要您验收。',1749804766,0),(77,4,4,600,'您完成的编号为WO202506101932392268711464960的工单，已经验收成功。',1749804864,0),(78,9,4,600,'您完成的编号为WO202506101932392268711464960的工单，已经验收成功。',1749804864,0);
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='职工表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff`
--

LOCK TABLES `staff` WRITE;
/*!40000 ALTER TABLE `staff` DISABLE KEYS */;
INSERT INTO `staff` VALUES (4,'10002','123456','李四','91440000HBZRRFFRF2','总公司','D91440000HBZRRFFRF2-0003','技术部','技术主管',0,NULL,NULL,'12345678910','kong@example.com','2025-06-09 21:15:32','2025-06-09 21:15:32'),(5,'10003','123456','王五','91440000HBZRRFFRF2','总公司','D91440000HBZRRFFRF2-0004','人力部','人力总监',0,NULL,NULL,'32145632100','@example.com','2025-06-09 21:15:32','2025-06-09 21:15:32'),(6,'10004','123456','alice','91440000QUW6NMS6V0','广东省公司','D91440000QUW6NMS6V0-0005','人力部','人力总监',0,NULL,NULL,'1231231234','122@example.com','2025-06-09 21:18:00','2025-06-09 21:18:00'),(7,'10006','123456','BOB','91440000QUW6NMS6V0','广东省公司','D91440000QUW6NMS6V0-0006','总务部','总经理',0,NULL,NULL,'1231231324','22@example.com','2025-06-09 21:19:20','2025-06-09 21:20:55'),(8,'10007','123456','candy','91440000QUW6NMS6V0','广东省公司','D91440000QUW6NMS6V0-0007','技术部','技术主管',0,NULL,NULL,'1425366325','2@example.com','2025-06-09 21:20:21','2025-06-09 22:06:07'),(9,'10008','123456','123','9144000086Q3T0VX77','广州市公司','D9144000086Q3T0VX77-0008','技术部','技术主管',0,NULL,NULL,'1212312345','aaaaaa','2025-06-09 21:22:15','2025-06-09 21:22:18'),(10,'10009','123456','234','9144000086Q3T0VX77','广州市公司','D9144000086Q3T0VX77-0009','人力部','人力总监',0,NULL,NULL,'14725836912','bbbbbb','2025-06-09 21:23:25','2025-06-09 21:23:28'),(11,'10010','123456','345','9144000086Q3T0VX77','广州市公司','D9144000086Q3T0VX77-0010','总务部','总经理',0,NULL,NULL,'1425367541','ccccccc','2025-06-09 21:24:22','2025-06-09 21:24:22');
/*!40000 ALTER TABLE `staff` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_order`
--

DROP TABLE IF EXISTS `work_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(64) NOT NULL COMMENT '编号',
  `type` tinyint NOT NULL COMMENT '类型',
  `title` varchar(128) DEFAULT NULL COMMENT '工单标题',
  `priority_level` tinyint DEFAULT NULL COMMENT '优先级',
  `status` int NOT NULL COMMENT '状态',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint DEFAULT NULL COMMENT '更新时间',
  `cancel_time` bigint DEFAULT NULL COMMENT '取消时间',
  `delete_time` bigint DEFAULT NULL COMMENT '删除时间',
  `deadline_time` bigint DEFAULT NULL COMMENT '截止时间',
  `content` text COMMENT '详情',
  `accessory_url` varchar(128) DEFAULT NULL COMMENT '附件url',
  `accessory_name` varchar(128) DEFAULT NULL COMMENT '附件名称',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除位',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_order`
--

LOCK TABLES `work_order` WRITE;
/*!40000 ALTER TABLE `work_order` DISABLE KEYS */;
INSERT INTO `work_order` VALUES (1,'a',1,'s',1,410,1748786909,1748962756,1748786909,1748786909,1748786909,'123','a','b',0),(7,'WO202506101932392268711464960',0,'租服务器',0,600,1749553193,1749554429,NULL,NULL,NULL,'算力不够啊',NULL,NULL,0);
/*!40000 ALTER TABLE `work_order` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-13 20:22:31
