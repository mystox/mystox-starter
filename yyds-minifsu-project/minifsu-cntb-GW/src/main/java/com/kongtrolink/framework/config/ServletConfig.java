package com.kongtrolink.framework.config;

import org.apache.axis2.transport.http.AxisServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean cntbServlet() throws FileNotFoundException {
        ServletRegistrationBean cntbServlet = new ServletRegistrationBean();
        cntbServlet.setServlet(new AxisServlet());//这里的AxisServlet就是web.xml中的org.apache.axis2.transport.http.AxisServlet
        cntbServlet.addUrlMappings("/services/*");
        //通过默认路径无法找到services.xml，这里需要指定一下路径，且必须是绝对路径
        System.out.println(ResourceUtils.getFile("FSUService"+File.separator).getAbsolutePath());
        cntbServlet.addInitParameter("axis2.repository.path", ResourceUtils.getFile("AppResource/FSUService"+File.separator).getAbsolutePath());
        cntbServlet.setLoadOnStartup(1);
        return cntbServlet;
    }

}
