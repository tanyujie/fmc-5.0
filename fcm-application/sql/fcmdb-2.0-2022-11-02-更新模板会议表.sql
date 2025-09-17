ALTER TABLE `fcmdb`.`busi_template_conference`
ADD COLUMN `is_auto_create_stream_url` int(11) not null DEFAULT '0' COMMENT '是否自动创建直播URL：1是，2否' AFTER `duration_time`;
