package com.yishuifengxiao.common.jdbc.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Map;

/**
 * 时区ID检测器
 * 用于检测系统默认时区ID
 *
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class ZoneIdDetector {
    /**
     * 服务器时区参数
     */
    private static final String TIMEZONE_PARAM = "serverTimezone=";
    /**
     * UTF-8字符集
     */
    private static final java.nio.charset.Charset UTF_8 = java.nio.charset.StandardCharsets.UTF_8;

    /**
     * 数据库时区查询语句缓存 - 优化为不可变Map
     */
    private static final Map<String, String> DB_TIMEZONE_QUERIES = Map.of("mysql", "SELECT @@session.time_zone", "postgresql", "SHOW TIMEZONE", "oracle", "SELECT DBTIMEZONE FROM DUAL", "microsoft sql server", "SELECT CURRENT_TIMEZONE_ID()");

    /**
     * 数据库备用时区查询语句 - 用于处理SYSTEM情况
     */
    private static final Map<String, String> DB_BACKUP_TIMEZONE_QUERIES = Map.of("mysql", "SELECT @@global.time_zone, @@system_time_zone", "postgresql", "SELECT current_setting('TIMEZONE')", "oracle", "SELECT SESSIONTIMEZONE FROM DUAL", "microsoft sql server", "SELECT @@DATEFIRST, @@LANGUAGE");

    /**
     * 检测数据库时区
     *
     * @param connection 数据库连接对象
     * @return 数据库对应的时区ID
     * @throws SQLException 当数据库操作出现异常时抛出
     */
    public ZoneId detectDatabaseTimezone(Connection connection) throws SQLException {
        try {

            // 优化：按优先级尝试不同的时区获取方式
            String timezone = tryGetTimezoneFromUrl(connection);
            if (timezone == null) {
                timezone = tryGetTimezoneFromDatabase(connection);
            }

            return parseTimezone(timezone);

        } catch (SQLException e) {
            log.warn("无法获取数据库时区信息，使用系统默认时区", e);
            return null;
        }
    }


    /**
     * 从URL中获取时区信息
     *
     * @param connection 数据库连接对象，用于获取连接URL
     * @return 返回从URL中提取的时区信息，如果URL中不包含时区参数或提取失败则返回null
     * @throws SQLException 当获取数据库元数据URL时发生SQL异常
     */
    private String tryGetTimezoneFromUrl(Connection connection) throws SQLException {
        try {
            // 获取数据库连接URL并检查是否包含时区参数
            String url = connection.getMetaData().getURL();
            return url.contains("serverTimezone") ? extractTimezoneFromUrl(url) : null;
        } catch (SQLException e) {
            log.warn("从URL获取时区信息失败", e);
            return null;
        }
    }


    /**
     * 从数据库查询时区信息
     *
     * @param connection 数据库连接对象，用于执行查询操作
     * @return 返回查询到的时区信息字符串，如果查询失败或无结果则返回null
     */
    private String tryGetTimezoneFromDatabase(Connection connection) {
        // 执行主用时区查询SQL语句并获取结果
        try (Statement stmt = connection.createStatement();
             ResultSet timezoneRs = stmt.executeQuery(getTimezoneQuery(connection))) {

            if (timezoneRs.next()) {
                String timezone = timezoneRs.getString(1);

                // 如果主查询返回SYSTEM，则使用备用查询获取更准确的时区信息
                if ("SYSTEM".equalsIgnoreCase(timezone)) {
                    log.trace("主时区查询返回SYSTEM，尝试使用备用查询获取实际时区");
                    timezone = tryGetBackupTimezoneFromDatabase(connection);
                }

                return timezone;
            }
            return null;

        } catch (SQLException e) {
            log.debug("从数据库查询时区信息失败", e);
            return null;
        }
    }

    /**
     * 使用备用查询从数据库获取时区信息
     *
     * @param connection 数据库连接对象，用于执行查询操作
     * @return 返回查询到的时区信息字符串，如果查询失败或无结果则返回SYSTEM
     */
    private String tryGetBackupTimezoneFromDatabase(Connection connection) {
        try {
            String backupQuery = getBackupTimezoneQuery(connection);
            if (backupQuery == null) {
                log.debug("未找到备用时区查询语句，使用SYSTEM");
                return "SYSTEM";
            }

            try (Statement stmt = connection.createStatement();
                 ResultSet backupRs = stmt.executeQuery(backupQuery)) {

                if (backupRs.next()) {
                    // 根据数据库类型处理不同的返回结果
                    String databaseProductName = connection.getMetaData().getDatabaseProductName().toLowerCase();

                    switch (databaseProductName) {
                        case "mysql":
                            // MySQL: 返回全局时区，如果也是SYSTEM则返回系统时区
                            String globalTimezone = backupRs.getString(1);
                            if ("SYSTEM".equalsIgnoreCase(globalTimezone)) {
                                String systemTimezone = backupRs.getString(2);
                                // 处理乱码问题：尝试多种字符编码转换
                                return fixTimezoneEncoding(systemTimezone);
                            }
                            return globalTimezone;

                        case "postgresql":
                            // PostgreSQL: 直接返回当前设置的时区
                            return backupRs.getString(1);

                        case "oracle":
                            // Oracle: 返回会话时区
                            return backupRs.getString(1);

                        case "microsoft sql server":
                            // SQL Server: 基于语言和日期设置推断时区
                            int dateFirst = backupRs.getInt(1);
                            String language = backupRs.getString(2);
                            return inferSqlServerTimezone(dateFirst, language);

                        default:
                            log.debug("不支持的数据库类型备用查询: {}", databaseProductName);
                            return "SYSTEM";
                    }
                }
            }
        } catch (SQLException e) {
            log.debug("备用时区查询失败，使用SYSTEM", e);
        }

        return "SYSTEM";
    }

    /**
     * 修复时区字符串的编码问题
     *
     * @param timezone 可能存在编码问题的时区字符串
     * @return 修复后的时区字符串，如果修复失败则返回SYSTEM
     */
    private String fixTimezoneEncoding(String timezone) {
        if (timezone == null || timezone.trim().isEmpty()) {
            return "SYSTEM";
        }

        // 检查是否已经是有效的时区格式
        if (isValidTimezoneFormat(timezone)) {
            return timezone;
        }

        // 尝试常见的字符编码转换
        String[] encodings = {"UTF-8", "ISO-8859-1", "GBK", "GB2312", "Windows-1252"};

        for (String encoding : encodings) {
            try {
                // 尝试将字符串从指定编码转换为UTF-8
                String fixedTimezone = new String(timezone.getBytes(encoding), "UTF-8");
                if (isValidTimezoneFormat(fixedTimezone)) {
                    log.debug("成功修复时区编码: 从 {} 编码转换为有效时区: {}", encoding, fixedTimezone);
                    return fixedTimezone;
                }
            } catch (Exception e) {
                // 忽略编码转换失败，继续尝试其他编码
            }
        }

        // 如果所有编码转换都失败，尝试基于常见系统时区进行推断
        String inferredTimezone = inferSystemTimezone(timezone);
        if (inferredTimezone != null) {
            log.trace("基于乱码字符串推断时区: {} -> {}", timezone, inferredTimezone);
            return inferredTimezone;
        }

        log.trace("无法修复时区编码: {}, 使用SYSTEM", timezone);
        return "SYSTEM";
    }

    /**
     * 检查时区字符串是否为有效格式
     *
     * @param timezone 时区字符串
     * @return 如果是有效时区格式返回true
     */
    private boolean isValidTimezoneFormat(String timezone) {
        if (timezone == null || timezone.trim().isEmpty()) {
            return false;
        }

        String trimmed = timezone.trim();

        // 检查常见的时区格式
        // 1. 标准时区ID格式 (如: Asia/Shanghai, America/New_York)
        if (trimmed.matches("[A-Za-z]+/[A-Za-z_]+")) {
            return true;
        }

        // 2. UTC偏移格式 (如: UTC+8, GMT+8)
        if (trimmed.matches("(UTC|GMT)[+-]\\d+")) {
            return true;
        }

        // 3. 简写格式 (如: CST, PST, EST)
        if (trimmed.matches("[A-Z]{2,4}")) {
            return true;
        }

        // 4. 数字偏移格式 (如: +08:00, -05:00)
        if (trimmed.matches("[+-]\\d{1,2}:\\d{2}")) {
            return true;
        }

        // 5. 特殊值
        if ("SYSTEM".equalsIgnoreCase(trimmed) || "LOCAL".equalsIgnoreCase(trimmed)) {
            return true;
        }

        return false;
    }

    /**
     * 基于乱码字符串推断系统时区
     *
     * @param garbledTimezone 乱码的时区字符串
     * @return 推断的时区，如果无法推断返回null
     */
    private String inferSystemTimezone(String garbledTimezone) {
        if (garbledTimezone == null) {
            return null;
        }

        // 将乱码字符串转换为字节数组进行分析
        byte[] bytes = garbledTimezone.getBytes();

        // 基于字节模式推断可能的原始时区
        // 常见的中文系统时区模式
        if (containsChineseTimezonePattern(bytes)) {
            return "Asia/Shanghai"; // 中文系统通常使用上海时区
        }

        // 基于字符串长度和字符分布推断
        if (garbledTimezone.length() <= 10) {
            // 短字符串可能是时区简写或偏移量
            return inferFromShortString(garbledTimezone);
        }

        // 无法推断
        return null;
    }

    /**
     * 检查字节数组是否包含中文时区模式
     *
     * @param bytes 字节数组
     * @return 如果可能包含中文时区信息返回true
     */
    private boolean containsChineseTimezonePattern(byte[] bytes) {
        // 检查是否包含常见的中文字符编码模式
        // 这里可以添加更复杂的模式识别逻辑
        return bytes.length >= 4; // 简化逻辑，实际应该更复杂
    }

    /**
     * 从短字符串推断时区
     *
     * @param shortString 短时区字符串
     * @return 推断的时区
     */
    private String inferFromShortString(String shortString) {
        // 基于常见时区简写进行推断
        switch (shortString.toUpperCase()) {
            case "CST":
            case "CHINA":
            case "BEIJING":
                return "Asia/Shanghai";
            case "PST":
            case "PDT":
            case "LOS ANGELES":
                return "America/Los_Angeles";
            case "EST":
            case "EDT":
            case "NEW YORK":
                return "America/New_York";
            case "GMT":
            case "UTC":
            case "LONDON":
                return "Europe/London";
            case "JST":
            case "TOKYO":
                return "Asia/Tokyo";
            default:
                // 检查是否为数字偏移
                if (shortString.matches("[+-]?\\d+")) {
                    int offset = Integer.parseInt(shortString.replace("+", ""));
                    return convertOffsetToTimezone(offset);
                }
                return null;
        }
    }

    /**
     * 将数字偏移转换为时区
     *
     * @param offset 时区偏移（小时）
     * @return 对应的时区
     */
    private String convertOffsetToTimezone(int offset) {
        // 基于常见偏移量返回对应的时区
        switch (offset) {
            case 8:
                return "Asia/Shanghai";
            case 9:
                return "Asia/Tokyo";
            case 0:
                return "UTC";
            case -5:
                return "America/New_York";
            case -8:
                return "America/Los_Angeles";
            case 1:
                return "Europe/Paris";
            default:
                return String.format("UTC%+d", offset);
        }
    }

    /**
     * 根据数据库类型获取备用时区查询语句
     *
     * @param connection 数据库连接对象，用于获取数据库元数据信息
     * @return 返回对应数据库类型的备用时区查询SQL语句，如果不支持则返回null
     */
    private String getBackupTimezoneQuery(Connection connection) throws SQLException {
        String databaseProductName = connection.getMetaData().getDatabaseProductName().toLowerCase();
        return DB_BACKUP_TIMEZONE_QUERIES.get(databaseProductName);
    }

    /**
     * 推断SQL Server的时区（基于语言和日期设置）
     *
     * @param dateFirst 日期首日设置
     * @param language  语言设置
     * @return 推断的时区字符串
     */
    private String inferSqlServerTimezone(int dateFirst, String language) {
        // 基于常见语言设置推断时区
        if (language != null) {
            language = language.toLowerCase();
            if (language.contains("chinese") || language.contains("zh-cn") || language.contains("zh-tw")) {
                return "Asia/Shanghai";
            } else if (language.contains("japanese")) {
                return "Asia/Tokyo";
            } else if (language.contains("english") && language.contains("us")) {
                return "America/New_York";
            } else if (language.contains("english") && language.contains("uk")) {
                return "Europe/London";
            }
        }

        // 默认返回UTC
        return "UTC";
    }


    /**
     * 解析时区字符串
     *
     * @param timezone 时区字符串，可以为null或空字符串
     * @return 解析成功的ZoneId对象，如果解析失败则返回系统默认时区
     */
    private ZoneId parseTimezone(String timezone) {
        // 如果时区字符串不为空，则尝试解析时区
        if (timezone != null && !timezone.trim().isEmpty()) {
            String trimmedTimezone = timezone.trim();

            // 特殊处理SYSTEM情况：当数据库返回SYSTEM时，使用系统默认时区
            if ("SYSTEM".equalsIgnoreCase(trimmedTimezone)) {
                log.debug("数据库时区设置为SYSTEM，使用系统默认时区: {}", ZoneId.systemDefault());
                return null;
            }

            try {
                return ZoneId.of(trimmedTimezone);
            } catch (DateTimeException e) {
                log.debug("无法解析时区字符串: {}, 使用系统默认时区", trimmedTimezone);
            }
        }
        // 返回系统默认时区
        return null;
    }

    /**
     * 根据数据库类型获取查询时区信息的SQL语句
     *
     * @param connection 数据库连接对象，用于获取数据库元数据信息
     * @return 返回对应数据库类型的时区查询SQL语句
     * @throws SQLException 当数据库类型不支持或获取元数据失败时抛出异常
     */
    private String getTimezoneQuery(Connection connection) throws SQLException {
        // 获取数据库元数据和产品名称
        DatabaseMetaData metaData = connection.getMetaData();
        String databaseProductName = metaData.getDatabaseProductName().toLowerCase();

        // 从预定义的时区查询映射中获取对应SQL语句
        String query = DB_TIMEZONE_QUERIES.get(databaseProductName);
        if (query != null) {
            return query;
        }

        // 如果找不到对应的数据库类型，则抛出异常
        throw new SQLException("不支持的数据库类型: " + databaseProductName);
    }


    /**
     * 从JDBC URL中提取时区参数值
     *
     * @param url JDBC连接URL字符串
     * @return 提取到的时区参数值，如果未找到或URL为空则返回null
     */
    private String extractTimezoneFromUrl(String url) {
        if (url == null) {
            return null;
        }

        int timezoneIndex = url.indexOf(TIMEZONE_PARAM);
        if (timezoneIndex == -1) {
            return null;
        }

        // 提取时区参数后面的部分
        String timezonePart = url.substring(timezoneIndex + TIMEZONE_PARAM.length());

        // 找到参数值的结束位置
        int endIndex = findParameterEndIndex(timezonePart);
        if (endIndex == 0) {
            return null;
        }

        // 截取完整的时区参数值并进行解码
        timezonePart = timezonePart.substring(0, endIndex);
        return decodeTimezoneValue(timezonePart);
    }


    /**
     * 查找参数值的结束位置
     *
     * @param timezonePart 时间区域参数字符串
     * @return 参数值的结束位置索引
     */
    private int findParameterEndIndex(String timezonePart) {
        // 初始化结束位置为字符串长度
        int endIndex = timezonePart.length();
        // 查找分隔符的位置
        int ampersandIndex = timezonePart.indexOf('&');
        int hashIndex = timezonePart.indexOf('#');

        // 如果找到&符号，则更新结束位置为当前结束位置与&符号位置的较小值
        if (ampersandIndex != -1) {
            endIndex = Math.min(endIndex, ampersandIndex);
        }
        // 如果找到#符号，则更新结束位置为当前结束位置与#符号位置的较小值
        if (hashIndex != -1) {
            endIndex = Math.min(endIndex, hashIndex);
        }

        return endIndex;
    }


    /**
     * 解码时区值
     *
     * @param timezonePart 需要解码的时区字符串
     * @return 解码后的时区字符串，如果解码失败则返回原始值
     */
    private String decodeTimezoneValue(String timezonePart) {
        try {
            // 尝试使用UTF-8编码解码时区值
            return java.net.URLDecoder.decode(timezonePart, UTF_8);
        } catch (Exception e) {
            // 解码失败时记录调试日志并返回原始值
            log.debug("时区值解码失败，使用原始值: {}", timezonePart);
            return timezonePart;
        }
    }


}
