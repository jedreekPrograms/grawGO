package pl.edu.go.model;

import java.util.*;

public class Board {
    private final int size;
    private final Color[][] grid;

    public Board(int size) {
        this.size = size;
        this.grid = new Color[size][size];

        for(int x = 0; x < size; x++) {
            for(int y = 0; y < size; y++) {
                grid[x][y] = Color.EMPTY;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public Color get(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) return null;
        return  grid[x][y];
    }

    public boolean isEmpty(int x, int y) {
        Color c = get(x, y);
        return c == Color.EMPTY;
    }

    public int placeStone(Color color, int x, int y) {
        if (x < 0 || x>= size || y < 0 || y >= size) return -1;
        if (!isEmpty(x, y)) return -1;

        grid[x][y] = color;

        int totalCaptured = 0;

        Color enemy = color.opponent();

        for (Point p : getAdjacentPoints(x, y)) {
            if (get(p.x, p.y) == enemy) {
                Set<Point> enemyGroup = getGroup(p.x, p.y);
                Set<Point> liberties = getLiberties(enemyGroup);

                if (liberties.isEmpty()) {
                    totalCaptured += removeGroup(enemyGroup);
                }
            }
        }

        Set<Point> myGroup = getGroup(x, y);
        Set<Point> myLiberties = getLiberties(myGroup);

        if (myLiberties.isEmpty() && totalCaptured == 0) {
            grid[x][y] = Color.EMPTY;
            return -1;
        }

        return totalCaptured;

    }

    public int removeGroup(Set<Point> group) {
        for (Point p: group) {
            grid[p.x][p.y] = Color.EMPTY;
        }
        return group.size();
    }

    public Set<Point> getLiberties(Set<Point> group) {
        Set<Point> liberties = new HashSet<>();

        for(Point p : group) {
            for (Point n : getAdjacentPoints(p.x, p.y)) {
                if (isEmpty(n.x, n.y)) {
                    liberties.add(n);
                }
            }
        }
        return liberties;
    }

    public int computeHash() {
        int hash = 1;
        int prime = 31;

        for (int x = 0; x < size; x++) {
            for(int y = 0; y < size; y++) {
                Color c = grid[x][y];

                int val = switch (c) {
                    case EMPTY -> 0;
                    case BLACK -> 1;
                    case WHITE -> 2;
                };

                hash = hash * prime + val;
            }
        }
        return hash;
    }

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

        while(!stack.isEmpty()) {
            Point p = stack.pop();
            for (Point n : getAdjacentPoints(p.x, p.y)) {
                Color c = get(n.x, n.y);
                if (c == color && !group.contains(n)) {
                    group.add(n);
                    stack.push(n);
                }
            }
        }
        return group;
    }



    public List<Point> getAdjacentPoints(int x, int y) {
        List<Point> list = new ArrayList<>();

        if(x > 0) list.add(new Point(x - 1, y));
        if (x < size - 1) list.add(new Point(x + 1, y));
        if (y > 0) list.add(new Point(x, y - 1));
        if (y < size - 1) list.add(new Point(x, y + 1));

        return list;
    }
}
