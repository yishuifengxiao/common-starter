package com.yishuifengxiao.common.support.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * ApiInfo数据展开工具类
 * 用于将包含多个路径和HTTP方法的ApiInfo对象展开为多个对象
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ApiInfoExpander {


    /**
     * 对ApiInfo列表进行排序
     * 排序规则：先根据moduleName排序，再根据fullClassName排序，然后根据path排序
     *
     * @param apiInfoList 需要排序的ApiInfo列表
     * @return 排序后的ApiInfo列表
     */
    public static List<ApiInfo> sortApiInfoList(List<ApiInfo> apiInfoList) {
        if (apiInfoList == null || apiInfoList.isEmpty()) {
            return new ArrayList<>();
        }

        List<ApiInfo> sortedList = new ArrayList<>(apiInfoList);

        sortedList.sort((apiInfo1, apiInfo2) -> {
            // 1. 先根据moduleName排序
            String moduleName1 = apiInfo1.getModuleName();
            String moduleName2 = apiInfo2.getModuleName();

            int moduleNameCompare = compareStringsWithNull(moduleName1, moduleName2);
            if (moduleNameCompare != 0) {
                return moduleNameCompare;
            }

            // 2. 再根据fullClassName排序
            String className1 = apiInfo1.getClassName();
            String className2 = apiInfo2.getClassName();

            int classNameCompare = compareStringsWithNull(className1, className2);
            if (classNameCompare != 0) {
                return classNameCompare;
            }

            // 3. 最后根据path排序（取第一个路径进行比较）
            String path1 = getFirstPath(apiInfo1.getPath());
            String path2 = getFirstPath(apiInfo2.getPath());

            int pathCompare = compareStringsWithNull(path1, path2);
            if (pathCompare != 0) {
                return pathCompare;
            }
            String method1 = getFirstPath(apiInfo1.getHttpMethods());
            String method2 = getFirstPath(apiInfo2.getHttpMethods());

            return compareStringsWithNull(method1, method2);
        });

        return sortedList;
    }

    /**
     * 展开并排序ApiInfo列表
     * 先展开数据，然后按照排序规则进行排序
     *
     * @param apiInfoList 原始ApiInfo列表
     * @return 展开并排序后的ApiInfo列表
     */
    public static List<ApiInfo> expandAndSortApiInfoList(List<ApiInfo> apiInfoList) {
        List<ApiInfo> expandedList = expandApiInfoList(apiInfoList);
        return sortApiInfoList(expandedList);
    }

    /**
     * 比较两个字符串，处理null值的情况
     * null值排在非null值之后
     *
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return 比较结果
     */
    private static int compareStringsWithNull(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return 0;
        }
        if (str1 == null) {
            return 1; // str1为null，排在后面
        }
        if (str2 == null) {
            return -1; // str2为null，排在后面
        }
        return str1.compareTo(str2);
    }

    /**
     * 获取路径集合中的第一个路径
     * 如果路径集合为空或为null，返回null
     *
     * @param paths 路径集合
     * @return 第一个路径或null
     */
    private static String getFirstPath(Set<String> paths) {
        if (paths == null || paths.isEmpty()) {
            return null;
        }
        return paths.iterator().next();
    }

    /**
     * 展开ApiInfo链表数据
     * 将包含多个路径和HTTP方法的单个ApiInfo对象展开为多个对象
     * 每个展开后的对象只包含一个路径和一个HTTP方法
     *
     * @param apiInfoList 原始ApiInfo列表
     * @return 展开后的ApiInfo列表
     */
    public static List<ApiInfo> expandApiInfoList(List<ApiInfo> apiInfoList) {
        List<ApiInfo> expandedList = new ArrayList<>();

        for (ApiInfo apiInfo : apiInfoList) {
            Set<String> paths = apiInfo.getPath();
            Set<String> httpMethods = apiInfo.getHttpMethods();

            // 如果路径或HTTP方法为空，跳过该记录
            if (paths == null || paths.isEmpty() || httpMethods == null || httpMethods.isEmpty()) {
                continue;
            }

            // 展开每个路径和HTTP方法的组合
            for (String path : paths) {
                for (String httpMethod : httpMethods) {
                    ApiInfo expandedApiInfo = new ApiInfo();

                    // 复制基本属性
                    expandedApiInfo.setClassName(apiInfo.getClassName());
                    expandedApiInfo.setMethodName(apiInfo.getMethodName());

                    // 设置单个路径和HTTP方法
                    expandedApiInfo.setPath(Set.of(path));
                    expandedApiInfo.setHttpMethods(Set.of(httpMethod));

                    // 复制自定义注解信息
                    expandedApiInfo.setModuleName(apiInfo.getModuleName());
                    expandedApiInfo.setModuleDescription(apiInfo.getModuleDescription());
                    expandedApiInfo.setMethodValue(apiInfo.getMethodValue());
                    expandedApiInfo.setMethodPermission(apiInfo.getMethodPermission());
                    expandedApiInfo.setMethodDescription(apiInfo.getMethodDescription());

                    expandedList.add(expandedApiInfo);
                }
            }
        }

        return expandedList;
    }

    /**
     * 展开单个ApiInfo对象
     * 将包含多个路径和HTTP方法的单个ApiInfo对象展开为多个对象
     * 每个展开后的对象只包含一个路径和一个HTTP方法
     *
     * @param apiInfo 原始ApiInfo对象
     * @return 展开后的ApiInfo列表
     */
    public static List<ApiInfo> expandApiInfo(ApiInfo apiInfo) {
        List<ApiInfo> expandedList = new ArrayList<>();

        Set<String> paths = apiInfo.getPath();
        Set<String> httpMethods = apiInfo.getHttpMethods();

        // 如果路径或HTTP方法为空，返回空列表
        if (paths == null || paths.isEmpty() || httpMethods == null || httpMethods.isEmpty()) {
            return expandedList;
        }

        // 展开每个路径和HTTP方法的组合
        for (String path : paths) {
            for (String httpMethod : httpMethods) {
                ApiInfo expandedApiInfo = new ApiInfo();

                // 复制基本属性
                expandedApiInfo.setClassName(apiInfo.getClassName());
                expandedApiInfo.setMethodName(apiInfo.getMethodName());

                // 设置单个路径和HTTP方法
                expandedApiInfo.setPath(Set.of(path));
                expandedApiInfo.setHttpMethods(Set.of(httpMethod));

                // 复制自定义注解信息
                expandedApiInfo.setModuleName(apiInfo.getModuleName());
                expandedApiInfo.setModuleDescription(apiInfo.getModuleDescription());
                expandedApiInfo.setMethodValue(apiInfo.getMethodValue());
                expandedApiInfo.setMethodPermission(apiInfo.getMethodPermission());
                expandedApiInfo.setMethodDescription(apiInfo.getMethodDescription());

                expandedList.add(expandedApiInfo);
            }
        }

        return expandedList;
    }

    /**
     * 计算展开后的数据条数
     * 用于预估展开操作会产生多少条数据
     *
     * @param apiInfoList 原始ApiInfo列表
     * @return 展开后的数据条数
     */
    public static int calculateExpandedCount(List<ApiInfo> apiInfoList) {
        int totalCount = 0;

        for (ApiInfo apiInfo : apiInfoList) {
            Set<String> paths = apiInfo.getPath();
            Set<String> httpMethods = apiInfo.getHttpMethods();

            if (paths != null && httpMethods != null) {
                totalCount += paths.size() * httpMethods.size();
            }
        }

        return totalCount;
    }

    /**
     * 计算单个ApiInfo对象展开后的数据条数
     *
     * @param apiInfo 原始ApiInfo对象
     * @return 展开后的数据条数
     */
    public static int calculateExpandedCount(ApiInfo apiInfo) {
        Set<String> paths = apiInfo.getPath();
        Set<String> httpMethods = apiInfo.getHttpMethods();

        if (paths == null || httpMethods == null) {
            return 0;
        }

        return paths.size() * httpMethods.size();
    }
}