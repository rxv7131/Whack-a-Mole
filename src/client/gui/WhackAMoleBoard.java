package client.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * The What a Mole board methods and process of what the board will do
 * @author Angela Hudak
 * @author Ryan Vay
 */
public class WhackAMoleBoard {
    private List<Observer<WhackAMoleBoard>> observers;
    public int[][] board;
    public int rows;
    public int columns;
    public int score = 0;
    public boolean won = false;
    public boolean lost = false;
    public boolean tied = false;
    public boolean ended = false;

    /**
     * The WAM Board creates the observers within the board to be called
     * upon to manipulate the function of the board
     */
    public WhackAMoleBoard()
    {
        this.observers = new LinkedList<>();
    }

    /**
     * The view calls this method to add themselves as an observer of the model.
     * @param observer The observer from the array list of observers
     */
    public void addObserver(Observer<WhackAMoleBoard> observer)
    {
        this.observers.add(observer);
    }

    /**
     * when the model changes, the observers are notified via their update() method
     */
    private void alertObservers() {
        for (Observer<WhackAMoleBoard> obs: this.observers ) {
            obs.update(this);
        }
    }

    /**
     * Alert the observers of when the game starts
     */
    public void start()
    {
        alertObservers();
    }

    /**
     * The start of the board has no moles, and then alerts
     * observers that the game is about to begin
     * @param rows The number of rows
     * @param columns The number of columns
     */
    public void setup(int rows, int columns)
    {
        this.rows = rows;
        this.columns = columns;
        this.board = new int[columns][rows];
        int rowNum;
        int colNum;
        for(rowNum = 0; rowNum < this.rows; rowNum++)
        {
            for(colNum = 0; colNum < this.columns; colNum++)
            {
                //The value of the columns and rows are 0
                // and thus the board only has mole downs
                this.board[colNum][rowNum] = 0;
            }
        }
        alertObservers();
    }

    /**
     * Gives a value for the placement of that mole on the board
     * @param row the row the mole is in
     * @param column the column the mole is in
     */
    public void moleUp(int row, int column)
    {
        board[column][row] = 1;
        alertObservers();
    }

    /**
     * The value for when a mole is not on particular spot on the board
     * @param row the row the mole is not in
     * @param column the column the mole is not in
     */
    public void moleDown(int row, int column)
    {
        board[column][row] = 0;
        alertObservers();
    }

    public void allDown()
    {
        ended = true;
        for(int row = 0; row < rows; row++)
        {
            for(int column = 0; column < columns; column++)
            {
                board[column][row] = 0;
            }
        }
        alertObservers();
    }

    /**
     * When a player presses a button on the view,
     * it either is a mole and changes the image and adds the score
     * or is a down mole and keeps the image and subtracts from the score.
     * @param num the placement of the whack
     */
    public void whack(int num)
    {
        int row = num / columns;
        int column = num % columns;
        //if the mole is up, add to the score and change the image to a down mole
        if(board[column][row] == 1)
        {
            this.board[column][row] = 0;
            this.score += 1;
        }
        //if the mole is down, subtract from the score and keep the image
        else if(this.board[column][row] == 0 && !ended){
            this.board[column][row] = 0;
            this.score -= 1;
        }
        alertObservers();
    }

    /**
     * Called when the game has been won by this player.
     */
    public void wonGame()
    {
        this.won = true;
        this.lost = false;
        this.tied = false;
        alertObservers();
    }
    /**
     * Called when the game has been won by the other player.
     */
    public void lostGame()
    {
        this.won = false;
        this.lost = true;
        this.tied = false;
        alertObservers();
    }
    /**
     * Called when the game has been tied.
     */
    public void tiedGame()
    {
        this.won = false;
        this.lost = true;
        this.tied = false;
        alertObservers();
    }

    /**
     * this is used in the server to measure the scores of the different players to determine the winner and/or losers
     * @return the score
     */
    public int getScore()
    {
        return this.score;
    }
}
