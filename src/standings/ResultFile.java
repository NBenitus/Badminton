package standings;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import database.PostgreSQLJDBC;
import excelHelper.POIExcelFileProcessor;
import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.TypeOfPlay;

public class ResultFile
{
	private static final int LIST_RESULTS_FIRST_ROW = 1;
	private static final String SHEET_NAME = "Données Joueurs";

	private static final int FIRST_ROW = 1;

	public enum Column {
		ID(0), PLAYER_NAME(1), SCHOOL_NAME(2), CATEGORY(3), GENDER(4), TYPE_OF_PLAY(5), FIRST_TOURNAMENT(6),
		SECOND_TOURNAMENT(7), THIRD_TOURNAMENT(8), FOURTH_TOURNAMENT(9), FIFTH_TOURNAMENT(10);

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

	private File inputFile;
	private File outputFile;

	private InputStream inputStream;

	/**
	 * Constructor.
	 *
	 * @param inputFile
	 *            file that contains the results
	 * @param outputFile
	 *            file that will contain the pre-filled results file without scores
	 */
	public ResultFile(InputStream inputStream, File outputFile)
	{
		this.inputStream = inputStream;
		this.outputFile = outputFile;
	}

	/**
	 * Constructor.
	 *
	 * @param inputFile
	 *            file that contains the results
	 */
	public ResultFile(File inputFile)
	{
		this.inputFile = inputFile;
	}

	/**
	 * Adds results to the database
	 *
	 * @throws Exception
	 */
	public void addResult()
	{
		PostgreSQLJDBC.clearTable("Result");
		PostgreSQLJDBC.addResult(read());
	}

	/**
	 * Read the contents of the results file
	 */
	public ArrayList<Result> read()
	{
		Workbook workbook = POIExcelFileProcessor.createWorkbook(inputFile);

		Sheet sheet = workbook.getSheet(SHEET_NAME);
		ArrayList<Result> listResults = new ArrayList<Result>();

		String id = new String();
		String schoolName = new String();
		Category category = null;
		TypeOfPlay typeOfPlay = TypeOfPlay.SIMPLE; // Initializing variable

		Row row = null;

		// Iterate over the number of rows in the sheet.
		for (int i = LIST_RESULTS_FIRST_ROW; i <= sheet.getLastRowNum(); i++)
		{
			ArrayList<Integer> scores = new ArrayList<Integer>();
			row = sheet.getRow(i);

			if (row != null)
			{
				// Read alls cells for each row. Stopping at the 10th column since there are only empty fields after
				for (int j = 0; j < Column.values().length; j++)
				{
					switch (Column.values()[j])
					{

					// Add the corresponding values to the result object
					case ID:

						id = POIExcelFileProcessor.getCellContents(row.getCell(j));

						break;

					case PLAYER_NAME:

						// Do nothing
						break;

					case GENDER:

						// Do nothing
						break;

					case SCHOOL_NAME:

						schoolName = POIExcelFileProcessor.getCellContents(row.getCell(j));

						break;

					case CATEGORY:

						category = Category
								.valueOf(POIExcelFileProcessor.getCellContents(row.getCell(j)).toUpperCase());

						break;

					case TYPE_OF_PLAY:

						typeOfPlay = TypeOfPlay
								.valueOf(POIExcelFileProcessor.getCellContents(row.getCell(j)).toUpperCase());

						break;

					case FIRST_TOURNAMENT:
					case SECOND_TOURNAMENT:
					case THIRD_TOURNAMENT:
					case FOURTH_TOURNAMENT:
					case FIFTH_TOURNAMENT:

						if (POIExcelFileProcessor.getCellContents(row.getCell(j)).equals(""))
						{
							scores.add(0);
						}
						else
						{
							scores.add(Integer.parseInt(POIExcelFileProcessor.getCellContents(row.getCell(j))));
						}

						break;
					}
				}

			}

			listResults.add(new Result(id, schoolName, category, typeOfPlay, scores));
		}

		POIExcelFileProcessor.closeWorkbook(workbook);

		return listResults;
	}

	/**
	 * Writes the pre-filled columns to the results file
	 */
	public void write()
	{
		ArrayList<Player> listPlayers = PostgreSQLJDBC.getAllPlayers();

		Workbook workbook = POIExcelFileProcessor.createWorkbook(inputStream);

		Sheet sheet = workbook.getSheet(SHEET_NAME);

		// Freeze the header rows for scrolling purposes
		for (int i = 0; i <= Column.FIRST_TOURNAMENT.number(); i++)
		{
			sheet.createFreezePane(i, 0);
		}

		// Iterate over both types of play
		for (int k = 0; k < TypeOfPlay.values().length; k++)
		{
			for (int i = 0; i < listPlayers.size(); i++)
			{
				Row row = sheet.createRow((short) (i * 2) + k + FIRST_ROW);

				// Iterate over each column where cells will be written
				for (int j = 0; j < Column.values().length; j++)
				{
					Cell cell = row.createCell((short) j);

					switch (Column.values()[j])
					{

					case ID:

						cell.setCellValue(listPlayers.get(i).getId());
						break;

					case PLAYER_NAME:

						cell.setCellValue(listPlayers.get(i).getName());
						break;

					case SCHOOL_NAME:

						cell.setCellValue(listPlayers.get(i).getSchoolName());
						break;

					case CATEGORY:

						cell.setCellValue(listPlayers.get(i).getCategory().text());
						break;

					case GENDER:

						cell.setCellValue(listPlayers.get(i).getGender().text());
						break;

					case TYPE_OF_PLAY:

						cell.setCellValue(TypeOfPlay.values()[k].text());
						break;

					case FIRST_TOURNAMENT:
					case SECOND_TOURNAMENT:
					case THIRD_TOURNAMENT:
					case FOURTH_TOURNAMENT:
					case FIFTH_TOURNAMENT:

						// Do nothing
						break;
					}
				}
			}
		}

		POIExcelFileProcessor.writeWorkbook(workbook, outputFile);
	}
}
