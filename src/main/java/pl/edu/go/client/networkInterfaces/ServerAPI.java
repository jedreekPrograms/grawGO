package pl.edu.go.client.networkInterfaces;
//Interfejs do komunikacji klienta z serwerem(do dokończenia)
public interface ServerAPI {

    // wysyła wiadomość do serwera
    void send(String message);

    // rejestruje listener odbierający wiadomości z serwera
    void setMessageListener(MessageListener listener);

    // zamknięcie połączenia
    void close();
}
