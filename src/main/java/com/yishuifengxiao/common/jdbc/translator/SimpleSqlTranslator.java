package com.yishuifengxiao.common.jdbc.translator;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.tool.entity.Slice;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * sql语句转换器sql语句生成器
 * 主要功能时根据pojo类属性数据列表生成对应的sql语句
 *
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleSqlTranslator implements SqlTranslator {

    /**
     * 生成数据插入的sql语句
     *
     * @param table       表名
     * @param fieldValues pojo类属性数据列表
     * @return 插入数据的sql语句
     */
    public String insert(String table, List<FieldValue> fieldValues) {
        if (table == null || table.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (fieldValues == null || fieldValues.isEmpty()) {
            throw new IllegalArgumentException("字段值列表不能为空");
        }

        List<FieldValue> insertValues = fieldValues.stream()
                .filter(fieldValue -> fieldValue != null)
                .filter(fieldValue -> {
                    if (fieldValue.isPrimary()) {
                        return fieldValue.isNotNullVal();
                    }
                    return true;
                }).collect(Collectors.toList());

        if (insertValues.isEmpty()) {
            throw new IllegalArgumentException("至少需要一个非空的字段值");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append("`").append(escapeTableName(table)).append("` (`");

        String fieldNames = insertValues.stream()
                .map(FieldValue::getColumnName)
                .map(this::escapeColumnName)
                .collect(Collectors.joining("`,`"));
        sql.append(fieldNames).append("`) VALUES (");

        String placeholders = insertValues.stream()
                .map(fieldValue -> "?")
                .collect(Collectors.joining(", "));
        sql.append(placeholders).append(")");

        return sql.toString();
    }


    /**
     * 生成根据主键更新数据的sql
     *
     * @param table       表名
     * @param primaryKey  主键属性
     * @param fieldValues pojo类属性数据列表
     * @return 更新数据的sql
     */
    public String updateByPrimaryKey(String table, FieldValue primaryKey, List<FieldValue> fieldValues) {
        if (table == null || table.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (primaryKey == null) {
            throw new IllegalArgumentException("主键不能为空");
        }

        List<FieldValue> nonNullValues = fieldValues.stream()
                .filter(fieldValue -> fieldValue != null)
                .filter(fieldValue -> !fieldValue.isPrimary())
                .collect(Collectors.toList());

        if (nonNullValues.isEmpty()) {
            throw new IllegalArgumentException("至少需要一个非主键字段用于更新");
        }

        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append("`").append(escapeTableName(table)).append("` SET ");

        String setClause = nonNullValues.stream()
                .map(fieldValue -> "`" + escapeColumnName(fieldValue.getColumnName()) + "` = ?")
                .collect(Collectors.joining(", "));
        sql.append(setClause);

        sql.append(" WHERE ").append("`").append(escapeColumnName(primaryKey.getColumnName())).append("` = ?");

        return sql.toString();
    }

    /**
     * 根据条件生成根据主键删除数据的sql
     *
     * @param table      表名
     * @param primaryKey 主键名称
     * @param values     主键值，单个值时使用等值匹配，多个值时使用in匹配
     * @return 根据主键批量删除数据的sql
     */
    public String deleteByPrimaryKeys(String table, String primaryKey, List<Object> values) {
        if (StringUtils.isBlank(table) || StringUtils.isBlank(primaryKey) || values == null) {
            throw new IllegalArgumentException("表名、主键名和值列表不能为空");
        }

        if (values.isEmpty()) {
            throw new IllegalArgumentException("主键值列表不能为空");
        }

        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append("`").append(escapeTableName(table)).append("` WHERE ")
           .append("`").append(escapeColumnName(primaryKey)).append("`");

        if (values.size() == 1) {
            sql.append(" = ?");
        } else {
            String placeholders = values.stream()
                    .map(value -> "?")
                    .collect(Collectors.joining(", "));
            sql.append(" IN (").append(placeholders).append(")");
        }

        return sql.toString();
    }

    /**
     * 转义表名，防止SQL注入
     *
     * @param tableName 原始表名
     * @return 转义后的表名
     */
    private String escapeTableName(String tableName) {
        if (tableName == null) {
            return "";
        }
        return tableName.replaceAll("[^a-zA-Z0-9_\\-\\.]", "");
    }

    /**
     * 转义列名，防止SQL注入
     *
     * @param columnName 原始列名
     * @return 转义后的列名
     */
    private String escapeColumnName(String columnName) {
        if (columnName == null) {
            return "";
        }
        return columnName.replaceAll("[^a-zA-Z0-9_\\-]", "");
    }

    /**
     * 根据条件生成数据查询sql语句
     *
     * @param table       表名
     * @param fieldValues pojo类属性数据列表
     * @param like        是否对字符串属性进行模糊查询
     * @param orders      排序条件
     * @param slice       分页参数
     * @return 查询sql语句
     */
    @Override
    public String findAll(String table, List<FieldValue> fieldValues, boolean like, List<Order> orders, Slice slice) {
        //@formatter:off
        StringBuilder sql = new StringBuilder("SELECT * FROM ");
        sql.append("`").append(table).append("`");

        // 构建WHERE条件
        if (fieldValues != null && !fieldValues.isEmpty()) {
            List<FieldValue> nonNullValues = fieldValues.stream()
                    .filter(fieldValue -> fieldValue != null && fieldValue.isNotNullVal())
                    .collect(Collectors.toList());

            if (!nonNullValues.isEmpty()) {
                sql.append(" WHERE ");

                String whereClause = nonNullValues.stream()
                        .map(fieldValue -> {
                            if (like && fieldValue.isNotNullVal()
                                    && fieldValue.getValue() instanceof String
                                    && !fieldValue.isPrimary()) {
                                return "`" + fieldValue.getColumnName() + "` LIKE CONCAT('%', ?, '%')";
                            } else {
                                return "`" + fieldValue.getColumnName() + "` = ?";
                            }
                        })
                        .collect(Collectors.joining(" AND "));
                sql.append(whereClause);
            }
        }

        // 构建ORDER BY条件
        if (orders != null && !orders.isEmpty()) {
            List<Order> validOrders = orders.stream()
                    .filter(order -> order != null && StringUtils.isNotBlank(order.getOrderName()))
                    .collect(Collectors.toList());

            if (!validOrders.isEmpty()) {
                sql.append(" ORDER BY ");

                String orderClause = validOrders.stream()
                        .map(order -> "`" + order.getOrderName() + "` " +
                                (order.getDirection() == Order.Direction.DESC ? "DESC" : "ASC"))
                        .collect(Collectors.joining(", "));
                sql.append(orderClause);
            }
        }

        // 优化分页条件构建逻辑
        if (slice != null) {
            // 检查分页参数是否存在
            Integer pageNum = slice.getNum() == null ? null : slice.getNum().intValue();
            Integer pageSize = slice.getSize() == null ? null : slice.getSize().intValue();

            if (pageSize != null) {
                // 只有pageSize参数的情况
                if (pageNum == null) {
                    // 只有size参数，生成LIMIT size语句
                    sql.append(" limit ").append(pageSize);
                } else {
                    // 同时有num和size参数，生成LIMIT offset, size语句
                    // 验证参数有效性
                    if (pageNum > 0 && pageSize > 0) {
                        int offset = (pageNum - 1) * pageSize;
                        sql.append(" limit ").append(offset).append(",").append(pageSize);
                    } else {
                        // 参数无效，不添加分页条件
                        // 可以记录日志或抛出异常，这里选择静默处理
                    }
                }
            }
            // 如果只有pageNum参数而没有pageSize参数，不添加分页条件
        }
        //@formatter:on
        return sql.toString();
    }

}
