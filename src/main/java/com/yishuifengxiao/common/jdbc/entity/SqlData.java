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
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
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
	 * @return 查询语句
	 */
	public String getSqlString() {
		return null == this.sql ? "" : this.sql.toString();
	}

}
