package pl.edu.go.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
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

    @Override
    public void start(Stage stage) throws IOException {

        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                board[i][j] = Color.EMPTY;

        double size = MARGIN * 2 + CELL * (BOARD_SIZE - 1);
        canvas = new Canvas(size, size);
        gc = canvas.getGraphicsContext2D();

        canvas.setOnMouseClicked(e -> {
            if (!myTurn) return;

            int x = (int) ((e.getX() - MARGIN + CELL / 2) / CELL);
            int y = (int) ((e.getY() - MARGIN + CELL / 2) / CELL);

            if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE) return;
            if (board[x][y] != Color.EMPTY) return;

            client.sendMove(x, y);
            myTurn = false;
        });

        drawBoard();

        stage.setScene(new Scene(new StackPane(canvas)));
        stage.setTitle("Go");
        stage.show();

        client = new GoClient("localhost", 5000, this);
        new Thread(client::run).start();
    }

    public void handleServerMessage(String msg) {

        if (msg.startsWith("START")) {
            myColor = Color.valueOf(msg.split(" ")[1]);
            System.out.println("Mój kolor: " + myColor);
            return;
        }

        if (msg.equals("YOUR_TURN")) {
            myTurn = true;
            return;
        }

        if (msg.startsWith("MOVE")) {
            String[] p = msg.split(" ");
            Color c = Color.valueOf(p[1]);
            int x = Integer.parseInt(p[2]);
            int y = Integer.parseInt(p[3]);
            board[x][y] = c;
            redraw();
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
