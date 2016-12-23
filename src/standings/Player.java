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
	  * @param category
	  *            category which the player participates in (ex: Benjamin, cadet or juvénile)
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
	  * @param name
	  *            name of the player
	  * @param schoolName
	  *            name of the school where the player competes
	  * @param gender
	  *            gender of the player
	  * @param category
	  *            category which the player participates in (ex: Benjamin, cadet or juvénile)
	  */
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

	public String getId()
	{
		return id;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((schoolName == null) ? 0 : schoolName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (category != other.category)
			return false;
		if (gender != other.gender)
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (schoolName == null)
		{
			if (other.schoolName != null)
				return false;
		}
		else if (!schoolName.equals(other.schoolName))
			return false;
		return true;
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
