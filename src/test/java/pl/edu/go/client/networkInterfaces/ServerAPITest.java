package pl.edu.go.client.networkInterfaces;

import org.junit.Test;

import static org.mockito.Mockito.*;

public class ServerAPITest {

    @Test
    public void shouldSendMessage() {
        ServerAPI api = mock(ServerAPI.class);

        api.send("MOVE 1 2");

        verify(api).send("MOVE 1 2");
    }

    @Test
    public void shouldRegisterListener() {
        ServerAPI api = mock(ServerAPI.class);
        MessageListener listener = mock(MessageListener.class);

        api.setMessageListener(listener);

        verify(api).setMessageListener(listener);
    }
}
