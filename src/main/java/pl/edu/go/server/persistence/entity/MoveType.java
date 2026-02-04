package pl.edu.go.server.persistence.entity;

/**
 * Typ ruchu w grze Go.
 * <p>
 * Określa rodzaj akcji wykonanej przez gracza w danym ruchu,
 * zarówno standardowe zagrania na planszy, jak i ruchy specjalne.
 * </p>
 */
public enum MoveType {

    /**
     * Standardowy ruch polegający na postawieniu kamienia
     * na planszy w określonym punkcie.
     */
    MOVE,

    /**
     * Ruch PASS – gracz pomija swoją kolej.
     */
    PASS,

    /**
     * Ruch RESIGN – gracz poddaje grę.
     */
    RESIGN,

    /**
     * Ruch ACCEPT – akceptacja zakończenia gry
     * (np. po propozycji liczenia punktów).
     */
    ACCEPT
}
