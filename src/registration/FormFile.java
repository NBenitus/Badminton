package registration;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import database.PostgreSQLJDBC;
import excelHelper.POIExcelFileProcessor;
import standings.Player;
import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.Gender;
import standings.StandingsCreationHelper.TypeOfResult;

public class FormFile
{
	private static final int MASCULINE_INDEX = 0;
	private static final int FEMININE_INDEX = 1;

	private static final int FIRST_ROW = 9;

	private static final int DATE_COLUMN = 1;
	private static final int DATE_ROW = 5;
	private static final String DATE_PREFIX = "DATE: ";

	private static final int SCHOOL_NAME_COLUMN = 1;
	private static final int SCHOOL_NAME_ROW = 4;
	private static final String SCHOOL_NAME_PREFIX = "INSTITUTION: ";

	private static final int NUMBER_OF_ROWS_BETWEEN_SECTIONS = 4;
	private static final Short CONTACT_INFO_HEIGHT = 950;

	private static final String HIDDEN_SHEET_NAME = "Hidden";
	private static final int NUMBER_DISTINCT_LISTS = 2;

	private ArrayList<DataValidationConstraint> listConstraints = new ArrayList<DataValidationConstraint>();
	private ArrayList<DataValidationConstraint> listConstraintsShort = new ArrayList<DataValidationConstraint>();

	private Workbook workbook;

	public enum Column {
		FIRST_NAME(1), MIDDLE(2), SECOND_NAME(3);

		private int number;

		Column(int number)
		{
			this.number = number;
		}

		public int number()
		{
			return number;
		}
	}

	public enum Section {
		SINGLE("SIMPLE MASCULIN"), DOUBLE_MASCULINE("DOUBLE MASCULIN"), DOUBLE_FEMININE("DOUBLE FÉMININ");

		private static Section[] vals = values();
		private String headerText;

		Section(String headerText)
		{
			this.headerText = headerText;
		}

		public String text()
		{
			return headerText;
		}

		public Section next()
		{
			return vals[(this.ordinal() + 1) % vals.length];
		}
	}

	public enum SectionHeader {
		SINGLE("SIMPLE MASCULIN"), DOUBLE_MASCULINE("DOUBLE MASCULIN"), DOUBLE_FEMININE("DOUBLE FÉMININ"), NAME(
				"NOM"), EMPTY("");

		private String headerText;

		SectionHeader(String headerText)
		{
			this.headerText = headerText;
		}

		public String text()
		{
			return headerText;
		}
	}

	private File file;
	private InputStream inputStream;
	private File outputFile;
	private String schoolName;

	/**
	 * Constructor.
	 *
	 * @param inputStream
	 *            input stream to the form file's template
	 * @param outputFile
	 *            form file created
	 * @param schoolName
	 *            name of the school for which the form is associated
	 */
	public FormFile(InputStream inputStream, File outputFile, String schoolName)
	{
		this.inputStream = inputStream;
		this.outputFile = outputFile;
		this.schoolName = schoolName;
	}

	/**
	 * Constructor.
	 *
	 * @param file
	 *            file object that contains the form file
	 * @param schoolName
	 *            name of the school for which the form is associated
	 */
	public FormFile(File file, String schoolName)
	{
		this.file = file;
		this.outputFile = null;
		this.schoolName = schoolName;
	}

	/**
	 * Adds the "ET" text in between the two rows of player names for the double sections
	 *
	 * @param sheet
	 *            sheet that contains the double rows
	 * @param firstRow
	 *            first row of the double rows
	 * @param numberRows
	 *            number of double rows
	 */
	public void addMiddleColumnText(Sheet sheet, int firstRow, int numberRows)
	{
		// Iterate for each row in the double section
		for (int i = firstRow; i <= firstRow + numberRows; i++)
		{
			POIExcelFileProcessor.createCell(sheet, Column.MIDDLE.number(), i, "ET");
		}
	}

