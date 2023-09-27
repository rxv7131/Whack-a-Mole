package client.gui;

import common.WAMException;
import common.WAMProtocol;
import server.WhackAMoleGame;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The client side network interface to a WhackAMole game server.
 * Each of the two players in a game gets its own connection to the server.
 * This class represents the controller part of a model-view-controller
 * triumvirate, in that part of its purpose is to forward user actions
 * to the remote server.
 *
 * @author Angela Hudak
 * @author Ryan Vay
 */

public class WhackAMoleClient implements WAMProtocol {

    private static final boolean DEBUG = false;

    private static void dPrint( Object logMsg ) {
        if ( WhackAMoleClient.DEBUG ) {
            System.out.println( logMsg );
        }
    }

    /**
     * The socket the client is connecting to
     */
    private Socket clientSocket;
    /**
     * the scanner to the input
     */
    private Scanner networkIn;
    /**
     * The printstream from the output
     */
    private PrintStream networkOut;
    /**
     * The Model that keeps track of the game
     */
    private WhackAMoleBoard board;
    /**
     * A boolean go command
     */
    private boolean go;
    public int rows;
    public int columns;

    /**
     * Accessor that takes multithreaded access into account
     *
     * @return whether it ok to continue or not
     */
    private synchronized boolean goodToGo() {
        return this.go;
    }

    /**
     * Multithread-safe mutator
     */
    public synchronized void stop() {
        this.go = false;
    }

//    /**
//      Called when the server sends a message saying that
//      gameplay is damaged. Ends the game.
//
//      @param arguments The error message sent from the reversi.server.
//      */
    public void error( String arguments ) {
        WhackAMoleClient.dPrint( '!' + ERROR + ',' + arguments );
        dPrint( "Fatal error: " + arguments );
        //this.board.error( arguments );
        this.stop();
    }


    /**
     * The Client method sets up public
     * @param host the host of the game
     * @param port The port the client would be connecting to
     * @param board The WhackAMole Board
     * @throws WAMException
     */
    public WhackAMoleClient(String host, int port, WhackAMoleBoard board)
            throws WAMException{
        try {
            this.clientSocket = new Socket(host, port);
            this.networkIn = new Scanner(clientSocket.getInputStream());
            this.networkOut = new PrintStream(clientSocket.getOutputStream());
            this.board = board;
            this.go = true;
        }
        catch(IOException e) {
            throw new WAMException(e);
        }

    }

    /**
     *Called from the GUI when it is ready to start receiving messages
     *from the server.
     */
    public void startListener() {
        new Thread(() -> this.run()).start();
    }

    /**
     * Called when the server sends a message saying that the
     * board has been won by this player. Ends the game.
     */
    public void gameWon() {
        WhackAMoleClient.dPrint( '!' + GAME_WON );

        dPrint( "You won! Yay!" );
        this.board.wonGame();
        this.stop();
    }


    /**
     * Called when the server sends a message saying that the
     * game has been won by the other player. Ends the game.
     */
    public void gameLost() {
        WhackAMoleClient.dPrint( '!' + GAME_LOST );
        dPrint( "You lost! Boo!" );
        this.board.lostGame();
        this.stop();
    }

    /**
     * Called when the server sends a message saying that the
     * game is a tie. Ends the game.
     */
    public void gameTied() {
        WhackAMoleClient.dPrint( '!' + GAME_TIED );
        dPrint( "You tied! Meh!" );
        this.board.tiedGame();
        this.stop();
    }

//    /**
//     * This method should be called at the end of the game to
//     * close the client connection.
//     */
//    public void close() {
//        try {
//            this.clientSocket.close();
//        }
//        catch( IOException ioe ) {
//            // squash
//        }
//        this.board.close();
//    }
    /**
     * UI wants to send a whack to the server.
     *
     * @param col the column
     */
    public void sendMove(int col) {
        this.networkOut.println( WHACK + " " + col );
    }

    /**
     * Start the game
     */
    public void startGame()
    {
        this.board.start();
    }

    /**
     *
     * @param arguments
     */
    public void moleUp(String arguments)
    {
        int num = Integer.parseInt(arguments);
        int row = num / this.columns;
        int column = num % this.columns;
        this.board.moleUp(row, column);
    }


    /**
     *
     * @param arguments
     */
    public void moleDown(String arguments)
    {
        int num = Integer.parseInt(arguments);
        int row = num / this.columns;
        int column = num % this.columns;
        this.board.moleDown(row, column);
    }

    public void allDown()
    {
        this.board.allDown();
    }

    /**
     * This method should be called at the end of the game to
     * close the client connection.
     */
    public void close()
    {
        try {
            clientSocket.close();
            networkIn.close();
            networkOut.close();
            System.out.println("Stopped");
        }
        catch(IOException e)
        {
            System.out.println("IOException");
        }
    }

    /**
     * Server gets the score from the certain player
     * @return the score
     */
    public int getScore()
    {
        return board.getScore();
    }

    // In this method, we will call a method in the client, that calls a method in the board, that calls
    // alertObserver(), that calls update(), that creates the GUI.

    /**
     * Run the main client loop. Intended to be started as a separate
     * thread internally. This method is made private so that no one
     * outside will call it or try to start a thread on it.
     */
    private void run() {
        while (this.goodToGo()) {
            try {
                String request = this.networkIn.next();
                String arguments = this.networkIn.nextLine().trim();
                WhackAMoleClient.dPrint("Net message in = \"" + request + '"');
                //System.out.println( "Net message in = \"" + request + '"' );
                switch ( request ) {

                    case WELCOME:
                        String[] num_array = arguments.split(" ");
                        this.rows = Integer.parseInt(num_array[0]);
                        this.columns = Integer.parseInt(num_array[1]);
                        this.board.setup(this.rows, this.columns);
                        WhackAMoleClient.dPrint("Connected to server " + this.clientSocket);
                        break;
                    case MOLE_UP:
                        moleUp(arguments);
                        break;
                    case MOLE_DOWN:
                        moleDown(arguments);
                        break;
                    case SCORE:
                        int score = getScore();
                        this.networkOut.print(score);
                        break;
                    case GAME_WON:
                        gameWon();
                        break;
                    case GAME_LOST:
                        gameLost();
                        break;
                    case GAME_TIED:
                        gameTied();
                        break;
                    case ALL_DOWN:
                        allDown();
                        break;
                    case ERROR:
                        error( arguments );
                        //System.out.println("Error");
                        this.stop();
                        break;
                    default:
                        System.err.println("Unrecognized request: " + request);
                        this.stop();
                        break;
                }
            }
            catch( NoSuchElementException nse ) {
                // Looks like the connection shut down.
                //this.error( "Lost connection to server." );
                this.stop();
            }
            catch( Exception e ) {
                //this.error( e.getMessage() + '?' );
                this.stop();
            }
        }

        this.close();
    }
}
