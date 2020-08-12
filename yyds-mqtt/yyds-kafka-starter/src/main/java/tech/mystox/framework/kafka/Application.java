package tech.mystox.framework.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import tech.mystox.framework.kafka.service.KafkaSender;

@SpringBootApplication
public class Application {


    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context
                = new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE)
                .run(args);
        KafkaSender kafkaSender = context.getBean("kafkaSender", KafkaSender.class);
//        context.getBean(Application.class).runDemo(context);
//        context.close();

        kafkaSender.sendToMqtt("abc","11111111111111111");
    }
}
