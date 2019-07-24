在日常开发过程中，发现有一个重要的组件会被经常使用到，但是又没有一个比较好用的功能集合，在开发项目是需要反复配置，造成了大量不必要的重复性简单劳动，因此对日常使用到功能进行了一个通用封装，形成了【易水风萧通用组件】，方便后期项目开发。
易水风萧通用组件主要包含以下一些常用功能：
- swagger-ui文档
- 全局跨域支持
- 全局异常捕获
- 通用辅助工具
- 验证码功能
- spring security
- oauth2
- spring social (QQ登录 、微信登录)

在使用 易水风萧通用组件 之前，需要先在项目的pom依赖里加入以下配置：


```
<dependency>
    <groupId>com.yishuifengxiao.common</groupId>
    <artifactId>common-spring-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

注意：在使用时请参考 [https://mvnrepository.com/artifact/com.yishuifengxiao.common/common-spring-boot-starter](hhttps://mvnrepository.com/artifact/com.yishuifengxiao.common/common-spring-boot-starter) 将版本号替换为最新版本。

> **本文档针对于3.0.0及后续版本，由于3.0.0版本更新内容较多，对于历史版本本说明文档可能会有较大出入**。


## 一 swagger-ui文档

### 1.1 快速启动

在配置文件中加入以下配置即可快速开启swagger-ui功能。


```
yishuifengxiao.swagger.base-package= 需要扫描的控制器代码的路径
```

加入上述配置后，即可通过  

http://ip:port/doc.html

或者 

http://ip:port/swagger-ui.html

查看自己的swagger-ui文档了。

此外，也可以通过http://ip:port/v2/api-docs查看元数据

> 这里只是简化了swagger-ui的扫描注解，对于软件开发过程中必须swagger-ui其他API注解任然不可省略。

下面是一个简单的swagger-ui配置文档示例


```
@Api(value = "【测试接口】测试接口", tags = {"测试接口"})
@Valid
@Controller
@RequestMapping
@Slf4j
public class WebConftroller  {

    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "登录的用户名"),
            @ApiImplicitParam(name = "loginIp", value = "登录ip"),
            @ApiImplicitParam(name = "pass", value = "登录结果,true表示成功，false失败"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小,分页的大小不能小于1,默认值为20"),
            @ApiImplicitParam(name = "pageNum", value = "当前页的页码,页码的大小不能小于1，默认值为1")})
    @ApiOperation(value = "分页查询登录记录", notes = "分页查询登录记录")
    @GetMapping("/demo")
    @ResponseBody
    public Response<String> findPage(
        HttpServletRequest request, HttpServletResponse response,
        @RequestParam(value = "username", required = false) String username,
        @RequestParam(value = "loginIp", required = false) String loginIp,
        @RequestParam(value = "pass", required = false) Boolean pass,
        @RequestParam(name = "pageSize", defaultValue = "20", required = false) Integer pageSize,
        @RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum) {


        return Response.suc();

    }

}
```



**特别鸣谢**： 此项功能中的doc.html界面中功能使用到了刀哥的 swagger-bootstrap-ui 中的功能 ，在此特别感谢 刀哥 的大力支持，关于swagger-bootstrap-ui的详细说明请参见刀哥的 [swagger-bootstrap-ui文档](https://doc.xiaominfo.com/guide/)
### 1.2 常规配置


```
# swagger-ui文档的标题
yishuifengxiao.swagger.title=API接口文档
# swagger-ui文档描述
yishuifengxiao.swagger.description=易水风萧 接口说明文档
#swagger-ui 项目服务的url
yishuifengxiao.swagger.terms-of-service-url=http://www.yishuifengxiao.com/
# swagger-ui 文档分组的名字
yishuifengxiao.swagger.group-name=default
# swagger-ui 文档版本
yishuifengxiao.swagger.version=1.0.0
# 项目联系人名字
yishuifengxiao.swagger.contact-user=yishuifengxiao
# 项目联系的url
yishuifengxiao.swagger.contact-url=http://www.yishuifengxiao.com/
# 项目联系邮箱
yishuifengxiao.swagger.contact-email=zhiyubujian@163.com
```

以上常规配置都有缺省默认值，用户在使用 易水风萧通用组件 时，如果没有特别需要，使用默认配置即可。


更详细的说明文档请参见[易水通用组件说明文档](http://www.yishuifengxiao.com/2019/07/24/%E6%98%93%E6%B0%B4%E9%80%9A%E7%94%A8%E7%BB%84%E4%BB%B6/)