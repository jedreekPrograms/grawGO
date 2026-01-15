package pl.edu.go.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.edu.go.model.Color;

import java.io.IOException;
import java.util.Objects;

public class GoBoardDemo extends Application {

    private static final int BOARD_SIZE = 9;
    private static final double CELL = 52;
    private static final double MARGIN = 36;
    private boolean scoringPhase = false;   // czy jesteśmy po dwóch pasach
    private final boolean[][] deadMarks = new boolean[BOARD_SIZE][BOARD_SIZE];

    private Button acceptButton;
    private Button continueButton;

    private final Color[][] board = new Color[BOARD_SIZE][BOARD_SIZE];

    private Canvas canvas;
    private GraphicsContext gc;
    private GoClient client;

    private Color myColor;
    private boolean myTurn = false;

    private Label turnLabel;
    private Label colorLabel;

    private int myCaptured = 0;
    private int opponentCaptured = 0;
    private Label myCapturedLabel;
    private Label opponentCapturedLabel;
    private Label passLabel;
    private boolean winner;

    @Override
    public void start(Stage stage) throws IOException {

        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                board[i][j] = Color.EMPTY;

        double size = MARGIN * 2 + CELL * (BOARD_SIZE - 1);
        canvas = new Canvas(size, size);
        gc = canvas.getGraphicsContext2D();

        Button passButton = new Button("PASS");
        passButton.getStyleClass().add("pass-button");

        Button resignButton = new Button("RESIGN");
        resignButton.getStyleClass().add("resign-button");

        acceptButton = new Button("ACCEPT");
        acceptButton.getStyleClass().add("accept-button");
        acceptButton.setDisable(true);

        continueButton = new Button("CONTINUE");
        continueButton.getStyleClass().add("continue-button");
        continueButton.setDisable(true);


        turnLabel = new Label("Waiting for opponent...");
        turnLabel.getStyleClass().add("title-label");

        colorLabel = new Label("Your Color: ❓");
        colorLabel.getStyleClass().add("color-label");

        passLabel = new Label("");
        passLabel.getStyleClass().add("status-label");
        passLabel.setVisible(false);

        myCapturedLabel = new Label("You: 0");
        myCapturedLabel.getStyleClass().add("captured-label");

        opponentCapturedLabel = new Label("Opponent: 0");
        opponentCapturedLabel.getStyleClass().add("captured-label");

        VBox capturedBox = new VBox(12, myCapturedLabel, opponentCapturedLabel);
        capturedBox.getStyleClass().addAll("card", "side-panel");

        VBox statusBox = new VBox(6, turnLabel, colorLabel);
        HBox actionsBox = new HBox(10, passButton, resignButton, acceptButton, continueButton);


        HBox infoBox = new HBox(20, statusBox, actionsBox, passLabel);
        infoBox.getStyleClass().add("top-bar");
        infoBox.setAlignment(Pos.CENTER_LEFT);

        passButton.setOnAction(e -> client.sendPass());
        resignButton.setOnAction(e -> client.sendResign());
        acceptButton.setOnAction(e -> client.sendAccept());
        continueButton.setOnAction(e -> client.sendContinue());



        canvas.setOnMouseClicked(e -> {

    int x = (int) ((e.getX() - MARGIN + CELL / 2) / CELL);
    int y = (int) ((e.getY() - MARGIN + CELL / 2) / CELL);

    if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE) return;

    // === TRYB OZNACZANIA MARTWYCH ===
    if (scoringPhase) {
        if (board[x][y] == Color.EMPTY) return;

        client.sendDead(x, y);
        return;
    }

    // === TRYB NORMALNEJ GRY ===
    if (board[x][y] != Color.EMPTY) return;

    client.sendMove(x, y);
    updateTurnLabel();
});

        StackPane centerPane = new StackPane(canvas);
        centerPane.getStyleClass().add("card");

        drawBoard();

