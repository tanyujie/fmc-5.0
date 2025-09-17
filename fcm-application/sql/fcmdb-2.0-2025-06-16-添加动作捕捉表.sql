CREATE TABLE `fcmdb`.`busi_conference_motion_capture` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `history_conference_id` BIGINT(20) NOT NULL COMMENT '历史会议ID',
  `co_space` VARCHAR(40) NOT NULL COMMENT 'coSpaceId',
  `motion` VARCHAR(100) NOT NULL COMMENT '动作',
  `image_name` VARCHAR(45) NOT NULL COMMENT '图片名',
  `create_time` DATETIME NULL COMMENT '创建时间',
  PRIMARY KEY (`id`))
COMMENT = '会议动作捕捉表';
