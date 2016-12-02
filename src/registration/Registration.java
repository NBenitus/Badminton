package registration;

import java.util.ArrayList;

import standings.Player;

public class Registration
{
	private ArrayList<Player> singlePlayers;
	private ArrayList<DoubleTeam> doubleTeams;

	/**
	  * Constructor
	  *
	  * @param singlePlayers
	  *            list of single players registering to the next tournament
	  * @param doubleTeams
	  *            list of doubles teams registering to the next tournament
	  */
	public Registration(ArrayList<Player> singlePlayers, ArrayList<DoubleTeam> doubleTeams)
	{
		this.singlePlayers = singlePlayers;
		this.doubleTeams = doubleTeams;
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
