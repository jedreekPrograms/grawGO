package pl.edu.go.server.networkInterfaces;

import java.net.Socket;
import pl.edu.go.model.Color;
import pl.edu.go.server.GameSession;

//Interfejs do komunikacji serwera z klientem
public interface ClientConnection {

    // wysyła wiadomość do klienta
    void send(String message);

    //to zmienilem!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // rejestruje listener odbierający wiadomości od klienta
    void setMessageListener(MessageListener listener);

    // zwraca identyfikator klienta lub nazwę
    Socket getSocket();

    // zamknięcie połączenia
    void close();

    void setPartner(ClientConnection partner);

    void setGameSession(GameSession session, Color color);
}
