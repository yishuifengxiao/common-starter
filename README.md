在日常开发过程中，发现有一个重要的功能会被经常使用到，但是又没有一个比较好用的功能集合，在开发项目是需要反复配置，造成了大量不必要的重复性简单劳动，因此对日常使用到功能进行了一个通用封装，形成了【易水公共组件】(以后简称组件)，方便后期项目开发。 本着"一次开发,开箱即用"的原则，组件在开发时遵守以下几点：

- 开箱即用
- 基本配置,即在开启本组件功能后，无须二次配置即能使用组件的基本工功能。
- 个性配置，组件提供大量的配置属性，能通过预留的配置属性自定义组件功能。
- 高级配置，在系统默认配置和个性配置不能满足开发需要时，能通过自定义组件中的某些元件实现高级配置。



交流 QQ 群 :<a target="_blank" href="//shang.qq.com/wpa/qunwpa?idkey=a81681f687ced1bf514d6226d00463798cefc0a9559fc7d34f1e17e719ca8573"><img border="0" src="//pub.idqqimg.com/wpa/images/group.png" alt="易水组件交流群" title="易水组件交流群"></a> (群号 624646260)


易水公共组件是基于spring security和spring security oauth2上的二次开发，除了对spring security和oauth2高度可定制化的功能封装外，还集成了在日常开发过程需要经常使用的swagger-ui和验证码功能以及项目中必不可少的全局异常捕获功能，另外，易水公共组件还支持第三方登录功能，对单点登录(sso)功能做了一个简单的默认实现，使用用户能够快速开启QQ登录和微信登录能力。

在保证功能灵活可用的基础上，易水公共组件还针对国人的使用习惯进行了一些本地化配置，提供了详细完整的中文使用说明文档。总的来说，易水公共组件在保证单机应用的高效性能同时，还能支持分部署环境，是目前主流的微服务开发过程中不可或缺的重要伙伴。

**快速使用**


```
    <dependency>
        <groupId>com.yishuifengxiao.common</groupId>
        <artifactId>common-spring-boot-starter</artifactId>
        <version>4.1.2</version>
    </dependency>
```

易水风萧通用组件主要包含以下一些常用功能：

- swagger-ui文档
> - 快速启动/关闭swagger-ui功能
> - 支持自定义swagger-ui启动参数
> - 支持生成离线文档
- 全局跨域支持
> - 快速启动/关闭跨域功能
> - 支持自定义跨域属性设置
- 全局异常捕获
> - 包含各种基本的异常信息捕获
> - 支持自定义异常提示信息
- 通用辅助工具
> - 默认支持各种字符处理方式，如去掉空白字符串，去掉非法字符
> - 快速获取spring上下文
> - 快速获取spring中的实例对象
- 验证码功能
> - 默认支持图形验证码，短信验证码和邮件验证码
> - 支持前后端分离情况下的验证码使用
> - 支持自定义验证码生成策略
> - 支持自定义验证码存储策略
> - 支持分布式与集群功能
- spring security
> - 大量丰富灵活的配置属性
> - 支持验证码功能，快速给任意资源设置各种类型的验证码
> - 简单清晰的资源管理方式，能够轻松管理系统中的应用资源
> - 支持短信登录
> - 支持修改表单登录参数
> - 支持自定义登录流程
> - 支持自定义权限配置
> - 支持并发登录管理
> - 支持"记住我"功能
> - 防止密码暴力破解
> - 内置各种消息时间，快速感知各种操作动作
- oauth2
> - 支持自定义token生成策略
> - 支持自定义token存储策略
> - 内置全局异常功能
> - 内置token解析功能，能够根据特定算法从token中解析出用户信息
> - 内置多种token提取方式，支持从url、请求头以及session中提取token
> - 支持oauth2模式下单用户多终端登录管理功能
- 第三方登陆
> - 默认支持QQ登录
> - 默认支持微信登录功能
> - 支持其他的第三方登录


**资源链接**

[易水公共组件官方文档地址](http://doc.yishuifengxiao.com)：http://doc.yishuifengxiao.com

[易水公共组件源码地址](https://gitee.com/zhiyubujian/common-starter)：https://gitee.com/zhiyubujian/common-starter

[易水风萧个人博客](http://www.yishuifengxiao.com) http://www.yishuifengxiao.com

**相关博客**

1. [swagger ui快速入门教程](http://www.yishuifengxiao.com/2019/10/31/swagger-ui%E5%BF%AB%E9%80%9F%E5%85%A5%E9%97%A8%E6%95%99%E7%A8%8B/)

2. [基于易水公共组件的全局异常捕获](http://www.yishuifengxiao.com/2019/10/31/%E5%9F%BA%E4%BA%8E%E6%98%93%E6%B0%B4%E5%85%AC%E5%85%B1%E7%BB%84%E4%BB%B6%E7%9A%84%E5%85%A8%E5%B1%80%E5%BC%82%E5%B8%B8%E6%8D%95%E8%8E%B7/)

3. [基于易水公共组件的验证码快速入门教程](http://www.yishuifengxiao.com/2019/10/31/%E5%9F%BA%E4%BA%8E%E6%98%93%E6%B0%B4%E5%85%AC%E5%85%B1%E7%BB%84%E4%BB%B6%E7%9A%84%E9%AA%8C%E8%AF%81%E7%A0%81%E5%BF%AB%E9%80%9F%E5%85%A5%E9%97%A8%E6%95%99%E7%A8%8B/)

4. [oauth2快速入门教程](http://www.yishuifengxiao.com/2019/11/01/oauth2%E5%BF%AB%E9%80%9F%E5%85%A5%E9%97%A8%E6%95%99%E7%A8%8B/)

5. [springboot整合单点登录sso](http://www.yishuifengxiao.com/2019/10/25/springboot%E6%95%B4%E5%90%88%E5%8D%95%E7%82%B9%E7%99%BB%E5%BD%95sso/)

6. [搭建基于易水公共组件的资源服务器](http://www.yishuifengxiao.com/2019/10/30/%E6%90%AD%E5%BB%BA%E5%9F%BA%E4%BA%8E%E6%98%93%E6%B0%B4%E5%85%AC%E5%85%B1%E7%BB%84%E4%BB%B6%E7%9A%84%E8%B5%84%E6%BA%90%E6%9C%8D%E5%8A%A1%E5%99%A8/)

7. [spring security之获取当前用户信息](http://www.yishuifengxiao.com/2019/10/15/spring-security%E4%B9%8B%E8%8E%B7%E5%8F%96%E5%BD%93%E5%89%8D%E7%94%A8%E6%88%B7%E4%BF%A1%E6%81%AF/)

8. [基于易水公共组件的权限管理系统](http://www.yishuifengxiao.com/2019/10/31/%E5%BF%AB%E9%80%9F%E6%90%AD%E5%BB%BA%E5%9F%BA%E4%BA%8E%E6%98%93%E6%B0%B4%E5%85%AC%E5%85%B1%E7%BB%84%E4%BB%B6%E7%9A%84%E6%9D%83%E9%99%90%E7%AE%A1%E7%90%86%E7%B3%BB%E7%BB%9F/)



