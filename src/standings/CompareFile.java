package standings;

import java.io.File;
import java.util.ArrayList;

import excelHelper.ExcelFileReader;
import excelHelper.ExcelFileProcessor;
import jxl.Cell;
import jxl.Workbook;
import jxl.write.WritableWorkbook;

public class CompareFile
{
	public static void main(String[] args) throws Exception
	{
		File excelOne = new File("C:\\Benoit\\Work\\Java\\Badminton\\R�sultats.xls");
		File excelTwo = new File("C:\\Benoit\\Work\\Java\\Badminton\\R�sultats_MisAJour.xls");
		File excelThree = new File("C:\\Benoit\\Work\\Java\\Badminton\\Compare_Test.xls");

		compareFiles(excelOne, excelTwo, excelThree);
	}

	// In progress
	public static void compareFiles(File excelFileOne, File excelFileTwo, File compareResultFile) throws Exception
	{
		Workbook workbook = Workbook.getWorkbook(excelFileOne);
		WritableWorkbook writableWorkbook = Workbook.createWorkbook(compareResultFile, workbook);

		ArrayList<Cell[]> firstExcelFileListCells = ExcelFileReader.readRows(excelFileOne,
				"Classement_Individuel_Combin�");
		ArrayList<Cell[]> secondExcelFileListCells = ExcelFileReader.readRows(excelFileTwo,
				"Classement_Individuel_Combin�");

		ExcelFileProcessor.compareSheet(writableWorkbook, firstExcelFileListCells, secondExcelFileListCells,
				"Classement_Individuel_Combin�");

		ExcelFileProcessor.writeAndClose(writableWorkbook, workbook);
	}
}
