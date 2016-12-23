package database;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import standings.IndividualCombinedResult;
import standings.IndividualResult;
import standings.Player;
import standings.Result;
import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.Gender;
import standings.StandingsCreationHelper.TypeOfPlay;
import standings.StandingsCreationHelper.TypeOfResult;
import standings.TeamResult;

import static com.ninja_squad.dbsetup.Operations.*;

public class PostgreSQLJDBCTest
{
	private static final String TEST_DB_URL = "jdbc:postgresql://localhost:5432/Badminton";
	private static final String TEST_DB_USER = "postgres";
	private static final String TEST_DB_PASSWORD = "postgrespass";

	private static DbSetupTracker dbSetupTracker = new DbSetupTracker();
	// dbSetupTracker.skipNextLaunch();

	@Before
	public void prepare() throws Exception
	{
		Operation operation = sequenceOf(CommonOperations.DELETE_ALL, CommonOperations.INSERT_REFERENCE_DATA);

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();
	}

	@Test
	public void testAddPlayer() throws SQLException
	{
		ArrayList<Player> mockPlayers = new ArrayList<Player>();

		Player mockPlayer = new Player("DARD12345678", "Dare Devil", "Harvard", Gender.MASCULIN, Category.BENJAMIN);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("HUKT23456789", "The Hulk", "MIT", Gender.MASCULIN, Category.JUVÉNILE);
		mockPlayers.add(mockPlayer);

		PostgreSQLJDBC.addPlayer(mockPlayers);
	}

	@Test(expected = SQLException.class)
	public void testAddPlayerFail_WrongSchool() throws SQLException
	{
		ArrayList<Player> mockPlayers = new ArrayList<Player>();

		Player mockPlayer = new Player("DARD12345678", "Dare Devil", "Wrong School", Gender.MASCULIN,
				Category.BENJAMIN);
		mockPlayers.add(mockPlayer);

		PostgreSQLJDBC.addPlayer(mockPlayers);
	}

	@Test(expected = SQLException.class)
	public void testAddPlayerFail_DuplicateID() throws SQLException
	{
		ArrayList<Player> mockPlayers = new ArrayList<Player>();

		Player mockPlayer = new Player("DARD12345678", "Dare Devil", "Wrong Harvard", Gender.MASCULIN,
				Category.BENJAMIN);
		mockPlayers.add(mockPlayer);
		mockPlayers.add(mockPlayer);

		PostgreSQLJDBC.addPlayer(mockPlayers);
	}

	@Test
	public void testAddResult() throws SQLException
	{
		ArrayList<Result> mockResults = new ArrayList<Result>();
		ArrayList<Integer> mockScores = new ArrayList<Integer>();
		mockScores.add(20);
		mockScores.add(10);
		mockScores.add(30);
		mockScores.add(40);
		mockScores.add(50);

		Result mockResult = new Result("AMECY92570003", "Princeton", Category.JUVÉNILE, TypeOfPlay.SINGLE, mockScores);
		mockResults.add(mockResult);

		PostgreSQLJDBC.addResult(mockResults);

	}

	@Test(expected = SQLException.class)
	public void testAddResultFail() throws SQLException
	{
		ArrayList<Result> mockResults = new ArrayList<Result>();
		ArrayList<Integer> mockScores = new ArrayList<Integer>();
		mockScores.add(20);
		mockScores.add(10);
		mockScores.add(30);
		mockScores.add(40);
		mockScores.add(50);

		Result mockResult = new Result("AMECY92570003", "Wrong school", Category.JUVÉNILE, TypeOfPlay.SINGLE,
				mockScores);
		mockResults.add(mockResult);

		PostgreSQLJDBC.addResult(mockResults);
	}

	@Test
	public void testClearTable() throws SQLException
	{
		PostgreSQLJDBC.clearTable("Result");
		PostgreSQLJDBC.clearTable("Player");

		assertEquals(PostgreSQLJDBC.getAllPlayers().size(), 0);
	}

	@Test(expected = SQLException.class)
	public void testClearTableFail() throws SQLException
	{
		PostgreSQLJDBC.clearTable("WrongTableName");
	}

	@Test
	public void testCloseDatabase() throws SQLException
	{
		PostgreSQLJDBC.openDatabase();
		PostgreSQLJDBC.closeDatabase();
	}

