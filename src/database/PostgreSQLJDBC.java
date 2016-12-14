package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import log.LoggerWrapper;
import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.Gender;
import standings.StandingsCreationHelper.TypeOfPlay;
import standings.StandingsCreationHelper.TypeOfResult;
import standings.Result;
import standings.TeamResult;
import standings.TeamResultSheet;
import utilities.Utilities;
import standings.IndividualCombinedResult;
import standings.IndividualResult;
import standings.Player;

public class PostgreSQLJDBC
{
	private static Connection connection;

	/**
	 * Adds a list of players to the database
	 *
	 * @param listPlayers
	 *            list of players from the XLS file
	 */
	public static void addPlayer(ArrayList<Player> listPlayers)
	{
		try
		{
			openDatabase();

			// Iterate over each entry of the list of players
			for (int i = 0; i < listPlayers.size(); i++)
			{
				PreparedStatement stm = connection.prepareStatement(
						"INSERT INTO Player (id, name, SchoolName, Gender, Category) VALUES(?,?,?,?,?);");

				stm.setString(1, listPlayers.get(i).getId());
				stm.setString(2, listPlayers.get(i).getName());
				stm.setString(3, listPlayers.get(i).getSchoolName());
				stm.setString(4, listPlayers.get(i).getGender().text());
				stm.setString(5, listPlayers.get(i).getCategory().text());
				stm.executeUpdate();

				stm.close();
			}

			closeDatabase();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LoggerWrapper.myLogger.severe(e.toString());
			System.exit(0);
		}
	}

	/**
	 * Adds a list of registrations of single players to the database
	 *
	 * @param listPlayers
	 *            list of players from the form file
	 */
	public static void addRegistrationSingles(ArrayList<Player> listPlayers)
	{
		try
		{
			openDatabase();

			// Iterate over each entry of the list of results
			for (int i = 0; i < listPlayers.size(); i++)
			{
				PreparedStatement stm = connection.prepareStatement(
						"INSERT INTO RegistrationSingle (PlayerId_fkey, SchoolName_fkey, Category_fkey, TournamentNumber_fkey"
								+ ",TypeOfPlay_fkey, Matched) VALUES(?,?,?,?,?,?);");

				stm.setString(1, listPlayers.get(i).getName());
				stm.setString(2, listPlayers.get(i).getSchoolName());
				stm.setString(3, listPlayers.get(i).getCategory().text());
				stm.setInt(4, 4);
				stm.setString(5, TypeOfPlay.SIMPLE.text());
				stm.setBoolean(6, false);

				stm.executeUpdate();
				stm.close();
			}

			closeDatabase();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LoggerWrapper.myLogger.severe(e.toString());
			System.exit(0);
		}
	}

	/**
	 * Adds a list of Results to the database
	 *
	 * @param listPlayers
	 *            list of Results from the XLS file
	 */
	public static void addResult(ArrayList<Result> listResults)
	{
		try
		{
			openDatabase();

			// Iterate over each entry of the list of results
			for (int i = 0; i < listResults.size(); i++)
			{
				// Add a result entry (name, schoolName, score) for each tournament
				for (int j = 1; j <= TeamResultSheet.NUMBER_TOURNAMENTS; j++)
				{
					PreparedStatement stm = connection.prepareStatement(
							"INSERT INTO Result (PlayerID_fkey, SchoolName_fkey, Category, TypeOfPlay, "
									+ "TournamentNumber, Score) VALUES(?,?,?,?,?,?);");

					stm.setString(1, listResults.get(i).getID());
					stm.setString(2, listResults.get(i).getSchoolName());
					stm.setString(3, listResults.get(i).getCategory().text());
					stm.setString(4, listResults.get(i).getTypeOfPlay().text());
					stm.setInt(5, j);
					stm.setInt(6, listResults.get(i).getScores().get(j - 1));

					stm.executeUpdate();
					stm.close();
				}
			}

			closeDatabase();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LoggerWrapper.myLogger.severe(e.toString());
			System.exit(0);
		}
	}

