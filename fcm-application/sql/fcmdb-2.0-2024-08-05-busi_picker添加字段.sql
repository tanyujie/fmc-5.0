ALTER TABLE `fcmdb`.`busi_picker`
    ADD COLUMN `dept_type` varchar(255) NULL COMMENT '控制类型' AFTER `user_id`;