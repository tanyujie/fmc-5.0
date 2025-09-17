CREATE TABLE `busi_free_switch_cluster` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `name` varchar(32) DEFAULT NULL COMMENT '集群组名，最长32',
  `description` varchar(128) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='FreeSwitch集群'
;

CREATE TABLE `busi_free_switch_cluster_map` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `cluster_id` bigint(20) DEFAULT NULL COMMENT 'FreeSwitch集群ID',
  `free_switch_id` bigint(20) DEFAULT NULL COMMENT 'FreeSwitch的ID',
  `weight` int(11) DEFAULT NULL COMMENT '节点在集群中的权重值',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `group_id` (`cluster_id`,`free_switch_id`,`weight`) USING BTREE,
  UNIQUE KEY `cluster_id` (`cluster_id`,`free_switch_id`) USING BTREE,
  KEY `free_switch_id` (`free_switch_id`) USING BTREE,
  CONSTRAINT `busi_free_switch_cluster_map_ibfk_1` FOREIGN KEY (`cluster_id`) REFERENCES `busi_free_switch_cluster` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_free_switch_cluster_map_ibfk_2` FOREIGN KEY (`free_switch_id`) REFERENCES `busi_free_switch` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='FreeSwitch-终端组中间表（多对多）'
;

ALTER TABLE `fcmdb`.`busi_free_switch_dept`
ADD COLUMN `fcm_type` INT NULL DEFAULT 1 COMMENT '1单节点，100集群' AFTER `server_id`,
CHANGE COLUMN `server_id` `server_id` BIGINT(20) NULL DEFAULT NULL COMMENT '服务器id(当fcm_type为1是，指向busi_free_switch的id字段，为100指向busi_free_switch_cluster的id字段' ;


ALTER TABLE `fcmdb`.`busi_terminal`
DROP FOREIGN KEY `busi_terminal_ibfk_4`;
ALTER TABLE `fcmdb`.`busi_terminal`
DROP INDEX `fs_server_id` ;
