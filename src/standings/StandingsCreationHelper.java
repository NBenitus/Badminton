package standings;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import database.PostgreSQLJDBC;
import excelHelper.ExcelFileReader;
import excelHelper.ExcelFileProcessor;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

public class StandingsCreationHelper
{
	private static final File TEMPLATEFILE = new File("files/Standings_Template.xls");
	private static final String TEMPLATESHEETNAME = "Moy par joueur PDF";

	private static File resultsFile;
	private static final String RESULTSSHEETNAME = "Données Joueurs";

	private static File standingsFile;

	private static StandingsFile resultsJExcelFile;

	private static boolean isFinished = false;

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

		BENJAMIN_FEMININ(Category.BENJAMIN, Gender.FÉMININ, 2), CADET_FEMININ(Category.CADET, Gender.FÉMININ, 6),
		JUVENIL_FEMININ(Category.JUVÉNILE, Gender.FÉMININ, 10), BENJAMIN_MASCULIN(Category.BENJAMIN,
						Gender.MASCULIN, 14), CADET_MASCULIN(Category.CADET, Gender.MASCULIN, 18),
		JUVENIL_MASCULIN(Category.JUVÉNILE, Gender.MASCULIN, 22);

		private Category category;
		private Gender gender;
		private int individualResultSheetStartColumn;

		TypeOfResult(Category category, Gender gender, int individualResultSheetStartColumn)
		{
			this.category = category;
			this.gender = gender;
			this.individualResultSheetStartColumn = individualResultSheetStartColumn;
		}

		public Category category()
		{
			return category;
		}

		public Gender gender()
		{
			return gender;
		}

		public int individualResultSheetStartColumn()
		{
			return individualResultSheetStartColumn;
		}
	}

	/**
	 * Adds players to the database
	 *
	 * @param dataFile
	 *            xls file that contains the list of players
	 * @throws Exception
	 */
	public static void addPlayers(File dataFile) throws Exception
	{
		PostgreSQLJDBC.addPlayer(ExcelFileReader.readListPlayersFromResultsFile(dataFile, RESULTSSHEETNAME));
	}

	/**
	 * Adds results to the database
	 *
	 * @param dataFile
	 *            xls file that contains the list of results
	 * @throws Exception
	 */
	public static void addResults(File dataFile) throws Exception
	{
		PostgreSQLJDBC.addResult(ExcelFileReader.readResults(dataFile, RESULTSSHEETNAME));
	}

	/**
	 * Closes the instance of the JExcelFile and its open input and output streams
	 *
	 * @param jExcelFile
	 *            JExcelFile to close
	 */
	public static void closeResultsFile(StandingsFile jExcelFile) throws BiffException, IOException, WriteException
	{
		ExcelFileProcessor.writeAndClose(jExcelFile.getWritableWorkbook(), jExcelFile.getWorkbook());
	}

	public static void createStandingsFile() throws Exception
	{
		resultsJExcelFile = new StandingsFile(TEMPLATEFILE, standingsFile, TEMPLATESHEETNAME);

		addPlayers(resultsFile);
		addResults(resultsFile);
		writeResultsFile(resultsJExcelFile, TEMPLATEFILE, standingsFile);
		deleteTemplateSheets(resultsJExcelFile);
		closeResultsFile(resultsJExcelFile);

		POIExcelFile resultsPOIExcelFile = new POIExcelFile(standingsFile, resultsJExcelFile.getIndividualResultSheets(),
				resultsJExcelFile.getTeamResultSheet());

		resultsPOIExcelFile.addPageBreaks();

		try
		{
			Desktop dt = Desktop.getDesktop();
			dt.open(standingsFile);
		}
		catch (Exception ex)
		{
			System.out.println(ex.getClass().getName() + ": " + ex.getMessage());
		}

		isFinished = true;
	}

	/**
	 * Deletes the template sheets in a JExcelFile
	 * !!! Could create an array instead of hard-coded values
	 *
	 * @param jExcelFile
	 *            jExcelFile which to delete template sheets from
	 */
	public static void deleteTemplateSheets(StandingsFile jExcelFile)
	{
		ExcelFileProcessor.deleteSheet(jExcelFile.getWritableWorkbook(), "Moy par joueur PDF");
		ExcelFileProcessor.deleteSheet(jExcelFile.getWritableWorkbook(), "Moyenne par ecole PDF");
	}

	public static File getResultsFile()
	{
		return resultsFile;
	}

	public static File getStandingsFile()
	{
		return standingsFile;
	}

	public static boolean isFinished()
	{
		return isFinished;
	}

	public static void main(String[] args) throws Exception
	{
		resultsFile = new File("C:\\Benoit\\Work\\Java\\Badminton\\RésultatsTournoi.xls");
		standingsFile = new File("C:\\Benoit\\Work\\Java\\Badminton\\Résultats.xls");
//		createStandingsFile();

		File file = new File("C:\\Benoit\\Work\\Java\\Badminton\\FormulairesEntrees_Tournoi3.xls");
		Inscription ins = ExcelFileReader.readListPlayersFromForms(file, "Juvénile");
		System.out.println("Finished");
	}

	public static void setResultsFile(File resultsFile)
	{
		StandingsCreationHelper.resultsFile = resultsFile;
	}

	public static void setStandingsFile(File standingsFile)
	{
		StandingsCreationHelper.standingsFile = standingsFile;
	}

	/**
	 * Writes all the results (individual and team) in a xls file
	 *
	 * @param jExcelFile
	 *            jExcelFile object used to create the results content
	 * @param templateFile
	 *            xls file which contains the structure and the presentation of sheets
	 * @param resultsFile
	 *            xls file created that will contain all the results
	 */
	public static void writeResultsFile(StandingsFile jExcelFile, File templateFile, File resultsFile)
			throws BiffException, IOException, WriteException
	{
		for (int i = 0; i < TypeOfPlay.values().length; i++)
		{
			jExcelFile.addIndividualResultSheet(TypeOfPlay.values()[i].text(), TypeOfPlay.values()[i]);
		}

		jExcelFile.setTeamResultSheet();
		jExcelFile.writeAllSheets();
	}


}
