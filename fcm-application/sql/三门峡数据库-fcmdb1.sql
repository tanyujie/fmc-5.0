/*
 Navicat Premium Data Transfer

 Source Server         : 本地MySQL
 Source Server Type    : MySQL
 Source Server Version : 50721
 Source Host           : localhost:3306
 Source Schema         : fcmdb

 Target Server Type    : MySQL
 Target Server Version : 50721
 File Encoding         : 65001

 Date: 27/04/2021 15:43:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for busi_call_leg_profile
-- ----------------------------
DROP TABLE IF EXISTS `busi_call_leg_profile`;
CREATE TABLE `busi_call_leg_profile`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `call_leg_profile_uuid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '入会方案对应的fme里面的记录的uuid',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '是否是默认入会方案:1是，2否',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '入会方案归属部门',
  `fme_id` bigint(20) NULL DEFAULT NULL COMMENT '入会方案归属的fme',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `call_leg_profile_uuid`(`call_leg_profile_uuid`) USING BTREE,
  INDEX `is_default`(`type`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  INDEX `fme_id`(`fme_id`) USING BTREE,
  CONSTRAINT `busi_call_leg_profile_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 160 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '入会方案配置，控制参会者进入会议的方案' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_call_leg_profile
-- ----------------------------
INSERT INTO `busi_call_leg_profile` VALUES (157, '2021-04-26 19:22:17', NULL, '7b0a276a-46d8-44d3-8dba-0c0a1c43805d', NULL, 220, 61);
INSERT INTO `busi_call_leg_profile` VALUES (158, '2021-04-26 19:22:28', NULL, '98bc2d3d-a7d8-4f79-be9b-dc6aaae226e2', NULL, 220, 61);
INSERT INTO `busi_call_leg_profile` VALUES (159, '2021-04-27 11:07:47', NULL, '6800fba8-ee93-43d6-9446-164cfcc2d5de', NULL, 220, 61);

-- ----------------------------
-- Table structure for busi_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_conference`;
CREATE TABLE `busi_conference`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '会议创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL,
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `create_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者用户名',
  `is_main` int(11) NULL DEFAULT NULL COMMENT '是否是会议的主体（发起者）1是，2否',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议名',
  `cascade_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所有级联在一起的会议和子会议的相同ID',
  `conference_number` bigint(20) NULL DEFAULT NULL COMMENT '活跃会议室用的会议号',
  `co_space_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '活跃会议室spaceId',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '活跃会议室对应的部门',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '模板会议的ID，标记出是哪个模板发起的，好从模板点击进入会议进行关联',
  `data` mediumblob NULL COMMENT '当前正在进行中的会议室序列化数据',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `co_space_id`(`co_space_id`) USING BTREE,
  INDEX `create_user_id`(`create_user_id`) USING BTREE,
  INDEX `dept_id`(`dept_id`, `template_conference_id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`) USING BTREE,
  INDEX `cascade_id`(`cascade_id`) USING BTREE,
  INDEX `conference_number`(`conference_number`) USING BTREE,
  CONSTRAINT `busi_conference_ibfk_1` FOREIGN KEY (`create_user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_conference_ibfk_2` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_conference_ibfk_3` FOREIGN KEY (`conference_number`) REFERENCES `busi_conference_number` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '活跃会议室信息，用于存放活跃的会议室' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_conference_number
-- ----------------------------
DROP TABLE IF EXISTS `busi_conference_number`;
CREATE TABLE `busi_conference_number`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，充当会议号',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `create_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者用户名',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '归属公司ID',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '会议号类型：1级联，2普通',
  `status` int(11) NULL DEFAULT NULL COMMENT '号码状态：1闲置，10已预约，60已绑定，100会议中',
  `remarks` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dept_id`(`dept_id`, `type`) USING BTREE,
  INDEX `create_user_id`(`create_user_id`) USING BTREE,
  CONSTRAINT `busi_conference_number_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_conference_number_ibfk_2` FOREIGN KEY (`create_user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议号码记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_conference_number
-- ----------------------------
INSERT INTO `busi_conference_number` VALUES (11111, '2021-04-26 19:22:28', NULL, 1, 'superAdmin', 220, 2, 60, NULL);

-- ----------------------------
-- Table structure for busi_fme
-- ----------------------------
DROP TABLE IF EXISTS `busi_fme`;
CREATE TABLE `busi_fme`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '终端创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '终端修改时间',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'FME的连接用户名，同一个组下的用户名相同',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'FME的连接密码，同一个组下的密码相同',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'fme显示名字',
  `ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备的IP地址',
  `cucm_ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '该IP是增强音视频效果',
  `port` int(11) NULL DEFAULT 9443 COMMENT 'fme端口',
  `status` int(11) NULL DEFAULT NULL COMMENT 'FME在线状态：1在线，2离线，3删除',
  `spare_fme_id` bigint(20) NULL DEFAULT NULL COMMENT '备用FME（本节点宕机后指向的备用节点）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ip`(`ip`) USING BTREE,
  INDEX `spare_fme_id`(`spare_fme_id`) USING BTREE,
  CONSTRAINT `busi_fme_ibfk_1` FOREIGN KEY (`spare_fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'FME终端信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_fme
-- ----------------------------
INSERT INTO `busi_fme` VALUES (61, '2021-04-26 19:14:08', '2021-04-27 14:58:28', 'admin', 'P@rad1se', '192.166.1.3', '192.166.1.3', NULL, 9443, 1, NULL);

-- ----------------------------
-- Table structure for busi_fme_cluster
-- ----------------------------
DROP TABLE IF EXISTS `busi_fme_cluster`;
CREATE TABLE `busi_fme_cluster`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '集群组名，最长32',
  `spare_fme_type` int(11) NULL DEFAULT NULL COMMENT '备用fme类型，1单节点，100集群',
  `spare_fme_id` bigint(20) NULL DEFAULT NULL COMMENT '当fme_type为1是，指向busi_fme的id字段，为100指向busi_fme_cluster的id字段',
  `description` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `spare_fme_id`(`spare_fme_id`) USING BTREE,
  INDEX `spare_fme_type`(`spare_fme_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'FME集群' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_fme_cluster_map
-- ----------------------------
DROP TABLE IF EXISTS `busi_fme_cluster_map`;
CREATE TABLE `busi_fme_cluster_map`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `cluster_id` bigint(20) NULL DEFAULT NULL COMMENT 'FME集群ID',
  `fme_id` bigint(20) NULL DEFAULT NULL COMMENT 'FME的ID',
  `weight` int(11) NULL DEFAULT NULL COMMENT '节点在集群中的权重值',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `group_id`(`cluster_id`, `fme_id`, `weight`) USING BTREE,
  UNIQUE INDEX `cluster_id`(`cluster_id`, `fme_id`) USING BTREE,
  INDEX `fme_id`(`fme_id`) USING BTREE,
  CONSTRAINT `busi_fme_cluster_map_ibfk_1` FOREIGN KEY (`cluster_id`) REFERENCES `busi_fme_cluster` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_fme_cluster_map_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 100035 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'FME-终端组中间表（多对多）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_fme_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_fme_dept`;
CREATE TABLE `busi_fme_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '分配给的租户',
  `fme_type` int(11) NULL DEFAULT NULL COMMENT '1单节点，100集群',
  `fme_id` bigint(20) NULL DEFAULT NULL COMMENT '当fme_type为1是，指向busi_fme的id字段，为100指向busi_fme_cluster的id字段',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `busi_fme_group_dept_ibfk_1`(`dept_id`) USING BTREE,
  INDEX `fme_group_id`(`fme_id`) USING BTREE,
  CONSTRAINT `busi_fme_dept_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'FME组分配租户的中间表（一个FME组可以分配给多个租户，一对多）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_fme_dept
-- ----------------------------
INSERT INTO `busi_fme_dept` VALUES (46, '2021-04-26 19:14:15', NULL, 220, 1, 61);

-- ----------------------------
-- Table structure for busi_fsbc_registration_server
-- ----------------------------
DROP TABLE IF EXISTS `busi_fsbc_registration_server`;
CREATE TABLE `busi_fsbc_registration_server`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册服务器名字',
  `ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册服务器ip地州',
  `port` int(11) NULL DEFAULT NULL COMMENT '注册服务器端口',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册服务器用户名',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册服务器密码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '终端FSBC注册服务器' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_fsbc_registration_server
-- ----------------------------
INSERT INTO `busi_fsbc_registration_server` VALUES (13, '2021-04-26 18:48:34', NULL, '三门峡FSBC服务器', '218.28.249.132', 443, 'admin', 'P@rad1se');

-- ----------------------------
-- Table structure for busi_fsbc_server_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_fsbc_server_dept`;
CREATE TABLE `busi_fsbc_server_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `fsbc_server_id` bigint(20) NULL DEFAULT NULL COMMENT 'FSBC服务器的id',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  INDEX `fsbc_server_id`(`fsbc_server_id`) USING BTREE,
  CONSTRAINT `busi_fsbc_server_dept_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_fsbc_server_dept_ibfk_2` FOREIGN KEY (`fsbc_server_id`) REFERENCES `busi_fsbc_registration_server` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'FSBC服务器-部门映射' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_fsbc_server_dept
-- ----------------------------
INSERT INTO `busi_fsbc_server_dept` VALUES (10, '2021-04-26 18:48:41', NULL, 13, 220);

-- ----------------------------
-- Table structure for busi_history_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_history_conference`;
CREATE TABLE `busi_history_conference`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `create_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者用户名',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板会议名',
  `number` int(11) NULL DEFAULT NULL COMMENT '会议号码',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `call_leg_profile_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '入会方案配置ID（关联FME里面的入会方案记录ID，会控端不存）',
  `bandwidth` int(11) NULL DEFAULT NULL COMMENT '带宽1,2,3,4,5,6M',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `busi_conference_template_ibfk_1`(`dept_id`) USING BTREE,
  CONSTRAINT `busi_history_conference_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '历史会议，每次挂断会保存该历史记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_history_participant
-- ----------------------------
DROP TABLE IF EXISTS `busi_history_participant`;
CREATE TABLE `busi_history_participant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `history_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的会议ID',
  `terminal_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的终端ID',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_conference_id`(`history_conference_id`) USING BTREE,
  INDEX `terminal_id`(`terminal_id`) USING BTREE,
  CONSTRAINT `busi_history_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_history_participant_ibfk_3` FOREIGN KEY (`history_conference_id`) REFERENCES `busi_history_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '历史会议的参会者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_template_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_conference`;
CREATE TABLE `busi_template_conference`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者id',
  `create_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者用户名',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板会议名',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `call_leg_profile_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '入会方案配置ID（关联FME里面的入会方案记录ID，会控端不存）',
  `bandwidth` int(11) NULL DEFAULT NULL COMMENT '带宽1,2,3,4,5,6M',
  `is_auto_call` int(11) NULL DEFAULT NULL COMMENT '是否自动呼叫与会者：1是，2否',
  `is_auto_monitor` int(11) NULL DEFAULT NULL COMMENT '是否自动监听会议：1是，2否',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '模板会议是否允许被级联：1允许，2不允许',
  `conference_number` bigint(20) NULL DEFAULT NULL COMMENT '模板绑定的会议号',
  `master_participant_id` bigint(20) NULL DEFAULT NULL COMMENT '主会场ID',
  `default_view_layout` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认视图布局类型',
  `default_view_is_broadcast` int(11) NULL DEFAULT NULL COMMENT '默认视图是否广播(1是，2否)',
  `default_view_is_display_self` int(11) NULL DEFAULT NULL COMMENT '默认视图是否显示自己(1是，2否)',
  `default_view_is_fill` int(11) NULL DEFAULT NULL COMMENT '默认视图是否补位(1是，2否)',
  `polling_interval` int(11) NULL DEFAULT NULL COMMENT '轮询时间间隔',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `busi_conference_template_ibfk_1`(`dept_id`) USING BTREE,
  INDEX `call_leg_profile_id`(`call_leg_profile_id`) USING BTREE,
  INDEX `conference_number`(`conference_number`) USING BTREE,
  INDEX `create_user_id`(`create_user_id`) USING BTREE,
  INDEX `master_participant_id`(`master_participant_id`) USING BTREE,
  CONSTRAINT `busi_template_conference_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_conference_ibfk_2` FOREIGN KEY (`conference_number`) REFERENCES `busi_conference_number` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_conference_ibfk_3` FOREIGN KEY (`create_user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_conference_ibfk_4` FOREIGN KEY (`master_participant_id`) REFERENCES `busi_template_participant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 77 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_conference
-- ----------------------------
INSERT INTO `busi_template_conference` VALUES (76, '2021-04-26 19:23:10', '2021-04-26 20:09:26', 1, 'superAdmin', '测试会议', 220, '7b0a276a-46d8-44d3-8dba-0c0a1c43805d', 2, 2, 2, 2, 11111, 19859, 'speakerOnly', 2, 2, 1, 10);

-- ----------------------------
-- Table structure for busi_template_conference_default_view_cell_screen
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_conference_default_view_cell_screen`;
CREATE TABLE `busi_template_conference_default_view_cell_screen`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的会议模板ID',
  `cell_sequence_number` int(11) NULL DEFAULT NULL COMMENT '单元格序号',
  `operation` int(11) NULL DEFAULT NULL COMMENT '分频单元格对应的操作，默认为选看101，105轮询',
  `is_fixed` int(11) NULL DEFAULT NULL COMMENT '分频单元格是否固定',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`) USING BTREE,
  INDEX `cell_sequence_number`(`cell_sequence_number`) USING BTREE,
  CONSTRAINT `busi_template_conference_default_view_cell_screen_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 259 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '默认视图下指定的多分频单元格' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_conference_default_view_cell_screen
-- ----------------------------
INSERT INTO `busi_template_conference_default_view_cell_screen` VALUES (258, NULL, NULL, 76, 1, 101, 2);

-- ----------------------------
-- Table structure for busi_template_conference_default_view_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_conference_default_view_dept`;
CREATE TABLE `busi_template_conference_default_view_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的会议模板ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID（部门也是FME终端，一种与会者）',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  INDEX `busi_template_default_view_dept_ibfk_1`(`template_conference_id`) USING BTREE,
  CONSTRAINT `busi_template_conference_default_view_dept_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_conference_default_view_dept_ibfk_2` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1345 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '默认视图的部门显示顺序' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_conference_default_view_dept
-- ----------------------------
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1343, NULL, NULL, 76, 220, 2);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1344, NULL, NULL, 76, 221, 1);

-- ----------------------------
-- Table structure for busi_template_conference_default_view_paticipant
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_conference_default_view_paticipant`;
CREATE TABLE `busi_template_conference_default_view_paticipant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的会议模板ID',
  `template_participant_id` bigint(20) NULL DEFAULT NULL COMMENT '参会终端ID，关联busi_template_participant的ID',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  `cell_sequence_number` int(11) NULL DEFAULT NULL COMMENT '多分频单元格序号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `template_participant_id`(`template_participant_id`) USING BTREE,
  INDEX `default_view_id`(`template_conference_id`) USING BTREE,
  INDEX `cell_sequence_number`(`cell_sequence_number`) USING BTREE,
  CONSTRAINT `busi_template_conference_default_view_paticipant_ibfk_3` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_conference_default_view_paticipant_ibfk_4` FOREIGN KEY (`template_participant_id`) REFERENCES `busi_template_participant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 11504 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '默认视图的参会者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_conference_default_view_paticipant
-- ----------------------------
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (11502, NULL, NULL, 76, 19859, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (11503, NULL, NULL, 76, 19860, 2, NULL);

-- ----------------------------
-- Table structure for busi_template_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_dept`;
CREATE TABLE `busi_template_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `uuid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板中的级联与会者终端的UUID(FME参会者ID)',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID（部门也是FME终端，一种与会者）',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `terminal_id`(`dept_id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`, `weight`) USING BTREE,
  CONSTRAINT `busi_template_dept_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_dept_ibfk_2` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 3172 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议模板的级联部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_dept
-- ----------------------------
INSERT INTO `busi_template_dept` VALUES (3171, '2021-04-26 20:09:27', NULL, '221164d3-0889-491c-a9b1-e2e5f081e788', 76, 221, 1);

-- ----------------------------
-- Table structure for busi_template_participant
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_participant`;
CREATE TABLE `busi_template_participant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `uuid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板中的与会者的UUID',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `terminal_id` bigint(20) NULL DEFAULT NULL COMMENT '终端ID',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uuid`(`uuid`) USING BTREE,
  INDEX `terminal_id`(`terminal_id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`, `weight`) USING BTREE,
  CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_participant_ibfk_3` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 19861 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议模板的参会者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_participant
-- ----------------------------
INSERT INTO `busi_template_participant` VALUES (19859, '2021-04-26 20:09:26', NULL, '2765d2cc-50a4-414a-88c3-962c9f1445e1', 76, 290, 2);
INSERT INTO `busi_template_participant` VALUES (19860, '2021-04-26 20:09:27', NULL, 'db4654cc-466c-4270-9681-5b058eaf0d4e', 76, 292, 2);

-- ----------------------------
-- Table structure for busi_template_polling_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_polling_dept`;
CREATE TABLE `busi_template_polling_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `polling_scheme_id` bigint(20) NULL DEFAULT NULL COMMENT '归属轮询方案ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID（部门也是FME终端，一种与会者）',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`) USING BTREE,
  INDEX `polling_scheme_id`(`polling_scheme_id`) USING BTREE,
  CONSTRAINT `busi_template_polling_dept_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_polling_dept_ibfk_2` FOREIGN KEY (`polling_scheme_id`) REFERENCES `busi_template_polling_scheme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1494 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮询方案的部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_template_polling_paticipant
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_polling_paticipant`;
CREATE TABLE `busi_template_polling_paticipant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `polling_scheme_id` bigint(20) NULL DEFAULT NULL COMMENT '归属轮询方案ID',
  `polling_interval` int(11) NULL DEFAULT NULL COMMENT '该参会者的特定的轮询间隔（如果该值存在，会覆盖轮询方案终端间隔）',
  `remote_party` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '终端的远程部分（唯一的）',
  `terminal_id` bigint(20) NULL DEFAULT NULL COMMENT '终端ID',
  `is_cascade_main` int(11) NULL DEFAULT NULL COMMENT '是否级联之主(1是，2否)',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`) USING BTREE,
  INDEX `polling_scheme_id`(`polling_scheme_id`) USING BTREE,
  INDEX `remote_party`(`remote_party`) USING BTREE,
  CONSTRAINT `busi_template_polling_paticipant_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_polling_paticipant_ibfk_2` FOREIGN KEY (`polling_scheme_id`) REFERENCES `busi_template_polling_scheme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 8875 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮询方案的参会者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_template_polling_scheme
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_polling_scheme`;
CREATE TABLE `busi_template_polling_scheme`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `polling_interval` int(11) NULL DEFAULT NULL COMMENT '轮询时间间隔',
  `polling_state_first` int(11) NULL DEFAULT NULL COMMENT '是否需要优先轮询地州（1是，2否）',
  `scheme_name` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '轮询方案名',
  `weight` int(11) NULL DEFAULT NULL COMMENT '轮询方案顺序，越大越靠前',
  `layout` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '多分频轮询支持',
  `is_broadcast` int(11) NULL DEFAULT NULL COMMENT '是否广播(1是，2否)',
  `is_display_self` int(11) NULL DEFAULT NULL COMMENT '是否显示自己(1是，2否)',
  `is_fill` int(11) NULL DEFAULT NULL COMMENT '是否补位(1是，2否)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`) USING BTREE,
  CONSTRAINT `busi_template_polling_scheme_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮询方案' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_terminal
-- ----------------------------
DROP TABLE IF EXISTS `busi_terminal`;
CREATE TABLE `busi_terminal`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '终端创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '终端修改时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `create_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者用户名',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '终端所属部门ID',
  `ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备的IP地址',
  `number` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备号，设备唯一标识（如果是sfbc终端，则对应凭据）',
  `camera_ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '摄像头IP地址',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '终端显示名字',
  `type` int(11) NULL DEFAULT NULL COMMENT '终端类型，枚举值int类型',
  `online_status` int(11) NULL DEFAULT NULL COMMENT '终端状态：1在线，2离线',
  `credential` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'fsbc账号',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'fsbc密码',
  `fsbc_server_id` bigint(20) NULL DEFAULT NULL COMMENT 'fsbc服务器ID',
  `protocol` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '协议',
  `vendor` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'FSBC终端厂商信息',
  `registration_time` datetime(0) NULL DEFAULT NULL COMMENT 'FSBC终端最后注册时间',
  `intranet_ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'FSBC终端内网IP',
  `port` int(11) NULL DEFAULT NULL COMMENT 'FSBC终端端口',
  `transport` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'FSBC终端的传输协议（TLS,TCP,UDP）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ip`(`ip`, `number`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  INDEX `create_user_id`(`create_user_id`) USING BTREE,
  UNIQUE INDEX `fsbc_server_id`(`fsbc_server_id`, `credential`) USING BTREE,
  CONSTRAINT `busi_terminal_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_terminal_ibfk_2` FOREIGN KEY (`create_user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_terminal_ibfk_3` FOREIGN KEY (`fsbc_server_id`) REFERENCES `busi_fsbc_registration_server` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 299 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '终端信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_terminal
-- ----------------------------
INSERT INTO `busi_terminal` VALUES (290, '2021-04-26 19:18:13', NULL, 1, 'superAdmin', 220, '192.166.1.12', NULL, NULL, '192.166.1.12', 310, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (291, '2021-04-26 19:18:31', '2021-04-26 20:22:25', 1, 'superAdmin', 220, '192.166.1.21', NULL, NULL, '192.166.1.21', 310, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (292, '2021-04-26 19:18:49', NULL, 1, 'superAdmin', 221, '192.166.2.12', NULL, NULL, '192.166.2.12', 310, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (293, '2021-04-26 19:19:06', NULL, 1, 'superAdmin', 221, '192.166.2.21', NULL, NULL, '192.166.2.21', 310, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (294, '2021-04-26 19:19:47', NULL, 1, 'superAdmin', 222, '192.166.1.13', NULL, NULL, '192.166.1.13', 310, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (295, '2021-04-27 09:59:03', NULL, 100, 'admin', 220, NULL, NULL, NULL, 'VHD-CS', 1100, 2, '10001', '10001', 13, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for busi_token
-- ----------------------------
DROP TABLE IF EXISTS `busi_token`;
CREATE TABLE `busi_token`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `token` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接口访问所需的token',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '第三方用户访问的api专用token' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 206 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table
-- ----------------------------
INSERT INTO `gen_table` VALUES (173, 'busi_conference_number', '会议号码记录表', NULL, NULL, 'BusiConferenceNumber', 'crud', 'com.paradisecloud.fcm', 'busi', 'number', '会议号码记录', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-01-20 16:15:27', '', '2021-01-20 16:16:35', NULL);
INSERT INTO `gen_table` VALUES (174, 'busi_fme', 'FME终端信息表', NULL, NULL, 'BusiFme', 'crud', 'com.paradisecloud.fcm', 'busi', 'fme', 'FME终端信息', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (175, 'busi_fme_group', 'FME终端组', NULL, NULL, 'BusiFmeGroup', 'crud', 'com.paradisecloud.fcm', 'busi', 'group', 'FME终端组', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (176, 'busi_history_conference', '历史会议，每次挂断会保存该历史记录', NULL, NULL, 'BusiHistoryConference', 'crud', 'com.paradisecloud.fcm', 'busi', 'conference', '历史会议，每次挂断会保存该历史记录', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (177, 'busi_history_participant', '历史会议的参会者', NULL, NULL, 'BusiHistoryParticipant', 'crud', 'com.paradisecloud.fcm', 'busi', 'participant', '历史会议的参会者', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (179, 'busi_template_conference', '会议模板表', NULL, NULL, 'BusiTemplateConference', 'crud', 'com.paradisecloud.fcm', 'busi', 'conference', '会议模板', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (180, 'busi_template_participant', '会议模板的参会者', NULL, NULL, 'BusiTemplateParticipant', 'crud', 'com.paradisecloud.fcm', 'busi', 'participant', '会议模板的参会者', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (184, 'busi_call_leg_profile', '入会方案配置，控制参会者进入会议的方案', NULL, NULL, 'BusiCallLegProfile', 'crud', 'com.paradisecloud.fcm', 'busi', 'callLegProfile', '入会方案配置，控制参会者进入会议的方案', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22', NULL);
INSERT INTO `gen_table` VALUES (186, 'busi_fme_group_dept', 'FME组分配租户的中间表（一个FME组可以分配给多个租户，一对多）', NULL, NULL, 'BusiFmeGroupDept', 'crud', 'com.paradisecloud.fcm', 'busi', 'dept', 'FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-01-28 14:42:35', '', '2021-01-28 14:42:54', NULL);
INSERT INTO `gen_table` VALUES (188, 'busi_template_dept', '会议模板的级联部门', NULL, NULL, 'BusiTemplateDept', 'crud', 'com.paradisecloud.fcm', 'busi', 'dept', '会议模板的级联部门', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-01-29 18:10:25', '', '2021-01-29 18:24:19', NULL);
INSERT INTO `gen_table` VALUES (189, 'busi_conference', '活跃会议室信息，用于存放活跃的会议室', NULL, NULL, 'BusiConference', 'crud', 'com.paradisecloud.fcm', 'busi', 'conference', '活跃会议室信息，用于存放活跃的会议室', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41', NULL);
INSERT INTO `gen_table` VALUES (190, 'busi_template_polling_dept', '轮询方案的部门', NULL, NULL, 'BusiTemplatePollingDept', 'crud', 'com.paradisecloud.system', 'system', 'dept', '轮询方案的部门', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-02-26 10:11:43', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (191, 'busi_template_polling_paticipant', '轮询方案的参会者', NULL, NULL, 'BusiTemplatePollingPaticipant', 'crud', 'com.paradisecloud.fcm', 'busi', 'paticipant', '轮询方案的参会者', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05', NULL);
INSERT INTO `gen_table` VALUES (192, 'busi_template_polling_scheme', '轮询方案', NULL, NULL, 'BusiTemplatePollingScheme', 'crud', 'com.paradisecloud.fcm', 'system', 'scheme', '轮询方案', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49', NULL);
INSERT INTO `gen_table` VALUES (194, 'busi_terminal_registration_server', '终端注册服务器', NULL, NULL, 'BusiTerminalRegistrationServer', 'crud', 'com.paradisecloud.fcm', 'busi', 'registrationServer', '终端注册服务器', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36', NULL);
INSERT INTO `gen_table` VALUES (195, 'busi_fme_cluster', 'FME集群', NULL, NULL, 'BusiFmeCluster', 'crud', 'com.paradisecloud.fcm', 'busi', 'fmeCluster', 'FME集群', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:38:19', NULL);
INSERT INTO `gen_table` VALUES (196, 'busi_fme_cluster_map', 'FME-终端组中间表（多对多）', NULL, NULL, 'BusiFmeClusterMap', 'crud', 'com.paradisecloud.fcm', 'busi', 'fmeClusterMap', 'FME-终端组中间（多对多）', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:40:48', NULL);
INSERT INTO `gen_table` VALUES (197, 'busi_fme_dept', 'FME组分配租户的中间表（一个FME组可以分配给多个租户，一对多）', NULL, NULL, 'BusiFmeDept', 'crud', 'com.paradisecloud.fcm', 'busi', 'fmeDept', 'FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:41:12', NULL);
INSERT INTO `gen_table` VALUES (198, 'busi_template_conference_default_view_cell_screen', '默认视图下指定的多分频单元格', NULL, NULL, 'BusiTemplateConferenceDefaultViewCellScreen', 'crud', 'com.paradisecloud.fcm', 'busi', 'defaultViewCellScreen', '默认视图下指定的多分频单元格', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:27', NULL);
INSERT INTO `gen_table` VALUES (199, 'busi_template_conference_default_view_dept', '默认视图的部门显示顺序', NULL, NULL, 'BusiTemplateConferenceDefaultViewDept', 'crud', 'com.paradisecloud.fcm', 'busi', 'defaultViewDept', '默认视图的部门显示顺序', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:45', NULL);
INSERT INTO `gen_table` VALUES (200, 'busi_template_conference_default_view_paticipant', '默认视图的参会者', NULL, NULL, 'BusiTemplateConferenceDefaultViewPaticipant', 'crud', 'com.paradisecloud.fcm', 'busi', 'DefaultViewPaticipant', '默认视图的参会者', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:44:03', NULL);
INSERT INTO `gen_table` VALUES (201, 'busi_sfbc_registration_server', '终端SFBC注册服务器', NULL, NULL, 'BusiSfbcRegistrationServer', 'crud', 'com.paradisecloud.fcm', 'busi', 'sfbcserver', '终端SFBC注册服务器', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25', NULL);
INSERT INTO `gen_table` VALUES (202, 'busi_sfbc_server_dept', 'SFBC服务器-部门映射', NULL, NULL, 'BusiSfbcServerDept', 'crud', 'com.paradisecloud.fcm', 'busi', 'sfbcDept', 'SFBC服务器-部门映射', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:49', NULL);
INSERT INTO `gen_table` VALUES (203, 'busi_fsbc_registration_server', '终端FSBC注册服务器', NULL, NULL, 'BusiFsbcRegistrationServer', 'crud', 'com.paradisecloud.fcm', 'busi', 'fsbcServer', '终端FSBC注册服务器', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:49:56', NULL);
INSERT INTO `gen_table` VALUES (204, 'busi_fsbc_server_dept', 'FSBC服务器-部门映射', NULL, NULL, 'BusiFsbcServerDept', 'crud', 'com.paradisecloud.fcm', 'busi', 'fsbcDept', 'FSBC服务器-部门映射', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:50:17', NULL);
INSERT INTO `gen_table` VALUES (205, 'busi_terminal', '终端信息表', NULL, NULL, 'BusiTerminal', 'crud', 'com.paradisecloud.fcm', 'busi', 'terminal', '终端信息', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38', NULL);

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1652 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------
INSERT INTO `gen_table_column` VALUES (1355, '173', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', '2021-01-20 16:16:35');
INSERT INTO `gen_table_column` VALUES (1356, '173', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', '2021-01-20 16:16:35');
INSERT INTO `gen_table_column` VALUES (1357, '173', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', '2021-01-20 16:16:35');
INSERT INTO `gen_table_column` VALUES (1359, '173', 'dept_id', '归属公司ID', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', '2021-01-20 16:16:35');
INSERT INTO `gen_table_column` VALUES (1360, '174', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1361, '174', 'create_time', '终端创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1362, '174', 'update_time', '终端修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1364, '174', 'name', 'fme显示名字', 'varchar(32)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1365, '174', 'ip', '设备的IP地址', 'varchar(16)', 'String', 'ip', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1366, '174', 'port', 'fme端口', 'int(11)', 'Integer', 'port', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1367, '174', 'priority', '优先级，越大越高(备用节点才会有优先级)', 'int(11)', 'Integer', 'priority', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1368, '174', 'status', 'FME在线状态：1在线，2离线，3删除', 'int(11)', 'Integer', 'status', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'radio', '', 9, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1369, '175', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1370, '175', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1371, '175', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1372, '175', 'dept_id', '所属部门（每个用户登录，只列出该用户所属部门下的FME组）', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1373, '175', 'username', 'FME的连接用户名，同一个组下的用户名相同', 'varchar(32)', 'String', 'username', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1374, '175', 'password', 'FME的连接密码，同一个组下的密码相同', 'varchar(128)', 'String', 'password', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1375, '175', 'name', '组名，最长32', 'varchar(32)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 7, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1376, '175', 'type', '1集群，2单节点', 'int(11)', 'Integer', 'type', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 8, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1377, '175', 'busi_type', '类型：1主用，2备用', 'int(11)', 'Integer', 'busiType', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 9, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1378, '175', 'spare_fme_group', '备用的FME节点（可以指向集群组和单节点组）', 'bigint(20)', 'Long', 'spareFmeGroup', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 10, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1379, '175', 'description', '备注信息', 'varchar(128)', 'String', 'description', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 11, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1380, '176', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1381, '176', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1382, '176', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1383, '176', 'name', '模板会议名', 'varchar(128)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1384, '176', 'number', '会议号码', 'int(11)', 'Integer', 'number', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1385, '176', 'dept_id', '部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1386, '176', 'call_leg_profile_id', '入会方案配置ID（关联FME里面的入会方案记录ID，会控端不存）', 'varchar(128)', 'String', 'callLegProfileId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1387, '176', 'bandwidth', '带宽1,2,3,4,5,6M', 'int(11)', 'Integer', 'bandwidth', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1388, '177', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1389, '177', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1390, '177', 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1391, '177', 'history_conference_id', '关联的会议ID', 'bigint(20)', 'Long', 'historyConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1392, '177', 'terminal_id', '关联的终端ID', 'bigint(20)', 'Long', 'terminalId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1393, '177', 'weight', '参会者顺序（权重倒叙排列）', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1400, '179', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1401, '179', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1402, '179', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1403, '179', 'name', '模板会议名', 'varchar(128)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1405, '179', 'dept_id', '部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1406, '179', 'call_leg_profile_id', '入会方案配置ID（关联FME里面的入会方案记录ID，会控端不存）', 'varchar(128)', 'String', 'callLegProfileId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1407, '179', 'bandwidth', '带宽1,2,3,4,5,6M', 'int(11)', 'Integer', 'bandwidth', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1408, '179', 'is_auto_call', '是否自动呼叫与会者：1是，2否', 'int(11)', 'Integer', 'isAutoCall', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 9, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1409, '180', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1410, '180', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1411, '180', 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1412, '180', 'template_conference_id', '会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1413, '180', 'terminal_id', '终端ID', 'bigint(20)', 'Long', 'terminalId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1414, '180', 'weight', '参会者顺序（权重倒叙排列）', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1440, '184', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22');
INSERT INTO `gen_table_column` VALUES (1441, '184', 'create_time', NULL, 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22');
INSERT INTO `gen_table_column` VALUES (1442, '184', 'update_time', NULL, 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22');
INSERT INTO `gen_table_column` VALUES (1443, '184', 'call_leg_profile_uuid', '入会方案对应的fme里面的记录的uuid', 'varchar(128)', 'String', 'callLegProfileUuid', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22');
INSERT INTO `gen_table_column` VALUES (1444, '184', 'type', '是否是默认入会方案:1是，2否', 'tinyint(1)', 'Integer', 'type', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 5, 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22');
INSERT INTO `gen_table_column` VALUES (1445, '184', 'dept_id', '入会方案归属部门', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22');
INSERT INTO `gen_table_column` VALUES (1446, '184', 'fme_id', '入会方案归属的fme', 'bigint(20)', 'Long', 'fmeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22');
INSERT INTO `gen_table_column` VALUES (1447, '173', 'type', '会议号类型：1默认，2普通', 'tinyint(1)', 'Integer', 'type', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 6, '', '2021-01-26 19:00:48', '', NULL);
INSERT INTO `gen_table_column` VALUES (1448, '173', 'remarks', '备注信息', 'varchar(32)', 'String', 'remarks', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, '', '2021-01-26 19:00:48', '', NULL);
INSERT INTO `gen_table_column` VALUES (1449, '173', 'status', '号码状态：1闲置，10已预约，100会议中', 'int(11)', 'Integer', 'status', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'radio', '', 7, '', '2021-01-27 10:50:34', '', NULL);
INSERT INTO `gen_table_column` VALUES (1455, '186', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-28 14:42:35', '', '2021-01-28 14:42:54');
INSERT INTO `gen_table_column` VALUES (1456, '186', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-28 14:42:35', '', '2021-01-28 14:42:54');
INSERT INTO `gen_table_column` VALUES (1457, '186', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-28 14:42:35', '', '2021-01-28 14:42:54');
INSERT INTO `gen_table_column` VALUES (1458, '186', 'fme_group_id', 'fme组ID', 'bigint(20)', 'Long', 'fmeGroupId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-28 14:42:35', '', '2021-01-28 14:42:54');
INSERT INTO `gen_table_column` VALUES (1459, '186', 'dept_id', '分配给的租户', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-28 14:42:35', '', '2021-01-28 14:42:54');
INSERT INTO `gen_table_column` VALUES (1466, '188', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-29 18:10:26', '', '2021-01-29 18:24:19');
INSERT INTO `gen_table_column` VALUES (1467, '188', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-29 18:10:26', '', '2021-01-29 18:24:19');
INSERT INTO `gen_table_column` VALUES (1468, '188', 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-29 18:10:26', '', '2021-01-29 18:24:19');
INSERT INTO `gen_table_column` VALUES (1469, '188', 'template_conference_id', '会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-29 18:10:26', '', '2021-01-29 18:24:19');
INSERT INTO `gen_table_column` VALUES (1470, '188', 'dept_id', '部门ID（部门也是FME终端，一种与会者）', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-29 18:10:26', '', '2021-01-29 18:24:19');
INSERT INTO `gen_table_column` VALUES (1471, '188', 'weight', '参会者顺序（权重倒叙排列）', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-29 18:10:26', '', '2021-01-29 18:24:19');
INSERT INTO `gen_table_column` VALUES (1472, '179', 'conference_number', '模板绑定的会议号', 'bigint(20)', 'Long', 'conferenceNumber', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 9, '', '2021-01-29 18:11:10', '', NULL);
INSERT INTO `gen_table_column` VALUES (1477, '179', 'create_user_id', '创建者id', 'bigint(20)', 'Long', 'createUserId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-01-30 10:18:59', '', NULL);
INSERT INTO `gen_table_column` VALUES (1478, '179', 'create_user_name', '创建者用户名', 'varchar(32)', 'String', 'createUserName', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 5, '', '2021-01-30 10:18:59', '', NULL);
INSERT INTO `gen_table_column` VALUES (1479, '176', 'create_user_id', '创建者ID', 'bigint(20)', 'Long', 'createUserId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-01-30 10:19:39', '', NULL);
INSERT INTO `gen_table_column` VALUES (1480, '176', 'create_user_name', '创建者用户名', 'varchar(32)', 'String', 'createUserName', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 5, '', '2021-01-30 10:19:39', '', NULL);
INSERT INTO `gen_table_column` VALUES (1481, '173', 'create_user_id', '创建者ID', 'bigint(20)', 'Long', 'createUserId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-01-30 10:19:51', '', NULL);
INSERT INTO `gen_table_column` VALUES (1482, '173', 'create_user_name', '创建者用户名', 'varchar(32)', 'String', 'createUserName', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 5, '', '2021-01-30 10:19:51', '', NULL);
INSERT INTO `gen_table_column` VALUES (1483, '173', 'co_space_id', '会议号对应的会议室ID', 'varchar(0)', 'String', 'coSpaceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, '', '2021-01-30 16:16:38', '', NULL);
INSERT INTO `gen_table_column` VALUES (1484, '179', 'type', '模板会议类型：1级联，2普通', 'tinyint(1)', 'Integer', 'type', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 11, '', '2021-02-01 10:05:01', '', NULL);
INSERT INTO `gen_table_column` VALUES (1485, '189', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1486, '189', 'create_time', '会议创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1487, '189', 'update_time', NULL, 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1488, '189', 'create_user_id', '创建者ID', 'bigint(20)', 'Long', 'createUserId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1489, '189', 'create_user_name', '创建者用户名', 'varchar(32)', 'String', 'createUserName', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 5, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1490, '189', 'name', '会议名', 'varchar(64)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 6, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1491, '189', 'conference_number', '活跃会议室用的会议号', 'bigint(20)', 'Long', 'conferenceNumber', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1492, '189', 'co_space_id', '活跃会议室spaceId', 'varchar(128)', 'String', 'coSpaceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1493, '189', 'dept_id', '活跃会议室对应的部门', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 9, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1494, '189', 'data', '当前正在进行中的会议室序列化数据', 'mediumblob', 'String', 'data', '0', '0', NULL, '1', '1', '1', '1', 'EQ', NULL, '', 10, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1495, '174', 'cucm_ip', '该IP是增强音视频效果', 'varchar(16)', 'String', 'cucmIp', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, '', '2021-02-03 11:43:07', '', NULL);
INSERT INTO `gen_table_column` VALUES (1498, '188', 'uuid', '模板中的级联与会者终端的UUID', 'varchar(128)', 'String', 'uuid', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-02-03 17:21:46', '', NULL);
INSERT INTO `gen_table_column` VALUES (1499, '180', 'uuid', '模板中的与会者的UUID', 'varchar(128)', 'String', 'uuid', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-02-03 17:23:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (1500, '189', 'is_main', '是否是会议的主体（发起者）', 'int(11)', 'Integer', 'isMain', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, '', '2021-02-04 11:08:35', '', NULL);
INSERT INTO `gen_table_column` VALUES (1501, '189', 'cascade_id', '所有级联在一起的会议和子会议的相同ID', 'varchar(128)', 'String', 'cascadeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, '', '2021-02-04 11:08:35', '', NULL);
INSERT INTO `gen_table_column` VALUES (1502, '189', 'template_conference_id', '模板会议的ID，标记出是哪个模板发起的，好从模板点击进入会议进行关联', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 12, '', '2021-02-04 11:08:35', '', NULL);
INSERT INTO `gen_table_column` VALUES (1503, '179', 'is_auto_monitor', '是否自动监听会议：1是，2否', 'int(11)', 'Integer', 'isAutoMonitor', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 11, '', '2021-02-24 18:20:56', '', NULL);
INSERT INTO `gen_table_column` VALUES (1504, '190', 'id', '自增ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-02-26 10:11:43', '', NULL);
INSERT INTO `gen_table_column` VALUES (1505, '190', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-02-26 10:11:43', '', NULL);
INSERT INTO `gen_table_column` VALUES (1506, '190', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-02-26 10:11:43', '', NULL);
INSERT INTO `gen_table_column` VALUES (1507, '190', 'template_conference_id', '会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-02-26 10:11:43', '', NULL);
INSERT INTO `gen_table_column` VALUES (1508, '190', 'polling_scheme_id', '归属轮询方案ID', 'bigint(20)', 'Long', 'pollingSchemeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-02-26 10:11:43', '', NULL);
INSERT INTO `gen_table_column` VALUES (1509, '190', 'dept_id', '部门ID（部门也是FME终端，一种与会者）', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-02-26 10:11:43', '', NULL);
INSERT INTO `gen_table_column` VALUES (1510, '190', 'weight', '参会者顺序（权重倒叙排列）', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-02-26 10:11:43', '', NULL);
INSERT INTO `gen_table_column` VALUES (1511, '191', 'id', '自增ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1512, '191', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1513, '191', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1514, '191', 'template_conference_id', '会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1515, '191', 'polling_scheme_id', '归属轮询方案ID', 'bigint(20)', 'Long', 'pollingSchemeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1516, '191', 'remote_party', '终端的远程部分（唯一的）', 'varchar(64)', 'String', 'remoteParty', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1517, '191', 'is_cascade_main', '是否级联之主(1是，2否)', 'int(11)', 'Integer', 'isCascadeMain', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1518, '191', 'weight', '参会者顺序（权重倒叙排列）', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1519, '192', 'id', '自增ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1520, '192', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1521, '192', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1522, '192', 'template_conference_id', '会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1523, '192', 'polling_interval', '轮询时间间隔', 'int(11)', 'Integer', 'pollingInterval', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1524, '192', 'polling_state_first', '是否需要优先轮询地州（1是，2否）', 'int(11)', 'Integer', 'pollingStateFirst', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1525, '192', 'scheme_name', '轮询方案名', 'varchar(48)', 'String', 'schemeName', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 7, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1526, '192', 'weight', '轮询方案顺序，越大越靠前', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1527, '191', 'terminal_id', '终端ID', 'bigint(20)', 'Long', 'terminalId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, '', '2021-02-26 10:24:46', '', NULL);
INSERT INTO `gen_table_column` VALUES (1528, '191', 'polling_interval', '该参会者的特定的轮询间隔（如果该值存在，会覆盖轮询方案终端间隔）', 'int(11)', 'Integer', 'pollingInterval', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, '', '2021-02-26 10:28:19', '', NULL);
INSERT INTO `gen_table_column` VALUES (1534, '194', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1535, '194', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1536, '194', 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1537, '194', 'server_type', '注册服务器类型（1:FSBC, 2CUCM）', 'int(11)', 'Integer', 'serverType', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 4, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1538, '194', 'server_ip', '注册服务器ip地州', 'varchar(16)', 'String', 'serverIp', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1539, '194', 'server_port', '注册服务器端口', 'int(11)', 'Integer', 'serverPort', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1540, '194', 'server_path', '注册服务器访问路径', 'varchar(128)', 'String', 'serverPath', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1541, '194', 'username', '注册服务器用户名', 'varchar(32)', 'String', 'username', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 8, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1542, '194', 'password', '注册服务器密码', 'varchar(64)', 'String', 'password', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 9, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1543, '195', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:38:19');
INSERT INTO `gen_table_column` VALUES (1544, '195', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:38:19');
INSERT INTO `gen_table_column` VALUES (1545, '195', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:38:19');
INSERT INTO `gen_table_column` VALUES (1546, '195', 'name', '组名，最长32', 'varchar(32)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:38:19');
INSERT INTO `gen_table_column` VALUES (1548, '195', 'description', '备注信息', 'varchar(128)', 'String', 'description', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:38:19');
INSERT INTO `gen_table_column` VALUES (1549, '196', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:40:48');
INSERT INTO `gen_table_column` VALUES (1550, '196', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:40:48');
INSERT INTO `gen_table_column` VALUES (1551, '196', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:40:48');
INSERT INTO `gen_table_column` VALUES (1552, '196', 'cluster_id', 'FME集群ID', 'bigint(20)', 'Long', 'clusterId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:40:48');
INSERT INTO `gen_table_column` VALUES (1553, '196', 'fme_id', 'FME的ID', 'bigint(20)', 'Long', 'fmeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:40:48');
INSERT INTO `gen_table_column` VALUES (1554, '196', 'weight', '节点在集群中的权重值', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:40:48');
INSERT INTO `gen_table_column` VALUES (1555, '197', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:41:12');
INSERT INTO `gen_table_column` VALUES (1556, '197', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:41:12');
INSERT INTO `gen_table_column` VALUES (1557, '197', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:41:12');
INSERT INTO `gen_table_column` VALUES (1558, '197', 'dept_id', '分配给的租户', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:41:12');
INSERT INTO `gen_table_column` VALUES (1559, '197', 'fme_type', '1单节点，100集群', 'int(11)', 'Integer', 'fmeType', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 5, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:41:12');
INSERT INTO `gen_table_column` VALUES (1560, '197', 'fme_id', '当fme_type为1是，指向busi_fme的id字段，为100指向busi_fme_cluster的id字段', 'bigint(20)', 'Long', 'fmeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:41:12');
INSERT INTO `gen_table_column` VALUES (1561, '174', 'username', 'FME的连接用户名，同一个组下的用户名相同', 'varchar(32)', 'String', 'username', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 4, '', '2021-03-17 16:37:36', '', NULL);
INSERT INTO `gen_table_column` VALUES (1562, '174', 'password', 'FME的连接密码，同一个组下的密码相同', 'varchar(128)', 'String', 'password', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, '', '2021-03-17 16:37:36', '', NULL);
INSERT INTO `gen_table_column` VALUES (1563, '174', 'spare_fme_id', '备用FME（本节点宕机后指向的备用节点）', 'bigint(20)', 'Long', 'spareFmeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 12, '', '2021-03-17 16:37:36', '', NULL);
INSERT INTO `gen_table_column` VALUES (1564, '195', 'spare_fme_type', '备用fme类型，1单节点，100集群', 'int(11)', 'Integer', 'spareFmeType', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 5, '', '2021-03-19 13:45:12', '', NULL);
INSERT INTO `gen_table_column` VALUES (1565, '195', 'spare_fme_id', '当fme_type为1是，指向busi_fme的id字段，为100指向busi_fme_cluster的id字段', 'bigint(20)', 'Long', 'spareFmeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, '', '2021-03-19 13:45:12', '', NULL);
INSERT INTO `gen_table_column` VALUES (1566, '192', 'layout', '多分频轮询支持', 'varchar(32)', 'String', 'layout', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 9, '', '2021-04-01 18:12:05', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1568, '179', 'default_view_layout', '默认视图布局类型', 'varchar(32)', 'String', 'defaultViewLayout', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 14, '', '2021-04-08 14:40:48', '', NULL);
INSERT INTO `gen_table_column` VALUES (1569, '179', 'default_view_is_broadcast', '默认视图是否广播(1是，2否)', 'int(11)', 'Integer', 'defaultViewIsBroadcast', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 15, '', '2021-04-08 14:40:48', '', NULL);
INSERT INTO `gen_table_column` VALUES (1570, '179', 'default_view_is_display_self', '默认视图是否显示自己', 'int(11)', 'Integer', 'defaultViewIsDisplaySelf', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 16, '', '2021-04-08 14:40:48', '', NULL);
INSERT INTO `gen_table_column` VALUES (1571, '179', 'default_view_is_fill', '默认视图是否补位', 'int(11)', 'Integer', 'defaultViewIsFill', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 17, '', '2021-04-08 14:40:48', '', NULL);
INSERT INTO `gen_table_column` VALUES (1572, '198', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:27');
INSERT INTO `gen_table_column` VALUES (1573, '198', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:27');
INSERT INTO `gen_table_column` VALUES (1574, '198', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:27');
INSERT INTO `gen_table_column` VALUES (1576, '198', 'cell_sequence_number', '单元格序号', 'int(11)', 'Integer', 'cellSequenceNumber', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:27');
INSERT INTO `gen_table_column` VALUES (1577, '198', 'operation', '分频单元格对应的操作，默认为选看101，105轮询', 'int(11)', 'Integer', 'operation', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:27');
INSERT INTO `gen_table_column` VALUES (1578, '199', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:45');
INSERT INTO `gen_table_column` VALUES (1579, '199', 'create_time', NULL, 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:45');
INSERT INTO `gen_table_column` VALUES (1580, '199', 'update_time', NULL, 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:45');
INSERT INTO `gen_table_column` VALUES (1581, '199', 'template_conference_id', '关联的会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:45');
INSERT INTO `gen_table_column` VALUES (1582, '199', 'dept_id', '部门ID（部门也是FME终端，一种与会者）', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:45');
INSERT INTO `gen_table_column` VALUES (1583, '199', 'weight', '参会者顺序（权重倒叙排列）', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:45');
INSERT INTO `gen_table_column` VALUES (1584, '200', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:44:03');
INSERT INTO `gen_table_column` VALUES (1585, '200', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:44:03');
INSERT INTO `gen_table_column` VALUES (1586, '200', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:44:03');
INSERT INTO `gen_table_column` VALUES (1587, '200', 'template_conference_id', '关联的会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:44:03');
INSERT INTO `gen_table_column` VALUES (1588, '200', 'template_participant_id', '参会终端ID，关联busi_template_participant的ID', 'bigint(20)', 'Long', 'templateParticipantId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:44:03');
INSERT INTO `gen_table_column` VALUES (1589, '200', 'weight', '参会者顺序（权重倒叙排列）', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:44:03');
INSERT INTO `gen_table_column` VALUES (1591, '198', 'template_conference_id', '关联的会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-04-08 15:26:36', '', NULL);
INSERT INTO `gen_table_column` VALUES (1592, '200', 'cell_sequence_number', '多分频单元格序号', 'int(11)', 'Integer', 'cellSequenceNumber', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, '', '2021-04-08 15:49:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (1593, '198', 'is_fixed', '分频单元格是否固定', 'int(11)', 'Integer', 'isFixed', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, '', '2021-04-08 16:05:55', '', NULL);
INSERT INTO `gen_table_column` VALUES (1594, '192', 'is_broadcast', '是否广播(1是，2否)', 'int(11)', 'Integer', 'isBroadcast', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 10, '', '2021-04-09 13:50:23', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1595, '192', 'is_display_self', '是否显示自己(1是，2否)', 'int(11)', 'Integer', 'isDisplaySelf', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 11, '', '2021-04-09 13:50:23', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1596, '192', 'is_fill', '是否补位(1是，2否)', 'int(11)', 'Integer', 'isFill', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 12, '', '2021-04-09 13:50:23', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1597, '179', 'polling_interval', '轮询时间间隔', 'int(11)', 'Integer', 'pollingInterval', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 18, '', '2021-04-13 16:28:52', '', NULL);
INSERT INTO `gen_table_column` VALUES (1598, '179', 'master_participant_id', '主会场ID', 'bigint(20)', 'Long', 'masterParticipantId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 14, '', '2021-04-15 17:46:50', '', NULL);
INSERT INTO `gen_table_column` VALUES (1600, '201', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1601, '201', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1602, '201', 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1603, '201', 'name', '注册服务器名字', 'varchar(32)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1604, '201', 'ip', '注册服务器ip地州', 'varchar(16)', 'String', 'ip', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1605, '201', 'port', '注册服务器端口', 'int(11)', 'Integer', 'port', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1606, '201', 'username', '注册服务器用户名', 'varchar(32)', 'String', 'username', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 7, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1607, '201', 'password', '注册服务器密码', 'varchar(64)', 'String', 'password', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1608, '202', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:49');
INSERT INTO `gen_table_column` VALUES (1609, '202', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:49');
INSERT INTO `gen_table_column` VALUES (1610, '202', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:49');
INSERT INTO `gen_table_column` VALUES (1611, '202', 'sfbc_server_id', 'SFBC服务器的id', 'bigint(20)', 'Long', 'sfbcServerId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:49');
INSERT INTO `gen_table_column` VALUES (1612, '202', 'dept_id', '部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:49');
INSERT INTO `gen_table_column` VALUES (1618, '203', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:49:56');
INSERT INTO `gen_table_column` VALUES (1619, '203', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:49:56');
INSERT INTO `gen_table_column` VALUES (1620, '203', 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:49:56');
INSERT INTO `gen_table_column` VALUES (1621, '203', 'name', '注册服务器名字', 'varchar(32)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:49:56');
INSERT INTO `gen_table_column` VALUES (1622, '203', 'ip', '注册服务器ip地州', 'varchar(16)', 'String', 'ip', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:49:56');
INSERT INTO `gen_table_column` VALUES (1623, '203', 'port', '注册服务器端口', 'int(11)', 'Integer', 'port', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:49:56');
INSERT INTO `gen_table_column` VALUES (1624, '203', 'username', '注册服务器用户名', 'varchar(32)', 'String', 'username', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 7, 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:49:56');
INSERT INTO `gen_table_column` VALUES (1625, '203', 'password', '注册服务器密码', 'varchar(64)', 'String', 'password', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:49:56');
INSERT INTO `gen_table_column` VALUES (1626, '204', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:50:17');
INSERT INTO `gen_table_column` VALUES (1627, '204', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:50:17');
INSERT INTO `gen_table_column` VALUES (1628, '204', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:50:17');
INSERT INTO `gen_table_column` VALUES (1629, '204', 'fsbc_server_id', 'FSBC服务器的id', 'bigint(20)', 'Long', 'fsbcServerId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:50:17');
INSERT INTO `gen_table_column` VALUES (1630, '204', 'dept_id', '部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-04-21 11:49:16', '', '2021-04-21 11:50:17');
INSERT INTO `gen_table_column` VALUES (1631, '205', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1632, '205', 'create_time', '终端创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1633, '205', 'update_time', '终端修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1634, '205', 'create_user_id', '创建者ID', 'bigint(20)', 'Long', 'createUserId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1635, '205', 'create_user_name', '创建者用户名', 'varchar(32)', 'String', 'createUserName', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 5, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1636, '205', 'dept_id', '终端所属部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1637, '205', 'ip', '设备的IP地址', 'varchar(16)', 'String', 'ip', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1638, '205', 'number', '设备号，设备唯一标识（如果是sfbc终端，则对应凭据）', 'varchar(16)', 'String', 'number', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1639, '205', 'camera_ip', '摄像头IP地址', 'varchar(16)', 'String', 'cameraIp', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 9, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1640, '205', 'name', '终端显示名字', 'varchar(32)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 10, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1641, '205', 'type', '终端类型，枚举值int类型', 'int(11)', 'Integer', 'type', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 11, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1642, '205', 'online_status', '终端状态：1在线，2离线', 'int(11)', 'Integer', 'onlineStatus', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'radio', '', 12, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1643, '205', 'protocol', '协议', 'varchar(16)', 'String', 'protocol', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 13, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1644, '205', 'registration_time', 'FSBC终端最后注册时间', 'datetime', 'Date', 'registrationTime', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'datetime', '', 14, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1645, '205', 'intranet_ip', 'FSBC终端内网IP', 'varchar(16)', 'String', 'intranetIp', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 15, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1646, '205', 'port', 'FSBC终端端口', 'int(11)', 'Integer', 'port', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 16, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1647, '205', 'transport', 'FSBC终端的传输协议（TLS,TCP,UDP）', 'varchar(8)', 'String', 'transport', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 17, 'superAdmin', '2021-04-21 11:51:12', '', '2021-04-21 11:51:38');
INSERT INTO `gen_table_column` VALUES (1648, '205', 'password', '密码', 'varchar(64)', 'String', 'password', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 13, '', '2021-04-22 09:52:45', '', NULL);
INSERT INTO `gen_table_column` VALUES (1649, '205', 'credential', 'fsbc账号', 'varchar(32)', 'String', 'credential', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 13, '', '2021-04-22 10:40:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (1650, '205', 'fsbc_server_id', 'fsbc服务器ID', 'bigint(20)', 'Long', 'fsbcServerId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 15, '', '2021-04-22 10:40:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (1651, '205', 'vendor', 'FSBC终端厂商信息', 'varchar(32)', 'String', 'vendor', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 17, '', '2021-04-23 10:32:30', '', NULL);

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2021-01-18 10:39:23', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2021-01-18 10:39:23', '', NULL, '初始化密码 123456');
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2021-01-18 10:39:23', '', NULL, '深色主题theme-dark，浅色主题theme-light');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE,
  INDEX `ancestors`(`ancestors`) USING BTREE,
  INDEX `parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 223 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, 0, '0', '云平台', 0, 'superAdmin', '15888888888', NULL, '0', '0', 'superAdmin', '2021-01-18 10:39:18', 'superAdmin', '2021-04-26 11:34:17');
INSERT INTO `sys_dept` VALUES (100, 1, '0,1', '多媒体交换平台', 0, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-18 10:39:18', 'superAdmin', '2021-04-26 11:34:16');
INSERT INTO `sys_dept` VALUES (220, 100, '0,1,100', '三门峡云平台', 1, NULL, NULL, NULL, '0', '0', 'superAdmin', '2021-04-26 18:37:35', '', NULL);
INSERT INTO `sys_dept` VALUES (221, 220, '0,1,100,220', '政法委', 1, NULL, NULL, NULL, '0', '0', 'superAdmin', '2021-04-26 18:38:30', '', NULL);
INSERT INTO `sys_dept` VALUES (222, 220, '0,1,100,220', '三门峡', 2, NULL, NULL, NULL, '0', '0', 'superAdmin', '2021-04-26 18:38:48', '', NULL);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '性别未知');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '默认分组');
INSERT INTO `sys_dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '系统分组');
INSERT INTO `sys_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '系统默认是');
INSERT INTO `sys_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '系统默认否');
INSERT INTO `sys_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '通知');
INSERT INTO `sys_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '公告');
INSERT INTO `sys_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '关闭状态');
INSERT INTO `sys_dict_data` VALUES (18, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '新增操作');
INSERT INTO `sys_dict_data` VALUES (19, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '修改操作');
INSERT INTO `sys_dict_data` VALUES (20, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '删除操作');
INSERT INTO `sys_dict_data` VALUES (21, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '授权操作');
INSERT INTO `sys_dict_data` VALUES (22, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '导出操作');
INSERT INTO `sys_dict_data` VALUES (23, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '导入操作');
INSERT INTO `sys_dict_data` VALUES (24, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:23', '', NULL, '强退操作');
INSERT INTO `sys_dict_data` VALUES (25, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2021-01-18 10:39:23', '', NULL, '生成操作');
INSERT INTO `sys_dict_data` VALUES (26, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:23', '', NULL, '清空操作');
INSERT INTO `sys_dict_data` VALUES (27, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2021-01-18 10:39:23', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (28, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:23', '', NULL, '停用状态');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '登录状态列表');

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime(0) NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 809 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (30, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-27 18:19:30');
INSERT INTO `sys_logininfor` VALUES (31, 'altAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-27 18:19:41');
INSERT INTO `sys_logininfor` VALUES (32, 'altAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-27 18:21:33');
INSERT INTO `sys_logininfor` VALUES (33, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-27 18:21:42');
INSERT INTO `sys_logininfor` VALUES (34, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 09:36:10');
INSERT INTO `sys_logininfor` VALUES (35, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 10:12:39');
INSERT INTO `sys_logininfor` VALUES (36, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 10:12:46');
INSERT INTO `sys_logininfor` VALUES (37, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 10:18:44');
INSERT INTO `sys_logininfor` VALUES (38, 'altAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-01-28 10:18:50');
INSERT INTO `sys_logininfor` VALUES (39, 'altAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 10:18:55');
INSERT INTO `sys_logininfor` VALUES (40, 'altAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 10:21:18');
INSERT INTO `sys_logininfor` VALUES (41, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 10:21:26');
INSERT INTO `sys_logininfor` VALUES (42, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 10:22:00');
INSERT INTO `sys_logininfor` VALUES (43, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-01-28 10:22:07');
INSERT INTO `sys_logininfor` VALUES (44, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 10:22:13');
INSERT INTO `sys_logininfor` VALUES (45, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-01-28 10:42:16');
INSERT INTO `sys_logininfor` VALUES (46, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 13:02:40');
INSERT INTO `sys_logininfor` VALUES (47, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-01-28 13:02:50');
INSERT INTO `sys_logininfor` VALUES (48, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 13:02:54');
INSERT INTO `sys_logininfor` VALUES (49, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 16:34:07');
INSERT INTO `sys_logininfor` VALUES (50, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-01-28 16:34:18');
INSERT INTO `sys_logininfor` VALUES (51, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 16:34:22');
INSERT INTO `sys_logininfor` VALUES (52, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 16:37:15');
INSERT INTO `sys_logininfor` VALUES (53, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 16:37:22');
INSERT INTO `sys_logininfor` VALUES (54, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 17:14:28');
INSERT INTO `sys_logininfor` VALUES (55, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 17:14:37');
INSERT INTO `sys_logininfor` VALUES (56, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 17:44:34');
INSERT INTO `sys_logininfor` VALUES (57, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 17:45:01');
INSERT INTO `sys_logininfor` VALUES (58, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 18:57:20');
INSERT INTO `sys_logininfor` VALUES (59, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 18:57:41');
INSERT INTO `sys_logininfor` VALUES (60, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-01-28 19:36:20');
INSERT INTO `sys_logininfor` VALUES (61, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-01-28 19:36:24');
INSERT INTO `sys_logininfor` VALUES (62, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-28 19:36:30');
INSERT INTO `sys_logininfor` VALUES (63, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-28 19:37:24');
INSERT INTO `sys_logininfor` VALUES (64, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 19:45:24');
INSERT INTO `sys_logininfor` VALUES (65, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-01-28 19:46:17');
INSERT INTO `sys_logininfor` VALUES (66, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 09:46:01');
INSERT INTO `sys_logininfor` VALUES (67, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-01-29 13:12:19');
INSERT INTO `sys_logininfor` VALUES (68, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-01-29 13:12:27');
INSERT INTO `sys_logininfor` VALUES (69, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 13:12:30');
INSERT INTO `sys_logininfor` VALUES (70, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 13:15:30');
INSERT INTO `sys_logininfor` VALUES (71, 'altAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 13:16:02');
INSERT INTO `sys_logininfor` VALUES (72, 'altAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 13:45:09');
INSERT INTO `sys_logininfor` VALUES (73, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-01-29 13:45:23');
INSERT INTO `sys_logininfor` VALUES (74, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 13:45:29');
INSERT INTO `sys_logininfor` VALUES (75, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 13:58:33');
INSERT INTO `sys_logininfor` VALUES (76, 'altAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 13:58:47');
INSERT INTO `sys_logininfor` VALUES (77, 'altAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 14:01:26');
INSERT INTO `sys_logininfor` VALUES (78, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 14:01:40');
INSERT INTO `sys_logininfor` VALUES (79, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-01-29 14:34:00');
INSERT INTO `sys_logininfor` VALUES (80, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-01-29 14:34:05');
INSERT INTO `sys_logininfor` VALUES (81, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 17:30:32');
INSERT INTO `sys_logininfor` VALUES (82, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 17:30:39');
INSERT INTO `sys_logininfor` VALUES (83, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 17:33:52');
INSERT INTO `sys_logininfor` VALUES (84, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 17:34:01');
INSERT INTO `sys_logininfor` VALUES (85, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 17:34:51');
INSERT INTO `sys_logininfor` VALUES (86, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 17:34:56');
INSERT INTO `sys_logininfor` VALUES (87, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 17:58:01');
INSERT INTO `sys_logininfor` VALUES (88, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 17:58:07');
INSERT INTO `sys_logininfor` VALUES (89, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 18:36:01');
INSERT INTO `sys_logininfor` VALUES (90, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:36:03');
INSERT INTO `sys_logininfor` VALUES (91, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:36:11');
INSERT INTO `sys_logininfor` VALUES (92, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:36:11');
INSERT INTO `sys_logininfor` VALUES (93, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:37:35');
INSERT INTO `sys_logininfor` VALUES (94, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:37:35');
INSERT INTO `sys_logininfor` VALUES (95, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:40:07');
INSERT INTO `sys_logininfor` VALUES (96, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:40:07');
INSERT INTO `sys_logininfor` VALUES (97, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:43:23');
INSERT INTO `sys_logininfor` VALUES (98, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:43:23');
INSERT INTO `sys_logininfor` VALUES (99, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:43:49');
INSERT INTO `sys_logininfor` VALUES (100, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:43:49');
INSERT INTO `sys_logininfor` VALUES (101, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:44:37');
INSERT INTO `sys_logininfor` VALUES (102, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:44:37');
INSERT INTO `sys_logininfor` VALUES (103, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:44:55');
INSERT INTO `sys_logininfor` VALUES (104, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:44:55');
INSERT INTO `sys_logininfor` VALUES (105, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-01-29 18:45:08');
INSERT INTO `sys_logininfor` VALUES (106, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:45:16');
INSERT INTO `sys_logininfor` VALUES (107, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:45:16');
INSERT INTO `sys_logininfor` VALUES (108, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-01-29 18:45:51');
INSERT INTO `sys_logininfor` VALUES (109, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-01-29 18:45:55');
INSERT INTO `sys_logininfor` VALUES (110, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 18:45:59');
INSERT INTO `sys_logininfor` VALUES (111, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 18:45:59');
INSERT INTO `sys_logininfor` VALUES (112, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 18:46:08');
INSERT INTO `sys_logininfor` VALUES (113, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 18:46:12');
INSERT INTO `sys_logininfor` VALUES (114, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-01-29 18:46:41');
INSERT INTO `sys_logininfor` VALUES (115, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:46:48');
INSERT INTO `sys_logininfor` VALUES (116, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:47:44');
INSERT INTO `sys_logininfor` VALUES (117, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-01-29 18:48:03');
INSERT INTO `sys_logininfor` VALUES (118, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:48:09');
INSERT INTO `sys_logininfor` VALUES (119, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:48:09');
INSERT INTO `sys_logininfor` VALUES (120, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-01-29 18:48:43');
INSERT INTO `sys_logininfor` VALUES (121, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-01-29 18:48:46');
INSERT INTO `sys_logininfor` VALUES (122, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:48:50');
INSERT INTO `sys_logininfor` VALUES (123, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:48:58');
INSERT INTO `sys_logininfor` VALUES (124, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-01-29 18:50:26');
INSERT INTO `sys_logininfor` VALUES (125, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-01-29 18:50:27');
INSERT INTO `sys_logininfor` VALUES (126, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-01-29 18:50:36');
INSERT INTO `sys_logininfor` VALUES (127, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:50:45');
INSERT INTO `sys_logininfor` VALUES (128, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码已失效', '2021-01-29 18:56:30');
INSERT INTO `sys_logininfor` VALUES (129, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 18:56:34');
INSERT INTO `sys_logininfor` VALUES (130, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-30 10:12:35');
INSERT INTO `sys_logininfor` VALUES (131, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-30 10:13:59');
INSERT INTO `sys_logininfor` VALUES (132, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-30 10:14:07');
INSERT INTO `sys_logininfor` VALUES (133, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-30 10:25:40');
INSERT INTO `sys_logininfor` VALUES (134, 'altAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-30 10:25:55');
INSERT INTO `sys_logininfor` VALUES (135, 'altAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-30 10:27:44');
INSERT INTO `sys_logininfor` VALUES (136, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-30 10:28:00');
INSERT INTO `sys_logininfor` VALUES (137, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-01-30 11:04:32');
INSERT INTO `sys_logininfor` VALUES (138, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-30 11:04:42');
INSERT INTO `sys_logininfor` VALUES (139, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-30 12:04:52');
INSERT INTO `sys_logininfor` VALUES (140, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-30 12:04:57');
INSERT INTO `sys_logininfor` VALUES (141, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-30 13:08:05');
INSERT INTO `sys_logininfor` VALUES (142, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码已失效', '2021-01-30 13:14:03');
INSERT INTO `sys_logininfor` VALUES (143, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-01-30 13:14:07');
INSERT INTO `sys_logininfor` VALUES (144, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-30 13:14:10');
INSERT INTO `sys_logininfor` VALUES (145, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-30 13:52:17');
INSERT INTO `sys_logininfor` VALUES (146, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-30 13:52:34');
INSERT INTO `sys_logininfor` VALUES (147, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-01 09:33:33');
INSERT INTO `sys_logininfor` VALUES (148, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-01 09:33:37');
INSERT INTO `sys_logininfor` VALUES (149, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-01 13:28:26');
INSERT INTO `sys_logininfor` VALUES (150, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-02-01 13:28:36');
INSERT INTO `sys_logininfor` VALUES (151, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-01 13:28:43');
INSERT INTO `sys_logininfor` VALUES (152, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-01 15:43:07');
INSERT INTO `sys_logininfor` VALUES (153, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-01 15:43:16');
INSERT INTO `sys_logininfor` VALUES (154, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-01 15:43:21');
INSERT INTO `sys_logininfor` VALUES (155, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-02 09:33:43');
INSERT INTO `sys_logininfor` VALUES (156, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-02 18:08:06');
INSERT INTO `sys_logininfor` VALUES (157, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-03 09:43:02');
INSERT INTO `sys_logininfor` VALUES (158, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 09:43:06');
INSERT INTO `sys_logininfor` VALUES (159, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 09:43:25');
INSERT INTO `sys_logininfor` VALUES (160, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 11:47:34');
INSERT INTO `sys_logininfor` VALUES (161, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 14:39:32');
INSERT INTO `sys_logininfor` VALUES (162, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 18:27:49');
INSERT INTO `sys_logininfor` VALUES (163, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-03 18:28:13');
INSERT INTO `sys_logininfor` VALUES (164, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 18:28:16');
INSERT INTO `sys_logininfor` VALUES (165, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 18:35:29');
INSERT INTO `sys_logininfor` VALUES (166, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 18:35:35');
INSERT INTO `sys_logininfor` VALUES (167, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 19:10:33');
INSERT INTO `sys_logininfor` VALUES (168, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 19:10:38');
INSERT INTO `sys_logininfor` VALUES (169, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 19:15:28');
INSERT INTO `sys_logininfor` VALUES (170, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 19:15:34');
INSERT INTO `sys_logininfor` VALUES (171, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 19:22:59');
INSERT INTO `sys_logininfor` VALUES (172, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 19:23:10');
INSERT INTO `sys_logininfor` VALUES (173, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 19:33:11');
INSERT INTO `sys_logininfor` VALUES (174, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 19:33:19');
INSERT INTO `sys_logininfor` VALUES (175, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 19:34:16');
INSERT INTO `sys_logininfor` VALUES (176, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 19:34:24');
INSERT INTO `sys_logininfor` VALUES (177, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 20:19:11');
INSERT INTO `sys_logininfor` VALUES (178, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 20:19:22');
INSERT INTO `sys_logininfor` VALUES (179, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-04 11:38:45');
INSERT INTO `sys_logininfor` VALUES (180, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-04 11:38:51');
INSERT INTO `sys_logininfor` VALUES (181, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-04 12:02:33');
INSERT INTO `sys_logininfor` VALUES (182, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-04 12:02:45');
INSERT INTO `sys_logininfor` VALUES (183, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-04 12:02:49');
INSERT INTO `sys_logininfor` VALUES (184, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-04 12:02:54');
INSERT INTO `sys_logininfor` VALUES (185, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-04 15:49:16');
INSERT INTO `sys_logininfor` VALUES (186, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-04 15:49:23');
INSERT INTO `sys_logininfor` VALUES (187, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-05 09:47:01');
INSERT INTO `sys_logininfor` VALUES (188, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-05 11:34:58');
INSERT INTO `sys_logininfor` VALUES (189, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-05 11:36:18');
INSERT INTO `sys_logininfor` VALUES (190, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-05 11:36:39');
INSERT INTO `sys_logininfor` VALUES (191, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-05 11:36:49');
INSERT INTO `sys_logininfor` VALUES (192, 'admin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-05 12:51:49');
INSERT INTO `sys_logininfor` VALUES (193, 'admin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-05 12:51:53');
INSERT INTO `sys_logininfor` VALUES (194, 'superAdmin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '验证码错误', '2021-02-05 12:52:02');
INSERT INTO `sys_logininfor` VALUES (195, 'superAdmin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-02-05 12:52:06');
INSERT INTO `sys_logininfor` VALUES (196, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-05 12:58:17');
INSERT INTO `sys_logininfor` VALUES (197, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-05 12:58:33');
INSERT INTO `sys_logininfor` VALUES (198, 'admin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-05 13:49:43');
INSERT INTO `sys_logininfor` VALUES (199, 'admin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-05 13:49:46');
INSERT INTO `sys_logininfor` VALUES (200, 'superAdmin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-02-05 13:50:08');
INSERT INTO `sys_logininfor` VALUES (201, 'superAdmin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '0', '退出成功', '2021-02-05 14:06:47');
INSERT INTO `sys_logininfor` VALUES (202, 'admin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-05 14:07:31');
INSERT INTO `sys_logininfor` VALUES (203, 'superAdmin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '验证码错误', '2021-02-05 14:07:47');
INSERT INTO `sys_logininfor` VALUES (204, 'superAdmin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-05 14:07:55');
INSERT INTO `sys_logininfor` VALUES (205, 'superAdmin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-02-05 14:08:10');
INSERT INTO `sys_logininfor` VALUES (206, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-05 19:57:02');
INSERT INTO `sys_logininfor` VALUES (207, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-05 19:57:07');
INSERT INTO `sys_logininfor` VALUES (208, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-07 09:52:33');
INSERT INTO `sys_logininfor` VALUES (209, 'ylzAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-02-08 12:24:11');
INSERT INTO `sys_logininfor` VALUES (210, 'ylzAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-02-08 12:24:41');
INSERT INTO `sys_logininfor` VALUES (211, 'ylzAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-02-08 12:25:02');
INSERT INTO `sys_logininfor` VALUES (212, 'admin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-08 17:14:50');
INSERT INTO `sys_logininfor` VALUES (213, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-08 17:15:00');
INSERT INTO `sys_logininfor` VALUES (214, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-08 17:15:06');
INSERT INTO `sys_logininfor` VALUES (215, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-08 17:15:11');
INSERT INTO `sys_logininfor` VALUES (216, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-08 17:19:27');
INSERT INTO `sys_logininfor` VALUES (217, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-08 17:19:33');
INSERT INTO `sys_logininfor` VALUES (218, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-08 18:12:23');
INSERT INTO `sys_logininfor` VALUES (219, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-08 18:12:29');
INSERT INTO `sys_logininfor` VALUES (220, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-08 19:04:01');
INSERT INTO `sys_logininfor` VALUES (221, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-09 12:01:47');
INSERT INTO `sys_logininfor` VALUES (222, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-09 12:42:11');
INSERT INTO `sys_logininfor` VALUES (223, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-09 12:42:15');
INSERT INTO `sys_logininfor` VALUES (224, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-09 15:00:33');
INSERT INTO `sys_logininfor` VALUES (225, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-09 15:54:38');
INSERT INTO `sys_logininfor` VALUES (226, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-09 15:54:48');
INSERT INTO `sys_logininfor` VALUES (227, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-09 15:54:56');
INSERT INTO `sys_logininfor` VALUES (228, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-09 15:55:02');
INSERT INTO `sys_logininfor` VALUES (229, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-19 10:01:11');
INSERT INTO `sys_logininfor` VALUES (230, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-19 10:37:25');
INSERT INTO `sys_logininfor` VALUES (231, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-02-19 11:04:08');
INSERT INTO `sys_logininfor` VALUES (232, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-19 11:04:18');
INSERT INTO `sys_logininfor` VALUES (233, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-19 14:51:17');
INSERT INTO `sys_logininfor` VALUES (234, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-19 14:51:25');
INSERT INTO `sys_logininfor` VALUES (235, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-19 14:51:37');
INSERT INTO `sys_logininfor` VALUES (236, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-19 14:51:48');
INSERT INTO `sys_logininfor` VALUES (237, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-19 14:51:54');
INSERT INTO `sys_logininfor` VALUES (238, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-19 15:32:03');
INSERT INTO `sys_logininfor` VALUES (239, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-19 15:32:18');
INSERT INTO `sys_logininfor` VALUES (240, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-19 15:38:57');
INSERT INTO `sys_logininfor` VALUES (241, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-19 15:39:37');
INSERT INTO `sys_logininfor` VALUES (242, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-19 16:25:58');
INSERT INTO `sys_logininfor` VALUES (243, 'admin', '172.16.100.151', '内网IP', 'Safari', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-19 18:04:56');
INSERT INTO `sys_logininfor` VALUES (244, 'admin', '172.16.100.151', '内网IP', 'Safari', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-19 18:05:05');
INSERT INTO `sys_logininfor` VALUES (245, 'admin', '172.16.100.151', '内网IP', 'Safari', 'Mac OS X', '0', '登录成功', '2021-02-19 18:05:19');
INSERT INTO `sys_logininfor` VALUES (246, 'admin', '172.16.100.151', '内网IP', 'Safari', 'Mac OS X', '0', '退出成功', '2021-02-19 18:05:48');
INSERT INTO `sys_logininfor` VALUES (247, 'superAdmin', '172.16.100.151', '内网IP', 'Safari', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-19 18:06:06');
INSERT INTO `sys_logininfor` VALUES (248, 'superAdmin', '172.16.100.151', '内网IP', 'Safari', 'Mac OS X', '0', '登录成功', '2021-02-19 18:06:16');
INSERT INTO `sys_logininfor` VALUES (249, 'superAdmin', '172.16.100.88', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-02-19 18:16:50');
INSERT INTO `sys_logininfor` VALUES (250, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码已失效', '2021-02-20 09:40:37');
INSERT INTO `sys_logininfor` VALUES (251, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-20 09:40:43');
INSERT INTO `sys_logininfor` VALUES (252, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (253, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (254, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (255, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (256, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (257, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (258, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (259, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (260, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-22 10:02:01');
INSERT INTO `sys_logininfor` VALUES (261, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-23 10:37:23');
INSERT INTO `sys_logininfor` VALUES (262, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-23 10:58:33');
INSERT INTO `sys_logininfor` VALUES (263, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-23 10:58:37');
INSERT INTO `sys_logininfor` VALUES (264, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-23 10:58:59');
INSERT INTO `sys_logininfor` VALUES (265, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-23 10:59:05');
INSERT INTO `sys_logininfor` VALUES (266, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-23 12:30:04');
INSERT INTO `sys_logininfor` VALUES (267, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-23 14:20:02');
INSERT INTO `sys_logininfor` VALUES (268, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-23 14:20:13');
INSERT INTO `sys_logininfor` VALUES (269, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-23 16:32:17');
INSERT INTO `sys_logininfor` VALUES (270, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-23 16:32:28');
INSERT INTO `sys_logininfor` VALUES (271, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-23 16:32:33');
INSERT INTO `sys_logininfor` VALUES (272, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-23 16:32:42');
INSERT INTO `sys_logininfor` VALUES (273, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-23 16:32:46');
INSERT INTO `sys_logininfor` VALUES (274, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-23 16:32:54');
INSERT INTO `sys_logininfor` VALUES (275, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-23 16:33:01');
INSERT INTO `sys_logininfor` VALUES (276, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-23 16:33:07');
INSERT INTO `sys_logininfor` VALUES (277, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-23 19:47:33');
INSERT INTO `sys_logininfor` VALUES (278, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-23 20:27:33');
INSERT INTO `sys_logininfor` VALUES (279, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-24 09:56:00');
INSERT INTO `sys_logininfor` VALUES (280, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-24 18:38:20');
INSERT INTO `sys_logininfor` VALUES (281, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-25 18:17:29');
INSERT INTO `sys_logininfor` VALUES (282, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-25 18:17:37');
INSERT INTO `sys_logininfor` VALUES (283, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-26 09:40:19');
INSERT INTO `sys_logininfor` VALUES (284, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-26 10:11:12');
INSERT INTO `sys_logininfor` VALUES (285, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-26 10:11:18');
INSERT INTO `sys_logininfor` VALUES (286, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-26 10:34:48');
INSERT INTO `sys_logininfor` VALUES (287, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-26 10:37:09');
INSERT INTO `sys_logininfor` VALUES (288, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-26 15:50:39');
INSERT INTO `sys_logininfor` VALUES (289, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-26 15:52:12');
INSERT INTO `sys_logininfor` VALUES (290, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-26 17:28:50');
INSERT INTO `sys_logininfor` VALUES (291, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-28 10:04:21');
INSERT INTO `sys_logininfor` VALUES (292, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-28 15:32:48');
INSERT INTO `sys_logininfor` VALUES (293, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-28 15:32:54');
INSERT INTO `sys_logininfor` VALUES (294, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-01 09:39:01');
INSERT INTO `sys_logininfor` VALUES (295, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-01 09:39:24');
INSERT INTO `sys_logininfor` VALUES (296, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-01 09:41:06');
INSERT INTO `sys_logininfor` VALUES (297, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-01 09:41:10');
INSERT INTO `sys_logininfor` VALUES (298, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-01 11:09:20');
INSERT INTO `sys_logininfor` VALUES (299, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-01 11:09:27');
INSERT INTO `sys_logininfor` VALUES (300, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-01 11:25:11');
INSERT INTO `sys_logininfor` VALUES (301, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-01 11:25:15');
INSERT INTO `sys_logininfor` VALUES (302, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-01 13:48:17');
INSERT INTO `sys_logininfor` VALUES (303, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-01 14:06:03');
INSERT INTO `sys_logininfor` VALUES (304, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-01 14:06:10');
INSERT INTO `sys_logininfor` VALUES (305, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-01 14:18:53');
INSERT INTO `sys_logininfor` VALUES (306, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-01 14:19:00');
INSERT INTO `sys_logininfor` VALUES (307, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-03-01 15:32:48');
INSERT INTO `sys_logininfor` VALUES (308, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-03-01 15:32:53');
INSERT INTO `sys_logininfor` VALUES (309, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-03-01 15:32:59');
INSERT INTO `sys_logininfor` VALUES (310, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-01 15:33:07');
INSERT INTO `sys_logininfor` VALUES (311, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-01 17:27:56');
INSERT INTO `sys_logininfor` VALUES (312, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-01 17:34:46');
INSERT INTO `sys_logininfor` VALUES (313, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-01 17:34:55');
INSERT INTO `sys_logininfor` VALUES (314, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-02 09:41:42');
INSERT INTO `sys_logininfor` VALUES (315, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-02 09:52:53');
INSERT INTO `sys_logininfor` VALUES (316, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-02 09:53:08');
INSERT INTO `sys_logininfor` VALUES (317, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 09:53:59');
INSERT INTO `sys_logininfor` VALUES (318, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-03 09:56:48');
INSERT INTO `sys_logininfor` VALUES (319, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 09:56:57');
INSERT INTO `sys_logininfor` VALUES (320, 'superAdmin', '172.16.101.230', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-03-03 10:32:51');
INSERT INTO `sys_logininfor` VALUES (321, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-03 10:38:39');
INSERT INTO `sys_logininfor` VALUES (322, 'admin', '172.16.101.230', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-03-03 10:39:33');
INSERT INTO `sys_logininfor` VALUES (323, 'superAdmin', '172.16.101.230', '内网IP', 'Firefox 8', 'Mac OS X', '1', '验证码错误', '2021-03-03 10:39:44');
INSERT INTO `sys_logininfor` VALUES (324, 'superAdmin', '172.16.101.230', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-03-03 10:39:47');
INSERT INTO `sys_logininfor` VALUES (325, 'superAdmin', '172.16.101.230', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-03-03 10:39:55');
INSERT INTO `sys_logininfor` VALUES (326, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-03 10:44:10');
INSERT INTO `sys_logininfor` VALUES (327, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-03 11:23:09');
INSERT INTO `sys_logininfor` VALUES (328, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 11:23:14');
INSERT INTO `sys_logininfor` VALUES (329, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-03 11:24:45');
INSERT INTO `sys_logininfor` VALUES (330, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 11:24:53');
INSERT INTO `sys_logininfor` VALUES (331, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-03 11:25:45');
INSERT INTO `sys_logininfor` VALUES (332, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 11:25:53');
INSERT INTO `sys_logininfor` VALUES (333, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-03 11:28:13');
INSERT INTO `sys_logininfor` VALUES (334, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 11:28:19');
INSERT INTO `sys_logininfor` VALUES (335, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-03 11:30:37');
INSERT INTO `sys_logininfor` VALUES (336, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 11:30:41');
INSERT INTO `sys_logininfor` VALUES (337, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-03 11:34:39');
INSERT INTO `sys_logininfor` VALUES (338, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 11:34:54');
INSERT INTO `sys_logininfor` VALUES (339, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-03 11:39:15');
INSERT INTO `sys_logininfor` VALUES (340, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-03 12:05:31');
INSERT INTO `sys_logininfor` VALUES (341, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-03 12:25:42');
INSERT INTO `sys_logininfor` VALUES (342, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-03 14:21:53');
INSERT INTO `sys_logininfor` VALUES (343, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 14:22:00');
INSERT INTO `sys_logininfor` VALUES (344, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-03 14:37:27');
INSERT INTO `sys_logininfor` VALUES (345, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-04 09:51:41');
INSERT INTO `sys_logininfor` VALUES (346, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-05 09:54:06');
INSERT INTO `sys_logininfor` VALUES (347, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-05 09:54:32');
INSERT INTO `sys_logininfor` VALUES (348, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-05 09:54:39');
INSERT INTO `sys_logininfor` VALUES (349, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-05 09:54:42');
INSERT INTO `sys_logininfor` VALUES (350, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-05 09:54:52');
INSERT INTO `sys_logininfor` VALUES (351, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-05 09:54:58');
INSERT INTO `sys_logininfor` VALUES (352, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-05 09:55:03');
INSERT INTO `sys_logininfor` VALUES (353, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-05 15:04:04');
INSERT INTO `sys_logininfor` VALUES (354, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-05 15:04:10');
INSERT INTO `sys_logininfor` VALUES (355, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-03-05 15:07:08');
INSERT INTO `sys_logininfor` VALUES (356, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-05 15:07:12');
INSERT INTO `sys_logininfor` VALUES (357, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-05 15:13:49');
INSERT INTO `sys_logininfor` VALUES (358, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-05 15:49:49');
INSERT INTO `sys_logininfor` VALUES (359, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-05 15:50:38');
INSERT INTO `sys_logininfor` VALUES (360, 'admin', '183.221.21.60', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-05 16:01:30');
INSERT INTO `sys_logininfor` VALUES (361, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-08 09:56:32');
INSERT INTO `sys_logininfor` VALUES (362, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-08 10:13:19');
INSERT INTO `sys_logininfor` VALUES (363, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-08 10:15:49');
INSERT INTO `sys_logininfor` VALUES (364, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-08 10:41:20');
INSERT INTO `sys_logininfor` VALUES (365, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-08 10:41:26');
INSERT INTO `sys_logininfor` VALUES (366, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-09 09:36:29');
INSERT INTO `sys_logininfor` VALUES (367, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-09 09:36:29');
INSERT INTO `sys_logininfor` VALUES (368, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-09 09:36:33');
INSERT INTO `sys_logininfor` VALUES (369, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 09:36:46');
INSERT INTO `sys_logininfor` VALUES (370, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-09 09:45:58');
INSERT INTO `sys_logininfor` VALUES (371, 'admin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-03-09 14:28:23');
INSERT INTO `sys_logininfor` VALUES (372, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 14:28:42');
INSERT INTO `sys_logininfor` VALUES (373, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-03-09 14:29:40');
INSERT INTO `sys_logininfor` VALUES (374, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '1', '验证码错误', '2021-03-09 14:29:50');
INSERT INTO `sys_logininfor` VALUES (375, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 14:30:08');
INSERT INTO `sys_logininfor` VALUES (376, 'admin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-03-09 14:30:35');
INSERT INTO `sys_logininfor` VALUES (377, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '1', '验证码错误', '2021-03-09 14:30:58');
INSERT INTO `sys_logininfor` VALUES (378, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 14:31:06');
INSERT INTO `sys_logininfor` VALUES (379, 'admin', '172.16.101.131', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-03-09 14:32:47');
INSERT INTO `sys_logininfor` VALUES (380, 'superAdmin', '172.16.101.131', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-09 14:33:04');
INSERT INTO `sys_logininfor` VALUES (381, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 14:34:19');
INSERT INTO `sys_logininfor` VALUES (382, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 14:35:12');
INSERT INTO `sys_logininfor` VALUES (383, 'superAdmin', '172.16.100.160', '内网IP', 'Chrome 8', 'Windows 7', '0', '登录成功', '2021-03-09 14:36:51');
INSERT INTO `sys_logininfor` VALUES (384, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-09 14:38:07');
INSERT INTO `sys_logininfor` VALUES (385, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 14:39:03');
INSERT INTO `sys_logininfor` VALUES (386, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-09 14:39:08');
INSERT INTO `sys_logininfor` VALUES (387, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-03-09 14:40:10');
INSERT INTO `sys_logininfor` VALUES (388, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-03-09 14:40:23');
INSERT INTO `sys_logininfor` VALUES (389, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 14:40:37');
INSERT INTO `sys_logininfor` VALUES (390, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 14:42:03');
INSERT INTO `sys_logininfor` VALUES (391, 'superAdmin', '172.16.101.221', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 14:45:48');
INSERT INTO `sys_logininfor` VALUES (392, 'admin', '172.16.101.131', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-03-09 14:54:07');
INSERT INTO `sys_logininfor` VALUES (393, 'superAdmin', '172.16.101.131', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-03-09 14:54:21');
INSERT INTO `sys_logininfor` VALUES (394, 'superAdmin', '172.16.101.131', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-09 14:54:26');
INSERT INTO `sys_logininfor` VALUES (395, 'superAdmin', '172.16.101.221', '内网IP', 'Firefox 8', 'Mac OS X', '1', '验证码错误', '2021-03-09 15:04:48');
INSERT INTO `sys_logininfor` VALUES (396, 'superAdmin', '172.16.101.221', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-03-09 15:04:52');
INSERT INTO `sys_logininfor` VALUES (397, 'superAdmin', '172.16.100.98', '内网IP', 'Firefox', 'Windows 10', '0', '登录成功', '2021-03-09 15:06:28');
INSERT INTO `sys_logininfor` VALUES (398, 'superAdmin', '172.16.100.160', '内网IP', 'Chrome', 'Windows 7', '0', '登录成功', '2021-03-09 15:07:37');
INSERT INTO `sys_logininfor` VALUES (399, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 15:10:07');
INSERT INTO `sys_logininfor` VALUES (400, 'admin', '172.16.101.212', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-09 15:13:06');
INSERT INTO `sys_logininfor` VALUES (401, 'admin', '172.16.101.212', '内网IP', 'Chrome 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-03-09 15:23:49');
INSERT INTO `sys_logininfor` VALUES (402, 'admin', '172.16.101.212', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 15:24:41');
INSERT INTO `sys_logininfor` VALUES (403, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 15:27:50');
INSERT INTO `sys_logininfor` VALUES (404, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-09 15:32:43');
INSERT INTO `sys_logininfor` VALUES (405, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 15:33:18');
INSERT INTO `sys_logininfor` VALUES (406, 'admin', '172.16.101.169', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 15:37:48');
INSERT INTO `sys_logininfor` VALUES (407, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 15:42:12');
INSERT INTO `sys_logininfor` VALUES (408, 'admin', '192.166.0.97', 'XX XX', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-10 03:14:26');
INSERT INTO `sys_logininfor` VALUES (409, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-10 03:30:26');
INSERT INTO `sys_logininfor` VALUES (410, 'admin', '192.166.0.97', 'XX XX', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-10 03:34:53');
INSERT INTO `sys_logininfor` VALUES (411, 'admin', '192.166.0.131', 'XX XX', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-10 03:35:48');
INSERT INTO `sys_logininfor` VALUES (412, 'admin', '192.166.0.77', 'XX XX', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-10 03:48:38');
INSERT INTO `sys_logininfor` VALUES (413, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-10 03:50:06');
INSERT INTO `sys_logininfor` VALUES (414, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-10 03:50:10');
INSERT INTO `sys_logininfor` VALUES (415, 'superAdmin', '192.166.0.97', 'XX XX', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-10 03:56:00');
INSERT INTO `sys_logininfor` VALUES (416, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-10 17:47:43');
INSERT INTO `sys_logininfor` VALUES (417, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-10 17:47:52');
INSERT INTO `sys_logininfor` VALUES (418, 'superAdmin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-10 17:48:03');
INSERT INTO `sys_logininfor` VALUES (419, 'admin', '192.166.0.97', 'XX XX', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-10 17:50:44');
INSERT INTO `sys_logininfor` VALUES (420, 'superAdmin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-10 18:06:29');
INSERT INTO `sys_logininfor` VALUES (421, 'superAdmin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-10 18:06:58');
INSERT INTO `sys_logininfor` VALUES (422, 'superAdmin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-10 18:08:52');
INSERT INTO `sys_logininfor` VALUES (423, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-10 18:08:58');
INSERT INTO `sys_logininfor` VALUES (424, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-11 04:46:56');
INSERT INTO `sys_logininfor` VALUES (425, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-11 05:09:56');
INSERT INTO `sys_logininfor` VALUES (426, 'admin', '192.166.0.77', 'XX XX', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-11 06:12:55');
INSERT INTO `sys_logininfor` VALUES (427, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-11 17:57:15');
INSERT INTO `sys_logininfor` VALUES (428, 'admin', '192.166.0.131', 'XX XX', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-11 18:28:04');
INSERT INTO `sys_logininfor` VALUES (429, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-11 20:46:30');
INSERT INTO `sys_logininfor` VALUES (430, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-11 18:13:25');
INSERT INTO `sys_logininfor` VALUES (431, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-11 19:19:19');
INSERT INTO `sys_logininfor` VALUES (432, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-11 19:19:40');
INSERT INTO `sys_logininfor` VALUES (433, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-12 11:17:22');
INSERT INTO `sys_logininfor` VALUES (434, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-12 14:24:26');
INSERT INTO `sys_logininfor` VALUES (435, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-12 14:24:32');
INSERT INTO `sys_logininfor` VALUES (436, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-12 14:33:29');
INSERT INTO `sys_logininfor` VALUES (437, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-12 14:33:36');
INSERT INTO `sys_logininfor` VALUES (438, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-12 14:33:56');
INSERT INTO `sys_logininfor` VALUES (439, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-12 14:34:02');
INSERT INTO `sys_logininfor` VALUES (440, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-12 21:55:58');
INSERT INTO `sys_logininfor` VALUES (441, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-12 21:56:05');
INSERT INTO `sys_logininfor` VALUES (442, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-12 22:18:03');
INSERT INTO `sys_logininfor` VALUES (443, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-15 18:37:06');
INSERT INTO `sys_logininfor` VALUES (444, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-15 18:37:27');
INSERT INTO `sys_logininfor` VALUES (445, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-15 18:37:33');
INSERT INTO `sys_logininfor` VALUES (446, 'admin', '172.16.101.229', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-16 09:40:07');
INSERT INTO `sys_logininfor` VALUES (447, 'admin', '172.16.101.229', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-16 09:41:53');
INSERT INTO `sys_logininfor` VALUES (448, 'superAdmin', '172.16.101.229', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-16 09:42:05');
INSERT INTO `sys_logininfor` VALUES (449, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-16 11:10:06');
INSERT INTO `sys_logininfor` VALUES (450, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-16 11:55:50');
INSERT INTO `sys_logininfor` VALUES (451, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-16 13:36:54');
INSERT INTO `sys_logininfor` VALUES (452, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-16 18:28:07');
INSERT INTO `sys_logininfor` VALUES (453, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-03-16 18:32:24');
INSERT INTO `sys_logininfor` VALUES (454, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-16 18:32:27');
INSERT INTO `sys_logininfor` VALUES (455, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-19 09:56:33');
INSERT INTO `sys_logininfor` VALUES (456, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-22 10:42:04');
INSERT INTO `sys_logininfor` VALUES (457, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-22 10:42:08');
INSERT INTO `sys_logininfor` VALUES (458, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-22 10:42:20');
INSERT INTO `sys_logininfor` VALUES (459, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-22 10:42:29');
INSERT INTO `sys_logininfor` VALUES (460, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-23 11:03:28');
INSERT INTO `sys_logininfor` VALUES (461, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-23 11:04:04');
INSERT INTO `sys_logininfor` VALUES (462, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-23 13:58:43');
INSERT INTO `sys_logininfor` VALUES (463, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-23 13:58:49');
INSERT INTO `sys_logininfor` VALUES (464, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-23 13:58:56');
INSERT INTO `sys_logininfor` VALUES (465, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-23 14:57:14');
INSERT INTO `sys_logininfor` VALUES (466, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-23 14:57:24');
INSERT INTO `sys_logininfor` VALUES (467, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-23 14:57:29');
INSERT INTO `sys_logininfor` VALUES (468, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-23 14:57:37');
INSERT INTO `sys_logininfor` VALUES (469, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-24 10:01:32');
INSERT INTO `sys_logininfor` VALUES (470, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-24 10:01:47');
INSERT INTO `sys_logininfor` VALUES (471, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-24 10:01:54');
INSERT INTO `sys_logininfor` VALUES (472, 'admin', '172.16.101.212', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-24 17:13:13');
INSERT INTO `sys_logininfor` VALUES (473, 'admin', '172.16.101.212', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-03-24 17:13:33');
INSERT INTO `sys_logininfor` VALUES (474, 'superAdmin', '172.16.101.212', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-24 17:13:50');
INSERT INTO `sys_logininfor` VALUES (475, 'superAdmin', '172.16.101.212', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-24 17:35:48');
INSERT INTO `sys_logininfor` VALUES (476, 'admin', '172.16.101.212', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-25 09:46:53');
INSERT INTO `sys_logininfor` VALUES (477, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-25 09:51:25');
INSERT INTO `sys_logininfor` VALUES (478, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-25 09:52:29');
INSERT INTO `sys_logininfor` VALUES (479, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-25 09:52:52');
INSERT INTO `sys_logininfor` VALUES (480, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-25 09:53:01');
INSERT INTO `sys_logininfor` VALUES (481, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-25 09:55:49');
INSERT INTO `sys_logininfor` VALUES (482, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-25 09:55:52');
INSERT INTO `sys_logininfor` VALUES (483, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-25 09:55:56');
INSERT INTO `sys_logininfor` VALUES (484, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-25 09:56:05');
INSERT INTO `sys_logininfor` VALUES (485, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-25 10:58:00');
INSERT INTO `sys_logininfor` VALUES (486, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-25 16:28:57');
INSERT INTO `sys_logininfor` VALUES (487, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-25 16:29:05');
INSERT INTO `sys_logininfor` VALUES (488, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-25 16:29:38');
INSERT INTO `sys_logininfor` VALUES (489, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-25 16:29:47');
INSERT INTO `sys_logininfor` VALUES (490, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-25 17:41:29');
INSERT INTO `sys_logininfor` VALUES (491, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-25 17:41:33');
INSERT INTO `sys_logininfor` VALUES (492, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-25 17:41:42');
INSERT INTO `sys_logininfor` VALUES (493, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-26 09:48:36');
INSERT INTO `sys_logininfor` VALUES (494, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-26 09:49:15');
INSERT INTO `sys_logininfor` VALUES (495, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-26 09:49:22');
INSERT INTO `sys_logininfor` VALUES (496, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-26 09:49:28');
INSERT INTO `sys_logininfor` VALUES (497, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-26 10:52:00');
INSERT INTO `sys_logininfor` VALUES (498, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-26 10:52:10');
INSERT INTO `sys_logininfor` VALUES (499, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-26 10:52:17');
INSERT INTO `sys_logininfor` VALUES (500, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-26 10:52:25');
INSERT INTO `sys_logininfor` VALUES (501, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-26 14:02:04');
INSERT INTO `sys_logininfor` VALUES (502, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-26 14:02:13');
INSERT INTO `sys_logininfor` VALUES (503, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-26 14:02:17');
INSERT INTO `sys_logininfor` VALUES (504, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-26 14:02:42');
INSERT INTO `sys_logininfor` VALUES (505, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-26 14:02:52');
INSERT INTO `sys_logininfor` VALUES (506, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-29 09:41:25');
INSERT INTO `sys_logininfor` VALUES (507, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-29 09:43:19');
INSERT INTO `sys_logininfor` VALUES (508, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-29 09:44:58');
INSERT INTO `sys_logininfor` VALUES (509, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-29 09:45:04');
INSERT INTO `sys_logininfor` VALUES (510, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-29 09:58:21');
INSERT INTO `sys_logininfor` VALUES (511, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-29 09:58:53');
INSERT INTO `sys_logininfor` VALUES (512, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-29 10:04:49');
INSERT INTO `sys_logininfor` VALUES (513, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-29 13:35:54');
INSERT INTO `sys_logininfor` VALUES (514, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-29 13:36:06');
INSERT INTO `sys_logininfor` VALUES (515, 'admin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '1', '验证码错误', '2021-03-29 16:25:17');
INSERT INTO `sys_logininfor` VALUES (516, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-29 16:25:30');
INSERT INTO `sys_logininfor` VALUES (517, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-29 17:09:00');
INSERT INTO `sys_logininfor` VALUES (518, 'admin', '172.16.101.200', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-03-29 17:11:27');
INSERT INTO `sys_logininfor` VALUES (519, 'admin', '172.16.101.200', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-29 17:11:31');
INSERT INTO `sys_logininfor` VALUES (520, 'admin', '172.16.101.200', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-29 17:13:34');
INSERT INTO `sys_logininfor` VALUES (521, 'superAdmin', '172.16.101.200', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-29 17:14:03');
INSERT INTO `sys_logininfor` VALUES (522, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-30 09:41:24');
INSERT INTO `sys_logininfor` VALUES (523, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-31 09:34:29');
INSERT INTO `sys_logininfor` VALUES (524, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-31 15:31:22');
INSERT INTO `sys_logininfor` VALUES (525, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-31 16:33:00');
INSERT INTO `sys_logininfor` VALUES (526, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-31 16:33:17');
INSERT INTO `sys_logininfor` VALUES (527, 'superAdmin', '172.16.101.224', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-03-31 16:34:25');
INSERT INTO `sys_logininfor` VALUES (528, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-01 09:36:23');
INSERT INTO `sys_logininfor` VALUES (529, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-01 18:11:44');
INSERT INTO `sys_logininfor` VALUES (530, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-02 09:40:54');
INSERT INTO `sys_logininfor` VALUES (531, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-02 09:42:12');
INSERT INTO `sys_logininfor` VALUES (532, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-02 09:42:26');
INSERT INTO `sys_logininfor` VALUES (533, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-04-02 17:39:20');
INSERT INTO `sys_logininfor` VALUES (534, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-02 17:39:30');
INSERT INTO `sys_logininfor` VALUES (535, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-06 17:25:32');
INSERT INTO `sys_logininfor` VALUES (536, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-07 10:00:11');
INSERT INTO `sys_logininfor` VALUES (537, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-08 09:55:46');
INSERT INTO `sys_logininfor` VALUES (538, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-08 10:57:48');
INSERT INTO `sys_logininfor` VALUES (539, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-08 10:58:01');
INSERT INTO `sys_logininfor` VALUES (540, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-08 10:58:05');
INSERT INTO `sys_logininfor` VALUES (541, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-08 10:58:27');
INSERT INTO `sys_logininfor` VALUES (542, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-08 18:48:25');
INSERT INTO `sys_logininfor` VALUES (543, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-08 18:50:32');
INSERT INTO `sys_logininfor` VALUES (544, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-09 18:27:45');
INSERT INTO `sys_logininfor` VALUES (545, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 09:39:56');
INSERT INTO `sys_logininfor` VALUES (546, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-12 13:31:02');
INSERT INTO `sys_logininfor` VALUES (547, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-12 14:34:15');
INSERT INTO `sys_logininfor` VALUES (548, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-12 15:55:25');
INSERT INTO `sys_logininfor` VALUES (549, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-12 16:22:39');
INSERT INTO `sys_logininfor` VALUES (550, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 16:22:39');
INSERT INTO `sys_logininfor` VALUES (551, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-12 16:22:39');
INSERT INTO `sys_logininfor` VALUES (552, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 16:22:39');
INSERT INTO `sys_logininfor` VALUES (553, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-12 16:22:39');
INSERT INTO `sys_logininfor` VALUES (554, 'admin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 16:22:39');
INSERT INTO `sys_logininfor` VALUES (555, 'admin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 16:22:39');
INSERT INTO `sys_logininfor` VALUES (556, 'admin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 16:22:40');
INSERT INTO `sys_logininfor` VALUES (557, 'admin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-04-12 16:22:45');
INSERT INTO `sys_logininfor` VALUES (558, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 16:22:52');
INSERT INTO `sys_logininfor` VALUES (559, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-12 18:02:55');
INSERT INTO `sys_logininfor` VALUES (560, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 18:03:48');
INSERT INTO `sys_logininfor` VALUES (561, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-13 09:39:05');
INSERT INTO `sys_logininfor` VALUES (562, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-13 10:33:56');
INSERT INTO `sys_logininfor` VALUES (563, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-13 17:01:35');
INSERT INTO `sys_logininfor` VALUES (564, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-13 17:01:49');
INSERT INTO `sys_logininfor` VALUES (565, 'superAdmin', '172.16.101.224', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-04-13 18:14:28');
INSERT INTO `sys_logininfor` VALUES (566, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-14 09:40:11');
INSERT INTO `sys_logininfor` VALUES (567, 'superAdmin', '172.16.101.224', '内网IP', 'Firefox 8', 'Mac OS X', '1', '验证码错误', '2021-04-14 10:08:37');
INSERT INTO `sys_logininfor` VALUES (568, 'superAdmin', '172.16.101.224', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-04-14 10:08:42');
INSERT INTO `sys_logininfor` VALUES (569, 'superAdmin', '172.16.101.224', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-04-14 11:12:12');
INSERT INTO `sys_logininfor` VALUES (570, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-04-14 14:54:16');
INSERT INTO `sys_logininfor` VALUES (571, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-14 14:54:22');
INSERT INTO `sys_logininfor` VALUES (572, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-14 14:54:32');
INSERT INTO `sys_logininfor` VALUES (573, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-14 14:54:38');
INSERT INTO `sys_logininfor` VALUES (574, 'admin', '172.16.101.226', '内网IP', 'Chrome Mobile', 'Android 1.x', '0', '登录成功', '2021-04-14 14:55:58');
INSERT INTO `sys_logininfor` VALUES (575, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-14 16:10:17');
INSERT INTO `sys_logininfor` VALUES (576, 'superAdmin', '172.16.100.170', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-14 18:08:51');
INSERT INTO `sys_logininfor` VALUES (577, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-15 09:54:48');
INSERT INTO `sys_logininfor` VALUES (578, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-15 09:59:10');
INSERT INTO `sys_logininfor` VALUES (579, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-16 09:55:17');
INSERT INTO `sys_logininfor` VALUES (580, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-16 09:55:47');
INSERT INTO `sys_logininfor` VALUES (581, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-16 09:55:59');
INSERT INTO `sys_logininfor` VALUES (582, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-16 10:59:41');
INSERT INTO `sys_logininfor` VALUES (583, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-16 10:59:56');
INSERT INTO `sys_logininfor` VALUES (584, 'superAdmin', '172.16.100.170', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-16 18:30:53');
INSERT INTO `sys_logininfor` VALUES (585, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-16 18:32:37');
INSERT INTO `sys_logininfor` VALUES (586, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-16 18:33:30');
INSERT INTO `sys_logininfor` VALUES (587, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-18 10:24:43');
INSERT INTO `sys_logininfor` VALUES (588, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-18 10:24:51');
INSERT INTO `sys_logininfor` VALUES (589, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-18 10:25:01');
INSERT INTO `sys_logininfor` VALUES (590, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-18 18:14:29');
INSERT INTO `sys_logininfor` VALUES (591, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-19 09:34:37');
INSERT INTO `sys_logininfor` VALUES (592, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-19 17:30:14');
INSERT INTO `sys_logininfor` VALUES (593, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-20 02:45:21');
INSERT INTO `sys_logininfor` VALUES (594, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 02:51:49');
INSERT INTO `sys_logininfor` VALUES (595, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-04-20 03:05:45');
INSERT INTO `sys_logininfor` VALUES (596, 'superadmin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 03:05:56');
INSERT INTO `sys_logininfor` VALUES (597, 'superAdmin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-04-20 03:08:00');
INSERT INTO `sys_logininfor` VALUES (598, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 03:08:05');
INSERT INTO `sys_logininfor` VALUES (599, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-04-20 03:09:10');
INSERT INTO `sys_logininfor` VALUES (600, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 03:09:14');
INSERT INTO `sys_logininfor` VALUES (601, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-04-20 03:09:21');
INSERT INTO `sys_logininfor` VALUES (602, 'superadmin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 03:09:29');
INSERT INTO `sys_logininfor` VALUES (603, 'superAdmin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-04-20 03:26:31');
INSERT INTO `sys_logininfor` VALUES (604, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 03:26:36');
INSERT INTO `sys_logininfor` VALUES (605, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-04-20 03:41:48');
INSERT INTO `sys_logininfor` VALUES (606, 'superadmin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 03:41:56');
INSERT INTO `sys_logininfor` VALUES (607, 'superAdmin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-04-20 04:05:39');
INSERT INTO `sys_logininfor` VALUES (608, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 04:05:45');
INSERT INTO `sys_logininfor` VALUES (609, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 04:07:34');
INSERT INTO `sys_logininfor` VALUES (610, 'admin', '172.16.100.98', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-20 04:08:02');
INSERT INTO `sys_logininfor` VALUES (611, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 18:10:40');
INSERT INTO `sys_logininfor` VALUES (612, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 18:10:50');
INSERT INTO `sys_logininfor` VALUES (613, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-04-20 18:10:57');
INSERT INTO `sys_logininfor` VALUES (614, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 18:11:01');
INSERT INTO `sys_logininfor` VALUES (615, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 18:29:12');
INSERT INTO `sys_logininfor` VALUES (616, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 10:36:19');
INSERT INTO `sys_logininfor` VALUES (617, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:00:07');
INSERT INTO `sys_logininfor` VALUES (618, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:00:18');
INSERT INTO `sys_logininfor` VALUES (619, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:00:39');
INSERT INTO `sys_logininfor` VALUES (620, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:00:44');
INSERT INTO `sys_logininfor` VALUES (621, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:02:33');
INSERT INTO `sys_logininfor` VALUES (622, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:02:39');
INSERT INTO `sys_logininfor` VALUES (623, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:04:04');
INSERT INTO `sys_logininfor` VALUES (624, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:04:10');
INSERT INTO `sys_logininfor` VALUES (625, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:05:07');
INSERT INTO `sys_logininfor` VALUES (626, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:05:12');
INSERT INTO `sys_logininfor` VALUES (627, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:07:01');
INSERT INTO `sys_logininfor` VALUES (628, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:07:06');
INSERT INTO `sys_logininfor` VALUES (629, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:07:37');
INSERT INTO `sys_logininfor` VALUES (630, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:07:45');
INSERT INTO `sys_logininfor` VALUES (631, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:07:53');
INSERT INTO `sys_logininfor` VALUES (632, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:07:58');
INSERT INTO `sys_logininfor` VALUES (633, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:52:57');
INSERT INTO `sys_logininfor` VALUES (634, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:53:04');
INSERT INTO `sys_logininfor` VALUES (635, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-20 16:08:55');
INSERT INTO `sys_logininfor` VALUES (636, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-20 16:08:59');
INSERT INTO `sys_logininfor` VALUES (637, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-20 16:09:08');
INSERT INTO `sys_logininfor` VALUES (638, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-21 10:58:39');
INSERT INTO `sys_logininfor` VALUES (639, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-21 10:58:51');
INSERT INTO `sys_logininfor` VALUES (640, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-21 10:58:57');
INSERT INTO `sys_logininfor` VALUES (641, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-21 11:00:58');
INSERT INTO `sys_logininfor` VALUES (642, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-21 11:42:57');
INSERT INTO `sys_logininfor` VALUES (643, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-21 11:43:06');
INSERT INTO `sys_logininfor` VALUES (644, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-21 16:12:43');
INSERT INTO `sys_logininfor` VALUES (645, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-21 16:12:47');
INSERT INTO `sys_logininfor` VALUES (646, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-21 16:40:28');
INSERT INTO `sys_logininfor` VALUES (647, 'superadmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-21 16:40:36');
INSERT INTO `sys_logininfor` VALUES (648, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-21 16:50:09');
INSERT INTO `sys_logininfor` VALUES (649, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-21 16:50:15');
INSERT INTO `sys_logininfor` VALUES (650, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-21 16:56:54');
INSERT INTO `sys_logininfor` VALUES (651, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-21 16:57:01');
INSERT INTO `sys_logininfor` VALUES (652, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-21 17:02:36');
INSERT INTO `sys_logininfor` VALUES (653, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-21 17:02:40');
INSERT INTO `sys_logininfor` VALUES (654, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-22 09:54:36');
INSERT INTO `sys_logininfor` VALUES (655, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-22 09:58:43');
INSERT INTO `sys_logininfor` VALUES (656, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-22 09:59:00');
INSERT INTO `sys_logininfor` VALUES (657, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-22 10:27:39');
INSERT INTO `sys_logininfor` VALUES (658, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-22 10:28:22');
INSERT INTO `sys_logininfor` VALUES (659, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-22 10:49:05');
INSERT INTO `sys_logininfor` VALUES (660, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-22 10:49:10');
INSERT INTO `sys_logininfor` VALUES (661, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-22 13:35:49');
INSERT INTO `sys_logininfor` VALUES (662, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-04-22 13:36:01');
INSERT INTO `sys_logininfor` VALUES (663, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-22 13:36:04');
INSERT INTO `sys_logininfor` VALUES (664, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-22 13:36:31');
INSERT INTO `sys_logininfor` VALUES (665, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-22 13:36:40');
INSERT INTO `sys_logininfor` VALUES (666, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-22 13:38:20');
INSERT INTO `sys_logininfor` VALUES (667, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-22 13:38:26');
INSERT INTO `sys_logininfor` VALUES (668, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-22 14:40:46');
INSERT INTO `sys_logininfor` VALUES (669, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-22 15:14:33');
INSERT INTO `sys_logininfor` VALUES (670, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-22 15:20:36');
INSERT INTO `sys_logininfor` VALUES (671, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-04-22 15:20:46');
INSERT INTO `sys_logininfor` VALUES (672, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-22 15:20:49');
INSERT INTO `sys_logininfor` VALUES (673, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-22 15:20:54');
INSERT INTO `sys_logininfor` VALUES (674, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-22 15:20:59');
INSERT INTO `sys_logininfor` VALUES (675, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-22 15:27:11');
INSERT INTO `sys_logininfor` VALUES (676, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-22 15:27:20');
INSERT INTO `sys_logininfor` VALUES (677, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-22 15:40:58');
INSERT INTO `sys_logininfor` VALUES (678, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码已失效', '2021-04-22 15:53:22');
INSERT INTO `sys_logininfor` VALUES (679, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-22 15:53:41');
INSERT INTO `sys_logininfor` VALUES (680, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-22 16:03:54');
INSERT INTO `sys_logininfor` VALUES (681, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-22 16:04:01');
INSERT INTO `sys_logininfor` VALUES (682, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-23 09:43:46');
INSERT INTO `sys_logininfor` VALUES (683, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-23 09:52:28');
INSERT INTO `sys_logininfor` VALUES (684, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-23 09:52:35');
INSERT INTO `sys_logininfor` VALUES (685, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-23 10:11:16');
INSERT INTO `sys_logininfor` VALUES (686, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-23 10:11:24');
INSERT INTO `sys_logininfor` VALUES (687, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-23 15:06:41');
INSERT INTO `sys_logininfor` VALUES (688, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-23 16:37:00');
INSERT INTO `sys_logininfor` VALUES (689, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-23 16:37:08');
INSERT INTO `sys_logininfor` VALUES (690, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-23 16:38:06');
INSERT INTO `sys_logininfor` VALUES (691, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-23 16:38:11');
INSERT INTO `sys_logininfor` VALUES (692, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-23 16:38:20');
INSERT INTO `sys_logininfor` VALUES (693, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-23 16:38:32');
INSERT INTO `sys_logininfor` VALUES (694, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-23 18:13:43');
INSERT INTO `sys_logininfor` VALUES (695, 'admin', '172.16.100.243', '内网IP', 'Chrome 8', 'Windows 10', '1', '验证码错误', '2021-04-25 11:32:02');
INSERT INTO `sys_logininfor` VALUES (696, 'admin', '172.16.100.243', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-25 11:32:06');
INSERT INTO `sys_logininfor` VALUES (697, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '1', '验证码错误', '2021-04-25 16:16:24');
INSERT INTO `sys_logininfor` VALUES (698, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-25 16:16:27');
INSERT INTO `sys_logininfor` VALUES (699, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-25 16:16:55');
INSERT INTO `sys_logininfor` VALUES (700, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-25 16:17:06');
INSERT INTO `sys_logininfor` VALUES (701, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-25 16:48:46');
INSERT INTO `sys_logininfor` VALUES (702, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-25 18:21:31');
INSERT INTO `sys_logininfor` VALUES (703, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-25 18:21:39');
INSERT INTO `sys_logininfor` VALUES (704, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-25 18:22:28');
INSERT INTO `sys_logininfor` VALUES (705, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-25 18:22:35');
INSERT INTO `sys_logininfor` VALUES (706, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-25 18:30:08');
INSERT INTO `sys_logininfor` VALUES (707, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-25 18:30:14');
INSERT INTO `sys_logininfor` VALUES (708, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-25 18:47:13');
INSERT INTO `sys_logininfor` VALUES (709, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-25 21:40:28');
INSERT INTO `sys_logininfor` VALUES (710, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-04-25 21:40:34');
INSERT INTO `sys_logininfor` VALUES (711, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-25 21:40:39');
INSERT INTO `sys_logininfor` VALUES (712, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 10:06:54');
INSERT INTO `sys_logininfor` VALUES (713, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-26 10:12:56');
INSERT INTO `sys_logininfor` VALUES (714, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 10:13:06');
INSERT INTO `sys_logininfor` VALUES (715, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-26 10:14:52');
INSERT INTO `sys_logininfor` VALUES (716, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 10:15:00');
INSERT INTO `sys_logininfor` VALUES (717, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-26 10:15:24');
INSERT INTO `sys_logininfor` VALUES (718, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 10:15:36');
INSERT INTO `sys_logininfor` VALUES (719, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '1', '验证码已失效', '2021-04-26 11:06:46');
INSERT INTO `sys_logininfor` VALUES (720, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-26 11:06:58');
INSERT INTO `sys_logininfor` VALUES (721, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-26 11:07:18');
INSERT INTO `sys_logininfor` VALUES (722, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-26 11:07:25');
INSERT INTO `sys_logininfor` VALUES (723, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-26 11:39:49');
INSERT INTO `sys_logininfor` VALUES (724, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 11:39:57');
INSERT INTO `sys_logininfor` VALUES (725, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-26 11:41:32');
INSERT INTO `sys_logininfor` VALUES (726, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '1', '验证码错误', '2021-04-26 11:41:40');
INSERT INTO `sys_logininfor` VALUES (727, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-26 11:41:44');
INSERT INTO `sys_logininfor` VALUES (728, 'admin', '172.16.101.249', '内网IP', 'Safari', 'Mac OS X', '0', '登录成功', '2021-04-26 11:42:47');
INSERT INTO `sys_logininfor` VALUES (729, 'admin', '172.16.101.249', '内网IP', 'Safari', 'Mac OS X', '0', '退出成功', '2021-04-26 11:44:35');
INSERT INTO `sys_logininfor` VALUES (730, 'superAdmin', '172.16.101.249', '内网IP', 'Safari', 'Mac OS X', '0', '登录成功', '2021-04-26 11:44:48');
INSERT INTO `sys_logininfor` VALUES (731, 'admin', '172.16.101.249', '内网IP', 'Safari', 'Mac OS X', '0', '登录成功', '2021-04-26 13:44:46');
INSERT INTO `sys_logininfor` VALUES (732, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-26 13:51:47');
INSERT INTO `sys_logininfor` VALUES (733, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 13:51:52');
INSERT INTO `sys_logininfor` VALUES (734, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-26 13:53:25');
INSERT INTO `sys_logininfor` VALUES (735, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-26 13:55:00');
INSERT INTO `sys_logininfor` VALUES (736, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '1', '验证码错误', '2021-04-26 13:55:12');
INSERT INTO `sys_logininfor` VALUES (737, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-26 13:55:15');
INSERT INTO `sys_logininfor` VALUES (738, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-26 14:06:29');
INSERT INTO `sys_logininfor` VALUES (739, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-26 14:06:37');
INSERT INTO `sys_logininfor` VALUES (740, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-26 14:09:03');
INSERT INTO `sys_logininfor` VALUES (741, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 14:09:08');
INSERT INTO `sys_logininfor` VALUES (742, 'admin', '172.16.101.249', '内网IP', 'Safari', 'Mac OS X', '0', '退出成功', '2021-04-26 14:11:26');
INSERT INTO `sys_logininfor` VALUES (743, 'superAdmin', '172.16.101.249', '内网IP', 'Safari', 'Mac OS X', '1', '用户不存在/密码错误', '2021-04-26 14:11:38');
INSERT INTO `sys_logininfor` VALUES (744, 'superAdmin', '172.16.101.249', '内网IP', 'Safari', 'Mac OS X', '1', '用户不存在/密码错误', '2021-04-26 14:11:45');
INSERT INTO `sys_logininfor` VALUES (745, 'superAdmin', '172.16.101.249', '内网IP', 'Safari', 'Mac OS X', '1', '验证码错误', '2021-04-26 14:11:49');
INSERT INTO `sys_logininfor` VALUES (746, 'superAdmin', '172.16.101.249', '内网IP', 'Safari', 'Mac OS X', '1', '用户不存在/密码错误', '2021-04-26 14:11:53');
INSERT INTO `sys_logininfor` VALUES (747, 'superAdmin', '172.16.101.249', '内网IP', 'Safari', 'Mac OS X', '1', '用户不存在/密码错误', '2021-04-26 14:12:02');
INSERT INTO `sys_logininfor` VALUES (748, 'superAdmin', '172.16.101.249', '内网IP', 'Safari', 'Mac OS X', '1', '用户不存在/密码错误', '2021-04-26 14:12:21');
INSERT INTO `sys_logininfor` VALUES (749, 'superAdmin', '172.16.101.249', '内网IP', 'Safari', 'Mac OS X', '0', '登录成功', '2021-04-26 14:12:51');
INSERT INTO `sys_logininfor` VALUES (750, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-26 14:13:53');
INSERT INTO `sys_logininfor` VALUES (751, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 15:03:32');
INSERT INTO `sys_logininfor` VALUES (752, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-26 15:08:09');
INSERT INTO `sys_logininfor` VALUES (753, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-04-26 15:08:17');
INSERT INTO `sys_logininfor` VALUES (754, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-04-26 15:08:21');
INSERT INTO `sys_logininfor` VALUES (755, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 15:08:37');
INSERT INTO `sys_logininfor` VALUES (756, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-26 15:10:45');
INSERT INTO `sys_logininfor` VALUES (757, 'xxadmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 15:10:54');
INSERT INTO `sys_logininfor` VALUES (758, 'xxAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-26 15:11:29');
INSERT INTO `sys_logininfor` VALUES (759, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 15:11:37');
INSERT INTO `sys_logininfor` VALUES (760, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-26 15:12:06');
INSERT INTO `sys_logininfor` VALUES (761, 'xxadmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 15:12:13');
INSERT INTO `sys_logininfor` VALUES (762, 'xxAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-26 15:34:51');
INSERT INTO `sys_logininfor` VALUES (763, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 15:34:58');
INSERT INTO `sys_logininfor` VALUES (764, 'superAdmin', '172.16.100.212', '内网IP', 'Chrome 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-04-26 15:46:16');
INSERT INTO `sys_logininfor` VALUES (765, 'superAdmin', '172.16.100.212', '内网IP', 'Chrome 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-04-26 15:46:22');
INSERT INTO `sys_logininfor` VALUES (766, 'superAdmin', '172.16.100.212', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-26 15:46:37');
INSERT INTO `sys_logininfor` VALUES (767, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-26 18:38:46');
INSERT INTO `sys_logininfor` VALUES (768, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-26 18:52:39');
INSERT INTO `sys_logininfor` VALUES (769, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 18:52:51');
INSERT INTO `sys_logininfor` VALUES (770, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-26 19:02:55');
INSERT INTO `sys_logininfor` VALUES (771, 'zfw', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-04-26 19:03:01');
INSERT INTO `sys_logininfor` VALUES (772, 'zfw', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-04-26 19:03:07');
INSERT INTO `sys_logininfor` VALUES (773, 'zfw', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 19:03:42');
INSERT INTO `sys_logininfor` VALUES (774, 'zfw', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-26 19:07:44');
INSERT INTO `sys_logininfor` VALUES (775, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-04-26 19:07:51');
INSERT INTO `sys_logininfor` VALUES (776, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-04-26 19:07:58');
INSERT INTO `sys_logininfor` VALUES (777, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-26 19:08:13');
INSERT INTO `sys_logininfor` VALUES (778, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-27 09:56:43');
INSERT INTO `sys_logininfor` VALUES (779, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-27 09:56:52');
INSERT INTO `sys_logininfor` VALUES (780, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-27 09:58:46');
INSERT INTO `sys_logininfor` VALUES (781, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-27 10:01:50');
INSERT INTO `sys_logininfor` VALUES (782, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-27 10:01:55');
INSERT INTO `sys_logininfor` VALUES (783, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-27 10:03:38');
INSERT INTO `sys_logininfor` VALUES (784, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-27 10:04:41');
INSERT INTO `sys_logininfor` VALUES (785, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-27 10:14:06');
INSERT INTO `sys_logininfor` VALUES (786, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '1', '验证码错误', '2021-04-27 10:14:19');
INSERT INTO `sys_logininfor` VALUES (787, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-27 10:14:23');
INSERT INTO `sys_logininfor` VALUES (788, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-27 10:15:45');
INSERT INTO `sys_logininfor` VALUES (789, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-27 10:15:59');
INSERT INTO `sys_logininfor` VALUES (790, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-27 10:36:02');
INSERT INTO `sys_logininfor` VALUES (791, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-27 10:36:29');
INSERT INTO `sys_logininfor` VALUES (792, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-27 10:49:33');
INSERT INTO `sys_logininfor` VALUES (793, 'zfw', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-27 10:49:38');
INSERT INTO `sys_logininfor` VALUES (794, 'admin', '172.16.100.212', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-27 10:50:21');
INSERT INTO `sys_logininfor` VALUES (795, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-27 10:56:56');
INSERT INTO `sys_logininfor` VALUES (796, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-27 10:57:00');
INSERT INTO `sys_logininfor` VALUES (797, 'zfw', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-27 11:00:18');
INSERT INTO `sys_logininfor` VALUES (798, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-27 11:00:27');
INSERT INTO `sys_logininfor` VALUES (799, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-27 11:05:22');
INSERT INTO `sys_logininfor` VALUES (800, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-27 11:05:34');
INSERT INTO `sys_logininfor` VALUES (801, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-27 11:36:11');
INSERT INTO `sys_logininfor` VALUES (802, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-27 11:36:16');
INSERT INTO `sys_logininfor` VALUES (803, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-27 11:48:43');
INSERT INTO `sys_logininfor` VALUES (804, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-27 11:48:50');
INSERT INTO `sys_logininfor` VALUES (805, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-27 13:57:18');
INSERT INTO `sys_logininfor` VALUES (806, 'admin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '退出成功', '2021-04-27 13:57:28');
INSERT INTO `sys_logininfor` VALUES (807, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '1', '用户不存在/密码错误', '2021-04-27 13:57:42');
INSERT INTO `sys_logininfor` VALUES (808, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 9', 'Mac OS X', '0', '登录成功', '2021-04-27 13:57:52');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `is_frame` int(1) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2024 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 3, 'system', NULL, 1, 0, 'M', '0', '0', '', 'system', 'admin', '2021-01-18 10:39:19', 'superAdmin', '2021-01-25 18:10:51', '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 3, 'monitor', NULL, 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2021-01-18 10:39:19', 'admin', '2021-01-18 13:35:18', '系统监控目录');
INSERT INTO `sys_menu` VALUES (3, '系统工具', 0, 4, 'tool', NULL, 1, 0, 'M', '0', '0', '', 'tool', 'admin', '2021-01-18 10:39:19', 'admin', '2021-01-18 13:35:22', '系统工具目录');
INSERT INTO `sys_menu` VALUES (4, '天堂云官网', 0, 5, 'http://www.ttclouds.cn', NULL, 0, 0, 'M', '0', '0', '', 'guide', 'admin', '2021-01-18 10:39:19', 'admin', '2021-01-18 13:35:27', '若依官网地址');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2021-01-18 10:39:19', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2021-01-18 10:39:19', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2021-01-18 10:39:19', '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2021-01-18 10:39:19', '', NULL, '部门管理菜单');
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', '2021-01-18 10:39:19', '', NULL, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2021-01-18 10:39:19', '', NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2021-01-18 10:39:19', '', NULL, '参数设置菜单');
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2021-01-18 10:39:19', '', NULL, '通知公告菜单');
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, 'log', '', 1, 0, 'M', '0', '0', '', 'log', 'admin', '2021-01-18 10:39:19', '', NULL, '日志管理菜单');
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2021-01-18 10:39:19', '', NULL, '在线用户菜单');
INSERT INTO `sys_menu` VALUES (111, '数据监控', 2, 3, 'druid', 'monitor/druid/index', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2021-01-18 10:39:19', '', NULL, '数据监控菜单');
INSERT INTO `sys_menu` VALUES (112, '服务监控', 2, 4, 'server', 'monitor/server/index', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2021-01-18 10:39:19', '', NULL, '服务监控菜单');
INSERT INTO `sys_menu` VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2021-01-18 10:39:19', '', NULL, '缓存监控菜单');
INSERT INTO `sys_menu` VALUES (114, '表单构建', 3, 1, 'build', 'tool/build/index', 1, 0, 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2021-01-18 10:39:19', '', NULL, '表单构建菜单');
INSERT INTO `sys_menu` VALUES (115, '代码生成', 3, 2, 'gen', 'tool/gen/index', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2021-01-18 10:39:19', '', NULL, '代码生成菜单');
INSERT INTO `sys_menu` VALUES (116, '系统接口', 3, 3, 'swagger', 'tool/swagger/index', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2021-01-18 10:39:19', '', NULL, '系统接口菜单');
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2021-01-18 10:39:19', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2021-01-18 10:39:19', '', NULL, '登录日志菜单');
INSERT INTO `sys_menu` VALUES (1001, '用户查询', 100, 1, '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户新增', 100, 2, '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户修改', 100, 3, '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户删除', 100, 4, '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导出', 100, 5, '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '用户导入', 100, 6, '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '重置密码', 100, 7, '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色查询', 101, 1, '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色新增', 101, 2, '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色修改', 101, 3, '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色删除', 101, 4, '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '角色导出', 101, 5, '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单查询', 102, 1, '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单新增', 102, 2, '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单修改', 102, 3, '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1016, '菜单删除', 102, 4, '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1017, '部门查询', 103, 1, '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1018, '部门新增', 103, 2, '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1019, '部门修改', 103, 3, '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1020, '部门删除', 103, 4, '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1021, '岗位查询', 104, 1, '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1022, '岗位新增', 104, 2, '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1023, '岗位修改', 104, 3, '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1024, '岗位删除', 104, 4, '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1025, '岗位导出', 104, 5, '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1026, '字典查询', 105, 1, '#', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1027, '字典新增', 105, 2, '#', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1028, '字典修改', 105, 3, '#', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1029, '字典删除', 105, 4, '#', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1030, '字典导出', 105, 5, '#', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1031, '参数查询', 106, 1, '#', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1032, '参数新增', 106, 2, '#', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1033, '参数修改', 106, 3, '#', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1034, '参数删除', 106, 4, '#', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1035, '参数导出', 106, 5, '#', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1036, '公告查询', 107, 1, '#', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1037, '公告新增', 107, 2, '#', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1038, '公告修改', 107, 3, '#', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1039, '公告删除', 107, 4, '#', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1040, '操作查询', 500, 1, '#', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1041, '操作删除', 500, 2, '#', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1042, '日志导出', 500, 4, '#', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1043, '登录查询', 501, 1, '#', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1044, '登录删除', 501, 2, '#', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1045, '日志导出', 501, 3, '#', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1046, '在线查询', 109, 1, '#', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1047, '批量强退', 109, 2, '#', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1048, '单条强退', 109, 3, '#', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1055, '生成查询', 115, 1, '#', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1056, '生成修改', 115, 2, '#', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1057, '生成删除', 115, 3, '#', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1058, '导入代码', 115, 2, '#', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1059, '预览代码', 115, 4, '#', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1060, '生成代码', 115, 5, '#', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2000, '会议管理', 0, 1, 'conference', NULL, 1, 0, 'M', '0', '0', '', 'table', 'admin', '2021-01-18 13:34:44', 'superAdmin', '2021-02-28 11:59:01', '');
INSERT INTO `sys_menu` VALUES (2002, '会议模板', 2000, 2, 'template', 'conference/template/index', 1, 1, 'C', '0', '0', '', 'component', 'admin', '2021-01-18 14:52:52', 'superAdmin', '2021-02-04 15:51:13', '');
INSERT INTO `sys_menu` VALUES (2004, '新增模板', 2000, 6, 'add-template', 'conference/addTemplate/index2', 1, 0, 'C', '1', '0', '', 'input', 'admin', '2021-01-18 15:27:32', 'superAdmin', '2021-03-22 17:33:47', '');
INSERT INTO `sys_menu` VALUES (2005, '模板修改', 2000, 7, 'modify-template', 'conference/modifyTemplate/index2', 1, 0, 'C', '1', '0', '', 'edit', 'admin', '2021-01-18 15:29:46', 'superAdmin', '2021-03-22 17:33:52', '');
INSERT INTO `sys_menu` VALUES (2007, '会议控制', 2000, 5, 'view-control', 'conference/viewControl/index', 1, 0, 'C', '1', '0', '', 'button', 'admin', '2021-01-18 15:51:00', 'superAdmin', '2021-02-05 10:48:48', '');
INSERT INTO `sys_menu` VALUES (2008, '终端管理', 2009, 4, 'terminal', 'config/terminal/index', 1, 0, 'C', '0', '0', '', 'international', 'admin', '2021-01-19 10:47:57', 'superAdmin', '2021-01-26 11:10:07', '');
INSERT INTO `sys_menu` VALUES (2009, '配置管理', 0, 2, 'configManagement', NULL, 1, 0, 'M', '0', '0', '', 'server', 'superAdmin', '2021-01-25 18:08:23', 'superAdmin', '2021-02-04 13:19:01', '');
INSERT INTO `sys_menu` VALUES (2010, '入会方案配置', 2009, 1, 'project', 'config/project/index', 1, 0, 'C', '0', '0', NULL, 'radio', 'superAdmin', '2021-01-26 11:21:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2011, '会议号配置', 2009, 2, 'conferenceNumber', 'config/conferenceNumber/index', 1, 0, 'C', '0', '0', '', 'number', 'superAdmin', '2021-01-26 19:18:08', 'superAdmin', '2021-01-26 19:18:52', '');
INSERT INTO `sys_menu` VALUES (2012, 'FME配置', 2009, 4, 'fme', 'config/fme/index', 1, 0, 'C', '0', '0', '', 'server', 'superAdmin', '2021-01-28 17:10:51', 'superAdmin', '2021-03-31 16:50:56', '');
INSERT INTO `sys_menu` VALUES (2013, '租户FME绑定', 2009, 5, 'fmeallot', 'config/fmeallot/index', 1, 0, 'C', '0', '0', '', 'cascader', 'superAdmin', '2021-01-28 17:13:59', 'superAdmin', '2021-03-31 16:52:55', '');
INSERT INTO `sys_menu` VALUES (2014, '我的会议', 2000, 1, 'conferenceList', 'conference/conferenceList/index', 1, 0, 'C', '1', '0', '', 'list', 'superAdmin', '2021-02-04 15:51:02', 'superAdmin', '2021-04-27 10:36:48', '');
INSERT INTO `sys_menu` VALUES (2015, '全局参数', 1, 10, 'parameter', 'system/parameter/index', 1, 0, 'C', '0', '0', '', 'skill', 'superAdmin', '2021-02-28 10:50:30', 'superAdmin', '2021-02-28 10:53:03', '');
INSERT INTO `sys_menu` VALUES (2016, '录制管理', 0, 4, 'record', 'record/index', 1, 0, 'C', '0', '0', '', 'luzhi', 'superAdmin', '2021-02-28 12:40:42', 'superAdmin', '2021-02-28 12:43:23', '');
INSERT INTO `sys_menu` VALUES (2019, '会议控制-表格', 2000, 3, 'table-control', 'conference/tableControl/index', 1, 0, 'C', '1', '0', '', 'button', 'superAdmin', '2021-03-08 10:05:51', 'superAdmin', '2021-03-09 15:34:07', '');
INSERT INTO `sys_menu` VALUES (2020, '会议室调试', 3, 4, 'conference-debugger', 'tool/debug/index', 1, 0, 'C', '1', '0', '', 'bug', 'superAdmin', '2021-03-25 17:28:40', 'superAdmin', '2021-03-25 17:34:55', '');
INSERT INTO `sys_menu` VALUES (2021, 'FSBC服务器', 2009, 6, 'FSBC', 'config/fsbcServer/index', 1, 0, 'C', '0', '0', '', 'redis', 'superAdmin', '2021-04-21 11:20:55', 'superAdmin', '2021-04-21 11:43:26', '');
INSERT INTO `sys_menu` VALUES (2022, '租户FSBC绑定', 2009, 7, 'fsbcallot', 'config/fsbcAllot/index', 1, 0, 'C', '0', '0', '', 'cascader', 'superAdmin', '2021-04-21 13:50:51', 'superAdmin', '2021-04-21 13:51:11', '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob NULL COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (1, '温馨提醒：2021-05-01 天堂云会控系统新版本发布啦', '2', 0xE696B0E78988E69CACE58685E5AEB9, '0', 'admin', '2021-01-18 10:39:23', 'superAdmin', '2021-04-20 04:05:05', '管理员');
INSERT INTO `sys_notice` VALUES (2, '维护通知：2018-07-01 天堂云会控系统凌晨维护', '1', 0xE7BBB4E68AA4E58685E5AEB9, '0', 'admin', '2021-01-18 10:39:23', '', NULL, '管理员');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(8000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int(1) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`oper_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_post` VALUES (2, 'se', '项目经理', 2, '0', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_post` VALUES (3, 'hr', '人力资源', 3, '0', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_post` VALUES (4, 'user', '普通员工', 4, '0', 'admin', '2021-01-18 10:39:19', '', NULL, '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '该角色是哪个部门创建的',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `del_flag`(`del_flag`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'superAdmin', 1, '1', 1, 1, NULL, '0', '0', 'admin', '2021-01-18 10:39:19', 'superAdmin', '2021-04-26 10:17:04', '超级管理员');
INSERT INTO `sys_role` VALUES (2, '系统管理员', 'systemAdmin', 2, '4', 1, 1, 1, '0', '0', 'admin', '2021-01-18 10:39:19', 'superAdmin', '2021-04-26 10:19:07', '普通角色');
INSERT INTO `sys_role` VALUES (5, '管理员', 'admin', 3, '1', 1, 1, 100, '0', '0', 'superAdmin', '2021-04-26 10:18:18', 'superAdmin', '2021-04-26 10:18:28', NULL);
INSERT INTO `sys_role` VALUES (6, '用户管理员', 'userAdmin', 4, '1', 1, 1, 220, '0', '0', 'superAdmin', '2021-04-26 10:20:04', 'superAdmin', '2021-04-26 15:11:59', NULL);

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  CONSTRAINT `sys_role_dept_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `sys_role_dept_ibfk_2` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 100);
INSERT INTO `sys_role_menu` VALUES (2, 101);
INSERT INTO `sys_role_menu` VALUES (2, 103);
INSERT INTO `sys_role_menu` VALUES (2, 1001);
INSERT INTO `sys_role_menu` VALUES (2, 1002);
INSERT INTO `sys_role_menu` VALUES (2, 1003);
INSERT INTO `sys_role_menu` VALUES (2, 1004);
INSERT INTO `sys_role_menu` VALUES (2, 1005);
INSERT INTO `sys_role_menu` VALUES (2, 1006);
INSERT INTO `sys_role_menu` VALUES (2, 1007);
INSERT INTO `sys_role_menu` VALUES (2, 1008);
INSERT INTO `sys_role_menu` VALUES (2, 1009);
INSERT INTO `sys_role_menu` VALUES (2, 1010);
INSERT INTO `sys_role_menu` VALUES (2, 1011);
INSERT INTO `sys_role_menu` VALUES (2, 1012);
INSERT INTO `sys_role_menu` VALUES (2, 1017);
INSERT INTO `sys_role_menu` VALUES (2, 1018);
INSERT INTO `sys_role_menu` VALUES (2, 1019);
INSERT INTO `sys_role_menu` VALUES (2, 1020);
INSERT INTO `sys_role_menu` VALUES (2, 2000);
INSERT INTO `sys_role_menu` VALUES (2, 2002);
INSERT INTO `sys_role_menu` VALUES (2, 2004);
INSERT INTO `sys_role_menu` VALUES (2, 2005);
INSERT INTO `sys_role_menu` VALUES (2, 2007);
INSERT INTO `sys_role_menu` VALUES (2, 2008);
INSERT INTO `sys_role_menu` VALUES (2, 2009);
INSERT INTO `sys_role_menu` VALUES (2, 2010);
INSERT INTO `sys_role_menu` VALUES (2, 2011);
INSERT INTO `sys_role_menu` VALUES (2, 2012);
INSERT INTO `sys_role_menu` VALUES (2, 2013);
INSERT INTO `sys_role_menu` VALUES (2, 2014);
INSERT INTO `sys_role_menu` VALUES (2, 2016);
INSERT INTO `sys_role_menu` VALUES (2, 2019);
INSERT INTO `sys_role_menu` VALUES (2, 2021);
INSERT INTO `sys_role_menu` VALUES (2, 2022);
INSERT INTO `sys_role_menu` VALUES (3, 1);
INSERT INTO `sys_role_menu` VALUES (3, 100);
INSERT INTO `sys_role_menu` VALUES (3, 101);
INSERT INTO `sys_role_menu` VALUES (3, 103);
INSERT INTO `sys_role_menu` VALUES (3, 1001);
INSERT INTO `sys_role_menu` VALUES (3, 1002);
INSERT INTO `sys_role_menu` VALUES (3, 1003);
INSERT INTO `sys_role_menu` VALUES (3, 1004);
INSERT INTO `sys_role_menu` VALUES (3, 1005);
INSERT INTO `sys_role_menu` VALUES (3, 1006);
INSERT INTO `sys_role_menu` VALUES (3, 1007);
INSERT INTO `sys_role_menu` VALUES (3, 1008);
INSERT INTO `sys_role_menu` VALUES (3, 1009);
INSERT INTO `sys_role_menu` VALUES (3, 1010);
INSERT INTO `sys_role_menu` VALUES (3, 1011);
INSERT INTO `sys_role_menu` VALUES (3, 1012);
INSERT INTO `sys_role_menu` VALUES (3, 1017);
INSERT INTO `sys_role_menu` VALUES (3, 1018);
INSERT INTO `sys_role_menu` VALUES (3, 1019);
INSERT INTO `sys_role_menu` VALUES (3, 1020);
INSERT INTO `sys_role_menu` VALUES (3, 2000);
INSERT INTO `sys_role_menu` VALUES (3, 2002);
INSERT INTO `sys_role_menu` VALUES (3, 2004);
INSERT INTO `sys_role_menu` VALUES (3, 2005);
INSERT INTO `sys_role_menu` VALUES (3, 2007);
INSERT INTO `sys_role_menu` VALUES (3, 2008);
INSERT INTO `sys_role_menu` VALUES (3, 2009);
INSERT INTO `sys_role_menu` VALUES (3, 2010);
INSERT INTO `sys_role_menu` VALUES (3, 2011);
INSERT INTO `sys_role_menu` VALUES (5, 1);
INSERT INTO `sys_role_menu` VALUES (5, 100);
INSERT INTO `sys_role_menu` VALUES (5, 101);
INSERT INTO `sys_role_menu` VALUES (5, 103);
INSERT INTO `sys_role_menu` VALUES (5, 1001);
INSERT INTO `sys_role_menu` VALUES (5, 1002);
INSERT INTO `sys_role_menu` VALUES (5, 1003);
INSERT INTO `sys_role_menu` VALUES (5, 1004);
INSERT INTO `sys_role_menu` VALUES (5, 1005);
INSERT INTO `sys_role_menu` VALUES (5, 1006);
INSERT INTO `sys_role_menu` VALUES (5, 1007);
INSERT INTO `sys_role_menu` VALUES (5, 1008);
INSERT INTO `sys_role_menu` VALUES (5, 1009);
INSERT INTO `sys_role_menu` VALUES (5, 1010);
INSERT INTO `sys_role_menu` VALUES (5, 1011);
INSERT INTO `sys_role_menu` VALUES (5, 1012);
INSERT INTO `sys_role_menu` VALUES (5, 1017);
INSERT INTO `sys_role_menu` VALUES (5, 1018);
INSERT INTO `sys_role_menu` VALUES (5, 1019);
INSERT INTO `sys_role_menu` VALUES (5, 1020);
INSERT INTO `sys_role_menu` VALUES (5, 2000);
INSERT INTO `sys_role_menu` VALUES (5, 2002);
INSERT INTO `sys_role_menu` VALUES (5, 2004);
INSERT INTO `sys_role_menu` VALUES (5, 2005);
INSERT INTO `sys_role_menu` VALUES (5, 2007);
INSERT INTO `sys_role_menu` VALUES (5, 2008);
INSERT INTO `sys_role_menu` VALUES (5, 2009);
INSERT INTO `sys_role_menu` VALUES (5, 2010);
INSERT INTO `sys_role_menu` VALUES (5, 2011);
INSERT INTO `sys_role_menu` VALUES (5, 2012);
INSERT INTO `sys_role_menu` VALUES (5, 2013);
INSERT INTO `sys_role_menu` VALUES (5, 2014);
INSERT INTO `sys_role_menu` VALUES (5, 2016);
INSERT INTO `sys_role_menu` VALUES (5, 2019);
INSERT INTO `sys_role_menu` VALUES (5, 2021);
INSERT INTO `sys_role_menu` VALUES (5, 2022);
INSERT INTO `sys_role_menu` VALUES (6, 1);
INSERT INTO `sys_role_menu` VALUES (6, 100);
INSERT INTO `sys_role_menu` VALUES (6, 101);
INSERT INTO `sys_role_menu` VALUES (6, 103);
INSERT INTO `sys_role_menu` VALUES (6, 1001);
INSERT INTO `sys_role_menu` VALUES (6, 1002);
INSERT INTO `sys_role_menu` VALUES (6, 1003);
INSERT INTO `sys_role_menu` VALUES (6, 1004);
INSERT INTO `sys_role_menu` VALUES (6, 1005);
INSERT INTO `sys_role_menu` VALUES (6, 1006);
INSERT INTO `sys_role_menu` VALUES (6, 1007);
INSERT INTO `sys_role_menu` VALUES (6, 1008);
INSERT INTO `sys_role_menu` VALUES (6, 1009);
INSERT INTO `sys_role_menu` VALUES (6, 1010);
INSERT INTO `sys_role_menu` VALUES (6, 1011);
INSERT INTO `sys_role_menu` VALUES (6, 1012);
INSERT INTO `sys_role_menu` VALUES (6, 1017);
INSERT INTO `sys_role_menu` VALUES (6, 1018);
INSERT INTO `sys_role_menu` VALUES (6, 1019);
INSERT INTO `sys_role_menu` VALUES (6, 1020);
INSERT INTO `sys_role_menu` VALUES (6, 2000);
INSERT INTO `sys_role_menu` VALUES (6, 2002);
INSERT INTO `sys_role_menu` VALUES (6, 2004);
INSERT INTO `sys_role_menu` VALUES (6, 2005);
INSERT INTO `sys_role_menu` VALUES (6, 2007);
INSERT INTO `sys_role_menu` VALUES (6, 2008);
INSERT INTO `sys_role_menu` VALUES (6, 2009);
INSERT INTO `sys_role_menu` VALUES (6, 2010);
INSERT INTO `sys_role_menu` VALUES (6, 2011);
INSERT INTO `sys_role_menu` VALUES (6, 2014);
INSERT INTO `sys_role_menu` VALUES (6, 2016);
INSERT INTO `sys_role_menu` VALUES (6, 2019);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `user_name`(`user_name`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 107 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, NULL, 'superAdmin', '超级管理员', '00', 'ry@163.com', '15888888888', '1', '/profile/avatar/2021/01/19/35ea7b84-19cf-4e18-8d98-3e396a080a4f.jpeg', '$2a$10$gmmDucg7xdbsyrbxsU1Er.7acAs9UfsqFPYMOwyxHE9O.MChiXfJa', '0', '0', '127.0.0.1', '2021-01-18 10:39:18', 'admin', '2021-01-18 10:39:18', '', NULL, '管理员');
INSERT INTO `sys_user` VALUES (100, 220, 'admin', '三门峡云平台', '00', '', '', '0', '', '$2a$10$d1wmaSUmRR8bs.Djrx5Bouyb4bE9i/0tn5nfAdfjiMd41IZcqFSp2', '0', '0', '', NULL, 'superAdmin', '2021-01-19 11:38:00', 'superAdmin', '2021-04-26 18:42:15', NULL);
INSERT INTO `sys_user` VALUES (106, 221, 'zfw', '政法委', '00', '', '', '0', '', '$2a$10$d1wmaSUmRR8bs.Djrx5Bouyb4bE9i/0tn5nfAdfjiMd41IZcqFSp2', '0', '0', '', NULL, 'superAdmin', '2021-04-26 18:40:22', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (1, 1);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `role_id`(`role_id`) USING BTREE,
  CONSTRAINT `sys_user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `sys_user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (100, 5);
INSERT INTO `sys_user_role` VALUES (106, 6);

SET FOREIGN_KEY_CHECKS = 1;
