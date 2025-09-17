ALTER TABLE `fcmdb`.`busi_conference_custom_button`
ADD COLUMN `mcu_type` VARCHAR(20) NOT NULL AFTER `sort`;

ALTER TABLE `fcmdb`.`busi_conference_custom_button`
DROP PRIMARY KEY,
ADD PRIMARY KEY (`id`, `mcu_type`);
