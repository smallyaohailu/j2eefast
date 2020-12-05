## 下个版本2.2.0 更新预告，主要完善代码生成器 (单表 主子表 树表/同步/异步) 一键生成 解放繁琐重复工作节省80%工作、以后版本都会同时兼容Mysql与PostgreSql. 请持续关注，点个Star！！！
<p align="center">
<img width="280" height="280" src="https://images.gitee.com/uploads/images/2020/0927/225746_1bb32a4b_1816537.png">
</p>
<p align="center">
<img src="https://img.shields.io/badge/%E9%AB%98%E6%95%88-%E5%BF%AB%E9%80%9F-brightgreen">
<img  src="https://img.shields.io/badge/%E5%AE%89%E5%85%A8-%E6%BA%90%E7%A0%81%E5%8F%AF%E6%8E%A7-blueviolet">
<a  href="https://jq.qq.com/?_wv=1027&k=5xTlnN6" target="_blank"><img  src="https://img.shields.io/badge/QQ%E6%8A%80%E6%9C%AF%E7%BE%A4-805468934-orange"></a>
</p>

---

J2eeFAST 是一个 Java EE 企业级快速开发平台， **致力于打造中小企业最好用的开源免费的后台框架平台** 。 系统基于（Spring Boot、Spring MVC、Apache Shiro、MyBatis-Plus、Freemarker、Bootstrap、AdminLTE）经典技术开发， 系统内置核心模块包含众多常用基础功能(在线代码生成功能、组织机构、角色用户、菜单及按钮授权、数据权限、系统参数、license认证、BPM工作流等)，  **让你用最低的成本、最高的效率，开发项目，她也适合新手朋友练手** 。同时具备， **界面简洁美观、高效、安全、源码可控、版本迭代快、免费技术交流群** 等特点。她适用于所有Web应用，她会成为你快速开发项目的好帮手。


#### 软件架构 

1.  _核心框架：Spring Boot 2.X
2.   _安全框架：Apache Shiro 1.X
3.   _模板引擎：Freemarker_ 
4.   _前端：AdminLTE 2.3.8, Bootstrap 3.3.7, Bootstrap-Table 1.11.0, JQuery 3.3.1_ 
5.   _持久层框架：MyBatis-Plus 3.3.1_ 
6.   _定时任务: Quartz_ 
7.   _数据库连接池：Druid 1.10.1_ 
9.   _数据库: Mysql5.7_
10.  _分布式缓存数据库: Redis 4.0.9_
11.  _工具类：Hutool 4.5.8_ 
12.  _工作流引擎:flowable 6.4.2_

#### 演示地址

1.  演示地址： http://www.j2eefast.com <br>账号 ：admin 密码：admin
2.  功能还在陆续更新中......

#### [内置功能](https://www.yuque.com/u1174484/j2eefast/wtakf0)


#### ****[搭建文档](https://www.yuque.com/u1174484/j2eefast/kt0nbi)**** 
#### 参与贡献

1.  本项目设计思路借鉴了当前Gitee中 开源项目中后台管理框架众多优秀项目的设计思路
2.  在此特别感谢 JeeSite、RuoYi、Mybatis-Plus 原作者的贡献付出！
3.  在此也感谢众多小伙伴提出项目问题及贡献的代码.

