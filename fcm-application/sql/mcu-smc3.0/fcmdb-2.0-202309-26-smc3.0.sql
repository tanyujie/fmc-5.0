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

 Date: 26/09/2023 11:21:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for busi_mcu_smc3
-- ----------------------------
DROP TABLE IF EXISTS `busi_mcu_smc3`;
CREATE TABLE `busi_mcu_smc3`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '终端创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '终端修改时间',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '连接用户名，同一个组下的用户名相同',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '连接密码，同一个组下的密码相同',
  `meeting_username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'meeting用户名',
  `meeting_password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'meeting密码',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示名字',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备的IP地址',
  `cucm_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '该IP是增强音视频效果',
  `port` int(11) NOT NULL DEFAULT 443 COMMENT '端口',
  `status` int(11) NULL DEFAULT NULL COMMENT '在线状态：1在线，2离线，3删除',
  `capacity` int(11) NULL DEFAULT NULL COMMENT '容量',
  `spare_mcu_id` bigint(20) NULL DEFAULT NULL COMMENT '备用（本节点宕机后指向的备用节点）',
  `mcu_domain` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'MCU域',
  `call_port` int(11) NULL DEFAULT NULL COMMENT '呼叫端口',
  `proxy_host` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '代理服务器地址',
  `proxy_port` int(11) NULL DEFAULT NULL COMMENT '代理服务器端口',
  `sc_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SC服务器地址',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ip`(`ip`, `port`) USING BTREE,
  INDEX `spare_mcu_id`(`spare_mcu_id`) USING BTREE,
  CONSTRAINT `busi_mcu_smc3_ibfk_1` FOREIGN KEY (`spare_mcu_id`) REFERENCES `busi_mcu_smc3` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMC3.0MCU终端信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_mcu_smc3_cluster
