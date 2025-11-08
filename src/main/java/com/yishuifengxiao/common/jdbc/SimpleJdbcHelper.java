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
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.entity.Slice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
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
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
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
            return executeCountQuery(countSql, toArray(context.nonNullValues));
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

        return executeSingleQuery((Class<T>) t.getClass(), sql, toArray(context.nonNullValues));
    }

    /**
     * 将FieldValue列表转换为数组
     *
     * @param nonNullValues 需要转换的FieldValue列表，可以为null
     * @return 转换后的FieldValue数组，如果输入为null则返回空数组
     */
    private FieldValue[] toArray(List<FieldValue> nonNullValues) {
        // 处理null输入情况，返回空数组
        if (null == nonNullValues) {
            return new FieldValue[0];
        }
        // 使用toArray方法将列表转换为数组
        return nonNullValues.toArray(new FieldValue[nonNullValues.size()]);
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

        return executeListQuery((Class<T>) t.getClass(), querySql, toArray(context.nonNullValues));
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
        FieldValue[] params = toArray(context.nonNullValues);

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
        return sqlExecutor.execute(jdbcTemplate, sql, toArray(context.fieldValues));
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
        return sqlExecutor.execute(jdbcTemplate, sql, toArray(context.fieldValues));
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

        List<Object> validPrimaryKeys = Arrays.stream(primaryKeys).filter(Objects::nonNull).filter(key -> !(key instanceof String) || StringUtils.isNotBlank(((String) key).trim())).collect(Collectors.toList());

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
     * @param t         待更新的对象实例
     * @param selective 是否为选择性更新，true表示只更新非空字段
     * @return 更新操作的上下文对象，包含表名、主键信息和待更新的字段值列表
     */
    private <T> UpdateContext createUpdateContext(T t, boolean selective) {
        // 提取表名和字段值信息
        String tableName = fieldExtractor.extractTableName(t.getClass());
        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(t);
        FieldValue primaryKey = fieldValues.stream().filter(FieldValue::isPrimary).findFirst().orElse(null);
        List<FieldValue> noPrimaryKeys = fieldValues.stream().filter(s -> !s.isPrimary()).collect(Collectors.toList());
        // 如果是选择性更新，则过滤掉空值字段
        if (selective) {
            noPrimaryKeys = extractNonNullFieldValues(noPrimaryKeys);
        }
        List<FieldValue> list = new ArrayList<>(fieldValues.size());
        list.addAll(noPrimaryKeys);
        list.add(primaryKey);
        return new UpdateContext(tableName, primaryKey, list);
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
        FieldValue primaryKey = fieldValues.stream().filter(v -> v.isPrimary()).findFirst().orElse(null);
        if (null != primaryKey && primaryKey.isNullVal()) {
            fieldValues = fieldValues.stream().filter(v -> !v.isPrimary()).collect(Collectors.toList());
        }

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
    public <T> KeyHolder saveOrUpdate(T t) {
        if (t == null) {
            log.warn("{}保存或更新数据为空", LOG_PREFIX);
            return null;
        }

        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(t);
        FieldValue primaryKeyValue = fieldValues.stream().filter(Objects::nonNull).filter(field -> field.isPrimary() && field.isNotNullVal()).findFirst().orElse(null);

        if (null == primaryKeyValue || primaryKeyValue.isNullVal()) {
            return this.insert(t);
        } else {
            String tableName = fieldExtractor.extractTableName(t.getClass());
            String sql = "SELECT COUNT(1) FROM " + tableName + " WHERE " + primaryKeyValue.getColumnName() + " = :id";

            List<Long> result = this.find(Long.class, sql, Map.of("id", primaryKeyValue.getValue()));

            if (result == null || result.isEmpty() || result.get(0) <= 0) {
                return this.insert(t);
            }
            this.updateByPrimaryKey(t);

            // 统一返回 KeyHolder 方式，模拟主键回填效果
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            Map<String, Object> keys = Collections.singletonMap(primaryKeyValue.getField().getName(), primaryKeyValue.getValue());
            keyHolder.getKeyList().add(keys);
            return keyHolder;
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
            this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.jdbcTemplate);
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
     * 提取非空字段值列表
     *
     * @param fieldValues 字段值列表，用于筛选其中非空的字段值
     * @return 包含所有非空字段值的新列表
     */
    private List<FieldValue> extractNonNullFieldValues(List<FieldValue> fieldValues) {
        // 使用Stream过滤出所有非空字段值并收集为新列表
        return fieldValues.stream().filter(FieldValue::isNotNullVal).collect(Collectors.toList());
    }


    /**
     * 根据SQL查询语句查找指定类型的对象列表
     * <p>命名参数不区分大小写，但建议与传入的 Map 或 JavaBean 属性保持一致</p>
     * <p>SQL 语句中的命名参数必须以冒号（:）开头，例如 :name。</p>
     * <p>
     * 此方法使用命名参数（如 :name）执行查询，参数值从 params 中获取。</p>
     *
     * @param <T>   查询结果的对象类型
     * @param clazz 返回结果的类型Class对象
     * @param sql   查询SQL语句
     * @param param SQL查询参数
     * @return 指定类型的对象列表
     */
    @Override
    public <T> List<T> find(Class<T> clazz, String sql, Object param) {
        if (null == param) {
            return this.find(clazz, sql, (SqlParameterSource) null);
        }
        SqlParameterSource params = new BeanPropertySqlParameterSource(param);
        return this.find(clazz, sql, params);
    }


    /**
     * 根据SQL查询语句和参数查找指定类型的对象列表
     * <p>命名参数不区分大小写，但建议与传入的 Map 或 JavaBean 属性保持一致</p>
     * <p>SQL 语句中的命名参数必须以冒号（:）开头，例如 :name。</p>
     * <p>
     * 此方法使用命名参数（如 :name）执行查询，参数值从 params 中获取。</p>
     * <pre>
     *     示例1：
     *     String sql = "UPDATE users SET name = :name, age = :age WHERE id = :id";
     *     Map<String, Object> params = new HashMap<>();
     *     params.put("name", user.getName());
     *     params.put("age", user.getAge());
     *     params.put("id", user.getId());
     * </pre>
     * <p>特别注意：在命名参数中，Like 查询需要特别注意，因为需要将百分号（%）包含在参数值中。</p>
     * <pre>
     *     示例2：  String sql = "SELECT * FROM users WHERE name LIKE :name";
     *             Map<String, Object> params = new HashMap<>();
     *             params.put("name", "%" + user.getName() + "%");
     * </pre>
     * <p>NamedParameterJdbcTemplate 支持 IN 子句，可以使用具名参数传入一个集合。</p>
     * <pre>
     *     示例3：  String sql = "SELECT * FROM users WHERE id IN (:ids)";
     *             Map<String, Object> params = new HashMap<>();
     *             params.put("ids", List.of(1, 2, 3));
     * </pre>
     *
     * @param <T>    查询结果的对象类型
     * @param clazz  返回结果的类型Class对象
     * @param sql    查询SQL语句
     * @param params SQL查询参数Map，键为参数名，值为参数值
     * @return 符合查询条件的对象列表，当前实现返回空列表
     */
    @Override
    public <T> List<T> find(Class<T> clazz, String sql, Map<String, Object> params) {

        if (null == params || params.isEmpty()) {
            return this.find(clazz, sql, (SqlParameterSource) null);
        }
        SqlParameterSource paramSource = null;
        if (this.timeZone != null) {
            // 对参数中的日期时间类型进行时区转换处理，包括集合中的元素
            Map<String, Object> processedParams = processDateTimeParameters(params);
            paramSource = new MapSqlParameterSource(processedParams);
        } else {
            paramSource = new MapSqlParameterSource(params);
        }

        return this.find(clazz, sql, paramSource);
    }


    /**
     * 对参数中的日期时间类型进行时区转换处理，包括集合中的元素
     *
     * @param params 原始参数Map
     * @return 处理后的参数Map
     */
    private Map<String, Object> processDateTimeParameters(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return params;
        }

        Map<String, Object> processedParams = new HashMap<>(params);

        for (Map.Entry<String, Object> entry : processedParams.entrySet()) {
            Object value = entry.getValue();
            if (value != null) {
                // 处理日期时间类型的时区转换，包括集合中的元素
                Object processedValue = processDateTimeValueRecursive(value);
                entry.setValue(processedValue);
            }
        }

        return processedParams;
    }

    /**
     * 递归处理日期时间类型的时区转换，包括集合中的元素
     *
     * @param value 原始值
     * @return 处理后的值
     */
    private Object processDateTimeValueRecursive(Object value) {
        if (value == null) {
            return null;
        }

        // 处理集合类型（List、Set、数组等）
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            List<Object> processedList = new ArrayList<>();
            for (Object element : collection) {
                processedList.add(processDateTimeValueRecursive(element));
            }
            // 保持原始集合类型
            if (value instanceof Set) {
                return new HashSet<>(processedList);
            } else {
                return processedList;
            }
        } else if (value.getClass().isArray()) {
            // 处理数组类型
            Object[] array = (Object[]) value;
            Object[] processedArray = new Object[array.length];
            for (int i = 0; i < array.length; i++) {
                processedArray[i] = processDateTimeValueRecursive(array[i]);
            }
            return processedArray;
        } else if (value instanceof Map) {
            // 处理Map类型（递归处理值）
            Map<?, ?> map = (Map<?, ?>) value;
            Map<Object, Object> processedMap = new HashMap<>();
            for (Map.Entry<?, ?> mapEntry : map.entrySet()) {
                processedMap.put(mapEntry.getKey(), processDateTimeValueRecursive(mapEntry.getValue()));
            }
            return processedMap;
        }

        // 处理单个日期时间类型的时区转换
        return processSingleDateTimeValue(value);
    }

    /**
     * 处理单个日期时间类型的时区转换
     *
     * @param value 原始值
     * @return 处理后的值
     */
    private Object processSingleDateTimeValue(Object value) {
        if (value == null) {
            return null;
        }

        // 如果配置了数据库时区，进行时区转换
        if (this.timeZone != null) {
            if (value instanceof java.util.Date) {
                // java.util.Date 转换为数据库时区的 LocalDateTime
                java.util.Date date = (java.util.Date) value;
                return date.toInstant().atZone(this.timeZone).toLocalDateTime();
            } else if (value instanceof java.time.LocalDateTime) {
                // LocalDateTime 没有时区信息，直接返回
                return value;
            } else if (value instanceof java.time.ZonedDateTime) {
                // ZonedDateTime 转换为数据库时区
                java.time.ZonedDateTime zonedDateTime = (java.time.ZonedDateTime) value;
                return zonedDateTime.withZoneSameInstant(this.timeZone).toLocalDateTime();
            } else if (value instanceof java.time.OffsetDateTime) {
                // OffsetDateTime 转换为数据库时区
                java.time.OffsetDateTime offsetDateTime = (java.time.OffsetDateTime) value;
                return offsetDateTime.atZoneSameInstant(this.timeZone).toLocalDateTime();
            } else if (value instanceof java.time.Instant) {
                // Instant 转换为数据库时区的 LocalDateTime
                java.time.Instant instant = (java.time.Instant) value;
                return instant.atZone(this.timeZone).toLocalDateTime();
            } else if (value instanceof java.time.LocalDate) {
                // LocalDate 转换为数据库时区的 LocalDate
                java.time.LocalDate localDate = (java.time.LocalDate) value;
                return localDate.atStartOfDay(this.timeZone).toLocalDate();
            } else if (value instanceof java.time.LocalTime) {
                // LocalTime 转换为数据库时区的 LocalTime
                java.time.LocalTime localTime = (java.time.LocalTime) value;
                return localTime.atDate(java.time.LocalDate.now()).atZone(this.timeZone).toLocalTime();
            } else if (value instanceof java.sql.Date) {
                // java.sql.Date 转换为数据库时区的 LocalDate
                java.sql.Date sqlDate = (java.sql.Date) value;
                return sqlDate.toLocalDate();
            } else if (value instanceof java.sql.Time) {
                // java.sql.Time 转换为数据库时区的 LocalTime
                java.sql.Time sqlTime = (java.sql.Time) value;
                return sqlTime.toLocalTime();
            } else if (value instanceof java.sql.Timestamp) {
                // java.sql.Timestamp 转换为数据库时区的 LocalDateTime
                java.sql.Timestamp timestamp = (java.sql.Timestamp) value;
                return timestamp.toLocalDateTime();
            }
        }

        // 如果没有配置时区或不是日期时间类型，直接返回原值
        return value;
    }


    /**
     * 根据SQL查询语句和参数查找指定类型的对象列表
     * <p>命名参数不区分大小写，但建议与传入的 Map 或 JavaBean 属性保持一致</p>
     * <p>SQL 语句中的命名参数必须以冒号（:）开头，例如 :name。</p>
     * <p>
     * 此方法使用命名参数（如 :name）执行查询，参数值从 params 中获取。</p>
     *
     * <pre>
     *     示例1：  String sql = "UPDATE users SET name = :name, age = :age WHERE id = :id";
     *              SqlParameterSource params = new MapSqlParameterSource()
     *             .addValue("name", user.getName())
     *             .addValue("age", user.getAge())
     *             .addValue("id", user.getId());
     * </pre>
     * <p>特别注意：在命名参数中，Like 查询需要特别注意，因为需要将百分号（%）包含在参数值中。</p>
     * <pre>
     *     示例2：  String sql = "SELECT * FROM users WHERE name LIKE :name";
     *             SqlParameterSource params = new MapSqlParameterSource()
     *             .addValue("name", "%" + user.getName() + "%");
     * </pre>
     * <p>NamedParameterJdbcTemplate 支持 IN 子句，可以使用具名参数传入一个集合。</p>
     * <pre>
     *     示例3：  String sql = "SELECT * FROM users WHERE id IN (:ids)";
     *             SqlParameterSource params = new MapSqlParameterSource()
     *             .addValue("ids", List.of(1, 2, 3));
     * </pre>
     *
     * @param <T>    查询结果的对象类型
     * @param clazz  返回结果的类型Class对象
     * @param sql    执行的SQL查询语句
     * @param params SQL查询参数
     * @return 指定类型的对象列表
     */
    @Override
    public <T> List<T> find(Class<T> clazz, String sql, SqlParameterSource params) {
        if (log.isTraceEnabled()) {
            log.trace("{}执行查询：{}，参数：{}", LOG_PREFIX, sql, params);
        }
        if (null == params) {
            return FieldUtils.isBasicResult(clazz) ? this.jdbcTemplate.queryForList(sql, clazz) : this.jdbcTemplate.query(sql, new SimpleRowMapper<>(clazz));
        }
        return FieldUtils.isBasicResult(clazz) ? this.namedParameterJdbcTemplate.queryForList(sql, params, clazz) : this.namedParameterJdbcTemplate.query(sql, params, new SimpleRowMapper<>(clazz));
    }

    /**
     * 根据SQL查询语句和参数查找指定类型的对象列表
     * <p>命名参数不区分大小写，但建议与传入的 Map 或 JavaBean 属性保持一致</p>
     * <p>SQL 语句中的命名参数必须以冒号（:）开头，例如 :name。</p>
     * <p>
     * 此方法使用命名参数（如 :name）执行查询，参数值从 params 中获取。</p>
     * <pre>
     *     示例1：
     *     String sql = "UPDATE users SET name = :name, age = :age WHERE id = :id";
     *     Map<String, Object> params = new HashMap<>();
     *     params.put("name", user.getName());
     *     params.put("age", user.getAge());
     *     params.put("id", user.getId());
     * </pre>
     * <p>特别注意：在命名参数中，Like 查询需要特别注意，因为需要将百分号（%）包含在参数值中。</p>
     * <pre>
     *     示例2：  String sql = "SELECT * FROM users WHERE name LIKE :name";
     *             Map<String, Object> params = new HashMap<>();
     *             params.put("name", "%" + user.getName() + "%");
     * </pre>
     * <p>NamedParameterJdbcTemplate 支持 IN 子句，可以使用具名参数传入一个集合。</p>
     * <pre>
     *     示例3：  String sql = "SELECT * FROM users WHERE id IN (:ids)";
     *             Map<String, Object> params = new HashMap<>();
     *             params.put("ids", List.of(1, 2, 3));
     * </pre>
     *
     * @param <T>    查询结果的对象类型
     * @param clazz  返回结果的类型Class对象
     * @param slice  分页参数
     * @param sql    查询SQL语句
     * @param params SQL查询参数Map，键为参数名，值为参数值
     * @return 符合查询条件的对象列表，当前实现返回空列表
     */
    @Override
    public <T> Page<T> findPage(Class<T> clazz, Slice slice, String sql, Map<String, Object> params) {
        if (clazz == null || StringUtils.isBlank(sql)) {
            log.info("{}参数不完整，clazz: {}, sql: {}", LOG_PREFIX, clazz, sql);
            return Page.ofEmpty();
        }

        // 处理分页参数
        slice = slice == null ? new Slice(DEFAULT_PAGE_SIZE, DEFAULT_PAGE_NUMBER) : slice;

        try {
            // 构建分页SQL
            String paginatedSql = buildPaginatedSql(sql, slice);
            // 执行分页查询
            List<T> dataList = this.find(clazz, paginatedSql, params);
            // 构建总数查询SQL
            String countSql = buildCountSql(sql);
            // 执行总数查询
            List<Long> countResult = this.find(Long.class, countSql, params);
            Long totalCount = countResult == null || countResult.isEmpty() ? 0L : countResult.get(0);

            // 返回分页结果
            return Page.of(dataList, totalCount, slice);

        } catch (Exception e) {
            log.error("{}执行findPage查询时发生异常，sql: {}, params: {}", LOG_PREFIX, sql, params, e);
            return Page.ofEmpty(slice.getSize());
        }
    }

    /**
     * 构建分页SQL
     *
     * @param sql   原始SQL
     * @param slice 分页参数
     * @return 分页SQL
     */
    private String buildPaginatedSql(String sql, Slice slice) {
        if (slice == null) {
            return sql;
        }

        // 移除SQL末尾的分号（如果有）
        String cleanSql = sql.trim();
        if (cleanSql.endsWith(";")) {
            cleanSql = cleanSql.substring(0, cleanSql.length() - 1);
        }
        // 构建分页SQL（使用LIMIT和OFFSET）
        int offset = (slice.getNum().intValue() - 1) * slice.getSize().intValue();
        String paginatedSql = String.format("%s LIMIT %d OFFSET %d", cleanSql, slice.getSize(), offset);

        return paginatedSql;
    }

    /**
     * 构建总数查询SQL
     *
     * @param sql 原始SQL
     * @return 总数查询SQL
     */
    private String buildCountSql(String sql) {
        // 移除SQL末尾的分号（如果有）
        String cleanSql = sql.trim();
        if (cleanSql.endsWith(";")) {
            cleanSql = cleanSql.substring(0, cleanSql.length() - 1);
        }

        // 检查SQL是否包含ORDER BY子句，如果有则移除
        String upperSql = cleanSql.toUpperCase();
        int orderByIndex = upperSql.indexOf("ORDER BY");
        if (orderByIndex != -1) {
            cleanSql = cleanSql.substring(0, orderByIndex).trim();
        }

        // 构建总数查询SQL
        return String.format("SELECT COUNT(1) FROM (%s) AS temp_count_table", cleanSql);
    }

    @Override
    public <T> List<T> find(Class<T> clazz, NamedHandler handler) {
        final Map<String, Object> map = new HashMap<>();
        String sql = handler.handle(map);
        return this.find(clazz, sql, map);
    }

    /**
     * 根据指定的处理逻辑查找分页数据
     *
     * @param clazz   要查询的实体类类型
     * @param slice   分页信息，包含页码和每页大小
     * @param handler 命名处理程序，用于生成SQL语句和参数
     * @param <T>     实体类泛型参数
     * @return 返回指定类型的分页结果
     */
    @Override
    public <T> Page<T> findPage(Class<T> clazz, Slice slice, NamedHandler handler) {
        // 构造SQL参数映射
        final Map<String, Object> map = new HashMap<>();
        // 通过处理器生成SQL语句
        String sql = handler.handle(map);
        // 执行分页查询并返回结果
        return this.findPage(clazz, slice, sql, map);
    }

}
