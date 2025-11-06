/**
 *
 */
package com.yishuifengxiao.common.jdbc;

import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.common.tool.entity.Slice;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * JdbcTemplate操作器
 * </p>
 * <p>
 * 【注意】在没有特意指出的前提下，所有筛选条件的笔记方式为完全匹配
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface JdbcHelper {

    /**
     * 根据主键查询一条数据
     *
     * @param clazz      POJO类型
     * @param primaryKey 主键值
     * @param <T>        数据类型
     * @return 查询出来的数据
     */
    <T> T findByPrimaryKey(Class<T> clazz, Object primaryKey);


    /**
     * 根据pojo实例中的非空属性值查询出所有符合条件的数据的数量
     *
     * @param t        pojo实例
     * @param likeMode 是否对字符串属性进行模糊查询，true表示为是，false为否
     * @param <T>      数据类型
     * @return 所有符合条件的数据的数量
     */
    <T> Long countAll(T t, boolean likeMode);

    /**
     * 根据pojo实例中的非空属性值查询出一条符合条件的数据
     *
     * @param t        pojo实例
     * @param likeMode 是否对字符串属性进行模糊查询，true表示为是，false为否
     * @param orders   排序条件
     * @param <T>      数据类型
     * @return 查询出来的数据
     */
    <T> T findOne(T t, boolean likeMode, Order... orders);


    /**
     * 根据pojo实例中的非空属性值查询出所有符合条件的数据
     *
     * @param t        pojo实例
     * @param likeMode 是否对字符串属性进行模糊查询，true表示为是，false为否
     * @param orders   排序条件
     * @param <T>      数据类型
     * @return 查询出来的数据
     */
    <T> List<T> findAll(T t, boolean likeMode, Order... orders);


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
    <T> Page<T> findPage(T t, boolean likeMode, Slice slice, Order... orders);

    /**
     * 根据pojo实例中的非空属性值分页查询出所有符合条件的数据
     *
     * @param pageQuery pojo实例查询条件
     * @param likeMode  是否对字符串属性进行模糊查询，true表示为是，false为否
     * @param orders    排序条件
     * @param <T>       数据类型
     * @return 查询出来的数据
     */
    <T> Page<T> findPage(PageQuery<T> pageQuery, boolean likeMode, Order... orders);


    /**
     * 根据主键全属性全量更新方式更新一条数据
     *
     * @param <T> POJO类
     * @param t   待更新的数据
     * @return 受影响的记录的数量
     */
    <T> int updateByPrimaryKey(T t);

    /**
     * 根据主键可选属性增量更新方式更新一条数据
     *
     * @param <T> POJO类
     * @param t   待更新的数据
     * @return 受影响的记录的数量
     */
    <T> int updateByPrimaryKeySelective(T t);


    /**
     * 根据主键删除一条数据
     *
     * @param <T>         POJO类
     * @param clazz       操作的对象
     * @param primaryKeys 主键值
     * @return 受影响的记录的数量
     */
    <T> int deleteByPrimaryKey(Class<T> clazz, Object... primaryKeys);

    /**
     * 以全属性方式新增一条数据
     *
     * @param <T> POJO类
     * @param t   待新增的数据
     * @return 保存数据的主键
     */
    <T> KeyHolder insert(T t);


    /**
     * 根据主键id判断数据是否存在，若存在则先删除存在的数据，然后再插入新的数据
     *
     * @param t   待操作的数据
     * @param <T> POJO类
     * @return 保存数据的主键;只有数据库为自增时才有用，其他情况下无效
     */
    <T> void saveOrUpdate(T t);

    /**
     * 批量保存数据
     *
     * @param list 待批量保存的数据
     * @param <T>  POJO数据类型
     */
    <T> void saveAll(Collection<T> list);


    /**
     * 根据sql查询出所有的数据
     *
     * @param clazz  数据类型
     * @param sql    sql语句
     * @param params 参数
     * @param <T>    POJO类
     * @return 查询出来的数据
     */
    <T> List<T> findAll(Class<T> clazz, String sql, Object... params);


    /**
     * 获取操作的JdbcTemplate实例
     *
     * @return JdbcTemplate实例
     */
    JdbcTemplate jdbcTemplate();
}
