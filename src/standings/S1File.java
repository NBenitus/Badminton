package standings;

import java.io.File;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import excelHelper.POIExcelFileProcessor;
import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.Gender;

public class S1File
{
	private static final int FIRST_ROW = 2;

	public enum Column {
		CATEGORY(1), SCHOOL_NAME(6), ID(7), LAST_NAME(8), FIRST_NAME(9), GENDER(11);

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

	private static final String SHEET_NAME = "Athlètes";
	private File xlsFile;

	public S1File(File file)
	{
		xlsFile = file;
	}

	/**
	 * Read a list of players from the S1 Excel file
	 *
	 * @return list of players
	 * @throws Exception
	 */
	public ArrayList<Player> read()
	{
		Workbook workbook = POIExcelFileProcessor.createWorkbook(xlsFile);

		Sheet sheet = workbook.getSheet(SHEET_NAME);

		ArrayList<Player> listPlayers = new ArrayList<Player>();
		Row row = null;

		String firstName = new String();
		String lastName = new String();
		Gender gender = null;
		Category category = null;
		String schoolName = null;
		String id = null;

		// Iterate over the number of rows in the sheet. Start from the second row as the first row is a header
		for (int i = FIRST_ROW; i <= sheet.getLastRowNum(); i++)
		{
			row = sheet.getRow(i);

			if (row != null)
			{
				// Read specific cells for each row. Stopping at the 4th column since other fields are not necessary
				for (int j = 0; j <= Column.values().length; j++)
				{
					switch (Column.values()[j])
					{
					case CATEGORY:
						category = Category.valueOf(POIExcelFileProcessor.getCellContents(row.getCell(j)).toUpperCase());
						break;
					case SCHOOL_NAME:
						schoolName = POIExcelFileProcessor.getCellContents(row.getCell(j));
						break;
					case ID:
						id = POIExcelFileProcessor.getCellContents(row.getCell(j));
						break;
					case LAST_NAME:
						lastName = POIExcelFileProcessor.getCellContents(row.getCell(j));
						break;
					case FIRST_NAME:
						firstName = POIExcelFileProcessor.getCellContents(row.getCell(j));
						break;
					case GENDER:
						if (POIExcelFileProcessor.getCellContents(row.getCell(j)).equals("M"))
						{
							gender = Gender.MASCULIN;
						}
						else if (POIExcelFileProcessor.getCellContents(row.getCell(j)).equals("F"))
						{
							gender = Gender.FÉMININ;
						}
						break;
					}
				}
			}

			listPlayers.add(new Player(id, firstName + " " + lastName, schoolName, gender, category));
		}

		POIExcelFileProcessor.closeWorkbook(workbook);

		return listPlayers;
	}
}
