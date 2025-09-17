ALTER TABLE `fcmdb`.`busi_free_switch`
ADD COLUMN `call_port` INT(11) NULL DEFAULT NULL COMMENT '呼叫端口' AFTER `port`,
ADD COLUMN `out_bound` INT(11) NOT NULL DEFAULT 0 COMMENT '能否被叫:0:不能,1:能' AFTER `call_port`;
