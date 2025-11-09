package com.yishuifengxiao.common.support;

import com.yishuifengxiao.common.tool.codec.AES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.*;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

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
    // 敏感信息关键词列表
    private static final Set<String> SENSITIVE_KEYWORDS = Set.of("password", "passwd", "pwd", "secret", "token", "key", "credential", "auth", "certificate", "private", "signature", "jwt", "oauth", "apikey", "apisecret", "encrypt");

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

    /**
     * 检测属性键是否为敏感信息
     *
     * @param key 属性键
     * @return 是否为敏感信息
     */
    private static boolean isSensitiveProperty(String key) {
        if (key == null) {
            return false;
        }

        String lowerKey = key.toLowerCase();
        return SENSITIVE_KEYWORDS.stream().anyMatch(lowerKey::contains);
    }

    /**
     * AES加密敏感信息
     *
     * @param value 原始值
     * @return 加密后的值
     */
    private static String encryptSensitiveValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }

        try {

            return AES.encrypt(value) + " (AES加密)";
        } catch (Exception e) {
            log.error("AES加密失败: {}", e.getMessage());
            return "***加密失败***";
        }
    }

    /**
     * 处理配置值，对敏感信息进行加密
     *
     * @param key   属性键
     * @param value 原始值
     * @return 处理后的值
     */
    private static Object processPropertyValue(String key, Object value) {
        if (value == null) {
            return null;
        }

        String valueStr = String.valueOf(value);

        // 如果是敏感信息且值不为空，进行加密
        if (isSensitiveProperty(key) && !valueStr.trim().isEmpty()) {
            return encryptSensitiveValue(valueStr);
        }

        return valueStr;
    }

    /**
     * 获取Spring环境中所有生效的配置属性
     *
     * @param environment Spring环境对象
     * @return 所有配置属性的映射
     */
    public static Map<String, Object> getAllConfigurations(Environment environment) {
        if (environment == null) {
            log.warn("环境对象为null，无法获取配置属性");
            return Collections.emptyMap();
        }

        // 如果是ConfigurableEnvironment，可以获取所有PropertySource
        if (environment instanceof ConfigurableEnvironment) {
            return getAllConfigurationsFromPropertySources(((ConfigurableEnvironment) environment).getPropertySources());
        } else {
            // 否则，只能尝试获取所有已知的配置键
            log.warn("环境对象不是ConfigurableEnvironment类型，无法获取所有属性源");
            return getKnownProperties(environment);
        }
    }

    /**
     * 从所有属性源中获取配置属性
     *
     * @param propertySources 属性源集合
     * @return 所有配置属性的映射
     */
    private static Map<String, Object> getAllConfigurationsFromPropertySources(MutablePropertySources propertySources) {
        Map<String, Object> allProperties = new LinkedHashMap<>();

        if (propertySources == null || !propertySources.iterator().hasNext()) {
            return allProperties;
        }

        // 从后往前遍历，确保后面的属性源（优先级更高）覆盖前面的
        Iterator<PropertySource<?>> iterator = propertySources.iterator();
        List<PropertySource<?>> reversedSources = new ArrayList<>();
        iterator.forEachRemaining(reversedSources::add);
        Collections.reverse(reversedSources);

        for (PropertySource<?> propertySource : reversedSources) {
            if (propertySource instanceof EnumerablePropertySource) {
                addEnumerablePropertySourceProperties(allProperties, (EnumerablePropertySource<?>) propertySource);
            } else if (propertySource instanceof CompositePropertySource) {
                // 处理复合属性源 - 直接处理复合属性源中的每个属性源
                CompositePropertySource compositeSource = (CompositePropertySource) propertySource;
                for (PropertySource<?> nestedSource : compositeSource.getPropertySources()) {
                    if (nestedSource instanceof EnumerablePropertySource) {
                        addEnumerablePropertySourceProperties(allProperties, (EnumerablePropertySource<?>) nestedSource);
                    } else if (nestedSource instanceof CompositePropertySource) {
                        // 递归处理嵌套的复合属性源
                        MutablePropertySources nestedSources = new MutablePropertySources();
                        nestedSources.addFirst(nestedSource);
                        Map<String, Object> nestedProperties = getAllConfigurationsFromPropertySources(nestedSources);
                        allProperties.putAll(nestedProperties);
                    } else {
                        log.debug("跳过不可枚举的嵌套属性源: {}", nestedSource.getName());
                    }
                }
            } else {
                log.debug("跳过不可枚举的属性源: {}", propertySource.getName());
            }
        }

        return allProperties;
    }

    /**
     * 添加可枚举属性源中的属性到总映射中
     *
     * @param allProperties    总属性映射
     * @param enumerableSource 可枚举属性源
     */
    private static void addEnumerablePropertySourceProperties(Map<String, Object> allProperties, EnumerablePropertySource<?> enumerableSource) {
        for (String propertyName : enumerableSource.getPropertyNames()) {
            try {
                Object value = enumerableSource.getProperty(propertyName);
                if (value != null) {
                    allProperties.put(propertyName, value);
                }
            } catch (Exception e) {
                log.error("获取属性 {} 时出错: {}", propertyName, e.getMessage());
            }
        }
    }

    /**
     * 尝试获取已知的配置属性
     * 当无法获取所有属性源时使用
     *
     * @param environment 环境对象
     * @return 已知的配置属性映射
     */
    private static Map<String, Object> getKnownProperties(Environment environment) {
        Map<String, Object> properties = new LinkedHashMap<>();

        // 添加一些常见的Spring Boot配置键
        String[] commonKeys = {"spring.application.name", "spring.profiles.active", "server.port", "spring.datasource.url", "spring.datasource.username", "spring.jpa.hibernate.ddl-auto", "logging.level.root", "spring.main.banner-mode"};

        for (String key : commonKeys) {
            try {
                if (environment.containsProperty(key)) {
                    properties.put(key, environment.getProperty(key));
                }
            } catch (Exception e) {
                log.error("获取属性 {} 时出错: {}", key, e.getMessage());
            }
        }

        return properties;
    }

    /**
     * 获取指定前缀的所有配置属性
     *
     * @param environment Spring环境对象
     * @param prefix      属性前缀
     * @return 指定前缀的配置属性映射
     */
    public static Map<String, Object> getConfigurationsByPrefix(Environment environment, String prefix) {
        if (environment == null || prefix == null || prefix.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        String normalizedPrefix = prefix.endsWith(".") ? prefix : prefix + ".";
        Map<String, Object> allProperties = getAllConfigurations(environment);

        return allProperties.entrySet().stream().filter(entry -> entry.getKey().startsWith(normalizedPrefix)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, LinkedHashMap::new));
    }

    /**
     * 获取指定前缀的所有配置属性，并移除前缀
     *
     * @param environment Spring环境对象
     * @param prefix      属性前缀
     * @return 移除前缀后的配置属性映射
     */
    public static Map<String, Object> getConfigurationsWithoutPrefix(Environment environment, String prefix) {
        if (environment == null || prefix == null || prefix.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        String normalizedPrefix = prefix.endsWith(".") ? prefix : prefix + ".";
        Map<String, Object> prefixedProperties = getConfigurationsByPrefix(environment, normalizedPrefix);

        if (CollectionUtils.isEmpty(prefixedProperties)) {
            return Collections.emptyMap();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : prefixedProperties.entrySet()) {
            String keyWithoutPrefix = entry.getKey().substring(normalizedPrefix.length());
            result.put(keyWithoutPrefix, entry.getValue());
        }

        return result;
    }

    /**
     * 将配置属性Map转换为JSON格式的字符串（包含敏感信息加密）
     *
     * @param configurations 配置属性映射
     * @param maxLength      最大属性值长度，超过则截断
     * @return JSON格式的字符串
     */
    private static String convertToJson(Map<String, Object> configurations, int maxLength) {
        if (configurations == null || configurations.isEmpty()) {
            return "{}";
        }

        Map<String, Object> processedConfigs = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : configurations.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Object processedValue = processPropertyValue(key, value);
            String valueStr = String.valueOf(processedValue);

            // 截断过长的值
            if (maxLength > 0 && valueStr.length() > maxLength) {
                valueStr = valueStr.substring(0, maxLength) + "...";
            }

            processedConfigs.put(key, valueStr);
        }

        // 简单的JSON格式转换
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");

        List<String> entries = new ArrayList<>();
        for (Map.Entry<String, Object> entry : processedConfigs.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // 转义特殊字符
            String escapedKey = key.replace("\"", "\\\"");
            String escapedValue = String.valueOf(value).replace("\"", "\\\"");
            entries.add("  \"" + escapedKey + "\": \"" + escapedValue + "\"");
        }

        jsonBuilder.append(String.join(",\n", entries));
        jsonBuilder.append("\n}");

        return jsonBuilder.toString();
    }

    /**
     * 获取处理后的配置属性（包含敏感信息加密）
     *
     * @param environment Spring环境对象
     * @return 处理后的配置属性映射
     */
    public static Map<String, Object> getProcessedConfigurations(Environment environment) {
        Map<String, Object> originalConfigs = getAllConfigurations(environment);
        Map<String, Object> processedConfigs = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : originalConfigs.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            processedConfigs.put(key, processPropertyValue(key, value));
        }

        return processedConfigs;
    }


    /**
     * 打印所有配置属性到日志（JSON格式，包含敏感信息加密）
     *
     * @param environment Spring环境对象
     * @param maxLength   最大打印的属性值长度，超过则截断
     */
    public static String logAllConfigurations(Environment environment, int maxLength) {
        Map<String, Object> allConfigurations = getProcessedConfigurations(environment);
        log.info("Spring环境中共有 {} 个配置属性（敏感信息已加密）", allConfigurations.size());

        String jsonConfigurations = convertToJson(allConfigurations, maxLength);
        log.info("所有配置属性（JSON格式，敏感信息已加密）:\n{}", jsonConfigurations);
        return jsonConfigurations;
    }

    /**
     * 打印所有配置属性到日志（JSON格式，默认截断长度为200）
     *
     * @param environment Spring环境对象
     */
    public static String logAllConfigurations(Environment environment) {
        return logAllConfigurations(environment, 200);
    }

    /**
     * 获取指定前缀的处理后配置属性（包含敏感信息加密）
     *
     * @param environment Spring环境对象
     * @param prefix      属性前缀
     * @return 处理后的配置属性映射
     */
    public static Map<String, Object> getProcessedConfigurationsByPrefix(Environment environment, String prefix) {
        Map<String, Object> originalConfigs = getConfigurationsByPrefix(environment, prefix);
        Map<String, Object> processedConfigs = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : originalConfigs.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            processedConfigs.put(key, processPropertyValue(key, value));
        }

        return processedConfigs;
    }

    /**
     * 打印指定前缀的配置属性到日志（JSON格式，包含敏感信息加密）
     *
     * @param environment Spring环境对象
     * @param prefix      属性前缀
     * @param maxLength   最大打印的属性值长度，超过则截断
     */
    public static String logConfigurationsByPrefix(Environment environment, String prefix, int maxLength) {
        Map<String, Object> configurations = getProcessedConfigurationsByPrefix(environment, prefix);
        log.info("前缀为 '{}' 的配置属性共有 {} 个（敏感信息已加密）", prefix, configurations.size());

        String jsonConfigurations = convertToJson(configurations, maxLength);
        log.info("前缀为 '{}' 的配置属性（JSON格式，敏感信息已加密）:\n{}", prefix, jsonConfigurations);
        return jsonConfigurations;
    }


    /**
     * 打印指定前缀的配置属性到日志（JSON格式，默认截断长度为200）
     *
     * @param environment Spring环境对象
     * @param prefix      属性前缀
     */
    public static String logConfigurationsByPrefix(Environment environment, String prefix) {
        return logConfigurationsByPrefix(environment, prefix, 200);
    }

    /**
     * 获取所有配置属性的JSON字符串（包含敏感信息加密）
     *
     * @param environment Spring环境对象
     * @param maxLength   最大属性值长度，超过则截断
     * @return JSON格式的配置属性字符串
     */
    public static String getAllConfigurationsAsJson(Environment environment, int maxLength) {
        Map<String, Object> allConfigurations = getProcessedConfigurations(environment);
        return convertToJson(allConfigurations, maxLength);
    }

    /**
     * 获取指定前缀的配置属性的JSON字符串（包含敏感信息加密）
     *
     * @param environment Spring环境对象
     * @param prefix      属性前缀
     * @param maxLength   最大属性值长度，超过则截断
     * @return JSON格式的配置属性字符串
     */
    public static String getConfigurationsByPrefixAsJson(Environment environment, String prefix, int maxLength) {
        Map<String, Object> configurations = getProcessedConfigurationsByPrefix(environment, prefix);
        return convertToJson(configurations, maxLength);
    }


    /**
     * 获取指定前缀的配置属性的JSON字符串（默认截断长度为200）
     *
     * @param environment Spring环境对象
     * @param prefix      属性前缀
     * @return JSON格式的配置属性字符串
     */
    public static String getConfigurationsByPrefixAsJson(Environment environment, String prefix) {
        return getConfigurationsByPrefixAsJson(environment, prefix, 200);
    }
}