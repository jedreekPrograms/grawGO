package pl.edu.go.model;

public interface MoveAbstractFactory {
    Move createPlace(Point pos, Color c);
    Move createPass(Color c);
    Move createResign(Color c);
}
