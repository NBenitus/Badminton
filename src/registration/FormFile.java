package registration;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFRegionUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import database.PostgreSQLJDBC;
import excelHelper.ExcelFileReader;
import excelHelper.POIExcelFileProcessor;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import standings.Player;
import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.Gender;
import standings.StandingsCreationHelper.TypeOfResult;

public class FormFile
{
	private static final int MASCULINE_INDEX = 0;
	private static final int FEMININE_INDEX = 1;

	private static final int NAME_COLUMNS[] = { 1, 3 };
	private static final int FIRST_NAME_COLUMN = 0;
	private static final int SECOND_NAME_COLUMN = 1;
	private static final int FIRST_ROW = 9;
	private static final int FIRST_COLUMN = 0;
	private static final int LAST_COLUMN = 3;

	private static final int DATE_COLUMN = 1;
	private static final int DATE_ROW = 5;
	private static final String DATE_PREFIX = "DATE: ";

	private static final int SCHOOL_NAME_COLUMN = 1;
	private static final int SCHOOL_NAME_ROW = 4;
	private static final String SCHOOL_NAME_PREFIX = "INSTITUTION: ";

	private static final int NUMBER_OF_ROWS_BETWEEN_SECTIONS = 4;

	private ArrayList<DVConstraint> listConstraints = new ArrayList<DVConstraint>();

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
	 */
	public FormFile(File file, String schoolName)
	{
		this.file = file;
		this.outputFile = null;
		this.schoolName = schoolName;
	}

	/**
	 * Formats the contact information cells since their parameters are lost during the shiftRows function
	 *
	 * @param workbook
	 *            workbook object that contains the form file
	 * @param sheet
	 *            sheet for which to format the contact information cells
	 * @param firstContactInformationRow
	 *            first row where the contact information cells are situated
	 */
	public void formatContactInformationCells(HSSFWorkbook workbook, HSSFSheet sheet, int firstContactInformationRow)
	{
		// Merges the cells for the first contact information row
		CellRangeAddress cellRangeAddress = new CellRangeAddress(firstContactInformationRow, firstContactInformationRow,
				FIRST_COLUMN, LAST_COLUMN);
		sheet.addMergedRegion(cellRangeAddress);

		// Merges the cells for the second contact information row
		cellRangeAddress = new CellRangeAddress(firstContactInformationRow + 1, firstContactInformationRow + 1,
				FIRST_COLUMN, LAST_COLUMN);
		sheet.addMergedRegion(cellRangeAddress);

		// Adjust the row's height to contain for the first contact information row
		HSSFRow row = sheet.getRow(firstContactInformationRow);
		row.setHeight((short) 750);

		// Add borders for the first contact information cell
		cellRangeAddress = new CellRangeAddress(firstContactInformationRow, firstContactInformationRow, FIRST_COLUMN,
				LAST_COLUMN);

		HSSFRegionUtil.setBorderTop(CellStyle.BORDER_MEDIUM, cellRangeAddress, sheet, workbook);
		HSSFRegionUtil.setBorderLeft(CellStyle.BORDER_MEDIUM, cellRangeAddress, sheet, workbook);
		HSSFRegionUtil.setBorderRight(CellStyle.BORDER_MEDIUM, cellRangeAddress, sheet, workbook);
		HSSFRegionUtil.setBorderBottom(CellStyle.BORDER_NONE, cellRangeAddress, sheet, workbook);

		// Add borders for the second contact information cell
		cellRangeAddress = new CellRangeAddress(firstContactInformationRow + 1, firstContactInformationRow + 1,
				FIRST_COLUMN, LAST_COLUMN);

		HSSFRegionUtil.setBorderTop(CellStyle.BORDER_NONE, cellRangeAddress, sheet, workbook);
		HSSFRegionUtil.setBorderBottom(CellStyle.BORDER_MEDIUM, cellRangeAddress, sheet, workbook);
		HSSFRegionUtil.setBorderLeft(CellStyle.BORDER_MEDIUM, cellRangeAddress, sheet, workbook);
		HSSFRegionUtil.setBorderRight(CellStyle.BORDER_MEDIUM, cellRangeAddress, sheet, workbook);
	}

