/**
 *
 */
package com.yishuifengxiao.common.jdbc.mapper;

import com.yishuifengxiao.common.jdbc.entity.FieldValue;
import com.yishuifengxiao.common.jdbc.extractor.FieldExtractor;
import com.yishuifengxiao.common.jdbc.extractor.SimpleFieldExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 将ResultSet转换成javabean
 * </p>
 * ResultSet中的列名为数据库里的列名一致,会自动完成数据库列名到javabean field的映射转换
 *
 * @param <T> 希望转换的javabean的数据类型
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class ColumnNameRowMapper<T> implements RowMapper<T> {


    private final FieldExtractor fieldExtractor = new SimpleFieldExtractor();

    /**
     * The class we are mapping to.
     */
    @Nullable
    private Class<T> mappedClass;

    /**
     * Whether we're strictly validating.
     */
    private boolean checkFullyPopulated = false;

    /**
     * Whether {@code NULL} database values should be ignored for primitive
     * properties in the target class.
     *
     * @see #setPrimitivesDefaultedForNullValue(boolean)
     */
    private boolean primitivesDefaultedForNullValue = false;

    /**
     * ConversionService for binding JDBC values to bean properties.
     */
    @Nullable
    private ConversionService conversionService = DefaultConversionService.getSharedInstance();

    /**
     * Map of the properties we provide mapping for.
     */
    @Nullable
    private Map<String, PropertyDescriptor> mappedProperties;

    /**
     * Set of bean property names we provide mapping for.
     */
    @Nullable
    private Set<String> mappedPropertyNames;


    /**
     * Create a new {@code ColumnNameRowMapper} for bean-style configuration.
     *
     * @see #setMappedClass
     * @see #setCheckFullyPopulated
     */
    public ColumnNameRowMapper() {
    }

    /**
     * Create a new {@code ColumnNameRowMapper}, accepting unpopulated
     * properties in the target bean.
     *
     * @param mappedClass the class that each row should be mapped to
     */
    public ColumnNameRowMapper(Class<T> mappedClass) {
        initialize(mappedClass);
    }

    /**
     * Create a new {@code ColumnNameRowMapper}.
     *
     * @param mappedClass         the class that each row should be mapped to
     * @param checkFullyPopulated whether we're strictly validating that
     *                            all bean properties have been mapped from corresponding database columns
     */
    public ColumnNameRowMapper(Class<T> mappedClass, boolean checkFullyPopulated) {
        initialize(mappedClass);
        this.checkFullyPopulated = checkFullyPopulated;
    }


    /**
     * Set the class that each row should be mapped to.
     */
    public void setMappedClass(Class<T> mappedClass) {
        if (this.mappedClass == null) {
            initialize(mappedClass);
        } else {
            if (this.mappedClass != mappedClass) {
                throw new InvalidDataAccessApiUsageException("The mapped class can not be reassigned to map to " + mappedClass + " since it is already providing mapping for " + this.mappedClass);
            }
        }
    }

    /**
     * Get the class that we are mapping to.
     */
    @Nullable
    public final Class<T> getMappedClass() {
        return this.mappedClass;
    }

    /**
     * Set whether we're strictly validating that all bean properties have been mapped
     * from corresponding database columns.
     * <p>Default is {@code false}, accepting unpopulated properties in the target bean.
     */
    public void setCheckFullyPopulated(boolean checkFullyPopulated) {
        this.checkFullyPopulated = checkFullyPopulated;
    }

    /**
     * Return whether we're strictly validating that all bean properties have been
     * mapped from corresponding database columns.
     */
    public boolean isCheckFullyPopulated() {
        return this.checkFullyPopulated;
    }

    /**
     * Set whether a {@code NULL} database column value should be ignored when
     * mapping to a corresponding primitive property in the target class.
     * <p>Default is {@code false}, throwing an exception when nulls are mapped
     * to Java primitives.
     * <p>If this flag is set to {@code true} and you use an <em>ignored</em>
     * primitive property value from the mapped bean to update the database, the
     * value in the database will be changed from {@code NULL} to the current value
     * of that primitive property. That value may be the property's initial value
     * (potentially Java's default value for the respective primitive type), or
     * it may be some other value set for the property in the default constructor
     * (or initialization block) or as a side effect of setting some other property
     * in the mapped bean.
     */
    public void setPrimitivesDefaultedForNullValue(boolean primitivesDefaultedForNullValue) {
        this.primitivesDefaultedForNullValue = primitivesDefaultedForNullValue;
    }

    /**
     * Get the value of the {@code primitivesDefaultedForNullValue} flag.
     *
     * @see #setPrimitivesDefaultedForNullValue(boolean)
     */
    public boolean isPrimitivesDefaultedForNullValue() {
        return this.primitivesDefaultedForNullValue;
    }

    /**
     * Set a {@link ConversionService} for binding JDBC values to bean properties,
     * or {@code null} for none.
     * <p>Default is a {@link DefaultConversionService}, as of Spring 4.3. This
     * provides support for {@code java.time} conversion and other special types.
     *
     * @see #initBeanWrapper(BeanWrapper)
     * @since 4.3
     */
    public void setConversionService(@Nullable ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * Return a {@link ConversionService} for binding JDBC values to bean properties,
     * or {@code null} if none.
     *
     * @since 4.3
     */
    @Nullable
    public ConversionService getConversionService() {
        return this.conversionService;
    }


    /**
     * Initialize the mapping meta-data for the given class.
     *
     * @param mappedClass the mapped class
     */
    protected void initialize(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        this.mappedProperties = new HashMap<>();
        this.mappedPropertyNames = new HashSet<>();
        List<FieldValue> fieldValues = fieldExtractor.extractFiled(mappedClass);

        for (PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(mappedClass)) {
            if (pd.getWriteMethod() != null) {

                //将给定的名称转换为小写。默认情况下，转换将在美国语言环境中进行。
                String lowerCaseName = lowerCaseName(pd.getName());
                this.mappedProperties.put(lowerCaseName, pd);

                //将camelCase中的名称转换为小写带下划线的名称。任何大写字母都将转换为带前下划线的小写字母
                String underscoreName = underscoreName(pd.getName());
                if (!lowerCaseName.equals(underscoreName)) {
                    this.mappedProperties.put(underscoreName, pd);
                }
                String colName = fieldValues.stream().filter(v -> org.apache.commons.lang3.StringUtils.equals(pd.getName(), v.getName())).findFirst().map(FieldValue::getColName).orElse(null);
                if (org.apache.commons.lang3.StringUtils.isNoneEmpty(colName)) {
                    this.mappedProperties.put(colName, pd);
                }

                this.mappedPropertyNames.add(pd.getName());
            }
        }
    }

    /**
     * Remove the specified property from the mapped properties.
     *
     * @param propertyName the property name (as used by property descriptors)
     * @since 5.3.9
     */
    protected void suppressProperty(String propertyName) {
        if (this.mappedProperties != null) {
            this.mappedProperties.remove(lowerCaseName(propertyName));
            this.mappedProperties.remove(underscoreName(propertyName));
        }
    }

    /**
     * Convert the given name to lower case.
     * By default, conversions will happen within the US locale.
     *
     * @param name the original name
     * @return the converted name
     * @since 4.2
     */
    protected String lowerCaseName(String name) {
        return name.toLowerCase(Locale.US);
    }

    /**
     * Convert a name in camelCase to an underscored name in lower case.
     * Any upper case letters are converted to lower case with a preceding underscore.
     *
     * @param name the original name
     * @return the converted name
     * @see #lowerCaseName
     * @since 4.2
     */
    protected String underscoreName(String name) {
        if (!org.springframework.util.StringUtils.hasLength(name)) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(name.charAt(0)));
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append('_').append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }


    /**
     * Extract the values for all columns in the current row.
     * <p>Utilizes public setters and result set meta-data.
     *
     * @see java.sql.ResultSetMetaData
     */
    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        BeanWrapperImpl bw = new BeanWrapperImpl();
        initBeanWrapper(bw);

        T mappedObject = constructMappedInstance(rs, bw);
        bw.setBeanInstance(mappedObject);

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Set<String> populatedProperties = (isCheckFullyPopulated() ? new HashSet<>() : null);

        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(rsmd, index);
            String property = lowerCaseName(StringUtils.delete(column, " "));
            PropertyDescriptor pd = (this.mappedProperties != null ? this.mappedProperties.get(property) : null);
            if (pd != null) {
                try {
                    Object value = getColumnValue(rs, index, pd);
                    if (rowNumber == 0 && log.isDebugEnabled()) {
                        log.debug("Mapping column '" + column + "' to property '" + pd.getName() + "' of type '" + ClassUtils.getQualifiedName(pd.getPropertyType()) + "'");
                    }
                    try {
                        bw.setPropertyValue(pd.getName(), value);
                    } catch (TypeMismatchException ex) {
                        if (value == null && this.primitivesDefaultedForNullValue) {
                            if (log.isDebugEnabled()) {
                                String propertyType = ClassUtils.getQualifiedName(pd.getPropertyType());
                                log.debug(String.format("Ignoring intercepted TypeMismatchException for row %d and column '%s' " + "with null value when setting property '%s' of type '%s' on object: %s", rowNumber, column, pd.getName(), propertyType, mappedObject), ex);
                            }
                        } else {
                            throw ex;
                        }
                    }
                    if (populatedProperties != null) {
                        populatedProperties.add(pd.getName());
                    }
                } catch (NotWritablePropertyException ex) {
                    throw new DataRetrievalFailureException("Unable to map column '" + column + "' to property '" + pd.getName() + "'", ex);
                }
            }
        }

        if (populatedProperties != null && !populatedProperties.equals(this.mappedPropertyNames)) {
            throw new InvalidDataAccessApiUsageException("Given ResultSet does not contain all properties " + "necessary to populate object of " + this.mappedClass + ": " + this.mappedPropertyNames);
        }

        return mappedObject;
    }

    /**
     * Construct an instance of the mapped class for the current row.
     *
     * @param rs the ResultSet to map (pre-initialized for the current row)
     * @param tc a TypeConverter with this RowMapper's conversion service
     * @return a corresponding instance of the mapped class
     * @throws SQLException if an SQLException is encountered
     * @since 5.3
     */
    protected T constructMappedInstance(ResultSet rs, TypeConverter tc) throws SQLException {
        Assert.state(this.mappedClass != null, "Mapped class was not specified");
        return BeanUtils.instantiateClass(this.mappedClass);
    }

    /**
     * Initialize the given BeanWrapper to be used for row mapping.
     * To be called for each row.
     * <p>The default implementation applies the configured {@link ConversionService},
     * if any. Can be overridden in subclasses.
     *
     * @param bw the BeanWrapper to initialize
     * @see #getConversionService()
     * @see BeanWrapper#setConversionService
     */
    protected void initBeanWrapper(BeanWrapper bw) {
        ConversionService cs = getConversionService();
        if (cs != null) {
            bw.setConversionService(cs);
        }
    }

    /**
     * Retrieve a JDBC object value for the specified column.
     * <p>The default implementation delegates to
     * {@link #getColumnValue(ResultSet, int, Class)}.
     *
     * @param rs    is the ResultSet holding the data
     * @param index is the column index
     * @param pd    the bean property that each result object is expected to match
     * @return the Object value
     * @throws SQLException in case of extraction failure
     * @see #getColumnValue(ResultSet, int, Class)
     */
    @Nullable
    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
    }

    /**
     * Retrieve a JDBC object value for the specified column.
     * <p>The default implementation calls
     * {@link JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)}.
     * Subclasses may override this to check specific value types upfront,
     * or to post-process values return from {@code getResultSetValue}.
     *
     * @param rs        is the ResultSet holding the data
     * @param index     is the column index
     * @param paramType the target parameter type
     * @return the Object value
     * @throws SQLException in case of extraction failure
     * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)
     * @since 5.3
     */
    @Nullable
    protected Object getColumnValue(ResultSet rs, int index, Class<?> paramType) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index, paramType);
    }


    private final static Map<String, ColumnNameRowMapper> columnNameRowMappers = new ConcurrentHashMap<>();

    /**
     * Static factory method to create a new {@code ColumnNameRowMapper}.
     *
     * @param mappedClass the class that each row should be mapped to
     * @see #newInstance(Class, ConversionService)
     */
    public static <T> ColumnNameRowMapper<T> newInstance(Class<T> mappedClass) {
        ColumnNameRowMapper<T> columnNameRowMapper = columnNameRowMappers.get(mappedClass.getName());
        synchronized (ColumnNameRowMapper.class) {
            if (null == columnNameRowMapper) {
                columnNameRowMapper = new ColumnNameRowMapper<>(mappedClass);
                columnNameRowMappers.put(mappedClass.getName(), columnNameRowMapper);
            }
        }

        return columnNameRowMapper;
    }

    /**
     * Static factory method to create a new {@code ColumnNameRowMapper}.
     *
     * @param mappedClass       the class that each row should be mapped to
     * @param conversionService the {@link ConversionService} for binding
     *                          JDBC values to bean properties, or {@code null} for none
     * @see #newInstance(Class)
     * @see #setConversionService
     * @since 5.2.3
     */
    public static <T> ColumnNameRowMapper<T> newInstance(Class<T> mappedClass, @Nullable ConversionService conversionService) {

        ColumnNameRowMapper<T> rowMapper = newInstance(mappedClass);
        rowMapper.setConversionService(conversionService);
        return rowMapper;
    }

}
