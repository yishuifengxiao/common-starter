/**
 * 
 */
package com.yishuifengxiao.common.jdbc;

import java.util.List;

import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.Example;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.tool.entity.Page;

/**
 * <p>
 * JdbcTemplate操作器
 * </p>
 * 
 * 【注意】在没有特意指出的前提下，所有筛选条件的笔记方式为完全匹配
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface JdbcHelper {

	/**
	 * 根据主键从指定表查询一条数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param primaryKey 主键
	 * @return 查询到的数据
	 */
	<T> T findByPrimaryKey(Class<T> clazz, Object primaryKey);

	/**
	 * 查询符合条件的记录的数量
	 * 
	 * @param <T> POJO类
	 * @param t   查询条件
	 * @return 符合条件的记录的数量
	 */
	<T> Long countAll(T t);

	/**
	 * 查询符合条件的记录的数量
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param conditions 筛选条件
	 * @return 符合条件的记录的数量
	 */
	<T> Long countAll(Class<T> clazz, Condition... conditions);

	/**
	 * 查询符合条件的记录的数量
	 * 
	 * @param <T>     POJO类
	 * @param clazz   POJO类
	 * @param example 筛选条件
	 * @return 符合条件的记录的数量
	 */
	<T> Long countAll(Class<T> clazz, Example example);

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T> POJO类
	 * @param t   查询条件
	 * @return 符合条件的数据
	 */
	<T> T findOne(T t);

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param conditions 筛选条件
	 * @return 符合条件的数据
	 */
	<T> T findOne(Class<T> clazz, Condition... conditions);

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>     POJO类
	 * @param clazz   POJO类
	 * @param example 筛选条件
	 * @return 符合条件的数据
	 */
	<T> T findOne(Class<T> clazz, Example example);

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param conditions 筛选条件
	 * @return 符合条件的数据
	 */
	<T> T findOne(Class<T> clazz, List<Condition> conditions);

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T> POJO类
	 * @param t   查询条件
	 * @return 符合条件的数据
	 */
	<T> List<T> findAll(T t);

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>   POJO类
	 * @param t     查询条件
	 * @param order 排序条件
	 * @return 符合条件的数据
	 */
	<T> List<T> findAll(T t, Order order);

	/**
	 * 查询所有符合条件的数据（默认升序）
	 * 
	 * @param <T>       POJO类
	 * @param t         查询条件
	 * @param orderName 排序字段，必须为对应的POJO属性的名字
	 * @return 符合条件的数据
	 */
	<T> List<T> findAll(T t, String orderName);

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>       POJO类
	 * @param t         查询条件
	 * @param orderName 排序字段，必须为对应的POJO属性的名字
	 * @param direction 排序方向
	 * @return 符合条件的数据
	 */
	<T> List<T> findAll(T t, String orderName, Order.Direction direction);

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param conditions 筛选条件
	 * @return 符合条件的数据
	 */
	<T> List<T> findAll(Class<T> clazz, Condition... conditions);

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>     POJO类
	 * @param clazz   POJO类
	 * @param example 筛选条件
	 * @return 符合条件的数据
	 */
	<T> List<T> findAll(Class<T> clazz, Example example);

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param conditions 筛选条件
	 * @return 符合条件的数据
	 */
	<T> List<T> findAll(Class<T> clazz, List<Condition> conditions);

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param order      排序条件
	 * @param conditions 筛选条件
	 * 
	 * @return 符合条件的数据
	 */
	<T> List<T> findAll(Class<T> clazz, Order order, Condition... conditions);

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>     POJO类
	 * @param clazz   POJO类
	 * @param order   排序条件
	 * @param example 筛选条件
	 * 
	 * @return 符合条件的数据
	 */
	<T> List<T> findAll(Class<T> clazz, Order order, Example example);

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param order      排序条件
	 * @param conditions 筛选条件
	 * 
	 * @return 符合条件的数据
	 */
	<T> List<T> findAll(Class<T> clazz, Order order, List<Condition> conditions);

	/**
	 * 根据条件查询前几条符合条件的记录
	 * 
	 * @param <T>    POJO类
	 * @param t      查询条件
	 * @param order  排序条件
	 * @param topNum 查询出的记录的数量
	 * @return 符合条件的数据
	 */
	<T> List<T> findTop(T t, Order order, int topNum);

	/**
	 * 根据条件查询前几条符合条件的记录
	 * 
	 * @param <T>     POJO类
	 * @param clazz   POJO类
	 * @param order   排序条件
	 * @param topNum  查询出的记录的数量
	 * @param example 筛选条件
	 * 
	 * @return 符合条件的数据
	 */
	<T> List<T> findTop(Class<T> clazz, Order order, int topNum, Example example);

	/**
	 * 根据条件查询前几条符合条件的记录
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param order      排序条件
	 * @param topNum     查询出的记录的数量
	 * @param conditions 筛选条件
	 * 
	 * @return 符合条件的数据
	 */
	<T> List<T> findTop(Class<T> clazz, Order order, int topNum, Condition... conditions);

	/**
	 * 分页查询所有符合条件的数据
	 * 
	 * @param <T>      POJO类
	 * @param t        查询条件
	 * @param pageSize 分页大小
	 * @param pageNum  当前页页码
	 * @return 符合条件的数据
	 */
	<T> Page<T> findPage(T t, int pageSize, int pageNum);

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
	<T> Page<T> findPage(T t, int pageSize, int pageNum, Order order);

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
	<T> Page<T> findPage(T t, int pageSize, int pageNum, String orderName);

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
	<T> Page<T> findPage(T t, int pageSize, int pageNum, String orderName, Order.Direction direction);

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
	<T> Page<T> findPage(Class<T> clazz, int pageSize, int pageNum, Condition... conditions);

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
	<T> Page<T> findPage(Class<T> clazz, int pageSize, int pageNum, Example example);

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
	<T> Page<T> findPage(Class<T> clazz, int pageSize, int pageNum, List<Condition> conditions);

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
	<T> Page<T> findPage(Class<T> clazz, int pageSize, int pageNum, Order order, Condition... conditions);

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
	<T> Page<T> findPage(Class<T> clazz, int pageSize, int pageNum, Order order, Example example);

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
	<T> Page<T> findPage(Class<T> clazz, int pageSize, int pageNum, Order order, List<Condition> conditions);

	/**
	 * 根据主键全属性更新方式更新一条数据
	 * 
	 * @param <T> POJO类
	 * @param t   待更新的数据
	 * @return 受影响的记录的数量
	 */
	<T> int updateByPrimaryKey(T t);

	/**
	 * 根据主键可选属性更新方式更新一条数据
	 * 
	 * @param <T> POJO类
	 * @param t   待更新的数据
	 * @return 受影响的记录的数量
	 */
	<T> int updateByPrimaryKeySelective(T t);

	/**
	 * 根据条件全属性更新方式批量更新数据
	 * 
	 * @param <T>     POJO类
	 * @param t       待更新的数据
	 * @param example 筛选条件
	 * @return 受影响的记录的数量
	 */
	<T> int update(T t, Example example);

	/**
	 * 根据条件全属性更新方式批量更新数据
	 * 
	 * @param <T>        POJO类
	 * @param t          待更新的数据
	 * @param conditions 筛选条件
	 * @return 受影响的记录的数量
	 */
	<T> int update(T t, List<Condition> conditions);

	/**
	 * 根据条件全属性更新方式批量更新数据
	 * 
	 * @param <T>       POJO类
	 * @param t         待更新的数据
	 * @param condition 更新条件
	 * @return 受影响的记录的数量
	 */
	<T> int update(T t, T condition);

	/**
	 * 根据条件可选属性更新方式批量更新数据
	 * 
	 * @param <T>       POJO类
	 * @param t         待更新的数据
	 * @param condition 更新条件
	 * @return 受影响的记录的数量
	 */
	<T> int updateSelective(T t, T condition);

	/**
	 * 根据条件可选属性更新方式批量更新数据
	 * 
	 * @param <T>     POJO类
	 * @param t       待更新的数据
	 * @param example 筛选条件
	 * @return 受影响的记录的数量
	 */
	<T> int updateSelective(T t, Example example);

	/**
	 * 根据条件可选属性更新方式批量更新数据
	 * 
	 * @param <T>        POJO类
	 * @param t          待更新的数据
	 * @param conditions 筛选条件
	 * @return 受影响的记录的数量
	 */
	<T> int updateSelective(T t, List<Condition> conditions);

	/**
	 * 根据主键删除一条数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      操作的对象
	 * @param primaryKey 主键值
	 * @return 受影响的记录的数量
	 */
	<T> int deleteByPrimaryKey(Class<T> clazz, Object primaryKey);

	/**
	 * 根据条件批量删除数据
	 * 
	 * @param <T> POJO类
	 * @param t   删除条件
	 * @return 受影响的记录的数量
	 */
	<T> int delete(T t);

	/**
	 * 根据条件批量删除数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param conditions 筛选条件
	 * @return 受影响的记录的数量
	 */
	<T> int delete(Class<T> clazz, Condition... conditions);

	/**
	 * 根据条件批量删除数据
	 * 
	 * @param <T>     POJO类
	 * @param clazz   POJO类
	 * @param example 筛选条件
	 * @return 受影响的记录的数量
	 */
	<T> int delete(Class<T> clazz, Example example);

	/**
	 * 根据条件批量删除数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param conditions 筛选条件
	 * @return 受影响的记录的数量
	 */
	<T> int delete(Class<T> clazz, List<Condition> conditions);

	/**
	 * 以全属性方式新增一条数据
	 * 
	 * @param <T> POJO类
	 * @param t   待新增的数据
	 * @return 受影响的记录的数量
	 */
	<T> int insert(T t);

	/**
	 * 以可选属性方式新增一条数据
	 * 
	 * @param <T> POJO类
	 * @param t   待新增的数据
	 * @return 受影响的记录的数量
	 */
	<T> int insertSelective(T t);
}
