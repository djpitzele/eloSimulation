package eloSimulation;

public class Player implements Runnable{
	private double elo;
	
	public Player() {
		elo = 0.0;
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
	
	
	// TODO: write player run where they decide whether or not to enter queue
	public void run() {
		
	}
}
