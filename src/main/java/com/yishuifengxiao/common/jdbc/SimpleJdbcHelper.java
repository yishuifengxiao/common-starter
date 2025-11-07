package com.yishuifengxiao.common.jdbc;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.jdbc.executor.SimpleSqlExecutor;
import com.yishuifengxiao.common.jdbc.executor.SqlExecutor;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.extractor.SimpleFieldExtractor;
import com.yishuifengxiao.common.jdbc.translator.SimpleSqlTranslator;
import com.yishuifengxiao.common.jdbc.translator.SqlTranslator;
import com.yishuifengxiao.common.jdbc.util.FieldUtils;
import com.yishuifengxiao.common.jdbc.util.SimpleRowMapper;
import com.yishuifengxiao.common.jdbc.util.ZoneIdDetector;
import com.yishuifengxiao.common.tool.collections.CollUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.entity.Slice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.SQLException;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统JdbcTemplate操作器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleJdbcHelper implements JdbcHelper {

    private static final String LOG_PREFIX = "【yishuifengxiao-common-spring-boot-starter】";
    private static final String TEMP_TABLE_PREFIX = "__tmp_result_";
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private final ZoneIdDetector zoneIdDetector = new ZoneIdDetector();
    private final SqlTranslator sqlTranslator = new SimpleSqlTranslator();
    private final FieldExtractor fieldExtractor = new SimpleFieldExtractor();
    private SqlExecutor sqlExecutor;


    private JdbcTemplate jdbcTemplate;
    private ZoneId timeZone;


    /**
     * 根据主键查询一条数据
     *
     * @param clazz      POJO类型
     * @param primaryKey 主键值
     * @param <T>        数据类型
     * @return 查询出来的数据
     */
    @Override
    public <T> T findByPrimaryKey(Class<T> clazz, Object primaryKey) {
        if (primaryKey == null) {
            log.debug("{}主键值为空，跳过查询", LOG_PREFIX);
            return null;
        }

        String tableName = fieldExtractor.extractTableName(clazz);
        FieldValue primaryKeyField = fieldExtractor.extractPrimaryFiled(clazz);
        String sql = sqlTranslator.findAll(tableName, Collections.singletonList(primaryKeyField), false, null, new Slice(1, 1));

        return executeSingleQuery(clazz, sql, primaryKeyField.setValue(primaryKey));
    }

    /**
     * 根据pojo实例中的非空属性值查询出所有符合条件的数据的数量
     *
     * @param t        pojo实例
     * @param likeMode 是否对字符串属性进行模糊查询，true表示为是，false为否
     * @param <T>      数据类型
     * @return 所有符合条件的数据的数量
     */
    @Override
    public <T> Long countAll(T t, boolean likeMode) {
        if (t == null) {
            return null;
        }

        try {
            QueryContext<T> context = createQueryContext(t, null, null);

            if (context.nonNullValues.isEmpty()) {
                String countSql = String.format("SELECT COUNT(1) FROM %s", context.tableName);
                return executeCountQuery(countSql);
            }

            String sql = sqlTranslator.findAll(context.tableName, context.nonNullValues, likeMode, null, null);
            String countSql = String.format("SELECT COUNT(1) FROM (%s) AS temp_table_1", sql);
            return executeCountQuery(countSql, CollUtil.toArray(context.nonNullValues));
        } catch (Exception e) {
            log.error("{}执行countAll时发生异常", LOG_PREFIX, e);
            return 0L;
        }
    }

    /**
     * 根据pojo实例中的非空属性值查询出一条符合条件的数据
     *
     * @param t        pojo实例
     * @param likeMode 是否对字符串属性进行模糊查询，true表示为是，false为否
     * @param orders   排序条件
     * @param <T>      数据类型
     * @return 查询出来的数据
     */
    @Override
    public <T> T findOne(T t, boolean likeMode, Order... orders) {
        if (t == null) {
            return null;
        }

        QueryContext<T> context = createQueryContext(t, orders, new Slice(1, 1));
        String sql = sqlTranslator.findAll(context.tableName, context.nonNullValues, likeMode, context.orders, context.slice);

        return executeSingleQuery((Class<T>) t.getClass(), sql, CollUtil.toArray(context.nonNullValues));
    }

    /**
     * 根据pojo实例中的非空属性值查询出所有符合条件的数据
     *
     * @param t        pojo实例
     * @param likeMode 是否对字符串属性进行模糊查询，true表示为是，false为否
     * @param orders   排序条件
     * @param <T>      数据类型
     * @return 查询出来的数据
     */
    @Override
    public <T> List<T> findAll(T t, boolean likeMode, Order... orders) {
        if (t == null) {
            return Collections.emptyList();
        }

        QueryContext<T> context = createQueryContext(t, orders, null);
        String querySql = sqlTranslator.findAll(context.tableName, context.nonNullValues, likeMode, context.orders, context.slice);

        return executeListQuery((Class<T>) t.getClass(), querySql, CollUtil.toArray(context.nonNullValues));
    }

    /**
     * 根据pojo实例中的非空属性值分页查询出所有符合条件的数据
     *
     * @param pageQuery pojo实例查询条件
     * @param likeMode  是否对字符串属性进行模糊查询，true表示为是，false为否
     * @param orders    排序条件
     * @param <T>       数据类型
     * @return 查询出来的数据
     */
    @Override
    public <T> Page<T> findPage(PageQuery<T> pageQuery, boolean likeMode, Order... orders) {
        if (pageQuery == null) {
            return Page.ofEmpty();
        }
        return this.findPage(pageQuery.getQuery(), likeMode, pageQuery, orders);
    }

    /**
     * 根据pojo实例中的非空属性值分页查询出所有符合条件的数据
     *
     * @param t        pojo实例
     * @param likeMode 是否对字符串属性进行模糊查询，true表示为是，false为否
     * @param slice    分页参数
     * @param orders   排序条件
     * @param <T>      数据类型
     * @return 查询出来的数据
     */
    @Override
    public <T> Page<T> findPage(T t, boolean likeMode, Slice slice, Order... orders) {
        slice = slice == null ? new Slice(DEFAULT_PAGE_SIZE, DEFAULT_PAGE_NUMBER) : slice;
        if (t == null) {
            return Page.ofEmpty(slice.size());
        }

        QueryContext<T> context = createQueryContext(t, orders, slice);
        FieldValue[] params = CollUtil.toArray(context.nonNullValues);

        String querySql = sqlTranslator.findAll(context.tableName, context.nonNullValues, likeMode, context.orders, slice);
        String countSql = sqlTranslator.findAll(context.tableName, context.nonNullValues, likeMode, null, null);
        String wrappedCountSql = String.format("SELECT COUNT(1) FROM (%s) AS %s9", countSql, TEMP_TABLE_PREFIX);

        List<T> list = executeListQuery((Class<T>) t.getClass(), querySql, params);
        Long count = executeCountQuery(wrappedCountSql, params);

        return Page.of(list, count, slice);
    }

    /**
     * 根据主键全属性全量更新方式更新一条数据
     *
     * @param <T> POJO类
     * @param t   待更新的数据
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int updateByPrimaryKey(T t) {
        if (t == null) {
            return 0;
        }

        UpdateContext context = createUpdateContext(t, false);
        String sql = sqlTranslator.updateByPrimaryKey(context.tableName, context.primaryKey, context.fieldValues);
        return sqlExecutor.execute(jdbcTemplate, sql, CollUtil.toArray(context.fieldValues));
    }

    /**
     * 根据主键可选属性增量更新方式更新一条数据
     *
     * @param <T> POJO类
     * @param t   待更新的数据
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int updateByPrimaryKeySelective(T t) {
        if (t == null) {
            return 0;
        }

        UpdateContext context = createUpdateContext(t, true);
        String sql = sqlTranslator.updateByPrimaryKey(context.tableName, context.primaryKey, context.fieldValues);
        return sqlExecutor.execute(jdbcTemplate, sql, CollUtil.toArray(context.fieldValues));
    }

    /**
     * 根据主键删除一条数据
     *
     * @param <T>         POJO类
     * @param clazz       操作的对象
     * @param primaryKeys 主键值
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int deleteByPrimaryKey(Class<T> clazz, Object... primaryKeys) {
        if (primaryKeys == null || primaryKeys.length == 0) {
            log.debug("{}主键参数为空，跳过删除", LOG_PREFIX);
            return 0;
        }

        List<Object> validPrimaryKeys = Arrays.stream(primaryKeys)
                .filter(Objects::nonNull)
                .filter(key -> !(key instanceof String) || StringUtils.isNotBlank(((String) key).trim()))
                .collect(Collectors.toList());

        if (validPrimaryKeys.isEmpty()) {
            log.debug("{}无有效主键值，跳过删除", LOG_PREFIX);
            return 0;
        }

        String tableName = fieldExtractor.extractTableName(clazz);
        FieldValue primaryKey = fieldExtractor.extractPrimaryFiled(clazz);
        primaryKey.setValue(Arrays.asList(validPrimaryKeys));

        String sql = sqlTranslator.deleteByPrimaryKeys(tableName, primaryKey.getColumnName(), validPrimaryKeys);
        return sqlExecutor.execute(jdbcTemplate, sql, primaryKey);
    }

    /**
     * 查询上下文
     */
    private static class QueryContext<T> {
        String tableName;
        List<FieldValue> fieldValues;
        List<FieldValue> nonNullValues;
        List<Order> orders;
        Slice slice;

        QueryContext(String tableName, List<FieldValue> fieldValues, List<FieldValue> nonNullValues, List<Order> orders, Slice slice) {
            this.tableName = tableName;
            this.fieldValues = fieldValues;
            this.nonNullValues = nonNullValues;
            this.orders = orders;
            this.slice = slice;
        }
    }

    /**
     * 更新上下文
     */
    private static class UpdateContext {
        String tableName;
        FieldValue primaryKey;
        List<FieldValue> fieldValues;

        UpdateContext(String tableName, FieldValue primaryKey, List<FieldValue> fieldValues) {
            this.tableName = tableName;
            this.primaryKey = primaryKey;
            this.fieldValues = fieldValues;
        }
    }


    /**
     * 创建查询上下文对象
     *
     * @param t      查询对象实例，用于提取表名和字段值
     * @param orders 排序条件数组
     * @param slice  分页信息
     * @return 包含查询所需各种信息的QueryContext对象
     */
    private <T> QueryContext<T> createQueryContext(T t, Order[] orders, Slice slice) {
        // 提取表名
        String tableName = fieldExtractor.extractTableName(t.getClass());
        // 提取字段值列表
        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(t);
        // 过滤出非空字段值
        List<FieldValue> nonNullValues = extractNonNullFieldValues(fieldValues);
        // 创建处理后的排序条件
        List<Order> processedOrders = createOrder(fieldValues, orders);

        return new QueryContext<>(tableName, fieldValues, nonNullValues, processedOrders, slice);
    }


    /**
     * 处理排序条件
     *
     * @param fieldValues pojo实例数据属性列表
     * @param orders      排序条件
     * @return 处理后的排序条件
     */
    private List<Order> createOrder(List<FieldValue> fieldValues, Order... orders) {
        if (orders == null || orders.length == 0) {
            return null;
        }

        return Arrays.stream(orders).filter(Objects::nonNull).filter(order -> StringUtils.isNotBlank(order.getOrderName())).map(order -> {
            String columnName = fieldValues.stream().filter(field -> field != null && field.getField() != null).filter(field -> field.getField().getName().equalsIgnoreCase(order.getOrderName())).map(FieldValue::getColumnName).findFirst().orElse(null);
            if (StringUtils.isNotBlank(columnName)) {
                order.setOrderName(columnName);
            }
            return order;
        }).collect(Collectors.toList());
    }


    /**
     * 创建更新操作的上下文对象
     *
     * @param t 待更新的对象实例
     * @param selective 是否为选择性更新，true表示只更新非空字段
     * @return 更新操作的上下文对象，包含表名、主键信息和待更新的字段值列表
     */
    private <T> UpdateContext createUpdateContext(T t, boolean selective) {
        // 提取表名和字段值信息
        String tableName = fieldExtractor.extractTableName(t.getClass());
        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(t);
        FieldValue primaryKey = fieldValues.stream().filter(FieldValue::isPrimary).findFirst().orElse(null);

        // 如果是选择性更新，则过滤掉空值字段
        if (selective) {
            fieldValues = extractNonNullFieldValues(fieldValues);
        }

        return new UpdateContext(tableName, primaryKey, fieldValues);
    }



    /**
     * 执行单条查询并返回结果
     *
     * @param clazz 结果对象的类型Class
     * @param sql   查询SQL语句
     * @param args  SQL参数数组
     * @return 查询结果，如果无结果则返回null
     */
    private <T> T executeSingleQuery(Class<T> clazz, String sql, FieldValue... args) {
        // 执行查询获取结果列表
        List<T> list = sqlExecutor.findAll(jdbcTemplate, clazz, sql, args);
        // 返回第一条记录或null（如果没有记录）
        return list == null || list.isEmpty() ? null : list.get(0);
    }


    /**
     * 执行列表查询
     */
    private <T> List<T> executeListQuery(Class<T> clazz, String sql, FieldValue... args) {
        List<T> list = sqlExecutor.findAll(jdbcTemplate, clazz, sql, args);
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 执行计数查询
     */
    private Long executeCountQuery(String sql, FieldValue... args) {
        List<Long> numbers = sqlExecutor.findAll(jdbcTemplate, Long.class, sql, args);
        return numbers == null || numbers.isEmpty() ? 0L : numbers.get(0);
    }


    /**
     * 以全属性方式新增一条数据
     *
     * @param <T> POJO类
     * @param t   待新增的数据
     * @return 保存数据的主键
     */
    @Override
    public <T> KeyHolder insert(T t) {
        if (t == null) {
            log.warn("{}新增数据为空", LOG_PREFIX);
            return null;
        }

        String tableName = fieldExtractor.extractTableName(t.getClass());
        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(t);
        String sql = sqlTranslator.insert(tableName, fieldValues);

        return this.sqlExecutor.update(this.jdbcTemplate, sql, fieldValues);
    }

    /**
     * 根据主键id判断数据是否存在，若存在则先删除存在的数据，然后再插入新的数据
     *
     * @param t   待操作的数据
     * @param <T> POJO类
     * @return 保存数据的主键
     */
    @Override
    public <T> void saveOrUpdate(T t) {
        if (t == null) {
            log.warn("{}保存或更新数据为空", LOG_PREFIX);
        }

        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(t);
        FieldValue primaryKeyValue = fieldValues.stream().filter(Objects::nonNull).filter(field -> field.isPrimary() && field.isNotNullVal()).findFirst().orElse(null);

        if (null == primaryKeyValue || primaryKeyValue.isNullVal()) {
            this.insert(t);
        } else {
            Object val = this.findByPrimaryKey(t.getClass(), primaryKeyValue.getValue());
            if (null == val) {
                this.insert(t);
            }
            this.updateByPrimaryKey(t);
        }
    }

    /**
     * 批量保存数据
     *
     * @param list 待批量保存的数据
     * @param <T>  POJO数据类型
     */
    @Override
    public <T> void saveAll(Collection<T> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        T firstValidItem = list.stream().filter(Objects::nonNull).findFirst().orElse(null);

        if (firstValidItem == null) {
            log.warn("{}批量保存数据列表中无有效数据", LOG_PREFIX);
            return;
        }

        String tableName = fieldExtractor.extractTableName(firstValidItem.getClass());
        List<FieldValue> fieldValues = fieldExtractor.extractFiled(firstValidItem.getClass());

        String sql = sqlTranslator.insert(tableName, fieldValues);
        int[] types = fieldValues.stream().mapToInt(field -> field.sqlType().getVendorTypeNumber()).toArray();

        List<List<FieldValue>> batchParams = list.stream().filter(Objects::nonNull).map(item -> fieldExtractor.extractFieldValue(item)).collect(Collectors.toList());

        sqlExecutor.batchUpdate(jdbcTemplate, sql, types, batchParams);
    }


    /**
     * 根据sql查询出所有的数据
     *
     * @param clazz  数据类型
     * @param sql    sql语句
     * @param params 参数
     * @param <T>    POJO类
     * @return 查询出来的数据
     */
    @Override
    public <T> List<T> findAll(Class<T> clazz, String sql, Object... params) {
        List<T> results = null;
        if (FieldUtils.isBasicResult(clazz)) {
            results = this.jdbcTemplate.queryForList(sql, clazz, params);
        } else {
            results = jdbcTemplate.query(sql, new SimpleRowMapper<>(clazz), params);
        }


        return results == null ? Collections.emptyList() : results;
    }


    /**
     * 获取增强工具使用的JdbcTemplate
     *
     * @return 增强工具使用的JdbcTemplate
     */
    @Override
    public JdbcTemplate jdbcTemplate() {
        return this.jdbcTemplate;
    }


    /**
     * 设置增强工具使用的JdbcTemplate
     *
     * @param jdbcTemplate JdbcTemplate
     */
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.initializeSqlExecutor();
    }

    /**
     * 构造函数
     */
    public SimpleJdbcHelper() {
    }

    /**
     * 构造函数
     *
     * @param jdbcTemplate JdbcTemplate
     */
    public SimpleJdbcHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.initializeSqlExecutor();
    }

    /**
     * 初始化SQL执行器
     */
    private void initializeSqlExecutor() {
        if (this.jdbcTemplate == null) {
            log.warn("{}JdbcTemplate为空，跳过SQL执行器初始化", LOG_PREFIX);
            return;
        }

        try {
            this.timeZone = zoneIdDetector.detectDatabaseTimezone(jdbcTemplate.getDataSource().getConnection());
            this.sqlExecutor = new SimpleSqlExecutor(this.timeZone);
            log.debug("{}SQL执行器初始化成功，时区: {}", LOG_PREFIX, this.timeZone);
        } catch (SQLException e) {
            log.warn("{}无法获取数据库时区信息，使用默认时区", LOG_PREFIX);
            this.timeZone = ZoneId.systemDefault();
            this.sqlExecutor = new SimpleSqlExecutor(this.timeZone);
        } catch (Exception e) {
            log.error("{}SQL执行器初始化失败", LOG_PREFIX, e);
            throw new RuntimeException("SQL执行器初始化失败", e);
        }
    }


    /**
     * 提取非空字段值
     */
    private List<FieldValue> extractNonNullFieldValues(List<FieldValue> fieldValues) {
        return fieldValues.stream().filter(FieldValue::isNotNullVal).collect(Collectors.toList());
    }


}