	/**
	 * Deletes entries for the specified table in the database
	 */
	public static void clearTable(String tableName)
	{
		PreparedStatement stm;

		try
		{
			openDatabase();

			stm = connection.prepareStatement("DELETE FROM " + tableName + ";");
			stm.execute();

			stm.close();

			closeDatabase();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			LoggerWrapper.myLogger.severe(e.toString());
		}
	}

	/**
	 * Closes the database
	 */
	public static void closeDatabase()
	{
		try
		{
			connection.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LoggerWrapper.myLogger.severe(e.toString());
			System.exit(0);
		}

//		System.out.println("Database closed successfully");
	}

	/**
	 * Get a list of individual Result based on the type of result needed
	 *
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @param typeOfPlay
	 *            type of play to write (ex: Single or Double)
	 * @return list of individual results
	 */
	public static ArrayList<IndividualResult> getIndividualResults(TypeOfResult typeOfResult, TypeOfPlay typeOfPlay)
	{
		ArrayList<IndividualResult> individualResults = new ArrayList<IndividualResult>();

		try
		{
			ResultSet resultSet;
			PreparedStatement stm;

			openDatabase();

			// Get individual Result for either Single or Double play
			if (!(typeOfPlay == TypeOfPlay.COMBINED))
			{
				stm = connection
						.prepareStatement("SELECT name, ttotal.PlayerId_fkey, SchoolName_fkey, SumScores, "
								+ "RANK() over (order by SumScores desc) AS OverallRank " + "FROM ( "
								+ "SELECT PlayerId_fkey, sum(coalesce(tBestIndividualScores.Score, 0)) AS SumScores "
								+ "FROM (" + individualResultsBestScores()
								+ "WHERE ScoreRank <= 4 GROUP BY PlayerId_fkey " + "ORDER BY SumScores DESC) ttotal "
								+ "LEFT JOIN ( " + individualResultsGetSchool(typeOfPlay)
								+ "ON ttotal.Playerid_fkey=tschool2.Playerid_fkey");

				stm.setString(1, typeOfResult.category().text());
				stm.setString(2, typeOfResult.gender().text());
				stm.setString(3, typeOfPlay.text());

				resultSet = stm.executeQuery();
			}

			// Get individual Result for both Single and Double play (different query needed)
			else
			{
				stm = connection.prepareStatement(
						"SELECT name, ttotal.PlayerId_fkey, SchoolName_fkey, SumScoresSimpleTotal, SumScoresDoubleTotal, "
								+ "rank() over (order by SumScoresTotal desc) AS OverallRank, SumScoresTotal "
								+ "FROM ( "
								+ "SELECT COALESCE(t1.PlayerId_fkey, t2.PlayerId_fkey) as PlayerId_fkey, "
								+ "coalesce(SumScoresSimple, 0) as SumScoresSimpleTotal, "
								+ "coalesce(SumScoresDouble,0) as SumScoresDoubleTotal, "
								+ "(coalesce(SumScoresSimple, 0) + coalesce(SumScoresDouble, 0)) as SumScoresTotal "
								+ "FROM ( " + "SELECT PlayerId_fkey, ScoreSimple, "
								+ "sum(ScoreSimple) over (partition by PlayerId_fkey) as SumScoresSimple " + "FROM ( "
								+ "SELECT PlayerId_fkey, score, ScoreRank, SUM(score) as ScoreSimple " + "FROM ( "
								+ individualResultsBestScores() + "WHERE ScoreRank <= 4 "
								+ "GROUP BY PlayerId_fkey, score, ScoreRank)tinside1) t1 "

								+ "FULL JOIN "

								+ "(SELECT PlayerId_fkey, ScoreDouble, "
								+ "sum(ScoreDouble) over (partition by PlayerId_fkey) as SumScoresDouble " + "FROM ( "
								+ "SELECT PlayerId_fkey, score, ScoreRank, SUM(score) as ScoreDouble " + "FROM ( "
								+ individualResultsBestScores() + "WHERE ScoreRank <= 4 "
								+ "GROUP BY PlayerId_fkey, score, ScoreRank) tinside2) t2 "

								+ "ON t1.PlayerId_fkey = t2.PlayerId_fkey "
								+ "GROUP BY t1.PlayerId_fkey, t2.PlayerId_fkey, SumScoresSimple, SumScoresDouble "
								+ "ORDER BY SumScoresTotal DESC) ttotal " + "LEFT JOIN ( "
								+ individualResultsGetSchool(TypeOfPlay.SIMPLE)
								+ "ON ttotal.PlayerId_fkey=tschool2.PlayerId_fkey");

				stm.setString(1, typeOfResult.category().text());
				stm.setString(2, typeOfResult.gender().text());
				stm.setString(3, TypeOfPlay.SIMPLE.text());
				stm.setString(4, typeOfResult.category().text());
				stm.setString(5, typeOfResult.gender().text());
				stm.setString(6, TypeOfPlay.DOUBLE.text());

				resultSet = stm.executeQuery();
			}

			// Add individual result from the database to the list of individual results
			while (resultSet.next())
			{
				// Add an individual result object for Single and Double play
				if (typeOfPlay != TypeOfPlay.COMBINED)
				{
					individualResults.add(
							new IndividualResult(resultSet.getString("Name"), resultSet.getString("SchoolName_fkey"),
									resultSet.getInt("SumScores"), resultSet.getInt("OverallRank")));
				}

				// Add a individual combined result object for Combined play
				else
				{
					individualResults.add(new IndividualCombinedResult(resultSet.getString("Name"),
							resultSet.getString("SchoolName_fkey"), resultSet.getInt("SumScoresSimpleTotal"),
							resultSet.getInt("SumScoresDoubleTotal"), resultSet.getInt("SumScoresTotal"),
							resultSet.getInt("OverallRank")));
				}
			}

			resultSet.close();
			stm.close();

			closeDatabase();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LoggerWrapper.myLogger.severe(e.toString());
			System.exit(0);
		}

		return individualResults;
	}

