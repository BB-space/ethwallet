/*
SQLyog Ultimate v9.10 
MySQL - 5.6.18-enterprise-commercial-advanced : Database - eth_wallet
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`eth_wallet` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `eth_wallet2`;

/*Table structure for table `account` */

DROP TABLE IF EXISTS `account`;

CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(128) DEFAULT NULL,
  `userId` varchar(128) DEFAULT NULL,
  `amount` varchar(128) DEFAULT NULL,
  `type` varchar(128) DEFAULT NULL,
  `identify` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49043 DEFAULT CHARSET=utf8;

/*Table structure for table `admin` */

DROP TABLE IF EXISTS `admin`;

CREATE TABLE `admin` (
  `username` varchar(128) NOT NULL,
  `password` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `syncinfo` */

DROP TABLE IF EXISTS `syncinfo`;

CREATE TABLE `syncinfo` (
  `key` varchar(128) NOT NULL,
  `value` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `token` */

DROP TABLE IF EXISTS `token`;

CREATE TABLE `token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `contractAddress` varchar(128) DEFAULT NULL,
  `accessPassword` varchar(128) DEFAULT NULL,
  `unlockPassword` varchar(128) DEFAULT NULL,
  `type` varchar(128) DEFAULT NULL,
  `limitGas` int(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

/*Table structure for table `tx` */

DROP TABLE IF EXISTS `tx`;

CREATE TABLE `tx` (
  `txid` varchar(128) NOT NULL,
  `account` varchar(128) DEFAULT NULL,
  `address` varchar(128) DEFAULT NULL,
  `category` varchar(128) DEFAULT NULL,
  `amount` varchar(128) DEFAULT NULL,
  `confirmations` varchar(128) DEFAULT NULL,
  `blockhash` varchar(128) DEFAULT NULL,
  `blockindex` varchar(128) DEFAULT NULL,
  `blocktime` varchar(128) DEFAULT NULL,
  `time` varchar(128) DEFAULT NULL,
  `timereceived` varchar(128) DEFAULT NULL,
  `fee` varchar(128) DEFAULT NULL,
  `identify` varchar(128) DEFAULT NULL,
  `contractAddress` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`txid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `username` varchar(128) NOT NULL,
  `password` varchar(128) DEFAULT NULL,
  `identify` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
