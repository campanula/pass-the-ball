/*------------------------------------*
 *      CLIENT - CLIENT PROGRAM       *
 *------------------------------------*/

package Client;

import java.util.Date;
import java.util.Scanner;

public class ClientProgram {

    public static void main(String[] args) {

        try {
            Scanner in = new Scanner(System.in);
            int playerId;
            int ballHolder = 0;

            try (Client client = new Client()) {
                //Read playerID from the server and assign
                playerId = client.getPlayerId();

                //Print the client start message
                System.out.println("------------------------------------------------\n" +
                        "|                                              |\n" +
                        "|                 PASS THE BALL                |\n" +
                        "|                                              |\n" +
                        "------------------------------------------------");
                System.out.println("Logged in successfully.\n-----");
                System.out.println("You are player " + playerId);

                // If the ballHolder is not assigned, check if the game has just started
                if (ballHolder == 0) {
                    ballHolder = client.getStartHolder();
                }

                while (true) {
                    //Set the options back to 0 at the start of the loop
                    int chooseOption = 0;
                    int choice = 0;

                    //Choose Option menu
                    while (chooseOption == 0) { //Only run when chooseOption is 0 to stop looping
                        System.out.println("------------------------------------------------\n" +
                                "|                                              |\n" +
                                "|                    OPTIONS                   |\n" +
                                "|                                              |\n" +
                                "------------------------------------------------");
                        System.out.println("1. Type 1 to pass the ball (if you are not the current ball holder, you cannot pass the ball). \n" +
                                "2. You can type 2 to see a list of players. \n" +
                                "3. You can type 3 to see the current ball holder. \n-----");

                        // Check who the current ball holder is by running checkBallHolder
                        int newBallHolder = client.checkBallHolder();

                        //New player list - calls on each refresh of the options menu as since the program constantly waits for user input before it writes, it can't be auto
                        int[] newPlayerList = client.getNewPlayers();  // Call the list of players from getNewPlayers

                        //Print out all the new players in the new player list
                        System.out.println("As of " + client.getNewPlayerTime() +" the newest player to join the game is:");
                        for (int player : newPlayerList) {
                                System.out.printf("Player " + player + "\n");
                        }
                        System.out.println("-----");

                        // If the ball holder is the current client, tell them that they are the current ball holder
                        if (newBallHolder == playerId) {
                            System.out.println("Player " + playerId + ", you are currently holding the ball. \n-----");
                        }

                        //Request choice until it is the correct number
                        do {
                            System.out.println("Waiting for you to choose an option...");

                            //Catching input exception
                            try {
                                choice = Integer.parseInt(in.nextLine());
                            } catch (NumberFormatException ex) {
                                System.out.println("You cannot enter any input other than a number");
                            }

                            System.out.println("-----");
                        } while (choice != 1 && choice != 2 && choice != 3);

                        chooseOption = 1;   // Disable choose option menu by setting chooseOption to 1 to stop it from looping
                    }

                    // If the player types 1
                    if (choice == 1) {
                        // Only run if they are the not current ball holder
                        int newBallHolder = client.checkBallHolder();
                        if (newBallHolder != playerId) {
                            System.out.println("You are not the current ball holder.\n-----");
                            chooseOption = 0;

                            //If they are the current ball holder, run the else loop
                        } else {
                            //Call 'passball' in the ClientHandler through using passBall1
                            client.passBall1();

                            //Request the player to choose someone to pass to (until they choose a player currently on the server)
                            do {
                                int ballChoice = 0; //Initialise and reset ballChoice to 0 on each loop
                                System.out.println("Please choose a player to pass the ball to by typing their number (e.g. 1) . If the player does not exist, you will be prompted again to choose a different player.");

                                // Try-Catch block to catch input exception to stop the player from inputting anything other than a number
                                try {
                                    //Wait for the player to input a number
                                    ballChoice = Integer.parseInt(in.nextLine());
                                } catch (NumberFormatException ex) {
                                    System.out.println("You cannot enter any input other than a number");
                                }

                                // Enter a variable to the toPlayer scanner in ClientHandler by calling passBall2 from the client class
                                client.passBall2(ballChoice);

                                //If the choice typed is not a valid player, repeat the do-while loop
                            } while (!client.getChoiceTyped());


                            ballHolder = client.getBallHolder();   // Assign the ballHolder to the new player
                            chooseOption = 0;   // Set chooseOption back to 0
                        }

                        // If player types 2
                    } else if (choice == 2) {
                        // Call the list of players from getPlayers
                        int[] playerList = client.getPlayers();

                        //Print out all the players in the player list
                        System.out.println("List of players: ");
                        for (int player : playerList) {
                            System.out.printf("Player: " + player + "\n");
                        }
                        System.out.println("-----");

                        chooseOption = 0; // Set chooseOption back to 0

                        // If player types 3
                    } else if (choice == 3) {

                        //Generating the current time
                        Date time = new Date();

                            // Call checkBallHolder from the client to get the current ballHolder, then print
                            System.out.println("As of " + time + " the current ball holder is player " + client.checkBallHolder() + "\n-----");

                        chooseOption = 0; // Set chooseOption back to 0
                    }
                }
            }
        } catch (Exception e) { //Catching any exceptions
            System.out.println(e.getMessage());
        }
    }
}
