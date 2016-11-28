package standings;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.util.IOUtils;

import database.PostgreSQLJDBC;
import excelHelper.ExcelFileReader;
import excelHelper.ExcelFileProcessor;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

public class StandingsCreationHelper
{
	// private static final File TEMPLATEFILE = new File("files/Standings_Template.xls");

	private static File resultsFile;
	private static final String RESULTSSHEETNAME = "Données Joueurs";

	private static File standingsFile;

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
		SIMPLE("Simple"), DOUBLE("Double"), COMBINED("Combiné"),;

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
								Gender.MASCULIN), JUVENIL_MASCULIN(Category.JUVÉNILE, Gender.MASCULIN);

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
	 * @param playersFile
	 *            xls file that contains the list of players
	 * @throws Exception
	 */
	public static void addPlayers(File playersFile) throws Exception
	{
		PostgreSQLJDBC.addPlayer(ExcelFileReader.readListPlayersFromResultsFile(playersFile, RESULTSSHEETNAME));
	}

	/**
	 * Adds results to the database
	 *
	 * @param resultsFile
	 *            xls file that contains the list of results
	 * @throws Exception
	 */
	public static void addResults(File resultsFile) throws Exception
	{
		PostgreSQLJDBC.addResult(ExcelFileReader.readResults(resultsFile, RESULTSSHEETNAME));
	}

	/**
	 * Closes the instance of the StandingsFile and its open input and output streams
	 *
	 * @param standingsFile
	 *            standingsFile to close
	 */
	public static void closeResultsFile(StandingsFile standingsFile) throws BiffException, IOException, WriteException
	{
		ExcelFileProcessor.writeAndClose(standingsFile.getWritableWorkbook(), standingsFile.getWorkbook());
	}

	/**
	 * Method that contains all the steps to create the standings file
	 */
	public static void createStandingsFile() throws Exception
	{
		StandingsCreationHelper sch = new StandingsCreationHelper();

		// Get the file from the resources folder
		File templateFile = new File("TemporaryPlaceHolderExcelFile.xls");
		OutputStream outputStream = new FileOutputStream(templateFile);
		IOUtils.copy(sch.getFile("Standings_Template.xls"), outputStream);
		outputStream.close();

		standingsExcelFile = new StandingsFile(templateFile, standingsFile);

		PostgreSQLJDBC.clearDatabase();
		addPlayers(resultsFile);
		addResults(resultsFile);
		writeResultsFile(standingsExcelFile, templateFile, standingsFile);
		deleteTemplateSheets(standingsExcelFile);
		closeResultsFile(standingsExcelFile);

		POIExcelFile resultsPOIExcelFile = new POIExcelFile(standingsFile,
				standingsExcelFile.getIndividualResultSheets(), standingsExcelFile.getTeamResultSheet());

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
	}

	/**
	 * Deletes the template sheets in a StandingsFile
	 *
	 * @param standingsFile
	 *            standingsFile which to delete template sheets from
	 */
	public static void deleteTemplateSheets(StandingsFile standingsFile)
	{
		ExcelFileProcessor.deleteSheet(standingsFile.getWritableWorkbook(), IndividualResultSheet.TEMPLATESHEETNAME);
	}

	/**
	 * Get an input stream from a filename
	 *
	 * @param fileName
	 *            path name of the file
	 * @return input stream of the file
	 */
	public InputStream getFile(String fileName)
	{
		return this.getClass().getClassLoader().getResourceAsStream(fileName);
	}

	public static File getResultsFile()
	{
		return resultsFile;
	}

	public static File getStandingsFile()
	{
		return standingsFile;
	}

	public static void main(String[] args) throws Exception
	{
		// Hard coded values used for testing
		resultsFile = new File("C:\\Benoit\\Work\\Java\\Badminton\\R2.xls");
		standingsFile = new File("C:\\Benoit\\Work\\Java\\Badminton\\Résultats.xls");
		createStandingsFile();
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
	 * @param standingsFile
	 *            jExcelFile object used to create the results content
	 * @param templateFile
	 *            xls file which contains the structure and the presentation of sheets
	 * @param resultsFile
	 *            xls file created that will contain all the results
	 */
	public static void writeResultsFile(StandingsFile standingsFile, File templateFile, File resultsFile)
			throws BiffException, IOException, WriteException
	{
		for (int i = 0; i < TypeOfPlay.values().length; i++)
		{
			standingsFile.addIndividualResultSheet(TypeOfPlay.values()[i].text(), TypeOfPlay.values()[i]);
		}

		standingsFile.setTeamResultSheet();
		standingsFile.writeAllSheets();
	}

}
