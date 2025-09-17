CREATE TABLE `fcmdb`.`busi_smart_room` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_name` VARCHAR(100) NOT NULL COMMENT '智慧办公房间名称',
  `room_status` INT NOT NULL COMMENT '房间状态 1：启用 2：停用',
  `room_type` INT NOT NULL DEFAULT 0 COMMENT '房间类型 0：会议室',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `create_by` VARCHAR(64) NOT NULL COMMENT '创建者',
  `update_time` DATETIME NULL COMMENT '更新时间',
  `update_by` VARCHAR(64) NULL COMMENT '更新者',
  `remark` VARCHAR(500) NULL COMMENT '备注',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `building` varchar(50) DEFAULT NULL COMMENT '楼宇',
  `floor` varchar(50) DEFAULT NULL COMMENT '楼层',
  `third_oa_type` int(11) DEFAULT '0' COMMENT '第三方OA类型 0：非第三方 1：企业微信 2：钉钉',
  `third_room_id` varchar(50) DEFAULT NULL COMMENT '第三方房间ID',
  `room_level` int(11) DEFAULT '1' COMMENT '会议室等级：0:任何人 1:账号登陆  2:人脸识别',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户Id',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `room_name` (`room_name` ASC, `building` ASC, `floor` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '智慧办公房间表';

CREATE TABLE `fcmdb`.`busi_smart_room_dept` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dept_id` BIGINT(20) NOT NULL COMMENT '部门ID',
  `room_id` BIGINT(20) NOT NULL COMMENT '房间ID',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `create_by` VARCHAR(64) NOT NULL COMMENT '创建者',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `dept_smart_room` (`dept_id` ASC, `room_id` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '房间分配租户的中间表（一个房间可以分配给多个租户，一对多）';

CREATE TABLE `fcmdb`.`busi_smart_room_book` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_id` BIGINT(20) NOT NULL COMMENT '房间ID',
  `book_name` VARCHAR(100) NOT NULL COMMENT '预约名',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `book_status` INT NOT NULL DEFAULT 0 COMMENT '预约状态 0：预约中 1：取消预约 2：结束预约',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `create_by` VARCHAR(64) NOT NULL COMMENT '创建者',
  `remark` VARCHAR(500) NULL COMMENT '备注',
  `mcu_type` VARCHAR(11) NULL DEFAULT NULL COMMENT 'mcu类型',
  `appointment_conference_id` INT(11) NULL DEFAULT NULL COMMENT '预约会议Id',
  `extend_minutes` INT(11) NULL DEFAULT NULL COMMENT '延长时间',
  PRIMARY KEY (`id`),
  INDEX `room_id` (`room_id` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '房间预约表';

CREATE TABLE `fcmdb`.`busi_smart_room_doorplate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '名称',
  `sn` VARCHAR(100) NOT NULL COMMENT '序列号',
  `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `mqtt_online_status` INT DEFAULT '2' COMMENT 'mqtt连接状态：1在线，2离线',
  `app_version_code` VARCHAR(50) DEFAULT NULL COMMENT 'APP版本号',
  `app_version_name` VARCHAR(50) DEFAULT NULL COMMENT 'APP版本名',
  `app_type` VARCHAR(64) DEFAULT NULL COMMENT '软件类型：与busi_terminal_upgrade中terminal_type等同',
  `connect_ip` VARCHAR(100) NULL COMMENT '连接IP',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` VARCHAR(64) NOT NULL COMMENT '创建者',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sn` (`sn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间门牌表';

CREATE TABLE `fcmdb`.`busi_smart_room_device_map` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_id` BIGINT(20) NOT NULL COMMENT '房间ID',
  `device_id` BIGINT(20) NOT NULL COMMENT '设备ID 当设备类型为0：电子门牌 时，设备ID为电子门牌ID 当设备类型为1：物联网网关时，设备ID为物联网网关ID',
  `device_type` INT NOT NULL COMMENT '设备类型 0：电子门牌 1：物联网网关 2：物联网设备  999：其它设备',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `create_by` VARCHAR(64) NOT NULL COMMENT '创建者',
  PRIMARY KEY (`id`),
  INDEX `room_id` (`room_id` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '房间设备关联表';

CREATE TABLE `fcmdb`.`busi_smart_room_doorplate_register` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sn` VARCHAR(100) NOT NULL COMMENT '序列号',
  `ip` VARCHAR(100) NOT NULL COMMENT 'IP地址',
  `app_version_code` VARCHAR(50) NULL COMMENT 'APP版本号',
  `app_version_name` VARCHAR(50) NULL COMMENT 'APP版本号',
  `connect_ip` VARCHAR(100) NULL COMMENT '连接IP',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `sn` (`sn` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '房间门牌注册表';

CREATE TABLE `fcmdb`.`busi_smart_room_device_classify` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `device_type` INT NOT NULL COMMENT '设备类型（999：其它设备）',
  `device_classify_name` VARCHAR(50) NOT NULL COMMENT '设备分类名',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `create_by` VARCHAR(64) NOT NULL COMMENT '创建者',
  `update_time` DATETIME NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` VARCHAR(64) NULL DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `device_classify_name` (`device_classify_name` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '房间设备分类表';

CREATE TABLE `fcmdb`.`busi_smart_room_device` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bind_id` varchar(50) DEFAULT NULL COMMENT '绑定Id',
  `device_name` VARCHAR(100) NOT NULL COMMENT '设备名',
  `device_type` INT NOT NULL COMMENT '设备类型 0：电子门牌 1：物联网网关 2：物联网设备 999：其它设备',
  `device_classify` BIGINT(20) NULL COMMENT '设备分类 （当设备类型为999：其它设备时，设备分类为设备分类表中id）',
  `status` INT NOT NULL DEFAULT 0 COMMENT '状态',
  `online_status` INT DEFAULT NULL COMMENT '在线状态：1在线，2离线',
  `brand` VARCHAR(50) NULL COMMENT '品牌',
  `device_model` VARCHAR(100) NULL COMMENT '设备型号',
  `applied_time` DATETIME NULL COMMENT '投用时间',
  `hardware_version` VARCHAR(50) NULL COMMENT '硬件版本',
  `software_version` VARCHAR(50) NULL COMMENT '软件版本',
  `remark` VARCHAR(100) NULL COMMENT '备注',
  `create_time` DATETIME NULL COMMENT '创建时间',
  `create_by` VARCHAR(64) NULL COMMENT '创建者',
  `update_time` DATETIME NULL COMMENT '更新时间',
  `update_by` VARCHAR(64) NULL COMMENT '更新者',
  `lot_id` BIGINT(20) DEFAULT NULL COMMENT '物联网关ID (当日设备类型为物联网设备时)',
  `lot_channel` INT DEFAULT NULL COMMENT '物联网关通道 (设备连上物联网关的通道号)',
  `lot_device_type` INT DEFAULT NULL COMMENT '物联网设备类型 0：未知 1：时序电源',
  `details` JSON DEFAULT NULL COMMENT '详情（更多设备信息）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '房间设备表';

CREATE TABLE `fcmdb`.`busi_smart_room_third_oa` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `oa_type` INT NOT NULL COMMENT 'OA类型 0：非第三方 1：企业微信 2：钉钉',
  `host` VARCHAR(100) NOT NULL COMMENT '服务器地址',
  `port` INT NOT NULL DEFAULT 443 COMMENT '端口',
  `suite_id` VARCHAR(100) NULL COMMENT '第三方ID',
  `suite_secret` VARCHAR(100) NULL COMMENT '第三方secret',
  `suite_access_token` VARCHAR(1000) NULL COMMENT '第三方access token',
  `suite_access_token_expired_time` DATETIME NULL COMMENT '第三方access token过期时间',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `create_by` VARCHAR(64) NOT NULL COMMENT '创建者',
  `update_time` DATETIME NULL COMMENT '更新时间',
  `update_by` VARCHAR(64) NULL COMMENT '更新者',
  `remark` VARCHAR(500) NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '智慧办公第三方OA表';

CREATE TABLE `fcmdb`.`busi_smart_room_lot` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `lot_name` VARCHAR(100) NOT NULL COMMENT '网关名称',
  `lot_type` INT NOT NULL DEFAULT 0 COMMENT '网关类型 0：未指定 1：串口网关',
  `lot_position` VARCHAR(100) NULL COMMENT '网关位置',
  `mqtt_online_status` INT DEFAULT '2' COMMENT 'mqtt连接状态：1在线，2离线',
  `brand` VARCHAR(50) NULL COMMENT '品牌',
  `lot_model` VARCHAR(100) NULL COMMENT '网关型号',
  `client_id` VARCHAR(50) NOT NULL COMMENT '客户端ID',
  `pub_topic` VARCHAR(100) NOT NULL COMMENT '发布主题',
  `sub_topic` VARCHAR(100) NOT NULL COMMENT '订阅主题',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `create_by` VARCHAR(64) NOT NULL COMMENT '创建者',
  `update_time` DATETIME NULL COMMENT '更新时间',
  `update_by` VARCHAR(64) NULL COMMENT '更新者',
  `remark` VARCHAR(500) NULL COMMENT '备注',
  `details` JSON DEFAULT NULL COMMENT '详情（更多网关信息）',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `lot_name` (`lot_name` ASC, `lot_position` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '智慧办公物联网关表';

CREATE TABLE `fcmdb`.`busi_smart_room_book_sign_in` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id` BIGINT(20) NOT NULL COMMENT '预约ID',
  `user_id` BIGINT(20) NULL COMMENT '用户ID',
  `user_name` VARCHAR(50) NOT NULL COMMENT '用户名',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '房间预约签到表';

CREATE TABLE `busi_smart_room_participant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id` bigint(20) NOT NULL COMMENT '预约ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) NOT NULL COMMENT '用户名称',
  `sign_in_time` datetime DEFAULT NULL COMMENT '签到时间',
  `sign_in_code` varchar(50) DEFAULT NULL COMMENT '签到码',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_by` varchar(64) NOT NULL COMMENT '创建者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='智慧办公房间参与人表';

