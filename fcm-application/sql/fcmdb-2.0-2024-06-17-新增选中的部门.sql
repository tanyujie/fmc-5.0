
CREATE TABLE `busi_picker`  (
                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                `dept_id` int(11) NULL DEFAULT NULL COMMENT '部门id',
                                `user_id` int(11) NULL DEFAULT NULL COMMENT '用户id',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;