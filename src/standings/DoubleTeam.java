package standings;

public class DoubleTeam
{
	private Player[] players = new Player[2];

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
