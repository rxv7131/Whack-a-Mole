package server;

import client.gui.Observer;
import client.gui.WhackAMoleBoard;
import common.WAMException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * The Whack A Mole Game
 * @author Angela Hudak
 * @author Ryan Vay
 */
public class WhackAMoleGame {

    public WhackAMolePlayer[] players;
    public int rows;
    public int columns;
    public int seconds;
    public boolean test;
    public boolean running = true;

    /**
     *Initialize the game.
     */
    public WhackAMoleGame(WhackAMolePlayer[] players, int rowNum, int columnNum, int seconds)
    {
        this.players = players;
        this.rows = rowNum;
        this.columns = columnNum;
        this.seconds = seconds;
    }

    /**
     * This function handles the movement of the moles and time limit of
     * the game, and collects scores and determines a winner when the game
     * is over.
     * @throws IOException
     */
    public void run() throws IOException {
        boolean running = true;
        int row;
        int column;
        int num;
        int count;
        int timer = seconds;
        Random random = new Random();
        while(running)
        {
            try
            {
                TimeUnit.SECONDS.sleep(1);
                for(row = 0; row < this.rows; row++)
                {
                    for(column = 0; column < this.columns; column++)
                    {
                        int up = random.nextInt(2);
                        if(up == 1)
                        {
                            num = column + this.columns * row;
                            for(count = 0; count < players.length; count++)
                            {
                                players[count].moleUp(num);
                            }
                        }
                        else if(up == 0)
                        {
                            num = column + this.columns * row;
                            for(count = 0; count < players.length; count++)
                            {
                                players[count].moleDown(num);
                            }
                        }
                    }
                }
                timer--;
                if(timer == 0)
                {
                    running = false;
                    for(count = 0; count < players.length; count++)
                    {
                        players[count].allDown();
                    }
                }
            }
            catch(InterruptedException e)
            {
                System.out.println("Game has been interrupted!");
                running = false;
            }
        }
        if(players.length == 1)
        {
            players[0].gameWon();
        }
        else {
            int count2;
            int[] scores = new int[players.length];
            for (count2 = 0; count2 < players.length; count2++) {
                System.out.println(count2);
                int score = players[count2].score();
                scores[count2] = score;
            }
            int highest = -99999;
            boolean tie = false;
            for (count2 = 0; count2 < scores.length; count2++) {
                if (scores[count2] > highest) {
                    highest = scores[count2];
                    tie = false;
                } else if (scores[count2] == highest) {
                    tie = true;
                }
            }
            for (count2 = 0; count2 < scores.length; count2++) {
                if (scores[count2] == highest && !tie) {
                    players[count2].gameWon();
                } else if (scores[count2] == highest && tie) {
                    players[count2].gameTied();
                } else {
                    players[count2].gameLost();
                }
            }
            for (count2 = 0; count2 < players.length; count2++) {
                players[count2].close();
            }
        }
    }

}
