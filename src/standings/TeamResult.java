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

	/**
	  * Constructor.
	  *
	  * @param schoolName
	  *            name of the school
	  */
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

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TeamResult other = (TeamResult) obj;
		if (rank != other.rank)
			return false;
		if (schoolName == null)
		{
			if (other.schoolName != null)
				return false;
		}
		else if (!schoolName.equals(other.schoolName))
			return false;
		if (scores == null)
		{
			if (other.scores != null)
				return false;
		}
		else if (!scores.equals(other.scores))
			return false;
		if (Float.floatToIntBits(totalScore) != Float.floatToIntBits(other.totalScore))
			return false;
		return true;
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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + rank;
		result = prime * result + ((schoolName == null) ? 0 : schoolName.hashCode());
		result = prime * result + ((scores == null) ? 0 : scores.hashCode());
		result = prime * result + Float.floatToIntBits(totalScore);
		return result;
	}

	// To do: verify if this method is really worth it
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
