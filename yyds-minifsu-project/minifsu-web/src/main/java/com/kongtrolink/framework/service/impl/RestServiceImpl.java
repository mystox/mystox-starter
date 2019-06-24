package com.kongtrolink.framework.service.impl;

import com.kongtrolink.framework.service.RestService;
import com.kongtrolink.framework.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by mystoxlol on 2018/7/25, 10:58.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class RestServiceImpl implements RestService {
    @Autowired
    RestTemplate restTemplate;
    @Value("${nginx.host:}")
    private String host;
    /**
     * 转发controller http请求
     *
     * @param url
     * @param headers
     * @param parameterMap
     * @return
     */
    @Override
    public JsonResult redirectPost(String url, HttpHeaders headers, Map<String, String[]> parameterMap)
    {
        return restTemplate.postForObject(url, /*携带header*/new HttpEntity<String>(headers), JsonResult.class, /*携带parameter*/parameterMap);
    }

    @Override
    public JsonResult redirectPost(String url, HttpHeaders headers, Map<String, String[]> parameterMap, Object requestBody)
    {
        return restTemplate.postForObject(url, /*携带header*/new HttpEntity<String>(headers), JsonResult.class, /*携带parameter*/parameterMap, requestBody);
    }

    @Override
    public JsonResult redirectPostJson(String url, HttpHeaders headers, Map<String, String[]> parameterMap, String requestBody)
    {
        return null;
    }

    @Override
    public JsonResult postInterface(String url, String bodyString, String... urlVariables)
    {
        url = "http://" + host + "/proxy_ap" + url;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(bodyString, httpHeaders);
        JsonResult result = null;
        try {
            result = restTemplate.postForObject(url, entity, JsonResult.class, urlVariables);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