	@Test
	public void testGetIndividualResults_SingleFiveTournaments() throws SQLException
	{
		Operation operation = sequenceOf(
				insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
						.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin").build(),
				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 40, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 45, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 30, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 10, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 65, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 25, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 30, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 22, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 35, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 26, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 40, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 30, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 55, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 35, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 35, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 15, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 20, "Double").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<IndividualResult> mockIndividualResults = new ArrayList<IndividualResult>();

		IndividualResult mockIndividualResult = new IndividualResult("POOD67100407", "Dead Pool", "Harvard", 200, 1);
		mockIndividualResults.add(mockIndividualResult);

		mockIndividualResult = new IndividualResult("DARD12345678", "Dare Devil", "Harvard", 160, 2);
		mockIndividualResults.add(mockIndividualResult);

		assertEquals(PostgreSQLJDBC.getIndividualResults(TypeOfResult.BENJAMIN_MASCULIN, TypeOfPlay.SINGLE),
				mockIndividualResults);
	}

	@Test
	public void testGetIndividualResults_SingleFourTournaments() throws SQLException
	{
		Operation operation = sequenceOf(
				insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
						.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin").build(),
				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 40, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 45, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 30, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 10, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 0, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 0, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 30, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 22, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 35, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 26, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 40, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 30, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 0, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 0, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 35, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 15, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 0, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 0, "Double").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<IndividualResult> mockIndividualResults = new ArrayList<IndividualResult>();

		IndividualResult mockIndividualResult = new IndividualResult("POOD67100407", "Dead Pool", "Harvard", 165, 1);
		mockIndividualResults.add(mockIndividualResult);

		mockIndividualResult = new IndividualResult("DARD12345678", "Dare Devil", "Harvard", 125, 2);
		mockIndividualResults.add(mockIndividualResult);

		assertEquals(PostgreSQLJDBC.getIndividualResults(TypeOfResult.BENJAMIN_MASCULIN, TypeOfPlay.SINGLE),
				mockIndividualResults);
	}

	@Test
	public void testGetIndividualResults_SinglePlayerChangedSchool() throws SQLException
	{
		Operation operation = sequenceOf(
				insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
						.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin")
						.values("DARD12345678", "Dare Devil", "MIT", "Masculin", "Benjamin").build(),
				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 40, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 45, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 30, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 10, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 65, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 25, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 30, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 22, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 35, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 26, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 40, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 30, "Double")
						.values("DARD12345678", "MIT", "Benjamin", 4, 20, "Simple")
						.values("DARD12345678", "MIT", "Benjamin", 4, 20, "Double")
						.values("DARD12345678", "MIT", "Benjamin", 5, 55, "Simple")
						.values("DARD12345678", "MIT", "Benjamin", 5, 35, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 35, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 15, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 20, "Double")
						.build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<IndividualResult> mockIndividualResults = new ArrayList<IndividualResult>();

		IndividualResult mockIndividualResult = new IndividualResult("POOD67100407", "Dead Pool", "Harvard", 200, 1);
		mockIndividualResults.add(mockIndividualResult);

		mockIndividualResult = new IndividualResult("DARD12345678", "Dare Devil", "MIT", 160, 2);
		mockIndividualResults.add(mockIndividualResult);

		assertEquals(PostgreSQLJDBC.getIndividualResults(TypeOfResult.BENJAMIN_MASCULIN, TypeOfPlay.SINGLE),
				mockIndividualResults);
	}

	@Test
	public void testGetIndividualResults_Double() throws SQLException
	{
		Operation operation = sequenceOf(
				insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
						.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin").build(),
				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 40, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 45, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 30, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 10, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 65, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 25, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 30, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 22, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 35, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 26, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 40, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 30, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 55, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 35, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 35, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 15, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 20, "Double").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<IndividualResult> mockIndividualResults = new ArrayList<IndividualResult>();

		IndividualResult mockIndividualResult = new IndividualResult("DARD12345678", "Dare Devil", "Harvard", 113, 1);
		mockIndividualResults.add(mockIndividualResult);

		mockIndividualResult = new IndividualResult("POOD67100407", "Dead Pool", "Harvard", 73, 2);
		mockIndividualResults.add(mockIndividualResult);

		assertEquals(PostgreSQLJDBC.getIndividualResults(TypeOfResult.BENJAMIN_MASCULIN, TypeOfPlay.DOUBLE),
				mockIndividualResults);
	}

