package com.kongtrolink.email;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dengqg
 */
public class SendMail {

    public static Boolean sendByJavaWebApi(ApiMailInfo info) throws IOException {
        //liuddtodo 后期优化，保存连接，避免每次都重新连接
        String url = info.getUrl();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("apiUser", info.getApiUser()));
        nvps.add(new BasicNameValuePair("apiKey", info.getApiKey()));
        nvps.add(new BasicNameValuePair("from", info.getFrom()));
        nvps.add(new BasicNameValuePair("subject", info.getSubject()));
        nvps.add(new BasicNameValuePair("xsmtpapi", info.getXsmtpapi()));
        nvps.add(new BasicNameValuePair("fromName", info.getFrom_name()));
        nvps.add(new BasicNameValuePair("templateInvokeName", info.getTemplateInvokeName()));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        HttpResponse response = client.execute(httpPost) ;
            String responseStr;
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                responseStr = EntityUtils.toString(response.getEntity());
                if (responseStr.contains("成功")) {
                    return true;
                }
            }

        return false;
    }

    /**
     * 多附件邮件发送（非文件流）
     *
     * @param info
     * @param files
     * @return
     * @throws IOException
     */
    public static Boolean sendWithAttachment(ApiMailInfo info, List<File> files) throws IOException{
        String url = "http://api.sendcloud.net/apiv2/mail/sendtemplate";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        //涉及到附件上传，需要使用 MultipartEntity
        MultipartEntity entity = createBaseMultipartEntity(info);
        entity.addPart("xsmtpapi", new StringBody(info.getXsmtpapi(), Charset.forName("UTF-8")));
        entity.addPart("templateInvokeName", new StringBody(info.getTemplateInvokeName(), Charset.forName("UTF-8")));
        for (File file : files){
            FileBody attachment = new FileBody(file, "application/octet-stream", "UTF-8");
            entity.addPart("attachments", attachment);
        }
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        return judgeResult(response);
    }

    /**
     * @auther: liudd
     * @date: 2018/7/9 13:23
     * 功能描述:发送HTML邮件内容
     */
    public static Boolean sendWithAttachmentHTML(ApiMailInfo info) throws IOException{
        String url = info.getUrl();
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        //涉及到附件上传，需要使用 MultipartEntity
        MultipartEntity entity = createBaseMultipartEntity(info);
        entity.addPart("html", new StringBody(info.getHtml(), Charset.forName("UTF-8")));
        //文件流形式添加附件
        Map<String, InputStream> inputStreamMap = info.getInputStreamMap();
        for (String attachName : inputStreamMap.keySet()){
            InputStream inputStream = inputStreamMap.get(attachName);
            InputStreamBody is = new InputStreamBody(inputStream, attachName);
            entity.addPart("attachments", is);
        }
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);
        return judgeResult(response);
    }

    private static boolean judgeResult(HttpResponse response)throws IOException{
        String responseStr;
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            responseStr = EntityUtils.toString(response.getEntity());
            System.out.print(responseStr);
            if (responseStr.contains("成功")) {
                return true;
            }
        }
        return false;
    }

    private static MultipartEntity createBaseMultipartEntity(ApiMailInfo info)throws IOException{
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName("UTF-8"));
        entity.addPart("apiUser", new StringBody(info.getApiUser(), Charset.forName("UTF-8")));
        entity.addPart("apiKey", new StringBody(info.getApiKey(), Charset.forName("UTF-8")));
        entity.addPart("to", new StringBody(info.getTo(), Charset.forName("UTF-8")));
        entity.addPart("from", new StringBody(info.getFrom(), Charset.forName("UTF-8")));
        entity.addPart("fromName", new StringBody(info.getFrom_name(), Charset.forName("UTF-8")));
        entity.addPart("subject", new StringBody(info.getSubject(), Charset.forName("UTF-8")));
        return entity;
    }
}
