package standings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import excelHelper.POIExcelFileProcessor;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

public class POIExcelFile
{
	File file;
	private ArrayList<IndividualResultSheet> listIndividualResultSheets = new ArrayList<IndividualResultSheet>();
	private TeamResultSheet teamResultSheet;

	/**
	 * Constructor.
	 *
	 * @param file
	 *            excel file to be modified
	 * @param individualResultSheets
	 *            list of individual sheets in the excel file
	 */
	public POIExcelFile(File file, ArrayList<IndividualResultSheet> listIndividualResultSheets, TeamResultSheet teamResultSheet)
			throws BiffException, IOException, WriteException
	{
		this.file = file;
		this.listIndividualResultSheets = listIndividualResultSheets;
		this.teamResultSheet = teamResultSheet;
	}

	/**
	 * Add column page breaks to each individual result sheet
	 *
	 */
	public void addPageBreaks() throws IOException
	{
		POIExcelFileProcessor.initializeFile(file);
		ArrayList<PageBreak> pageBreaks = new ArrayList<PageBreak>();

		// Iterate over all the individual result sheets
		for (int i = 0; i < listIndividualResultSheets.size(); i++)
		{
			PageBreak individualResultPageBreak = new PageBreak(listIndividualResultSheets.get(i).getName());

			// Iterate over each type of result
			for (int j = 0; j < StandingsCreationHelper.TypeOfResult.values().length; j++)
			{
				individualResultPageBreak.addColumnPageBreak(
						StandingsCreationHelper.TypeOfResult.values()[j].individualResultSheetStartColumn() - 1);
//				individualResultPageBreak.setRowPageBreaks(listIndividualResultSheets.get(i).getRowPageBreaks());
				individualResultPageBreak.setRowPageBreaks(null);
			}

			pageBreaks.add(individualResultPageBreak);
		}

		pageBreaks.add(new PageBreak(teamResultSheet.getName(), teamResultSheet.getRowPageBreak(), null));

		POIExcelFileProcessor.addPageBreaks(pageBreaks);

		// Close and write the file
		POIExcelFileProcessor.closeFile();
	}

	// public void setBorderToCells(int lastRow) throws IOException
	// {
	// POIExcelFileProcessor.initializeFile(file);
	//
	// POIExcelFileProcessor.setBorderToCells(jExcelFile.getTeamResulSheets().getName(), jExcelFile.getLastRow());
	//
	// POIExcelFileProcessor.closeFile();
	// }
}