-- ----------------------------
DROP TABLE IF EXISTS `busi_mcu_smc3_cluster`;
CREATE TABLE `busi_mcu_smc3_cluster`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '集群组名，最长32',
  `spare_mcu_type` int(11) NULL DEFAULT NULL COMMENT '备用mcu类型，1单节点，100集群',
  `spare_mcu_id` bigint(20) NULL DEFAULT NULL COMMENT '当mcu_type为1是，指向busi_mcu_smc3的id字段，为100指向busi_mcu_smc3_cluster的id字段',
  `description` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `spare_mcu_id`(`spare_mcu_id`) USING BTREE,
  INDEX `spare_mcu_type`(`spare_mcu_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMC3.0MCU集群' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_mcu_smc3_cluster_map
-- ----------------------------
DROP TABLE IF EXISTS `busi_mcu_smc3_cluster_map`;
CREATE TABLE `busi_mcu_smc3_cluster_map`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `cluster_id` bigint(20) NULL DEFAULT NULL COMMENT 'FME集群ID',
  `mcu_id` bigint(20) NULL DEFAULT NULL COMMENT 'MCU的ID',
  `weight` int(11) NULL DEFAULT NULL COMMENT '节点在集群中的权重值',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `group_id`(`cluster_id`, `mcu_id`, `weight`) USING BTREE,
  UNIQUE INDEX `cluster_id`(`cluster_id`, `mcu_id`) USING BTREE,
  INDEX `mcu_id`(`mcu_id`) USING BTREE,
  CONSTRAINT `busi_mcu_smc3_cluster_map_ibfk_1` FOREIGN KEY (`cluster_id`) REFERENCES `busi_mcu_smc3_cluster` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_mcu_smc3_cluster_map_ibfk_2` FOREIGN KEY (`mcu_id`) REFERENCES `busi_mcu_smc3` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMC3.0MCU-终端组中间表（多对多）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_mcu_smc3_conference_appointment
-- ----------------------------
DROP TABLE IF EXISTS `busi_mcu_smc3_conference_appointment`;
CREATE TABLE `busi_mcu_smc3_conference_appointment`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '归属租户',
  `template_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的会议模板ID',
  `is_auto_create_template` int(11) NULL DEFAULT NULL COMMENT '是否自动创建模板',
  `start_time` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议开始时间',
  `end_time` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议结束时间',
  `extend_minutes` int(11) NULL DEFAULT NULL COMMENT '延长分钟数',
  `is_hang_up` int(11) NULL DEFAULT NULL COMMENT '是否主动挂断',
  `status` int(11) NULL DEFAULT NULL COMMENT '预约会议的状态：1启动，2停止',
  `is_start` int(11) NULL DEFAULT NULL COMMENT '会议是否开始',
  `start_failed_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '启动失败原因记录',
  `repeat_rate` int(11) NULL DEFAULT NULL COMMENT '1自定义，2每天，3每周，4每月',
  `repeat_date` int(11) NULL DEFAULT NULL COMMENT 'repeat_rate为3，范围为[1-7]，repeat_rate为4范围是1-30',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '入会密码',
  `attendee_limit` int(11) NULL DEFAULT NULL COMMENT '参会者上限',
  `type` int(11) NULL DEFAULT 1 COMMENT '会议类型:1:预约会议;2:即时会议',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  INDEX `template_id`(`template_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `is_auto_create_template`(`is_auto_create_template`) USING BTREE,
  CONSTRAINT `busi_mcu_smc3_conference_appointment_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_mcu_smc3_conference_appointment_ibfk_2` FOREIGN KEY (`template_id`) REFERENCES `busi_mcu_smc3_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMC3.0MCU会议预约记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_mcu_smc3_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_mcu_smc3_dept`;
CREATE TABLE `busi_mcu_smc3_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '分配给的租户',
  `mcu_type` int(11) NULL DEFAULT NULL COMMENT '1单节点，100集群',
  `mcu_id` bigint(20) NULL DEFAULT NULL COMMENT '当mcu_type为1是，指向busi_mcu_smc3的id字段，为100指向busi_mcu_smc3_cluster的id字段',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `busi_mcu_smc3_group_dept_ibfk_1`(`dept_id`) USING BTREE,
  INDEX `mcu_group_id`(`mcu_id`) USING BTREE,
  CONSTRAINT `busi_mcu_smc3_dept_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMC3.0组分配租户的中间表（一个MCU组可以分配给多个租户，一对多）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_mcu_smc3_template_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_mcu_smc3_template_conference`;
CREATE TABLE `busi_mcu_smc3_template_conference`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者id',
  `create_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者用户名',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板会议名',
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
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '模板会议是否允许被级联：1允许，2不允许',
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
  `conference_ctrl_password` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议控制密码',
  `last_conference_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后会议id',
  `mute_type` int(11) NULL DEFAULT 1 COMMENT '静音类型 0:不静音 1:静音',
  `is_auto_create_stream_url` int(11) NULL DEFAULT 2 COMMENT '是否自动创建直播URL：1是，2否',
  `presenter` int(11) NULL DEFAULT NULL COMMENT '主持人终端id',
  `video_protocol` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频协议',
  `video_resolution` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分辨率',
  `chairman_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主席密码',
  `guest_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '嘉宾密码',
  `smc_template_id` varchar(70) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SMC3模板ID',
  `conf_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议类型',
  `max_participant_num` int(10) NULL DEFAULT NULL COMMENT '最大入会数',
  `up_cascade_conference_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上级会议ID',
  `up_cascade_type` int(11) NULL DEFAULT 0 COMMENT '级联类型 0:自动生成模板 1:手动选择未开会模板 2:手动选择已开会模板',
  `presence_multi_pic` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '预设画面',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `dept_id`(`dept_id`, `conference_number`) USING BTREE,
  UNIQUE INDEX `conference_number`(`conference_number`) USING BTREE,
  INDEX `busi_conference_template_ibfk_1`(`dept_id`) USING BTREE,
  INDEX `call_leg_profile_id`(`call_leg_profile_id`) USING BTREE,
  INDEX `create_user_id`(`create_user_id`) USING BTREE,
  INDEX `master_participant_id`(`master_participant_id`) USING BTREE,
  INDEX `create_type`(`create_type`) USING BTREE,
  INDEX `business_field_type`(`business_field_type`) USING BTREE,
  CONSTRAINT `busi_mcu_smc3_template_conference_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_mcu_smc3_template_conference_ibfk_3` FOREIGN KEY (`create_user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_mcu_smc3_template_conference_ibfk_4` FOREIGN KEY (`master_participant_id`) REFERENCES `busi_mcu_smc3_template_participant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_mcu_smc3_template_conference_ibfk_5` FOREIGN KEY (`conference_number`) REFERENCES `busi_conference_number` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMC3.0MCU会议模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_mcu_smc3_template_conference_default_view_cell_screen
-- ----------------------------
DROP TABLE IF EXISTS `busi_mcu_smc3_template_conference_default_view_cell_screen`;
CREATE TABLE `busi_mcu_smc3_template_conference_default_view_cell_screen`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的会议模板ID',
  `cell_sequence_number` int(11) NULL DEFAULT NULL COMMENT '单元格序号',
  `operation` int(11) NULL DEFAULT NULL COMMENT '分频单元格对应的操作，默认为选看101，105轮询',
  `is_fixed` int(11) NULL DEFAULT NULL COMMENT '分频单元格是否固定',
  `type` int(11) NULL DEFAULT 1 COMMENT '与会者类型：1:主会场 2:观众',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`) USING BTREE,
  INDEX `cell_sequence_number`(`cell_sequence_number`) USING BTREE,
  CONSTRAINT `busi_mcu_smc3_default_view_cell_screen_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_mcu_smc3_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMC3.0MCU默认视图下指定的多分频单元格' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_mcu_smc3_template_conference_default_view_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_mcu_smc3_template_conference_default_view_dept`;
CREATE TABLE `busi_mcu_smc3_template_conference_default_view_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的会议模板ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID（部门也是MCU终端，一种与会者）',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  `type` int(11) NULL DEFAULT 1 COMMENT '与会者类型：1:主会场 2:观众',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  INDEX `busi_mcu_plc_template_default_view_dept_ibfk_1`(`template_conference_id`) USING BTREE,
  CONSTRAINT `busi_mcu_smc3_default_view_dept_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_mcu_smc3_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_mcu_smc3_default_view_dept_ibfk_2` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMC3.0MCU默认视图的部门显示顺序' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_mcu_smc3_template_conference_default_view_paticipant
-- ----------------------------
DROP TABLE IF EXISTS `busi_mcu_smc3_template_conference_default_view_paticipant`;
CREATE TABLE `busi_mcu_smc3_template_conference_default_view_paticipant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的会议模板ID',
  `template_participant_id` bigint(20) NULL DEFAULT NULL COMMENT '参会终端ID，关联busi_template_participant的ID',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  `cell_sequence_number` int(11) NULL DEFAULT NULL COMMENT '多分频单元格序号',
  `type` int(11) NULL DEFAULT 1 COMMENT '与会者类型：1:主会场 2:观众',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_participant_id`(`template_participant_id`, `type`) USING BTREE,
  INDEX `default_view_id`(`template_conference_id`) USING BTREE,
  INDEX `cell_sequence_number`(`cell_sequence_number`) USING BTREE,
  CONSTRAINT `busi_mcu_smc3_default_view_paticipant_ibfk_3` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_mcu_smc3_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_mcu_smc3_default_view_paticipant_ibfk_4` FOREIGN KEY (`template_participant_id`) REFERENCES `busi_mcu_smc3_template_participant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMC3.0MCU默认视图的参会者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_mcu_smc3_template_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_mcu_smc3_template_dept`;
CREATE TABLE `busi_mcu_smc3_template_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `uuid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板中的级联与会者终端的UUID(MCU参会者ID)',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID（部门也是MCU终端，一种与会者）',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `terminal_id`(`dept_id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`, `weight`) USING BTREE,
  CONSTRAINT `busi_mcu_smc3_template_dept_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_mcu_smc3_template_dept_ibfk_2` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_mcu_smc3_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMC3.0MCU会议模板的级联部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_mcu_smc3_template_participant
-- ----------------------------
DROP TABLE IF EXISTS `busi_mcu_smc3_template_participant`;
CREATE TABLE `busi_mcu_smc3_template_participant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `attend_type` int(11) NULL DEFAULT NULL COMMENT '参会类型：1被叫，2手动主叫，3自动主叫，10直播',
  `uuid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板中的与会者的UUID',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `terminal_id` bigint(20) NULL DEFAULT NULL COMMENT '终端ID',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  `business_properties` json NULL COMMENT '业务属性',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uuid`(`uuid`) USING BTREE,
  UNIQUE INDEX `template_conference_id_2`(`template_conference_id`, `terminal_id`) USING BTREE,
  INDEX `terminal_id`(`terminal_id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`, `weight`) USING BTREE,
  CONSTRAINT `busi_mcu_smc3_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_mcu_smc3_template_participant_ibfk_3` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_mcu_smc3_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMC3.0MCU会议模板的参会者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_mcu_smc3_template_polling_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_mcu_smc3_template_polling_dept`;
CREATE TABLE `busi_mcu_smc3_template_polling_dept`  (
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
  CONSTRAINT `busi_mcu_smc3_template_polling_dept_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_mcu_smc3_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_mcu_smc3_template_polling_dept_ibfk_2` FOREIGN KEY (`polling_scheme_id`) REFERENCES `busi_mcu_smc3_template_polling_scheme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMC3.0MCU轮询方案的部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_mcu_smc3_template_polling_paticipant
-- ----------------------------
DROP TABLE IF EXISTS `busi_mcu_smc3_template_polling_paticipant`;
CREATE TABLE `busi_mcu_smc3_template_polling_paticipant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `polling_scheme_id` bigint(20) NULL DEFAULT NULL COMMENT '归属轮询方案ID',
  `polling_interval` int(11) NULL DEFAULT NULL COMMENT '该参会者的特定的轮询间隔（如果该值存在，会覆盖轮询方案终端间隔）',
  `attendee_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会场UUID',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `polling_scheme_id`(`polling_scheme_id`, `attendee_id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`) USING BTREE,
  INDEX `attendee_id`(`attendee_id`) USING BTREE,
  CONSTRAINT `busi_mcu_smc3_template_polling_paticipant_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_mcu_smc3_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_mcu_smc3_template_polling_paticipant_ibfk_2` FOREIGN KEY (`polling_scheme_id`) REFERENCES `busi_mcu_smc3_template_polling_scheme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMC3.0MCU轮询方案的参会者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_mcu_smc3_template_polling_scheme
-- ----------------------------
DROP TABLE IF EXISTS `busi_mcu_smc3_template_polling_scheme`;
CREATE TABLE `busi_mcu_smc3_template_polling_scheme`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `polling_interval` int(11) NULL DEFAULT NULL COMMENT '轮询时间间隔',
  `polling_strategy` int(11) NULL DEFAULT NULL COMMENT '轮询策略：1全局轮询，2选定范围，3全局轮询+组织架构优先，4选定范围+组织架构优先',
  `scheme_name` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '轮询方案名',
  `enable_status` int(11) NULL DEFAULT NULL COMMENT '启用状态：1启用，2禁用',
  `weight` int(11) NULL DEFAULT NULL COMMENT '轮询方案顺序，越大越靠前',
  `layout` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '多分频轮询支持',
  `is_broadcast` int(11) NULL DEFAULT NULL COMMENT '是否广播(1是，2否)',
  `is_display_self` int(11) NULL DEFAULT NULL COMMENT '是否显示自己(1是，2否)',
  `is_fill` int(11) NULL DEFAULT NULL COMMENT '是否补位(1是，2否)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`, `enable_status`) USING BTREE,
  CONSTRAINT `busi_mcu_smc3_template_polling_scheme_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_mcu_smc3_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SMC3.0MCU轮询方案' ROW_FORMAT = Dynamic;
SET FOREIGN_KEY_CHECKS = 1;