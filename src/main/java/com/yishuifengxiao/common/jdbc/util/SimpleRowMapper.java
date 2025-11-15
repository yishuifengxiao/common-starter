package com.yishuifengxiao.common.jdbc.util;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单的行映射器
 *
 * @author yishui
 * @version 1.0.0
 */
@Slf4j
public class SimpleRowMapper<T> implements RowMapper<T> {

    /**
     * 字段映射缓存
     */
    private final Map<Class<?>, Map<String, FieldMapping>> fieldMappingCache = new ConcurrentHashMap<>();


    /**
     * 基本类型默认值缓存
     */
    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = Map.of(int.class, 0, long.class, 0L, double.class, 0.0, float.class, 0.0f, boolean.class, false, byte.class, (byte) 0, short.class, (short) 0, char.class, '\0');

    /**
     * 日期时间类型集合
     */
    private static final Set<Class<?>> DATE_TIME_TYPES = Set.of(Date.class, LocalDateTime.class, LocalDate.class, LocalTime.class, Instant.class, ZonedDateTime.class, OffsetDateTime.class, java.sql.Date.class, java.sql.Time.class, java.sql.Timestamp.class);

    /**
     * 目标类目标类型
     */
    private final Class<T> targetClass;
    /**
     * 数据库时区ID
     */
    private ZoneId databaseZoneId;


    /**
     * 构造函数
     *
     * @param targetClass    目标类
     * @param databaseZoneId 数据库时区ID
     */
    public SimpleRowMapper(Class<T> targetClass, ZoneId databaseZoneId) {
        this.targetClass = targetClass;
        this.databaseZoneId = databaseZoneId;
        initializeFieldMapping(targetClass);
    }

    /**
     * 构造函数
     *
     * @param targetClass 目标类
     */
    public SimpleRowMapper(Class<T> targetClass) {
        this(targetClass, null);
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {

            T instance = targetClass.getDeclaredConstructor().newInstance();
Map<String, FieldMapping> fieldMappings = getFieldMappings(targetClass);

            // 优化：预构建列名映射表
            Map<String, FieldMapping> columnToFieldMap = buildColumnToFieldMap(fieldMappings);

            // 优化：批量处理列数据
            processColumns(rs, instance, columnToFieldMap);

            return instance;

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new SQLException("无法创建 " + targetClass.getName() + " 的实例", e);
        }
    }

    /**
     * 构建列名到字段的映射表
     * <p>
     * 该方法将字段映射信息转换为以列名为键的映射表，支持大小写不敏感的列名查找。
     * 对于每个字段映射，会同时添加小写列名和原始列名（如果两者不同）作为键。
     *
     * @param fieldMappings 字段映射信息，键为字段名，值为字段映射对象
     * @return 列名到字段映射的映射表，键为列名（小写形式），值为字段映射对象
     */
    private Map<String, FieldMapping> buildColumnToFieldMap(Map<String, FieldMapping> fieldMappings) {
        Map<String, FieldMapping> columnToFieldMap = new HashMap<>(fieldMappings.size() * 2);

        // 遍历所有字段映射，构建列名到字段的映射关系
        for (FieldMapping mapping : fieldMappings.values()) {
            String lowerColumnName = mapping.columnName.toLowerCase();
            columnToFieldMap.put(lowerColumnName, mapping);

            // 同时添加原始列名（如果不同）
            if (!mapping.columnName.equals(mapping.originalColumnName)) {
                columnToFieldMap.put(mapping.originalColumnName.toLowerCase(), mapping);
            }
        }

        return columnToFieldMap;
    }


    /**
     * 处理所有列数据
     *
     * @param rs               结果集对象，包含查询返回的数据
     * @param instance         目标对象实例，用于设置字段值
     * @param columnToFieldMap 列名到字段映射关系的Map集合
     * @throws SQLException 当数据库访问错误时抛出此异常
     */
    private void processColumns(ResultSet rs, T instance, Map<String, FieldMapping> columnToFieldMap) throws SQLException {
        // 获取结果集的元数据和列数
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // 遍历所有列，根据列名映射设置对象字段值
        for (int i = 1; i <= columnCount; i++) {
            String columnName = getColumnName(metaData, i);
            FieldMapping mapping = columnToFieldMap.get(columnName.toLowerCase());

            if (mapping != null) {
                setFieldValue(instance, mapping, rs, columnName);
            }
        }
    }


