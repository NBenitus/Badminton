package compare;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import excelHelper.POIExcelFileProcessor;
import standings.StandingsCreationHelper;

public class CompareFileTest
{
	@Test
	public void testCompare_DifferentFiles() throws IOException
	{
		File excelOne = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input1.xls");
		File excelTwo = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input2.xls");
		File excelThree = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Output.xls");

		CompareFile compareFile = new CompareFile(excelOne, excelTwo, excelThree);
		assertEquals(compareFile.compare(), false);
	}

	@Test
	public void testCompare_SameFiles() throws IOException
	{
		File excelOne = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input1.xls");
		File excelTwo = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input1.xls");
		File excelThree = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Output.xls");

		CompareFile compareFile = new CompareFile(excelOne, excelTwo, excelThree);
		assertEquals(compareFile.compare(), true);
	}

	@Test
	public void testCompareSheet_MatchingSheets() throws IOException
	{
		File excelOne = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input1.xls");
		File excelTwo = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input2.xls");
		File excelThree = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Output.xls");

		Workbook excelOneWorkbook = POIExcelFileProcessor.createWorkbook(excelOne);
		Workbook excelTwoWorkbook = POIExcelFileProcessor.createWorkbook(excelTwo);
		Workbook excelThreeWorkbook = POIExcelFileProcessor.createWorkbook(excelThree);

		Sheet excelOneSheet = excelOneWorkbook.getSheet("Classement_Individuel_Double");
		Sheet excelTwoSheet = excelTwoWorkbook.getSheet("Classement_Individuel_Double");
		Sheet excelThreeSheet = excelThreeWorkbook.getSheet("Classement_Individuel_Double");

		CompareFile compareFile = new CompareFile(excelOne, excelTwo, excelThree);
		assertEquals(compareFile.compareSheet(excelOneSheet, excelTwoSheet, excelThreeSheet), true);
	}

	@Test
	public void testCompareSheet_DifferentSheets() throws IOException
	{
		File excelOne = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input1.xls");
		File excelTwo = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input2.xls");
		File excelThree = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Output.xls");

		Workbook excelOneWorkbook = POIExcelFileProcessor.createWorkbook(excelOne);
		Workbook excelTwoWorkbook = POIExcelFileProcessor.createWorkbook(excelTwo);
		Workbook excelThreeWorkbook = POIExcelFileProcessor.createWorkbook(excelThree);

		Sheet excelOneSheet = excelOneWorkbook.getSheet("Classement_Individuel_Simple");
		Sheet excelTwoSheet = excelTwoWorkbook.getSheet("Classement_Individuel_Simple");
		Sheet excelThreeSheet = excelThreeWorkbook.getSheet("Classement_Individuel_Simple");

		CompareFile compareFile = new CompareFile(excelOne, excelTwo, excelThree);
		assertEquals(compareFile.compareSheet(excelOneSheet, excelTwoSheet, excelThreeSheet), false);
	}

	@Test
	public void testCompareRow_SameRows() throws IOException
	{
		File excelOne = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input1.xls");
		File excelTwo = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input2.xls");
		File excelThree = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Output.xls");

		Workbook excelOneWorkbook = POIExcelFileProcessor.createWorkbook(excelOne);
		Workbook excelTwoWorkbook = POIExcelFileProcessor.createWorkbook(excelTwo);
		Workbook excelThreeWorkbook = POIExcelFileProcessor.createWorkbook(excelThree);

		Sheet excelOneSheet = excelOneWorkbook.getSheet("Classement_Individuel_Simple");
		Sheet excelTwoSheet = excelTwoWorkbook.getSheet("Classement_Individuel_Simple");
		Sheet excelThreeSheet = excelThreeWorkbook.getSheet("Classement_Individuel_Simple");

		Row excelOneRow = excelOneSheet.getRow(23);
		Row excelTwoRow = excelTwoSheet.getRow(23);
		Row excelThreeRow = excelThreeSheet.getRow(23);

		CompareFile compareFile = new CompareFile(excelOne, excelTwo, excelThree);
		assertEquals(compareFile.compareRow(excelOneRow, excelTwoRow, excelThreeRow), true);
	}

