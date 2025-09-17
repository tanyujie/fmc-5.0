/*
 Navicat Premium Data Transfer

 Source Server         : 本地MySQL
 Source Server Type    : MySQL
 Source Server Version : 50721
 Source Host           : localhost:3306
 Source Schema         : fcmdb

 Target Server Type    : MySQL
 Target Server Version : 50721
 File Encoding         : 65001

 Date: 20/04/2021 18:58:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for busi_call_leg_profile
-- ----------------------------
DROP TABLE IF EXISTS `busi_call_leg_profile`;
CREATE TABLE `busi_call_leg_profile`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `call_leg_profile_uuid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '入会方案对应的fme里面的记录的uuid',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '是否是默认入会方案:1是，2否',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '入会方案归属部门',
  `fme_id` bigint(20) NULL DEFAULT NULL COMMENT '入会方案归属的fme',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `call_leg_profile_uuid`(`call_leg_profile_uuid`) USING BTREE,
  INDEX `is_default`(`type`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  INDEX `fme_id`(`fme_id`) USING BTREE,
  CONSTRAINT `busi_call_leg_profile_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 142 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '入会方案配置，控制参会者进入会议的方案' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_call_leg_profile
-- ----------------------------
INSERT INTO `busi_call_leg_profile` VALUES (24, '2021-02-20 14:43:50', NULL, '0f3107b8-1a0d-4906-8ca1-ba45e150975d', 1, 200, 36);
INSERT INTO `busi_call_leg_profile` VALUES (25, '2021-02-20 14:44:13', NULL, '9c84786b-71e2-410c-b13f-f77b2815552e', 1, 201, 37);
INSERT INTO `busi_call_leg_profile` VALUES (26, '2021-02-20 14:44:24', NULL, '4a159919-4320-4d09-b567-c41abacc333f', 1, 202, 38);
INSERT INTO `busi_call_leg_profile` VALUES (27, '2021-02-20 14:44:45', NULL, '4e43ace4-33a4-4133-986c-94bb1237ac97', 1, 203, 39);
INSERT INTO `busi_call_leg_profile` VALUES (28, '2021-02-20 14:45:00', NULL, '18cdc91c-f161-420d-bbde-914b0d273a88', 1, 204, 40);
INSERT INTO `busi_call_leg_profile` VALUES (29, '2021-02-20 14:45:14', NULL, '9cda8d51-0c62-4fd2-97c9-cb8087d84904', 1, 205, 41);
INSERT INTO `busi_call_leg_profile` VALUES (30, '2021-02-20 14:45:24', NULL, 'a3b2b931-4b89-4efd-a758-991220dd07e2', 1, 206, 42);
INSERT INTO `busi_call_leg_profile` VALUES (31, '2021-02-20 14:45:48', NULL, '8fcfce0a-ca31-40b4-90fa-45ac822077df', 1, 207, 44);
INSERT INTO `busi_call_leg_profile` VALUES (32, '2021-02-20 14:45:59', '2021-02-23 19:11:09', '1bb33674-d84b-483b-a023-5e6e74daae64', 1, 208, 45);
INSERT INTO `busi_call_leg_profile` VALUES (43, '2021-03-26 16:42:08', NULL, 'cca6ac97-3658-4bea-a87d-f99de7772f81', NULL, 212, 50);
INSERT INTO `busi_call_leg_profile` VALUES (44, '2021-03-26 16:42:08', NULL, 'b192476b-249a-4988-91d4-646117556ca0', NULL, 214, 51);
INSERT INTO `busi_call_leg_profile` VALUES (45, '2021-03-26 16:42:08', NULL, '371c2f54-8854-42e5-8f7b-b725bace7630', NULL, 213, 51);
INSERT INTO `busi_call_leg_profile` VALUES (46, '2021-03-26 16:42:08', NULL, '82a3d03b-b939-4f0a-a377-57d1e81e2144', NULL, 213, 51);
INSERT INTO `busi_call_leg_profile` VALUES (47, '2021-03-26 16:42:08', NULL, 'db11853f-fcd9-4f27-b7e4-48e217aecb3f', NULL, 214, 51);
INSERT INTO `busi_call_leg_profile` VALUES (48, '2021-03-26 16:42:08', NULL, '16444640-db17-4ceb-a9f9-ba3684ccd26b', NULL, 212, 51);
INSERT INTO `busi_call_leg_profile` VALUES (49, '2021-03-29 16:26:42', NULL, 'fd425b74-bb5e-45d4-8afa-ebc2b9aa11ab', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (50, '2021-03-29 16:28:09', NULL, '02295bac-d050-4ddb-a30f-e3f80cdab142', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (51, '2021-03-29 16:29:05', NULL, 'a49192b6-3fe9-4db9-b1eb-bf730c1bd6bc', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (52, '2021-03-29 16:29:17', NULL, 'bd6e4187-622b-4520-97c2-cced16a06b78', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (53, '2021-03-29 16:29:28', NULL, 'cd5faa17-d97b-4efb-9da8-d910a3220580', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (54, '2021-03-29 16:29:40', NULL, 'd06a2d02-cc02-4c06-a6a6-59f8b6b17e5a', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (55, '2021-03-29 16:29:49', NULL, '4f2fd5a1-969f-4ed6-9f2a-a0491892acf2', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (56, '2021-03-29 16:29:58', NULL, '179d2f7c-48c7-49fc-a9b9-01c5e10b0071', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (57, '2021-03-29 16:30:06', NULL, '3e11f665-b4e2-4a97-9725-21303b31807f', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (58, '2021-03-29 16:30:14', NULL, '7c6c2739-6cbb-465c-8799-ec96db0cadd0', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (59, '2021-03-29 16:30:25', NULL, '7c624b2f-1a4b-44a7-891c-b5790eac2d11', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (60, '2021-03-29 16:30:48', NULL, 'dbef809f-bf38-4501-95d3-5909c6bc0cbc', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (61, '2021-03-29 16:31:03', NULL, 'bc556ad7-03de-4bd6-b47b-5959c6080f16', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (62, '2021-03-29 16:31:16', NULL, 'b5dedd07-8021-42c1-b1e4-62b32968b6b1', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (63, '2021-03-29 16:31:27', NULL, '1f97bc7b-ac05-4f1c-9389-63fb085ed731', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (64, '2021-03-29 16:32:00', NULL, 'ff0380e1-60d1-4be2-9622-4471d85d978a', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (65, '2021-03-29 16:32:10', NULL, 'b2a1ad0f-3e5f-4a89-80fe-dfde07658f46', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (66, '2021-03-29 16:32:19', NULL, 'a267be7c-ebd2-4bc8-8d05-f82769a841d2', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (67, '2021-03-29 16:32:28', NULL, '44dd4861-2eae-443e-ba1d-cd0913da7f3c', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (68, '2021-03-29 16:32:36', NULL, 'cae35bf4-559d-493c-ba59-a1ecadaa1abe', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (69, '2021-03-29 16:32:46', NULL, '848b1722-f422-452a-a63b-1f61eaf08f8d', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (70, '2021-03-29 16:32:55', NULL, 'a0b4ec2e-7d5b-4c4e-91c0-336cb4ac1917', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (71, '2021-03-29 16:33:06', NULL, 'b3f33f37-21c2-4958-b15c-e050f348ed03', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (72, '2021-03-29 16:33:18', NULL, '755a7aa7-5211-420b-b397-e5ad23edcfeb', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (73, '2021-03-29 16:33:28', NULL, 'cd192ea7-53a0-4b84-a043-74db88b879fb', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (74, '2021-03-29 16:33:40', NULL, 'a01009a7-71d3-4c74-a5bf-abdf75e117aa', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (75, '2021-03-29 16:33:50', NULL, 'bf6cf5c3-d766-4734-9160-76afdd3274b2', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (76, '2021-03-29 16:33:58', NULL, '21b6d917-841f-4283-bafd-a7a7b939eda6', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (77, '2021-03-29 16:34:20', NULL, 'd37cbbbf-4346-4894-8248-52106962d90d', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (78, '2021-03-29 16:34:36', NULL, '9801b184-5bf2-41b5-b2af-7c7f0301ce0c', NULL, 214, 50);
INSERT INTO `busi_call_leg_profile` VALUES (79, '2021-03-29 18:20:50', NULL, '3e813570-2ac8-4a6a-b822-acabd1c73450', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (80, '2021-03-29 18:21:13', NULL, '84bc0f66-c0c6-4b8c-8fff-8de7d21fa5b4', NULL, 213, 50);
INSERT INTO `busi_call_leg_profile` VALUES (81, '2021-03-29 18:23:10', NULL, 'c529eee7-2d8d-4277-8ce1-6c1e01512781', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (82, '2021-03-29 18:23:18', NULL, 'fc355482-c8e0-4fe3-b390-7ce7cfae2df3', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (83, '2021-03-29 18:24:03', NULL, '82847711-2ea3-4fed-b6fc-714335c99ab1', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (84, '2021-03-29 18:24:11', NULL, 'ca7b449a-d5c4-4d5e-98e2-7da87108e467', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (85, '2021-03-29 18:24:18', NULL, '65e9c117-5dc4-4a30-9b9a-d21ac06c3e0b', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (86, '2021-03-29 18:24:25', NULL, '866af5af-55c5-42aa-8018-eb5888e050a4', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (87, '2021-03-29 18:24:32', NULL, 'e57d8e86-e88f-4b89-8a36-ed40c5337a6a', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (88, '2021-03-29 18:24:40', NULL, '238d4a22-7bf0-4b18-a83d-840ed7c35d68', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (89, '2021-03-29 18:24:47', NULL, 'a9216c1e-b707-4209-80af-75313c5d73e5', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (90, '2021-03-29 18:24:54', NULL, '3abcbacf-9eab-46d8-94e4-e98a2c9713dd', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (91, '2021-03-29 18:25:02', NULL, 'bf2c84a0-126d-4a14-b7c5-27ec615c7597', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (92, '2021-03-29 18:25:12', NULL, 'a323fd5f-5f60-4c07-9469-cbefd503e3c7', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (93, '2021-03-29 18:25:20', NULL, '73fae517-155d-4e7d-9184-1f06416103a9', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (94, '2021-03-29 18:25:28', NULL, 'b939739d-c3f7-401f-8004-5451c62a35f7', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (95, '2021-03-29 18:25:34', NULL, '6010fda6-2a90-4dcf-ad4e-ae4e09e1ca61', NULL, 215, 50);
INSERT INTO `busi_call_leg_profile` VALUES (96, '2021-03-29 18:25:48', NULL, '0e6d4949-a262-4820-bcf3-84a9a243c99f', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (97, '2021-03-29 18:25:56', NULL, '5824c531-3746-4f0d-bc50-a912fe8508fd', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (98, '2021-03-29 18:26:02', NULL, '2c2aadc5-e695-4546-8e7c-b92f437e1687', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (99, '2021-03-29 18:26:09', NULL, 'bc0e9dca-0ddc-4793-a6d2-80d5db7e86b4', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (100, '2021-03-29 18:26:33', NULL, '264f785a-bf40-43e9-809e-d5bd94ce18ef', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (101, '2021-03-29 18:26:41', NULL, 'a06ca159-b951-4e67-9604-f75129b737b6', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (102, '2021-03-29 18:26:48', NULL, '0e87125b-7ef7-4153-8b1d-7fd076f571ac', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (103, '2021-03-29 18:26:54', NULL, '27fb020c-0786-44bf-aa2e-38a5dbc767ca', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (104, '2021-03-29 18:27:01', NULL, '417f3628-42a9-4bbd-8fea-4a2efd28cfd1', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (105, '2021-03-29 18:27:07', NULL, '47cbed61-da7c-406b-ac78-574d2d3bafc8', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (106, '2021-03-29 18:27:14', NULL, 'd0d9c1f8-c25e-449d-8ff7-2c9fa1db12ef', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (107, '2021-03-29 18:27:21', NULL, '5da67bf8-e06b-4760-ab52-a8db675fa564', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (108, '2021-03-29 18:27:27', NULL, 'a825d1a5-9544-48a9-b5a0-9dcaf99900f3', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (109, '2021-03-29 18:27:36', NULL, '04705894-bdcf-4396-8fb0-a479aa6b733d', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (110, '2021-03-29 18:27:42', NULL, '11e2a783-7af1-4e02-9c34-91124dab1a60', NULL, 216, 50);
INSERT INTO `busi_call_leg_profile` VALUES (111, '2021-03-29 18:28:11', NULL, '6e11116b-cb1b-4139-84ee-a2f9f99738b4', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (112, '2021-03-29 18:28:19', NULL, '0d863213-0155-440b-835c-d31244f6d58d', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (113, '2021-03-29 18:28:25', NULL, '46152323-5195-4052-a8d0-ceeec2e50264', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (114, '2021-03-29 18:28:39', NULL, '4c79e701-89f2-4f8e-8aa5-5e993d96dcd4', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (115, '2021-03-29 18:28:46', NULL, '7758916a-36b0-4989-8626-029b4c34161a', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (116, '2021-03-29 18:28:52', NULL, '63540b53-19ca-469c-a8e2-bfd5c0517e92', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (117, '2021-03-29 18:28:57', NULL, '524bff8c-f22c-46fb-bba0-6ca1330688f1', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (118, '2021-03-29 18:29:05', NULL, 'c2b10fc0-b999-41fc-a604-8d3705f89f0a', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (119, '2021-03-29 18:29:12', NULL, '7a488f31-ab69-4a82-8019-1b029371873b', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (120, '2021-03-29 18:29:22', NULL, 'd16d1721-039d-4eab-ad3c-2ea8b8d0070f', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (121, '2021-03-29 18:29:30', NULL, '22004e31-42b0-4e5b-b24a-ed5beefa0ac3', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (122, '2021-03-29 18:29:38', NULL, 'd6a4f959-2c4f-4ba4-8f49-00061b4a4095', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (123, '2021-03-29 18:29:44', NULL, 'b8545d9b-596a-4fd2-b9e1-c3c2586a76ab', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (124, '2021-03-29 18:29:51', NULL, 'e643cd32-52dc-4a0c-95f3-acb97cf5b643', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (125, '2021-03-29 18:29:57', NULL, 'ba9ea71c-83fc-4c9a-97f8-61b6945476d3', NULL, 217, 50);
INSERT INTO `busi_call_leg_profile` VALUES (127, '2021-04-08 18:38:57', NULL, '20adbb6b-251f-41fd-b3e5-05dbecfa6c6c', NULL, 212, 50);
INSERT INTO `busi_call_leg_profile` VALUES (141, '2021-04-19 15:26:43', NULL, 'fd725d50-1926-49dc-9252-2fa3b1c1a1b0', NULL, 100, 48);

-- ----------------------------
-- Table structure for busi_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_conference`;
CREATE TABLE `busi_conference`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '会议创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL,
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `create_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者用户名',
  `is_main` int(11) NULL DEFAULT NULL COMMENT '是否是会议的主体（发起者）1是，2否',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会议名',
  `cascade_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所有级联在一起的会议和子会议的相同ID',
  `conference_number` bigint(20) NULL DEFAULT NULL COMMENT '活跃会议室用的会议号',
  `co_space_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '活跃会议室spaceId',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '活跃会议室对应的部门',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '模板会议的ID，标记出是哪个模板发起的，好从模板点击进入会议进行关联',
  `data` mediumblob NULL COMMENT '当前正在进行中的会议室序列化数据',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `co_space_id`(`co_space_id`) USING BTREE,
  INDEX `create_user_id`(`create_user_id`) USING BTREE,
  INDEX `dept_id`(`dept_id`, `template_conference_id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`) USING BTREE,
  INDEX `cascade_id`(`cascade_id`) USING BTREE,
  INDEX `conference_number`(`conference_number`) USING BTREE,
  CONSTRAINT `busi_conference_ibfk_1` FOREIGN KEY (`create_user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_conference_ibfk_2` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_conference_ibfk_3` FOREIGN KEY (`conference_number`) REFERENCES `busi_conference_number` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '活跃会议室信息，用于存放活跃的会议室' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_conference_number
-- ----------------------------
DROP TABLE IF EXISTS `busi_conference_number`;
CREATE TABLE `busi_conference_number`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，充当会议号',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `create_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者用户名',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '归属公司ID',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '会议号类型：1级联，2普通',
  `status` int(11) NULL DEFAULT NULL COMMENT '号码状态：1闲置，10已预约，60已绑定，100会议中',
  `remarks` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dept_id`(`dept_id`, `type`) USING BTREE,
  INDEX `create_user_id`(`create_user_id`) USING BTREE,
  CONSTRAINT `busi_conference_number_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_conference_number_ibfk_2` FOREIGN KEY (`create_user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议号码记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_conference_number
-- ----------------------------
INSERT INTO `busi_conference_number` VALUES (11111, '2021-03-16 12:11:30', '2021-03-25 11:28:34', 100, 'admin', 100, 2, 100, NULL);
INSERT INTO `busi_conference_number` VALUES (15000, '2021-03-29 16:26:42', NULL, 1, 'superAdmin', 213, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (15001, '2021-03-29 16:28:09', NULL, 1, 'superAdmin', 213, 2, 1, '15001');
INSERT INTO `busi_conference_number` VALUES (15002, '2021-03-29 16:29:05', NULL, 1, 'superAdmin', 213, 2, 1, '15002');
INSERT INTO `busi_conference_number` VALUES (15003, '2021-03-29 16:29:17', NULL, 1, 'superAdmin', 213, 2, 1, '15003');
INSERT INTO `busi_conference_number` VALUES (15004, '2021-03-29 16:29:28', NULL, 1, 'superAdmin', 213, 2, 1, '15004');
INSERT INTO `busi_conference_number` VALUES (15005, '2021-03-29 16:29:40', NULL, 1, 'superAdmin', 213, 2, 1, '15005');
INSERT INTO `busi_conference_number` VALUES (15006, '2021-03-29 16:29:49', NULL, 1, 'superAdmin', 213, 2, 1, '15006');
INSERT INTO `busi_conference_number` VALUES (15007, '2021-03-29 16:29:58', NULL, 1, 'superAdmin', 213, 2, 1, '15007');
INSERT INTO `busi_conference_number` VALUES (15008, '2021-03-29 16:30:06', NULL, 1, 'superAdmin', 213, 2, 1, '15008');
INSERT INTO `busi_conference_number` VALUES (15009, '2021-03-29 16:30:14', NULL, 1, 'superAdmin', 213, 2, 1, '15009');
INSERT INTO `busi_conference_number` VALUES (15010, '2021-03-29 16:30:25', NULL, 1, 'superAdmin', 213, 2, 1, '15010');
INSERT INTO `busi_conference_number` VALUES (15011, '2021-03-29 16:30:48', NULL, 1, 'superAdmin', 213, 2, 1, '15011');
INSERT INTO `busi_conference_number` VALUES (15012, '2021-03-29 16:31:03', NULL, 1, 'superAdmin', 213, 2, 1, '15012');
INSERT INTO `busi_conference_number` VALUES (15013, '2021-03-29 16:31:16', NULL, 1, 'superAdmin', 213, 2, 1, '15013');
INSERT INTO `busi_conference_number` VALUES (15014, '2021-03-29 16:31:27', NULL, 1, 'superAdmin', 213, 2, 1, '15014');
INSERT INTO `busi_conference_number` VALUES (15015, '2021-03-29 18:20:50', '2021-03-29 18:20:59', 1, 'superAdmin', 213, 2, 1, '');
INSERT INTO `busi_conference_number` VALUES (15016, '2021-03-29 18:21:13', NULL, 1, 'superAdmin', 213, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (16000, '2021-03-29 16:32:00', NULL, 1, 'superAdmin', 214, 2, 1, '16000');
INSERT INTO `busi_conference_number` VALUES (16001, '2021-03-29 16:32:10', NULL, 1, 'superAdmin', 214, 2, 1, '16001');
INSERT INTO `busi_conference_number` VALUES (16002, '2021-03-29 16:32:19', NULL, 1, 'superAdmin', 214, 2, 1, '16002');
INSERT INTO `busi_conference_number` VALUES (16003, '2021-03-29 16:32:28', NULL, 1, 'superAdmin', 214, 2, 1, '16003');
INSERT INTO `busi_conference_number` VALUES (16004, '2021-03-29 16:32:36', NULL, 1, 'superAdmin', 214, 2, 1, '16004');
INSERT INTO `busi_conference_number` VALUES (16005, '2021-03-29 16:32:46', NULL, 1, 'superAdmin', 214, 2, 1, '16005');
INSERT INTO `busi_conference_number` VALUES (16006, '2021-03-29 16:32:55', NULL, 1, 'superAdmin', 214, 2, 1, '16006');
INSERT INTO `busi_conference_number` VALUES (16007, '2021-03-29 16:34:36', NULL, 1, 'superAdmin', 214, 2, 1, '16007');
INSERT INTO `busi_conference_number` VALUES (16008, '2021-03-29 16:33:06', NULL, 1, 'superAdmin', 214, 2, 1, '16008');
INSERT INTO `busi_conference_number` VALUES (16009, '2021-03-29 16:33:18', NULL, 1, 'superAdmin', 214, 2, 1, '16009');
INSERT INTO `busi_conference_number` VALUES (16010, '2021-03-29 16:33:28', NULL, 1, 'superAdmin', 214, 2, 1, '16010');
INSERT INTO `busi_conference_number` VALUES (16011, '2021-03-29 16:33:40', NULL, 1, 'superAdmin', 214, 2, 1, '16011');
INSERT INTO `busi_conference_number` VALUES (16012, '2021-03-29 16:33:50', NULL, 1, 'superAdmin', 214, 2, 1, '16012');
INSERT INTO `busi_conference_number` VALUES (16013, '2021-03-29 16:33:58', NULL, 1, 'superAdmin', 214, 2, 1, '16013');
INSERT INTO `busi_conference_number` VALUES (16014, '2021-03-29 16:34:20', NULL, 1, 'superAdmin', 214, 2, 1, '16014');
INSERT INTO `busi_conference_number` VALUES (17000, '2021-03-29 18:23:10', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (17001, '2021-03-29 18:23:18', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (17002, '2021-03-29 18:24:03', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (17003, '2021-03-29 18:24:11', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (17004, '2021-03-29 18:24:17', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (17005, '2021-03-29 18:24:25', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (17006, '2021-03-29 18:24:32', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (17007, '2021-03-29 18:24:40', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (17008, '2021-03-29 18:24:47', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (17009, '2021-03-29 18:24:54', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (17010, '2021-03-29 18:25:02', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (17011, '2021-03-29 18:25:12', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (17012, '2021-03-29 18:25:20', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (17013, '2021-03-29 18:25:28', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (17014, '2021-03-29 18:25:34', NULL, 1, 'superAdmin', 215, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18000, '2021-03-29 18:25:48', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18001, '2021-03-29 18:25:56', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18002, '2021-03-29 18:26:02', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18003, '2021-03-29 18:26:09', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18004, '2021-03-29 18:26:33', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18005, '2021-03-29 18:26:41', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18006, '2021-03-29 18:26:47', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18007, '2021-03-29 18:26:54', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18008, '2021-03-29 18:27:01', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18009, '2021-03-29 18:27:07', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18010, '2021-03-29 18:27:14', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18011, '2021-03-29 18:27:21', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18012, '2021-03-29 18:27:27', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18013, '2021-03-29 18:27:36', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (18014, '2021-03-29 18:27:42', NULL, 1, 'superAdmin', 216, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19000, '2021-03-29 18:28:11', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19001, '2021-03-29 18:28:19', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19002, '2021-03-29 18:28:25', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19003, '2021-03-29 18:28:39', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19004, '2021-03-29 18:28:46', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19005, '2021-03-29 18:28:51', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19006, '2021-03-29 18:28:57', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19007, '2021-03-29 18:29:04', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19008, '2021-03-29 18:29:12', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19009, '2021-03-29 18:29:22', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19010, '2021-03-29 18:29:30', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19011, '2021-03-29 18:29:38', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19012, '2021-03-29 18:29:44', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19013, '2021-03-29 18:29:51', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (19014, '2021-03-29 18:29:57', NULL, 1, 'superAdmin', 217, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (50000, '2021-04-18 15:09:18', NULL, 1, 'superAdmin', 100, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (80102, '2021-02-20 14:27:19', NULL, 1, 'superAdmin', 103, 1, 1, '80102');
INSERT INTO `busi_conference_number` VALUES (80202, '2021-02-20 14:27:35', '2021-03-01 10:29:41', 1, 'superAdmin', 104, 1, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (80302, '2021-02-20 14:27:48', '2021-02-28 10:25:26', 1, 'superAdmin', 105, 1, 1, '80302');
INSERT INTO `busi_conference_number` VALUES (80402, '2021-02-20 14:28:03', NULL, 1, 'superAdmin', 106, 1, 1, '80402');
INSERT INTO `busi_conference_number` VALUES (80502, '2021-02-20 14:20:21', NULL, 1, 'superAdmin', 107, 1, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (80602, '2021-02-20 14:28:38', NULL, 1, 'superAdmin', 200, 1, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (80702, '2021-02-20 14:28:51', NULL, 1, 'superAdmin', 201, 1, 1, '80702');
INSERT INTO `busi_conference_number` VALUES (80802, '2021-02-20 14:29:01', NULL, 1, 'superAdmin', 202, 1, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (80902, '2021-02-20 14:29:27', NULL, 1, 'superAdmin', 203, 1, 1, '80902');
INSERT INTO `busi_conference_number` VALUES (81002, '2021-02-20 14:29:48', NULL, 1, 'superAdmin', 204, 1, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (81102, '2021-02-20 14:30:05', NULL, 1, 'superAdmin', 205, 1, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (81202, '2021-02-20 14:30:13', NULL, 1, 'superAdmin', 206, 1, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (81302, '2021-02-20 14:34:29', NULL, 1, 'superAdmin', 207, 1, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (81402, '2021-02-20 14:34:38', '2021-02-24 15:52:59', 1, 'superAdmin', 208, 1, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (88000, '2021-02-20 14:27:01', '2021-03-25 11:28:29', 1, 'superAdmin', 100, 2, 60, '88000');
INSERT INTO `busi_conference_number` VALUES (88002, '2021-04-15 17:27:15', '2021-03-23 17:18:44', 1, 'superAdmin', 100, 2, 1, NULL);
INSERT INTO `busi_conference_number` VALUES (1000000, '2021-03-24 15:55:08', NULL, 1, 'superAdmin', 212, 2, 100, NULL);
INSERT INTO `busi_conference_number` VALUES (1234567, '2021-04-08 18:38:57', NULL, 1, 'superAdmin', 212, 2, 60, 'fdsfs');
INSERT INTO `busi_conference_number` VALUES (1321312, '2021-04-15 18:24:37', NULL, 1, 'superAdmin', 100, 2, 1, NULL);

-- ----------------------------
-- Table structure for busi_fme
-- ----------------------------
DROP TABLE IF EXISTS `busi_fme`;
CREATE TABLE `busi_fme`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '终端创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '终端修改时间',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'FME的连接用户名，同一个组下的用户名相同',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'FME的连接密码，同一个组下的密码相同',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'fme显示名字',
  `ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备的IP地址',
  `cucm_ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '该IP是增强音视频效果',
  `port` int(11) NULL DEFAULT 9443 COMMENT 'fme端口',
  `status` int(11) NULL DEFAULT NULL COMMENT 'FME在线状态：1在线，2离线，3删除',
  `spare_fme_id` bigint(20) NULL DEFAULT NULL COMMENT '备用FME（本节点宕机后指向的备用节点）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ip`(`ip`) USING BTREE,
  INDEX `spare_fme_id`(`spare_fme_id`) USING BTREE,
  CONSTRAINT `busi_fme_ibfk_1` FOREIGN KEY (`spare_fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'FME终端信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_fme
-- ----------------------------
INSERT INTO `busi_fme` VALUES (31, '2021-02-19 18:14:38', '2021-04-20 18:20:05', 'admin', 'P@rad1se', '新疆维吾尔自治区', '192.166.0.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (32, '2021-02-19 18:27:33', '2021-04-20 18:27:19', 'admin', 'P@rad1se', '克州', '192.166.2.3', '192.166.0.41', 9443, 2, NULL);
INSERT INTO `busi_fme` VALUES (33, '2021-02-19 18:37:00', '2021-04-20 18:27:20', 'admin', 'P@rad1se', '喀什地区', '192.166.3.3', '192.166.0.41', 9443, 2, NULL);
INSERT INTO `busi_fme` VALUES (34, '2021-02-19 18:38:32', '2021-04-20 18:27:21', 'admin', 'P@rad1se', '和田地区', '192.166.4.3', '192.166.0.41', 9443, 2, NULL);
INSERT INTO `busi_fme` VALUES (35, '2021-02-19 18:42:20', '2021-04-20 18:27:22', 'admin', 'P@rad1se', '阿克苏地区', '192.166.5.3', '192.166.0.41', 9443, 2, NULL);
INSERT INTO `busi_fme` VALUES (36, '2021-02-19 19:40:17', '2021-04-20 18:27:23', 'admin', 'P@rad1se', '巴州地区', '192.166.6.3', '192.166.0.41', 9443, 2, NULL);
INSERT INTO `busi_fme` VALUES (37, '2021-02-19 19:41:40', '2021-04-20 18:27:24', 'admin', 'P@rad1se', '吐鲁番市', '192.166.7.3', '192.166.0.41', 9443, 2, NULL);
INSERT INTO `busi_fme` VALUES (38, '2021-02-19 19:42:41', '2021-04-20 18:27:25', 'admin', 'P@rad1se', '哈密市', '192.166.8.3', '192.166.0.41', 9443, 2, NULL);
INSERT INTO `busi_fme` VALUES (39, '2021-02-19 19:43:42', '2021-04-20 18:27:26', 'admin', 'P@rad1se', '乌鲁木齐市', '192.166.9.3', '192.166.0.41', 9443, 2, NULL);
INSERT INTO `busi_fme` VALUES (40, '2021-02-19 19:44:33', '2021-04-20 18:27:27', 'admin', 'P@rad1se', '昌吉州', '192.166.10.3', '192.166.0.41', 9443, 2, NULL);
INSERT INTO `busi_fme` VALUES (41, '2021-02-19 19:46:28', '2021-04-20 18:27:28', 'admin', 'P@rad1se', '塔城地区', '192.166.11.3', '192.166.0.41', 9443, 2, NULL);
INSERT INTO `busi_fme` VALUES (42, '2021-02-19 19:47:57', '2021-04-20 18:27:13', 'admin', 'P@rad1se', '阿勒泰地区', '192.166.12.3', '192.166.0.41', 9443, 2, NULL);
INSERT INTO `busi_fme` VALUES (44, '2021-02-20 14:33:24', '2021-04-20 18:27:14', 'admin', 'P@rad1se', '博州', '192.166.13.3', '192.166.0.41', 9443, 2, NULL);
INSERT INTO `busi_fme` VALUES (45, '2021-02-20 14:33:46', '2021-04-20 18:27:15', 'admin', 'P@rad1se', '克拉玛依', '192.166.14.3', '192.166.0.41', 9443, 2, NULL);
INSERT INTO `busi_fme` VALUES (48, '2021-03-03 14:57:27', '2021-04-20 18:20:19', 'admin', 'P@rad1se', '伊犁州', '192.166.1.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (50, '2021-03-26 16:35:25', '2021-04-20 18:20:23', 'admin', 'P@rad1se', '集群A节点1', '172.16.100.190', NULL, 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (51, '2021-03-26 16:36:17', '2021-04-20 18:20:23', 'admin', 'P@rad1se', '集群A节点2', '172.16.100.191', NULL, 9443, 1, NULL);

-- ----------------------------
-- Table structure for busi_fme_cluster
-- ----------------------------
DROP TABLE IF EXISTS `busi_fme_cluster`;
CREATE TABLE `busi_fme_cluster`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '集群组名，最长32',
  `spare_fme_type` int(11) NULL DEFAULT NULL COMMENT '备用fme类型，1单节点，100集群',
  `spare_fme_id` bigint(20) NULL DEFAULT NULL COMMENT '当fme_type为1是，指向busi_fme的id字段，为100指向busi_fme_cluster的id字段',
  `description` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `spare_fme_id`(`spare_fme_id`) USING BTREE,
  INDEX `spare_fme_type`(`spare_fme_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'FME集群' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_fme_cluster
-- ----------------------------
INSERT INTO `busi_fme_cluster` VALUES (59, '2021-03-22 16:24:41', '2021-03-26 16:36:48', '集群A', NULL, NULL, '测试专用集群');

-- ----------------------------
-- Table structure for busi_fme_cluster_map
-- ----------------------------
DROP TABLE IF EXISTS `busi_fme_cluster_map`;
CREATE TABLE `busi_fme_cluster_map`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `cluster_id` bigint(20) NULL DEFAULT NULL COMMENT 'FME集群ID',
  `fme_id` bigint(20) NULL DEFAULT NULL COMMENT 'FME的ID',
  `weight` int(11) NULL DEFAULT NULL COMMENT '节点在集群中的权重值',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `group_id`(`cluster_id`, `fme_id`, `weight`) USING BTREE,
  UNIQUE INDEX `cluster_id`(`cluster_id`, `fme_id`) USING BTREE,
  INDEX `fme_id`(`fme_id`) USING BTREE,
  CONSTRAINT `busi_fme_cluster_map_ibfk_1` FOREIGN KEY (`cluster_id`) REFERENCES `busi_fme_cluster` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_fme_cluster_map_ibfk_2` FOREIGN KEY (`fme_id`) REFERENCES `busi_fme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 100030 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'FME-终端组中间表（多对多）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_fme_cluster_map
-- ----------------------------
INSERT INTO `busi_fme_cluster_map` VALUES (100027, '2021-03-26 16:37:14', '2021-03-26 16:40:50', 59, 50, 100);
INSERT INTO `busi_fme_cluster_map` VALUES (100029, '2021-03-26 16:40:40', NULL, 59, 51, 11);

-- ----------------------------
-- Table structure for busi_fme_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_fme_dept`;
CREATE TABLE `busi_fme_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '分配给的租户',
  `fme_type` int(11) NULL DEFAULT NULL COMMENT '1单节点，100集群',
  `fme_id` bigint(20) NULL DEFAULT NULL COMMENT '当fme_type为1是，指向busi_fme的id字段，为100指向busi_fme_cluster的id字段',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `busi_fme_group_dept_ibfk_1`(`dept_id`) USING BTREE,
  INDEX `fme_group_id`(`fme_id`) USING BTREE,
  CONSTRAINT `busi_fme_dept_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'FME组分配租户的中间表（一个FME组可以分配给多个租户，一对多）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_fme_dept
-- ----------------------------
INSERT INTO `busi_fme_dept` VALUES (35, '2021-03-24 15:45:43', '2021-03-26 16:41:32', 212, 100, 59);
INSERT INTO `busi_fme_dept` VALUES (38, '2021-04-16 17:56:42', '2021-04-19 15:25:49', 100, 1, 48);

-- ----------------------------
-- Table structure for busi_history_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_history_conference`;
CREATE TABLE `busi_history_conference`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `create_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者用户名',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板会议名',
  `number` int(11) NULL DEFAULT NULL COMMENT '会议号码',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `call_leg_profile_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '入会方案配置ID（关联FME里面的入会方案记录ID，会控端不存）',
  `bandwidth` int(11) NULL DEFAULT NULL COMMENT '带宽1,2,3,4,5,6M',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `busi_conference_template_ibfk_1`(`dept_id`) USING BTREE,
  CONSTRAINT `busi_history_conference_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '历史会议，每次挂断会保存该历史记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_history_participant
-- ----------------------------
DROP TABLE IF EXISTS `busi_history_participant`;
CREATE TABLE `busi_history_participant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `history_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的会议ID',
  `terminal_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的终端ID',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_conference_id`(`history_conference_id`) USING BTREE,
  INDEX `terminal_id`(`terminal_id`) USING BTREE,
  CONSTRAINT `busi_history_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_history_participant_ibfk_3` FOREIGN KEY (`history_conference_id`) REFERENCES `busi_history_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '历史会议的参会者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_sfbc_registration_server
-- ----------------------------
DROP TABLE IF EXISTS `busi_sfbc_registration_server`;
CREATE TABLE `busi_sfbc_registration_server`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册服务器名字',
  `ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册服务器ip地州',
  `port` int(11) NULL DEFAULT NULL COMMENT '注册服务器端口',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册服务器用户名',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册服务器密码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '终端SFBC注册服务器' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_sfbc_registration_server
-- ----------------------------
INSERT INTO `busi_sfbc_registration_server` VALUES (4, '2021-04-20 16:08:16', '2021-04-20 16:31:15', NULL, '172.16.100.99', 8080, 'P@rad1se', NULL);

-- ----------------------------
-- Table structure for busi_sfbc_server_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_sfbc_server_dept`;
CREATE TABLE `busi_sfbc_server_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `sfbc_server_id` bigint(20) NULL DEFAULT NULL COMMENT 'SFBC服务器的id',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'SFBC服务器-部门映射' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for busi_template_conference
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_conference`;
CREATE TABLE `busi_template_conference`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者id',
  `create_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者用户名',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板会议名',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `call_leg_profile_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '入会方案配置ID（关联FME里面的入会方案记录ID，会控端不存）',
  `bandwidth` int(11) NULL DEFAULT NULL COMMENT '带宽1,2,3,4,5,6M',
  `is_auto_call` int(11) NULL DEFAULT NULL COMMENT '是否自动呼叫与会者：1是，2否',
  `is_auto_monitor` int(11) NULL DEFAULT NULL COMMENT '是否自动监听会议：1是，2否',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '模板会议是否允许被级联：1允许，2不允许',
  `conference_number` bigint(20) NULL DEFAULT NULL COMMENT '模板绑定的会议号',
  `master_participant_id` bigint(20) NULL DEFAULT NULL COMMENT '主会场ID',
  `default_view_layout` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认视图布局类型',
  `default_view_is_broadcast` int(11) NULL DEFAULT NULL COMMENT '默认视图是否广播(1是，2否)',
  `default_view_is_display_self` int(11) NULL DEFAULT NULL COMMENT '默认视图是否显示自己(1是，2否)',
  `default_view_is_fill` int(11) NULL DEFAULT NULL COMMENT '默认视图是否补位(1是，2否)',
  `polling_interval` int(11) NULL DEFAULT NULL COMMENT '轮询时间间隔',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `busi_conference_template_ibfk_1`(`dept_id`) USING BTREE,
  INDEX `call_leg_profile_id`(`call_leg_profile_id`) USING BTREE,
  INDEX `conference_number`(`conference_number`) USING BTREE,
  INDEX `create_user_id`(`create_user_id`) USING BTREE,
  INDEX `master_participant_id`(`master_participant_id`) USING BTREE,
  CONSTRAINT `busi_template_conference_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_conference_ibfk_2` FOREIGN KEY (`conference_number`) REFERENCES `busi_conference_number` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_conference_ibfk_3` FOREIGN KEY (`create_user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_conference_ibfk_4` FOREIGN KEY (`master_participant_id`) REFERENCES `busi_template_participant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 65 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_conference
-- ----------------------------
INSERT INTO `busi_template_conference` VALUES (39, '2021-03-09 17:17:21', '2021-04-18 14:17:00', 1, 'superAdmin', '党政专用高清调度系统', 100, '183a7d40-901e-4c29-a999-d1b37224d477', 2, 1, 2, 2, 88000, 19010, 'allEqualQuarters', 1, 1, 1, 3);
INSERT INTO `busi_template_conference` VALUES (42, '2021-03-24 16:00:04', '2021-03-30 17:26:27', 1, 'superAdmin', '测试跨部门单体会议', 212, 'cca6ac97-3658-4bea-a87d-f99de7772f81', 3, 2, 2, 2, 1000000, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_template_conference` VALUES (48, '2021-04-08 18:50:48', NULL, 1, 'superAdmin', 'dsadasd', 212, '16444640-db17-4ceb-a9f9-ba3684ccd26b', 2, 2, 2, 2, 1234567, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_template_conference` VALUES (64, '2021-04-18 15:19:22', '2021-04-18 15:37:04', 1, 'superAdmin', '测试多分屏', 100, 'fd725d50-1926-49dc-9252-2fa3b1c1a1b0', 2, 2, 2, 2, 11111, 19163, 'allEqualQuarters', 2, 2, 1, 10);

-- ----------------------------
-- Table structure for busi_template_conference_default_view_cell_screen
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_conference_default_view_cell_screen`;
CREATE TABLE `busi_template_conference_default_view_cell_screen`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的会议模板ID',
  `cell_sequence_number` int(11) NULL DEFAULT NULL COMMENT '单元格序号',
  `operation` int(11) NULL DEFAULT NULL COMMENT '分频单元格对应的操作，默认为选看101，105轮询',
  `is_fixed` int(11) NULL DEFAULT NULL COMMENT '分频单元格是否固定',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`) USING BTREE,
  INDEX `cell_sequence_number`(`cell_sequence_number`) USING BTREE,
  CONSTRAINT `busi_template_conference_default_view_cell_screen_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 245 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '默认视图下指定的多分频单元格' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_conference_default_view_cell_screen
-- ----------------------------
INSERT INTO `busi_template_conference_default_view_cell_screen` VALUES (217, NULL, NULL, 39, 1, 101, 2);
INSERT INTO `busi_template_conference_default_view_cell_screen` VALUES (218, NULL, NULL, 39, 2, 101, 2);
INSERT INTO `busi_template_conference_default_view_cell_screen` VALUES (219, NULL, NULL, 39, 3, 101, 2);
INSERT INTO `busi_template_conference_default_view_cell_screen` VALUES (220, NULL, NULL, 39, 4, 101, 2);
INSERT INTO `busi_template_conference_default_view_cell_screen` VALUES (228, NULL, NULL, 64, 1, 101, 2);
INSERT INTO `busi_template_conference_default_view_cell_screen` VALUES (229, NULL, NULL, 64, 2, 101, 2);
INSERT INTO `busi_template_conference_default_view_cell_screen` VALUES (230, NULL, NULL, 64, 3, 101, 2);
INSERT INTO `busi_template_conference_default_view_cell_screen` VALUES (231, NULL, NULL, 64, 4, 101, 2);

-- ----------------------------
-- Table structure for busi_template_conference_default_view_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_conference_default_view_dept`;
CREATE TABLE `busi_template_conference_default_view_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的会议模板ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID（部门也是FME终端，一种与会者）',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  INDEX `busi_template_default_view_dept_ibfk_1`(`template_conference_id`) USING BTREE,
  CONSTRAINT `busi_template_conference_default_view_dept_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_conference_default_view_dept_ibfk_2` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1281 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '默认视图的部门显示顺序' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_conference_default_view_dept
-- ----------------------------
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1233, NULL, NULL, 39, 100, 15);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1234, NULL, NULL, 39, 105, 14);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1235, NULL, NULL, 39, 104, 13);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1236, NULL, NULL, 39, 107, 12);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1237, NULL, NULL, 39, 200, 11);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1238, NULL, NULL, 39, 201, 10);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1239, NULL, NULL, 39, 202, 9);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1240, NULL, NULL, 39, 203, 8);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1241, NULL, NULL, 39, 204, 7);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1242, NULL, NULL, 39, 207, 6);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1243, NULL, NULL, 39, 208, 5);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1244, NULL, NULL, 39, 206, 4);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1245, NULL, NULL, 39, 106, 3);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1246, NULL, NULL, 39, 205, 2);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1247, NULL, NULL, 39, 103, 1);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (1254, NULL, NULL, 64, 100, 1);

-- ----------------------------
-- Table structure for busi_template_conference_default_view_paticipant
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_conference_default_view_paticipant`;
CREATE TABLE `busi_template_conference_default_view_paticipant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的会议模板ID',
  `template_participant_id` bigint(20) NULL DEFAULT NULL COMMENT '参会终端ID，关联busi_template_participant的ID',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  `cell_sequence_number` int(11) NULL DEFAULT NULL COMMENT '多分频单元格序号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `template_participant_id`(`template_participant_id`) USING BTREE,
  INDEX `default_view_id`(`template_conference_id`) USING BTREE,
  INDEX `cell_sequence_number`(`cell_sequence_number`) USING BTREE,
  CONSTRAINT `busi_template_conference_default_view_paticipant_ibfk_3` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_conference_default_view_paticipant_ibfk_4` FOREIGN KEY (`template_participant_id`) REFERENCES `busi_template_participant` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 10727 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '默认视图的参会者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_conference_default_view_paticipant
-- ----------------------------
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10395, NULL, NULL, 39, 19006, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10396, NULL, NULL, 39, 19007, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10397, NULL, NULL, 39, 19008, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10398, NULL, NULL, 39, 19009, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10399, NULL, NULL, 39, 19010, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10400, NULL, NULL, 39, 19011, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10401, NULL, NULL, 39, 19012, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10402, NULL, NULL, 39, 19013, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10403, NULL, NULL, 39, 19014, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10404, NULL, NULL, 39, 19015, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10405, NULL, NULL, 39, 19016, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10406, NULL, NULL, 39, 19017, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10407, NULL, NULL, 39, 19018, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10408, NULL, NULL, 39, 19019, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10409, NULL, NULL, 39, 19020, 10, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10410, NULL, NULL, 39, 19021, 11, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10411, NULL, NULL, 39, 19022, 12, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10412, NULL, NULL, 39, 19023, 13, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10413, NULL, NULL, 39, 19024, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10414, NULL, NULL, 39, 19025, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10415, NULL, NULL, 39, 19026, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10416, NULL, NULL, 39, 19027, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10417, NULL, NULL, 39, 19028, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10418, NULL, NULL, 39, 19029, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10419, NULL, NULL, 39, 19030, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10420, NULL, NULL, 39, 19031, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10421, NULL, NULL, 39, 19032, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10422, NULL, NULL, 39, 19033, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10423, NULL, NULL, 39, 19034, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10424, NULL, NULL, 39, 19035, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10425, NULL, NULL, 39, 19036, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10426, NULL, NULL, 39, 19037, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10427, NULL, NULL, 39, 19038, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10428, NULL, NULL, 39, 19039, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10429, NULL, NULL, 39, 19040, 10, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10430, NULL, NULL, 39, 19041, 11, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10431, NULL, NULL, 39, 19042, 12, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10432, NULL, NULL, 39, 19043, 13, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10433, NULL, NULL, 39, 19044, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10434, NULL, NULL, 39, 19045, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10435, NULL, NULL, 39, 19046, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10436, NULL, NULL, 39, 19047, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10437, NULL, NULL, 39, 19048, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10438, NULL, NULL, 39, 19049, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10439, NULL, NULL, 39, 19050, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10440, NULL, NULL, 39, 19051, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10441, NULL, NULL, 39, 19052, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10442, NULL, NULL, 39, 19053, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10443, NULL, NULL, 39, 19054, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10444, NULL, NULL, 39, 19055, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10445, NULL, NULL, 39, 19056, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10446, NULL, NULL, 39, 19057, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10447, NULL, NULL, 39, 19058, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10448, NULL, NULL, 39, 19059, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10449, NULL, NULL, 39, 19060, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10450, NULL, NULL, 39, 19061, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10451, NULL, NULL, 39, 19062, 10, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10452, NULL, NULL, 39, 19063, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10453, NULL, NULL, 39, 19064, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10454, NULL, NULL, 39, 19065, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10455, NULL, NULL, 39, 19066, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10456, NULL, NULL, 39, 19067, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10457, NULL, NULL, 39, 19068, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10458, NULL, NULL, 39, 19069, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10459, NULL, NULL, 39, 19070, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10460, NULL, NULL, 39, 19071, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10461, NULL, NULL, 39, 19072, 10, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10462, NULL, NULL, 39, 19073, 11, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10463, NULL, NULL, 39, 19074, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10464, NULL, NULL, 39, 19075, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10465, NULL, NULL, 39, 19076, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10466, NULL, NULL, 39, 19077, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10467, NULL, NULL, 39, 19078, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10468, NULL, NULL, 39, 19079, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10469, NULL, NULL, 39, 19080, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10470, NULL, NULL, 39, 19081, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10471, NULL, NULL, 39, 19082, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10472, NULL, NULL, 39, 19083, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10473, NULL, NULL, 39, 19084, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10474, NULL, NULL, 39, 19085, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10475, NULL, NULL, 39, 19086, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10476, NULL, NULL, 39, 19087, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10477, NULL, NULL, 39, 19088, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10478, NULL, NULL, 39, 19089, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10479, NULL, NULL, 39, 19090, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10480, NULL, NULL, 39, 19091, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10481, NULL, NULL, 39, 19092, 10, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10482, NULL, NULL, 39, 19093, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10483, NULL, NULL, 39, 19094, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10484, NULL, NULL, 39, 19095, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10485, NULL, NULL, 39, 19096, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10486, NULL, NULL, 39, 19097, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10487, NULL, NULL, 39, 19098, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10488, NULL, NULL, 39, 19099, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10489, NULL, NULL, 39, 19100, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10490, NULL, NULL, 39, 19101, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10491, NULL, NULL, 39, 19102, 10, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10492, NULL, NULL, 39, 19103, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10493, NULL, NULL, 39, 19104, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10494, NULL, NULL, 39, 19105, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10495, NULL, NULL, 39, 19106, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10496, NULL, NULL, 39, 19107, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10497, NULL, NULL, 39, 19108, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10498, NULL, NULL, 39, 19109, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10499, NULL, NULL, 39, 19110, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10500, NULL, NULL, 39, 19111, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10501, NULL, NULL, 39, 19112, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10502, NULL, NULL, 39, 19113, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10503, NULL, NULL, 39, 19114, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10504, NULL, NULL, 39, 19115, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10505, NULL, NULL, 39, 19116, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10506, NULL, NULL, 39, 19117, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10507, NULL, NULL, 39, 19118, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10508, NULL, NULL, 39, 19119, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10509, NULL, NULL, 39, 19120, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10510, NULL, NULL, 39, 19121, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10511, NULL, NULL, 39, 19122, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10512, NULL, NULL, 39, 19123, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10513, NULL, NULL, 39, 19124, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10514, NULL, NULL, 39, 19125, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10515, NULL, NULL, 39, 19126, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10516, NULL, NULL, 39, 19127, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10517, NULL, NULL, 39, 19128, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10518, NULL, NULL, 39, 19129, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10519, NULL, NULL, 39, 19130, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10562, NULL, NULL, 64, 19165, 0, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10563, NULL, NULL, 64, 19154, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10564, NULL, NULL, 64, 19155, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10565, NULL, NULL, 64, 19156, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10566, NULL, NULL, 64, 19157, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10567, NULL, NULL, 64, 19158, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10568, NULL, NULL, 64, 19159, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10569, NULL, NULL, 64, 19160, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10570, NULL, NULL, 64, 19161, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10571, NULL, NULL, 64, 19162, 11, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10572, NULL, NULL, 64, 19163, 12, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (10573, NULL, NULL, 64, 19164, 14, NULL);

-- ----------------------------
-- Table structure for busi_template_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_dept`;
CREATE TABLE `busi_template_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `uuid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板中的级联与会者终端的UUID(FME参会者ID)',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID（部门也是FME终端，一种与会者）',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `terminal_id`(`dept_id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`, `weight`) USING BTREE,
  CONSTRAINT `busi_template_dept_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_dept_ibfk_2` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 3149 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议模板的级联部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_dept
-- ----------------------------
INSERT INTO `busi_template_dept` VALUES (1424, '2021-03-30 17:26:27', NULL, '0b51f2de-2008-429d-a62e-1f874b4f130b', 42, 213, 5);
INSERT INTO `busi_template_dept` VALUES (1425, '2021-03-30 17:26:27', NULL, 'f9b5cf1d-da50-44da-919d-ccc0dea11ac5', 42, 214, 4);
INSERT INTO `busi_template_dept` VALUES (1426, '2021-03-30 17:26:27', NULL, 'c5f29e6b-e09d-4ecc-bf45-69c6dfa1a159', 42, 215, 3);
INSERT INTO `busi_template_dept` VALUES (1427, '2021-03-30 17:26:27', NULL, '9f36db9a-2539-474e-bab5-76abeb9c5b2f', 42, 216, 2);
INSERT INTO `busi_template_dept` VALUES (1428, '2021-03-30 17:26:27', NULL, 'd164653c-8142-476b-ae55-76f595fe91f7', 42, 217, 1);
INSERT INTO `busi_template_dept` VALUES (3125, '2021-04-18 14:17:00', NULL, '46181cda-26c4-4c1c-8166-6ec11e998efa', 39, 103, 14);
INSERT INTO `busi_template_dept` VALUES (3126, '2021-04-18 14:17:00', NULL, 'f62bd30c-fd67-4d57-a941-214ff000164d', 39, 205, 13);
INSERT INTO `busi_template_dept` VALUES (3127, '2021-04-18 14:17:00', NULL, '1cb1a974-9ad9-4971-969d-c2de941f5c06', 39, 106, 12);
INSERT INTO `busi_template_dept` VALUES (3128, '2021-04-18 14:17:00', NULL, '51b80849-14bf-4b85-ba0b-6e4217c06ed5', 39, 206, 11);
INSERT INTO `busi_template_dept` VALUES (3129, '2021-04-18 14:17:00', NULL, 'e0babca1-839c-46cb-b490-1ead01061f1d', 39, 208, 10);
INSERT INTO `busi_template_dept` VALUES (3130, '2021-04-18 14:17:00', NULL, '0e5cf8c0-8ed5-4faf-9bd3-59c0af68e0d3', 39, 207, 9);
INSERT INTO `busi_template_dept` VALUES (3131, '2021-04-18 14:17:00', NULL, '22045a48-84e6-4306-a7c5-a3f1049f1a1f', 39, 204, 8);
INSERT INTO `busi_template_dept` VALUES (3132, '2021-04-18 14:17:00', NULL, '3e490fcd-2fde-4673-865a-36d09bcd2ffe', 39, 203, 7);
INSERT INTO `busi_template_dept` VALUES (3133, '2021-04-18 14:17:00', NULL, 'c9c7e861-2ae6-4346-90bd-f0d4df51afbc', 39, 202, 6);
INSERT INTO `busi_template_dept` VALUES (3134, '2021-04-18 14:17:00', NULL, '60e05b32-4ca3-4974-a4a3-4af77dc48d82', 39, 201, 5);
INSERT INTO `busi_template_dept` VALUES (3135, '2021-04-18 14:17:00', NULL, '01b144c3-329c-447e-bf96-14b865ff170f', 39, 200, 4);
INSERT INTO `busi_template_dept` VALUES (3136, '2021-04-18 14:17:00', NULL, 'd4f6f3c8-2ce0-4c66-b2df-eda312657e0b', 39, 107, 3);
INSERT INTO `busi_template_dept` VALUES (3137, '2021-04-18 14:17:00', NULL, '926e1543-ecba-455d-b89e-f0db6d05211b', 39, 104, 2);
INSERT INTO `busi_template_dept` VALUES (3138, '2021-04-18 14:17:00', NULL, '603bf199-f0f0-4914-99fe-f4571b5c4dcc', 39, 105, 1);

-- ----------------------------
-- Table structure for busi_template_participant
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_participant`;
CREATE TABLE `busi_template_participant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `uuid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板中的与会者的UUID',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `terminal_id` bigint(20) NULL DEFAULT NULL COMMENT '终端ID',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uuid`(`uuid`) USING BTREE,
  INDEX `terminal_id`(`terminal_id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`, `weight`) USING BTREE,
  CONSTRAINT `busi_template_participant_ibfk_2` FOREIGN KEY (`terminal_id`) REFERENCES `busi_terminal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_participant_ibfk_3` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 19274 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议模板的参会者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_participant
-- ----------------------------
INSERT INTO `busi_template_participant` VALUES (2975, '2021-03-30 17:26:27', NULL, 'dcdfcdd5-93b7-42c1-b517-630c21d35735', 42, 180, 1);
INSERT INTO `busi_template_participant` VALUES (2976, '2021-03-30 17:26:27', NULL, '3aefb56f-cb37-49f9-ac7c-b9dbe86a9676', 42, 181, 0);
INSERT INTO `busi_template_participant` VALUES (2977, '2021-03-30 17:26:27', NULL, '8bdd10a0-f921-4fde-9dda-203c76f31230', 42, 182, 0);
INSERT INTO `busi_template_participant` VALUES (2978, '2021-03-30 17:26:27', NULL, '7c5da688-81fd-4c9f-a9d1-29690bf46f04', 42, 183, 0);
INSERT INTO `busi_template_participant` VALUES (2979, '2021-03-30 17:26:27', NULL, '8fc286ef-8093-4822-9807-1be1810a4044', 42, 184, 0);
INSERT INTO `busi_template_participant` VALUES (2980, '2021-03-30 17:26:27', NULL, 'bb71299a-5364-46d9-8bff-13202cac4957', 42, 216, 0);
INSERT INTO `busi_template_participant` VALUES (2981, '2021-03-30 17:26:27', NULL, '2cc502bc-06e9-4436-8a80-cbce7c31c954', 42, 217, 0);
INSERT INTO `busi_template_participant` VALUES (2982, '2021-03-30 17:26:27', NULL, '5eaf51d4-aa79-47ec-993f-82c87a8f4032', 42, 231, 0);
INSERT INTO `busi_template_participant` VALUES (2983, '2021-03-30 17:26:27', NULL, '936aa8cb-367c-4479-84d2-ae829b111810', 42, 232, 0);
INSERT INTO `busi_template_participant` VALUES (2984, '2021-03-30 17:26:27', NULL, 'c8d6e7f9-7244-4df0-9903-9cf0cfa6fd8a', 42, 248, 0);
INSERT INTO `busi_template_participant` VALUES (2985, '2021-03-30 17:26:27', NULL, 'c4123dd2-8dd4-41c8-afe9-15e09847cbe2', 42, 249, 0);
INSERT INTO `busi_template_participant` VALUES (2997, '2021-04-08 18:50:48', NULL, '47c07816-36c6-422e-b0eb-a49a63708c59', 48, 180, 1);
INSERT INTO `busi_template_participant` VALUES (19006, '2021-04-18 14:17:00', NULL, 'd3613161-5785-4f94-bd88-223f76d2764f', 39, 58, 1);
INSERT INTO `busi_template_participant` VALUES (19007, '2021-04-18 14:17:00', NULL, 'cc3fcbd9-90f0-47ee-a786-590a4bf0ca5a', 39, 60, 2);
INSERT INTO `busi_template_participant` VALUES (19008, '2021-04-18 14:17:00', NULL, '1736b569-db98-488e-85b3-e77f1db41bf0', 39, 61, 3);
INSERT INTO `busi_template_participant` VALUES (19009, '2021-04-18 14:17:00', NULL, 'ab65bbc7-82f2-428b-9516-257d85f5f600', 39, 62, 4);
INSERT INTO `busi_template_participant` VALUES (19010, '2021-04-18 14:17:00', NULL, '3ef0174e-c5cc-4728-9f21-145fc8f86029', 39, 59, 5);
INSERT INTO `busi_template_participant` VALUES (19011, '2021-04-18 14:17:00', NULL, 'e083bc3d-babd-4753-b782-19cd10cc7395', 39, 78, 1);
INSERT INTO `busi_template_participant` VALUES (19012, '2021-04-18 14:17:00', NULL, 'eae7bda3-3322-4be6-957e-819c43106661', 39, 77, 2);
INSERT INTO `busi_template_participant` VALUES (19013, '2021-04-18 14:17:00', NULL, 'cdf36ca9-7b79-48dd-a396-3df6e472d099', 39, 76, 3);
INSERT INTO `busi_template_participant` VALUES (19014, '2021-04-18 14:17:00', NULL, '06cea01a-f579-41a5-bd16-29be0099c908', 39, 75, 4);
INSERT INTO `busi_template_participant` VALUES (19015, '2021-04-18 14:17:00', NULL, '555645c1-126a-47ce-b3a0-c1c1b7d78375', 39, 74, 5);
INSERT INTO `busi_template_participant` VALUES (19016, '2021-04-18 14:17:00', NULL, 'a40d63db-efe3-420c-ba58-8d2ba21fb008', 39, 73, 6);
INSERT INTO `busi_template_participant` VALUES (19017, '2021-04-18 14:17:00', NULL, '6a70dc09-92fa-4888-bf7e-5ed1c9f3ab91', 39, 72, 7);
INSERT INTO `busi_template_participant` VALUES (19018, '2021-04-18 14:17:00', NULL, '88ecb3b6-7a95-4466-a77c-ec9b8ce4d91a', 39, 71, 8);
INSERT INTO `busi_template_participant` VALUES (19019, '2021-04-18 14:17:00', NULL, '7ad35a5c-fff4-41f6-bfc6-55cda97dbb60', 39, 70, 9);
INSERT INTO `busi_template_participant` VALUES (19020, '2021-04-18 14:17:00', NULL, '4d3b96df-d84f-46f0-992d-db749824b405', 39, 69, 10);
INSERT INTO `busi_template_participant` VALUES (19021, '2021-04-18 14:17:00', NULL, 'a71b681f-3519-418f-b329-b30fb2dd66b8', 39, 68, 11);
INSERT INTO `busi_template_participant` VALUES (19022, '2021-04-18 14:17:00', NULL, '35d91b81-0817-40a5-ab65-7e123a0bada3', 39, 67, 12);
INSERT INTO `busi_template_participant` VALUES (19023, '2021-04-18 14:17:00', NULL, 'dcce7136-6a61-4f9d-8ac9-470d3cb53f09', 39, 66, 13);
INSERT INTO `busi_template_participant` VALUES (19024, '2021-04-18 14:17:00', NULL, '9a82e089-6981-43bd-b731-fdd39d7e6db0', 39, 85, 1);
INSERT INTO `busi_template_participant` VALUES (19025, '2021-04-18 14:17:00', NULL, 'b4eaddef-9c8d-47e5-90e2-d4e61ab97e09', 39, 84, 2);
INSERT INTO `busi_template_participant` VALUES (19026, '2021-04-18 14:17:00', NULL, '62460f1c-ae8e-4740-b2b2-5dc80e9e73dc', 39, 83, 3);
INSERT INTO `busi_template_participant` VALUES (19027, '2021-04-18 14:17:00', NULL, '22aa2c3c-8b01-497e-9022-19badf6821a2', 39, 82, 4);
INSERT INTO `busi_template_participant` VALUES (19028, '2021-04-18 14:17:00', NULL, 'd9287a2e-2db8-4fb4-bc8c-a07b7f0bad65', 39, 81, 5);
INSERT INTO `busi_template_participant` VALUES (19029, '2021-04-18 14:17:00', NULL, '03d1a540-c53b-4857-8138-15ec09f6549e', 39, 80, 6);
INSERT INTO `busi_template_participant` VALUES (19030, '2021-04-18 14:17:00', NULL, '07cf893a-2e06-4df4-afe1-c1f52961b456', 39, 79, 7);
INSERT INTO `busi_template_participant` VALUES (19031, '2021-04-18 14:17:00', NULL, 'c72f81bb-f4e7-4138-9e1f-90df059e40aa', 39, 98, 1);
INSERT INTO `busi_template_participant` VALUES (19032, '2021-04-18 14:17:00', NULL, '1be57105-2b51-4530-9f32-6138e155f6e2', 39, 97, 2);
INSERT INTO `busi_template_participant` VALUES (19033, '2021-04-18 14:17:00', NULL, 'd4d59daf-6424-45e9-ae8d-645f031d8b5c', 39, 96, 3);
INSERT INTO `busi_template_participant` VALUES (19034, '2021-04-18 14:17:00', NULL, 'cfe57056-fb71-4398-8832-a2e646020f16', 39, 95, 4);
INSERT INTO `busi_template_participant` VALUES (19035, '2021-04-18 14:17:00', NULL, '2a981644-ab82-439c-bc8b-f006be2088df', 39, 94, 5);
INSERT INTO `busi_template_participant` VALUES (19036, '2021-04-18 14:17:00', NULL, '6b8a774c-643f-45a0-bd09-0d374bcb7357', 39, 93, 6);
INSERT INTO `busi_template_participant` VALUES (19037, '2021-04-18 14:17:00', NULL, '43018ab3-b61e-4f2d-a6a0-25d747cc4946', 39, 92, 7);
INSERT INTO `busi_template_participant` VALUES (19038, '2021-04-18 14:17:00', NULL, 'a2c9896c-eaaf-449b-b875-772d95908d24', 39, 91, 8);
INSERT INTO `busi_template_participant` VALUES (19039, '2021-04-18 14:17:00', NULL, '81797359-3018-455e-b417-00f97628b6de', 39, 90, 9);
INSERT INTO `busi_template_participant` VALUES (19040, '2021-04-18 14:17:00', NULL, '51f11795-ed92-41f7-9120-64e7ece30265', 39, 89, 10);
INSERT INTO `busi_template_participant` VALUES (19041, '2021-04-18 14:17:00', NULL, '9a9cc590-19c1-4828-a667-47e278312102', 39, 88, 11);
INSERT INTO `busi_template_participant` VALUES (19042, '2021-04-18 14:17:00', NULL, '89d5d0be-faef-4ac0-9039-705326a2d0b2', 39, 87, 12);
INSERT INTO `busi_template_participant` VALUES (19043, '2021-04-18 14:17:00', NULL, '2056aa0c-8a9c-486d-8180-f46891a96096', 39, 86, 13);
INSERT INTO `busi_template_participant` VALUES (19044, '2021-04-18 14:17:00', NULL, 'd4fe763e-ec02-461f-9004-7148b78e046f', 39, 107, 1);
INSERT INTO `busi_template_participant` VALUES (19045, '2021-04-18 14:17:00', NULL, '9c9ac057-7dbd-4c1d-bbb4-90fc080090ad', 39, 106, 2);
INSERT INTO `busi_template_participant` VALUES (19046, '2021-04-18 14:17:00', NULL, '19de58b7-dd4a-4626-b64d-519895b3f086', 39, 105, 3);
INSERT INTO `busi_template_participant` VALUES (19047, '2021-04-18 14:17:00', NULL, '3e418a02-c970-47f4-b849-6d8b1eaa221c', 39, 104, 4);
INSERT INTO `busi_template_participant` VALUES (19048, '2021-04-18 14:17:00', NULL, '1fb23540-1ee7-4a20-ad3b-1c8e8e66577a', 39, 103, 5);
INSERT INTO `busi_template_participant` VALUES (19049, '2021-04-18 14:17:00', NULL, '6c84eca3-90d2-48e0-b33f-6b9274c9ac45', 39, 102, 6);
INSERT INTO `busi_template_participant` VALUES (19050, '2021-04-18 14:17:00', NULL, '71dd8915-53bf-4fd5-9319-43730632d9e9', 39, 101, 7);
INSERT INTO `busi_template_participant` VALUES (19051, '2021-04-18 14:17:00', NULL, '5efccc60-fd28-47a5-9481-3bab60580240', 39, 100, 8);
INSERT INTO `busi_template_participant` VALUES (19052, '2021-04-18 14:17:00', NULL, '1474e410-a8eb-44c5-ba48-d118bf52a66f', 39, 99, 9);
INSERT INTO `busi_template_participant` VALUES (19053, '2021-04-18 14:17:00', NULL, '47f953aa-b2a7-4875-9bba-88cf20173f8e', 39, 117, 1);
INSERT INTO `busi_template_participant` VALUES (19054, '2021-04-18 14:17:00', NULL, '3cc694d8-21f4-43a5-a09a-60ead1da616c', 39, 116, 2);
INSERT INTO `busi_template_participant` VALUES (19055, '2021-04-18 14:17:00', NULL, '737bb5e1-8a01-4725-be01-3d9c83f86b81', 39, 114, 3);
INSERT INTO `busi_template_participant` VALUES (19056, '2021-04-18 14:17:00', NULL, 'd24df0d7-2521-4d79-a5de-14f96fd9ade0', 39, 113, 4);
INSERT INTO `busi_template_participant` VALUES (19057, '2021-04-18 14:17:00', NULL, '808cf4da-4118-4a42-b313-811a1ebd562e', 39, 112, 5);
INSERT INTO `busi_template_participant` VALUES (19058, '2021-04-18 14:17:00', NULL, '7f99aac1-f944-4c2b-b7f2-afa9dee0c60d', 39, 111, 6);
INSERT INTO `busi_template_participant` VALUES (19059, '2021-04-18 14:17:00', NULL, 'aa380903-af00-49f8-9ea6-46977e01c4a6', 39, 115, 7);
INSERT INTO `busi_template_participant` VALUES (19060, '2021-04-18 14:17:00', NULL, '0529286d-6cd3-49dc-8efc-8a1757ea10ee', 39, 110, 8);
INSERT INTO `busi_template_participant` VALUES (19061, '2021-04-18 14:17:00', NULL, '961eb01b-3b9a-4a22-b91f-99ebf6a03ec2', 39, 109, 9);
INSERT INTO `busi_template_participant` VALUES (19062, '2021-04-18 14:17:00', NULL, 'c99b4ce3-aae0-4ba3-b26a-f578a50e2a46', 39, 108, 10);
INSERT INTO `busi_template_participant` VALUES (19063, '2021-04-18 14:17:00', NULL, 'afb7fe32-50e7-4cdd-b4b8-9c24b34caeb0', 39, 128, 1);
INSERT INTO `busi_template_participant` VALUES (19064, '2021-04-18 14:17:00', NULL, 'e81488eb-b98f-4f5b-9f2b-0505df71ba55', 39, 127, 2);
INSERT INTO `busi_template_participant` VALUES (19065, '2021-04-18 14:17:00', NULL, '4bf4a493-e38c-4ec9-91c6-dd3f3b77f2ee', 39, 126, 3);
INSERT INTO `busi_template_participant` VALUES (19066, '2021-04-18 14:17:00', NULL, '77aa747e-a5df-473c-846a-7f2a41608df3', 39, 125, 4);
INSERT INTO `busi_template_participant` VALUES (19067, '2021-04-18 14:17:00', NULL, '431998a8-d766-456a-875f-214d7bb1a539', 39, 124, 5);
INSERT INTO `busi_template_participant` VALUES (19068, '2021-04-18 14:17:00', NULL, 'f13b2053-63a3-4bb3-9d26-0f857b51ebce', 39, 123, 6);
INSERT INTO `busi_template_participant` VALUES (19069, '2021-04-18 14:17:00', NULL, '3395eca9-7cde-4405-a1bd-c93e054d7741', 39, 122, 7);
INSERT INTO `busi_template_participant` VALUES (19070, '2021-04-18 14:17:00', NULL, '75cdc235-790a-4815-99fa-44523661e9e3', 39, 121, 8);
INSERT INTO `busi_template_participant` VALUES (19071, '2021-04-18 14:17:00', NULL, '3db5817d-32f8-48bb-aee3-55ba14db33d6', 39, 120, 9);
INSERT INTO `busi_template_participant` VALUES (19072, '2021-04-18 14:17:00', NULL, 'd7ce52b1-89d2-4ab8-816f-14040d1bcd18', 39, 119, 10);
INSERT INTO `busi_template_participant` VALUES (19073, '2021-04-18 14:17:00', NULL, '591ff989-cda0-4943-a8d5-633774e6262d', 39, 118, 11);
INSERT INTO `busi_template_participant` VALUES (19074, '2021-04-18 14:17:00', NULL, '4e75dd1c-0731-4616-a483-aff9de3fc572', 39, 133, 1);
INSERT INTO `busi_template_participant` VALUES (19075, '2021-04-18 14:17:00', NULL, '539f4bd7-6822-40ec-9a6b-fc851a169219', 39, 132, 2);
INSERT INTO `busi_template_participant` VALUES (19076, '2021-04-18 14:17:00', NULL, 'f4845cc7-7d64-441f-aaa7-d3bf17b1f2f0', 39, 131, 3);
INSERT INTO `busi_template_participant` VALUES (19077, '2021-04-18 14:17:00', NULL, 'dc942300-9e7f-42f7-9f17-491b3cdaf766', 39, 130, 4);
INSERT INTO `busi_template_participant` VALUES (19078, '2021-04-18 14:17:00', NULL, 'ed3b992e-e8be-4ec9-a795-d14ee7e0514d', 39, 129, 5);
INSERT INTO `busi_template_participant` VALUES (19079, '2021-04-18 14:17:00', NULL, 'affa5d2a-d28d-4c09-a4af-bda1fc88c8c9', 39, 137, 1);
INSERT INTO `busi_template_participant` VALUES (19080, '2021-04-18 14:17:00', NULL, '89089f9e-fec2-431f-a2f3-951ffa7281f1', 39, 136, 2);
INSERT INTO `busi_template_participant` VALUES (19081, '2021-04-18 14:17:00', NULL, '5d6584d8-2ad0-45b2-84f1-b5df383aa10c', 39, 135, 3);
INSERT INTO `busi_template_participant` VALUES (19082, '2021-04-18 14:17:00', NULL, '7828bf92-e895-4994-a678-4b84ef25a846', 39, 134, 4);
INSERT INTO `busi_template_participant` VALUES (19083, '2021-04-18 14:17:00', NULL, '2beb1b0b-2a66-4ba6-9f7c-ac54024d6b74', 39, 147, 1);
INSERT INTO `busi_template_participant` VALUES (19084, '2021-04-18 14:17:00', NULL, 'be243b7b-e876-4eb4-922b-2c94273e0d16', 39, 146, 2);
INSERT INTO `busi_template_participant` VALUES (19085, '2021-04-18 14:17:00', NULL, '2a2d5a39-343d-41b2-8682-9176477faea2', 39, 145, 3);
INSERT INTO `busi_template_participant` VALUES (19086, '2021-04-18 14:17:00', NULL, 'caca9db5-3c71-4ce7-a2ab-ca51673c5e4b', 39, 144, 4);
INSERT INTO `busi_template_participant` VALUES (19087, '2021-04-18 14:17:00', NULL, 'e1889db3-8d87-49d7-94c9-f7f66536b6ed', 39, 143, 5);
INSERT INTO `busi_template_participant` VALUES (19088, '2021-04-18 14:17:00', NULL, '606dadd3-e41c-410a-89ac-f5234222393c', 39, 142, 6);
INSERT INTO `busi_template_participant` VALUES (19089, '2021-04-18 14:17:00', NULL, '7dc5b3cf-9570-40d3-a52b-bfd29df2a12b', 39, 141, 7);
INSERT INTO `busi_template_participant` VALUES (19090, '2021-04-18 14:17:00', NULL, '8db952df-45b1-41f4-a080-76913790872a', 39, 140, 8);
INSERT INTO `busi_template_participant` VALUES (19091, '2021-04-18 14:17:00', NULL, 'c158ddbb-4cfe-4b71-9296-aba691338e1d', 39, 139, 9);
INSERT INTO `busi_template_participant` VALUES (19092, '2021-04-18 14:17:00', NULL, '5ac69ab1-bfb9-431a-b1bc-bbc326646079', 39, 138, 10);
INSERT INTO `busi_template_participant` VALUES (19093, '2021-04-18 14:17:00', NULL, '7771a806-5301-46ed-9cb0-f5b8de7706fd', 39, 156, 1);
INSERT INTO `busi_template_participant` VALUES (19094, '2021-04-18 14:17:00', NULL, '1d993acc-c078-4c48-9f40-f794ea3a549d', 39, 155, 2);
INSERT INTO `busi_template_participant` VALUES (19095, '2021-04-18 14:17:00', NULL, 'd0e52032-a9e4-4774-9f32-30be7eca88f1', 39, 154, 3);
INSERT INTO `busi_template_participant` VALUES (19096, '2021-04-18 14:17:00', NULL, 'a2c3a04b-a425-427d-8ea0-4b7b678faa19', 39, 153, 4);
INSERT INTO `busi_template_participant` VALUES (19097, '2021-04-18 14:17:00', NULL, '12b242fb-fc68-4610-a470-3446624cb89d', 39, 152, 5);
INSERT INTO `busi_template_participant` VALUES (19098, '2021-04-18 14:17:00', NULL, 'b6907b07-0c05-4be7-93f8-45247b0f1335', 39, 151, 6);
INSERT INTO `busi_template_participant` VALUES (19099, '2021-04-18 14:17:00', NULL, '5dd505e1-3af0-4906-b5b9-8984d79ae0ca', 39, 150, 7);
INSERT INTO `busi_template_participant` VALUES (19100, '2021-04-18 14:17:00', NULL, '2f17cd8d-2d3e-4caa-9065-36c6d383f243', 39, 149, 8);
INSERT INTO `busi_template_participant` VALUES (19101, '2021-04-18 14:17:00', NULL, 'a01effca-c9f3-4663-8276-6dbf9b0e909d', 39, 148, 9);
INSERT INTO `busi_template_participant` VALUES (19102, '2021-04-18 14:17:00', NULL, '05551e09-e844-4967-a129-7661781b3367', 39, 46, 10);
INSERT INTO `busi_template_participant` VALUES (19103, '2021-04-18 14:17:00', NULL, 'd3d9b0a6-9575-4ce6-91ff-1aadb497d5fd', 39, 163, 1);
INSERT INTO `busi_template_participant` VALUES (19104, '2021-04-18 14:17:00', NULL, '6a9c9caa-1161-42a4-bec0-379bcb4e9246', 39, 162, 2);
INSERT INTO `busi_template_participant` VALUES (19105, '2021-04-18 14:17:00', NULL, 'e110b240-da06-4b3f-bf5f-cfc355586c90', 39, 161, 3);
INSERT INTO `busi_template_participant` VALUES (19106, '2021-04-18 14:17:00', NULL, '62d3e38d-8ff3-4fc2-9790-917cf99fc43d', 39, 160, 4);
INSERT INTO `busi_template_participant` VALUES (19107, '2021-04-18 14:17:00', NULL, 'f7e564df-644a-4b9a-a30f-5e55892c07db', 39, 159, 5);
INSERT INTO `busi_template_participant` VALUES (19108, '2021-04-18 14:17:00', NULL, 'af23a506-1ee9-40fe-abc3-87663246a19b', 39, 158, 6);
INSERT INTO `busi_template_participant` VALUES (19109, '2021-04-18 14:17:00', NULL, 'bef0fff4-bd9f-4e2b-b109-b61e15d76d2b', 39, 157, 7);
INSERT INTO `busi_template_participant` VALUES (19110, '2021-04-18 14:17:00', NULL, '96a08d13-bd99-4b69-a259-daa94b746702', 39, 47, 8);
INSERT INTO `busi_template_participant` VALUES (19111, '2021-04-18 14:17:00', NULL, '50af82e2-80a7-4657-8e88-ded39485a594', 39, 171, 1);
INSERT INTO `busi_template_participant` VALUES (19112, '2021-04-18 14:17:00', NULL, '5d44e5f0-152c-4712-8392-b71e5663b3f8', 39, 170, 2);
INSERT INTO `busi_template_participant` VALUES (19113, '2021-04-18 14:17:00', NULL, '595c83cc-7096-4472-99cd-ebe14630dd3d', 39, 169, 3);
INSERT INTO `busi_template_participant` VALUES (19114, '2021-04-18 14:17:00', NULL, '2239448a-5aff-432e-846e-c620204dc966', 39, 168, 4);
INSERT INTO `busi_template_participant` VALUES (19115, '2021-04-18 14:17:00', NULL, 'f57206b6-d5ff-4eff-b328-06ff10b69620', 39, 167, 5);
INSERT INTO `busi_template_participant` VALUES (19116, '2021-04-18 14:17:00', NULL, '9026cd1a-ef4a-498c-a3ed-5e4e13aa17bd', 39, 166, 6);
INSERT INTO `busi_template_participant` VALUES (19117, '2021-04-18 14:17:00', NULL, '953a41fd-6cec-4bc1-86a4-708b1489528e', 39, 165, 7);
INSERT INTO `busi_template_participant` VALUES (19118, '2021-04-18 14:17:00', NULL, '815ef3ad-92b4-4cf7-a4fd-6e1eca57b28c', 39, 164, 8);
INSERT INTO `busi_template_participant` VALUES (19119, '2021-04-18 14:17:00', NULL, 'bfdb419a-b28e-468d-9c25-64700d6cf975', 39, 36, 9);
INSERT INTO `busi_template_participant` VALUES (19120, '2021-04-18 14:17:00', NULL, '311754b2-21c7-4afd-9221-d6f838a6f28a', 39, 176, 1);
INSERT INTO `busi_template_participant` VALUES (19121, '2021-04-18 14:17:00', NULL, '1c2d3ace-b0ed-48b1-a8cf-4348a9cc1b62', 39, 175, 2);
INSERT INTO `busi_template_participant` VALUES (19122, '2021-04-18 14:17:00', NULL, 'aa806e38-f24c-4db0-aaa6-db834f833c2b', 39, 174, 3);
INSERT INTO `busi_template_participant` VALUES (19123, '2021-04-18 14:17:00', NULL, '9dc0757d-5bd3-4e66-b355-3213d048ddbb', 39, 173, 4);
INSERT INTO `busi_template_participant` VALUES (19124, '2021-04-18 14:17:00', NULL, 'd7cd3b5f-b45d-45b7-97c9-7c0d6eb8e312', 39, 172, 5);
INSERT INTO `busi_template_participant` VALUES (19125, '2021-04-18 14:17:00', NULL, '08f1d7c2-375b-474c-a2c9-8e059ed62d72', 39, 48, 6);
INSERT INTO `busi_template_participant` VALUES (19126, '2021-04-18 14:17:00', NULL, '4b2759bc-65c0-415e-9bcb-d5731ab4ab3b', 39, 179, 1);
INSERT INTO `busi_template_participant` VALUES (19127, '2021-04-18 14:17:00', NULL, '9d548f74-f628-44fb-904e-e952ceb242f2', 39, 178, 2);
INSERT INTO `busi_template_participant` VALUES (19128, '2021-04-18 14:17:00', NULL, 'a1641ee7-bec2-4b62-8f0c-357cc04fb1c1', 39, 177, 3);
INSERT INTO `busi_template_participant` VALUES (19129, '2021-04-18 14:17:00', NULL, 'd91a29ef-551d-4bca-8277-e3632535f77f', 39, 53, 4);
INSERT INTO `busi_template_participant` VALUES (19130, '2021-04-18 14:17:00', NULL, '170e5a29-2d2c-4d22-829b-c779a1d8b69f', 39, 49, 5);
INSERT INTO `busi_template_participant` VALUES (19154, '2021-04-18 15:37:04', NULL, '4942be5c-3b9a-4aa0-8d20-bcab3e74438b', 64, 264, 1);
INSERT INTO `busi_template_participant` VALUES (19155, '2021-04-18 15:37:04', NULL, '0e80618b-597e-4725-9efa-6f448f866ba9', 64, 263, 2);
INSERT INTO `busi_template_participant` VALUES (19156, '2021-04-18 15:37:04', NULL, 'a14a20e0-db53-406a-bf2e-869b7ffa8d9b', 64, 175, 3);
INSERT INTO `busi_template_participant` VALUES (19157, '2021-04-18 15:37:04', NULL, '0372d07d-fb3b-4d10-967e-1a516f24ca0b', 64, 134, 4);
INSERT INTO `busi_template_participant` VALUES (19158, '2021-04-18 15:37:04', NULL, 'b3d36619-8bf6-4823-a9eb-e92b6a5ec3d8', 64, 80, 5);
INSERT INTO `busi_template_participant` VALUES (19159, '2021-04-18 15:37:04', NULL, '82b7fccb-c3f6-4133-9f0c-92b6dc89b1cc', 64, 79, 6);
INSERT INTO `busi_template_participant` VALUES (19160, '2021-04-18 15:37:04', NULL, '48c4f900-2c31-4652-afba-bf0ca140cbb0', 64, 67, 7);
INSERT INTO `busi_template_participant` VALUES (19161, '2021-04-18 15:37:04', NULL, 'c5235052-ff99-456b-9232-6b9b5d8f6266', 64, 66, 8);
INSERT INTO `busi_template_participant` VALUES (19162, '2021-04-18 15:37:04', NULL, '04a4971f-5e66-4583-8312-7c0432d08b1e', 64, 60, 11);
INSERT INTO `busi_template_participant` VALUES (19163, '2021-04-18 15:37:04', NULL, '11a56ff7-25a8-4c35-ba00-74f23997bbd4', 64, 59, 12);
INSERT INTO `busi_template_participant` VALUES (19164, '2021-04-18 15:37:04', NULL, 'bfa79369-dc2c-4993-9678-a782eac7a622', 64, 49, 14);
INSERT INTO `busi_template_participant` VALUES (19165, '2021-04-18 15:37:04', NULL, 'fbab4757-ed65-4c4b-a629-e03e08938ac8', 64, 265, 0);

-- ----------------------------
-- Table structure for busi_template_polling_dept
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_polling_dept`;
CREATE TABLE `busi_template_polling_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `polling_scheme_id` bigint(20) NULL DEFAULT NULL COMMENT '归属轮询方案ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID（部门也是FME终端，一种与会者）',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`) USING BTREE,
  INDEX `polling_scheme_id`(`polling_scheme_id`) USING BTREE,
  CONSTRAINT `busi_template_polling_dept_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_polling_dept_ibfk_2` FOREIGN KEY (`polling_scheme_id`) REFERENCES `busi_template_polling_scheme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1494 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮询方案的部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_polling_dept
-- ----------------------------
INSERT INTO `busi_template_polling_dept` VALUES (1438, '2021-04-18 15:07:05', NULL, 39, 57, 100, 15);
INSERT INTO `busi_template_polling_dept` VALUES (1439, '2021-04-18 15:07:05', NULL, 39, 57, 103, 14);
INSERT INTO `busi_template_polling_dept` VALUES (1440, '2021-04-18 15:07:05', NULL, 39, 57, 205, 13);
INSERT INTO `busi_template_polling_dept` VALUES (1441, '2021-04-18 15:07:05', NULL, 39, 57, 106, 12);
INSERT INTO `busi_template_polling_dept` VALUES (1442, '2021-04-18 15:07:05', NULL, 39, 57, 206, 11);
INSERT INTO `busi_template_polling_dept` VALUES (1443, '2021-04-18 15:07:05', NULL, 39, 57, 208, 10);
INSERT INTO `busi_template_polling_dept` VALUES (1444, '2021-04-18 15:07:05', NULL, 39, 57, 207, 9);
INSERT INTO `busi_template_polling_dept` VALUES (1445, '2021-04-18 15:07:05', NULL, 39, 57, 204, 8);
INSERT INTO `busi_template_polling_dept` VALUES (1446, '2021-04-18 15:07:05', NULL, 39, 57, 203, 7);
INSERT INTO `busi_template_polling_dept` VALUES (1447, '2021-04-18 15:07:05', NULL, 39, 57, 202, 6);
INSERT INTO `busi_template_polling_dept` VALUES (1448, '2021-04-18 15:07:05', NULL, 39, 57, 201, 5);
INSERT INTO `busi_template_polling_dept` VALUES (1449, '2021-04-18 15:07:05', NULL, 39, 57, 200, 4);
INSERT INTO `busi_template_polling_dept` VALUES (1450, '2021-04-18 15:07:05', NULL, 39, 57, 107, 3);
INSERT INTO `busi_template_polling_dept` VALUES (1451, '2021-04-18 15:07:05', NULL, 39, 57, 104, 2);
INSERT INTO `busi_template_polling_dept` VALUES (1452, '2021-04-18 15:07:05', NULL, 39, 57, 105, 1);
INSERT INTO `busi_template_polling_dept` VALUES (1493, '2021-04-20 04:07:01', NULL, 64, 59, 100, 1);

-- ----------------------------
-- Table structure for busi_template_polling_paticipant
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_polling_paticipant`;
CREATE TABLE `busi_template_polling_paticipant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `polling_scheme_id` bigint(20) NULL DEFAULT NULL COMMENT '归属轮询方案ID',
  `polling_interval` int(11) NULL DEFAULT NULL COMMENT '该参会者的特定的轮询间隔（如果该值存在，会覆盖轮询方案终端间隔）',
  `remote_party` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '终端的远程部分（唯一的）',
  `terminal_id` bigint(20) NULL DEFAULT NULL COMMENT '终端ID',
  `is_cascade_main` int(11) NULL DEFAULT NULL COMMENT '是否级联之主(1是，2否)',
  `weight` int(11) NULL DEFAULT NULL COMMENT '参会者顺序（权重倒叙排列）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`) USING BTREE,
  INDEX `polling_scheme_id`(`polling_scheme_id`) USING BTREE,
  INDEX `remote_party`(`remote_party`) USING BTREE,
  CONSTRAINT `busi_template_polling_paticipant_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_template_polling_paticipant_ibfk_2` FOREIGN KEY (`polling_scheme_id`) REFERENCES `busi_template_polling_scheme` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 8875 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮询方案的参会者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_polling_paticipant
-- ----------------------------
INSERT INTO `busi_template_polling_paticipant` VALUES (8320, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.0.81', 62, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8321, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.0.71', 61, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8322, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.0.51', 60, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8323, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.0.61', 58, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8324, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.1.12', 66, 1, 13);
INSERT INTO `busi_template_polling_paticipant` VALUES (8325, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.1.21', 67, 2, 12);
INSERT INTO `busi_template_polling_paticipant` VALUES (8326, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.1.31', 68, 2, 11);
INSERT INTO `busi_template_polling_paticipant` VALUES (8327, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.1.41', 69, 2, 10);
INSERT INTO `busi_template_polling_paticipant` VALUES (8328, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.1.51', 70, 2, 9);
INSERT INTO `busi_template_polling_paticipant` VALUES (8329, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.1.61', 71, 2, 8);
INSERT INTO `busi_template_polling_paticipant` VALUES (8330, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.1.71', 72, 2, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8331, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.1.81', 73, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8332, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.1.91', 74, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8333, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.1.101', 75, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8334, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.1.111', 76, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8335, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.1.121', 77, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8336, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.1.131', 78, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8337, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.11.12', 47, 1, 8);
INSERT INTO `busi_template_polling_paticipant` VALUES (8338, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.11.21', 157, 2, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8339, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.11.31', 158, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8340, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.11.41', 159, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8341, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.11.51', 160, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8342, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.11.61', 161, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8343, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.11.71', 162, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8344, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.11.81', 163, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8345, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.4.12', 99, 1, 9);
INSERT INTO `busi_template_polling_paticipant` VALUES (8346, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.4.21', 100, 2, 8);
INSERT INTO `busi_template_polling_paticipant` VALUES (8347, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.4.31', 101, 2, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8348, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.4.41', 102, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8349, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.4.51', 103, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8350, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.4.61', 104, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8351, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.4.71', 105, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8352, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.4.81', 106, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8353, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.4.91', 107, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8354, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.12.12', 36, 1, 9);
INSERT INTO `busi_template_polling_paticipant` VALUES (8355, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.12.21', 164, 2, 8);
INSERT INTO `busi_template_polling_paticipant` VALUES (8356, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.12.31', 165, 2, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8357, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.12.41', 166, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8358, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.12.51', 167, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8359, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.12.61', 168, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8360, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.12.71', 169, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8361, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.12.81', 170, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8362, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.12.91', 171, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8363, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.14.12', 49, 1, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8364, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.14.21', 53, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8365, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.14.31', 177, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8366, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.14.41', 178, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8367, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.14.51', 179, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8368, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.13.12', 48, 1, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8369, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.13.21', 172, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8370, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.13.31', 173, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8371, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.13.41', 174, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8372, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.13.51', 175, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8373, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.13.61', 176, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8374, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.10.12', 46, 1, 10);
INSERT INTO `busi_template_polling_paticipant` VALUES (8375, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.10.21', 148, 2, 9);
INSERT INTO `busi_template_polling_paticipant` VALUES (8376, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.10.31', 149, 2, 8);
INSERT INTO `busi_template_polling_paticipant` VALUES (8377, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.10.41', 150, 2, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8378, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.10.51', 151, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8379, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.10.61', 152, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8380, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.10.71', 153, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8381, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.10.81', 154, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8382, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.10.91', 155, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8383, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.10.101', 156, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8384, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.9.12', 138, 1, 10);
INSERT INTO `busi_template_polling_paticipant` VALUES (8385, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.9.21', 139, 2, 9);
INSERT INTO `busi_template_polling_paticipant` VALUES (8386, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.9.31', 140, 2, 8);
INSERT INTO `busi_template_polling_paticipant` VALUES (8387, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.9.41', 141, 2, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8388, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.9.51', 142, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8389, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.9.61', 143, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8390, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.9.71', 144, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8391, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.9.81', 145, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8392, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.9.91', 146, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8393, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.9.101', 147, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8394, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.8.12', 134, 1, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8395, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.8.21', 135, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8396, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.8.31', 136, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8397, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.8.41', 137, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8398, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.7.12', 129, 1, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8399, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.7.21', 130, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8400, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.7.31', 131, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8401, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.7.41', 132, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8402, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.7.51', 133, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8403, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.6.12', 118, 1, 11);
INSERT INTO `busi_template_polling_paticipant` VALUES (8404, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.6.21', 119, 2, 10);
INSERT INTO `busi_template_polling_paticipant` VALUES (8405, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.6.31', 120, 2, 9);
INSERT INTO `busi_template_polling_paticipant` VALUES (8406, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.6.41', 121, 2, 8);
INSERT INTO `busi_template_polling_paticipant` VALUES (8407, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.6.51', 122, 2, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8408, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.6.61', 123, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8409, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.6.71', 124, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8410, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.6.81', 125, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8411, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.6.91', 126, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8412, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.6.101', 127, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8413, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.6.111', 128, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8414, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.5.12', 108, 1, 10);
INSERT INTO `busi_template_polling_paticipant` VALUES (8415, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.5.21', 109, 2, 9);
INSERT INTO `busi_template_polling_paticipant` VALUES (8416, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.5.31', 110, 2, 8);
INSERT INTO `busi_template_polling_paticipant` VALUES (8417, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.5.41', 115, 2, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8418, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.5.51', 111, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8419, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.5.61', 112, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8420, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.5.71', 113, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8421, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.5.81', 114, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8422, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.5.91', 116, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8423, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.5.101', 117, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8424, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.2.12', 79, 1, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8425, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.2.21', 80, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8426, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.2.31', 81, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8427, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.2.41', 82, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8428, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.2.51', 83, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8429, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.2.61', 84, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8430, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.2.71', 85, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8431, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.3.12', 86, 1, 13);
INSERT INTO `busi_template_polling_paticipant` VALUES (8432, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.3.21', 87, 2, 12);
INSERT INTO `busi_template_polling_paticipant` VALUES (8433, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.3.31', 88, 2, 11);
INSERT INTO `busi_template_polling_paticipant` VALUES (8434, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.3.41', 89, 2, 10);
INSERT INTO `busi_template_polling_paticipant` VALUES (8435, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.3.51', 90, 2, 9);
INSERT INTO `busi_template_polling_paticipant` VALUES (8436, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.3.61', 91, 2, 8);
INSERT INTO `busi_template_polling_paticipant` VALUES (8437, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.3.71', 92, 2, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8438, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.3.81', 93, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8439, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.3.91', 94, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8440, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.3.101', 95, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8441, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.3.111', 96, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8442, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.3.121', 97, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8443, '2021-04-18 15:07:05', NULL, 39, 57, NULL, '192.166.3.131', 98, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8864, '2021-04-20 04:07:01', NULL, 64, 59, NULL, '192.166.14.12', 49, 2, 11);
INSERT INTO `busi_template_polling_paticipant` VALUES (8865, '2021-04-20 04:07:01', NULL, 64, 59, NULL, '192.166.0.51', 60, 2, 10);
INSERT INTO `busi_template_polling_paticipant` VALUES (8866, '2021-04-20 04:07:01', NULL, 64, 59, NULL, '192.166.1.12', 66, 2, 9);
INSERT INTO `busi_template_polling_paticipant` VALUES (8867, '2021-04-20 04:07:01', NULL, 64, 59, NULL, '192.166.1.21', 67, 2, 8);
INSERT INTO `busi_template_polling_paticipant` VALUES (8868, '2021-04-20 04:07:01', NULL, 64, 59, NULL, '192.166.2.12', 79, 2, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8869, '2021-04-20 04:07:01', NULL, 64, 59, NULL, '192.166.2.21', 80, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8870, '2021-04-20 04:07:01', NULL, 64, 59, NULL, '192.166.8.12', 134, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8871, '2021-04-20 04:07:01', NULL, 64, 59, NULL, '192.166.13.51', 175, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8872, '2021-04-20 04:07:01', NULL, 64, 59, NULL, '192.166.0.77', 263, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8873, '2021-04-20 04:07:01', NULL, 64, 59, NULL, '192.166.0.187', 264, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8874, '2021-04-20 04:07:01', NULL, 64, 59, NULL, '192.166.0.150', 265, 2, 1);

-- ----------------------------
-- Table structure for busi_template_polling_scheme
-- ----------------------------
DROP TABLE IF EXISTS `busi_template_polling_scheme`;
CREATE TABLE `busi_template_polling_scheme`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `template_conference_id` bigint(20) NULL DEFAULT NULL COMMENT '会议模板ID',
  `polling_interval` int(11) NULL DEFAULT NULL COMMENT '轮询时间间隔',
  `polling_state_first` int(11) NULL DEFAULT NULL COMMENT '是否需要优先轮询地州（1是，2否）',
  `scheme_name` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '轮询方案名',
  `weight` int(11) NULL DEFAULT NULL COMMENT '轮询方案顺序，越大越靠前',
  `layout` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '多分频轮询支持',
  `is_broadcast` int(11) NULL DEFAULT NULL COMMENT '是否广播(1是，2否)',
  `is_display_self` int(11) NULL DEFAULT NULL COMMENT '是否显示自己(1是，2否)',
  `is_fill` int(11) NULL DEFAULT NULL COMMENT '是否补位(1是，2否)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `template_conference_id`(`template_conference_id`) USING BTREE,
  CONSTRAINT `busi_template_polling_scheme_ibfk_1` FOREIGN KEY (`template_conference_id`) REFERENCES `busi_template_conference` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮询方案' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_polling_scheme
-- ----------------------------
INSERT INTO `busi_template_polling_scheme` VALUES (57, '2021-04-13 18:22:42', '2021-04-18 15:07:05', 39, 4, 1, 'das', 1, 'allEqualQuarters', 1, 1, 1);
INSERT INTO `busi_template_polling_scheme` VALUES (59, '2021-04-18 15:20:39', '2021-04-20 04:07:01', 64, 3, 1, '测试多分屏轮询', 1, 'speakerOnly', 2, 2, 1);

-- ----------------------------
-- Table structure for busi_terminal
-- ----------------------------
DROP TABLE IF EXISTS `busi_terminal`;
CREATE TABLE `busi_terminal`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '终端创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '终端修改时间',
  `create_user_id` bigint(20) NULL DEFAULT NULL COMMENT '创建者ID',
  `create_user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建者用户名',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '终端所属部门ID',
  `ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备的IP地址',
  `number` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备号，设备唯一标识（如果是sfbc终端，则对应凭据）',
  `camera_ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '摄像头IP地址',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '终端显示名字',
  `type` int(11) NULL DEFAULT NULL COMMENT '终端类型，枚举值int类型',
  `online_status` int(11) NULL DEFAULT NULL COMMENT '终端状态：1在线，2离线',
  `protocol` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '协议',
  `registration_time` datetime(0) NULL DEFAULT NULL COMMENT 'SFBC终端最后注册时间',
  `intranet_ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SFBC终端内网IP',
  `port` int(11) NULL DEFAULT NULL COMMENT 'SFBC终端端口',
  `transport` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'SFBC终端的传输协议（TLS,TCP,UDP）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ip`(`ip`, `number`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  INDEX `create_user_id`(`create_user_id`) USING BTREE,
  CONSTRAINT `busi_terminal_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_terminal_ibfk_2` FOREIGN KEY (`create_user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 269 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '终端信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_terminal
-- ----------------------------
INSERT INTO `busi_terminal` VALUES (36, '2021-02-04 13:12:17', '2021-03-09 16:14:15', 1, 'superAdmin', 206, '192.166.12.12', NULL, '192.166.12.13', '阿勒泰地区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (46, '2021-02-20 14:40:39', '2021-03-09 16:06:55', 1, 'superAdmin', 204, '192.166.10.12', NULL, '192.166.10.13', '昌吉州', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (47, '2021-02-20 14:41:00', '2021-03-09 16:10:45', 1, 'superAdmin', 205, '192.166.11.12', NULL, '192.166.11.13', '塔城地区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (48, '2021-02-20 14:41:48', '2021-03-09 16:16:59', 1, 'superAdmin', 207, '192.166.13.12', NULL, '192.166.13.13', '博州', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (49, '2021-02-20 14:42:00', '2021-04-18 15:12:56', 1, 'superAdmin', 100, '192.166.14.12', NULL, '192.166.14.13', '克拉玛依', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (53, '2021-02-23 18:43:14', '2021-03-09 16:36:19', 1, 'superAdmin', 208, '192.166.14.21', NULL, '192.166.14.22', '克拉玛依区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (58, '2021-03-09 15:15:46', NULL, 1, 'superAdmin', 100, '192.166.0.61', NULL, NULL, '指挥部', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (59, '2021-03-09 15:16:40', NULL, 1, 'superAdmin', 100, '192.166.0.22', NULL, NULL, '二会', 100, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (60, '2021-03-09 15:17:05', NULL, 1, 'superAdmin', 100, '192.166.0.51', NULL, NULL, '控制中兴', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (61, '2021-03-09 15:17:41', NULL, 1, 'superAdmin', 100, '192.166.0.71', NULL, NULL, '兵团指挥部', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (62, '2021-03-09 15:18:02', NULL, 1, 'superAdmin', 100, '192.166.0.81', NULL, NULL, '宣传干部学院', 0, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (66, '2021-03-09 15:20:17', '2021-04-18 15:12:34', 1, 'superAdmin', 100, '192.166.1.12', NULL, '192.166.1.13', '伊犁州', 100, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (67, '2021-03-09 15:20:31', '2021-04-18 15:12:42', 1, 'superAdmin', 100, '192.166.1.21', NULL, '192.166.1.22', '伊宁市', 100, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (68, '2021-03-09 15:20:50', NULL, 1, 'superAdmin', 103, '192.166.1.31', NULL, '192.166.1.32', '伊宁县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (69, '2021-03-09 15:21:05', NULL, 1, 'superAdmin', 103, '192.166.1.41', NULL, '192.166.1.42', '霍尔果斯市', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (70, '2021-03-09 15:21:21', NULL, 1, 'superAdmin', 103, '192.166.1.51', NULL, '192.166.1.52', '霍城县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (71, '2021-03-09 15:21:37', NULL, 1, 'superAdmin', 103, '192.166.1.61', NULL, '192.166.1.62', '巩留县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (72, '2021-03-09 15:22:07', NULL, 1, 'superAdmin', 103, '192.166.1.71', NULL, '192.166.1.72', '察布查尔县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (73, '2021-03-09 15:22:23', NULL, 1, 'superAdmin', 103, '192.166.1.81', NULL, '192.166.1.82', '昭苏县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (74, '2021-03-09 15:22:39', NULL, 1, 'superAdmin', 103, '192.166.1.91', NULL, '192.166.1.92', '特克斯县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (75, '2021-03-09 15:23:36', NULL, 1, 'superAdmin', 103, '192.166.1.101', NULL, '192.166.1.102', '奎屯市', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (76, '2021-03-09 15:23:52', NULL, 1, 'superAdmin', 103, '192.166.1.111', NULL, '192.166.1.112', '新源县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (77, '2021-03-09 15:24:10', NULL, 1, 'superAdmin', 103, '192.166.1.121', NULL, '192.166.1.122', '尼勒克县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (78, '2021-03-09 15:24:26', NULL, 1, 'superAdmin', 103, '192.166.1.131', NULL, '192.166.1.132', '都拉塔口岸', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (79, '2021-03-09 15:24:58', '2021-04-18 15:18:04', 1, 'superAdmin', 100, '192.166.2.12', NULL, '192.166.2.13', '克州', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (80, '2021-03-09 15:25:11', '2021-04-18 15:18:07', 1, 'superAdmin', 100, '192.166.2.21', NULL, '192.166.2.22', '阿图什市', 100, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (81, '2021-03-09 15:25:28', NULL, 1, 'superAdmin', 104, '192.166.2.31', NULL, '192.166.2.32', '阿克陶县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (82, '2021-03-09 15:25:41', NULL, 1, 'superAdmin', 104, '192.166.2.41', NULL, '192.166.2.42', '阿合奇县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (83, '2021-03-09 15:25:56', NULL, 1, 'superAdmin', 104, '192.166.2.51', NULL, '192.166.2.52', '乌恰县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (84, '2021-03-09 15:26:15', NULL, 1, 'superAdmin', 104, '192.166.2.61', NULL, '192.166.2.62', '伊尔克什坦口岸', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (85, '2021-03-09 15:26:27', NULL, 1, 'superAdmin', 104, '192.166.2.71', NULL, '192.166.2.72', '吐尔尕特口岸', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (86, '2021-03-09 15:33:17', '2021-03-09 15:34:08', 1, 'superAdmin', 105, '192.166.3.12', NULL, '192.166.3.13', '喀什地区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (87, '2021-03-09 15:33:33', NULL, 1, 'superAdmin', 105, '192.166.3.21', NULL, '192.166.3.22', '喀什市', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (88, '2021-03-09 15:34:22', NULL, 1, 'superAdmin', 105, '192.166.3.31', NULL, '192.166.3.32', '塔什库尔干县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (89, '2021-03-09 15:34:41', NULL, 1, 'superAdmin', 105, '192.166.3.41', NULL, '192.166.3.42', '伽师县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (90, '2021-03-09 15:34:54', NULL, 1, 'superAdmin', 105, '192.166.3.51', NULL, '192.166.3.52', '疏勒县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (91, '2021-03-09 15:35:07', NULL, 1, 'superAdmin', 105, '192.166.3.61', NULL, '192.166.3.62', '疏附县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (92, '2021-03-09 15:35:21', NULL, 1, 'superAdmin', 105, '192.166.3.71', NULL, '192.166.3.72', '岳普湖县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (93, '2021-03-09 15:35:42', NULL, 1, 'superAdmin', 105, '192.166.3.81', NULL, '192.166.3.82', '莎车县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (94, '2021-03-09 15:36:04', NULL, 1, 'superAdmin', 105, '192.166.3.91', NULL, '192.166.3.92', '英吉沙县', 0, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (95, '2021-03-09 15:36:19', NULL, 1, 'superAdmin', 105, '192.166.3.101', NULL, '192.166.3.102', '巴楚县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (96, '2021-03-09 15:36:55', NULL, 1, 'superAdmin', 105, '192.166.3.111', NULL, '192.166.3.112', '麦盖提县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (97, '2021-03-09 15:37:23', NULL, 1, 'superAdmin', 105, '192.166.3.121', NULL, '192.166.3.122', '泽普县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (98, '2021-03-09 15:37:43', NULL, 1, 'superAdmin', 105, '192.166.3.131', NULL, '192.166.3.132', '叶城县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (99, '2021-03-09 15:39:20', NULL, 1, 'superAdmin', 106, '192.166.4.12', NULL, '192.166.4.13', '和田地区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (100, '2021-03-09 15:39:37', NULL, 1, 'superAdmin', 106, '192.166.4.21', NULL, '192.166.4.22', '和田县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (101, '2021-03-09 15:39:51', NULL, 1, 'superAdmin', 106, '192.166.4.31', NULL, '192.166.4.32', '于田县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (102, '2021-03-09 15:40:07', NULL, 1, 'superAdmin', 106, '192.166.4.41', NULL, '192.166.4.42', '策勒县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (103, '2021-03-09 15:40:21', NULL, 1, 'superAdmin', 106, '192.166.4.51', NULL, '192.166.4.52', '皮山县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (104, '2021-03-09 15:40:47', NULL, 1, 'superAdmin', 106, '192.166.4.61', NULL, '192.166.4.62', '民丰县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (105, '2021-03-09 15:41:29', NULL, 1, 'superAdmin', 106, '192.166.4.71', NULL, '192.166.4.72', '和田市', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (106, '2021-03-09 15:42:58', NULL, 1, 'superAdmin', 106, '192.166.4.81', NULL, '192.166.4.82', '墨玉县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (107, '2021-03-09 15:47:27', NULL, 1, 'superAdmin', 106, '192.166.4.91', NULL, '192.166.4.92', '洛浦县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (108, '2021-03-09 15:47:56', NULL, 1, 'superAdmin', 107, '192.166.5.12', NULL, '192.166.5.13', '阿克苏地区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (109, '2021-03-09 15:48:11', NULL, 1, 'superAdmin', 107, '192.166.5.21', NULL, '192.166.5.22', '阿克苏市', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (110, '2021-03-09 15:48:32', NULL, 1, 'superAdmin', 107, '192.166.5.31', NULL, '192.166.5.32', '温宿县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (111, '2021-03-09 15:49:25', NULL, 1, 'superAdmin', 107, '192.166.5.51', NULL, '192.166.5.52', '新和县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (112, '2021-03-09 15:51:08', NULL, 1, 'superAdmin', 107, '192.166.5.61', NULL, '192.166.5.62', '沙雅县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (113, '2021-03-09 15:51:28', '2021-03-11 18:44:58', 1, 'superAdmin', 107, '192.166.5.71', NULL, '192.166.5.72', '库车县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (114, '2021-03-09 15:51:43', NULL, 1, 'superAdmin', 107, '192.166.5.81', NULL, '192.166.5.82', '乌什县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (115, '2021-03-09 15:52:14', NULL, 1, 'superAdmin', 107, '192.166.5.41', NULL, '192.166.5.42', '拜城县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (116, '2021-03-09 15:52:37', NULL, 1, 'superAdmin', 107, '192.166.5.91', NULL, '192.166.5.92', '柯坪县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (117, '2021-03-09 15:52:50', '2021-03-11 18:45:06', 1, 'superAdmin', 107, '192.166.5.101', NULL, '192.166.5.102', '阿瓦提县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (118, '2021-03-09 15:53:30', NULL, 1, 'superAdmin', 200, '192.166.6.12', NULL, '192.166.6.13', '巴州地区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (119, '2021-03-09 15:54:13', NULL, 1, 'superAdmin', 200, '192.166.6.21', NULL, '192.166.6.22', '库尔勒市', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (120, '2021-03-09 15:54:33', '2021-03-09 15:55:07', 1, 'superAdmin', 200, '192.166.6.31', NULL, '192.166.6.32', '轮台县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (121, '2021-03-09 15:56:09', NULL, 1, 'superAdmin', 200, '192.166.6.41', NULL, '192.166.6.42', '且末县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (122, '2021-03-09 15:56:30', NULL, 1, 'superAdmin', 200, '192.166.6.51', NULL, '192.166.6.52', '若羌县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (123, '2021-03-09 15:56:50', NULL, 1, 'superAdmin', 200, '192.166.6.61', NULL, '192.166.6.62', '尉犁县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (124, '2021-03-09 15:58:35', NULL, 1, 'superAdmin', 200, '192.166.6.71', NULL, '192.166.6.72', '焉耆县', 0, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (125, '2021-03-09 15:58:51', NULL, 1, 'superAdmin', 200, '192.166.6.81', NULL, '192.166.6.82', '博湖县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (126, '2021-03-09 15:59:27', NULL, 1, 'superAdmin', 200, '192.166.6.91', NULL, '192.166.6.92', '和静县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (127, '2021-03-09 15:59:43', NULL, 1, 'superAdmin', 200, '192.166.6.101', NULL, '192.166.6.102', '和硕县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (128, '2021-03-09 15:59:57', NULL, 1, 'superAdmin', 200, '192.166.6.111', NULL, '192.166.6.112', '开发区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (129, '2021-03-09 16:00:26', NULL, 1, 'superAdmin', 201, '192.166.7.12', NULL, '192.166.7.13', '吐鲁番市', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (130, '2021-03-09 16:00:44', NULL, 1, 'superAdmin', 201, '192.166.7.21', NULL, '192.166.7.22', '高昌区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (131, '2021-03-09 16:01:02', NULL, 1, 'superAdmin', 201, '192.166.7.31', NULL, '192.166.7.32', '示范区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (132, '2021-03-09 16:01:25', NULL, 1, 'superAdmin', 201, '192.166.7.41', NULL, '192.166.7.42', '托克逊县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (133, '2021-03-09 16:01:39', NULL, 1, 'superAdmin', 201, '192.166.7.51', NULL, '192.166.7.52', '鄯善县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (134, '2021-03-09 16:02:08', '2021-04-18 15:18:22', 1, 'superAdmin', 100, '192.166.8.12', NULL, '192.166.8.13', '哈密市', 100, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (135, '2021-03-09 16:02:21', NULL, 1, 'superAdmin', 202, '192.166.8.21', NULL, '192.166.8.22', '伊州区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (136, '2021-03-09 16:02:38', NULL, 1, 'superAdmin', 202, '192.166.8.31', NULL, '192.166.8.32', '巴里坤县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (137, '2021-03-09 16:02:55', NULL, 1, 'superAdmin', 202, '192.166.8.41', NULL, '192.166.8.42', '伊吾县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (138, '2021-03-09 16:03:24', NULL, 1, 'superAdmin', 203, '192.166.9.12', NULL, '192.166.9.13', '乌鲁木齐市', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (139, '2021-03-09 16:03:40', NULL, 1, 'superAdmin', 203, '192.166.9.21', NULL, '192.166.9.22', '乌鲁木齐县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (140, '2021-03-09 16:03:57', NULL, 1, 'superAdmin', 203, '192.166.9.31', NULL, '192.166.9.32', '水磨沟区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (141, '2021-03-09 16:04:11', NULL, 1, 'superAdmin', 203, '192.166.9.41', NULL, '192.166.9.42', '天山区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (142, '2021-03-09 16:04:29', NULL, 1, 'superAdmin', 203, '192.166.9.51', NULL, '192.166.9.52', '沙依巴格区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (143, '2021-03-09 16:04:44', '2021-03-09 16:05:04', 1, 'superAdmin', 203, '192.166.9.61', NULL, '192.166.9.62', '头屯河区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (144, '2021-03-09 16:05:19', NULL, 1, 'superAdmin', 203, '192.166.9.71', NULL, '192.166.9.72', '新市区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (145, '2021-03-09 16:05:33', NULL, 1, 'superAdmin', 203, '192.166.9.81', NULL, '192.166.9.82', '米东区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (146, '2021-03-09 16:06:01', '2021-03-09 16:06:15', 1, 'superAdmin', 203, '192.166.9.91', NULL, '192.166.9.92', '甘泉堡区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (147, '2021-03-09 16:06:40', NULL, 1, 'superAdmin', 203, '192.166.9.101', NULL, '192.166.9.102', '达板城区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (148, '2021-03-09 16:07:12', NULL, 1, 'superAdmin', 204, '192.166.10.21', NULL, '192.166.10.22', '昌吉市', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (149, '2021-03-09 16:07:36', NULL, 1, 'superAdmin', 204, '192.166.10.31', NULL, '192.166.10.32', '阜康市', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (150, '2021-03-09 16:08:16', NULL, 1, 'superAdmin', 204, '192.166.10.41', NULL, '192.166.10.42', '呼图壁县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (151, '2021-03-09 16:08:36', NULL, 1, 'superAdmin', 204, '192.166.10.51', NULL, '192.166.10.52', '玛纳斯县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (152, '2021-03-09 16:08:51', NULL, 1, 'superAdmin', 204, '192.166.10.61', NULL, '192.166.10.62', '奇台县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (153, '2021-03-09 16:09:12', NULL, 1, 'superAdmin', 204, '192.166.10.71', NULL, '192.166.10.72', '吉木萨尔县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (154, '2021-03-09 16:09:35', NULL, 1, 'superAdmin', 204, '192.166.10.81', NULL, '192.166.10.82', '木垒县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (155, '2021-03-09 16:09:56', NULL, 1, 'superAdmin', 204, '192.166.10.91', NULL, '192.166.10.92', '高新区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (156, '2021-03-09 16:10:15', NULL, 1, 'superAdmin', 204, '192.166.10.101', NULL, '192.166.10.102', '准东开发区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (157, '2021-03-09 16:11:06', NULL, 1, 'superAdmin', 205, '192.166.11.21', NULL, '192.166.11.22', '塔城市', 0, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (158, '2021-03-09 16:11:23', NULL, 1, 'superAdmin', 205, '192.166.11.31', NULL, '192.166.11.32', '乌苏市', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (159, '2021-03-09 16:11:40', NULL, 1, 'superAdmin', 205, '192.166.11.41', NULL, '192.166.11.42', '额敏县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (160, '2021-03-09 16:12:00', NULL, 1, 'superAdmin', 205, '192.166.11.51', NULL, '192.166.11.52', '裕民县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (161, '2021-03-09 16:12:14', NULL, 1, 'superAdmin', 205, '192.166.11.61', NULL, '192.166.11.62', '托里县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (162, '2021-03-09 16:12:32', NULL, 1, 'superAdmin', 205, '192.166.11.71', NULL, '192.166.11.72', '沙湾县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (163, '2021-03-09 16:12:44', NULL, 1, 'superAdmin', 205, '192.166.11.81', NULL, '192.166.11.82', '和布克赛尔县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (164, '2021-03-09 16:14:33', NULL, 1, 'superAdmin', 206, '192.166.12.21', NULL, '192.166.12.22', '阿勒泰市', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (165, '2021-03-09 16:14:48', NULL, 1, 'superAdmin', 206, '192.166.12.31', NULL, '192.166.12.32', '哈巴河县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (166, '2021-03-09 16:15:08', NULL, 1, 'superAdmin', 206, '192.166.12.41', NULL, '192.166.12.42', '布尔津县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (167, '2021-03-09 16:15:31', NULL, 1, 'superAdmin', 206, '192.166.12.51', NULL, '192.166.12.52', '吉木乃县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (168, '2021-03-09 16:15:49', NULL, 1, 'superAdmin', 206, '192.166.12.61', NULL, '192.166.12.62', '富蕴县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (169, '2021-03-09 16:16:02', NULL, 1, 'superAdmin', 206, '192.166.12.71', NULL, '192.166.12.72', '福海县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (170, '2021-03-09 16:16:16', NULL, 1, 'superAdmin', 206, '192.166.12.81', NULL, '192.166.12.82', '青河县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (171, '2021-03-09 16:16:35', NULL, 1, 'superAdmin', 206, '192.166.12.91', NULL, '192.166.12.92', '喀纳斯管委会', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (172, '2021-03-09 16:17:12', NULL, 1, 'superAdmin', 207, '192.166.13.21', NULL, '192.166.13.22', '博乐市', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (173, '2021-03-09 16:17:27', NULL, 1, 'superAdmin', 207, '192.166.13.31', NULL, '192.166.13.32', '温泉县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (174, '2021-03-09 16:17:41', NULL, 1, 'superAdmin', 207, '192.166.13.41', NULL, '192.166.13.42', '精河县', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (175, '2021-03-09 16:17:58', '2021-04-18 15:18:31', 1, 'superAdmin', 100, '192.166.13.51', NULL, '192.166.13.52', '阿拉山口口岸', 100, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (176, '2021-03-09 16:21:43', NULL, 1, 'superAdmin', 207, '192.166.13.61', NULL, '192.166.13.62', '赛里木湖管委会', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (177, '2021-03-09 16:36:31', NULL, 1, 'superAdmin', 208, '192.166.14.31', NULL, '192.166.14.32', '独山子区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (178, '2021-03-09 16:36:45', NULL, 1, 'superAdmin', 208, '192.166.14.41', NULL, '192.166.14.42', '白碱滩区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (179, '2021-03-09 16:37:05', '2021-03-09 16:37:21', 1, 'superAdmin', 208, '192.166.14.51', NULL, '192.166.14.52', '乌尔禾区', 100, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (180, '2021-03-24 15:57:11', '2021-03-26 17:07:14', 1, 'superAdmin', 212, '172.16.101.222', NULL, NULL, '172.16.101.222', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (181, '2021-03-24 15:58:31', '2021-03-26 17:13:06', 1, 'superAdmin', 213, '172.16.101.223', NULL, NULL, '172.16.101.223', 0, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (182, '2021-03-24 15:58:49', '2021-03-26 17:10:04', 1, 'superAdmin', 213, '172.16.101.212', NULL, NULL, '172.16.101.212', 0, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (183, '2021-03-24 15:59:16', '2021-03-26 17:27:18', 1, 'superAdmin', 214, '172.16.101.208', NULL, NULL, '172.16.101.208', 0, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (184, '2021-03-24 15:59:28', NULL, 1, 'superAdmin', 214, '192.166.2.21', NULL, NULL, '192.166.2.21', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (185, '2021-03-29 16:27:51', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15000', NULL, '15000@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (186, '2021-03-29 16:35:11', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15001', NULL, '15001@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (187, '2021-03-29 16:35:25', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15002', NULL, '15002@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (189, '2021-03-29 16:35:59', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15003', NULL, '15003@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (190, '2021-03-29 16:36:10', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15004', NULL, '15004@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (191, '2021-03-29 16:36:18', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15005', NULL, '15005@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (192, '2021-03-29 16:36:28', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15006', NULL, '15006@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (193, '2021-03-29 16:36:37', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15007', NULL, '15007@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (194, '2021-03-29 16:36:46', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15008', NULL, '15008@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (195, '2021-03-29 16:36:56', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15009', NULL, '15009@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (196, '2021-03-29 16:38:12', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15010', NULL, '15010@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (197, '2021-03-29 16:38:35', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15011', NULL, '15011@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (198, '2021-03-29 16:38:42', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15012', NULL, '15012@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (199, '2021-03-29 16:38:55', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15013', NULL, '15013@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (200, '2021-03-29 16:39:35', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15014', NULL, '15014@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (201, '2021-03-29 16:40:09', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16001', NULL, '16001@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (202, '2021-03-29 16:40:20', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16002', NULL, '16002@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (203, '2021-03-29 16:40:29', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16003', NULL, '16003@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (204, '2021-03-29 16:40:50', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16004', NULL, '16004@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (205, '2021-03-29 16:41:54', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16000', NULL, '16000@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (206, '2021-03-29 16:42:04', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16005', NULL, '16005@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (207, '2021-03-29 16:42:13', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16006', NULL, '16006@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (208, '2021-03-29 16:42:26', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16007', NULL, '16007@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (209, '2021-03-29 16:42:36', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16008', NULL, '16008@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (210, '2021-03-29 16:42:55', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16009', NULL, '16009@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (211, '2021-03-29 16:43:07', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16010', NULL, '16010@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (212, '2021-03-29 16:43:19', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16011', NULL, '16011@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (213, '2021-03-29 16:43:29', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16012', NULL, '16012@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (214, '2021-03-29 16:43:50', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16013', NULL, '16013@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (215, '2021-03-29 16:44:01', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16014', NULL, '16014@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (216, '2021-03-29 18:43:13', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17000', NULL, '17000@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (217, '2021-03-29 18:43:42', '2021-03-29 18:43:52', 1, 'superAdmin', 215, '172.16.100.191', '17001', NULL, '17001@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (218, '2021-03-29 18:44:01', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17002', NULL, '17002@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (219, '2021-03-29 18:44:10', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17003', NULL, '17003@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (220, '2021-03-29 18:44:18', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17004', NULL, '17004@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (221, '2021-03-29 18:44:25', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17005', NULL, '17005@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (222, '2021-03-29 18:44:35', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17006', NULL, '17006@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (223, '2021-03-29 18:44:46', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17007', NULL, '17007@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (224, '2021-03-29 18:44:54', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17008', NULL, '17008@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (225, '2021-03-29 18:45:02', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17009', NULL, '17009@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (226, '2021-03-29 18:45:10', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17010', NULL, '17010@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (227, '2021-03-29 18:45:21', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17011', NULL, '17011@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (228, '2021-03-29 18:45:29', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17012', NULL, '17012@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (229, '2021-03-29 18:45:42', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17013', NULL, '17013@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (230, '2021-03-29 18:45:54', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17014', NULL, '17014@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (231, '2021-03-29 18:49:01', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18000', NULL, '18000@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (232, '2021-03-29 18:49:14', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18001', NULL, '18001@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (233, '2021-03-29 18:49:23', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18002', NULL, '18002@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (234, '2021-03-29 18:58:19', '2021-03-30 09:47:29', 1, 'superAdmin', 216, '172.16.100.191', '18003', NULL, '18003@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (235, '2021-03-29 18:58:50', '2021-03-29 18:59:18', 1, 'superAdmin', 216, '172.16.100.191', '18004', NULL, '18004@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (236, '2021-03-29 18:59:00', '2021-03-29 18:59:23', 1, 'superAdmin', 216, '172.16.100.191', '18005', NULL, '18005@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (237, '2021-03-29 18:59:12', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18006', NULL, '18006@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (238, '2021-03-29 18:59:33', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18007', NULL, '18007@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (240, '2021-03-29 18:59:56', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18008', NULL, '18008@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (241, '2021-03-29 19:00:05', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18009', NULL, '18009@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (242, '2021-03-29 19:00:14', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18010', NULL, '18010@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (243, '2021-03-29 19:00:22', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18011', NULL, '18011@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (244, '2021-03-29 19:00:33', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18012', NULL, '18012@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (245, '2021-03-29 19:00:42', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18013', NULL, '18013@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (246, '2021-03-29 19:00:50', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18014', NULL, '18014@172.16.100.191', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (247, '2021-03-29 19:01:11', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19000', NULL, '19000@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (248, '2021-03-29 19:01:18', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19001', NULL, '19001@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (249, '2021-03-29 19:01:43', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19002', NULL, '19002@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (250, '2021-03-29 19:01:49', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19003', NULL, '19003@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (251, '2021-03-29 19:01:58', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19004', NULL, '19004@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (252, '2021-03-29 19:02:07', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19005', NULL, '19005@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (253, '2021-03-29 19:02:14', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19006', NULL, '19006@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (255, '2021-03-29 19:03:02', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19007', NULL, '19007@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (256, '2021-03-29 19:03:09', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19008', NULL, '19008@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (257, '2021-03-29 19:03:17', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19009', NULL, '19009@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (258, '2021-03-29 19:03:24', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19010', NULL, '19010@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (259, '2021-03-29 19:03:34', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19011', NULL, '19011@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (260, '2021-03-29 19:03:42', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19012', NULL, '19012@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (261, '2021-03-29 19:03:52', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19013', NULL, '19013@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (262, '2021-03-29 19:04:03', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19014', NULL, '19014@172.16.100.190', 0, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (263, '2021-04-18 15:13:27', NULL, 1, 'superAdmin', 100, '192.166.0.77', NULL, NULL, '192.166.0.77', 0, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (264, '2021-04-18 15:13:56', NULL, 1, 'superAdmin', 100, '192.166.0.187', NULL, NULL, '192.166.0.187', 0, 2, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_terminal` VALUES (265, '2021-04-18 15:36:36', NULL, 1, 'superAdmin', 100, '192.166.0.150', NULL, NULL, '192.166.0.150', 0, 1, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for busi_token
-- ----------------------------
DROP TABLE IF EXISTS `busi_token`;
CREATE TABLE `busi_token`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `token` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '接口访问所需的token',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '第三方用户访问的api专用token' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 203 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table
-- ----------------------------
INSERT INTO `gen_table` VALUES (173, 'busi_conference_number', '会议号码记录表', NULL, NULL, 'BusiConferenceNumber', 'crud', 'com.paradisecloud.fcm', 'busi', 'number', '会议号码记录', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-01-20 16:15:27', '', '2021-01-20 16:16:35', NULL);
INSERT INTO `gen_table` VALUES (174, 'busi_fme', 'FME终端信息表', NULL, NULL, 'BusiFme', 'crud', 'com.paradisecloud.fcm', 'busi', 'fme', 'FME终端信息', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (175, 'busi_fme_group', 'FME终端组', NULL, NULL, 'BusiFmeGroup', 'crud', 'com.paradisecloud.fcm', 'busi', 'group', 'FME终端组', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (176, 'busi_history_conference', '历史会议，每次挂断会保存该历史记录', NULL, NULL, 'BusiHistoryConference', 'crud', 'com.paradisecloud.fcm', 'busi', 'conference', '历史会议，每次挂断会保存该历史记录', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (177, 'busi_history_participant', '历史会议的参会者', NULL, NULL, 'BusiHistoryParticipant', 'crud', 'com.paradisecloud.fcm', 'busi', 'participant', '历史会议的参会者', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (179, 'busi_template_conference', '会议模板表', NULL, NULL, 'BusiTemplateConference', 'crud', 'com.paradisecloud.fcm', 'busi', 'conference', '会议模板', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (180, 'busi_template_participant', '会议模板的参会者', NULL, NULL, 'BusiTemplateParticipant', 'crud', 'com.paradisecloud.fcm', 'busi', 'participant', '会议模板的参会者', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (181, 'busi_terminal', '终端信息表', NULL, NULL, 'BusiTerminal', 'crud', 'com.paradisecloud.fcm', 'busi', 'terminal', '终端信息', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-01-20 16:15:27', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (184, 'busi_call_leg_profile', '入会方案配置，控制参会者进入会议的方案', NULL, NULL, 'BusiCallLegProfile', 'crud', 'com.paradisecloud.fcm', 'busi', 'callLegProfile', '入会方案配置，控制参会者进入会议的方案', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22', NULL);
INSERT INTO `gen_table` VALUES (186, 'busi_fme_group_dept', 'FME组分配租户的中间表（一个FME组可以分配给多个租户，一对多）', NULL, NULL, 'BusiFmeGroupDept', 'crud', 'com.paradisecloud.fcm', 'busi', 'dept', 'FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-01-28 14:42:35', '', '2021-01-28 14:42:54', NULL);
INSERT INTO `gen_table` VALUES (188, 'busi_template_dept', '会议模板的级联部门', NULL, NULL, 'BusiTemplateDept', 'crud', 'com.paradisecloud.fcm', 'busi', 'dept', '会议模板的级联部门', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-01-29 18:10:25', '', '2021-01-29 18:24:19', NULL);
INSERT INTO `gen_table` VALUES (189, 'busi_conference', '活跃会议室信息，用于存放活跃的会议室', NULL, NULL, 'BusiConference', 'crud', 'com.paradisecloud.fcm', 'busi', 'conference', '活跃会议室信息，用于存放活跃的会议室', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41', NULL);
INSERT INTO `gen_table` VALUES (190, 'busi_template_polling_dept', '轮询方案的部门', NULL, NULL, 'BusiTemplatePollingDept', 'crud', 'com.paradisecloud.system', 'system', 'dept', '轮询方案的部门', 'lilinhai', '0', '/', NULL, 'superAdmin', '2021-02-26 10:11:43', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (191, 'busi_template_polling_paticipant', '轮询方案的参会者', NULL, NULL, 'BusiTemplatePollingPaticipant', 'crud', 'com.paradisecloud.fcm', 'busi', 'paticipant', '轮询方案的参会者', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05', NULL);
INSERT INTO `gen_table` VALUES (192, 'busi_template_polling_scheme', '轮询方案', NULL, NULL, 'BusiTemplatePollingScheme', 'crud', 'com.paradisecloud.fcm', 'system', 'scheme', '轮询方案', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49', NULL);
INSERT INTO `gen_table` VALUES (194, 'busi_terminal_registration_server', '终端注册服务器', NULL, NULL, 'BusiTerminalRegistrationServer', 'crud', 'com.paradisecloud.fcm', 'busi', 'registrationServer', '终端注册服务器', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36', NULL);
INSERT INTO `gen_table` VALUES (195, 'busi_fme_cluster', 'FME集群', NULL, NULL, 'BusiFmeCluster', 'crud', 'com.paradisecloud.fcm', 'busi', 'fmeCluster', 'FME集群', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:38:19', NULL);
INSERT INTO `gen_table` VALUES (196, 'busi_fme_cluster_map', 'FME-终端组中间表（多对多）', NULL, NULL, 'BusiFmeClusterMap', 'crud', 'com.paradisecloud.fcm', 'busi', 'fmeClusterMap', 'FME-终端组中间（多对多）', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:40:48', NULL);
INSERT INTO `gen_table` VALUES (197, 'busi_fme_dept', 'FME组分配租户的中间表（一个FME组可以分配给多个租户，一对多）', NULL, NULL, 'BusiFmeDept', 'crud', 'com.paradisecloud.fcm', 'busi', 'fmeDept', 'FME组分配租户的中间（一个FME组可以分配给多个租户，一对多）', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:41:12', NULL);
INSERT INTO `gen_table` VALUES (198, 'busi_template_conference_default_view_cell_screen', '默认视图下指定的多分频单元格', NULL, NULL, 'BusiTemplateConferenceDefaultViewCellScreen', 'crud', 'com.paradisecloud.fcm', 'busi', 'defaultViewCellScreen', '默认视图下指定的多分频单元格', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:27', NULL);
INSERT INTO `gen_table` VALUES (199, 'busi_template_conference_default_view_dept', '默认视图的部门显示顺序', NULL, NULL, 'BusiTemplateConferenceDefaultViewDept', 'crud', 'com.paradisecloud.fcm', 'busi', 'defaultViewDept', '默认视图的部门显示顺序', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:45', NULL);
INSERT INTO `gen_table` VALUES (200, 'busi_template_conference_default_view_paticipant', '默认视图的参会者', NULL, NULL, 'BusiTemplateConferenceDefaultViewPaticipant', 'crud', 'com.paradisecloud.fcm', 'busi', 'DefaultViewPaticipant', '默认视图的参会者', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:44:03', NULL);
INSERT INTO `gen_table` VALUES (201, 'busi_sfbc_registration_server', '终端SFBC注册服务器', NULL, NULL, 'BusiSfbcRegistrationServer', 'crud', 'com.paradisecloud.fcm', 'busi', 'sfbcserver', '终端SFBC注册服务器', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25', NULL);
INSERT INTO `gen_table` VALUES (202, 'busi_sfbc_server_dept', 'SFBC服务器-部门映射', NULL, NULL, 'BusiSfbcServerDept', 'crud', 'com.paradisecloud.fcm', 'busi', 'sfbcDept', 'SFBC服务器-部门映射', 'lilinhai', '0', '/', '{}', 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:49', NULL);

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1618 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------
INSERT INTO `gen_table_column` VALUES (1355, '173', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', '2021-01-20 16:16:35');
INSERT INTO `gen_table_column` VALUES (1356, '173', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', '2021-01-20 16:16:35');
INSERT INTO `gen_table_column` VALUES (1357, '173', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', '2021-01-20 16:16:35');
INSERT INTO `gen_table_column` VALUES (1359, '173', 'dept_id', '归属公司ID', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', '2021-01-20 16:16:35');
INSERT INTO `gen_table_column` VALUES (1360, '174', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1361, '174', 'create_time', '终端创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1362, '174', 'update_time', '终端修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1364, '174', 'name', 'fme显示名字', 'varchar(32)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1365, '174', 'ip', '设备的IP地址', 'varchar(16)', 'String', 'ip', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1366, '174', 'port', 'fme端口', 'int(11)', 'Integer', 'port', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1367, '174', 'priority', '优先级，越大越高(备用节点才会有优先级)', 'int(11)', 'Integer', 'priority', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1368, '174', 'status', 'FME在线状态：1在线，2离线，3删除', 'int(11)', 'Integer', 'status', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'radio', '', 9, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1369, '175', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1370, '175', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1371, '175', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1372, '175', 'dept_id', '所属部门（每个用户登录，只列出该用户所属部门下的FME组）', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1373, '175', 'username', 'FME的连接用户名，同一个组下的用户名相同', 'varchar(32)', 'String', 'username', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1374, '175', 'password', 'FME的连接密码，同一个组下的密码相同', 'varchar(128)', 'String', 'password', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1375, '175', 'name', '组名，最长32', 'varchar(32)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 7, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1376, '175', 'type', '1集群，2单节点', 'int(11)', 'Integer', 'type', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 8, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1377, '175', 'busi_type', '类型：1主用，2备用', 'int(11)', 'Integer', 'busiType', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 9, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1378, '175', 'spare_fme_group', '备用的FME节点（可以指向集群组和单节点组）', 'bigint(20)', 'Long', 'spareFmeGroup', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 10, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1379, '175', 'description', '备注信息', 'varchar(128)', 'String', 'description', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 11, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1380, '176', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1381, '176', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1382, '176', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1383, '176', 'name', '模板会议名', 'varchar(128)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1384, '176', 'number', '会议号码', 'int(11)', 'Integer', 'number', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1385, '176', 'dept_id', '部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1386, '176', 'call_leg_profile_id', '入会方案配置ID（关联FME里面的入会方案记录ID，会控端不存）', 'varchar(128)', 'String', 'callLegProfileId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1387, '176', 'bandwidth', '带宽1,2,3,4,5,6M', 'int(11)', 'Integer', 'bandwidth', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1388, '177', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1389, '177', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1390, '177', 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1391, '177', 'history_conference_id', '关联的会议ID', 'bigint(20)', 'Long', 'historyConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1392, '177', 'terminal_id', '关联的终端ID', 'bigint(20)', 'Long', 'terminalId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1393, '177', 'weight', '参会者顺序（权重倒叙排列）', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1400, '179', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1401, '179', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1402, '179', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1403, '179', 'name', '模板会议名', 'varchar(128)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1405, '179', 'dept_id', '部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1406, '179', 'call_leg_profile_id', '入会方案配置ID（关联FME里面的入会方案记录ID，会控端不存）', 'varchar(128)', 'String', 'callLegProfileId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1407, '179', 'bandwidth', '带宽1,2,3,4,5,6M', 'int(11)', 'Integer', 'bandwidth', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1408, '179', 'is_auto_call', '是否自动呼叫与会者：1是，2否', 'int(11)', 'Integer', 'isAutoCall', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 9, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1409, '180', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1410, '180', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1411, '180', 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1412, '180', 'template_conference_id', '会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1413, '180', 'terminal_id', '终端ID', 'bigint(20)', 'Long', 'terminalId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1414, '180', 'weight', '参会者顺序（权重倒叙排列）', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1415, '181', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1416, '181', 'create_time', '终端创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1417, '181', 'update_time', '终端修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1418, '181', 'dept_id', '终端所属部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1419, '181', 'ip', '设备的IP地址', 'varchar(16)', 'String', 'ip', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1420, '181', 'number', '设备号，设备唯一标识', 'varchar(16)', 'String', 'number', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1421, '181', 'camera_ip', '摄像头IP地址', 'varchar(16)', 'String', 'cameraIp', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1422, '181', 'name', '终端显示名字', 'varchar(32)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 8, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1423, '181', 'type', '终端类型，枚举值int类型', 'int(11)', 'Integer', 'type', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 9, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1424, '181', 'online_status', '终端状态：1在线，2离线', 'int(11)', 'Integer', 'onlineStatus', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'radio', '', 10, 'superAdmin', '2021-01-20 16:15:27', '', NULL);
INSERT INTO `gen_table_column` VALUES (1440, '184', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22');
INSERT INTO `gen_table_column` VALUES (1441, '184', 'create_time', NULL, 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22');
INSERT INTO `gen_table_column` VALUES (1442, '184', 'update_time', NULL, 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22');
INSERT INTO `gen_table_column` VALUES (1443, '184', 'call_leg_profile_uuid', '入会方案对应的fme里面的记录的uuid', 'varchar(128)', 'String', 'callLegProfileUuid', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22');
INSERT INTO `gen_table_column` VALUES (1444, '184', 'type', '是否是默认入会方案:1是，2否', 'tinyint(1)', 'Integer', 'type', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 5, 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22');
INSERT INTO `gen_table_column` VALUES (1445, '184', 'dept_id', '入会方案归属部门', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22');
INSERT INTO `gen_table_column` VALUES (1446, '184', 'fme_id', '入会方案归属的fme', 'bigint(20)', 'Long', 'fmeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-01-26 14:29:01', '', '2021-01-26 14:34:22');
INSERT INTO `gen_table_column` VALUES (1447, '173', 'type', '会议号类型：1默认，2普通', 'tinyint(1)', 'Integer', 'type', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 6, '', '2021-01-26 19:00:48', '', NULL);
INSERT INTO `gen_table_column` VALUES (1448, '173', 'remarks', '备注信息', 'varchar(32)', 'String', 'remarks', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, '', '2021-01-26 19:00:48', '', NULL);
INSERT INTO `gen_table_column` VALUES (1449, '173', 'status', '号码状态：1闲置，10已预约，100会议中', 'int(11)', 'Integer', 'status', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'radio', '', 7, '', '2021-01-27 10:50:34', '', NULL);
INSERT INTO `gen_table_column` VALUES (1455, '186', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-28 14:42:35', '', '2021-01-28 14:42:54');
INSERT INTO `gen_table_column` VALUES (1456, '186', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-28 14:42:35', '', '2021-01-28 14:42:54');
INSERT INTO `gen_table_column` VALUES (1457, '186', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-28 14:42:35', '', '2021-01-28 14:42:54');
INSERT INTO `gen_table_column` VALUES (1458, '186', 'fme_group_id', 'fme组ID', 'bigint(20)', 'Long', 'fmeGroupId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-28 14:42:35', '', '2021-01-28 14:42:54');
INSERT INTO `gen_table_column` VALUES (1459, '186', 'dept_id', '分配给的租户', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-28 14:42:35', '', '2021-01-28 14:42:54');
INSERT INTO `gen_table_column` VALUES (1466, '188', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-01-29 18:10:26', '', '2021-01-29 18:24:19');
INSERT INTO `gen_table_column` VALUES (1467, '188', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-01-29 18:10:26', '', '2021-01-29 18:24:19');
INSERT INTO `gen_table_column` VALUES (1468, '188', 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-01-29 18:10:26', '', '2021-01-29 18:24:19');
INSERT INTO `gen_table_column` VALUES (1469, '188', 'template_conference_id', '会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-01-29 18:10:26', '', '2021-01-29 18:24:19');
INSERT INTO `gen_table_column` VALUES (1470, '188', 'dept_id', '部门ID（部门也是FME终端，一种与会者）', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-01-29 18:10:26', '', '2021-01-29 18:24:19');
INSERT INTO `gen_table_column` VALUES (1471, '188', 'weight', '参会者顺序（权重倒叙排列）', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-01-29 18:10:26', '', '2021-01-29 18:24:19');
INSERT INTO `gen_table_column` VALUES (1472, '179', 'conference_number', '模板绑定的会议号', 'bigint(20)', 'Long', 'conferenceNumber', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 9, '', '2021-01-29 18:11:10', '', NULL);
INSERT INTO `gen_table_column` VALUES (1475, '181', 'create_user_id', '创建者ID', 'bigint(20)', 'Long', 'createUserId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-01-30 10:18:47', '', NULL);
INSERT INTO `gen_table_column` VALUES (1476, '181', 'create_user_name', '创建者用户名', 'varchar(32)', 'String', 'createUserName', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 5, '', '2021-01-30 10:18:47', '', NULL);
INSERT INTO `gen_table_column` VALUES (1477, '179', 'create_user_id', '创建者id', 'bigint(20)', 'Long', 'createUserId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-01-30 10:18:59', '', NULL);
INSERT INTO `gen_table_column` VALUES (1478, '179', 'create_user_name', '创建者用户名', 'varchar(32)', 'String', 'createUserName', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 5, '', '2021-01-30 10:18:59', '', NULL);
INSERT INTO `gen_table_column` VALUES (1479, '176', 'create_user_id', '创建者ID', 'bigint(20)', 'Long', 'createUserId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-01-30 10:19:39', '', NULL);
INSERT INTO `gen_table_column` VALUES (1480, '176', 'create_user_name', '创建者用户名', 'varchar(32)', 'String', 'createUserName', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 5, '', '2021-01-30 10:19:39', '', NULL);
INSERT INTO `gen_table_column` VALUES (1481, '173', 'create_user_id', '创建者ID', 'bigint(20)', 'Long', 'createUserId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-01-30 10:19:51', '', NULL);
INSERT INTO `gen_table_column` VALUES (1482, '173', 'create_user_name', '创建者用户名', 'varchar(32)', 'String', 'createUserName', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 5, '', '2021-01-30 10:19:51', '', NULL);
INSERT INTO `gen_table_column` VALUES (1483, '173', 'co_space_id', '会议号对应的会议室ID', 'varchar(0)', 'String', 'coSpaceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, '', '2021-01-30 16:16:38', '', NULL);
INSERT INTO `gen_table_column` VALUES (1484, '179', 'type', '模板会议类型：1级联，2普通', 'tinyint(1)', 'Integer', 'type', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 11, '', '2021-02-01 10:05:01', '', NULL);
INSERT INTO `gen_table_column` VALUES (1485, '189', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1486, '189', 'create_time', '会议创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1487, '189', 'update_time', NULL, 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1488, '189', 'create_user_id', '创建者ID', 'bigint(20)', 'Long', 'createUserId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1489, '189', 'create_user_name', '创建者用户名', 'varchar(32)', 'String', 'createUserName', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 5, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1490, '189', 'name', '会议名', 'varchar(64)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 6, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1491, '189', 'conference_number', '活跃会议室用的会议号', 'bigint(20)', 'Long', 'conferenceNumber', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1492, '189', 'co_space_id', '活跃会议室spaceId', 'varchar(128)', 'String', 'coSpaceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1493, '189', 'dept_id', '活跃会议室对应的部门', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 9, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1494, '189', 'data', '当前正在进行中的会议室序列化数据', 'mediumblob', 'String', 'data', '0', '0', NULL, '1', '1', '1', '1', 'EQ', NULL, '', 10, 'superAdmin', '2021-02-02 18:08:55', '', '2021-02-02 18:09:41');
INSERT INTO `gen_table_column` VALUES (1495, '174', 'cucm_ip', '该IP是增强音视频效果', 'varchar(16)', 'String', 'cucmIp', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, '', '2021-02-03 11:43:07', '', NULL);
INSERT INTO `gen_table_column` VALUES (1498, '188', 'uuid', '模板中的级联与会者终端的UUID', 'varchar(128)', 'String', 'uuid', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-02-03 17:21:46', '', NULL);
INSERT INTO `gen_table_column` VALUES (1499, '180', 'uuid', '模板中的与会者的UUID', 'varchar(128)', 'String', 'uuid', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-02-03 17:23:31', '', NULL);
INSERT INTO `gen_table_column` VALUES (1500, '189', 'is_main', '是否是会议的主体（发起者）', 'int(11)', 'Integer', 'isMain', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, '', '2021-02-04 11:08:35', '', NULL);
INSERT INTO `gen_table_column` VALUES (1501, '189', 'cascade_id', '所有级联在一起的会议和子会议的相同ID', 'varchar(128)', 'String', 'cascadeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, '', '2021-02-04 11:08:35', '', NULL);
INSERT INTO `gen_table_column` VALUES (1502, '189', 'template_conference_id', '模板会议的ID，标记出是哪个模板发起的，好从模板点击进入会议进行关联', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 12, '', '2021-02-04 11:08:35', '', NULL);
INSERT INTO `gen_table_column` VALUES (1503, '179', 'is_auto_monitor', '是否自动监听会议：1是，2否', 'int(11)', 'Integer', 'isAutoMonitor', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 11, '', '2021-02-24 18:20:56', '', NULL);
INSERT INTO `gen_table_column` VALUES (1504, '190', 'id', '自增ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-02-26 10:11:43', '', NULL);
INSERT INTO `gen_table_column` VALUES (1505, '190', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-02-26 10:11:43', '', NULL);
INSERT INTO `gen_table_column` VALUES (1506, '190', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-02-26 10:11:43', '', NULL);
INSERT INTO `gen_table_column` VALUES (1507, '190', 'template_conference_id', '会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-02-26 10:11:43', '', NULL);
INSERT INTO `gen_table_column` VALUES (1508, '190', 'polling_scheme_id', '归属轮询方案ID', 'bigint(20)', 'Long', 'pollingSchemeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-02-26 10:11:43', '', NULL);
INSERT INTO `gen_table_column` VALUES (1509, '190', 'dept_id', '部门ID（部门也是FME终端，一种与会者）', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-02-26 10:11:43', '', NULL);
INSERT INTO `gen_table_column` VALUES (1510, '190', 'weight', '参会者顺序（权重倒叙排列）', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-02-26 10:11:43', '', NULL);
INSERT INTO `gen_table_column` VALUES (1511, '191', 'id', '自增ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1512, '191', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1513, '191', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1514, '191', 'template_conference_id', '会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1515, '191', 'polling_scheme_id', '归属轮询方案ID', 'bigint(20)', 'Long', 'pollingSchemeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1516, '191', 'remote_party', '终端的远程部分（唯一的）', 'varchar(64)', 'String', 'remoteParty', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1517, '191', 'is_cascade_main', '是否级联之主(1是，2否)', 'int(11)', 'Integer', 'isCascadeMain', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1518, '191', 'weight', '参会者顺序（权重倒叙排列）', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-02-26 10:11:43', '', '2021-02-26 10:13:05');
INSERT INTO `gen_table_column` VALUES (1519, '192', 'id', '自增ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1520, '192', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1521, '192', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1522, '192', 'template_conference_id', '会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1523, '192', 'polling_interval', '轮询时间间隔', 'int(11)', 'Integer', 'pollingInterval', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1524, '192', 'polling_state_first', '是否需要优先轮询地州（1是，2否）', 'int(11)', 'Integer', 'pollingStateFirst', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1525, '192', 'scheme_name', '轮询方案名', 'varchar(48)', 'String', 'schemeName', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 7, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1526, '192', 'weight', '轮询方案顺序，越大越靠前', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-02-26 10:11:43', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1527, '191', 'terminal_id', '终端ID', 'bigint(20)', 'Long', 'terminalId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, '', '2021-02-26 10:24:46', '', NULL);
INSERT INTO `gen_table_column` VALUES (1528, '191', 'polling_interval', '该参会者的特定的轮询间隔（如果该值存在，会覆盖轮询方案终端间隔）', 'int(11)', 'Integer', 'pollingInterval', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, '', '2021-02-26 10:28:19', '', NULL);
INSERT INTO `gen_table_column` VALUES (1534, '194', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1535, '194', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1536, '194', 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1537, '194', 'server_type', '注册服务器类型（1:FSBC, 2CUCM）', 'int(11)', 'Integer', 'serverType', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 4, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1538, '194', 'server_ip', '注册服务器ip地州', 'varchar(16)', 'String', 'serverIp', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1539, '194', 'server_port', '注册服务器端口', 'int(11)', 'Integer', 'serverPort', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1540, '194', 'server_path', '注册服务器访问路径', 'varchar(128)', 'String', 'serverPath', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1541, '194', 'username', '注册服务器用户名', 'varchar(32)', 'String', 'username', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 8, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1542, '194', 'password', '注册服务器密码', 'varchar(64)', 'String', 'password', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 9, 'superAdmin', '2021-03-17 14:34:38', '', '2021-03-17 14:51:36');
INSERT INTO `gen_table_column` VALUES (1543, '195', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:38:19');
INSERT INTO `gen_table_column` VALUES (1544, '195', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:38:19');
INSERT INTO `gen_table_column` VALUES (1545, '195', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:38:19');
INSERT INTO `gen_table_column` VALUES (1546, '195', 'name', '组名，最长32', 'varchar(32)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:38:19');
INSERT INTO `gen_table_column` VALUES (1548, '195', 'description', '备注信息', 'varchar(128)', 'String', 'description', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:38:19');
INSERT INTO `gen_table_column` VALUES (1549, '196', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:40:48');
INSERT INTO `gen_table_column` VALUES (1550, '196', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:40:48');
INSERT INTO `gen_table_column` VALUES (1551, '196', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:40:48');
INSERT INTO `gen_table_column` VALUES (1552, '196', 'cluster_id', 'FME集群ID', 'bigint(20)', 'Long', 'clusterId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:40:48');
INSERT INTO `gen_table_column` VALUES (1553, '196', 'fme_id', 'FME的ID', 'bigint(20)', 'Long', 'fmeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:40:48');
INSERT INTO `gen_table_column` VALUES (1554, '196', 'weight', '节点在集群中的权重值', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:40:48');
INSERT INTO `gen_table_column` VALUES (1555, '197', 'id', '主键ID', 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:41:12');
INSERT INTO `gen_table_column` VALUES (1556, '197', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:41:12');
INSERT INTO `gen_table_column` VALUES (1557, '197', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:41:12');
INSERT INTO `gen_table_column` VALUES (1558, '197', 'dept_id', '分配给的租户', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:41:12');
INSERT INTO `gen_table_column` VALUES (1559, '197', 'fme_type', '1单节点，100集群', 'int(11)', 'Integer', 'fmeType', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 5, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:41:12');
INSERT INTO `gen_table_column` VALUES (1560, '197', 'fme_id', '当fme_type为1是，指向busi_fme的id字段，为100指向busi_fme_cluster的id字段', 'bigint(20)', 'Long', 'fmeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-03-17 16:37:31', '', '2021-03-17 16:41:12');
INSERT INTO `gen_table_column` VALUES (1561, '174', 'username', 'FME的连接用户名，同一个组下的用户名相同', 'varchar(32)', 'String', 'username', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 4, '', '2021-03-17 16:37:36', '', NULL);
INSERT INTO `gen_table_column` VALUES (1562, '174', 'password', 'FME的连接密码，同一个组下的密码相同', 'varchar(128)', 'String', 'password', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, '', '2021-03-17 16:37:36', '', NULL);
INSERT INTO `gen_table_column` VALUES (1563, '174', 'spare_fme_id', '备用FME（本节点宕机后指向的备用节点）', 'bigint(20)', 'Long', 'spareFmeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 12, '', '2021-03-17 16:37:36', '', NULL);
INSERT INTO `gen_table_column` VALUES (1564, '195', 'spare_fme_type', '备用fme类型，1单节点，100集群', 'int(11)', 'Integer', 'spareFmeType', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 5, '', '2021-03-19 13:45:12', '', NULL);
INSERT INTO `gen_table_column` VALUES (1565, '195', 'spare_fme_id', '当fme_type为1是，指向busi_fme的id字段，为100指向busi_fme_cluster的id字段', 'bigint(20)', 'Long', 'spareFmeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, '', '2021-03-19 13:45:12', '', NULL);
INSERT INTO `gen_table_column` VALUES (1566, '192', 'layout', '多分频轮询支持', 'varchar(32)', 'String', 'layout', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 9, '', '2021-04-01 18:12:05', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1568, '179', 'default_view_layout', '默认视图布局类型', 'varchar(32)', 'String', 'defaultViewLayout', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 14, '', '2021-04-08 14:40:48', '', NULL);
INSERT INTO `gen_table_column` VALUES (1569, '179', 'default_view_is_broadcast', '默认视图是否广播(1是，2否)', 'int(11)', 'Integer', 'defaultViewIsBroadcast', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 15, '', '2021-04-08 14:40:48', '', NULL);
INSERT INTO `gen_table_column` VALUES (1570, '179', 'default_view_is_display_self', '默认视图是否显示自己', 'int(11)', 'Integer', 'defaultViewIsDisplaySelf', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 16, '', '2021-04-08 14:40:48', '', NULL);
INSERT INTO `gen_table_column` VALUES (1571, '179', 'default_view_is_fill', '默认视图是否补位', 'int(11)', 'Integer', 'defaultViewIsFill', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 17, '', '2021-04-08 14:40:48', '', NULL);
INSERT INTO `gen_table_column` VALUES (1572, '198', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:27');
INSERT INTO `gen_table_column` VALUES (1573, '198', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:27');
INSERT INTO `gen_table_column` VALUES (1574, '198', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:27');
INSERT INTO `gen_table_column` VALUES (1576, '198', 'cell_sequence_number', '单元格序号', 'int(11)', 'Integer', 'cellSequenceNumber', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:27');
INSERT INTO `gen_table_column` VALUES (1577, '198', 'operation', '分频单元格对应的操作，默认为选看101，105轮询', 'int(11)', 'Integer', 'operation', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:27');
INSERT INTO `gen_table_column` VALUES (1578, '199', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:45');
INSERT INTO `gen_table_column` VALUES (1579, '199', 'create_time', NULL, 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:45');
INSERT INTO `gen_table_column` VALUES (1580, '199', 'update_time', NULL, 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:45');
INSERT INTO `gen_table_column` VALUES (1581, '199', 'template_conference_id', '关联的会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:45');
INSERT INTO `gen_table_column` VALUES (1582, '199', 'dept_id', '部门ID（部门也是FME终端，一种与会者）', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:45');
INSERT INTO `gen_table_column` VALUES (1583, '199', 'weight', '参会者顺序（权重倒叙排列）', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:43:45');
INSERT INTO `gen_table_column` VALUES (1584, '200', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:44:03');
INSERT INTO `gen_table_column` VALUES (1585, '200', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:44:03');
INSERT INTO `gen_table_column` VALUES (1586, '200', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:44:03');
INSERT INTO `gen_table_column` VALUES (1587, '200', 'template_conference_id', '关联的会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:44:03');
INSERT INTO `gen_table_column` VALUES (1588, '200', 'template_participant_id', '参会终端ID，关联busi_template_participant的ID', 'bigint(20)', 'Long', 'templateParticipantId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:44:03');
INSERT INTO `gen_table_column` VALUES (1589, '200', 'weight', '参会者顺序（权重倒叙排列）', 'int(11)', 'Integer', 'weight', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-04-08 14:41:08', '', '2021-04-08 14:44:03');
INSERT INTO `gen_table_column` VALUES (1591, '198', 'template_conference_id', '关联的会议模板ID', 'bigint(20)', 'Long', 'templateConferenceId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-04-08 15:26:36', '', NULL);
INSERT INTO `gen_table_column` VALUES (1592, '200', 'cell_sequence_number', '多分频单元格序号', 'int(11)', 'Integer', 'cellSequenceNumber', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, '', '2021-04-08 15:49:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (1593, '198', 'is_fixed', '分频单元格是否固定', 'int(11)', 'Integer', 'isFixed', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, '', '2021-04-08 16:05:55', '', NULL);
INSERT INTO `gen_table_column` VALUES (1594, '192', 'is_broadcast', '是否广播(1是，2否)', 'int(11)', 'Integer', 'isBroadcast', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 10, '', '2021-04-09 13:50:23', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1595, '192', 'is_display_self', '是否显示自己(1是，2否)', 'int(11)', 'Integer', 'isDisplaySelf', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 11, '', '2021-04-09 13:50:23', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1596, '192', 'is_fill', '是否补位(1是，2否)', 'int(11)', 'Integer', 'isFill', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 12, '', '2021-04-09 13:50:23', '', '2021-04-09 13:51:49');
INSERT INTO `gen_table_column` VALUES (1597, '179', 'polling_interval', '轮询时间间隔', 'int(11)', 'Integer', 'pollingInterval', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 18, '', '2021-04-13 16:28:52', '', NULL);
INSERT INTO `gen_table_column` VALUES (1598, '179', 'master_participant_id', '主会场ID', 'bigint(20)', 'Long', 'masterParticipantId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 14, '', '2021-04-15 17:46:50', '', NULL);
INSERT INTO `gen_table_column` VALUES (1600, '201', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1601, '201', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1602, '201', 'update_time', '更新时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1603, '201', 'name', '注册服务器名字', 'varchar(32)', 'String', 'name', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1604, '201', 'ip', '注册服务器ip地州', 'varchar(16)', 'String', 'ip', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1605, '201', 'port', '注册服务器端口', 'int(11)', 'Integer', 'port', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1606, '201', 'username', '注册服务器用户名', 'varchar(32)', 'String', 'username', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 7, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1607, '201', 'password', '注册服务器密码', 'varchar(64)', 'String', 'password', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:25');
INSERT INTO `gen_table_column` VALUES (1608, '202', 'id', NULL, 'bigint(20)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:49');
INSERT INTO `gen_table_column` VALUES (1609, '202', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', NULL, '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 2, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:49');
INSERT INTO `gen_table_column` VALUES (1610, '202', 'update_time', '修改时间', 'datetime', 'Date', 'updateTime', '0', '0', NULL, '1', '1', NULL, NULL, 'EQ', 'datetime', '', 3, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:49');
INSERT INTO `gen_table_column` VALUES (1611, '202', 'sfbc_server_id', 'SFBC服务器的id', 'bigint(20)', 'Long', 'sfbcServerId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:49');
INSERT INTO `gen_table_column` VALUES (1612, '202', 'dept_id', '部门ID', 'bigint(20)', 'Long', 'deptId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'superAdmin', '2021-04-20 18:20:40', '', '2021-04-20 18:21:49');
INSERT INTO `gen_table_column` VALUES (1613, '181', 'protocol', '协议', 'varchar(16)', 'String', 'protocol', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 13, '', '2021-04-20 18:23:02', '', NULL);
INSERT INTO `gen_table_column` VALUES (1614, '181', 'registration_time', 'SFBC终端最后注册时间', 'datetime', 'Date', 'registrationTime', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'datetime', '', 14, '', '2021-04-20 18:23:02', '', NULL);
INSERT INTO `gen_table_column` VALUES (1615, '181', 'intranet_ip', 'SFBC终端内网IP', 'varchar(16)', 'String', 'intranetIp', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 15, '', '2021-04-20 18:23:02', '', NULL);
INSERT INTO `gen_table_column` VALUES (1616, '181', 'port', 'SFBC终端端口', 'int(11)', 'Integer', 'port', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 16, '', '2021-04-20 18:23:02', '', NULL);
INSERT INTO `gen_table_column` VALUES (1617, '181', 'transport', 'SFBC终端的传输协议（TLS,TCP,UDP）', 'varchar(8)', 'String', 'transport', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 17, '', '2021-04-20 18:23:02', '', NULL);

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2021-01-18 10:39:23', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2021-01-18 10:39:23', '', NULL, '初始化密码 123456');
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2021-01-18 10:39:23', '', NULL, '深色主题theme-dark，浅色主题theme-light');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE,
  INDEX `ancestors`(`ancestors`) USING BTREE,
  INDEX `parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 218 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, 0, '0', '部门顶级节点', 0, 'superAdmin', '15888888888', NULL, '0', '0', 'superAdmin', '2021-01-18 10:39:18', 'superAdmin', '2021-04-20 03:26:19');
INSERT INTO `sys_dept` VALUES (100, 1, '0,1', '成都天堂云科技有限公司', 0, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-18 10:39:18', 'superAdmin', '2021-04-20 03:26:19');
INSERT INTO `sys_dept` VALUES (103, 100, '0,1,100', '伊犁州', 1, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-18 10:39:18', 'superAdmin', '2021-01-22 10:23:07');
INSERT INTO `sys_dept` VALUES (104, 100, '0,1,100', '克州', 2, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-18 10:39:18', 'admin', '2021-01-19 11:26:40');
INSERT INTO `sys_dept` VALUES (105, 100, '0,1,100', '喀什地区', 3, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-18 10:39:18', 'superAdmin', '2021-03-09 17:12:00');
INSERT INTO `sys_dept` VALUES (106, 100, '0,1,100', '和田地区', 4, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-18 10:39:18', 'superAdmin', '2021-03-09 17:12:07');
INSERT INTO `sys_dept` VALUES (107, 100, '0,1,100', '阿克苏地区', 5, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-18 10:39:18', 'superAdmin', '2021-03-09 17:12:13');
INSERT INTO `sys_dept` VALUES (200, 100, '0,1,100', '巴州', 6, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:25:26', 'admin', '2021-01-19 11:27:04');
INSERT INTO `sys_dept` VALUES (201, 100, '0,1,100', '吐鲁番', 7, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:25:37', 'admin', '2021-01-19 11:27:11');
INSERT INTO `sys_dept` VALUES (202, 100, '0,1,100', '哈密市', 8, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:26:03', 'admin', '2021-01-19 11:27:15');
INSERT INTO `sys_dept` VALUES (203, 100, '0,1,100', '乌鲁木齐市', 9, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:47:37', 'superAdmin', '2021-03-09 17:12:29');
INSERT INTO `sys_dept` VALUES (204, 100, '0,1,100', '昌吉州', 10, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:47:57', '', NULL);
INSERT INTO `sys_dept` VALUES (205, 100, '0,1,100', '塔城地区', 11, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:48:10', 'superAdmin', '2021-03-09 17:12:37');
INSERT INTO `sys_dept` VALUES (206, 100, '0,1,100', '阿勒泰地区', 12, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:48:20', 'superAdmin', '2021-03-09 17:12:44');
INSERT INTO `sys_dept` VALUES (207, 100, '0,1,100', '博州', 13, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:48:44', '', NULL);
INSERT INTO `sys_dept` VALUES (208, 100, '0,1,100', '克拉玛依市', 14, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-19 11:48:57', 'superAdmin', '2021-03-09 17:12:51');
INSERT INTO `sys_dept` VALUES (212, 100, '0,1,100', '测试上级部门', 22, NULL, NULL, NULL, '0', '0', 'superAdmin', '2021-03-24 12:25:09', 'superAdmin', '2021-03-29 18:22:38');
INSERT INTO `sys_dept` VALUES (213, 212, '0,1,100,212', '测试下级部门01', 23, NULL, NULL, NULL, '0', '0', 'superAdmin', '2021-03-24 12:25:28', '', NULL);
INSERT INTO `sys_dept` VALUES (214, 212, '0,1,100,212', '测试下级部门02', 24, NULL, NULL, NULL, '0', '0', 'superAdmin', '2021-03-24 12:25:44', '', NULL);
INSERT INTO `sys_dept` VALUES (215, 212, '0,1,100,212', '测试下级部门03', 25, NULL, NULL, NULL, '0', '0', 'superAdmin', '2021-03-29 18:21:50', 'superAdmin', '2021-03-29 18:22:38');
INSERT INTO `sys_dept` VALUES (216, 212, '0,1,100,212', '测试下级部门04', 26, NULL, NULL, NULL, '0', '0', 'superAdmin', '2021-03-29 18:22:05', 'superAdmin', '2021-03-29 18:22:32');
INSERT INTO `sys_dept` VALUES (217, 212, '0,1,100,212', '测试下级部门05', 30, NULL, NULL, NULL, '0', '0', 'superAdmin', '2021-03-29 18:22:24', '', NULL);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '性别未知');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '默认分组');
INSERT INTO `sys_dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '系统分组');
INSERT INTO `sys_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '系统默认是');
INSERT INTO `sys_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '系统默认否');
INSERT INTO `sys_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '通知');
INSERT INTO `sys_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '公告');
INSERT INTO `sys_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '关闭状态');
INSERT INTO `sys_dict_data` VALUES (18, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '新增操作');
INSERT INTO `sys_dict_data` VALUES (19, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '修改操作');
INSERT INTO `sys_dict_data` VALUES (20, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '删除操作');
INSERT INTO `sys_dict_data` VALUES (21, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '授权操作');
INSERT INTO `sys_dict_data` VALUES (22, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '导出操作');
INSERT INTO `sys_dict_data` VALUES (23, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '导入操作');
INSERT INTO `sys_dict_data` VALUES (24, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:23', '', NULL, '强退操作');
INSERT INTO `sys_dict_data` VALUES (25, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2021-01-18 10:39:23', '', NULL, '生成操作');
INSERT INTO `sys_dict_data` VALUES (26, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:23', '', NULL, '清空操作');
INSERT INTO `sys_dict_data` VALUES (27, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2021-01-18 10:39:23', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (28, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2021-01-18 10:39:23', '', NULL, '停用状态');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2021-01-18 10:39:22', '', NULL, '登录状态列表');

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime(0) NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 638 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (30, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-27 18:19:30');
INSERT INTO `sys_logininfor` VALUES (31, 'altAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-27 18:19:41');
INSERT INTO `sys_logininfor` VALUES (32, 'altAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-27 18:21:33');
INSERT INTO `sys_logininfor` VALUES (33, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-27 18:21:42');
INSERT INTO `sys_logininfor` VALUES (34, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 09:36:10');
INSERT INTO `sys_logininfor` VALUES (35, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 10:12:39');
INSERT INTO `sys_logininfor` VALUES (36, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 10:12:46');
INSERT INTO `sys_logininfor` VALUES (37, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 10:18:44');
INSERT INTO `sys_logininfor` VALUES (38, 'altAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-01-28 10:18:50');
INSERT INTO `sys_logininfor` VALUES (39, 'altAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 10:18:55');
INSERT INTO `sys_logininfor` VALUES (40, 'altAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 10:21:18');
INSERT INTO `sys_logininfor` VALUES (41, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 10:21:26');
INSERT INTO `sys_logininfor` VALUES (42, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 10:22:00');
INSERT INTO `sys_logininfor` VALUES (43, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-01-28 10:22:07');
INSERT INTO `sys_logininfor` VALUES (44, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 10:22:13');
INSERT INTO `sys_logininfor` VALUES (45, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-01-28 10:42:16');
INSERT INTO `sys_logininfor` VALUES (46, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 13:02:40');
INSERT INTO `sys_logininfor` VALUES (47, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-01-28 13:02:50');
INSERT INTO `sys_logininfor` VALUES (48, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 13:02:54');
INSERT INTO `sys_logininfor` VALUES (49, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 16:34:07');
INSERT INTO `sys_logininfor` VALUES (50, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-01-28 16:34:18');
INSERT INTO `sys_logininfor` VALUES (51, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 16:34:22');
INSERT INTO `sys_logininfor` VALUES (52, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 16:37:15');
INSERT INTO `sys_logininfor` VALUES (53, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 16:37:22');
INSERT INTO `sys_logininfor` VALUES (54, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 17:14:28');
INSERT INTO `sys_logininfor` VALUES (55, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 17:14:37');
INSERT INTO `sys_logininfor` VALUES (56, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 17:44:34');
INSERT INTO `sys_logininfor` VALUES (57, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 17:45:01');
INSERT INTO `sys_logininfor` VALUES (58, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-28 18:57:20');
INSERT INTO `sys_logininfor` VALUES (59, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 18:57:41');
INSERT INTO `sys_logininfor` VALUES (60, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-01-28 19:36:20');
INSERT INTO `sys_logininfor` VALUES (61, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-01-28 19:36:24');
INSERT INTO `sys_logininfor` VALUES (62, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-28 19:36:30');
INSERT INTO `sys_logininfor` VALUES (63, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-28 19:37:24');
INSERT INTO `sys_logininfor` VALUES (64, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-28 19:45:24');
INSERT INTO `sys_logininfor` VALUES (65, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-01-28 19:46:17');
INSERT INTO `sys_logininfor` VALUES (66, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 09:46:01');
INSERT INTO `sys_logininfor` VALUES (67, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-01-29 13:12:19');
INSERT INTO `sys_logininfor` VALUES (68, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-01-29 13:12:27');
INSERT INTO `sys_logininfor` VALUES (69, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 13:12:30');
INSERT INTO `sys_logininfor` VALUES (70, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 13:15:30');
INSERT INTO `sys_logininfor` VALUES (71, 'altAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 13:16:02');
INSERT INTO `sys_logininfor` VALUES (72, 'altAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 13:45:09');
INSERT INTO `sys_logininfor` VALUES (73, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-01-29 13:45:23');
INSERT INTO `sys_logininfor` VALUES (74, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 13:45:29');
INSERT INTO `sys_logininfor` VALUES (75, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 13:58:33');
INSERT INTO `sys_logininfor` VALUES (76, 'altAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 13:58:47');
INSERT INTO `sys_logininfor` VALUES (77, 'altAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 14:01:26');
INSERT INTO `sys_logininfor` VALUES (78, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 14:01:40');
INSERT INTO `sys_logininfor` VALUES (79, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-01-29 14:34:00');
INSERT INTO `sys_logininfor` VALUES (80, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-01-29 14:34:05');
INSERT INTO `sys_logininfor` VALUES (81, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 17:30:32');
INSERT INTO `sys_logininfor` VALUES (82, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 17:30:39');
INSERT INTO `sys_logininfor` VALUES (83, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 17:33:52');
INSERT INTO `sys_logininfor` VALUES (84, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 17:34:01');
INSERT INTO `sys_logininfor` VALUES (85, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 17:34:51');
INSERT INTO `sys_logininfor` VALUES (86, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 17:34:56');
INSERT INTO `sys_logininfor` VALUES (87, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 17:58:01');
INSERT INTO `sys_logininfor` VALUES (88, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 17:58:07');
INSERT INTO `sys_logininfor` VALUES (89, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 18:36:01');
INSERT INTO `sys_logininfor` VALUES (90, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:36:03');
INSERT INTO `sys_logininfor` VALUES (91, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:36:11');
INSERT INTO `sys_logininfor` VALUES (92, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:36:11');
INSERT INTO `sys_logininfor` VALUES (93, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:37:35');
INSERT INTO `sys_logininfor` VALUES (94, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:37:35');
INSERT INTO `sys_logininfor` VALUES (95, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:40:07');
INSERT INTO `sys_logininfor` VALUES (96, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:40:07');
INSERT INTO `sys_logininfor` VALUES (97, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:43:23');
INSERT INTO `sys_logininfor` VALUES (98, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:43:23');
INSERT INTO `sys_logininfor` VALUES (99, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:43:49');
INSERT INTO `sys_logininfor` VALUES (100, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:43:49');
INSERT INTO `sys_logininfor` VALUES (101, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:44:37');
INSERT INTO `sys_logininfor` VALUES (102, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:44:37');
INSERT INTO `sys_logininfor` VALUES (103, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:44:55');
INSERT INTO `sys_logininfor` VALUES (104, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:44:55');
INSERT INTO `sys_logininfor` VALUES (105, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-01-29 18:45:08');
INSERT INTO `sys_logininfor` VALUES (106, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:45:16');
INSERT INTO `sys_logininfor` VALUES (107, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:45:16');
INSERT INTO `sys_logininfor` VALUES (108, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-01-29 18:45:51');
INSERT INTO `sys_logininfor` VALUES (109, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-01-29 18:45:55');
INSERT INTO `sys_logininfor` VALUES (110, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 18:45:59');
INSERT INTO `sys_logininfor` VALUES (111, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 18:45:59');
INSERT INTO `sys_logininfor` VALUES (112, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-29 18:46:08');
INSERT INTO `sys_logininfor` VALUES (113, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 18:46:12');
INSERT INTO `sys_logininfor` VALUES (114, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-01-29 18:46:41');
INSERT INTO `sys_logininfor` VALUES (115, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:46:48');
INSERT INTO `sys_logininfor` VALUES (116, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:47:44');
INSERT INTO `sys_logininfor` VALUES (117, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-01-29 18:48:03');
INSERT INTO `sys_logininfor` VALUES (118, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:48:09');
INSERT INTO `sys_logininfor` VALUES (119, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:48:09');
INSERT INTO `sys_logininfor` VALUES (120, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-01-29 18:48:43');
INSERT INTO `sys_logininfor` VALUES (121, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-01-29 18:48:46');
INSERT INTO `sys_logininfor` VALUES (122, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:48:50');
INSERT INTO `sys_logininfor` VALUES (123, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-29 18:48:58');
INSERT INTO `sys_logininfor` VALUES (124, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-01-29 18:50:26');
INSERT INTO `sys_logininfor` VALUES (125, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-01-29 18:50:27');
INSERT INTO `sys_logininfor` VALUES (126, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-01-29 18:50:36');
INSERT INTO `sys_logininfor` VALUES (127, 'admin', '172.16.101.201', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-29 18:50:45');
INSERT INTO `sys_logininfor` VALUES (128, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码已失效', '2021-01-29 18:56:30');
INSERT INTO `sys_logininfor` VALUES (129, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-29 18:56:34');
INSERT INTO `sys_logininfor` VALUES (130, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-30 10:12:35');
INSERT INTO `sys_logininfor` VALUES (131, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-30 10:13:59');
INSERT INTO `sys_logininfor` VALUES (132, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-30 10:14:07');
INSERT INTO `sys_logininfor` VALUES (133, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-30 10:25:40');
INSERT INTO `sys_logininfor` VALUES (134, 'altAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-30 10:25:55');
INSERT INTO `sys_logininfor` VALUES (135, 'altAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-30 10:27:44');
INSERT INTO `sys_logininfor` VALUES (136, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-30 10:28:00');
INSERT INTO `sys_logininfor` VALUES (137, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-01-30 11:04:32');
INSERT INTO `sys_logininfor` VALUES (138, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-30 11:04:42');
INSERT INTO `sys_logininfor` VALUES (139, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-30 12:04:52');
INSERT INTO `sys_logininfor` VALUES (140, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-30 12:04:57');
INSERT INTO `sys_logininfor` VALUES (141, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-01-30 13:08:05');
INSERT INTO `sys_logininfor` VALUES (142, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码已失效', '2021-01-30 13:14:03');
INSERT INTO `sys_logininfor` VALUES (143, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-01-30 13:14:07');
INSERT INTO `sys_logininfor` VALUES (144, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-01-30 13:14:10');
INSERT INTO `sys_logininfor` VALUES (145, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-01-30 13:52:17');
INSERT INTO `sys_logininfor` VALUES (146, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-01-30 13:52:34');
INSERT INTO `sys_logininfor` VALUES (147, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-01 09:33:33');
INSERT INTO `sys_logininfor` VALUES (148, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-01 09:33:37');
INSERT INTO `sys_logininfor` VALUES (149, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-01 13:28:26');
INSERT INTO `sys_logininfor` VALUES (150, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-02-01 13:28:36');
INSERT INTO `sys_logininfor` VALUES (151, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-01 13:28:43');
INSERT INTO `sys_logininfor` VALUES (152, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-01 15:43:07');
INSERT INTO `sys_logininfor` VALUES (153, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-01 15:43:16');
INSERT INTO `sys_logininfor` VALUES (154, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-01 15:43:21');
INSERT INTO `sys_logininfor` VALUES (155, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-02 09:33:43');
INSERT INTO `sys_logininfor` VALUES (156, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-02 18:08:06');
INSERT INTO `sys_logininfor` VALUES (157, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-03 09:43:02');
INSERT INTO `sys_logininfor` VALUES (158, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 09:43:06');
INSERT INTO `sys_logininfor` VALUES (159, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 09:43:25');
INSERT INTO `sys_logininfor` VALUES (160, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 11:47:34');
INSERT INTO `sys_logininfor` VALUES (161, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 14:39:32');
INSERT INTO `sys_logininfor` VALUES (162, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 18:27:49');
INSERT INTO `sys_logininfor` VALUES (163, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-03 18:28:13');
INSERT INTO `sys_logininfor` VALUES (164, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 18:28:16');
INSERT INTO `sys_logininfor` VALUES (165, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 18:35:29');
INSERT INTO `sys_logininfor` VALUES (166, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 18:35:35');
INSERT INTO `sys_logininfor` VALUES (167, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 19:10:33');
INSERT INTO `sys_logininfor` VALUES (168, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 19:10:38');
INSERT INTO `sys_logininfor` VALUES (169, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 19:15:28');
INSERT INTO `sys_logininfor` VALUES (170, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 19:15:34');
INSERT INTO `sys_logininfor` VALUES (171, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 19:22:59');
INSERT INTO `sys_logininfor` VALUES (172, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 19:23:10');
INSERT INTO `sys_logininfor` VALUES (173, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 19:33:11');
INSERT INTO `sys_logininfor` VALUES (174, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 19:33:19');
INSERT INTO `sys_logininfor` VALUES (175, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 19:34:16');
INSERT INTO `sys_logininfor` VALUES (176, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 19:34:24');
INSERT INTO `sys_logininfor` VALUES (177, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-03 20:19:11');
INSERT INTO `sys_logininfor` VALUES (178, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-03 20:19:22');
INSERT INTO `sys_logininfor` VALUES (179, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-04 11:38:45');
INSERT INTO `sys_logininfor` VALUES (180, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-04 11:38:51');
INSERT INTO `sys_logininfor` VALUES (181, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-04 12:02:33');
INSERT INTO `sys_logininfor` VALUES (182, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-04 12:02:45');
INSERT INTO `sys_logininfor` VALUES (183, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-04 12:02:49');
INSERT INTO `sys_logininfor` VALUES (184, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-04 12:02:54');
INSERT INTO `sys_logininfor` VALUES (185, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-04 15:49:16');
INSERT INTO `sys_logininfor` VALUES (186, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-04 15:49:23');
INSERT INTO `sys_logininfor` VALUES (187, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-05 09:47:01');
INSERT INTO `sys_logininfor` VALUES (188, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-05 11:34:58');
INSERT INTO `sys_logininfor` VALUES (189, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-05 11:36:18');
INSERT INTO `sys_logininfor` VALUES (190, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-05 11:36:39');
INSERT INTO `sys_logininfor` VALUES (191, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-05 11:36:49');
INSERT INTO `sys_logininfor` VALUES (192, 'admin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-05 12:51:49');
INSERT INTO `sys_logininfor` VALUES (193, 'admin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-05 12:51:53');
INSERT INTO `sys_logininfor` VALUES (194, 'superAdmin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '验证码错误', '2021-02-05 12:52:02');
INSERT INTO `sys_logininfor` VALUES (195, 'superAdmin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-02-05 12:52:06');
INSERT INTO `sys_logininfor` VALUES (196, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-05 12:58:17');
INSERT INTO `sys_logininfor` VALUES (197, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-05 12:58:33');
INSERT INTO `sys_logininfor` VALUES (198, 'admin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-05 13:49:43');
INSERT INTO `sys_logininfor` VALUES (199, 'admin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-05 13:49:46');
INSERT INTO `sys_logininfor` VALUES (200, 'superAdmin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-02-05 13:50:08');
INSERT INTO `sys_logininfor` VALUES (201, 'superAdmin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '0', '退出成功', '2021-02-05 14:06:47');
INSERT INTO `sys_logininfor` VALUES (202, 'admin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-05 14:07:31');
INSERT INTO `sys_logininfor` VALUES (203, 'superAdmin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '验证码错误', '2021-02-05 14:07:47');
INSERT INTO `sys_logininfor` VALUES (204, 'superAdmin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-05 14:07:55');
INSERT INTO `sys_logininfor` VALUES (205, 'superAdmin', '172.16.101.216', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-02-05 14:08:10');
INSERT INTO `sys_logininfor` VALUES (206, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-05 19:57:02');
INSERT INTO `sys_logininfor` VALUES (207, 'superAdmin', '172.16.101.216', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-05 19:57:07');
INSERT INTO `sys_logininfor` VALUES (208, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-07 09:52:33');
INSERT INTO `sys_logininfor` VALUES (209, 'ylzAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-02-08 12:24:11');
INSERT INTO `sys_logininfor` VALUES (210, 'ylzAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-02-08 12:24:41');
INSERT INTO `sys_logininfor` VALUES (211, 'ylzAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-02-08 12:25:02');
INSERT INTO `sys_logininfor` VALUES (212, 'admin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-08 17:14:50');
INSERT INTO `sys_logininfor` VALUES (213, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-08 17:15:00');
INSERT INTO `sys_logininfor` VALUES (214, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-08 17:15:06');
INSERT INTO `sys_logininfor` VALUES (215, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-08 17:15:11');
INSERT INTO `sys_logininfor` VALUES (216, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-08 17:19:27');
INSERT INTO `sys_logininfor` VALUES (217, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-08 17:19:33');
INSERT INTO `sys_logininfor` VALUES (218, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-08 18:12:23');
INSERT INTO `sys_logininfor` VALUES (219, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-08 18:12:29');
INSERT INTO `sys_logininfor` VALUES (220, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-08 19:04:01');
INSERT INTO `sys_logininfor` VALUES (221, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-09 12:01:47');
INSERT INTO `sys_logininfor` VALUES (222, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-09 12:42:11');
INSERT INTO `sys_logininfor` VALUES (223, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-09 12:42:15');
INSERT INTO `sys_logininfor` VALUES (224, 'superAdmin', '182.130.249.15', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-09 15:00:33');
INSERT INTO `sys_logininfor` VALUES (225, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-09 15:54:38');
INSERT INTO `sys_logininfor` VALUES (226, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-09 15:54:48');
INSERT INTO `sys_logininfor` VALUES (227, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-09 15:54:56');
INSERT INTO `sys_logininfor` VALUES (228, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-09 15:55:02');
INSERT INTO `sys_logininfor` VALUES (229, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-19 10:01:11');
INSERT INTO `sys_logininfor` VALUES (230, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-19 10:37:25');
INSERT INTO `sys_logininfor` VALUES (231, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-02-19 11:04:08');
INSERT INTO `sys_logininfor` VALUES (232, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-19 11:04:18');
INSERT INTO `sys_logininfor` VALUES (233, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-19 14:51:17');
INSERT INTO `sys_logininfor` VALUES (234, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-19 14:51:25');
INSERT INTO `sys_logininfor` VALUES (235, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-19 14:51:37');
INSERT INTO `sys_logininfor` VALUES (236, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-19 14:51:48');
INSERT INTO `sys_logininfor` VALUES (237, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-19 14:51:54');
INSERT INTO `sys_logininfor` VALUES (238, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-19 15:32:03');
INSERT INTO `sys_logininfor` VALUES (239, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-19 15:32:18');
INSERT INTO `sys_logininfor` VALUES (240, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-19 15:38:57');
INSERT INTO `sys_logininfor` VALUES (241, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-19 15:39:37');
INSERT INTO `sys_logininfor` VALUES (242, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-19 16:25:58');
INSERT INTO `sys_logininfor` VALUES (243, 'admin', '172.16.100.151', '内网IP', 'Safari', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-19 18:04:56');
INSERT INTO `sys_logininfor` VALUES (244, 'admin', '172.16.100.151', '内网IP', 'Safari', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-19 18:05:05');
INSERT INTO `sys_logininfor` VALUES (245, 'admin', '172.16.100.151', '内网IP', 'Safari', 'Mac OS X', '0', '登录成功', '2021-02-19 18:05:19');
INSERT INTO `sys_logininfor` VALUES (246, 'admin', '172.16.100.151', '内网IP', 'Safari', 'Mac OS X', '0', '退出成功', '2021-02-19 18:05:48');
INSERT INTO `sys_logininfor` VALUES (247, 'superAdmin', '172.16.100.151', '内网IP', 'Safari', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-19 18:06:06');
INSERT INTO `sys_logininfor` VALUES (248, 'superAdmin', '172.16.100.151', '内网IP', 'Safari', 'Mac OS X', '0', '登录成功', '2021-02-19 18:06:16');
INSERT INTO `sys_logininfor` VALUES (249, 'superAdmin', '172.16.100.88', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-02-19 18:16:50');
INSERT INTO `sys_logininfor` VALUES (250, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码已失效', '2021-02-20 09:40:37');
INSERT INTO `sys_logininfor` VALUES (251, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-20 09:40:43');
INSERT INTO `sys_logininfor` VALUES (252, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (253, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (254, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (255, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (256, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (257, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (258, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (259, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-20 12:51:11');
INSERT INTO `sys_logininfor` VALUES (260, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-22 10:02:01');
INSERT INTO `sys_logininfor` VALUES (261, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-23 10:37:23');
INSERT INTO `sys_logininfor` VALUES (262, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-23 10:58:33');
INSERT INTO `sys_logininfor` VALUES (263, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-23 10:58:37');
INSERT INTO `sys_logininfor` VALUES (264, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-23 10:58:59');
INSERT INTO `sys_logininfor` VALUES (265, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-23 10:59:05');
INSERT INTO `sys_logininfor` VALUES (266, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-23 12:30:04');
INSERT INTO `sys_logininfor` VALUES (267, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-23 14:20:02');
INSERT INTO `sys_logininfor` VALUES (268, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-23 14:20:13');
INSERT INTO `sys_logininfor` VALUES (269, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-23 16:32:17');
INSERT INTO `sys_logininfor` VALUES (270, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-23 16:32:28');
INSERT INTO `sys_logininfor` VALUES (271, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-23 16:32:33');
INSERT INTO `sys_logininfor` VALUES (272, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-23 16:32:42');
INSERT INTO `sys_logininfor` VALUES (273, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-23 16:32:46');
INSERT INTO `sys_logininfor` VALUES (274, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-02-23 16:32:54');
INSERT INTO `sys_logininfor` VALUES (275, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-02-23 16:33:01');
INSERT INTO `sys_logininfor` VALUES (276, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-23 16:33:07');
INSERT INTO `sys_logininfor` VALUES (277, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-23 19:47:33');
INSERT INTO `sys_logininfor` VALUES (278, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-23 20:27:33');
INSERT INTO `sys_logininfor` VALUES (279, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-24 09:56:00');
INSERT INTO `sys_logininfor` VALUES (280, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-24 18:38:20');
INSERT INTO `sys_logininfor` VALUES (281, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-25 18:17:29');
INSERT INTO `sys_logininfor` VALUES (282, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-25 18:17:37');
INSERT INTO `sys_logininfor` VALUES (283, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-26 09:40:19');
INSERT INTO `sys_logininfor` VALUES (284, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-26 10:11:12');
INSERT INTO `sys_logininfor` VALUES (285, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-26 10:11:18');
INSERT INTO `sys_logininfor` VALUES (286, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-26 10:34:48');
INSERT INTO `sys_logininfor` VALUES (287, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-26 10:37:09');
INSERT INTO `sys_logininfor` VALUES (288, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-02-26 15:50:39');
INSERT INTO `sys_logininfor` VALUES (289, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-26 15:52:12');
INSERT INTO `sys_logininfor` VALUES (290, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-26 17:28:50');
INSERT INTO `sys_logininfor` VALUES (291, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-02-28 10:04:21');
INSERT INTO `sys_logininfor` VALUES (292, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-02-28 15:32:48');
INSERT INTO `sys_logininfor` VALUES (293, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-02-28 15:32:54');
INSERT INTO `sys_logininfor` VALUES (294, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-01 09:39:01');
INSERT INTO `sys_logininfor` VALUES (295, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-01 09:39:24');
INSERT INTO `sys_logininfor` VALUES (296, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-01 09:41:06');
INSERT INTO `sys_logininfor` VALUES (297, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-01 09:41:10');
INSERT INTO `sys_logininfor` VALUES (298, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-01 11:09:20');
INSERT INTO `sys_logininfor` VALUES (299, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-01 11:09:27');
INSERT INTO `sys_logininfor` VALUES (300, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-01 11:25:11');
INSERT INTO `sys_logininfor` VALUES (301, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-01 11:25:15');
INSERT INTO `sys_logininfor` VALUES (302, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-01 13:48:17');
INSERT INTO `sys_logininfor` VALUES (303, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-01 14:06:03');
INSERT INTO `sys_logininfor` VALUES (304, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-01 14:06:10');
INSERT INTO `sys_logininfor` VALUES (305, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-01 14:18:53');
INSERT INTO `sys_logininfor` VALUES (306, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-01 14:19:00');
INSERT INTO `sys_logininfor` VALUES (307, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-03-01 15:32:48');
INSERT INTO `sys_logininfor` VALUES (308, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-03-01 15:32:53');
INSERT INTO `sys_logininfor` VALUES (309, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-03-01 15:32:59');
INSERT INTO `sys_logininfor` VALUES (310, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-01 15:33:07');
INSERT INTO `sys_logininfor` VALUES (311, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-01 17:27:56');
INSERT INTO `sys_logininfor` VALUES (312, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-01 17:34:46');
INSERT INTO `sys_logininfor` VALUES (313, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-01 17:34:55');
INSERT INTO `sys_logininfor` VALUES (314, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-02 09:41:42');
INSERT INTO `sys_logininfor` VALUES (315, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-02 09:52:53');
INSERT INTO `sys_logininfor` VALUES (316, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-02 09:53:08');
INSERT INTO `sys_logininfor` VALUES (317, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 09:53:59');
INSERT INTO `sys_logininfor` VALUES (318, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-03 09:56:48');
INSERT INTO `sys_logininfor` VALUES (319, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 09:56:57');
INSERT INTO `sys_logininfor` VALUES (320, 'superAdmin', '172.16.101.230', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-03-03 10:32:51');
INSERT INTO `sys_logininfor` VALUES (321, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-03 10:38:39');
INSERT INTO `sys_logininfor` VALUES (322, 'admin', '172.16.101.230', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-03-03 10:39:33');
INSERT INTO `sys_logininfor` VALUES (323, 'superAdmin', '172.16.101.230', '内网IP', 'Firefox 8', 'Mac OS X', '1', '验证码错误', '2021-03-03 10:39:44');
INSERT INTO `sys_logininfor` VALUES (324, 'superAdmin', '172.16.101.230', '内网IP', 'Firefox 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-03-03 10:39:47');
INSERT INTO `sys_logininfor` VALUES (325, 'superAdmin', '172.16.101.230', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-03-03 10:39:55');
INSERT INTO `sys_logininfor` VALUES (326, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-03 10:44:10');
INSERT INTO `sys_logininfor` VALUES (327, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-03 11:23:09');
INSERT INTO `sys_logininfor` VALUES (328, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 11:23:14');
INSERT INTO `sys_logininfor` VALUES (329, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-03 11:24:45');
INSERT INTO `sys_logininfor` VALUES (330, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 11:24:53');
INSERT INTO `sys_logininfor` VALUES (331, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-03 11:25:45');
INSERT INTO `sys_logininfor` VALUES (332, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 11:25:53');
INSERT INTO `sys_logininfor` VALUES (333, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-03 11:28:13');
INSERT INTO `sys_logininfor` VALUES (334, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 11:28:19');
INSERT INTO `sys_logininfor` VALUES (335, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-03 11:30:37');
INSERT INTO `sys_logininfor` VALUES (336, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 11:30:41');
INSERT INTO `sys_logininfor` VALUES (337, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-03 11:34:39');
INSERT INTO `sys_logininfor` VALUES (338, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 11:34:54');
INSERT INTO `sys_logininfor` VALUES (339, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-03 11:39:15');
INSERT INTO `sys_logininfor` VALUES (340, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-03 12:05:31');
INSERT INTO `sys_logininfor` VALUES (341, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-03 12:25:42');
INSERT INTO `sys_logininfor` VALUES (342, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-03 14:21:53');
INSERT INTO `sys_logininfor` VALUES (343, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-03 14:22:00');
INSERT INTO `sys_logininfor` VALUES (344, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-03 14:37:27');
INSERT INTO `sys_logininfor` VALUES (345, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-04 09:51:41');
INSERT INTO `sys_logininfor` VALUES (346, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-05 09:54:06');
INSERT INTO `sys_logininfor` VALUES (347, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-05 09:54:32');
INSERT INTO `sys_logininfor` VALUES (348, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-05 09:54:39');
INSERT INTO `sys_logininfor` VALUES (349, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-05 09:54:42');
INSERT INTO `sys_logininfor` VALUES (350, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-05 09:54:52');
INSERT INTO `sys_logininfor` VALUES (351, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-05 09:54:58');
INSERT INTO `sys_logininfor` VALUES (352, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-05 09:55:03');
INSERT INTO `sys_logininfor` VALUES (353, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-05 15:04:04');
INSERT INTO `sys_logininfor` VALUES (354, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-05 15:04:10');
INSERT INTO `sys_logininfor` VALUES (355, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-03-05 15:07:08');
INSERT INTO `sys_logininfor` VALUES (356, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-05 15:07:12');
INSERT INTO `sys_logininfor` VALUES (357, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-05 15:13:49');
INSERT INTO `sys_logininfor` VALUES (358, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-05 15:49:49');
INSERT INTO `sys_logininfor` VALUES (359, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-05 15:50:38');
INSERT INTO `sys_logininfor` VALUES (360, 'admin', '183.221.21.60', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-05 16:01:30');
INSERT INTO `sys_logininfor` VALUES (361, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-08 09:56:32');
INSERT INTO `sys_logininfor` VALUES (362, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-08 10:13:19');
INSERT INTO `sys_logininfor` VALUES (363, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-08 10:15:49');
INSERT INTO `sys_logininfor` VALUES (364, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-08 10:41:20');
INSERT INTO `sys_logininfor` VALUES (365, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-08 10:41:26');
INSERT INTO `sys_logininfor` VALUES (366, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-09 09:36:29');
INSERT INTO `sys_logininfor` VALUES (367, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-09 09:36:29');
INSERT INTO `sys_logininfor` VALUES (368, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-09 09:36:33');
INSERT INTO `sys_logininfor` VALUES (369, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 09:36:46');
INSERT INTO `sys_logininfor` VALUES (370, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-09 09:45:58');
INSERT INTO `sys_logininfor` VALUES (371, 'admin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-03-09 14:28:23');
INSERT INTO `sys_logininfor` VALUES (372, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 14:28:42');
INSERT INTO `sys_logininfor` VALUES (373, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-03-09 14:29:40');
INSERT INTO `sys_logininfor` VALUES (374, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '1', '验证码错误', '2021-03-09 14:29:50');
INSERT INTO `sys_logininfor` VALUES (375, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 14:30:08');
INSERT INTO `sys_logininfor` VALUES (376, 'admin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-03-09 14:30:35');
INSERT INTO `sys_logininfor` VALUES (377, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '1', '验证码错误', '2021-03-09 14:30:58');
INSERT INTO `sys_logininfor` VALUES (378, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 14:31:06');
INSERT INTO `sys_logininfor` VALUES (379, 'admin', '172.16.101.131', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-03-09 14:32:47');
INSERT INTO `sys_logininfor` VALUES (380, 'superAdmin', '172.16.101.131', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-09 14:33:04');
INSERT INTO `sys_logininfor` VALUES (381, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 14:34:19');
INSERT INTO `sys_logininfor` VALUES (382, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 14:35:12');
INSERT INTO `sys_logininfor` VALUES (383, 'superAdmin', '172.16.100.160', '内网IP', 'Chrome 8', 'Windows 7', '0', '登录成功', '2021-03-09 14:36:51');
INSERT INTO `sys_logininfor` VALUES (384, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-09 14:38:07');
INSERT INTO `sys_logininfor` VALUES (385, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 14:39:03');
INSERT INTO `sys_logininfor` VALUES (386, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-09 14:39:08');
INSERT INTO `sys_logininfor` VALUES (387, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-03-09 14:40:10');
INSERT INTO `sys_logininfor` VALUES (388, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '1', '用户不存在/密码错误', '2021-03-09 14:40:23');
INSERT INTO `sys_logininfor` VALUES (389, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 14:40:37');
INSERT INTO `sys_logininfor` VALUES (390, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 14:42:03');
INSERT INTO `sys_logininfor` VALUES (391, 'superAdmin', '172.16.101.221', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 14:45:48');
INSERT INTO `sys_logininfor` VALUES (392, 'admin', '172.16.101.131', '内网IP', 'Firefox 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-03-09 14:54:07');
INSERT INTO `sys_logininfor` VALUES (393, 'superAdmin', '172.16.101.131', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-03-09 14:54:21');
INSERT INTO `sys_logininfor` VALUES (394, 'superAdmin', '172.16.101.131', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-09 14:54:26');
INSERT INTO `sys_logininfor` VALUES (395, 'superAdmin', '172.16.101.221', '内网IP', 'Firefox 8', 'Mac OS X', '1', '验证码错误', '2021-03-09 15:04:48');
INSERT INTO `sys_logininfor` VALUES (396, 'superAdmin', '172.16.101.221', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-03-09 15:04:52');
INSERT INTO `sys_logininfor` VALUES (397, 'superAdmin', '172.16.100.98', '内网IP', 'Firefox', 'Windows 10', '0', '登录成功', '2021-03-09 15:06:28');
INSERT INTO `sys_logininfor` VALUES (398, 'superAdmin', '172.16.100.160', '内网IP', 'Chrome', 'Windows 7', '0', '登录成功', '2021-03-09 15:07:37');
INSERT INTO `sys_logininfor` VALUES (399, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 15:10:07');
INSERT INTO `sys_logininfor` VALUES (400, 'admin', '172.16.101.212', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-09 15:13:06');
INSERT INTO `sys_logininfor` VALUES (401, 'admin', '172.16.101.212', '内网IP', 'Chrome 8', 'Windows 10', '1', '用户不存在/密码错误', '2021-03-09 15:23:49');
INSERT INTO `sys_logininfor` VALUES (402, 'admin', '172.16.101.212', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 15:24:41');
INSERT INTO `sys_logininfor` VALUES (403, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 15:27:50');
INSERT INTO `sys_logininfor` VALUES (404, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-09 15:32:43');
INSERT INTO `sys_logininfor` VALUES (405, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-09 15:33:18');
INSERT INTO `sys_logininfor` VALUES (406, 'admin', '172.16.101.169', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 15:37:48');
INSERT INTO `sys_logininfor` VALUES (407, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-09 15:42:12');
INSERT INTO `sys_logininfor` VALUES (408, 'admin', '192.166.0.97', 'XX XX', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-10 03:14:26');
INSERT INTO `sys_logininfor` VALUES (409, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-10 03:30:26');
INSERT INTO `sys_logininfor` VALUES (410, 'admin', '192.166.0.97', 'XX XX', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-10 03:34:53');
INSERT INTO `sys_logininfor` VALUES (411, 'admin', '192.166.0.131', 'XX XX', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-10 03:35:48');
INSERT INTO `sys_logininfor` VALUES (412, 'admin', '192.166.0.77', 'XX XX', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-10 03:48:38');
INSERT INTO `sys_logininfor` VALUES (413, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-10 03:50:06');
INSERT INTO `sys_logininfor` VALUES (414, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-10 03:50:10');
INSERT INTO `sys_logininfor` VALUES (415, 'superAdmin', '192.166.0.97', 'XX XX', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-10 03:56:00');
INSERT INTO `sys_logininfor` VALUES (416, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-10 17:47:43');
INSERT INTO `sys_logininfor` VALUES (417, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-10 17:47:52');
INSERT INTO `sys_logininfor` VALUES (418, 'superAdmin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-10 17:48:03');
INSERT INTO `sys_logininfor` VALUES (419, 'admin', '192.166.0.97', 'XX XX', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-10 17:50:44');
INSERT INTO `sys_logininfor` VALUES (420, 'superAdmin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-10 18:06:29');
INSERT INTO `sys_logininfor` VALUES (421, 'superAdmin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-10 18:06:58');
INSERT INTO `sys_logininfor` VALUES (422, 'superAdmin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-10 18:08:52');
INSERT INTO `sys_logininfor` VALUES (423, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-10 18:08:58');
INSERT INTO `sys_logininfor` VALUES (424, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-11 04:46:56');
INSERT INTO `sys_logininfor` VALUES (425, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-11 05:09:56');
INSERT INTO `sys_logininfor` VALUES (426, 'admin', '192.166.0.77', 'XX XX', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-11 06:12:55');
INSERT INTO `sys_logininfor` VALUES (427, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-11 17:57:15');
INSERT INTO `sys_logininfor` VALUES (428, 'admin', '192.166.0.131', 'XX XX', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-11 18:28:04');
INSERT INTO `sys_logininfor` VALUES (429, 'admin', '192.166.0.99', 'XX XX', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-11 20:46:30');
INSERT INTO `sys_logininfor` VALUES (430, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-11 18:13:25');
INSERT INTO `sys_logininfor` VALUES (431, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-11 19:19:19');
INSERT INTO `sys_logininfor` VALUES (432, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-11 19:19:40');
INSERT INTO `sys_logininfor` VALUES (433, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-12 11:17:22');
INSERT INTO `sys_logininfor` VALUES (434, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-12 14:24:26');
INSERT INTO `sys_logininfor` VALUES (435, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-12 14:24:32');
INSERT INTO `sys_logininfor` VALUES (436, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-12 14:33:29');
INSERT INTO `sys_logininfor` VALUES (437, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-12 14:33:36');
INSERT INTO `sys_logininfor` VALUES (438, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-12 14:33:56');
INSERT INTO `sys_logininfor` VALUES (439, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-12 14:34:02');
INSERT INTO `sys_logininfor` VALUES (440, 'admin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-12 21:55:58');
INSERT INTO `sys_logininfor` VALUES (441, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-12 21:56:05');
INSERT INTO `sys_logininfor` VALUES (442, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-12 22:18:03');
INSERT INTO `sys_logininfor` VALUES (443, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-15 18:37:06');
INSERT INTO `sys_logininfor` VALUES (444, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-15 18:37:27');
INSERT INTO `sys_logininfor` VALUES (445, 'superAdmin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-15 18:37:33');
INSERT INTO `sys_logininfor` VALUES (446, 'admin', '172.16.101.229', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-16 09:40:07');
INSERT INTO `sys_logininfor` VALUES (447, 'admin', '172.16.101.229', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-16 09:41:53');
INSERT INTO `sys_logininfor` VALUES (448, 'superAdmin', '172.16.101.229', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-16 09:42:05');
INSERT INTO `sys_logininfor` VALUES (449, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-16 11:10:06');
INSERT INTO `sys_logininfor` VALUES (450, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-16 11:55:50');
INSERT INTO `sys_logininfor` VALUES (451, 'admin', '172.16.101.230', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-16 13:36:54');
INSERT INTO `sys_logininfor` VALUES (452, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-16 18:28:07');
INSERT INTO `sys_logininfor` VALUES (453, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-03-16 18:32:24');
INSERT INTO `sys_logininfor` VALUES (454, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-16 18:32:27');
INSERT INTO `sys_logininfor` VALUES (455, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-19 09:56:33');
INSERT INTO `sys_logininfor` VALUES (456, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-22 10:42:04');
INSERT INTO `sys_logininfor` VALUES (457, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-22 10:42:08');
INSERT INTO `sys_logininfor` VALUES (458, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-22 10:42:20');
INSERT INTO `sys_logininfor` VALUES (459, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-22 10:42:29');
INSERT INTO `sys_logininfor` VALUES (460, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-23 11:03:28');
INSERT INTO `sys_logininfor` VALUES (461, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-23 11:04:04');
INSERT INTO `sys_logininfor` VALUES (462, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-23 13:58:43');
INSERT INTO `sys_logininfor` VALUES (463, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-23 13:58:49');
INSERT INTO `sys_logininfor` VALUES (464, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-23 13:58:56');
INSERT INTO `sys_logininfor` VALUES (465, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-23 14:57:14');
INSERT INTO `sys_logininfor` VALUES (466, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-23 14:57:24');
INSERT INTO `sys_logininfor` VALUES (467, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-23 14:57:29');
INSERT INTO `sys_logininfor` VALUES (468, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-23 14:57:37');
INSERT INTO `sys_logininfor` VALUES (469, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-24 10:01:32');
INSERT INTO `sys_logininfor` VALUES (470, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-24 10:01:47');
INSERT INTO `sys_logininfor` VALUES (471, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-24 10:01:54');
INSERT INTO `sys_logininfor` VALUES (472, 'admin', '172.16.101.212', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-24 17:13:13');
INSERT INTO `sys_logininfor` VALUES (473, 'admin', '172.16.101.212', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-03-24 17:13:33');
INSERT INTO `sys_logininfor` VALUES (474, 'superAdmin', '172.16.101.212', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-24 17:13:50');
INSERT INTO `sys_logininfor` VALUES (475, 'superAdmin', '172.16.101.212', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-24 17:35:48');
INSERT INTO `sys_logininfor` VALUES (476, 'admin', '172.16.101.212', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-25 09:46:53');
INSERT INTO `sys_logininfor` VALUES (477, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-25 09:51:25');
INSERT INTO `sys_logininfor` VALUES (478, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-25 09:52:29');
INSERT INTO `sys_logininfor` VALUES (479, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-25 09:52:52');
INSERT INTO `sys_logininfor` VALUES (480, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-25 09:53:01');
INSERT INTO `sys_logininfor` VALUES (481, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-25 09:55:49');
INSERT INTO `sys_logininfor` VALUES (482, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-25 09:55:52');
INSERT INTO `sys_logininfor` VALUES (483, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-25 09:55:56');
INSERT INTO `sys_logininfor` VALUES (484, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-25 09:56:05');
INSERT INTO `sys_logininfor` VALUES (485, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-25 10:58:00');
INSERT INTO `sys_logininfor` VALUES (486, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-25 16:28:57');
INSERT INTO `sys_logininfor` VALUES (487, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-25 16:29:05');
INSERT INTO `sys_logininfor` VALUES (488, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-25 16:29:38');
INSERT INTO `sys_logininfor` VALUES (489, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-25 16:29:47');
INSERT INTO `sys_logininfor` VALUES (490, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-25 17:41:29');
INSERT INTO `sys_logininfor` VALUES (491, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-25 17:41:33');
INSERT INTO `sys_logininfor` VALUES (492, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-25 17:41:42');
INSERT INTO `sys_logininfor` VALUES (493, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-26 09:48:36');
INSERT INTO `sys_logininfor` VALUES (494, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-26 09:49:15');
INSERT INTO `sys_logininfor` VALUES (495, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-26 09:49:22');
INSERT INTO `sys_logininfor` VALUES (496, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-26 09:49:28');
INSERT INTO `sys_logininfor` VALUES (497, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-26 10:52:00');
INSERT INTO `sys_logininfor` VALUES (498, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-26 10:52:10');
INSERT INTO `sys_logininfor` VALUES (499, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-26 10:52:17');
INSERT INTO `sys_logininfor` VALUES (500, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-26 10:52:25');
INSERT INTO `sys_logininfor` VALUES (501, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-26 14:02:04');
INSERT INTO `sys_logininfor` VALUES (502, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-03-26 14:02:13');
INSERT INTO `sys_logininfor` VALUES (503, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-26 14:02:17');
INSERT INTO `sys_logininfor` VALUES (504, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-26 14:02:42');
INSERT INTO `sys_logininfor` VALUES (505, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-26 14:02:52');
INSERT INTO `sys_logininfor` VALUES (506, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-29 09:41:25');
INSERT INTO `sys_logininfor` VALUES (507, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-29 09:43:19');
INSERT INTO `sys_logininfor` VALUES (508, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-29 09:44:58');
INSERT INTO `sys_logininfor` VALUES (509, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-29 09:45:04');
INSERT INTO `sys_logininfor` VALUES (510, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-29 09:58:21');
INSERT INTO `sys_logininfor` VALUES (511, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-29 09:58:53');
INSERT INTO `sys_logininfor` VALUES (512, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-29 10:04:49');
INSERT INTO `sys_logininfor` VALUES (513, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-29 13:35:54');
INSERT INTO `sys_logininfor` VALUES (514, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-29 13:36:06');
INSERT INTO `sys_logininfor` VALUES (515, 'admin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '1', '验证码错误', '2021-03-29 16:25:17');
INSERT INTO `sys_logininfor` VALUES (516, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-29 16:25:30');
INSERT INTO `sys_logininfor` VALUES (517, 'superAdmin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-03-29 17:09:00');
INSERT INTO `sys_logininfor` VALUES (518, 'admin', '172.16.101.200', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-03-29 17:11:27');
INSERT INTO `sys_logininfor` VALUES (519, 'admin', '172.16.101.200', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-29 17:11:31');
INSERT INTO `sys_logininfor` VALUES (520, 'admin', '172.16.101.200', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-03-29 17:13:34');
INSERT INTO `sys_logininfor` VALUES (521, 'superAdmin', '172.16.101.200', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-03-29 17:14:03');
INSERT INTO `sys_logininfor` VALUES (522, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-30 09:41:24');
INSERT INTO `sys_logininfor` VALUES (523, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-31 09:34:29');
INSERT INTO `sys_logininfor` VALUES (524, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-31 15:31:22');
INSERT INTO `sys_logininfor` VALUES (525, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-03-31 16:33:00');
INSERT INTO `sys_logininfor` VALUES (526, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-03-31 16:33:17');
INSERT INTO `sys_logininfor` VALUES (527, 'superAdmin', '172.16.101.224', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-03-31 16:34:25');
INSERT INTO `sys_logininfor` VALUES (528, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-01 09:36:23');
INSERT INTO `sys_logininfor` VALUES (529, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-01 18:11:44');
INSERT INTO `sys_logininfor` VALUES (530, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-02 09:40:54');
INSERT INTO `sys_logininfor` VALUES (531, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-02 09:42:12');
INSERT INTO `sys_logininfor` VALUES (532, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-02 09:42:26');
INSERT INTO `sys_logininfor` VALUES (533, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '1', '验证码错误', '2021-04-02 17:39:20');
INSERT INTO `sys_logininfor` VALUES (534, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-02 17:39:30');
INSERT INTO `sys_logininfor` VALUES (535, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-06 17:25:32');
INSERT INTO `sys_logininfor` VALUES (536, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-07 10:00:11');
INSERT INTO `sys_logininfor` VALUES (537, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-08 09:55:46');
INSERT INTO `sys_logininfor` VALUES (538, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-08 10:57:48');
INSERT INTO `sys_logininfor` VALUES (539, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-08 10:58:01');
INSERT INTO `sys_logininfor` VALUES (540, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-08 10:58:05');
INSERT INTO `sys_logininfor` VALUES (541, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-08 10:58:27');
INSERT INTO `sys_logininfor` VALUES (542, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-08 18:48:25');
INSERT INTO `sys_logininfor` VALUES (543, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-08 18:50:32');
INSERT INTO `sys_logininfor` VALUES (544, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-09 18:27:45');
INSERT INTO `sys_logininfor` VALUES (545, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 09:39:56');
INSERT INTO `sys_logininfor` VALUES (546, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-12 13:31:02');
INSERT INTO `sys_logininfor` VALUES (547, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-12 14:34:15');
INSERT INTO `sys_logininfor` VALUES (548, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-12 15:55:25');
INSERT INTO `sys_logininfor` VALUES (549, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-12 16:22:39');
INSERT INTO `sys_logininfor` VALUES (550, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 16:22:39');
INSERT INTO `sys_logininfor` VALUES (551, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-12 16:22:39');
INSERT INTO `sys_logininfor` VALUES (552, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 16:22:39');
INSERT INTO `sys_logininfor` VALUES (553, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-12 16:22:39');
INSERT INTO `sys_logininfor` VALUES (554, 'admin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 16:22:39');
INSERT INTO `sys_logininfor` VALUES (555, 'admin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 16:22:39');
INSERT INTO `sys_logininfor` VALUES (556, 'admin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 16:22:40');
INSERT INTO `sys_logininfor` VALUES (557, 'admin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-04-12 16:22:45');
INSERT INTO `sys_logininfor` VALUES (558, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 16:22:52');
INSERT INTO `sys_logininfor` VALUES (559, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-12 18:02:55');
INSERT INTO `sys_logininfor` VALUES (560, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-12 18:03:48');
INSERT INTO `sys_logininfor` VALUES (561, 'superAdmin', '172.16.101.219', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-13 09:39:05');
INSERT INTO `sys_logininfor` VALUES (562, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-13 10:33:56');
INSERT INTO `sys_logininfor` VALUES (563, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-13 17:01:35');
INSERT INTO `sys_logininfor` VALUES (564, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-13 17:01:49');
INSERT INTO `sys_logininfor` VALUES (565, 'superAdmin', '172.16.101.224', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-04-13 18:14:28');
INSERT INTO `sys_logininfor` VALUES (566, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-14 09:40:11');
INSERT INTO `sys_logininfor` VALUES (567, 'superAdmin', '172.16.101.224', '内网IP', 'Firefox 8', 'Mac OS X', '1', '验证码错误', '2021-04-14 10:08:37');
INSERT INTO `sys_logininfor` VALUES (568, 'superAdmin', '172.16.101.224', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-04-14 10:08:42');
INSERT INTO `sys_logininfor` VALUES (569, 'superAdmin', '172.16.101.224', '内网IP', 'Firefox 8', 'Mac OS X', '0', '登录成功', '2021-04-14 11:12:12');
INSERT INTO `sys_logininfor` VALUES (570, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-04-14 14:54:16');
INSERT INTO `sys_logininfor` VALUES (571, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-14 14:54:22');
INSERT INTO `sys_logininfor` VALUES (572, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-14 14:54:32');
INSERT INTO `sys_logininfor` VALUES (573, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-14 14:54:38');
INSERT INTO `sys_logininfor` VALUES (574, 'admin', '172.16.101.226', '内网IP', 'Chrome Mobile', 'Android 1.x', '0', '登录成功', '2021-04-14 14:55:58');
INSERT INTO `sys_logininfor` VALUES (575, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-14 16:10:17');
INSERT INTO `sys_logininfor` VALUES (576, 'superAdmin', '172.16.100.170', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-14 18:08:51');
INSERT INTO `sys_logininfor` VALUES (577, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-15 09:54:48');
INSERT INTO `sys_logininfor` VALUES (578, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-15 09:59:10');
INSERT INTO `sys_logininfor` VALUES (579, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-16 09:55:17');
INSERT INTO `sys_logininfor` VALUES (580, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-16 09:55:47');
INSERT INTO `sys_logininfor` VALUES (581, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-16 09:55:59');
INSERT INTO `sys_logininfor` VALUES (582, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-16 10:59:41');
INSERT INTO `sys_logininfor` VALUES (583, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-16 10:59:56');
INSERT INTO `sys_logininfor` VALUES (584, 'superAdmin', '172.16.100.170', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-16 18:30:53');
INSERT INTO `sys_logininfor` VALUES (585, 'superAdmin', '172.16.101.131', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-16 18:32:37');
INSERT INTO `sys_logininfor` VALUES (586, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-16 18:33:30');
INSERT INTO `sys_logininfor` VALUES (587, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-18 10:24:43');
INSERT INTO `sys_logininfor` VALUES (588, 'admin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-18 10:24:51');
INSERT INTO `sys_logininfor` VALUES (589, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-18 10:25:01');
INSERT INTO `sys_logininfor` VALUES (590, 'superAdmin', '127.0.0.1', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-18 18:14:29');
INSERT INTO `sys_logininfor` VALUES (591, 'superAdmin', '172.16.101.224', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-19 09:34:37');
INSERT INTO `sys_logininfor` VALUES (592, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-19 17:30:14');
INSERT INTO `sys_logininfor` VALUES (593, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-20 02:45:21');
INSERT INTO `sys_logininfor` VALUES (594, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 02:51:49');
INSERT INTO `sys_logininfor` VALUES (595, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-04-20 03:05:45');
INSERT INTO `sys_logininfor` VALUES (596, 'superadmin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 03:05:56');
INSERT INTO `sys_logininfor` VALUES (597, 'superAdmin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-04-20 03:08:00');
INSERT INTO `sys_logininfor` VALUES (598, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 03:08:05');
INSERT INTO `sys_logininfor` VALUES (599, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-04-20 03:09:10');
INSERT INTO `sys_logininfor` VALUES (600, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 03:09:14');
INSERT INTO `sys_logininfor` VALUES (601, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-04-20 03:09:21');
INSERT INTO `sys_logininfor` VALUES (602, 'superadmin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 03:09:29');
INSERT INTO `sys_logininfor` VALUES (603, 'superAdmin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-04-20 03:26:31');
INSERT INTO `sys_logininfor` VALUES (604, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 03:26:36');
INSERT INTO `sys_logininfor` VALUES (605, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-04-20 03:41:48');
INSERT INTO `sys_logininfor` VALUES (606, 'superadmin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 03:41:56');
INSERT INTO `sys_logininfor` VALUES (607, 'superAdmin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2021-04-20 04:05:39');
INSERT INTO `sys_logininfor` VALUES (608, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 04:05:45');
INSERT INTO `sys_logininfor` VALUES (609, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 04:07:34');
INSERT INTO `sys_logininfor` VALUES (610, 'admin', '172.16.100.98', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-20 04:08:02');
INSERT INTO `sys_logininfor` VALUES (611, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 18:10:40');
INSERT INTO `sys_logininfor` VALUES (612, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 18:10:50');
INSERT INTO `sys_logininfor` VALUES (613, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '1', '验证码错误', '2021-04-20 18:10:57');
INSERT INTO `sys_logininfor` VALUES (614, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 18:11:01');
INSERT INTO `sys_logininfor` VALUES (615, 'admin', '172.16.100.98', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2021-04-20 18:29:12');
INSERT INTO `sys_logininfor` VALUES (616, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 10:36:19');
INSERT INTO `sys_logininfor` VALUES (617, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:00:07');
INSERT INTO `sys_logininfor` VALUES (618, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:00:18');
INSERT INTO `sys_logininfor` VALUES (619, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:00:39');
INSERT INTO `sys_logininfor` VALUES (620, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:00:44');
INSERT INTO `sys_logininfor` VALUES (621, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:02:33');
INSERT INTO `sys_logininfor` VALUES (622, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:02:39');
INSERT INTO `sys_logininfor` VALUES (623, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:04:04');
INSERT INTO `sys_logininfor` VALUES (624, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:04:10');
INSERT INTO `sys_logininfor` VALUES (625, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:05:07');
INSERT INTO `sys_logininfor` VALUES (626, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:05:12');
INSERT INTO `sys_logininfor` VALUES (627, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:07:01');
INSERT INTO `sys_logininfor` VALUES (628, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:07:06');
INSERT INTO `sys_logininfor` VALUES (629, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:07:37');
INSERT INTO `sys_logininfor` VALUES (630, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:07:45');
INSERT INTO `sys_logininfor` VALUES (631, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:07:53');
INSERT INTO `sys_logininfor` VALUES (632, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:07:58');
INSERT INTO `sys_logininfor` VALUES (633, 'admin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '退出成功', '2021-04-20 11:52:57');
INSERT INTO `sys_logininfor` VALUES (634, 'superAdmin', '172.16.101.249', '内网IP', 'Chrome 8', 'Mac OS X', '0', '登录成功', '2021-04-20 11:53:04');
INSERT INTO `sys_logininfor` VALUES (635, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-20 16:08:55');
INSERT INTO `sys_logininfor` VALUES (636, 'admin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '退出成功', '2021-04-20 16:08:59');
INSERT INTO `sys_logininfor` VALUES (637, 'superAdmin', '172.16.100.170', '内网IP', 'Firefox 8', 'Windows 10', '0', '登录成功', '2021-04-20 16:09:08');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `is_frame` int(1) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2021 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 3, 'system', NULL, 1, 0, 'M', '0', '0', '', 'system', 'admin', '2021-01-18 10:39:19', 'superAdmin', '2021-01-25 18:10:51', '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 3, 'monitor', NULL, 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2021-01-18 10:39:19', 'admin', '2021-01-18 13:35:18', '系统监控目录');
INSERT INTO `sys_menu` VALUES (3, '系统工具', 0, 4, 'tool', NULL, 1, 0, 'M', '0', '0', '', 'tool', 'admin', '2021-01-18 10:39:19', 'admin', '2021-01-18 13:35:22', '系统工具目录');
INSERT INTO `sys_menu` VALUES (4, '天堂云官网', 0, 5, 'http://www.ttclouds.cn', NULL, 0, 0, 'M', '0', '0', '', 'guide', 'admin', '2021-01-18 10:39:19', 'admin', '2021-01-18 13:35:27', '若依官网地址');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2021-01-18 10:39:19', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2021-01-18 10:39:19', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2021-01-18 10:39:19', '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2021-01-18 10:39:19', '', NULL, '部门管理菜单');
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', '2021-01-18 10:39:19', '', NULL, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2021-01-18 10:39:19', '', NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2021-01-18 10:39:19', '', NULL, '参数设置菜单');
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2021-01-18 10:39:19', '', NULL, '通知公告菜单');
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, 'log', '', 1, 0, 'M', '0', '0', '', 'log', 'admin', '2021-01-18 10:39:19', '', NULL, '日志管理菜单');
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2021-01-18 10:39:19', '', NULL, '在线用户菜单');
INSERT INTO `sys_menu` VALUES (111, '数据监控', 2, 3, 'druid', 'monitor/druid/index', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2021-01-18 10:39:19', '', NULL, '数据监控菜单');
INSERT INTO `sys_menu` VALUES (112, '服务监控', 2, 4, 'server', 'monitor/server/index', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2021-01-18 10:39:19', '', NULL, '服务监控菜单');
INSERT INTO `sys_menu` VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2021-01-18 10:39:19', '', NULL, '缓存监控菜单');
INSERT INTO `sys_menu` VALUES (114, '表单构建', 3, 1, 'build', 'tool/build/index', 1, 0, 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2021-01-18 10:39:19', '', NULL, '表单构建菜单');
INSERT INTO `sys_menu` VALUES (115, '代码生成', 3, 2, 'gen', 'tool/gen/index', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2021-01-18 10:39:19', '', NULL, '代码生成菜单');
INSERT INTO `sys_menu` VALUES (116, '系统接口', 3, 3, 'swagger', 'tool/swagger/index', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2021-01-18 10:39:19', '', NULL, '系统接口菜单');
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2021-01-18 10:39:19', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2021-01-18 10:39:19', '', NULL, '登录日志菜单');
INSERT INTO `sys_menu` VALUES (1001, '用户查询', 100, 1, '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户新增', 100, 2, '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户修改', 100, 3, '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户删除', 100, 4, '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导出', 100, 5, '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '用户导入', 100, 6, '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '重置密码', 100, 7, '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色查询', 101, 1, '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色新增', 101, 2, '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色修改', 101, 3, '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色删除', 101, 4, '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '角色导出', 101, 5, '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单查询', 102, 1, '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单新增', 102, 2, '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单修改', 102, 3, '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1016, '菜单删除', 102, 4, '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1017, '部门查询', 103, 1, '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1018, '部门新增', 103, 2, '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1019, '部门修改', 103, 3, '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1020, '部门删除', 103, 4, '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1021, '岗位查询', 104, 1, '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1022, '岗位新增', 104, 2, '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1023, '岗位修改', 104, 3, '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1024, '岗位删除', 104, 4, '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1025, '岗位导出', 104, 5, '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1026, '字典查询', 105, 1, '#', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1027, '字典新增', 105, 2, '#', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1028, '字典修改', 105, 3, '#', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1029, '字典删除', 105, 4, '#', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1030, '字典导出', 105, 5, '#', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1031, '参数查询', 106, 1, '#', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1032, '参数新增', 106, 2, '#', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1033, '参数修改', 106, 3, '#', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1034, '参数删除', 106, 4, '#', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1035, '参数导出', 106, 5, '#', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1036, '公告查询', 107, 1, '#', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1037, '公告新增', 107, 2, '#', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1038, '公告修改', 107, 3, '#', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1039, '公告删除', 107, 4, '#', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1040, '操作查询', 500, 1, '#', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1041, '操作删除', 500, 2, '#', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1042, '日志导出', 500, 4, '#', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1043, '登录查询', 501, 1, '#', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1044, '登录删除', 501, 2, '#', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1045, '日志导出', 501, 3, '#', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1046, '在线查询', 109, 1, '#', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1047, '批量强退', 109, 2, '#', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1048, '单条强退', 109, 3, '#', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1055, '生成查询', 115, 1, '#', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1056, '生成修改', 115, 2, '#', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1057, '生成删除', 115, 3, '#', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1058, '导入代码', 115, 2, '#', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1059, '预览代码', 115, 4, '#', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1060, '生成代码', 115, 5, '#', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2021-01-18 10:39:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2000, '会议管理', 0, 1, 'conference', NULL, 1, 0, 'M', '0', '0', '', 'table', 'admin', '2021-01-18 13:34:44', 'superAdmin', '2021-02-28 11:59:01', '');
INSERT INTO `sys_menu` VALUES (2002, '会议模板', 2000, 2, 'template', 'conference/template/index', 1, 1, 'C', '0', '0', '', 'component', 'admin', '2021-01-18 14:52:52', 'superAdmin', '2021-02-04 15:51:13', '');
INSERT INTO `sys_menu` VALUES (2004, '新增模板', 2000, 6, 'add-template', 'conference/addTemplate/index2', 1, 0, 'C', '1', '0', '', 'input', 'admin', '2021-01-18 15:27:32', 'superAdmin', '2021-03-22 17:33:47', '');
INSERT INTO `sys_menu` VALUES (2005, '模板修改', 2000, 7, 'modify-template', 'conference/modifyTemplate/index2', 1, 0, 'C', '1', '0', '', 'edit', 'admin', '2021-01-18 15:29:46', 'superAdmin', '2021-03-22 17:33:52', '');
INSERT INTO `sys_menu` VALUES (2007, '会议控制', 2000, 5, 'view-control', 'conference/viewControl/index', 1, 0, 'C', '1', '0', '', 'button', 'admin', '2021-01-18 15:51:00', 'superAdmin', '2021-02-05 10:48:48', '');
INSERT INTO `sys_menu` VALUES (2008, '终端管理', 2009, 4, 'terminal', 'config/terminal/index', 1, 0, 'C', '0', '0', '', 'international', 'admin', '2021-01-19 10:47:57', 'superAdmin', '2021-01-26 11:10:07', '');
INSERT INTO `sys_menu` VALUES (2009, '配置管理', 0, 2, 'configManagement', NULL, 1, 0, 'M', '0', '0', '', 'server', 'superAdmin', '2021-01-25 18:08:23', 'superAdmin', '2021-02-04 13:19:01', '');
INSERT INTO `sys_menu` VALUES (2010, '入会方案配置', 2009, 1, 'project', 'config/project/index', 1, 0, 'C', '0', '0', NULL, 'radio', 'superAdmin', '2021-01-26 11:21:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2011, '会议号配置', 2009, 2, 'conferenceNumber', 'config/conferenceNumber/index', 1, 0, 'C', '0', '0', '', 'number', 'superAdmin', '2021-01-26 19:18:08', 'superAdmin', '2021-01-26 19:18:52', '');
INSERT INTO `sys_menu` VALUES (2012, 'FME配置', 2009, 4, 'fme', 'config/fme/index', 1, 0, 'C', '0', '0', '', 'server', 'superAdmin', '2021-01-28 17:10:51', 'superAdmin', '2021-03-31 16:50:56', '');
INSERT INTO `sys_menu` VALUES (2013, '租户FME绑定', 2009, 5, 'fmeallot', 'config/fmeallot/index', 1, 0, 'C', '0', '0', '', 'cascader', 'superAdmin', '2021-01-28 17:13:59', 'superAdmin', '2021-03-31 16:52:55', '');
INSERT INTO `sys_menu` VALUES (2014, '我的会议', 2000, 1, 'conferenceList', 'conference/conferenceList/index', 1, 0, 'C', '0', '0', '', 'list', 'superAdmin', '2021-02-04 15:51:02', 'superAdmin', '2021-02-28 12:04:21', '');
INSERT INTO `sys_menu` VALUES (2015, '全局参数', 1, 10, 'parameter', 'system/parameter/index', 1, 0, 'C', '0', '0', '', 'skill', 'superAdmin', '2021-02-28 10:50:30', 'superAdmin', '2021-02-28 10:53:03', '');
INSERT INTO `sys_menu` VALUES (2016, '录制管理', 0, 4, 'record', 'record/index', 1, 0, 'C', '0', '0', '', 'luzhi', 'superAdmin', '2021-02-28 12:40:42', 'superAdmin', '2021-02-28 12:43:23', '');
INSERT INTO `sys_menu` VALUES (2019, '会议控制-表格', 2000, 3, 'table-control', 'conference/tableControl/index', 1, 0, 'C', '1', '0', '', 'button', 'superAdmin', '2021-03-08 10:05:51', 'superAdmin', '2021-03-09 15:34:07', '');
INSERT INTO `sys_menu` VALUES (2020, '会议室调试', 3, 4, 'conference-debugger', 'tool/debug/index', 1, 0, 'C', '1', '0', '', 'bug', 'superAdmin', '2021-03-25 17:28:40', 'superAdmin', '2021-03-25 17:34:55', '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob NULL COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (1, '温馨提醒：2021-05-01 天堂云会控系统新版本发布啦', '2', 0xE696B0E78988E69CACE58685E5AEB9, '0', 'admin', '2021-01-18 10:39:23', 'superAdmin', '2021-04-20 04:05:05', '管理员');
INSERT INTO `sys_notice` VALUES (2, '维护通知：2018-07-01 天堂云会控系统凌晨维护', '1', 0xE7BBB4E68AA4E58685E5AEB9, '0', 'admin', '2021-01-18 10:39:23', '', NULL, '管理员');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(8000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int(1) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`oper_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5391 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_post` VALUES (2, 'se', '项目经理', 2, '0', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_post` VALUES (3, 'hr', '人力资源', 3, '0', 'admin', '2021-01-18 10:39:19', '', NULL, '');
INSERT INTO `sys_post` VALUES (4, 'user', '普通员工', 4, '0', 'admin', '2021-01-18 10:39:19', '', NULL, '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `del_flag`(`del_flag`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2021-01-18 10:39:19', '', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '系统管理员', 'system', 2, '4', 1, 1, '0', '0', 'admin', '2021-01-18 10:39:19', 'superAdmin', '2021-04-20 11:07:33', '普通角色');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  CONSTRAINT `sys_role_dept_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `sys_role_dept_ibfk_2` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 100);
INSERT INTO `sys_role_menu` VALUES (2, 101);
INSERT INTO `sys_role_menu` VALUES (2, 103);
INSERT INTO `sys_role_menu` VALUES (2, 1001);
INSERT INTO `sys_role_menu` VALUES (2, 1002);
INSERT INTO `sys_role_menu` VALUES (2, 1003);
INSERT INTO `sys_role_menu` VALUES (2, 1004);
INSERT INTO `sys_role_menu` VALUES (2, 1005);
INSERT INTO `sys_role_menu` VALUES (2, 1006);
INSERT INTO `sys_role_menu` VALUES (2, 1007);
INSERT INTO `sys_role_menu` VALUES (2, 1008);
INSERT INTO `sys_role_menu` VALUES (2, 1009);
INSERT INTO `sys_role_menu` VALUES (2, 1010);
INSERT INTO `sys_role_menu` VALUES (2, 1011);
INSERT INTO `sys_role_menu` VALUES (2, 1012);
INSERT INTO `sys_role_menu` VALUES (2, 1017);
INSERT INTO `sys_role_menu` VALUES (2, 1018);
INSERT INTO `sys_role_menu` VALUES (2, 1019);
INSERT INTO `sys_role_menu` VALUES (2, 1020);
INSERT INTO `sys_role_menu` VALUES (2, 2000);
INSERT INTO `sys_role_menu` VALUES (2, 2002);
INSERT INTO `sys_role_menu` VALUES (2, 2004);
INSERT INTO `sys_role_menu` VALUES (2, 2005);
INSERT INTO `sys_role_menu` VALUES (2, 2007);
INSERT INTO `sys_role_menu` VALUES (2, 2008);
INSERT INTO `sys_role_menu` VALUES (2, 2009);
INSERT INTO `sys_role_menu` VALUES (2, 2010);
INSERT INTO `sys_role_menu` VALUES (2, 2011);
INSERT INTO `sys_role_menu` VALUES (2, 2012);
INSERT INTO `sys_role_menu` VALUES (2, 2013);
INSERT INTO `sys_role_menu` VALUES (2, 2014);
INSERT INTO `sys_role_menu` VALUES (2, 2019);
INSERT INTO `sys_role_menu` VALUES (3, 1);
INSERT INTO `sys_role_menu` VALUES (3, 100);
INSERT INTO `sys_role_menu` VALUES (3, 101);
INSERT INTO `sys_role_menu` VALUES (3, 103);
INSERT INTO `sys_role_menu` VALUES (3, 1001);
INSERT INTO `sys_role_menu` VALUES (3, 1002);
INSERT INTO `sys_role_menu` VALUES (3, 1003);
INSERT INTO `sys_role_menu` VALUES (3, 1004);
INSERT INTO `sys_role_menu` VALUES (3, 1005);
INSERT INTO `sys_role_menu` VALUES (3, 1006);
INSERT INTO `sys_role_menu` VALUES (3, 1007);
INSERT INTO `sys_role_menu` VALUES (3, 1008);
INSERT INTO `sys_role_menu` VALUES (3, 1009);
INSERT INTO `sys_role_menu` VALUES (3, 1010);
INSERT INTO `sys_role_menu` VALUES (3, 1011);
INSERT INTO `sys_role_menu` VALUES (3, 1012);
INSERT INTO `sys_role_menu` VALUES (3, 1017);
INSERT INTO `sys_role_menu` VALUES (3, 1018);
INSERT INTO `sys_role_menu` VALUES (3, 1019);
INSERT INTO `sys_role_menu` VALUES (3, 1020);
INSERT INTO `sys_role_menu` VALUES (3, 2000);
INSERT INTO `sys_role_menu` VALUES (3, 2002);
INSERT INTO `sys_role_menu` VALUES (3, 2004);
INSERT INTO `sys_role_menu` VALUES (3, 2005);
INSERT INTO `sys_role_menu` VALUES (3, 2007);
INSERT INTO `sys_role_menu` VALUES (3, 2008);
INSERT INTO `sys_role_menu` VALUES (3, 2009);
INSERT INTO `sys_role_menu` VALUES (3, 2010);
INSERT INTO `sys_role_menu` VALUES (3, 2011);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `user_name`(`user_name`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 105 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, NULL, 'superAdmin', '超级管理员', '00', 'ry@163.com', '15888888888', '1', '/profile/avatar/2021/01/19/35ea7b84-19cf-4e18-8d98-3e396a080a4f.jpeg', '$2a$10$d1wmaSUmRR8bs.Djrx5Bouyb4bE9i/0tn5nfAdfjiMd41IZcqFSp2', '0', '0', '127.0.0.1', '2021-01-18 10:39:18', 'admin', '2021-01-18 10:39:18', '', NULL, '管理员');
INSERT INTO `sys_user` VALUES (100, 100, 'admin', '视频会议管理员', '00', '', '', '0', '', '$2a$10$d1wmaSUmRR8bs.Djrx5Bouyb4bE9i/0tn5nfAdfjiMd41IZcqFSp2', '0', '0', '', NULL, 'superAdmin', '2021-01-19 11:38:00', 'superAdmin', '2021-04-20 04:02:54', NULL);
INSERT INTO `sys_user` VALUES (101, 206, 'altAdmin', '阿勒泰管理员', '00', '', '13666667777', '0', '', '$2a$10$5qIPs0Lck8Hsk514f2IMkOpA/dOjOahjN5lO8NY8rHN1mfPLT.s2u', '0', '2', '', NULL, 'admin', '2021-01-25 18:18:41', 'admin', '2021-01-29 17:56:46', NULL);
INSERT INTO `sys_user` VALUES (103, 103, 'ylzAdmin', 'ylzAdmin', '00', '', '', '0', '', '$2a$10$ZaHS6slx3kX88Xr4TnWwFe4SFEEd6JRZq7L/bTdg/zDjNS9mkinOO', '0', '2', '', NULL, 'superAdmin', '2021-01-30 13:38:37', '', NULL, NULL);
INSERT INTO `sys_user` VALUES (104, 104, 'admin2', 'fsdfs', '00', '', '', '0', '', '$2a$10$D3/A1bP9QSchsSK8Fo1EmO5ZamATaVroAuQbemeJ6qirS.GBIw2q2', '0', '2', '', NULL, 'admin', '2021-04-20 11:03:05', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (1, 1);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `role_id`(`role_id`) USING BTREE,
  CONSTRAINT `sys_user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `sys_user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (100, 2);

SET FOREIGN_KEY_CHECKS = 1;
