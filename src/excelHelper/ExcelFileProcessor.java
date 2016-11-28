package excelHelper;

import java.io.IOException;
import java.util.ArrayList;

import database.PostgreSQLJDBC;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import standings.IndividualCombinedResult;
import standings.IndividualResult;
import standings.IndividualResultSheet;
import standings.StandingsFile;
import standings.TeamResult;
import standings.TeamResultSheet;
import standings.StandingsCreationHelper.TypeOfResult;

public class ExcelFileProcessor
{
	private static int currentRow;

	/**
	 * Compares two sheets and writes the differences using a red background
	 * TO DO: Have two sheet names in case the sheet names for both files is different
	 *
	 * @param writableWorkbook
	 *            workbook that contains the sheet to be copied
	 * @param firstExcelFileListCells
	 *            array of cells from the first file
	 * @param secondExcelFileListCells
	 *            array of cells from the second file
	 * @param sheetName
	 *            name of the sheet to compare in both files
	 */
	public static void compareSheet(WritableWorkbook resultsWorkbook, ArrayList<Cell[]> firstExcelFileListCells,
			ArrayList<Cell[]> secondExcelFileListCells, String sheetName) throws Exception
	{
		WritableSheet writableSheet = ExcelFileProcessor.getSheet(resultsWorkbook,
				ExcelFileProcessor.getSheetIndex(resultsWorkbook, sheetName));

		// Cell formats for the headings
		WritableCellFormat cellFormatHighlighted = new WritableCellFormat();
		cellFormatHighlighted.setAlignment(Alignment.CENTRE);
		cellFormatHighlighted.setBackground(Colour.RED);

		WritableCellFormat cellFormatNormal = new WritableCellFormat();
		cellFormatNormal.setAlignment(Alignment.CENTRE);
		cellFormatNormal.setBackground(Colour.WHITE);

		// Iterate over the number of rows in the first file
		for (int i = 0; i < firstExcelFileListCells.size(); i++)
		{
			// Iterate over the number of cells in the row
			for (int j = 1; j < firstExcelFileListCells.get(i).length; j++)
			{
				if ((i < secondExcelFileListCells.size()) && (j < secondExcelFileListCells.get(i).length))
				{
					WritableCell cell = writableSheet.getWritableCell(secondExcelFileListCells.get(i)[j].getColumn(),
							firstExcelFileListCells.get(i)[j].getRow());

					// Write the cell with a red background if the contents on both files is different
					if (!firstExcelFileListCells.get(i)[j].getContents()
							.equals(secondExcelFileListCells.get(i)[j].getContents()))
					{
						writeCell(writableSheet, cell, firstExcelFileListCells.get(i)[j].getContents(),
								cellFormatHighlighted);
					}

					// Write the cell with a white background if the contents on both files is the same
					else
					{
						writeCell(writableSheet, cell, firstExcelFileListCells.get(i)[j].getContents(),
								cellFormatNormal);
					}
				}
			}
		}
	}

	/**
	 * Copies a sheet and gives it a new name
	 *
	 * @param writableWorkbook
	 *            workbook that contains the sheet to be copied
	 * @param sourceSheetName
	 *            name of the template sheet to be copied
	 * @param newSheetName
	 *            new name of the copied sheet
	 */
	public static void copySheet(WritableWorkbook writableWorkbook, String sourceSheetName, String newSheetName)
	{
		writableWorkbook.copySheet(getSheetIndex(writableWorkbook, sourceSheetName), newSheetName,
				writableWorkbook.getNumberOfSheets() + 1);
	}

	/**
	 * Deletes a sheet
	 *
	 * @param writableWorkbook
	 *            workbook that contains the sheet to be deleted
	 * @param index
	 *            index of the sheet to be deleted
	 */
	public static void deleteSheet(WritableWorkbook writableWorkbook, String sheetname)
	{
		writableWorkbook.removeSheet(getSheetIndex(writableWorkbook, sheetname));
	}

	/**
	 * Gets the sheet at the provided index
	 *
	 * @param writableWorkbook
	 *            workbook that contains the sheet to be obtained
	 * @param index
	 *            index of the sheet to be retrieved
	 * @return sheet at the provided index
	 */
	public static WritableSheet getSheet(WritableWorkbook writableWorkbook, int index)
	{
		return writableWorkbook.getSheet(index);
	}