	/**
	 * Populates the hidden sheet that contains name for all the players of each school
	 * Will be used as a reference for drop-down lists for the main sheets.
	 *
	 * @param categoryNumber
	 *            number of the category (from 0 to 2)
	 * @param genderNumber
	 *            number of the gender (from 0 to 1)
	 * @param listPlayerNames
	 *            list of the player names for a given category and a given gender
	 */
	public void populateHiddenSheet(int categoryNumber, int genderNumber, ArrayList<String> listPlayerNames)
	{
		DataValidationConstraint constraint = null;
		DataValidationHelper validationHelper = null;

		Cell cell = null;

		Sheet hiddenSheet = workbook.getSheet(HIDDEN_SHEET_NAME);

		// Creates the hidden sheet if it does not exist
		if (hiddenSheet == null)
		{
			hiddenSheet = workbook.createSheet(HIDDEN_SHEET_NAME);
			workbook.setSheetHidden(workbook.getSheetIndex(HIDDEN_SHEET_NAME), Workbook.SHEET_STATE_VERY_HIDDEN);
		}

		hiddenSheet.protectSheet("Test");

		// Adds the list of player names in the sheet
		for (int i = 0; i <= listPlayerNames.size(); i++)
		{
			String name = null;


			if (i == listPlayerNames.size())
			{
				// Adds the entry "RECHERCHE PARTENAIRE" which is used for double teams
				if (listPlayerNames.size() != 0)
				{
					name = "RECHERCHE PARTENAIRE";
				}

				// Adds the entry that there is no player for the given category and gender
				else
				{
					name = "AUCUN JOUEUR LISTÉ";
				}
			}
			else
			{
				name = listPlayerNames.get(i);
			}

			Row row = hiddenSheet.getRow(i);

			// Create row if it does not exist
			if (row == null)
			{
				row = hiddenSheet.createRow(i);
			}

			cell = row.createCell((categoryNumber * Gender.values().length)+ genderNumber);
			cell.setCellValue(name);
		}

		// Set the validation for the drop-down lists
		validationHelper = hiddenSheet.getDataValidationHelper();
		constraint = validationHelper
				.createFormulaListConstraint(HIDDEN_SHEET_NAME + "!$" + (char) (cell.getColumnIndex() + 65)
						+ "$1:$" + (char) (cell.getColumnIndex() + 65) + "$" + (listPlayerNames.size() + 1));

		listConstraints.add(constraint);

		constraint = validationHelper
				.createFormulaListConstraint(HIDDEN_SHEET_NAME + "!$" + (char) (cell.getColumnIndex() + 65)
						+ "$1:$" + (char) (cell.getColumnIndex() + 65) + "$" + Math.max(listPlayerNames.size(), 1));

		listConstraintsShort.add(constraint);
	}

	/**
	 * Formats the contact information cells since their parameters are lost during the shiftRows function
	 *
	 * @param sheet
	 *            sheet for which to format the contact information cells
	 * @param firstContactInformationRow
	 *            first row where the contact information cells are situated
	 */
	public void formatContactInformationCells(Sheet sheet, int firstContactInformationRow)
	{
		// Merges the cells for the first contact information row
		CellRangeAddress cellRangeAddress = new CellRangeAddress(firstContactInformationRow, firstContactInformationRow,
				Column.FIRST_NAME.number(), Column.SECOND_NAME.number());
		sheet.addMergedRegion(cellRangeAddress);

		// Merges the cells for the second contact information row
		cellRangeAddress = new CellRangeAddress(firstContactInformationRow + 1, firstContactInformationRow + 1,
				Column.FIRST_NAME.number(), Column.SECOND_NAME.number());
		sheet.addMergedRegion(cellRangeAddress);

		// Adjust the row's height to contain for the first contact information row
		Row row = sheet.getRow(firstContactInformationRow);
		row.setHeight(CONTACT_INFO_HEIGHT);

		// Add borders for the first contact information cell
		cellRangeAddress = new CellRangeAddress(firstContactInformationRow, firstContactInformationRow,
				Column.FIRST_NAME.number(), Column.SECOND_NAME.number());

		POIExcelFileProcessor.setBorder(sheet, cellRangeAddress, BorderStyle.MEDIUM, BorderStyle.MEDIUM,
				BorderStyle.MEDIUM, BorderStyle.NONE);

		// Add borders for the second contact information cell
		cellRangeAddress = new CellRangeAddress(firstContactInformationRow + 1, firstContactInformationRow + 1,
				Column.FIRST_NAME.number(), Column.SECOND_NAME.number());

		POIExcelFileProcessor.setBorder(sheet, cellRangeAddress, BorderStyle.MEDIUM, BorderStyle.MEDIUM,
				BorderStyle.MEDIUM, BorderStyle.MEDIUM);
	}

