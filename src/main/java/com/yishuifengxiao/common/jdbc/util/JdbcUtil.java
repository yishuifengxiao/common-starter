package com.yishuifengxiao.common.jdbc.util;

import java.util.List;

import com.yishuifengxiao.common.jdbc.JdbcHelper;
import com.yishuifengxiao.common.jdbc.entity.Condition;
import com.yishuifengxiao.common.jdbc.entity.Example;
import com.yishuifengxiao.common.jdbc.entity.Order;
import com.yishuifengxiao.common.tool.entity.Page;

/**
 * JdbcTemplate操作器工具<br/>
 * 
 * 【注意】在没有特意指出的前提下，所有筛选条件的笔记方式为完全匹配
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
public class JdbcUtil {
	private static JdbcHelper jdbcHelper;

	public JdbcUtil(JdbcHelper jdbcHelper) {
		JdbcUtil.jdbcHelper = jdbcHelper;
	}

	/**
	 * 根据主键从指定表查询一条数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param primaryKey 主键
	 * @return 查询到的数据
	 */
	public static <T> T findByPrimaryKey(Class<T> clazz, Object primaryKey) {
		return jdbcHelper.findByPrimaryKey(clazz, primaryKey);
	}

	/**
	 * 查询符合条件的记录的数量
	 * 
	 * @param <T> POJO类
	 * @param t   查询条件
	 * @return 符合条件的记录的数量
	 */
	public static <T> Long countAll(T t) {
		return jdbcHelper.countAll(t);
	}

	/**
	 * 查询符合条件的记录的数量
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param conditions 筛选条件
	 * @return 符合条件的记录的数量
	 */
	public static <T> Long countAll(Class<T> clazz, Condition... conditions) {
		return jdbcHelper.countAll(clazz, conditions);
	}

	/**
	 * 查询符合条件的记录的数量
	 * 
	 * @param <T>     POJO类
	 * @param clazz   POJO类
	 * @param example 筛选条件
	 * @return 符合条件的记录的数量
	 */
	public static <T> Long countAll(Class<T> clazz, Example example) {
		return jdbcHelper.countAll(clazz, example);
	}

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T> POJO类
	 * @param t   查询条件
	 * @return 符合条件的数据
	 */
	public static <T> T findOne(T t) {
		return jdbcHelper.findOne(t);
	}

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param conditions 筛选条件
	 * @return 符合条件的数据
	 */
	public static <T> T findOne(Class<T> clazz, Condition... conditions) {
		return jdbcHelper.findOne(clazz, conditions);
	}

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>     POJO类
	 * @param clazz   POJO类
	 * @param example 筛选条件
	 * @return 符合条件的数据
	 */
	public static <T> T findOne(Class<T> clazz, Example example) {
		return jdbcHelper.findOne(clazz, example);
	}

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param conditions 筛选条件
	 * @return 符合条件的数据
	 */
	public static <T> T findOne(Class<T> clazz, List<Condition> conditions) {
		return jdbcHelper.findOne(clazz, conditions);
	}

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T> POJO类
	 * @param t   查询条件
	 * @return 符合条件的数据
	 */
	public static <T> List<T> findAll(T t) {
		return jdbcHelper.findAll(t);
	}

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>   POJO类
	 * @param t     查询条件
	 * @param order 排序条件
	 * @return 符合条件的数据
	 */
	public static <T> List<T> findAll(T t, Order order) {
		return jdbcHelper.findAll(t, order);
	}

	/**
	 * 查询所有符合条件的数据（默认升序）
	 * 
	 * @param <T>       POJO类
	 * @param t         查询条件
	 * @param orderName 排序字段，必须为对应的POJO属性的名字
	 * @return 符合条件的数据
	 */
	public static <T> List<T> findAll(T t, String orderName) {
		return jdbcHelper.findAll(t, orderName);
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
	public static <T> List<T> findAll(T t, String orderName, Order.Direction direction) {
		return jdbcHelper.findAll(t, orderName, direction);
	}

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param conditions 筛选条件
	 * @return 符合条件的数据
	 */
	public static <T> List<T> findAll(Class<T> clazz, Condition... conditions) {
		return jdbcHelper.findAll(clazz, conditions);
	}

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>     POJO类
	 * @param clazz   POJO类
	 * @param example 筛选条件
	 * @return 符合条件的数据
	 */
	public static <T> List<T> findAll(Class<T> clazz, Example example) {
		return jdbcHelper.findAll(clazz, example);
	}

	/**
	 * 查询所有符合条件的数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param conditions 筛选条件
	 * @return 符合条件的数据
	 */
	public static <T> List<T> findAll(Class<T> clazz, List<Condition> conditions) {
		return jdbcHelper.findAll(clazz, conditions);
	}

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
	public static <T> List<T> findAll(Class<T> clazz, Order order, Condition... conditions) {
		return jdbcHelper.findAll(clazz, order, conditions);
	}

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
	public static <T> List<T> findAll(Class<T> clazz, Order order, Example example) {
		return jdbcHelper.findAll(clazz, order, example);
	}

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
	public static <T> List<T> findAll(Class<T> clazz, Order order, List<Condition> conditions) {
		return jdbcHelper.findAll(clazz, order, conditions);
	}

	/**
	 * 根据条件查询前几条符合条件的记录
	 * 
	 * @param <T>   POJO类
	 * @param t     查询条件
	 * @param order 排序条件
	 * @topNum 查询出的记录的数量
	 * @return 符合条件的数据
	 */
	public static <T> List<T> findTop(T t, Order order, Integer topNum) {
		return jdbcHelper.findTop(t, order, topNum);
	}

	/**
	 * 根据条件查询前几条符合条件的记录
	 * 
	 * @param <T>   POJO类
	 * @param clazz POJO类
	 * @param order 排序条件
	 * @topNum 查询出的记录的数量
	 * @param example 筛选条件
	 * 
	 * @return 符合条件的数据
	 */
	public static <T> List<T> findTop(Class<T> clazz, Order order, Integer topNum, Example example) {
		return jdbcHelper.findTop(clazz, order, topNum, example);
	}

	/**
	 * 根据条件查询前几条符合条件的记录
	 * 
	 * @param <T>   POJO类
	 * @param clazz POJO类
	 * @param order 排序条件
	 * @topNum 查询出的记录的数量
	 * @param conditions 筛选条件
	 * 
	 * @return 符合条件的数据
	 */
	public static <T> List<T> findTop(Class<T> clazz, Order order, Integer topNum, Condition... conditions) {
		return jdbcHelper.findTop(clazz, order, topNum, conditions);
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
	public static <T> Page<T> findPage(T t, Integer pageSize, Integer pageNum) {
		return jdbcHelper.findPage(t, pageSize, pageNum);
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
	public static <T> Page<T> findPage(T t, Integer pageSize, Integer pageNum, Order order) {
		return jdbcHelper.findPage(t, pageSize, pageNum, order);
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
	public static <T> Page<T> findPage(T t, Integer pageSize, Integer pageNum, String orderName) {
		return jdbcHelper.findPage(t, pageSize, pageNum, orderName);
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
	public static <T> Page<T> findPage(T t, Integer pageSize, Integer pageNum, String orderName,
			Order.Direction direction) {
		return jdbcHelper.findPage(t, pageSize, pageNum, orderName, direction);
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
	public static <T> Page<T> findPage(Class<T> clazz, Integer pageSize, Integer pageNum, Condition... conditions) {
		return jdbcHelper.findPage(clazz, pageSize, pageNum, conditions);
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
	public static <T> Page<T> findPage(Class<T> clazz, Integer pageSize, Integer pageNum, Example example) {
		return jdbcHelper.findPage(clazz, pageSize, pageNum, example);
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
	public static <T> Page<T> findPage(Class<T> clazz, Integer pageSize, Integer pageNum, List<Condition> conditions) {
		return jdbcHelper.findPage(clazz, pageSize, pageNum, conditions);
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

	public static <T> Page<T> findPage(Class<T> clazz, Integer pageSize, Integer pageNum, Order order,
			Condition... conditions) {
		return jdbcHelper.findPage(clazz, pageSize, pageNum, order, conditions);
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
	public static <T> Page<T> findPage(Class<T> clazz, Integer pageSize, Integer pageNum, Order order,
			Example example) {
		return jdbcHelper.findPage(clazz, pageSize, pageNum, order, example);
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
	public static <T> Page<T> findPage(Class<T> clazz, Integer pageSize, Integer pageNum, Order order,
			List<Condition> conditions) {
		return jdbcHelper.findPage(clazz, pageSize, pageNum, order, conditions);
	}

	/**
	 * 根据主键全属性更新方式更新一条数据
	 * 
	 * @param <T> POJO类
	 * @param t   待更新的数据
	 * @return 受影响的记录的数量
	 */
	public static <T> int updateByPrimaryKey(T t) {
		return jdbcHelper.updateByPrimaryKey(t);
	}

	/**
	 * 根据主键可选属性更新方式更新一条数据
	 * 
	 * @param <T> POJO类
	 * @param t   待更新的数据
	 * @return 受影响的记录的数量
	 */
	public static <T> int updateByPrimaryKeySelective(T t) {
		return jdbcHelper.updateByPrimaryKeySelective(t);
	}

	/**
	 * 根据条件全属性更新方式批量更新数据
	 * 
	 * @param <T>        POJO类
	 * @param t          待更新的数据
	 * @param conditions 筛选条件
	 * @return 受影响的记录的数量
	 */
	public static <T> int update(T t, Condition... conditions) {
		return jdbcHelper.update(t, conditions);
	}

	/**
	 * 根据条件全属性更新方式批量更新数据
	 * 
	 * @param <T>     POJO类
	 * @param t       待更新的数据
	 * @param example 筛选条件
	 * @return 受影响的记录的数量
	 */
	public static <T> int update(T t, Example example) {
		return jdbcHelper.update(t, example);
	}

	/**
	 * 根据条件全属性更新方式批量更新数据
	 * 
	 * @param <T>        POJO类
	 * @param t          待更新的数据
	 * @param conditions 筛选条件
	 * @return 受影响的记录的数量
	 */
	public static <T> int update(T t, List<Condition> conditions) {
		return jdbcHelper.update(t, conditions);
	}

	/**
	 * 根据条件全属性更新方式批量更新数据
	 * 
	 * @param <T>       POJO类
	 * @param t         待更新的数据
	 * @param condition 更新条件
	 * @return 受影响的记录的数量
	 */
	public static <T> int update(T t, T condition) {
		return jdbcHelper.update(t, condition);
	}

	/**
	 * 根据条件可选属性更新方式批量更新数据
	 * 
	 * @param <T>       POJO类
	 * @param t         待更新的数据
	 * @param condition 更新条件
	 * @return 受影响的记录的数量
	 */
	public static <T> int updateSelective(T t, T condition) {
		return jdbcHelper.updateSelective(t, condition);
	}

	/**
	 * 根据条件可选属性更新方式批量更新数据
	 * 
	 * @param <T>        POJO类
	 * @param t          待更新的数据
	 * @param conditions 筛选条件
	 * @return 受影响的记录的数量
	 */
	public static <T> int updateSelective(T t, Condition... conditions) {
		return jdbcHelper.updateSelective(t, conditions);
	}

	/**
	 * 根据条件可选属性更新方式批量更新数据
	 * 
	 * @param <T>     POJO类
	 * @param t       待更新的数据
	 * @param example 筛选条件
	 * @return 受影响的记录的数量
	 */
	public static <T> int updateSelective(T t, Example example) {
		return jdbcHelper.updateSelective(t, example);
	}

	/**
	 * 根据条件可选属性更新方式批量更新数据
	 * 
	 * @param <T>        POJO类
	 * @param t          待更新的数据
	 * @param conditions 筛选条件
	 * @return 受影响的记录的数量
	 */
	public static <T> int updateSelective(T t, List<Condition> conditions) {
		return jdbcHelper.updateSelective(t, conditions);
	}

	/**
	 * 根据主键删除一条数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      操作的对象
	 * @param primaryKey 主键值
	 * @return 受影响的记录的数量
	 */
	public static <T> int deleteByPrimaryKey(Class<T> clazz, Object primaryKey) {
		return jdbcHelper.deleteByPrimaryKey(clazz, primaryKey);
	}

	/**
	 * 根据条件批量删除数据
	 * 
	 * @param <T> POJO类
	 * @param t   删除条件
	 * @return 受影响的记录的数量
	 */
	public static <T> int delete(T t) {
		return jdbcHelper.delete(t);
	}

	/**
	 * 根据条件批量删除数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param conditions 筛选条件
	 * @return 受影响的记录的数量
	 */
	public static <T> int delete(Class<T> clazz, Condition... conditions) {
		return jdbcHelper.delete(clazz, conditions);
	}

	/**
	 * 根据条件批量删除数据
	 * 
	 * @param <T>     POJO类
	 * @param clazz   POJO类
	 * @param example 筛选条件
	 * @return 受影响的记录的数量
	 */
	public static <T> int delete(Class<T> clazz, Example example) {
		return jdbcHelper.delete(clazz, example);
	}

	/**
	 * 根据条件批量删除数据
	 * 
	 * @param <T>        POJO类
	 * @param clazz      POJO类
	 * @param conditions 筛选条件
	 * @return 受影响的记录的数量
	 */
	public static <T> int delete(Class<T> clazz, List<Condition> conditions) {
		return jdbcHelper.delete(clazz, conditions);
	}

	/**
	 * 以全属性方式新增一条数据
	 * 
	 * @param <T> POJO类
	 * @param t   待新增的数据
	 * @return 受影响的记录的数量
	 */
	public static <T> int insert(T t) {
		return jdbcHelper.insert(t);
	}

	/**
	 * 以可选属性方式新增一条数据
	 * 
	 * @param <T> POJO类
	 * @param t   待新增的数据
	 * @return 受影响的记录的数量
	 */
	public static <T> int insertSelective(T t) {
		return jdbcHelper.insertSelective(t);
	}

}
