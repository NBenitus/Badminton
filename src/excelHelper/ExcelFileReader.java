package excelHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import standings.DoubleTeam;
import standings.Registration;
import standings.Player;
import standings.Result;
import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.Gender;
import standings.StandingsCreationHelper.TypeOfPlay;
import utilities.Utilities;

public class ExcelFileReader
{
	private static Workbook workbook;

	private static final int LISTPLAYERSFORMMASCULINEFIRSTCOLUMN = 1;
	private static final int LISTPLAYERSFORMFEMININEFIRSTCOLUMN = 4;
	private static final int LISTPLAYERSFORMFIRSTROW = 8;
	private static final int SCHOOLNAMEFORMCOLUMN = 1;
	private static final int SCHOOLNAMEFORMROW = 4;

	private static final int LISTPLAYERSFIRSTROW = 1;
	private static final int LISTRESULTSFIRSTROW = 1;
	private static final int COMPAREFILESFIRSTROW = 10;
	private static final int MAXIUMCOLUMNSLISTPLAYERSFILE = 6;

	/**
	 * Closes the objects used to read the XLS file
	 *
	 * @throws Exception
	 */
	public static void close() throws Exception
	{
		workbook.close();
	}

	/**
	 * Initialize the objects to read the XLS file
	 *
	 * @param xlsFile
	 *            xls file to be read
	 * @throws Exception
	 */
	public static void initialize(File xlsFile) throws Exception
	{
		try
		{
			// Excel document to be imported
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setLocale(new Locale("en", "EN"));
			workbook = Workbook.getWorkbook(xlsFile, workbookSettings);
		}
		catch (Exception e)
		{
			System.err.println(e.toString());

			throw e;
		}
	}

	/**
	 * Read a list of players from the results Excel file
	 *
	 * @param xlsFile
	 *            xls file to be read
	 * @param sheetName
	 *            name of the sheet that contains the list of players
	 * @return list of players
	 * @throws Exception
	 */
	public static Registration readListPlayersFromForms(File xlsFile, String sheetName) throws Exception
	{
		initialize(xlsFile);

		Sheet sheet = workbook.getSheet(sheetName);

		ArrayList<Player> singlePlayers = new ArrayList<Player>();
		ArrayList<DoubleTeam> doubleTeams = new ArrayList<DoubleTeam>();
		Cell[] row = null;

		String firstName = new String();
		String lastName = new String();
		String schoolName = Utilities
				.getSchoolNameFromForm(sheet.getRow(SCHOOLNAMEFORMROW)[SCHOOLNAMEFORMCOLUMN].getContents());
		Category category = Category.valueOf(sheetName.toUpperCase());
		boolean isPlayer = true;
		boolean isSingle = true;
		Gender gender = Gender.MASCULIN;
		Player doublePlayer = null;

		// Iterate over the number of rows in the sheet. Start from the second row as the first row is a header
		for (int i = LISTPLAYERSFORMFIRSTROW; i < sheet.getRows(); i++)
		{
			row = sheet.getRow(i);

			if (row.length > 0)
			{
				// Read specific cells for each row. Stopping at the 4th column since other fields are not necessary
				for (int j = 0; j <= LISTPLAYERSFORMFEMININEFIRSTCOLUMN + 1; j++)
				{
					// Add the following cells 0: player name, 1: school name, 3: gender. Skipping 2: category since
					// players can participate in multiple categories
					switch (j)
					{
					case LISTPLAYERSFORMMASCULINEFIRSTCOLUMN:

						if (row[LISTPLAYERSFORMMASCULINEFIRSTCOLUMN].getContents().equals("DOUBLE MASCULIN"))
						{
							isSingle = false;
							isPlayer = false;
							gender = Gender.MASCULIN;
						}
						else if (row[LISTPLAYERSFORMMASCULINEFIRSTCOLUMN].getContents().equals("DOUBLE FÉMININ"))
						{
							isPlayer = false;
							gender = Gender.FÉMININ;
						}
						else if (row[LISTPLAYERSFORMMASCULINEFIRSTCOLUMN].getContents().equals("DOUBLE MIXTE"))
						{
							isPlayer = false;
							i = sheet.getRows();
						}
						else if (row[LISTPLAYERSFORMMASCULINEFIRSTCOLUMN].getContents().equals("Nom")
								|| (row[LISTPLAYERSFORMMASCULINEFIRSTCOLUMN].getContents().equals("")))
						{
							isPlayer = false;
						}
						else
						{
							lastName = row[j].getContents();
						}

						break;

					case LISTPLAYERSFORMMASCULINEFIRSTCOLUMN + 1:

						if (isPlayer == true)
						{
							firstName = row[j].getContents();

							if (isSingle == true)
							{
								singlePlayers.add(new Player(firstName + " " + lastName, schoolName, gender, category));
							}
							else
							{
								doublePlayer = new Player(firstName + " " + lastName, schoolName, gender, category);
							}
						}
						break;

					case LISTPLAYERSFORMFEMININEFIRSTCOLUMN:
						if (j < row.length)
						{
							if (isPlayer == true)
							{
								lastName = row[j].getContents();
							}
						}

						break;

					case LISTPLAYERSFORMFEMININEFIRSTCOLUMN + 1:

						if (j < row.length)
						{
							if (isPlayer == true)
							{
								if (!lastName.equals(""))
								{
									firstName = row[j].getContents();

									if (isSingle == true)
									{
										singlePlayers
												.add(new Player(firstName + " " + lastName, schoolName, gender, category));
									}
									else
									{
										if (!lastName.equals("Partenaire"))
										{
											doubleTeams.add(new DoubleTeam(doublePlayer,
													new Player(firstName + " " + lastName, schoolName, gender, category)));
										}
										else
										{
											doubleTeams.add(new DoubleTeam(doublePlayer,
													new Player("Partenaire", schoolName, gender, category)));
										}
									}
								}
							}
							else
							{
								isPlayer = true;
							}
						}
						break;
					}
				}
			}
		}

		close();

		return new Registration(singlePlayers, doubleTeams);
	}

