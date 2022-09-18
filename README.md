 

### 一 项目背景
在日常开发过程中，时常发现有一些简单的功能会被经常使用到，又没有一个比较好用的功能集合，因此在开发项目是需要反复配置，造成了大量不必要的重复性简单劳动，所以在从过往经验的基础上对日常使用到功能进行了一个通用封装，形成了 **易水公共组件** ，方便后期项目开发。 本着"一次开发,开箱即用"的原则，组件在开发时遵守以下几点：

- 开箱即用，可以选择性开启仅仅使用到的功能
- 基本配置, 在开启本组件功能后，无须二次配置即能使用组件的基本工功能。
- 个性配置，组件提供大量的配置属性，能通过预留的配置属性自定义组件功能。
- 高级配置，在系统默认配置和个性配置不能满足开发需要时，能通过自定义组件中的某些元件实现高级配置。



交流 QQ 群 :<a target="_blank" href="//shang.qq.com/wpa/qunwpa?idkey=a81681f687ced1bf514d6226d00463798cefc0a9559fc7d34f1e17e719ca8573"><img border="0" src="//pub.idqqimg.com/wpa/images/group.png" alt="易水组件交流群" title="易水组件交流群"></a> (群号 624646260)


易水公共组件是基于springboot的高度封装的通用型组件，在对spring security和spring security oauth2高度可定制化的功能封装外，还支持第三方登录和sso单点登录功能，使用户能够快速开启QQ登录和微信登录能力，搭建属于自己的认证/授权中心。

此外，工具还提供各种常见的图形验证码、短信验证码和邮件验证码功能，并支持跨域设置和全局异常捕获功能，实现自定义异常信息提示。

另外，组件还包含swagger接口文档功能，支持一键导出离线接口使用文档。

最后，组件提供了大量丰富的配置属性，支持通过属性配置完成各项功能设置，真正实现零侵入、防止暴力破解的无缝接入功能。

在保证功能灵活可用的基础上，易水公共组件还针对国人的使用习惯进行了一些本地化配置，提供了详细完整的中文使用说明文档。总的来说，易水公共组件在保证单机应用的高效性能同时，还能支持分布式署环境，能自动识别单体应用还是集群应用，是目前主流的微服务开发过程中不可或缺的重要伙伴。

<br/>

### 二 快速启动 

在项目中加入以下依赖

```xml
<dependency>
    <groupId>com.yishuifengxiao.common</groupId>
    <artifactId>common-spring-boot-starter</artifactId>
    <version>5.5.14</version>
</dependency>
```