        BorderPane root = new BorderPane();
        root.setTop(infoBox);
        root.setCenter(centerPane);
        root.setRight(capturedBox);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass()
                        .getResource("/styles/style.css")).toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Go");
        stage.show();

        client = new GoClient("localhost", 5000, this);
        new Thread(client::run).start();
    }

    /* ===================== SERVER ===================== */

    public void handleServerMessage(String msg) {

        if (msg.startsWith("START")) {
            myColor = Color.valueOf(msg.split(" ")[1]);

            Platform.runLater(() -> {
                if (myColor == Color.BLACK) {
                    colorLabel.setText("Your Color: Czarny");
                    colorLabel.getStyleClass().add("color-black");
                } else {
                    colorLabel.setText("Your Color: Biały");
                    colorLabel.getStyleClass().add("color-white");
                }
            });

            myTurn = myColor == Color.BLACK;
            updateTurnLabel();
            return;
        }

            if (msg.startsWith("BOARD")) {
            handleBoardMessage(msg);
            return;
        }
            if (msg.startsWith("ACCEPTED")) {
            return; // tylko informacyjne
        }
        if (msg.startsWith("GAME_RESUMED")) {
    String[] parts = msg.split(" ");
    Color next = Color.valueOf(parts[1]);
    
    scoringPhase = false;
    acceptButton.setDisable(true);
    continueButton.setDisable(true);
    
    myTurn = (myColor == next);
    updateTurnLabel();
    redraw();
    return;
}

        
        if(msg.startsWith("STOPPED")){
            scoringPhase = true;
            acceptButton.setDisable(false);
    continueButton.setDisable(false);
    updateTurnLabel();
        }

        if (msg.startsWith("MOVE")) {
            String[] p = msg.split(" ");
            Color c = Color.valueOf(p[1]);
            int x = Integer.parseInt(p[2]);
            int y = Integer.parseInt(p[3]);
            int captured = Integer.parseInt(p[4]);

            board[x][y] = c;
            redraw();

            if (c != myColor) {
                myTurn = true;
                opponentCaptured += captured;
                Platform.runLater(() ->
                        opponentCapturedLabel.setText("Opponent: " + opponentCaptured)
                );
            } else {
                myTurn = false;
                myCaptured += captured;
                Platform.runLater(() ->
                        myCapturedLabel.setText("You: " + myCaptured)
                );
            }
            updateTurnLabel();
        }

        if (msg.startsWith("PASS")) {
            Color c = Color.valueOf(msg.split(" ")[1]);
            myTurn = c != myColor;
            updateTurnLabel();
        }

        if (msg.startsWith("RESIGN")) {
            Color c = Color.valueOf(msg.split(" ")[1]);
            winner = c != myColor;
            updateWinLabel();
        }
    }

    private void loadBoard(String s) {
        String[] rows = s.split("/");

        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                char c = rows[y].charAt(x);
                board[x][y] =
                        c == 'B' ? Color.BLACK :
                                c == 'W' ? Color.WHITE :
                                        Color.EMPTY;
            }
        }
    }


    /* ===================== UI ===================== */

    private void updateTurnLabel() {
        Platform.runLater(() -> {
            turnLabel.getStyleClass().removeAll("turn-your", "turn-opponent");
            if (myTurn) {
                turnLabel.setText("Your Turn");
                turnLabel.getStyleClass().add("turn-your");
            } else {
                turnLabel.setText("Opponent's Turn");
                turnLabel.getStyleClass().add("turn-opponent");
            }
        });
    }

    private void updateWinLabel() {
        Platform.runLater(() -> {
            turnLabel.setText("Game Over");
            passLabel.setVisible(true);
            passLabel.getStyleClass().removeAll("win", "lose");
            if (winner) {
                passLabel.setText("YOU WIN 🎉");
                passLabel.getStyleClass().add("win");
            } else {
                passLabel.setText("YOU LOSE 💀");
                passLabel.getStyleClass().add("lose");
            }
        });
    }

    /* ===================== DRAWING ===================== */

    private void drawBoard() {
        // ciepłe, jasne drewno – pasuje do białego UI
        gc.setFill(javafx.scene.paint.Color.web("#E3C58F"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(javafx.scene.paint.Color.web("#2F2F2F"));
        gc.setLineWidth(1);

        for (int i = 0; i < BOARD_SIZE; i++) {
            double p = MARGIN + i * CELL;
            gc.strokeLine(p, MARGIN, p, MARGIN + CELL * (BOARD_SIZE - 1));
            gc.strokeLine(MARGIN, p, MARGIN + CELL * (BOARD_SIZE - 1), p);
        }

        drawStarPoints();
    }

    private void drawStarPoints() {
        int[] hoshi = {2, 4, 6}; // rogi + środek

        gc.setFill(javafx.scene.paint.Color.web("#2F2F2F"));

        for (int x : hoshi) {
            for (int y : hoshi) {
                if ((x == 4 && y == 4) || (x != 4 && y != 4)) {
                    double cx = MARGIN + x * CELL;
                    double cy = MARGIN + y * CELL;
                    gc.fillOval(cx - 3, cy - 3, 6, 6);
                }
            }
        }
    }

    private void redraw() {
        drawBoard();

        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (board[x][y] != Color.EMPTY) {

                    double cx = MARGIN + x * CELL;
                    double cy = MARGIN + y * CELL;

                    // cień
                    gc.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.25));
                    gc.fillOval(cx - 16 + 2, cy - 16 + 2, 32, 32);

                    // kamień
                    gc.setFill(board[x][y] == Color.BLACK
                            ? javafx.scene.paint.Color.web("#1F2937")
                            : javafx.scene.paint.Color.web("#F9FAFB"));

                    gc.fillOval(cx - 16, cy - 16, 32, 32);

                    // === OZNACZENIE MARTWYCH ===
                    if (deadMarks[x][y]) {
                        gc.setStroke(javafx.scene.paint.Color.RED);
                        gc.setLineWidth(3);
                        gc.strokeLine(cx - 12, cy - 12, cx + 12, cy + 12);
                        gc.strokeLine(cx - 12, cy + 12, cx + 12, cy - 12);
                    }

                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void handleBoardMessage(String msg) {

    // reset oznaczeń
    for (int x = 0; x < BOARD_SIZE; x++)
        for (int y = 0; y < BOARD_SIZE; y++)
            deadMarks[x][y] = false;

    String[] parts = msg.split(" ", 4);

    String boardStr = parts[2];
    loadBoard(boardStr);

    //scoringPhase = false;

    if (parts.length == 4 && parts[3].startsWith("DEAD")) {
        scoringPhase = true;

        String[] deadParts = parts[3].split(" ");
        for (int i = 1; i < deadParts.length; i++) {
            String[] xy = deadParts[i].split(",");
            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);
            deadMarks[x][y] = true;
        }
    }

    Platform.runLater(() -> {
        acceptButton.setDisable(!scoringPhase);
        continueButton.setDisable(!scoringPhase);
        redraw();
    });
}


}
