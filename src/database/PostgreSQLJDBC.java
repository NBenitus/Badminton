package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import standings.StandingsCreationHelper.TypeOfPlay;
import standings.StandingsCreationHelper.TypeOfResult;
import standings.Result;
import standings.TeamResult;
import standings.TeamResultSheet;
import standings.IndividualResult;
import standings.Player;

public class PostgreSQLJDBC
{
	private static Connection c;
	private static Statement stmt;

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
			stmt = c.createStatement();

			// Iterate over each entry of the list of players
			for (int i = 0; i < listPlayers.size(); i++)
			{

				// Check if the player / school combination exists already in the database
				PreparedStatement stm = c.prepareStatement("SELECT name FROM players WHERE name=? AND school_name=?");
				stm.setString(1, listPlayers.get(i).getName());
				stm.setString(2, listPlayers.get(i).getSchoolName());

				ResultSet rs = stm.executeQuery();

				// If no matching player / school combination, add player to the database
				if (!rs.next())
				{
					stm = c.prepareStatement("INSERT INTO players (name, school_name, gender) VALUES(?,?,?);");

					stm.setString(1, listPlayers.get(i).getName());
					stm.setString(2, listPlayers.get(i).getSchoolName());
					stm.setString(3, listPlayers.get(i).getGender().text());
					stm.executeUpdate();
				}
			}

