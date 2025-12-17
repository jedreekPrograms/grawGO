package pl.edu.go.model;

/**
 * Konkretna implementacja fabryki ruchów.
 */
public class MoveFactory implements MoveAbstractFactory {

    @Override
    public Move createPlace(Point pos, Color c) {
        return new Move(Move.Type.PLACE, pos, c);
    }

    @Override
    public Move createPass(Color c) {
        return new Move(Move.Type.PASS, null, c);
    }

    @Override
    public Move createResign(Color c) {
        return new Move(Move.Type.RESIGN, null, c);
    }
}