####  版本更新
* 本次更新
    * `2020-11-30 v2.1.1 ` 
    - 系统核心功能全面支持PostgreSql数据库.
    - 修复表格选择组件查询不能下拉问题
    - 升级阿里云OSS依赖
    - 组件支持阿里云OSS
    - 调整项目结构移除system模块
    - 修复多选表格组件回显勾选问题
    - 增强输入组件支持日期时间选择
    - 细化前端模板引擎libs引入JS,按需引入减少无关JS引入
    - 优化表格边框设置、允许表格列拉伸、支持通过参数配置是否需要显示边框
    - 新增表单单元格过长使用隐藏函数可以复制单元格内容
    - 升级Spring Boot 2.2.10.RELEASE 版本阻止安全漏洞(CVE-2020-5421)
    - 修复日志查询业务表格字段过长显示不友好问题
    - 修复安全认证关闭，登录异常问题
    - 修复树表父子节点错乱问题
    - 修改登录图片修改成base64
    - 修复角色管理序号字段排序问题
    - 优化登录安全认证
    - 新增文件预览器支持gif预览
    - 移除多余文件
    - 优化数据库自动填充delFlag字段
    - 修复mybatis-plus配置无效问题
    - 优化Shiro 密钥问题
    - 升级Oshi到最新版本 5.2.5
    - 升级Shiro到最新版 1.7.0,阻止权限绕过漏洞
    - 优化其他细节
    
2. [更新日志](https://www.yuque.com/u1174484/j2eefast/yscyux) 

#### 关于系统
* J2eeFAST (快速开发开源系统)名字由来:包含作者对于软件开发的美好幻想，希望软件开发变的快速而简单，每个人都能分享自己的技术从而达到快速又强大的软件系统,让我们有更多的时间去陪伴家人!你可能在本系统中看到众多优秀开源项目的影子，因为她本身集成了众多优秀的开源项目精华功能，在这种环境中成长的，但是她目前还有很多缺点，希望大神们嘴下留情。如果你也喜欢开源、喜欢本项目,作者欢迎您的加入, J2eeFAST会因为您的加入而变的更加完善与丰富!
* 如果本项目对你有帮助,[请点击Star收藏](https://gitee.com/zhouhuanOGP/J2EEFAST),**本项目会长期维护**,若你在使用中有任何问题或建议,欢迎在[码云issue提交问题](https://gitee.com/zhouhuanOGP/J2EEFAST/issues)作者会第一时间处理，让我们一起完善J2eeFAST
* 官网: [http://www.j2eefast.com](http://www.j2eefast.com)
* 关于文档：[语雀 文档](https://www.yuque.com/u1174484/j2eefast/tv3kee)
* 关于更新：项目现在会放缓更新优化细节,演示网站会不定期暂停访问,带来不便尽情谅解!
* QQ群：[805468934](https://jq.qq.com/?_wv=1027&k=5xTlnN6)
#### 免责声明：
 1. **本项目代码全部开源，免费。但请遵循开源协议** 
1. 不得将 J2eeFAST于危害国家安全、荣誉和利益的行为，不能以任何形式用于非法为目的的行为,否则后果自负
1. J2eeFAST前身主要用于银行项目,本身很注重安全因素,可以从项目登陆可以看出。再者本身项目100%开源，但是您任需了解是软件皆有漏洞，任何人都无法保证软件100%没有漏洞。所以由本软件漏洞造成损失不予赔偿，同时也不承担任何因使用本软件而产生的相关法律责任。也请在软件上线前进行必要的安全监测，避免安全问题发生。

#### 参与开发
* 谢谢大家支持，如果你希望参与开发，欢迎fork本项目，并Pull Request您的commit。

>  **码云Gitee(主): [https://gitee.com/zhouhuanOGP/J2EEFAST](https://gitee.com/zhouhuanOGP/J2EEFAST)** 
> 
>  **Github(辅): [https://github.com/zhouhuan751312/J2EEFAST](https://github.com/zhouhuan751312/J2EEFAST)** 

#### 项目演示
![输入图片说明](https://images.gitee.com/uploads/images/2020/0908/084001_75d40312_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0908/084020_e10905d8_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0908/084104_5497a596_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0908/084221_70975dcf_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0810/142932_30f1f459_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0810/143020_d4583af2_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0810/143057_462c279f_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0810/143152_b0ff9fd0_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0908/084132_00c4292f_1816537.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0908/084145_791f453f_1816537.png "屏幕截图.png")