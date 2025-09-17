CREATE TABLE `fcmdb`.`busi_info_display` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(64) NOT NULL COMMENT '名称',
  `dept_id` BIGINT(20) NOT NULL COMMENT '部门ID',
  `type` INT NOT NULL COMMENT '类型 1：开机信息 2：一般信息',
  `display_type` INT NOT NULL COMMENT '显示类型 1：图片 2：视频',
  `url_data` VARCHAR(5000) NOT NULL COMMENT '地址或数据',
  `push_type` INT NOT NULL COMMENT '推送类型 1：终端 2：选中会议室',
  `push_object` INT NOT NULL COMMENT '推送对象 1：本部门终端 2：本部门及下级部门终端 3：自定义选择',
  `push_terminal_ids` VARCHAR(5000) NULL COMMENT '推送终端',
  `last_push_time` datetime DEFAULT NULL COMMENT '最后推送时间',
  `status` INT NOT NULL COMMENT '状态 1：启用 2：未启用',
  `create_by` varchar(30) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(30) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `dept_id`(`dept_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '信息展示';
