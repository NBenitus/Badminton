package standings;

import java.util.ArrayList;

public class TeamResult
{
	private String schoolName;
	private ArrayList<Float> scores;
	private int rank;
	private float totalScore;

	/**
	  * Constructor.
	  *
	  * @param schoolName
	  *            name of the school
	  * @param scores
	  *            list of scores for all the tournaments for a given type of result (ex: "Benjamin masculin")
	  * @param rank
	  *            rank of the school for a given type of result
	  * @param totalScore
	  *            total score for the tournaments 2 to 5
	  */
	public TeamResult(String schoolName, int rank, float totalScore)
	{
		this.rank = rank;
		this.schoolName = schoolName;
		this.totalScore = totalScore;

		scores = new ArrayList<Float>();
	}

	public TeamResult(String schoolName)
	{
		this.schoolName = schoolName;
		scores = new ArrayList<Float>();
	}

	/**
	  * Adds a tournament score to the list
	  *
	  * @param score
	  *            tournament score to be added
	  */
	public void addScore(float score)
	{
		scores.add(score);
	}

	public int getRank()
	{
		return rank;
	}

	public ArrayList<Float> getScores()
	{
		return scores;
	}

	public String getSchoolName()
	{
		return schoolName;
	}

	public float getTotalScore()
	{
		return totalScore;
	}

	public boolean hasSameName(String name)
	{
		boolean hasSameName = false;

		if (schoolName.equals(name))
		{
			hasSameName = true;
		}

		return hasSameName;
	}

	public void setScores(ArrayList<Float> scores)
	{
		this.scores = scores;
	}

}
