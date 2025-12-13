package pl.edu.go.server.networkInterfaces;

import java.net.Socket;
import pl.edu.go.model.Color;

//Interfejs do komunikacji serwera z klientem
public interface ClientConnection {

    // wysyła wiadomość do klienta
    void send(String message);

    void setMessageListener(MessageListener listener);

    // zwraca identyfikator klienta lub nazwę
    Socket getSocket();

    // zamknięcie połączenia
    void close();

    void setPartner(ClientConnection partner);

}
