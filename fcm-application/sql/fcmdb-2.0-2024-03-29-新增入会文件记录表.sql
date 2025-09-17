CREATE TABLE `busi_meeting_file`  (
                                      `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                      `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名称',
                                      `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件url',
                                      `participant_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '入会名称',
                                      `codec_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '转码状态：0：未转码，1：已转码',
                                      `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件类型',
                                      `file_size` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件大小',
                                      `create_time` datetime(0) NOT NULL COMMENT '创建时间',
                                      `create_user_id` int(10) NULL DEFAULT NULL COMMENT '创建人',
                                      `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
                                      `file_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '文件状态：0：删除，1:正常',
                                      `dept_id` int(11) NULL DEFAULT NULL COMMENT '归属部门id',
                                      `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
                                      `out_file` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '转码文件路径',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '入会文件' ROW_FORMAT = Dynamic;