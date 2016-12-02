package registration;

import standings.Player;

public class DoubleTeam
{
	private Player[] players = new Player[2];

	/**
	  * Constructor
	  *
	  * @param firstPlayer
	  *            first player in the doubles team
	  * @param secondPlayer
	  *            second player in the doubles team
	  */
	public DoubleTeam(Player firstPlayer, Player secondPlayer)
	{
		players[0] = firstPlayer;
		players[1] = secondPlayer;
	}

	public Player[] getPlayers()
	{
		return players;
	}
}