	/**
	 * Gets the index of a sheet
	 *
	 * @param writableWorkbook
	 *            workbook that contains the sheet
	 * @param sheetName
	 *            name of the sheet which to get the index
	 * @return the index of the sheet
	 */
	public static int getSheetIndex(WritableWorkbook writableWorkbook, String sheetName)
	{
		Integer index = null;
		final Sheet[] sheets = writableWorkbook.getSheets();

		// Iterate through each sheet to see if the sheet's name matches the searched name
		for (int i = 0; i < sheets.length && index == null; i++)
		{
			if (sheets[i].getName().equals(sheetName))
			{
				index = i;
			}
		}

		return index;
	}

	/**
	 * Renames a sheet
	 *
	 * @param writableSheet
	 *            sheet object to be renamed
	 * @param sheetName
	 *            new name of the sheet
	 */
	public void renameSheet(WritableSheet writableSheet, String sheetName)
	{
		writableSheet.setName(sheetName);
	}

	/**
	 * Closes both the original and copy workbooks after writing the changes
	 *
	 * @param writableWorkbook
	 *            copy workbook to be written and closed
	 * @param workbook
	 *            original workbook to be closed
	 */
	public static void writeAndClose(WritableWorkbook writableWorkbook, Workbook workbook)
			throws BiffException, IOException, WriteException
	{
		writableWorkbook.write();
		writableWorkbook.close();
		workbook.close();
	}

	/**
	 * Writes the content of a cell
	 *
	 * @param writableSheet
	 *            sheet object that contains the cell to be modified
	 * @param cell
	 *            cell to be modified
	 * @param value
	 *            new value of the cell
	 * @param cellFormat
	 *            cell format (border, alignment, etc.) for the cell
	 */
	public static void writeCell(WritableSheet writableSheet, WritableCell cell, String value,
			WritableCellFormat cellFormat) throws RowsExceededException, WriteException
	{
		// Create a label or number object to write content to the cell if the value is not empty
		if (value != "")
		{
			Label label = new Label(cell.getColumn(), cell.getRow(), value, cellFormat);
			writableSheet.addCell(label);
		}

		// Create a blank object if the value is empty
		else
		{
			Blank blank = new Blank(cell.getColumn(), cell.getRow(), cellFormat);
			writableSheet.addCell(blank);
		}
	}

