# <p align="center">【catalpa-core-project】</p>

## 简介
***

- 基于
  - *SpringBoot3.0.5*
  - *MyBatis-Plus3.5.3.1*
  - *HuTool5.8.16*
  - *SpringDoc2.0.4*

[CatalpaWoo](https://blog.csdn.net/CatalpaWoo?type=blog)的个人核心工具类，实现一些业务上常用的代码，以及基于MyBatis-Plus的fuzzy模糊查询、分页功能。

当前版本：1.0-SNAPSHOT

## 模块
***

- 一、通用模块
  - 1.常用数字
  - 2.业务异常
  - 3.响应消息

- 二、数据模块（关于MyBatis-Plus）
  - 1.自动填充与分页配置代码
  - 2.分页信息传输
  - 3.模糊查询接口

- 三、Redis缓存配置模块

## 使用
***

1. 拉取代码：
``` java
git clone git@github.com:CatalpaWoo/catalpa-core-project.git
```
2. 执行配置文件生效：
``` java
source ~/.bash_profile
```
3. 执行打包：
``` java
mvn install
```
4. Maven引入：
``` java
<dependency>
  <groupId>com.catalpa.core</groupId>
  <artifactId>catalpa-core-common</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>

<dependency>
	<groupId>com.catalpa.core</groupId>
	<artifactId>catalpa-core-datasource</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>

<dependency>
	<groupId>com.catalpa.core</groupId>
	<artifactId>catalpa-core-redis</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```
