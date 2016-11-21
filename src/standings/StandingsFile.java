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
	private String individualResultTemplateSheetName;

	private ArrayList<IndividualResultSheet> individualResultSheets = new ArrayList<IndividualResultSheet>();
	private TeamResultSheet teamResultSheet;

	/**
	 * Constructor.
	 *
	 * @param templateFile
	 *            file that contains the templates for the team and individual result sheets
	 * @param outputFile
	 *            file that will contain the created team and individual results
	 * @param individualResultTemplateSheetName
	 *            name of the template sheet for the individual results
	 */
	public StandingsFile(File templateFile, File outputFile, String individualResultTemplateSheetName)
			throws BiffException, IOException, WriteException
	{
		this.workbook = Workbook.getWorkbook(templateFile);
		this.writableWorkbook = Workbook.createWorkbook(outputFile, workbook);
		this.individualResultTemplateSheetName = individualResultTemplateSheetName;
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
		individualResultSheets
				.add(new IndividualResultSheet(this, individualResultTemplateSheetName, sheetName, typeOfPlay));
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

	public void setTemplateSheet(String templateSheetName)
	{
		this.individualResultTemplateSheetName = templateSheetName;
	}

	/**
	 * Writes all the sheets (individuals and team)
	 */
	public void writeAllSheets() throws BiffException, IOException, WriteException
	{
		// Iterate over all the individual sheets (and the one team result sheet)
		for (int i = 0; i < individualResultSheets.size(); i++)
		{
			ExcelFileProcessor.copySheet(writableWorkbook, individualResultTemplateSheetName,
					individualResultSheets.get(i).getName());
			// Iterate over all the different types of result
			for (int j = 0; j < StandingsCreationHelper.TypeOfResult.values().length; j++)
			{
				ExcelFileProcessor.writeIndividualResultSheet(writableWorkbook, individualResultSheets.get(i),
						StandingsCreationHelper.TypeOfResult.values()[j]);

				// Write the team result sheet as well
				if (i == (individualResultSheets.size() - 1))
				{
					// Copy the team result sheet once, after all individual sheets have been copied
					if (j == 0)
					{
						ExcelFileProcessor.copySheet(writableWorkbook, TeamResultSheet.TEMPLATETEAMSHEETPREFIX,
								TeamResultSheet.OUTPUTTEAMSHEETPREFIX);
					}

					ExcelFileProcessor.writeTeamResultSheet(this, writableWorkbook, teamResultSheet,
							StandingsCreationHelper.TypeOfResult.values()[j]);
				}
			}
		}
	}
}
