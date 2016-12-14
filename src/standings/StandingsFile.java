package standings;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import database.PostgreSQLJDBC;
import excelHelper.POIExcelFileProcessor;
import standings.StandingsCreationHelper.Gender;
import standings.StandingsCreationHelper.TypeOfPlay;
import standings.StandingsCreationHelper.TypeOfResult;

public class StandingsFile
{
	private Workbook workbook;

	private ArrayList<IndividualResultSheet> individualResultSheets = new ArrayList<IndividualResultSheet>();
	private TeamResultSheet teamResultSheet;

	private static final int NUMBER_OF_HEADER_ROWS = 4;
	private static final int NUMBER_TOURNAMENTS = 5;

	private File outputFile;
	private int currentRow;
	private boolean isColor;

	private static final String FONT_NAME = "ARIAL";
	private static final Short FONT_SIZE = 11;

	private ArrayList<IndexedColors> backgroundColors = new ArrayList<IndexedColors>();
	private ArrayList<Font> fonts = new ArrayList<Font>();
	private ArrayList<CellStyle> coloredCellStyles = new ArrayList<CellStyle>();

	public enum IndividualColumn {
		RANK(0), PLAYER_NAME(1), SCHOOL_NAME(2), SCORE(3), SINGLE_SCORE(3), DOUBLE_SCORE(4), TOTAL_SCORE(5);

		private int number;

		IndividualColumn(int number)
		{
			this.number = number;
		}

		public int number()
		{
			return number;
		}
	}

	public enum TeamColumn {
		RANK(0), SCHOOL_NAME(1), FIRST_TOURNAMENT(2);

		private int number;

		TeamColumn(int number)
		{
			this.number = number;
		}

		public int number()
		{
			return number;
		}
	}

	/**
	 * Constructor.
	 *
	 * @param inputStream
	 *            input stream from the template file
	 * @param outputFile
	 *            file that will contain the created team and individual results
	 */
	public StandingsFile(InputStream inputStream, File outputFile, boolean isColor)
	{
		this.outputFile = outputFile;
		this.isColor = isColor;
		workbook = POIExcelFileProcessor.createWorkbook(inputStream);

		for (int i = 0; i < TypeOfPlay.values().length; i++)
		{
			this.addIndividualResultSheet(TypeOfPlay.values()[i].text(), TypeOfPlay.values()[i]);
		}

		teamResultSheet = new TeamResultSheet();

		initializeColorsAndFonts();
	}

	/**
	 * Adds an individual result sheet to the list of individual result sheets
	 *
	 * @param sheetName
	 *            sheet name of the individual result sheet
	 * @param typeOfPlay
	 *            type of play (either single or double)
	 */
	public void addIndividualResultSheet(String sheetName, TypeOfPlay typeOfPlay)
	{
		if (typeOfPlay != TypeOfPlay.COMBINED)
		{
			individualResultSheets.add(new IndividualResultSheet(this, sheetName, typeOfPlay));
		}
		else
		{
			individualResultSheets.add(new IndividualCombinedResultSheet(this, typeOfPlay));
		}
	}

	/**
	 * Adds page breaks to all the individual result sheets
	 */
	public void addPageBreaks()
	{
		// Iterate over all the individual result sheets
		for (int i = 0; i < individualResultSheets.size(); i++)
		{
			Sheet sheet = workbook.getSheet(individualResultSheets.get(i).getName());

			// Iterate over each type of result
			for (int j = 0; j < StandingsCreationHelper.TypeOfResult.values().length; j++)
			{
				sheet.setColumnBreak(IndividualResultSheet.FIRST_COLUMN
						+ (j * individualResultSheets.get(i).getNumberOfColumns() - 1));
			}
		}
	}

	/**
	 * Initializes the color coding for the schools
	 */
	public void initializeColorsAndFonts()
	{
		Font whiteFont = workbook.createFont();
		whiteFont.setFontHeightInPoints(FONT_SIZE);
		whiteFont.setFontName(FONT_NAME);
		whiteFont.setBold(true);
		whiteFont.setColor(IndexedColors.WHITE.getIndex());

		Font blackFont = workbook.createFont();
		blackFont.setFontHeightInPoints(FONT_SIZE);
		blackFont.setFontName(FONT_NAME);
		blackFont.setBold(true);
		blackFont.setColor(IndexedColors.BLACK.getIndex());

		backgroundColors.addAll(Arrays.asList(IndexedColors.RED, IndexedColors.YELLOW, IndexedColors.ORANGE,
				IndexedColors.DARK_BLUE, IndexedColors.TURQUOISE, IndexedColors.BLACK, IndexedColors.TEAL,
				IndexedColors.LIME, IndexedColors.ROSE, IndexedColors.GREEN));

		fonts.addAll(Arrays.asList(blackFont, blackFont, blackFont, whiteFont, blackFont, whiteFont, blackFont,
				blackFont, blackFont, blackFont));
	}

