/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50734
 Source Host           : localhost:3306
 Source Schema         : fcmdb

 Target Server Type    : MySQL
 Target Server Version : 50734
 File Encoding         : 65001

 Date: 06/05/2021 17:36:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for busi_live_setting
-- ----------------------------
DROP TABLE IF EXISTS `busi_live_setting`;
CREATE TABLE `busi_live_setting`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '名称',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '直播地址',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态{1:启用;0禁用}',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '描述',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '活跃会议室对应的部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '终端创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '终端修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '直播地址配置管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_record_setting
-- ----------------------------
DROP TABLE IF EXISTS `busi_record_setting`;
CREATE TABLE `busi_record_setting`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '录制路径',
  `path` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'nfs存储录制文件路径',
  `folder` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '录制文件夹名称',
  `merge_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '合并视频文件名',
  `merge_cover_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '合并视频文件封面名称',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态{1:启用;2禁用}',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '描述',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '活跃会议室对应的部门',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '终端创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '终端修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '录制管理' ROW_FORMAT = Dynamic;
