ALTER TABLE `fcmdb`.`busi_mcu_zj_resource_template`
DROP INDEX `name_UNIQUE` ,
ADD UNIQUE INDEX `name_UNIQUE` USING BTREE (`name`, `mcu_zj_server_id`);
;
