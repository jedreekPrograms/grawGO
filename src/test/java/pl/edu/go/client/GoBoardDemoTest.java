package pl.edu.go.client;

import javafx.application.Platform;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Testy stabilności klasy GoBoardDemo.
 *
 * Testujemy WYŁĄCZNIE:
 * - reakcję na komunikaty serwera
 *
 * NIE testujemy:
 * - start(Stage)
 * - Canvas
 * - rysowania
 */
public class GoBoardDemoTest {

    private static boolean fxStarted = false;
    private GoBoardDemo ui;

    @BeforeClass
    public static void initJavaFX() {
        if (!fxStarted) {
            Platform.startup(() -> {});
            fxStarted = true;
        }
    }

    @Before
    public void setUp() {
        ui = new GoBoardDemo();

        // Minimalna inicjalizacja pól używanych w handleServerMessage
        initUiFields();
    }

    private void initUiFields() {
        runFx(() -> {
            ui.turnLabel = new javafx.scene.control.Label();
            ui.colorLabel = new javafx.scene.control.Label();
            ui.passLabel = new javafx.scene.control.Label();
            ui.acceptButton = new javafx.scene.control.Button();
            ui.continueButton = new javafx.scene.control.Button();
            ui.myCapturedLabel = new javafx.scene.control.Label();
            ui.opponentCapturedLabel = new javafx.scene.control.Label();
        });
    }

    // =======================
    // TESTY
    // =======================

    @Test
    public void handleServerMessage_START_doesNotCrash() {
        ui.handleServerMessage("START BLACK");
    }

    @Test
    public void handleServerMessage_STOPPED_doesNotCrash() {
        ui.handleServerMessage("STOPPED");
    }

    @Test
    public void handleServerMessage_PASS_doesNotCrash() {
        ui.handleServerMessage("PASS BLACK");
    }

    @Test
    public void handleServerMessage_GAME_END_doesNotCrash() {
        ui.handleServerMessage("GAME_END 10 5 BLACK");
    }

    @Test
    public void handleServerMessage_RESIGN_doesNotCrash() {
        ui.handleServerMessage("RESIGN WHITE");
    }

    // =======================
    // POMOCNICZE
    // =======================

    private void runFx(Runnable r) {
        try {
            Platform.runLater(r);
            Thread.sleep(30);
        } catch (InterruptedException ignored) {}
    }
}