	/**
	 * Read a list of players from the forms Excel file
	 *
	 * @return list of registrations corresponding to the form file
	 */
	public ArrayList<Registration> read()
	{
		ArrayList<Registration> registrationList = new ArrayList<Registration>();

		try
		{
			// InputStream inputStream = new FileInputStream(file);
			workbook = WorkbookFactory.create(file);

			for (int i = 0; i < Category.values().length; i++)
			{
				Sheet sheet = workbook.getSheet(Category.values()[i].text());

				ArrayList<Player> listSinglePlayers = new ArrayList<Player>();
				ArrayList<DoubleTeam> listDoubleTeams = new ArrayList<DoubleTeam>();
				Row row = null;

				String name = new String();
				Category category = Category.values()[i];

				boolean isPlayer = true;
				Section section = Section.SINGLE;
				Gender gender = Gender.MASCULIN;
				Player doublePlayer = null;

				// Iterate over the number of rows in the sheet. Start from the second row as the first row is a header
				for (int j = FIRST_ROW; j < sheet.getLastRowNum(); j++)
				{
					row = sheet.getRow(j);

					// Check if the row is empty
					if (row != null)
					{
						// Read specific cells for each row.
						for (int k = Column.FIRST_NAME.number(); k <= Column.values().length; k++)
						{
							// Add the content of the cells to the list of single players and double players
							if (k == Column.FIRST_NAME.number())
							{
								switch (SectionHeader.valueOf(row.getCell(k).getStringCellValue()))
								{

								// Category headers
								case DOUBLE_MASCULINE:

									isPlayer = false;

									section = Section.DOUBLE_MASCULINE;
									gender = Gender.MASCULIN;

									break;

								case DOUBLE_FEMININE:

									isPlayer = false;

									section = Section.DOUBLE_FEMININE;
									gender = Gender.FÉMININ;

									break;

								// Subsection header
								case NAME:
								case EMPTY:

									isPlayer = false;
									break;

								default:
									isPlayer = true;

									name = row.getCell(k).getStringCellValue();

									if (!name.equals(""))
									{

										// Add player to list of single players
										if (section == Section.SINGLE)
										{
											listSinglePlayers.add(new Player(name, schoolName, gender, category));
										}

										// Add player to list of double players
										else
										{
											doublePlayer = new Player(name, schoolName, gender, category);
										}
									}

									break;
								}
							}

							else if (k == Column.SECOND_NAME.number())
							{

								// Check if cell exists in row
								if (k < row.getLastCellNum())
								{
									// Check if cell is for a player, instead of a header
									if (isPlayer == true)
									{
										name = row.getCell(k).getStringCellValue();

										if (!name.equals(""))
										{
											// Add player to list of single players
											if (section == Section.SINGLE)
											{
												listSinglePlayers.add(new Player(name, schoolName, gender, category));
											}
											else
											{
												// Both players are specified for a double team
												// Add players to list of double players
												if (!name.equals("RECHERCHE PARTENAIRE"))
												{
													listDoubleTeams.add(new DoubleTeam(doublePlayer,
															new Player(name, schoolName, gender, category)));
												}
												// A player needs a partner for a double team
												else
												{
													listDoubleTeams.add(new DoubleTeam(doublePlayer, new Player(
															"RECHERCHE PARTENAIRE", schoolName, gender, category)));
												}
											}
										}
									}
								}
							}
						}
					}
				}

				registrationList.add(new Registration(listSinglePlayers, listDoubleTeams));
			}

			// Code to close objects
		}
		catch (

		Exception e)
		{
			e.printStackTrace();
		}

		return registrationList;
	}