	@Test
	public void testGetIndividualResults_CombinedDifferentRank() throws SQLException
	{
		Operation operation = sequenceOf(
				insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
						.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin").build(),
				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 45, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 30, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 10, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 65, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 25, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 30, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 22, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 35, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 26, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 40, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 30, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 55, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 35, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 35, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 15, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 20, "Double").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<IndividualCombinedResult> mockIndividualResults = new ArrayList<IndividualCombinedResult>();

		IndividualCombinedResult mockIndividualResult = new IndividualCombinedResult("POOD67100407", "Dead Pool",
				"Harvard", 210, 73, 283, 1);
		mockIndividualResults.add(mockIndividualResult);

		mockIndividualResult = new IndividualCombinedResult("DARD12345678", "Dare Devil", "Harvard", 160, 113, 273, 2);
		mockIndividualResults.add(mockIndividualResult);

		// ArrayList<IndividualResult> test = PostgreSQLJDBC.getIndividualResults(TypeOfResult.BENJAMIN_MASCULIN,
		// TypeOfPlay.COMBINED);

		assertEquals(PostgreSQLJDBC.getIndividualResults(TypeOfResult.BENJAMIN_MASCULIN, TypeOfPlay.COMBINED),
				mockIndividualResults);
	}

	@Test
	public void testGetIndividualResults_CombinedSameRank() throws SQLException
	{
		Operation operation = sequenceOf(
				insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
						.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin").build(),
				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 40, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 45, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 30, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 10, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 65, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 25, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 30, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 22, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 35, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 26, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 40, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 30, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 55, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 35, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 35, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 15, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 20, "Double").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<IndividualCombinedResult> mockIndividualResults = new ArrayList<IndividualCombinedResult>();

		IndividualCombinedResult mockIndividualResult = new IndividualCombinedResult("DARD12345678", "Dare Devil",
				"Harvard", 160, 113, 273, 1);
		mockIndividualResults.add(mockIndividualResult);

		mockIndividualResult = new IndividualCombinedResult("POOD67100407", "Dead Pool", "Harvard", 200, 73, 273, 1);
		mockIndividualResults.add(mockIndividualResult);

		assertEquals(PostgreSQLJDBC.getIndividualResults(TypeOfResult.BENJAMIN_MASCULIN, TypeOfPlay.COMBINED),
				mockIndividualResults);
	}

	@Test
	public void testGetIndividualResults_CombinedMissingSingleEntry() throws SQLException
	{
		Operation operation = sequenceOf(
				insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
						.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin").build(),
				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Cadet", 1, 40, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Cadet", 2, 45, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "Harvard", "Cadet", 3, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "Harvard", "Cadet", 4, 30, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 10, "Double")
						.values("POOD67100407", "Harvard", "Cadet", 5, 65, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 25, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 30, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 22, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 35, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 26, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 40, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 30, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 55, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 35, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 35, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 15, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 20, "Double").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<IndividualCombinedResult> mockIndividualResults = new ArrayList<IndividualCombinedResult>();

		IndividualCombinedResult mockIndividualResult = new IndividualCombinedResult("DARD12345678", "Dare Devil",
				"Harvard", 160, 113, 273, 1);
		mockIndividualResults.add(mockIndividualResult);

		mockIndividualResult = new IndividualCombinedResult("POOD67100407", "Dead Pool", "Harvard", 0, 73, 73, 2);
		mockIndividualResults.add(mockIndividualResult);

		assertEquals(PostgreSQLJDBC.getIndividualResults(TypeOfResult.BENJAMIN_MASCULIN, TypeOfPlay.COMBINED),
				mockIndividualResults);
	}

