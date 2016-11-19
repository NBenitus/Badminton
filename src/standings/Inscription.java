package standings;

import java.util.ArrayList;

public class Inscription
{
	private ArrayList<Player> singlePlayers;
	private ArrayList<DoubleTeam> doubleTeams;

	public Inscription(ArrayList<Player> players, ArrayList<DoubleTeam> teams)
	{
		this.singlePlayers = players;
		this.doubleTeams = teams;
	}

	public ArrayList<DoubleTeam> getDoubleTeams()
	{
		return doubleTeams;
	}

	public ArrayList<Player> getSinglePlayers()
	{
		return singlePlayers;
	}

	public void setDoubleTeams(ArrayList<DoubleTeam> doubleTeams)
	{
		this.doubleTeams = doubleTeams;
	}

	public void setSinglePlayers(ArrayList<Player> singlePlayers)
	{
		this.singlePlayers = singlePlayers;
	}
}
