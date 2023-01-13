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
public class Game {
	private boolean keepRunning;
	private Queue<Player> q;
	private Player[] MMSlots;
	private double damper;

	public Game() {
		keepRunning = true;
		q = null;
		MMSlots = new Player[5];
		damper = 0.0;
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
	 * Changes the damper by adding the given amount
	 * 
	 * @param amount the amount to change the damper by
	 */
	public void changeDamper(double amount) {
		damper += amount;
	}

	/**
	 * Starts the queue for this game, and keeps it continuously running as as long
	 * as keepRunning stays true.
	 */
	public void startQueue() {
		q = new ArrayDeque<>();

		// smallRadius is the range within an instant match can be made (gets larger as queue grows)
		double smallRadius = 0.0;

		// Each iteration of this loop will remove the first thing from the queue (if there are any elements in it)
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
	 * Conducts a complete game between two players. Decides a winner and awards ELO
	 * gain/loss appropriately.
	 * 
	 * @param p1 the first player
	 * @param p2 the second player
	 */
	public void playGame(Player p1, Player p2) {
		// If outcome is true, p1 wins, if false p2 wins
		boolean outcome = getOutcome(p1, p2);

		// change ELO as appropriate
		Player winningPlayer, losingPlayer;

		if (outcome) {
			winningPlayer = p1;
			losingPlayer = p2;
		} else {
			winningPlayer = p2;
			losingPlayer = p1;
		}

		double eloGap = winningPlayer.eloDiff(losingPlayer);

		// apply the eloEquation as seen in the PNG file
		double eloChange = (-0.8 * Math.cbrt(eloGap)) + 5;

		// check for extreme cases (will rarely happen)
		if (eloChange > 7.5) {
			eloChange = 7.5;
		} else if (eloChange < 2.5) {
			eloChange = 2.5;
		}

		// eloChange will be applied to winner, lossEloChange to loser
		double lossEloChange = eloChange;

		// apply damper, if there is one (offset is always a positive number)
		if (damper > 0) {
			double offset = 0.1;
			if (damper < 0.1) {
				offset = damper;
			}

			lossEloChange += offset;
			damper -= offset;
		} else if (damper < 0) {
			double offset = 0.1;
			if (damper > -0.1) {
				offset = -1 * damper;
			}

			lossEloChange -= offset;
			damper += offset;
		}

		// finally, apply ELO changes
		winningPlayer.changeElo(eloChange);
		losingPlayer.changeElo(-1 * lossEloChange);
	}

	/**
	 * Gets the outcome of 1 game between these two players.
	 * 
	 * @param p1 player 1
	 * @param p2 player 2
	 * 
	 * @return true = player 1 wins, false = player 2 wins
	 */
	private boolean getOutcome(Player p1, Player p2) {
		// SIMPLE VERSION: higher ELO player has 60% chance of winning
		double eloDiff = p1.eloDiff(p2);
		double rand = Math.random();
		boolean res = true;

		if (eloDiff == 0) {
			if (rand < 0.5) {
				res = false;
			}
		} else if (eloDiff > 0) {
			if (rand < 0.4) {
				res = false;
			}
		} else {
			if (rand < 0.6) {
				res = false;
			}
		}

		return res;
	}

	/**
	 * Applies an equation to the current size of the queue to generate a new small
	 * radius.
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

	/**
	 * Checks if a given player is already in the queue.
	 * 
	 * @param p the player to check for
	 * @return whether or not the player is present in queue
	 */
	public boolean isInQueue(Player p) {
		return (q.contains(p));
	}
}
