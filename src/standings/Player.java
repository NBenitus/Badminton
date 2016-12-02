package standings;

import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.Gender;

public class Player
{
	private String id;
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
	  *            gender of the player
	  */
	public Player(String name, String schoolName, Gender gender)
	{
		this.name = name;
		this.schoolName = schoolName;
		this.gender = gender;
		this.category = null;
	}

	/**
	  * Constructor.
	  *
	  * @param id
	  *            unique id of the player
	  * @param playerName
	  *            name of the player
	  * @param schoolName
	  *            name of the school where the player competes
	  * @param gender
	  *            gender of the player
	  */
	public Player(String id, String name, String schoolName, Gender gender, Category category)
	{
		this.name = name;
		this.schoolName = schoolName;
		this.gender = gender;
		this.category = category;
		this.id = id;
	}

	/**
	  * Constructor.
	  *
	  * @param playerName
	  *            name of the player
	  * @param schoolName
	  *            name of the school where the player competes
	  * @param gender
	  *            gender of the player
	  */
	public Player(String name, String schoolName, Gender gender, Category category)
	{
		this.name = name;
		this.schoolName = schoolName;
		this.gender = gender;
		this.category = null;
	}

	public Category getCategory()
	{
		return category;
	}

	public Gender getGender()
	{
		return gender;
	}

	public String getId()
	{
		return id;
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

	public void setId(String id)
	{
		this.id = id;
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
