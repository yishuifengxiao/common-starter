package com.yishuifengxiao.common.security.token.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleAuthority implements GrantedAuthority, Serializable {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final String role;

    public SimpleAuthority(String role) {
        Assert.hasText(role, "A granted authority textual representation is required");
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    private SimpleAuthority() {
        this.role = "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleAuthority)) {
            return false;
        }
        SimpleAuthority that = (SimpleAuthority) o;
        return Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role);
    }

    @Override
    public String toString() {
        return "SimpleAuthority{" +
                "role='" + role + '\'' +
                '}';
    }
}
