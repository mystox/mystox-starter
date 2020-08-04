package tech.mystox.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import tech.mystox.framework.stereotype.EnableOpera;

/**
 * Created by mystoxlol on 2019/8/12, 15:32.
 * company: mystox
 * description:
 * update record:
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class})
@EnableOpera
public class SpringbootYydsMqttDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootYydsMqttDemoApplication.class, args);
    }
}