	@Test
	public void testGetIndividualResults_CombinedMissingDoubleEntry() throws SQLException
	{
		Operation operation = sequenceOf(
				insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
						.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin").build(),
				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 40, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 45, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 30, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 10, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 65, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 25, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 30, "Simple")
						.values("DARD12345678", "Harvard", "Cadet", 1, 22, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 35, "Simple")
						.values("DARD12345678", "Harvard", "Cadet", 2, 26, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 40, "Simple")
						.values("DARD12345678", "Harvard", "Cadet", 3, 30, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Simple")
						.values("DARD12345678", "Harvard", "Cadet", 4, 20, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 55, "Simple")
						.values("DARD12345678", "Harvard", "Cadet", 5, 35, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 35, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 15, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 20, "Double").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<IndividualCombinedResult> mockIndividualResults = new ArrayList<IndividualCombinedResult>();

		IndividualCombinedResult mockIndividualResult = new IndividualCombinedResult("POOD67100407", "Dead Pool",
				"Harvard", 200, 73, 273, 1);
		mockIndividualResults.add(mockIndividualResult);

		mockIndividualResult = new IndividualCombinedResult("DARD12345678", "Dare Devil", "Harvard", 160, 0, 160, 2);
		mockIndividualResults.add(mockIndividualResult);

		assertEquals(PostgreSQLJDBC.getIndividualResults(TypeOfResult.BENJAMIN_MASCULIN, TypeOfPlay.COMBINED),
				mockIndividualResults);
	}

	@Test
	public void testGetIndividualResults_CombinedPlayerChangedSchool() throws SQLException
	{
		Operation operation = sequenceOf(
				insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
						.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin")
						.values("POOD67100407", "Dead Pool", "MIT", "Masculin", "Benjamin").build(),
				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 40, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 45, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "MIT", "Benjamin", 3, 50, "Simple")
						.values("POOD67100407", "MIT", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "MIT", "Benjamin", 4, 30, "Simple")
						.values("POOD67100407", "MIT", "Benjamin", 4, 10, "Double")
						.values("POOD67100407", "MIT", "Benjamin", 5, 65, "Simple")
						.values("POOD67100407", "MIT", "Benjamin", 5, 25, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 30, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 22, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 35, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 26, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 40, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 30, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 55, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 35, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 35, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 15, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 20, "Double").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<IndividualCombinedResult> mockIndividualResults = new ArrayList<IndividualCombinedResult>();

		IndividualCombinedResult mockIndividualResult = new IndividualCombinedResult("DARD12345678", "Dare Devil",
				"Harvard", 160, 113, 273, 1);
		mockIndividualResults.add(mockIndividualResult);

		mockIndividualResult = new IndividualCombinedResult("POOD67100407", "Dead Pool", "MIT", 200, 73, 273, 1);
		mockIndividualResults.add(mockIndividualResult);

		assertEquals(PostgreSQLJDBC.getIndividualResults(TypeOfResult.BENJAMIN_MASCULIN, TypeOfPlay.COMBINED),
				mockIndividualResults);
	}

	@Test
	public void testGetNumberOfPools() throws SQLException
	{
		// To do
	}

	@Test
	public void testGetPlayersByFilter() throws SQLException
	{
		Operation operation = sequenceOf(insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
				.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin")
				.values("STRD23456789", "Dr Strange", "Harvard", "Masculin", "Cadet")
				.values("HULT34567890", "The Hulk", "Harvard", "Masculin", "Juvénile")
				.values("HULS67100202", "She Hulk", "Harvard", "Féminin", "Benjamin").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<Player> mockPlayers = new ArrayList<Player>();

		Player mockPlayer = new Player("DARD12345678", "Dare Devil", "Harvard", Gender.MASCULIN, Category.BENJAMIN);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("POOD67100407", "Dead Pool", "Harvard", Gender.MASCULIN, Category.BENJAMIN);
		mockPlayers.add(mockPlayer);

		assertEquals(PostgreSQLJDBC.getPlayersByFilter("Harvard", Category.BENJAMIN, Gender.MASCULIN), mockPlayers);
	}

	@Test
	public void testGetPlayersByFilter_WrongSchool() throws SQLException
	{
		assertEquals(PostgreSQLJDBC.getPlayersByFilter("WrongSchoolName", Category.BENJAMIN, Gender.MASCULIN).size(),
				0);
	}

	@Test
	public void testGetAllPlayers() throws SQLException
	{
		Operation operation = sequenceOf(insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
				.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin")
				.values("STRD23456789", "Dr Strange", "Harvard", "Masculin", "Cadet")
				.values("HULT34567890", "The Hulk", "Harvard", "Masculin", "Juvénile")
				.values("HULS67100202", "She Hulk", "Harvard", "Féminin", "Benjamin").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<Player> mockPlayers = new ArrayList<Player>();

		Player mockPlayer = new Player("MANSJ81090109", "Spider Man", "Columbia", Gender.MASCULIN, Category.CADET);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("DARD12345678", "Dare Devil", "Harvard", Gender.MASCULIN, Category.BENJAMIN);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("POOD67100407", "Dead Pool", "Harvard", Gender.MASCULIN, Category.BENJAMIN);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("HULS67100202", "She Hulk", "Harvard", Gender.FÉMININ, Category.BENJAMIN);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("STRD23456789", "Dr Strange", "Harvard", Gender.MASCULIN, Category.CADET);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("HULT34567890", "The Hulk", "Harvard", Gender.MASCULIN, Category.JUVÉNILE);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("WIDB84120103", "Black Widow", "MIT", Gender.FÉMININ, Category.CADET);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("AMECY92570003", "Captain America", "Princeton", Gender.MASCULIN, Category.JUVÉNILE);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("WOMC67530106", "Cat Woman", "Sorbonne", Gender.FÉMININ, Category.JUVÉNILE);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("WOMW67100202", "Wonder Woman", "Yale", Gender.FÉMININ, Category.BENJAMIN);
		mockPlayers.add(mockPlayer);

		assertEquals(PostgreSQLJDBC.getAllPlayers(), mockPlayers);
	}

	@Test
	public void testGetAllSchools() throws SQLException
	{
		ArrayList<String> mockSchools = new ArrayList<String>();

		mockSchools.add("Columbia");
		mockSchools.add("Harvard");
		mockSchools.add("MIT");
		mockSchools.add("Princeton");
		mockSchools.add("Sorbonne");
		mockSchools.add("Yale");

		assertEquals(PostgreSQLJDBC.getAllSchools(), mockSchools);
	}

	@Test
	public void testGetTeamResults() throws SQLException
	{
		Operation operation = sequenceOf(insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
				.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin")
				.values("STRD23456789", "Dr Strange", "MIT", "Masculin", "Benjamin")
				.values("HULT34567890", "The Hulk", "MIT", "Masculin", "Benjamin").build(),

				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 45, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 30, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 10, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 65, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 25, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 30, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 22, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 35, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 26, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 40, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 30, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 55, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 35, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 1, 10, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 1, 12, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 2, 15, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 2, 16, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 3, 20, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 3, 20, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 4, 12, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 4, 14, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 5, 15, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 5, 15, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 1, 50, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 1, 42, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 2, 55, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 2, 46, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 3, 70, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 3, 50, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 4, 50, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 4, 60, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 5, 75, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 5, 56, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 35, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 15, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 20, "Double").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<TeamResult> mockTeamResults = new ArrayList<TeamResult>();

		TeamResult mockTeamResult = new TeamResult("MIT", 1, (float) 294.5);
		mockTeamResult.addScore((float) 57);
		mockTeamResult.addScore((float) 66);
		mockTeamResult.addScore((float) 80);
		mockTeamResult.addScore((float) 68);
		mockTeamResult.addScore((float) 80.5);

		mockTeamResults.add(mockTeamResult);

		mockTeamResult = new TeamResult("Harvard", 2, (float) 261);
		mockTeamResult.addScore((float) 57);
		mockTeamResult.addScore((float) 61);
		mockTeamResult.addScore((float) 70);
		mockTeamResult.addScore((float) 40);
		mockTeamResult.addScore((float) 90);

		mockTeamResults.add(mockTeamResult);

		assertEquals(PostgreSQLJDBC.getTeamResults(TypeOfResult.BENJAMIN_MASCULIN), mockTeamResults);
	}

	@Test
	public void testGetTeamResults_OnePlayerPerTeam() throws SQLException
	{
		Operation operation = sequenceOf(insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
				.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin")
				.values("STRD23456789", "Dr Strange", "MIT", "Masculin", "Benjamin")
				.values("HULT34567890", "The Hulk", "MIT", "Masculin", "Benjamin").build(),

				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 45, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 30, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 10, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 65, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 25, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 1, 10, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 1, 12, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 2, 15, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 2, 16, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 3, 20, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 3, 20, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 4, 12, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 4, 14, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 5, 15, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 5, 15, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 1, 50, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 1, 42, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 2, 55, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 2, 46, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 3, 70, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 3, 50, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 4, 50, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 4, 60, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 5, 75, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 5, 56, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 35, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 15, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 20, "Double").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<TeamResult> mockTeamResults = new ArrayList<TeamResult>();

		TeamResult mockTeamResult = new TeamResult("MIT", 1, (float) 294.5);
		mockTeamResult.addScore((float) 57);
		mockTeamResult.addScore((float) 66);
		mockTeamResult.addScore((float) 80);
		mockTeamResult.addScore((float) 68);
		mockTeamResult.addScore((float) 80.5);

		mockTeamResults.add(mockTeamResult);

		mockTeamResult = new TeamResult("Harvard", 2, (float) 130.5);
		mockTeamResult.addScore((float) 31);
		mockTeamResult.addScore((float) 30.5);
		mockTeamResult.addScore((float) 35);
		mockTeamResult.addScore((float) 20);
		mockTeamResult.addScore((float) 45);

		mockTeamResults.add(mockTeamResult);

		assertEquals(PostgreSQLJDBC.getTeamResults(TypeOfResult.BENJAMIN_MASCULIN), mockTeamResults);
	}

	@Test
	public void testGetTeamResults_OnePlayerWithoutSingle() throws SQLException
	{
		Operation operation = sequenceOf(insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
				.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin")
				.values("STRD23456789", "Dr Strange", "MIT", "Masculin", "Benjamin")
				.values("HULT34567890", "The Hulk", "MIT", "Masculin", "Benjamin").build(),

				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 10, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 25, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 30, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 22, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 35, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 26, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 40, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 30, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 55, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 35, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 1, 10, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 1, 12, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 2, 15, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 2, 16, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 3, 20, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 3, 20, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 4, 12, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 4, 14, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 5, 15, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 5, 15, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 1, 50, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 1, 42, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 2, 55, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 2, 46, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 3, 70, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 3, 50, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 4, 50, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 4, 60, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 5, 75, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 5, 56, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 35, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 15, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 20, "Double").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<TeamResult> mockTeamResults = new ArrayList<TeamResult>();

		TeamResult mockTeamResult = new TeamResult("MIT", 1, (float) 294.5);
		mockTeamResult.addScore((float) 57);
		mockTeamResult.addScore((float) 66);
		mockTeamResult.addScore((float) 80);
		mockTeamResult.addScore((float) 68);
		mockTeamResult.addScore((float) 80.5);

		mockTeamResults.add(mockTeamResult);

		mockTeamResult = new TeamResult("Harvard", 2, (float) 166);
		mockTeamResult.addScore((float) 32);
		mockTeamResult.addScore((float) 38.5);
		mockTeamResult.addScore((float) 45);
		mockTeamResult.addScore((float) 25);
		mockTeamResult.addScore((float) 57.5);

		mockTeamResults.add(mockTeamResult);

		assertEquals(PostgreSQLJDBC.getTeamResults(TypeOfResult.BENJAMIN_MASCULIN), mockTeamResults);
	}

	@Test
	public void testGetTeamResults_OnePlayerWithoutDouble() throws SQLException
	{
		Operation operation = sequenceOf(insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
				.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin")
				.values("STRD23456789", "Dr Strange", "MIT", "Masculin", "Benjamin")
				.values("HULT34567890", "The Hulk", "MIT", "Masculin", "Benjamin").build(),

				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 45, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 30, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 10, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 65, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 25, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 30, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 22, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 35, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 26, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 40, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 30, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 20, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 55, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 35, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 1, 10, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 1, 12, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 2, 15, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 2, 16, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 3, 20, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 3, 20, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 4, 12, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 4, 14, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 5, 15, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 5, 15, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 1, 50, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 2, 55, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 3, 70, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 4, 50, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 5, 75, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 35, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 15, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 20, "Double").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<TeamResult> mockTeamResults = new ArrayList<TeamResult>();

		TeamResult 	mockTeamResult = new TeamResult("Harvard", 1, (float) 261);
		mockTeamResult.addScore((float) 57);
		mockTeamResult.addScore((float) 61);
		mockTeamResult.addScore((float) 70);
		mockTeamResult.addScore((float) 40);
		mockTeamResult.addScore((float) 90);

		mockTeamResults.add(mockTeamResult);

		mockTeamResult = new TeamResult("MIT", 2, (float) 188.5);
		mockTeamResult.addScore((float) 36);
		mockTeamResult.addScore((float) 43);
		mockTeamResult.addScore((float) 55);
		mockTeamResult.addScore((float) 38);
		mockTeamResult.addScore((float) 52.5);

		mockTeamResults.add(mockTeamResult);

		assertEquals(PostgreSQLJDBC.getTeamResults(TypeOfResult.BENJAMIN_MASCULIN), mockTeamResults);
	}

	@Test
	public void testGetTeamResults_TeamWithSixPlayers() throws SQLException
	{
		Operation operation = sequenceOf(insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
				.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin")
				.values("STRD23456789", "Dr Strange", "Harvard", "Masculin", "Benjamin")
				.values("HULT34567890", "The Hulk", "Harvard", "Masculin", "Benjamin")
				.values("CAGL45678901", "Luke Cage", "Harvard", "Masculin", "Benjamin")
				.values("PANB56789012", "Black Panther", "Harvard", "Masculin", "Benjamin").build(),

				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 45, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 0, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 0, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 0, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 0, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 30, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 22, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 35, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 26, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 40, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 30, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 0, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 4, 0, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 0, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 5, 0, "Double")
						.values("STRD23456789", "Harvard", "Benjamin", 1, 10, "Simple")
						.values("STRD23456789", "Harvard", "Benjamin", 1, 19, "Double")
						.values("STRD23456789", "Harvard", "Benjamin", 2, 15, "Simple")
						.values("STRD23456789", "Harvard", "Benjamin", 2, 16, "Double")
						.values("STRD23456789", "Harvard", "Benjamin", 3, 20, "Simple")
						.values("STRD23456789", "Harvard", "Benjamin", 3, 20, "Double")
						.values("STRD23456789", "Harvard", "Benjamin", 4, 0, "Simple")
						.values("STRD23456789", "Harvard", "Benjamin", 4, 0, "Double")
						.values("STRD23456789", "Harvard", "Benjamin", 5, 0, "Simple")
						.values("STRD23456789", "Harvard", "Benjamin", 5, 0, "Double")
						.values("HULT34567890", "Harvard", "Benjamin", 1, 50, "Simple")
						.values("HULT34567890", "Harvard", "Benjamin", 1, 70, "Double")
						.values("HULT34567890", "Harvard", "Benjamin", 2, 55, "Simple")
						.values("HULT34567890", "Harvard", "Benjamin", 2, 52, "Double")
						.values("HULT34567890", "Harvard", "Benjamin", 3, 70, "Simple")
						.values("HULT34567890", "Harvard", "Benjamin", 3, 66, "Double")
						.values("HULT34567890", "Harvard", "Benjamin", 4, 0, "Simple")
						.values("HULT34567890", "Harvard", "Benjamin", 4, 0, "Double")
						.values("HULT34567890", "Harvard", "Benjamin", 5, 0, "Simple")
						.values("HULT34567890", "Harvard", "Benjamin", 5, 0, "Double")
						.values("CAGL45678901", "Harvard", "Benjamin", 1, 80, "Simple")
						.values("CAGL45678901", "Harvard", "Benjamin", 1, 90, "Double")
						.values("CAGL45678901", "Harvard", "Benjamin", 2, 65, "Simple")
						.values("CAGL45678901", "Harvard", "Benjamin", 2, 70, "Double")
						.values("CAGL45678901", "Harvard", "Benjamin", 3, 82, "Simple")
						.values("CAGL45678901", "Harvard", "Benjamin", 3, 74, "Double")
						.values("CAGL45678901", "Harvard", "Benjamin", 4, 0, "Simple")
						.values("CAGL45678901", "Harvard", "Benjamin", 4, 0, "Double")
						.values("CAGL45678901", "Harvard", "Benjamin", 5, 0, "Simple")
						.values("CAGL45678901", "Harvard", "Benjamin", 5, 0, "Double")
						.values("PANB56789012", "Harvard", "Benjamin", 1, 20, "Simple")
						.values("PANB56789012", "Harvard", "Benjamin", 1, 30, "Double")
						.values("PANB56789012", "Harvard", "Benjamin", 2, 25, "Simple")
						.values("PANB56789012", "Harvard", "Benjamin", 2, 20, "Double")
						.values("PANB56789012", "Harvard", "Benjamin", 3, 10, "Simple")
						.values("PANB56789012", "Harvard", "Benjamin", 3, 16, "Double")
						.values("PANB56789012", "Harvard", "Benjamin", 4, 0, "Simple")
						.values("PANB56789012", "Harvard", "Benjamin", 4, 0, "Double")
						.values("PANB56789012", "Harvard", "Benjamin", 5, 0, "Simple")
						.values("PANB56789012", "Harvard", "Benjamin", 5, 0, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 20, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 0, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 0, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 0, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 0, "Double").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<TeamResult> mockTeamResults = new ArrayList<TeamResult>();

		TeamResult 	mockTeamResult = new TeamResult("Harvard", 1, (float) 176.2);
		mockTeamResult.addScore((float) 92.2);
		mockTeamResult.addScore((float) 81.8);
		mockTeamResult.addScore((float) 94.4);

		mockTeamResults.add(mockTeamResult);

		assertEquals(PostgreSQLJDBC.getTeamResults(TypeOfResult.BENJAMIN_MASCULIN), mockTeamResults);
	}

	@Test
	public void testGetTeamResults_PlayerChangedSchool() throws SQLException
	{
		Operation operation = sequenceOf(insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
				.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin")
				.values("DARD12345678", "Dare Devil", "MIT", "Masculin", "Benjamin")
				.values("STRD23456789", "Dr Strange", "MIT", "Masculin", "Benjamin")
				.values("HULT34567890", "The Hulk", "MIT", "Masculin", "Benjamin").build(),

				insertInto("Result")
						.columns("PlayerId_fkey", "SchoolName_fkey", "Category", "TournamentNumber", "Score",
								"TypeOfPlay")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 1, 12, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 45, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 2, 16, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 50, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 3, 20, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 30, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 4, 10, "Double")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 65, "Simple")
						.values("POOD67100407", "Harvard", "Benjamin", 5, 25, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 30, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 1, 22, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 35, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 2, 26, "Double")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 40, "Simple")
						.values("DARD12345678", "Harvard", "Benjamin", 3, 30, "Double")
						.values("DARD12345678", "MIT", "Benjamin", 4, 20, "Simple")
						.values("DARD12345678", "MIT", "Benjamin", 4, 20, "Double")
						.values("DARD12345678", "MIT", "Benjamin", 5, 55, "Simple")
						.values("DARD12345678", "MIT", "Benjamin", 5, 35, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 1, 10, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 1, 12, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 2, 15, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 2, 16, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 3, 20, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 3, 20, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 4, 12, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 4, 14, "Double")
						.values("STRD23456789", "MIT", "Benjamin", 5, 15, "Simple")
						.values("STRD23456789", "MIT", "Benjamin", 5, 15, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 1, 50, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 1, 42, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 2, 55, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 2, 46, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 3, 70, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 3, 50, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 4, 50, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 4, 60, "Double")
						.values("HULT34567890", "MIT", "Benjamin", 5, 75, "Simple")
						.values("HULT34567890", "MIT", "Benjamin", 5, 56, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 1, 12, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 25, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 2, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 28, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 3, 10, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 35, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 4, 15, "Double")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 30, "Simple")
						.values("WOMW67100202", "Yale", "Benjamin", 5, 20, "Double").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		ArrayList<TeamResult> mockTeamResults = new ArrayList<TeamResult>();

		TeamResult mockTeamResult = new TeamResult("MIT", 1, (float) 288.333334);
		mockTeamResult.addScore((float) 57);
		mockTeamResult.addScore((float) 66);
		mockTeamResult.addScore((float) 80);
		mockTeamResult.addScore((float) 58.666668);
		mockTeamResult.addScore((float) 83.666664);

		mockTeamResults.add(mockTeamResult);

		mockTeamResult = new TeamResult("Harvard", 2, (float) 196);
		mockTeamResult.addScore((float) 57);
		mockTeamResult.addScore((float) 61);
		mockTeamResult.addScore((float) 70);
		mockTeamResult.addScore((float) 20);
		mockTeamResult.addScore((float) 45);

		mockTeamResults.add(mockTeamResult);

		assertEquals(PostgreSQLJDBC.getTeamResults(TypeOfResult.BENJAMIN_MASCULIN), mockTeamResults);
	}
}