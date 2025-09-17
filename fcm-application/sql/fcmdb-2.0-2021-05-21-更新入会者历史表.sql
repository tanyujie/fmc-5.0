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

 Date: 21/05/2021 18:06:00
*/
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
  `call_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议ID',
  `call_leg_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'call_leg_id:会场ID',
  `dept_id` int(11) NULL DEFAULT NULL COMMENT '部门Id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_conference_id`(`history_conference_id`) USING BTREE,
  INDEX `terminal_id`(`terminal_id`) USING BTREE,
  CONSTRAINT `busi_history_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_history_participant_ibfk_3` FOREIGN KEY (`history_conference_id`) REFERENCES `busi_history_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '历史会议的参会者' ROW_FORMAT = Dynamic;


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
  `owner_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '远程系统分配的所有者ID',
  `sip_call_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'sip连接的callID',
  `time` datetime(0) NULL DEFAULT NULL COMMENT '记录时间',
  PRIMARY KEY (`cdr_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 675 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'callLegEnd记录表' ROW_FORMAT = Dynamic;