易水组件已经发布到maven中央仓库，最新版本的依赖可参见 [https://mvnrepository.com/artifact/com.yishuifengxiao.common/common-spring-boot-starter](https://mvnrepository.com/artifact/com.yishuifengxiao.common/common-spring-boot-starter)。

在项目中引入上述依赖之后，就可以直接使用易水组件的相关功能了。

#### 2.1 数据库操作

例如操作数据库时再也不需要编写简单的CURD的操作代码，只需要在项目中加入以下代码即可操作数据库了：



```java
    @Autowired
    private JdbcHelper jdbcHelper;
```

也可以使用静态工具类`JdbcUtil `对数据进行操作。


下面是数据库操作工具`JdbcHelper `的一些典型接口

```java
        /**
	 * 根据主键从指定表查询一条数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param primaryKey 主键
	 * @return 查询到的数据
	 */
	<T> T findByPrimaryKey(Class<T> clazz, Object primaryKey);
        /**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T> POJO类
	 * @param t   查询条件
	 * @return 符合条件的数据
	 */
	<T> T findOne(T t);

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>   POJO类
	 * @param t     查询条件
	 * @param order 排序条件
	 * @return 符合条件的数据
	 */
	<T> List<T> findAll(T t, Order order);

	/**
	 * 根据主键全属性更新方式更新一条数据
	 * 
	 * @param <T> POJO类
	 * @param t   待更新的数据
	 * @return 受影响的记录的数量
	 */
	<T> int updateByPrimaryKey(T t);

	/**
	 * 根据主键可选属性更新方式更新一条数据
	 * 
	 * @param <T> POJO类
	 * @param t   待更新的数据
	 * @return 受影响的记录的数量
	 */
	<T> int updateByPrimaryKeySelective(T t);
        /**
	 * 根据主键删除一条数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      操作的对象
	 * @param primaryKey 主键值
	 * @return 受影响的记录的数量
	 */
	<T> int deleteByPrimaryKey(Class<T> clazz, Object primaryKey);
        /**
	 * 以全属性方式新增一条数据
	 * 
	 * @param <T> POJO类
	 * @param t   待新增的数据
	 * @return 受影响的记录的数量
	 */
	<T> int insert(T t);

	/**
	 * 以可选属性方式新增一条数据
	 * 
	 * @param <T> POJO类
	 * @param t   待新增的数据
	 * @return 受影响的记录的数量
	 */
	<T> int insertSelective(T t);
```


<br/>

#### 2.2 spring security使用

在使用组件的安全管理功能时，需要依赖于spring security的功能。

1 在项目中加入 spring security依赖

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

2 在项目中加入以下启动代码

下面的代码用户应该保证能被 `@ComponentScan`扫描到。

```

@Configuration
@EnableWebSecurity
public class SecurityConfig extends AbstractSecurityConfig {
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
	super.configure(http);
    }

}
```

该代码的示例代码可参见 `com.yishuifengxiao.common.security.SecurityConfig`

实现 `UserDetailsService` 接口，完成自己的授权逻辑，然后按照名字 `userDetailsService`将其注入到spring 之中。

【特别注意】

在用户未按照本步骤配置自己的授权逻辑时，组件会默认进行一个缺省实现。在缺省实现的情况下，用户能使用任意用户名配合密码(12345678)进行登录。
加入上述配置之后，只有组件中内置的默认路径能通过授权，访问其他的url都被重定向到 `/index`这个地址，具体的配置及原因请参照后续说明。
注入的`UserDetailsService`实例的名字必须为`userDetailsService`,否则组件会使用默认的缺省实现。
进行上述配置后，组件的安全管理功能是开启的。

#### 2.3 验证码使用

将验证码工具注入到需要使用到验证码的地方

注入代码如下：


```
  @Autowired
  private CodeProcessor codeProcessor;
```

在注入一个验证码工具后，通过以下代码即可快速生成一个图形验证码。 具体的示例代码如下：


```
@GetMapping("/code/image")
@ResponseBody
public Response<String> image(HttpServletRequest request, HttpServletResponse response){

  try {
  codeProcessor.create(new ServletWebRequest(request, response),CodeType.IMAGE);
   } catch (ValidateException e) {
       return Response.error(e.getMessage());
   }
 return Response.suc();
    
    }
```

在以上代码后，用户即可通过 `http://ip:port/code/image?image=唯一的随机值`   获取图形验证码了。

#### 2.4 swagger-ui

快速启动
在配置文件中加入以下配置即可快速开启swagger-ui功能。
```
yishuifengxiao.swagger.base-package= 需要扫描的控制器代码的路径

```
加入上述配置后即可通过`http://ip:port/doc.html`查看swagger-ui增强文档。

![输入图片说明](https://doc.xiaominfo.com/static/des.png "在这里输入图片标题")

【特别鸣谢】

此项功能中的doc.html界面中功能使用到了刀哥的 swagger-bootstrap-ui 1.9.x 版本中的功能 ，在此特别感谢 刀哥 的大力支持，关于swagger-bootstrap-ui的详细说明请参见刀哥的 [swagger-bootstrap-ui文档](https://doc.xiaominfo.com/guide/),如需要更多强悍的swagger功能，使用刀哥的2.0.x以及以上的原生版本。

#### 2.5 全局异常处理
在默认情况下，全局异常处理功能已经开启，如果要关闭此功能，可以通过以下配置进行

```
yishuifengxiao.error.enable=false
```
再开启此功能的情况下，可以根据不同的异常配置不同的提示信息，配置格式下

```
yishuifengxiao.error.map.异常类=提示信息
```
示例如下

```
yishuifengxiao.error.map.ConstraintViolationException=已有重复数据
```
在上述配置中ConstraintViolationException是异常类的名字，例如ConstraintViolationException、DataIntegrityViolationException和DuplicateKeyException,对于多个需要提示的错误，配置成多行即可，例如

```
yishuifengxiao.error.map.ConstraintViolationException=全局异常捕获到异常信息了
yishuifengxiao.error.map.DataIntegrityViolationException=全局异常捕获到异常信息了
yishuifengxiao.error.map.DuplicateKeyException=全局异常捕获到异常信息了
```
<br/>

#### 2.6 全局参数校验

全局参数校验主要是解决对于开启了参数校验的情况下需要每次手动判断是否存在异常信息导致产生重复代码的问题而创建了一个AOP切面，同意进行参数校验的结果判断的问题。

在默认情况下，全局参数校验结果判断功能是开启的，如果需要关闭此功能，可以通过以下配置进行
```
yishuifengxiao.aop.enable=false
```

在开启全局参数校验结果判断功能的前提下，要想此功能生效，还需要在Controller的方法里携带上`BindingResult`参数。一个简单的例子如下:


```
@AuthUser(required = true)
@ApiOperation(value = "更新用户信息", notes = "根据id更新用户基本信息")
@PostMapping("/updateUser")
@ResponseBody
public Response<Object> updateUser(HttpServletRequest request, HttpServletResponse response, 
@Validated @RequestBody AduitDto aduitDto, BindingResult errors) throws CustomException {

    aduitService.updateUser(aduitDto);
    return Response.suc();
}
```

易水工具组件主要是根据`@ResponseBody`和`BindingResult`设置切面的，要想此功能生效，这两个注解不能忘记。

#### 2.7 oauth2功能

使用 oauth2 功能首先需要按照 安全管理 的步骤开启 spring security 的相关功能。

快速启动

1 在项目中加入 oauth 相关的依赖


```
<dependency>
    <groupId>org.springframework.security.oauth.boot</groupId>
    <artifactId>spring-security-oauth2-autoconfigure</artifactId>
    <version>2.2.0.RELEASE</version>
</dependency>
<dependency>
    <groupId>com.yishuifengxiao.common</groupId>
    <artifactId>common-spring-boot-starter</artifactId>
    <version>4.2.1</version>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

2 在项目加入以下代码

```

@Configuration
public class CustomOauth2Config extends Oauth2Config{

}
```


3 加上`@EnableResourceServer`和 `@EnableAuthorizationServer`注解

完全开启示例代码如下：


```
@Configuration
@EnableWebSecurity
@EnableResourceServer
@EnableAuthorizationServer
public class SecurityConfig extends AbstractSecurityConfig {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
	}

	@Configuration
	public class CustomOauth2Config extends Oauth2Config{

	}

}
```

4 实现`ClientDetailsService`接口，完成自己的认证逻辑

完成自定义实现后，按照名字`customClientDetailsService`向spring中注入一个`ClientDetailsService`实例

【特别注意】在用户未按照(4)中的步骤配置自己的授权逻辑时，组件会默认进行一个缺省实现。在缺省实现的情况下，用户能使用任意用户名配合密码(12345678)进行登录

#### 2.8 异步消息总线

使用该异步消息总线的示例如下：


```
@Component
public class DemoEventBus {
 @Resource
 private EventBus asyncEventBus;
 
 @PostConstruct
 public void init() {
 	asyncEventBus.register(this);
 }
}
```

 
发送消息  
```
asyncEventBus.post("需要发送的数据");
```

接收消息  
```
@Subscribe
 public void recieve(Object data) {
 	// 注意guava是根据入参的数据类型进行接收的
 	// 发送的数据可以被多个接收者同时接收
 } 
```


#### 2.9 第三方登陆
1 在项目的 pom 里增加 spring social 相关的依赖

```

<dependency>
    <groupId>org.springframework.social</groupId>
    <artifactId>spring-social-core</artifactId>
    <version>${spring-social.version}</version>
</dependency>

<dependency>
    <groupId>org.springframework.social</groupId>
    <artifactId>spring-social-config</artifactId>
    <version>${spring-social.version}</version>
</dependency>

<dependency>
    <groupId>org.springframework.social</groupId>
    <artifactId>spring-social-security</artifactId>
    <version>${spring-social.version}</version>
</dependency>

<dependency>
    <groupId>org.springframework.social</groupId>
    <artifactId>spring-social-web</artifactId>
    <version>${spring-social.version}</version>
</dependency>
```

2 在项目的任意一个 `@Configuration`下增加 `@EnableSocial` 注解

3 将`SpringSocialConfigurer`的实例对象注入到 spring security 之中。

4 在属性配置文件增加相关的配置

一个完整的配置文件的示例如下：

```
@Configuration
@EnableWebSecurity
@EnableSocial
public class SecurityConfig extends AbstractSecurityConfig {

@Autowired
private  SpringSocialConfigurer socialSecurityConfig;

@Override
protected void configure(HttpSecurity http) throws Exception {
	// 调用父类中的默认配置
    super.configure(http);

    http.apply(socialSecurityConfig);
    }

}
```

 **QQ 登录** 

开启 QQ 登录首先需要在 QQ 互联  申请账号和密码

接下来，在项目的配置文件里增加以下配置


```
# spring social拦截器拦截的标志,默认为 /auth
yishuifengxiao.security.social.filter-processes-url=/callback
# QQ登录的appId
yishuifengxiao.security.social.qq.app-id=QQ互联上申请的appId
# QQ登录的appSecret
yishuifengxiao.security.social.qq.app-secret=QQ互联上申请的appId对应的appSecret
# QQ登录的成功后的跳转路径
yishuifengxiao.security.social.qq.register-url=/registerUrl
# QQ登录的服务提供商标志，默认为qq
yishuifengxiao.security.social.qq.provider-id=qq
```


在完成上述配置 ，访问 `http：//ip:port/callback/qq`即可进行 QQ 登录流程了

注意： 访问的的 url 中的`/callback/qq` 由 `yishuifengxiao.social.filter-processes-url` 和`yishuifengxiao.social.qq.provider-id` 两部分共同组成。

 **微信登录** 

配置微信登录只需要在上述基础上进行一下配置即可


```
# 微信登录的appId
yishuifengxiao.security.social.weixin.app-id=微信开发平台上申请的appId
# 微信登录的appSecret
yishuifengxiao.security.social.weixin.app-secret=微信开发平台申请的appId对应的appSecret
# 微信登录的成功后的跳转路径
yishuifengxiao.security.social.weixin.register-url=/registerUrl
# 微信登录的服务提供商标志，默认为weixin
yishuifengxiao.security.social.weixin.provider-id=weixin
```


在完成上述配置 ，访问`http：//ip:port/callback/weixin`即可进行微信登录流程了

### 三 组件功能


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
> - 支持token自动续签
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
> - 支持token自动续签
> - 内置全局异常功能
> - 内置token解析功能，能够根据特定算法从token中解析出用户信息
> - 内置多种token提取方式，支持从url、请求头以及session中提取token
> - 支持oauth2模式下单用户多终端登录管理功能
- 第三方登陆
> - 默认支持QQ登录
> - 默认支持微信登录功能
> - 支持其他的第三方登录

<br/>

### 四 资源链接


[易水公共组件官方文档地址](http://doc.yishuifengxiao.com)：http://doc.yishuifengxiao.com

[易水公共组件源码地址](https://gitee.com/zhiyubujian/common-starter)：https://gitee.com/zhiyubujian/common-starter

[易水风萧个人博客](http://www.yishuifengxiao.com) http://www.yishuifengxiao.com


<br/><br/>





### 五 相关博客


1. [swagger ui快速入门教程](http://www.yishuifengxiao.com/2019/10/31/swagger-ui%E5%BF%AB%E9%80%9F%E5%85%A5%E9%97%A8%E6%95%99%E7%A8%8B/)

2. [基于易水公共组件的全局异常捕获](http://www.yishuifengxiao.com/2019/10/31/%E5%9F%BA%E4%BA%8E%E6%98%93%E6%B0%B4%E5%85%AC%E5%85%B1%E7%BB%84%E4%BB%B6%E7%9A%84%E5%85%A8%E5%B1%80%E5%BC%82%E5%B8%B8%E6%8D%95%E8%8E%B7/)

3. [基于易水公共组件的验证码快速入门教程](http://www.yishuifengxiao.com/2019/10/31/%E5%9F%BA%E4%BA%8E%E6%98%93%E6%B0%B4%E5%85%AC%E5%85%B1%E7%BB%84%E4%BB%B6%E7%9A%84%E9%AA%8C%E8%AF%81%E7%A0%81%E5%BF%AB%E9%80%9F%E5%85%A5%E9%97%A8%E6%95%99%E7%A8%8B/)

4. [oauth2快速入门教程](http://www.yishuifengxiao.com/2019/11/01/oauth2%E5%BF%AB%E9%80%9F%E5%85%A5%E9%97%A8%E6%95%99%E7%A8%8B/)

5. [springboot整合单点登录sso](http://www.yishuifengxiao.com/2019/10/25/springboot%E6%95%B4%E5%90%88%E5%8D%95%E7%82%B9%E7%99%BB%E5%BD%95sso/)

6. [搭建基于易水公共组件的资源服务器](http://www.yishuifengxiao.com/2019/10/30/%E6%90%AD%E5%BB%BA%E5%9F%BA%E4%BA%8E%E6%98%93%E6%B0%B4%E5%85%AC%E5%85%B1%E7%BB%84%E4%BB%B6%E7%9A%84%E8%B5%84%E6%BA%90%E6%9C%8D%E5%8A%A1%E5%99%A8/)

7. [spring security之获取当前用户信息](http://www.yishuifengxiao.com/2019/10/15/spring-security%E4%B9%8B%E8%8E%B7%E5%8F%96%E5%BD%93%E5%89%8D%E7%94%A8%E6%88%B7%E4%BF%A1%E6%81%AF/)

8. [基于易水公共组件的权限管理系统](http://www.yishuifengxiao.com/2019/10/31/%E5%BF%AB%E9%80%9F%E6%90%AD%E5%BB%BA%E5%9F%BA%E4%BA%8E%E6%98%93%E6%B0%B4%E5%85%AC%E5%85%B1%E7%BB%84%E4%BB%B6%E7%9A%84%E6%9D%83%E9%99%90%E7%AE%A1%E7%90%86%E7%B3%BB%E7%BB%9F/)

<br/><br/>

 
登录界面
![登录界面](https://images.gitee.com/uploads/images/2019/1113/090913_bbe000a5_400404.png "login.png")

用户管理
![用户管理](https://images.gitee.com/uploads/images/2019/1113/090958_0d4c4c09_400404.png "user.png")

终端管理
![终端管理](https://images.gitee.com/uploads/images/2019/1113/091049_4442a7eb_400404.png "client.png")

在线用户管理
![在线用户管理](https://images.gitee.com/uploads/images/2019/1113/091145_f5415447_400404.png "online.png")

登录记录
![登录记录](https://images.gitee.com/uploads/images/2019/1113/091209_a4c911fb_400404.png "record.png")

说明文档
![说明文档](https://images.gitee.com/uploads/images/2019/1113/091240_2bac1057_400404.png "swagger.png")

oauth2 密码模式
![oauth2 密码模式](https://images.gitee.com/uploads/images/2019/1113/092322_8e7b4af4_400404.png "oauth2-password.png")
