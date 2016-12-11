package compare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import excelHelper.POIExcelFileProcessor;

public class CompareFile
{
	private File outputFile;

	private Workbook workbookComparisonFile1;
	private Workbook workbookComparisonFile2;
	private Workbook workbookOutputFile;

	/**
	 * Constructor.
	 *
	 * @param comparisonFile1
	 *            first file to be compared
	 * @param comparisonFile2
	 *            second file to be compared
	 * @param outputFile
	 *            file that contains the result of the comparison
	 */
	public CompareFile(File comparisonFile1, File comparisonFile2, File outputFile)
	{
		this.outputFile = outputFile;

		try
		{
			workbookComparisonFile1 = POIExcelFileProcessor.createWorkbook(new FileInputStream(comparisonFile1));
			workbookComparisonFile2 = POIExcelFileProcessor.createWorkbook(new FileInputStream(comparisonFile2));
			workbookOutputFile = POIExcelFileProcessor.createWorkbook(new FileInputStream(outputFile));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Compare each cell in all the sheets
	 *
	 */
	public void compare()
	{
		Sheet sheetComparisonFile1;
		Sheet sheetComparisonFile2;
		Sheet sheetOutputFile;

		CellStyle redStyle = workbookOutputFile.createCellStyle();

		// Iterate over all sheets in the workbook
		for (int i = 0; i < workbookComparisonFile1.getNumberOfSheets(); i++)
		{
			sheetComparisonFile1 = workbookComparisonFile1.getSheetAt(i);
			sheetComparisonFile2 = workbookComparisonFile2.getSheet(sheetComparisonFile1.getSheetName());
			sheetOutputFile = workbookOutputFile.getSheet(sheetComparisonFile1.getSheetName());

			// Check that their is a corresponding sheet in the second compared file
			if (sheetComparisonFile2 != null)
			{
				// Iterate over all the rows in the sheet
				for (int j = 0; j < sheetComparisonFile1.getLastRowNum(); j++)
				{
					Row rowComparisonFile1 = sheetComparisonFile1.getRow(j);
					Row rowComparisonFile2 = sheetComparisonFile2.getRow(j);
					Row rowOutputFile = sheetOutputFile.getRow(j);

					// Check that the rows are not empty and that the number of columns is the same in both
					if ((rowComparisonFile1 != null && rowComparisonFile2 != null)
							&& (rowComparisonFile1.getLastCellNum() == rowComparisonFile2.getLastCellNum()))
					{
						for (int k = 0; k < rowComparisonFile1.getLastCellNum(); k++)
						{
							Cell cellComparisonFile1 = rowComparisonFile1.getCell(k);
							Cell cellComparisonFile2 = rowComparisonFile2.getCell(k);
							Cell cellOutputFile = rowOutputFile.getCell(k);

							// Check that the cells are not empty
							if (cellComparisonFile1 != null && cellComparisonFile2 != null)
							{
								// Assign a green background color if the cells content match
								if (isCellContentEqual(cellComparisonFile1, cellComparisonFile2) == false)
								{
									CellStyle cellOutputFileStyle = cellOutputFile.getCellStyle();
									redStyle.cloneStyleFrom(cellOutputFileStyle);
									redStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
									redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
									cellOutputFile.setCellStyle(redStyle);
								}
							}
						}
					}
				}
			}
		}

		POIExcelFileProcessor.writeWorkbook(workbookOutputFile, outputFile);
		POIExcelFileProcessor.closeWorkbook(workbookComparisonFile1);
		POIExcelFileProcessor.closeWorkbook(workbookComparisonFile2);
	}

	/**
	 * Compare if two cells have the same content
	 *
	 * @param cell1
	 *            first cell to be compared
	 * @param cell2
	 *            second cell to be compared
	 */
	public boolean isCellContentEqual(Cell cell1, Cell cell2)
	{
		return POIExcelFileProcessor.getCellContents(cell1).equals(POIExcelFileProcessor.getCellContents(cell2));
	}
}
