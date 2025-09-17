CREATE TABLE `fcmdb`.`busi_operation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '操作用户id',
  `operator_name` varchar(255) DEFAULT NULL COMMENT '操作者姓名',
  `time` datetime DEFAULT NULL COMMENT '操作时间',
  `action_details` varchar(500) DEFAULT NULL COMMENT '操作的具体细节',
  `action_result` int(10) DEFAULT NULL COMMENT '操作的结果，1=成功、2=失败',
  `ip` varchar(255) DEFAULT NULL COMMENT '操作的用户的IP地址',
  `device_type` varchar(255) DEFAULT NULL COMMENT '操作的用户的设备信息，操作系统、浏览器',
  `history_conference_id` int(20) DEFAULT NULL COMMENT '操作会议的历史会议id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录表';