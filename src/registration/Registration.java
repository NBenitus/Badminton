package registration;

import java.util.ArrayList;

import standings.Player;
import standings.StandingsCreationHelper.Category;

public class Registration
{
	private ArrayList<Player> singlePlayers;
	private ArrayList<DoubleTeam> doubleTeams;
	private Category category;

	/**
	  * Constructor
	  *
	  * @param singlePlayers
	  *            list of single players registering to the next tournament
	  * @param doubleTeams
	  *            list of doubles teams registering to the next tournament
	  * @param category
	  *            category of the list of registered players
	  */
	public Registration(ArrayList<Player> singlePlayers, ArrayList<DoubleTeam> doubleTeams, Category category)
	{
		this.singlePlayers = singlePlayers;
		this.doubleTeams = doubleTeams;
		this.category = category;
	}

	public Category getCategory()
	{
		return category;
	}

	public ArrayList<DoubleTeam> getDoubleTeams()
	{
		return doubleTeams;
	}

	public ArrayList<Player> getSinglePlayers()
	{
		return singlePlayers;
	}

	public void setCategory(Category category)
	{
		this.category = category;
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
