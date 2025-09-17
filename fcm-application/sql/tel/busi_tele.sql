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

 Date: 20/10/2022 14:18:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for busi_tele
-- ----------------------------
DROP TABLE IF EXISTS `busi_tele`;
CREATE TABLE `busi_tele`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '终端创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '终端修改时间',
  `admin_username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'admin用户名',
  `admin_password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'admin密码',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'tele显示名字',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备的IP地址',
  `port` int(11) NULL DEFAULT 9443 COMMENT 'tele端口',
  `status` int(11) NULL DEFAULT NULL COMMENT 'FME在线状态：1在线，2离线，3删除',
  `capacity` int(11) NULL DEFAULT NULL COMMENT 'FME容量',
  `spare_tele_id` bigint(20) NULL DEFAULT NULL COMMENT '备用FME（本节点宕机后指向的备用节点）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ip`(`ip`, `port`) USING BTREE,
  INDEX `spare_tele_id`(`spare_tele_id`) USING BTREE,
  CONSTRAINT `busi_tele_ibfk_1` FOREIGN KEY (`spare_tele_id`) REFERENCES `busi_tele` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'tele终端信息表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
