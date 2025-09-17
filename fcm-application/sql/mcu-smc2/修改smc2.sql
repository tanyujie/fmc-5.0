ALTER TABLE `fcmdb`.`busi_mcu_smc2_template_conference`
    ADD COLUMN `up_cascade_index` int(11) NULL DEFAULT 0 COMMENT '级联索引' AFTER `up_cascade_mcu_type`,
ADD COLUMN `tenant_id` varchar(13) CHARACTER SET tis620 COLLATE tis620_thai_ci NULL DEFAULT NULL COMMENT '接入号' AFTER `up_cascade_index`;