
ALTER TABLE `fcmdb`.`busi_template_conference`
    ADD COLUMN `presenter` int(11) NULL COMMENT '主持人终端id' AFTER `is_auto_create_stream_url`;