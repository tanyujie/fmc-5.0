CREATE TABLE `busi_dial_plan_rule_inbound`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `plan_uuid` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '业务uuid',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '内呼计划' ROW_FORMAT = Dynamic;


CREATE TABLE `busi_dial_plan_rule_outbound`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `plan_uuid` varchar(64) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '业务uuid',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '外呼计划' ROW_FORMAT = Dynamic;


ALTER TABLE `fcmdb`.`busi_fme` 
ADD COLUMN `admin_username` VARCHAR(32) NULL DEFAULT NULL COMMENT 'admin用户名' AFTER `password`,
ADD COLUMN `admin_password` VARCHAR(128) NULL DEFAULT NULL COMMENT 'admin密码' AFTER `admin_username`;
