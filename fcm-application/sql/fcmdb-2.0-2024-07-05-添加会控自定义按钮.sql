CREATE TABLE `fcmdb`.`busi_conference_custom_button` (
  `id` VARCHAR(50) NOT NULL COMMENT 'ID',
  `name` VARCHAR(50) NOT NULL COMMENT '名称',
  `sort` INT NOT NULL COMMENT '顺序',
  PRIMARY KEY (`id`))
COMMENT = '会议自定义按钮';