	/**
	 * Read a list of players from the results Excel file
	 *
	 * @param xlsFile
	 *            xls file to be read
	 * @param sheetName
	 *            name of the sheet that contains the list of players
	 * @return list of players
	 * @throws Exception
	 */
	public static ArrayList<Player> readListPlayersFromResultsFile(File xlsFile, String sheetName) throws Exception
	{
		initialize(xlsFile);

		Sheet sheet = workbook.getSheet(sheetName);

		ArrayList<Player> listPlayers = new ArrayList<Player>();
		Cell[] row = null;

		String name = new String();
		String schoolName = new String();
		Gender gender = null;

		// Iterate over the number of rows in the sheet. Start from the second row as the first row is a header
		for (int i = LISTPLAYERSFIRSTROW; i < sheet.getRows(); i++)
		{
			row = sheet.getRow(i);

			if (row.length > 0)
			{
				// Read specific cells for each row. Stopping at the 4th column since other fields are not necessary
				for (int j = 0; j <= 3; j++)
				{
					// If the line number of the row is empty, skip to the end of the file
					if (row[j].getContents().isEmpty())
					{
						i = sheet.getRows();
						break;
					}

					// Add the following cells 0: player name, 1: school name, 3: gender. Skipping 2: category since
					// players can participate in multiple categories
					switch (j)
					{
					case 0:
						name = row[j].getContents();
						break;
					case 1:
						schoolName = row[j].getContents();
						break;
					case 2:
						// Do nothing
						break;
					case 3:
						gender = Gender.valueOf(row[j].getContents().toUpperCase());
						break;
					}
				}
			}

			// Add the player if the file is not at the last empty row
			if (i != sheet.getRows())
			{
				listPlayers.add(new Player(name, schoolName, gender));
			}
		}

		close();

		return listPlayers;
	}

