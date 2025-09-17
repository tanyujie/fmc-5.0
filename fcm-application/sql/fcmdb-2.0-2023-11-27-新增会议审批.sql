CREATE TABLE `fcmdb`.`busi_conference_approval` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `appointment_conference_id` bigint(20) NOT NULL COMMENT '预约会议ID',
  `mcu_type` VARCHAR(20) NOT NULL COMMENT 'MCU类型',
  `conference_name` varchar(128) NOT NULL COMMENT '会议名',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  `approval_status` int(11) NOT NULL DEFAULT '0' COMMENT '审批状态 0：待审批 1：审批通过 2：审批不通过 3：会议删除',
  `approval_fail_reason` varchar(100) DEFAULT NULL COMMENT '审批未通过原因',
  `approval_user_id` bigint(20) DEFAULT NULL COMMENT '审批用户ID',
  `approval_by` varchar(30) DEFAULT NULL COMMENT '审批人',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `create_user_id` varchar(30) DEFAULT NULL COMMENT '创建用户ID',
  `create_by` varchar(30) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `conference_detail` json DEFAULT NULL COMMENT '会议详情',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议审批';
