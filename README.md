# birds-novel 飞鸟小说

#### 介绍
飞鸟小说建站系统，支持分布式爬虫


#### 软件架构
软件架构说明（待完善）


#### 技术选型

| 技术                 | 说明                                                         
| -------------------- | ---------------------------
| SpringBoot           | Spring应用快速开发脚手架  
| WebMagic             | 爬虫框架
| Hutool               | 简化开发工具包
| ikanalyzer           | 分词器
| MongoDb              | 数据库
| Shiro                | 安全框架  
| Redis                | 分布式缓存                                                
| Redisson             | 实现rpc远程调用、消息队列、延迟消息队列、topic                                     
| Lombok               | 简化对象封装工具                                                                               
| Thymeleaf            | 模板引擎     
| Layui                | 前端UI   
| Vue                  | 前端UI 
| Vant                 | 前端UI
| Pear Admin           | 前端UI  

#### 开发计划
- [x] 0.1
    - [x] 数据采集-任务执行
    - [x] 数据采集-插队任务
    - [x] wap模板
    - [x] 阅读数据统计
    - [x] 后台管理-推荐管理
    - [x] 后台管理-采集规则管理
- [ ] 0.2
    - [ ] 后台管理-用户管理
    - [ ] 后台管理-数据统计展示
    - [ ] 采集优化
- [ ] 0.3
    - [ ] 后台管理-采集监控
    - [ ] app


#### 项目演示站
项目演示站（待完善）

###### 前端展示

首页：

![首页](https://img-blog.csdnimg.cn/e4a58943acd24ddc969d1167e8cf18c7.png?x-oss-process=type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6Kej5b-n5bCP56ul5a2Q,size_11,color_FFFFFF,t_70,g_se,x_16)

分类：

![分类](https://img-blog.csdnimg.cn/0e9673573a7c43038e375e819278513a.png?x-oss-process=type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6Kej5b-n5bCP56ul5a2Q,size_11,color_FFFFFF,t_70,g_se,x_16)

排行：

![排行](https://img-blog.csdnimg.cn/77ecca52f61e4e44bb891b3b82b63945.png?x-oss-process=type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6Kej5b-n5bCP56ul5a2Q,size_11,color_FFFFFF,t_70,g_se,x_16)

书架：

![书架](https://img-blog.csdnimg.cn/8d98a02c80fd4d6a97863cf9feb1a796.png?x-oss-process=type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6Kej5b-n5bCP56ul5a2Q,size_11,color_FFFFFF,t_70,g_se,x_16)

用户登录：

![用户登录](https://img-blog.csdnimg.cn/2b8e7145b33f4488903ebbe0daff066e.png?x-oss-process=type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6Kej5b-n5bCP56ul5a2Q,size_11,color_FFFFFF,t_70,g_se,x_16)

简介：

![简介](https://img-blog.csdnimg.cn/2c8727265ea94b20a3962f77df537f91.png?x-oss-process=type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6Kej5b-n5bCP56ul5a2Q,size_11,color_FFFFFF,t_70,g_se,x_16)

阅读：

![阅读](https://img-blog.csdnimg.cn/ce771ce51bb047b592a9c5cf7503485e.png?x-oss-process=type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6Kej5b-n5bCP56ul5a2Q,size_11,color_FFFFFF,t_70,g_se,x_16)

章节目录：

![章节目录](https://img-blog.csdnimg.cn/fae7022867aa4621ac85ab13a726b42d.png?x-oss-process=type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6Kej5b-n5bCP56ul5a2Q,size_11,color_FFFFFF,t_70,g_se,x_16)

###### 后台管理

登录：

![登录](https://img-blog.csdnimg.cn/f0f8fc5a0b634f59938562b90934ecbf.png?x-oss-process=type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6Kej5b-n5bCP56ul5a2Q,size_20,color_FFFFFF,t_70,g_se,x_16)

推荐设置：

![推荐设置](https://img-blog.csdnimg.cn/9109889d02fb458b9a03743df9f3517c.png?x-oss-process=type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6Kej5b-n5bCP56ul5a2Q,size_20,color_FFFFFF,t_70,g_se,x_16)

书籍查询：

![书籍查询](https://img-blog.csdnimg.cn/1fde1f3e875d42b19263f2a8a027d013.png?x-oss-process=type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6Kej5b-n5bCP56ul5a2Q,size_20,color_FFFFFF,t_70,g_se,x_16)

规则管理：

![规则管理](https://img-blog.csdnimg.cn/8beda25d5ba044f7abf33552d72004db.png?x-oss-process=type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6Kej5b-n5bCP56ul5a2Q,size_20,color_FFFFFF,t_70,g_se,x_16)

规则新增、复制：

![规则新增](https://img-blog.csdnimg.cn/40a04d793ad34d4388958aedeb5a81db.png?x-oss-process=type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6Kej5b-n5bCP56ul5a2Q,size_20,color_FFFFFF,t_70,g_se,x_16)

xpath规则测试：

![xpath规则测试](https://img-blog.csdnimg.cn/9c7b4488e6024fa3ab1e021f7c88d402.png?x-oss-process=type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6Kej5b-n5bCP56ul5a2Q,size_20,color_FFFFFF,t_70,g_se,x_16)

#### 安装教程
安装教程（待完善）


#### 催更邮箱

caobincoding@163.com


#### 赞赏支持

- 开源不易，且行且珍惜。
- 您的赞赏与鼓励，意见及建议是我最大的动力

![打赏码.png](https://s2.loli.net/2022/02/08/AxEF3LIK9vJrp7P.png)


#### 免责声明
本项目仅供学习，请勿用于商业盈利。
因使用本系统而引致的任何意外、疏忽、合约毁坏、诽谤、版权或知识产权侵犯及其所造成的任何损失，本人概不负责，亦概不承担任何民事或刑事法律责任。

