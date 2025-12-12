package pl.edu.go.model;

public class BoardBuilder {
    private int size = 19;

    public BoardBuilder size(int size) {
        this.size = size;
        return this;
    }

    public Board build()  {
        return new Board(size);
    }
}
