package pl.edu.go.server.persistence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("pl.edu.go.server")
public class PersistenceApplication {
    private static ConfigurableApplicationContext context;

    public static void start() {
        if (context == null) {
            context = SpringApplication.run(PersistenceApplication.class);
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }
}
