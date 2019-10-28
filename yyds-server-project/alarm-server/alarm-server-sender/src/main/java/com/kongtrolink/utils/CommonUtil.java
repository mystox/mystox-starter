package com.kongtrolink.utils;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.service.MessageService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

public class CommonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    public static Map<String, String> parseXml(String str) {
        Map<String, String> map = new HashMap<>();
        Document document = null;
        try {
            document = DocumentHelper.parseText(str);
        } catch (DocumentException e) {
            LOGGER.error(e.getMessage(), e);
        }
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();

        // 遍历所有子节点以及子节点的子节点
        for (Element e : elementList) {
            map.put(e.getName(), e.getText());
            List<Element> subElementList = e.elements();
            for (Element se : subElementList) {
                map.put(se.getName(), se.getText());
            }
        }
        return map;
    }

    /**
     * 发起https请求并获取结果
     *
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        StringBuilder buffer = new StringBuilder();
        try {

            URL url = new URL(requestUrl);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(requestMethod);

            if ("GET".equalsIgnoreCase(requestMethod)) {
                httpUrlConn.connect();
            }

            // 当有数据需要提交时
            if (null != outputStr) {
                // 注意编码格式，防止中文乱码
                try (OutputStream outputStream = httpUrlConn.getOutputStream()) {
                    // 注意编码格式，防止中文乱码
                    outputStream.write(outputStr.getBytes("UTF-8"));
                }
            }

            try ( // 将返回的输入流转换成字符串
                    InputStream inputStream = httpUrlConn.getInputStream(); InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8"); BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str);
                }
            }
            httpUrlConn.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (ConnectException ex) {
            LOGGER.error(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        return jsonObject;
    }

    public static String getTimestamp() {
        String timeStamp = "";
        String url = "http://sendcloud.sohu.com/smsapi/timestamp/get";
        JSONObject jsonObject = httpRequest(url, "GET", null);
        if (jsonObject != null) {
            JSONObject infoObject = jsonObject.getJSONObject("info");
            timeStamp = infoObject.getString("timestamp");
        }
        return timeStamp;
    }

    /**
     * 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）后，使用 URL 键值对的 格式（即
     * key1=value1&key2=value2…）拼接成字符串
     *
     * @param paraMap 传入参数
     * @param urlEncode value是否进行url编码
     * @return
     */
    public static String FormatBizQueryParaMap(HashMap<String, String> paraMap,
            boolean urlEncode) {

        String buff = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<>(
                    paraMap.entrySet());

            Collections.sort(infoIds,
                    new Comparator<Map.Entry<String, String>>() {
                        @Override
                        public int compare(Map.Entry<String, String> o1,
                                Map.Entry<String, String> o2) {
                            return (o1.getKey()).compareTo(o2.getKey());
                        }
                    });

            for (Map.Entry<String, String> item : infoIds) {
                if (!"".equals(item.getKey())) {

                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");

                    }
                    buff += key + "=" + val + "&";

                }
            }

            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return buff;
    }

    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}
