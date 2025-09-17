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

 Date: 18/07/2023 09:09:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for busi_smc2
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc2`;
CREATE TABLE `busi_smc2`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '终端创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '终端修改时间',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统管理员用户',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统管理员密码',
  `meeting_username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议管理员用户名',
  `meeting_password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议管理员密码',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'smc显示名字',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备的IP地址',
  `sc_ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SC设备的IP地址',
  `status` int(11) NULL DEFAULT NULL COMMENT 'smc在线状态：1在线，2离线，3删除',
  `capacity` int(11) NULL DEFAULT NULL COMMENT 'FME容量',
  `spare_smc_id` bigint(20) NULL DEFAULT NULL COMMENT '备用SMC（本节点宕机后指向的备用节点）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'smc2信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_smc2_appointment_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc2_appointment_conference`;
CREATE TABLE `busi_smc2_appointment_conference`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_id` int(11) NOT NULL COMMENT '部门id',
  `chairman_password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `conference_time_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议类型',
  `duration` int(10) NULL DEFAULT NULL COMMENT '时长',
  `guest_password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `schedule_start_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'utc时间',
  `subject` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主题',
  `vmr_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '虚拟号码',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '视频会议，语音会议',
  `max_participant_num` int(10) NULL DEFAULT NULL COMMENT '最大入会数量',
  `voice_active` int(1) NULL DEFAULT NULL COMMENT '静音入会',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `rate` int(10) NULL DEFAULT NULL COMMENT '带宽',
  `period_conference_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `duration_per_period_unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `period_unit_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `start_date` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `end_date` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `week_index_in_month_mode` int(10) NULL DEFAULT NULL,
  `conference_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议ID',
  `video_resolution` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '视频分辨率（MPI_1080P）',
  `svc_video_resolution` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'svc视频分辨率',
  `auto_mute` int(1) UNSIGNED NULL DEFAULT NULL COMMENT '自动静音',
  `support_live` int(1) NULL DEFAULT NULL COMMENT '直播',
  `support_record` int(1) NULL DEFAULT NULL COMMENT '录播',
  `amc_record` int(1) NULL DEFAULT NULL COMMENT '数据会议',
  `access_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议数字ID',
  `create_hy_admin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_user_id` int(11) NULL DEFAULT NULL COMMENT '本地创建者',
  `account_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议账号',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议用户',
  `token` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议token',
  `stage` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议状态',
  `guest_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '嘉宾链接',
  `chairman_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主席链接',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类',
  `active` int(1) NULL DEFAULT NULL COMMENT '会议是否活动',
  `legacy_id` int(10) NULL DEFAULT NULL,
  `organization_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组织名称',
  `master_terminal_id` int(10) NULL DEFAULT NULL COMMENT '主会场',
  `enable_data_conf` int(1) NULL DEFAULT NULL COMMENT '数据会议',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议密码',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `smc2_template_id` int(10) NULL DEFAULT NULL COMMENT '本地模板ID',
  `is_hang_up` int(11) NULL DEFAULT NULL COMMENT '是否主动挂断',
  `status` int(11) NULL DEFAULT NULL COMMENT '预约会议的状态：1启动，2停止',
  `is_start` int(11) NULL DEFAULT NULL COMMENT '会议是否开始',
  `start_failed_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '启动失败原因记录',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `conferenceId`(`conference_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 162 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'smc2预约会议' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for busi_smc2_appointment_conference_paticipant
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc2_appointment_conference_paticipant`;
CREATE TABLE `busi_smc2_appointment_conference_paticipant`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `conference_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议ID',
  `appointment_id` int(10) NULL DEFAULT NULL COMMENT '预约ID',
  `terminal_id` int(10) NULL DEFAULT NULL COMMENT '终端id',
  `weight` int(10) NULL DEFAULT NULL COMMENT '排序',
  `smcnumber` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'uri',
  `terminal_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '终端名字',
  `terminal_dept_id` int(10) NULL DEFAULT NULL COMMENT '终端部门id',
  `attend_type` int(10) NULL DEFAULT NULL COMMENT '入会类型',
  `participant_id` int(10) NULL DEFAULT NULL COMMENT '与会者ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'smc2预约会议成员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_smc2_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc2_dept`;
CREATE TABLE `busi_smc2_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '分配给的租户',
  `smc_type` int(11) NULL DEFAULT NULL COMMENT '1单节点，100集群',
  `smc_id` bigint(20) NULL DEFAULT NULL COMMENT '当smc_type为1是，指向busi_smc的id字段，为100指向busi_smc_cluster的id字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'smc2部门绑定' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_smc2_dept_template
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc2_dept_template`;
CREATE TABLE `busi_smc2_dept_template`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_id` int(11) NOT NULL COMMENT '部门id',
  `smc_template_id` varchar(70) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `template_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `duration` int(10) NOT NULL COMMENT '会议时长分钟数',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议类型',
  `rate` int(10) NULL DEFAULT NULL COMMENT '带宽',
  `support_live` tinyint(1) NULL DEFAULT NULL COMMENT '直播',
  `support_record` tinyint(1) NULL DEFAULT NULL COMMENT '录制',
  `amc_record` tinyint(1) NULL DEFAULT NULL COMMENT '数据会议',
  `auto_mute` tinyint(1) NULL DEFAULT NULL COMMENT '自动静音',
  `chairman_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主席密码',
  `guest_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '嘉宾密码',
  `vmr_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '虚拟号码',
  `max_participant_num` int(10) NULL DEFAULT NULL COMMENT '最大入会数',
  `master_terminal_id` int(10) NULL DEFAULT NULL COMMENT '主会场ID',
  `enable_data_conf` int(1) NULL DEFAULT NULL COMMENT '数据会议',
  `smc2_template_id` int(11) NULL DEFAULT NULL COMMENT '模板id',
  `access_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议激活号码,Ad Hoc模板时必须填写',
  `bill_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '计费码',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议密码',
  `creat_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `confid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'smc2会议ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 327 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'smc2部门模板关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_smc2_history_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc2_history_conference`;
CREATE TABLE `busi_smc2_history_conference`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `conference_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会议id',
  `dept_id` int(10) NULL DEFAULT NULL COMMENT '部门ID',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `subject` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主题',
  `conference_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议数字ID',
  `end_status` int(255) NULL DEFAULT NULL COMMENT '是否结束1结束2否',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '会议结束时间',
  `template_id` int(10) NULL DEFAULT NULL COMMENT '模板ID',
  `conference_avc_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '音视频会议类型',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '会议开始时间',
  `duration` int(10) NULL DEFAULT NULL COMMENT '会议时长',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `conferenceIdIndex`(`conference_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 300 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'smc2会议历史表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for busi_smc2_template_terminal
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc2_template_terminal`;
CREATE TABLE `busi_smc2_template_terminal`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `smc2_template_id` int(11) NOT NULL COMMENT '模板id',
  `terminal_id` int(11) NOT NULL COMMENT '终端id',
  `terminal_dept_id` int(11) NOT NULL COMMENT '终端部门id',
  `smcnumber` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'accessCode',
  `weight` int(10) NULL DEFAULT NULL COMMENT '排序',
  `participant_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NULL DEFAULT NULL COMMENT '与会者ID',
  `attend_type` int(1) NULL DEFAULT NULL COMMENT '参会类型：1被叫，2手动主叫，3自动主叫，10直播',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1635 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci COMMENT = 'smc2模板终端关系表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
