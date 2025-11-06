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
            return ZoneId.systemDefault();
        } finally {
            if (null != connection) {
                connection.close();
            }
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
        // 执行时区查询SQL语句并获取结果
        try (Statement stmt = connection.createStatement(); ResultSet timezoneRs = stmt.executeQuery(getTimezoneQuery(connection))) {
            return timezoneRs.next() ? timezoneRs.getString(1) : null;
        } catch (SQLException e) {
            log.debug("从数据库查询时区信息失败", e);
            return null;
        }
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
            try {
                return ZoneId.of(timezone.trim());
            } catch (DateTimeException e) {
                log.warn("无法解析时区字符串: {}, 使用系统默认时区", timezone);
            }
        }
        // 返回系统默认时区
        return ZoneId.systemDefault();
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