			closeDatabase();
		}
		catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Adds a list of players to the database
	 *
	 * @param listPlayers
	 *            list of players from the XLS file
	 */
	public static void addPlayerAlternate(ArrayList<Player> listPlayers)
	{
		try
		{
			openDatabase();
			stmt = c.createStatement();

			// Iterate over each entry of the list of players
			for (int i = 0; i < listPlayers.size(); i++)
			{
				// Check if the player / school combination exists already in the database
				PreparedStatement stm = c.prepareStatement("SELECT name FROM players_alternate WHERE name=? AND school_name=?");
				stm.setString(1, listPlayers.get(i).getName());
				stm.setString(2, listPlayers.get(i).getSchoolName());

				ResultSet rs = stm.executeQuery();

				// If no matching player / school combination, add player to the database
				if (!rs.next())
				{
					stm = c.prepareStatement("INSERT INTO players_alternate (name, school_name, gender, category) VALUES(?,?,?,?);");

					stm.setString(1, listPlayers.get(i).getName());
					stm.setString(2, listPlayers.get(i).getSchoolName());
					stm.setString(3, listPlayers.get(i).getGender().text());
					stm.setString(4, listPlayers.get(i).getCategory().text());
					stm.executeUpdate();
				}
			}

			closeDatabase();
		}
		catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Adds a list of results to the database
	 *
	 * @param listPlayers
	 *            list of results from the XLS file
	 */
	@SuppressWarnings("resource")
	public static void addResult(ArrayList<Result> listResults)
	{
		try
		{
			openDatabase();
			stmt = c.createStatement();

			// Iterate over each entry of the list of results
			for (int i = 0; i < listResults.size(); i++)
			{
				// Add a result entry (name, schoolName, score) for each tournament
				for (int j = 1; j <= TeamResultSheet.NUMBEROFTOURNAMENTS; j++)
				{
					// Check if the result exists already in the database
					PreparedStatement stm = c.prepareStatement("SELECT player_name_fkey FROM "
							+ listResults.get(i).getTypeOfPlay().text().toLowerCase()
							+ " WHERE player_name_fkey=? AND category=? AND tournament_number=?");
					stm.setString(1, listResults.get(i).getName());
					stm.setString(2, listResults.get(i).getCategory());
					stm.setInt(3, j);

					ResultSet rs = stm.executeQuery();

					// If no matching result, add it to the database
					if (!rs.next())
					{
						// No result added for the Demi-Joueur entry, only used in the previous template
						if (!listResults.get(i).getName().equals("DEMI-JOUEUR"))
						{
							stm = c.prepareStatement(
									"INSERT INTO " + listResults.get(i).getTypeOfPlay().text().toLowerCase()
											+ "(player_name_fkey, school_name_fkey, category, tournament_number, score) "
											+ "VALUES(?,?,?,?,?);");

							stm.setString(1, listResults.get(i).getName());
							stm.setString(2, listResults.get(i).getSchoolName());
							stm.setString(3, listResults.get(i).getCategory());
							stm.setInt(4, j);
							stm.setInt(5, listResults.get(i).getScores().get(j - 1));

							stm.executeUpdate();
						}
					}

					// If there is already a matching result, update it with the new value
					else
					{
						stm = c.prepareStatement("UPDATE "
								+ listResults.get(i).getTypeOfPlay().text().toLowerCase()
								+ " SET score=? WHERE player_name_fkey=? AND school_name_fkey=? AND category=? "
								+ "AND tournament_number=?;");

						stm.setInt(1, listResults.get(i).getScores().get(j - 1));
						stm.setString(2, listResults.get(i).getName());
						stm.setString(3, listResults.get(i).getSchoolName());
						stm.setString(4, listResults.get(i).getCategory());
						stm.setInt(5, j);

						stm.executeUpdate();
					}
				}
			}

			closeDatabase();
		}
		catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}


//	public static void addSchool(ArrayList<String> newSchool)
//	{
//		try
//		{
//			openDatabase();
//			stmt = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
//
//			ResultSet rs = stmt.executeQuery(
//					"SELECT * FROM schools WHERE abbreviation='" + newSchool.get(SchoolsPanel.ABBREVIATION) + "';");
//			rs.last();
//
//			if (rs.getRow() <= 0)
//			{
//				PreparedStatement stm = c.prepareStatement(
//						"INSERT INTO schools (name, abbreviation, address, status)" + "VALUES(?,?,?,'active');");
//				stm.setString(1, newSchool.get(SchoolsPanel.NAME));
//				stm.setString(2, newSchool.get(SchoolsPanel.ABBREVIATION));
//				stm.setString(3, newSchool.get(SchoolsPanel.ADDRESS));
//				stm.executeUpdate();
//			}
//			else
//			{
//				PreparedStatement stm = c.prepareStatement(
//						"UPDATE schools SET name=?,address=?," + "status='active' WHERE abbreviation=?;");
//				stm.setString(1, newSchool.get(SchoolsPanel.NAME));
//				stm.setString(2, newSchool.get(SchoolsPanel.ADDRESS));
//				stm.setString(3, newSchool.get(SchoolsPanel.ABBREVIATION));
//				stm.executeUpdate();
//			}
//
//			closeDatabase();
//		}
//		catch (Exception e)
//		{
//			System.err.println(e.getClass().getName() + ": " + e.getMessage());
//			System.exit(0);
//		}
//	}

	/**
	 * Close the database
	 */
	public static void closeDatabase()
	{
		try
		{
			stmt.close();
			c.close();
		}
		catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Database closed successfully");
	}

	/**
	 * Close the database
	 * @param resultSet
	 *            result set used to query the database, which needs to be closed
	 */
	public static void closeDatabase(ResultSet resultSet)
	{
		try
		{
			resultSet.close();
			stmt.close();
			c.close();
		}
		catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Database closed successfully");
	}

//	public static ArrayList<ArrayList<String>> getPlayer(TypeOfResult typeOfResult)
//	{
//		ArrayList<ArrayList<String>> listPlayers = new ArrayList<ArrayList<String>>();
//		ArrayList<String> listNames = new ArrayList<String>();
//		ArrayList<String> listAbbreviations = new ArrayList<String>();
//
//		try
//		{
//			openDatabase();
//			stmt = c.createStatement();
//			PreparedStatement stm = c.prepareStatement("SELECT * FROM players WHERE gender=?;");
//			stm.setString(1, typeOfResult.gender().text());
//			ResultSet rs = stm.executeQuery();
//
//			while (rs.next())
//			{
//				String firstName = rs.getString("firstname");
//				String dateOfBirth = rs.getString("dateofbirth");
//				String abbreviation = rs.getString("school_id");
//
//				if (Utilities.compareDates(dateOfBirth, typeOfResult.category()) == true)
//				{
//					listNames.add(firstName);
//					listAbbreviations.add(abbreviation);
//				}
//			}
//			closeDatabase(rs);
//		}
//		catch (Exception e)
//		{
//			System.err.println(e.getClass().getName() + ": " + e.getMessage());
//			System.exit(0);
//		}
//
//		listPlayers.add(listNames);
//		listPlayers.add(listAbbreviations);
//
//		return listPlayers;
//	}

//	public static ArrayList<ArrayList<String>> getListActiveSchools()
//	{
//		ArrayList<ArrayList<String>> listSchools = new ArrayList<ArrayList<String>>();
//		ArrayList<String> listNames = new ArrayList<String>();
//		ArrayList<String> listAbbreviations = new ArrayList<String>();
//
//		try
//		{
//			openDatabase();
//			stmt = c.createStatement();
//			ResultSet rs = stmt.executeQuery("SELECT * FROM schools WHERE status='active';");
//
//			while (rs.next())
//			{
//				String name = rs.getString("name");
//				String abbreviation = rs.getString("abbreviation");
//				listNames.add(name);
//				listAbbreviations.add(abbreviation);
//			}
//			closeDatabase(rs);
//		}
//		catch (Exception e)
//		{
//			System.err.println(e.getClass().getName() + ": " + e.getMessage());
//			System.exit(0);
//		}
//		listSchools.add(listNames);
//		listSchools.add(listAbbreviations);
//
//		return listSchools;
//	}

	/**
	 * Get a list of individual results based on the type of result needed
	 *
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @param typeOfPlay
	 *            type of play to write (ex: Single)
	 * @return list of individual results
	 */
	public static ArrayList<IndividualResult> getIndividualResults(TypeOfResult typeOfResult, TypeOfPlay typeOfPlay)
	{
		ArrayList<IndividualResult> individualResults = new ArrayList<IndividualResult>();

		try
		{
			ResultSet rs;
			openDatabase();
			stmt = c.createStatement();

			// Get individual results for either Single or Double play
			if (!(typeOfPlay == TypeOfPlay.COMBINED))
			{
				String typeOfPlayString = typeOfPlay.text().toLowerCase();

				rs = stmt.executeQuery(
						"SELECT player_name_fkey, school_name_fkey, sum_scores, RANK() over (order by sum_scores desc) AS overallrank " +
						"FROM ( " +
							"SELECT player_name_fkey, school_name_fkey, sum(coalesce(t1.score, 0)) AS sum_scores " +
								"FROM (" +
								"SELECT player_name_fkey, school_name_fkey, score, " +
								"rank() over (PARTITION BY player_name_fkey order by score desc) AS scorerank " +
									"FROM " + typeOfPlayString + " LEFT JOIN players ON players.name=" + typeOfPlayString
									+ ".player_name_fkey " +
								"WHERE category='" + typeOfResult.category().text() + "' AND gender='"
								+ typeOfResult.gender().text() + "') t1 " +
							"WHERE scoreRank <= 4 GROUP BY player_name_fkey, school_name_fkey " +
							"ORDER BY sum_scores DESC) ttotal;");
			}

			// Get individual results for both Single and Double play (different query needed)
			else
			{
				String whereClause = "WHERE category='" + typeOfResult.category().text() + "' AND gender='"
						+ typeOfResult.gender().text() + "' ";
				rs = stmt.executeQuery(
							"SELECT player_name_fkey, school_name_fkey, sum_scores, RANK() over (order by sum_scores desc) AS overallrank " +
							"FROM ( " +
								"SELECT player_name_fkey, school_name_fkey, score_simple, sum(score_simple) over "
								+ "(partition by player_name_fkey) as sum_scores " +
								"FROM ( " +
									"SELECT player_name_fkey, school_name_fkey, score, scorerank, SUM(score) as score_simple " +
									"FROM ( " +
										"SELECT player_name_fkey, school_name_fkey, score, rank() "
										+ "over (PARTITION BY player_name_fkey order by score desc) AS scorerank " +
										"FROM simple LEFT JOIN players on players.name=simple.player_name_fkey " +
										whereClause + " ) trank " +
									"WHERE scorerank <= 4 " +
									"GROUP BY player_name_fkey, school_name_fkey, score, scorerank " +

									"UNION ALL " +

									"SELECT player_name_fkey, school_name_fkey, score, scorerank, SUM(score) as score_simple " +
									"FROM ( " +
										"SELECT player_name_fkey, school_name_fkey, score, rank() "
										+ "over (PARTITION BY player_name_fkey order by score desc) AS scorerank " +
										"FROM double LEFT JOIN players on players.name=double.player_name_fkey " +
										whereClause + ") trank " +
									"WHERE scorerank <= 4 " +
									"GROUP BY player_name_fkey, school_name_fkey, score, scorerank ) t1) t2 " +
								"WHERE sum_scores > 0 " +
								"GROUP BY player_name_fkey, school_name_fkey, sum_scores "+
								"ORDER BY overallrank");
			}

			// Add individual result from the database to the list of individual results
			while (rs.next())
			{
				individualResults.add(new IndividualResult(rs.getString("player_name_fkey"), rs.getString("school_name_fkey")
						, rs.getInt("sum_scores"), rs.getInt("overallrank")));
			}
			closeDatabase(rs);
		}
		catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return individualResults;
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
			// Add each tournament score to each school
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
	 * Get a list of teams results based on their name, their overall score and their rank
	 *
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @return list of team results including name, overall score and rank
	 */
	private static ArrayList<TeamResult> getTeamResultsByOverallRank(TypeOfResult typeOfResult)
	{
		ArrayList<TeamResult> teamsResults = new ArrayList<TeamResult>();

		try
		{
			ResultSet rs;
			openDatabase();
			stmt = c.createStatement();

			rs = stmt.executeQuery(teamResultsGetOverallRankBySchoolQuery(typeOfResult));

			while (rs.next())
			{
				teamsResults.add(new TeamResult(rs.getString("school_name_fkey"), rs.getInt("overallRank"),
						rs.getFloat("total_score_by_school")));
			}
		}

		catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return teamsResults;
	}

	/**
	 * Get a list of teams results based on their name, and their score for each tournament
	 *
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @return list of team results including name, and score for each tournament
	 */
	private static ArrayList<TeamResult> getTeamResultsByTournament(TypeOfResult typeOfResult)
	{
		ArrayList<TeamResult> teamsResults = new ArrayList<TeamResult>();

		try
		{
			ResultSet rs;
			openDatabase();
			stmt = c.createStatement();

			rs = stmt.executeQuery(teamResultsGetScoresByTournamentQuery(typeOfResult));

			while (rs.next())
			{
				if ((teamsResults.size() == 0) ||
						(teamsResults.get(teamsResults.size() - 1).hasSameName(rs.getString("school_name_fkey")) == false))
				{
					teamsResults.add(new TeamResult(rs.getString("school_name_fkey")));
				}

				teamsResults.get(teamsResults.size() - 1).addScore(rs.getFloat("sum_scores_by_tournament"));
			}
		}

		catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return teamsResults;
	}

	/**
	 * Open the database
	 */
	public static void openDatabase()
	{
		c = null;
		stmt = null;
		try
		{
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Badminton", "postgres", "postgrespass");
			c.setAutoCommit(true);
		}
		catch (Exception e)
		{
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Database opened successfully");
	}

//	public static void removeSchools(ArrayList<String> schoolsToRemove)
//	{
//		try
//		{
//			openDatabase();
//			stmt = c.createStatement();
//
//			for (int i = 0; i < schoolsToRemove.size(); i++)
//			{
//				int openParentesis = schoolsToRemove.get(i).indexOf("(");
//				int closeParentesis = schoolsToRemove.get(i).indexOf(")");
//				String abbreviation = schoolsToRemove.get(i).substring(openParentesis + 1, closeParentesis);
//
//				stmt.executeUpdate("UPDATE schools SET status='inactive' WHERE abbreviation='" + abbreviation + "';");
//			}
//
//			closeDatabase();
//		}
//		catch (Exception e)
//		{
//			System.err.println(e.getClass().getName() + ": " + e.getMessage());
//			System.exit(0);
//		}
//	}

	/**
	 * SQL query to obtain a list of schools based on their overall rank and their total score over all tournaments
	 *
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @return sql query
	 */
	public static String teamResultsGetOverallRankBySchoolQuery(TypeOfResult typeOfResult)
	{
		return "SELECT school_name_fkey, total_score_by_school, RANK() over (order by total_score_by_school desc) AS overallrank " +
				"FROM ( " +
				"SELECT school_name_fkey, SUM(sum_scores_by_tournament) AS total_score_by_school " +
				"FROM ( " + teamResultsGetScoresByTournamentQuery(typeOfResult) +
				") tresultsbytournament " +
				"WHERE tournament_number >= 2 " +
				"GROUP BY school_name_fkey " +
				"ORDER BY total_score_by_school DESC) trankbyschool";
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
		return "SELECT school_name_fkey, sum_scores_by_tournament, tournament_number " +
		"FROM( " +
			"SELECT school_name_fkey, average_school_scores, sum(average_school_scores) " +
			"over (partition by school_name_fkey, tournament_number) as sum_scores_by_tournament, tournament_number " +
			"FROM ( " +
				teamResultGetScoresByTypeOfPlayQuery("simple", typeOfResult) +
			"UNION ALL " +
				teamResultGetScoresByTypeOfPlayQuery("double", typeOfResult) +
				") tinside) tinside2 " +
		"WHERE school_name_fkey != 'CSD' AND sum_scores_by_tournament > 0 " +
		"GROUP BY school_name_fkey, tournament_number, sum_scores_by_tournament " +
		"ORDER BY school_name_fkey, tournament_number";
	}

	/**
	 * SQL query to obtain the sum of the top 5 players for each school for both single and double
	 *
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @return sql query
	 */
	public static String teamResultGetScoresByTypeOfPlayQuery(String databaseName, TypeOfResult typeOfResult)
	{
		return "SELECT school_name_fkey, number_players, sum_school_scores, tournament_number, " +
		"CASE WHEN number_players = 1 THEN sum_school_scores / 2 ELSE sum_school_scores / number_players END " +
		"AS average_school_scores " +
	"FROM( " +
		"SELECT school_name_fkey, sum(sum_individual_scores) as sum_school_scores, count(sum_individual_scores) " +
		"as number_players, tournament_number " +
		"FROM( " +
			"SELECT player_name_fkey, school_name_fkey, sum_individual_scores, tournament_number " +
			"FROM( " +
				"SELECT player_name_fkey, school_name_fkey, sum_individual_scores, tournament_number, " +
					"ROW_NUMBER() over (PARTITION BY school_name_fkey, tournament_number order by sum_individual_scores desc) " +
					"AS scorerank " +
				"FROM( " +
					"SELECT player_name_fkey, school_name_fkey, SUM(score) as sum_individual_scores, tournament_number " +
					"FROM " + databaseName + " LEFT JOIN players on players.name=" + databaseName + ".player_name_fkey " +
					"WHERE category='" + typeOfResult.category().text() + "' AND gender='" +  typeOfResult.gender().text() +
					"' AND score > 0 " +
					"GROUP BY player_name_fkey, school_name_fkey, tournament_number) t1 " +
				"GROUP BY t1.player_name_fkey, t1.school_name_fkey, t1.sum_individual_scores, t1.tournament_number) t2 " +
			"WHERE scoreRank >= 1 AND scoreRank <= 5" +
			"GROUP BY player_name_fkey, school_name_fkey, sum_individual_scores, tournament_number " +
			"ORDER BY school_name_fkey) t3 " +
			"GROUP BY school_name_fkey, tournament_number) t4 ";
	}
}
