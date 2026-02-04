package pl.edu.go.server.persistence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Główna klasa startowa modułu persystencji aplikacji.
 * <p>
 * Odpowiada za uruchomienie kontekstu Spring Boot
 * oraz udostępnienie dostępu do beanów Springa
 * w innych częściach aplikacji.
 * </p>
 */
@SpringBootApplication
@ComponentScan("pl.edu.go.server")
public class PersistenceApplication {

    /**
     * Kontekst aplikacji Spring.
     * Przechowywany statycznie, aby umożliwić dostęp
     * do beanów spoza warstwy Spring.
     */
    private static ConfigurableApplicationContext context;

    /**
     * Uruchamia kontekst Spring Boot, jeśli nie został jeszcze uruchomiony.
     */
    public static void start() {
        if (context == null) {
            context = SpringApplication.run(PersistenceApplication.class);
        }
    }

    /**
     * Zwraca bean z kontekstu Springa na podstawie jego klasy.
     *
     * @param clazz klasa beana
     * @param <T>   typ beana
     * @return instancja beana zarządzanego przez Spring
     */
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }
}
