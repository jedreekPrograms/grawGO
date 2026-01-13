package pl.edu.go.model;

import java.util.*;

/**
 * Klasa reprezentująca planszę gry Go.
 * Odpowiada za przechowywanie stanu planszy
 * oraz logikę stawiania i zbijania kamieni.
 */
public class Board {

    /** Rozmiar planszy. */
    private final int size;

    /** Dwuwymiarowa tablica przechowująca stan pól. */
    private final Color[][] grid;
    int totalCaptured;

    /**
     * Tworzy planszę o podanym rozmiarze.
     *
     * @param size rozmiar planszy
     */
    public Board(int size) {
        this.size = size;
        this.grid = new Color[size][size];

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                grid[x][y] = Color.EMPTY;
            }
        }
    }
    //Konstruktor kopiujacy stan planszy do symulowania KO
    public Board(Board other) {
        this.size = other.size;
        this.totalCaptured = other.totalCaptured;
        this.grid = new Color[size][size];
        for (int x = 0; x < size; x++) {
            System.arraycopy(other.grid[x], 0, this.grid[x], 0, size);
        }
    }

    public int getTotalCaptured() {
        return totalCaptured;
    }

    /** @return rozmiar planszy */
    public int getSize() {
        return size;
    }

    /**
     * Zwraca kolor pola o podanych współrzędnych.
     *
     * @param x współrzędna x
     * @param y współrzędna y
     * @return kolor pola lub null jeśli poza planszą
     */
    public Color get(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) return null;
        return grid[x][y];
    }

    /**
     * Sprawdza, czy pole jest puste.
     *
     * @param x współrzędna x
     * @param y współrzędna y
     * @return true jeśli pole jest puste
     */
    public boolean isEmpty(int x, int y) {
        Color c = get(x, y);
        return c == Color.EMPTY;
    }

    /**
     * Próbuje postawić kamień na planszy.
     *
     * @param color kolor kamienia
     * @param x współrzędna x
     * @param y współrzędna y
     * @return liczba zbitych kamieni lub -1 jeśli ruch nielegalny
     */
    public int placeStone(Color color, int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) return -1;
        if (!isEmpty(x, y)) return -1;

        grid[x][y] = color;
        totalCaptured = 0;
        Color enemy = color.opponent();
        Set<Point> capturedStones = new HashSet<>();
        
        for (Point p : getAdjacentPoints(x, y)) {
            if (get(p.x, p.y) == enemy) {
                Set<Point> enemyGroup = getGroup(p.x, p.y);
                if (getLiberties(enemyGroup).isEmpty()) {
                    capturedStones.addAll(enemyGroup);
                }
            }
        }

        totalCaptured = removeGroup(capturedStones);

        if (totalCaptured == 0) {
            Set<Point> myGroup = getGroup(x, y);
            if (getLiberties(myGroup).isEmpty()) {
                // Ruch samobójczy bez bicia - cofamy i zwracamy błąd
                grid[x][y] = Color.EMPTY;
                return -1;
            }
        }

        return totalCaptured;
    }


    /**
     * Usuwa grupę kamieni z planszy.
     *
     * @param group grupa kamieni
     * @return liczba usuniętych kamieni
     */
    public int removeGroup(Set<Point> group) {
        for (Point p : group) {
            grid[p.x][p.y] = Color.EMPTY;
        }
        return group.size();
    }

    /**
     * Zwraca zbiór oddechów dla danej grupy.
     *
     * @param group grupa kamieni
     * @return zbiór pól będących oddechami
     */
    public Set<Point> getLiberties(Set<Point> group) {
        Set<Point> liberties = new HashSet<>();

        for (Point p : group) {
            for (Point n : getAdjacentPoints(p.x, p.y)) {
                if (isEmpty(n.x, n.y)) {
                    liberties.add(n);
                }
            }
        }
        return liberties;
    }

    /**
     * Oblicza hash aktualnego stanu planszy.
     *
     * @return hash planszy
     */
    public int computeHash() {
        int hash = 1;
        int prime = 31;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int val = switch (grid[x][y]) {
                    case EMPTY -> 0;
                    case BLACK -> 1;
                    case WHITE -> 2;
                };
                hash = hash * prime + val;
            }
        }
        return hash;
    }

    /**
     * Zwraca grupę połączonych kamieni tego samego koloru.
     *
     * @param x współrzędna startowa
     * @param y współrzędna startowa
     * @return zbiór punktów należących do grupy
     */
    public Set<Point> getGroup(int x, int y) {
        Set<Point> group = new HashSet<>();
        Color color = get(x, y);

        if (color == null || color == Color.EMPTY) {
            return group;
        }

        Deque<Point> stack = new ArrayDeque<>();
        Point start = new Point(x, y);
        stack.push(start);
        group.add(start);

        while (!stack.isEmpty()) {
            Point p = stack.pop();
            for (Point n : getAdjacentPoints(p.x, p.y)) {
                if (get(n.x, n.y) == color && !group.contains(n)) {
                    group.add(n);
                    stack.push(n);
                }
            }
        }
        return group;
    }

    /**
     * Zwraca listę sąsiednich punktów dla danego pola.
     *
     * @param x współrzędna x
     * @param y współrzędna y
     * @return lista sąsiadów
     */
    public List<Point> getAdjacentPoints(int x, int y) {
        List<Point> list = new ArrayList<>();

        if (x > 0) list.add(new Point(x - 1, y));
        if (x < size - 1) list.add(new Point(x + 1, y));
        if (y > 0) list.add(new Point(x, y - 1));
        if (y < size - 1) list.add(new Point(x, y + 1));

        return list;
    }

    public String toSingleLineString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Color c = get(x, y);
                sb.append(c == Color.BLACK ? 'B' :
                        c == Color.WHITE ? 'W' : '.');
            }
            if (y < size - 1) sb.append('/');
        }
        return sb.toString();
    }

}
