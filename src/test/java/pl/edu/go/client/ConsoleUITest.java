//package pl.edu.go.client;
//
//import org.junit.Test;
//import pl.edu.go.model.Board;
//import pl.edu.go.model.Color;
//
//import java.io.ByteArrayInputStream;
//import java.util.Scanner;
//
//import static org.junit.Assert.assertEquals;
//
//public class ConsoleUITest {
//
//    @Test
//    public void shouldReturnMoveCommand() {
//        String input = "3 4\n";
//        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
//        ConsoleUI ui = new ConsoleUI(scanner);
//
//        Board board = new Board(9);
//        String command = ui.getMoveCommand(Color.BLACK, board);
//
//        assertEquals("MOVE 3 4", command);
//    }
//
//    @Test
//    public void shouldReturnPassCommand() {
//        String input = "pass\n";
//        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
//        ConsoleUI ui = new ConsoleUI(scanner);
//
//        String command = ui.getMoveCommand(Color.WHITE, new Board(9));
//
//        assertEquals("PASS", command);
//    }
//
//    @Test
//    public void shouldReturnResignCommand() {
//        String input = "resign\n";
//        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
//        ConsoleUI ui = new ConsoleUI(scanner);
//
//        String command = ui.getMoveCommand(Color.BLACK, new Board(9));
//
//        assertEquals("RESIGN", command);
//    }
//}
