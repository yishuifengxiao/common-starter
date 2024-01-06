/**
 *
 */
package com.yishuifengxiao.common.swagger;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.License;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * swagger扩展支持属性配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "yishuifengxiao.swagger")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwaggerProperties {

    /**
     * 是否开启swagger-ui增强的功能，默认开启
     */
    private Boolean enable = true;
    /**
     * swagger 扫描的根路径
     */
    private String basePackage;
    /**
     * swagger 文档的标题
     */
    private String title;
    /**
     * swagger 文档的描述
     */
    private String description;
    /**
     * swagger 文档的中组织的链接
     */
    private String termsOfServiceUrl = "http://www.yishuifengxiao.com/";
    /**
     * swagger 文档的分组名
     */
    private String groupName = "default";
    /**
     * 版本号
     */
    private String version = "v1.0.0";

    /**
     * 项目联系人
     */
    private String contactUser = "yishuifengxiao";

    /**
     * 项目的url
     */
    private String contactUrl = "http://www.yishuifengxiao.com/";

    /**
     * 项目联系邮箱
     */
    private String contactEmail = "zhiyubujian@163.com";

    /**
     * 访问swagger页面时需要输入的用户名，默认值为admin，如果该用户名为空表示则不开启认证功能。只有username和password参数均不为空时才会开启认证功能
     */
    private String username = "admin";

    /**
     * 访问swagger页面时需要输入的密码，如果该密码为空表示则不开启认证功能。只有username和password参数均不为空时才会开启认证功能
     */
    private String password;

    /**
     * 附加信息
     */
    private ExternalDocumentation externalDocumentation = new ExternalDocumentation();

    private License license=new License();

    /**
     * 是否显示加载日志，默认为false
     */
    private Boolean showDetail = false;


}
