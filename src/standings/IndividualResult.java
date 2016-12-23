package standings;

public class IndividualResult
{
	private String id;
	private String playerName;
	private String schoolName;
	private int score;
	private int rank;

	/**
	  * Constructor.
	  *
	  * @param playerName
	  *            name of the player
	  * @param schoolName
	  *            name of the school where the player competes
	  * @param score
	  *            score of the tournament
	  * @param rank
	  *            rank of the player
	  */
	public IndividualResult(String id, String playerName, String schoolName, int score, int rank)
	{
		this.id = id;
		this.playerName = playerName;
		this.schoolName = schoolName;
		this.score = score;
		this.rank = rank;
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
		IndividualResult other = (IndividualResult) obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (playerName == null)
		{
			if (other.playerName != null)
				return false;
		}
		else if (!playerName.equals(other.playerName))
			return false;
		if (rank != other.rank)
			return false;
		if (schoolName == null)
		{
			if (other.schoolName != null)
				return false;
		}
		else if (!schoolName.equals(other.schoolName))
			return false;
		if (score != other.score)
			return false;
		return true;
	}

	public int getDoubleScore()
	{
		return -1;
	}

	public String getId()
	{
		return id;
	}

	public String getPlayerName()
	{
		return playerName;
	}

	public int getRank()
	{
		return rank;
	}

	public String getSchoolName()
	{
		return schoolName;
	}

	public int getSingleScore()
	{
		return -1;
	}

	public int getScore()
	{
		return score;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((playerName == null) ? 0 : playerName.hashCode());
		result = prime * result + rank;
		result = prime * result + ((schoolName == null) ? 0 : schoolName.hashCode());
		result = prime * result + score;
		return result;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}

	public void setRank(int rank)
	{
		this.rank = rank;
	}

	public void setSchoolName(String schoolName)
	{
		this.schoolName = schoolName;
	}

	public void setScore(int score)
	{
		this.score = score;
	}
}
