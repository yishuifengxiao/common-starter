package com.yishuifengxiao.common.jdbc;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.Example;
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
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.Slice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

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
     * 根据主键从指定表查询一条数据
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param primaryKey 主键
     * @return 查询到的数据
     */
    @Override
    public <T> T findByPrimaryKey(Class<T> clazz, Object primaryKey) {
        return queryTranslator.findByPrimaryKey(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, primaryKey);
    }

    /**
     * 查询符合条件的记录的数量
     *
     * @param <T> POJO类
     * @param t   查询条件
     * @return 符合条件的记录的数量
     */
    @Override
    public <T> Long countAll(T t) {
        return queryTranslator.countAll(this.jdbcTemplate(), fieldExtractor, executeExecutor, t);
    }

    /**
     * 查询符合条件的记录的数量
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param conditions 筛选条件
     * @return 符合条件的记录的数量
     */
    @Override
    public <T> Long countAll(Class<T> clazz, Condition... conditions) {
        return queryTranslator.countAll(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, this.collect(conditions));
    }

    /**
     * 查询符合条件的记录的数量
     *
     * @param <T>     POJO类
     * @param clazz   POJO类
     * @param example 筛选条件
     * @return 符合条件的记录的数量
     */
    @Override
    public <T> Long countAll(Class<T> clazz, Example example) {
        return queryTranslator.countAll(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, null == example ? new ArrayList<>() : example.toCondition());
    }

    /**
     * 收集筛选条件
     *
     * @param conditions
     * @return
     */
    private List<Condition> collect(Condition... conditions) {
        List<Condition> list = new ArrayList<>();
        if (null != conditions) {
            for (Condition condition : conditions) {
                if (null != condition) {
                    list.add(condition);
                }
            }
        }
        return list;
    }

    /**
     * 查询所有符合条件的数据
     *
     * @param <T> POJO类
     * @param t   查询条件
     * @return 符合条件的数据
     */
    @Override
    public <T> T findOne(T t) {
        return DataUtil.first(this.findAll(t));
    }

    /**
     * 查询所有符合条件的数据
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param conditions 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> T findOne(Class<T> clazz, Condition... conditions) {
        return DataUtil.first(this.findAll(clazz, conditions));
    }

    /**
     * 查询所有符合条件的数据
     *
     * @param <T>     POJO类
     * @param clazz   POJO类
     * @param example 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> T findOne(Class<T> clazz, Example example) {
        return DataUtil.first(this.findAll(clazz, example));
    }

    /**
     * 查询所有符合条件的数据
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param conditions 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> T findOne(Class<T> clazz, List<Condition> conditions) {
        return DataUtil.first(this.findAll(clazz, conditions));
    }

    /**
     * 查询所有符合条件的数据
     *
     * @param <T> POJO类
     * @param t   查询条件
     * @return 符合条件的数据
     */
    @Override
    public <T> List<T> findAll(T t) {
        return queryTranslator.findAll(this.jdbcTemplate(), fieldExtractor, executeExecutor, t, null);
    }

    /**
     * 查询所有符合条件的数据
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param conditions 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> List<T> findAll(Class<T> clazz, Condition... conditions) {
        return queryTranslator.findAll(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, this.collect(conditions), null);
    }

    /**
     * 查询所有符合条件的数据
     *
     * @param <T>     POJO类
     * @param clazz   POJO类
     * @param example 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> List<T> findAll(Class<T> clazz, Example example) {
        return queryTranslator.findAll(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, null == example ? new ArrayList<>() : example.toCondition(), null);
    }

    /**
     * 查询所有符合条件的数据
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param conditions 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> List<T> findAll(Class<T> clazz, List<Condition> conditions) {
        return queryTranslator.findAll(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, conditions, null);
    }

    /**
     * 查询所有符合条件的数据
     *
     * @param <T>   POJO类
     * @param t     查询条件
     * @param order 排序条件
     * @return 符合条件的数据
     */
    @Override
    public <T> List<T> findAll(T t, Order order) {
        return queryTranslator.findAll(this.jdbcTemplate(), fieldExtractor, executeExecutor, t, order);
    }

    /**
     * 查询所有符合条件的数据
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param order      排序条件
     * @param conditions 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> List<T> findAll(Class<T> clazz, Order order, Condition... conditions) {
        return queryTranslator.findAll(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, this.collect(conditions), order);
    }

    /**
     * 查询所有符合条件的数据
     *
     * @param <T>     POJO类
     * @param clazz   POJO类
     * @param order   排序条件
     * @param example 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> List<T> findAll(Class<T> clazz, Order order, Example example) {
        return queryTranslator.findAll(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, null == example ? new ArrayList<>() : example.toCondition(), order);
    }

    /**
     * 查询所有符合条件的数据
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param order      排序条件
     * @param conditions 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> List<T> findAll(Class<T> clazz, Order order, List<Condition> conditions) {
        return queryTranslator.findAll(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, conditions, order);
    }

    /**
     * 查询所有符合条件的数据（默认升序）
     *
     * @param <T>       POJO类
     * @param t         查询条件
     * @param orderName 排序字段，必须为对应的POJO属性的名字
     * @return 符合条件的数据
     */
    @Override
    public <T> List<T> findAll(T t, String orderName) {
        return queryTranslator.findAll(this.jdbcTemplate(), fieldExtractor, executeExecutor, t, Order.of(orderName));
    }

    /**
     * 查询所有符合条件的数据
     *
     * @param <T>       POJO类
     * @param t         查询条件
     * @param orderName 排序字段，必须为对应的POJO属性的名字
     * @param direction 排序方向
     * @return 符合条件的数据
     */
    @Override
    public <T> List<T> findAll(T t, String orderName, Order.Direction direction) {
        return queryTranslator.findAll(this.jdbcTemplate(), fieldExtractor, executeExecutor, t, Order.of(orderName, direction));
    }

    /**
     * 根据条件查询前几条符合条件的记录
     *
     * @param <T>    POJO类
     * @param t      查询条件
     * @param order  排序条件
     * @param topNum 查询出的记录的数量
     * @return 符合条件的数据
     */
    @Override
    public <T> List<T> findTop(T t, Order order, int topNum) {
        return queryTranslator.findTop(this.jdbcTemplate(), fieldExtractor, executeExecutor, t, order, topNum);
    }

    /**
     * 根据条件查询前几条符合条件的记录
     *
     * @param <T>     POJO类
     * @param clazz   POJO类
     * @param order   排序条件
     * @param topNum  查询出的记录的数量
     * @param example 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> List<T> findTop(Class<T> clazz, Order order, int topNum, Example example) {
        return queryTranslator.findTop(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, example.toCondition(), order, topNum);
    }

    /**
     * 根据条件查询前几条符合条件的记录
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param order      排序条件
     * @param topNum     查询出的记录的数量
     * @param conditions 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> List<T> findTop(Class<T> clazz, Order order, int topNum, Condition... conditions) {
        return queryTranslator.findTop(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, this.collect(conditions), order, topNum);
    }

    /**
     * 分页查询所有符合条件的数据
     *
     * @param <T>      POJO类
     * @param t        查询条件
     * @param pageSize 分页大小
     * @param pageNum  当前页页码
     * @return 符合条件的数据
     */
    @Override
    public <T> Page<T> findPage(T t, int pageSize, int pageNum) {
        return queryTranslator.findPage(this.jdbcTemplate(), fieldExtractor, executeExecutor, t, null, pageSize, pageNum);
    }

    /**
     * 分页查询所有符合条件的数据
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param pageSize   分页大小
     * @param pageNum    当前页页码
     * @param conditions 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> Page<T> findPage(Class<T> clazz, int pageSize, int pageNum, Condition... conditions) {
        return queryTranslator.findPage(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, this.collect(conditions), null, pageSize, pageNum);
    }

    /**
     * 分页查询所有符合条件的数据
     *
     * @param <T>      POJO类
     * @param clazz    POJO类
     * @param pageSize 分页大小
     * @param pageNum  当前页页码
     * @param example  筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> Page<T> findPage(Class<T> clazz, int pageSize, int pageNum, Example example) {
        return queryTranslator.findPage(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, null == example ? new ArrayList<>() : example.toCondition(), null, pageSize, pageNum);
    }

    /**
     * 分页查询所有符合条件的数据
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param pageSize   分页大小
     * @param pageNum    当前页页码
     * @param conditions 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> Page<T> findPage(Class<T> clazz, int pageSize, int pageNum, List<Condition> conditions) {
        return queryTranslator.findPage(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, conditions, null, pageSize, pageNum);
    }

    /**
     * 分页查询所有符合条件的数据
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param pageSize   分页大小
     * @param pageNum    当前页页码
     * @param order      排序条件
     * @param conditions 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> Page<T> findPage(Class<T> clazz, int pageSize, int pageNum, Order order, Condition... conditions) {
        return queryTranslator.findPage(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, this.collect(conditions), order, pageSize, pageNum);
    }

    /**
     * 分页查询所有符合条件的数据
     *
     * @param <T>      POJO类
     * @param clazz    POJO类
     * @param pageSize 分页大小
     * @param pageNum  当前页页码
     * @param order    排序条件
     * @param example  筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> Page<T> findPage(Class<T> clazz, int pageSize, int pageNum, Order order, Example example) {
        return queryTranslator.findPage(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, null == example ? new ArrayList<>() : example.toCondition(), order, pageSize, pageNum);
    }

    /**
     * 分页查询所有符合条件的数据
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param pageSize   分页大小
     * @param pageNum    当前页页码
     * @param order      排序条件
     * @param conditions 筛选条件
     * @return 符合条件的数据
     */
    @Override
    public <T> Page<T> findPage(Class<T> clazz, int pageSize, int pageNum, Order order, List<Condition> conditions) {
        return queryTranslator.findPage(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, conditions, order, pageSize, pageNum);
    }

    /**
     * 分页查询所有符合条件的数据
     *
     * @param <T>      POJO类
     * @param t        查询条件
     * @param pageSize 分页大小
     * @param pageNum  当前页页码
     * @param order    排序条件
     * @return 符合条件的数据
     */
    @Override
    public <T> Page<T> findPage(T t, int pageSize, int pageNum, Order order) {
        return queryTranslator.findPage(this.jdbcTemplate(), fieldExtractor, executeExecutor, t, order, pageSize, pageNum);
    }

    /**
     * 分页查询所有符合条件的数据
     *
     * @param <T>       POJO类
     * @param t         查询条件
     * @param pageSize  分页大小
     * @param pageNum   当前页页码
     * @param orderName 排序字段，必须为对应的POJO属性的名字
     * @return 符合条件的数据
     */
    @Override
    public <T> Page<T> findPage(T t, int pageSize, int pageNum, String orderName) {
        return queryTranslator.findPage(this.jdbcTemplate(), fieldExtractor, executeExecutor, t, null, pageSize, pageNum);
    }

    /**
     * 分页查询所有符合条件的数据
     *
     * @param <T>       POJO类
     * @param t         查询条件
     * @param pageSize  分页大小
     * @param pageNum   当前页页码
     * @param orderName 排序字段，必须为对应的POJO属性的名字
     * @param direction 排序方向
     * @return 符合条件的数据
     */
    @Override
    public <T> Page<T> findPage(T t, int pageSize, int pageNum, String orderName, Order.Direction direction) {
        return queryTranslator.findPage(this.jdbcTemplate(), fieldExtractor, executeExecutor, t, Order.of(orderName, direction), pageSize, pageNum);
    }

    /**
     * 根据主键全属性更新方式更新一条数据
     *
     * @param <T> POJO类
     * @param t   待更新的数据
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int updateByPrimaryKey(T t) {
        return updateTranslator.updateByPrimaryKey(this.jdbcTemplate(), fieldExtractor, executeExecutor, false, t);
    }

    /**
     * 根据主键可选属性更新方式更新一条数据
     *
     * @param <T> POJO类
     * @param t   待更新的数据
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int updateByPrimaryKeySelective(T t) {
        return updateTranslator.updateByPrimaryKey(this.jdbcTemplate(), fieldExtractor, executeExecutor, true, t);
    }

    /**
     * 根据条件全属性更新方式批量更新数据
     *
     * @param <T>     POJO类
     * @param t       待更新的数据
     * @param example 筛选条件
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int update(T t, Example example) {
        return updateTranslator.update(this.jdbcTemplate(), fieldExtractor, executeExecutor, false, t, null == example ? new ArrayList<>() : example.toCondition());
    }

    /**
     * 根据条件全属性更新方式批量更新数据
     *
     * @param <T>        POJO类
     * @param t          待更新的数据
     * @param conditions 筛选条件
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int update(T t, List<Condition> conditions) {
        return updateTranslator.update(this.jdbcTemplate(), fieldExtractor, executeExecutor, false, t, conditions);
    }

    /**
     * 根据条件全属性更新方式批量更新数据
     *
     * @param <T>       POJO类
     * @param t         待更新的数据
     * @param condition 更新条件
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int update(T t, T condition) {
        return updateTranslator.update(this.jdbcTemplate(), fieldExtractor, executeExecutor, false, t, condition);
    }

    /**
     * 根据条件可选属性更新方式批量更新数据
     *
     * @param <T>       POJO类
     * @param t         待更新的数据
     * @param condition 更新条件
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int updateSelective(T t, T condition) {
        return updateTranslator.update(this.jdbcTemplate(), fieldExtractor, executeExecutor, true, t, condition);
    }

    /**
     * 根据条件可选属性更新方式批量更新数据
     *
     * @param <T>     POJO类
     * @param t       待更新的数据
     * @param example 筛选条件
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int updateSelective(T t, Example example) {
        return updateTranslator.update(this.jdbcTemplate(), fieldExtractor, executeExecutor, true, t, null == example ? new ArrayList<>() : example.toCondition());
    }

    /**
     * 根据条件可选属性更新方式批量更新数据
     *
     * @param <T>        POJO类
     * @param t          待更新的数据
     * @param conditions 筛选条件
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int updateSelective(T t, List<Condition> conditions) {
        return updateTranslator.update(this.jdbcTemplate(), fieldExtractor, executeExecutor, true, t, conditions);
    }

    /**
     * 根据主键删除一条数据
     *
     * @param <T>        POJO类
     * @param clazz      操作的对象
     * @param primaryKey 主键值
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int deleteByPrimaryKey(Class<T> clazz, Object primaryKey) {
        return deleteTranslator.deleteByPrimaryKey(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, primaryKey);

    }

    /**
     * 根据条件批量删除数据
     *
     * @param <T> POJO类
     * @param t   删除条件
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int delete(T t) {
        return deleteTranslator.delete(this.jdbcTemplate(), fieldExtractor, executeExecutor, true, t);
    }

    /**
     * 根据条件批量删除数据
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param conditions 筛选条件
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int delete(Class<T> clazz, Condition... conditions) {
        return deleteTranslator.delete(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, true, this.collect(conditions));
    }

    /**
     * 根据条件批量删除数据
     *
     * @param <T>     POJO类
     * @param clazz   POJO类
     * @param example 筛选条件
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int delete(Class<T> clazz, Example example) {
        return deleteTranslator.delete(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, true, null == example ? new ArrayList<>() : example.toCondition());
    }

    /**
     * 根据条件批量删除数据
     *
     * @param <T>        POJO类
     * @param clazz      POJO类
     * @param conditions 筛选条件
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int delete(Class<T> clazz, List<Condition> conditions) {
        return deleteTranslator.delete(this.jdbcTemplate(), fieldExtractor, executeExecutor, clazz, true, conditions);
    }

    /**
     * 以全属性方式新增一条数据
     *
     * @param <T> POJO类
     * @param t   待新增的数据
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int insert(T t) {
        return insertTranslator.insert(this.jdbcTemplate(), fieldExtractor, false, executeExecutor, t);
    }

    /**
     * 以可选属性方式新增一条数据
     *
     * @param <T> POJO类
     * @param t   待新增的数据
     * @return 受影响的记录的数量
     */
    @Override
    public <T> int insertSelective(T t) {
        return insertTranslator.insert(this.jdbcTemplate(), fieldExtractor, true, executeExecutor, t);
    }

    @Override
    public <T> T saveOrUpdate(T t) {
        if (isUpdate(t)) {
            this.updateByPrimaryKey(t);
        } else {
            this.insert(t);
        }
        return t;
    }


    @Override
    public <T> T saveOrUpdateSelective(T t) {
        if (isUpdate(t)) {
            this.updateByPrimaryKeySelective(t);
        } else {
            this.insertSelective(t);
        }
        return t;
    }

    /**
     * 判断是否更新操作
     *
     * @param t   待操作的数据
     * @param <T> POJO类
     * @return true表示更新操作，false为插入操作
     */
    private <T> boolean isUpdate(T t) {
        FieldValue primaryKey = fieldExtractor.extractPrimaryKey(t.getClass());
        Object value = fieldExtractor.extractValue(t, primaryKey.getName());
        if (null == value) {
            return false;
        }
        return this.countAll(t.getClass(), Condition.andEqual(primaryKey.getName(), value)) > 0;
    }


    @Override
    public <T> Optional<T> queryOne(Class<T> clazz, String sql, Object... params) {
        List<Object> args = Arrays.asList(params);
        List<T> list = executeExecutor.findAll(this.jdbcTemplate(), clazz, sql, args);
        return null == list || list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.get(0));
    }

    @Override
    public <T> Optional<List<T>> query(Class<T> clazz, String sql, Object... params) {
        List<Object> args = Arrays.asList(params);
        return Optional.ofNullable(executeExecutor.findAll(this.jdbcTemplate(), clazz, sql, args));
    }

    @Override
    public <T> Page<T> query(Class<T> clazz, Slice slice, String sql, Object... params) {
        sql = sql.trim().endsWith(";") ? StringUtils.substringAfterLast(sql.trim(), ";") : sql.trim();
        String dataSql = new StringBuffer("SELECT * from (").append(sql).append(") as __tmp_result limit ").append(slice.startOffset().longValue()).append(",").append(slice.endOffset().longValue()).toString();
        String countSql = new StringBuffer("SELECT count(*) from (").append(sql).append(") as __tmp_result").toString();
        return Page.of(this.query(clazz, dataSql, params).orElse(Collections.EMPTY_LIST), this.jdbcTemplate().queryForObject(countSql, Long.class, params), slice.size(), slice.num());
    }


    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    @Override
    public JdbcTemplate jdbcTemplate() {
        return this.jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