	/**
	 * Read a list of players from the S1 Excel file
	 *
	 * @param xlsFile
	 *            xls file to be read
	 * @param sheetName
	 *            name of the sheet that contains the list of players
	 * @return list of players
	 * @throws Exception
	 */
	public static ArrayList<Player> readListPlayersFromS1File(File xlsFile, String sheetName) throws Exception
	{
		initialize(xlsFile);

		Sheet sheet = workbook.getSheet(sheetName);

		ArrayList<Player> listPlayers = new ArrayList<Player>();
		Cell[] row = null;

		String firstName = new String();
		String lastName = new String();
		Gender gender = null;
		Category category = null;
		String schoolName = Utilities.getSchoolNameFromS1File(xlsFile);

		// Iterate over the number of rows in the sheet. Start from the second row as the first row is a header
		for (int i = LISTPLAYERSFIRSTROW; i < sheet.getRows(); i++)
		{
			row = sheet.getRow(i);

			if (row.length > 0)
			{
				// Read specific cells for each row. Stopping at the 4th column since other fields are not necessary
				for (int j = 0; j < MAXIUMCOLUMNSLISTPLAYERSFILE; j++)
				{
					// If the line number of the row is empty, skip to the end of the file
					if (row[j].getContents().isEmpty())
					{
						i = sheet.getRows();
						break;
					}

					// Add the following cells 0: player name, 1: school name, 3: gender. Skipping 2: category since
					// players can participate in multiple categories
					switch (j)
					{
					case 0:
						lastName = row[j].getContents();
						break;
					case 1:
						firstName = row[j].getContents();
						break;
					case 2:
						Gender.valueOf(row[j].getContents());
						break;
					case 3:
					case 4:
						// Do nothing
						break;
					case 5:
						category = Utilities.getCategoryFromDateOfBirth(row[j].getContents());
						break;
					}
				}
			}

			// Add the player if the file is not at the last empty row
			if (i != sheet.getRows())
			{
				listPlayers.add(new Player(firstName + " " + lastName, schoolName, gender, category));
			}
		}

		close();

		return listPlayers;
	}

	/**
	 * Read a list of results from the xls file
	 *
	 * @param xlsFile
	 *            xls file to be read
	 * @param sheetName
	 *            name of the sheet that contains the list of results
	 * @return list of results
	 * @throws Exception
	 */
	public static ArrayList<Result> readResults(File xlsFile, String sheetName) throws Exception
	{
		initialize(xlsFile);

		Sheet sheet = workbook.getSheet(sheetName);
		ArrayList<Result> listResults = new ArrayList<Result>();

		String name = new String();
		String schoolName = new String();
		String category = new String();
		TypeOfPlay typeOfPlay = TypeOfPlay.SIMPLE; // Initializing variable

		Cell[] row = null;

		// Iterate over the number of rows in the sheet.
		for (int i = LISTRESULTSFIRSTROW; i < sheet.getRows(); i++)
		{
			ArrayList<Integer> scores = new ArrayList<Integer>();
			row = sheet.getRow(i);

			if (row.length > 0)
			{
				// Read alls cells for each row. Stopping at the 10th column since there are only empty fields after
				for (int j = 0; j <= 9; j++)
				{
					// If the line number of the row is empty, skip to the end of the file
					if (row[0].getContents().isEmpty())
					{
						i = sheet.getRows();
						break;
					}

					switch (j)
					{

					// Add the corresponding values to the result object
					case 0:
						name = row[j].getContents();
						break;
					case 1:
						schoolName = row[j].getContents();
						break;
					case 2:
						category = row[j].getContents();
						break;
					case 3:
						// Do nothing
						break;
					case 4:
//						System.out.println(row[j].getContents())
						typeOfPlay = TypeOfPlay.valueOf(row[j].getContents().toUpperCase());
						break;
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:

						// JXL seems to change the number of cells in a row if subsquent values are empty
						if (j >= row.length)
						{
							scores.add(0);
						}
						else
						{
							if ((row[j].getContents().equals("")))
							{
								scores.add(0);
							}
							else
							{
								scores.add(Integer.parseInt(row[j].getContents()));
							}
						}
						break;
					}
				}

			}

			// Add the result if the file is not at the last empty row
			if (i != sheet.getRows())
			{
				listResults.add(new Result(name, schoolName, category, typeOfPlay, scores));
			}
		}

		close();

		return listResults;
	}

	/**
	 * Read a list of results from the xls file
	 *
	 * @param xlsFile
	 *            xls file to be read
	 * @param sheetName
	 *            name of the sheet that contains the list of results
	 * @return list of results
	 * @throws Exception
	 */
	public static ArrayList<Cell[]> readRows(File xlsFile, String sheetName) throws Exception
	{
		initialize(xlsFile);

		Sheet sheet = workbook.getSheet(sheetName);
		ArrayList<Cell[]> listCells = new ArrayList<Cell[]>();

		// Iterate over the number of rows in the sheet. Start from the second row as the first row is a header
		for (int i = COMPAREFILESFIRSTROW; i < sheet.getRows(); i++)
		{
			listCells.add(sheet.getRow(i));
		}

		close();

		return listCells;
	}
}
