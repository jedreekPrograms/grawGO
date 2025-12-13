package pl.edu.go.model;

public class Move {
    public enum Type {PLACE, PASS, RESIGN}

    private final Type type;
    private final Point pos;
    private final Color color;

    public Move(Type type, Point pos, Color color) {
        this.type = type;
        this.pos = pos;
        this.color = color;
    }

    public Type getType() { return type; }
    public Point getPos() { return pos; }
    public Color getColor() { return color; }

    @Override
    public String toString() {
        switch (type) {
            case PLACE: return color + " ->" + pos;
            case PASS: return color + " passes";
            case RESIGN: return color + " resigns";
            default: return "";
        }
    }


}
