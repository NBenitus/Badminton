package excelHelper;

import java.io.File;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;

public class ExcelFileReader
{
	private static Workbook workbook;

	/**
	 * Closes the objects used to read the XLS file
	 *
	 * @throws Exception
	 */
	public static void close() throws Exception
	{
		workbook.close();
	}

	/**
	 * Initialize the objects to read the XLS file
	 *
	 * @param xlsFile
	 *            xls file to be read
	 * @throws Exception
	 */
	public static Workbook initialize(File xlsFile) throws Exception
	{
		try
		{
			// Excel document to be imported
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setLocale(new Locale("en", "EN"));
			workbook = Workbook.getWorkbook(xlsFile, workbookSettings);
		}
		catch (Exception e)
		{
			System.err.println(e.toString());

			e.printStackTrace();
		}

		return workbook;
	}
}
