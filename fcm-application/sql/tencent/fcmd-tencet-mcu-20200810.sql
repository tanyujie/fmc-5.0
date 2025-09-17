/*
 Navicat Premium Data Transfer

 Source Server         : LLLCOCAL
 Source Server Type    : MySQL
 Source Server Version : 50728
 Source Host           : localhost:3306
 Source Schema         : fcmdb

 Target Server Type    : MySQL
 Target Server Version : 50728
 File Encoding         : 65001

 Date: 10/08/2023 14:22:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for busi_tencent
-- ----------------------------
DROP TABLE IF EXISTS `busi_tencent`;
CREATE TABLE `busi_tencent`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `app_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '企业id',
  `sdk_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用ID',
  `secret_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '安全凭证密钥ID',
  `secret_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '安全凭证密钥KEY',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'tencent显示名字',
  `status` int(11) NULL DEFAULT NULL COMMENT 'tencent在线状态：1在线，2离线，3删除',
  `capacity` int(11) NULL DEFAULT NULL COMMENT 'tencent会议容量',
  `spare_smc_id` bigint(20) NULL DEFAULT NULL COMMENT '备用（本节点宕机后指向的备用节点）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'smc2信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_tencent_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_tencent_dept`;
CREATE TABLE `busi_tencent_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '分配给的租户',
  `smc_type` int(11) NULL DEFAULT NULL COMMENT '1单节点，100集群',
  `smc_id` bigint(20) NULL DEFAULT NULL COMMENT '当smc_type为1是，指向busi_smc的id字段，为100指向busi_smc_cluster的id字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'smc2部门绑定' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_tencent_conference_appointment
-- ----------------------------
DROP TABLE IF EXISTS `busi_tencent_conference_appointment`;
CREATE TABLE `busi_tencent_conference_appointment`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '归属租户',
  `template_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的会议模板ID',
  `is_auto_create_template` int(11) NULL DEFAULT NULL COMMENT '是否自动创建模板',
  `start_date` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议开始时间',
  `end_date` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议结束时间',
  `extend_minutes` int(11) NULL DEFAULT NULL COMMENT '延长分钟数',
  `is_hang_up` int(11) NULL DEFAULT NULL COMMENT '是否主动挂断',
  `status` int(11) NULL DEFAULT NULL COMMENT '预约会议的状态：1启动，2停止',
  `is_start` int(11) NULL DEFAULT NULL COMMENT '会议是否开始',
  `start_failed_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '启动失败原因记录',
  `repeat_rate` int(11) NULL DEFAULT NULL COMMENT '1自定义，2每天，3每周，4每月',
  `repeat_date` int(11) NULL DEFAULT NULL COMMENT 'repeat_rate为3，范围为[1-7]，repeat_rate为4范围是1-30',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '入会密码',
  `attendee_limit` int(11) NULL DEFAULT NULL COMMENT '参会者上限',
  `type` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '会议类型:1:预约会议;2:即时会议',
  `recurring_rule` json NULL COMMENT '周期性会议规则',
  `subject` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主题',
  `duration` int(10) NULL DEFAULT NULL COMMENT '时长',
  `tencent_type` int(1) NULL DEFAULT NULL COMMENT '会议类型:1:预约会议;2:即时会议',
  `access_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议数字ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  INDEX `template_id`(`template_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `is_auto_create_template`(`is_auto_create_template`) USING BTREE,
  CONSTRAINT `busi_tencent_conference_appointment_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议预约记录' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for busi_tencent_history_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_tencent_history_conference`;
CREATE TABLE `busi_tencent_history_conference`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 547 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'tencent会议历史表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Table structure for busi_tencent_template_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_tencent_template_conference`;
CREATE TABLE `busi_tencent_template_conference`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者id',
  `create_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者用户名',
  `subject` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板会议名',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `call_leg_profile_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '入会方案配置ID（关联MCU里面的入会方案记录ID，会控端不存）',
  `call_profile_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '虚拟会议室参数',
  `call_branding_profile_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '呼入标识参数',
  `stream_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '直播地址',
  `recording_enabled` int(11) NULL DEFAULT NULL COMMENT '是否启用录制(1是，2否)',
  `streaming_enabled` int(11) NULL DEFAULT NULL COMMENT '开启直播：1是，2否',
  `bandwidth` int(11) NULL DEFAULT NULL COMMENT '带宽1,2,3,4,5,6M',
  `is_auto_call` int(11) NULL DEFAULT NULL COMMENT '是否自动呼叫与会者：1是，2否',
  `is_auto_monitor` int(11) NULL DEFAULT NULL COMMENT '是否自动监听会议：1是，2否',
  `is_auto_create_conference_number` int(11) NULL DEFAULT NULL COMMENT '是否自动创建会议号：1是，2否',
  `create_type` int(11) NULL DEFAULT NULL COMMENT '创建类型：1自动，2手动',
  `cascad` tinyint(1) NULL DEFAULT NULL COMMENT '模板会议是否允许被级联：1允许，2不允许',
  `view_type` int(11) NULL DEFAULT NULL COMMENT '视图类型（1卡片，2列表）',
  `conference_number` bigint(20) NULL DEFAULT NULL COMMENT '模板绑定的会议号',
  `master_participant_id` bigint(20) NULL DEFAULT NULL COMMENT '主会场ID',
  `default_view_layout` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认视图布局类型',
  `default_view_is_broadcast` int(11) NULL DEFAULT NULL COMMENT '默认视图是否广播(1是，2否)',
  `default_view_is_display_self` int(11) NULL DEFAULT NULL COMMENT '默认视图是否显示自己(1是，2否，3空白)panePlacementSelfPaneMode',
  `default_view_is_fill` int(11) NULL DEFAULT NULL COMMENT '默认视图是否补位(1是，2否)',
  `polling_interval` int(11) NULL DEFAULT NULL COMMENT '轮询时间间隔',
  `conference_password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议密码',
  `remarks` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议备注',
  `cover` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '会议封面（最大5MB）',
  `business_field_type` int(11) NULL DEFAULT NULL COMMENT '业务领域类型',
  `business_properties` json NULL COMMENT '业务属性',
  `duration_enabled` int(11) NULL DEFAULT 0 COMMENT '是否启用会议时长(1是，2否)',
  `duration_time` int(11) NULL DEFAULT 1440 COMMENT '会议时长单位分钟',
  `last_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '最后会议id',
  `resource_template_id` int(11) NULL DEFAULT NULL COMMENT '资源模板id',
  `tencent_userid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '企业唯一用户标识',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '会议类型：\n0：预约会议  \n1：快速会议\n',
  `instanceid` int(2) NULL DEFAULT NULL COMMENT '用户的终端设备类型：\n0：PSTN\n1：PC\n2：Mac\n3：Android\n4：iOS\n5：Web\n6：iPad\n7：Android Pad\n8：小程序\n9：voip、sip 设备\n10：linux\n20：Rooms for Touch Windows\n21：Rooms for Touch MacOS\n22：Rooms for Touch Android\n30：Controller for Touch Windows\n32：Controller for Touch Android\n33：Controller for Touch iOS \n',
  `settings` json NULL COMMENT '会议媒体参数配置对象',
  `live_config` json NULL COMMENT '直播配置',
  `meeting_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议的唯一标识',
  `meeting_type` tinyint(1) NULL DEFAULT 0 COMMENT '默认值为0。\n0：普通会议\n1：周期性会议（周期性会议时 type 不能为快速会议，同一账号同时最多可预定50场周期性会议）\n',
  `webinar_type` tinyint(1) NULL DEFAULT 0 COMMENT '网络研讨会:0:一般会议，1：研讨会',
  `admission_type` tinyint(1) NULL DEFAULT 0 COMMENT '观众观看限制类型：\n0：公开\n1：报名\n2：密码\n',
  `layout_json` json NULL COMMENT '默认布局',
  `backgrounds` json NULL COMMENT '会议背景',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `dept_id`(`dept_id`, `conference_number`) USING BTREE,
  UNIQUE INDEX `conference_number`(`conference_number`) USING BTREE,
  INDEX `busi_conference_template_ibfk_1`(`dept_id`) USING BTREE,
  INDEX `call_leg_profile_id`(`call_leg_profile_id`) USING BTREE,
  INDEX `create_user_id`(`create_user_id`) USING BTREE,
  INDEX `master_participant_id`(`master_participant_id`) USING BTREE,
  INDEX `create_type`(`create_type`) USING BTREE,
  INDEX `business_field_type`(`business_field_type`) USING BTREE,
  CONSTRAINT `busi_tencent_template_conference_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_tencent_template_conference_ibfk_2` FOREIGN KEY (`create_user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '华为MCU会议模板表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;