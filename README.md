
金科双向机器人系统
===============

当前最新版本： v.1.0（发布日期：2024-03-19） 


[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/zhangdaiscott/jeecg-boot/blob/master/LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/zhangdaiscott/jeecg-boot.svg?style=social&label=Stars)](https://github.com/zhangdaiscott/jeecg-boot)
[![GitHub forks](https://img.shields.io/github/forks/zhangdaiscott/jeecg-boot.svg?style=social&label=Fork)](https://github.com/zhangdaiscott/jeecg-boot)



项目介绍
-----------------------------------

金科双向机器人系统 是一款基于 jeecgboot 框架开发的电报机器人系统


`宗旨是:` 实现一款灵活、强大的电报双向机器人管理系统！

遇到技术问题，[请在这里反馈BUG](https://github.com/jeecgboot/jeecg-boot/issues/new)


快速搭建开发环境
-----------------------------------

- [通过IDEA导入项目](https://help.jeecg.com/java/setup/idea.html)
- [通过IDEA启动项目](https://help.jeecg.com/java/setup/idea/startup.html)


技术支持
-----------------------------------

关闭gitee的issue通道，使用中遇到问题或者BUG可以在 [Github上提Issues](https://github.com/jeecgboot/jeecg-boot/issues/new)


技术架构：
-----------------------------------
#### 开发环境

- 语言：Java 8+ (小于17)

- IDE(JAVA)： IDEA (必须安装lombok插件 )

- 依赖管理：Maven

- 缓存：Redis

- 数据库脚本：MySQL5.7+  &  Oracle 11g & Sqlserver2017（其他数据库，[需要自己转](https://my.oschina.net/jeecg/blog/4905722)）


#### 开发框架

- 基础框架：Spring Boot 2.6.14

- 持久层框架：MybatisPlus 3.5.1

- 安全框架：Apache Shiro 1.10.0，Jwt 3.11.0

- 数据库连接池：阿里巴巴Druid 1.1.22

- 日志打印：logback

- 其他：autopoi, fastjson，poi，Swagger-ui，quartz, lombok等

#### 支持库

|  数据库   |  支持   |
| --- | --- |
|   MySQL   |  √   |
|  Oracle11g   |  √   |
|  Sqlserver2017   |  √   |
|   PostgreSQL   |  √   |
|   MariaDB   |  √   |



### 系统效果



##### PC端
![](https://oscimg.oschina.net/oscnet/up-000530d95df337b43089ac77e562494f454.png)

![输入图片说明](https://static.oschina.net/uploads/img/201904/14155402_AmlV.png "在这里输入图片标题")

![](https://oscimg.oschina.net/oscnet/up-9d6f36f251e71a0b515a01323474b03004c.png)

![输入图片说明](https://static.oschina.net/uploads/img/201904/14160813_KmXS.png "在这里输入图片标题")

![输入图片说明](https://static.oschina.net/uploads/img/201904/14160935_Nibs.png "在这里输入图片标题")

![输入图片说明](https://static.oschina.net/uploads/img/201904/14161004_bxQ4.png "在这里输入图片标题")

#####  系统交互
![](https://oscimg.oschina.net/oscnet/up-78b151fc888d4319377bf1cc311fe826871.png)

![](https://oscimg.oschina.net/oscnet/up-16c07e000278329b69b228ae3189814b8e9.png)


##### 流程设计
![](https://oscimg.oschina.net/oscnet/up-981ce174e4fbb48c8a2ce4ccfd7372e2994.png)

![输入图片说明](https://static.oschina.net/uploads/img/201907/05165142_yyQ7.png "在这里输入图片标题")

![输入图片说明](https://static.oschina.net/uploads/img/201904/14160917_9Ftz.png "在这里输入图片标题")

![输入图片说明](https://static.oschina.net/uploads/img/201904/14160633_u59G.png "在这里输入图片标题")
