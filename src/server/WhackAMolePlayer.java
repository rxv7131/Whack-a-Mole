package server;

import common.WAMException;
import common.WAMProtocol;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Represents the client side of a WhackAMole game. Establishes a connection
 * with the server and then responds to requests from the server (often by
 * prompting the real user).
 *
 * @author Angela Hudak
 * @author Ryan Vay
 */
public class WhackAMolePlayer implements WAMProtocol, Closeable {
    private Socket socket;
    private Scanner scanner;
    private PrintStream printer;
    private int rowNum;
    private int columnNum;

    /**
     *
     * @param socket
     * @param rowNum
     * @param columnNum
     * @throws WAMException
     */
    public WhackAMolePlayer (Socket socket, int rowNum, int columnNum) throws WAMException {
        this.socket = socket;
        this.rowNum = rowNum;
        this.columnNum = columnNum;
        try
        {
            scanner = new Scanner(socket.getInputStream());
            printer = new PrintStream(socket.getOutputStream());
        }
        catch (IOException e)
        {
            throw new WAMException(e);
        }
    }

    public void welcome()
    {
        printer.println(WELCOME + " " + rowNum + " " + columnNum);
    }

    public void moleUp(int num)
    {
        printer.println(MOLE_UP + " " + num);
    }

    public void moleDown(int num)
    {
        printer.println(MOLE_DOWN + " " + num);
    }

    public int score()
    {
        printer.println(SCORE);
        System.out.println("Sent message");
        String temp = scanner.next();
        int score = Integer.parseInt(temp);
        return score;
    }

    /**
     * Called to send a request to the client because the highest scorer
     * after the time has ended has won the game
     *
     */
    public void gameWon()
    {
        printer.println(GAME_WON);
        printer.println(SCORE);
    }

    /**
     * Called to send a {@link #GAME_LOST} request to the client because the
     * other player's most recent move won the game.
     *
     */
    public void gameLost()
    {
        printer.println(GAME_LOST);
        printer.println(SCORE);
    }

    /**
     * Called to send a game tied request to the client because the
     * game tied.
     */
    public void gameTied()
    {
        printer.println(GAME_TIED);
        printer.println(SCORE);
    }

    /**
     * Called to send an error to the client. This is called if either
     * client has invalidated themselves with a bad response.
     *
     * @param message The error message.
     */
    public void error(String message)
    {
        printer.println(ERROR + " " + message);
    }



    /**
     * Connects client to server
     */
    public void connectClient(){
        printer.println(WELCOME);
    }

    public void allDown()
    {
        printer.println(ALL_DOWN);
    }

    /**
     *Closes the socket to the client
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        try {
            socket.close();
        }
        catch(IOException ignored) {

        }
    }
}