	/**
	 * Writes an individual result sheet, one type of result at a time
	 *
	 * @param workbookCopy
	 *            copy of the workbook where the sheet is situated
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 * @param typeOfPlay
	 *            type of play to write (ex: Single)
	 */
	public static void writeIndividualResultSheet(WritableWorkbook workbookCopy,
			IndividualResultSheet individualResultSheet, TypeOfResult typeOfResult, int index)
			throws BiffException, IOException, WriteException
	{
		ArrayList<IndividualResult> listIndividualResults = PostgreSQLJDBC.getIndividualResults(typeOfResult,
				individualResultSheet.getTypeOfPlay());

		// Get the sheet object from the sheet's name
		WritableSheet writableSheet = getSheet(workbookCopy,
				getSheetIndex(workbookCopy, individualResultSheet.getName()));

		// Cell format for cells without borders
		WritableCellFormat cellFormatNone = new WritableCellFormat();
		cellFormatNone.setBorder(Border.NONE, BorderLineStyle.NONE);

		// Fonts for the headings
		WritableFont cellFontH1 = new WritableFont(WritableFont.ARIAL, 16);
		cellFontH1.setBoldStyle(WritableFont.BOLD);

		// Cell formats for the headings
		WritableCellFormat cellFormatH1 = new WritableCellFormat();
		cellFormatH1.setAlignment(Alignment.CENTRE);
		cellFormatH1.setFont(cellFontH1);

		// Write heading
		WritableCell headerCell = writableSheet.getWritableCell(
				(individualResultSheet.getNumberOfColumnsForStandings() * index) + IndividualResultSheet.FIRSTCOLUMN,
				IndividualResultSheet.HEADERROW);
		writeCell(writableSheet, headerCell, individualResultSheet.getHeaderName(), cellFormatH1);

		// Delete all the rows contents for the type of result
		for (int i = 0; i < IndividualResultSheet.NUMBEROFROWSTODELETE; i++)
		{
			for (int j = 0; j < individualResultSheet.getNumberOfColumnsForStandings(); j++)
			{
				WritableCell deleteCell = writableSheet
						.getWritableCell((individualResultSheet.getNumberOfColumnsForStandings() * index)
								+ IndividualResultSheet.FIRSTCOLUMN + j, IndividualResultSheet.FIRSTROW + i);
				writeCell(writableSheet, deleteCell, "", cellFormatNone);
			}
		}

		// Iterate over each individual result
		for (int i = 0; i < listIndividualResults.size(); i++)
		{

			// Rank cell
			WritableCell rankCell = writableSheet
					.getWritableCell((individualResultSheet.getNumberOfColumnsForStandings() * index)
							+ IndividualResultSheet.FIRSTCOLUMN, IndividualResultSheet.FIRSTROW + i);
			writeCell(writableSheet, rankCell, listIndividualResults.get(i).getRank() + "", cellFormatNone);

			// Player name cell
			WritableCell playerNameCell = writableSheet
					.getWritableCell((individualResultSheet.getNumberOfColumnsForStandings() * index)
							+ IndividualResultSheet.FIRSTCOLUMN + 1, IndividualResultSheet.FIRSTROW + i);
			writeCell(writableSheet, playerNameCell, listIndividualResults.get(i).getPlayerName(), cellFormatNone);

			// School name cell
			WritableCell schoolNameCell = writableSheet
					.getWritableCell((individualResultSheet.getNumberOfColumnsForStandings() * index)
							+ IndividualResultSheet.FIRSTCOLUMN + 2, IndividualResultSheet.FIRSTROW + i);
			writeCell(writableSheet, schoolNameCell, listIndividualResults.get(i).getSchoolName(), cellFormatNone);

			// Write the extra single and double score cells as well as the total score for Individual Combined results
			if (listIndividualResults.get(i) instanceof IndividualCombinedResult)
			{
				// Single score cell
				WritableCell singleScoreCell = writableSheet
						.getWritableCell((individualResultSheet.getNumberOfColumnsForStandings() * index)
								+ IndividualResultSheet.FIRSTCOLUMN + 3, IndividualResultSheet.FIRSTROW + i);
				writeCell(writableSheet, singleScoreCell, listIndividualResults.get(i).getSingleScore() + "",
						cellFormatNone);

				// Double score cell
				WritableCell doubleScoreCell = writableSheet
						.getWritableCell((individualResultSheet.getNumberOfColumnsForStandings() * index)
								+ IndividualResultSheet.FIRSTCOLUMN + 4, IndividualResultSheet.FIRSTROW + i);
				writeCell(writableSheet, doubleScoreCell, listIndividualResults.get(i).getDoubleScore() + "",
						cellFormatNone);

				// Total score cell
				WritableCell totalScoreCell = writableSheet
						.getWritableCell((individualResultSheet.getNumberOfColumnsForStandings() * index)
								+ IndividualResultSheet.FIRSTCOLUMN + 5, IndividualResultSheet.FIRSTROW + i);
				writeCell(writableSheet, totalScoreCell, listIndividualResults.get(i).getScore() + "", cellFormatNone);
			}

			// Write the score for Single and Double results
			else
			{
				// Score cell
				WritableCell scoreCell = writableSheet
						.getWritableCell((individualResultSheet.getNumberOfColumnsForStandings() * index)
								+ IndividualResultSheet.FIRSTCOLUMN + 3, IndividualResultSheet.FIRSTROW + i);
				writeCell(writableSheet, scoreCell, listIndividualResults.get(i).getScore() + "", cellFormatNone);
			}
		}
	}

