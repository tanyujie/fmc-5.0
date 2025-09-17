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

 Date: 25/10/2022 11:39:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for busi_smc_conference_state
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc_conference_state`;
CREATE TABLE `busi_smc_conference_state`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `conference_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL COMMENT 'smc会议id',
  `chooseId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL COMMENT '选看者id',
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
