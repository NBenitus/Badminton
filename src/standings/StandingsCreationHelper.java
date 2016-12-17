package standings;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;

import compare.CompareFile;
import database.PostgreSQLJDBC;
import registration.FormFile;

public class StandingsCreationHelper
{
	private static final String TEMPLATE_FILENAME = "/resources/Standings_Template.xls";
	private static final String FORM_TEMPLATE_FILENAME = "/resources/Form_Template.xlsx";
	private static final String RESULT_TEMPLATE_FILENAME = "/resources/Results_Template.xlsx";

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
		MASCULINE("Masculin"), FEMININE("Féminin");

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

		BENJAMIN_FEMININ(Category.BENJAMIN, Gender.FEMININE), CADET_FEMININ(Category.CADET,
				Gender.FEMININE), JUVENIL_FEMININ(Category.JUVÉNILE, Gender.FEMININE), BENJAMIN_MASCULIN(
						Category.BENJAMIN, Gender.MASCULINE), CADET_MASCULIN(Category.CADET,
								Gender.MASCULINE), JUVÉNILE_MASCULIN(Category.JUVÉNILE, Gender.MASCULINE);

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

	public static ResultFile getResultFile()
	{
		return resultFile;
	}

	public static File getStandingsFile()
	{
		return outputFile;
	}

	public static void main(String[] args) throws Exception
	{
		// Hard coded values used for testing
		s1File = new S1File(new File("C:\\Benoit\\Work\\Java\\Badminton\\ListeJoueursS1_2016_2017.xls"));
		resultFile = new ResultFile(new File("C:\\Benoit\\Work\\Java\\Badminton\\Liste_Résultats_Complet.xls"));
		outputFile = new File("C:\\Benoit\\Work\\Java\\Badminton\\Résultats.xls");

		// addPlayers(s1File);
//		testCreationResultsFile();
//		 createStandingsFile();
		 testFormsFile();
		// testCompareFiles();
	}

	public static void setResultFile(ResultFile resultFile)
	{
		StandingsCreationHelper.resultFile = resultFile;
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

	public static void testCreationResultsFile()
	{
		File outputFile = new File("C:\\Benoit\\Work\\Java\\Badminton\\Liste_Résultats.xls");
		ResultFile resultFile = new ResultFile(
				StandingsCreationHelper.class.getResourceAsStream(RESULT_TEMPLATE_FILENAME), outputFile);
		resultFile.write();
	}

	public static void testFormsFile()
	{
		String outputFilePrefix = "C:\\Benoit\\Work\\Java\\Badminton\\Registration\\Filled\\Formulaire_";

		ArrayList<String> listSchoolNames = PostgreSQLJDBC.getAllSchools();

		for (int i = 0; i < listSchoolNames.size(); i++)
		{
			File outputFile = new File(outputFilePrefix + listSchoolNames.get(i) + ".xlsx");

//			FormFile formFile = new FormFile(StandingsCreationHelper.class.getResourceAsStream(FORM_TEMPLATE_FILENAME),
//					outputFile, listSchoolNames.get(i));
//
//			formFile.write();

			 FormFile testFormFile = new FormFile(new File(outputFilePrefix + listSchoolNames.get(i) + ".xls"),
			 listSchoolNames.get(i));
			 testFormFile.read();
		}
	}
}