	/**
	 * Writes the team result sheet
	 *
	 * @param standingsFile
	 *            jExcelFile object containing the sheets for the standings
	 * @param workbookCopy
	 *            copy of the workbook where sheets are written
	 * @param teamResultSheet
	 *            sheet where to write the team results
	 * @param typeOfResult
	 *            type of result (ex: Benjamin masculin) to write
	 */
	public static void writeTeamResultSheet(StandingsFile standingsFile, WritableWorkbook workbookCopy,
			TeamResultSheet teamResultSheet, TypeOfResult typeOfResult)
			throws BiffException, IOException, WriteException
	{
		// Get the sheet object from the sheet's name
		WritableSheet writableSheet = getSheet(workbookCopy, getSheetIndex(workbookCopy, teamResultSheet.getName()));

		// Get the list of team results for the matching type of result
		ArrayList<TeamResult> teamsResults = PostgreSQLJDBC.getTeamResults(typeOfResult);

		// Cell formats for the left most cells (left border)
		WritableCellFormat cellFormatLeft = new WritableCellFormat();
		cellFormatLeft.setBorder(Border.LEFT, BorderLineStyle.MEDIUM, Colour.BLACK);
		cellFormatLeft.setAlignment(Alignment.CENTRE);

		// Cell formats for the right most cells (right border)
		WritableCellFormat cellFormatRight = new WritableCellFormat();
		cellFormatRight.setBorder(Border.RIGHT, BorderLineStyle.MEDIUM, Colour.BLACK);
		cellFormatRight.setAlignment(Alignment.CENTRE);

		// Cell formats for the middle cells (no border)
		WritableCellFormat cellFormatMiddle = new WritableCellFormat();
		cellFormatMiddle.setBorder(Border.NONE, BorderLineStyle.NONE);
		cellFormatMiddle.setAlignment(Alignment.CENTRE);

		// Fonts for the headings
		WritableFont cellFontH1 = new WritableFont(WritableFont.ARIAL, 16);
		cellFontH1.setBoldStyle(WritableFont.BOLD);

		WritableFont cellFontH2 = new WritableFont(WritableFont.ARIAL, 14);
		cellFontH1.setBoldStyle(WritableFont.BOLD);

		// Cell formats for the headings
		WritableCellFormat cellFormatAH1 = new WritableCellFormat();
		cellFormatAH1.setBorder(Border.ALL, BorderLineStyle.MEDIUM);
		cellFormatAH1.setAlignment(Alignment.CENTRE);
		cellFormatAH1.setBackground(Colour.GRAY_25);
		cellFormatAH1.setFont(cellFontH1);

		WritableCellFormat cellFormatAH2 = cellFormatAH1;
		cellFormatAH2.setFont(cellFontH2);

		// Extra line for the h1 headers for the two genders
		if ((typeOfResult == TypeOfResult.BENJAMIN_FEMININ) || (typeOfResult == TypeOfResult.BENJAMIN_MASCULIN))
		{
			if (typeOfResult == TypeOfResult.BENJAMIN_FEMININ)
			{
				currentRow = TeamResultSheet.FIRSTROW;
			}
			else if (typeOfResult == TypeOfResult.BENJAMIN_MASCULIN)
			{
				standingsFile.getTeamResultSheet().setRowPageBreak(currentRow - 1);
			}

			WritableCell h1Cell = writableSheet.getWritableCell(TeamResultSheet.FIRSTCOLUMN, currentRow);
			writeCell(writableSheet, h1Cell, typeOfResult.gender().text(), cellFormatAH1);

			currentRow++;
		}

		// H2 headers for the categories of players
		WritableCell h2Cell = writableSheet.getWritableCell(TeamResultSheet.FIRSTCOLUMN, currentRow);
		writeCell(writableSheet, h2Cell, typeOfResult.category().text(), cellFormatAH2);

		currentRow++;

		// Empty line cells below the h2 header
		WritableCell emptyLineCellBelowH2HeaderLeft = writableSheet.getWritableCell(TeamResultSheet.FIRSTCOLUMN,
				currentRow);
		writeCell(writableSheet, emptyLineCellBelowH2HeaderLeft, "", cellFormatLeft);

		WritableCell emptyLineCellBelowH2HeaderRight = writableSheet
				.getWritableCell(TeamResultSheet.FIRSTCOLUMN + TeamResultSheet.NUMBEROFCOLUMNSTOTAL - 1, currentRow);
		writeCell(writableSheet, emptyLineCellBelowH2HeaderRight, "", cellFormatRight);

		// Skip the column headers ("Rang", "École", "Tournoi1", etc.)
		currentRow += 2;

		// Empty line between column headers and school information -- Bug fix for black line at the last category
		for (int i = 0; i < TeamResultSheet.FIRSTCOLUMN + TeamResultSheet.NUMBEROFCOLUMNSTOTAL; i++)
		{
			switch (i)
			{
			case 0:
				// Empty line cells below the column headers
				WritableCell emptyLineCellBelowColumnHeadersLeft = writableSheet
						.getWritableCell(TeamResultSheet.FIRSTCOLUMN + i, currentRow);
				writeCell(writableSheet, emptyLineCellBelowColumnHeadersLeft, "", cellFormatLeft);
				break;

			case TeamResultSheet.NUMBEROFCOLUMNSTOTAL - 1:
				WritableCell emptyLineCellBelowColumnHeadersRight = writableSheet
						.getWritableCell(TeamResultSheet.FIRSTCOLUMN + i, currentRow);
				writeCell(writableSheet, emptyLineCellBelowColumnHeadersRight, "", cellFormatRight);
				break;

			default:
				WritableCell emptyLineCellBelowColumnHeadersMiddle = writableSheet
						.getWritableCell(TeamResultSheet.FIRSTCOLUMN + i, currentRow);
				writeCell(writableSheet, emptyLineCellBelowColumnHeadersMiddle, "", cellFormatMiddle);
				break;
			}
		}

		currentRow++;

		// Iterate over each team of the category
		for (int i = 0; i < teamsResults.size(); i++)
		{
			// Set cell format for the last row (bottom border) when it is the last type of result
			if (((typeOfResult == TypeOfResult.JUVENIL_MASCULIN) || (typeOfResult == TypeOfResult.JUVENIL_FEMININ))
					&& (i == teamsResults.size() - 1))
			{
				// Cell format for the bottom left cell (bottom left border)
				cellFormatLeft = new WritableCellFormat();
				cellFormatLeft.setBorder(Border.LEFT, BorderLineStyle.MEDIUM, Colour.BLACK);
				cellFormatLeft.setBorder(Border.BOTTOM, BorderLineStyle.MEDIUM, Colour.BLACK);
				cellFormatLeft.setAlignment(Alignment.CENTRE);

				// Cell format for the bottom right cell (bottom right border)
				cellFormatRight = new WritableCellFormat();
				cellFormatRight.setBorder(Border.RIGHT, BorderLineStyle.MEDIUM, Colour.BLACK);
				cellFormatRight.setBorder(Border.BOTTOM, BorderLineStyle.MEDIUM, Colour.BLACK);
				cellFormatRight.setAlignment(Alignment.CENTRE);

				// Cell format for the bottom middle cells (bottom border)
				cellFormatMiddle = new WritableCellFormat();
				cellFormatMiddle.setBorder(Border.NONE, BorderLineStyle.NONE, Colour.BLACK);
				cellFormatMiddle.setBorder(Border.BOTTOM, BorderLineStyle.MEDIUM, Colour.BLACK);
				cellFormatMiddle.setAlignment(Alignment.CENTRE);
			}

			// Insert new row to write the team result
			writableSheet.insertRow(currentRow);

			// Rank cell
			WritableCell rankCell = writableSheet.getWritableCell(TeamResultSheet.FIRSTCOLUMN, currentRow);
			writeCell(writableSheet, rankCell, teamsResults.get(i).getRank() + "", cellFormatLeft);

			// School name cell
			WritableCell schoolNameCell = writableSheet.getWritableCell(TeamResultSheet.FIRSTCOLUMN + 1, currentRow);
			writeCell(writableSheet, schoolNameCell, teamsResults.get(i).getSchoolName(), cellFormatMiddle);

			// Iterate over each tournament (1 to 5)
			for (int j = 0; j <= TeamResultSheet.NUMBEROFTOURNAMENTS; j++)
			{
				float score;

				// Unsure what this does
				if (teamsResults.get(i).getScores().size() < (j + 1))
				{
					score = 0;
				}
				else
				{
					score = teamsResults.get(i).getScores().get(j);
				}

				WritableCell scoreCell = writableSheet.getWritableCell(TeamResultSheet.FIRSTCOLUMN + 2 + j, currentRow);
				writeCell(writableSheet, scoreCell, String.format("%.2f", score), cellFormatMiddle);
			}

			// Total score for the schools cell
			WritableCell totalScoreCell = writableSheet
					.getWritableCell(TeamResultSheet.FIRSTCOLUMN + 2 + TeamResultSheet.NUMBEROFTOURNAMENTS, currentRow);
			totalScoreCell.setCellFormat(cellFormatRight);
			writeCell(writableSheet, totalScoreCell, String.format("%.2f", teamsResults.get(i).getTotalScore()),
					cellFormatRight);

			currentRow++;
		}
	}
}
