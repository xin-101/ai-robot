package io.github.zh;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {


        /*加载env文件 */
        Dotenv dotenv = Dotenv.configure().load();
        /*把加载的env文件设置环境变量 */
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));


        SpringApplication.run(ClientApplication.class);

    }
}
