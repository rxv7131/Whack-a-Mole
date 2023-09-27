package common;

import common.WAMException;
import client.gui.Observer;
import client.gui.WhackAMoleBoard;
import client.gui.WhackAMoleClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

/**
 * A JavaFX GUI for the networked What A Mole Game.
 * @author Angela Hudak
 * @author Ryan Vay
 */
public class WAMGUI extends Application implements Observer<WhackAMoleBoard> {

    private Stage stage;
    public boolean created = false;
    public WhackAMoleBoard board;
    public WhackAMoleClient client;
    public Scene scene;
    public GridPane gridPane;
    public Button buttonArray[][];
    public Label labelArray[] = new Label[3];

    /**
     * init method
     */
    public void init()
    {
        List<String> args = getParameters().getRaw();
        String host = args.get(0);
        int port = Integer.parseInt(args.get(1));

        this.board = new WhackAMoleBoard();
        this.board.addObserver(this);
        try
        {
            WhackAMoleClient client = new WhackAMoleClient(host, port, board);
            this.client = client;
        }
        catch(WAMException e)
        {
            throw new RuntimeException();
        }
    }

    public void start(Stage stage)
    {
        this.stage = stage;
        this.client.startListener();
    }

    /**
     *
     */
    public void createGUI()
    {
        GridPane gridPane = new GridPane();
        this.buttonArray = new Button[this.board.columns][this.board.rows];
        int row;
        int column;
        for(row = 0; row < this.board.rows; row++)
        {
            for(column = 0; column < this.board.columns; column++)
            {
                Button button = new Button();
                button.setPrefSize(50, 50);
                Image image = new Image(getClass().getResourceAsStream("p1black.png"));
                button.setGraphic(new ImageView(image));
                int num = column + board.columns * row;
                button.setOnAction(event -> board.whack(num));
                gridPane.add(button, column, row);
                buttonArray[column][row] = button;
            }
        }
        this.gridPane = gridPane;
        Label score = new Label("Score: 0");
        score.setFont(Font.font("Arial", 20));
        labelArray[0] = score;
        Label time = new Label("   Time: ");
        time.setFont(Font.font("Arial", 20));
        labelArray[1] = time;
        Label win = new Label("   ");
        time.setFont(Font.font("Arial", 20));
        labelArray[2] = win;
        HBox hbox = new HBox();
        hbox.getChildren().add(score);
        VBox vbox = new VBox();
        vbox.getChildren().add(hbox);
        vbox.getChildren().add(this.gridPane);
        Scene scene = new Scene(vbox);
        this.stage.setTitle("Whack-a-Mole");
        this.stage.setScene(scene);
        this.stage.show();
        this.created = true;
    }

    /**
     * Gui updates here
     */
    public void refresh()
    {
        int[][] grid = board.board;
        int row;
        int column;
        for(row = 0; row < 3; row++)
        {
            for(column = 0; column < 4; column++)
            {
                if(grid[column][row] == 0)
                {
                    Image newImage = new Image(getClass().getResourceAsStream("p1black.png"));
                    buttonArray[column][row].setGraphic(new ImageView(newImage));
                }
                else if(grid[column][row] == 1)
                {
                    Image newImage = new Image(getClass().getResourceAsStream("p2red.png"));
                    buttonArray[column][row].setGraphic(new ImageView(newImage));
                }
            }
        }
        labelArray[0].setText("Score: " + board.score);
        if(board.won)
        {
            labelArray[2].setText("   You win!");
        }
        else if(board.lost)
        {
            labelArray[2].setText("   You lose!");
        }
        else if(board.tied)
        {
            labelArray[2].setText("   You tied!");
        }
    }

    public void update(WhackAMoleBoard board)
    {
        this.board = board;
        if(!created)
        {
            Platform.runLater(() -> createGUI());
        }
        else
        {
            Platform.runLater(() -> this.refresh());
        }
    }

    public void stop()
    {
        this.client.stop();
    }
}
