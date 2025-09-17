CREATE TABLE `busi_trans_server`  (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                      `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
                                      `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
                                      `user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务器用户名',
                                      `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务器密码',
                                      `ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务器ip',
                                      `port` int(11) NULL DEFAULT NULL COMMENT '端口号',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE INDEX `ip`(`ip`) USING BTREE COMMENT '服务器ip'
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '转流服务器' ROW_FORMAT = Dynamic;