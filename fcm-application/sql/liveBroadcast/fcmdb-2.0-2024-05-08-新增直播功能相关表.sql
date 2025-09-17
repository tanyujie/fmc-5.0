CREATE TABLE `busi_live_broadcast` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_by` varchar(200) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '归属租户',
  `start_time` varchar(24) DEFAULT NULL COMMENT '直播开始时间',
  `end_time` varchar(24) DEFAULT NULL COMMENT '直播结束时间',
  `extend_minutes` int(11) DEFAULT NULL COMMENT '延长分钟数',
  `is_start` int(11) DEFAULT NULL COMMENT '直播是否开始',
  `status` int(11) DEFAULT NULL COMMENT '直播的状态：1启动，2停止，3已结束',
  `type` int(11) DEFAULT '1' COMMENT '直播类型:1:会议直播;2:普通直播',
  `stream_url` varchar(256) DEFAULT NULL COMMENT '直播推流地址',
  `name` varchar(128) DEFAULT NULL COMMENT '直播名',
  `introduce` varchar(5000) DEFAULT NULL COMMENT '直播介绍',
  `playback_enabled` int(11) DEFAULT NULL COMMENT '是否启用回放(1是，2否)',
  `comments_enabled` int(11) DEFAULT NULL COMMENT '是否启用评论(1是，2否)',
  `gifts_enabled` int(11) DEFAULT NULL COMMENT '是否启用送礼(1是，2否)',
  `duration` int(11) DEFAULT '0' COMMENT '直播时长',
  `device_num` int(128) DEFAULT NULL COMMENT '终端总数',
  `terminal_id` bigint(11) DEFAULT NULL COMMENT '终端id',
  `end_reasons_type` int(11) NOT NULL DEFAULT '1' COMMENT '直播结束原因：1:管理员挂断; 2:到时自动结束; 3:异常结束 ',
  `history_conference_id` bigint(11) DEFAULT NULL COMMENT '历史会议id',
  `meeting_file_id` bigint(11) DEFAULT NULL COMMENT '文件直播的文件id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `dept_id` (`dept_id`) USING BTREE,
  KEY `status` (`status`) USING BTREE,
  KEY `terminal_id` (`terminal_id`) USING BTREE,
  CONSTRAINT `busi_live_broadcast_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='直播记录表';


CREATE TABLE `busi_live_broadcast_appointment_map` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `live_broadcast_id` bigint(20) DEFAULT NULL COMMENT '直播id',
  `mcu_type` varbinary(20) DEFAULT NULL COMMENT 'mcu类型',
  `appointment_id` bigint(20) DEFAULT NULL COMMENT '预约会议id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='直播会议对应表';


CREATE TABLE `busi_live_information` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_by` varchar(200) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `url` varchar(255) DEFAULT NULL COMMENT '资料url地址',
  `live_broadcast_id` bigint(20) DEFAULT NULL COMMENT '直播id',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播资料表';


CREATE TABLE `busi_live_records` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `live_id` bigint(20) NOT NULL COMMENT '直播ID',
  `file_name` varchar(64) DEFAULT NULL COMMENT '文件名',
  `file_url` varchar(100) DEFAULT NULL COMMENT '文件URL',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(30) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_by` varchar(30) DEFAULT NULL COMMENT '更新人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `delete_by` varchar(30) DEFAULT NULL COMMENT '删除人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `live_id` (`live_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='直播录制文件记录表';


CREATE TABLE `busi_live_comments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `live_id` bigint(20) NOT NULL COMMENT '直播ID',
  `comment_content` varchar(100) DEFAULT NULL COMMENT '评论',
  `comment_user_id` bigint(20) DEFAULT NULL COMMENT '评论用户ID',
  `comment_user_name` varchar(64) DEFAULT NULL COMMENT '评论用户名',
  `parent_comment_id` bigint(20) DEFAULT NULL COMMENT '父评论ID',
  `reply_comment_id` bigint(20) DEFAULT NULL COMMENT '回复评论ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(30) DEFAULT NULL COMMENT '创建人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `delete_by` varchar(30) DEFAULT NULL COMMENT '删除人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `live_id` (`live_id`) USING BTREE,
  KEY `parent_comment_id` (`parent_comment_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='直播回放评论表';

