//package pl.edu.go.client;
//
//import org.junit.Before;
//import org.junit.Test;
//import pl.edu.go.client.networkInterfaces.ServerAPI;
//
//import java.lang.reflect.Method;
//
//import static org.mockito.Mockito.mock;
//
//public class GoClientTest {
//
//    private GoClient client;
//
//    @Before
//    public void setup() {
//        ServerAPI api = mock(ServerAPI.class);
//        ConsoleUI ui = mock(ConsoleUI.class);
//
//        // ✅ NIE MA SOCKETA
//        client = new GoClient(api, ui);
//    }
//
//    @Test
//    public void shouldHandleStartMessage() throws Exception {
//        invokeHandle("START BLACK");
//    }
//
//    @Test
//    public void shouldHandleInvalidMoveMessage() throws Exception {
//        invokeHandle("INVALID");
//    }
//
//    @Test
//    public void shouldHandleWinnerMessage() throws Exception {
//        invokeHandle("WINNER WHITE");
//    }
//
//    @Test
//    public void shouldHandleBoardMessage() throws Exception {
//        String msg =
//                "BOARD\n" +
//                        "5\n" +
//                        ".....\n" +
//                        "..B..\n" +
//                        "..W..\n" +
//                        ".....\n" +
//                        ".....";
//
//        invokeHandle(msg);
//    }
//
//    // wywołanie private handleServerMessage()
//    private void invokeHandle(String message) throws Exception {
//        Method m = GoClient.class
//                .getDeclaredMethod("handleServerMessage", String.class);
//        m.setAccessible(true);
//        m.invoke(client, message);
//    }
//}
