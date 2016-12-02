package compare;

import java.io.File;
import java.util.ArrayList;

import excelHelper.ExcelFileReader;
import excelHelper.ExcelFileProcessor;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableWorkbook;

public class CompareFile
{
	private static final int COMPARE_FILES_FIRST_ROW = 10;

	// In progress
	public CompareFile(File excelFileOne, File excelFileTwo, File compareResultFile) throws Exception
	{
		Workbook workbook = Workbook.getWorkbook(excelFileOne);
		WritableWorkbook writableWorkbook = Workbook.createWorkbook(compareResultFile, workbook);

		ArrayList<Cell[]> firstExcelFileListCells = read(excelFileOne, "Classement_Individuel_Combiné");
		ArrayList<Cell[]> secondExcelFileListCells = read(excelFileTwo, "Classement_Individuel_Combiné");

		ExcelFileProcessor.compareSheet(writableWorkbook, firstExcelFileListCells, secondExcelFileListCells,
				"Classement_Individuel_Combiné");

		ExcelFileProcessor.writeAndClose(writableWorkbook, workbook);
	}

	/**
	 * Read a list of rows (array of cells) from the xls file
	 *
	 * @param xlsFile
	 *            xls file to be read
	 * @param sheetName
	 *            name of the sheet that contains the list of results
	 * @return list of rows (array of cells)
	 * @throws Exception
	 */
	public static ArrayList<Cell[]> read(File xlsFile, String sheetName) throws Exception
	{
		Workbook workbook = ExcelFileReader.initialize(xlsFile);

		Sheet sheet = workbook.getSheet(sheetName);
		ArrayList<Cell[]> listCells = new ArrayList<Cell[]>();

		// Iterate over the number of rows in the sheet. Start from the second row as the first row is a header
		for (int i = COMPARE_FILES_FIRST_ROW; i < sheet.getRows(); i++)
		{
			listCells.add(sheet.getRow(i));
		}

		ExcelFileReader.close();

		return listCells;
	}
}
