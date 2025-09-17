CREATE TABLE `busi_packet` (
                               `id` int(11) NOT NULL AUTO_INCREMENT,
                               `ip` varchar(255) NOT NULL COMMENT '服务器IP',
                               `username` varchar(255) NOT NULL COMMENT '账户',
                               `password` varchar(255) NOT NULL COMMENT '密码',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抓包服务器';