	/**
	 * Sets the black background to the cells in the middle column in the Single section
	 *
	 * @param sheet
	 *            sheet which contains the cells that will get the black background
	 * @param startRow
	 *            first row of the Single section
	 * @param numberOfRows
	 *            number of rows in the Single section
	 */
	public void setBlackBackground(Sheet sheet, int startRow, int numberOfRows)
	{
		CellStyle blackBackground = workbook.createCellStyle();
		blackBackground.setFillForegroundColor(IndexedColors.BLACK.getIndex());
		blackBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		// Iterate for each row in the Single section that contains players validation
		for (int i = startRow; i <= startRow + numberOfRows; i++)
		{
			POIExcelFileProcessor.createCell(sheet, Column.MIDDLE.number(), i, "", blackBackground);

			// Set borders for the middle column cell
			CellRangeAddress cellRangeAddress = new CellRangeAddress(i, i, Column.MIDDLE.number(),
					Column.MIDDLE.number());

			POIExcelFileProcessor.setBorder(sheet, cellRangeAddress, BorderStyle.MEDIUM, BorderStyle.MEDIUM,
					BorderStyle.MEDIUM, BorderStyle.MEDIUM);
		}
	}

	/**
	 * Sets the border for each section
	 *
	 * @param sheet
	 *            sheet for which to add border to cells
	 * @param startRow
	 *            first row for the range of cells
	 * @param numberOfRows
	 *            number of rows contained in the range of cells
	 */
	public void setSectionBorder(Sheet sheet, int startRow, int numberOfRows)
	{
		CellRangeAddress cellRangeAddress = new CellRangeAddress(startRow, startRow + numberOfRows,
				Column.FIRST_NAME.number(), Column.SECOND_NAME.number());

		POIExcelFileProcessor.setBorder(sheet, cellRangeAddress, BorderStyle.MEDIUM, BorderStyle.MEDIUM,
				BorderStyle.MEDIUM, BorderStyle.MEDIUM);
	}

	/**
	 * Sets the date for the form file
	 *
	 * @param sheet
	 *            sheet for which to add the date
	 */
	public void setCurrentDate(Sheet sheet)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();

