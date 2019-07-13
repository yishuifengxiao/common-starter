/**
 * 
 */
package com.yishuifengxiao.common.properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import springfox.documentation.service.Contact;

/**
 * swagger属性配置文件路径
 * 
 * @author yishui
 * @date 2019年1月17日
 * @Version 0.0.1
 */
@ConfigurationProperties(prefix = "yishuifengxiao.swagger")
public class SwaggerProperties {
	/**
	 * swagger 扫描的根路径
	 */
	private String basePackage;
	/**
	 * swagger 文档的标题
	 */
	private String title = "API接口文档";
	/**
	 * swagger 文档的描述
	 */
	private String description = " RESTful APIs";
	/**
	 * swagger 文档的中组织的链接
	 */
	private String termsOfServiceUrl = "http://www.yishuifengxiao.com/";
	/**
	 * swagger 文档的分组名
	 */
	private String groupNmae = "default";
	/**
	 * 版本号
	 */
	private String version = "1.0";
	/**
	 * 是否开启swagger-ui，默认为true
	 */
	private Boolean enable=true;
	/**
	 * 联系人
	 */
	private Contact contact = new Contact("yishui", "http://www.yishuifengxiao.com/", "zhiyubujian@163.com");
	/**
	 * 附加信息
	 */
	private List<AuthoriZationPar> auths = new ArrayList<>();

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTermsOfServiceUrl() {
		return termsOfServiceUrl;
	}

	public void setTermsOfServiceUrl(String termsOfServiceUrl) {
		this.termsOfServiceUrl = termsOfServiceUrl;
	}

	public String getGroupNmae() {
		return groupNmae;
	}

	public void setGroupNmae(String groupNmae) {
		this.groupNmae = groupNmae;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public List<AuthoriZationPar> getAuths() {
		return auths;
	}

	public void setAuths(List<AuthoriZationPar> auths) {
		this.auths = auths;
	}

	public static class AuthoriZationPar implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7046632466056115744L;
		private String name;
		private String description;
		private String modelRef;
		private String parameterType;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getModelRef() {
			return modelRef;
		}

		public void setModelRef(String modelRef) {
			this.modelRef = modelRef;
		}

		public String getParameterType() {
			return parameterType;
		}

		public void setParameterType(String parameterType) {
			this.parameterType = parameterType;
		}

		@Override
		public String toString() {
			return "AuthoriZationPar [name=" + name + ", description=" + description + ", modelRef=" + modelRef
					+ ", parameterType=" + parameterType + "]";
		}

	}

	@Override
	public String toString() {
		return "SwaggerProperties [basePackage=" + basePackage + ", title=" + title + ", description=" + description
				+ ", termsOfServiceUrl=" + termsOfServiceUrl + ", groupNmae=" + groupNmae + ", version=" + version
				+ ", contact=" + contact + ", auths=" + auths + "]";
	}
    /**
     * 是否开启swagger-ui，默认为true
     * @return
     */
	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	
	

}
