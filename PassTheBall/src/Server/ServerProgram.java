/*------------------------------------*
 *      SERVER - SERVER PROGRAM       *
 *------------------------------------*/

package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerProgram {
    private final static int port = 8888;
    private static final Game game = new Game();
    private static int playerId = 0;

    //Main method
    public static void main(String[] args) {
        RunServer();
    }

    //Creating the server
    private static void RunServer() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Waiting for incoming connections");
            while (true) {
                //Create a new thread for each ClientHandler instance, ran when a player joins the game
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket, game)).start();
            }
            //Catching exceptions
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Creating the playerID function here as it didn't increment properly in ClientHandler
    public static int newPlayer(){
        playerId++;
        return playerId;
    }
}
