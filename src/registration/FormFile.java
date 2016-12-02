package registration;

import java.io.File;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.util.CellRangeAddressList;

import database.PostgreSQLJDBC;
import excelHelper.ExcelFileReader;
import excelHelper.POIExcelFileProcessor;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import standings.Player;
import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.Gender;
import utilities.Utilities;

public class FormFile
{
	private static final int LIST_PLAYERS_MASCULINE_FIRST_COLUMN = 1;
	private static final int LIST_PLAYERS_FEMININE_FIRST_COLUMN = 4;
	private static final int LIST_PLAYERS_FIRST_ROW = 8;
	private static final int SCHOOL_NAME_COLUMN = 1;
	private static final int SCHOOL_NAME_ROW = 4;

	/**
	 * Read a list of players from the forms Excel file
	 *
	 * @param xlsFile
	 *            xls file to be read
	 * @param sheetName
	 *            name of the sheet that contains the list of players
	 * @return registration object that contains the list of single and double players for a school and a category
	 * @throws Exception
	 */
	public static Registration read(File xlsFile, String sheetName) throws Exception
	{
		Workbook workbook = ExcelFileReader.initialize(xlsFile);

		Sheet sheet = workbook.getSheet(sheetName);

		ArrayList<Player> listSinglePlayers = new ArrayList<Player>();
		ArrayList<DoubleTeam> listDoubleTeams = new ArrayList<DoubleTeam>();
		Cell[] row = null;

		String firstName = new String();
		String lastName = new String();
		String schoolName = Utilities
				.getSchoolNameFromForm(sheet.getRow(SCHOOL_NAME_ROW)[SCHOOL_NAME_COLUMN].getContents());
		Category category = Category.valueOf(sheetName.toUpperCase());

		boolean isPlayer = true;
		boolean isSingle = true;
		Gender gender = Gender.MASCULIN;
		Player doublePlayer = null;

		// Iterate over the number of rows in the sheet. Start from the second row as the first row is a header
		for (int i = LIST_PLAYERS_FIRST_ROW; i < sheet.getRows(); i++)
		{
			row = sheet.getRow(i);

			// Check if the row is empty
			if (row.length > 0)
			{
				// Read specific cells for each row.
				for (int j = 0; j <= LIST_PLAYERS_FEMININE_FIRST_COLUMN + 1; j++)
				{
					// Add the content of the cells to the list of single players and double players
					switch (j)
					{
					case LIST_PLAYERS_MASCULINE_FIRST_COLUMN:

						// Category headers
						if (row[LIST_PLAYERS_MASCULINE_FIRST_COLUMN].getContents().equals("DOUBLE MASCULIN"))
						{
							isSingle = false;
							isPlayer = false;
							gender = Gender.MASCULIN;
						}
						else if (row[LIST_PLAYERS_MASCULINE_FIRST_COLUMN].getContents().equals("DOUBLE FÉMININ"))
						{
							isPlayer = false;
							gender = Gender.FÉMININ;
						}
						else if (row[LIST_PLAYERS_MASCULINE_FIRST_COLUMN].getContents().equals("DOUBLE MIXTE"))
						{
							isPlayer = false;
							i = sheet.getRows();
						}

						// Subsection header
						else if (row[LIST_PLAYERS_MASCULINE_FIRST_COLUMN].getContents().equals("Nom")
								|| (row[LIST_PLAYERS_MASCULINE_FIRST_COLUMN].getContents().equals("")))
						{
							isPlayer = false;
						}
						else
						{
							isPlayer = true;
							lastName = row[j].getContents();
						}

						break;

					case LIST_PLAYERS_MASCULINE_FIRST_COLUMN + 1:

						// Check if cell is for a player, instead of a header
						if (isPlayer == true)
						{
							firstName = row[j].getContents();

							// Add player to list of single players
							if (isSingle == true)
							{
								listSinglePlayers
										.add(new Player(firstName + " " + lastName, schoolName, gender, category));
							}

							// Add player to list of double players
							else
							{
								doublePlayer = new Player(firstName + " " + lastName, schoolName, gender, category);
							}
						}
						break;

					case LIST_PLAYERS_FEMININE_FIRST_COLUMN:

						// Check if cell exists in row
						if (j < row.length)
						{
							// Check if cell is for a player, instead of a header
							if (isPlayer == true)
							{
								lastName = row[j].getContents();
							}
						}

						break;

					case LIST_PLAYERS_FEMININE_FIRST_COLUMN + 1:

						// Check if cell exists in row
						if (j < row.length)
						{
							// Check if cell is for a player, instead of a header
							if (isPlayer == true)
							{
								if (!lastName.equals(""))
								{
									firstName = row[j].getContents();

									// Add player to list of single players
									if (isSingle == true)
									{
										listSinglePlayers.add(
												new Player(firstName + " " + lastName, schoolName, gender, category));
									}
									else
									{
										// Both players are specified for a double team
										// Add players to list of double players
										if (!lastName.equals("Partenaire"))
										{
											listDoubleTeams.add(new DoubleTeam(doublePlayer, new Player(
													firstName + " " + lastName, schoolName, gender, category)));
										}
										// A player needs a partner for a double team
										else
										{
											listDoubleTeams.add(new DoubleTeam(doublePlayer,
													new Player("Partenaire", schoolName, gender, category)));
										}
									}
								}
							}
						}
						break;
					}
				}
			}
		}

		ExcelFileReader.close();

		return new Registration(listSinglePlayers, listDoubleTeams);
	}


	public static void write(File inputFile, File outputFile, String sheetName)
	{
		try
		{
			String[] myStringArray = {"a","b","c"};
			ArrayList<Player> listPlayers = PostgreSQLJDBC.getAllPlayers();

			HSSFWorkbook workbook = POIExcelFileProcessor.initialize(inputFile, outputFile);

			HSSFSheet sheet = workbook.getSheet(sheetName);

			DVConstraint constraint = DVConstraint.createExplicitListConstraint(myStringArray);
			CellRangeAddressList addressList = new CellRangeAddressList(1, 10, 1, 10);

			HSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);
			validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
			validation.setSuppressDropDownArrow(false);

			sheet.addValidationData(validation);

			POIExcelFileProcessor.write();
			POIExcelFileProcessor.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
