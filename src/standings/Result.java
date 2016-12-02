package standings;

import java.util.ArrayList;

import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.TypeOfPlay;

public class Result
{
	private String id;
	private String schoolName;
	private Category category;
	private TypeOfPlay typeOfPlay;
	private ArrayList<Integer> scores;

	/**
	  * Constructor
	  *
	  * @param id
	  *            id of the player
	  * @param schoolName
	  *            name of the school where the player competes
	  * @param category
	  *            category (ex: "Benjamin") in which the player is participating
	  * @param typeOfPlay
	  *            either "Single / Simple" or "double"
	  * @param scores
	  *            list of scores for all the tournaments participated with the same category and type of play
	  */
	public Result(String id, String schoolName, Category category, TypeOfPlay typeOfPlay, ArrayList<Integer> scores)
	{
		this.id = id;
		this.schoolName = schoolName;
		this.category = category;
		this.typeOfPlay = typeOfPlay;
		this.scores = scores;
	}

	public Category getCategory()
	{
		return category;
	}

	public String getID()
	{
		return id;
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

	public void setCategory(Category category)
	{
		this.category = category;
	}

	public void setID(String id)
	{
		this.id = id;
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
