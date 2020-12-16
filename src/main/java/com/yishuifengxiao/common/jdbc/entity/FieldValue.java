package com.yishuifengxiao.common.jdbc.entity;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * POJO类属性提取对象
 * 
 * @author qingteng
 * @date 2020年12月5日
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FieldValue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3388630521659133444L;

	/**
	 * POJO类中属性的名字
	 */
	private String name;

	/**
	 * POJO类中的属性在数据库中对应的名字
	 */
	private String colName;

	/**
	 * POJO类中的属性在数据库中对应的属性的名字<br/>
	 * 获取方法如下: 如果colName的值为null就使用name的值
	 * 
	 * @return POJO类中的属性在数据库中对应的属性的名字
	 */
	public String getSimpleName() {
		if (StringUtils.isNotBlank(this.colName)) {
			return this.colName;
		}
		return this.name;

	}

}
