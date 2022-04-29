package tech.mystox.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tech.mystox.framework.stereotype.EnableOpera;

/**
 * Created by mystox on 2021/8/12, 17:51.
 * company:
 * description:
 * update record:
 */
@SpringBootApplication
@EnableOpera
public class ServerDemoApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(ServerDemoApplication.class, args);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.exit(0); //退出程序使用0命令优雅退出
        }
    }

}
