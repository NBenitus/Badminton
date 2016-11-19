package standings;

public class IndividualResult
{
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
	public IndividualResult(String playerName, String schoolName, int score, int rank)
	{
		this.playerName = playerName;
		this.schoolName = schoolName;
		this.score = score;
		this.rank = rank;
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

	public int getScore()
	{
		return score;
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
