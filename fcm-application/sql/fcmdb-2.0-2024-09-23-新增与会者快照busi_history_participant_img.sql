
CREATE TABLE `busi_history_participant_img`  (
                                                 `id` int(11) NOT NULL AUTO_INCREMENT,
                                                 `remote_party` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参会者url',
                                                 `co_space` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'coSpaceId',
                                                 `call_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'callID',
                                                 `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参会者名称',
                                                 `image_base64` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '图片',
                                                 `call_leg_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '参会者ID',
                                                 `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                                 `img_time` datetime NULL DEFAULT NULL COMMENT '图片时间',
                                                 `join_time` datetime NULL DEFAULT NULL COMMENT '入会时间',
                                                 `chairman` tinyint(1) NULL DEFAULT NULL COMMENT '是否主会场',
                                                 `history_id` int(11) NULL DEFAULT NULL COMMENT '历史会议id',
                                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