	/**
	 * Writes all the sheets (individual and team)
	 */
	public void write()
	{
		// Iterate over all the individual sheets (and the one team result sheet)
		for (int i = 0; i < individualResultSheets.size(); i++)
		{
			if (i != (individualResultSheets.size() - 1))
			{
				workbook.cloneSheet(workbook.getSheetIndex(individualResultSheets.get(i).getTemplateSheetName()));
				workbook.setSheetName(
						workbook.getSheetIndex(individualResultSheets.get(i).getTemplateSheetName() + " (2)"),
						individualResultSheets.get(i).getName());

			}

			// Iterate over all the different types of result
			for (int j = 0; j < StandingsCreationHelper.TypeOfResult.values().length; j++)
			{
				writeIndividualResultSheet(individualResultSheets.get(i), TypeOfResult.values()[j], j);

				// Write the team result sheet as well
				if (i == (individualResultSheets.size() - 1))
				{
					writeTeamResultSheet(StandingsCreationHelper.TypeOfResult.values()[j]);
				}
			}
		}

		workbook.removeSheetAt(workbook.getSheetIndex(individualResultSheets.get(0).getTemplateSheetName()));

		addPageBreaks();

		POIExcelFileProcessor.writeWorkbook(workbook, outputFile);
	}

	/**
	 * Writes an individual result sheet, one type of result at a time
	 *
	 * @param individualResultSheet
	 *            individual result sheet object (containing properties such as name, as well as row and column numbers)
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @param currentFirstColumnNumber
	 *            type of play to write (ex: Single)
	 */
	public void writeIndividualResultSheet(IndividualResultSheet individualResultSheet,
			TypeOfResult typeOfResult, int currentFirstColumnNumber)
	{
		ArrayList<IndividualResult> listIndividualResults = PostgreSQLJDBC.getIndividualResults(typeOfResult,
				individualResultSheet.getTypeOfPlay());
		ArrayList<String> listSchoolNames = PostgreSQLJDBC.getAllSchools();

		Sheet sheet = workbook.getSheet(individualResultSheet.getName());
		Font normalFont = workbook.createFont();
		normalFont.setFontHeightInPoints(FONT_SIZE);
		normalFont.setFontName(FONT_NAME);
		normalFont.setBold(false);

		// Create cell style for individual results cells
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.LEFT);

		if (isColor == true)
		{
			for (int i = 0; i < backgroundColors.size(); i++)
			{
				CellStyle coloredCellStyle = workbook.createCellStyle();
				coloredCellStyle.cloneStyleFrom(cellStyle);
				coloredCellStyle.setFont(fonts.get(i));
				coloredCellStyle.setFillForegroundColor(backgroundColors.get(i).getIndex());
				coloredCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

				coloredCellStyles.add(coloredCellStyle);
			}
		}

		// Setting the category's header
		Cell cell = sheet.getRow(IndividualResultSheet.HEADER_ROW)
				.getCell((individualResultSheet.getNumberOfColumns() * currentFirstColumnNumber)
						+ IndividualResultSheet.FIRST_COLUMN);
		cell.setCellValue(individualResultSheet.getHeaderName());

		// Delete all the rows contents for the type of result
		for (int i = 0; i < IndividualResultSheet.NUMBER_OF_ROWS_TO_DELETE; i++)
		{
			for (int j = 0; j < individualResultSheet.getNumberOfColumns(); j++)
			{
				Row row = sheet.getRow(IndividualResultSheet.FIRST_ROW + i);

				if (row != null)
				{
					POIExcelFileProcessor
							.createCell(sheet,
									(individualResultSheet.getNumberOfColumns() * currentFirstColumnNumber)
											+ IndividualResultSheet.FIRST_COLUMN + j,
									IndividualResultSheet.FIRST_ROW + i, "");
				}
			}
		}

