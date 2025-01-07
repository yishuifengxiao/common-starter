package com.yishuifengxiao.common.jdbc;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.jdbc.executor.ExecuteExecutor;
import com.yishuifengxiao.common.jdbc.executor.impl.SimpleExecuteExecutor;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.extractor.SimpleFieldExtractor;
import com.yishuifengxiao.common.jdbc.translator.DeleteTranslator;
import com.yishuifengxiao.common.jdbc.translator.InsertTranslator;
import com.yishuifengxiao.common.jdbc.translator.QueryTranslator;
import com.yishuifengxiao.common.jdbc.translator.UpdateTranslator;
import com.yishuifengxiao.common.jdbc.translator.impl.SimpleDeleteTranslator;
import com.yishuifengxiao.common.jdbc.translator.impl.SimpleInsertTranslator;
import com.yishuifengxiao.common.jdbc.translator.impl.SimpleQueryTranslator;
import com.yishuifengxiao.common.jdbc.translator.impl.SimpleUpdateTranslator;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.entity.Slice;
import com.yishuifengxiao.common.tool.text.RegexUtil;
import com.yishuifengxiao.common.tool.utils.ValidateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统JdbcTemplate操作器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleJdbcHelper implements JdbcHelper {

    private final DeleteTranslator deleteTranslator = new SimpleDeleteTranslator();

    private final InsertTranslator insertTranslator = new SimpleInsertTranslator();

    private final QueryTranslator queryTranslator = new SimpleQueryTranslator();

    private final UpdateTranslator updateTranslator = new SimpleUpdateTranslator();

    private final FieldExtractor fieldExtractor = new SimpleFieldExtractor();

    private final ExecuteExecutor executeExecutor = new SimpleExecuteExecutor();

    private JdbcTemplate jdbcTemplate;

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
        if (null == primaryKey || null == primaryKey) {
            return null;
        }
        String tableName = fieldExtractor.extractTableName(clazz);
        List<FieldValue> fields = fieldExtractor.extractFiled(clazz);
        FieldValue fieldValue = primaryKey(fields);

        String sql = queryTranslator.findAll(tableName, Arrays.asList(fieldValue), false, null, new Slice(null, 1));
        List<T> list = executeExecutor.findAll(jdbcTemplate, clazz, sql, new Object[]{primaryKey});

        return null == list || list.isEmpty() ? null : list.get(0);
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
        if (null == t) {
            return null;
        }
        String tableName = fieldExtractor.extractTableName(t.getClass());
        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(t);
        List<FieldValue> values = fieldValues.stream().filter(FieldValue::isNotNullVal).collect(Collectors.toList());
        Object[] params = values.stream().map(FieldValue::getValue).toArray(Object[]::new);


        String sql = queryTranslator.findAll(tableName, values, likeMode, null, null);

        String countSql =
                new StringBuffer("SELECT count(1) from ( ").append(sql).append(" ) as " + "__tmp_result_9").toString();
        List<Long> numbers = executeExecutor.findAll(jdbcTemplate, Long.class, countSql, params);

        return null == numbers || numbers.isEmpty() ? 0 : numbers.get(0).longValue();
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
        if (null == t) {
            return null;
        }
        String tableName = fieldExtractor.extractTableName(t.getClass());

        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(t);
        List<FieldValue> values = fieldValues.stream().filter(FieldValue::isNotNullVal).collect(Collectors.toList());
        Object[] params = values.stream().map(FieldValue::getValue).toArray(Object[]::new);


        String sql = queryTranslator.findAll(tableName, values, likeMode, createOrder(fieldValues, orders),
                new Slice(null, 1));


        List<?> list = executeExecutor.findAll(jdbcTemplate, t.getClass(), sql, params);
        return null == list || list.isEmpty() ? null : (T) list.get(0);
    }

    /**
     * 处理排序条件
     *
     * @param fieldValues pojo实例数据属性列表
     * @param orders      排序条件
     * @return 处理后的排序条件
     */
    private List<Order> createOrder(List<FieldValue> fieldValues, Order... orders) {
        if (null == orders || orders.length == 0) {
            return null;
        }
        //@formatter:off
        return Arrays.asList(orders).stream().filter(Objects::nonNull)
                .filter(v -> StringUtils.isNotBlank(v.getOrderName()))
                .map(s -> {
                    String orderName =
                            fieldValues.stream().filter(v -> null != v.getField())
                                    .filter(v -> v.getField().getName().equalsIgnoreCase(s.getOrderName()))
                                    .map(FieldValue::getSimpleName).findFirst().orElse(null);
                    if (StringUtils.isNotBlank(orderName)) {
                        s.setOrderName(orderName);
                    }
                    return s;
                }).collect(Collectors.toList());
        //@formatter:on
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
        if (null == t) {
            return Collections.EMPTY_LIST;
        }
        String tableName = fieldExtractor.extractTableName(t.getClass());

        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(t);
        List<FieldValue> values = fieldValues.stream().filter(FieldValue::isNotNullVal).collect(Collectors.toList());
        Object[] params = values.stream().map(FieldValue::getValue).toArray(Object[]::new);

        String querySql = queryTranslator.findAll(tableName, values, likeMode, createOrder(fieldValues, orders), null);

        List<?> list = executeExecutor.findAll(jdbcTemplate, t.getClass(), querySql, params);
        return null == list ? Collections.EMPTY_LIST : (List<T>) list;
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
        slice = null == slice ? new Slice(10, 1) : slice;
        if (null == t) {
            return Page.ofEmpty(slice.size());
        }
        String tableName = fieldExtractor.extractTableName(t.getClass());

        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(t);

        List<FieldValue> values = fieldValues.stream().filter(FieldValue::isNotNullVal).collect(Collectors.toList());
        Object[] params = values.stream().map(FieldValue::getValue).toArray(Object[]::new);

        String querySql = queryTranslator.findAll(tableName, values, likeMode, createOrder(fieldValues, orders), slice);
        String countSql = queryTranslator.findAll(tableName, values, likeMode, null, null);

        countSql =
                new StringBuffer("SELECT count(1) from ( ").append(countSql).append(" ) as " + "__tmp_result_9").toString();

        List<?> list = executeExecutor.findAll(jdbcTemplate, t.getClass(), querySql, params);
        List<Long> numbers = executeExecutor.findAll(jdbcTemplate, Long.class, countSql, params);

        Long count = null == numbers || numbers.isEmpty() ? 0 : numbers.get(0);
        return (Page<T>) Page.of(list, count, slice);
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
        if (null == pageQuery) {
            return Page.ofEmpty();
        }
        return this.findPage(pageQuery.getQuery(), likeMode, pageQuery, orders);
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
        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(t);
        FieldValue primaryKey = primaryKey(fieldValues);

        String tableName = fieldExtractor.extractTableName(t.getClass());
        String sql = updateTranslator.updateByPrimaryKey(tableName, primaryKey, fieldValues);

        Object[] params = fieldValues.stream().map(FieldValue::getValue).toArray(Object[]::new);

        return executeExecutor.execute(jdbcTemplate, sql, params);
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
        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(t);
        FieldValue primaryKey = primaryKey(fieldValues);
        String tableName = fieldExtractor.extractTableName(t.getClass());

        List<FieldValue> values = fieldValues.stream().filter(FieldValue::isNotNullVal).collect(Collectors.toList());
        Object[] params = values.stream().map(FieldValue::getValue).toArray(Object[]::new);


        String sql = updateTranslator.updateByPrimaryKey(tableName, primaryKey, values);

        return executeExecutor.execute(jdbcTemplate, sql, params);
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
        if (null == primaryKeys || primaryKeys.length == 0) {
            return 0;
        }
        List<Object> params = Arrays.asList(primaryKeys).stream().filter(Objects::nonNull).filter(v -> {
            if (v instanceof String val) {
                return StringUtils.isNotBlank(val.trim());
            }
            return true;
        }).collect(Collectors.toList());
        if (null == params || params.isEmpty()) {
            return 0;
        }

        String tableName = fieldExtractor.extractTableName(clazz);

        List<FieldValue> fields = fieldExtractor.extractFiled(clazz);
        FieldValue primaryKey = primaryKey(fields);

        String sql = deleteTranslator.deleteByPrimaryKeys(tableName, primaryKey.getSimpleName(), params);

        return executeExecutor.execute(jdbcTemplate, sql, null);


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
        if (null == t) {
            return null;
        }
        String tableName = fieldExtractor.extractTableName(t.getClass());

        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(t);
        String sql = insertTranslator.insert(tableName, fieldValues);

        return this.executeExecutor.update(this.jdbcTemplate, sql, fieldValues);
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
        if (null == t) {
            return null;
        }
        List<FieldValue> fieldValues = fieldExtractor.extractFieldValue(t);
        List<FieldValue> values =
                fieldValues.stream().filter(Objects::nonNull).filter(v -> v.isPrimary() && v.isNotNullVal()).distinct().collect(Collectors.toList());

        if (values.isEmpty()) {
            return this.insert(t);
        } else if (values.size() == 1) {
            this.deleteByPrimaryKey(t.getClass(), values.get(0).getValue());
            return this.insert(t);
        } else {
            //多个主键属性
            ValidateUtils.throwException(JdbcError.MULTIPLE_PRIMARY_KEYS);
        }
        return null;
    }

    /**
     * 批量保存数据
     *
     * @param list 待批量保存的数据
     * @param <T>  POJO数据类型
     */
    @Override
    public synchronized <T> void saveAll(List<T> list) {
        if (null == list || list.isEmpty()) {
            return;
        }
        T val = list.stream().filter(Objects::nonNull).findFirst().orElse(null);
        if (null == val) {
            return;
        }

        String tableName = fieldExtractor.extractTableName(val.getClass());
        List<FieldValue> fieldValues = fieldExtractor.extractFiled(val.getClass());

        String sql = insertTranslator.insert(tableName, fieldValues);

        int[] types = new int[fieldValues.size()];
        for (int i = 0; i < fieldValues.size(); i++) {
            types[i] = fieldValues.get(i).sqlType().getVendorTypeNumber();
        }

        List<Object[]> values = list.parallelStream().filter(Objects::nonNull).map(s -> {
            List<FieldValue> vals = fieldExtractor.extractFieldValue(s);
            return fieldValues.stream().map(v -> vals.stream().filter(t -> Objects.equals(v.getField().getName(),
                    t.getField().getName())).findFirst().orElse(null)).toArray(Object[]::new);
        }).collect(Collectors.toList());


        executeExecutor.batchUpdate(jdbcTemplate, sql, types, values);
    }

    @Override
    public <T> T findOne(Class<T> clazz, String sql, Object... params) {
        List<T> list = this.findAll(clazz, sql, params);
        return list.isEmpty() ? null : list.get(0);
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
        Map<String, Object> map = detectingQueryType(sql, params);
        List<T> list = executeExecutor.findAll(this.jdbcTemplate(), clazz, sql, null == map ? params : map);

        return null == list ? Collections.EMPTY_LIST : list;
    }

    /**
     * 根据原生sql进行分页查询
     * 注意：此原生sql不能携带分页参数
     *
     * @param clazz  数据类型
     * @param slice  分页参数
     * @param sql    原生sql
     * @param params 查询参数
     * @param <T>    结果数据的类型
     * @return
     */
    @Override
    public <T> Page<T> findPage(Class<T> clazz, Slice slice, String sql, Object... params) {
        //formatter:off
        slice = null == slice ? new Slice(10, 1) : slice;

        Map<String, Object> map = detectingQueryType(sql, params);

        sql = sql.trim().endsWith(";") ? StringUtils.substringAfterLast(sql.trim(), ";") : sql.trim();
        String dataSql = new StringBuffer("SELECT __tmp_result_8.* from ( ").append(sql).append(" ) as " +
                "__tmp_result_8 " + "limit" + " ").append(slice.startOffset().longValue()).append(",").append(slice.size().longValue()).toString();
        String countSql =
                new StringBuffer("SELECT count(1) from ( ").append(sql).append(" ) as " + "__tmp_result_9").toString();

        List<T> list = this.executeExecutor.findAll(jdbcTemplate, clazz, dataSql, null == map ? params : map);

        List<Long> numbers = this.executeExecutor.findAll(jdbcTemplate, Long.class, countSql, null == map ? params :
                map);
        Long count = null == numbers || numbers.isEmpty() ? 0 : numbers.get(0);

        //formatter:on
        return Page.of(list, count, slice);
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
    }


    /**
     * 侦测是否命名参数查询
     *
     * @param sql  查询sql语句
     * @param args 查询参数
     * @return 若是命名参数查询则返回查询参数，否则为null
     */
    private Map<String, Object> detectingQueryType(String sql, Object[] args) {
        if (StringUtils.isBlank(sql) || null == args || args.length == 0) {
            return null;
        }
        if (args.length == 1 && null != args[0] && args[0] instanceof Map) {
            return (Map<String, Object>) args[0];
        }
        List<String> list = RegexUtil.extractAll(":(\\w+)", sql);
        if (null == list || !Objects.equals(list.size(), args.length)) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            map.put(StringUtils.trim(StringUtils.substringAfter(list.get(i), ":")), args[i]);
        }
        return map;
    }

    /**
     * 提取主键属性
     *
     * @param fieldValues 数据字段属性
     * @return 主键属性
     */
    private FieldValue primaryKey(List<FieldValue> fieldValues) {
        if (null == fieldValues || fieldValues.isEmpty()) {
            ValidateUtils.throwException(JdbcError.NO_PRIMARY_KEY);
        }
        List<FieldValue> values =
                fieldValues.stream().filter(Objects::nonNull).filter(FieldValue::isPrimary).collect(Collectors.toList());
        if (null == values || values.isEmpty()) {
            ValidateUtils.throwException(JdbcError.NO_PRIMARY_KEY);
        }
        if (values.size() > 1) {
            ValidateUtils.throwException(JdbcError.MULTIPLE_PRIMARY_KEYS);
        }
        return values.get(0);
    }
}
