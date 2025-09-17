SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for busi_smc
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc`;
CREATE TABLE `busi_smc`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '终端创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '终端修改时间',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统管理员用户',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统管理员密码',
  `meeting_username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议管理员用户名',
  `meeting_password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议管理员密码',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'smc显示名字',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备的IP地址',
  `sc_ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SC设备的IP地址',
  `status` int(11) NULL DEFAULT NULL COMMENT 'smc在线状态：1在线，2离线，3删除',
  `capacity` int(11) NULL DEFAULT NULL COMMENT 'FME容量',
  `spare_smc_id` bigint(20) NULL DEFAULT NULL COMMENT '备用SMC（本节点宕机后指向的备用节点）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'smc信息表' ROW_FORMAT = Dynamic;



-- ----------------------------
-- Table structure for busi_smc_appointment_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc_appointment_conference`;
CREATE TABLE `busi_smc_appointment_conference`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_id` int(11) NOT NULL COMMENT '部门id',
  `chairman_password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `conference_time_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议类型',
  `duration` int(10) NULL DEFAULT NULL COMMENT '时长',
  `guest_password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `schedule_start_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'utc时间',
  `subject` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主题',
  `vmr_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '虚拟号码',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '视频会议，语音会议',
  `max_participant_num` int(10) NULL DEFAULT NULL COMMENT '最大入会数量',
  `voice_active` int(1) NULL DEFAULT NULL COMMENT '静音入会',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `rate` int(10) NULL DEFAULT NULL COMMENT '带宽',
  `period_conference_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `duration_per_period_unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `period_unit_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `start_date` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `end_date` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `week_index_in_month_mode` int(10) NULL DEFAULT NULL,
  `conference_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议ID',
  `video_resolution` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '视频分辨率（MPI_1080P）',
  `svc_video_resolution` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'svc视频分辨率',
  `auto_mute` int(1) UNSIGNED NULL DEFAULT NULL COMMENT '自动静音',
  `support_live` int(1) NULL DEFAULT NULL COMMENT '直播',
  `support_record` int(1) NULL DEFAULT NULL COMMENT '录播',
  `amc_record` int(1) NULL DEFAULT NULL COMMENT '数据会议',
  `access_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议数字ID',
  `create_hy_admin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_user_id` int(11) NULL DEFAULT NULL COMMENT '本地创建者',
  `account_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议账号',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议用户',
  `token` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议token',
  `stage` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议状态',
  `guest_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '嘉宾链接',
  `chairman_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主席链接',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类',
  `active` int(1) NULL DEFAULT NULL COMMENT '会议是否活动',
  `legacy_id` int(10) NULL DEFAULT NULL,
  `organization_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组织名称',
  `master_terminal_id` int(10) NULL DEFAULT NULL COMMENT '主会场',
  `enable_data_conf` int(1) NULL DEFAULT NULL COMMENT '数据会议',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `conferenceId`(`conference_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_smc_appointment_conference
-- ----------------------------

-- ----------------------------
-- Table structure for busi_smc_appointment_conference_paticipant
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc_appointment_conference_paticipant`;
CREATE TABLE `busi_smc_appointment_conference_paticipant`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `conference_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议ID',
  `appointment_id` int(10) NULL DEFAULT NULL COMMENT '预约ID',
  `terminal_id` int(10) NULL DEFAULT NULL COMMENT '终端id',
  `weight` int(10) NULL DEFAULT NULL COMMENT '排序',
  `smcnumber` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'uri',
  `terminal_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '终端名字',
  `terminal_dept_id` int(10) NULL DEFAULT NULL COMMENT '终端部门id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_smc_appointment_conference_paticipant
-- ----------------------------

-- ----------------------------
-- Table structure for busi_smc_conference_state
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc_conference_state`;
CREATE TABLE `busi_smc_conference_state`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `conference_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL COMMENT 'smc会议id',
  `chooseId` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL COMMENT '选看者id',
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_smc_conference_state
-- ----------------------------

-- ----------------------------
-- Table structure for busi_smc_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc_dept`;
CREATE TABLE `busi_smc_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '分配给的租户',
  `smc_type` int(11) NULL DEFAULT NULL COMMENT '1单节点，100集群',
  `smc_id` bigint(20) NULL DEFAULT NULL COMMENT '当smc_type为1是，指向busi_smc的id字段，为100指向busi_smc_cluster的id字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_smc_dept
-- ----------------------------

-- ----------------------------
-- Table structure for busi_smc_dept_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc_dept_conference`;
CREATE TABLE `busi_smc_dept_conference`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_id` int(11) NOT NULL COMMENT '部门id',
  `smc_conference_id` varchar(70) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门smc会议关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_smc_dept_conference
-- ----------------------------

-- ----------------------------
-- Table structure for busi_smc_dept_template
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc_dept_template`;
CREATE TABLE `busi_smc_dept_template`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_id` int(11) NOT NULL COMMENT '部门id',
  `smc_template_id` varchar(70) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板ID',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `template_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `duration` int(10) NOT NULL COMMENT '会议时长分钟数',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议类型',
  `rate` int(10) NULL DEFAULT NULL COMMENT '带宽',
  `support_live` tinyint(1) NULL DEFAULT NULL COMMENT '直播',
  `support_record` tinyint(1) NULL DEFAULT NULL COMMENT '录制',
  `amc_record` tinyint(1) NULL DEFAULT NULL COMMENT '数据会议',
  `auto_mute` tinyint(1) NULL DEFAULT NULL COMMENT '自动静音',
  `chairman_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主席密码',
  `guest_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '嘉宾密码',
  `vmr_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '虚拟号码',
  `max_participant_num` int(10) NULL DEFAULT NULL COMMENT '最大入会数',
  `master_terminal_id` int(10) NULL DEFAULT NULL COMMENT '主会场ID',
  `enable_data_conf` int(1) NULL DEFAULT NULL COMMENT '数据会议',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 133 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门smc模板关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_smc_dept_template
-- ----------------------------

-- ----------------------------
-- Table structure for busi_smc_history_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc_history_conference`;
CREATE TABLE `busi_smc_history_conference`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `conference_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会议id',
  `dept_id` int(10) NULL DEFAULT NULL COMMENT '部门ID',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `subject` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主题',
  `conference_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会议数字ID',
  `end_status` int(255) NULL DEFAULT NULL COMMENT '是否结束1结束2否',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '会议结束时间',
  `template_id` int(10) NULL DEFAULT NULL COMMENT '模板ID',
  `conference_avc_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '音视频会议类型',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '会议开始时间',
  `duration` int(10) NULL DEFAULT NULL COMMENT '会议时长',
  `participant_num` int(10) NULL DEFAULT 0 COMMENT '与会者数量',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `conferenceIdIndex`(`conference_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 325 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_smc_history_conference
-- ----------------------------

-- ----------------------------
-- Table structure for busi_smc_mulitpic
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc_mulitpic`;
CREATE TABLE `busi_smc_mulitpic`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `conference_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL COMMENT 'smc会议id',
  `mulitpic` json NOT NULL COMMENT '布局',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_smc_mulitpic
-- ----------------------------

-- ----------------------------
-- Table structure for busi_smc_template_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_smc_template_conference`;
CREATE TABLE `busi_smc_template_conference`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `smc_template_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'smc模板ID',
  `conference_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL COMMENT 'smc会议ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 515 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci COMMENT = 'smc模板会议关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_smc_template_conference
-- ----------------------------

-- ----------------------------
-- Table structure for busi_tele
-- ----------------------------
DROP TABLE IF EXISTS `busi_tele`;
CREATE TABLE `busi_tele`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '终端创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '终端修改时间',
  `admin_username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'admin用户名',
  `admin_password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'admin密码',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'tele显示名字',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备的IP地址',
  `port` int(11) NULL DEFAULT 9443 COMMENT 'tele端口',
  `status` int(11) NULL DEFAULT NULL COMMENT 'FME在线状态：1在线，2离线，3删除',
  `capacity` int(11) NULL DEFAULT NULL COMMENT 'FME容量',
  `spare_tele_id` bigint(20) NULL DEFAULT NULL COMMENT '备用FME（本节点宕机后指向的备用节点）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ip`(`ip`, `port`) USING BTREE,
  INDEX `spare_tele_id`(`spare_tele_id`) USING BTREE,
  CONSTRAINT `busi_tele_ibfk_1` FOREIGN KEY (`spare_tele_id`) REFERENCES `busi_tele` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'tele终端信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_tele
-- ----------------------------

-- ----------------------------
-- Table structure for busi_tele_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_tele_dept`;
CREATE TABLE `busi_tele_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '分配给的租户',
  `tele_type` int(11) NULL DEFAULT NULL COMMENT '1单节点，100集群',
  `tele_id` bigint(20) NULL DEFAULT NULL COMMENT '当smc_type为1是，指向busi_tele的id字段，为100指向busi_tele_cluster的id字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_tele_dept
-- ----------------------------

-- ----------------------------
-- Table structure for busi_tele_participant
-- ----------------------------
DROP TABLE IF EXISTS `busi_tele_participant`;
CREATE TABLE `busi_tele_participant`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `conference_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL COMMENT '会议名称',
  `participant_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL COMMENT '与会者民',
  `choose` int(11) NOT NULL DEFAULT 0 COMMENT '选中',
  `calltheroll` int(11) NOT NULL DEFAULT 0 COMMENT '点名',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `conference_number` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NULL DEFAULT NULL COMMENT '会议数字ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_tele_participant
-- ----------------------------
-- ----------------------------
-- Table structure for smc_template_terminal
-- ----------------------------
DROP TABLE IF EXISTS `smc_template_terminal`;
CREATE TABLE `smc_template_terminal`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `smc_template_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `terminal_id` int(11) NOT NULL,
  `terminal_dept_id` int(11) NOT NULL,
  `smcnumber` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `weight` int(10) NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1089 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of smc_template_terminal
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
