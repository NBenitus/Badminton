package standings;

import java.awt.Desktop;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import compare.CompareFile;
import database.PostgreSQLJDBC;
import registration.FormFile;

public class StandingsCreationHelper
{
	public static final String directoryPath = "C:\\Benoit\\Work\\Java\\Badminton\\";
	private static final String TEMPLATE_FILENAME = "/resources/Template/Standings_Template.xls";
	private static final String FORM_TEMPLATE_FILENAME = "/resources/Template/Form_Template.xlsx";
	private static final String RESULT_TEMPLATE_FILENAME = "/resources/Template/Results_Template.xlsx";

	private static File outputFile;
	private static ResultFile resultFile;
	private static S1File s1File;

	private static StandingsFile standingsExcelFile;

	public enum Category {
		BENJAMIN("Benjamin"), CADET("Cadet"), JUVÉNILE("Juvénile");

		private String text;

		Category(String text)
		{
			this.text = text;
		}

		public String text()
		{
			return text;
		}
	}

	public enum Gender {
		MASCULIN("Masculin"), FÉMININ("Féminin");

		private String text;

		Gender(String text)
		{
			this.text = text;
		}

		public String text()
		{
			return text;
		}
	}

	public enum TypeOfPlay {
		SINGLE("Simple"), DOUBLE("Double"), COMBINED("Combiné"),;

		private String text;

		TypeOfPlay(String text)
		{
			this.text = text;
		}

		public String text()
		{
			return text;
		}
	}

	public enum TypeOfResult {

		BENJAMIN_FEMININ(Category.BENJAMIN, Gender.FÉMININ), CADET_FEMININ(Category.CADET,
				Gender.FÉMININ), JUVENIL_FEMININ(Category.JUVÉNILE, Gender.FÉMININ), BENJAMIN_MASCULIN(
						Category.BENJAMIN, Gender.MASCULIN), CADET_MASCULIN(Category.CADET,
								Gender.MASCULIN), JUVÉNILE_MASCULIN(Category.JUVÉNILE, Gender.MASCULIN);

		private Category category;
		private Gender gender;

		TypeOfResult(Category category, Gender gender)
		{
			this.category = category;
			this.gender = gender;
		}

		public Category category()
		{
			return category;
		}

		public Gender gender()
		{
			return gender;
		}
	}

	/**
	 * Adds players to the database
	 *
	 * @param s1File
	 *            S1 file that contains the list of players
	 * @throws Exception
	 */
	public static void addPlayers(S1File s1File) throws Exception
	{
		PostgreSQLJDBC.clearTable("Result");
		PostgreSQLJDBC.clearTable("Player");
		PostgreSQLJDBC.addPlayer(s1File.read());
	}

	/**
	 * Method that contains all the steps to create the standings file
	 */
	public static void createStandingsFile() throws Exception
	{
		resultFile.addResult();

		standingsExcelFile = new StandingsFile(StandingsCreationHelper.class.getResourceAsStream(TEMPLATE_FILENAME),
				outputFile, false);
		standingsExcelFile.write();

		try
		{
			Desktop dt = Desktop.getDesktop();
			dt.open(outputFile);
		}
		catch (Exception ex)
		{
			System.out.println(ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	public static void generateFormsFile() throws SQLException
	{
		String outputFilePrefix = directoryPath + "Registration\\Formulaire_";

		ArrayList<String> listSchoolNames = PostgreSQLJDBC.getAllSchools();

		for (int i = 0; i < listSchoolNames.size(); i++)
		{
			File outputFile = new File(outputFilePrefix + listSchoolNames.get(i) + ".xlsx");

			FormFile formFile = new FormFile(outputFile, listSchoolNames.get(i));
			formFile.write();

//			 FormFile testFormFile = new FormFile(new File(outputFilePrefix + listSchoolNames.get(i) + ".xls"),
//			 listSchoolNames.get(i));
//			 testFormFile.read();
		}
	}

	public static ResultFile getResultFile()
	{
		return resultFile;
	}

	public static S1File getS1File()
	{
		return s1File;
	}

	public static File getStandingsFile()
	{
		return outputFile;
	}

	public static void main(String[] args) throws Exception
	{
		// Hard coded values used for testing
		resultFile = new ResultFile(new File(directoryPath + "Liste_Résultats_Complet.xls"));
		outputFile = new File(directoryPath + "Résultats.xls");
		s1File = new S1File(new File(directoryPath + "ListeJoueursS1_2016_2017.xls"));

//		addPlayers(s1File);
		generateFormsFile();
//		testCreationResultsFile();
//		createStandingsFile();
		// testCompareFiles();
	}

	public static void setResultFile(ResultFile resultFile)
	{
		StandingsCreationHelper.resultFile = resultFile;
	}

	public static void setS1File(S1File s1File)
	{
		StandingsCreationHelper.s1File = s1File;
	}

	public static void setStandingsFile(File standingsFile)
	{
		StandingsCreationHelper.outputFile = standingsFile;
	}

	public static void testCompareFiles() throws Exception
	{
		File excelOne = new File("C:\\Benoit\\Work\\Java\\Badminton\\Résultats_Test1.xls");
		File excelTwo = new File("C:\\Benoit\\Work\\Java\\Badminton\\Résultats_Test2.xls");
		File excelThree = new File("C:\\Benoit\\Work\\Java\\Badminton\\Compare_Test.xls");
		CompareFile compareFile = new CompareFile(excelOne, excelTwo, excelThree);
		compareFile.compare();

		try
		{
			Desktop dt = Desktop.getDesktop();
			dt.open(excelThree);
		}
		catch (Exception ex)
		{
			System.out.println(ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

//	public static void testCreationResultsFile()
//	{
//		File outputFile = new File("C:\\Benoit\\Work\\Java\\Badminton\\Liste_Résultats.xls");
//		ResultFile resultFile = new ResultFile(
//				StandingsCreationHelper.class.getResourceAsStream(RESULT_TEMPLATE_FILENAME), outputFile);
//		resultFile.write();
//	}
}
