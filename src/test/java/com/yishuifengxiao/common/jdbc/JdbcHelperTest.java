package com.yishuifengxiao.common.jdbc;

import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.Slice;
import com.yishuifengxiao.demo.entity.AutoTable;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Ignore;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JdbcHelperTest.TestConfig.class)
//@TestPropertySource(properties = {
//        "spring.datasource.url=jdbc:mysql://127.0.0.1:3306/personkit",
//        "spring.datasource.username=root",
//        "spring.datasource.password=123456",
//        "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver"
//})
@Slf4j
public class JdbcHelperTest {

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

        jdbcHelper.saveOrUpdate(autoTable);
    }

    @Test
    public void test_findByPrimaryKey_exists() {
        AutoTable primaryKey = jdbcHelper.findByPrimaryKey(AutoTable.class, 1);
        assertNotNull(primaryKey);
        log.info("查询结果为：{}", primaryKey);
    }

    @Test
    public void test_findByPrimaryKey_not_exists() {
        AutoTable primaryKey = jdbcHelper.findByPrimaryKey(AutoTable.class, System.currentTimeMillis());
        assertNull(primaryKey);
    }

    @Test
    public void test_findAll_like_mode() {
        List<AutoTable> primaryKey = jdbcHelper.findAll(autoTable, true);
        log.info("查询结果为：{}", primaryKey);
    }

    @Test
    public void test_findAll_not_like_mode() {
        List<AutoTable> primaryKey = jdbcHelper.findAll(autoTable, false);
        log.info("查询结果为：{}", primaryKey);
    }

    @Test
    public void test_findAll_like_mode_01() {
        List<AutoTable> primaryKey = jdbcHelper.findAll(new AutoTable().setName(autoTable.getName()).setAge(autoTable.getAge()), true);
        log.info("查询结果为：{}", primaryKey);
    }

    @Test
    public void test_findAll_not_like_mode_01() {
        List<AutoTable> primaryKey = jdbcHelper.findAll(new AutoTable().setName(autoTable.getName()).setAge(autoTable.getAge()), false);
        log.info("查询结果为：{}", primaryKey);
    }


    @Test
    public void test_countAll_like_mode() {
        Long counted = jdbcHelper.countAll(autoTable, true);
        log.info("查询结果为：{}", counted);
    }

    @Test
    public void test_countAll_not_like_mode() {
        Long counted = jdbcHelper.countAll(autoTable, false);
        log.info("查询结果为：{}", counted);
    }

    @Test
    public void test_countAll_like_mode_01() {
        Long counted = jdbcHelper.countAll(new AutoTable().setName(autoTable.getName()).setAge(autoTable.getAge()), true);
        log.info("查询结果为：{}", counted);
    }

    @Test
    public void test_countAll_not_like_mode_01() {
        Long counted = jdbcHelper.countAll(new AutoTable().setName(autoTable.getName()).setAge(autoTable.getAge()), false);
        log.info("查询结果为：{}", counted);
    }

    @Test
    public void test_findOne_like_mode() {
        AutoTable one = jdbcHelper.findOne(autoTable, true);
        log.info("查询结果为：{}", one);
    }

    @Test
    public void test_findOne_like_mode_01() {
        AutoTable one = jdbcHelper.findOne(new AutoTable().setName(autoTable.getName()).setAge(autoTable.getAge()), true);
        log.info("查询结果为：{}", one);
    }


    @Test
    public void test_findOne_not_like_mode() {
        AutoTable one = jdbcHelper.findOne(autoTable, false);
        log.info("查询结果为：{}", one);
    }

    @Test
    public void test_findOne_not_like_mode_01() {
        AutoTable one = jdbcHelper.findOne(new AutoTable().setName(autoTable.getName()).setAge(autoTable.getAge()), false);
        log.info("查询结果为：{}", one);
    }

    @Test
    public void test_findPage_like_mode() {
        Page<AutoTable> page = jdbcHelper.findPage(autoTable, true, new Slice(10, 1));
        log.info("查询结果为：{}", page);
    }

    @Test
    public void test_findPage_like_mode_01() {
        Page<AutoTable> page = jdbcHelper.findPage(new AutoTable().setName(autoTable.getName()).setAge(autoTable.getAge()), true, new Slice(10, 1));
        log.info("查询结果为：{}", page);
    }

    @Test
    public void test_updateByPrimaryKey() {
        AutoTable autoTable = new AutoTable();
        autoTable.setId(1L);
        autoTable.setName("测试更新");
        int updated = jdbcHelper.updateByPrimaryKey(autoTable);
        log.info("更新结果为：{}", updated);
    }

    @Test
    public void test_updateByPrimaryKeySelective() {
        autoTable.setId(1L);
        autoTable.setName("测试更新");
        int updated = jdbcHelper.updateByPrimaryKeySelective(autoTable);
        log.info("更新结果为：{}", updated);
    }

    @Ignore
    @Test
    public void test_deleteByPrimaryKey() {
        int deleted = jdbcHelper.deleteByPrimaryKey(AutoTable.class, 1);
        log.info("删除结果为：{}", deleted);
    }

    @Test
    @Ignore
    public void test_deleteByPrimaryKey_batch() {
        jdbcHelper.insert(new AutoTable().setId(2L).setName("测试删除2"));
        jdbcHelper.insert(new AutoTable().setId(3L).setName("测试删除3"));
        int deleted = jdbcHelper.deleteByPrimaryKey(AutoTable.class, 2, 3);
        log.info("删除结果为：{}", deleted);
    }

    @Test
    public void test_saveAll() {
        jdbcHelper.saveAll(Arrays.asList(new AutoTable().setId(2L).setName("测试批量保存2"),
                new AutoTable().setId(3L).setName("测试批量保存3")));
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
