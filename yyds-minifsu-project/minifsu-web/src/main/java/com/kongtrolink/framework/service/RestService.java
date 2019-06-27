package com.kongtrolink.framework.service;


import com.kongtrolink.framework.util.JsonResult;
import org.springframework.http.HttpHeaders;

import java.util.Map;

/**
 * Created by mystoxlol on 2018/7/25, 10:58.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface RestService
{
    JsonResult redirectPost(String url, HttpHeaders headers, Map<String, String[]> parameterMap);

    JsonResult redirectPost(String url, HttpHeaders headers, Map<String, String[]> parameterMap, Object requestBody);
    JsonResult redirectPostJson(String url, HttpHeaders headers, Map<String, String[]> parameterMap, String requestBody);

    JsonResult postInterface(String url, String bodyString, String... variables);

//    public JsonResult postInterface(String url, HttpHeaders httpHeaders, String bodyString, String... variables);

}
