package eloSimulation;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * A game object represents one unique game with its own ELO rating system. A
 * game will have a queue that is constantly running once the startQueue()
 * method is called.
 * 
 * @author dpitz
 *
 */
public class Game{
	private boolean keepRunning;
	private Queue<Player> q;
	private Player[] MMSlots;
	
	public Game() {
		keepRunning = true;
		q = null;
		MMSlots = new Player[5];
	}
	
	/**
	 * Adds the parameter to the end of the queue for this game.
	 * 
	 * @param p the player to be added to the queue
	 */
	public void addToQueue(Player p) {
		if (q != null) {
			q.add(p);
		}
	}
	
	/**
	 * Starts the queue for this game, and keeps it continuously running as
	 * as long as keepRunning stays true.
	 */
	public void startQueue() {
		q = new ArrayDeque<>();
		
		// smallRadius is the range within an instant match can be made (gets
		// larger as queue grows)
		double smallRadius = 0.0;
		
		// Each iteration of this loop will remove the first thing from the
		// queue (if there are any elements in it)
		while (keepRunning) {
			if (!q.isEmpty()) {
				smallRadius = getSmall(q.size());
				
				Player cur = q.poll();
				boolean needsMatch = true;
				
				// Check for any instant matches
				for (int i = 0; i < MMSlots.length; i++) {
					if (needsMatch) {
						Player p = MMSlots[i];
						
						// If this condition is met, an instant match can be made
						if (p != null && Math.abs(cur.eloDiff(p)) < smallRadius) {
							playGame(cur, p);
							needsMatch = false;
							MMSlots[i] = null;
						}
					}
				}
				
				// If no instant matches can be made, try to fill an empty slot
				for (int i = 0; i < MMSlots.length; i++) {
					if (needsMatch && MMSlots[i] == null) {
						MMSlots[i] = cur;
						needsMatch = false;
					}
				}
				
				// If all else fails, match with the CLOSEST player in ELO
				if (needsMatch) {
					int closestInd = -1;
					double closestDiff = Double.MAX_VALUE;
					
					for (int i = 0; i < MMSlots.length; i++) {
						Player p = MMSlots[i];
						if (Math.abs(cur.eloDiff(p)) < closestDiff) {
							closestInd = i;
							closestDiff = Math.abs(cur.eloDiff(p));
						}
					}
					
					playGame(cur, MMSlots[closestInd]);
					MMSlots[closestInd] = null;
					needsMatch = false;
				}
			}
		}
	}
	
	/**
	 * Conducts a complete game between two players. Decides a winner and
	 * awards ELO gain/loss appropriately.
	 * 
	 * @param p1 the first player
	 * @param p2 the second player
	 */
	public void playGame(Player p1, Player p2) {
		
	}
	
	/**
	 * Applies an equation to the current size of the queue to generate a new
	 * small radius.
	 * 
	 * @param size the number of elements currently in the queue
	 * 
	 * @return the smallRadius for this sized queue
	 */
	private double getSmall(int size) {
		double ans = Math.sqrt(size);
		ans *= 1.1;
		
		if (ans > 20) { // small radius should not get too big
			ans = 20;
		}
		
		return ans;
	}
}
