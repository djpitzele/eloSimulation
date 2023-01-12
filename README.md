# eloSimulation
A simulation of a more "fair" ELO rating system than most online video games have.

## Background
I grew up playing competitive online video games with my friends. It was an integral part of my childhood, and it was a good part of what made me want to become a game developer. Throughout this experience I have with ranking systems, I noticed some common repeating issues across these different games. The most glaring of these issues was that players would gain more ranked points than they lost overall, which led to what I call "ELO inflation". Over time, a player of the same skill level will rise in ranking, since it is an easy way to keep people playing. Many games combat this endless ELO inflation with so-called "season resets", which take rank points away from the top (or all) players in scheduled intervals. However, even with these resets, the ELO systems are still not fair to the players, since a player who wins the same amount as they lose will usually gain rating. Therefore, this is my attempt at a "fair" ranking system.

## Fairness
There are multiple ways one could define a fair ranking system. I chose to define it as:
- The sum of all the player's ELO points will always tend toward 0.
- Each player starts with a rating of 0, and a loss loses rating while a win gains rating.
- A more closely matched game might award more ELO to the winner than a less closely matched one.
- The winner of a game will (almost*) gain the same amount as the loser loses.

\* There is a damper to ELO changes, as explained below.

### Damper
In the event of a player quitting the game, the sum of all the ELO's in the system will no longer be 0. To combat this, I implemented a damper to ELO changes. When a player is recognized as "quitting", their profile will be deleted, and their ELO will be added to the damper (which is a running sum). In each conclucded game, the ELO awarded will be slightly off-balance in the direction of decreasing the damper. (ex: if the current damper is -300, the winner will gain slightly less ELO than the loser loses, and the damper might become 299.8.)
