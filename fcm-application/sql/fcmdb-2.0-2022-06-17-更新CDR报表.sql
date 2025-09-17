CREATE TABLE `busi_history_all_call` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `call_id` varchar(40) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT 'callId',
  `co_space` varchar(40) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT 'coSpaceId',
  `history_conference_id` bigint(20) DEFAULT NULL COMMENT '关联的会议ID(busi_history_all_conference)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `call_id` (`call_id`) USING BTREE,
  KEY `history_conference_id` (`history_conference_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='历史全call表；busi_history_all_conference与该表是一对多的关系';


CREATE TABLE `busi_history_all_conference` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '创建者ID',
  `create_user_name` varchar(32) DEFAULT NULL COMMENT '创建者用户名',
  `name` varchar(128) DEFAULT NULL COMMENT '模板会议名',
  `number` varchar(20) DEFAULT NULL COMMENT '会议号码',
  `call_leg_profile_id` varchar(128) DEFAULT NULL COMMENT '入会方案配置ID（关联FME里面的入会方案记录ID，会控端不存）',
  `bandwidth` int(11) DEFAULT NULL COMMENT '带宽1,2,3,4,5,6M',
  `call_id` varchar(40) DEFAULT NULL COMMENT 'callId',
  `co_space` varchar(40) DEFAULT NULL COMMENT 'coSpaceId',
  `conference_start_time` datetime DEFAULT NULL COMMENT '会议开始时间',
  `conference_end_time` datetime DEFAULT NULL COMMENT '会议结束时间',
  `duration` int(11) DEFAULT '0' COMMENT '会议时长',
  `device_num` int(11) DEFAULT NULL COMMENT '终端总数',
  `type` int(11) DEFAULT '0' COMMENT '会议类型:0:普通会议（模板会议）;1:预约会议;2:即时会议',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `callId` (`call_id`) USING BTREE,
  KEY `conference_start_time` (`conference_start_time`) USING BTREE,
  KEY `conference_end_time` (`conference_end_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='历史全会议，每次挂断会保存该历史记录';

CREATE TABLE `busi_history_all_participant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `history_conference_id` bigint(20) DEFAULT NULL COMMENT '关联的会议ID(busi_history_all_conference)',
  `weight` int(11) DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  `co_space` varchar(40) DEFAULT NULL COMMENT 'coSpaceId',
  `call_id` varchar(40) DEFAULT NULL COMMENT '会议ID',
  `call_leg_id` varchar(40) DEFAULT NULL COMMENT 'call_leg_id:会场ID',
  `remote_party` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'callLeg的远程参与方地址',
  `name` varchar(40) DEFAULT NULL COMMENT '终端名称',
  `join_time` datetime DEFAULT NULL COMMENT '入会时间',
  `outgoing_time` datetime DEFAULT NULL COMMENT '离会时间',
  `duration_seconds` int(11) DEFAULT '0' COMMENT 'call leg处于活动的时长(s)',
  `joined` int(11) DEFAULT '0' COMMENT '是否入会',
  `media_info` json DEFAULT NULL COMMENT '流媒体信息',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `callLegId` (`call_leg_id`) USING BTREE,
  KEY `callId` (`call_id`) USING BTREE,
  KEY `outgoing_time` (`outgoing_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='历史全会议的参会者';


CREATE TABLE `cdr_all_task_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `report_type` int(11) NOT NULL COMMENT '报表类型',
  `duration_or_num` int(11) DEFAULT NULL COMMENT '时长或数量',
  `date` date NOT NULL COMMENT '统计结果所属日期',
  `group_type` int(11) DEFAULT NULL COMMENT '统计维度分组类型',
  `create_time` datetime DEFAULT NULL COMMENT '统计时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `type_date` (`report_type`,`date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='cdr全定时任务结果表';


CREATE TABLE `cdr_all_call_num_date` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `fme_ip` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'fmeIP',
  `number` int(11) DEFAULT NULL COMMENT '会议数量',
  `record_date` date DEFAULT NULL COMMENT '日期',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='全每天开始会议的数量';


CREATE TABLE `cdr_all_call_leg_num_date` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `fme_ip` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'fmeIP',
  `number` int(11) DEFAULT NULL COMMENT '入会者数量',
  `record_date` date DEFAULT NULL COMMENT '日期',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='全每天参会的数量';


ALTER TABLE `fcmdb`.`busi_history_conference`
CHANGE COLUMN `type` `type` INT(11) NULL DEFAULT '0' COMMENT '会议类型:0:普通会议（模板会议）;1:预约会议;2:即时会议' ;


ALTER TABLE `fcmdb`.`busi_conference_appointment`
ADD COLUMN `type` INT NULL DEFAULT 1 COMMENT '会议类型:1:预约会议;2:即时会议' AFTER `attendee_limit`;


ALTER TABLE `fcmdb`.`busi_history_participant`
ADD COLUMN `terminal_id` BIGINT(20) NULL COMMENT '关联的终端ID' AFTER `media_info`;


CREATE TABLE `busi_history_participant_terminal` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `history_conference_id` bigint(20) NOT NULL COMMENT '关联的会议ID',
  `terminal_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '关联的终端ID',
  `remote_party` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '远程参与方地址',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门Id',
  `name` varchar(40) DEFAULT NULL COMMENT '终端名称',
  `join_time` datetime DEFAULT NULL COMMENT '入会时间',
  `outgoing_time` datetime DEFAULT NULL COMMENT '离会时间',
  `duration_seconds` int(11) DEFAULT '0' COMMENT '处于活动的时长(s)',
  `joined` int(11) DEFAULT '0' COMMENT '是否入会',
  `joined_times` int(11) DEFAULT '0' COMMENT '入会次数',
  `media_info` json DEFAULT NULL COMMENT '流媒体信息',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `template_conference_id` (`history_conference_id`,`joined`,`join_time`) USING BTREE,
  KEY `conference_terminal` (`history_conference_id`,`terminal_id`,`remote_party`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='历史会议的参会者终端';


CREATE TABLE `cdr_terminal_usage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dept_id` int(11) NOT NULL COMMENT '部门Id',
  `terminal_id` bigint(20) NOT NULL COMMENT '终端Id',
  `date` date NOT NULL COMMENT '日期',
  `num` int(11) DEFAULT NULL COMMENT '参会数量',
  `duration_seconds` int(11) DEFAULT NULL COMMENT '参会时长（秒）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `dept_terminal_date` (`dept_id`,`terminal_id`,`date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='cdr使用情况表';

