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

 Date: 15/04/2021 18:26:47
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
) ENGINE = InnoDB AUTO_INCREMENT = 134 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '入会方案配置，控制参会者进入会议的方案' ROW_FORMAT = Dynamic;

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
INSERT INTO `busi_call_leg_profile` VALUES (39, '2021-03-23 17:13:49', NULL, 'd805f65b-ad36-4dc8-b9bb-268a61a279ac', NULL, 100, 31);
INSERT INTO `busi_call_leg_profile` VALUES (41, '2021-03-25 11:28:29', NULL, '4509a451-877d-4f12-bba4-48df0da034a1', NULL, 100, 31);
INSERT INTO `busi_call_leg_profile` VALUES (42, '2021-03-25 11:28:34', NULL, '78ec27d8-9d8e-4771-9cf6-bc47fecaa43b', NULL, 100, 31);
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
INSERT INTO `busi_call_leg_profile` VALUES (130, '2021-04-12 16:33:31', NULL, '58de46c8-6344-47c8-a313-fe6db8458cbc', NULL, 100, 31);
INSERT INTO `busi_call_leg_profile` VALUES (131, '2021-04-12 18:24:37', NULL, '9e29c207-5978-4700-9502-08ef9c85796d', NULL, 100, 31);
INSERT INTO `busi_call_leg_profile` VALUES (132, '2021-04-13 15:21:12', NULL, 'd0ab23ff-f49d-4865-b9bb-bdc263f0f6c1', NULL, 100, 31);
INSERT INTO `busi_call_leg_profile` VALUES (133, '2021-04-13 18:09:32', NULL, '04b88c17-c802-4e53-8768-87606443b0f4', NULL, 100, 31);

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
INSERT INTO `busi_conference_number` VALUES (11111, '2021-03-16 12:11:30', '2021-03-25 11:28:34', 100, 'admin', 100, 2, 1, NULL);
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
INSERT INTO `busi_conference_number` VALUES (88002, '2021-04-15 17:27:15', '2021-03-23 17:18:44', 1, 'superAdmin', 100, 2, 60, NULL);
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
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'FME终端信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_fme
-- ----------------------------
INSERT INTO `busi_fme` VALUES (31, '2021-02-19 18:14:38', '2021-04-15 18:24:52', 'admin', 'P@rad1se', '新疆维吾尔自治区', '192.166.0.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (32, '2021-02-19 18:27:33', '2021-04-15 18:24:54', 'admin', 'P@rad1se', '克州', '192.166.2.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (33, '2021-02-19 18:37:00', '2021-04-15 18:24:54', 'admin', 'P@rad1se', '喀什地区', '192.166.3.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (34, '2021-02-19 18:38:32', '2021-04-15 18:24:54', 'admin', 'P@rad1se', '和田地区', '192.166.4.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (35, '2021-02-19 18:42:20', '2021-04-15 18:24:54', 'admin', 'P@rad1se', '阿克苏地区', '192.166.5.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (36, '2021-02-19 19:40:17', '2021-04-15 18:24:54', 'admin', 'P@rad1se', '巴州地区', '192.166.6.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (37, '2021-02-19 19:41:40', '2021-04-15 18:24:54', 'admin', 'P@rad1se', '吐鲁番市', '192.166.7.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (38, '2021-02-19 19:42:41', '2021-04-15 18:24:54', 'admin', 'P@rad1se', '哈密市', '192.166.8.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (39, '2021-02-19 19:43:42', '2021-04-15 18:24:55', 'admin', 'P@rad1se', '乌鲁木齐市', '192.166.9.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (40, '2021-02-19 19:44:33', '2021-04-15 18:24:55', 'admin', 'P@rad1se', '昌吉州', '192.166.10.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (41, '2021-02-19 19:46:28', '2021-04-15 18:24:55', 'admin', 'P@rad1se', '塔城地区', '192.166.11.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (42, '2021-02-19 19:47:57', '2021-04-15 18:24:56', 'admin', 'P@rad1se', '阿勒泰地区', '192.166.12.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (44, '2021-02-20 14:33:24', '2021-04-15 18:24:56', 'admin', 'P@rad1se', '博州', '192.166.13.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (45, '2021-02-20 14:33:46', '2021-04-15 18:24:56', 'admin', 'P@rad1se', '克拉玛依', '192.166.14.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (48, '2021-03-03 14:57:27', '2021-04-15 18:24:56', 'admin', 'P@rad1se', '伊犁州', '192.166.1.3', '192.166.0.41', 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (50, '2021-03-26 16:35:25', '2021-04-15 18:25:00', 'admin', 'P@rad1se', '集群A节点1', '172.16.100.190', NULL, 9443, 1, NULL);
INSERT INTO `busi_fme` VALUES (51, '2021-03-26 16:36:17', '2021-04-15 18:25:00', 'admin', 'P@rad1se', '集群A节点2', '172.16.100.191', NULL, 9443, 1, NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'FME组分配租户的中间表（一个FME组可以分配给多个租户，一对多）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_fme_dept
-- ----------------------------
INSERT INTO `busi_fme_dept` VALUES (35, '2021-03-24 15:45:43', '2021-03-26 16:41:32', 212, 100, 59);
INSERT INTO `busi_fme_dept` VALUES (37, '2021-04-12 14:57:02', NULL, 100, 1, 31);

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
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_conference
-- ----------------------------
INSERT INTO `busi_template_conference` VALUES (39, '2021-03-09 17:17:21', '2021-04-15 17:50:14', 1, 'superAdmin', '党政专用高清调度系统', 100, 'd805f65b-ad36-4dc8-b9bb-268a61a279ac', 2, 2, 2, 2, 88000, NULL, 'speakerOnly', 2, 1, 1, 10);
INSERT INTO `busi_template_conference` VALUES (42, '2021-03-24 16:00:04', '2021-03-30 17:26:27', 1, 'superAdmin', '测试跨部门单体会议', 212, 'cca6ac97-3658-4bea-a87d-f99de7772f81', 3, 2, 2, 2, 1000000, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_template_conference` VALUES (48, '2021-04-08 18:50:48', NULL, 1, 'superAdmin', 'dsadasd', 212, '16444640-db17-4ceb-a9f9-ba3684ccd26b', 2, 2, 2, 2, 1234567, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `busi_template_conference` VALUES (62, '2021-04-14 17:18:59', '2021-04-15 18:19:49', 1, 'superAdmin', 'fdfsf', 100, '4509a451-877d-4f12-bba4-48df0da034a1', 2, 2, 2, 2, 88002, NULL, 'allEqualQuarters', 1, 1, 1, 2);

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
) ENGINE = InnoDB AUTO_INCREMENT = 121 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '默认视图下指定的多分频单元格' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_conference_default_view_cell_screen
-- ----------------------------
INSERT INTO `busi_template_conference_default_view_cell_screen` VALUES (112, NULL, NULL, 39, 1, 101, 2);
INSERT INTO `busi_template_conference_default_view_cell_screen` VALUES (117, NULL, NULL, 62, 1, 105, 1);
INSERT INTO `busi_template_conference_default_view_cell_screen` VALUES (118, NULL, NULL, 62, 2, 105, 2);
INSERT INTO `busi_template_conference_default_view_cell_screen` VALUES (119, NULL, NULL, 62, 3, 101, 1);
INSERT INTO `busi_template_conference_default_view_cell_screen` VALUES (120, NULL, NULL, 62, 4, 101, 2);

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
) ENGINE = InnoDB AUTO_INCREMENT = 510 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '默认视图的部门显示顺序' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_conference_default_view_dept
-- ----------------------------
INSERT INTO `busi_template_conference_default_view_dept` VALUES (483, NULL, NULL, 39, 100, 15);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (484, NULL, NULL, 39, 103, 14);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (485, NULL, NULL, 39, 205, 13);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (486, NULL, NULL, 39, 206, 12);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (487, NULL, NULL, 39, 208, 11);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (488, NULL, NULL, 39, 207, 10);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (489, NULL, NULL, 39, 204, 9);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (490, NULL, NULL, 39, 203, 8);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (491, NULL, NULL, 39, 202, 7);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (492, NULL, NULL, 39, 201, 6);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (493, NULL, NULL, 39, 200, 5);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (494, NULL, NULL, 39, 107, 4);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (495, NULL, NULL, 39, 104, 3);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (496, NULL, NULL, 39, 105, 2);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (497, NULL, NULL, 39, 106, 1);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (504, NULL, NULL, 62, 100, 6);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (505, NULL, NULL, 62, 103, 5);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (506, NULL, NULL, 62, 104, 4);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (507, NULL, NULL, 62, 105, 3);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (508, NULL, NULL, 62, 106, 2);
INSERT INTO `busi_template_conference_default_view_dept` VALUES (509, NULL, NULL, 62, 107, 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 3757 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '默认视图的参会者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_conference_default_view_paticipant
-- ----------------------------
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3518, NULL, NULL, 39, 12642, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3519, NULL, NULL, 39, 12647, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3520, NULL, NULL, 39, 12660, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3521, NULL, NULL, 39, 12667, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3522, NULL, NULL, 39, 12680, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3523, NULL, NULL, 39, 12689, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3524, NULL, NULL, 39, 12699, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3525, NULL, NULL, 39, 12710, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3526, NULL, NULL, 39, 12715, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3527, NULL, NULL, 39, 12719, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3528, NULL, NULL, 39, 12729, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3529, NULL, NULL, 39, 12739, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3530, NULL, NULL, 39, 12747, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3531, NULL, NULL, 39, 12756, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3532, NULL, NULL, 39, 12762, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3533, NULL, NULL, 39, 12643, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3534, NULL, NULL, 39, 12648, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3535, NULL, NULL, 39, 12661, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3536, NULL, NULL, 39, 12668, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3537, NULL, NULL, 39, 12681, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3538, NULL, NULL, 39, 12690, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3539, NULL, NULL, 39, 12700, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3540, NULL, NULL, 39, 12711, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3541, NULL, NULL, 39, 12716, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3542, NULL, NULL, 39, 12720, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3543, NULL, NULL, 39, 12730, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3544, NULL, NULL, 39, 12740, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3545, NULL, NULL, 39, 12748, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3546, NULL, NULL, 39, 12757, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3547, NULL, NULL, 39, 12763, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3548, NULL, NULL, 39, 12644, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3549, NULL, NULL, 39, 12649, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3550, NULL, NULL, 39, 12662, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3551, NULL, NULL, 39, 12669, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3552, NULL, NULL, 39, 12682, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3553, NULL, NULL, 39, 12691, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3554, NULL, NULL, 39, 12701, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3555, NULL, NULL, 39, 12712, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3556, NULL, NULL, 39, 12717, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3557, NULL, NULL, 39, 12721, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3558, NULL, NULL, 39, 12731, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3559, NULL, NULL, 39, 12741, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3560, NULL, NULL, 39, 12749, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3561, NULL, NULL, 39, 12758, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3562, NULL, NULL, 39, 12764, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3563, NULL, NULL, 39, 12645, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3564, NULL, NULL, 39, 12650, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3565, NULL, NULL, 39, 12663, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3566, NULL, NULL, 39, 12670, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3567, NULL, NULL, 39, 12683, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3568, NULL, NULL, 39, 12692, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3569, NULL, NULL, 39, 12702, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3570, NULL, NULL, 39, 12713, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3571, NULL, NULL, 39, 12718, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3572, NULL, NULL, 39, 12722, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3573, NULL, NULL, 39, 12732, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3574, NULL, NULL, 39, 12742, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3575, NULL, NULL, 39, 12750, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3576, NULL, NULL, 39, 12759, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3577, NULL, NULL, 39, 12765, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3578, NULL, NULL, 39, 12646, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3579, NULL, NULL, 39, 12651, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3580, NULL, NULL, 39, 12664, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3581, NULL, NULL, 39, 12671, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3582, NULL, NULL, 39, 12684, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3583, NULL, NULL, 39, 12693, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3584, NULL, NULL, 39, 12703, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3585, NULL, NULL, 39, 12714, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3586, NULL, NULL, 39, 12723, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3587, NULL, NULL, 39, 12733, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3588, NULL, NULL, 39, 12743, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3589, NULL, NULL, 39, 12751, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3590, NULL, NULL, 39, 12760, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3591, NULL, NULL, 39, 12766, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3592, NULL, NULL, 39, 12652, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3593, NULL, NULL, 39, 12665, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3594, NULL, NULL, 39, 12672, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3595, NULL, NULL, 39, 12685, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3596, NULL, NULL, 39, 12694, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3597, NULL, NULL, 39, 12704, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3598, NULL, NULL, 39, 12724, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3599, NULL, NULL, 39, 12734, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3600, NULL, NULL, 39, 12744, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3601, NULL, NULL, 39, 12752, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3602, NULL, NULL, 39, 12761, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3603, NULL, NULL, 39, 12653, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3604, NULL, NULL, 39, 12666, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3605, NULL, NULL, 39, 12673, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3606, NULL, NULL, 39, 12686, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3607, NULL, NULL, 39, 12695, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3608, NULL, NULL, 39, 12705, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3609, NULL, NULL, 39, 12725, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3610, NULL, NULL, 39, 12735, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3611, NULL, NULL, 39, 12745, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3612, NULL, NULL, 39, 12753, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3613, NULL, NULL, 39, 12654, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3614, NULL, NULL, 39, 12674, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3615, NULL, NULL, 39, 12687, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3616, NULL, NULL, 39, 12696, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3617, NULL, NULL, 39, 12706, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3618, NULL, NULL, 39, 12726, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3619, NULL, NULL, 39, 12736, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3620, NULL, NULL, 39, 12746, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3621, NULL, NULL, 39, 12754, 8, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3622, NULL, NULL, 39, 12655, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3623, NULL, NULL, 39, 12675, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3624, NULL, NULL, 39, 12688, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3625, NULL, NULL, 39, 12697, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3626, NULL, NULL, 39, 12707, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3627, NULL, NULL, 39, 12727, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3628, NULL, NULL, 39, 12737, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3629, NULL, NULL, 39, 12755, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3630, NULL, NULL, 39, 12656, 10, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3631, NULL, NULL, 39, 12676, 10, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3632, NULL, NULL, 39, 12698, 10, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3633, NULL, NULL, 39, 12708, 10, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3634, NULL, NULL, 39, 12728, 10, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3635, NULL, NULL, 39, 12738, 10, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3636, NULL, NULL, 39, 12657, 11, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3637, NULL, NULL, 39, 12677, 11, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3638, NULL, NULL, 39, 12709, 11, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3639, NULL, NULL, 39, 12658, 12, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3640, NULL, NULL, 39, 12678, 12, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3641, NULL, NULL, 39, 12659, 13, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3642, NULL, NULL, 39, 12679, 13, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3700, NULL, NULL, 62, 12861, 3, 1);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3701, NULL, NULL, 62, 12860, 2, 1);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3702, NULL, NULL, 62, 12841, 1, 1);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3703, NULL, NULL, 62, 12859, 3, 2);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3704, NULL, NULL, 62, 12840, 2, 2);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3705, NULL, NULL, 62, 12836, 1, 2);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3706, NULL, NULL, 62, 12879, 3, 3);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3707, NULL, NULL, 62, 12835, 2, 3);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3708, NULL, NULL, 62, 12880, 1, 3);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3709, NULL, NULL, 62, 12856, 3, 4);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3710, NULL, NULL, 62, 12869, 2, 4);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3711, NULL, NULL, 62, 12878, 1, 4);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3712, NULL, NULL, 62, 12842, 0, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3713, NULL, NULL, 62, 12843, 0, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3714, NULL, NULL, 62, 12824, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3715, NULL, NULL, 62, 12829, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3716, NULL, NULL, 62, 12844, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3717, NULL, NULL, 62, 12849, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3718, NULL, NULL, 62, 12862, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3719, NULL, NULL, 62, 12871, 1, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3720, NULL, NULL, 62, 12825, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3721, NULL, NULL, 62, 12830, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3722, NULL, NULL, 62, 12845, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3723, NULL, NULL, 62, 12850, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3724, NULL, NULL, 62, 12863, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3725, NULL, NULL, 62, 12872, 2, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3726, NULL, NULL, 62, 12826, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3727, NULL, NULL, 62, 12831, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3728, NULL, NULL, 62, 12846, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3729, NULL, NULL, 62, 12851, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3730, NULL, NULL, 62, 12864, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3731, NULL, NULL, 62, 12873, 3, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3732, NULL, NULL, 62, 12827, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3733, NULL, NULL, 62, 12832, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3734, NULL, NULL, 62, 12847, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3735, NULL, NULL, 62, 12852, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3736, NULL, NULL, 62, 12865, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3737, NULL, NULL, 62, 12874, 4, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3738, NULL, NULL, 62, 12828, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3739, NULL, NULL, 62, 12833, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3740, NULL, NULL, 62, 12848, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3741, NULL, NULL, 62, 12853, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3742, NULL, NULL, 62, 12866, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3743, NULL, NULL, 62, 12875, 5, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3744, NULL, NULL, 62, 12834, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3745, NULL, NULL, 62, 12854, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3746, NULL, NULL, 62, 12867, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3747, NULL, NULL, 62, 12876, 6, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3748, NULL, NULL, 62, 12855, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3749, NULL, NULL, 62, 12868, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3750, NULL, NULL, 62, 12877, 7, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3751, NULL, NULL, 62, 12837, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3752, NULL, NULL, 62, 12857, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3753, NULL, NULL, 62, 12870, 9, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3754, NULL, NULL, 62, 12838, 10, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3755, NULL, NULL, 62, 12858, 10, NULL);
INSERT INTO `busi_template_conference_default_view_paticipant` VALUES (3756, NULL, NULL, 62, 12839, 11, NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 2511 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议模板的级联部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_dept
-- ----------------------------
INSERT INTO `busi_template_dept` VALUES (1424, '2021-03-30 17:26:27', NULL, '0b51f2de-2008-429d-a62e-1f874b4f130b', 42, 213, 5);
INSERT INTO `busi_template_dept` VALUES (1425, '2021-03-30 17:26:27', NULL, 'f9b5cf1d-da50-44da-919d-ccc0dea11ac5', 42, 214, 4);
INSERT INTO `busi_template_dept` VALUES (1426, '2021-03-30 17:26:27', NULL, 'c5f29e6b-e09d-4ecc-bf45-69c6dfa1a159', 42, 215, 3);
INSERT INTO `busi_template_dept` VALUES (1427, '2021-03-30 17:26:27', NULL, '9f36db9a-2539-474e-bab5-76abeb9c5b2f', 42, 216, 2);
INSERT INTO `busi_template_dept` VALUES (1428, '2021-03-30 17:26:27', NULL, 'd164653c-8142-476b-ae55-76f595fe91f7', 42, 217, 1);
INSERT INTO `busi_template_dept` VALUES (2487, '2021-04-15 17:50:14', NULL, '99e7f687-c630-4f09-bcd0-820c3c885858', 39, 103, 14);
INSERT INTO `busi_template_dept` VALUES (2488, '2021-04-15 17:50:14', NULL, '3542cae7-ecc1-4e6e-b6fb-720c641b9604', 39, 205, 13);
INSERT INTO `busi_template_dept` VALUES (2489, '2021-04-15 17:50:14', NULL, '4b35732b-bb50-4bc2-a551-d175feee694c', 39, 206, 12);
INSERT INTO `busi_template_dept` VALUES (2490, '2021-04-15 17:50:14', NULL, '347d3a00-f782-4e8a-bcc8-ab1117722503', 39, 208, 11);
INSERT INTO `busi_template_dept` VALUES (2491, '2021-04-15 17:50:14', NULL, '816e2c6d-7c50-45ae-a0ad-49e9e82409ee', 39, 207, 10);
INSERT INTO `busi_template_dept` VALUES (2492, '2021-04-15 17:50:14', NULL, 'a1bb54ee-fcd2-468a-95b0-9316c0141243', 39, 204, 9);
INSERT INTO `busi_template_dept` VALUES (2493, '2021-04-15 17:50:14', NULL, '75b1c2ff-3e73-4b17-a987-1515f9cdfc78', 39, 203, 8);
INSERT INTO `busi_template_dept` VALUES (2494, '2021-04-15 17:50:14', NULL, '6ee93486-e40a-4b2e-99c2-8f0912a132bb', 39, 202, 7);
INSERT INTO `busi_template_dept` VALUES (2495, '2021-04-15 17:50:14', NULL, '82fe53f1-417f-43fd-8863-5f4875ac5fcd', 39, 201, 6);
INSERT INTO `busi_template_dept` VALUES (2496, '2021-04-15 17:50:14', NULL, 'e3b7ca1c-5f6e-4465-a98f-40bd2728f800', 39, 200, 5);
INSERT INTO `busi_template_dept` VALUES (2497, '2021-04-15 17:50:14', NULL, '235c0517-9ae3-47a9-9fe0-9fc78931f15c', 39, 107, 4);
INSERT INTO `busi_template_dept` VALUES (2498, '2021-04-15 17:50:14', NULL, '1481f43e-0bfd-4a18-9fb5-8778b6b764b2', 39, 104, 3);
INSERT INTO `busi_template_dept` VALUES (2499, '2021-04-15 17:50:14', NULL, '70f7e4ef-00f9-4da8-816b-f42df6b59866', 39, 105, 2);
INSERT INTO `busi_template_dept` VALUES (2500, '2021-04-15 17:50:14', NULL, 'adf4d073-dece-445e-9a48-5e69802a3609', 39, 106, 1);
INSERT INTO `busi_template_dept` VALUES (2506, '2021-04-15 18:19:50', NULL, 'd6ebbb43-7b26-4747-b43a-4f3e0e3d1071', 62, 103, 5);
INSERT INTO `busi_template_dept` VALUES (2507, '2021-04-15 18:19:50', NULL, 'ad5f0223-89c9-42aa-b9f2-68cb10b25f33', 62, 104, 4);
INSERT INTO `busi_template_dept` VALUES (2508, '2021-04-15 18:19:50', NULL, 'e129e898-7fca-4467-90a4-6a7e47dd23c8', 62, 105, 3);
INSERT INTO `busi_template_dept` VALUES (2509, '2021-04-15 18:19:50', NULL, '290d564b-c2aa-45b7-afaf-e70d3e820ca9', 62, 106, 2);
INSERT INTO `busi_template_dept` VALUES (2510, '2021-04-15 18:19:50', NULL, '2887d3e4-6c36-4b3c-9026-24f1cecc9b2f', 62, 107, 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 12881 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会议模板的参会者' ROW_FORMAT = Dynamic;

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
INSERT INTO `busi_template_participant` VALUES (12642, '2021-04-15 17:50:14', NULL, 'd09ec0d0-6537-4852-a359-2ac321404beb', 39, 62, 1);
INSERT INTO `busi_template_participant` VALUES (12643, '2021-04-15 17:50:14', NULL, 'df8b0373-7c19-4509-bdf5-e52c7c8bb7c8', 39, 61, 2);
INSERT INTO `busi_template_participant` VALUES (12644, '2021-04-15 17:50:14', NULL, 'd55c6e6d-8f74-4cb6-accc-92c041956719', 39, 58, 3);
INSERT INTO `busi_template_participant` VALUES (12645, '2021-04-15 17:50:14', NULL, 'f160d8c8-95f4-4ba7-b978-3311ab3f8a7b', 39, 60, 4);
INSERT INTO `busi_template_participant` VALUES (12646, '2021-04-15 17:50:14', NULL, '5efe2c48-0c0d-4a57-9fd7-db09651c9580', 39, 59, 5);
INSERT INTO `busi_template_participant` VALUES (12647, '2021-04-15 17:50:14', NULL, '014b4256-89ef-4076-a752-e0b6eadea4aa', 39, 78, 1);
INSERT INTO `busi_template_participant` VALUES (12648, '2021-04-15 17:50:14', NULL, '2f23b101-90b2-491f-b726-ade79bbe7c8c', 39, 77, 2);
INSERT INTO `busi_template_participant` VALUES (12649, '2021-04-15 17:50:14', NULL, '7a083b89-0339-48fc-ace3-1f5147468e37', 39, 76, 3);
INSERT INTO `busi_template_participant` VALUES (12650, '2021-04-15 17:50:14', NULL, '2c5a4465-24f8-4af2-ae45-137a17f2b998', 39, 75, 4);
INSERT INTO `busi_template_participant` VALUES (12651, '2021-04-15 17:50:14', NULL, 'a5668172-f1a7-4a46-9b87-b0e19855010c', 39, 74, 5);
INSERT INTO `busi_template_participant` VALUES (12652, '2021-04-15 17:50:14', NULL, 'a08d21cb-debe-4de4-a673-3acbdbd71a7a', 39, 73, 6);
INSERT INTO `busi_template_participant` VALUES (12653, '2021-04-15 17:50:14', NULL, 'b96afc4f-bdaf-4dd5-8f63-8a6648ccc589', 39, 72, 7);
INSERT INTO `busi_template_participant` VALUES (12654, '2021-04-15 17:50:14', NULL, '3937132e-f21c-4f2b-af4b-89e91e6f2e6c', 39, 71, 8);
INSERT INTO `busi_template_participant` VALUES (12655, '2021-04-15 17:50:14', NULL, '62449860-6684-461b-9fa3-b78d395abd19', 39, 70, 9);
INSERT INTO `busi_template_participant` VALUES (12656, '2021-04-15 17:50:14', NULL, '59d523f3-b484-4ed1-9311-7c4c149f69eb', 39, 69, 10);
INSERT INTO `busi_template_participant` VALUES (12657, '2021-04-15 17:50:14', NULL, '423122fe-4143-4ab2-8fb0-352ae2f89d5b', 39, 68, 11);
INSERT INTO `busi_template_participant` VALUES (12658, '2021-04-15 17:50:14', NULL, 'ef49a028-196f-48f7-8b55-290c6d7c7454', 39, 67, 12);
INSERT INTO `busi_template_participant` VALUES (12659, '2021-04-15 17:50:14', NULL, 'e57e7f48-c14c-43eb-8948-dd672f3ec182', 39, 66, 13);
INSERT INTO `busi_template_participant` VALUES (12660, '2021-04-15 17:50:14', NULL, 'c2a72e7f-8e10-4119-b337-fd0a4b565058', 39, 85, 1);
INSERT INTO `busi_template_participant` VALUES (12661, '2021-04-15 17:50:14', NULL, '20fc8566-306a-426f-a542-0a0d95258e11', 39, 84, 2);
INSERT INTO `busi_template_participant` VALUES (12662, '2021-04-15 17:50:14', NULL, '1a0e4ab0-988f-4237-a5c8-ed32a8ab92a6', 39, 83, 3);
INSERT INTO `busi_template_participant` VALUES (12663, '2021-04-15 17:50:14', NULL, '7d857439-869c-4dfe-82a5-262f15ef693b', 39, 82, 4);
INSERT INTO `busi_template_participant` VALUES (12664, '2021-04-15 17:50:14', NULL, '0c75cbfc-fffe-49df-894b-bb2251998e14', 39, 81, 5);
INSERT INTO `busi_template_participant` VALUES (12665, '2021-04-15 17:50:14', NULL, 'e398bfc2-82bb-4329-af04-9456e3f75eac', 39, 80, 6);
INSERT INTO `busi_template_participant` VALUES (12666, '2021-04-15 17:50:14', NULL, 'f7aacf99-426e-402d-b820-1fd0b47f5e89', 39, 79, 7);
INSERT INTO `busi_template_participant` VALUES (12667, '2021-04-15 17:50:14', NULL, '72c43a61-7b40-42b2-89ba-fd09d8315604', 39, 98, 1);
INSERT INTO `busi_template_participant` VALUES (12668, '2021-04-15 17:50:14', NULL, '9653355f-0b96-44b9-835a-010332596b53', 39, 97, 2);
INSERT INTO `busi_template_participant` VALUES (12669, '2021-04-15 17:50:14', NULL, '8b78ce72-7b95-41bf-94d1-808ae92a62c2', 39, 96, 3);
INSERT INTO `busi_template_participant` VALUES (12670, '2021-04-15 17:50:14', NULL, '440ee23f-435e-4e11-b08d-62af3fb9c970', 39, 95, 4);
INSERT INTO `busi_template_participant` VALUES (12671, '2021-04-15 17:50:14', NULL, 'f090c692-7083-48e3-9f28-31f087e1ee29', 39, 94, 5);
INSERT INTO `busi_template_participant` VALUES (12672, '2021-04-15 17:50:14', NULL, '16f01d7c-a5b7-4265-9a09-9dbd500f221b', 39, 93, 6);
INSERT INTO `busi_template_participant` VALUES (12673, '2021-04-15 17:50:14', NULL, 'c89f2e9c-32e8-4824-b4da-7bf123689110', 39, 92, 7);
INSERT INTO `busi_template_participant` VALUES (12674, '2021-04-15 17:50:14', NULL, '0635d037-1114-476d-b450-56a1a129b5f8', 39, 91, 8);
INSERT INTO `busi_template_participant` VALUES (12675, '2021-04-15 17:50:14', NULL, '3a1fde53-c06b-4fef-819c-d755f29d74d5', 39, 90, 9);
INSERT INTO `busi_template_participant` VALUES (12676, '2021-04-15 17:50:14', NULL, '0122a258-d1e1-4b51-9846-78067778feb6', 39, 89, 10);
INSERT INTO `busi_template_participant` VALUES (12677, '2021-04-15 17:50:14', NULL, '24a253f1-ada8-4f27-a36c-14bfaa864554', 39, 88, 11);
INSERT INTO `busi_template_participant` VALUES (12678, '2021-04-15 17:50:14', NULL, '2d6c31ae-952a-462a-892d-f62edac50676', 39, 87, 12);
INSERT INTO `busi_template_participant` VALUES (12679, '2021-04-15 17:50:14', NULL, '4b5476a2-e1f6-443f-946e-0cee5a01f442', 39, 86, 13);
INSERT INTO `busi_template_participant` VALUES (12680, '2021-04-15 17:50:14', NULL, 'b170b0c0-4ca1-4460-ab26-5c965aaedef3', 39, 107, 1);
INSERT INTO `busi_template_participant` VALUES (12681, '2021-04-15 17:50:14', NULL, '5431eb67-14c5-40c9-85cd-d97fc57aa5a0', 39, 106, 2);
INSERT INTO `busi_template_participant` VALUES (12682, '2021-04-15 17:50:14', NULL, 'a8ed3496-e276-42e1-a26e-a5dbf6b2e442', 39, 105, 3);
INSERT INTO `busi_template_participant` VALUES (12683, '2021-04-15 17:50:14', NULL, 'ebe47c8a-3ff5-44e4-ab20-6996a5f410dd', 39, 104, 4);
INSERT INTO `busi_template_participant` VALUES (12684, '2021-04-15 17:50:14', NULL, 'd3548ed6-94f5-4a2d-aa80-b0ded1e1274a', 39, 103, 5);
INSERT INTO `busi_template_participant` VALUES (12685, '2021-04-15 17:50:14', NULL, 'df5be673-9bb7-4cc2-aa3e-76210f9b8216', 39, 102, 6);
INSERT INTO `busi_template_participant` VALUES (12686, '2021-04-15 17:50:14', NULL, '654fab03-dc5d-485d-af2d-58410e14578f', 39, 101, 7);
INSERT INTO `busi_template_participant` VALUES (12687, '2021-04-15 17:50:14', NULL, 'c70b3f74-4bb7-459d-b897-6845f09a25dd', 39, 100, 8);
INSERT INTO `busi_template_participant` VALUES (12688, '2021-04-15 17:50:14', NULL, 'df251cd4-af6a-48ca-a0ca-8bf80c6a9c0d', 39, 99, 9);
INSERT INTO `busi_template_participant` VALUES (12689, '2021-04-15 17:50:14', NULL, '9edf0295-8d48-426c-a43f-dd53c7917512', 39, 117, 1);
INSERT INTO `busi_template_participant` VALUES (12690, '2021-04-15 17:50:14', NULL, '5470dcff-df34-4226-a625-8ed714936afa', 39, 116, 2);
INSERT INTO `busi_template_participant` VALUES (12691, '2021-04-15 17:50:14', NULL, '39f9e7e9-e335-444c-a669-4dfcd3943b02', 39, 114, 3);
INSERT INTO `busi_template_participant` VALUES (12692, '2021-04-15 17:50:14', NULL, '1cdbacf9-7542-4571-84c7-c1d1f350ff35', 39, 113, 4);
INSERT INTO `busi_template_participant` VALUES (12693, '2021-04-15 17:50:14', NULL, '5a272dc6-9635-43f8-9eaf-a37dedbdf5dd', 39, 112, 5);
INSERT INTO `busi_template_participant` VALUES (12694, '2021-04-15 17:50:14', NULL, '22d673e9-896b-409f-a8e1-ecbba9430372', 39, 111, 6);
INSERT INTO `busi_template_participant` VALUES (12695, '2021-04-15 17:50:14', NULL, 'c7938c76-fc1f-4693-b7e8-042b50b16bf0', 39, 115, 7);
INSERT INTO `busi_template_participant` VALUES (12696, '2021-04-15 17:50:14', NULL, 'b780c723-223f-4c74-885d-66d59b6528b4', 39, 110, 8);
INSERT INTO `busi_template_participant` VALUES (12697, '2021-04-15 17:50:14', NULL, 'da943963-b634-41b7-be43-c14cef337eaf', 39, 109, 9);
INSERT INTO `busi_template_participant` VALUES (12698, '2021-04-15 17:50:14', NULL, '5e161082-25e4-43ee-aaf2-499cca60d154', 39, 108, 10);
INSERT INTO `busi_template_participant` VALUES (12699, '2021-04-15 17:50:14', NULL, 'ceb7c28b-ea0f-4b71-8f83-fa655138bb91', 39, 128, 1);
INSERT INTO `busi_template_participant` VALUES (12700, '2021-04-15 17:50:14', NULL, 'ae61023c-466c-4e17-8272-351d94e48500', 39, 127, 2);
INSERT INTO `busi_template_participant` VALUES (12701, '2021-04-15 17:50:14', NULL, 'ecf95aaa-94e0-482c-9412-61b2e9c7bddc', 39, 126, 3);
INSERT INTO `busi_template_participant` VALUES (12702, '2021-04-15 17:50:14', NULL, '2c62abcb-da8e-4dbc-bb72-14b0526c8c3c', 39, 125, 4);
INSERT INTO `busi_template_participant` VALUES (12703, '2021-04-15 17:50:14', NULL, '549ee3a6-20f0-4dd1-9bba-cfb231dae9e3', 39, 124, 5);
INSERT INTO `busi_template_participant` VALUES (12704, '2021-04-15 17:50:14', NULL, '630942bf-6d42-42db-9a85-db84848ebec1', 39, 123, 6);
INSERT INTO `busi_template_participant` VALUES (12705, '2021-04-15 17:50:14', NULL, '128e882c-45be-4e9f-a6bd-e9983b60845d', 39, 122, 7);
INSERT INTO `busi_template_participant` VALUES (12706, '2021-04-15 17:50:14', NULL, '45a7d405-30ba-4041-a816-0547d9a9a003', 39, 121, 8);
INSERT INTO `busi_template_participant` VALUES (12707, '2021-04-15 17:50:14', NULL, 'f23c6b2b-b0a9-45e1-aeb3-ec90c0e2def1', 39, 120, 9);
INSERT INTO `busi_template_participant` VALUES (12708, '2021-04-15 17:50:14', NULL, '174e3534-ce12-4a19-8071-2817bbcfdba7', 39, 119, 10);
INSERT INTO `busi_template_participant` VALUES (12709, '2021-04-15 17:50:14', NULL, '51bfbd55-28cd-4274-8fd5-2ba64cfe3580', 39, 118, 11);
INSERT INTO `busi_template_participant` VALUES (12710, '2021-04-15 17:50:14', NULL, '8c406327-491b-40aa-acef-5308c999d286', 39, 133, 1);
INSERT INTO `busi_template_participant` VALUES (12711, '2021-04-15 17:50:14', NULL, '74056e27-d467-4a8b-b2ce-8e0b4b73f2c0', 39, 132, 2);
INSERT INTO `busi_template_participant` VALUES (12712, '2021-04-15 17:50:14', NULL, '195ab159-7c82-47c5-9911-f2568e024b96', 39, 131, 3);
INSERT INTO `busi_template_participant` VALUES (12713, '2021-04-15 17:50:14', NULL, 'd404cb9f-3ea9-40e7-9236-2d2561156aff', 39, 130, 4);
INSERT INTO `busi_template_participant` VALUES (12714, '2021-04-15 17:50:14', NULL, '35a4b94c-b4d4-437e-9e93-379df8646af0', 39, 129, 5);
INSERT INTO `busi_template_participant` VALUES (12715, '2021-04-15 17:50:14', NULL, 'a67d9c34-4b43-4ad4-9cea-117aac776ece', 39, 137, 1);
INSERT INTO `busi_template_participant` VALUES (12716, '2021-04-15 17:50:14', NULL, 'edbe7c18-e47b-477e-9fa2-37fddb365216', 39, 136, 2);
INSERT INTO `busi_template_participant` VALUES (12717, '2021-04-15 17:50:14', NULL, 'd758f7a3-1187-4726-9783-e9c62390f5bd', 39, 135, 3);
INSERT INTO `busi_template_participant` VALUES (12718, '2021-04-15 17:50:14', NULL, 'e607d05b-3c87-4fe0-bb94-e48ed11f085c', 39, 134, 4);
INSERT INTO `busi_template_participant` VALUES (12719, '2021-04-15 17:50:14', NULL, 'fd210308-486e-4aa1-a6d9-6d6f5d03811a', 39, 147, 1);
INSERT INTO `busi_template_participant` VALUES (12720, '2021-04-15 17:50:14', NULL, '6374d21b-2997-4656-a950-383482bcbf7e', 39, 146, 2);
INSERT INTO `busi_template_participant` VALUES (12721, '2021-04-15 17:50:14', NULL, 'c342999e-52d5-4eda-9080-f98fe09ebf40', 39, 145, 3);
INSERT INTO `busi_template_participant` VALUES (12722, '2021-04-15 17:50:14', NULL, '61357080-c991-410d-9c04-0c09e525811d', 39, 144, 4);
INSERT INTO `busi_template_participant` VALUES (12723, '2021-04-15 17:50:14', NULL, 'f398356c-a265-4b94-9502-648bae7f1954', 39, 143, 5);
INSERT INTO `busi_template_participant` VALUES (12724, '2021-04-15 17:50:14', NULL, '0d32385f-226f-4e8d-9526-5c625ae6eed0', 39, 142, 6);
INSERT INTO `busi_template_participant` VALUES (12725, '2021-04-15 17:50:14', NULL, '55de3502-55ca-4228-b1ca-985111292293', 39, 141, 7);
INSERT INTO `busi_template_participant` VALUES (12726, '2021-04-15 17:50:14', NULL, '7e29f1fb-e9a2-48d7-b140-53aa2a3a6c3a', 39, 140, 8);
INSERT INTO `busi_template_participant` VALUES (12727, '2021-04-15 17:50:14', NULL, '805a04cf-6bb1-4615-ba2a-de8d2e6d61cf', 39, 139, 9);
INSERT INTO `busi_template_participant` VALUES (12728, '2021-04-15 17:50:14', NULL, '18294fa7-b030-4f83-a8a6-fa7d09792231', 39, 138, 10);
INSERT INTO `busi_template_participant` VALUES (12729, '2021-04-15 17:50:14', NULL, 'be578268-e578-4126-835a-ef72fb2a653b', 39, 156, 1);
INSERT INTO `busi_template_participant` VALUES (12730, '2021-04-15 17:50:14', NULL, 'f527f505-621f-44b6-a744-ad1998b8843e', 39, 155, 2);
INSERT INTO `busi_template_participant` VALUES (12731, '2021-04-15 17:50:14', NULL, '91bbaada-ac2c-42b0-9fca-0ae62a5617ca', 39, 154, 3);
INSERT INTO `busi_template_participant` VALUES (12732, '2021-04-15 17:50:14', NULL, '4aff12ff-9777-49fa-a7c3-13f179e2527b', 39, 153, 4);
INSERT INTO `busi_template_participant` VALUES (12733, '2021-04-15 17:50:14', NULL, '1806bdea-e0c4-4d3d-81f9-b51ba18b4772', 39, 152, 5);
INSERT INTO `busi_template_participant` VALUES (12734, '2021-04-15 17:50:14', NULL, '180f1000-fa61-44c5-a9c6-4948d8de4e09', 39, 151, 6);
INSERT INTO `busi_template_participant` VALUES (12735, '2021-04-15 17:50:14', NULL, '5a5bc555-6a59-454b-bc6c-11dfe827d99a', 39, 150, 7);
INSERT INTO `busi_template_participant` VALUES (12736, '2021-04-15 17:50:14', NULL, '8805c1f3-f206-4eea-b5b4-353404d6fc1c', 39, 149, 8);
INSERT INTO `busi_template_participant` VALUES (12737, '2021-04-15 17:50:14', NULL, '3c4d7fad-5f90-4787-874f-13fd3f10d68d', 39, 148, 9);
INSERT INTO `busi_template_participant` VALUES (12738, '2021-04-15 17:50:14', NULL, '78b32f4f-af17-4de0-ae7a-cff7c451885f', 39, 46, 10);
INSERT INTO `busi_template_participant` VALUES (12739, '2021-04-15 17:50:14', NULL, '19898ba3-0ab8-49c9-894f-35f8175d574b', 39, 163, 1);
INSERT INTO `busi_template_participant` VALUES (12740, '2021-04-15 17:50:14', NULL, 'ec1ae24f-c04b-4527-bb28-306b05a40078', 39, 162, 2);
INSERT INTO `busi_template_participant` VALUES (12741, '2021-04-15 17:50:14', NULL, 'a3c26fca-cc6c-4d3a-9053-f1f9f1ff127b', 39, 161, 3);
INSERT INTO `busi_template_participant` VALUES (12742, '2021-04-15 17:50:14', NULL, '8a0718e2-2d69-466d-8793-d2860b19494d', 39, 160, 4);
INSERT INTO `busi_template_participant` VALUES (12743, '2021-04-15 17:50:14', NULL, '3ebecb04-aecc-486d-b468-b68a627e78b1', 39, 159, 5);
INSERT INTO `busi_template_participant` VALUES (12744, '2021-04-15 17:50:14', NULL, '6f6feefc-d307-4c6b-aeab-c8a76ca7d9a0', 39, 158, 6);
INSERT INTO `busi_template_participant` VALUES (12745, '2021-04-15 17:50:14', NULL, '69180960-65e4-47c0-a6a7-48fe92c815d5', 39, 157, 7);
INSERT INTO `busi_template_participant` VALUES (12746, '2021-04-15 17:50:14', NULL, 'c760e940-869d-4ea8-9aac-d974352fb365', 39, 47, 8);
INSERT INTO `busi_template_participant` VALUES (12747, '2021-04-15 17:50:14', NULL, '3baa1bf5-4b9e-49e5-86a6-9956d0c81675', 39, 171, 1);
INSERT INTO `busi_template_participant` VALUES (12748, '2021-04-15 17:50:14', NULL, 'd64d3a03-fee1-476a-8b82-f3479f4e89b6', 39, 170, 2);
INSERT INTO `busi_template_participant` VALUES (12749, '2021-04-15 17:50:14', NULL, '01042ddb-59c6-4e54-b874-8655cf0da209', 39, 169, 3);
INSERT INTO `busi_template_participant` VALUES (12750, '2021-04-15 17:50:14', NULL, 'a8dc7e84-ac55-4c81-adcc-b807e210346a', 39, 168, 4);
INSERT INTO `busi_template_participant` VALUES (12751, '2021-04-15 17:50:14', NULL, '4f9247f6-189e-4403-a2db-4f410c7faee6', 39, 167, 5);
INSERT INTO `busi_template_participant` VALUES (12752, '2021-04-15 17:50:14', NULL, 'e9fc4f14-690a-4645-bd95-ab5786f0bb7d', 39, 166, 6);
INSERT INTO `busi_template_participant` VALUES (12753, '2021-04-15 17:50:14', NULL, 'cd013710-8d5e-45ea-9961-8d7a292a4834', 39, 165, 7);
INSERT INTO `busi_template_participant` VALUES (12754, '2021-04-15 17:50:14', NULL, '6467bf2c-a1bd-4e4c-a479-727acc75a98b', 39, 164, 8);
INSERT INTO `busi_template_participant` VALUES (12755, '2021-04-15 17:50:14', NULL, '52b503df-38ba-4757-b902-f77ca6875602', 39, 36, 9);
INSERT INTO `busi_template_participant` VALUES (12756, '2021-04-15 17:50:14', NULL, '28e0224f-84ae-46ce-b1a9-87bfbe8b5aa5', 39, 176, 1);
INSERT INTO `busi_template_participant` VALUES (12757, '2021-04-15 17:50:14', NULL, 'e70014be-7e71-4e19-945b-732e66e4fa8c', 39, 175, 2);
INSERT INTO `busi_template_participant` VALUES (12758, '2021-04-15 17:50:14', NULL, '2c79e69d-a94c-471e-8fb7-ef2b7a7bfe65', 39, 174, 3);
INSERT INTO `busi_template_participant` VALUES (12759, '2021-04-15 17:50:14', NULL, '3d4fc67e-0fa7-4d2b-8518-61f8314ba64b', 39, 173, 4);
INSERT INTO `busi_template_participant` VALUES (12760, '2021-04-15 17:50:14', NULL, 'f7f649e4-3c0f-4dcf-ad90-c2e0d15535a9', 39, 172, 5);
INSERT INTO `busi_template_participant` VALUES (12761, '2021-04-15 17:50:14', NULL, '354b1589-72a9-4901-93df-e752950c09e5', 39, 48, 6);
INSERT INTO `busi_template_participant` VALUES (12762, '2021-04-15 17:50:14', NULL, '408eb3c5-165a-4651-a725-9b341ef57039', 39, 179, 1);
INSERT INTO `busi_template_participant` VALUES (12763, '2021-04-15 17:50:14', NULL, '4aafb756-7458-41d1-a5d2-c9d4da4747e5', 39, 178, 2);
INSERT INTO `busi_template_participant` VALUES (12764, '2021-04-15 17:50:14', NULL, 'cc311df3-2aac-4074-804d-5b34298b8515', 39, 177, 3);
INSERT INTO `busi_template_participant` VALUES (12765, '2021-04-15 17:50:14', NULL, '7b525664-bb9f-44e7-935f-97db27d19720', 39, 53, 4);
INSERT INTO `busi_template_participant` VALUES (12766, '2021-04-15 17:50:14', NULL, '6527f58b-c24f-4b7f-bf2e-8816bf1533e9', 39, 49, 5);
INSERT INTO `busi_template_participant` VALUES (12824, '2021-04-15 18:19:49', NULL, 'b873cb0c-2d6b-41a1-b83a-d34c978ee165', 62, 62, 1);
INSERT INTO `busi_template_participant` VALUES (12825, '2021-04-15 18:19:49', NULL, 'ae08d6e5-5541-471d-ac37-0b2d0ec936f9', 62, 61, 2);
INSERT INTO `busi_template_participant` VALUES (12826, '2021-04-15 18:19:50', NULL, 'b4f8a035-159a-4806-b3d2-5d55ffccf295', 62, 60, 3);
INSERT INTO `busi_template_participant` VALUES (12827, '2021-04-15 18:19:50', NULL, '7abd045e-08e9-4f0b-8d48-fc903ef4d949', 62, 59, 4);
INSERT INTO `busi_template_participant` VALUES (12828, '2021-04-15 18:19:50', NULL, '63626734-0652-415f-b481-34f71ceb2b66', 62, 58, 5);
INSERT INTO `busi_template_participant` VALUES (12829, '2021-04-15 18:19:50', NULL, 'd3e5ce44-0ceb-4099-85f1-6eed0ffc7eb4', 62, 78, 1);
INSERT INTO `busi_template_participant` VALUES (12830, '2021-04-15 18:19:50', NULL, '8fe4c5ff-d3c2-4cbb-baa8-4c85bce97d29', 62, 77, 2);
INSERT INTO `busi_template_participant` VALUES (12831, '2021-04-15 18:19:50', NULL, '7b1640d2-b4fd-4508-bffb-ec36d69d75df', 62, 76, 3);
INSERT INTO `busi_template_participant` VALUES (12832, '2021-04-15 18:19:50', NULL, 'd6a55189-e549-4a23-a692-79a96955fbcb', 62, 75, 4);
INSERT INTO `busi_template_participant` VALUES (12833, '2021-04-15 18:19:50', NULL, '5d0b0934-9d5f-4327-a900-82a50c561d76', 62, 74, 5);
INSERT INTO `busi_template_participant` VALUES (12834, '2021-04-15 18:19:50', NULL, 'dc41b633-8fbf-4dcc-b6a7-f7245a455ff5', 62, 73, 6);
INSERT INTO `busi_template_participant` VALUES (12835, '2021-04-15 18:19:50', NULL, 'c7df6332-94dd-4e6f-afa1-cbf9eae40462', 62, 72, 7);
INSERT INTO `busi_template_participant` VALUES (12836, '2021-04-15 18:19:50', NULL, '6d5c4251-7240-43de-b44b-dc8c71c0c479', 62, 71, 8);
INSERT INTO `busi_template_participant` VALUES (12837, '2021-04-15 18:19:50', NULL, '8032237d-0849-41d8-aeea-067eeefdedc8', 62, 70, 9);
INSERT INTO `busi_template_participant` VALUES (12838, '2021-04-15 18:19:50', NULL, '76f02449-c70c-4d63-8ed6-193ae02cb446', 62, 69, 10);
INSERT INTO `busi_template_participant` VALUES (12839, '2021-04-15 18:19:50', NULL, 'f8e23700-0c96-41f6-8057-3b816e7a7933', 62, 68, 11);
INSERT INTO `busi_template_participant` VALUES (12840, '2021-04-15 18:19:50', NULL, 'd4996962-e87e-46a6-8032-71251f369249', 62, 67, 12);
INSERT INTO `busi_template_participant` VALUES (12841, '2021-04-15 18:19:50', NULL, '6e8309b2-f012-4121-8c86-f57ab7b74ecd', 62, 66, 13);
INSERT INTO `busi_template_participant` VALUES (12842, '2021-04-15 18:19:50', NULL, 'bb64a836-d461-4d4d-ac32-ac96bf5590b4', 62, 79, 0);
INSERT INTO `busi_template_participant` VALUES (12843, '2021-04-15 18:19:50', NULL, '40542f0b-0322-46e0-9b63-33e3bc542443', 62, 80, 0);
INSERT INTO `busi_template_participant` VALUES (12844, '2021-04-15 18:19:50', NULL, '6d7db688-a394-4a70-8615-27babad9eb74', 62, 85, 1);
INSERT INTO `busi_template_participant` VALUES (12845, '2021-04-15 18:19:50', NULL, '0e342997-bdae-45ee-93cc-d4032a3a2a08', 62, 84, 2);
INSERT INTO `busi_template_participant` VALUES (12846, '2021-04-15 18:19:50', NULL, '5f59c62f-14ef-473e-ba22-6edf7adde179', 62, 83, 3);
INSERT INTO `busi_template_participant` VALUES (12847, '2021-04-15 18:19:50', NULL, '53e874e6-bd90-4051-b226-f77a6eae8543', 62, 82, 4);
INSERT INTO `busi_template_participant` VALUES (12848, '2021-04-15 18:19:50', NULL, '7eada2cc-4a2f-476c-9563-1569cc5c7653', 62, 81, 5);
INSERT INTO `busi_template_participant` VALUES (12849, '2021-04-15 18:19:50', NULL, '8e2b0c9b-4261-4100-a103-000eb130a9f2', 62, 98, 1);
INSERT INTO `busi_template_participant` VALUES (12850, '2021-04-15 18:19:50', NULL, '99deef7b-a1bd-4be9-95ca-451949541f77', 62, 97, 2);
INSERT INTO `busi_template_participant` VALUES (12851, '2021-04-15 18:19:50', NULL, 'e81bde53-25fe-49ac-a0f5-8797e009c2c5', 62, 96, 3);
INSERT INTO `busi_template_participant` VALUES (12852, '2021-04-15 18:19:50', NULL, '87aa62de-2373-4590-b567-7512a78d50f6', 62, 95, 4);
INSERT INTO `busi_template_participant` VALUES (12853, '2021-04-15 18:19:50', NULL, '34f53c3c-1fd7-4ddf-a274-4d8f6bc1eb7a', 62, 94, 5);
INSERT INTO `busi_template_participant` VALUES (12854, '2021-04-15 18:19:50', NULL, '1b844447-1007-47be-9fb5-ddea92d3acdf', 62, 93, 6);
INSERT INTO `busi_template_participant` VALUES (12855, '2021-04-15 18:19:50', NULL, 'b1dc0bdc-2f05-479b-bcd3-b5592dfb62dc', 62, 92, 7);
INSERT INTO `busi_template_participant` VALUES (12856, '2021-04-15 18:19:50', NULL, 'a21e1283-b389-487e-9cc6-ae7c4f72e8c7', 62, 91, 8);
INSERT INTO `busi_template_participant` VALUES (12857, '2021-04-15 18:19:50', NULL, 'b57db7c7-f852-4ac5-bf27-c6adcfaa8124', 62, 90, 9);
INSERT INTO `busi_template_participant` VALUES (12858, '2021-04-15 18:19:50', NULL, 'be9aa234-eb30-4364-9f9f-4deae3ca73eb', 62, 89, 10);
INSERT INTO `busi_template_participant` VALUES (12859, '2021-04-15 18:19:50', NULL, 'a361ec21-1770-47e7-a664-89639c861c6a', 62, 88, 11);
INSERT INTO `busi_template_participant` VALUES (12860, '2021-04-15 18:19:50', NULL, 'b14c87b0-1240-4af0-89ff-e537874a1132', 62, 87, 12);
INSERT INTO `busi_template_participant` VALUES (12861, '2021-04-15 18:19:50', NULL, '58b5a960-94e4-4535-8b77-1e985c02b312', 62, 86, 13);
INSERT INTO `busi_template_participant` VALUES (12862, '2021-04-15 18:19:50', NULL, 'd0bd4c8e-8031-4d85-a032-3e700392a790', 62, 107, 1);
INSERT INTO `busi_template_participant` VALUES (12863, '2021-04-15 18:19:50', NULL, 'bc0a1eaf-3b02-43c0-9dd7-bd97f2e7a7f2', 62, 106, 2);
INSERT INTO `busi_template_participant` VALUES (12864, '2021-04-15 18:19:50', NULL, '79ed6e13-9abc-4b3f-8a2b-48374fb52eeb', 62, 105, 3);
INSERT INTO `busi_template_participant` VALUES (12865, '2021-04-15 18:19:50', NULL, '9e08a6bb-0df9-4db3-a38d-7d42eadcfdad', 62, 104, 4);
INSERT INTO `busi_template_participant` VALUES (12866, '2021-04-15 18:19:50', NULL, '1f1d203a-c5ba-41d5-a99c-11f44e51bc4d', 62, 103, 5);
INSERT INTO `busi_template_participant` VALUES (12867, '2021-04-15 18:19:50', NULL, '061d9fe4-6572-420c-9ec0-0cd7ac3add39', 62, 102, 6);
INSERT INTO `busi_template_participant` VALUES (12868, '2021-04-15 18:19:50', NULL, 'dab3b5f8-c373-4413-a246-3941332cfaa9', 62, 101, 7);
INSERT INTO `busi_template_participant` VALUES (12869, '2021-04-15 18:19:50', NULL, '6f02a3d7-2fd8-43fa-ac73-62c6d24ce4fc', 62, 100, 8);
INSERT INTO `busi_template_participant` VALUES (12870, '2021-04-15 18:19:50', NULL, '07385dac-4f16-4149-adfd-610d9a7594be', 62, 99, 9);
INSERT INTO `busi_template_participant` VALUES (12871, '2021-04-15 18:19:50', NULL, '409946cb-73a4-4783-9755-469a02c835e7', 62, 117, 1);
INSERT INTO `busi_template_participant` VALUES (12872, '2021-04-15 18:19:50', NULL, 'fd8324b8-ab90-4ec9-be05-ffcfc059edcd', 62, 116, 2);
INSERT INTO `busi_template_participant` VALUES (12873, '2021-04-15 18:19:50', NULL, 'e7f8fe45-cdb5-458e-9674-f508f1b06b74', 62, 115, 3);
INSERT INTO `busi_template_participant` VALUES (12874, '2021-04-15 18:19:50', NULL, 'becdefef-13a2-4033-b09f-6f94eecc154c', 62, 114, 4);
INSERT INTO `busi_template_participant` VALUES (12875, '2021-04-15 18:19:50', NULL, '639fc4b1-fa20-4c70-91a7-11b2f979be71', 62, 113, 5);
INSERT INTO `busi_template_participant` VALUES (12876, '2021-04-15 18:19:50', NULL, 'd6766611-e49b-4777-8128-54d0b6b8994a', 62, 112, 6);
INSERT INTO `busi_template_participant` VALUES (12877, '2021-04-15 18:19:50', NULL, '1d19b601-7c13-432f-9880-6ad052cfd42e', 62, 111, 7);
INSERT INTO `busi_template_participant` VALUES (12878, '2021-04-15 18:19:50', NULL, '7b75caf5-4f74-46cc-83ec-8c76e1b6b402', 62, 110, 8);
INSERT INTO `busi_template_participant` VALUES (12879, '2021-04-15 18:19:50', NULL, '837f96a7-88c0-4eb4-9f58-bcd6e5e7f6b0', 62, 109, 9);
INSERT INTO `busi_template_participant` VALUES (12880, '2021-04-15 18:19:50', NULL, 'd8137954-a27f-48ee-a066-4242cb3a5643', 62, 108, 10);

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
) ENGINE = InnoDB AUTO_INCREMENT = 1438 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮询方案的部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_polling_dept
-- ----------------------------
INSERT INTO `busi_template_polling_dept` VALUES (1430, '2021-04-14 10:41:36', NULL, 39, 57, 100, 2);
INSERT INTO `busi_template_polling_dept` VALUES (1431, '2021-04-14 10:41:36', NULL, 39, 57, 103, 1);
INSERT INTO `busi_template_polling_dept` VALUES (1432, '2021-04-15 17:22:08', NULL, 62, 58, 100, 6);
INSERT INTO `busi_template_polling_dept` VALUES (1433, '2021-04-15 17:22:08', NULL, 62, 58, 103, 5);
INSERT INTO `busi_template_polling_dept` VALUES (1434, '2021-04-15 17:22:08', NULL, 62, 58, 104, 4);
INSERT INTO `busi_template_polling_dept` VALUES (1435, '2021-04-15 17:22:08', NULL, 62, 58, 105, 3);
INSERT INTO `busi_template_polling_dept` VALUES (1436, '2021-04-15 17:22:08', NULL, 62, 58, 106, 2);
INSERT INTO `busi_template_polling_dept` VALUES (1437, '2021-04-15 17:22:08', NULL, 62, 58, 107, 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 8320 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮询方案的参会者' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_polling_paticipant
-- ----------------------------
INSERT INTO `busi_template_polling_paticipant` VALUES (8262, '2021-04-14 10:41:36', NULL, 39, 57, NULL, '192.166.0.22', 59, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8263, '2021-04-14 10:41:36', NULL, 39, 57, NULL, '192.166.1.12', 66, 1, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8264, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.0.22', 59, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8265, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.0.51', 60, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8266, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.0.71', 61, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8267, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.0.81', 62, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8268, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.1.12', 66, 1, 13);
INSERT INTO `busi_template_polling_paticipant` VALUES (8269, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.1.21', 67, 2, 12);
INSERT INTO `busi_template_polling_paticipant` VALUES (8270, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.1.31', 68, 2, 11);
INSERT INTO `busi_template_polling_paticipant` VALUES (8271, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.1.41', 69, 2, 10);
INSERT INTO `busi_template_polling_paticipant` VALUES (8272, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.1.51', 70, 2, 9);
INSERT INTO `busi_template_polling_paticipant` VALUES (8273, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.1.61', 71, 2, 8);
INSERT INTO `busi_template_polling_paticipant` VALUES (8274, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.1.71', 72, 2, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8275, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.1.81', 73, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8276, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.1.91', 74, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8277, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.1.101', 75, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8278, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.1.111', 76, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8279, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.1.121', 77, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8280, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.1.131', 78, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8281, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.2.12', 79, 1, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8282, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.2.21', 80, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8283, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.2.31', 81, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8284, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.2.41', 82, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8285, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.2.51', 83, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8286, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.2.61', 84, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8287, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.2.71', 85, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8288, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.3.12', 86, 1, 13);
INSERT INTO `busi_template_polling_paticipant` VALUES (8289, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.3.21', 87, 2, 12);
INSERT INTO `busi_template_polling_paticipant` VALUES (8290, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.3.31', 88, 2, 11);
INSERT INTO `busi_template_polling_paticipant` VALUES (8291, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.3.41', 89, 2, 10);
INSERT INTO `busi_template_polling_paticipant` VALUES (8292, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.3.51', 90, 2, 9);
INSERT INTO `busi_template_polling_paticipant` VALUES (8293, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.3.61', 91, 2, 8);
INSERT INTO `busi_template_polling_paticipant` VALUES (8294, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.3.71', 92, 2, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8295, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.3.81', 93, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8296, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.3.91', 94, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8297, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.3.101', 95, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8298, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.3.111', 96, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8299, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.3.121', 97, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8300, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.3.131', 98, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8301, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.4.12', 99, 1, 9);
INSERT INTO `busi_template_polling_paticipant` VALUES (8302, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.4.21', 100, 2, 8);
INSERT INTO `busi_template_polling_paticipant` VALUES (8303, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.4.31', 101, 2, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8304, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.4.41', 102, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8305, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.4.51', 103, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8306, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.4.61', 104, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8307, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.4.71', 105, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8308, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.4.81', 106, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8309, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.4.91', 107, 2, 1);
INSERT INTO `busi_template_polling_paticipant` VALUES (8310, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.5.12', 108, 1, 10);
INSERT INTO `busi_template_polling_paticipant` VALUES (8311, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.5.21', 109, 2, 9);
INSERT INTO `busi_template_polling_paticipant` VALUES (8312, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.5.31', 110, 2, 8);
INSERT INTO `busi_template_polling_paticipant` VALUES (8313, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.5.51', 111, 2, 7);
INSERT INTO `busi_template_polling_paticipant` VALUES (8314, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.5.61', 112, 2, 6);
INSERT INTO `busi_template_polling_paticipant` VALUES (8315, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.5.71', 113, 2, 5);
INSERT INTO `busi_template_polling_paticipant` VALUES (8316, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.5.81', 114, 2, 4);
INSERT INTO `busi_template_polling_paticipant` VALUES (8317, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.5.41', 115, 2, 3);
INSERT INTO `busi_template_polling_paticipant` VALUES (8318, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.5.91', 116, 2, 2);
INSERT INTO `busi_template_polling_paticipant` VALUES (8319, '2021-04-15 17:22:08', NULL, 62, 58, NULL, '192.166.5.101', 117, 2, 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮询方案' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_template_polling_scheme
-- ----------------------------
INSERT INTO `busi_template_polling_scheme` VALUES (57, '2021-04-13 18:22:42', '2021-04-14 10:41:36', 39, 2, 1, 'das', 1, 'speakerOnly', 2, 1, 1);
INSERT INTO `busi_template_polling_scheme` VALUES (58, '2021-04-15 17:22:08', '2021-04-15 17:22:08', 62, 10, 1, '22222', 1, 'speakerOnly', 2, 1, 1);

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
  `number` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备号，设备唯一标识',
  `camera_ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '摄像头IP地址',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '终端显示名字',
  `type` int(11) NULL DEFAULT NULL COMMENT '终端类型，枚举值int类型',
  `online_status` int(11) NULL DEFAULT NULL COMMENT '终端状态：1在线，2离线',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ip`(`ip`, `number`) USING BTREE,
  INDEX `dept_id`(`dept_id`) USING BTREE,
  INDEX `create_user_id`(`create_user_id`) USING BTREE,
  CONSTRAINT `busi_terminal_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `busi_terminal_ibfk_2` FOREIGN KEY (`create_user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 263 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '终端信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of busi_terminal
-- ----------------------------
INSERT INTO `busi_terminal` VALUES (36, '2021-02-04 13:12:17', '2021-03-09 16:14:15', 1, 'superAdmin', 206, '192.166.12.12', NULL, '192.166.12.13', '阿勒泰地区', 100, 2);
INSERT INTO `busi_terminal` VALUES (46, '2021-02-20 14:40:39', '2021-03-09 16:06:55', 1, 'superAdmin', 204, '192.166.10.12', NULL, '192.166.10.13', '昌吉州', 100, 2);
INSERT INTO `busi_terminal` VALUES (47, '2021-02-20 14:41:00', '2021-03-09 16:10:45', 1, 'superAdmin', 205, '192.166.11.12', NULL, '192.166.11.13', '塔城地区', 100, 2);
INSERT INTO `busi_terminal` VALUES (48, '2021-02-20 14:41:48', '2021-03-09 16:16:59', 1, 'superAdmin', 207, '192.166.13.12', NULL, '192.166.13.13', '博州', 100, 2);
INSERT INTO `busi_terminal` VALUES (49, '2021-02-20 14:42:00', '2021-03-09 16:36:05', 1, 'superAdmin', 208, '192.166.14.12', NULL, '192.166.14.13', '克拉玛依', 100, 1);
INSERT INTO `busi_terminal` VALUES (53, '2021-02-23 18:43:14', '2021-03-09 16:36:19', 1, 'superAdmin', 208, '192.166.14.21', NULL, '192.166.14.22', '克拉玛依区', 100, 2);
INSERT INTO `busi_terminal` VALUES (58, '2021-03-09 15:15:46', NULL, 1, 'superAdmin', 100, '192.166.0.61', NULL, NULL, '指挥部', 100, 2);
INSERT INTO `busi_terminal` VALUES (59, '2021-03-09 15:16:40', NULL, 1, 'superAdmin', 100, '192.166.0.22', NULL, NULL, '二会', 100, 1);
INSERT INTO `busi_terminal` VALUES (60, '2021-03-09 15:17:05', NULL, 1, 'superAdmin', 100, '192.166.0.51', NULL, NULL, '控制中兴', 0, 1);
INSERT INTO `busi_terminal` VALUES (61, '2021-03-09 15:17:41', NULL, 1, 'superAdmin', 100, '192.166.0.71', NULL, NULL, '兵团指挥部', 100, 2);
INSERT INTO `busi_terminal` VALUES (62, '2021-03-09 15:18:02', NULL, 1, 'superAdmin', 100, '192.166.0.81', NULL, NULL, '宣传干部学院', 0, 2);
INSERT INTO `busi_terminal` VALUES (66, '2021-03-09 15:20:17', '2021-03-09 15:32:31', 1, 'superAdmin', 103, '192.166.1.12', NULL, '192.166.1.13', '伊犁州', 100, 1);
INSERT INTO `busi_terminal` VALUES (67, '2021-03-09 15:20:31', NULL, 1, 'superAdmin', 103, '192.166.1.21', NULL, '192.166.1.22', '伊宁市', 100, 1);
INSERT INTO `busi_terminal` VALUES (68, '2021-03-09 15:20:50', NULL, 1, 'superAdmin', 103, '192.166.1.31', NULL, '192.166.1.32', '伊宁县', 100, 2);
INSERT INTO `busi_terminal` VALUES (69, '2021-03-09 15:21:05', NULL, 1, 'superAdmin', 103, '192.166.1.41', NULL, '192.166.1.42', '霍尔果斯市', 100, 2);
INSERT INTO `busi_terminal` VALUES (70, '2021-03-09 15:21:21', NULL, 1, 'superAdmin', 103, '192.166.1.51', NULL, '192.166.1.52', '霍城县', 100, 2);
INSERT INTO `busi_terminal` VALUES (71, '2021-03-09 15:21:37', NULL, 1, 'superAdmin', 103, '192.166.1.61', NULL, '192.166.1.62', '巩留县', 100, 2);
INSERT INTO `busi_terminal` VALUES (72, '2021-03-09 15:22:07', NULL, 1, 'superAdmin', 103, '192.166.1.71', NULL, '192.166.1.72', '察布查尔县', 100, 2);
INSERT INTO `busi_terminal` VALUES (73, '2021-03-09 15:22:23', NULL, 1, 'superAdmin', 103, '192.166.1.81', NULL, '192.166.1.82', '昭苏县', 100, 2);
INSERT INTO `busi_terminal` VALUES (74, '2021-03-09 15:22:39', NULL, 1, 'superAdmin', 103, '192.166.1.91', NULL, '192.166.1.92', '特克斯县', 100, 2);
INSERT INTO `busi_terminal` VALUES (75, '2021-03-09 15:23:36', NULL, 1, 'superAdmin', 103, '192.166.1.101', NULL, '192.166.1.102', '奎屯市', 100, 2);
INSERT INTO `busi_terminal` VALUES (76, '2021-03-09 15:23:52', NULL, 1, 'superAdmin', 103, '192.166.1.111', NULL, '192.166.1.112', '新源县', 100, 2);
INSERT INTO `busi_terminal` VALUES (77, '2021-03-09 15:24:10', NULL, 1, 'superAdmin', 103, '192.166.1.121', NULL, '192.166.1.122', '尼勒克县', 100, 2);
INSERT INTO `busi_terminal` VALUES (78, '2021-03-09 15:24:26', NULL, 1, 'superAdmin', 103, '192.166.1.131', NULL, '192.166.1.132', '都拉塔口岸', 100, 2);
INSERT INTO `busi_terminal` VALUES (79, '2021-03-09 15:24:58', '2021-03-09 15:32:38', 1, 'superAdmin', 104, '192.166.2.12', NULL, '192.166.2.13', '克州', 100, 1);
INSERT INTO `busi_terminal` VALUES (80, '2021-03-09 15:25:11', NULL, 1, 'superAdmin', 104, '192.166.2.21', NULL, '192.166.2.22', '阿图什市', 100, 1);
INSERT INTO `busi_terminal` VALUES (81, '2021-03-09 15:25:28', NULL, 1, 'superAdmin', 104, '192.166.2.31', NULL, '192.166.2.32', '阿克陶县', 100, 2);
INSERT INTO `busi_terminal` VALUES (82, '2021-03-09 15:25:41', NULL, 1, 'superAdmin', 104, '192.166.2.41', NULL, '192.166.2.42', '阿合奇县', 100, 2);
INSERT INTO `busi_terminal` VALUES (83, '2021-03-09 15:25:56', NULL, 1, 'superAdmin', 104, '192.166.2.51', NULL, '192.166.2.52', '乌恰县', 100, 2);
INSERT INTO `busi_terminal` VALUES (84, '2021-03-09 15:26:15', NULL, 1, 'superAdmin', 104, '192.166.2.61', NULL, '192.166.2.62', '伊尔克什坦口岸', 100, 2);
INSERT INTO `busi_terminal` VALUES (85, '2021-03-09 15:26:27', NULL, 1, 'superAdmin', 104, '192.166.2.71', NULL, '192.166.2.72', '吐尔尕特口岸', 100, 2);
INSERT INTO `busi_terminal` VALUES (86, '2021-03-09 15:33:17', '2021-03-09 15:34:08', 1, 'superAdmin', 105, '192.166.3.12', NULL, '192.166.3.13', '喀什地区', 100, 2);
INSERT INTO `busi_terminal` VALUES (87, '2021-03-09 15:33:33', NULL, 1, 'superAdmin', 105, '192.166.3.21', NULL, '192.166.3.22', '喀什市', 100, 2);
INSERT INTO `busi_terminal` VALUES (88, '2021-03-09 15:34:22', NULL, 1, 'superAdmin', 105, '192.166.3.31', NULL, '192.166.3.32', '塔什库尔干县', 100, 2);
INSERT INTO `busi_terminal` VALUES (89, '2021-03-09 15:34:41', NULL, 1, 'superAdmin', 105, '192.166.3.41', NULL, '192.166.3.42', '伽师县', 100, 2);
INSERT INTO `busi_terminal` VALUES (90, '2021-03-09 15:34:54', NULL, 1, 'superAdmin', 105, '192.166.3.51', NULL, '192.166.3.52', '疏勒县', 100, 2);
INSERT INTO `busi_terminal` VALUES (91, '2021-03-09 15:35:07', NULL, 1, 'superAdmin', 105, '192.166.3.61', NULL, '192.166.3.62', '疏附县', 100, 2);
INSERT INTO `busi_terminal` VALUES (92, '2021-03-09 15:35:21', NULL, 1, 'superAdmin', 105, '192.166.3.71', NULL, '192.166.3.72', '岳普湖县', 100, 2);
INSERT INTO `busi_terminal` VALUES (93, '2021-03-09 15:35:42', NULL, 1, 'superAdmin', 105, '192.166.3.81', NULL, '192.166.3.82', '莎车县', 100, 2);
INSERT INTO `busi_terminal` VALUES (94, '2021-03-09 15:36:04', NULL, 1, 'superAdmin', 105, '192.166.3.91', NULL, '192.166.3.92', '英吉沙县', 0, 2);
INSERT INTO `busi_terminal` VALUES (95, '2021-03-09 15:36:19', NULL, 1, 'superAdmin', 105, '192.166.3.101', NULL, '192.166.3.102', '巴楚县', 100, 2);
INSERT INTO `busi_terminal` VALUES (96, '2021-03-09 15:36:55', NULL, 1, 'superAdmin', 105, '192.166.3.111', NULL, '192.166.3.112', '麦盖提县', 100, 2);
INSERT INTO `busi_terminal` VALUES (97, '2021-03-09 15:37:23', NULL, 1, 'superAdmin', 105, '192.166.3.121', NULL, '192.166.3.122', '泽普县', 100, 2);
INSERT INTO `busi_terminal` VALUES (98, '2021-03-09 15:37:43', NULL, 1, 'superAdmin', 105, '192.166.3.131', NULL, '192.166.3.132', '叶城县', 100, 2);
INSERT INTO `busi_terminal` VALUES (99, '2021-03-09 15:39:20', NULL, 1, 'superAdmin', 106, '192.166.4.12', NULL, '192.166.4.13', '和田地区', 100, 2);
INSERT INTO `busi_terminal` VALUES (100, '2021-03-09 15:39:37', NULL, 1, 'superAdmin', 106, '192.166.4.21', NULL, '192.166.4.22', '和田县', 100, 2);
INSERT INTO `busi_terminal` VALUES (101, '2021-03-09 15:39:51', NULL, 1, 'superAdmin', 106, '192.166.4.31', NULL, '192.166.4.32', '于田县', 100, 2);
INSERT INTO `busi_terminal` VALUES (102, '2021-03-09 15:40:07', NULL, 1, 'superAdmin', 106, '192.166.4.41', NULL, '192.166.4.42', '策勒县', 100, 2);
INSERT INTO `busi_terminal` VALUES (103, '2021-03-09 15:40:21', NULL, 1, 'superAdmin', 106, '192.166.4.51', NULL, '192.166.4.52', '皮山县', 100, 2);
INSERT INTO `busi_terminal` VALUES (104, '2021-03-09 15:40:47', NULL, 1, 'superAdmin', 106, '192.166.4.61', NULL, '192.166.4.62', '民丰县', 100, 2);
INSERT INTO `busi_terminal` VALUES (105, '2021-03-09 15:41:29', NULL, 1, 'superAdmin', 106, '192.166.4.71', NULL, '192.166.4.72', '和田市', 100, 2);
INSERT INTO `busi_terminal` VALUES (106, '2021-03-09 15:42:58', NULL, 1, 'superAdmin', 106, '192.166.4.81', NULL, '192.166.4.82', '墨玉县', 100, 2);
INSERT INTO `busi_terminal` VALUES (107, '2021-03-09 15:47:27', NULL, 1, 'superAdmin', 106, '192.166.4.91', NULL, '192.166.4.92', '洛浦县', 100, 2);
INSERT INTO `busi_terminal` VALUES (108, '2021-03-09 15:47:56', NULL, 1, 'superAdmin', 107, '192.166.5.12', NULL, '192.166.5.13', '阿克苏地区', 100, 2);
INSERT INTO `busi_terminal` VALUES (109, '2021-03-09 15:48:11', NULL, 1, 'superAdmin', 107, '192.166.5.21', NULL, '192.166.5.22', '阿克苏市', 100, 2);
INSERT INTO `busi_terminal` VALUES (110, '2021-03-09 15:48:32', NULL, 1, 'superAdmin', 107, '192.166.5.31', NULL, '192.166.5.32', '温宿县', 100, 2);
INSERT INTO `busi_terminal` VALUES (111, '2021-03-09 15:49:25', NULL, 1, 'superAdmin', 107, '192.166.5.51', NULL, '192.166.5.52', '新和县', 100, 2);
INSERT INTO `busi_terminal` VALUES (112, '2021-03-09 15:51:08', NULL, 1, 'superAdmin', 107, '192.166.5.61', NULL, '192.166.5.62', '沙雅县', 100, 2);
INSERT INTO `busi_terminal` VALUES (113, '2021-03-09 15:51:28', '2021-03-11 18:44:58', 1, 'superAdmin', 107, '192.166.5.71', NULL, '192.166.5.72', '库车县', 100, 2);
INSERT INTO `busi_terminal` VALUES (114, '2021-03-09 15:51:43', NULL, 1, 'superAdmin', 107, '192.166.5.81', NULL, '192.166.5.82', '乌什县', 100, 2);
INSERT INTO `busi_terminal` VALUES (115, '2021-03-09 15:52:14', NULL, 1, 'superAdmin', 107, '192.166.5.41', NULL, '192.166.5.42', '拜城县', 100, 2);
INSERT INTO `busi_terminal` VALUES (116, '2021-03-09 15:52:37', NULL, 1, 'superAdmin', 107, '192.166.5.91', NULL, '192.166.5.92', '柯坪县', 100, 2);
INSERT INTO `busi_terminal` VALUES (117, '2021-03-09 15:52:50', '2021-03-11 18:45:06', 1, 'superAdmin', 107, '192.166.5.101', NULL, '192.166.5.102', '阿瓦提县', 100, 2);
INSERT INTO `busi_terminal` VALUES (118, '2021-03-09 15:53:30', NULL, 1, 'superAdmin', 200, '192.166.6.12', NULL, '192.166.6.13', '巴州地区', 100, 2);
INSERT INTO `busi_terminal` VALUES (119, '2021-03-09 15:54:13', NULL, 1, 'superAdmin', 200, '192.166.6.21', NULL, '192.166.6.22', '库尔勒市', 100, 2);
INSERT INTO `busi_terminal` VALUES (120, '2021-03-09 15:54:33', '2021-03-09 15:55:07', 1, 'superAdmin', 200, '192.166.6.31', NULL, '192.166.6.32', '轮台县', 100, 2);
INSERT INTO `busi_terminal` VALUES (121, '2021-03-09 15:56:09', NULL, 1, 'superAdmin', 200, '192.166.6.41', NULL, '192.166.6.42', '且末县', 100, 2);
INSERT INTO `busi_terminal` VALUES (122, '2021-03-09 15:56:30', NULL, 1, 'superAdmin', 200, '192.166.6.51', NULL, '192.166.6.52', '若羌县', 100, 2);
INSERT INTO `busi_terminal` VALUES (123, '2021-03-09 15:56:50', NULL, 1, 'superAdmin', 200, '192.166.6.61', NULL, '192.166.6.62', '尉犁县', 100, 2);
INSERT INTO `busi_terminal` VALUES (124, '2021-03-09 15:58:35', NULL, 1, 'superAdmin', 200, '192.166.6.71', NULL, '192.166.6.72', '焉耆县', 0, 2);
INSERT INTO `busi_terminal` VALUES (125, '2021-03-09 15:58:51', NULL, 1, 'superAdmin', 200, '192.166.6.81', NULL, '192.166.6.82', '博湖县', 100, 2);
INSERT INTO `busi_terminal` VALUES (126, '2021-03-09 15:59:27', NULL, 1, 'superAdmin', 200, '192.166.6.91', NULL, '192.166.6.92', '和静县', 100, 2);
INSERT INTO `busi_terminal` VALUES (127, '2021-03-09 15:59:43', NULL, 1, 'superAdmin', 200, '192.166.6.101', NULL, '192.166.6.102', '和硕县', 100, 2);
INSERT INTO `busi_terminal` VALUES (128, '2021-03-09 15:59:57', NULL, 1, 'superAdmin', 200, '192.166.6.111', NULL, '192.166.6.112', '开发区', 100, 2);
INSERT INTO `busi_terminal` VALUES (129, '2021-03-09 16:00:26', NULL, 1, 'superAdmin', 201, '192.166.7.12', NULL, '192.166.7.13', '吐鲁番市', 100, 2);
INSERT INTO `busi_terminal` VALUES (130, '2021-03-09 16:00:44', NULL, 1, 'superAdmin', 201, '192.166.7.21', NULL, '192.166.7.22', '高昌区', 100, 2);
INSERT INTO `busi_terminal` VALUES (131, '2021-03-09 16:01:02', NULL, 1, 'superAdmin', 201, '192.166.7.31', NULL, '192.166.7.32', '示范区', 100, 2);
INSERT INTO `busi_terminal` VALUES (132, '2021-03-09 16:01:25', NULL, 1, 'superAdmin', 201, '192.166.7.41', NULL, '192.166.7.42', '托克逊县', 100, 2);
INSERT INTO `busi_terminal` VALUES (133, '2021-03-09 16:01:39', NULL, 1, 'superAdmin', 201, '192.166.7.51', NULL, '192.166.7.52', '鄯善县', 100, 2);
INSERT INTO `busi_terminal` VALUES (134, '2021-03-09 16:02:08', NULL, 1, 'superAdmin', 202, '192.166.8.12', NULL, '192.166.8.13', '哈密市', 100, 1);
INSERT INTO `busi_terminal` VALUES (135, '2021-03-09 16:02:21', NULL, 1, 'superAdmin', 202, '192.166.8.21', NULL, '192.166.8.22', '伊州区', 100, 2);
INSERT INTO `busi_terminal` VALUES (136, '2021-03-09 16:02:38', NULL, 1, 'superAdmin', 202, '192.166.8.31', NULL, '192.166.8.32', '巴里坤县', 100, 2);
INSERT INTO `busi_terminal` VALUES (137, '2021-03-09 16:02:55', NULL, 1, 'superAdmin', 202, '192.166.8.41', NULL, '192.166.8.42', '伊吾县', 100, 2);
INSERT INTO `busi_terminal` VALUES (138, '2021-03-09 16:03:24', NULL, 1, 'superAdmin', 203, '192.166.9.12', NULL, '192.166.9.13', '乌鲁木齐市', 100, 2);
INSERT INTO `busi_terminal` VALUES (139, '2021-03-09 16:03:40', NULL, 1, 'superAdmin', 203, '192.166.9.21', NULL, '192.166.9.22', '乌鲁木齐县', 100, 2);
INSERT INTO `busi_terminal` VALUES (140, '2021-03-09 16:03:57', NULL, 1, 'superAdmin', 203, '192.166.9.31', NULL, '192.166.9.32', '水磨沟区', 100, 2);
INSERT INTO `busi_terminal` VALUES (141, '2021-03-09 16:04:11', NULL, 1, 'superAdmin', 203, '192.166.9.41', NULL, '192.166.9.42', '天山区', 100, 2);
INSERT INTO `busi_terminal` VALUES (142, '2021-03-09 16:04:29', NULL, 1, 'superAdmin', 203, '192.166.9.51', NULL, '192.166.9.52', '沙依巴格区', 100, 2);
INSERT INTO `busi_terminal` VALUES (143, '2021-03-09 16:04:44', '2021-03-09 16:05:04', 1, 'superAdmin', 203, '192.166.9.61', NULL, '192.166.9.62', '头屯河区', 100, 2);
INSERT INTO `busi_terminal` VALUES (144, '2021-03-09 16:05:19', NULL, 1, 'superAdmin', 203, '192.166.9.71', NULL, '192.166.9.72', '新市区', 100, 2);
INSERT INTO `busi_terminal` VALUES (145, '2021-03-09 16:05:33', NULL, 1, 'superAdmin', 203, '192.166.9.81', NULL, '192.166.9.82', '米东区', 100, 2);
INSERT INTO `busi_terminal` VALUES (146, '2021-03-09 16:06:01', '2021-03-09 16:06:15', 1, 'superAdmin', 203, '192.166.9.91', NULL, '192.166.9.92', '甘泉堡区', 100, 2);
INSERT INTO `busi_terminal` VALUES (147, '2021-03-09 16:06:40', NULL, 1, 'superAdmin', 203, '192.166.9.101', NULL, '192.166.9.102', '达板城区', 100, 2);
INSERT INTO `busi_terminal` VALUES (148, '2021-03-09 16:07:12', NULL, 1, 'superAdmin', 204, '192.166.10.21', NULL, '192.166.10.22', '昌吉市', 100, 2);
INSERT INTO `busi_terminal` VALUES (149, '2021-03-09 16:07:36', NULL, 1, 'superAdmin', 204, '192.166.10.31', NULL, '192.166.10.32', '阜康市', 100, 2);
INSERT INTO `busi_terminal` VALUES (150, '2021-03-09 16:08:16', NULL, 1, 'superAdmin', 204, '192.166.10.41', NULL, '192.166.10.42', '呼图壁县', 100, 2);
INSERT INTO `busi_terminal` VALUES (151, '2021-03-09 16:08:36', NULL, 1, 'superAdmin', 204, '192.166.10.51', NULL, '192.166.10.52', '玛纳斯县', 100, 2);
INSERT INTO `busi_terminal` VALUES (152, '2021-03-09 16:08:51', NULL, 1, 'superAdmin', 204, '192.166.10.61', NULL, '192.166.10.62', '奇台县', 100, 2);
INSERT INTO `busi_terminal` VALUES (153, '2021-03-09 16:09:12', NULL, 1, 'superAdmin', 204, '192.166.10.71', NULL, '192.166.10.72', '吉木萨尔县', 100, 2);
INSERT INTO `busi_terminal` VALUES (154, '2021-03-09 16:09:35', NULL, 1, 'superAdmin', 204, '192.166.10.81', NULL, '192.166.10.82', '木垒县', 100, 2);
INSERT INTO `busi_terminal` VALUES (155, '2021-03-09 16:09:56', NULL, 1, 'superAdmin', 204, '192.166.10.91', NULL, '192.166.10.92', '高新区', 100, 2);
INSERT INTO `busi_terminal` VALUES (156, '2021-03-09 16:10:15', NULL, 1, 'superAdmin', 204, '192.166.10.101', NULL, '192.166.10.102', '准东开发区', 100, 2);
INSERT INTO `busi_terminal` VALUES (157, '2021-03-09 16:11:06', NULL, 1, 'superAdmin', 205, '192.166.11.21', NULL, '192.166.11.22', '塔城市', 0, 2);
INSERT INTO `busi_terminal` VALUES (158, '2021-03-09 16:11:23', NULL, 1, 'superAdmin', 205, '192.166.11.31', NULL, '192.166.11.32', '乌苏市', 100, 2);
INSERT INTO `busi_terminal` VALUES (159, '2021-03-09 16:11:40', NULL, 1, 'superAdmin', 205, '192.166.11.41', NULL, '192.166.11.42', '额敏县', 150, 2);
INSERT INTO `busi_terminal` VALUES (160, '2021-03-09 16:12:00', NULL, 1, 'superAdmin', 205, '192.166.11.51', NULL, '192.166.11.52', '裕民县', 100, 2);
INSERT INTO `busi_terminal` VALUES (161, '2021-03-09 16:12:14', NULL, 1, 'superAdmin', 205, '192.166.11.61', NULL, '192.166.11.62', '托里县', 100, 2);
INSERT INTO `busi_terminal` VALUES (162, '2021-03-09 16:12:32', NULL, 1, 'superAdmin', 205, '192.166.11.71', NULL, '192.166.11.72', '沙湾县', 100, 2);
INSERT INTO `busi_terminal` VALUES (163, '2021-03-09 16:12:44', NULL, 1, 'superAdmin', 205, '192.166.11.81', NULL, '192.166.11.82', '和布克赛尔县', 100, 2);
INSERT INTO `busi_terminal` VALUES (164, '2021-03-09 16:14:33', NULL, 1, 'superAdmin', 206, '192.166.12.21', NULL, '192.166.12.22', '阿勒泰市', 100, 2);
INSERT INTO `busi_terminal` VALUES (165, '2021-03-09 16:14:48', NULL, 1, 'superAdmin', 206, '192.166.12.31', NULL, '192.166.12.32', '哈巴河县', 100, 2);
INSERT INTO `busi_terminal` VALUES (166, '2021-03-09 16:15:08', NULL, 1, 'superAdmin', 206, '192.166.12.41', NULL, '192.166.12.42', '布尔津县', 100, 2);
INSERT INTO `busi_terminal` VALUES (167, '2021-03-09 16:15:31', NULL, 1, 'superAdmin', 206, '192.166.12.51', NULL, '192.166.12.52', '吉木乃县', 100, 2);
INSERT INTO `busi_terminal` VALUES (168, '2021-03-09 16:15:49', NULL, 1, 'superAdmin', 206, '192.166.12.61', NULL, '192.166.12.62', '富蕴县', 100, 2);
INSERT INTO `busi_terminal` VALUES (169, '2021-03-09 16:16:02', NULL, 1, 'superAdmin', 206, '192.166.12.71', NULL, '192.166.12.72', '福海县', 100, 2);
INSERT INTO `busi_terminal` VALUES (170, '2021-03-09 16:16:16', NULL, 1, 'superAdmin', 206, '192.166.12.81', NULL, '192.166.12.82', '青河县', 100, 2);
INSERT INTO `busi_terminal` VALUES (171, '2021-03-09 16:16:35', NULL, 1, 'superAdmin', 206, '192.166.12.91', NULL, '192.166.12.92', '喀纳斯管委会', 100, 2);
INSERT INTO `busi_terminal` VALUES (172, '2021-03-09 16:17:12', NULL, 1, 'superAdmin', 207, '192.166.13.21', NULL, '192.166.13.22', '博乐市', 100, 2);
INSERT INTO `busi_terminal` VALUES (173, '2021-03-09 16:17:27', NULL, 1, 'superAdmin', 207, '192.166.13.31', NULL, '192.166.13.32', '温泉县', 100, 2);
INSERT INTO `busi_terminal` VALUES (174, '2021-03-09 16:17:41', NULL, 1, 'superAdmin', 207, '192.166.13.41', NULL, '192.166.13.42', '精河县', 100, 2);
INSERT INTO `busi_terminal` VALUES (175, '2021-03-09 16:17:58', NULL, 1, 'superAdmin', 207, '192.166.13.51', NULL, '192.166.13.52', '阿拉山口口岸', 100, 1);
INSERT INTO `busi_terminal` VALUES (176, '2021-03-09 16:21:43', NULL, 1, 'superAdmin', 207, '192.166.13.61', NULL, '192.166.13.62', '赛里木湖管委会', 100, 2);
INSERT INTO `busi_terminal` VALUES (177, '2021-03-09 16:36:31', NULL, 1, 'superAdmin', 208, '192.166.14.31', NULL, '192.166.14.32', '独山子区', 100, 2);
INSERT INTO `busi_terminal` VALUES (178, '2021-03-09 16:36:45', NULL, 1, 'superAdmin', 208, '192.166.14.41', NULL, '192.166.14.42', '白碱滩区', 100, 2);
INSERT INTO `busi_terminal` VALUES (179, '2021-03-09 16:37:05', '2021-03-09 16:37:21', 1, 'superAdmin', 208, '192.166.14.51', NULL, '192.166.14.52', '乌尔禾区', 100, 2);
INSERT INTO `busi_terminal` VALUES (180, '2021-03-24 15:57:11', '2021-03-26 17:07:14', 1, 'superAdmin', 212, '172.16.101.222', NULL, NULL, '172.16.101.222', 0, 2);
INSERT INTO `busi_terminal` VALUES (181, '2021-03-24 15:58:31', '2021-03-26 17:13:06', 1, 'superAdmin', 213, '172.16.101.223', NULL, NULL, '172.16.101.223', 0, 1);
INSERT INTO `busi_terminal` VALUES (182, '2021-03-24 15:58:49', '2021-03-26 17:10:04', 1, 'superAdmin', 213, '172.16.101.212', NULL, NULL, '172.16.101.212', 0, 2);
INSERT INTO `busi_terminal` VALUES (183, '2021-03-24 15:59:16', '2021-03-26 17:27:18', 1, 'superAdmin', 214, '172.16.101.208', NULL, NULL, '172.16.101.208', 0, 1);
INSERT INTO `busi_terminal` VALUES (184, '2021-03-24 15:59:28', NULL, 1, 'superAdmin', 214, '192.166.2.21', NULL, NULL, '192.166.2.21', 0, 1);
INSERT INTO `busi_terminal` VALUES (185, '2021-03-29 16:27:51', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15000', NULL, '15000@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (186, '2021-03-29 16:35:11', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15001', NULL, '15001@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (187, '2021-03-29 16:35:25', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15002', NULL, '15002@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (189, '2021-03-29 16:35:59', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15003', NULL, '15003@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (190, '2021-03-29 16:36:10', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15004', NULL, '15004@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (191, '2021-03-29 16:36:18', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15005', NULL, '15005@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (192, '2021-03-29 16:36:28', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15006', NULL, '15006@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (193, '2021-03-29 16:36:37', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15007', NULL, '15007@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (194, '2021-03-29 16:36:46', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15008', NULL, '15008@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (195, '2021-03-29 16:36:56', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15009', NULL, '15009@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (196, '2021-03-29 16:38:12', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15010', NULL, '15010@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (197, '2021-03-29 16:38:35', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15011', NULL, '15011@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (198, '2021-03-29 16:38:42', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15012', NULL, '15012@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (199, '2021-03-29 16:38:55', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15013', NULL, '15013@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (200, '2021-03-29 16:39:35', NULL, 1, 'superAdmin', 213, '172.16.100.190', '15014', NULL, '15014@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (201, '2021-03-29 16:40:09', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16001', NULL, '16001@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (202, '2021-03-29 16:40:20', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16002', NULL, '16002@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (203, '2021-03-29 16:40:29', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16003', NULL, '16003@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (204, '2021-03-29 16:40:50', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16004', NULL, '16004@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (205, '2021-03-29 16:41:54', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16000', NULL, '16000@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (206, '2021-03-29 16:42:04', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16005', NULL, '16005@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (207, '2021-03-29 16:42:13', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16006', NULL, '16006@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (208, '2021-03-29 16:42:26', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16007', NULL, '16007@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (209, '2021-03-29 16:42:36', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16008', NULL, '16008@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (210, '2021-03-29 16:42:55', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16009', NULL, '16009@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (211, '2021-03-29 16:43:07', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16010', NULL, '16010@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (212, '2021-03-29 16:43:19', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16011', NULL, '16011@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (213, '2021-03-29 16:43:29', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16012', NULL, '16012@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (214, '2021-03-29 16:43:50', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16013', NULL, '16013@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (215, '2021-03-29 16:44:01', NULL, 1, 'superAdmin', 214, '172.16.100.191', '16014', NULL, '16014@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (216, '2021-03-29 18:43:13', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17000', NULL, '17000@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (217, '2021-03-29 18:43:42', '2021-03-29 18:43:52', 1, 'superAdmin', 215, '172.16.100.191', '17001', NULL, '17001@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (218, '2021-03-29 18:44:01', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17002', NULL, '17002@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (219, '2021-03-29 18:44:10', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17003', NULL, '17003@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (220, '2021-03-29 18:44:18', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17004', NULL, '17004@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (221, '2021-03-29 18:44:25', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17005', NULL, '17005@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (222, '2021-03-29 18:44:35', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17006', NULL, '17006@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (223, '2021-03-29 18:44:46', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17007', NULL, '17007@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (224, '2021-03-29 18:44:54', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17008', NULL, '17008@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (225, '2021-03-29 18:45:02', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17009', NULL, '17009@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (226, '2021-03-29 18:45:10', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17010', NULL, '17010@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (227, '2021-03-29 18:45:21', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17011', NULL, '17011@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (228, '2021-03-29 18:45:29', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17012', NULL, '17012@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (229, '2021-03-29 18:45:42', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17013', NULL, '17013@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (230, '2021-03-29 18:45:54', NULL, 1, 'superAdmin', 215, '172.16.100.191', '17014', NULL, '17014@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (231, '2021-03-29 18:49:01', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18000', NULL, '18000@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (232, '2021-03-29 18:49:14', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18001', NULL, '18001@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (233, '2021-03-29 18:49:23', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18002', NULL, '18002@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (234, '2021-03-29 18:58:19', '2021-03-30 09:47:29', 1, 'superAdmin', 216, '172.16.100.191', '18003', NULL, '18003@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (235, '2021-03-29 18:58:50', '2021-03-29 18:59:18', 1, 'superAdmin', 216, '172.16.100.191', '18004', NULL, '18004@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (236, '2021-03-29 18:59:00', '2021-03-29 18:59:23', 1, 'superAdmin', 216, '172.16.100.191', '18005', NULL, '18005@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (237, '2021-03-29 18:59:12', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18006', NULL, '18006@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (238, '2021-03-29 18:59:33', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18007', NULL, '18007@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (240, '2021-03-29 18:59:56', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18008', NULL, '18008@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (241, '2021-03-29 19:00:05', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18009', NULL, '18009@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (242, '2021-03-29 19:00:14', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18010', NULL, '18010@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (243, '2021-03-29 19:00:22', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18011', NULL, '18011@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (244, '2021-03-29 19:00:33', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18012', NULL, '18012@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (245, '2021-03-29 19:00:42', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18013', NULL, '18013@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (246, '2021-03-29 19:00:50', NULL, 1, 'superAdmin', 216, '172.16.100.191', '18014', NULL, '18014@172.16.100.191', 0, 1);
INSERT INTO `busi_terminal` VALUES (247, '2021-03-29 19:01:11', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19000', NULL, '19000@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (248, '2021-03-29 19:01:18', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19001', NULL, '19001@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (249, '2021-03-29 19:01:43', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19002', NULL, '19002@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (250, '2021-03-29 19:01:49', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19003', NULL, '19003@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (251, '2021-03-29 19:01:58', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19004', NULL, '19004@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (252, '2021-03-29 19:02:07', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19005', NULL, '19005@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (253, '2021-03-29 19:02:14', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19006', NULL, '19006@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (255, '2021-03-29 19:03:02', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19007', NULL, '19007@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (256, '2021-03-29 19:03:09', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19008', NULL, '19008@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (257, '2021-03-29 19:03:17', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19009', NULL, '19009@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (258, '2021-03-29 19:03:24', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19010', NULL, '19010@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (259, '2021-03-29 19:03:34', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19011', NULL, '19011@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (260, '2021-03-29 19:03:42', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19012', NULL, '19012@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (261, '2021-03-29 19:03:52', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19013', NULL, '19013@172.16.100.190', 0, 1);
INSERT INTO `busi_terminal` VALUES (262, '2021-03-29 19:04:03', NULL, 1, 'superAdmin', 217, '172.16.100.190', '19014', NULL, '19014@172.16.100.190', 0, 1);

-- ----------------------------
-- Table structure for busi_terminal_registration_server
-- ----------------------------
DROP TABLE IF EXISTS `busi_terminal_registration_server`;
CREATE TABLE `busi_terminal_registration_server`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `server_type` int(11) NULL DEFAULT NULL COMMENT '注册服务器类型（1:FSBC, 2CUCM）',
  `server_ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册服务器ip地州',
  `server_port` int(11) NULL DEFAULT NULL COMMENT '注册服务器端口',
  `server_path` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册服务器访问路径',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册服务器用户名',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册服务器密码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '终端注册服务器' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 201 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1599 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

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
INSERT INTO `gen_table_column` VALUES (1497, '181', 'uuid', '终端唯一标识ID', 'varchar(128)', 'String', 'uuid', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, '', '2021-02-03 17:09:57', '', NULL);
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
INSERT INTO `sys_dept` VALUES (1, 0, '0', '部门顶级节点', 0, 'superAdmin', '15888888888', NULL, '0', '0', 'superAdmin', '2021-01-18 10:39:18', 'superAdmin', '2021-03-29 18:22:38');
INSERT INTO `sys_dept` VALUES (100, 1, '0,1', '新疆维吾尔自治区', 0, 'admin', '15888888888', NULL, '0', '0', 'admin', '2021-01-18 10:39:18', 'superAdmin', '2021-03-29 18:22:38');
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
) ENGINE = InnoDB AUTO_INCREMENT = 579 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

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
INSERT INTO `sys_notice` VALUES (1, '温馨提醒：2018-07-01 天堂云会控系统新版本发布啦', '2', 0xE696B0E78988E69CACE58685E5AEB9, '0', 'admin', '2021-01-18 10:39:23', '', NULL, '管理员');
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
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (5345, '活跃会议室信息，用于存放活跃的会议室', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.startByTemplate()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/startByTemplate/58', '172.16.101.224', '内网IP', '58', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 15:21:12');
INSERT INTO `sys_oper_log` VALUES (5346, '挂断会议', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.endConference()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/endConference/B0B347B94EA8701564635671AB626E2E/1', '172.16.101.224', '内网IP', 'B0B347B94EA8701564635671AB626E2E 1', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 15:21:29');
INSERT INTO `sys_oper_log` VALUES (5347, '活跃会议室信息，用于存放活跃的会议室', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.startByTemplate()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/startByTemplate/58', '172.16.101.224', '内网IP', '58', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 15:21:57');
INSERT INTO `sys_oper_log` VALUES (5348, '挂断会议', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.endConference()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/endConference/B0B347B94EA8701564635671AB626E2E/1', '172.16.101.224', '内网IP', 'B0B347B94EA8701564635671AB626E2E 1', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 15:22:12');
INSERT INTO `sys_oper_log` VALUES (5349, '活跃会议室信息，用于存放活跃的会议室', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.startByTemplate()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/startByTemplate/58', '172.16.101.224', '内网IP', '58', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 16:07:35');
INSERT INTO `sys_oper_log` VALUES (5350, '挂断会议', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.endConference()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/endConference/B0B347B94EA8701564635671AB626E2E/1', '172.16.101.224', '内网IP', 'B0B347B94EA8701564635671AB626E2E 1', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 16:07:38');
INSERT INTO `sys_oper_log` VALUES (5351, '活跃会议室信息，用于存放活跃的会议室', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.startByTemplate()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/startByTemplate/58', '172.16.101.224', '内网IP', '58', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 16:12:37');
INSERT INTO `sys_oper_log` VALUES (5352, '挂断会议', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.endConference()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/endConference/B0B347B94EA8701564635671AB626E2E/1', '172.16.101.224', '内网IP', 'B0B347B94EA8701564635671AB626E2E 1', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 16:12:41');
INSERT INTO `sys_oper_log` VALUES (5353, '活跃会议室信息，用于存放活跃的会议室', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.startByTemplate()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/startByTemplate/58', '172.16.101.224', '内网IP', '58', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 16:13:05');
INSERT INTO `sys_oper_log` VALUES (5354, '挂断会议', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.endConference()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/endConference/B0B347B94EA8701564635671AB626E2E/1', '172.16.101.224', '内网IP', 'B0B347B94EA8701564635671AB626E2E 1', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 16:13:23');
INSERT INTO `sys_oper_log` VALUES (5355, '代码生成', 2, 'com.paradisecloud.generator.controller.GenController.synchDb()', 'GET', 1, 'superAdmin', NULL, '/fcm/tool/gen/synchDb/busi_template_conference', '172.16.100.170', '内网IP', '{tableName=busi_template_conference}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 16:28:52');
INSERT INTO `sys_oper_log` VALUES (5356, '会议模板', 3, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.remove()', 'DELETE', 1, 'superAdmin', NULL, '/fcm/busi/templateConference/59', '172.16.101.224', '内网IP', '{id=59}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 16:43:44');
INSERT INTO `sys_oper_log` VALUES (5357, '会议模板', 3, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.remove()', 'DELETE', 1, 'superAdmin', NULL, '/fcm/busi/templateConference/58', '172.16.101.224', '内网IP', '{id=58}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 16:43:47');
INSERT INTO `sys_oper_log` VALUES (5358, '菜单管理', 3, 'com.paradisecloud.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'superAdmin', NULL, '/fcm/system/menu/2021', '172.16.101.224', '内网IP', '{menuId=2021}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 17:03:35');
INSERT INTO `sys_oper_log` VALUES (5359, '轮询方案', 1, 'com.paradisecloud.fcm.web.controller.business.BusiTemplatePollingSchemeController.add()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/templatePollingScheme', '172.16.101.224', '内网IP', '{\"pollingScheme\":{\"schemeName\":\"sfds\",\"pollingInterval\":10,\"pollingStateFirst\":1,\"templateConferenceId\":60,\"isBroadcast\":2,\"isDisplaySelf\":1,\"isFill\":1,\"layout\":\"speakerOnly\"},\"pollingDepts\":[{\"deptId\":100,\"weight\":10},{\"deptId\":103,\"weight\":9},{\"deptId\":205,\"weight\":8},{\"deptId\":206,\"weight\":7},{\"deptId\":208,\"weight\":6},{\"deptId\":207,\"weight\":5},{\"deptId\":204,\"weight\":4},{\"deptId\":203,\"weight\":3},{\"deptId\":202,\"weight\":2},{\"deptId\":201,\"weight\":1}],\"pollingParticipants\":[{\"remoteParty\":\"192.166.0.22\",\"isCascadeMain\":2,\"weight\":4,\"terminalId\":59},{\"remoteParty\":\"192.166.0.51\",\"isCascadeMain\":2,\"weight\":3,\"terminalId\":60},{\"remoteParty\":\"192.166.0.71\",\"isCascadeMain\":2,\"weight\":2,\"terminalId\":61},{\"remoteParty\":\"192.166.0.81\",\"isCascadeMain\":2,\"weight\":1,\"terminalId\":62},{\"remoteParty\":\"192.166.1.12\",\"isCascadeMain\":1,\"weight\":13,\"terminalId\":66},{\"remoteParty\":\"192.166.1.21\",\"isCascadeMain\":2,\"weight\":12,\"terminalId\":67},{\"remoteParty\":\"192.166.1.31\",\"isCascadeMain\":2,\"weight\":11,\"terminalId\":68},{\"remoteParty\":\"192.166.1.41\",\"isCascadeMain\":2,\"weight\":10,\"terminalId\":69},{\"remoteParty\":\"192.166.1.51\",\"isCascadeMain\":2,\"weight\":9,\"terminalId\":70},{\"remoteParty\":\"192.166.1.61\",\"isCascadeMain\":2,\"weight\":8,\"terminalId\":71},{\"remoteParty\":\"192.166.1.71\",\"isCascadeMain\":2,\"weight\":7,\"terminalId\":72},{\"remoteParty\":\"192.166.1.81\",\"isCascadeMain\":2,\"weight\":6,\"terminalId\":73},{\"remoteParty\":\"192.166.1.91\",\"isCascadeMain\":2,\"weight\":5,\"terminalId\":74},{\"remoteParty\":\"192.166.1.101\",\"isCascadeMain\":2,\"weight\":4,\"terminalId\":75},{\"remoteParty\":\"192.166.1.111\",\"isCascadeMain\":2,\"weight\":3,\"terminalId\":76},{\"remoteParty\":\"192.166.1.121\",\"isCascadeMain\":2,\"weight\":2,\"terminalId\":77},{\"remoteParty\":\"192.166.1.131\",\"isCascadeMain\":2,\"weight\":1,\"terminalId\":78},{\"remoteParty\":\"192.166.11.12\",\"isCascadeMain\":1,\"weight\":8,\"terminalId\":47},{\"remoteParty\":\"192.166.11.21\",\"isCascadeMain\":2,\"weight\":7,\"terminalId\":157},{\"remoteParty\":\"192.166.11.31\",\"isCascadeMain\":2,\"weight\":6,\"termina', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 17:05:50');
INSERT INTO `sys_oper_log` VALUES (5360, '轮询方案', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplatePollingSchemeController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/templatePollingScheme/edit', '172.16.101.224', '内网IP', '[{\"weight\":1,\"updateTime\":1618304750791,\"params\":{},\"id\":55}]', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 17:05:50');
INSERT INTO `sys_oper_log` VALUES (5361, '轮询方案', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplatePollingSchemeController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/templatePollingScheme/55', '172.16.101.224', '内网IP', '{\"pollingScheme\":{\"id\":55,\"schemeName\":\"sfds\",\"pollingInterval\":5,\"pollingStateFirst\":1,\"templateConferenceId\":60,\"weight\":1,\"isBroadcast\":2,\"isDisplaySelf\":1,\"isFill\":1,\"layout\":\"speakerOnly\"},\"pollingDepts\":[{\"deptId\":100,\"weight\":10},{\"deptId\":103,\"weight\":9},{\"deptId\":205,\"weight\":8},{\"deptId\":206,\"weight\":7},{\"deptId\":208,\"weight\":6},{\"deptId\":207,\"weight\":5},{\"deptId\":204,\"weight\":4},{\"deptId\":203,\"weight\":3},{\"deptId\":202,\"weight\":2},{\"deptId\":201,\"weight\":1}],\"pollingParticipants\":[{\"remoteParty\":\"192.166.0.22\",\"isCascadeMain\":2,\"weight\":4,\"terminalId\":59},{\"remoteParty\":\"192.166.0.51\",\"isCascadeMain\":2,\"weight\":3,\"terminalId\":60},{\"remoteParty\":\"192.166.0.71\",\"isCascadeMain\":2,\"weight\":2,\"terminalId\":61},{\"remoteParty\":\"192.166.0.81\",\"isCascadeMain\":2,\"weight\":1,\"terminalId\":62},{\"remoteParty\":\"192.166.1.12\",\"isCascadeMain\":1,\"weight\":13,\"terminalId\":66},{\"remoteParty\":\"192.166.1.21\",\"isCascadeMain\":2,\"weight\":12,\"terminalId\":67},{\"remoteParty\":\"192.166.1.31\",\"isCascadeMain\":2,\"weight\":11,\"terminalId\":68},{\"remoteParty\":\"192.166.1.41\",\"isCascadeMain\":2,\"weight\":10,\"terminalId\":69},{\"remoteParty\":\"192.166.1.51\",\"isCascadeMain\":2,\"weight\":9,\"terminalId\":70},{\"remoteParty\":\"192.166.1.61\",\"isCascadeMain\":2,\"weight\":8,\"terminalId\":71},{\"remoteParty\":\"192.166.1.71\",\"isCascadeMain\":2,\"weight\":7,\"terminalId\":72},{\"remoteParty\":\"192.166.1.81\",\"isCascadeMain\":2,\"weight\":6,\"terminalId\":73},{\"remoteParty\":\"192.166.1.91\",\"isCascadeMain\":2,\"weight\":5,\"terminalId\":74},{\"remoteParty\":\"192.166.1.101\",\"isCascadeMain\":2,\"weight\":4,\"terminalId\":75},{\"remoteParty\":\"192.166.1.111\",\"isCascadeMain\":2,\"weight\":3,\"terminalId\":76},{\"remoteParty\":\"192.166.1.121\",\"isCascadeMain\":2,\"weight\":2,\"terminalId\":77},{\"remoteParty\":\"192.166.1.131\",\"isCascadeMain\":2,\"weight\":1,\"terminalId\":78},{\"remoteParty\":\"192.166.11.12\",\"isCascadeMain\":1,\"weight\":8,\"terminalId\":47},{\"remoteParty\":\"192.166.11.21\",\"isCascadeMain\":2,\"weight\":7,\"terminalId\":157},{\"remoteParty\":\"192.166.11.31\",\"isCascadeMain\":2,\"', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 17:06:00');
INSERT INTO `sys_oper_log` VALUES (5362, '活跃会议室信息，用于存放活跃的会议室', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.startByTemplate()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/startByTemplate/60', '172.16.101.224', '内网IP', '60', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:04:41');
INSERT INTO `sys_oper_log` VALUES (5363, '挂断会议', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.endConference()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/endConference/B0B347B94EA8701564635671AB626E2E/1', '172.16.101.224', '内网IP', 'B0B347B94EA8701564635671AB626E2E 1', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:05:09');
INSERT INTO `sys_oper_log` VALUES (5364, '活跃会议室信息，用于存放活跃的会议室', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.startByTemplate()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/startByTemplate/39', '172.16.101.224', '内网IP', '39', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:09:32');
INSERT INTO `sys_oper_log` VALUES (5365, '挂断会议', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.endConference()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/endConference/37257A471FE07999856AC0773C2FF745/1', '172.16.101.224', '内网IP', '37257A471FE07999856AC0773C2FF745 1', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:09:49');
INSERT INTO `sys_oper_log` VALUES (5366, '活跃会议室信息，用于存放活跃的会议室', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.startByTemplate()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/startByTemplate/39', '172.16.101.224', '内网IP', '39', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:09:59');
INSERT INTO `sys_oper_log` VALUES (5367, '挂断会议', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.endConference()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/endConference/37257A471FE07999856AC0773C2FF745/1', '172.16.101.224', '内网IP', '37257A471FE07999856AC0773C2FF745 1', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:10:23');
INSERT INTO `sys_oper_log` VALUES (5368, '活跃会议室信息，用于存放活跃的会议室', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.startByTemplate()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/startByTemplate/39', '172.16.101.224', '内网IP', '39', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:11:32');
INSERT INTO `sys_oper_log` VALUES (5369, '轮询方案', 1, 'com.paradisecloud.fcm.web.controller.business.BusiTemplatePollingSchemeController.add()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/templatePollingScheme', '172.16.101.224', '内网IP', '{\"pollingScheme\":{\"schemeName\":\"dsada\",\"pollingInterval\":10,\"pollingStateFirst\":1,\"templateConferenceId\":39,\"isBroadcast\":2,\"isDisplaySelf\":1,\"isFill\":1,\"layout\":\"speakerOnly\"},\"pollingDepts\":[{\"deptId\":100,\"weight\":15},{\"deptId\":103,\"weight\":14},{\"deptId\":205,\"weight\":13},{\"deptId\":206,\"weight\":12},{\"deptId\":208,\"weight\":11},{\"deptId\":207,\"weight\":10},{\"deptId\":204,\"weight\":9},{\"deptId\":203,\"weight\":8},{\"deptId\":202,\"weight\":7},{\"deptId\":201,\"weight\":6},{\"deptId\":200,\"weight\":5},{\"deptId\":107,\"weight\":4},{\"deptId\":104,\"weight\":3},{\"deptId\":105,\"weight\":2},{\"deptId\":106,\"weight\":1}],\"pollingParticipants\":[{\"remoteParty\":\"192.166.0.61\",\"isCascadeMain\":2,\"weight\":4,\"terminalId\":58},{\"remoteParty\":\"192.166.0.22\",\"isCascadeMain\":2,\"weight\":3,\"terminalId\":59},{\"remoteParty\":\"192.166.0.71\",\"isCascadeMain\":2,\"weight\":2,\"terminalId\":61},{\"remoteParty\":\"192.166.0.81\",\"isCascadeMain\":2,\"weight\":1,\"terminalId\":62},{\"remoteParty\":\"192.166.1.12\",\"isCascadeMain\":1,\"weight\":13,\"terminalId\":66},{\"remoteParty\":\"192.166.1.21\",\"isCascadeMain\":2,\"weight\":12,\"terminalId\":67},{\"remoteParty\":\"192.166.1.31\",\"isCascadeMain\":2,\"weight\":11,\"terminalId\":68},{\"remoteParty\":\"192.166.1.41\",\"isCascadeMain\":2,\"weight\":10,\"terminalId\":69},{\"remoteParty\":\"192.166.1.51\",\"isCascadeMain\":2,\"weight\":9,\"terminalId\":70},{\"remoteParty\":\"192.166.1.61\",\"isCascadeMain\":2,\"weight\":8,\"terminalId\":71},{\"remoteParty\":\"192.166.1.71\",\"isCascadeMain\":2,\"weight\":7,\"terminalId\":72},{\"remoteParty\":\"192.166.1.81\",\"isCascadeMain\":2,\"weight\":6,\"terminalId\":73},{\"remoteParty\":\"192.166.1.91\",\"isCascadeMain\":2,\"weight\":5,\"terminalId\":74},{\"remoteParty\":\"192.166.1.101\",\"isCascadeMain\":2,\"weight\":4,\"terminalId\":75},{\"remoteParty\":\"192.166.1.111\",\"isCascadeMain\":2,\"weight\":3,\"terminalId\":76},{\"remoteParty\":\"192.166.1.121\",\"isCascadeMain\":2,\"weight\":2,\"terminalId\":77},{\"remoteParty\":\"192.166.1.131\",\"isCascadeMain\":2,\"weight\":1,\"terminalId\":78},{\"remoteParty\":\"192.166.11.12\",\"isCascadeMain\":1,\"weight\":8,\"terminalId\":47},{\"remotePa', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:20:06');
INSERT INTO `sys_oper_log` VALUES (5370, '轮询方案', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplatePollingSchemeController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/templatePollingScheme/edit', '172.16.101.224', '内网IP', '[{\"weight\":1,\"updateTime\":1618309206861,\"params\":{},\"id\":56}]', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:20:06');
INSERT INTO `sys_oper_log` VALUES (5371, '轮询方案', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplatePollingSchemeController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/templatePollingScheme/56', '172.16.101.224', '内网IP', '{\"pollingScheme\":{\"id\":56,\"schemeName\":\"dsada\",\"pollingInterval\":2,\"pollingStateFirst\":1,\"templateConferenceId\":39,\"weight\":1,\"isBroadcast\":2,\"isDisplaySelf\":1,\"isFill\":1,\"layout\":\"speakerOnly\"},\"pollingDepts\":[{\"deptId\":100,\"weight\":15},{\"deptId\":103,\"weight\":14},{\"deptId\":205,\"weight\":13},{\"deptId\":206,\"weight\":12},{\"deptId\":208,\"weight\":11},{\"deptId\":207,\"weight\":10},{\"deptId\":204,\"weight\":9},{\"deptId\":203,\"weight\":8},{\"deptId\":202,\"weight\":7},{\"deptId\":201,\"weight\":6},{\"deptId\":200,\"weight\":5},{\"deptId\":107,\"weight\":4},{\"deptId\":104,\"weight\":3},{\"deptId\":105,\"weight\":2},{\"deptId\":106,\"weight\":1}],\"pollingParticipants\":[{\"remoteParty\":\"192.166.0.61\",\"isCascadeMain\":2,\"weight\":4,\"terminalId\":58},{\"remoteParty\":\"192.166.0.22\",\"isCascadeMain\":2,\"weight\":3,\"terminalId\":59},{\"remoteParty\":\"192.166.0.71\",\"isCascadeMain\":2,\"weight\":2,\"terminalId\":61},{\"remoteParty\":\"192.166.0.81\",\"isCascadeMain\":2,\"weight\":1,\"terminalId\":62},{\"remoteParty\":\"192.166.1.12\",\"isCascadeMain\":1,\"weight\":13,\"terminalId\":66},{\"remoteParty\":\"192.166.1.21\",\"isCascadeMain\":2,\"weight\":12,\"terminalId\":67},{\"remoteParty\":\"192.166.1.31\",\"isCascadeMain\":2,\"weight\":11,\"terminalId\":68},{\"remoteParty\":\"192.166.1.41\",\"isCascadeMain\":2,\"weight\":10,\"terminalId\":69},{\"remoteParty\":\"192.166.1.51\",\"isCascadeMain\":2,\"weight\":9,\"terminalId\":70},{\"remoteParty\":\"192.166.1.61\",\"isCascadeMain\":2,\"weight\":8,\"terminalId\":71},{\"remoteParty\":\"192.166.1.71\",\"isCascadeMain\":2,\"weight\":7,\"terminalId\":72},{\"remoteParty\":\"192.166.1.81\",\"isCascadeMain\":2,\"weight\":6,\"terminalId\":73},{\"remoteParty\":\"192.166.1.91\",\"isCascadeMain\":2,\"weight\":5,\"terminalId\":74},{\"remoteParty\":\"192.166.1.101\",\"isCascadeMain\":2,\"weight\":4,\"terminalId\":75},{\"remoteParty\":\"192.166.1.111\",\"isCascadeMain\":2,\"weight\":3,\"terminalId\":76},{\"remoteParty\":\"192.166.1.121\",\"isCascadeMain\":2,\"weight\":2,\"terminalId\":77},{\"remoteParty\":\"192.166.1.131\",\"isCascadeMain\":2,\"weight\":1,\"terminalId\":78},{\"remoteParty\":\"192.166.11.12\",\"isCascadeMain\":1,\"weight\":8,\"terminal', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:20:12');
INSERT INTO `sys_oper_log` VALUES (5372, '轮询方案', 3, 'com.paradisecloud.fcm.web.controller.business.BusiTemplatePollingSchemeController.remove()', 'DELETE', 1, 'superAdmin', NULL, '/fcm/busi/templatePollingScheme/56', '172.16.101.224', '内网IP', '{id=56}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:22:30');
INSERT INTO `sys_oper_log` VALUES (5373, '轮询方案', 1, 'com.paradisecloud.fcm.web.controller.business.BusiTemplatePollingSchemeController.add()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/templatePollingScheme', '172.16.101.224', '内网IP', '{\"pollingScheme\":{\"schemeName\":\"das\",\"pollingInterval\":10,\"pollingStateFirst\":1,\"templateConferenceId\":39,\"isBroadcast\":2,\"isDisplaySelf\":1,\"isFill\":1,\"layout\":\"speakerOnly\"},\"pollingDepts\":[{\"deptId\":100,\"weight\":15},{\"deptId\":103,\"weight\":14},{\"deptId\":205,\"weight\":13},{\"deptId\":206,\"weight\":12},{\"deptId\":208,\"weight\":11},{\"deptId\":207,\"weight\":10},{\"deptId\":204,\"weight\":9},{\"deptId\":203,\"weight\":8},{\"deptId\":202,\"weight\":7},{\"deptId\":201,\"weight\":6},{\"deptId\":200,\"weight\":5},{\"deptId\":107,\"weight\":4},{\"deptId\":104,\"weight\":3},{\"deptId\":105,\"weight\":2},{\"deptId\":106,\"weight\":1}],\"pollingParticipants\":[{\"remoteParty\":\"192.166.0.61\",\"isCascadeMain\":2,\"weight\":4,\"terminalId\":58},{\"remoteParty\":\"192.166.0.22\",\"isCascadeMain\":2,\"weight\":3,\"terminalId\":59},{\"remoteParty\":\"192.166.0.71\",\"isCascadeMain\":2,\"weight\":2,\"terminalId\":61},{\"remoteParty\":\"192.166.0.81\",\"isCascadeMain\":2,\"weight\":1,\"terminalId\":62},{\"remoteParty\":\"192.166.1.12\",\"isCascadeMain\":1,\"weight\":13,\"terminalId\":66},{\"remoteParty\":\"192.166.1.21\",\"isCascadeMain\":2,\"weight\":12,\"terminalId\":67},{\"remoteParty\":\"192.166.1.31\",\"isCascadeMain\":2,\"weight\":11,\"terminalId\":68},{\"remoteParty\":\"192.166.1.41\",\"isCascadeMain\":2,\"weight\":10,\"terminalId\":69},{\"remoteParty\":\"192.166.1.51\",\"isCascadeMain\":2,\"weight\":9,\"terminalId\":70},{\"remoteParty\":\"192.166.1.61\",\"isCascadeMain\":2,\"weight\":8,\"terminalId\":71},{\"remoteParty\":\"192.166.1.71\",\"isCascadeMain\":2,\"weight\":7,\"terminalId\":72},{\"remoteParty\":\"192.166.1.81\",\"isCascadeMain\":2,\"weight\":6,\"terminalId\":73},{\"remoteParty\":\"192.166.1.91\",\"isCascadeMain\":2,\"weight\":5,\"terminalId\":74},{\"remoteParty\":\"192.166.1.101\",\"isCascadeMain\":2,\"weight\":4,\"terminalId\":75},{\"remoteParty\":\"192.166.1.111\",\"isCascadeMain\":2,\"weight\":3,\"terminalId\":76},{\"remoteParty\":\"192.166.1.121\",\"isCascadeMain\":2,\"weight\":2,\"terminalId\":77},{\"remoteParty\":\"192.166.1.131\",\"isCascadeMain\":2,\"weight\":1,\"terminalId\":78},{\"remoteParty\":\"192.166.11.12\",\"isCascadeMain\":1,\"weight\":8,\"terminalId\":47},{\"remotePart', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:22:41');
INSERT INTO `sys_oper_log` VALUES (5374, '轮询方案', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplatePollingSchemeController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/templatePollingScheme/edit', '172.16.101.224', '内网IP', '[{\"weight\":1,\"updateTime\":1618309362075,\"params\":{},\"id\":57}]', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:22:42');
INSERT INTO `sys_oper_log` VALUES (5375, '轮询方案', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplatePollingSchemeController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/templatePollingScheme/57', '172.16.101.224', '内网IP', '{\"pollingScheme\":{\"id\":57,\"schemeName\":\"das\",\"pollingInterval\":2,\"pollingStateFirst\":1,\"templateConferenceId\":39,\"weight\":1,\"isBroadcast\":2,\"isDisplaySelf\":1,\"isFill\":1,\"layout\":\"speakerOnly\"},\"pollingDepts\":[{\"deptId\":100,\"weight\":15},{\"deptId\":103,\"weight\":14},{\"deptId\":205,\"weight\":13},{\"deptId\":206,\"weight\":12},{\"deptId\":208,\"weight\":11},{\"deptId\":207,\"weight\":10},{\"deptId\":204,\"weight\":9},{\"deptId\":203,\"weight\":8},{\"deptId\":202,\"weight\":7},{\"deptId\":201,\"weight\":6},{\"deptId\":200,\"weight\":5},{\"deptId\":107,\"weight\":4},{\"deptId\":104,\"weight\":3},{\"deptId\":105,\"weight\":2},{\"deptId\":106,\"weight\":1}],\"pollingParticipants\":[{\"remoteParty\":\"192.166.0.61\",\"isCascadeMain\":2,\"weight\":4,\"terminalId\":58},{\"remoteParty\":\"192.166.0.22\",\"isCascadeMain\":2,\"weight\":3,\"terminalId\":59},{\"remoteParty\":\"192.166.0.71\",\"isCascadeMain\":2,\"weight\":2,\"terminalId\":61},{\"remoteParty\":\"192.166.0.81\",\"isCascadeMain\":2,\"weight\":1,\"terminalId\":62},{\"remoteParty\":\"192.166.1.12\",\"isCascadeMain\":1,\"weight\":13,\"terminalId\":66},{\"remoteParty\":\"192.166.1.21\",\"isCascadeMain\":2,\"weight\":12,\"terminalId\":67},{\"remoteParty\":\"192.166.1.31\",\"isCascadeMain\":2,\"weight\":11,\"terminalId\":68},{\"remoteParty\":\"192.166.1.41\",\"isCascadeMain\":2,\"weight\":10,\"terminalId\":69},{\"remoteParty\":\"192.166.1.51\",\"isCascadeMain\":2,\"weight\":9,\"terminalId\":70},{\"remoteParty\":\"192.166.1.61\",\"isCascadeMain\":2,\"weight\":8,\"terminalId\":71},{\"remoteParty\":\"192.166.1.71\",\"isCascadeMain\":2,\"weight\":7,\"terminalId\":72},{\"remoteParty\":\"192.166.1.81\",\"isCascadeMain\":2,\"weight\":6,\"terminalId\":73},{\"remoteParty\":\"192.166.1.91\",\"isCascadeMain\":2,\"weight\":5,\"terminalId\":74},{\"remoteParty\":\"192.166.1.101\",\"isCascadeMain\":2,\"weight\":4,\"terminalId\":75},{\"remoteParty\":\"192.166.1.111\",\"isCascadeMain\":2,\"weight\":3,\"terminalId\":76},{\"remoteParty\":\"192.166.1.121\",\"isCascadeMain\":2,\"weight\":2,\"terminalId\":77},{\"remoteParty\":\"192.166.1.131\",\"isCascadeMain\":2,\"weight\":1,\"terminalId\":78},{\"remoteParty\":\"192.166.11.12\",\"isCascadeMain\":1,\"weight\":8,\"terminalId', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:23:35');
INSERT INTO `sys_oper_log` VALUES (5376, '挂断会议', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.endConference()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/endConference/37257A471FE07999856AC0773C2FF745/1', '172.16.101.224', '内网IP', '37257A471FE07999856AC0773C2FF745 1', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:24:58');
INSERT INTO `sys_oper_log` VALUES (5377, '活跃会议室信息，用于存放活跃的会议室', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.startByTemplate()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/startByTemplate/39', '172.16.101.224', '内网IP', '39', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:25:02');
INSERT INTO `sys_oper_log` VALUES (5378, '挂断会议', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.endConference()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/endConference/37257A471FE07999856AC0773C2FF745/1', '172.16.101.224', '内网IP', '37257A471FE07999856AC0773C2FF745 1', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:25:37');
INSERT INTO `sys_oper_log` VALUES (5379, '活跃会议室信息，用于存放活跃的会议室', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.startByTemplate()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/startByTemplate/39', '172.16.101.224', '内网IP', '39', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:25:43');
INSERT INTO `sys_oper_log` VALUES (5380, '挂断会议', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.endConference()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/endConference/37257A471FE07999856AC0773C2FF745/1', '172.16.101.224', '内网IP', '37257A471FE07999856AC0773C2FF745 1', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:26:02');
INSERT INTO `sys_oper_log` VALUES (5381, '活跃会议室信息，用于存放活跃的会议室', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.startByTemplate()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/startByTemplate/39', '172.16.101.224', '内网IP', '39', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:26:05');
INSERT INTO `sys_oper_log` VALUES (5382, '挂断会议', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.endConference()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/endConference/37257A471FE07999856AC0773C2FF745/1', '172.16.101.224', '内网IP', '37257A471FE07999856AC0773C2FF745 1', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-13 18:26:08');
INSERT INTO `sys_oper_log` VALUES (5383, '活跃会议室信息，用于存放活跃的会议室', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.startByTemplate()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/startByTemplate/39', '172.16.101.224', '内网IP', '39', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-14 10:03:29');
INSERT INTO `sys_oper_log` VALUES (5384, '挂断会议', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.endConference()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/endConference/37257A471FE07999856AC0773C2FF745/1', '172.16.101.224', '内网IP', '37257A471FE07999856AC0773C2FF745 1', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-14 10:03:44');
INSERT INTO `sys_oper_log` VALUES (5385, '活跃会议室信息，用于存放活跃的会议室', 1, 'com.paradisecloud.fcm.web.controller.business.BusiConferenceController.startByTemplate()', 'POST', 1, 'superAdmin', NULL, '/fcm/busi/conference/startByTemplate/39', '172.16.101.224', '内网IP', '39', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-14 10:03:54');
INSERT INTO `sys_oper_log` VALUES (5386, '轮询方案', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplatePollingSchemeController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/templatePollingScheme/57', '172.16.101.224', '内网IP', '{\"pollingScheme\":{\"id\":57,\"schemeName\":\"das\",\"pollingInterval\":2,\"pollingStateFirst\":1,\"templateConferenceId\":39,\"weight\":1,\"isBroadcast\":2,\"isDisplaySelf\":1,\"isFill\":1,\"layout\":\"speakerOnly\"},\"pollingDepts\":[{\"deptId\":100,\"weight\":1}],\"pollingParticipants\":[{\"remoteParty\":\"192.166.0.22\",\"isCascadeMain\":2,\"weight\":1,\"terminalId\":59}]} 57', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-14 10:40:34');
INSERT INTO `sys_oper_log` VALUES (5387, '轮询方案', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplatePollingSchemeController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/templatePollingScheme/57', '172.16.101.224', '内网IP', '{\"pollingScheme\":{\"id\":57,\"schemeName\":\"das\",\"pollingInterval\":2,\"pollingStateFirst\":1,\"templateConferenceId\":39,\"weight\":1,\"isBroadcast\":2,\"isDisplaySelf\":1,\"isFill\":1,\"layout\":\"speakerOnly\"},\"pollingDepts\":[{\"deptId\":100,\"weight\":2},{\"deptId\":103,\"weight\":1}],\"pollingParticipants\":[{\"remoteParty\":\"192.166.0.22\",\"isCascadeMain\":2,\"weight\":1,\"terminalId\":59},{\"remoteParty\":\"192.166.1.12\",\"isCascadeMain\":1,\"weight\":1,\"terminalId\":66}]} 57', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-14 10:41:36');
INSERT INTO `sys_oper_log` VALUES (5388, '会议模板', 3, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.remove()', 'DELETE', 1, 'superAdmin', NULL, '/fcm/busi/templateConference/60', '172.16.101.224', '内网IP', '{id=60}', '{\"code\":0,\"message\":\"Success\",\"success\":true}', 0, NULL, '2021-04-14 16:21:14');
INSERT INTO `sys_oper_log` VALUES (5389, '会议模板', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/templateConference/61', '172.16.101.224', '内网IP', '{\"templateDepts\":[{\"deptId\":103,\"weight\":9},{\"deptId\":205,\"weight\":8},{\"deptId\":206,\"weight\":7},{\"deptId\":208,\"weight\":6},{\"deptId\":207,\"weight\":5},{\"deptId\":204,\"weight\":4},{\"deptId\":203,\"weight\":3},{\"deptId\":202,\"weight\":2},{\"deptId\":201,\"weight\":1}],\"templateConference\":{\"createTime\":\"2021-04-14 16:21:39\",\"params\":{},\"id\":61,\"name\":\"fsd\",\"createUserId\":1,\"createUserName\":\"superAdmin\",\"deptId\":100,\"callLegProfileId\":\"78ec27d8-9d8e-4771-9cf6-bc47fecaa43b\",\"bandwidth\":2,\"isAutoCall\":2,\"conferenceNumber\":11111,\"type\":2,\"isAutoMonitor\":2,\"defaultViewLayout\":\"allEqualQuarters\",\"defaultViewIsBroadcast\":2,\"defaultViewIsDisplaySelf\":1,\"defaultViewIsFill\":1,\"pollingInterval\":10},\"templateParticipants\":[{\"terminalId\":62,\"weight\":1},{\"terminalId\":61,\"weight\":2},{\"terminalId\":60,\"weight\":3},{\"terminalId\":59,\"weight\":4},{\"terminalId\":58,\"weight\":5},{\"terminalId\":78,\"weight\":1},{\"terminalId\":77,\"weight\":2},{\"terminalId\":76,\"weight\":3},{\"terminalId\":75,\"weight\":4},{\"terminalId\":74,\"weight\":5},{\"terminalId\":73,\"weight\":6},{\"terminalId\":72,\"weight\":7},{\"terminalId\":71,\"weight\":8},{\"terminalId\":70,\"weight\":9},{\"terminalId\":69,\"weight\":10},{\"terminalId\":68,\"weight\":11},{\"terminalId\":67,\"weight\":12},{\"terminalId\":66,\"weight\":13},{\"terminalId\":133,\"weight\":1},{\"terminalId\":132,\"weight\":2},{\"terminalId\":131,\"weight\":3},{\"terminalId\":130,\"weight\":4},{\"terminalId\":129,\"weight\":5},{\"terminalId\":137,\"weight\":1},{\"terminalId\":136,\"weight\":2},{\"terminalId\":135,\"weight\":3},{\"terminalId\":134,\"weight\":4},{\"terminalId\":147,\"weight\":1},{\"terminalId\":146,\"weight\":2},{\"terminalId\":145,\"weight\":3},{\"terminalId\":144,\"weight\":4},{\"terminalId\":143,\"weight\":5},{\"terminalId\":142,\"weight\":6},{\"terminalId\":141,\"weight\":7},{\"terminalId\":140,\"weight\":8},{\"terminalId\":139,\"weight\":9},{\"terminalId\":138,\"weight\":10},{\"terminalId\":156,\"weight\":1},{\"terminalId\":155,\"weight\":2},{\"terminalId\":154,\"weight\":3},{\"terminalId\":153,\"weight\":4},{\"terminalId\":152,\"weight\":5},{\"terminalId\":151,\"weight\":6},{\"terminalId\":150,', 'null', 1, '\r\n### Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException: Lock wait timeout exceeded; try restarting transaction\r\n### The error may exist in file [D:\\dev-env\\workspace_join\\fcm_2.0\\fcm-dao\\target\\classes\\mapper\\BusiConferenceNumberMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiConferenceNumberMapper.updateBusiConferenceNumber-Inline\r\n### The error occurred while setting parameters\r\n### SQL: update busi_conference_number          SET status = ?          where id = ?\r\n### Cause: com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException: Lock wait timeout exceeded; try restarting transaction\n; Lock wait timeout exceeded; try restarting transaction; nested exception is com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException: Lock wait timeout exceeded; try restarting transaction', '2021-04-14 16:52:51');
INSERT INTO `sys_oper_log` VALUES (5390, '会议模板', 2, 'com.paradisecloud.fcm.web.controller.business.BusiTemplateConferenceController.edit()', 'PUT', 1, 'superAdmin', NULL, '/fcm/busi/templateConference/61', '172.16.101.224', '内网IP', '{\"templateDepts\":[{\"deptId\":103,\"weight\":9},{\"deptId\":205,\"weight\":8},{\"deptId\":206,\"weight\":7},{\"deptId\":208,\"weight\":6},{\"deptId\":207,\"weight\":5},{\"deptId\":204,\"weight\":4},{\"deptId\":203,\"weight\":3},{\"deptId\":202,\"weight\":2},{\"deptId\":201,\"weight\":1}],\"templateConference\":{\"createTime\":\"2021-04-14 16:21:39\",\"params\":{},\"id\":61,\"name\":\"fsd\",\"createUserId\":1,\"createUserName\":\"superAdmin\",\"deptId\":100,\"callLegProfileId\":\"78ec27d8-9d8e-4771-9cf6-bc47fecaa43b\",\"bandwidth\":2,\"isAutoCall\":2,\"conferenceNumber\":11111,\"type\":2,\"isAutoMonitor\":2,\"defaultViewLayout\":\"allEqualQuarters\",\"defaultViewIsBroadcast\":2,\"defaultViewIsDisplaySelf\":1,\"defaultViewIsFill\":1,\"pollingInterval\":10},\"templateParticipants\":[{\"terminalId\":62,\"weight\":1},{\"terminalId\":61,\"weight\":2},{\"terminalId\":60,\"weight\":3},{\"terminalId\":59,\"weight\":4},{\"terminalId\":58,\"weight\":5},{\"terminalId\":78,\"weight\":1},{\"terminalId\":77,\"weight\":2},{\"terminalId\":76,\"weight\":3},{\"terminalId\":75,\"weight\":4},{\"terminalId\":74,\"weight\":5},{\"terminalId\":73,\"weight\":6},{\"terminalId\":72,\"weight\":7},{\"terminalId\":71,\"weight\":8},{\"terminalId\":70,\"weight\":9},{\"terminalId\":69,\"weight\":10},{\"terminalId\":68,\"weight\":11},{\"terminalId\":67,\"weight\":12},{\"terminalId\":66,\"weight\":13},{\"terminalId\":133,\"weight\":1},{\"terminalId\":132,\"weight\":2},{\"terminalId\":131,\"weight\":3},{\"terminalId\":130,\"weight\":4},{\"terminalId\":129,\"weight\":5},{\"terminalId\":137,\"weight\":1},{\"terminalId\":136,\"weight\":2},{\"terminalId\":135,\"weight\":3},{\"terminalId\":134,\"weight\":4},{\"terminalId\":147,\"weight\":1},{\"terminalId\":146,\"weight\":2},{\"terminalId\":145,\"weight\":3},{\"terminalId\":144,\"weight\":4},{\"terminalId\":143,\"weight\":5},{\"terminalId\":142,\"weight\":6},{\"terminalId\":141,\"weight\":7},{\"terminalId\":140,\"weight\":8},{\"terminalId\":139,\"weight\":9},{\"terminalId\":138,\"weight\":10},{\"terminalId\":156,\"weight\":1},{\"terminalId\":155,\"weight\":2},{\"terminalId\":154,\"weight\":3},{\"terminalId\":153,\"weight\":4},{\"terminalId\":152,\"weight\":5},{\"terminalId\":151,\"weight\":6},{\"terminalId\":150,', 'null', 1, '\r\n### Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException: Lock wait timeout exceeded; try restarting transaction\r\n### The error may exist in file [D:\\dev-env\\workspace_join\\fcm_2.0\\fcm-dao\\target\\classes\\mapper\\BusiTemplateConferenceMapper.xml]\r\n### The error may involve com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper.updateBusiTemplateConference-Inline\r\n### The error occurred while setting parameters\r\n### SQL: update busi_template_conference          SET create_time = ?,             update_time = ?,             create_user_id = ?,             name = ?,             create_user_name = ?,             dept_id = ?,             call_leg_profile_id = ?,             bandwidth = ?,             is_auto_call = ?,             conference_number = ?,             type = ?,             is_auto_monitor = ?,             default_view_layout = ?,             default_view_is_broadcast = ?,             default_view_is_display_self = ?,             default_view_is_fill = ?,             polling_interval = ?          where id = ?\r\n### Cause: com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException: Lock wait timeout exceeded; try restarting transaction\n; Lock wait timeout exceeded; try restarting transaction; nested exception is com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException: Lock wait timeout exceeded; try restarting transaction', '2021-04-14 16:52:57');

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
INSERT INTO `sys_role` VALUES (2, '系统管理员', 'system', 2, '4', 1, 1, '0', '0', 'admin', '2021-01-18 10:39:19', 'superAdmin', '2021-03-12 14:33:51', '普通角色');

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
INSERT INTO `sys_role_menu` VALUES (2, 2000);
INSERT INTO `sys_role_menu` VALUES (2, 2002);
INSERT INTO `sys_role_menu` VALUES (2, 2004);
INSERT INTO `sys_role_menu` VALUES (2, 2005);
INSERT INTO `sys_role_menu` VALUES (2, 2007);
INSERT INTO `sys_role_menu` VALUES (2, 2008);
INSERT INTO `sys_role_menu` VALUES (2, 2009);
INSERT INTO `sys_role_menu` VALUES (2, 2010);
INSERT INTO `sys_role_menu` VALUES (2, 2011);
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
) ENGINE = InnoDB AUTO_INCREMENT = 104 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, NULL, 'superAdmin', '超级管理员', '00', 'ry@163.com', '15888888888', '1', '/profile/avatar/2021/01/19/35ea7b84-19cf-4e18-8d98-3e396a080a4f.jpeg', '$2a$10$d1wmaSUmRR8bs.Djrx5Bouyb4bE9i/0tn5nfAdfjiMd41IZcqFSp2', '0', '0', '127.0.0.1', '2021-01-18 10:39:18', 'admin', '2021-01-18 10:39:18', '', NULL, '管理员');
INSERT INTO `sys_user` VALUES (100, 100, 'admin', '新疆自治区管理员', '00', '', '', '0', '', '$2a$10$d1wmaSUmRR8bs.Djrx5Bouyb4bE9i/0tn5nfAdfjiMd41IZcqFSp2', '0', '0', '', NULL, 'superAdmin', '2021-01-19 11:38:00', 'superAdmin', '2021-01-25 18:48:17', NULL);
INSERT INTO `sys_user` VALUES (101, 206, 'altAdmin', '阿勒泰管理员', '00', '', '13666667777', '0', '', '$2a$10$5qIPs0Lck8Hsk514f2IMkOpA/dOjOahjN5lO8NY8rHN1mfPLT.s2u', '0', '0', '', NULL, 'admin', '2021-01-25 18:18:41', 'admin', '2021-01-29 17:56:46', NULL);
INSERT INTO `sys_user` VALUES (103, 103, 'ylzAdmin', 'ylzAdmin', '00', '', '', '0', '', '$2a$10$ZaHS6slx3kX88Xr4TnWwFe4SFEEd6JRZq7L/bTdg/zDjNS9mkinOO', '0', '0', '', NULL, 'superAdmin', '2021-01-30 13:38:37', '', NULL, NULL);

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
INSERT INTO `sys_user_post` VALUES (103, 4);

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
INSERT INTO `sys_user_role` VALUES (101, 2);
INSERT INTO `sys_user_role` VALUES (103, 2);

SET FOREIGN_KEY_CHECKS = 1;