	/**
	 * Gets the number of pools in a type of result
	 *
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @param typeOfPlay
	 *            type of play to write (ex: Single or Double)
	 * @param tournamentNumber
	 *            number of the tournament to check the number of pools
	 * @return number of pools
	 */
	public static int getNumberOfPools(TypeOfResult typeOfResult, TypeOfPlay typeOfPlay, int tournamentNumber)
	{
		int numberOfPools = 0;

		try
		{
			openDatabase();

			PreparedStatement stm = connection.prepareStatement(
					"SELECT score, " + "From Result LEFT JOIN Player on Result.PlayerId_fkey = Player.Id "
							+ "WHERE category=? AND Gender=? AND TypeOfPlay=? AND TournamentNumber=?"
							+ "ORDER BY score DESC " + "LIMIT 1");
			stm.setString(1, typeOfResult.category().text());
			stm.setString(2, typeOfResult.gender().text());
			stm.setString(3, typeOfPlay.text());
			stm.setInt(4, tournamentNumber);

			ResultSet resultSet = stm.executeQuery();

			while (resultSet.next())
			{
				numberOfPools = resultSet.getInt("score");
			}

			resultSet.close();
			stm.close();

			closeDatabase();
		}

		catch (Exception e)
		{
			e.printStackTrace();
			LoggerWrapper.myLogger.severe(e.toString());
			System.exit(0);
		}

		return numberOfPools;
	}

	/**
	 * Gets the list of the names all players filtered by school, category and gender
	 *
	 * @return list of player names
	 */
	public static ArrayList<String> getPlayersByFilter(String schoolName, Category category, Gender gender)
	{
		ArrayList<String> listPlayersNames = new ArrayList<String>();

		try
		{
			openDatabase();

			PreparedStatement stm = connection.prepareStatement("SELECT Name FROM Player "
					+ "WHERE SchoolName=? AND (Category=? OR Category=?) AND Gender=?");

			stm.setString(1, schoolName);
			stm.setString(2, category.text());
			stm.setString(3, Utilities.getPreviousCategory(category).text());
			stm.setString(4, gender.text());
			ResultSet resultSet = stm.executeQuery();

			while (resultSet.next())
			{
				listPlayersNames.add(resultSet.getString("Name"));
			}

			stm.close();
			resultSet.close();

			closeDatabase();
		}

		catch (Exception e)
		{
			e.printStackTrace();
			LoggerWrapper.myLogger.severe(e.toString());
			System.exit(0);
		}

		return listPlayersNames;
	}

