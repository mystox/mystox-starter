package com.kongtrolink.framework.config;

import org.apache.axis2.transport.http.AxisServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Value("${servlet.resourcePath}")
    private String resourcePath;

    @Bean
    public ServletRegistrationBean cntbServlet() {
        ServletRegistrationBean cntbServlet = new ServletRegistrationBean();
        cntbServlet.setServlet(new AxisServlet());//这里的AxisServlet就是web.xml中的org.apache.axis2.transport.http.AxisServlet
        cntbServlet.addUrlMappings("/services/*");

        //通过默认路径无法找到services.xml，这里需要指定一下路径，且必须是绝对路径
        cntbServlet.addInitParameter("axis2.repository.path", resourcePath);
//        cntbServlet.addInitParameter("axis2.repository.path", this.getClass().getResource("/WEB-INF").getPath().toString());
        cntbServlet.setLoadOnStartup(1);
        return cntbServlet;
    }
}
