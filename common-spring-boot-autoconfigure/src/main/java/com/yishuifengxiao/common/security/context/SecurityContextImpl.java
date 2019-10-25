package com.yishuifengxiao.common.security.context;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.SpringSecurityCoreVersion;

import com.yishuifengxiao.common.security.entity.SecurityContextException;

/**
 * 自定义异常信息上下文默认实现类
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class SecurityContextImpl implements SecurityContext {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private SecurityContextException excption;

	public SecurityContextImpl() {
	}

	public SecurityContextImpl(SecurityContextException excption) {
		this.excption = excption;
	}

	@Override
	public void setSecurityExcepion(SecurityContextException excption) {
		this.excption = excption;
	}

	@Override
	public SecurityContextException getSecurityExcepion() {
		return excption;
	}

	@Override
	public void setSecurityExcepion(HttpServletRequest request,Exception excption) {
		this.excption = new SecurityContextException( request,excption);

	}

	@Override
	public void setSecurityExcepion(Exception excption) {
		this.excption = new SecurityContextException(null, excption);

	}

	@Override
	public void setSecurityExcepion(HttpServletRequest request) {
		this.excption = new SecurityContextException(request, null);

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SecurityContextImpl) {
			SecurityContextImpl test = (SecurityContextImpl) obj;

			if ((this.getSecurityExcepion() == null) && (test.getSecurityExcepion() == null)) {
				return true;
			}

			if ((this.getSecurityExcepion() != null) && (test.getSecurityExcepion() != null)
					&& this.getSecurityExcepion().equals(test.getSecurityExcepion())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		if (this.excption == null) {
			return -1;
		} else {
			return this.excption.hashCode();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());

		if (this.excption == null) {
			sb.append(": Null excption");
		} else {
			sb.append(": excption: ").append(this.excption);
		}

		return sb.toString();
	}

}
