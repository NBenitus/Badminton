package standings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import excelHelper.ExcelFileProcessor;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import standings.StandingsCreationHelper.TypeOfPlay;

public class StandingsFile
{
	private Workbook workbook;
	private WritableWorkbook writableWorkbook;

	private ArrayList<IndividualResultSheet> individualResultSheets = new ArrayList<IndividualResultSheet>();
	private TeamResultSheet teamResultSheet;

	/**
	 * Constructor.
	 *
	 * @param inputStream
	 *            input stream from the template file
	 * @param outputFile
	 *            file that will contain the created team and individual results
	 */
	public StandingsFile(InputStream inputStream, File outputFile)
			throws BiffException, IOException, WriteException
	{
		this.workbook = Workbook.getWorkbook(inputStream);
		this.writableWorkbook = Workbook.createWorkbook(outputFile, workbook);

		for (int i = 0; i < TypeOfPlay.values().length; i++)
		{
			this.addIndividualResultSheet(TypeOfPlay.values()[i].text(), TypeOfPlay.values()[i]);
		}

		this.setTeamResultSheet();
	}

	/**
	 * Adds an individual result sheet to the list of individual result sheets
	 *
	 * @param sheetName
	 *            sheet name of the individual result sheet
	 * @param typeOfPlay
	 *            type of play (either single or double)
	 */
	public void addIndividualResultSheet(String sheetName, TypeOfPlay typeOfPlay)
	{
		if (typeOfPlay != TypeOfPlay.COMBINED)
		{
			individualResultSheets
				.add(new IndividualResultSheet(this, sheetName, typeOfPlay));
		}
		else
		{
			individualResultSheets.add(new IndividualCombinedResultSheet(this, typeOfPlay));
		}
	}

	/**
	 * Closes the instance of the StandingsFile and its open input and output streams
	 *
	 */
	public void close() throws BiffException, IOException, WriteException
	{
		ExcelFileProcessor.writeAndClose(this.getWritableWorkbook(), this.getWorkbook());
	}

	/**
	 * Deletes the template sheets in a StandingsFile
	 *
	 * @param standingsFile
	 *            standingsFile which to delete template sheets from
	 */
	public void deleteTemplateSheet()
	{
		ExcelFileProcessor.deleteSheet(this.getWritableWorkbook(), IndividualResultSheet.TEMPLATESHEETNAME);
	}

	public ArrayList<IndividualResultSheet> getIndividualResultSheets()
	{
		return individualResultSheets;
	}

	public TeamResultSheet getTeamResultSheet()
	{
		return teamResultSheet;
	}

	public WritableWorkbook getWritableWorkbook()
	{
		return writableWorkbook;
	}

	public Workbook getWorkbook()
	{
		return workbook;
	}

	public void setTeamResultSheet()
	{
		this.teamResultSheet = new TeamResultSheet();
	}

	/**
	 * Writes all the sheets (individuals and team)
	 */
	public void write() throws BiffException, IOException, WriteException
	{
		// Iterate over all the individual sheets (and the one team result sheet)
		for (int i = 0; i < individualResultSheets.size(); i++)
		{
			if (i != (individualResultSheets.size() - 1))
			{
				ExcelFileProcessor.copySheet(writableWorkbook, individualResultSheets.get(i).getTemplateSheetName(),
						individualResultSheets.get(i).getName());
			}

			// Iterate over all the different types of result
			for (int j = 0; j < StandingsCreationHelper.TypeOfResult.values().length; j++)
			{
				ExcelFileProcessor.writeIndividualResultSheet(writableWorkbook, individualResultSheets.get(i),
						StandingsCreationHelper.TypeOfResult.values()[j], j);

				// Write the team result sheet as well
				if (i == (individualResultSheets.size() - 1))
				{
					ExcelFileProcessor.writeTeamResultSheet(this, writableWorkbook, teamResultSheet,
							StandingsCreationHelper.TypeOfResult.values()[j]);
				}
			}
		}
	}
}
