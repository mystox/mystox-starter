package tech.mystox.framework.foo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import tech.mystox.framework.stereotype.EnableOpera;


@SpringBootApplication(exclude = MultipartAutoConfiguration.class)
@EnableOpera
public class FooServerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FooServerDemoApplication.class, args);
    }

}
