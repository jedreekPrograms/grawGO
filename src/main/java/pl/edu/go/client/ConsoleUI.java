package pl.edu.go.client;

import pl.edu.go.model.Board;
import pl.edu.go.model.Color;

import java.util.Scanner;

/**
 * Klasa reprezentująca konsolowy interfejs użytkownika dla gry Go.
 *
 * <p>
 * ConsoleUI odpowiada za wyświetlanie planszy w konsoli oraz pobieranie ruchów
 * od gracza w formacie tekstowym. Nie zawiera logiki gry – służy wyłącznie do
 * interakcji z użytkownikiem.
 * </p>
 */
public class ConsoleUI {

    /** Obiekt Scanner służący do odczytu danych od użytkownika. */
    private final Scanner scanner;

    /**
     * Tworzy interfejs konsolowy korzystający z podanego Scannera.
     *
     * @param scanner obiekt Scanner używany do wczytywania danych od gracza
     */
    public ConsoleUI(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Wyświetla aktualny stan planszy w konsoli.
     *
     * <p>
     * Plansza jest wyświetlana w formie siatki z numerami wierszy i kolumn.
     * Pola oznaczone są literami:
     * <ul>
     *     <li>B – czarny kamień</li>
     *     <li>W – biały kamień</li>
     *     <li>. – puste pole</li>
     * </ul>
     * </p>
     *
     * @param board plansza do wyświetlenia; jeśli null, wyświetlany jest komunikat "(break planszy)"
     */
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

    /**
     * Pobiera ruch od gracza w formie tekstowej.
     *
     * <p>
     * Gracz może wpisać:
     * <ul>
     *     <li>'x y' – numer kolumny i wiersza ruchu</li>
     *     <li>'pass' – przekazanie tury</li>
     *     <li>'resign' – rezygnacja z gry</li>
     * </ul>
     * Metoda weryfikuje poprawność formatu i zakres współrzędnych w obrębie planszy.
     * </p>
     *
     * @param color kolor gracza wykonującego ruch
     * @param board plansza, do której odnoszą się współrzędne ruchu
     * @return polecenie ruchu w formacie tekstowym: "MOVE x y", "PASS" lub "RESIGN"
     */
//    public String getMoveCommand(Color color, Board board) {
//        while(true) {
//            System.out.println(color + " move (format: x y, pass, resign): ");
//            String line = scanner.nextLine().trim();
//            if(line.isEmpty()) continue;
//
//            String lower = line.toLowerCase();
//            if (lower.equals("pass")) {
//                return "PASS";
//            } else if (lower.equals("resign") || lower.equals("resign()")) {
//                return "RESIGN";
//            } else {
//                String[] parts = line.split("\\s+");
//                if (parts.length == 2) {
//                    try {
//                        int x = Integer.parseInt(parts[0]);
//                        int y = Integer.parseInt(parts[1]);
//                        if (board != null && (x < 0 || x >= board.getSize() || y < 0 || y >= board.getSize())) {
//                            System.out.println("Współrzędne poza planszą (0.." + (board.getSize() - 1) + ").");
//                        } else {
//                            return "MOVE " + x + " " + y;
//                        }
//                    } catch (NumberFormatException e) {
//                        System.out.println("Niepoprawne liczby. Spróbuj ponownie.");
//                    }
//                } else {
//                    System.out.println("Niepoprawny format. Użyj 'x y' lub 'pass' lub 'resign'.");
//                }
//            }
//        }
//    }
}