		Row row = sheet.getRow(DATE_ROW);
		Cell cell = row.getCell(DATE_COLUMN);
		cell.setCellValue(DATE_PREFIX + dateFormat.format(date));
	}

	/**
	 * Sets the school name for the form file
	 *
	 * @param sheet
	 *            sheet for which to add the school name
	 */
	public void setSchoolName(Sheet sheet)
	{
		Row row = sheet.getRow(SCHOOL_NAME_ROW);
		Cell cell = row.getCell(SCHOOL_NAME_COLUMN);
		cell.setCellValue(SCHOOL_NAME_PREFIX + schoolName);
	}

	/**
	 * Unlocks cells in the section.
	 * CURRENTLY NOT WORKING (HSSF VS XSSF)???
	 *
	 * @param sheet
	 *            sheet that contains the cells to unlocked
	 * @param startRow
	 *            first row of the section to unlock
	 * @param numberOfRows
	 *            number of rows in the section to unlock
	 */
	public void unlockCells(Sheet sheet, int startRow, int numberOfRows)
	{
		CellStyle unlockedCellStyle = workbook.createCellStyle();
		unlockedCellStyle.setLocked(false);

		// Iterate for each row in the section
		for (int i = startRow; i < startRow + numberOfRows; i++)
		{
			Row row = sheet.getRow(i);

			// Iterate for each cell in the section
			for (int j = 0; j < row.getLastCellNum(); j++)
			{
				Cell cell = row.getCell(j);

				if (cell != null)
				{
					cell.setCellStyle(unlockedCellStyle);
				}
			}
		}
	}

	/**
	 * Writes the form file
	 */
	public void write()
	{
		workbook = POIExcelFileProcessor.createWorkbook(inputStream);
		DataValidationHelper validationHelper = null;
		int[] constraintIndexes = new int[2];

		// Iterate for each category of play (ex: Benjamin, cadet and juvénile)
		for (int i = 0; i < Category.values().length; i++)
		{
			Sheet sheet = workbook.getSheet(TypeOfResult.values()[i].category().text());
			validationHelper = sheet.getDataValidationHelper();
			// sheet.protectSheet("Test");

			ArrayList<ArrayList<String>> listsPlayers = new ArrayList<ArrayList<String>>();

			listsPlayers.add(PostgreSQLJDBC.getPlayersByFilter(schoolName, Category.values()[i],
					Gender.values()[MASCULINE_INDEX]));

			listsPlayers.add(PostgreSQLJDBC.getPlayersByFilter(schoolName, Category.values()[i],
					Gender.values()[FEMININE_INDEX]));

			setSchoolName(sheet);
			setCurrentDate(sheet);

			// Freeze the header rows for scrolling purposes
			for (int m = 0; m < FIRST_ROW - 1; m++)
			{
				sheet.createFreezePane(0, m);
			}

			int firstRow = FIRST_ROW;

			// Iterate over the three sections of a sheet in a form file (Single, Double Masculin and Double
			// Féminin)
			for (int j = 0; j < Section.values().length; j++)
			{
				int numberRows = 0;

				switch (Section.values()[j])
				{

				case SINGLE:

					numberRows = Math.max(listsPlayers.get(MASCULINE_INDEX).size(),
							listsPlayers.get(FEMININE_INDEX).size());

					constraintIndexes[0] = i * Gender.values().length;
					constraintIndexes[1] = (i * Gender.values().length) + 1;

					break;

				case DOUBLE_MASCULINE:

					numberRows = (listsPlayers.get(MASCULINE_INDEX).size() / 2) + 1;

					constraintIndexes[0] = constraintIndexes[1] = i * Gender.values().length;

					break;

				case DOUBLE_FEMININE:

					numberRows = (listsPlayers.get(FEMININE_INDEX).size() / 2) + 1;

					constraintIndexes[0] = constraintIndexes[1] = (i * Gender.values().length) + 1;

					break;
				}

				sheet.shiftRows(firstRow, sheet.getLastRowNum(), numberRows);
				setSectionBorder(sheet, firstRow, numberRows);
				// unlockCells(workbook, sheet, firstRow, numberRows);

				// Add the "ET" text in the middle column for double play
				if ((Section.values()[j] == Section.DOUBLE_MASCULINE)
						|| (Section.values()[j] == Section.DOUBLE_FEMININE))
				{
					addMiddleColumnText(sheet, firstRow, numberRows);
				}
				else
				{
					setBlackBackground(sheet, firstRow, numberRows);
				}

				CellRangeAddressList[] addressList = new CellRangeAddressList[2];

				addressList[MASCULINE_INDEX] = new CellRangeAddressList(firstRow, firstRow + numberRows,
						Column.FIRST_NAME.number(), Column.FIRST_NAME.number());
				addressList[FEMININE_INDEX] = new CellRangeAddressList(firstRow, firstRow + numberRows,
						Column.SECOND_NAME.number(), Column.SECOND_NAME.number());

				// Iterate for each gender
				for (int k = 0; k < Gender.values().length; k++)
				{
					DataValidation validation = null;

					if (Section.values()[j] == Section.SINGLE)
					{
						populateHiddenSheet(i, k, listsPlayers.get(k));
						validation = validationHelper
							.createValidation(listConstraintsShort.get(constraintIndexes[k]), addressList[k]);
					}
					else
					{
						validation = validationHelper
								.createValidation(listConstraints.get(constraintIndexes[k]), addressList[k]);
					}

					sheet.addValidationData(validation);
				}

				switch (Section.values()[j])
				{
				case SINGLE:
				case DOUBLE_MASCULINE:

					firstRow += numberRows + NUMBER_OF_ROWS_BETWEEN_SECTIONS;

					break;

				case DOUBLE_FEMININE:

					formatContactInformationCells(sheet, firstRow + numberRows + 2);
					break;
				}
			}
		}

		POIExcelFileProcessor.writeWorkbook(workbook, outputFile);
	}
}
