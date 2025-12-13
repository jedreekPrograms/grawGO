package pl.edu.go.client;

import pl.edu.go.model.Board;
import pl.edu.go.model.Color;

import java.util.Scanner;

public class ConsoleUI {

    private final Scanner scanner;

    public ConsoleUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayBoard(Board board) {
        if (board == null) {
            System.out.println("(break planszy)");
            return;
        }

        int size = board.getSize();
        System.out.print("   ");
        for (int x = 0; x < size; x++) {
            System.out.printf("%2d", x);
        }
        System.out.println();

        for (int y = 0; y < size; y++) {
            System.out.printf("%2d ", y);
            for (int x = 0; x < size; x++) {
                Color c = board.get(x, y);
                char symbol;

                switch(c) {
                    case BLACK: symbol = 'B'; break;
                    case WHITE: symbol = 'W'; break;
                    default: symbol = '.'; break;
                }
                System.out.print(symbol + " ");
            }
            System.out.println();
        }
    }

    public String getMoveCommand(Color color, Board board) {
        while(true) {
            System.out.println(color + " move (format: x y, pass, resign): ");
            String line = scanner.nextLine().trim();
            if(line.isEmpty()) continue;

            String lower = line.toLowerCase();
            if (lower.equals("pass")) {
                return "PASS";
            } else if (lower.equals("resign") || lower.equals("resign()")) {
                return "RESIGN";
            } else {
                String[] parts = line.split("\\s+");
                if (parts.length == 2) {
                    try {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        if (board != null && (x < 0 || x >= board.getSize() || y < 0 || y >= board.getSize())) {
                            System.out.println("Współrzędne poza planszą (0.." + (board.getSize() - 1) + ").");
                        } else {
                            return "MOVE " + x + " " + y;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Niepoprawne liczby. Spróbuj ponownie.");
                    }
                } else {
                    System.out.println("Niepoprawny format. Użyj 'x y' lub 'pass' lub 'resign'.");
                }
            }
        }
    }
}
