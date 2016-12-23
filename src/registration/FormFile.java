package registration;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
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
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
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
import standings.StandingsCreationHelper.TypeOfPlay;
import standings.StandingsCreationHelper.TypeOfResult;
import utilities.Utilities;

public class FormFile
{
	private static final String FORM_FILE_TEMPLATE_FILENAME = "/resources/Template/FormFile_Template.xlsx";

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

	private static final String OVERANKED_STRING = "Surclassement: ";
	private static final String NO_PLAYER_STRING = "AUCUN JOUEUR LISTÉ";
	private static final String LOOKING_FOR_PARTNER_STRING = "RECHERCHE PARTENAIRE";

	private static final int NUMBER_OF_ID_DIGTS = 3;

	private static final String FONT_NAME = "Arial";
	private static final Short FONT_SIZE = 12;

	private ArrayList<DataValidationConstraint> listConstraints = new ArrayList<DataValidationConstraint>();
	private ArrayList<DataValidationConstraint> listConstraintsShort = new ArrayList<DataValidationConstraint>();

	private Workbook workbook;

	private File file;
	private String schoolName;

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
		SINGLE("SIMPLE MASCULIN", Gender.MASCULIN, TypeOfPlay.SINGLE), DOUBLE_MASCULINE("DOUBLE MASCULIN",
				Gender.MASCULIN,
				TypeOfPlay.DOUBLE), DOUBLE_FEMININE("DOUBLE FÉMININ", Gender.FÉMININ, TypeOfPlay.DOUBLE);

		private static Section[] vals = values();
		private String headerText;
		private TypeOfPlay typeOfPlay;
		private Gender gender;

		Section(String headerText, Gender gender, TypeOfPlay typeOfPlay)
		{
			this.headerText = headerText;
			this.gender = gender;
			this.typeOfPlay = typeOfPlay;
		}

		public String headerText()
		{
			return headerText;
		}

		public Gender gender()
		{
			return gender;
		}

		public TypeOfPlay typeOfPlay()
		{
			return typeOfPlay;
		}

