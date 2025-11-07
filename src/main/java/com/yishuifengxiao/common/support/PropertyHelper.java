package com.yishuifengxiao.common.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * 属性配置帮助类
 * 用于动态设置Spring配置属性
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class PropertyHelper {

    private static final Map<String, String> PROPERTY_CACHE = new HashMap<>();

    /**
     * 设置配置属性（如果该属性尚未设置）
     *
     * @param environment 环境对象
     * @param key         属性键
     * @param value       属性值
     */
    public static void setPropertyIfAbsent(Environment environment, String key, String value) {
        if (environment != null && !environment.containsProperty(key)) {
            // 使用系统属性设置，Spring Boot会自动读取
            System.setProperty(key, value);
            PROPERTY_CACHE.put(key, value);
            log.debug("设置系统属性: {} = {}", key, value);
        } else if (environment != null) {
            log.debug("属性 {} 已存在，值为: {}", key, environment.getProperty(key));
        }
    }

    /**
     * 批量设置配置属性
     *
     * @param environment 环境对象
     * @param properties  属性映射
     */
    public static void setPropertiesIfAbsent(Environment environment, Map<String, String> properties) {
        if (properties == null || properties.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            setPropertyIfAbsent(environment, entry.getKey(), entry.getValue());
        }
    }

    /**
     * 获取已设置的属性值
     *
     * @param key 属性键
     * @return 属性值
     */
    public static String getProperty(String key) {
        return PROPERTY_CACHE.get(key);
    }

    /**
     * 清除所有设置的属性
     */
    public static void clearProperties() {
        for (String key : PROPERTY_CACHE.keySet()) {
            System.clearProperty(key);
        }
        PROPERTY_CACHE.clear();
    }
}