	/**
	 * Gets the list of all subscribed players
	 *
	 * @return number of pools
	 */
	public static ArrayList<Player> getAllPlayers()
	{
		ArrayList<Player> listPlayers = new ArrayList<Player>();

		try
		{
			openDatabase();

			PreparedStatement stm = connection.prepareStatement("SELECT Id, Name, SchoolName, Gender, Category "
					+ "FROM Player ORDER BY SchoolName, Category, Gender, Name");

			ResultSet resultSet = stm.executeQuery();

			while (resultSet.next())
			{
				listPlayers.add(new Player(resultSet.getString("Id"), resultSet.getString("Name"),
						resultSet.getString("SchoolName"), Gender.valueOf(resultSet.getString("Gender").toUpperCase()),
						Category.valueOf(resultSet.getString("Category").toUpperCase())));
			}

			resultSet.close();
			stm.close();

			closeDatabase();
		}

		catch (Exception e)
		{
			e.printStackTrace();
			LoggerWrapper.myLogger.severe(e.toString());
			System.exit(0);
		}

		return listPlayers;
	}

	/**
	 * Gets the list of all schools
	 *
	 * @return number of pools
	 */
	public static ArrayList<String> getAllSchools()
	{
		ArrayList<String> listSchoolNames = new ArrayList<String>();

		try
		{
			openDatabase();

			PreparedStatement stm = connection.prepareStatement("SELECT Name FROM School");

			ResultSet resultSet = stm.executeQuery();

			while (resultSet.next())
			{
				listSchoolNames.add(resultSet.getString("Name"));
			}

			stm.close();
			resultSet.close();

			closeDatabase();
		}

		catch (Exception e)
		{
			e.printStackTrace();
			LoggerWrapper.myLogger.severe(e.toString());
			System.exit(0);
		}

		return listSchoolNames;
	}

	/**
	 * Get a list of team results based on the type of result needed
	 *
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @return list of team results
	 */
	public static ArrayList<TeamResult> getTeamResults(TypeOfResult typeOfResult)
	{
		ArrayList<TeamResult> listOfTeams = getTeamResultsByOverallRank(typeOfResult);
		ArrayList<TeamResult> listOfTournamentScoresByTeams = getTeamResultsByTournament(typeOfResult);

		// Iterate over the number of schools in the corresponding category
		for (int i = 0; i < listOfTournamentScoresByTeams.size(); i++)
		{
			// Add each tournament Score to each school
			for (int j = 0; j < listOfTeams.size(); j++)
			{
				if (listOfTeams.get(j).hasSameName(listOfTournamentScoresByTeams.get(i).getSchoolName()))
				{
					listOfTeams.get(j).setScores(listOfTournamentScoresByTeams.get(i).getScores());
				}
			}
		}

		return listOfTeams;
	}

	/**
	 * Get a list of teams results based on their name, their overall Score and their rank
	 *
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @return list of team results including name, overall Score and rank
	 */
	private static ArrayList<TeamResult> getTeamResultsByOverallRank(TypeOfResult typeOfResult)
	{
		ArrayList<TeamResult> teamsResults = new ArrayList<TeamResult>();

		try
		{
			ResultSet resultSet;

			openDatabase();

			Statement stmt = connection.createStatement();

			resultSet = stmt.executeQuery(teamResultsGetOverallRankBySchoolQuery(typeOfResult));

			while (resultSet.next())
			{
				teamsResults.add(new TeamResult(resultSet.getString("SchoolName_fkey"), resultSet.getInt("OverallRank"),
						resultSet.getFloat("TotalScoreBySchool")));
			}

			stmt.close();
			resultSet.close();

			closeDatabase();
		}

		catch (Exception e)
		{
			e.printStackTrace();
			LoggerWrapper.myLogger.severe(e.toString());
			System.exit(0);
		}

		return teamsResults;
	}

