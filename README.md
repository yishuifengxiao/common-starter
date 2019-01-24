&#160;&#160;&#160;&#160;封装本工具的主要目的是将日常springboot项目开发过程中经常使用到一些重复配置集合起来，减少每次项目过程中的代码搬运工作。

本项目的maven坐标为:

       
```
 	<dependency>
			<groupId>com.yishuifengxiao.common</groupId>
			<artifactId>common-spring-boot-starter</artifactId>
			<version>${common-spring-boot-starter.version}</version>
	</dependency>
```

**包含的功能**

1. springMVC 全局异常捕获
1. swagger-ui自动化配置
1. 数据库使用的相关依赖

**参数配置说明:**

开启swagger-ui :
```
yishuifengxiao.swagger.basePackage=swagger-ui 扫描路径
```
其他参数说明:

```
yishuifengxiao.swagger.title=swagger 文档的标题
yishuifengxiao.swagger.description=文档的描述
yishuifengxiao.swagger.termsOfServiceUrl=swagger 文档的中组织的链接
yishuifengxiao.swagger.groupNmae= swagger 文档的分组名
yishuifengxiao.swagger.version=版本号
yishuifengxiao.swagger.contact.name=作者名字
yishuifengxiao.swagger.contact.url=作者的介绍连接
yishuifengxiao.swagger.contact.email=作者的邮箱
```
高级配置

```
yishuifengxiao.swagger.contact.auths[0].name=Authorization
yishuifengxiao.swagger.contact.auths[0].description=自定义必填请求头
yishuifengxiao.swagger.contact.auths[0].modelRef=string
yishuifengxiao.swagger.contact.auths[0].parameterType=header
yishuifengxiao.swagger.contact.auths[0].required=false
```

此配置参见swagger ui的ParameterBuilder用法配置


<br/><br/><br/>

&#160;&#160;&#160;&#160; 本自定义启动封装的插件有

- pagehelper分页插件



```
        <dependency>
			<groupId>com.github.pagehelper</groupId>
			<artifactId>pagehelper-spring-boot-starter</artifactId>
			<version>1.2.10</version>
		</dependency>
```


该插件的使用说明参见

    [github使用说明](https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md)
    
    [github源码](https://github.com/pagehelper/pagehelper-spring-boot/blob/master/README.md)


- tk.myabtis


```
   <dependency>
        <groupId>tk.mybatis</groupId>
        <artifactId>mapper-spring-boot-starter</artifactId>
        <version>1.2.4</version>
    </dependency>
```
该插件的使用说明参见

    [github源码](https://github.com/abel533/mapper-boot-starter)
    
    [码云使用文档](https://gitee.com/free/Mapper/wikis/Home)
    
    [git使用文档](https://github.com/abel533/Mapper/wiki)
        
        
- druid连接池


```
	<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid-spring-boot-starter</artifactId>
			<version>${druid.version}</version>
	</dependency>
```


该插件的使用说明参见
    
    [github源码](https://github.com/drtrang/druid-spring-boot)
    
    [常见问题] (https://github.com/alibaba/druid/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98)


- 易水通用工具包



```
	<dependency>
			<groupId>com.yishuifengxiao.common</groupId>
			<artifactId>common-tool</artifactId>
			<version>${yishuifengxiao.tool.version}</version>
	</dependency>
```

该插件的使用说明参见
    
    [码云源码](https://gitee.com/zhiyubujian/tool)


此外，本启动依赖的其他插件还有

```
	    <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		
		<dependency>
			<groupId>org.jolokia</groupId>
			<artifactId>jolokia-core</artifactId>
		</dependency>
		
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
		</dependency>
```


## 参与贡献
1. Fork 本项目
1. 新建 Feat_xxx 分支
1. 提交代码
1. 新建 Pull Request

##有问题反馈
在使用中有任何问题，欢迎反馈给我，可以用以下联系方式跟我交流

* 邮件(zhiyubujian#163.com, 把#换成@)
* QQ: 979653327
* 开源中国: [@止于不见](https://gitee.com/zhiyubujian)