		public Section next()
		{
			return vals[(this.ordinal() + 1) % vals.length];
		}
	}

	public enum SectionHeader {
		SINGLE("SIMPLE MASCULIN"), DOUBLE_MASCULINE("DOUBLE MASCULIN"), DOUBLE_FEMININE("DOUBLE FÉMININ"), NAME(
				"Nom"), EMPTY("");

		private String headerText;

		SectionHeader(String headerText)
		{
			this.headerText = headerText;
		}

		public String headerText()
		{
			return headerText;
		}
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
		Font normalFont = workbook.createFont();
		normalFont.setFontHeightInPoints(FONT_SIZE);
		normalFont.setFontName(FONT_NAME);
		normalFont.setBold(false);

		// Create cell style for individual results cells
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFont(normalFont);

		// Iterate for each row in the double section
		for (int i = 0; i <= numberRows; i++)
		{
			POIExcelFileProcessor.createCell(sheet, Column.MIDDLE.number(), firstRow + i, "ET", cellStyle);
		}
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
	 * Formats the name to be displayed if there are two identical names in one school
	 * WARNING: What about the same category?
	 *
	 * @param playerInCategory
	 *            player to verify the presence of a duplicate
	 * @param playersInNextCategory
	 *            list of players in the next category
	 */
	public String formatName(Player playerInCategory, ArrayList<Player> playersInNextCategory)
	{
		String name = playerInCategory.getName();
		boolean isMatchingPlayer = false;

		// Iterate over each player in the list of players for the next category
		for (int i = 0; i < playersInNextCategory.size(); i++)
		{
			if (name.equals(playersInNextCategory.get(i).getName()))
			{
				isMatchingPlayer = true;
			}
		}

		if (isMatchingPlayer)
		{
			name += " (" + playerInCategory.getId().substring(playerInCategory.getId().length() - NUMBER_OF_ID_DIGTS) + ")";
		}

		return name;
	}

	/**
	 * Gets the formatted name from a cell
	 *
	 * @param cell
	 *            cell that contains the name of a player
	 * @return formatted (trimmed and Surclassement string removed)
	 */
	public String getName(String cellContent)
	{
		// Trim the leading space in front of the name
		String name = cellContent.trim();

		// Remove the Overanked string in front of the name if present
		if (name.indexOf(OVERANKED_STRING) != -1)
		{
			name = name.substring(OVERANKED_STRING.length());
		}

		// Get only the name component if an ID is present
		if (name.indexOf("(") != -1)
		{
			name = name.substring(0, name.indexOf("(") - 1);
		}

		return name;
	}

	/**
	 * Gets the formatted id from a cell
	 *
	 * @param cellContent
	 *            content of the cell (name and possibly id)
	 * @return id
	 */
	public String getID(String cellContent)
	{
		String id = null;

		// Get the ID if present
		if (cellContent.indexOf("(") != -1)
		{
			id = cellContent.substring(cellContent.indexOf("(") + 1, cellContent.indexOf(")"));
		}

		return id;
	}

	/**
	 * Populates the hidden sheet that contains name for all the players of each school
	 * Will be used as a reference for drop-down lists for the main sheets.
	 *
	 * @param categoryNumber
	 *            number of the category (from 0 to 2)
	 * @param genderNumber
	 *            number of the gender (from 0 to 1)
	 * @param players
	 *            list of the player names for a given category and a given gender
	 * @param overankedPlayers
	 *            list of the overanked player names for a given category and a given gender
	 */
	public void populateHiddenSheet(int categoryNumber, int genderNumber, ArrayList<Player> players,
			ArrayList<Player> overankedPlayers)
	{
		DataValidationConstraint constraint = null;
		DataValidationHelper validationHelper = null;
		final String NAME_PREFIX = " ";
		String name = new String();

		Sheet hiddenSheet = workbook.getSheet(HIDDEN_SHEET_NAME);
		int columnNumber = (categoryNumber * Gender.values().length) + genderNumber;

		// Creates the hidden sheet if it does not exist
		if (hiddenSheet == null)
		{
			hiddenSheet = workbook.createSheet(HIDDEN_SHEET_NAME);
			workbook.setSheetHidden(workbook.getSheetIndex(HIDDEN_SHEET_NAME), Workbook.SHEET_STATE_VERY_HIDDEN);
		}

		// Adds the list of player names in the sheet
		for (int i = 0; i < players.size(); i++)
		{
			name = players.get(i).getName();

			name = formatName(players.get(i), overankedPlayers);

			POIExcelFileProcessor.createCell(hiddenSheet, columnNumber, i, NAME_PREFIX + name);
		}

		// Adds the list of overanked player names in the sheet
		for (int i = 0; i < overankedPlayers.size(); i++)
		{
			name = OVERANKED_STRING + formatName(overankedPlayers.get(i), players);

			POIExcelFileProcessor.createCell(hiddenSheet, columnNumber, i + players.size(), NAME_PREFIX + name);
		}

		// Adds the entry "RECHERCHE PARTENAIRE" which is used for double teams
		if ((players.size() != 0) || (overankedPlayers.size() != 0))
		{
			name = LOOKING_FOR_PARTNER_STRING;
		}

		// Adds the entry that there is no player for the given category and gender
		else
		{
			name = NO_PLAYER_STRING;
		}

		POIExcelFileProcessor.createCell(hiddenSheet, columnNumber, overankedPlayers.size() + players.size(),
				NAME_PREFIX + name);

		// Sets the validation for the drop-down lists
		validationHelper = hiddenSheet.getDataValidationHelper();
		constraint = validationHelper.createFormulaListConstraint(HIDDEN_SHEET_NAME + "!$" + (char) (columnNumber + 65)
				+ "$1:$" + (char) (columnNumber + 65) + "$" + (players.size() + overankedPlayers.size() + 1));

		listConstraints.add(constraint);

		constraint = validationHelper.createFormulaListConstraint(HIDDEN_SHEET_NAME + "!$" + (char) (columnNumber + 65)
				+ "$1:$" + (char) (columnNumber + 65) + "$" + Math.max(players.size() + overankedPlayers.size(), 1));

		listConstraintsShort.add(constraint);
	}

	/**
	 * Read a list of players from the forms Excel file
	 *
	 * @return list of registrations corresponding to the form file
	 */
	public RegistrationTeam read()
	{
		workbook = POIExcelFileProcessor.createWorkbook(file);

		RegistrationTeam registrationTeam = new RegistrationTeam(schoolName);

		for (int i = 0; i < Category.values().length; i++)
		{
			Sheet sheet = workbook.getSheet(Category.values()[i].text());

			ArrayList<Player> listSinglePlayers = new ArrayList<Player>();
			ArrayList<DoubleTeam> listDoubleTeams = new ArrayList<DoubleTeam>();
			Row row = null;

			String name = new String();
			String id = null;

			Category category = Category.values()[i];
			Player doublePlayer = null;

			Section section = Section.SINGLE;

			// Iterate over the number of rows in the sheet. Start from the second row as the first row is a header
			for (int j = FIRST_ROW; j < sheet.getLastRowNum(); j++)
			{
				row = sheet.getRow(j);

				// Check if the row is empty
				if (row != null)
				{
					// Read specific cells for each row.
					for (int k = Column.FIRST_NAME.number(); k <= Column.SECOND_NAME.number(); k++)
					{
						// Add the content of the cells to the list of single players and double players
						if (k == Column.FIRST_NAME.number())
						{
							if (row.getCell(k).getStringCellValue().equals(SectionHeader.DOUBLE_MASCULINE.headerText())
									|| row.getCell(k).getStringCellValue()
											.equals(SectionHeader.DOUBLE_FEMININE.headerText()))
							{
								section = section.next();

								// Skip row as the row only contains header text
								k = Column.values().length + 1;
							}
							else if (row.getCell(k).getStringCellValue().equals(SectionHeader.NAME.headerText()))
							{
								// Skip row as the row only contains header text
								k = Column.values().length + 1;
							}
							else if (row.getCell(k).getStringCellValue().equals(SectionHeader.EMPTY.headerText()))
							{
								// Do nothing
							}
							else
							{
								name = getName(row.getCell(k).getStringCellValue());
								id = getID(row.getCell(k).getStringCellValue());

								if (!name.equals("") && !name.equals(NO_PLAYER_STRING))
								{
									// Add player to list of single players
									if (section == Section.SINGLE)
									{
										listSinglePlayers
												.add(new Player(id, name, schoolName, Gender.MASCULIN, category));
									}
									// Add player to list of double players
									else
									{

										doublePlayer = new Player(id, name, schoolName, section.gender(), category);
									}
								}
								else
								{
									doublePlayer = null;
								}
							}
						}

						else if (k == Column.SECOND_NAME.number())
						{
							// Check if cell exists in row
							if (k < row.getLastCellNum())
							{
								name = getName(row.getCell(k).getStringCellValue());
								id = getID(row.getCell(k).getStringCellValue());

								if (!name.equals("") && !name.equals(NO_PLAYER_STRING))
								{
									// Add player to list of single players
									if (section == Section.SINGLE)
									{
										listSinglePlayers
												.add(new Player(id, name, schoolName, Gender.FÉMININ, category));
									}
									else
									{
										// Add players to list of double players
										if (!(name.equals(LOOKING_FOR_PARTNER_STRING)
												&& doublePlayer.getName().equals(LOOKING_FOR_PARTNER_STRING)))
										{
											listDoubleTeams.add(new DoubleTeam(doublePlayer,
													new Player(id, name, schoolName, section.gender(), category)));
										}
									}
								}
							}
						}
					}
				}
			}

			registrationTeam.addRegistration(new Registration(listSinglePlayers, listDoubleTeams, category));
		}

		return registrationTeam;
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
	 * WARNING: Currently unused, possibly irrelevant.
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
		for (int i = startRow; i <= startRow + numberOfRows; i++)
		{
			// Iterate for each cell in the section
			for (int j = 0; j <= Column.SECOND_NAME.number(); j++)
			{
				POIExcelFileProcessor.createCell(sheet, j, i, "", unlockedCellStyle);
			}
		}
	}

	/**
	 * Writes the form file
	 * WARNING1: Has a bug about the last line of a section having a bigger font than the previous lines.
	 * WARNING2: Could possibly be enhanced to lock all the contents of the sheets besides the drop-down lists.
	 *
	 * @throws SQLException
	 */
	public void write() throws SQLException
	{
		workbook = POIExcelFileProcessor
				.createWorkbook(FormFileTest.class.getResourceAsStream(FORM_FILE_TEMPLATE_FILENAME));
		DataValidationHelper validationHelper = null;
		int[] constraintIndexes = new int[2];

		// Iterate for each category of play (ex: Benjamin, cadet and juvénile)
		for (int i = 0; i < Category.values().length; i++)
		{
			Sheet sheet = workbook.getSheet(TypeOfResult.values()[i].category().text());
			validationHelper = sheet.getDataValidationHelper();
			// sheet.protectSheet("Test");

			ArrayList<Player> malePlayers = new ArrayList<Player>();
			ArrayList<Player> overankedMalePlayers = new ArrayList<Player>();
			ArrayList<Player> femalePlayers = new ArrayList<Player>();
			ArrayList<Player> overankedFemalePlayers = new ArrayList<Player>();

			malePlayers = PostgreSQLJDBC.getPlayersByFilter(schoolName, Category.values()[i], Gender.MASCULIN);

			if (Utilities.getPreviousCategory(Category.values()[i]) != null)
			{
				overankedMalePlayers = PostgreSQLJDBC.getPlayersByFilter(schoolName,
						Utilities.getPreviousCategory(Category.values()[i]), Gender.MASCULIN);
			}

			femalePlayers = PostgreSQLJDBC.getPlayersByFilter(schoolName, Category.values()[i], Gender.FÉMININ);

			if (Utilities.getPreviousCategory(Category.values()[i]) != null)
			{
				overankedFemalePlayers = PostgreSQLJDBC.getPlayersByFilter(schoolName,
						Utilities.getPreviousCategory(Category.values()[i]), Gender.FÉMININ);
			}

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

					numberRows = Math.max(malePlayers.size() + overankedMalePlayers.size(),
							femalePlayers.size() + overankedFemalePlayers.size());

					constraintIndexes[0] = i * Gender.values().length;
					constraintIndexes[1] = (i * Gender.values().length) + 1;

					break;

				case DOUBLE_MASCULINE:

					numberRows = ((malePlayers.size() + overankedMalePlayers.size()) / 2) + 1;

					constraintIndexes[0] = constraintIndexes[1] = i * Gender.values().length;

					break;

				case DOUBLE_FEMININE:

					numberRows = ((femalePlayers.size() + overankedFemalePlayers.size()) / 2) + 1;

					constraintIndexes[0] = constraintIndexes[1] = (i * Gender.values().length) + 1;

					break;
				}

				// Shift rows if the number of players for the section is not empty
				if (numberRows != 0)
				{
					sheet.shiftRows(firstRow, sheet.getLastRowNum(), numberRows);
				}

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

				setSectionBorder(sheet, firstRow, numberRows);

				CellRangeAddressList[] addressList = new CellRangeAddressList[2];

				addressList[0] = new CellRangeAddressList(firstRow, firstRow + numberRows, Column.FIRST_NAME.number(),
						Column.FIRST_NAME.number());
				addressList[1] = new CellRangeAddressList(firstRow, firstRow + numberRows, Column.SECOND_NAME.number(),
						Column.SECOND_NAME.number());

				// Iterate for each gender
				for (int k = 0; k < Gender.values().length; k++)
				{
					DataValidation validation = null;

					ArrayList<Player> players = null;
					ArrayList<Player> overankedPlayers = null;

					if (Gender.values()[k] == Gender.MASCULIN)
					{
						players = malePlayers;
						overankedPlayers = overankedMalePlayers;
					}
					else
					{
						players = femalePlayers;
						overankedPlayers = overankedFemalePlayers;
					}

					// Create or populate the hidden sheet only once per sheet
					if (Section.values()[j] == Section.SINGLE)
					{
						populateHiddenSheet(i, k, players, overankedPlayers);
						validation = validationHelper.createValidation(listConstraintsShort.get(constraintIndexes[k]),
								addressList[k]);
					}
					else
					{
						validation = validationHelper.createValidation(listConstraints.get(constraintIndexes[k]),
								addressList[k]);
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

		POIExcelFileProcessor.writeWorkbook(workbook, file);
	}
}
