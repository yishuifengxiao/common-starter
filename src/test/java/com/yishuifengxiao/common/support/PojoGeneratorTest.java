package com.yishuifengxiao.common.support;

import org.junit.Test;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
public class PojoGeneratorTest {

    @Test
    public void test() {
        // 创建配置
        PojoGenerator.GeneratorConfig config = new PojoGenerator.GeneratorConfig(
                "jdbc:mysql://127.0.0.1:3306/personkit",
                "root",
                "123456"
        );

        // 配置参数
        config.setPackageName("com.yishuifengxiao.demo.entity");
        config.setOutputPath("src/test/java/com/yishuifengxiao/demo/entity");

        // 设置要去除的表前缀
        config.addTablePrefix("sys_");
        config.addTablePrefix("tbl_");
        config.addTablePrefix("app_");

        // 排除系统表
        config.addExcludeTable("schema_migrations");
        config.addExcludeTable("flyway_schema_history");

        config.addIncludeTable("auto_table");

        // 启用所有高级功能
        config.setUseLombok(true);
        config.setUseJpa(true);
        config.setUseSwagger(false);
        config.setUseJava8Date(true);
        config.setGenerateColumnDefinition(true);

        // 创建生成器并执行
        PojoGenerator generator = new PojoGenerator();
        generator.generateAllPojos(config);
    }
}
