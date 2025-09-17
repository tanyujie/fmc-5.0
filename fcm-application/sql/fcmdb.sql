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

 Date: 28/01/2021 18:26:59
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
  CONSTRAINT `busi_call_leg_profile_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '入会方案配置，控制参会者进入会议的方案' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_call_leg_profile
-- ----------------------------
INSERT INTO `busi_call_leg_profile` VALUES (8, '2021-01-28 12:27:48', NULL, '24daa8e4-17dd-47d9-9071-9b1d53c671ad', 1, 100, 19);

-- ----------------------------
-- Table structure for busi_conference_number
-- ----------------------------
DROP TABLE IF EXISTS `busi_conference_number`;
CREATE TABLE `busi_conference_number`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，充当会议号',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '归属公司ID',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '会议号类型：1默认，2普通',
  `status` int(11) NULL DEFAULT NULL COMMENT '号码状态：1闲置，10已预约，100会议中',
  `remarks` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dept_id`(`dept_id`, `type`) USING BTREE,
  CONSTRAINT `busi_conference_number_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议号码记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_conference_number
-- ----------------------------
INSERT INTO `busi_conference_number` VALUES (1111111111, '2021-01-27 11:51:49', '2021-01-27 11:54:55', 100, 2, 1, 'ads');
INSERT INTO `busi_conference_number` VALUES (2222222222, '2021-01-27 11:51:59', NULL, 100, 2, 1, 'xvczx');
INSERT INTO `busi_conference_number` VALUES (3333333333, '2021-01-27 11:56:31', NULL, 100, 1, 1, '13');

-- ----------------------------
-- Table structure for busi_fme
-- ----------------------------
DROP TABLE IF EXISTS `busi_fme`;
CREATE TABLE `busi_fme`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '终端创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '终端修改时间',
  `group_id` bigint(20) NULL DEFAULT NULL COMMENT '所属fme组',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'fme显示名字',
  `ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备的IP地址',
  `port` int(11) NULL DEFAULT 9443 COMMENT 'fme端口',
  `priority` int(11) NULL DEFAULT NULL COMMENT '优先级，越大越高(备用节点才会有优先级)',
  `status` int(11) NULL DEFAULT NULL COMMENT 'FME在线状态：1在线，2离线，3删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE,
  UNIQUE INDEX `ip`(`ip`) USING BTREE,
  CONSTRAINT `busi_fme_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `busi_fme_group` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'FME终端信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_fme
-- ----------------------------
INSERT INTO `busi_fme` VALUES (19, '2021-01-28 12:27:02', '2021-01-28 18:17:12', 1, '93', '172.16.100.93', 9443, 100, 1);

-- ----------------------------
-- Table structure for busi_fme_group
-- ----------------------------
DROP TABLE IF EXISTS `busi_fme_group`;
CREATE TABLE `busi_fme_group`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'FME的连接用户名，同一个组下的用户名相同',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'FME的连接密码，同一个组下的密码相同',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组名，最长32',
  `type` int(11) NULL DEFAULT NULL COMMENT '1集群，2单节点',
  `busi_type` int(11) NULL DEFAULT NULL COMMENT '类型：1主用，2备用',
  `spare_fme_group` bigint(20) NULL DEFAULT NULL COMMENT '备用的FME节点（可以指向集群组和单节点组）',
  `description` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `spare_fme_group`(`spare_fme_group`) USING BTREE,
  CONSTRAINT `busi_fme_group_ibfk_2` FOREIGN KEY (`spare_fme_group`) REFERENCES `busi_fme_group` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'FME终端组' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_fme_group
-- ----------------------------
INSERT INTO `busi_fme_group` VALUES (1, '2021-01-21 10:45:51', NULL, 'admin', 'P@rad1se', '单节点组', 2, 1, NULL, '单节点组');
INSERT INTO `busi_fme_group` VALUES (4, NULL, NULL, 'cz', 'qadas', '测试备用FME组1', 2, 2, NULL, '备注描述~~~~');
INSERT INTO `busi_fme_group` VALUES (21, '2021-01-22 12:41:01', NULL, 'superAdmin', 'admin123', '测试备用FME组2', 2, 2, NULL, '备注描述1111');
INSERT INTO `busi_fme_group` VALUES (22, '2021-01-22 14:04:42', NULL, 'superAdmin', 'admin123', '测试FME组', 2, 1, 4, 'fdsfsdsadadcczxcxzczxc');
INSERT INTO `busi_fme_group` VALUES (24, '2021-01-22 15:50:04', NULL, 'superAdmin', 'admin123', '测试备用FME组3', 1, 2, NULL, '备注描述2222');
INSERT INTO `busi_fme_group` VALUES (25, '2021-01-25 18:16:09', NULL, 'superAdmin', 'admin123', 'dsad', 2, 1, NULL, NULL);
INSERT INTO `busi_fme_group` VALUES (27, '2021-01-25 18:18:11', NULL, 'superAdmin', 'admin123', 'dsadasd', 2, 1, NULL, NULL);
INSERT INTO `busi_fme_group` VALUES (28, '2021-01-25 18:18:40', NULL, 'superAdmin', 'admin123', 'fdsfsd', 2, 1, NULL, NULL);
INSERT INTO `busi_fme_group` VALUES (29, '2021-01-25 18:19:15', NULL, 'superAdmin', 'admin123', 'rwerwe', 2, 1, 21, 'dsad');
INSERT INTO `busi_fme_group` VALUES (31, '2021-01-25 18:57:04', NULL, '111', '111', '111', 2, 2, NULL, NULL);
INSERT INTO `busi_fme_group` VALUES (37, '2021-01-26 09:56:40', NULL, 'admin', 'P@rad1se', '阿勒泰FME组', 2, 1, NULL, NULL);

-- ----------------------------
-- Table structure for busi_fme_group_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_fme_group_dept`;
CREATE TABLE `busi_fme_group_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '分配给的租户',
  `fme_group_id` bigint(20) NULL DEFAULT NULL COMMENT 'fme组ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `busi_fme_group_dept_ibfk_1`(`dept_id`) USING BTREE,
  INDEX `fme_group_id`(`fme_group_id`) USING BTREE,
  CONSTRAINT `busi_fme_group_dept_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_fme_group_dept_ibfk_2` FOREIGN KEY (`fme_group_id`) REFERENCES `busi_fme_group` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'FME组分配租户的中间表（一个FME组可以分配给多个租户，一对多）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_fme_group_dept
-- ----------------------------
INSERT INTO `busi_fme_group_dept` VALUES (1, '2021-01-28 16:49:44', NULL, 100, 1);

-- ----------------------------
-- Table structure for busi_history_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_history_conference`;
CREATE TABLE `busi_history_conference`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
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
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板会议名',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `call_leg_profile_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '入会方案配置ID（关联FME里面的入会方案记录ID，会控端不存）',
  `bandwidth` int(11) NULL DEFAULT NULL COMMENT '带宽1,2,3,4,5,6M',
  `is_auto_call` int(11) NULL DEFAULT NULL COMMENT '是否自动呼叫与会者：1是，2否',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `busi_conference_template_ibfk_1`(`dept_id`) USING BTREE,
  INDEX `call_leg_profile_id`(`call_leg_profile_id`) USING BTREE,
  CONSTRAINT `busi_template_conference_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_conference
-- ----------------------------
INSERT INTO `busi_template_conference` VALUES (2, '2021-01-28 12:58:32', NULL, '测试模板', 100, '24daa8e4-17dd-47d9-9071-9b1d53c671ad', 2, 1);
INSERT INTO `busi_template_conference` VALUES (3, '2021-01-28 13:01:42', NULL, 'dasdasda', NULL, '24daa8e4-17dd-47d9-9071-9b1d53c671ad', 2, 1);
INSERT INTO `busi_template_conference` VALUES (4, '2021-01-28 13:09:08', NULL, '模板二', 100, '24daa8e4-17dd-47d9-9071-9b1d53c671ad', 2, 1);

-- ----------------------------
-- Table structure for busi_template_participant
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_participant`;
CREATE TABLE `busi_template_participant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `terminal_id` bigint(20) NULL DEFAULT NULL COMMENT '终端ID',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`, `weight`) USING BTREE,
  INDEX `terminal_id`(`terminal_id`) USING BTREE,
  CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_participant_ibfk_3` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议模板的参会者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_participant
-- ----------------------------
INSERT INTO `busi_template_participant` VALUES (5, NULL, NULL, 2, 19, 4);
INSERT INTO `busi_template_participant` VALUES (6, NULL, NULL, 2, 13, 3);
INSERT INTO `busi_template_participant` VALUES (7, NULL, NULL, 2, 17, 2);
INSERT INTO `busi_template_participant` VALUES (8, NULL, NULL, 2, 18, 1);
INSERT INTO `busi_template_participant` VALUES (9, NULL, NULL, 4, 13, 5);
INSERT INTO `busi_template_participant` VALUES (10, NULL, NULL, 4, 15, 4);
INSERT INTO `busi_template_participant` VALUES (11, NULL, NULL, 4, 17, 3);
INSERT INTO `busi_template_participant` VALUES (12, NULL, NULL, 4, 18, 2);
INSERT INTO `busi_template_participant` VALUES (13, NULL, NULL, 4, 19, 1);

-- ----------------------------
-- Table structure for busi_terminal
-- ----------------------------
DROP TABLE IF EXISTS `busi_terminal`;
CREATE TABLE `busi_terminal`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '终端创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '终端修改时间',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '终端所属部门ID',
  `ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备的IP地址',
  `number` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备号，设备唯一标识',
  `camera_ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '摄像头IP地址',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '终端显示名字',
  `type` int(11) NULL DEFAULT NULL COMMENT '终端类型，枚举值int类型',
  `online_status` int(11) NULL DEFAULT NULL COMMENT '终端状态：1在线，2离线',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ip`(`ip`, `number`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  CONSTRAINT `busi_terminal_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '终端信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_terminal
-- ----------------------------
INSERT INTO `busi_terminal` VALUES (3, '2021-01-25 13:22:38', NULL, 200, '172.16.100.123', '2312', NULL, 'test', 2, 2);
INSERT INTO `busi_terminal` VALUES (6, '2021-01-25 14:17:19', NULL, 103, '172.16.100.124', '3212', '172.16.100.123', 'dwe', 0, 2);
INSERT INTO `busi_terminal` VALUES (13, '2021-01-25 19:45:39', NULL, 100, '172.16.100.127', '3213', NULL, 'tryrt', 0, 2);
INSERT INTO `busi_terminal` VALUES (15, '2021-01-25 20:33:44', '2021-01-28 13:08:44', 100, '172.16.100.147', NULL, NULL, '172.16.100.147', 100, 1);
INSERT INTO `busi_terminal` VALUES (17, '2021-01-27 20:49:53', NULL, 100, '172.16.100.121', 'das', NULL, 'dasda', 0, 2);
INSERT INTO `busi_terminal` VALUES (18, '2021-01-27 20:50:08', NULL, 100, '172.16.100.182', '32131', NULL, 'fsfsfzda', 0, 2);
INSERT INTO `busi_terminal` VALUES (19, '2021-01-27 20:50:21', '2021-01-28 17:32:35', 100, '172.16.100.127', 'dsadas', NULL, 'fdsd4444', 0, 2);

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
) ENGINE = InnoDB AUTO_INCREMENT = 187 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table
-- ----------------------------
INSERT INTO `gen_table` VALUES (173, 'busi_conference_number', '会议号码记录表', NULL, NULL, 'BusiConferenceNumber', 'crud', 'com.paradisecloud.fcm', 'busi', 'number', '会议号码记录', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-01-20 16:15:27', '', '2021-01-20 16:16:35', NULL);
INSERT INTO `gen_table` VALUES (174, 'busi_fme', 'FME终端信息表', NULL, NULL, 'BusiFme', 'crud', 'com.paradisecloud.fcm', 'busi', 'fme', 'FME终端信息', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (175, 'busi_fme_group', 'FME终端组', NULL, NULL, 'BusiFmeGroup', 'crud', 'com.paradisecloud.fcm', 'busi', 'group', 'FME终端组', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (176, 'busi_history_conference', '历史会议，每次挂断会保存该历史记录', NULL, NULL, 'BusiHistoryConference', 'crud', 'com.paradisecloud.fcm', 'busi', 'conference', '历史会议，每次挂断会保存该历史记录', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (177, 'busi_history_participant', '历史会议的参会者', NULL, NULL, 'BusiHistoryParticipant', 'crud', 'com.paradisecloud.fcm', 'busi', 'participant', '历史会议的参会者', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (178, 'busi_param_config', '业务参数配置表', NULL, NULL, 'BusiParamConfig', 'crud', 'com.paradisecloud.fcm', 'busi', 'config', '业务参数配置', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (179, 'busi_template_conference', '会议模板表', NULL, NULL, 'BusiTemplateConference', 'crud', 'com.paradisecloud.fcm', 'busi', 'conference', '会议模板', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (180, 'busi_template_participant', '会议模板的参会者', NULL, NULL, 'BusiTemplateParticipant', 'crud', 'com.paradisecloud.fcm', 'busi', 'participant', '会议模板的参会者', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (181, 'busi_terminal', '终端信息表', NULL, NULL, 'BusiTerminal', 'crud', 'com.paradisecloud.fcm', 'busi', 'terminal', '终端信息', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (184, 'busi_call_leg_profile', '入会方案配置，控制参会者进入会议的方案', NULL, NULL, 'BusiCallLegProfile', 'crud', 'com.paradisecloud.fcm', 'busi', 'callLegProfile', '入会方案配置，控制参会者进入会议的方案', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22', NULL);
INSERT INTO `gen_table` VALUES (186, 'busi_fme_group_dept', 'FME组分配租户的中间表（一个FME组可以分配给多个租户，一对多）', NULL, NULL, 'BusiFmeGroupDept', 'crud', 'com.paradisecloud.fcm', 'busi', 'dept', 'FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-01-28 14:42:35', '', '2021-01-28 14:42:54', NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 1460 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

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
INSERT INTO `gen_table_column` VALUES (1363, '174', 'group_id', '所属fme组', 'bigint(20)', 'Long', 'groupId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
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
INSERT INTO `gen_table_column` VALUES (1394, '178', 'id', '主键ID', 'bigint(11)', 'Integer', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1395, '178', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1396, '178', 'update_time', '创建时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1397, '178', 'dept_id', '归属部门ID（部门就是公司，一个客户的概念）', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1398, '178', 'default_call_leg_profile_id', '默认入会方案ID', 'varchar(128)', 'String', 'defaultCallLegProfileId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1399, '178', 'default_conference_number', '默认会议号码', 'int(11)', 'Integer', 'defaultConferenceNumber', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
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
INSERT INTO `gen_table_column` VALUES (1415, '181', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1416, '181', 'create_time', '终端创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1417, '181', 'update_time', '终端修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1418, '181', 'dept_id', '终端所属部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1419, '181', 'ip', '设备的IP地址', 'varchar(16)', 'String', 'ip', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1420, '181', 'number', '设备号，设备唯一标识', 'varchar(16)', 'String', 'number', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1421, '181', 'camera_ip', '摄像头IP地址', 'varchar(16)', 'String', 'cameraIp', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1422, '181', 'name', '终端显示名字', 'varchar(32)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 8, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1423, '181', 'type', '终端类型，枚举值int类型', 'int(11)', 'Integer', 'type', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 9, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1424, '181', 'online_status', '终端状态：1在线，2离线', 'int(11)', 'Integer', 'onlineStatus', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'radio', '', 10, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1425, '174', 'dept_id', '归属部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-01-21 10:56:33', '', NULL);
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
) ENGINE = InnoDB AUTO_INCREMENT = 210 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 0, '0', '新疆', 0, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-18 10:39:18', 'superAdmin', '2021-01-22 10:23:07');
INSERT INTO `sys_dept` VALUES (103, 100, '0,100', '伊犁州', 1, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-18 10:39:18', 'superAdmin', '2021-01-22 10:23:07');
INSERT INTO `sys_dept` VALUES (104, 100, '0,100', '克州', 2, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-18 10:39:18', 'admin', '2021-01-19 11:26:40');
INSERT INTO `sys_dept` VALUES (105, 100, '0,100', '喀什', 3, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-18 10:39:18', 'admin', '2021-01-19 11:26:46');
INSERT INTO `sys_dept` VALUES (106, 100, '0,100', '和田', 4, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-18 10:39:18', 'admin', '2021-01-19 11:26:50');
INSERT INTO `sys_dept` VALUES (107, 100, '0,100', '阿克苏', 5, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-18 10:39:18', 'admin', '2021-01-19 11:26:54');
INSERT INTO `sys_dept` VALUES (200, 100, '0,100', '巴州', 6, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:25:26', 'admin', '2021-01-19 11:27:04');
INSERT INTO `sys_dept` VALUES (201, 100, '0,100', '吐鲁番', 7, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:25:37', 'admin', '2021-01-19 11:27:11');
INSERT INTO `sys_dept` VALUES (202, 100, '0,100', '哈密市', 8, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:26:03', 'admin', '2021-01-19 11:27:15');
INSERT INTO `sys_dept` VALUES (203, 100, '0,100', '乌鲁木齐', 9, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:47:37', 'admin', '2021-01-19 11:48:00');
INSERT INTO `sys_dept` VALUES (204, 100, '0,100', '昌吉州', 10, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:47:57', '', NULL);
INSERT INTO `sys_dept` VALUES (205, 100, '0,100', '塔城', 11, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:48:10', '', NULL);
INSERT INTO `sys_dept` VALUES (206, 100, '0,100', '阿勒泰', 12, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:48:20', '', NULL);
INSERT INTO `sys_dept` VALUES (207, 100, '0,100', '博州', 13, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:48:44', '', NULL);
INSERT INTO `sys_dept` VALUES (208, 100, '0,100', '克拉玛依', 14, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:48:57', '', NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 2014 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

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
INSERT INTO `sys_menu` VALUES (2000, '会议管理', 0, 1, 'conference', NULL, 1, 0, 'M', '0', '0', NULL, 'table', 'admin', '2021-01-18 13:34:44', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2002, '会议模板', 2000, 1, 'template', 'conference/template/index', 1, 0, 'C', '0', '0', '', 'component', 'admin', '2021-01-18 14:52:52', 'admin', '2021-01-18 14:53:02', '');
INSERT INTO `sys_menu` VALUES (2004, '新增模板', 2000, 6, 'add-template', 'conference/addTemplate/index', 1, 0, 'C', '0', '0', '', 'input', 'admin', '2021-01-18 15:27:32', 'admin', '2021-01-18 16:45:13', '');
INSERT INTO `sys_menu` VALUES (2005, '模板修改', 2000, 7, 'modify-template', 'conference/modifyTemplate/index', 1, 0, 'C', '0', '0', '', 'edit', 'admin', '2021-01-18 15:29:46', 'superAdmin', '2021-01-25 17:58:03', '');
INSERT INTO `sys_menu` VALUES (2007, '会议控制', 2000, 5, 'view-control', 'conference/viewControl/index', 1, 0, 'C', '0', '0', '', 'button', 'admin', '2021-01-18 15:51:00', 'admin', '2021-01-18 15:52:44', '');
INSERT INTO `sys_menu` VALUES (2008, '终端管理', 2009, 4, 'terminal', 'config/terminal/index', 1, 0, 'C', '0', '0', '', 'international', 'admin', '2021-01-19 10:47:57', 'superAdmin', '2021-01-26 11:10:07', '');
INSERT INTO `sys_menu` VALUES (2009, '配置管理', 0, 2, 'config', NULL, 1, 0, 'M', '0', '0', '', 'server', 'superAdmin', '2021-01-25 18:08:23', 'superAdmin', '2021-01-26 10:49:16', '');
INSERT INTO `sys_menu` VALUES (2010, '入会方案配置', 2009, 1, 'project', 'config/project/index', 1, 0, 'C', '0', '0', NULL, 'radio', 'superAdmin', '2021-01-26 11:21:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2011, '会议号配置', 2009, 2, 'conferenceNumber', 'config/conferenceNumber/index', 1, 0, 'C', '0', '0', '', 'number', 'superAdmin', '2021-01-26 19:18:08', 'superAdmin', '2021-01-26 19:18:52', '');
INSERT INTO `sys_menu` VALUES (2012, 'FME配置', 2009, 4, 'fme', 'config/fme/index', 1, 0, 'C', '0', '0', NULL, 'server', 'superAdmin', '2021-01-28 17:10:51', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2013, '租户FME分配', 2009, 5, 'fmeallot', 'config/fmeallot/index', 1, 0, 'C', '0', '0', '', 'cascader', 'superAdmin', '2021-01-28 17:13:59', 'superAdmin', '2021-01-28 17:48:00', '');

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
INSERT INTO `sys_notice` VALUES (1, '温馨提醒：2018-07-01 天堂云会控系统新版本发布啦', '2', 0xE696B0E78988E69CACE58685E5AEB9, '0', 'admin', '2021-01-18 10:39:23', '', NULL, '管理员');
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
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int(1) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`oper_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 480 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (433, '终端信息', 1, 'com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.add()', 'POST', 1, 'admin', NULL, '/fcm/busi/terminal', '127.0.0.1', '内网IP', '{\"ip\":\"213213123\",\"deptId\":100,\"params\":{},\"type\":0,\"number\":\"21312\",\"name\":\"dasdasd\"}', 'null', 1, 'IP格式不正确', '2021-01-27 20:49:26');
INSERT INTO `sys_oper_log` VALUES (434, '终端信息', 1, 'com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.add()', 'POST', 1, 'admin', NULL, '/fcm/busi/terminal', '127.0.0.1', '内网IP', '{\"ip\":\"172.16.100.127\",\"onlineStatus\":2,\"deptId\":100,\"params\":{},\"type\":0,\"number\":\"3213\",\"createTime\":1611751777798,\"name\":\"172.16.100.127\"}', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'172.16.100.127-3213\' for key \'ip\'\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiTerminalMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper.insertBusiTerminal-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into busi_terminal          ( create_time,                          dept_id,             ip,             number,                          name,             type,             online_status )           values ( ?,                          ?,             ?,             ?,                          ?,             ?,             ? )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'172.16.100.127-3213\' for key \'ip\'\n; Duplicate entry \'172.16.100.127-3213\' for key \'ip\'; nested exception is java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'172.16.100.127-3213\' for key \'ip\'', '2021-01-27 20:49:38');
INSERT INTO `sys_oper_log` VALUES (435, '终端信息', 1, 'com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.add()', 'POST', 1, 'admin', NULL, '/fcm/busi/terminal', '127.0.0.1', '内网IP', '{\"ip\":\"172.16.100.121\",\"onlineStatus\":2,\"deptId\":100,\"params\":{},\"type\":0,\"number\":\"das\",\"createTime\":1611751792636,\"name\":\"dasda\",\"id\":17}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-27 20:49:52');
INSERT INTO `sys_oper_log` VALUES (436, '终端信息', 1, 'com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.add()', 'POST', 1, 'admin', NULL, '/fcm/busi/terminal', '127.0.0.1', '内网IP', '{\"ip\":\"172.16.100.182\",\"onlineStatus\":2,\"deptId\":100,\"params\":{},\"type\":0,\"number\":\"32131\",\"createTime\":1611751808420,\"name\":\"fsfsfzda\",\"id\":18}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-27 20:50:08');
INSERT INTO `sys_oper_log` VALUES (437, '终端信息', 1, 'com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.add()', 'POST', 1, 'admin', NULL, '/fcm/busi/terminal', '127.0.0.1', '内网IP', '{\"ip\":\"172.16.100.127\",\"onlineStatus\":2,\"deptId\":100,\"params\":{},\"type\":0,\"number\":\"dsadas\",\"createTime\":1611751820502,\"name\":\"fdsd\",\"id\":19}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-27 20:50:20');
INSERT INTO `sys_oper_log` VALUES (438, '入会方案配置，控制参会者进入会议的方案', 2, 'com.paradisecloud.fcm.web.controller.fme.BusiCallLegProfileController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/callLegProfile/1b661df3-b1a1-47f0-b9e3-9af3148ffdb7', '127.0.0.1', '内网IP', '{\"deptId\":100,\"params\":{\"rxAudioMute\":true,\"qualityMain\":\"unrestricted\",\"deptId\":100,\"isDefault\":true,\"participantLabels\":false,\"qualityPresentation\":\"unrestricted\",\"sipPresentationChannelEnabled\":true,\"telepresenceCallsAllowed\":false,\"name\":\"入会静音\",\"bfcpMode\":\"serverOnly\",\"participantCounter\":\"never\",\"id\":\"1b661df3-b1a1-47f0-b9e3-9af3148ffdb7\",\"defaultLayout\":\"speakerOnly\",\"allowAllPresentationContributionAllowed\":true}} 1b661df3-b1a1-47f0-b9e3-9af3148ffdb7', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiCallLegProfileMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiCallLegProfileMapper.insertBusiCallLegProfile-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into busi_call_leg_profile          ( create_time,                          call_leg_profile_uuid,             type,             dept_id,             fme_id )           values ( ?,                          ?,             ?,             ?,             ? )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\n; Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)', '2021-01-28 11:31:04');
INSERT INTO `sys_oper_log` VALUES (439, '入会方案配置，控制参会者进入会议的方案', 2, 'com.paradisecloud.fcm.web.controller.fme.BusiCallLegProfileController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/callLegProfile/1b661df3-b1a1-47f0-b9e3-9af3148ffdb7', '127.0.0.1', '内网IP', '{\"deptId\":100,\"params\":{\"rxAudioMute\":true,\"qualityMain\":\"unrestricted\",\"deptId\":100,\"isDefault\":true,\"participantLabels\":false,\"qualityPresentation\":\"unrestricted\",\"sipPresentationChannelEnabled\":true,\"telepresenceCallsAllowed\":false,\"name\":\"入会静音\",\"bfcpMode\":\"serverOnly\",\"participantCounter\":\"never\",\"id\":\"1b661df3-b1a1-47f0-b9e3-9af3148ffdb7\",\"defaultLayout\":\"speakerOnly\",\"allowAllPresentationContributionAllowed\":true}} 1b661df3-b1a1-47f0-b9e3-9af3148ffdb7', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiCallLegProfileMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiCallLegProfileMapper.insertBusiCallLegProfile-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into busi_call_leg_profile          ( create_time,                          call_leg_profile_uuid,             type,             dept_id,             fme_id )           values ( ?,                          ?,             ?,             ?,             ? )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\n; Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)', '2021-01-28 11:31:28');
INSERT INTO `sys_oper_log` VALUES (440, '入会方案配置，控制参会者进入会议的方案', 2, 'com.paradisecloud.fcm.web.controller.fme.BusiCallLegProfileController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/callLegProfile/1b661df3-b1a1-47f0-b9e3-9af3148ffdb7', '127.0.0.1', '内网IP', '{\"deptId\":100,\"params\":{\"rxAudioMute\":true,\"qualityMain\":\"unrestricted\",\"deptId\":100,\"isDefault\":false,\"participantLabels\":true,\"qualityPresentation\":\"unrestricted\",\"sipPresentationChannelEnabled\":true,\"telepresenceCallsAllowed\":false,\"name\":\"入会静音\",\"bfcpMode\":\"serverOnly\",\"participantCounter\":\"never\",\"id\":\"1b661df3-b1a1-47f0-b9e3-9af3148ffdb7\",\"defaultLayout\":\"speakerOnly\",\"allowAllPresentationContributionAllowed\":true}} 1b661df3-b1a1-47f0-b9e3-9af3148ffdb7', '{\"code\":0,\"data\":{\"rxAudioMute\":true,\"qualityMain\":\"unrestricted\",\"deptId\":100,\"isDefault\":false,\"participantLabels\":true,\"qualityPresentation\":\"unrestricted\",\"sipPresentationChannelEnabled\":true,\"telepresenceCallsAllowed\":false,\"name\":\"入会静音\",\"bfcpMode\":\"serverOnly\",\"participantCounter\":\"never\",\"id\":\"1b661df3-b1a1-47f0-b9e3-9af3148ffdb7\",\"defaultLayout\":\"speakerOnly\",\"allowAllPresentationContributionAllowed\":true},\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 11:31:43');
INSERT INTO `sys_oper_log` VALUES (441, '入会方案配置，控制参会者进入会议的方案', 2, 'com.paradisecloud.fcm.web.controller.fme.BusiCallLegProfileController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/callLegProfile/1b661df3-b1a1-47f0-b9e3-9af3148ffdb7', '127.0.0.1', '内网IP', '{\"deptId\":100,\"params\":{\"rxAudioMute\":true,\"qualityMain\":\"unrestricted\",\"deptId\":100,\"isDefault\":true,\"participantLabels\":true,\"qualityPresentation\":\"unrestricted\",\"sipPresentationChannelEnabled\":true,\"telepresenceCallsAllowed\":false,\"name\":\"入会静音\",\"bfcpMode\":\"serverOnly\",\"participantCounter\":\"never\",\"id\":\"1b661df3-b1a1-47f0-b9e3-9af3148ffdb7\",\"defaultLayout\":\"speakerOnly\",\"allowAllPresentationContributionAllowed\":true}} 1b661df3-b1a1-47f0-b9e3-9af3148ffdb7', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiCallLegProfileMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiCallLegProfileMapper.insertBusiCallLegProfile-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into busi_call_leg_profile          ( create_time,                          call_leg_profile_uuid,             type,             dept_id,             fme_id )           values ( ?,                          ?,             ?,             ?,             ? )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\n; Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)', '2021-01-28 11:32:06');
INSERT INTO `sys_oper_log` VALUES (442, '入会方案配置，控制参会者进入会议的方案', 2, 'com.paradisecloud.fcm.web.controller.fme.BusiCallLegProfileController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/callLegProfile/1b661df3-b1a1-47f0-b9e3-9af3148ffdb7', '127.0.0.1', '内网IP', '{\"deptId\":100,\"params\":{\"rxAudioMute\":true,\"qualityMain\":\"unrestricted\",\"deptId\":100,\"isDefault\":true,\"participantLabels\":true,\"qualityPresentation\":\"unrestricted\",\"sipPresentationChannelEnabled\":true,\"telepresenceCallsAllowed\":false,\"name\":\"入会静音\",\"bfcpMode\":\"serverOnly\",\"participantCounter\":\"never\",\"id\":\"1b661df3-b1a1-47f0-b9e3-9af3148ffdb7\",\"defaultLayout\":\"speakerOnly\",\"allowAllPresentationContributionAllowed\":true}} 1b661df3-b1a1-47f0-b9e3-9af3148ffdb7', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiCallLegProfileMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiCallLegProfileMapper.insertBusiCallLegProfile-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into busi_call_leg_profile          ( create_time,                          call_leg_profile_uuid,             type,             dept_id,             fme_id )           values ( ?,                          ?,             ?,             ?,             ? )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\n; Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)', '2021-01-28 11:32:13');
INSERT INTO `sys_oper_log` VALUES (443, '入会方案配置，控制参会者进入会议的方案', 2, 'com.paradisecloud.fcm.web.controller.fme.BusiCallLegProfileController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/callLegProfile/f9061086-360e-4c65-a1fd-4a2e3bf65462', '127.0.0.1', '内网IP', '{\"deptId\":100,\"params\":{\"rxAudioMute\":true,\"isDefault\":true,\"participantLabels\":false,\"qualityPresentation\":\"max1080p30\",\"qualityMain\":\"max1080p30\",\"name\":\"testtestgg\",\"deptId\":100,\"participantCounter\":\"never\",\"id\":\"f9061086-360e-4c65-a1fd-4a2e3bf65462\",\"allowAllPresentationContributionAllowed\":true}} f9061086-360e-4c65-a1fd-4a2e3bf65462', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiCallLegProfileMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiCallLegProfileMapper.insertBusiCallLegProfile-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into busi_call_leg_profile          ( create_time,                          call_leg_profile_uuid,             type,             dept_id,             fme_id )           values ( ?,                          ?,             ?,             ?,             ? )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\n; Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)', '2021-01-28 11:32:33');
INSERT INTO `sys_oper_log` VALUES (444, '入会方案配置，控制参会者进入会议的方案', 2, 'com.paradisecloud.fcm.web.controller.fme.BusiCallLegProfileController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/callLegProfile/87ff031a-b3ff-4733-bfa5-2e119729cab3', '127.0.0.1', '内网IP', '{\"deptId\":100,\"params\":{\"rxAudioMute\":true,\"qualityMain\":\"unrestricted\",\"deptId\":100,\"isDefault\":true,\"participantLabels\":true,\"qualityPresentation\":\"unrestricted\",\"sipPresentationChannelEnabled\":true,\"telepresenceCallsAllowed\":false,\"name\":\"haha\",\"bfcpMode\":\"serverOnly\",\"participantCounter\":\"auto\",\"id\":\"87ff031a-b3ff-4733-bfa5-2e119729cab3\",\"allowAllPresentationContributionAllowed\":true}} 87ff031a-b3ff-4733-bfa5-2e119729cab3', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiCallLegProfileMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiCallLegProfileMapper.insertBusiCallLegProfile-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into busi_call_leg_profile          ( create_time,                          call_leg_profile_uuid,             type,             dept_id,             fme_id )           values ( ?,                          ?,             ?,             ?,             ? )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\n; Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_call_leg_profile`, CONSTRAINT `busi_call_leg_profile_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)', '2021-01-28 11:32:59');
INSERT INTO `sys_oper_log` VALUES (445, '会议模板', 1, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.add()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/templateConference', '127.0.0.1', '内网IP', '{\"templateConference\":{\"name\":\"测试模板\",\"deptId\":100,\"callLegProfileId\":\"238fc681-ecdf-4abf-ae9a-9fcf7f29b1d8\",\"isAutoCall\":1,\"bandwidth\":2},\"templateParticipants\":[{\"terminalId\":13,\"weight\":4},{\"terminalId\":17,\"weight\":3},{\"terminalId\":18,\"weight\":2},{\"terminalId\":19,\"weight\":1}]}', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'id\' doesn\'t have a default value\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiTemplateConferenceMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper.insertBusiTemplateConference-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into busi_template_conference          ( create_time,                          name,                          call_leg_profile_id,             bandwidth,             is_auto_call )           values ( ?,                          ?,                          ?,             ?,             ? )\r\n### Cause: java.sql.SQLException: Field \'id\' doesn\'t have a default value\n; Field \'id\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'id\' doesn\'t have a default value', '2021-01-28 11:40:09');
INSERT INTO `sys_oper_log` VALUES (446, '会议模板', 1, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.add()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/templateConference', '127.0.0.1', '内网IP', '{\"templateConference\":{\"name\":\"测试模板\",\"deptId\":100,\"callLegProfileId\":\"238fc681-ecdf-4abf-ae9a-9fcf7f29b1d8\",\"isAutoCall\":1,\"bandwidth\":2},\"templateParticipants\":[{\"terminalId\":13,\"weight\":4},{\"terminalId\":17,\"weight\":3},{\"terminalId\":18,\"weight\":2},{\"terminalId\":19,\"weight\":1}]}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 11:42:02');
INSERT INTO `sys_oper_log` VALUES (447, '入会方案配置，控制参会者进入会议的方案', 2, 'com.paradisecloud.fcm.web.controller.fme.BusiCallLegProfileController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/callLegProfile/24daa8e4-17dd-47d9-9071-9b1d53c671ad', '127.0.0.1', '内网IP', '{\"deptId\":100,\"params\":{\"rxAudioMute\":true,\"qualityMain\":\"unrestricted\",\"deptId\":100,\"isDefault\":true,\"participantLabels\":true,\"qualityPresentation\":\"unrestricted\",\"sipPresentationChannelEnabled\":true,\"telepresenceCallsAllowed\":false,\"name\":\"测试方便看\",\"bfcpMode\":\"serverOnly\",\"participantCounter\":\"never\",\"id\":\"24daa8e4-17dd-47d9-9071-9b1d53c671ad\",\"allowAllPresentationContributionAllowed\":false}} 24daa8e4-17dd-47d9-9071-9b1d53c671ad', '{\"code\":0,\"data\":{\"rxAudioMute\":true,\"qualityMain\":\"unrestricted\",\"deptId\":100,\"isDefault\":true,\"participantLabels\":true,\"qualityPresentation\":\"unrestricted\",\"sipPresentationChannelEnabled\":true,\"telepresenceCallsAllowed\":false,\"name\":\"测试方便看\",\"bfcpMode\":\"serverOnly\",\"participantCounter\":\"never\",\"id\":\"24daa8e4-17dd-47d9-9071-9b1d53c671ad\",\"allowAllPresentationContributionAllowed\":false},\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 12:27:48');
INSERT INTO `sys_oper_log` VALUES (448, '会议模板', 1, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.add()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/templateConference', '127.0.0.1', '内网IP', '{\"templateConference\":{\"name\":\"测试模板\",\"deptId\":100,\"callLegProfileId\":\"24daa8e4-17dd-47d9-9071-9b1d53c671ad\",\"isAutoCall\":1,\"bandwidth\":2},\"templateParticipants\":[{\"terminalId\":19,\"weight\":4},{\"terminalId\":13,\"weight\":3},{\"terminalId\":17,\"weight\":2},{\"terminalId\":18,\"weight\":1}]}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 12:58:31');
INSERT INTO `sys_oper_log` VALUES (449, '会议模板', 1, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.add()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/templateConference', '127.0.0.1', '内网IP', '{\"templateConference\":{\"name\":\"dasdasda\",\"deptId\":100,\"callLegProfileId\":\"24daa8e4-17dd-47d9-9071-9b1d53c671ad\",\"isAutoCall\":1,\"bandwidth\":2},\"templateParticipants\":[]}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 13:01:41');
INSERT INTO `sys_oper_log` VALUES (450, '终端信息', 1, 'com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.add()', 'POST', 1, 'admin', NULL, '/fcm/busi/terminal', '127.0.0.1', '内网IP', '{\"ip\":\"172.16.100.147\",\"onlineStatus\":2,\"deptId\":100,\"params\":{},\"type\":0,\"number\":\"3112\",\"createTime\":1611810493254,\"name\":\"在线\",\"id\":20}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 13:08:13');
INSERT INTO `sys_oper_log` VALUES (451, '终端信息', 3, 'com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.remove()', 'DELETE', 1, 'admin', NULL, '/fcm/busi/terminal/20', '127.0.0.1', '内网IP', '{ids=20}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 13:08:35');
INSERT INTO `sys_oper_log` VALUES (452, '终端信息', 2, 'com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.edit()', 'PUT', 1, 'admin', NULL, '/fcm/busi/terminal/15', '127.0.0.1', '内网IP', '{\"ip\":\"172.16.100.147\",\"onlineStatus\":1,\"deptId\":100,\"updateTime\":1611810523935,\"params\":{},\"type\":100,\"name\":\"172.16.100.147\",\"id\":15} 15', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 13:08:43');
INSERT INTO `sys_oper_log` VALUES (453, '会议模板', 1, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.add()', 'POST', 1, 'admin', NULL, '/fcm/busi/templateConference', '127.0.0.1', '内网IP', '{\"templateConference\":{\"name\":\"模板二\",\"deptId\":100,\"callLegProfileId\":\"24daa8e4-17dd-47d9-9071-9b1d53c671ad\",\"isAutoCall\":1,\"bandwidth\":2},\"templateParticipants\":[{\"terminalId\":13,\"weight\":5},{\"terminalId\":15,\"weight\":4},{\"terminalId\":17,\"weight\":3},{\"terminalId\":18,\"weight\":2},{\"terminalId\":19,\"weight\":1}]}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 13:09:08');
INSERT INTO `sys_oper_log` VALUES (454, '会议模板', 3, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.remove()', 'DELETE', 1, 'admin', NULL, '/fcm/busi/templateConference/2', '127.0.0.1', '内网IP', '{ids=2}', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot delete or update a parent row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_3` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UP)\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiTemplateConferenceMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper.deleteBusiTemplateConferenceByIds-Inline\r\n### The error occurred while setting parameters\r\n### SQL: delete from busi_template_conference where id in           (               ?          )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot delete or update a parent row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_3` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UP)\n; Cannot delete or update a parent row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_3` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UP); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot delete or update a parent row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_3` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UP)', '2021-01-28 13:44:53');
INSERT INTO `sys_oper_log` VALUES (455, '代码生成', 6, 'com.paradisecloud.generator.controller.GenController.importTableSave()', 'POST', 1, 'superAdmin', NULL, '/fcm/tool/gen/importTable', '127.0.0.1', '内网IP', 'busi_fme_group_dept', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 14:28:00');
INSERT INTO `sys_oper_log` VALUES (456, '代码生成', 2, 'com.paradisecloud.generator.controller.GenController.editSave()', 'PUT', 1, 'superAdmin', NULL, '/fcm/tool/gen', '127.0.0.1', '内网IP', '{\"sub\":false,\"functionAuthor\":\"lilinhai\",\"columns\":[{\"capJavaField\":\"Id\",\"usableColumn\":false,\"columnId\":1450,\"isIncrement\":\"0\",\"increment\":false,\"insert\":true,\"dictType\":\"\",\"required\":false,\"superColumn\":false,\"updateBy\":\"\",\"isInsert\":\"1\",\"javaField\":\"id\",\"htmlType\":\"input\",\"edit\":false,\"query\":false,\"sort\":1,\"list\":false,\"params\":{},\"javaType\":\"Long\",\"queryType\":\"EQ\",\"columnType\":\"bigint(20)\",\"createBy\":\"superAdmin\",\"isPk\":\"0\",\"createTime\":1611815280000,\"tableId\":185,\"pk\":false,\"columnName\":\"id\"},{\"capJavaField\":\"CreateTime\",\"usableColumn\":false,\"columnId\":1451,\"isIncrement\":\"0\",\"increment\":false,\"insert\":true,\"dictType\":\"\",\"required\":false,\"superColumn\":true,\"updateBy\":\"\",\"isInsert\":\"1\",\"javaField\":\"createTime\",\"htmlType\":\"datetime\",\"edit\":false,\"query\":false,\"sort\":2,\"list\":false,\"params\":{},\"javaType\":\"Date\",\"queryType\":\"EQ\",\"columnType\":\"datetime\",\"createBy\":\"superAdmin\",\"isPk\":\"0\",\"createTime\":1611815280000,\"tableId\":185,\"pk\":false,\"columnName\":\"create_time\"},{\"capJavaField\":\"UpdateTime\",\"usableColumn\":false,\"columnId\":1452,\"isIncrement\":\"0\",\"increment\":false,\"insert\":true,\"dictType\":\"\",\"required\":false,\"superColumn\":true,\"updateBy\":\"\",\"isInsert\":\"1\",\"javaField\":\"updateTime\",\"htmlType\":\"datetime\",\"edit\":true,\"query\":false,\"sort\":3,\"list\":false,\"params\":{},\"javaType\":\"Date\",\"queryType\":\"EQ\",\"columnType\":\"datetime\",\"createBy\":\"superAdmin\",\"isPk\":\"0\",\"createTime\":1611815280000,\"isEdit\":\"1\",\"tableId\":185,\"pk\":false,\"columnName\":\"update_time\"},{\"capJavaField\":\"FmeGroupId\",\"usableColumn\":false,\"columnId\":1453,\"isIncrement\":\"0\",\"increment\":false,\"insert\":true,\"isList\":\"1\",\"dictType\":\"\",\"required\":false,\"superColumn\":false,\"updateBy\":\"\",\"isInsert\":\"1\",\"javaField\":\"fmeGroupId\",\"htmlType\":\"input\",\"edit\":true,\"query\":true,\"columnComment\":\"fme组ID\",\"isQuery\":\"1\",\"sort\":4,\"list\":true,\"params\":{},\"javaType\":\"Long\",\"queryType\":\"EQ\",\"columnType\":\"bigint(20)\",\"createBy\":\"superAdmin\",\"isPk\":\"0\",\"createTime\":1611815280000,\"isEdit\":\"1\",\"tableId\":185,\"pk\":false,\"columnName\":\"fme_g', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 14:28:27');
INSERT INTO `sys_oper_log` VALUES (457, '代码生成', 8, 'com.paradisecloud.generator.controller.GenController.batchGenCode()', 'GET', 1, 'superAdmin', NULL, '/fcm/tool/gen/batchGenCode', '127.0.0.1', '内网IP', '{}', 'null', 0, NULL, '2021-01-28 14:28:34');
INSERT INTO `sys_oper_log` VALUES (458, '代码生成', 2, 'com.paradisecloud.generator.controller.GenController.synchDb()', 'GET', 1, 'superAdmin', NULL, '/fcm/tool/gen/synchDb/busi_fme_group_dept', '127.0.0.1', '内网IP', '{tableName=busi_fme_group_dept}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 14:42:21');
INSERT INTO `sys_oper_log` VALUES (459, '代码生成', 3, 'com.paradisecloud.generator.controller.GenController.remove()', 'DELETE', 1, 'superAdmin', NULL, '/fcm/tool/gen/185', '127.0.0.1', '内网IP', '{tableIds=185}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 14:42:32');
INSERT INTO `sys_oper_log` VALUES (460, '代码生成', 6, 'com.paradisecloud.generator.controller.GenController.importTableSave()', 'POST', 1, 'superAdmin', NULL, '/fcm/tool/gen/importTable', '127.0.0.1', '内网IP', 'busi_fme_group_dept', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 14:42:35');
INSERT INTO `sys_oper_log` VALUES (461, '代码生成', 2, 'com.paradisecloud.generator.controller.GenController.editSave()', 'PUT', 1, 'superAdmin', NULL, '/fcm/tool/gen', '127.0.0.1', '内网IP', '{\"sub\":false,\"functionAuthor\":\"lilinhai\",\"columns\":[{\"capJavaField\":\"Id\",\"usableColumn\":false,\"columnId\":1455,\"isIncrement\":\"1\",\"increment\":true,\"insert\":true,\"dictType\":\"\",\"required\":false,\"superColumn\":false,\"updateBy\":\"\",\"isInsert\":\"1\",\"javaField\":\"id\",\"htmlType\":\"input\",\"edit\":false,\"query\":false,\"columnComment\":\"主键ID\",\"sort\":1,\"list\":false,\"params\":{},\"javaType\":\"Long\",\"queryType\":\"EQ\",\"columnType\":\"bigint(20)\",\"createBy\":\"superAdmin\",\"isPk\":\"1\",\"createTime\":1611816155000,\"tableId\":186,\"pk\":true,\"columnName\":\"id\"},{\"capJavaField\":\"CreateTime\",\"usableColumn\":false,\"columnId\":1456,\"isIncrement\":\"0\",\"increment\":false,\"insert\":true,\"dictType\":\"\",\"required\":false,\"superColumn\":true,\"updateBy\":\"\",\"isInsert\":\"1\",\"javaField\":\"createTime\",\"htmlType\":\"datetime\",\"edit\":false,\"query\":false,\"columnComment\":\"创建时间\",\"sort\":2,\"list\":false,\"params\":{},\"javaType\":\"Date\",\"queryType\":\"EQ\",\"columnType\":\"datetime\",\"createBy\":\"superAdmin\",\"isPk\":\"0\",\"createTime\":1611816155000,\"tableId\":186,\"pk\":false,\"columnName\":\"create_time\"},{\"capJavaField\":\"UpdateTime\",\"usableColumn\":false,\"columnId\":1457,\"isIncrement\":\"0\",\"increment\":false,\"insert\":true,\"dictType\":\"\",\"required\":false,\"superColumn\":true,\"updateBy\":\"\",\"isInsert\":\"1\",\"javaField\":\"updateTime\",\"htmlType\":\"datetime\",\"edit\":true,\"query\":false,\"columnComment\":\"修改时间\",\"sort\":3,\"list\":false,\"params\":{},\"javaType\":\"Date\",\"queryType\":\"EQ\",\"columnType\":\"datetime\",\"createBy\":\"superAdmin\",\"isPk\":\"0\",\"createTime\":1611816155000,\"isEdit\":\"1\",\"tableId\":186,\"pk\":false,\"columnName\":\"update_time\"},{\"capJavaField\":\"FmeGroupId\",\"usableColumn\":false,\"columnId\":1458,\"isIncrement\":\"0\",\"increment\":false,\"insert\":true,\"isList\":\"1\",\"dictType\":\"\",\"required\":false,\"superColumn\":false,\"updateBy\":\"\",\"isInsert\":\"1\",\"javaField\":\"fmeGroupId\",\"htmlType\":\"input\",\"edit\":true,\"query\":true,\"columnComment\":\"fme组ID\",\"isQuery\":\"1\",\"sort\":4,\"list\":true,\"params\":{},\"javaType\":\"Long\",\"queryType\":\"EQ\",\"columnType\":\"bigint(20)\",\"createBy\":\"superAdmin\",\"isPk\":\"0\",\"createTime\":1611', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 14:42:54');
INSERT INTO `sys_oper_log` VALUES (462, '会议模板', 1, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.add()', 'POST', 1, 'admin', NULL, '/fcm/busi/templateConference', '127.0.0.1', '内网IP', '{\"templateConference\":{\"name\":\"hahahahah\",\"deptId\":100,\"callLegProfileId\":\"6fc4d99d-c64d-48d9-9fc8-699e7d117007\",\"isAutoCall\":2,\"bandwidth\":4},\"templateParticipants\":[{\"terminalId\":15,\"weight\":5},{\"terminalId\":13,\"weight\":4},{\"terminalId\":17,\"weight\":3},{\"terminalId\":18,\"weight\":2},{\"terminalId\":19,\"weight\":1}]}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 16:26:51');
INSERT INTO `sys_oper_log` VALUES (463, '会议模板', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.edit()', 'PUT', 1, 'admin', NULL, '/fcm/busi/templateConference/5', '127.0.0.1', '内网IP', '{\"templateConference\":{\"createTime\":\"2021-01-28 16:26:51\",\"params\":{},\"id\":5,\"name\":\"hahahahah\",\"deptId\":100,\"callLegProfileId\":\"6fc4d99d-c64d-48d9-9fc8-699e7d117007\",\"bandwidth\":2,\"isAutoCall\":2},\"templateParticipants\":[{\"terminalId\":18,\"weight\":1},{\"terminalId\":17,\"weight\":2},{\"terminalId\":16,\"weight\":3},{\"terminalId\":15,\"weight\":4},{\"terminalId\":14,\"weight\":5}]} 5', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiTemplateParticipantMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiTemplateParticipantMapper.insertBusiTemplateParticipant-Inline\r\n### The error occurred while setting parameters\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\n; Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)', '2021-01-28 16:58:48');
INSERT INTO `sys_oper_log` VALUES (464, '会议模板', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.edit()', 'PUT', 1, 'admin', NULL, '/fcm/busi/templateConference/5', '127.0.0.1', '内网IP', '{\"templateConference\":{\"createTime\":\"2021-01-28 16:26:51\",\"params\":{},\"id\":5,\"name\":\"hahahahah\",\"deptId\":100,\"callLegProfileId\":\"6fc4d99d-c64d-48d9-9fc8-699e7d117007\",\"bandwidth\":2,\"isAutoCall\":2},\"templateParticipants\":[{\"terminalId\":18,\"weight\":1},{\"terminalId\":17,\"weight\":2},{\"terminalId\":16,\"weight\":3},{\"terminalId\":15,\"weight\":4},{\"terminalId\":14,\"weight\":5}]} 5', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiTemplateParticipantMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiTemplateParticipantMapper.insertBusiTemplateParticipant-Inline\r\n### The error occurred while setting parameters\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\n; Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)', '2021-01-28 16:59:08');
INSERT INTO `sys_oper_log` VALUES (465, '会议模板', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.edit()', 'PUT', 1, 'admin', NULL, '/fcm/busi/templateConference/5', '127.0.0.1', '内网IP', '{\"templateConference\":{\"createTime\":\"2021-01-28 16:26:51\",\"params\":{},\"id\":5,\"name\":\"hahahahah\",\"deptId\":100,\"callLegProfileId\":\"6fc4d99d-c64d-48d9-9fc8-699e7d117007\",\"bandwidth\":2,\"isAutoCall\":2},\"templateParticipants\":[{\"terminalId\":18,\"weight\":1},{\"terminalId\":17,\"weight\":2},{\"terminalId\":16,\"weight\":3},{\"terminalId\":15,\"weight\":4},{\"terminalId\":14,\"weight\":5}]} 5', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiTemplateParticipantMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiTemplateParticipantMapper.insertBusiTemplateParticipant-Inline\r\n### The error occurred while setting parameters\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\n; Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)', '2021-01-28 17:01:00');
INSERT INTO `sys_oper_log` VALUES (466, '会议模板', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.edit()', 'PUT', 1, 'admin', NULL, '/fcm/busi/templateConference/5', '127.0.0.1', '内网IP', '{\"templateConference\":{\"createTime\":\"2021-01-28 16:26:51\",\"params\":{},\"id\":5,\"name\":\"hahahahah\",\"deptId\":100,\"callLegProfileId\":\"6fc4d99d-c64d-48d9-9fc8-699e7d117007\",\"bandwidth\":4,\"isAutoCall\":2},\"templateParticipants\":[{\"terminalId\":15,\"weight\":5},{\"terminalId\":13,\"weight\":4},{\"terminalId\":17,\"weight\":3},{\"terminalId\":18,\"weight\":2},{\"terminalId\":19,\"weight\":1}]} 5', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 17:03:19');
INSERT INTO `sys_oper_log` VALUES (467, '会议模板', 3, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.remove()', 'DELETE', 1, 'admin', NULL, '/fcm/busi/templateConference/5', '127.0.0.1', '内网IP', '{id=5}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 17:03:47');
INSERT INTO `sys_oper_log` VALUES (468, '会议模板', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.edit()', 'PUT', 1, 'admin', NULL, '/fcm/busi/templateConference/4', '127.0.0.1', '内网IP', '{\"templateConference\":{\"createTime\":\"2021-01-28 13:09:08\",\"params\":{},\"id\":4,\"name\":\"模板二\",\"deptId\":100,\"callLegProfileId\":\"24daa8e4-17dd-47d9-9071-9b1d53c671ad\",\"bandwidth\":4,\"isAutoCall\":1},\"templateParticipants\":[{\"terminalId\":13,\"weight\":4},{\"terminalId\":12,\"weight\":3},{\"terminalId\":11,\"weight\":2},{\"terminalId\":10,\"weight\":1}]} 4', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiTemplateParticipantMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiTemplateParticipantMapper.insertBusiTemplateParticipant-Inline\r\n### The error occurred while setting parameters\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\n; Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)', '2021-01-28 17:04:03');
INSERT INTO `sys_oper_log` VALUES (469, '会议模板', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.edit()', 'PUT', 1, 'admin', NULL, '/fcm/busi/templateConference/4', '127.0.0.1', '内网IP', '{\"templateConference\":{\"createTime\":\"2021-01-28 13:09:08\",\"params\":{},\"id\":4,\"name\":\"模板二\",\"deptId\":100,\"callLegProfileId\":\"24daa8e4-17dd-47d9-9071-9b1d53c671ad\",\"bandwidth\":4,\"isAutoCall\":1},\"templateParticipants\":[{\"terminalId\":13,\"weight\":4},{\"terminalId\":12,\"weight\":3},{\"terminalId\":11,\"weight\":2},{\"terminalId\":10,\"weight\":1}]} 4', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiTemplateParticipantMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiTemplateParticipantMapper.insertBusiTemplateParticipant-Inline\r\n### The error occurred while setting parameters\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\n; Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)', '2021-01-28 17:04:46');
INSERT INTO `sys_oper_log` VALUES (470, '会议模板', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.edit()', 'PUT', 1, 'admin', NULL, '/fcm/busi/templateConference/4', '127.0.0.1', '内网IP', '{\"templateConference\":{\"createTime\":\"2021-01-28 13:09:08\",\"params\":{},\"id\":4,\"name\":\"模板二\",\"deptId\":100,\"callLegProfileId\":\"24daa8e4-17dd-47d9-9071-9b1d53c671ad\",\"bandwidth\":2,\"isAutoCall\":1},\"templateParticipants\":[{\"terminalId\":9,\"weight\":4},{\"terminalId\":10,\"weight\":3},{\"terminalId\":11,\"weight\":2},{\"terminalId\":12,\"weight\":1}]} 4', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiTemplateParticipantMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiTemplateParticipantMapper.insertBusiTemplateParticipant-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into busi_template_participant          ( template_conference_id,             terminal_id,             weight )           values ( ?,             ?,             ? )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\n; Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)', '2021-01-28 17:05:05');
INSERT INTO `sys_oper_log` VALUES (471, '菜单管理', 1, 'com.paradisecloud.web.controller.system.SysMenuController.add()', 'POST', 1, 'superAdmin', NULL, '/fcm/system/menu', '127.0.0.1', '内网IP', '{\"visible\":\"0\",\"icon\":\"server\",\"orderNum\":\"4\",\"menuName\":\"FME配置\",\"params\":{},\"parentId\":2009,\"isCache\":\"0\",\"path\":\"fme\",\"component\":\"config/fme/index\",\"createBy\":\"superAdmin\",\"children\":[],\"isFrame\":\"1\",\"menuType\":\"C\",\"status\":\"0\"}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 17:10:51');
INSERT INTO `sys_oper_log` VALUES (472, '菜单管理', 1, 'com.paradisecloud.web.controller.system.SysMenuController.add()', 'POST', 1, 'superAdmin', NULL, '/fcm/system/menu', '127.0.0.1', '内网IP', '{\"visible\":\"0\",\"icon\":\"cascader\",\"orderNum\":\"5\",\"menuName\":\"租户FME分配\",\"params\":{},\"parentId\":2009,\"isCache\":\"0\",\"path\":\"fmeallot\",\"component\":\"/config/fmeallot/index\",\"createBy\":\"superAdmin\",\"children\":[],\"isFrame\":\"1\",\"menuType\":\"C\",\"status\":\"0\"}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 17:13:59');
INSERT INTO `sys_oper_log` VALUES (473, '终端信息', 3, 'com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.remove()', 'DELETE', 1, 'superAdmin', NULL, '/fcm/busi/terminal/19', '127.0.0.1', '内网IP', '{ids=19}', 'null', 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot delete or update a parent row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\r\n### The error may exist in URL [jar:file:/D:/dev-env/workspace_join/ruoyi_back_app/paradisecloud-admin/lib/fcm-application.jar!/BOOT-INF/lib/fcm-dao-2.0-RELEASE.jar!/mapper/BusiTerminalMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper.deleteBusiTerminalById-Inline\r\n### The error occurred while setting parameters\r\n### SQL: delete from busi_terminal where id = ?\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot delete or update a parent row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)\n; Cannot delete or update a parent row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION); nested exception is java.sql.SQLIntegrityConstraintViolationException: Cannot delete or update a parent row: a foreign key constraint fails (`fcmdb`.`busi_template_participant`, CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION)', '2021-01-28 17:32:01');
INSERT INTO `sys_oper_log` VALUES (474, '终端信息', 1, 'com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.add()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/terminal', '127.0.0.1', '内网IP', '{\"ip\":\"172.16.100.123\",\"onlineStatus\":2,\"deptId\":100,\"params\":{},\"type\":0,\"number\":\"432423\",\"createTime\":1611826340834,\"name\":\"ewrewrwer\",\"id\":21}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 17:32:20');
INSERT INTO `sys_oper_log` VALUES (475, '终端信息', 3, 'com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.remove()', 'DELETE', 1, 'superAdmin', NULL, '/fcm/busi/terminal/21', '127.0.0.1', '内网IP', '{ids=21}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 17:32:24');
INSERT INTO `sys_oper_log` VALUES (476, '终端信息', 2, 'com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/terminal/19', '127.0.0.1', '内网IP', '{\"ip\":\"172.16.100.127\",\"onlineStatus\":2,\"deptId\":100,\"updateTime\":1611826354648,\"params\":{},\"type\":0,\"number\":\"dsadas\",\"name\":\"fdsd4444\",\"id\":19} 19', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 17:32:34');
INSERT INTO `sys_oper_log` VALUES (477, '终端信息', 1, 'com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.add()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/terminal', '127.0.0.1', '内网IP', '{\"ip\":\"172.16.100.126\",\"onlineStatus\":2,\"deptId\":100,\"params\":{},\"type\":3,\"number\":\"43243\",\"createTime\":1611826379897,\"name\":\"11111111\",\"id\":22}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 17:32:59');
INSERT INTO `sys_oper_log` VALUES (478, '终端信息', 3, 'com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.remove()', 'DELETE', 1, 'superAdmin', NULL, '/fcm/busi/terminal/22', '127.0.0.1', '内网IP', '{ids=22}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 17:33:04');
INSERT INTO `sys_oper_log` VALUES (479, '菜单管理', 2, 'com.paradisecloud.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/system/menu', '127.0.0.1', '内网IP', '{\"visible\":\"0\",\"icon\":\"cascader\",\"orderNum\":\"5\",\"menuName\":\"租户FME分配\",\"params\":{},\"parentId\":2009,\"isCache\":\"0\",\"path\":\"fmeallot\",\"component\":\"config/fmeallot/index\",\"children\":[],\"createTime\":1611825239000,\"updateBy\":\"superAdmin\",\"isFrame\":\"1\",\"menuId\":2013,\"menuType\":\"C\",\"perms\":\"\",\"status\":\"0\"}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-01-28 17:48:00');

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
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2021-01-18 10:39:19', '', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '新疆省级用户', 'province', 2, '4', 1, 1, '0', '0', 'admin', '2021-01-18 10:39:19', 'superAdmin', '2021-01-26 19:20:26', '普通角色');
INSERT INTO `sys_role` VALUES (3, '新疆地级用户', 'city', 3, '4', 1, 1, '0', '0', 'admin', '2021-01-25 18:17:43', 'superAdmin', '2021-01-26 19:20:32', NULL);

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
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
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 102 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, NULL, 'superAdmin', '超级管理员', '00', 'ry@163.com', '15888888888', '1', '/profile/avatar/2021/01/19/35ea7b84-19cf-4e18-8d98-3e396a080a4f.jpeg', '$2a$10$d1wmaSUmRR8bs.Djrx5Bouyb4bE9i/0tn5nfAdfjiMd41IZcqFSp2', '0', '0', '127.0.0.1', '2021-01-18 10:39:18', 'admin', '2021-01-18 10:39:18', '', NULL, '管理员');
INSERT INTO `sys_user` VALUES (100, 100, 'admin', '新疆自治区管理员', '00', '', '', '0', '', '$2a$10$d1wmaSUmRR8bs.Djrx5Bouyb4bE9i/0tn5nfAdfjiMd41IZcqFSp2', '0', '0', '', NULL, 'superAdmin', '2021-01-19 11:38:00', 'superAdmin', '2021-01-25 18:48:17', NULL);
INSERT INTO `sys_user` VALUES (101, 206, 'altAdmin', '阿勒泰管理员', '00', '', '13666667777', '0', '', '$2a$10$5qIPs0Lck8Hsk514f2IMkOpA/dOjOahjN5lO8NY8rHN1mfPLT.s2u', '0', '0', '', NULL, 'admin', '2021-01-25 18:18:41', 'admin', '2021-01-25 18:19:51', NULL);

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
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (100, 2);
INSERT INTO `sys_user_role` VALUES (101, 3);

SET FOREIGN_KEY_CHECKS = 1;
