package server;

import common.WAMException;
import common.WAMProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Server class that connects a client to the server in order to play the game.
 * @author Angela Hudak
 * @author Ryan Vay
 */
public class WAMServer implements WAMProtocol, Runnable {

    private ServerSocket server;
    private static int rowNum;
    private static int columnNum;
    private static int playerNum;
    private static int seconds;

    /**
     *
     * @param port the port to the server that the client connects to
     * @throws WAMException
     */
    public WAMServer(int port) throws WAMException
    {
        try
        {
            server = new ServerSocket(port);
        }
        catch(IOException e)
        {
            throw new WAMException(e);
        }
    }

    /**
     *
     * @param args
     * @throws WAMException
     */
    public static void main(String args[]) throws WAMException
    {
        if (args.length < 1) {
            System.out.println("Usage: java WAMServer <port>");
            System.exit(1);
        }
        /*
        for(int x = 0; x < args.length; x++)
        {
            System.out.println(args[x]);
        }
        */
        //String hostname = args[0];
        int port = Integer.parseInt(args[0]);
        rowNum = Integer.parseInt(args[1]);
        columnNum = Integer.parseInt(args[2]);
        playerNum = Integer.parseInt(args[3]);
        seconds = Integer.parseInt(args[4]);
        //System.out.println(seconds);
        WAMServer server = new WAMServer(port);
        server.run();
    }

    /**
     *The Server connecting the players to the game
     */

    public void run()
    {
        try
        {
            /*
            System.out.println("Waiting for player one...");
            Socket playerOneSocket = server.accept();
            WhackAMolePlayer playerOne =
                    new WhackAMolePlayer(playerOneSocket);
            playerOne.connectClient();
            System.out.println("Player one connected!");

            System.out.println("Waiting for player two...");
            Socket playerTwoSocket = server.accept();
            WhackAMolePlayer playerTwo =
                    new WhackAMolePlayer(playerTwoSocket);
            playerTwo.connectClient();
            System.out.println("Player two connected!");

            System.out.println("Starting game!");
            WhackAMoleGame game = new WhackAMoleGame(playerOne, playerTwo);
            game.run();
            */
            int playerCount = 0;
            WhackAMolePlayer[] players = new WhackAMolePlayer[playerNum];
            while(playerCount < playerNum)
            {
                int display = playerCount + 1;
                System.out.println("Waiting for player " + display + "...");
                Socket newPlayerSocket = server.accept();
                WhackAMolePlayer newPlayer = new WhackAMolePlayer(newPlayerSocket, rowNum, columnNum);
                players[playerCount] = newPlayer;
                newPlayer.welcome();
                System.out.println("Player " + display + " connected!");
                playerCount++;
            }
            System.out.println("Wait...");
            try {
                TimeUnit.SECONDS.sleep(5);
            }
            catch(InterruptedException e)
            {
                System.out.println("Interrupted");
            }
            System.out.println("Starting game!");
            WhackAMoleGame game = new WhackAMoleGame(players, rowNum, columnNum, seconds);
            game.run();
        }
        catch(IOException e)
        {
            System.err.println("IOException");
            e.printStackTrace();
        }
        catch(WAMException e)
        {
            System.err.println("Failed to create players!");
            e.printStackTrace();
        }
    }
}
