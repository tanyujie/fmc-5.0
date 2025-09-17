CREATE TABLE `busi_ops_resource`  (
                                      `id` int(11) NOT NULL AUTO_INCREMENT,
                                      `sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '序列号',
                                      `free_minutes` int(11) NULL DEFAULT 10 COMMENT '免费时长',
                                      `conference_number` int(11) NULL DEFAULT 1 COMMENT '会议个数',
                                      `purchase_duration` int(11) NULL DEFAULT 0 COMMENT '购买时长',
                                      `purchase_quantity` int(11) NULL DEFAULT 0 COMMENT '购买个数',
                                      `using_number` int(11) NULL DEFAULT 0 COMMENT '正在使用会议个数',
                                      `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
                                      `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                      `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                      `purchase_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '购买类型',
                                      `enable_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '启用类型TIME,NUMBER',
                                      `used_time` int(11) NULL DEFAULT 0 COMMENT '已使用时间',
                                      `mcu_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'MCU类型',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ops资源表' ROW_FORMAT = Dynamic;