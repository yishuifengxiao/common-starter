package com.yishuifengxiao.common.support.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class ApiInfo implements Serializable {
    // 基本路径信息
    private Set<String> path;
    private Set<String> httpMethods;
    private String methodName;
    private String className;

    // 自定义注解信息
    private String moduleName;
    private String moduleDescription;
    private String methodValue;
    private String methodPermission;
    private String methodDescription;


}