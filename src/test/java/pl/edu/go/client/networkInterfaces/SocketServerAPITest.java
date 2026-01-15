package pl.edu.go.client.networkInterfaces;

import org.junit.After;
import org.junit.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Testy jednostkowe klasy SocketServerAPI.
 *
 * Testujemy:
 * - wysyłanie danych do serwera
 * - odbiór danych przez MessageListener
 *
 * NIE testujemy:
 * - protokołu Go
 * - parsowania wiadomości
 */
public class SocketServerAPITest {

    private ServerSocket server;

    @After
    public void tearDown() throws IOException {
        if (server != null && !server.isClosed()) {
            server.close();
        }
    }

    // ==========================
    // SEND
    // ==========================

    @Test
    public void send_sendsMessageToServer() throws Exception {
        server = new ServerSocket(0); // losowy wolny port
        int port = server.getLocalPort();

        AtomicReference<String> received = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            try (Socket s = server.accept();
                 BufferedReader in = new BufferedReader(
                         new InputStreamReader(s.getInputStream()))) {

                received.set(in.readLine());
                latch.countDown();

            } catch (IOException ignored) {}
        }).start();

        SocketServerAPI api = new SocketServerAPI("localhost", port);
        api.send("HELLO");

        assertEquals(true, latch.await(500, TimeUnit.MILLISECONDS));
        assertEquals("HELLO", received.get());
    }

    // ==========================
    // LISTENER
    // ==========================

    @Test
    public void listener_receivesMessageFromServer() throws Exception {
        server = new ServerSocket(0);
        int port = server.getLocalPort();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> message = new AtomicReference<>();

        SocketServerAPI api = new SocketServerAPI("localhost", port);

        api.setMessageListener(msg -> {
            message.set(msg);
            latch.countDown();
        });

        new Thread(() -> {
            try (Socket s = server.accept();
                 PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {

                out.println("TEST_MSG");

            } catch (IOException ignored) {}
        }).start();

        assertTrue(latch.await(500, TimeUnit.MILLISECONDS));
        assertEquals("TEST_MSG", message.get());
    }


    // ==========================
    // CLOSE
    // ==========================

    @Test
    public void close_closesConnection() throws Exception {
        server = new ServerSocket(0);
        int port = server.getLocalPort();

        new Thread(() -> {
            try {
                server.accept();
            } catch (IOException ignored) {}
        }).start();

        SocketServerAPI api = new SocketServerAPI("localhost", port);

        api.close();

        // brak wyjątku = OK
        assertNotNull(api);
    }
}
