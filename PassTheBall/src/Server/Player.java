/*------------------------------------*
 *          SERVER - PLAYER           *
 *------------------------------------*/

package Server;

public class Player {
    private final int playerId;

    // Making the player constructor
    public Player(int playerId) {
        this.playerId = playerId;
    }

    // Getter for returning playerId
    public int getPlayerId() {
        return playerId;
    }
}
