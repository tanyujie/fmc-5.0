ALTER TABLE `fcmdb`.`busi_terminal` 
MODIFY COLUMN `number` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备号，设备唯一标识（如果是sfbc终端，则对应凭据）' AFTER `ip`;