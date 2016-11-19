package standings;

import java.util.ArrayList;

import standings.StandingsCreationHelper.TypeOfPlay;

public class Result
{
	private String name;
	private String schoolName;
	private String category;
	private TypeOfPlay typeOfPlay;
	private ArrayList<Integer> scores;

	/**
	  * Constructor
	  *
	  * @param name
	  *            name of the player
	  * @param schoolName
	  *            name of the school where the player competes
	  * @param category
	  *            category (ex: "Benjamin") in which the player is participating
	  * @param typeOfPlay
	  *            either "Single / Simple" or "double"
	  * @param scores
	  *            list of scores for all the tournaments participated with the same category and type of play
	  */
	public Result(String name, String schoolName, String category, TypeOfPlay typeOfPlay, ArrayList<Integer> scores)
	{
		this.name = name;
		this.schoolName = schoolName;
		this.category = category;
		this.typeOfPlay = typeOfPlay;
		this.scores = scores;
	}

	public String getCategory()
	{
		return category;
	}

	public String getName()
	{
		return name;
	}

	public String getSchoolName()
	{
		return schoolName;
	}

	public ArrayList<Integer> getScores()
	{
		return scores;
	}

	public TypeOfPlay getTypeOfPlay()
	{
		return typeOfPlay;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setSchoolName(String schoolName)
	{
		this.schoolName = schoolName;
	}

	public void setScores(ArrayList<Integer> scores)
	{
		this.scores = scores;
	}

	public void setTypeOfPlay(TypeOfPlay typeOfPlay)
	{
		this.typeOfPlay = typeOfPlay;
	}
}
