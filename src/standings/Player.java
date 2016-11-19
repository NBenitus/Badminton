package standings;

import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.Gender;

public class Player
{
	private String name;
	private String schoolName;
	private Gender gender;
	private Category category;

	/**
	  * Constructor.
	  *
	  * @param playerName
	  *            name of the player
	  * @param schoolName
	  *            name of the school where the player competes
	  * @param gender
	  *            gender of the tournament
	  */
	public Player(String name, String schoolName, Gender gender)
	{
		this.name = name;
		this.schoolName = schoolName;
		this.gender = gender;
		this.category = null;
	}

	public Player(String name, String schoolName, Gender gender, Category category)
	{
		this.name = name;
		this.schoolName = schoolName;
		this.gender = gender;
		this.category = category;
	}

	public Category getCategory()
	{
		return category;
	}

	public Gender getGender()
	{
		return gender;
	}

	public String getName()
	{
		return name;
	}

	public String getSchoolName()
	{
		return schoolName;
	}

	public void setCategory(Category category)
	{
		this.category = category;
	}

	public void setGender(Gender gender)
	{
		this.gender = gender;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setSchoolName(String schoolName)
	{
		this.schoolName = schoolName;
	}
}
