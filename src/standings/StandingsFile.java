package standings;

import java.io.File;
import java.io.IOException;
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
	 * @param templateFile
	 *            file that contains the templates for the team and individual result sheets
	 * @param outputFile
	 *            file that will contain the created team and individual results
	 */
	public StandingsFile(File templateFile, File outputFile)
			throws BiffException, IOException, WriteException
	{
		this.workbook = Workbook.getWorkbook(templateFile);
		this.writableWorkbook = Workbook.createWorkbook(outputFile, workbook);
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
	public void writeAllSheets() throws BiffException, IOException, WriteException
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
