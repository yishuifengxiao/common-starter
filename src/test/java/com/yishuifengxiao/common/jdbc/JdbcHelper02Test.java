package com.yishuifengxiao.common.jdbc;

import com.yishuifengxiao.common.jdbc.entity.Order;
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
@SpringBootTest(classes = JdbcHelper02Test.TestConfig.class)
@Slf4j
public class JdbcHelper02Test {

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
    public void test_find_001() {
        String sql = "SELECT * FROM auto_table a WHERE a.create_datetime > :create_datetime";
        List<AutoTable> list = jdbcHelper.find(AutoTable.class, sql, Map.of("create_datetime", LocalDateTime.of(2025, 11, 9, 19, 40, 56)));
        System.out.println(list);
    }

    @Test
    public void test_findOne_01() {
        AutoTable table = jdbcHelper.findOne(new AutoTable().setName("测试"), true, Order.desc("id"), Order.asc("create_datetime"));
        System.out.println(table);
    }

    @Test
    public void test_findAll_01() {
        List<AutoTable> list = jdbcHelper.findAll(new AutoTable().setName("测试"), true, Order.desc("id"), Order.asc("create_datetime"));
        System.out.println(list);
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
