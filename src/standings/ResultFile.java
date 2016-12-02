package standings;

import java.io.File;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import database.PostgreSQLJDBC;
import excelHelper.ExcelFileReader;
import excelHelper.POIExcelFileProcessor;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.TypeOfPlay;

public class ResultFile
{
	private static final int LIST_RESULTS_FIRST_ROW = 1;
	private static final String SHEET_NAME = "Données Joueurs";

	private static final int FIRST_ROW = 1;
	private static final int FIRST_COLUMN = 0;
	private static final int LAST_COLUMN_WRITE = 5;
	private static final int LAST_COLUMN_READ = 10;
	private static final int ID_COLUMN = 0;
	private static final int PLAYER_NAME_COLUMN = 1;
	private static final int SCHOOL_NAME_COLUMN = 2;
	private static final int CATEGORY_COLUMN = 3;
	private static final int GENDER_COLUMN = 4;
	private static final int TYPE_OF_PLAY_COLUMN = 5;
	private static final int FIRST_TOURNAMENT_COLUMN = 6;

	private File inputFile;
	private File outputFile;

	public ResultFile(File inputFile, File outputFile)
	{
		this.inputFile = inputFile;
		this.outputFile = outputFile;
	}

	public ResultFile(File inputFile)
	{
		this.inputFile = inputFile;
		this.outputFile = null;
	}

	/**
	 * Read a list of results from the xls file
	 *
	 * @param inputFile
	 *            xls file to be read
	 * @param sheetName
	 *            name of the sheet that contains the list of results
	 * @return list of results
	 * @throws Exception
	 */
	public ArrayList<Result> read() throws Exception
	{
		Workbook workbook = ExcelFileReader.initialize(inputFile);

		Sheet sheet = workbook.getSheet(SHEET_NAME);
		ArrayList<Result> listResults = new ArrayList<Result>();

		String id = new String();
		String schoolName = new String();
		Category category = null;
		TypeOfPlay typeOfPlay = TypeOfPlay.SIMPLE; // Initializing variable

		Cell[] row = null;

		// Iterate over the number of rows in the sheet.
		for (int i = LIST_RESULTS_FIRST_ROW; i < sheet.getRows(); i++)
		{
			ArrayList<Integer> scores = new ArrayList<Integer>();
			row = sheet.getRow(i);

			if (row.length > 0)
			{
				// Read alls cells for each row. Stopping at the 10th column since there are only empty fields after
				for (int j = 0; j <= LAST_COLUMN_READ; j++)
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
					case ID_COLUMN:
						id = row[j].getContents();
						break;
					case SCHOOL_NAME_COLUMN:
						schoolName = row[j].getContents();
						break;
					case CATEGORY_COLUMN:
						category = Category.valueOf(row[j].getContents().toUpperCase());
						break;
					case TYPE_OF_PLAY_COLUMN:
						typeOfPlay = TypeOfPlay.valueOf(row[j].getContents().toUpperCase());
						break;
					case FIRST_TOURNAMENT_COLUMN:
					case FIRST_TOURNAMENT_COLUMN + 1:
					case FIRST_TOURNAMENT_COLUMN + 2:
					case FIRST_TOURNAMENT_COLUMN + 3:
					case FIRST_TOURNAMENT_COLUMN + 4:

						// JXL seems to change the number of cells in a row if subsequent values are empty
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
				listResults.add(new Result(id, schoolName, category, typeOfPlay, scores));
			}
		}

		ExcelFileReader.close();

		return listResults;
	}

	public void write()
	{
		ArrayList<Player> listPlayers = PostgreSQLJDBC.getAllPlayers();

		try
		{
			HSSFWorkbook workbook = POIExcelFileProcessor.initialize(inputFile, outputFile);
			HSSFSheet sheet = workbook.getSheet(SHEET_NAME);

			for (int k = 0; k < 2; k++)
			{
				for (int i = 0; i < listPlayers.size(); i++)
				{
					HSSFRow row = sheet.createRow((short) (i * 2) + k + FIRST_ROW);

					for (int j = FIRST_COLUMN; j <= LAST_COLUMN_WRITE; j++)
					{
						HSSFCell cell = row.createCell((short) j);

						switch (j)
						{
						case ID_COLUMN:
							cell.setCellValue(listPlayers.get(i).getId());
							break;
						case PLAYER_NAME_COLUMN:
							cell.setCellValue(listPlayers.get(i).getName());
							break;
						case SCHOOL_NAME_COLUMN:
							cell.setCellValue(listPlayers.get(i).getSchoolName());
							break;
						case CATEGORY_COLUMN:
							cell.setCellValue(listPlayers.get(i).getCategory().text());
							break;
						case GENDER_COLUMN:
							cell.setCellValue(listPlayers.get(i).getGender().text());
							break;
						case TYPE_OF_PLAY_COLUMN:
							if (k == 0)
							{
								cell.setCellValue(TypeOfPlay.SIMPLE.text());
							}
							else if (k == 1)
							{
								cell.setCellValue(TypeOfPlay.DOUBLE.text());
							}
							break;
						}
					}
				}
			}

			POIExcelFileProcessor.write();
			POIExcelFileProcessor.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
