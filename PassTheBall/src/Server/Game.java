/*------------------------------------*
 *           SERVER - GAME            *
 *------------------------------------*/

package Server;

import java.util.*;

public class Game {

    public final Map<Integer, Player> players = new TreeMap<>(); // Creating map to store all players in game
    Map<Integer, Player> newPlayers;
    boolean ballHeld = false; // Creating boolean that says whether the ball is being held by any player or not
    int ballHolder = 0; //ID of the player holding the ball currently
    int toPlayer = 0; // Creating variable to store which player is being passed to

    // Function to add players to the player TreeMap when they join
    public void playerJoined(int playerId) {
        Player player = new Player(playerId);
        players.put(playerId, player);
    }

    //Add new players to treemap and print
    public void getNewPlayers(int playerId){
        newPlayers = new TreeMap<>();

        Player newPlayer = new Player(playerId);
        newPlayers.put(playerId, newPlayer);
    }

    public List<Integer> returnNewPlayers(){
        List<Integer> result = new ArrayList<>();

        for (Player newPlayer2 : newPlayers.values()) {
            result.add(newPlayer2.getPlayerId());
        }

        return(result);
    }

    // Function to remove players from the player TreeMap when they leave
    public void removeFromList(int playerId){
            players.remove(playerId);
    }

    //player leave event
    public void playerLeft(int playerId) {

        //Print the leave event to the server
        System.out.println("Player " + playerId + " left");

        // If the player who left the game was the ballHolder, set ballHeld to false
        if(playerId == ballHolder){
            ballHeld = false;
        }

        // Remove player from TreeMap
        removeFromList(playerId);
    }

    // Print out a list of players
    public List<Integer> listOfPlayers() {

        //For every player in players, add to the result ArrayList, and then print result
        List<Integer> result = new ArrayList<>();

        for (Player player : players.values())
            result.add(player.getPlayerId());

        return result;
    }

    // Pass the ball if it belongs to nobody ( so if the last ballHolder left the game)
    public int ballPassed() {

        List<Integer> listOfPlayers = listOfPlayers(); // Calling the listOfPlayers

        // If the ball is not held by any player, run the main ballPassed method
        // Avoiding illegal argument exception by only running when players > 0
        if((!ballHeld) && (listOfPlayers.size() > 0)) {

            // Get a random value from the players TreeMap
            Random random = new Random();
            Object[] playerValues = players.values().toArray();
            int randomPlayerRaw = random.nextInt(playerValues.length);
            Object randomPlayer = playerValues[randomPlayerRaw];

            // Get ID from the value to print
            Set<Map.Entry<Integer, Player>> intPlayers;
            intPlayers = players.entrySet();

            // For player in the players entrySet, if the variable equals the randomPlayer variable, set toPlayer to the key value of that variable
            for( Map.Entry<Integer, Player> player : intPlayers){

                if(player.getValue().equals(randomPlayer)){
                    toPlayer = player.getKey();
                    break;
                }
            }

            // Set the clientHandler class & Game class ballHolder variables to the new player
            ballHolder = toPlayer;
            ClientHandler.clientBallHolder = toPlayer;

            // Set the ball held value to true
            ballHeld = true;

            // Print out the ballPassed event to the server
            System.out.println("The ball has been passed to player " + toPlayer);
        }
        return toPlayer; // Return the new ball holder
    }

    //Pass the ball to the first player to join the game
    public int firstBall(int playerId){
        List<Integer> listOfPlayers = listOfPlayers(); // Calling the listOfPlayers

        // When game has just started, run firstBall to pass to the first player
        // Avoiding illegal argument exception by only running when players > 0
        if(listOfPlayers.size() > 0) {

            ballHeld = true;
            ballHolder = playerId;
            ClientHandler.clientBallHolder = playerId;
            System.out.println("Game started. The ball has been passed to player " + playerId);
        }
        return playerId;
    }

    // Pass the ball to another player
    public int passBall(int toPlayer){
        // When the ball is passed, set the Game class ballHolder to the new player and set the ball held value to true
        ballHolder = toPlayer;
        ballHeld = true;

        return ballHolder; //Return the new ball holder
        }
}
