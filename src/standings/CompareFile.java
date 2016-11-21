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
	public void main(String[] args)
	{
		File excelOne = new File("C:\\Benoit\\Work\\Java\\Badminton\\Résultats.xls");
		File excelTwo = new File("C:\\Benoit\\Work\\Java\\Badminton\\Résultats_Normand.xls");
		File excelThree = new File("C:\\Benoit\\Work\\Java\\Badminton\\Compare_Test.xls");

//		StandingsFile resultsJExcelFile = new StandingsFile();
//		resultsJExcelFile.compareFiles(excelOne, excelTwo, excelThree);
	}

	// To be continued
	public void compareFiles(File excelFileOne, File excelFileTwo, File compareResultFile) throws Exception
	{
		Workbook workbook = Workbook.getWorkbook(excelFileOne);
		WritableWorkbook writableWorkbook = Workbook.createWorkbook(compareResultFile, workbook);

		ArrayList<Cell[]> firstExcelFileListCells = ExcelFileReader.readRows(excelFileOne,
				"Classement_Individuel_Combiné");
		ArrayList<Cell[]> secondExcelFileListCells = ExcelFileReader.readRows(excelFileTwo,
				"Classement_Individuel_Combiné");

		ExcelFileProcessor.compareSheet(writableWorkbook, firstExcelFileListCells, secondExcelFileListCells,
				"Classement_Individuel_Combiné");

		ExcelFileProcessor.writeAndClose(writableWorkbook, workbook);

//		firstExcelFileListCells = ExcelFileReader.readRows(excelFileOne, teamResultSheet.getName());
//		secondExcelFileListCells = ExcelFileReader.readRows(excelFileTwo, teamResultSheet.getName());
//
//		JExcelFileProcessor.compareSheet(writableWorkbook, firstExcelFileListCells, secondExcelFileListCells,
//				teamResultSheet.getName());
//
//		close(writableWorkbook, workbook);
	}
}
