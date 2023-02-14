package com.yishuifengxiao.common.social.util;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

public class JacksonFromTextHtmlHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    // 添加对text/html的支持
    public JacksonFromTextHtmlHttpMessageConverter() {
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_HTML);
        setSupportedMediaTypes(mediaTypes);
    }

}