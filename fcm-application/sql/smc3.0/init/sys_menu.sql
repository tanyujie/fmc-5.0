/*
 Navicat Premium Data Transfer

 Source Server         : 172.16.100.55
 Source Server Type    : MySQL
 Source Server Version : 50735
 Source Host           : 172.16.100.55:3306
 Source Schema         : fcmdb

 Target Server Type    : MySQL
 Target Server Version : 50735
 File Encoding         : 65001

 Date: 19/05/2023 17:33:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE = InnoDB AUTO_INCREMENT = 2115 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '组织架构管理', 0, 80, 'system', NULL, 1, 0, 'M', '0', '0', '', 'system', 'admin', '2021-01-18 10:39:19', 'superAdmin', '2021-10-29 11:35:01', '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 110, 'monitor', NULL, 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2021-01-18 10:39:19', 'superAdmin', '2021-06-07 16:20:31', '系统监控目录');
INSERT INTO `sys_menu` VALUES (3, '系统工具', 0, 120, 'tool', NULL, 1, 0, 'M', '1', '0', '', 'tool', 'admin', '2021-01-18 10:39:19', 'superAdmin', '2023-03-31 11:29:37', '系统工具目录');
INSERT INTO `sys_menu` VALUES (4, '天堂云官网', 0, 1000, 'http://www.ttclouds.cn', NULL, 0, 0, 'M', '1', '0', '', 'guide', 'admin', '2021-01-18 10:39:19', 'superAdmin', '2023-03-31 11:29:27', '若依官网地址');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2021-01-18 10:39:19', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2021-01-18 10:39:19', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 4, 'menu', 'system/menu/index', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2021-01-18 10:39:19', 'superAdmin', '2021-07-12 14:40:28', '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 0, 'dept', 'system/dept/index', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2021-01-18 10:39:19', 'superAdmin', '2021-07-12 14:39:39', '部门管理菜单');
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', '2021-01-18 10:39:19', '', NULL, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2021-01-18 10:39:19', '', NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 3, 'config', 'system/config/index', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2021-01-18 10:39:19', 'superAdmin', '2021-07-12 14:40:01', '参数设置菜单');
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
INSERT INTO `sys_menu` VALUES (2000, '会议管理', 0, 10, 'conference', NULL, 1, 0, 'M', '0', '0', '', 'table', 'admin', '2021-01-18 13:34:44', 'superAdmin', '2021-06-07 16:21:30', '');
INSERT INTO `sys_menu` VALUES (2002, '模板会议-', 2000, 20, 'template', 'conference/template/index', 1, 1, 'C', '1', '0', '', 'component', 'admin', '2021-01-18 14:52:52', 'superAdmin', '2023-03-30 11:50:06', '');
INSERT INTO `sys_menu` VALUES (2004, '新增模板', 2000, 60, 'add-template', 'conference/addTemplate/index2', 1, 0, 'C', '1', '0', '', 'input', 'admin', '2021-01-18 15:27:32', 'superAdmin', '2023-03-30 11:48:50', '');
INSERT INTO `sys_menu` VALUES (2005, '模板修改', 2000, 70, 'modify-template', 'conference/modifyTemplate/index2', 1, 0, 'C', '1', '0', '', 'edit', 'admin', '2021-01-18 15:29:46', 'superAdmin', '2023-03-30 11:48:55', '');
INSERT INTO `sys_menu` VALUES (2007, '会议控制', 2000, 50, 'view-control', 'conference/viewControl/index', 1, 0, 'C', '1', '0', '', 'button', 'admin', '2021-01-18 15:51:00', 'superAdmin', '2021-09-28 10:39:25', '');
INSERT INTO `sys_menu` VALUES (2014, '我的会议', 2000, 30, 'conferenceList', 'conference/conferenceList/index', 1, 0, 'C', '1', '0', '', 'list', 'superAdmin', '2021-02-04 15:51:02', 'superAdmin', '2021-09-28 10:39:46', '');
INSERT INTO `sys_menu` VALUES (2015, '全局参数', 1, 10, 'parameter', 'system/parameter/index', 1, 0, 'C', '0', '0', '', 'skill', 'superAdmin', '2021-02-28 10:50:30', 'superAdmin', '2021-02-28 10:53:03', '');
INSERT INTO `sys_menu` VALUES (2016, '录制管理', 0, 60, 'record', 'record/index', 1, 0, 'C', '0', '0', '', 'luzhi', 'superAdmin', '2021-02-28 12:40:42', 'superAdmin', '2021-08-20 20:57:27', '');
INSERT INTO `sys_menu` VALUES (2019, '会议控制-表格', 2000, 40, 'table-control', 'conference/tableControl/index', 1, 0, 'C', '1', '0', '', 'button', 'superAdmin', '2021-03-08 10:05:51', 'superAdmin', '2021-09-28 10:39:41', '');
INSERT INTO `sys_menu` VALUES (2020, '会议室调试', 3, 4, 'conference-debugger', 'tool/debug/index', 1, 0, 'C', '1', '0', '', 'bug', 'superAdmin', '2021-03-25 17:28:40', 'superAdmin', '2021-03-25 17:34:55', '');
INSERT INTO `sys_menu` VALUES (2025, '预约会议-', 2000, 10, 'appointmentMeeting', 'conference/appointmentMeeting/AppointmentMeeting', 1, 0, 'C', '1', '0', '', 'job', 'superAdmin', '2021-05-26 14:50:19', 'superAdmin', '2023-03-30 11:50:02', '');
INSERT INTO `sys_menu` VALUES (2026, '报表管理', 0, 50, 'statisticsReport', NULL, 1, 0, 'M', '0', '0', '', 'chart', 'superAdmin', '2021-05-27 10:56:42', 'superAdmin', '2021-08-20 20:57:20', '');
INSERT INTO `sys_menu` VALUES (2027, '历史会议列表', 2026, 1, 'historyList', 'report/historyList/HistoryList', 1, 0, 'C', '0', '0', '', 'date', 'superAdmin', '2021-05-27 10:58:47', 'superAdmin', '2021-07-12 13:20:12', '');
INSERT INTO `sys_menu` VALUES (2028, '终端呼叫次数统计', 2026, 2, 'fmeConsume', 'report/fmeConsume/FmeConsume', 1, 0, 'C', '1', '0', '', 'druid', 'superAdmin', '2021-05-27 10:59:34', 'superAdmin', '2021-12-21 11:27:04', '');
INSERT INTO `sys_menu` VALUES (2029, '业务配置', 0, 40, 'bussConfig', '', 1, 0, 'M', '0', '0', '', 'guide', 'superAdmin', '2021-06-07 16:17:01', 'superAdmin', '2021-08-20 20:56:57', '');
INSERT INTO `sys_menu` VALUES (2030, '资源配置', 0, 28, 'resourceConfig', NULL, 1, 0, 'M', '0', '0', '', 'system', 'superAdmin', '2021-06-07 16:18:53', 'superAdmin', '2021-10-21 21:24:06', '');
INSERT INTO `sys_menu` VALUES (2031, '资源分配', 0, 30, 'resourceAllot', NULL, 1, 0, 'M', '0', '0', '', 'system', 'superAdmin', '2021-06-07 16:19:35', 'superAdmin', '2021-10-29 11:31:38', '');
INSERT INTO `sys_menu` VALUES (2032, '固定会议号码', 2029, 1, 'conferenceNumber', 'config/conferenceNumber/index', 1, 0, 'C', '0', '0', '', 'number', 'superAdmin', '2021-06-07 16:36:16', 'superAdmin', '2021-08-02 10:05:05', '');
INSERT INTO `sys_menu` VALUES (2033, '终端管理', 2030, 0, 'terminal', 'config/terminal/index', 1, 0, 'C', '0', '0', '', 'international', 'superAdmin', '2021-06-07 16:37:39', 'superAdmin', '2021-07-12 15:46:17', '');
INSERT INTO `sys_menu` VALUES (2034, '入会参数', 2029, 10, 'project', 'config/project/index', 1, 0, 'C', '0', '0', '', 'example', 'superAdmin', '2021-06-07 16:38:13', 'superAdmin', '2021-08-20 17:38:49', '');
INSERT INTO `sys_menu` VALUES (2035, '录制/直播配置', 2030, 7, 'liveRecording', 'config/liveRecording/index.vue', 1, 0, 'C', '0', '0', '', 'radio', 'superAdmin', '2021-06-07 16:39:03', 'superAdmin', '2021-07-12 15:44:49', '');
INSERT INTO `sys_menu` VALUES (2036, 'FME配置', 2030, 1, 'fme', 'config/fme/index', 1, 0, 'C', '0', '0', NULL, 'server', 'superAdmin', '2021-06-07 16:39:47', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2037, 'FSBC配置', 2030, 2, 'FSBC', 'config/fsbcServer/index', 1, 0, 'C', '0', '0', '', 'redis', 'superAdmin', '2021-06-07 16:40:28', 'superAdmin', '2021-07-12 14:43:09', '');
INSERT INTO `sys_menu` VALUES (2038, 'FME资源分配', 2031, 1, 'fmeallot', 'config/fmeallot/index', 1, 0, 'C', '0', '0', '', 'cascader', 'superAdmin', '2021-06-07 16:43:52', 'superAdmin', '2022-09-17 17:44:03', '');
INSERT INTO `sys_menu` VALUES (2039, 'FSBC资源分配', 2031, 2, 'fsbcallot', 'config/fsbcAllot/index', 1, 0, 'C', '0', '0', NULL, 'cascader', 'superAdmin', '2021-06-07 16:44:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2040, '会议号段分配', 2031, 3, 'conferenceNumberRange', 'config/conferenceNumberRange/index', 1, 0, 'C', '0', '0', NULL, 'input', 'superAdmin', '2021-06-07 16:45:53', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2041, '会议次数统计', 2026, 3, 'meetingCounts', 'report/meetingCounts/MeetingCounts', 1, 0, 'C', '0', '0', '', 'number', 'superAdmin', '2021-06-18 16:55:36', 'superAdmin', '2021-07-09 22:35:52', '');
INSERT INTO `sys_menu` VALUES (2042, '会议时段统计', 2026, 4, 'meetingDuration', 'report/meetingDuration/MeetingDuration', 1, 0, 'C', '0', '0', '', 'time', 'superAdmin', '2021-06-18 16:56:40', 'superAdmin', '2021-06-29 21:49:02', '');
INSERT INTO `sys_menu` VALUES (2043, '通话质量统计', 2026, 5, 'voiceQuality', 'report/voiceQuality/VoiceQuality', 1, 0, 'C', '0', '0', NULL, 'discuss', 'superAdmin', '2021-06-18 16:59:32', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2044, '断线原因统计', 2026, 2, 'disconnectionReason', 'report/disconnectionReason/DisconnectionReason', 1, 0, 'C', '0', '0', '', 'polling', 'superAdmin', '2021-06-18 17:02:18', 'superAdmin', '2021-12-21 11:27:20', '');
INSERT INTO `sys_menu` VALUES (2045, '会议时长统计', 2026, 7, 'terminalDuration', 'report/terminalDuration/TerminalDuration', 1, 0, 'C', '0', '0', '', 'monitor', 'superAdmin', '2021-06-18 17:03:37', 'superAdmin', '2021-06-29 21:49:18', '');
INSERT INTO `sys_menu` VALUES (2046, '参会告警统计', 2026, 8, 'attendanceWarning', 'report/attendanceWarning/AttendanceWarning', 1, 0, 'C', '0', '0', '', '告警', 'superAdmin', '2021-06-18 17:05:28', 'superAdmin', '2021-07-12 12:20:47', '');
INSERT INTO `sys_menu` VALUES (2047, '终端使用情况', 2026, 8, 'terminalUsed', 'report/terminalUsed/TerminalUsed', 1, 0, 'C', '0', '0', '', 'terminal', 'superAdmin', '2021-06-27 16:24:00', 'superAdmin', '2021-07-12 12:15:46', '');
INSERT INTO `sys_menu` VALUES (2048, '设备类型统计', 2026, 9, 'deviceCounts', 'report/deviceCounts/DeviceCounts', 1, 0, 'C', '0', '0', NULL, 'phone', 'superAdmin', '2021-06-27 18:19:08', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2054, 'DTMF配置', 2029, 20, 'dtmf', 'config/dtmf/Dtmf', 1, 0, 'C', '0', '0', '', 'system', 'superAdmin', '2021-07-26 10:32:10', 'superAdmin', '2021-07-27 12:53:18', '');
INSERT INTO `sys_menu` VALUES (2055, '虚拟会议室参数', 2029, 30, 'callProfiles', 'config/callProfiles/CallProfiles', 1, 0, 'C', '0', '0', '', '会场排序', 'superAdmin', '2021-07-28 16:10:45', 'superAdmin', '2021-07-30 18:15:20', '');
INSERT INTO `sys_menu` VALUES (2056, '呼入安全参数', 2029, 40, 'dialInSecurityProfiles', 'config/dialInSecurityProfiles/DialInSecurityProfiles', 1, 0, 'C', '0', '0', NULL, 'job', 'superAdmin', '2021-07-29 13:14:19', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2057, '兼容性参数', 2029, 40, 'compatibilityProfiles', 'config/compatibilityProfiles/CompatibilityProfiles', 1, 0, 'C', '0', '0', NULL, 'guide', 'superAdmin', '2021-07-29 15:37:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2058, '呼叫标识参数', 2029, 50, 'callBrandingProfiles', 'config/callBrandingProfiles/CallBrandingProfiles', 1, 0, 'C', '0', '0', '', 'redis', 'superAdmin', '2021-07-30 14:40:41', 'superAdmin', '2021-08-20 10:21:53', '');
INSERT INTO `sys_menu` VALUES (2059, '语音应答标识', 2029, 70, 'ivrBrandingProfiles', 'config/ivrBrandingProfiles/IvrBrandingProfiles', 1, 0, 'C', '0', '0', '', 'form', 'superAdmin', '2021-07-30 14:42:21', 'superAdmin', '2022-05-14 10:43:26', '');
INSERT INTO `sys_menu` VALUES (2060, '租户设置', 2031, 5, 'tenant', 'config/tenant/Tenant', 1, 0, 'C', '0', '0', '', 'guide', 'superAdmin', '2021-08-05 16:41:22', 'superAdmin', '2021-09-23 16:26:01', '');
INSERT INTO `sys_menu` VALUES (2061, 'FCM配置', 2030, 6, 'fcmConfig/:fcmId', 'config/fcmConfig/index', 1, 0, 'M', '1', '0', '', 'system', 'superAdmin', '2021-08-18 17:00:18', 'superAdmin', '2021-11-22 14:21:09', '');
INSERT INTO `sys_menu` VALUES (2063, 'FMQ配置', 2030, 3, 'mqtt', 'config/mqtt/index', 1, 0, 'C', '0', '0', '', 'dict', 'superAdmin', '2021-08-20 10:03:08', 'superAdmin', '2022-01-05 10:39:38', '');
INSERT INTO `sys_menu` VALUES (2064, 'FMQ资源分配', 2031, 3, 'fmqallot', 'config/mqttAllot/MqttAllot', 1, 0, 'C', '0', '0', '', 'redis', 'superAdmin', '2021-08-20 10:05:35', 'superAdmin', '2022-01-05 10:41:16', '');
INSERT INTO `sys_menu` VALUES (2071, '基础配置', 2061, 10, 'basics', 'config/fcmConfig/components/basics.vue', 1, 0, 'C', '0', '0', '', '#', 'superAdmin', '2021-08-20 18:00:22', 'superAdmin', '2021-11-22 12:31:52', '');
INSERT INTO `sys_menu` VALUES (2072, 'Cuturn配置', 2061, 20, 'cuturn', 'config/fcmConfig/components/cuturn.vue', 1, 0, 'C', '0', '0', '', '#', 'superAdmin', '2021-08-20 18:01:24', 'superAdmin', '2021-11-22 11:18:10', '');
INSERT INTO `sys_menu` VALUES (2073, 'Dialplan配置', 2061, 30, 'dialplan', 'config/fcmConfig/components/dialplan.vue', 1, 0, 'C', '0', '0', '', '#', 'superAdmin', '2021-08-20 18:01:55', 'superAdmin', '2021-11-22 11:18:20', '');
INSERT INTO `sys_menu` VALUES (2074, 'Fme源配置', 2061, 50, 'fme', 'config/fcmConfig/components/fme.vue', 1, 0, 'C', '0', '0', '', '#', 'superAdmin', '2021-08-20 18:02:19', 'superAdmin', '2021-11-22 11:18:43', '');
INSERT INTO `sys_menu` VALUES (2075, 'NFS录制配置', 2061, 60, 'nfs', 'config/fcmConfig/components/nfs.vue', 1, 0, 'C', '0', '0', '', '#', 'superAdmin', '2021-08-20 18:02:44', 'superAdmin', '2021-11-22 11:19:00', '');
INSERT INTO `sys_menu` VALUES (2076, '重启FCM', 2061, 70, 'restart', 'config/fcmConfig/components/restart.vue', 1, 0, 'C', '0', '0', '', '#', 'superAdmin', '2021-08-20 18:03:13', 'superAdmin', '2021-11-22 14:24:23', '');
INSERT INTO `sys_menu` VALUES (2077, '视频客户端', 0, 999, 'https://172.16.100.145:9443', NULL, 0, 0, 'C', '1', '0', '', 'guide', 'superAdmin', '2021-09-23 15:50:03', 'superAdmin', '2023-03-31 11:29:23', '');
INSERT INTO `sys_menu` VALUES (2078, 'FCM管理', 2030, 4, 'fcm', 'config/fcm/index', 1, 0, 'C', '0', '0', '', 'terminal', 'superAdmin', '2021-09-23 16:23:22', 'superAdmin', '2021-11-22 11:01:27', '');
INSERT INTO `sys_menu` VALUES (2079, 'FCM资源分配', 2031, 4, 'fcmAllot', 'config/fcmAllot/index', 1, 0, 'C', '0', '0', '', 'terminal', 'superAdmin', '2021-09-23 16:25:44', 'superAdmin', '2021-11-22 12:53:32', '');
INSERT INTO `sys_menu` VALUES (2080, '即时会议', 2000, 0, 'instantConference', 'conference/instantConference/InstantConference', 1, 0, 'C', '1', '0', '', 'time', 'superAdmin', '2021-09-28 10:42:43', 'superAdmin', '2023-03-30 11:50:23', '');
INSERT INTO `sys_menu` VALUES (2081, '专递课堂', 0, 15, 'edu', NULL, 1, 0, 'M', '1', '0', '', 'tab', 'superAdmin', '2021-10-10 17:45:02', 'superAdmin', '2023-03-31 11:29:14', '');
INSERT INTO `sys_menu` VALUES (2083, '专递课表', 2081, 10, 'courseSchedule', 'edu/CourseSchedule/CourseSchedule', 1, 0, 'C', '0', '0', '', 'date-range', 'superAdmin', '2021-10-10 17:58:51', 'superAdmin', '2021-10-28 10:30:03', '');
INSERT INTO `sys_menu` VALUES (2084, '节次方案', 2081, 20, 'sectionScheme', 'edu/SectionScheme/SectionScheme', 1, 0, 'C', '0', '0', '', 'time-range', 'superAdmin', '2021-10-10 18:04:28', 'superAdmin', '2021-10-15 12:35:40', '');
INSERT INTO `sys_menu` VALUES (2085, '教学楼管理', 2081, 10, 'teachingBuilding', 'edu/TeachingBuilding/TeachingBuilding', 1, 0, 'C', '1', '1', '', 'build', 'superAdmin', '2021-10-10 18:12:14', 'superAdmin', '2021-10-28 10:30:49', '');
INSERT INTO `sys_menu` VALUES (2086, '教室管理', 2081, 20, 'classroom', 'edu/Classroom/Classroom', 1, 0, 'C', '0', '0', '', 'terminal', 'superAdmin', '2021-10-10 18:14:45', 'superAdmin', '2021-10-28 10:30:57', '');
INSERT INTO `sys_menu` VALUES (2087, '班级管理', 2081, 40, 'class', 'edu/ClassGrade/ClassGrade', 1, 0, 'C', '0', '0', '', 'peoples', 'superAdmin', '2021-10-10 18:22:27', 'superAdmin', '2021-10-28 10:31:12', '');
INSERT INTO `sys_menu` VALUES (2088, '学段管理', 2081, 30, 'learningStage', 'edu/LearningStage/LearningStage', 1, 0, 'C', '0', '0', '', 'slider', 'superAdmin', '2021-10-10 18:23:53', 'superAdmin', '2021-10-28 10:31:06', '');
INSERT INTO `sys_menu` VALUES (2089, '学科管理', 2081, 50, 'subject', 'edu/Subject/Subject', 1, 0, 'C', '0', '0', '', 'documentation', 'superAdmin', '2021-10-10 18:25:58', 'superAdmin', '2021-10-28 10:31:20', '');
INSERT INTO `sys_menu` VALUES (2090, '专递模板', 2081, 15, 'courseTemplate', 'edu/CourseTemplate/CourseTemplate', 1, 0, 'C', '0', '0', '', 'component', 'superAdmin', '2021-10-21 20:40:19', 'superAdmin', '2021-10-28 10:29:24', '');
INSERT INTO `sys_menu` VALUES (2091, '我的互动', 2081, 30, 'conferenceList', 'conference/conferenceList/index', 1, 0, 'C', '1', '0', '', 'list', 'superAdmin', '2021-10-22 08:44:00', 'superAdmin', '2021-11-08 18:30:27', '');
INSERT INTO `sys_menu` VALUES (2092, '互动控制-表格', 2081, 40, 'table-control', 'conference/tableControl/index', 1, 0, 'C', '1', '0', '', 'button', 'superAdmin', '2021-10-22 08:46:05', 'superAdmin', '2021-11-08 18:30:35', '');
INSERT INTO `sys_menu` VALUES (2093, '互动控制', 2081, 50, 'view-control', 'conference/viewControl/index', 1, 0, 'C', '1', '0', '', 'button', 'superAdmin', '2021-10-22 08:47:00', 'superAdmin', '2021-11-08 18:30:45', '');
INSERT INTO `sys_menu` VALUES (2094, '新增模板', 2081, 60, 'add-template', 'conference/addTemplate/index2', 1, 0, 'C', '1', '0', NULL, 'input', 'superAdmin', '2021-10-22 08:47:45', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2095, '模板修改', 2081, 70, 'modify-template', 'conference/modifyTemplate/index2', 1, 0, 'C', '1', '0', NULL, 'edit', 'superAdmin', '2021-10-22 08:48:41', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2096, '专递组管理', 0, 90, 'eduManage', NULL, 1, 0, 'M', '0', '0', NULL, 'system', 'superAdmin', '2021-11-05 14:00:29', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2097, '专递组架构', 2096, 1, 'deuDept', 'system/dept/index', 1, 0, 'C', '0', '0', NULL, 'tree', 'superAdmin', '2021-11-05 14:02:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2098, '专递用户管理', 2096, 2, 'eduUser', 'system/user/index', 1, 0, 'C', '0', '0', NULL, 'user', 'superAdmin', '2021-11-05 14:03:44', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2099, 'FCM号段分配', 2031, 6, 'fcmSipNumberRange', 'config/fcmSipNumberRange/index', 1, 0, 'C', '0', '0', NULL, 'input', 'superAdmin', '2022-03-19 17:04:39', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2100, '服务器配置', 2030, 10, 'serverConfig', 'config/serverConfig/index.vue', 1, 0, 'C', '0', '0', NULL, 'redis', 'superAdmin', '2022-09-17 11:04:09', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2101, 'SMC配置', 2030, 8, 'smc', 'config/smc/index.vue', 1, 0, 'C', '0', '0', NULL, 'dict', 'superAdmin', '2022-09-17 17:39:07', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2102, 'SMC资源分配', 2031, 2, 'smcAllot', 'config/smcAllot/index.vue', 1, 0, 'C', '0', '0', '', 'tree-table', 'superAdmin', '2022-09-17 17:41:25', 'superAdmin', '2022-09-17 17:50:15', '');
INSERT INTO `sys_menu` VALUES (2103, '会议模板', 2000, 80, 'scmTemplate', 'smcConference/template/index.vue', 1, 0, 'C', '0', '0', '', 'build', 'superAdmin', '2022-09-17 19:58:50', 'superAdmin', '2023-03-30 11:49:28', '');
INSERT INTO `sys_menu` VALUES (2104, '新增SMC模板', 2000, 90, 'add-smcTemplate', 'smcConference/addTemplate/index.vue', 1, 0, 'C', '1', '0', '', '#', 'superAdmin', '2022-09-18 08:38:58', 'superAdmin', '2022-09-18 08:42:30', '');
INSERT INTO `sys_menu` VALUES (2105, '修改SMC模板', 2000, 100, 'modify-smcTemplate', 'smcConference/modifyTemplate/index.vue', 1, 0, 'C', '1', '0', '', '#', 'superAdmin', '2022-09-19 14:53:04', 'superAdmin', '2023-03-28 18:24:09', '');
INSERT INTO `sys_menu` VALUES (2106, 'SMC会议控制', 2000, 110, 'smcConferenceControl', 'smcConference/conferenceControl/index.vue', 1, 0, 'C', '1', '0', NULL, '#', 'superAdmin', '2022-09-20 17:22:17', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2107, '会议预约', 2000, 120, 'appointmentsmcMeeting', 'smcConference/appointmentConference/index.vue', 1, 0, 'C', '0', '0', '', 'job', 'superAdmin', '2023-03-28 17:35:01', 'superAdmin', '2023-03-30 11:49:39', '');
INSERT INTO `sys_menu` VALUES (2108, 'SMC新增预约', 2000, 121, 'appointmentConference-create', 'smcConference/createAppointment/index.vue', 1, 0, 'C', '1', '0', '', '#', 'superAdmin', '2023-03-28 17:36:58', 'superAdmin', '2023-03-28 17:50:52', '');
INSERT INTO `sys_menu` VALUES (2110, '会议列表', 2000, 123, 'scmMeeting', 'smcConference/conferenceList/index.vue', 1, 0, 'C', '0', '0', '', 'list', 'superAdmin', '2023-03-28 17:41:02', 'superAdmin', '2023-03-30 11:49:46', '');
INSERT INTO `sys_menu` VALUES (2111, 'SMC历史会议', 2026, 10, 'smcHistoryMeeting', 'smcReport/historicalConference/index.vue', 1, 0, 'C', '0', '0', '', 'nested', 'superAdmin', '2023-03-28 17:44:17', 'superAdmin', '2023-03-28 17:56:23', '');
INSERT INTO `sys_menu` VALUES (2112, 'SMC历史会议详情', 2026, 11, 'historyMeetingDetails', 'smcReport/historicalConference/details.vue', 1, 0, 'C', '1', '0', '', '#', 'superAdmin', '2023-03-28 17:58:10', 'superAdmin', '2023-03-28 18:21:52', '');
INSERT INTO `sys_menu` VALUES (2113, 'SMC修改预约', 2000, 122, 'modifySmcAppointment', 'smcConference/modifyAppointment/index.vue', 1, 0, 'C', '1', '0', '', '#', 'superAdmin', '2023-03-28 18:26:49', 'superAdmin', '2023-03-28 18:32:13', '');
INSERT INTO `sys_menu` VALUES (2114, 'License配置', 2030, 12, 'LicenseManagement', 'config/license/smcLicense.vue', 1, 0, 'C', '0', '0', NULL, 're-preset', 'superAdmin', '2023-04-24 17:13:31', '', NULL, '');

SET FOREIGN_KEY_CHECKS = 1;
