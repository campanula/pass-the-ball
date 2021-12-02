/*------------------------------------*
 *      SERVER - CLIENT HANDLER       *
 *------------------------------------*/

package Server;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\\
//                                                                                                                                                                                                      \\
//      NOTICE: Commented out writer.println lines are for ease-of-use when using ONLY putty as testing clients for the server. Commenting them back in will break the in-built ClientProgram.          \\
//                                                                                                                                                                                                       \\
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\\

public class ClientHandler implements Runnable {
    private final Socket socket;
    private Game game;

    public int playerId = 0; //Initialise the playerId
    static int clientBallHolder; //Initalise the ballHolder

    //ClientHandler constructor
    public ClientHandler(Socket socket, Game game) {
        this.socket = socket;
        this.game = game;
    }


    //Returns the clientBallHolder variable
    public int getClientBallHolder(){return clientBallHolder;}

    //Prints out a list of players to the ServerProgram terminal
    public void callListOfPlayers(){
        List<Integer> listOfPlayers = game.listOfPlayers(); //grabs the list of players

        System.out.println("There are " + listOfPlayers.size() + " players"); //prints the number of players

        System.out.println("List of players: ");
        // If there are more than 0 players, print the list of players
        if(listOfPlayers.size() > 0) {
            for (Integer playerNum : listOfPlayers) {
                System.out.println("Player " + playerNum);
            }
        }
        // If the size of players is 0, do not try and print the list (to avoid an exception occuring)
        else if(listOfPlayers.size() == 0){
            System.out.println("None");
        }
    }

    @Override
    public void run() {
        try (
                //Setting up the scanner and writer
                Scanner scanner = new Scanner(socket.getInputStream());
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            try {
                //When player joins, assign an ID and print
                playerId = ServerProgram.newPlayer();
                System.out.println("New connection, ID: " + playerId);
                //writer.println("You are player " + playerId);
                writer.println(playerId); //Writing playerId so that the client can read it

                game.playerJoined(playerId); //Add the new player to the players TreeMap

                game.getNewPlayers(playerId); //Add new player to NewPlayerList

                // Call the list of players to the ServerProgram terminal
                callListOfPlayers();

                //If game has just started and nobody has the ball, call firstBall to give someone the ball
                if(!game.ballHeld) {
                    writer.println("STARTED"); //Writing STARTED so that the client can read it
                    game.firstBall(playerId);
                    writer.println(getClientBallHolder());
                } else {
                    writer.println("NOTSTART"); //Writing NOTSTART so that the client can read it
                }

                while (true) {

                    // comment block below is for putty use only
                    /*  if (getClientBallHolder() == playerId) {
                        writer.println("Player " + playerId + ", you are currently holding the ball. Please type passball to pass the ball.");
                    }*/

                    String line = scanner.nextLine(); // Set up nextLine to read user input
                    String[] substrings = line.split(" ");

                    switch (substrings[0].toLowerCase()) {

                        case "passball":
                            //Only run if the current player is the one holding the ball
                            if (getClientBallHolder() == playerId) {
                                writer.println("SUCCESS"); //Writing SUCCESS for the client to read
                                System.out.println("Player " + playerId + " is choosing a player to pass the ball to"); //Writing output to server
                                int toPlayer; //Creating toPlayer

                                List<Integer> listOfPlayers = game.listOfPlayers(); // Calling the list of players

                                //Using do-while loop to handle incorrect input
                                do{
                                    //writer.println("Please choose a player to pass the ball to by typing their number (e.g. 1) . If the player does not exist, you will be prompted again to choose a different player.");
                                    toPlayer = Integer.parseInt(scanner.nextLine()); //Set up nextLine to read user input

                                    // If the player chosen does not exist in the list of players, repeat the loop and wait for input again
                                    if(!listOfPlayers.contains(toPlayer)){
                                        //writer.println("Player does not exist");
                                        writer.println("NOTFOUND"); //Writing NOTFOUND for the client to read
                                    }
                                    //wait for input again if player does not exist
                                } while(!listOfPlayers.contains(toPlayer));

                                game.passBall(toPlayer); // Calling passBall to pass the ball to the new player

                                writer.println("SUCCESS"); //Writing SUCCESS for the client to read
                                writer.println(toPlayer); //Writing the toPlayer variable for the client to read
                                System.out.println("Player " + playerId + " passed the ball to player " + toPlayer); //Writing event output for serverProgram terminal
                                clientBallHolder = toPlayer; // Setting the CH ball holder to the toPlayer variable
                                System.out.println("Player " + clientBallHolder + " is the current ball holder"); //Writing event output for serverProgram terminal
                            }

                            break;

                        case "players":
                            List<Integer> listOfPlayers = game.listOfPlayers();
                            //Print the number of Players
                            writer.println(listOfPlayers.size());
                            //Print players
                            for (Integer playerNum : listOfPlayers) {
                                writer.println(playerNum);
                            }

                            break;

                        case "ballholder":
                            // Call getClientBallHolder to print the current ballHolder
                            writer.println(getClientBallHolder());

                            break;

                        case "newplayers":
                            //Get new players
                            List<Integer> returnNewPlayers = game.returnNewPlayers();
                            //Print the number of Players
                            writer.println(returnNewPlayers.size());
                            //Print players
                            for (Integer playerNum : returnNewPlayers) {
                                writer.println(playerNum);
                            }

                            break;

                        default:
                            throw new Exception("Unknown command: " + substrings[0]); //Catching exceptions
                    }
                }

            } catch (Exception e) { //Catching exceptions
                writer.println("Error: " + e.getMessage());
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Running playerleave event
            game.playerLeft(playerId);
            game.ballPassed();

            // Call the list of players to the server terminal
            callListOfPlayers();

        }

    }

}