    /**
     * 获取列名
     *
     * @param metaData    结果集元数据对象
     * @param columnIndex 列索引
     * @return 返回列名或列标签
     * @throws SQLException 当数据库访问错误时抛出异常
     */
    private String getColumnName(ResultSetMetaData metaData, int columnIndex) throws SQLException {
        // 优先获取列标签，如果为空则获取列名
        String columnName = metaData.getColumnLabel(columnIndex);
        return columnName != null ? columnName : metaData.getColumnName(columnIndex);
    }


    /**
     * 初始化字段映射
     *
     * @param clazz 需要初始化字段映射的类
     * @return 该类对应的字段映射表
     */
    private Map<String, FieldMapping> initializeFieldMapping(Class<?> clazz) {
        // 从缓存中获取字段映射，如果不存在则创建新的字段映射
        return fieldMappingCache.computeIfAbsent(clazz, this::createFieldMappings);
    }


    /**
     * 创建字段映射
     *
     * @param clazz 需要创建字段映射的类
     * @return 包含所有字段映射的不可变Map，键为字段名，值为字段映射对象
     */
    private Map<String, FieldMapping> createFieldMappings(Class<?> clazz) {
        // 获取类的所有声明字段
        Field[] fields = getAllDeclaredFields(clazz);
        Map<String, FieldMapping> fieldMappings = new HashMap<>(fields.length);

        // 遍历所有字段，创建字段映射
        for (Field field : fields) {
            if (shouldSkipField(field)) {
                continue;
            }

            FieldMapping mapping = createFieldMapping(field);
            fieldMappings.put(mapping.fieldName, mapping);
        }

        // 返回不可变的字段映射集合
        return Collections.unmodifiableMap(fieldMappings);
    }


    /**
     * 判断是否跳过字段
     *
     * @param field 需要判断的字段对象
     * @return true表示应该跳过该字段，false表示不应该跳过该字段
     */
    private boolean shouldSkipField(Field field) {
        // 获取字段的修饰符
        int modifiers = field.getModifiers();
        // 判断字段是否为静态字段，如果是静态字段则跳过
        return Modifier.isStatic(modifiers);
    }


    /**
     * 创建字段映射对象
     *
     * @param field 需要创建映射的字段对象
     * @return 返回创建的字段映射对象
     */
    private FieldMapping createFieldMapping(Field field) {
        FieldMapping mapping = new FieldMapping();
        mapping.field = field;
        mapping.fieldName = field.getName();
        mapping.fieldType = field.getType();
        mapping.isPrimitive = mapping.fieldType.isPrimitive();

        // 设置列映射配置
        setupColumnMapping(mapping, field);
        // 设置字段访问权限
        setupFieldAccessibility(mapping, field);

        return mapping;
    }


    /**
     * 设置列映射
     *
     * @param mapping 字段映射对象，用于存储列映射信息
     * @param field   类字段对象，用于获取注解和字段名称
     */
    private void setupColumnMapping(FieldMapping mapping, Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        String columnName;

        // 获取字段上的@Column注解，如果存在且指定了列名，则使用注解中的列名
        if (columnAnnotation != null && !(columnName = columnAnnotation.name()).isEmpty()) {
            mapping.originalColumnName = columnName;
            mapping.columnName = columnName.toLowerCase();
        } else {
            // 如果没有指定@Column注解或列名为空，则将字段名转换为蛇形命名作为列名
            String snakeCaseName = camelToSnakeCase(field.getName());
            mapping.originalColumnName = snakeCaseName;
            mapping.columnName = snakeCaseName.toLowerCase();
        }

        // 判断字段是否为主键字段
        mapping.isId = field.getAnnotation(Id.class) != null;
    }


    /**
     * 设置字段可访问性
     *
     * @param mapping 字段映射对象
     * @param field   需要设置可访问性的字段
     */
    private void setupFieldAccessibility(FieldMapping mapping, Field field) {
        // 不再在初始化阶段设置setAccessible(true)
        // 仅记录字段是否需要特殊访问权限
        int modifiers = field.getModifiers();
        mapping.isAccessible = !Modifier.isPublic(modifiers) || Modifier.isFinal(modifiers);
    }


    /**
     * 获取类及其父类的所有字段
     *
     * @param clazz 要获取字段的类
     * @return 包含当前类及其所有父类声明字段的数组
     */
    private Field[] getAllDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;

