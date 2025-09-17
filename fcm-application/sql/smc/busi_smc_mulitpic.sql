/*
 Navicat Premium Data Transfer

 Source Server         : 172.16.100.135
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : 172.16.100.135:3306
 Source Schema         : fcmdb

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 25/10/2022 14:41:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for busi_smc_mulitpic
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc_mulitpic`;
CREATE TABLE `busi_smc_mulitpic`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `conference_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL COMMENT 'smc会议id',
  `mulitpic` json NOT NULL COMMENT '布局',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
