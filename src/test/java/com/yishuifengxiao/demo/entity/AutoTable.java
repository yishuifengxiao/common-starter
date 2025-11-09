package com.yishuifengxiao.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * AutoTable 实体类
 * 对应数据库表: auto_table
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "auto_table")
public class AutoTable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT(20) NOT NULL AUTO_INCREMENT", updatable = false)
    private Long id;

    /**
     * 用户名
     */
    @Column(name = "name", nullable = false, length = 255, columnDefinition = "VARCHAR(255) NOT NULL COMMENT '用户名'")
    private String name;

    /**
     * 年龄
     */
    @Column(name = "age", columnDefinition = "INT(11) NULL COMMENT '年龄'")
    private Integer age;

    /**
     * datetime格式时间
     */
    @Column(name = "create_datetime", columnDefinition = "DATETIME NULL COMMENT 'datetime格式时间'")
    private Date createDatetime;

    /**
     * timestamp格式时间
     */
    @Column(name = "update_timestamp", columnDefinition = "TIMESTAMP NULL COMMENT 'timestamp格式时间'")
    private LocalDateTime updateTimestamp;

    /**
     * date格式时间
     */
    @Column(name = "birth_date", columnDefinition = "DATE NULL COMMENT 'date格式时间'")
    private LocalDate birthDate;

    /**
     * time格式时间
     */
    @Column(name = "start_time", columnDefinition = "TIME NULL COMMENT 'time格式时间'")
    private LocalTime startTime;

    /**
     * instant格式时间
     */
    @Column(name = "instant", columnDefinition = "DATETIME NULL COMMENT 'instant格式时间'")
    private Instant instant;

    /**
     * localdatetime格式时间
     */
    @Column(name = "date", columnDefinition = "DATETIME NULL COMMENT 'localdatetime格式时间'")
    private LocalDateTime date;

    /**
     * tinyint格式数据
     */
    @Column(name = "tinyint", columnDefinition = "TINYINT(3) NULL COMMENT 'tinyint格式数据'")
    private Integer tinyint;

    /**
     * smallint格式数据
     */
    @Column(name = "smallint", columnDefinition = "SMALLINT(5) NULL COMMENT 'smallint格式数据'")
    private Integer smallint;

    /**
     * mediumint格式数据
     */
    @Column(name = "mediumint", columnDefinition = "MEDIUMINT(7) NULL COMMENT 'mediumint格式数据'")
    private Integer mediumint;

    /**
     * float格式数据
     */
    @Column(name = "float_val", precision = 10, scale = 2, columnDefinition = "FLOAT(10,2) NULL COMMENT 'float格式数据'")
    private Float floatVal;

    /**
     * double格式数据
     */
    @Column(name = "double_val", precision = 10, scale = 2, columnDefinition = "DOUBLE(10,2) NULL COMMENT 'double格式数据'")
    private Double doubleVal;

    /**
     * decimal格式数据
     */
    @Column(name = "decimal_val", precision = 10, columnDefinition = "DECIMAL(10) NULL COMMENT 'decimal格式数据'")
    private BigDecimal decimalVal;

    /**
     * text格式文本
     */
    @Column(name = "text", columnDefinition = "TEXT NULL COMMENT 'text格式文本'")
    private String text;

    /**
     * longtext格式文本
     */
    @Column(name = "longtext", columnDefinition = "LONGTEXT NULL COMMENT 'longtext格式文本'")
    private String longtext;

    /**
     * mediumtext格式文本
     */
    @Column(name = "mediumtext", columnDefinition = "MEDIUMTEXT NULL COMMENT 'mediumtext格式文本'")
    private String mediumtext;

    /**
     * tinytext格式文本
     */
    @Column(name = "tinytext", columnDefinition = "TINYTEXT NULL COMMENT 'tinytext格式文本'")
    private String tinytext;

    /**
     * blob格式数据
     */
    @Column(name = "blob", columnDefinition = "BLOB NULL COMMENT 'blob格式数据'")
    private String blob;

    /**
     * char格式数据
     */
    @Column(name = "char_val", length = 255, columnDefinition = "CHAR(255) NULL COMMENT 'char格式数据'")
    private String charVal;

    /**
     * json格式数据
     */
    @Column(name = "json", columnDefinition = "JSON NULL COMMENT 'json格式数据'")
    private String json;

}
