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
import standings.PageBreak;

public class POIExcelFileProcessor
{
	private static File excelFile;
	private static HSSFWorkbook workbook;
	private static FileOutputStream fileOut;
	private static InputStream inputStream;

	/**
	 * Adds column page breaks to a sheet
	 *
	 * @param sheetName
	 *            name of the sheet to add column page breaks
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) which to set column page breaks
	 */
	public static void addPageBreaks(ArrayList<PageBreak> pageBreaks)
	{
		try
		{
			for (int i = 0; i < pageBreaks.size(); i++)
			{

				HSSFSheet sheet = workbook.getSheet(pageBreaks.get(i).getSheetName());

				sheet.setAutobreaks(false);

				if (pageBreaks.get(i).getRowPageBreaks() != null)
				{
					for (int j = 0; j < pageBreaks.get(i).getRowPageBreaks().size(); j ++)
					{
						sheet.setRowBreak(pageBreaks.get(i).getRowPageBreaks().get(j));
					}
				}

				if (pageBreaks.get(i).getColumnPageBreaks() != null)
				{
					for (int j = 0; j < pageBreaks.get(i).getColumnPageBreaks().size(); j ++)
					{
						sheet.setColumnBreak(pageBreaks.get(i).getColumnPageBreaks().get(j));
					}
				}
			}

			fileOut = new FileOutputStream(excelFile);

			workbook.write(fileOut);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Closes the excel file
	 */
	public static void closeFile() throws IOException
	{
		workbook.close();
		inputStream.close();
		fileOut.close();
	}

	/**
	 * Creates the objects to write the excel file
	 *
	 * @param file
	 *            excel file to makes changes to
	 */
	public static void initializeFile(File file) throws FileNotFoundException, IOException
	{
		excelFile = file;

		inputStream = new FileInputStream(excelFile);

		// Create Workbook instance holding reference to .xlsx file
		workbook = new HSSFWorkbook(inputStream);
	}

}
