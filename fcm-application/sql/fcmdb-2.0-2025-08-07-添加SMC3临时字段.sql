ALTER TABLE `fcmdb`.`busi_mcu_smc3_template_conference`
ADD COLUMN `cascade_nodes_temp` VARCHAR(2000) NULL DEFAULT NULL COMMENT '多级会议节点（临时）' AFTER `category`;
