ALTER TABLE `fcmdb`.`busi_history_conference`
    ADD COLUMN `minutes_doc` tinyint NULL DEFAULT 0 COMMENT '纪要文档：0:没有，1：有' AFTER `template_id`;
