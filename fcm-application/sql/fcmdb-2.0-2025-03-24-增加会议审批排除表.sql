CREATE TABLE `busi_conference_approval_exclude` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `type` int(11) NOT NULL COMMENT '类型 0：部门 1：用户',
    `exclude_id` bigint(20) NOT NULL COMMENT '排除ID 类型为部门时为部门ID，类型为用户时为用户ID',
    `create_by` varchar(30) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `type_id` (`type`,`exclude_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='会议审批排除表';
