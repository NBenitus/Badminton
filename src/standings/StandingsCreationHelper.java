package standings;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.poi.util.IOUtils;

import Log.LoggerWrapper;
import compare.CompareFile;
import database.PostgreSQLJDBC;
import excelHelper.ExcelFileProcessor;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import pageBreak.PageBreakFile;

public class StandingsCreationHelper
{
	private static final String TEMPLATE_FILENAME = "resources/Standings_Template.xls";

	private static File standingsFile;
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
	public static void addPlayers(S1File s1File) throws Exception
	{
		PostgreSQLJDBC.addPlayer(s1File.read());
	}

	/**
	 * Adds results to the database
	 *
	 * @param resultsFile
	 *            xls file that contains the list of results
	 * @throws Exception
	 */
	public static void addResults(ResultFile resultFile) throws Exception
	{
		PostgreSQLJDBC.addResult(resultFile.read());
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
		// File inputFile = new File("C:\\Benoit\\Work\\Java\\Badminton\\RésultatsTournoi_Template.xls");
		// File outputFile = new File("C:\\Benoit\\Work\\Java\\Badminton\\Liste_Résultats.xls");

		StandingsCreationHelper sch = new StandingsCreationHelper();
		standingsExcelFile = new StandingsFile(StandingsCreationHelper.class.getResourceAsStream(TEMPLATE_FILENAME), standingsFile);

//		File templateFile = new File("TemporaryPlaceHolderExcelFile.xls");
//		OutputStream outputStream = new FileOutputStream(templateFile);
//		IOUtils.copy(sch.getFile(TEMPLATE_FILENAME), outputStream);
//		outputStream.close();
//
//		standingsExcelFile = new StandingsFile(templateFile, standingsFile);

		PostgreSQLJDBC.clearDatabase();

		addResults(resultFile);
		write(standingsExcelFile);
		deleteTemplateSheets(standingsExcelFile);
		closeResultsFile(standingsExcelFile);

		PageBreakFile pageBreakFile = new PageBreakFile(standingsFile, standingsExcelFile.getIndividualResultSheets(),
				standingsExcelFile.getTeamResultSheet());

		pageBreakFile.write();

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
//	public InputStream getFile(String fileName)
//	{
//		return this.getClass().getResourceAsStream(fileName);
//	}

	public static ResultFile getResultFile()
	{
		return resultFile;
	}

	public static File getStandingsFile()
	{
		return standingsFile;
	}

	public static void main(String[] args) throws Exception
	{
		// Hard coded values used for testing
		s1File = new S1File(new File("C:\\Benoit\\Work\\Java\\Badminton\\ListeJoueursS1_2016_2017.xls"));
		resultFile = new ResultFile(new File("C:\\Benoit\\Work\\Java\\Badminton\\Liste_Résultats_Complet.xls"));
		standingsFile = new File("C:\\Benoit\\Work\\Java\\Badminton\\Résultats.xls");

//		addPlayers(s1File);
		createStandingsFile();
		// testFormsFile();
		// testCompareFiles();
	}

	public static void setResultFile(ResultFile resultFile)
	{
		StandingsCreationHelper.resultFile = resultFile;
	}

	public static void setStandingsFile(File standingsFile)
	{
		StandingsCreationHelper.standingsFile = standingsFile;
	}

	public static void testCompareFiles() throws Exception
	{
		File excelOne = new File("C:\\Benoit\\Work\\Java\\Badminton\\Résultats.xls");
		File excelTwo = new File("C:\\Benoit\\Work\\Java\\Badminton\\Résultats_MisAJour.xls");
		File excelThree = new File("C:\\Benoit\\Work\\Java\\Badminton\\Compare_Test.xls");
		CompareFile compareFile = new CompareFile(excelOne, excelTwo, excelThree);
	}

	public static void testCreationResultsFile()
	{
		File inputFile = new File("C:\\Benoit\\Work\\Java\\Badminton\\RésultatsTournoi_Template.xls");
		File outputFile = new File("C:\\Benoit\\Work\\Java\\Badminton\\Liste_Résultats.xls");
		ResultFile resultFile = new ResultFile(inputFile, outputFile);
		resultFile.write();
	}

	public static void testFormsFile()
	{
		File inputFile = new File("C:\\Benoit\\Work\\Java\\Badminton\\FormulairesEntrees_Template.xls");
		File outputFile = new File("C:\\Benoit\\Work\\Java\\Badminton\\Formulaire.xls");
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
	public static void write(StandingsFile standingsFile) throws BiffException, IOException, WriteException
	{
		for (int i = 0; i < TypeOfPlay.values().length; i++)
		{
			standingsFile.addIndividualResultSheet(TypeOfPlay.values()[i].text(), TypeOfPlay.values()[i]);
		}

		standingsFile.setTeamResultSheet();
		standingsFile.write();
	}

}