	@Test
	public void testCompareRow_DifferentRows() throws IOException
	{
		File excelOne = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input1.xls");
		File excelTwo = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input2.xls");
		File excelThree = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Output.xls");

		Workbook excelOneWorkbook = POIExcelFileProcessor.createWorkbook(excelOne);
		Workbook excelTwoWorkbook = POIExcelFileProcessor.createWorkbook(excelTwo);
		Workbook excelThreeWorkbook = POIExcelFileProcessor.createWorkbook(excelThree);

		Sheet excelOneSheet = excelOneWorkbook.getSheet("Classement_Individuel_Simple");
		Sheet excelTwoSheet = excelTwoWorkbook.getSheet("Classement_Individuel_Simple");
		Sheet excelThreeSheet = excelThreeWorkbook.getSheet("Classement_Individuel_Simple");

		Row excelOneRow = excelOneSheet.getRow(29);
		Row excelTwoRow = excelTwoSheet.getRow(29);
		Row excelThreeRow = excelThreeSheet.getRow(29);

		CompareFile compareFile = new CompareFile(excelOne, excelTwo, excelThree);
		assertEquals(compareFile.compareRow(excelOneRow, excelTwoRow, excelThreeRow), false);
	}

	@Test
	public void testCompareCell_SameCells() throws IOException
	{
		File excelOne = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input1.xls");
		File excelTwo = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input2.xls");
		File excelThree = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Output.xls");

		Workbook excelOneWorkbook = POIExcelFileProcessor.createWorkbook(excelOne);
		Workbook excelTwoWorkbook = POIExcelFileProcessor.createWorkbook(excelTwo);
		Workbook excelThreeWorkbook = POIExcelFileProcessor.createWorkbook(excelThree);

		Sheet excelOneSheet = excelOneWorkbook.getSheet("Classement_Individuel_Simple");
		Sheet excelTwoSheet = excelTwoWorkbook.getSheet("Classement_Individuel_Simple");
		Sheet excelThreeSheet = excelThreeWorkbook.getSheet("Classement_Individuel_Simple");

		Row excelOneRow = excelOneSheet.getRow(26);
		Row excelTwoRow = excelTwoSheet.getRow(26);
		Row excelThreeRow = excelThreeSheet.getRow(26);

		Cell excelOneCell = excelOneRow.getCell(10);
		Cell excelTwoCell = excelTwoRow.getCell(10);
		Cell excelThreeCell = excelThreeRow.getCell(10);

		CompareFile compareFile = new CompareFile(excelOne, excelTwo, excelThree);
		assertEquals(compareFile.compareCell(excelOneCell, excelTwoCell, excelThreeCell), true);
	}

	@Test
	public void testCompareCell_DifferentCells() throws IOException
	{
		File excelOne = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input1.xls");
		File excelTwo = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Input2.xls");
		File excelThree = new File(StandingsCreationHelper.directoryPath + "Test\\CompareFile_Test_Output.xls");

		Workbook excelOneWorkbook = POIExcelFileProcessor.createWorkbook(excelOne);
		Workbook excelTwoWorkbook = POIExcelFileProcessor.createWorkbook(excelTwo);
		Workbook excelThreeWorkbook = POIExcelFileProcessor.createWorkbook(excelThree);

		Sheet excelOneSheet = excelOneWorkbook.getSheet("Classement_Individuel_Simple");
		Sheet excelTwoSheet = excelTwoWorkbook.getSheet("Classement_Individuel_Simple");
		Sheet excelThreeSheet = excelThreeWorkbook.getSheet("Classement_Individuel_Simple");

		Row excelOneRow = excelOneSheet.getRow(27);
		Row excelTwoRow = excelTwoSheet.getRow(27);
		Row excelThreeRow = excelThreeSheet.getRow(27);

		Cell excelOneCell = excelOneRow.getCell(10);
		Cell excelTwoCell = excelTwoRow.getCell(10);
		Cell excelThreeCell = excelThreeRow.getCell(10);

		CompareFile compareFile = new CompareFile(excelOne, excelTwo, excelThree);
		assertEquals(compareFile.compareCell(excelOneCell, excelTwoCell, excelThreeCell), false);
	}
}