	/**
	 * Adds border around a range of cells
	 *
	 * @param workbook
	 *            workbook object that contains the form file
	 * @param sheet
	 *            sheet for which to add border to cells
	 * @param startRow
	 *            first row for the range of cells
	 * @param numberOfRows
	 *            number of rows contained in the range of cells
	 */
	public void addBorder(HSSFWorkbook workbook, HSSFSheet sheet, int startRow, int numberOfRows)
	{
		// Set borders for the left most cell
		CellRangeAddress cellRangeAddress = new CellRangeAddress(startRow, startRow + numberOfRows, FIRST_COLUMN,
				FIRST_COLUMN);
		HSSFRegionUtil.setBorderLeft(CellStyle.BORDER_MEDIUM, cellRangeAddress, sheet, workbook);
		HSSFRegionUtil.setBorderRight(CellStyle.BORDER_MEDIUM, cellRangeAddress, sheet, workbook);

		// Set borders for the first name column
		cellRangeAddress = new CellRangeAddress(startRow, startRow + numberOfRows, NAME_COLUMNS[FIRST_NAME_COLUMN],
				NAME_COLUMNS[FIRST_NAME_COLUMN]);
		HSSFRegionUtil.setBorderRight(CellStyle.BORDER_MEDIUM, cellRangeAddress, sheet, workbook);

		// Set borders for the second name column
		cellRangeAddress = new CellRangeAddress(startRow, startRow + numberOfRows, LAST_COLUMN, LAST_COLUMN);
		HSSFRegionUtil.setBorderLeft(CellStyle.BORDER_THIN, cellRangeAddress, sheet, workbook);
		HSSFRegionUtil.setBorderRight(CellStyle.BORDER_MEDIUM, cellRangeAddress, sheet, workbook);

		// Set border for the first row of names
		cellRangeAddress = new CellRangeAddress(startRow, startRow, FIRST_COLUMN, LAST_COLUMN);
		HSSFRegionUtil.setBorderTop(CellStyle.BORDER_MEDIUM, cellRangeAddress, sheet, workbook);
		HSSFRegionUtil.setBorderBottom(CellStyle.BORDER_NONE, cellRangeAddress, sheet, workbook);

		// Set border for the last row of names
		cellRangeAddress = new CellRangeAddress(startRow + numberOfRows, startRow + numberOfRows, FIRST_COLUMN,
				LAST_COLUMN);
		HSSFRegionUtil.setBorderBottom(CellStyle.BORDER_MEDIUM, cellRangeAddress, sheet, workbook);
		HSSFRegionUtil.setBorderTop(CellStyle.BORDER_NONE, cellRangeAddress, sheet, workbook);
	}

	/**
	 * Creates a hidden sheet that contains name for players for a given category and a given gender
	 * Will be used as a reference for drop-down lists for the main sheets.
	 *
	 * @param workbook
	 *            workbook object that contains the form file
	 * @param sheetName
	 *            name of the hidden sheet to create
	 * @param listPlayerNames
	 *            list of the player names for a given category and a given gender
	 */
	public void createHiddenSheet(HSSFWorkbook workbook, String sheetName, ArrayList<String> listPlayerNames)
	{
		// Creates and hides a sheet
		HSSFSheet hiddenSheet = workbook.createSheet(sheetName);
		workbook.setSheetHidden(workbook.getSheetIndex(sheetName), HSSFWorkbook.SHEET_STATE_VERY_HIDDEN);
		hiddenSheet.protectSheet("Test");

		Name namedCell = workbook.createName();
		namedCell.setNameName(sheetName);

		// Adds the list of player names in the sheet
		for (int k = 0; k <= listPlayerNames.size(); k++)
		{
			String name = null;

			// Adds the entry "RECHERCHE PARTENAIRE" which is used for dobule teams
			if (k == listPlayerNames.size())
			{
				name = "RECHERCHE PARTENAIRE";
			}
			else
			{
				name = listPlayerNames.get(k);
			}

			HSSFRow row = hiddenSheet.createRow(k);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue(name);
		}

		namedCell.setRefersToFormula(sheetName + "!$A$1:$A$" + (listPlayerNames.size() + 1));
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(sheetName);

		listConstraints.add(constraint);
	}

