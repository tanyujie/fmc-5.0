ALTER TABLE `fcmdb`.`busi_conference_number_section`
ADD COLUMN `section_type` INT NULL DEFAULT 0 COMMENT '号段类型 0: 通用号段 2: 固定号段' AFTER `mcu_type`;
