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
	public IndividualCombinedResult(String playerName, String schoolName, int singleScore, int doubleScore,
			int totalScore, int rank)
	{
		super(playerName, schoolName, totalScore, rank);
		this.singleScore = singleScore;
		this.doubleScore = doubleScore;
	}

	public int getDoubleScore()
	{
		return doubleScore;
	}

	public int getSingleScore()
	{
		return singleScore;
	}
}
