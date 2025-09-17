-- 会议桥组表新增字段
ALTER TABLE `fcm`.`hg_bridge_host_group` 
ADD COLUMN `type` int(11) NULL COMMENT '会议桥组类型：1集群，2单节点' AFTER `dept_id`;

ALTER TABLE `fcm`.`hg_bridge_host_group` 
ADD COLUMN `spare_bridge_host_id` int(11) NULL COMMENT '备用会议桥ID' AFTER `type`;

-- 会议桥表新增字段
ALTER TABLE `fcm`.`hg_bridge_host` 
ADD COLUMN `busi_status` int(11) NULL COMMENT '业务状态：1正常，2异常' AFTER `status`

ALTER TABLE `fcm`.`hg_bridge_host` 
ADD COLUMN `type` int(11) NULL COMMENT '会议桥类型：1业务，2备用' AFTER `dept_id`;

ALTER TABLE `fcm`.`hg_bridge_host` 
ADD COLUMN `spare_bridge_host_id` int(11) NULL COMMENT '备用会议桥的ID（关联会议桥表主键）' AFTER `type`;