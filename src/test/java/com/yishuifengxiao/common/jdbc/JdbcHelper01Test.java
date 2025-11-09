package com.yishuifengxiao.common.jdbc;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.Slice;
import com.yishuifengxiao.demo.entity.AutoTable;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JdbcHelper01Test.TestConfig.class)
@Slf4j
public class JdbcHelper01Test {

    @Autowired
    private JdbcHelper jdbcHelper;
    private AutoTable autoTable;

    @Before
    public void before() {
        autoTable = new AutoTable(
                1L,
                "测试",
                18,
                new Date(),
                LocalDateTime.now(),
                LocalDate.now(),
                LocalTime.now(),
                Instant.now(),
                LocalDateTime.now(),
                1,
                1,
                1,
                1.0f,
                1.0d,
                new BigDecimal(1),
                "测试text",
                "测试longtext",
                "测试mediumtext",
                "测试tinytext",
                "测试blob",
                "测试charVal",
                null
        );

    }

    @Test
    public void test_find_01() {
        String sql = "SELECT * FROM auto_table WHERE id = :id and name = :name";
        List<AutoTable> list = jdbcHelper.find(AutoTable.class, sql, new AutoTable().setId(1L).setName("测试"));
        System.out.println(list);
    }

    @Test
    public void test_find_02() {
        String sql = "SELECT * FROM auto_table WHERE id = :id and name = :name";
        List<AutoTable> list = jdbcHelper.find(AutoTable.class, sql, Map.of("id", 1L, "name", "测试"));
        System.out.println(list);
    }

    @Test
    public void test_findPage_01() {
        String sql = "SELECT * FROM auto_table WHERE id = :id and name = :name";
        Page<AutoTable> page = jdbcHelper.findPage(AutoTable.class, Slice.of(10, 1), sql, Map.of("id", 1L, "name", "测试"));
        System.out.println(page);
    }

    @Test
    public void test_find_001() {
        List<AutoTable> list = jdbcHelper.find(AutoTable.class, params -> {
            String sql = "SELECT * FROM auto_table WHERE id = :id and name = :name";
            params.putAll(Map.of("id", 1L, "name", "测试"));
            return sql;
        });
        System.out.println(list);
    }

    @Test
    public void test_find_002() {
        Page<AutoTable> page = jdbcHelper.findPage(AutoTable.class, Slice.of(10, 1), params -> {
            String sql = "SELECT * FROM auto_table WHERE id = :id and name = :name";
            params.putAll(Map.of("id", 1L, "name", "测试"));
            return sql;
        });
        System.out.println(page);
    }


    @Configuration
    static class TestConfig {

        @Bean
        public DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/personkit");
            dataSource.setUsername("root");
            dataSource.setPassword("123456");
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            return dataSource;
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        public JdbcHelper jdbcHelper(JdbcTemplate jdbcTemplate) {
            return new SimpleJdbcHelper(jdbcTemplate);
        }
    }
}
