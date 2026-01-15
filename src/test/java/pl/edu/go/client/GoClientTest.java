package pl.edu.go.client;

import org.junit.Before;
import org.junit.Test;
import pl.edu.go.client.networkInterfaces.ServerAPI;

import static org.mockito.Mockito.*;

/**
 * Testy jednostkowe klasy GoClient.
 * Testujemy WYŁĄCZNIE:
 * - czy klient wysyła poprawne komendy do ServerAPI
 *
 * Nie testujemy:
 * - socketów
 * - JavaFX
 * - realnej komunikacji sieciowej
 */
public class GoClientTest {

    private ServerAPI api;
    private GoBoardDemo ui;
    private GoClient client;

    @Before
    public void setUp() {
        api = mock(ServerAPI.class);
        ui = mock(GoBoardDemo.class);

        // używamy KONSTRUKTORA TESTOWEGO
        client = new GoClient(api, ui);
    }

    @Test
    public void sendMove_sendsCorrectCommand() {
        client.sendMove(3, 4);

        verify(api).send("MOVE 3 4");
    }

    @Test
    public void sendPass_sendsPASS() {
        client.sendPass();

        verify(api).send("PASS");
    }

    @Test
    public void sendResign_sendsRESIGN() {
        client.sendResign();

        verify(api).send("RESIGN");
    }

    @Test
    public void sendDead_sendsCorrectCommand() {
        client.sendDead(1, 2);

        verify(api).send("DEAD 1 2");
    }

    @Test
    public void sendAccept_sendsACCEPT() {
        client.sendAccept();

        verify(api).send("ACCEPT");
    }

    @Test
    public void sendContinue_sendsCONTINUE() {
        client.sendContinue();

        verify(api).send("CONTINUE");
    }
}
