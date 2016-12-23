package registration;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import compare.CompareFile;
import database.CommonOperations;
import database.PostgreSQLJDBC;
import standings.Player;
import standings.StandingsCreationHelper;
import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.Gender;

public class FormFileTest
{
	private static final String FORM_FILE_TEST_WRITE_FILENAME = "/resources/Test/FormFileTest_Write.xlsx";

	private static final String TEST_DB_URL = "jdbc:postgresql://localhost:5432/Badminton";
	private static final String TEST_DB_USER = "postgres";
	private static final String TEST_DB_PASSWORD = "postgrespass";

	FormFile tester = new FormFile(null, "Harvard");

	@Rule
    public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void prepare() throws Exception
	{
		Operation operation = sequenceOf(CommonOperations.DELETE_ALL, CommonOperations.INSERT_REFERENCE_DATA);

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();
	}

	@Test
	public void testFormatName_NoId()
	{
		Player testPlayer = new Player("DARD20029999", "Dare Devil", "Harvard", Gender.MASCULIN, Category.JUVÉNILE);

		ArrayList<Player> testPlayers = new ArrayList<Player>();

		Player player = new Player("POOD01019999", "Dead Pool", "Harvard", Gender.MASCULIN, Category.JUVÉNILE);
		testPlayers.add(player);

		player = new Player("AMEC01019999", "Captain America", "Harvard", Gender.MASCULIN, Category.JUVÉNILE);
		testPlayers.add(player);

		player = new Player("MANS01019999", "Spider Man", "Harvard", Gender.MASCULIN, Category.JUVÉNILE);
		testPlayers.add(player);

		assertEquals("Dare Devil", tester.formatName(testPlayer, testPlayers));
	}

	@Test
	public void testFormatName_WithId()
	{
		Player testPlayer = new Player("DARD20029999", "Dare Devil", "Harvard", Gender.MASCULIN, Category.JUVÉNILE);

		ArrayList<Player> testPlayers = new ArrayList<Player>();

		Player player = new Player("POOD01019999", "Dead Pool", "Harvard", Gender.MASCULIN, Category.JUVÉNILE);
		testPlayers.add(player);

		player = new Player("AMEC01019999", "Captain America", "Harvard", Gender.MASCULIN, Category.JUVÉNILE);
		testPlayers.add(player);

		player = new Player("MANS01019999", "Spider Man", "Harvard", Gender.MASCULIN, Category.JUVÉNILE);
		testPlayers.add(player);

		player = new Player("DARD15081234", "Dare Devil", "Harvard", Gender.MASCULIN, Category.JUVÉNILE);
		testPlayers.add(player);

		assertEquals("Dare Devil (999)", tester.formatName(testPlayer, testPlayers));
	}

	@Test
	public void testGetName()
	{
		assertEquals("Dead Pool", tester.getName(" Dead Pool"));
	}

	@Test
	public void testGetName_Overanked()
	{
		assertEquals("Dead Pool", tester.getName(" Surclassement: Dead Pool"));
	}

	@Test
	public void testGetName_Id()
	{
		assertEquals("Dead Pool", tester.getName(" Dead Pool (999)"));
	}

	@Test
	public void testGetName_OverankedId()
	{
		assertEquals("Dead Pool", tester.getName(" Surclassement: Dead Pool (999)"));
	}

	@Test
	public void testGetID_NoId()
	{
		assertEquals(null, tester.getID(" Dead Pool"));
	}

	@Test
	public void testGetID_NoIdOveranked()
	{
		assertEquals(null, tester.getID(" Surclassement: Dead Pool"));
	}

	@Test
	public void testGetID_WithId()
	{
		assertEquals("DARD20029999", tester.getID(" Dead Pool (DARD20029999)"));
	}

	@Test
	public void testGetID_WithIdOveranked()
	{
		assertEquals("DARD20029999", tester.getID(" Surclassement: Dead Pool (DARD20029999)"));
	}

	@Test
	public void testRead()
	{
//		public static void testFormsFile()
//		{
//			String outputFilePrefix = "C:\\Benoit\\Work\\Java\\Badminton\\Registration\\Filled\\Formulaire_";
//
//			ArrayList<String> listSchoolNames = PostgreSQLJDBC.getAllSchools();
//
//			for (int i = 0; i < listSchoolNames.size(); i++)
//			{
//				File outputFile = new File(outputFilePrefix + listSchoolNames.get(i) + ".xlsx");
//
////				FormFile formFile = new FormFile(StandingsCreationHelper.class.getResourceAsStream(FORM_TEMPLATE_FILENAME),
////						outputFile, listSchoolNames.get(i));
//	//
////				formFile.write();
//
//				 FormFile testFormFile = new FormFile(new File(outputFilePrefix + listSchoolNames.get(i) + ".xls"),
//				 listSchoolNames.get(i));
//				 testFormFile.read();
//			}
//		}
	}

	@Test
	public void testWrite() throws SQLException, IOException
	{
		File createdFile = folder.newFile("myfile.xlsx");
		File createdFile2 = folder.newFile("myfile2.xlsx");
		File createdFile3 = folder.newFile("myfile3.xlsx");

		Operation operation = sequenceOf(insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
				.values("DARD12345678", "Dare Devil", "Harvard", "Masculin", "Benjamin")
				.values("STRD23456789", "Dr Strange", "Harvard", "Masculin", "Cadet")
				.values("HULT34567890", "The Hulk", "Harvard", "Masculin", "Cadet")
				.values("CAGL45678901", "Luke Cage", "Harvard", "Masculin", "Juvénile")
				.values("PANB56789012", "Black Panther", "Harvard", "Masculin", "Juvénile")
				.values("HULS67100202", "She Hulk", "Harvard", "Féminin", "Benjamin")
				.values("MARM78901234", "Miss Marvel", "Harvard", "Féminin", "Cadet")
				.values("USAM89012345", "Med Usa", "Harvard", "Féminin", "Juvénile").build());

		DbSetup dbSetup = new DbSetup(new DriverManagerDestination(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD),
				operation);
		dbSetup.launch();

		tester = new FormFile(createdFile, "Harvard");
		tester.write();

		// Copy the file from the resources folder to an actual file on the disk
		OutputStream outputStream = new FileOutputStream(createdFile2);
		IOUtils.copy(FormFileTest.class.getResourceAsStream(FORM_FILE_TEST_WRITE_FILENAME), outputStream);
		outputStream.close();

		CompareFile compareFile = new CompareFile(createdFile, createdFile2, createdFile3);

		assertEquals(compareFile.compare(), true);
	}
}
