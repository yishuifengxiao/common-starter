/*
 * Copyright © 2017-2023 Knife4j(xiaoymin@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.yishuifengxiao.common.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.DefaultPropertiesPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @auth <a href="xiaoymin@foxmail.com">xiaoymin@foxmail.com</a>
 * 2022/8/11 21:52
 * @since 4.0.0
 */
@Slf4j
public class SpringEnvironmentPostProcessor implements EnvironmentPostProcessor {

    /**
     * 配置名称key值
     */
    final static String SPRING_MVC_MATCHING_STRATEGY = "spring.mvc.pathmatch.matching-strategy";
    /**
     * 开启swagger增强功能的key
     */
    final static String SWAGGER_SCAN_KEY = "yishuifengxiao.swagger.enable";
    /**
     * 当前策略枚举类全路径
     */
    final static String MATCHING_CLASS_NAME = "org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties.MatchingStrategy";


    /**
     * 处理springfox2.x版本兼容高版本Spring Boot出现空指针的异常的情况，为开发者自动配置matchingStrategy策略(如果开发者没要求的情况下)
     * springfox2 默认使用{@link org.springframework.util.AntPathMatcher}
     *
     * @param environment Spring Boot环境上下文环境对象
     * @param application Spring Boot主程序Application
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        // 首先，判断当前Spring Boot版本
        // 高版本才处理,自2.4.0开始提供MatchingStrategy
        // 判断当前类是否存在
        String enable = environment.getProperty(SWAGGER_SCAN_KEY);
        // 判断是否开启了增强swagger功能
        boolean disable = null != enable && !"false".equalsIgnoreCase(enable.trim());
        log.debug("springfox-post-processor,The activation status of the swagger enhancement function is {}", enable);
        if (disable) {
            return;
        }
        if (ClassUtils.isPresent(MATCHING_CLASS_NAME, ClassUtils.getDefaultClassLoader())) {
            log.debug("Spring Boot Version Getter than 2.4.0,handle MatchingStrategy");
            Map<String, Object> sources = new HashMap<>();
            String matchingStrategy = environment.getProperty(SPRING_MVC_MATCHING_STRATEGY);

            if (matchingStrategy == null || "".equals(matchingStrategy)) {
                // 当前对象为空，给定默认值ant_path_matcher

                // springfox使用的策略是AntPathMatcher
//                sources.put(SPRING_MVC_MATCHING_STRATEGY, "ant_path_matcher");

            }
            sources.put("knife4j.enable", true);
            DefaultPropertiesPropertySource defaultPropertiesPropertySource = new DefaultPropertiesPropertySource(sources);
            // 更新，添加一个默认值
            environment.getPropertySources().addLast(defaultPropertiesPropertySource);
        }
    }
}