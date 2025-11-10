package com.yishuifengxiao.common.support;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.*;


/**
 * POJO类生成器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class PojoGenerator {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Accessors(chain = true)
    // 配置类
    public static class GeneratorConfig {
        private String driver = "com.mysql.cj.jdbc.Driver";
        private String url;
        private String username;
        private String password;
        private String packageName = "com.example.entity";
        private String outputPath = "src/main/java/com/example/entity";
        private List<String> tablePrefixes = new ArrayList<>();
        private Set<String> includeTables = new HashSet<>();
        private Set<String> excludeTables = new HashSet<>();
        private boolean useLombok = true;
        private boolean useSwagger = false;
        private boolean useJpa = true;
        private String idType = "Long";
        private boolean useJava8Date = true;
        private boolean generateColumnDefinition = true;
        private String databaseProductName;

        public GeneratorConfig(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }

        // 便捷方法
        public GeneratorConfig addTablePrefix(String prefix) {
            this.tablePrefixes.add(prefix);
            return this;
        }

        public GeneratorConfig addIncludeTable(String table) {
            this.includeTables.add(table);
            return this;
        }

        public GeneratorConfig addExcludeTable(String table) {
            this.excludeTables.add(table);
            return this;
        }
    }

    /**
     * 生成所有POJO类
     *
     * @param config 生成器配置信息，包含数据库连接信息、输出路径等配置参数
     */
    public void generateAllPojos(GeneratorConfig config) {
        Connection conn = null;

        try {
            // 加载数据库驱动
            Class.forName(config.getDriver());
            conn = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());

            // 获取数据库产品名称并设置到配置中
            DatabaseMetaData dbMetaData = conn.getMetaData();
            config.setDatabaseProductName(dbMetaData.getDatabaseProductName());

            // 获取所有表信息
            List<TableInfo> tables = getAllTables(conn, config);
            System.out.println("发现 " + tables.size() + " 个表");

            // 创建输出目录
            File outputDir = new File(config.getOutputPath());
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // 遍历所有表，为每个表生成对应的POJO类
            for (TableInfo table : tables) {
                generatePojoForTable(table, config);
            }

            System.out.println("所有POJO类生成完成! 共生成 " + tables.size() + " 个类");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭数据库连接
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 获取所有表信息
     */
    private List<TableInfo> getAllTables(Connection conn, GeneratorConfig config) throws SQLException {
        List<TableInfo> tables = new ArrayList<>();
        DatabaseMetaData metaData = conn.getMetaData();

        // 获取数据库名称
        String databaseName = conn.getCatalog();

        ResultSet tablesRs = metaData.getTables(databaseName, null, "%", new String[]{"TABLE"});

        while (tablesRs.next()) {
            String tableName = tablesRs.getString("TABLE_NAME");
            String remarks = tablesRs.getString("REMARKS");

            // 过滤表
            if (!shouldProcessTable(tableName, config)) {
                continue;
            }

            TableInfo table = new TableInfo();
            table.setOriginalName(tableName);
            table.setRemarks(remarks);
            table.setClassName(generateClassName(tableName, config));

            // 获取列信息和主键信息
            List<ColumnInfo> columns = getTableColumns(conn, tableName);
            table.setColumns(columns);

            // 获取主键信息
            PrimaryKeyInfo primaryKey = getPrimaryKeyInfo(conn, tableName);
            table.setPrimaryKey(primaryKey);

            tables.add(table);
        }

        return tables;
    }

    /**
     * 判断是否应该处理该表
     */
    private boolean shouldProcessTable(String tableName, GeneratorConfig config) {
        // 如果在排除列表中，则跳过
        if (config.getExcludeTables().contains(tableName)) {
            return false;
        }

        // 如果指定了包含表，则只处理包含的表
        if (!config.getIncludeTables().isEmpty() && !config.getIncludeTables().contains(tableName)) {
            return false;
        }

        return true;
    }

    /**
     * 生成类名（去除前缀）
     */
    private String generateClassName(String tableName, GeneratorConfig config) {
        String processedName = tableName;

        // 去除前缀
        for (String prefix : config.getTablePrefixes()) {
            if (processedName.startsWith(prefix)) {
                processedName = processedName.substring(prefix.length());
                break; // 只去除第一个匹配的前缀
            }
        }

        // 转换为驼峰命名
        return toCamelCase(processedName, true);
    }

    /**
     * 获取表的列信息
     */
    private List<ColumnInfo> getTableColumns(Connection conn, String tableName) throws SQLException {
        List<ColumnInfo> columns = new ArrayList<>();
        DatabaseMetaData metaData = conn.getMetaData();
        String databaseName = conn.getCatalog();

        ResultSet columnsRs = metaData.getColumns(databaseName, null, tableName, null);

        while (columnsRs.next()) {
            String remarks = columnsRs.getString("REMARKS");
            remarks = null == remarks ? null : remarks.replaceAll("(\r\n|\r|\n)", "").trim();

            ColumnInfo column = new ColumnInfo();
            column.setColumnName(columnsRs.getString("COLUMN_NAME"));
            column.setDataType(columnsRs.getString("TYPE_NAME"));
            column.setRemarks(remarks);
            column.setNullable(columnsRs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
            column.setColumnSize(columnsRs.getInt("COLUMN_SIZE"));
            column.setDecimalDigits(columnsRs.getInt("DECIMAL_DIGITS"));
            column.setDefaultValue(columnsRs.getString("COLUMN_DEF"));

            // 判断是否自增
            String isAutoIncrement = columnsRs.getString("IS_AUTOINCREMENT");
            column.setAutoIncrement("YES".equals(isAutoIncrement));

            columns.add(column);
        }

        // 对于MySQL，我们需要通过查询information_schema来获取正确的显示宽度
        if ("MySQL".equalsIgnoreCase(conn.getMetaData().getDatabaseProductName())) {
            fixMySQLIntegerDisplayWidth(conn, tableName, columns);
        }

        return columns;
    }

    /**
     * 通过查询information_schema获取正确的显示宽度
     */
    private void fixMySQLIntegerDisplayWidth(Connection conn, String tableName, List<ColumnInfo> columns) throws SQLException {
        String databaseName = conn.getCatalog();
        String sql = "SELECT COLUMN_NAME, COLUMN_TYPE FROM information_schema.COLUMNS " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, databaseName);
            pstmt.setString(2, tableName);

            ResultSet rs = pstmt.executeQuery();
            Map<String, String> columnTypes = new HashMap<>();

            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String columnType = rs.getString("COLUMN_TYPE");
                columnTypes.put(columnName, columnType);
            }

            // 更新列的显示宽度
            for (ColumnInfo column : columns) {
                String columnType = columnTypes.get(column.getColumnName());
                if (columnType != null && isIntegerType(column.getDataType())) {
                    // 从COLUMN_TYPE中提取显示宽度
                    Integer displayWidth = extractDisplayWidth(columnType);
                    if (displayWidth != null) {
                        column.setColumnSize(displayWidth);
                    }
                }
            }
        }
    }

    /**
     * 从MySQL的COLUMN_TYPE中提取显示宽度
     * 例如: "int(11)" -> 11, "bigint(20)" -> 20, "tinyint(4)" -> 4
     */
    private Integer extractDisplayWidth(String columnType) {
        if (columnType == null) return null;

        // 查找括号中的数字
        int start = columnType.indexOf('(');
        int end = columnType.indexOf(')');

        if (start != -1 && end != -1 && end > start + 1) {
            try {
                String widthStr = columnType.substring(start + 1, end);
                // 修复：对于int类型，确保返回正确的显示宽度
                if (columnType.toLowerCase().contains("int") &&
                        !columnType.toLowerCase().contains("bigint") &&
                        !columnType.toLowerCase().contains("tinyint") &&
                        !columnType.toLowerCase().contains("smallint") &&
                        !columnType.toLowerCase().contains("mediumint")) {
                    // 标准int类型应该返回11
                    return 11;
                }
                return Integer.parseInt(widthStr);
            } catch (NumberFormatException e) {
                // 如果解析失败，返回null
                return null;
            }
        }

        return null;
    }


    /**
     * 获取表的主键信息
     */
    private PrimaryKeyInfo getPrimaryKeyInfo(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        String databaseName = conn.getCatalog();

        ResultSet primaryKeysRs = metaData.getPrimaryKeys(databaseName, null, tableName);

        PrimaryKeyInfo primaryKey = new PrimaryKeyInfo();
        List<String> primaryKeyColumns = new ArrayList<>();

        while (primaryKeysRs.next()) {
            String columnName = primaryKeysRs.getString("COLUMN_NAME");
            String keySeq = primaryKeysRs.getString("KEY_SEQ");
            String pkName = primaryKeysRs.getString("PK_NAME");

            primaryKeyColumns.add(columnName);
            primaryKey.setName(pkName);
        }

        primaryKey.setColumns(primaryKeyColumns);

        // 如果只有一个主键列，设置其自增信息
        if (primaryKeyColumns.size() == 1) {
            String pkColumn = primaryKeyColumns.get(0);
            // 需要从列信息中获取自增状态
            List<ColumnInfo> columns = getTableColumns(conn, tableName);
            for (ColumnInfo column : columns) {
                if (column.getColumnName().equals(pkColumn)) {
                    primaryKey.setAutoIncrement(column.isAutoIncrement());
                    break;
                }
            }
        }

        return primaryKey;
    }

    /**
     * 为单个表生成POJO
     */
    private void generatePojoForTable(TableInfo table, GeneratorConfig config) throws Exception {
        System.out.println("正在生成表: " + table.getOriginalName() + " -> " + table.getClassName());

        String javaCode = generatePojoCode(table, config);

        // 写入文件
        File file = new File(config.getOutputPath(), table.getClassName() + ".java");
        FileWriter writer = new FileWriter(file);
        writer.write(javaCode);
        writer.flush();
        writer.close();

        System.out.println("生成: " + file.getAbsolutePath());
    }

    /**
     * 生成POJO代码
     */
    private String generatePojoCode(TableInfo table, GeneratorConfig config) {
        CodeBuilder cb = new CodeBuilder();

        // 包声明
        cb.append("package ").append(config.getPackageName()).append(";").newLine().newLine();

        // 导入语句
        generateImports(cb, table, config);

        // 类注释
        cb.append("/**").newLine();
        cb.append(" * ").append(table.getClassName()).append(" 实体类").newLine();
        cb.append(" * 对应数据库表: ").append(table.getOriginalName()).newLine();
        if (table.getRemarks() != null && !table.getRemarks().isEmpty()) {
            cb.append(" * 表注释: ").append(table.getRemarks()).newLine();
        }
        cb.append(" */").newLine();

        // 类注解
        if (config.isUseSwagger()) {
            cb.append("@ApiModel(description = \"").append(table.getRemarks() != null ? table.getRemarks() :
                    table.getClassName()).append("\")").newLine();
        }

        // Lombok注解
        if (config.isUseLombok()) {
            cb.append("@Data").newLine();
            cb.append("@Accessors(chain = true)").newLine();
            cb.append("@AllArgsConstructor").newLine();
            cb.append("@NoArgsConstructor").newLine();
        }

        // JPA注解
        if (config.isUseJpa()) {
            cb.append("@Entity").newLine();
            cb.append("@Table(name = \"").append(table.getOriginalName()).append("\")");
            if (table.getRemarks() != null && !table.getRemarks().isEmpty()) {
                cb.append(" // ").append(table.getRemarks());
            }
            cb.newLine();
        }

        // 类声明
        cb.append("public class ").append(table.getClassName());

        if (config.isUseJpa()) {
            cb.append(" implements Serializable");
        }

        cb.append(" {").newLine().newLine();

        if (config.isUseJpa()) {
            cb.append("    private static final long serialVersionUID = 1L;").newLine().newLine();
        }

        // 字段声明
        for (ColumnInfo column : table.getColumns()) {
            generateField(cb, column, table, config);
        }

        // 如果不用Lombok，生成getter和setter
        if (!config.isUseLombok()) {
            for (ColumnInfo column : table.getColumns()) {
                generateGetterSetter(cb, column);
            }
        }

        cb.append("}").newLine();

        return cb.toString();
    }

    /**
     * 生成导入语句
     */
    private void generateImports(CodeBuilder cb, TableInfo table, GeneratorConfig config) {
        Set<String> imports = new LinkedHashSet<>();

        if (config.isUseJpa()) {
            imports.add("java.io.Serializable");
            imports.add("jakarta.persistence.*");
        }

        // 检查字段类型需要的导入
        boolean needBigDecimal = false;
        boolean needDate = false;
        boolean needLocalDateTime = false;
        boolean needLocalDate = false;
        boolean needLocalTime = false;

        for (ColumnInfo column : table.getColumns()) {
            String javaType = getJavaType(column.getDataType(), config);
            if ("BigDecimal".equals(javaType)) {
                needBigDecimal = true;
            } else if ("Date".equals(javaType)) {
                needDate = true;
            } else if ("LocalDateTime".equals(javaType)) {
                needLocalDateTime = true;
            } else if ("LocalDate".equals(javaType)) {
                needLocalDate = true;
            } else if ("LocalTime".equals(javaType)) {
                needLocalTime = true;
            }
        }

        if (needBigDecimal) {
            imports.add("java.math.BigDecimal");
        }
        if (needDate) {
            imports.add("java.util.Date");
        }
        if (needLocalDateTime) {
            imports.add("java.time.LocalDateTime");
        }
        if (needLocalDate) {
            imports.add("java.time.LocalDate");
        }
        if (needLocalTime) {
            imports.add("java.time.LocalTime");
        }

        // 添加注解导入
        if (config.isUseLombok()) {
            imports.add("lombok.Data");
            imports.add("lombok.NoArgsConstructor");
            imports.add("lombok.AllArgsConstructor");
            imports.add("lombok.experimental.Accessors");
        }
        if (config.isUseSwagger()) {
            imports.add("io.swagger.annotations.ApiModel");
            imports.add("io.swagger.annotations.ApiModelProperty");
        }

        // 写入import语句
        for (String importStr : imports) {
            cb.append("import ").append(importStr).append(";").newLine();
        }
        cb.newLine();
    }

    /**
     * 生成字段声明
     */
    private void generateField(CodeBuilder cb, ColumnInfo column, TableInfo table, GeneratorConfig config) {
        String fieldName = toCamelCase(column.getColumnName(), false);
        String javaType = getJavaType(column.getDataType(), config);

        // 字段注释
        if (column.getRemarks() != null && !column.getRemarks().isEmpty()) {
            cb.append("    /**").newLine();
            cb.append("     * ").append(column.getRemarks()).newLine();
            cb.append("     */").newLine();
        }

        // Swagger注解
        if (config.isUseSwagger()) {
            cb.append("    @ApiModelProperty(value = \"").append(column.getRemarks() != null ? column.getRemarks() :
                    fieldName).append("\")").newLine();
        }

        // 主键注解
        if (table.getPrimaryKey() != null && table.getPrimaryKey().getColumns().contains(column.getColumnName())) {
            cb.append("    @Id").newLine();

            // 自增主键
            if (table.getPrimaryKey().isAutoIncrement()) {
                cb.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)").newLine();
            } else {
                // 非自增主键
                cb.append("    @GeneratedValue(generator = \"assigned\")").newLine();
            }
        }

        // JPA Column注解
        if (config.isUseJpa()) {
            cb.append("    @Column(");

            boolean hasAttributes = false;

            // name属性
            cb.append("name = \"").append(column.getColumnName()).append("\"");
            hasAttributes = true;

            // nullable属性
            if (!column.isNullable()) {
                if (hasAttributes) cb.append(", ");
                cb.append("nullable = false");
                hasAttributes = true;
            }

            // 对于大文本类型（如longtext），不设置length属性
            if (!isLargeTextType(column.getDataType()) &&
                    !isJsonType(column.getDataType()) &&
                    isStringType(column.getDataType()) &&
                    column.getColumnSize() > 0 &&
                    column.getColumnSize() != 2147483647) {
                if (hasAttributes) cb.append(", ");
                cb.append("length = ").append(String.valueOf(column.getColumnSize()));
                hasAttributes = true;
            }

            // precision和scale属性（数值类型）
            if (isDecimalType(column.getDataType()) && column.getColumnSize() > 0) {
                if (hasAttributes) cb.append(", ");
                cb.append("precision = ").append(String.valueOf(column.getColumnSize()));
                hasAttributes = true;

                if (column.getDecimalDigits() > 0) {
                    cb.append(", scale = ").append(String.valueOf(column.getDecimalDigits()));
                }
            }

            // columnDefinition属性
            if (config.isGenerateColumnDefinition()) {
                String columnDefinition = generateColumnDefinition(column, config);
                if (columnDefinition != null && !columnDefinition.isEmpty()) {
                    if (hasAttributes) cb.append(", ");
                    cb.append("columnDefinition = \"").append(columnDefinition).append("\"");
                    hasAttributes = true;
                }
            }

            // 主键字段通常不可更新
            if (table.getPrimaryKey() != null && table.getPrimaryKey().getColumns().contains(column.getColumnName())) {
                if (hasAttributes) cb.append(", ");
                cb.append("updatable = false");
            }

            cb.append(")");

            // 添加字段注释到注解后面
//            if (column.getRemarks() != null && !column.getRemarks().isEmpty()) {
//                cb.append(" // ").append(column.getRemarks());
//            }

            cb.newLine();
        }

        cb.append("    private ").append(javaType).append(" ").append(fieldName).append(";").newLine().newLine();
    }

    /**
     * 生成columnDefinition
     */
    private String generateColumnDefinition(ColumnInfo column, GeneratorConfig config) {
        StringBuilder definition = new StringBuilder();

        // 处理JSON类型
        if (isJsonType(column.getDataType())) {
            // 对于JSON类型，使用适当的数据库特定类型
            if ("MySQL".equalsIgnoreCase(config.getDatabaseProductName())) {
                definition.append("JSON");
            } else {
                definition.append(column.getDataType().toUpperCase());
            }
        } else {
            // 基本类型
            definition.append(column.getDataType().toUpperCase());

            // 对于大文本类型，不添加长度
            if (!isLargeTextType(column.getDataType()) &&
                    isStringType(column.getDataType()) &&
                    column.getColumnSize() > 0 &&
                    column.getColumnSize() != 2147483647) {
                definition.append("(").append(column.getColumnSize()).append(")");
            } else if (isDecimalType(column.getDataType()) && column.getColumnSize() > 0) {
                definition.append("(").append(column.getColumnSize());
                if (column.getDecimalDigits() > 0) {
                    definition.append(",").append(column.getDecimalDigits());
                }
                definition.append(")");
            } else if (isIntegerType(column.getDataType()) && column.getColumnSize() > 0) {
                // 整数类型需要指定显示宽度
                String dbType = column.getDataType().toLowerCase();
                if (dbType.contains("bigint")) {
                    // BIGINT类型使用20位显示宽度（MySQL标准）
                    definition.append("(20)");
                } else if (dbType.contains("int") && !dbType.contains("tinyint") &&
                        !dbType.contains("smallint") && !dbType.contains("mediumint")) {
                    // 标准int类型使用11位显示宽度
                    definition.append("(11)");
                } else {
                    // 其他整数类型使用实际提取的显示宽度
                    definition.append("(").append(column.getColumnSize()).append(")");
                }
            }
            // 添加字符集和排序规则（对于MySQL）
            if ("MySQL".equalsIgnoreCase(config.getDatabaseProductName()) && isStringType(column.getDataType())) {
                // 这里可以添加字符集和排序规则信息
                // 例如: definition.append(" CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
            }
        }

        // 是否可为空
        if (!column.isNullable()) {
            definition.append(" NOT NULL");
        }
//        else {
//            definition.append("DEFAULT NULL");
//        }

        // 自增
        if (column.isAutoIncrement()) {
            definition.append(" AUTO_INCREMENT");
        }

        // 默认值
        if (column.getDefaultValue() != null && !column.getDefaultValue().isEmpty()) {
            // 处理默认值中的特殊字符
            String defaultValue = escapeSqlString(column.getDefaultValue());
            definition.append(" DEFAULT ").append(defaultValue);
        }

        // 注释 - 添加到columnDefinition中
        if (column.getRemarks() != null && !column.getRemarks().isEmpty()) {
            definition.append(" COMMENT '").append(escapeSqlString(column.getRemarks())).append("'");
        }

        return definition.toString();
    }

    /**
     * 判断是否为大文本类型
     */
    private boolean isLargeTextType(String dbType) {
        if (dbType == null) return false;
        dbType = dbType.toLowerCase();
        return dbType.contains("text") || dbType.contains("blob") || dbType.contains("clob");
    }

    /**
     * 转义SQL字符串中的特殊字符
     */
    private String escapeSqlString(String str) {
        if (str == null) return "";
        return str.replace("'", "''").replace("\\", "\\\\");
    }

    /**
     * 生成getter和setter方法
     */
    private void generateGetterSetter(CodeBuilder cb, ColumnInfo column) {
        String fieldName = toCamelCase(column.getColumnName(), false);
        String javaType = getJavaType(column.getDataType(), new GeneratorConfig("", "", ""));
        String methodSuffix = toCamelCase(column.getColumnName(), true);

        // Getter
        cb.append("    public ").append(javaType).append(" get").append(methodSuffix).append("() {").newLine();
        cb.append("        return ").append(fieldName).append(";").newLine();
        cb.append("    }").newLine().newLine();

        // Setter
        cb.append("    public void set").append(methodSuffix).append("(").append(javaType)
                .append(" ").append(fieldName).append(") {").newLine();
        cb.append("        this.").append(fieldName).append(" = ").append(fieldName).append(";").newLine();
        cb.append("    }").newLine().newLine();
    }

    /**
     * 下划线转驼峰命名
     */
    private String toCamelCase(String name, boolean firstCharUpper) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        StringBuilder result = new StringBuilder();
        String[] parts = name.split("_");

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) continue;

            if (i == 0 && !firstCharUpper) {
                result.append(Character.toLowerCase(part.charAt(0)));
            } else {
                result.append(Character.toUpperCase(part.charAt(0)));
            }

            if (part.length() > 1) {
                result.append(part.substring(1).toLowerCase());
            }
        }

        return result.toString();
    }

    /**
     * 数据库类型转Java类型
     */
    private String getJavaType(String dbType, GeneratorConfig config) {
        if (dbType == null) return "String";

        dbType = dbType.toLowerCase();

        if (dbType.contains("char") || dbType.contains("text") || dbType.contains("enum") || dbType.contains("blob") || dbType.contains("clob")) {
            return "String";
        } else if (dbType.contains("json")) {
            return "String"; // JSON类型通常映射为String，或者可以使用具体的JSON对象类型
        } else if (dbType.contains("bigint")) {
            return "Long"; // 确保bigint映射为Long，必须先于int判断
        } else if (dbType.contains("int") || dbType.contains("integer")) {
            return "Integer";
        } else if (dbType.contains("float")) {
            return "Float";
        } else if (dbType.contains("double")) {
            return "Double";
        } else if (dbType.contains("decimal") || dbType.contains("numeric")) {
            return "BigDecimal";
        } else if (dbType.contains("datetime") || dbType.contains("timestamp")) {
            return config.isUseJava8Date() ? "LocalDateTime" : "Date";
        } else if (dbType.contains("date")) {
            return config.isUseJava8Date() ? "LocalDate" : "Date";
        } else if (dbType.contains("time")) {
            return config.isUseJava8Date() ? "LocalTime" : "Date";
        } else if (dbType.contains("bit") || dbType.contains("boolean")) {
            return "Boolean";
        } else {
            return "String";
        }
    }

    /**
     * 判断是否为字符串类型
     */
    private boolean isStringType(String dbType) {
        if (dbType == null) return false;
        dbType = dbType.toLowerCase();
        return dbType.contains("char") || dbType.contains("text") || dbType.contains("enum") ||
                dbType.contains("blob") || dbType.contains("clob");
    }

    /**
     * 判断是否为JSON类型
     */
    private boolean isJsonType(String dbType) {
        if (dbType == null) return false;
        dbType = dbType.toLowerCase();
        return dbType.contains("json");
    }

    /**
     * 判断是否为小数类型
     */
    private boolean isDecimalType(String dbType) {
        if (dbType == null) return false;
        dbType = dbType.toLowerCase();
        return dbType.contains("decimal") || dbType.contains("numeric") || dbType.contains("float") || dbType.contains("double");
    }

    /**
     * 判断是否为整数类型
     */
    private boolean isIntegerType(String dbType) {
        if (dbType == null) return false;
        dbType = dbType.toLowerCase();
        return dbType.contains("int") || dbType.contains("integer") || dbType.contains("bigint") || dbType.contains(
                "tinyint") || dbType.contains("smallint") || dbType.contains("mediumint");
    }

    /**
     * 代码构建器
     */
    private static class CodeBuilder {
        private StringBuilder sb = new StringBuilder();

        public CodeBuilder append(String text) {
            sb.append(text);
            return this;
        }

        public CodeBuilder newLine() {
            sb.append("\n");
            return this;
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }

    /**
     * 表信息类
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Accessors(chain = true)
    private static class TableInfo {
        private String originalName;
        private String className;
        private String remarks;
        private List<ColumnInfo> columns;
        private PrimaryKeyInfo primaryKey;

    }

    /**
     * 列信息类
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Accessors(chain = true)
    private static class ColumnInfo {
        private String columnName;
        private String dataType;
        private String remarks;
        private boolean nullable;
        private int columnSize;
        private int decimalDigits;
        private String defaultValue;
        private boolean autoIncrement;

    }

    /**
     * 主键信息类
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Accessors(chain = true)
    private static class PrimaryKeyInfo {
        private String name;
        private List<String> columns = new ArrayList<>();
        private boolean autoIncrement;

    }

    /**
     * 使用示例
     */
    public static void main(String[] args) {
        // 创建配置
        GeneratorConfig config = new GeneratorConfig(
                "jdbc:mysql://127.0.0.1:3306/demo",
                "root",
                "123456"
        );

        // 配置参数
        config.setPackageName("com.example.entity");
        config.setOutputPath("src/main/java/com/example/entity");

        // 设置要去除的表前缀
        config.addTablePrefix("sys_");
        config.addTablePrefix("tbl_");
        config.addTablePrefix("app_");

        // 排除系统表
        config.addExcludeTable("schema_migrations");
        config.addExcludeTable("flyway_schema_history");

        // 启用所有高级功能
        config.setUseLombok(true);
        config.setUseJpa(true);
        config.setUseSwagger(true);
        config.setUseJava8Date(true);
        config.setGenerateColumnDefinition(true);

        // 创建生成器并执行
        PojoGenerator generator = new PojoGenerator();
        generator.generateAllPojos(config);
    }
}
