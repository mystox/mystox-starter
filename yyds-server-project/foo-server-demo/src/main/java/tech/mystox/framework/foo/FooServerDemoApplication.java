package tech.mystox.framework.foo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tech.mystox.framework.stereotype.EnableOpera;


@SpringBootApplication
@EnableOpera
public class FooServerDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FooServerDemoApplication.class, args);
	}

}
