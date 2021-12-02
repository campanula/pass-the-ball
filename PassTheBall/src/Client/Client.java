/*------------------------------------*
 *          CLIENT - CLIENT           *
 *------------------------------------*/

package Client;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class Client implements AutoCloseable {
    final int port = 8888; // Setting the port number

    // Creating the reader and writer
    private final Scanner reader;
    private final PrintWriter writer;

    public boolean choiceTyped = false; //Set choiceTyped to false initially

    private Date newPlayerTime;

    public Client() throws Exception {
        // Connecting to the server and creating objects for communications
        Socket socket = new Socket("localhost", port);
        reader = new Scanner(socket.getInputStream());

        // Automatically flushes the stream with every command
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    // Get the playerId from the ClientHandler output and return it to the client
    public int getPlayerId(){
        int playerId = reader.nextInt();

        return playerId;
    }

    // Get the current ballHolder from the ClientHandler output and return it to the client
    public int getBallHolder(){
        int ballHolder = reader.nextInt();

        return ballHolder;
    }

    // Function to get the first ball holder into the client if the game has just started
    public int getStartHolder(){
        int ballHolder = 0; //Initialising ballHolder

        //Taking blank input to avoid error
        String blank = reader.nextLine();

        //Reading "STARTED"
        String started = reader.nextLine();

        // If the game has just started, print the first ball holder
        if (started.trim().compareToIgnoreCase("STARTED") == 0) {
            ballHolder = reader.nextInt();
        }

        return ballHolder;
    }

    // Get a list of players from the ClientHandler output
    public int[] getPlayers() {
        // Writing to the server
        writer.println("players");

        // Reading the number of players
        int line = reader.nextInt();

        int numberOfPlayers = line; //Set the read value to be the numberOfPlayers variable
        System.out.println("There are " + numberOfPlayers + " players");

        // For the amount of players there are, read the number of players
        int[] players = new int[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {

            String scan = reader.nextLine();
            line = reader.nextInt();
            players[i] = line;
        }
        System.out.println("-");
        return players; // Return the players
    }

    // Get a list of players from the ClientHandler output
    public int[] getNewPlayers() {
        // Writing to the server
        writer.println("newplayers");

        //Get time
        newPlayerTime = new Date();

        // Reading the number of new players
        int line = reader.nextInt();

        int numberOfPlayers = line; //Set the read value to be the numberOfPlayers variable

        // For the amount of new players there are, read the number of players
        int[] players = new int[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {

            String scan = reader.nextLine();
            line = reader.nextInt();
                players[i] = line;
        }

        return players; // Return the new players
    }

    public void passBall1(){
        //Writing to the server
        writer.println("passball");

        //Making sure choiceTyped is set to false initially
        choiceTyped = false;

        //accounting for blank line
        String blank = reader.nextLine();

        //Reading "SUCCESS"
        String success = reader.nextLine();

        //Tell player they cannot pass the ball if not the ball holder (just in case)
        if (success.trim().compareToIgnoreCase("SUCCESS") != 0) {
            System.out.println("You cannot currently pass the ball");
        }
    }

    public void passBall2(int ballChoice){
        //Writing to the server
        writer.println(ballChoice);

        //Reading the next line
        String outcome = reader.nextLine();

        //If the next line is "SUCCESS"
        if (outcome.trim().compareToIgnoreCase("success") == 0) {
            System.out.println("You passed the ball.\n-----");
            choiceTyped = true; //Set choiceTyped to true so the loop in ClientProgram stops
        }

        //Else, if the next line is "NOTFOUND"
        //Request the player to try again (in ClientProgram) instead
        if (outcome.trim().compareToIgnoreCase("NOTFOUND") == 0) {
            System.out.println("Player does not exist.");
        }

    }

    // Getter to get the choice typed
    public boolean getChoiceTyped(){ return choiceTyped;}

    // Method to check which player is currently holding the ball
    public int checkBallHolder(){
        //Writing to the server
        writer.println("ballholder");

        int ballHolder = reader.nextInt();

        return ballHolder; //Return the current ball holder

    }

    public Date getNewPlayerTime(){
        return newPlayerTime;
    }

    @Override
    public void close() {
        //Close the reader and writer
        reader.close();
        writer.close();
    }
}