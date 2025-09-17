# 会议控制管理系统FCM2.0

#### 介绍
会控2.0系统，较1.0相比，工程结构，系统架构，以及安装部署上做重大调整

#### 软件架构
    1. 基于springboot2.0做基础框架，对配置模块的改造，极大增强对应用初始设置的灵活性，扩展性以及可维护性。
    2. 前后端分离，前端完全部署到nginx上。
    3. 采用maven分模块管理项目，合理，准确，精细的模块进行依赖设计，避免造成依赖管理冗余，臃肿等可维护性低的设计。
    4. 设备管理模块和会控进行合并，通过maven分模块对齐进行合理的依赖管理，从此不再是两个独立应用。
	5. 应用模块分为：
	    FME对接模块（包含对FME所有api调用封装，以及业务数据交互处理）
		设备管理模块（包含终端配置，终端api调用，等功能）
		基础平台模块（用户登陆，权限管理，菜单配置，告警模块，报表模块等）

#### 技术选型
    1. 基础架构基于springboot 2.4.1
	2. websocket采用Java-WebSocket 1.4.1
	3. rest 采用springmvc
	4. 数据源驱动采用阿里巴巴druid。
	5. 数据库采用mysql-5.7.21
	6. 数据库中间件：mybatis 3.5.1
	7. 

#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx

#### 使用说明

1.  xxxx
2.  xxxx
3.  xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