		// Iterate over each individual result
		for (int i = 0; i < listIndividualResults.size(); i++)
		{
			CellStyle currentCellStyle;

			if (isColor == true)
			{
				currentCellStyle = coloredCellStyles
						.get(listSchoolNames.indexOf(listIndividualResults.get(i).getSchoolName()));
			}
			else
			{
				currentCellStyle = cellStyle;
				currentCellStyle.setFont(normalFont);
			}

			POIExcelFileProcessor.createCell(sheet,
					(individualResultSheet.getNumberOfColumns() * currentFirstColumnNumber)
							+ IndividualResultSheet.FIRST_COLUMN + IndividualColumn.RANK.number(),
					IndividualResultSheet.FIRST_ROW + i, listIndividualResults.get(i).getRank() + "", currentCellStyle);

			POIExcelFileProcessor.createCell(sheet,
					(individualResultSheet.getNumberOfColumns() * currentFirstColumnNumber)
							+ IndividualResultSheet.FIRST_COLUMN + IndividualColumn.PLAYER_NAME.number(),
					IndividualResultSheet.FIRST_ROW + i, listIndividualResults.get(i).getPlayerName(),
					currentCellStyle);

			POIExcelFileProcessor.createCell(sheet,
					(individualResultSheet.getNumberOfColumns() * currentFirstColumnNumber)
							+ IndividualResultSheet.FIRST_COLUMN + IndividualColumn.SCHOOL_NAME.number(),
					IndividualResultSheet.FIRST_ROW + i, listIndividualResults.get(i).getSchoolName(),
					currentCellStyle);

			// Write the extra single and double score cells as well as the total score for Individual Combined results
			if (listIndividualResults.get(i) instanceof IndividualCombinedResult)
			{
				POIExcelFileProcessor.createCell(sheet,
						(individualResultSheet.getNumberOfColumns() * currentFirstColumnNumber)
								+ IndividualResultSheet.FIRST_COLUMN + IndividualColumn.SINGLE_SCORE.number(),
						IndividualResultSheet.FIRST_ROW + i, listIndividualResults.get(i).getSingleScore() + "",
						currentCellStyle);

				POIExcelFileProcessor.createCell(sheet,
						(individualResultSheet.getNumberOfColumns() * currentFirstColumnNumber)
								+ IndividualResultSheet.FIRST_COLUMN + IndividualColumn.DOUBLE_SCORE.number(),
						IndividualResultSheet.FIRST_ROW + i, listIndividualResults.get(i).getDoubleScore() + "",
						currentCellStyle);

				POIExcelFileProcessor.createCell(sheet,
						(individualResultSheet.getNumberOfColumns() * currentFirstColumnNumber)
								+ IndividualResultSheet.FIRST_COLUMN + IndividualColumn.TOTAL_SCORE.number(),
						IndividualResultSheet.FIRST_ROW + i, listIndividualResults.get(i).getScore() + "",
						currentCellStyle);
			}

			// Write the score for Single and Double results
			else
			{
				POIExcelFileProcessor.createCell(sheet,
						(individualResultSheet.getNumberOfColumns() * currentFirstColumnNumber)
								+ IndividualResultSheet.FIRST_COLUMN + IndividualColumn.SCORE.number(),
						IndividualResultSheet.FIRST_ROW + i, listIndividualResults.get(i).getScore() + "",
						currentCellStyle);
			}
		}
	}

	/**
	 * Writes the team result sheet
	 *
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 */
	public void writeTeamResultSheet(TypeOfResult typeOfResult)
	{
		Sheet sheet = workbook.getSheet(teamResultSheet.getTemplateSheetName());

		// Get the list of team results for the matching type of result
		ArrayList<TeamResult> teamsResults = PostgreSQLJDBC.getTeamResults(typeOfResult);

		// Create cell style for individual results cells
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.LEFT);

		// Extra line for the h1 headers for the two genders
		if ((typeOfResult == TypeOfResult.BENJAMIN_FEMININ) || (typeOfResult == TypeOfResult.BENJAMIN_MASCULIN))
		{
			if (typeOfResult == TypeOfResult.BENJAMIN_FEMININ)
			{
				currentRow = TeamResultSheet.FIRST_ROW;
			}
			else if (typeOfResult == TypeOfResult.BENJAMIN_MASCULIN)
			{
				// Adding the border to the last line of the last category
				CellRangeAddress cellRangeAddress = new CellRangeAddress(currentRow - 1, currentRow - 1,
						TeamColumn.values()[0].number(),
						TeamColumn.values()[TeamColumn.values().length - 1].number() + NUMBER_TOURNAMENTS);

				// Set the border around the type of result
				POIExcelFileProcessor.setBorder(sheet, cellRangeAddress, BorderStyle.MEDIUM, BorderStyle.MEDIUM,
						BorderStyle.NONE, BorderStyle.MEDIUM);

				// Merge the gender header
				cellRangeAddress = new CellRangeAddress(currentRow, currentRow, TeamColumn.values()[0].number(),
						TeamColumn.values()[TeamColumn.values().length - 1].number() + NUMBER_TOURNAMENTS);

				sheet.addMergedRegion(cellRangeAddress);

				// Setting the row page break
				sheet.setRowBreak(currentRow - 1);
			}

			currentRow++;
		}

		// Merge the category headers for the Male players (unsure why it is not needed for the Female players)
		if (typeOfResult.gender() == Gender.MASCULIN)
		{
			CellRangeAddress cellRangeAddress = new CellRangeAddress(currentRow, currentRow,
					TeamColumn.values()[0].number(),
					TeamColumn.values()[TeamColumn.values().length - 1].number() + NUMBER_TOURNAMENTS);
			sheet.addMergedRegion(cellRangeAddress);
		}

		// Skip the column headers ("Rang", "École", "Tournoi1", etc.)
		currentRow += NUMBER_OF_HEADER_ROWS;

		sheet.shiftRows(currentRow, sheet.getLastRowNum(), teamsResults.size());

		// Iterate over each team of the category
		for (int i = 0; i < teamsResults.size(); i++)
		{
			POIExcelFileProcessor.createCell(sheet, TeamResultSheet.FIRST_COLUMN + TeamColumn.RANK.number(), currentRow,
					teamsResults.get(i).getRank() + "", cellStyle);

			POIExcelFileProcessor.createCell(sheet, TeamResultSheet.FIRST_COLUMN + TeamColumn.SCHOOL_NAME.number(),
					currentRow, teamsResults.get(i).getSchoolName());

			// Iterate over each tournament (1 to 5) and write the appropriate score
			for (int j = 0; j <= TeamResultSheet.NUMBER_TOURNAMENTS; j++)
			{
				float score;

				// Unsure what this does
				if (teamsResults.get(i).getScores().size() < (j + 1))
				{
					score = 0;
				}
				else
				{
					score = teamsResults.get(i).getScores().get(j);
				}

				POIExcelFileProcessor.createCell(sheet,
						TeamResultSheet.FIRST_COLUMN + TeamColumn.FIRST_TOURNAMENT.number() + j, currentRow,
						String.format("%.2f", score));
			}

			POIExcelFileProcessor.createCell(sheet,
					TeamResultSheet.FIRST_COLUMN + TeamColumn.FIRST_TOURNAMENT.number()
							+ TeamResultSheet.NUMBER_TOURNAMENTS,
					currentRow, String.format("%.2f", teamsResults.get(i).getTotalScore()));

			currentRow++;
		}

		// Adding the border around the category's table once the rows have been written
		CellRangeAddress cellRangeAddress = null;

		if (typeOfResult != TypeOfResult.JUVÉNILE_MASCULIN)
		{
			cellRangeAddress = new CellRangeAddress(currentRow - teamsResults.size() - NUMBER_OF_HEADER_ROWS,
					currentRow, TeamColumn.values()[0].number(),
					TeamColumn.values()[TeamColumn.values().length - 1].number() + NUMBER_TOURNAMENTS);
		}
		else
		{
			// Remove the border line that shows in the middle of the category's table
			cellRangeAddress = new CellRangeAddress(currentRow - teamsResults.size() - 1,
					currentRow - teamsResults.size() - 1, TeamColumn.values()[0].number(),
					TeamColumn.values()[TeamColumn.values().length - 1].number() + NUMBER_TOURNAMENTS);

			POIExcelFileProcessor.setBorder(sheet, cellRangeAddress, BorderStyle.MEDIUM, BorderStyle.MEDIUM,
					BorderStyle.NONE, BorderStyle.NONE);

			cellRangeAddress = new CellRangeAddress(currentRow - teamsResults.size() - NUMBER_OF_HEADER_ROWS,
					currentRow - 1, TeamColumn.values()[0].number(),
					TeamColumn.values()[TeamColumn.values().length - 1].number() + NUMBER_TOURNAMENTS);
		}

		// Set the border around the type of result
		POIExcelFileProcessor.setBorder(sheet, cellRangeAddress, BorderStyle.MEDIUM, BorderStyle.MEDIUM,
				BorderStyle.MEDIUM, BorderStyle.MEDIUM);
	}
}
