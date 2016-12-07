package standings;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.poi.util.IOUtils;

import compare.CompareFile;
import database.PostgreSQLJDBC;
import excelHelper.ExcelFileProcessor;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import log.LoggerWrapper;
import pageBreak.PageBreakFile;
import registration.FormFile;

public class StandingsCreationHelper
{
	private static final String TEMPLATE_FILENAME = "/resources/Standings_Template.xls";
	private static final String FORM_TEMPLATE_FILENAME = "/resources/FormulairesEntrees_Template.xls";

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
		PostgreSQLJDBC.clearTable("Player");
		PostgreSQLJDBC.addPlayer(s1File.read());
	}

	/**
	 * Method that contains all the steps to create the standings file
	 */
	public static void createStandingsFile() throws Exception
	{
		standingsExcelFile = new StandingsFile(StandingsCreationHelper.class.getResourceAsStream(TEMPLATE_FILENAME),
				outputFile);

		resultFile.addResult();
		standingsExcelFile.write();

		standingsExcelFile.deleteTemplateSheet();
		standingsExcelFile.close();

		PageBreakFile pageBreakFile = new PageBreakFile(outputFile, standingsExcelFile.getIndividualResultSheets(),
				standingsExcelFile.getTeamResultSheet());

		pageBreakFile.write();

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
		// createStandingsFile();
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
		String outputFilePrefix = "C:\\Benoit\\Work\\Java\\Badminton\\Registration\\Formulaire_";

		ArrayList<String> listSchoolNames = PostgreSQLJDBC.getAllSchools();

		for (int i = 0; i < listSchoolNames.size(); i++)
		{
			File outputFile = new File(outputFilePrefix + listSchoolNames.get(i) + ".xls");

			FormFile formFile = new FormFile(StandingsCreationHelper.class.getResourceAsStream(FORM_TEMPLATE_FILENAME),
					outputFile, listSchoolNames.get(i));
			formFile.write();

//			FormFile testFormFile = new FormFile(new File(outputFilePrefix + listSchoolNames.get(i) + ".xls"),
//					listSchoolNames.get(i));
//			testFormFile.read();
		}
	}
}
