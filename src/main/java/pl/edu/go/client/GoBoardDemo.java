package pl.edu.go.client;

import javafx.application.Application;
import javafx.application.Platform;
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

public class GoBoardDemo extends Application {

    private static final int BOARD_SIZE = 9;
    private static final double CELL = 50;
    private static final double MARGIN = 40;

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
        Button resignButton = new Button("RESIGN");
        turnLabel = new Label("Waiting for opponent...");
        turnLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10;");

        colorLabel = new Label("Your Color: ❓");
        colorLabel.setStyle("-fx-font-size: 16px; -fx-padding: 10;");

        passLabel = new Label("Opponent passed");
        passLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10; -fx-text-fill: white;");


        myCapturedLabel = new Label("Your Captured: 0");
        myCapturedLabel.setStyle("-fx-font-size: 16px; -fx-padding: 5;");
        opponentCapturedLabel = new Label("Opponent Captured: 0");
        opponentCapturedLabel.setStyle("-fx-font-size: 16px; -fx-padding: 5;");

        VBox capturedBox = new VBox(10, myCapturedLabel, opponentCapturedLabel);
        capturedBox.setStyle("-fx-padding: 10; -fx-border-width: 1; -fx-border-color: gray;");

        HBox infoBox = new HBox(20, passButton,resignButton, passLabel, colorLabel, turnLabel);

        passButton.setOnAction(e -> {
            client.sendPass();
        });
        resignButton.setOnAction(e->{
            client.sendResign();
        });

        canvas.setOnMouseClicked(e -> {
            if (!myTurn) return;

            int x = (int) ((e.getX() - MARGIN + CELL / 2) / CELL);
            int y = (int) ((e.getY() - MARGIN + CELL / 2) / CELL);

            if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE) return;
            if (board[x][y] != Color.EMPTY) return;

            client.sendMove(x, y);
            myTurn = false;
            updateTurnLabel();
        });

        drawBoard();

        BorderPane root = new BorderPane();
        root.setTop(infoBox);
        root.setCenter(new StackPane(canvas));
        root.setRight(capturedBox);
        
        stage.setScene(new Scene(root));
        stage.setTitle("Go");
        stage.show();

        client = new GoClient("localhost", 5000, this);
        new Thread(client::run).start();
    }

    public void handleServerMessage(String msg) {

        if (msg.startsWith("START")) {
            myColor = Color.valueOf(msg.split(" ")[1]);
            System.out.println("Mój kolor: " + myColor);



            Platform.runLater(() -> {
                if (myColor == Color.BLACK) {
                    colorLabel.setText("Your Color: Czarny"); // czarny ornament
                    colorLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black; -fx-padding: 10;"); // czarny
                } else {
                    colorLabel.setText("Your Color: Biały"); // biały ornament
                    colorLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: silver; -fx-padding: 10;"); // srebrny
                }
            });


            myTurn = (myColor == Color.BLACK);
            updateTurnLabel();
            return;
        }

//        if (msg.equals("YOUR_TURN")) {
//            myTurn = true;
//            updateTurnLabel();
//            return;
//        }

        if (msg.startsWith("MOVE")) {
            String[] p = msg.split(" ");
            Color c = Color.valueOf(p[1]);
            int x = Integer.parseInt(p[2]);
            int y = Integer.parseInt(p[3]);
            int captured = Integer.parseInt(p[4]);

            board[x][y] = c;
            redraw();

            if (c != myColor) {
                myTurn = true; // !!! teraz moja tura
                opponentCaptured += captured; // !!! przeciwnik zebrał moje kamienie
                Platform.runLater(() -> opponentCapturedLabel.setText("Opponent Captured: " + opponentCaptured));
            } else {
                myTurn = false; // !!! po moim ruchu teraz przeciwnik
                myCaptured += captured; // !!! dodajemy zbicia przeciwnika
                Platform.runLater(() -> myCapturedLabel.setText("Your Captured: " + myCaptured));
            } // jeśli przeciwnik wykonał ruch, teraz moja tura
            updateTurnLabel();
            return;
        }

        if (msg.startsWith("BOARD")) {
            String[] parts = msg.split(" ", 3);
            String[] rows = parts[2].split("/");

            for (int y = 0; y < BOARD_SIZE; y++)
                for (int x = 0; x < BOARD_SIZE; x++)
                    board[x][y] =
                            rows[y].charAt(x) == 'B' ? Color.BLACK :
                                    rows[y].charAt(x) == 'W' ? Color.WHITE :
                                            Color.EMPTY;

            redraw();
        }

        if (msg.startsWith("PASS")) {
            String[] p = msg.split(" ");
            Color c = Color.valueOf(p[1]);
            if (c != myColor) {
                myTurn = true;
            } else {
                myTurn = false;
            }
            updatePassLabel();
        }
        if (msg.startsWith("RESIGN")) {
            String[] p = msg.split(" ");
            Color c = Color.valueOf(p[1]);
            if(c == myColor) {
                winner = true;
            } else {
                winner = false;
            }
            updateWinLabel();
        }
    }

    private void updateWinLabel() {
        Platform.runLater(() -> {
            if (winner) {
                passLabel.setText("YOU ARE WINNER!!!!!!!");
                passLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: green; -fx-padding: 10;");
                turnLabel.setText("The end of game");
                turnLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black; -fx-padding: 10;");
            } else {
                passLabel.setText("YOU ARE LOSER!!!!!!!");
                passLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: red; -fx-padding: 10;");
                turnLabel.setText("The end of game");
                turnLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black; -fx-padding: 10;");
            }

        });
    }

    private void updatePassLabel() {
        Platform.runLater(() -> {
            if (myTurn) {
                passLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: red; -fx-padding: 10;");
                turnLabel.setText("Your Turn");
                turnLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: green; -fx-padding: 10;");
            } else {
                turnLabel.setText("Opponent's Turn");
                turnLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: red; -fx-padding: 10;");
            }

        });
    }



    private void updateTurnLabel() {
        Platform.runLater(() -> {
            if (myTurn) {
                passLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 10;");
                turnLabel.setText("Your Turn");
                turnLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: green; -fx-padding: 10;");
            } else {
                passLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 10;");
                turnLabel.setText("Opponent's Turn");
                turnLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: red; -fx-padding: 10;");
            }
        });
    }

    private void drawBoard() {
        gc.setFill(javafx.scene.paint.Color.BURLYWOOD);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(javafx.scene.paint.Color.BLACK);

        for (int i = 0; i < BOARD_SIZE; i++) {
            double p = MARGIN + i * CELL;
            gc.strokeLine(p, MARGIN, p, MARGIN + CELL * (BOARD_SIZE - 1));
            gc.strokeLine(MARGIN, p, MARGIN + CELL * (BOARD_SIZE - 1), p);
        }
    }

    private void redraw() {
        drawBoard();
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (board[x][y] != Color.EMPTY) {
                    gc.setFill(board[x][y] == Color.BLACK ?
                            javafx.scene.paint.Color.BLACK :
                            javafx.scene.paint.Color.WHITE);

                    gc.fillOval(
                            MARGIN + x * CELL - 16,
                            MARGIN + y * CELL - 16,
                            32, 32
                    );
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
