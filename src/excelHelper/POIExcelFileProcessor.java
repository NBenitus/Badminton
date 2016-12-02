package excelHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import pageBreak.PageBreak;

public class POIExcelFileProcessor
{
	private static File inputFile;
	private static File outputFile;

	private static HSSFWorkbook workbook;

	private static FileOutputStream fileOut;
	private static InputStream inputStream;

	/**
	 * Adds page breaks (columns and rows) to a sheet
	 *
	 * @param pageBreaks
	 *            list of page breaks objects that contain the name of the sheet, as well as the column and row page
	 *            breaks
	 */
	public static void addPageBreaks(ArrayList<PageBreak> pageBreaks)
	{
		try
		{
			// Iterate for each page break object
			for (int i = 0; i < pageBreaks.size(); i++)
			{
				// Get sheet from page break object
				HSSFSheet sheet = workbook.getSheet(pageBreaks.get(i).getSheetName());

				// Line needed to set the page breaks in the page
				sheet.setAutobreaks(false);

				// Iterate over all the row page breaks
				if (pageBreaks.get(i).getRowPageBreaks() != null)
				{
					for (int j = 0; j < pageBreaks.get(i).getRowPageBreaks().size(); j++)
					{
						sheet.setRowBreak(pageBreaks.get(i).getRowPageBreaks().get(j));
					}
				}

				// Iterate over all the column page breaks
				if (pageBreaks.get(i).getColumnPageBreaks() != null)
				{
					for (int j = 0; j < pageBreaks.get(i).getColumnPageBreaks().size(); j++)
					{
						sheet.setColumnBreak(pageBreaks.get(i).getColumnPageBreaks().get(j));
					}
				}
			}

			write();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Closes the objects used for the Excel file
	 */
	public static void close() throws IOException
	{
		workbook.close();

		inputStream.close();

		fileOut.flush();
		fileOut.close();
	}

	/**
	 * Creates the HSSF Workbook and other objects to write the excel file
	 *
	 * @param inputFileArg
	 *            excel file to makes changes to
	 */
	public static HSSFWorkbook initialize(File inputFileArg, File outputFileArg)
			throws FileNotFoundException, IOException
	{
		inputFile = inputFileArg;
		outputFile = outputFileArg;

		inputStream = new FileInputStream(inputFile);

		// Create HSSF Workbook instance holding reference to .xls file
		workbook = new HSSFWorkbook(inputStream);

		return workbook;
	}

	public static void write()
	{
		try
		{
			fileOut = new FileOutputStream(outputFile);

			workbook.write(fileOut);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
