package com.yishuifengxiao.common.jdbc.entity;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * SQL执行对象
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SqlData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5299170094568206693L;

	/**
	 * SQL语句
	 */
	private StringBuilder sql;

	/**
	 * 该SQL语句对应的参数
	 */
	private List<Object> args;

	/**
	 * 获取到查询语句
	 * 
	 * @return
	 */
	public String getSqlString() {
		return null == this.sql ? "" : this.sql.toString();
	}

	/**
	 * 获取最终查询的参数
	 * 
	 * @return
	 */
	public Object[] getSqlArgs() {
		if (null == this.args) {
			return new Object[] {};
		}

		Object[] array = new Object[this.args.size()];

		for (int i = 0; i < this.args.size(); i++) {
			array[i] = this.args.get(i);
		}
		return array;

	}

}
