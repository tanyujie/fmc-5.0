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

 Date: 20/10/2022 14:18:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for busi_tele_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_tele_dept`;
CREATE TABLE `busi_tele_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '分配给的租户',
  `tele_type` int(11) NULL DEFAULT NULL COMMENT '1单节点，100集群',
  `tele_id` bigint(20) NULL DEFAULT NULL COMMENT '当smc_type为1是，指向busi_tele的id字段，为100指向busi_tele_cluster的id字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
