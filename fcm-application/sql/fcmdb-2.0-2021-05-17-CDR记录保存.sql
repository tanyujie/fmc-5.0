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

 Date: 17/05/2021 09:53:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cdr_call
-- ----------------------------
DROP TABLE IF EXISTS `cdr_call`;
CREATE TABLE `cdr_call`  (
  `cdr_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'callId',
  `session` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会话ID',
  `call_bridge` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会议桥ID',
  `record_index` int(11) NULL DEFAULT NULL COMMENT '记录索引',
  `correlator_index` int(11) NULL DEFAULT NULL COMMENT '记录索引',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'call名称',
  `co_space` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'coSpaceId',
  `owner_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '所有者名称',
  `tenant` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '租户标识',
  `cdr_tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'cdr标识',
  `call_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'call类型:coSpace| coSpace实例',
  `call_correlator` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '一个或多个call bridge上的call leg唯一标识',
  `call_legs_completed` int(11) NULL DEFAULT NULL COMMENT '当前call中已完成的 call leg数量',
  `call_legs_max_active` int(11) NULL DEFAULT NULL COMMENT '当前call中同时活动的最大 call leg数量',
  `duration_seconds` int(11) NULL DEFAULT NULL COMMENT '当前call 处于活动状态的时间长度（以秒为单位）',
  `record_type` int(11) NULL DEFAULT NULL COMMENT '0:callEnd;1:callStart',
  `time` datetime(0) NULL DEFAULT NULL COMMENT '记录时间',
  PRIMARY KEY (`cdr_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'callstart 和callend记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cdr_call_leg_end
-- ----------------------------
DROP TABLE IF EXISTS `cdr_call_leg_end`;
CREATE TABLE `cdr_call_leg_end`  (
  `cdr_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'callLegID',
  `session` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会话ID',
  `call_bridge` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会议桥ID',
  `record_index` int(11) NULL DEFAULT NULL COMMENT '记录索引',
  `correlator_index` int(11) NULL DEFAULT NULL COMMENT '记录索引',
  `cdr_tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'cdr标识',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '结束原因',
  `remote_teardown` tinyint(1) NULL DEFAULT NULL COMMENT 'true:由远程参会者终止；false : 由会议服务器发起终止',
  `encrypted_media` tinyint(1) NULL DEFAULT NULL COMMENT '是否存在加密的Media',
  `unencrypted_media` tinyint(1) NULL DEFAULT NULL COMMENT '是否存在未加密的Media',
  `duration_seconds` int(11) NULL DEFAULT NULL COMMENT 'call leg处于活动的时长(s)',
  `activated_duration` int(11) NULL DEFAULT NULL COMMENT 'call leg已活动的时长(s)',
  `main_video_viewer` double(11, 0) NULL DEFAULT NULL COMMENT '不同类型媒体活动的百分比信息:主视频查看',
  `main_video_contributor` double(11, 0) NULL DEFAULT NULL COMMENT '不同类型媒体活动的百分比信息:主要视频参与者',
  `presentation_viewer` double(11, 0) NULL DEFAULT NULL COMMENT '不同类型媒体活动的百分比信息:演示文稿查看器',
  `presentation_contributor` double(11, 0) NULL DEFAULT NULL COMMENT '不同类型媒体活动的百分比信息:演示文稿的贡献者',
  `multistream_video` double(11, 0) NULL DEFAULT NULL COMMENT '不同类型媒体活动的百分比信息:多流媒体视频',
  `max_screens` int(11) NULL DEFAULT NULL COMMENT '最大屏幕数量',
  `rx_audio_codec` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '接收音频的相关信息:codec类型',
  `tx_audio_codec` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '传输音频的相关信息:codec类型',
  `rx_video_codec` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '接收视频的相关信息:codec类型',
  `tx_video_codec` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '传输视频的相关信息:codec类型',
  `tx_video_max_size_width` int(11) NULL DEFAULT NULL COMMENT '传输视频最大视频分辨率宽度',
  `tx_video_max_size_height` int(11) NULL DEFAULT NULL COMMENT '传输视频最大视频分辨率高度',
  `rx_video_max_size_width` int(11) NULL DEFAULT NULL COMMENT '接收视频最大视频分辨率宽度',
  `rx_video_max_size_height` int(11) NULL DEFAULT NULL COMMENT '接收视频最大视频分辨率高度',
  `owner_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '远程系统分配的所有者ID',
  `sip_call_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'sip连接的callID',
  `time` datetime(0) NULL DEFAULT NULL COMMENT '记录时间',
  PRIMARY KEY (`cdr_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'callLegEnd记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cdr_call_leg_end_alarm
-- ----------------------------
DROP TABLE IF EXISTS `cdr_call_leg_end_alarm`;
CREATE TABLE `cdr_call_leg_end_alarm`  (
  `cdr_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `cdr_leg_end_id` bigint(11) NULL DEFAULT NULL COMMENT 'callLegEnd主键Id',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '报警类型:packetLoss|excessiveJitter|highRoundTripTime',
  `duration_percentage` double(11, 0) NULL DEFAULT NULL COMMENT '发生该报警条件的呼叫持续时间的百分比',
  PRIMARY KEY (`cdr_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'callLegEnd报警信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cdr_call_leg_end_media_info
-- ----------------------------
DROP TABLE IF EXISTS `cdr_call_leg_end_media_info`;
CREATE TABLE `cdr_call_leg_end_media_info`  (
  `cdr_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'callLegID',
  `session` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会话ID',
  `call_bridge` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会议桥ID',
  `record_index` int(11) NULL DEFAULT NULL COMMENT '记录索引',
  `correlator_index` int(11) NULL DEFAULT NULL COMMENT '记录索引',
  `codec` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '传输视频的相关信息:codec类型',
  `max_size_width` int(11) NULL DEFAULT NULL COMMENT '传输视频最大视频分辨率宽度',
  `max_size_height` int(11) NULL DEFAULT NULL COMMENT '传输视频最大视频分辨率高度',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'txVideo/rxVideo/txAudio/rxAudio',
  `packet_loss_bursts_duration` double(11, 0) NULL DEFAULT NULL COMMENT '丢包间隔',
  `packet_loss_bursts_density` double(11, 0) NULL DEFAULT NULL COMMENT '丢包频率',
  `packet_gap_duration` double(11, 0) NULL DEFAULT NULL COMMENT '数据包间隙',
  `packet_gap_density` double(11, 0) NOT NULL COMMENT '数据包频率',
  PRIMARY KEY (`cdr_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '视频流、音频流传输信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cdr_call_leg_start
-- ----------------------------
DROP TABLE IF EXISTS `cdr_call_leg_start`;
CREATE TABLE `cdr_call_leg_start`  (
  `cdr_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'callLegID',
  `session` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会话ID',
  `call_bridge` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会议桥ID',
  `record_index` int(11) NULL DEFAULT NULL COMMENT '记录索引',
  `correlator_index` int(11) NULL DEFAULT NULL COMMENT '记录索引',
  `display_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '显示名称',
  `local_address` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '传入呼叫的被叫者ID/传出呼叫的呼叫者ID',
  `remote_address` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '呼叫方或被叫者的远程地址，具体看连接是呼入还是呼出',
  `remote_party` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'callLeg的远程参与方地址',
  `cdr_tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'cdr标识',
  `guest_connection` tinyint(1) NULL DEFAULT NULL COMMENT '启用访客连接时为true',
  `recording` tinyint(1) NULL DEFAULT NULL COMMENT '是否启用录制',
  `streaming` tinyint(1) NULL DEFAULT NULL COMMENT '是否创建流媒体连接',
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'call leg的类型: sip|acano',
  `sub_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'call leg的子类型 :lync | avaya | distributionLink | lyncdistribution | webApp',
  `lync_sub_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'lync子类型的子类型:audioVideo| applicationSharing| instantMessaging',
  `direction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '呼入incoming/呼出类型outgoing',
  `call_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'call ID',
  `owner_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '远程系统分配的所有者ID',
  `sip_call_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'sip连接的callID',
  `group_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Lync模式下的分组ID',
  `replaces_sip_call_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'SIP连接替换时的 CallID',
  `can_move` tinyint(1) NULL DEFAULT NULL COMMENT '是否可使用movedParticipant API移除参会者',
  `moved_call_leg` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '移动CallLeg时的 CallLeg GUID',
  `moved_call_leg_call_bridge` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '移动CallLeg时的CallBridge ID',
  `time` datetime(0) NULL DEFAULT NULL COMMENT '记录时间',
  PRIMARY KEY (`cdr_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'callLegStart 记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cdr_call_leg_update
-- ----------------------------
DROP TABLE IF EXISTS `cdr_call_leg_update`;
CREATE TABLE `cdr_call_leg_update`  (
  `cdr_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'callLegID',
  `session` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会话ID',
  `call_bridge` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会议桥ID',
  `record_index` int(11) NULL DEFAULT NULL COMMENT '记录索引',
  `correlator_index` int(11) NULL DEFAULT NULL COMMENT '记录索引',
  `cdr_tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'cdr标识',
  `state` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'callLeg状态:connected;如何为空则未连接',
  `deactivated` tinyint(1) NULL DEFAULT NULL COMMENT 'callLeg当前是否已停用',
  `remote_address` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '呼叫方或被叫者的远程地址，具体看连接是呼入还是呼出',
  `call_Ivr` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '如何ivr状态时为空,否则为callLeg的callId',
  `owner_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '远程系统分配的所有者ID',
  `sip_call_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'sip连接的callID',
  `group_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Lync模式下的分组ID',
  `display_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '显示名称',
  `can_move` tinyint(1) NULL DEFAULT NULL COMMENT '是否可使用movedParticipant API移除参会者',
  `time` datetime(0) NULL DEFAULT NULL COMMENT '记录时间',
  PRIMARY KEY (`cdr_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'callLegUpdate记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cdr_recording
-- ----------------------------
DROP TABLE IF EXISTS `cdr_recording`;
CREATE TABLE `cdr_recording`  (
  `cdr_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'recording的Id',
  `session` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会话ID',
  `call_bridge` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会议桥ID',
  `record_index` int(11) NULL DEFAULT NULL COMMENT '记录索引',
  `correlator_index` int(11) NULL DEFAULT NULL COMMENT '记录索引',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '录制文件路径',
  `recorder_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '记录设备的Uri',
  `call` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '正在记录的callId',
  `callLeg` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '正在记录的callLegId',
  `time` datetime(0) NULL DEFAULT NULL COMMENT '记录时间',
  `record_type` int(11) NULL DEFAULT NULL COMMENT '0:recordingEnd;1:recordingStart',
  PRIMARY KEY (`cdr_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'recordingStart和recordingEnd记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cdr_streaming
-- ----------------------------
DROP TABLE IF EXISTS `cdr_streaming`;
CREATE TABLE `cdr_streaming`  (
  `cdr_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'streaming标签ID',
  `session` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会话ID',
  `call_bridge` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会议桥ID',
  `record_index` int(11) NULL DEFAULT NULL COMMENT '记录索引',
  `correlator_index` int(11) NULL DEFAULT NULL COMMENT '记录索引',
  `stream_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'RTMP类型的URL地址',
  `streamer_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '流媒体设备的Uri',
  `call` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '正在记录的callId',
  `callLeg` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '正在记录的callLegId',
  `time` datetime(0) NULL DEFAULT NULL COMMENT '记录时间',
  `record_type` int(11) NULL DEFAULT NULL COMMENT '0:streamingEnd;1:streamingStart',
  PRIMARY KEY (`cdr_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'streamingStart和streamingEnd记录表' ROW_FORMAT = Dynamic;
