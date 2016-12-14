package excelHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

public class POIExcelFileProcessor
{
	/**
	 * Creates a cell at the specified location.
	 *
	 * @param sheet
	 *            sheet that contains the cell to be created
	 * @param columnNumber
	 *            number of the column where the cell is situated
	 * @param rowNumber
	 *            number of the row where the cell is situated
	 * @param value
	 *            value of the cell to be created
	 */
	public static void createCell(Sheet sheet, int columnNumber, int rowNumber, String value)
	{
		Row row = sheet.getRow(rowNumber);

		// Create the row at the specified location is null
		if (row == null)
		{
			row = sheet.createRow(rowNumber);
		}

		Cell cell = sheet.getRow(rowNumber).getCell(columnNumber);

		// Create the cell at the specified location is null
		if (cell == null)
		{
			cell = sheet.getRow(rowNumber).createCell(columnNumber);
		}

		cell.setCellValue(value);
	}

	/**
	 * Creates a cell at the specified location.
	 *
	 * @param sheet
	 *            sheet that contains the cell to be created
	 * @param columnNumber
	 *            number of the column where the cell is situated
	 * @param rowNumber
	 *            number of the row where the cell is situated
	 * @param value
	 *            value of the cell to be created
	 * @param cellStyle
	 *            style to be applied to the cell
	 */
	public static void createCell(Sheet sheet, int columnNumber, int rowNumber, String value, CellStyle cellStyle)
	{
		Row row = sheet.getRow(rowNumber);

		// Create the row at the specified location is null
		if (row == null)
		{
			row = sheet.createRow(rowNumber);
		}

		// Create the cell at the specified location is null
		Cell cell = sheet.getRow(rowNumber).getCell(columnNumber);

		if (cell == null)
		{
			cell = sheet.getRow(rowNumber).createCell(columnNumber);
		}

		// Apply the cell style to the cell
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);
	}

	/**
	 * Creates the workbook object to be used for the Excel file operations
	 *
	 * @param inputFile
	 *            corresponding Excel File to perform read or write operations on
	 * @return	workbook object created from the input file
	 */
	public static Workbook createWorkbook(File inputFile)
	{
		Workbook workbook = null;

		try
		{
			workbook = WorkbookFactory.create(inputFile);
		}
		catch (EncryptedDocumentException e)
		{
			e.printStackTrace();
		}
		catch (InvalidFormatException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return workbook;
	}

	/**
	 * Creates the workbook object to be used for the Excel file operations
	 *
	 * @param inputStream
	 *            corresponding input stream linked to an Excel File (usually a template file) to perform read or write
	 *            operations on
	 * @return	workbook object created from the input stream
	 */
	public static Workbook createWorkbook(InputStream inputStream)
	{
		Workbook workbook = null;

		try
		{
			workbook = WorkbookFactory.create(inputStream);
		}
		catch (EncryptedDocumentException e)
		{
			e.printStackTrace();
		}
		catch (InvalidFormatException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return workbook;
	}

	/**
	 * Closes the workbook object that was used for the Excel file operations
	 * Usually called after read operations
	 *
	 * @param workbook
	 *            workbook object used for Excel file operations
	 */
	public static void closeWorkbook(Workbook workbook)
	{
		try
		{
			workbook.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Gets the content of a cell as a string, regardless of the type of the cell (number, date, etc.)
	 *
	 * @param cell
	 *            cell to get the content from
	 * @return string that contains the content of the cell
	 */
	public static String getCellContents(Cell cell)
	{
		DataFormatter df = new DataFormatter();

		return df.formatCellValue(cell);
	}

	/**
	 * Sets the border of a range of cells
	 *
	 * @param sheet
	 *            sheet that contains the range of cells to apply the border to
	 * @param columnNumber
	 *            number of the column where the cell is situated
	 * @param rowNumber
	 *            number of the row where the cell is situated
	 * @param value
	 *            value of the cell to be created
	 */
	public static void setBorder(Sheet sheet, CellRangeAddress cellRangeAddress, BorderStyle leftBorder, BorderStyle rightBorder,
			BorderStyle topBorder, BorderStyle bottomBorder)
	{
		RegionUtil.setBorderLeft(leftBorder, cellRangeAddress, sheet);
		RegionUtil.setBorderRight(rightBorder, cellRangeAddress, sheet);
		RegionUtil.setBorderTop(topBorder, cellRangeAddress, sheet);
		RegionUtil.setBorderBottom(bottomBorder, cellRangeAddress, sheet);

	}

	/**
	 * Writes to file the workbook object that was used for the Excel file operations
	 *
	 * @param workbook
	 *            workbook object used for Excel file operations
	 * @param outputFile
	 *            file created on disk that contains the result of the Excel file operations
	 */
	public static void writeWorkbook(Workbook workbook, File outputFile)
	{
		try
		{
			FileOutputStream fileOut = new FileOutputStream(outputFile);
			workbook.write(fileOut);

			fileOut.flush();
			fileOut.close();
			workbook.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
