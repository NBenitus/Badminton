package standings;

public class IndividualCombinedResult extends IndividualResult
{
	private int singleScore;
	private int doubleScore;

	/**
	 * Constructor.
	 *
	 * @param playerName
	 *            name of the player
	 * @param schoolName
	 *            name of the school where the player competes
	 * @param singleScore
	 *            player's score in single type of play
	 * @param doubleScore
	 *            player's score in double type of play
	 * @param totalScore
	 *            player's score in both types of play combined
	 * @param rank
	 *            rank of the player
	 */
	public IndividualCombinedResult(String id, String playerName, String schoolName, int singleScore, int doubleScore,
			int totalScore, int rank)
	{
		super(id, playerName, schoolName, totalScore, rank);
		this.singleScore = singleScore;
		this.doubleScore = doubleScore;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndividualCombinedResult other = (IndividualCombinedResult) obj;
		if (doubleScore != other.doubleScore)
			return false;
		if (singleScore != other.singleScore)
			return false;
		return true;
	}

	public int getDoubleScore()
	{
		return doubleScore;
	}

	public int getSingleScore()
	{
		return singleScore;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + doubleScore;
		result = prime * result + singleScore;
		return result;
	}
}
