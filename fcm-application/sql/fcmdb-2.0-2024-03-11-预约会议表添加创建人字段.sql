ALTER TABLE `fcmdb`.`busi_conference_appointment`
ADD COLUMN `create_by` VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人';

ALTER TABLE `fcmdb`.`busi_mcu_ding_conference_appointment`
ADD COLUMN `create_by` VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人';

ALTER TABLE `fcmdb`.`busi_mcu_hwcloud_conference_appointment`
ADD COLUMN `create_by` VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人';

ALTER TABLE `fcmdb`.`busi_mcu_kdc_conference_appointment`
ADD COLUMN `create_by` VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人';

ALTER TABLE `fcmdb`.`busi_mcu_plc_conference_appointment`
ADD COLUMN `create_by` VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人';

ALTER TABLE `fcmdb`.`busi_mcu_smc2_conference_appointment`
ADD COLUMN `create_by` VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人';

ALTER TABLE `fcmdb`.`busi_mcu_smc3_conference_appointment`
ADD COLUMN `create_by` VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人';

ALTER TABLE `fcmdb`.`busi_mcu_tencent_conference_appointment`
ADD COLUMN `create_by` VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人';

ALTER TABLE `fcmdb`.`busi_mcu_zj_conference_appointment`
ADD COLUMN `create_by` VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人';

ALTER TABLE `fcmdb`.`busi_smc_appointment_conference`
ADD COLUMN `create_by` VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人';

ALTER TABLE `fcmdb`.`busi_smc2_appointment_conference`
ADD COLUMN `create_by` VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人';

ALTER TABLE `fcmdb`.`busi_tencent_conference_appointment`
ADD COLUMN `create_by` VARCHAR(30) NULL DEFAULT NULL COMMENT '创建人';