        // 遍历当前类及其所有父类，收集所有声明的字段
        while (currentClass != null && currentClass != Object.class) {
            Collections.addAll(fields, currentClass.getDeclaredFields());
            currentClass = currentClass.getSuperclass();
        }

        return fields.toArray(new Field[0]);
    }


    /**
     * 获取字段映射
     *
     * @param clazz 需要获取字段映射的类
     * @return 返回指定类的字段映射关系，如果缓存中不存在则初始化后返回
     */
    private Map<String, FieldMapping> getFieldMappings(Class<?> clazz) {
        // 从缓存中获取字段映射，如果不存在则初始化并存入缓存
        return fieldMappingCache.computeIfAbsent(clazz, this::initializeFieldMapping);
    }


    /**
     * 设置字段值
     *
     * @param instance   实例对象
     * @param mapping    字段映射信息
     * @param rs         结果集
     * @param columnName 列名
     * @throws SQLException 当数据库访问错误或字段设置失败时抛出
     */
    private void setFieldValue(T instance, FieldMapping mapping, ResultSet rs, String columnName) throws SQLException {
        try {
            // 获取列值并设置到对象字段中
            Object value = getColumnValue(rs, columnName, mapping.fieldType, mapping.isPrimitive);
            if (value != null || !mapping.isPrimitive) {
                // 仅在需要时临时设置可访问性
                boolean originalAccessible = mapping.isAccessible;
                if (originalAccessible) {
                    mapping.field.setAccessible(true);
                }
                try {
                    mapping.field.set(instance, value);
                } finally {
                    // 操作完成后恢复原来的访问权限
                    if (originalAccessible) {
                        mapping.field.setAccessible(false);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new SQLException("无法设置字段值: " + mapping.fieldName, e);
        } catch (IllegalArgumentException e) {
            throw new SQLException("字段类型不匹配: " + mapping.fieldName + ", 期望类型: " + mapping.fieldType + ", 实际值: " + getColumnValueForError(rs, columnName), e);
        }
    }


    /**
     * 获取列值并进行类型转换
     *
     * @param rs          ResultSet对象，用于获取数据库查询结果
     * @param columnName  列名，指定要获取值的列
     * @param targetType  目标类型，指定要转换成的数据类型
     * @param isPrimitive 是否为基本类型，true表示基本类型，false表示包装类型
     * @return 转换后的列值，可能为null
     * @throws SQLException 当数据库访问错误时抛出
     */
    private Object getColumnValue(ResultSet rs, String columnName, Class<?> targetType, boolean isPrimitive) throws SQLException {
        // 检查是否为NULL
        Object rawValue = rs.getObject(columnName);
        if (rawValue == null || rs.wasNull()) {
            return handleNullValue(targetType, isPrimitive);
        }
        
        // 特殊处理：当数据库字段为tinyint(1)时，某些JDBC驱动会错误地将整数值转换为布尔值
        // 例如：数据库值为4，但rs.getObject()返回true，这会导致数据错误
        if (rawValue instanceof Boolean && isNumericType(targetType)) {
            // 如果是布尔值但目标类型是数值类型，重新获取原始整数值
            try {
                // 使用getInt获取原始整数值，避免JDBC驱动的错误转换
                int intValue = rs.getInt(columnName);
                if (!rs.wasNull()) {
                    // 将整数值转换为目标类型
                    return convertNumber(intValue, targetType, isPrimitive);
                }
            } catch (SQLException e) {
                // 如果获取整数值失败，回退到原始逻辑
                log.debug("获取列 {} 的整数值失败，使用原始值: {}", columnName, e.getMessage());
            }
        }
        
        // 特殊处理BLOB类型：如果数据库返回的是byte数组（BLOB），但目标类型是String
        if (rawValue instanceof byte[] && targetType == String.class) {
            try {
                return new String((byte[]) rawValue, java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception e) {
                // 如果转换失败，返回原始值的字符串表示
                return new String((byte[]) rawValue);
            }
        }
        // 日期时间类型处理
        if (isDateTimeType(targetType)) {
            return convertDateTimeValue(rs, columnName, targetType);
        }

        // BLOB类型处理：当目标类型为String且原始值为byte数组时，转换为String
        if (targetType == String.class && rawValue instanceof byte[]) {
            try {
                return new String((byte[]) rawValue, "UTF-8");
            } catch (Exception e) {
                // 如果UTF-8转换失败，尝试使用平台默认编码
                return new String((byte[]) rawValue);
            }
        }
        // 基本类型和包装类型处理
        return convertBasicType(rawValue, targetType, isPrimitive);
    }

    /**
     * 判断目标类型是否为数值类型
     *
     * @param targetType 目标类型
     * @return 如果是数值类型返回true，否则返回false
     */
    private boolean isNumericType(Class<?> targetType) {
        return targetType == Integer.class || targetType == int.class ||
               targetType == Long.class || targetType == long.class ||
               targetType == Double.class || targetType == double.class ||
               targetType == Float.class || targetType == float.class ||
               targetType == Short.class || targetType == short.class ||
               targetType == Byte.class || targetType == byte.class ||
               targetType == BigDecimal.class;
    }

    /**
     * 转换数字类型（重载方法，支持直接传入int值）
     *
     * @param number      需要转换的整数值
     * @param targetType  目标类型
     * @param isPrimitive 是否为基本数据类型
     * @return 转换后的数字对象
     */
    private Object convertNumber(int number, Class<?> targetType, boolean isPrimitive) {
        // 处理整数类型转换
        if (targetType == Integer.class || targetType == int.class) {
            return number;
        } else if (targetType == Long.class || targetType == long.class) {
            return (long) number;
        } else if (targetType == Double.class || targetType == double.class) {
            return (double) number;
        } else if (targetType == Float.class || targetType == float.class) {
            return (float) number;
        } else if (targetType == Short.class || targetType == short.class) {
            return (short) number;
        } else if (targetType == Byte.class || targetType == byte.class) {
            return (byte) number;
            // 处理BigDecimal类型转换
        } else if (targetType == BigDecimal.class) {
            return BigDecimal.valueOf(number);
        }
        return number;
    }


    /**
     * 处理NULL值转换为目标类型的操作
     *
     * @param targetType  目标类型Class对象
     * @param isPrimitive 是否为基本数据类型
     * @return 如果是基本数据类型则返回对应的默认值，否则返回null
     */
    private Object handleNullValue(Class<?> targetType, boolean isPrimitive) {
        // 根据是否为基本数据类型决定返回值：
        // 基本数据类型返回其对应默认值，引用类型返回null
        return isPrimitive ? PRIMITIVE_DEFAULTS.get(targetType) : null;
    }


    /**
     * 判断指定的类型是否为日期时间类型
     *
     * @param targetType 待判断的目标类型
     * @return 如果目标类型在日期时间类型集合中则返回true，否则返回false
     */
    private boolean isDateTimeType(Class<?> targetType) {
        return DATE_TIME_TYPES.contains(targetType);
    }


    /**
     * 转换日期时间值
     *
     * @param rs         结果集对象，用于获取数据库查询结果
     * @param columnName 数据库列名，指定要转换的列
     * @param targetType 目标类型，指定要转换成的日期时间类型
     * @return 转换后的日期时间对象
     * @throws SQLException 当数据库访问错误或转换失败时抛出
     */
    private Object convertDateTimeValue(ResultSet rs, String columnName, Class<?> targetType) throws SQLException {
        try {
            // 根据目标类型的不同，调用相应的转换方法进行日期时间转换
            return switch (targetType.getSimpleName()) {
                case "Date" -> convertToUtilDate(rs, columnName);
                case "LocalDateTime" -> convertToLocalDateTime(rs, columnName);
                case "LocalDate" -> convertToLocalDate(rs, columnName);
                case "LocalTime" -> convertToLocalTime(rs, columnName);
                case "Instant" -> convertToInstant(rs, columnName);
                case "ZonedDateTime" -> convertToZonedDateTime(rs, columnName);
                case "OffsetDateTime" -> convertToOffsetDateTime(rs, columnName);
                case "java.sql.Date" -> rs.getDate(columnName);
                case "java.sql.Time" -> rs.getTime(columnName);
                case "java.sql.Timestamp" -> rs.getTimestamp(columnName);
                default -> convertDateTimeFallback(rs, columnName, targetType);
            };
        } catch (SQLException e) {
            // 当转换过程中发生SQL异常时，使用备用转换方法进行处理
            return convertDateTimeFallback(rs, columnName, targetType);
        }
    }


    /**
     * 转换为java.util.Date
     *
     * @param rs         ResultSet对象，用于获取数据库查询结果
     * @param columnName 数据库列名
     * @return 转换后的Date对象，如果数据库值为null则返回null
     * @throws SQLException 当数据库访问发生错误时抛出
     */
    private Date convertToUtilDate(ResultSet rs, String columnName) throws SQLException {
        // 获取指定列的Timestamp值
        Timestamp timestamp = rs.getTimestamp(columnName);
        if (timestamp == null) {
            return null;
        }

        // 如果指定了数据库时区，则进行时区转换
        if (databaseZoneId != null) {
            Instant instant = timestamp.toInstant();
            ZonedDateTime zonedDateTime = instant.atZone(databaseZoneId);
            return Date.from(zonedDateTime.toInstant());
        }
        // 使用默认时区转换Timestamp为Date
        return new Date(timestamp.getTime());
    }


    /**
     * 转换为LocalDateTime
     *
     * @param rs         结果集对象，用于获取时间戳数据
     * @param columnName 数据库列名，指定要转换的时间戳列
     * @return 转换后的LocalDateTime对象，如果数据库值为null则返回null
     * @throws SQLException 当访问结果集出现SQL异常时抛出
     */
    private LocalDateTime convertToLocalDateTime(ResultSet rs, String columnName) throws SQLException {
        // 获取指定列的时间戳值
        Timestamp timestamp = rs.getTimestamp(columnName);
        if (timestamp == null) {
            return null;
        }

        // 如果指定了数据库时区，则使用时区信息进行转换
        if (databaseZoneId != null) {
            Instant instant = timestamp.toInstant();
            ZonedDateTime zonedDateTime = instant.atZone(databaseZoneId);
            return zonedDateTime.toLocalDateTime();
        }
        // 否则直接转换为LocalDateTime
        return timestamp.toLocalDateTime();
    }


    /**
     * 转换为LocalDate
     *
     * @param rs         ResultSet对象，用于获取数据库查询结果
     * @param columnName 数据库列名，指定要转换的日期列
     * @return 转换后的LocalDate对象，如果数据库值为null则返回null
     * @throws SQLException 当数据库访问错误时抛出
     */
    private LocalDate convertToLocalDate(ResultSet rs, String columnName) throws SQLException {
        // 获取数据库中的日期值
        java.sql.Date sqlDate = rs.getDate(columnName);
        if (sqlDate == null) {
            return null;
        }

        // 如果指定了数据库时区，则使用时区信息进行转换
        if (databaseZoneId != null) {
            Instant instant = Instant.ofEpochMilli(sqlDate.getTime());
            ZonedDateTime zonedDateTime = instant.atZone(databaseZoneId);
            return zonedDateTime.toLocalDate();
        }
        // 否则直接转换为LocalDate
        return sqlDate.toLocalDate();
    }


    /**
     * 转换为LocalTime
     *
     * @param rs         ResultSet对象，用于获取时间数据
     * @param columnName 数据库列名，指定要转换的时间列
     * @return 转换后的LocalTime对象，如果数据库时间为null则返回null
     * @throws SQLException 当数据库访问错误时抛出
     */
    private LocalTime convertToLocalTime(ResultSet rs, String columnName) throws SQLException {
        // 获取数据库中的时间值
        Time sqlTime = rs.getTime(columnName);
        if (sqlTime == null) {
            return null;
        }

        // 如果指定了数据库时区，则进行时区转换
        if (databaseZoneId != null) {
            Instant instant = Instant.ofEpochMilli(sqlTime.getTime());
            ZonedDateTime zonedDateTime = instant.atZone(databaseZoneId);
            return zonedDateTime.toLocalTime();
        }
        // 使用默认时区转换
        return sqlTime.toLocalTime();
    }


    /**
     * 转换为Instant
     *
     * @param rs         ResultSet对象，用于获取时间戳数据
     * @param columnName 数据库列名，指定要获取的时间戳列
     * @return 转换后的Instant对象，如果数据库值为null则返回null
     * @throws SQLException 当访问数据库结果集出现异常时抛出
     */
    private Instant convertToInstant(ResultSet rs, String columnName) throws SQLException {
        // 获取指定列的时间戳值
        Timestamp timestamp = rs.getTimestamp(columnName);
        if (timestamp == null) {
            return null;
        }

        // 如果指定了数据库时区，则进行时区转换
        if (databaseZoneId != null) {
            Instant instant = timestamp.toInstant();
            ZonedDateTime zonedDateTime = instant.atZone(databaseZoneId);
            return zonedDateTime.toInstant();
        }
        // 直接转换为Instant对象
        return timestamp.toInstant();
    }


    /**
     * 转换为ZonedDateTime
     *
     * @param rs         结果集对象，用于获取时间戳数据
     * @param columnName 数据库列名，指定要获取时间戳的列
     * @return 转换后的ZonedDateTime对象，如果时间戳为null则返回null
     * @throws SQLException 当数据库访问错误时抛出
     */
    private ZonedDateTime convertToZonedDateTime(ResultSet rs, String columnName) throws SQLException {
        // 获取指定列的时间戳值
        Timestamp timestamp = rs.getTimestamp(columnName);
        if (timestamp == null) {
            return null;
        }

        // 根据配置的数据库时区或系统默认时区进行转换
        if (databaseZoneId != null) {
            Instant instant = timestamp.toInstant();
            return instant.atZone(databaseZoneId);
        }
        return timestamp.toInstant().atZone(ZoneId.systemDefault());
    }


    /**
     * 转换为OffsetDateTime
     *
     * @param rs         结果集对象，用于获取数据库查询结果
     * @param columnName 列名，指定要转换的数据库列
     * @return OffsetDateTime对象，如果转换的ZonedDateTime为null则返回null
     * @throws SQLException 当数据库访问错误或列不存在时抛出
     */
    private OffsetDateTime convertToOffsetDateTime(ResultSet rs, String columnName) throws SQLException {
        // 先转换为ZonedDateTime，再转换为OffsetDateTime
        ZonedDateTime zonedDateTime = convertToZonedDateTime(rs, columnName);
        return zonedDateTime != null ? zonedDateTime.toOffsetDateTime() : null;
    }


    /**
     * 日期时间转换备用方法
     * 当常规日期时间转换失败时，使用此方法进行备用转换处理
     *
     * @param rs         ResultSet对象，用于获取数据库查询结果
     * @param columnName 数据库列名
     * @param targetType 目标转换类型
     * @return 转换后的日期时间对象，如果转换失败则返回原始值
     * @throws SQLException 当数据库访问发生错误时抛出
     */
    private Object convertDateTimeFallback(ResultSet rs, String columnName, Class<?> targetType) throws SQLException {

        Object rawValue = rs.getObject(columnName);
        if (rawValue == null) {
            return null;
        }

        // 如果数据库返回的是字符串，尝试解析
        if (rawValue instanceof String) {
            return parseDateTimeFromString((String) rawValue, targetType);
        }

        // 其他情况返回原始值
        return rawValue;
    }


    /**
     * 从字符串解析日期时间
     *
     * @param value      需要解析的日期时间字符串
     * @param targetType 目标类型，支持LocalDateTime、LocalDate、LocalTime、Instant
     * @return 解析成功返回对应的日期时间对象，解析失败返回null
     */
    private Object parseDateTimeFromString(String value, Class<?> targetType) {
        try {
            // 根据目标类型进行相应的日期时间解析
            if (targetType == LocalDateTime.class) {
                return LocalDateTime.parse(value);
            } else if (targetType == LocalDate.class) {
                return LocalDate.parse(value);
            } else if (targetType == LocalTime.class) {
                return LocalTime.parse(value);
            } else if (targetType == Instant.class) {
                return Instant.parse(value);
            }
        } catch (Exception e) {
            // 解析失败，返回null
        }
        return null;
    }


    /**
     * 转换基本类型
     *
     * @param rawValue    原始值对象
     * @param targetType  目标类型Class对象
     * @param isPrimitive 是否为基本类型
     * @return 转换后的对象，如果无法转换则返回原始值
     */
    private Object convertBasicType(Object rawValue, Class<?> targetType, boolean isPrimitive) {
        if (targetType.isInstance(rawValue)) {
            return rawValue;
        }

        // 处理常见的类型转换
        if (rawValue instanceof Number) {
            return convertNumber((Number) rawValue, targetType, isPrimitive);
        } else if (rawValue instanceof Boolean) {
            return convertBoolean((Boolean) rawValue, targetType, isPrimitive);
        } else if (rawValue instanceof String) {
            return convertString((String) rawValue, targetType, isPrimitive);
        }

        return rawValue;
    }


    /**
     * 转换数字类型
     *
     * @param number      需要转换的数字对象
     * @param targetType  目标类型
     * @param isPrimitive 是否为基本数据类型
     * @return 转换后的数字对象
     */
    private Object convertNumber(Number number, Class<?> targetType, boolean isPrimitive) {
        // 处理整数类型转换
        if (targetType == Integer.class || targetType == int.class) {
            return number.intValue();
        } else if (targetType == Long.class || targetType == long.class) {
            return number.longValue();
        } else if (targetType == Double.class || targetType == double.class) {
            return number.doubleValue();
        } else if (targetType == Float.class || targetType == float.class) {
            return number.floatValue();
        } else if (targetType == Short.class || targetType == short.class) {
            return number.shortValue();
        } else if (targetType == Byte.class || targetType == byte.class) {
            return number.byteValue();
            // 处理BigDecimal类型转换
        } else if (targetType == BigDecimal.class) {
            if (number instanceof BigDecimal) {
                return number;
            }
            return BigDecimal.valueOf(number.doubleValue());
        }
        return number;
    }


    /**
     * 转换布尔类型
     *
     * @param bool        原始布尔值
     * @param targetType  目标类型
     * @param isPrimitive 是否为基础类型
     * @return 转换后的对象
     */
    private Object convertBoolean(Boolean bool, Class<?> targetType, boolean isPrimitive) {
        // 根据目标类型进行相应的转换
        if (targetType == Boolean.class || targetType == boolean.class) {
            return bool;
        } else if (targetType == Integer.class || targetType == int.class) {
            return bool ? 1 : 0;
        } else if (targetType == String.class) {
            return bool.toString();
        }
        return bool;
    }


    /**
     * 转换字符串类型
     *
     * @param str         待转换的字符串
     * @param targetType  目标类型
     * @param isPrimitive 是否为基本数据类型
     * @return 转换后的对象，如果转换失败则返回默认值
     */
    private Object convertString(String str, Class<?> targetType, boolean isPrimitive) {
        if (targetType == String.class) {
            return str;
        }

        // 尝试将字符串转换为目标数值类型
        try {
            if (targetType == Integer.class || targetType == int.class) {
                return Integer.parseInt(str);
            } else if (targetType == Long.class || targetType == long.class) {
                return Long.parseLong(str);
            } else if (targetType == Double.class || targetType == double.class) {
                return Double.parseDouble(str);
            } else if (targetType == Float.class || targetType == float.class) {
                return Float.parseFloat(str);
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return parseBoolean(str);
            }
        } catch (NumberFormatException e) {
            // 转换失败，返回默认值
            return handleNullValue(targetType, isPrimitive);
        }

        return str;
    }


    /**
     * 解析布尔值（支持多种格式）
     *
     * @param str 待解析的字符串，支持"true"/"false"、"1"/"0"、"yes"/"no"、"y"/"n"等格式（不区分大小写）
     * @return 解析结果，当输入为"true"、"1"、"yes"、"y"（不区分大小写）时返回true，其他情况返回false
     */
    private boolean parseBoolean(String str) {
        // 处理空值情况
        if (str == null) {
            return false;
        }
        // 统一转换为小写进行比较，提高代码可读性
        String lower = str.toLowerCase();
        // 支持多种布尔值格式的解析
        return lower.equals("true") || lower.equals("1") || lower.equals("yes") || lower.equals("y");
    }


    /**
     * 驼峰转下划线
     */
    private String camelToSnakeCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * 获取错误信息中的列值
     */
    private String getColumnValueForError(ResultSet rs, String columnName) {
        try {
            Object value = rs.getObject(columnName);
            return value != null ? value.toString() : "NULL";
        } catch (SQLException e) {
            return "无法获取值";
        }
    }

    /**
     * 字段映射内部类
     */
    private static class FieldMapping {
        /**
         * 字段对象
         */
        Field field;
        /**
         * 字段名
         */
        String fieldName;
        /**
         * 字段类型
         */
        Class<?> fieldType;
        /**
         * 是否为基本数据类型
         */
        boolean isPrimitive;
        /**
         * 列名
         */
        String columnName;
        /**
         * 原始列名
         */
        String originalColumnName;
        /**
         * 是否为主键
         */
        boolean isId;
        /**
         * 是否需要特殊访问权限
         */
        boolean isAccessible;
    }
}