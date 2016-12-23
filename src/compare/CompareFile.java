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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import excelHelper.POIExcelFileProcessor;

public class CompareFile
{
	private File outputFile;

	private Workbook workbookComparisonFile1;
	private Workbook workbookComparisonFile2;
	private Workbook workbookOutputFile;

	private CellStyle redStyle;

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
			workbookOutputFile = POIExcelFileProcessor.createWorkbook(new FileInputStream(comparisonFile1));
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
	public boolean compare()
	{
		boolean isEqual = true;

		Sheet sheetComparisonFile1;
		Sheet sheetComparisonFile2;
		Sheet sheetOutputFile;

		redStyle = workbookOutputFile.createCellStyle();
		redStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		// Iterate over all sheets in the workbook
		for (int i = 0; i < workbookComparisonFile1.getNumberOfSheets(); i++)
		{
			sheetComparisonFile1 = workbookComparisonFile1.getSheetAt(i);
			sheetComparisonFile2 = workbookComparisonFile2.getSheet(sheetComparisonFile1.getSheetName());
			sheetOutputFile = workbookOutputFile.getSheet(sheetComparisonFile1.getSheetName());

			if (compareSheet(sheetComparisonFile1, sheetComparisonFile2, sheetOutputFile) == false)
			{
				isEqual = false;
			}
		}

		POIExcelFileProcessor.writeWorkbook(workbookOutputFile, outputFile);
		POIExcelFileProcessor.closeWorkbook(workbookComparisonFile1);
		POIExcelFileProcessor.closeWorkbook(workbookComparisonFile2);
		POIExcelFileProcessor.closeWorkbook(workbookOutputFile);

		return isEqual;
	}

	/**
	 * Compare if two cells have the same content
	 *
	 * @param cell1
	 *            first cell to be compared
	 * @param cell2
	 *            second cell to be compared
	 */
	public boolean compareCell(Cell cellComparisonFile1, Cell cellComparisonFile2, Cell cellOutputFile)
	{
		boolean isEqual = true;

		// Check that the cells are not empty
		if (cellComparisonFile1 != null && cellComparisonFile2 != null)
		{
			// Assign a red background color if the cells content do not match
			if (POIExcelFileProcessor.getCellContents(cellComparisonFile1)
					.equals(POIExcelFileProcessor.getCellContents(cellComparisonFile2)) == false)
			{
				cellOutputFile.setCellStyle(redStyle);

				isEqual = false;
			}
		}

		return isEqual;
	}

	public boolean compareRow(Row rowComparisonFile1, Row rowComparisonFile2, Row rowOutputFile)
	{
		boolean isEqual = true;
		int counter = 0;

		// Check that the rows are not empty and that the number of columns is the same in both
		if ((rowComparisonFile1 != null && rowComparisonFile2 != null))
		{
			if (rowComparisonFile1.getLastCellNum() == rowComparisonFile2.getLastCellNum())
			{
				counter = rowComparisonFile1.getLastCellNum();
			}
			else
			{
				counter = Math.min(rowComparisonFile1.getLastCellNum(), rowComparisonFile2.getLastCellNum());
			}

			for (int i = 0; i <= counter; i++)
			{
				Cell cellComparisonFile1 = rowComparisonFile1.getCell(i);
				Cell cellComparisonFile2 = rowComparisonFile2.getCell(i);
				Cell cellOutputFile = rowOutputFile.getCell(i);

				if (compareCell(cellComparisonFile1, cellComparisonFile2, cellOutputFile) == false)
				{
					isEqual = false;
				}
			}

			if (rowComparisonFile1.getLastCellNum() != rowComparisonFile2.getLastCellNum())
			{
				for (int i = 0; i <= Math.max(rowComparisonFile1.getLastCellNum(), rowComparisonFile2.getLastCellNum()) - counter; i++)
				{

				}
			}
		}

		return isEqual;
	}

	public boolean compareSheet(Sheet sheetComparisonFile1, Sheet sheetComparisonFile2, Sheet sheetOutputFile)
	{
		boolean isEqual = true;

		// Check that their is a corresponding sheet in the second compared file
		if (sheetComparisonFile2 != null)
		{
			// Iterate over all the rows in the sheet
			for (int i = 0; i < sheetComparisonFile1.getLastRowNum(); i++)
			{
				Row rowComparisonFile1 = sheetComparisonFile1.getRow(i);
				Row rowComparisonFile2 = sheetComparisonFile2.getRow(i);
				Row rowOutputFile = sheetOutputFile.getRow(i);

				if (compareRow(rowComparisonFile1, rowComparisonFile2, rowOutputFile) == false)
				{
					isEqual = false;
				}
			}
		}

		return isEqual;
	}
}
