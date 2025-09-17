ALTER TABLE `fcmdb`.`busi_conference_appointment`
ADD COLUMN `approval_id` BIGINT(20) NULL DEFAULT NULL COMMENT '审批ID' AFTER `create_by`,
ADD COLUMN `room_book_id` BIGINT(20) NULL DEFAULT NULL COMMENT '房间预约ID' AFTER `approval_id`;

ALTER TABLE `fcmdb`.`busi_mcu_zj_conference_appointment`
ADD COLUMN `approval_id` BIGINT(20) NULL DEFAULT NULL COMMENT '审批ID' AFTER `create_by`,
ADD COLUMN `room_book_id` BIGINT(20) NULL DEFAULT NULL COMMENT '房间预约ID' AFTER `approval_id`;

ALTER TABLE `fcmdb`.`busi_mcu_plc_conference_appointment`
ADD COLUMN `approval_id` BIGINT(20) NULL DEFAULT NULL COMMENT '审批ID' AFTER `create_by`,
ADD COLUMN `room_book_id` BIGINT(20) NULL DEFAULT NULL COMMENT '房间预约ID' AFTER `approval_id`;

ALTER TABLE `fcmdb`.`busi_mcu_kdc_conference_appointment`
ADD COLUMN `approval_id` BIGINT(20) NULL DEFAULT NULL COMMENT '审批ID' AFTER `create_by`,
ADD COLUMN `room_book_id` BIGINT(20) NULL DEFAULT NULL COMMENT '房间预约ID' AFTER `approval_id`;

ALTER TABLE `fcmdb`.`busi_mcu_smc3_conference_appointment`
ADD COLUMN `approval_id` BIGINT(20) NULL DEFAULT NULL COMMENT '审批ID' AFTER `create_by`,
ADD COLUMN `room_book_id` BIGINT(20) NULL DEFAULT NULL COMMENT '房间预约ID' AFTER `approval_id`;

ALTER TABLE `fcmdb`.`busi_mcu_smc2_conference_appointment`
ADD COLUMN `approval_id` BIGINT(20) NULL DEFAULT NULL COMMENT '审批ID' AFTER `create_by`,
ADD COLUMN `room_book_id` BIGINT(20) NULL DEFAULT NULL COMMENT '房间预约ID' AFTER `approval_id`;

ALTER TABLE `fcmdb`.`busi_mcu_tencent_conference_appointment`
ADD COLUMN `approval_id` BIGINT(20) NULL DEFAULT NULL COMMENT '审批ID' AFTER `create_by`,
ADD COLUMN `room_book_id` BIGINT(20) NULL DEFAULT NULL COMMENT '房间预约ID' AFTER `approval_id`;

ALTER TABLE `fcmdb`.`busi_mcu_ding_conference_appointment`
ADD COLUMN `approval_id` BIGINT(20) NULL DEFAULT NULL COMMENT '审批ID' AFTER `create_by`,
ADD COLUMN `room_book_id` BIGINT(20) NULL DEFAULT NULL COMMENT '房间预约ID' AFTER `approval_id`;

ALTER TABLE `fcmdb`.`busi_mcu_hwcloud_conference_appointment`
ADD COLUMN `approval_id` BIGINT(20) NULL DEFAULT NULL COMMENT '审批ID' AFTER `create_by`,
ADD COLUMN `room_book_id` BIGINT(20) NULL DEFAULT NULL COMMENT '房间预约ID' AFTER `approval_id`;