	/**
	 * Read a list of players from the forms Excel file
	 *
	 * @throws Exception
	 */
	public ArrayList<Registration> read()
	{
		ArrayList<Registration> registrationList = new ArrayList<Registration>();

		try
		{
			Workbook workbook = ExcelFileReader.initialize(file);

			for (int i = 0; i < Category.values().length; i++)
			{
				Sheet sheet = workbook.getSheet(Category.values()[i].text());

				ArrayList<Player> listSinglePlayers = new ArrayList<Player>();
				ArrayList<DoubleTeam> listDoubleTeams = new ArrayList<DoubleTeam>();
				Cell[] row = null;

				String name = new String();
				Category category = Category.values()[i];

				boolean isPlayer = true;
				Section section = Section.SINGLE;
				Gender gender = Gender.MASCULIN;
				Player doublePlayer = null;

				// Iterate over the number of rows in the sheet. Start from the second row as the first row is a header
				for (int j = FIRST_ROW; j < sheet.getRows(); j++)
				{
					row = sheet.getRow(j);

					// Check if the row is empty
					if (row.length > 0)
					{
						// Read specific cells for each row.
						for (int k = 0; k <= NAME_COLUMNS[NAME_COLUMNS.length - 1]; k++)
						{
							// Add the content of the cells to the list of single players and double players
							if (k == NAME_COLUMNS[FIRST_NAME_COLUMN])
							{
								// Category headers
								if (row[NAME_COLUMNS[FIRST_NAME_COLUMN]].getContents() == Section.DOUBLE_MASCULINE
										.text())
								{
									isPlayer = false;

									section = Section.DOUBLE_MASCULINE;
									gender = Gender.MASCULIN;
								}
								else if (row[NAME_COLUMNS[FIRST_NAME_COLUMN]].getContents() == Section.DOUBLE_FEMININE
										.text())
								{
									isPlayer = false;

									section = Section.DOUBLE_FEMININE;
									gender = Gender.FÉMININ;
								}

								// Subsection header
								else if (row[NAME_COLUMNS[FIRST_NAME_COLUMN]].getContents().equals("Nom")
										|| (row[NAME_COLUMNS[FIRST_NAME_COLUMN]].getContents().equals("")))
								{
									isPlayer = false;
								}
								else
								{
									isPlayer = true;

									name = row[k].getContents();

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
								}
							}
							else if (k == NAME_COLUMNS[SECOND_NAME_COLUMN])
							{

								// Check if cell exists in row
								if (k < row.length)
								{
									// Check if cell is for a player, instead of a header
									if (isPlayer == true)
									{
										name = row[k].getContents();

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

			ExcelFileReader.close();
		}
		catch (

		Exception e)
		{
			e.printStackTrace();
		}

		return registrationList;
	}

	/**
	 * Sets the date for the form file
	 *
	 * @param sheet
	 *            sheet for which to add the date
	 */
	public void setCurrentDate(HSSFSheet sheet)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();

		HSSFRow row = sheet.getRow(DATE_ROW);
		HSSFCell cell = row.getCell(DATE_COLUMN);
		cell.setCellValue(DATE_PREFIX + dateFormat.format(date));
	}

	/**
	 * Sets the school name for the form file
	 *
	 * @param sheet
	 *            sheet for which to add the school name
	 */
	public void setSchoolName(HSSFSheet sheet)
	{
		HSSFRow row = sheet.getRow(SCHOOL_NAME_ROW);
		HSSFCell cell = row.getCell(SCHOOL_NAME_COLUMN);
		cell.setCellValue(SCHOOL_NAME_PREFIX + schoolName);
	}

	public void unlockCells(HSSFWorkbook workbook, HSSFSheet sheet, int startRow, int numberOfRows)
	{
		CellStyle unlockedCellStyle = workbook.createCellStyle();
		unlockedCellStyle.setLocked(false);

		for (int i = startRow; i < startRow + numberOfRows; i++)
		{
			Row row = sheet.getRow(i);

			for (int j = 0; j < row.getLastCellNum(); j++)
			{
				org.apache.poi.ss.usermodel.Cell cell = row.getCell(j);

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
		try
		{
			HSSFWorkbook workbook = POIExcelFileProcessor.initialize(inputStream, outputFile);

			// Iterate for each category of play (ex: Benjamin, cadet and juvénile)
			for (int i = 0; i < Category.values().length; i++)
			{
				HSSFSheet sheet = workbook.getSheet(TypeOfResult.values()[i].category().text());
//				sheet.protectSheet("Test");

				ArrayList<ArrayList<String>> listsPlayers = new ArrayList<ArrayList<String>>();

				listsPlayers.add(PostgreSQLJDBC.getPlayersBySchool(schoolName, Category.values()[i],
						Gender.values()[MASCULINE_INDEX]));

				listsPlayers.add(PostgreSQLJDBC.getPlayersBySchool(schoolName, Category.values()[i],
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
					int[] constraintIndexes = new int[NAME_COLUMNS.length];

					CellRangeAddressList[] addressList = new CellRangeAddressList[NAME_COLUMNS.length];

					switch (Section.values()[j])
					{

					case SINGLE:

						numberRows = Math.max(listsPlayers.get(MASCULINE_INDEX).size(),
								listsPlayers.get(FEMININE_INDEX).size());

						constraintIndexes[0] = i * 2;
						constraintIndexes[1] = (i * 2) + 1;

						break;

					case DOUBLE_MASCULINE:

						numberRows = (listsPlayers.get(MASCULINE_INDEX).size() / 2) + 1;

						constraintIndexes[0] = constraintIndexes[1] = i * 2;

						break;

					case DOUBLE_FEMININE:

						numberRows = (listsPlayers.get(FEMININE_INDEX).size() / 2) + 1;
						constraintIndexes[0] = constraintIndexes[1] = (i * 2) + 1;

						break;
					}

					sheet.shiftRows(firstRow, sheet.getLastRowNum(), numberRows);
					addBorder(workbook, sheet, firstRow, numberRows);
//					unlockCells(workbook, sheet, firstRow, numberRows);

					addressList[FIRST_NAME_COLUMN] = new CellRangeAddressList(firstRow, firstRow + numberRows,
							NAME_COLUMNS[FIRST_NAME_COLUMN], NAME_COLUMNS[FIRST_NAME_COLUMN]);
					addressList[SECOND_NAME_COLUMN] = new CellRangeAddressList(firstRow, firstRow + numberRows,
							NAME_COLUMNS[SECOND_NAME_COLUMN], NAME_COLUMNS[SECOND_NAME_COLUMN]);

					for (int k = 0; k < Gender.values().length; k++)
					{
						// Create hidden sheet for each gender only once
						if (Section.values()[j] == Section.SINGLE)
						{
							createHiddenSheet(workbook, Category.values()[i].text() + "_" + Gender.values()[k].text(),
									listsPlayers.get(k));
						}

						HSSFDataValidation validation = new HSSFDataValidation(addressList[k],
								listConstraints.get(constraintIndexes[k]));

						sheet.addValidationData(validation);
					}

					switch (Section.values()[j])
					{
					case SINGLE:
					case DOUBLE_MASCULINE:

						firstRow += numberRows + NUMBER_OF_ROWS_BETWEEN_SECTIONS;

						break;

					case DOUBLE_FEMININE:

						formatContactInformationCells(workbook, sheet, firstRow + numberRows + 2);
						break;
					}
				}
			}

			POIExcelFileProcessor.write();
			POIExcelFileProcessor.close();
		}
		catch (

		Exception e)
		{
			e.printStackTrace();
		}
	}
}
