package pl.edu.go.client.networkInterfaces;

/**
 * Interfejs do obsługi wiadomości przychodzących z serwera.
 *
 * <p>
 * Implementacja tej klasy powinna definiować, co zrobić z każdą
 * odebraną wiadomością w kliencie (np. aktualizacja planszy,
 * komunikaty dla gracza, itp.).
 * </p>
 */
public interface MessageListener {

    /**
     * Wywoływane przy każdej wiadomości otrzymanej od serwera.
     *
     * @param message treść wiadomości
     */
    void onMessage(String message);
}
