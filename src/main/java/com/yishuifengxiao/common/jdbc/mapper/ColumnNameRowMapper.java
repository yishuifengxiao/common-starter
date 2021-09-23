/**
 * 
 */
package com.yishuifengxiao.common.jdbc.mapper;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.extractor.SimpleFieldExtractor;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 将ResultSet转换成javabean
 * </p>
 * ResultSet中的列名为数据库里的列名一致,会自动完成数据库列名到javabean field的映射转换
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 * @param <T> 希望转换的javabean的数据类型
 */
@Slf4j
public class ColumnNameRowMapper<T> implements RowMapper<T> {

	private FieldExtractor fieldExtractor = new SimpleFieldExtractor();

	private Class<T> clazz = null;

	private List<FieldValue> fieldValues = null;

	public ColumnNameRowMapper(Class<T> clazz) {
		Assert.notNull(clazz, "【易水组件】转换目标对象不能为空");
		this.clazz = clazz;
		init();
	}

	/**
	 * 初始化操作
	 */
	private void init() {
		this.fieldValues = this.fieldExtractor.extractFiled(this.clazz);
	}

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		try {
			T instance = BeanUtils.instantiateClass(this.clazz);
			for (FieldValue fieldValue : fieldValues) {
				Object value = rs.getObject(fieldValue.getColName(), fieldValue.getType());
				if (null == value) {
					continue;
				}
				String setter = this.getSetter(fieldValue.getName());
				Method method = this.clazz.getMethod(setter, fieldValue.getType());
				method.setAccessible(true);
				method.invoke(instance, value);
			}
			return instance;
		} catch (Exception e) {
			log.warn("【易水组件】将 ResultSet转换成javabean时出现问题，出现问题的原因为 {}", e.getMessage());
		}
		return null;
	}

	/**
	 * 根据属性名字获取对应的set方法的名字
	 * 
	 * @param filedName 属性名字
	 * @return 属性名字获取对应的set方法的名字
	 */
	private String getSetter(String filedName) {
		String firstWord = StringUtils.substring(filedName, 0, 1);
		return new StringBuffer("set").append(firstWord.toUpperCase()).append(StringUtils.substring(filedName, 1))
				.toString();
	}

}
