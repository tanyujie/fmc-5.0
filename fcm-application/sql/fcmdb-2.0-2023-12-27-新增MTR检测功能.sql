CREATE TABLE `fcmdb`.`busi_mtr` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `source_ip` VARCHAR(100) NOT NULL COMMENT '源地址',
  `target_ip` VARCHAR(100) NOT NULL COMMENT '目标地址',
  `times` INT NOT NULL COMMENT '次数',
  `file_name` VARCHAR(100) NULL COMMENT '文件名',
  `pid` VARCHAR(20) NULL COMMENT 'PID',
  `status` INT NOT NULL DEFAULT 0 COMMENT '状态：0：检测中 1：检测完成 2：检测错误',
  `name` VARCHAR(50) NULL COMMENT '名称',
  `create_by` VARCHAR(50) NULL COMMENT '创建用户',
  `create_time` DATETIME NULL COMMENT '创建时间',
  `update_time` DATETIME NULL COMMENT '更新时间',
  `delete_time` DATETIME NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = 'MTR检测记录表';