	/**
	 * Get a list of teams results based on their name, and their Score for each tournament
	 *
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @return list of team results including name, and Score for each tournament
	 */
	private static ArrayList<TeamResult> getTeamResultsByTournament(TypeOfResult typeOfResult)
	{
		ArrayList<TeamResult> teamsResults = new ArrayList<TeamResult>();

		try
		{
			ResultSet resultSet;
			openDatabase();
			Statement statement = connection.createStatement();

			resultSet = statement.executeQuery(teamResultsGetScoresByTournamentQuery(typeOfResult));

			while (resultSet.next())
			{
				if ((teamsResults.size() == 0) || (teamsResults.get(teamsResults.size() - 1)
						.hasSameName(resultSet.getString("SchoolName_fkey")) == false))
				{
					teamsResults.add(new TeamResult(resultSet.getString("SchoolName_fkey")));
				}

				teamsResults.get(teamsResults.size() - 1).addScore(resultSet.getFloat("SumScoresByTournament"));
			}

			resultSet.close();
			statement.close();

			closeDatabase();
		}

		catch (Exception e)
		{
			e.printStackTrace();
			LoggerWrapper.myLogger.severe(e.toString());
			System.exit(0);
		}

		return teamsResults;
	}

	/**
	 * SQL query that returns the list of players and their best tournament scores
	 *
	 * @return list of players and their best tournament scores
	 */
	public static String individualResultsBestScores()
	{
		return "SELECT PlayerId_fkey, score, tournamentNumber, rank() "
				+ "over (PARTITION BY PlayerId_fkey order by Score desc) AS ScoreRank " + "FROM ( "
				+ "SELECT PlayerId_fkey, score, TournamentNumber "
				+ "FROM Result LEFT JOIN Player ON Player.id=Result.PlayerId_fkey "
				+ "WHERE Result.Category=? AND Gender=? AND TypeOfPlay=? AND score > 0 ) tBestIndividualScoresWithDuplicates "
				+ "GROUP BY Playerid_fkey, score, tournamentNumber) tBestIndividualScores ";
	}

	/**
	 * SQL query that returns a list of players and their most recent school
	 *
	 * @return list of players and their most recent school
	 */
	public static String individualResultsGetSchool(TypeOfPlay typeOfPlay)
	{
		return "SELECT Name, PlayerId_fkey, SchoolName_fkey " + "FROM ( "
				+ "SELECT Name, PlayerId_fkey, SchoolName_fkey, TournamentNumber, ROW_NUMBER() over "
				+ "(PARTITION by PlayerId_fkey order by TournamentNumber desc) AS ScoreRank "
				+ "FROM Result LEFT JOIN player on Result.PlayerID_fkey = player.id "
				+ "WHERE Score > 0 AND TypeOfPlay='" + typeOfPlay.text() + "' "
				+ "ORDER BY TournamentNumber DESC) tschool " + "WHERE ScoreRank=1) tschool2 ";
	}

	/**
	 * Opens the database
	 */
	public static void openDatabase()
	{
		connection = null;

		try
		{
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Badminton", "postgres",
					"postgrespass");
			connection.setAutoCommit(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LoggerWrapper.myLogger.severe(e.toString());
			System.exit(0);
		}

//		System.out.println("Database opened successfully");
	}

	/**
	 * SQL query to obtain a list of schools based on their overall rank and their total score over all tournaments
	 *
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @return sql query
	 */
	public static String teamResultsGetOverallRankBySchoolQuery(TypeOfResult typeOfResult)
	{
		return "SELECT SchoolName_fkey, TotalScoreBySchool, RANK() over (order by TotalScoreBySchool desc) AS OverallRank "
				+ "FROM ( " + "SELECT SchoolName_fkey, SUM(SumScoresByTournament) AS TotalScoreBySchool " + "FROM ( "
				+ teamResultsGetScoresByTournamentQuery(typeOfResult) + ") tresultsbytournament "
				+ "WHERE TournamentNumber >= 2 " + "GROUP BY SchoolName_fkey "
				+ "ORDER BY TotalScoreBySchool DESC) trankbyschool";
	}

