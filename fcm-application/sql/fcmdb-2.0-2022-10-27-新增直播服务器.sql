CREATE TABLE `busi_live` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime DEFAULT NULL COMMENT '终端创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '终端修改时间',
  `name` varchar(32) DEFAULT NULL COMMENT '显示名字',
  `ip` varchar(64) NOT NULL COMMENT '设备的IP地址',
  `port` int(11) DEFAULT NULL COMMENT '端口',
  `status` int(11) DEFAULT NULL COMMENT '在线状态：1在线，2离线，3删除',
  `capacity` int(11) DEFAULT NULL COMMENT '容量',
  `uri_path` varchar(255) DEFAULT NULL COMMENT '直播路径',
  `protocol_type` varchar(255) DEFAULT NULL COMMENT '协议类型',
  `domain_name` varchar(128) CHARACTER SET utf8 DEFAULT NULL COMMENT '域名',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `ip` (`ip`,`port`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='直播服务器信息表';


CREATE TABLE `busi_live_cluster` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `name` varchar(32) DEFAULT NULL COMMENT '集群组名，最长32',
  `description` varchar(128) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='直播服务器集群';


CREATE TABLE `busi_live_cluster_map` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `cluster_id` bigint(20) DEFAULT NULL COMMENT '集群ID',
  `live_id` bigint(20) DEFAULT NULL COMMENT 'live的ID',
  `weight` int(11) DEFAULT NULL COMMENT '节点在集群中的权重值',
  `live_type` int(11) DEFAULT '0' COMMENT '直播服务器类型: 0:拉流 1:推流',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `group_id` (`cluster_id`,`live_id`,`weight`) USING BTREE,
  UNIQUE KEY `cluster_id` (`cluster_id`,`live_id`) USING BTREE,
  KEY `live_id` (`live_id`) USING BTREE,
  CONSTRAINT `busi_live_cluster_map_ibfk_1` FOREIGN KEY (`cluster_id`) REFERENCES `busi_live_cluster` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_live_cluster_map_ibfk_2` FOREIGN KEY (`live_id`) REFERENCES `busi_live` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='直播服务器集群组中间表（多对多）';


CREATE TABLE `busi_live_dept` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '分配给的租户',
  `live_type` int(11) DEFAULT NULL COMMENT '1单节点，100集群',
  `live_id` bigint(20) DEFAULT NULL COMMENT '当live_type为1是，指向busi_live的id字段，为100指向busi_live_cluster的id字段',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `busi_fme_group_dept_ibfk_1` (`dept_id`) USING BTREE,
  KEY `fme_group_id` (`live_id`) USING BTREE,
  CONSTRAINT `busi_live_dept_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='直播服务器组分配租户的中间表（一个FME组可以分配给多个租户，一对多）';