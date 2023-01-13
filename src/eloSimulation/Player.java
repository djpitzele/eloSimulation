package eloSimulation;

/**
 * The Player class is a class implemented with the goal of multithreading in
 * mind. Each player instance represents one player in a specific game that
 * the player has a reference to. Each player also has an ELO value which can
 * change based on the results of their games. The player uses random chance
 * to decide whether to enter their game's queue or not, and once they are in
 * the queue they wait to have a match made for them and then play that game
 * out, and receive the gain/loss in ELO as appropriate.
 * 
 * @author dpitz
 *
 */
public class Player implements Runnable {
	private double elo;
	private Game game;
	private double willingToPlay;
	private int loopsSincePlay;
	
	public Player(Game g) {
		game = g;
		elo = 0.0;
		loopsSincePlay = 0;
		
		// represents how much this player likes to play the game, will be
		// a decimal between 0.2 (inclusive) and 0.8 (exclusive)
		willingToPlay = 0.2 + (Math.random() * 0.6);
	}
	
	/**
	 * Changes this player's ELO by the given amount.
	 * 
	 * @param amount the amount to change by
	 */
	public void changeElo(double amount) {
		elo += amount;
	}
	
	/**
	 * Returns the difference in ELO between this player and a given one.
	 * @param p2 the player to compare to
	 * @return this player's ELO - the other player's ELO
	 */
	public double eloDiff(Player p2) {
		return (elo - p2.elo);
	}
	
	
	/**
	 * Each player will keep choosing to either enter the queue or not 
	 * until they are destroyed
	 */
	public void run() {
		while (loopsSincePlay < 3) {
			if (!game.isInQueue(this)) {
				double rand = Math.random();
				
				// if the random value is below or at the player's willingness
				// to play, they will join the queue
				if (rand <= willingToPlay) {
					game.addToQueue(this);
					loopsSincePlay = 0;
				}
				else {
					loopsSincePlay += 1;
				}
			}
		}
		
		// change damper and stop queuing, effectively deleting this object
		game.changeDamper(-1 * elo);
	}
}
