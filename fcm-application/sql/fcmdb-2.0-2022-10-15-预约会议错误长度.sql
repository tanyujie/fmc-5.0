ALTER TABLE `busi_conference_appointment`
MODIFY COLUMN `start_failed_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '启动失败原因记录' AFTER `is_start`;