	/**
	 * SQL query to obtain a list of schools and all of their tournament scores
	 *
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @return sql query
	 */
	public static String teamResultsGetScoresByTournamentQuery(TypeOfResult typeOfResult)
	{
		return "SELECT SchoolName_fkey, SumScoresByTournament, TournamentNumber " + "FROM( "
				+ "SELECT SchoolName_fkey, AverageSchoolScores, sum(AverageSchoolScores) "
				+ "over (partition by SchoolName_fkey, TournamentNumber) as SumScoresByTournament, TournamentNumber "
				+ "FROM ( " + teamResultGetScoresByTypeOfPlayQuery(TypeOfPlay.SIMPLE, typeOfResult) + "UNION ALL "
				+ teamResultGetScoresByTypeOfPlayQuery(TypeOfPlay.DOUBLE, typeOfResult) + ") tinside) tinside2 "
				+ "WHERE SchoolName_fkey != 'CSD' AND SumScoresByTournament > 0 "
				+ "GROUP BY SchoolName_fkey, TournamentNumber, SumScoresByTournament "
				+ "ORDER BY SchoolName_fkey, TournamentNumber";
	}

	/**
	 * SQL query to obtain the sum of the top 5 players for each school for both single and double
	 *
	 * @param typeOfPlay
	 *            type of play to write (ex: Single or Double)
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @return sql query
	 */
	public static String teamResultGetScoresByTypeOfPlayQuery(TypeOfPlay typeOfPlay, TypeOfResult typeOfResult)
	{
		return "SELECT SchoolName_fkey, NumberPlayers, SumSchoolScores, TournamentNumber, "
				+ "CASE WHEN NumberPlayers = 1 THEN SumSchoolScores / 2 ELSE SumSchoolScores / NumberPlayers END "
				+ "AS AverageSchoolScores " + "FROM( "
				+ "SELECT SchoolName_fkey, sum(SumIndividualScores) as SumSchoolScores, count(SumIndividualScores) "
				+ "as NumberPlayers, TournamentNumber " + "FROM( "
				+ "SELECT PlayerId_fkey, SchoolName_fkey, SumIndividualScores, TournamentNumber " + "FROM( "
				+ "SELECT PlayerId_fkey, SchoolName_fkey, SumIndividualScores, TournamentNumber, "
				+ "ROW_NUMBER() over (PARTITION BY SchoolName_fkey, TournamentNumber order by SumIndividualScores desc) "
				+ "AS ScoreRank " + "FROM( "
				+ " SELECT PlayerId_fkey, SchoolName_fkey, SUM(score) as SumIndividualScores, TournamentNumber "
				+ " FROM ( " + "SELECT PlayerId_fkey, SchoolName_fkey, score, tournamentNumber, rank() "
				+ "over (PARTITION BY PlayerId_fkey order by Score desc) AS ScoreRank " + "FROM ( "
				+ "SELECT PlayerId_fkey, SchoolName_fkey, score, TournamentNumber "
				+ "FROM Result LEFT JOIN Player ON Player.Id=Result.PlayerId_fkey " + "WHERE Result.Category='"
				+ typeOfResult.category().text() + "' AND Gender='" + typeOfResult.gender().text()
				+ "' AND TypeOfPlay='" + typeOfPlay.text() + "' AND score > 0 ) tBestIndividualScoresWithDuplicates "
				+ "GROUP BY PlayerId_fkey, SchoolName_fkey, score, tournamentNumber) tBestIndividualScores "
				+ "GROUP BY SchoolName_fkey, PlayerId_fkey, TournamentNumber " + "ORDER BY PlayerId_fkey) t1 "
				+ "GROUP BY t1.PlayerId_fkey, t1.SchoolName_fkey, t1.SumIndividualScores, t1.TournamentNumber) t2 "
				+ "WHERE ScoreRank >= 1 AND ScoreRank <= 5"
				+ "GROUP BY PlayerId_fkey, SchoolName_fkey, SumIndividualScores, TournamentNumber "
				+ "ORDER BY SchoolName_fkey) t3 " + "GROUP BY SchoolName_fkey, TournamentNumber) t4 ";
	}
}
