-- MySQL dump 10.13  Distrib 5.7.29, for Linux (x86_64)
--
-- Host: localhost    Database: gift_me_five
-- ------------------------------------------------------
-- Server version	5.7.29-0ubuntu0.18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `gift_me_five`
--

/*!40000 DROP DATABASE IF EXISTS `gift_me_five`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `gift_me_five` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `gift_me_five`;

--
-- Table structure for table `giver_see_wishlist`
--

DROP TABLE IF EXISTS `giver_see_wishlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `giver_see_wishlist` (
  `user_id` bigint(20) NOT NULL,
  `wishlist_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`wishlist_id`),
  KEY `FKtjfxrvecq90uwwktkpmxbsbsd` (`wishlist_id`),
  CONSTRAINT `FKri67425we0dxvars2jtrhcc6m` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKtjfxrvecq90uwwktkpmxbsbsd` FOREIGN KEY (`wishlist_id`) REFERENCES `wishlist` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `giver_see_wishlist`
--

LOCK TABLES `giver_see_wishlist` WRITE;
/*!40000 ALTER TABLE `giver_see_wishlist` DISABLE KEYS */;
INSERT INTO `giver_see_wishlist` VALUES (4,2),(5,2),(3,3),(5,3),(4,4),(3,5),(5,5),(4,6),(5,6);
/*!40000 ALTER TABLE `giver_see_wishlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modify_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'2020-02-05 13:01:22','2020-02-05 13:01:22','admin'),(2,'2020-02-05 13:01:22','2020-02-05 13:01:22','unregistered'),(3,'2020-02-05 13:01:22','2020-02-05 13:01:22','registered'),(4,'2020-02-05 13:01:22','2020-02-05 13:01:22','pending');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `theme`
--

DROP TABLE IF EXISTS `theme`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `theme` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `background_picture` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `modify_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `theme`
--

LOCK TABLES `theme` WRITE;
/*!40000 ALTER TABLE `theme` DISABLE KEYS */;
INSERT INTO `theme` VALUES (1,'Theme_Picture_3_mod.png',NULL,'2020-02-05 12:39:23'),(2,'birthday.jpg',NULL,'2020-02-05 12:39:23'),(3,'wedding-rings.jpg',NULL,'2020-02-05 12:39:23'),(4,'celebration.jpg',NULL,'2020-02-05 12:39:23'),(5,'egg.jpg',NULL,'2020-02-05 12:39:23'),(6,'flower.jpg',NULL,'2020-02-05 12:39:23'),(7,'heartframe.jpg',NULL,'2020-02-05 12:39:23'),(8,'lucky-pig.png',NULL,'2020-02-05 12:39:23'),(9,'orchid.jpg',NULL,'2020-02-05 12:39:23'),(10,'present.jpg',NULL,'2020-02-05 12:39:23'),(11,'snowman.jpg',NULL,'2020-02-05 12:39:23'),(12,'spouses.png',NULL,'2020-02-05 12:39:23'),(13,'balloon.jpg',NULL,'2020-02-05 12:39:23'),(14,'Theme_Picture_1_mod.jpg',NULL,'2020-02-05 12:39:23');
/*!40000 ALTER TABLE `theme` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `email` varchar(255) NOT NULL,
  `failed_logins` bigint(20) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `last_login` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastname` varchar(255) DEFAULT NULL,
  `modify_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `password` varchar(255) NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'2020-02-05 12:39:23','admin',0,NULL,'2020-02-05 12:39:23',NULL,'2020-02-05 12:39:23','$2y$12$ajSQvX5AGg09XRqnF3odv.eV.ifGx6lm0Lowsp9dwG.jgiJEta0qy','admin'),(2,'2020-02-05 12:39:23','no@reply.com',0,NULL,'2020-02-05 12:39:23',NULL,'2020-02-05 12:39:23','$2a$10$qxdmShj3B9U95HJ5aZSHy.tkwZIksfkHq3QFvPtXlKOXQIak.1C4q','unregistered'),(3,'2020-02-05 12:39:23','mi@usermail.com',0,NULL,'2020-02-05 12:39:23',NULL,'2020-02-05 12:39:23','$2a$10$bnFYDXrIiA3UxqqyaPf/c.3ukMCZixgojEVNsEzX7b4ppRIacSPce','registered'),(4,'2020-02-05 12:39:23','Granny@gmails.com',0,NULL,'2020-02-05 12:39:23',NULL,'2020-02-05 12:39:23','$2a$10$24ToDGZ3gXGzxXpEBP9/.O5Cwpc1NpHB1KCGwWcjqlPEqIVywTm.m','registered'),(5,'2020-02-05 12:39:24','Alfred@technicbase.com',0,NULL,'2020-02-05 12:39:24',NULL,'2020-02-05 12:39:24','$2a$10$XB8wZOZLcd7vQlqHmzFJvuvRCEw0O7Ca0TzMUq9GdiXi13lyBzqM2','admin'),(6,'2020-02-05 12:50:36','pending@test.com',0,'Pending','2020-02-05 12:50:36','Test','2020-02-05 20:39:19','$2a$10$klyelTHhtXZzaSz8n4sjPO.Qpw.5mWAjDKP1Y0n4.LMQ6uZYhUhti','pending'),(10,'2020-02-05 21:02:10','n@u.com',0,'n','2020-02-05 21:02:10','u','2020-02-05 21:02:50','$2a$10$WEcE4ZCmKhvbWOWUagL8JOXDIyWmIcgJys2bDJ8vM4ZCR8jezpYwK','pending');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wish`
--

DROP TABLE IF EXISTS `wish`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wish` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` text,
  `image` varchar(255) DEFAULT NULL,
  `item` text,
  `link` text,
  `modify_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `price` float DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `giver_id` bigint(20) DEFAULT NULL,
  `wishlist_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKaoudos8e3i8b1b5mt2fr7ami` (`giver_id`),
  KEY `FKf2656374n9at6vxifmpphd9fr` (`wishlist_id`),
  CONSTRAINT `FKaoudos8e3i8b1b5mt2fr7ami` FOREIGN KEY (`giver_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKf2656374n9at6vxifmpphd9fr` FOREIGN KEY (`wishlist_id`) REFERENCES `wishlist` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wish`
--

LOCK TABLES `wish` WRITE;
/*!40000 ALTER TABLE `wish` DISABLE KEYS */;
INSERT INTO `wish` VALUES (1,'2020-02-05 12:39:23','I definitely need this! I dream of an X Box since years!','',NULL,'https://www.idealo.de/preisvergleich/OffersOfProduct/3922755_-xbox-one-microsoft.html','2020-02-05 12:39:23',0,'X Box',NULL,1),(2,'2020-02-05 12:39:23','From the top 10 list, I have #1 and #3. I don\'t like #4 and #9. Please choose what you want from the rest!','',NULL,'https://www.spiegel.de/netzwelt/games/best-of-2019-die-zehn-besten-videospiele-des-jahres-a-1298479.html','2020-02-05 12:39:23',0,'Game(s)',NULL,1),(3,'2020-02-05 12:39:23','Should be a very stable and reactive controller. I\'ve already tried the one linked below andI liked it!','XBox-Controller.png',NULL,'https://www.pcgamer.com/best-controller-for-pc-gaming/','2020-02-05 12:39:23',0,'Controller',NULL,1),(4,'2020-02-05 12:39:23','Untold riches!!!!!!!!!','',NULL,'','2020-02-05 12:39:23',0,'Money',NULL,1),(5,'2020-02-05 12:39:23','I definitely need this! I dream of an X Box since years!','',NULL,'https://www.idealo.de/preisvergleich/OffersOfProduct/3922755_-xbox-one-microsoft.html','2020-02-05 12:39:23',0,'X Box',NULL,2),(6,'2020-02-05 12:39:23','From the top 10 list, I have #1 and #3. I don\'t like #4 and #9. Please choose what you want from the rest!','',NULL,'https://www.spiegel.de/netzwelt/games/best-of-2019-die-zehn-besten-videospiele-des-jahres-a-1298479.html','2020-02-05 12:39:23',0,'Game(s)',NULL,2),(7,'2020-02-05 12:39:23','Should be a very stable and reactive controller. I\'ve already tried the one linked below andI liked it!','XBox-Controller.png',NULL,'https://www.pcgamer.com/best-controller-for-pc-gaming/','2020-02-05 12:39:23',0,'Controller',NULL,2),(8,'2020-02-05 12:39:23','Untold riches!!!!!!!!!','',NULL,'','2020-02-05 12:39:23',0,'Money',NULL,2),(9,'2020-02-05 12:39:23','I definitely need this! I dream of an X Box since years!','',NULL,'https://www.idealo.de/preisvergleich/OffersOfProduct/3922755_-xbox-one-microsoft.html','2020-02-05 12:39:23',0,'X Box',NULL,3),(10,'2020-02-05 12:39:23','From the top 10 list, I have #1 and #3. I don\'t like #4 and #9. Please choose what you want from the rest!','',NULL,'https://www.spiegel.de/netzwelt/games/best-of-2019-die-zehn-besten-videospiele-des-jahres-a-1298479.html','2020-02-05 12:39:23',0,'Game(s)',NULL,3),(11,'2020-02-05 12:39:23','Should be a very stable and reactive controller. I\'ve already tried the one linked below andI liked it!','XBox-Controller.png',NULL,'https://www.pcgamer.com/best-controller-for-pc-gaming/','2020-02-05 12:39:23',0,'Controller',NULL,3),(12,'2020-02-05 12:39:23','Untold riches!!!!!!!!!','',NULL,'','2020-02-05 12:39:23',0,'Money',NULL,3),(13,'2020-02-05 12:39:24','I definitely need this! I dream of an X Box since years!','',NULL,'https://www.idealo.de/preisvergleich/OffersOfProduct/3922755_-xbox-one-microsoft.html','2020-02-05 13:52:47',0,'X Box',4,4),(14,'2020-02-05 12:39:24','From the top 10 list, I have #1 and #3. I don\'t like #4 and #9. Please choose what you want from the rest!','',NULL,'https://www.spiegel.de/netzwelt/games/best-of-2019-die-zehn-besten-videospiele-des-jahres-a-1298479.html','2020-02-05 12:39:24',0,'Game(s)',NULL,4),(15,'2020-02-05 12:39:24','Should be a very stable and reactive controller. I\'ve already tried the one linked below andI liked it!','XBox-Controller.png',NULL,'https://www.pcgamer.com/best-controller-for-pc-gaming/','2020-02-05 12:39:24',0,'Controller',NULL,4),(16,'2020-02-05 12:39:24','Untold riches!!!!!!!!!','',NULL,'','2020-02-05 12:39:24',0,'Money',NULL,4),(17,'2020-02-05 12:39:24','I definitely need this! I dream of an X Box since years!','',NULL,'https://www.idealo.de/preisvergleich/OffersOfProduct/3922755_-xbox-one-microsoft.html','2020-02-05 12:39:24',0,'X Box',NULL,5),(18,'2020-02-05 12:39:24','From the top 10 list, I have #1 and #3. I don\'t like #4 and #9. Please choose what you want from the rest!','',NULL,'https://www.spiegel.de/netzwelt/games/best-of-2019-die-zehn-besten-videospiele-des-jahres-a-1298479.html','2020-02-05 12:39:24',0,'Game(s)',NULL,5),(19,'2020-02-05 12:39:24','Should be a very stable and reactive controller. I\'ve already tried the one linked below andI liked it!','XBox-Controller.png',NULL,'https://www.pcgamer.com/best-controller-for-pc-gaming/','2020-02-05 12:39:24',0,'Controller',NULL,5),(20,'2020-02-05 12:39:24','Untold riches!!!!!!!!!','',NULL,'','2020-02-05 12:39:24',0,'Money',NULL,5),(21,'2020-02-05 12:39:24','I definitely need this! I dream of an X Box since years!','',NULL,'https://www.idealo.de/preisvergleich/OffersOfProduct/3922755_-xbox-one-microsoft.html','2020-02-05 12:39:24',0,'X Box',NULL,6),(22,'2020-02-05 12:39:24','From the top 10 list, I have #1 and #3. I don\'t like #4 and #9. Please choose what you want from the rest!','',NULL,'https://www.spiegel.de/netzwelt/games/best-of-2019-die-zehn-besten-videospiele-des-jahres-a-1298479.html','2020-02-05 12:39:24',0,'Game(s)',NULL,6),(23,'2020-02-05 12:39:24','Should be a very stable and reactive controller. I\'ve already tried the one linked below andI liked it!','XBox-Controller.png',NULL,'https://www.pcgamer.com/best-controller-for-pc-gaming/','2020-02-05 12:39:24',0,'Controller',NULL,6),(24,'2020-02-05 12:39:24','Untold riches!!!!!!!!!','',NULL,'','2020-02-05 12:39:24',0,'Money',NULL,6);
/*!40000 ALTER TABLE `wish` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wishlist`
--

DROP TABLE IF EXISTS `wishlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wishlist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modify_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `title` varchar(255) DEFAULT NULL,
  `unique_url_giver` varchar(255) DEFAULT NULL,
  `unique_url_receiver` varchar(255) DEFAULT NULL,
  `receiver_id` bigint(20) NOT NULL,
  `theme_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpuosnphoi0lacly74rpm89p5a` (`receiver_id`),
  KEY `FKfx21yg2sjppvs4ei80qeey96l` (`theme_id`),
  CONSTRAINT `FKfx21yg2sjppvs4ei80qeey96l` FOREIGN KEY (`theme_id`) REFERENCES `theme` (`id`),
  CONSTRAINT `FKpuosnphoi0lacly74rpm89p5a` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlist`
--

LOCK TABLES `wishlist` WRITE;
/*!40000 ALTER TABLE `wishlist` DISABLE KEYS */;
INSERT INTO `wishlist` VALUES (1,'2020-02-05 12:39:23','2020-02-05 12:39:23','Wishlist #1: publicDummyUser',NULL,NULL,2,1),(2,'2020-02-05 12:39:23','2020-02-05 12:39:23','Wishlist #2: Michaela',NULL,NULL,3,2),(3,'2020-02-05 12:39:23','2020-02-05 12:39:23','Wishlist #3: Frieda',NULL,NULL,4,3),(4,'2020-02-05 12:39:24','2020-02-05 12:39:24','Wishlist #4: Alfred',NULL,NULL,5,4),(5,'2020-02-05 12:39:24','2020-02-05 12:39:24','Granny\'s Wishlist',NULL,NULL,4,5),(6,'2020-02-05 12:39:24','2020-02-05 12:39:24','Michaelas\'s Wishlist',NULL,NULL,3,6);
/*!40000 ALTER TABLE `wishlist` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-02-05 22:15:51
