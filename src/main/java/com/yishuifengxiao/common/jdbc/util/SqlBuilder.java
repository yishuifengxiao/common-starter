package com.yishuifengxiao.common.jdbc.util;

import com.yishuifengxiao.common.jdbc.entity.SqlData;
import com.yishuifengxiao.common.jdbc.sql.ConditionBuilder;
import com.yishuifengxiao.common.jdbc.sql.impl.SimpleConditionBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * SQL动态生成工具
 * </p>
 * <p>
 * 最终生成的sql语句的拼接方式为 sql + [apendSql] + [order] + [limit语句]
 * <p>
 * 
 * 最基本的分页方式的sql语句格式如下
 * 
 * <pre>
 *  {@code
 *  SELECT ... FROM ... WHERE ... ORDER BY ... LIMIT ...
 *  }
 * </pre>
 * 
 * 生成动态SQL语句的工具
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SqlBuilder {

	/**
	 * 查询条件标志
	 */
	private static final String WHERE_FLAG = "where";

	/**
	 * 模糊模糊查询的标志
	 */
	private static final String LIKE_FLAG = "like";
	/**
	 * 查询条件生成器
	 */
	private final ConditionBuilder conditionBuilder = new SimpleConditionBuilder();

	/**
	 * 比较条件
	 */
	private List<Condition> conditions = new ArrayList<>();

	/**
	 * 是否为动态方式,默认为true
	 */
	private boolean selective = true;

	/**
	 * 分页大小
	 */
	private int pageSize;

	/**
	 * 当前页页码
	 */
	private int pageNum;

	/**
	 * 查询语句的主体
	 */
	private String sql;

	/**
	 * 排序条件
	 */
	private String order;

	/**
	 * <p>
	 * 创建一个SQL语句生成工具
	 * </p>
	 * 该部分必须为一个可行的sql语句，例如
	 * 
	 * <pre>
	 * {@code
	 *  select * from user where 1=1
	 * }
	 * </pre>
	 * 
	 * 也可以为
	 * 
	 * <pre>
	 * {@code
	 *  select u.id as id ,u.user_name as name  from user u , app a  where u.id=a.user_id 
	 * }
	 * 
	 * 形式
	 * </pre>
	 * 
	 * 
	 * @param sql sql语句前半部分
	 * @return SQL动态生成工具
	 */
	public static SqlBuilder create(String sql) {
		SqlBuilder sqlUtil = new SqlBuilder();
		sqlUtil.sql = sql;
		return sqlUtil;
	}

	/**
	 * 是否为动态方式,默认为true
	 * 
	 * @param selective true表示值为null或空字符串的条件不参与生成sql
	 * @return SQL动态生成工具
	 */
	public SqlBuilder selective(boolean selective) {
		this.selective = selective;
		return this;
	}

	/**
	 * <p>
	 * 增加一个比较条件
	 * </p>
	 * 
	 * 最终生成的sql语句的顺序与添加的比较条件的顺序有关系
	 * 
	 * 
	 * @param condition 比较条件的属性 例如 and name = ? ,需要携带上联条件
	 * @param value     比较条件的值
	 * @return SQL动态生成工具
	 */
	public SqlBuilder condition(String condition, Object value) {
		this.conditions.add(new Condition(condition, value));
		return this;

	}

	/**
	 * 设置分页大小
	 * 
	 * @param pageSize 分页大小
	 * @return SQL动态生成工具
	 */
	public SqlBuilder pageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	/**
	 * 设置当前页页码
	 * 
	 * @param pageNum 当前页页码
	 * @return SQL动态生成工具
	 */
	public SqlBuilder pageNum(int pageNum) {
		this.pageNum = pageNum;
		return this;
	}

	/**
	 * 设置排除条件
	 * 
	 * @param order 排序条件
	 * @return SQL动态生成工具
	 */
	public SqlBuilder order(String order) {
		this.order = order;
		return this;
	}

	/**
	 * 获取所有过滤后的比较条件
	 * 
	 * @return 过滤后的比较条件
	 */
	private List<Condition> conditions() {
		return this.conditions.stream().filter(Objects::nonNull).filter(t -> StringUtils.isNotBlank(t.getCondition()))
				.filter(t -> {
					if (this.selective) {
						if (null == t.getValue()) {
							return false;
						}
						return StringUtils.isNotBlank(t.getValue().toString());
					}
					return true;
				}).collect(Collectors.toList());
	}

	/**
	 * 解析比较条件
	 * 
	 * @return 解析后的比较条件
	 */
	private SqlData build() {
		StringBuilder sql = new StringBuilder();
		List<Object> list = new ArrayList<>();
		for (Condition condition : this.conditions()) {

			if (StringUtils.containsIgnoreCase(condition.getCondition(), LIKE_FLAG)) {
				// 模糊查询
				sql.append(" ").append(StringUtils.replace(condition.getCondition(), "?",
						null == condition.getValue() ? null : condition.getValue().toString().trim())).append(" ");
			} else {

				sql.append(" ").append(condition.getCondition()).append(" ");
				list.add(condition.getValue());

			}

		}
		return new SqlData(sql, list);
	}

	/**
	 * 完成基础语句解析
	 * 
	 * @param apendSql 需要追加的sql语句
	 * @param limit    是否生成分页语句
	 * @return 生成的sql语句
	 */
	private String sql(String apendSql, boolean limit) {

		StringBuilder sql = new StringBuilder(this.sql).append(" ");

		if (!StringUtils.containsIgnoreCase(this.sql, WHERE_FLAG)) {
			sql.append("where 1=1 ");
		}

		sql.append(this.build().getSql());

		if (StringUtils.isNotBlank(apendSql)) {
			sql.append(" ").append(apendSql).append(" ");
		}

		if (StringUtils.isNotBlank(this.order)) {
			sql.append(" ").append(this.order).append(" ");
		}

		if (limit) {
			sql = sql.append(conditionBuilder.createLimit(this.pageSize, this.pageNum));
		}

		log.trace("【yishuifengxiao-common-spring-boot-starter】生成的sql语句为 {} ", sql);
		return sql.toString();

	}

	/**
	 * 提取出查询参数
	 * 
	 * @return 查询参数
	 */
	public Object[] getArgs() {
		List<Object> args = this.build().getArgs();

		if (null == args) {
			return new Object[] {};
		}

		Object[] array = new Object[args.size()];

		for (int i = 0; i < args.size(); i++) {
			array[i] = args.get(i);
		}
		return array;
	}

	/**
	 * 构造一个查询所有结果的查询语句
	 * 
	 * @return 查询所有结果的查询语句
	 */
	public String sql() {
		return this.sql(null);
	}

	/**
	 * 构造一个查询所有结果的查询语句
	 * 
	 * @param apendSql 最后追加的SQL
	 * @return 查询所有结果的查询语句
	 */
	public String sql(String apendSql) {

		return this.sql(apendSql, false);
	}

	/**
	 * 构造一个分页查询语句
	 * 
	 * @return 分页查询语句
	 */
	public String pageSql() {
		return this.pageSql(null);
	}

	/**
	 * 构造一个分页查询语句
	 * 
	 * @param apendSql 最后追加的SQL
	 * @return 分页查询语句
	 */
	public String pageSql(String apendSql) {
		return this.sql(apendSql, true);
	}

	/**
	 * 构造一个统计查询语句
	 * 
	 * @return 统计查询语句
	 */
	public String countSql() {
		String fragment = StringUtils.substringAfter(this.sql, "from");
		StringBuilder sql = new StringBuilder("select count(*) from ").append(fragment).append(this.build().getSql());
		log.trace("【yishuifengxiao-common-spring-boot-starter】生成的sql语句为 {} ", sql);
		return sql.toString();
	}

	/**
	 * 构造函数
	 */
	private SqlBuilder() {

	}

	/**
	 * 比较条件
	 * 
	 * @author qingteng
	 * @version 1.0.0
	 */
	private static class Condition {
		private String condition;
		private Object value;

		public String getCondition() {
			return condition;
		}

		public Object getValue() {
			return value;
		}

		public Condition(String condition, Object value) {
			this.condition = condition;
			this.value = value;
		}

	}

}
