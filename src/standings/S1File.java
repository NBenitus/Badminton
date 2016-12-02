package standings;

import java.io.File;
import java.util.ArrayList;

import excelHelper.ExcelFileReader;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.Gender;

public class S1File
{
	private static final int LIST_PLAYERS_FIRST_ROW = 2;
	private static final int LIST_PLAYERS_FIRST_COLUMN = 1;
	private static final int LIST_PLAYERS_LAST_COLUMN = 11;
	private static final int LIST_PLAYERS_CATEGORY = 1;
	private static final int LIST_PLAYERS_SCHOOL_NAME = 6;
	private static final int LIST_PLAYERS_ID = 7;
	private static final int LIST_PLAYERS_LAST_NAME = 8;
	private static final int LIST_PLAYERS_FIRST_NAME = 9;
	private static final int LIST_PLAYERS_GENDER = 11;

	private static final String SHEET_NAME = "Athlètes";
	private File xlsFile;

	public S1File(File file)
	{
		xlsFile = file;
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
	public ArrayList<Player> read() throws Exception
	{
		Workbook workbook = ExcelFileReader.initialize(xlsFile);

		Sheet sheet = workbook.getSheet(SHEET_NAME);

		ArrayList<Player> listPlayers = new ArrayList<Player>();
		Cell[] row = null;

		String firstName = new String();
		String lastName = new String();
		Gender gender = null;
		Category category = null;
		String schoolName = null;
		String id = null;

		// Iterate over the number of rows in the sheet. Start from the second row as the first row is a header
		for (int i = LIST_PLAYERS_FIRST_ROW; i < sheet.getRows(); i++)
		{
			row = sheet.getRow(i);

			if (row.length > 0)
			{
				// If the line number of the row is empty, skip to the end of the file
				if (row[LIST_PLAYERS_FIRST_COLUMN].getContents().isEmpty())
				{
					i = sheet.getRows();
					break;
				}

				// Read specific cells for each row. Stopping at the 4th column since other fields are not necessary
				for (int j = LIST_PLAYERS_FIRST_COLUMN; j <= LIST_PLAYERS_LAST_COLUMN; j++)
				{
					switch (j)
					{
					case LIST_PLAYERS_CATEGORY:
						category = Category.valueOf(row[j].getContents().toUpperCase());
						break;
					case LIST_PLAYERS_SCHOOL_NAME:
						schoolName = row[j].getContents();
						break;
					case LIST_PLAYERS_ID:
						id = row[j].getContents();
						break;
					case LIST_PLAYERS_LAST_NAME:
						lastName = row[j].getContents();
						break;
					case LIST_PLAYERS_FIRST_NAME:
						firstName = row[j].getContents();
						break;
					case LIST_PLAYERS_GENDER:
						if (row[j].getContents().equals("M"))
						{
							gender = Gender.MASCULIN;
						}
						else if (row[j].getContents().equals("F"))
						{
							gender = Gender.FÉMININ;
						}
						break;
					}
				}
			}

			// Add the player if the file is not at the last empty row
			if (i != sheet.getRows())
			{
				listPlayers.add(new Player(id, firstName + " " + lastName, schoolName, gender, category));
			}
		}

		ExcelFileReader.close();

		return listPlayers;
	}

}
