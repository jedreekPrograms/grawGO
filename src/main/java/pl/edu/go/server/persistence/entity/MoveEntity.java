package pl.edu.go.server.persistence.entity;

import jakarta.persistence.*;
import pl.edu.go.model.Color;

@Entity
public class MoveEntity {

    @Id
    @GeneratedValue
    private Long id;

    private int moveNumber;
    private Integer x;
    private Integer y;

    @Enumerated(EnumType.STRING)
    private Color color;

    @Enumerated(EnumType.STRING)
    private MoveType type;
    @ManyToOne
    private GameEntity game;

    public void setGame(GameEntity g) {
        this.game = g;
    }

    public void setMoveNumber(int nr) {
        this.moveNumber = nr;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    public void setType(MoveType type) {
        this.type = type;
    }

    public MoveType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }
}
