
ALTER TABLE `fcmdb`.`busi_mcu_smc3_template_conference` ADD COLUMN `up_cascade_id` bigint(20) NULL DEFAULT NULL COMMENT '上级ID' AFTER `presenter`;

ALTER TABLE `fcmdb`.`busi_mcu_smc3_template_conference` ADD COLUMN `up_cascade_mcu_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上级MCU类型' AFTER `up_cascade_id`;

ALTER TABLE `fcmdb`.`busi_mcu_smc3_template_conference` MODIFY COLUMN `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板会议名' AFTER `create_user_name`;

ALTER TABLE `fcmdb`.`busi_mcu_smc3_template_conference` MODIFY COLUMN `duration_time` int(11) NULL DEFAULT NULL COMMENT '会议时长单位分钟' AFTER `duration_enabled`;

ALTER TABLE `fcmdb`.`busi_mcu_tencent_template_conference` ADD COLUMN `up_cascade_index` int(11) NULL DEFAULT 0 COMMENT '级联索引' AFTER `up_cascade_type`;

ALTER TABLE `fcmdb`.`busi_mcu_smc3_template_conference`
    ADD COLUMN `confPresetParam` json NULL COMMENT '预设画面' AFTER `up_cascade_index`,
ADD COLUMN `tenant_id` varchar(13) CHARACTER SET tis620 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接入号' AFTER `confPresetParam`,
ADD COLUMN `cascade_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '多级会议模板ID' AFTER `tenant_id`,
ADD COLUMN `cascade_nodes` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '多级会议节点' AFTER `cascade_id`,
ADD COLUMN `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'NORMAL' COMMENT '模板会议类型：CASCADE,NORMAL' AFTER `cascade_nodes`;


ALTER TABLE `fcmdb`.`busi_mcu_smc3_template_conference` MODIFY COLUMN `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板会议名' AFTER `create_user_name`;
ALTER TABLE `fcmdb`.`busi_mcu_smc3_template_conference` MODIFY COLUMN `duration_time` int(11) NULL DEFAULT NULL COMMENT '会议时长单位分钟' AFTER `duration_enabled`;
ALTER TABLE `fcmdb`.`busi_mcu_smc3_template_conference` ADD COLUMN `up_cascade_id` bigint(20) NULL DEFAULT NULL COMMENT '上级ID' AFTER `presenter`;
ALTER TABLE `fcmdb`.`busi_mcu_smc3_template_conference` ADD COLUMN `up_cascade_mcu_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上级MCU类型' AFTER `up_cascade_id`;





ALTER TABLE `fcmdb`.`busi_smc_dept_template` ADD COLUMN `videoProtocol` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频协议' AFTER `enable_data_conf`;
ALTER TABLE `fcmdb`.`busi_smc_dept_template` ADD COLUMN `audioProtocol` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '音频协议' AFTER `videoProtocol`;
ALTER TABLE `fcmdb`.`busi_smc_dept_template` ADD COLUMN `videoResolution` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频分辨率' AFTER `audioProtocol`;