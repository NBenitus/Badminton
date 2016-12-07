package pageBreak;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import excelHelper.POIExcelFileProcessor;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import standings.IndividualResultSheet;
import standings.StandingsCreationHelper;
import standings.TeamResultSheet;

public class PageBreakFile
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
	 *            list of individual sheets to set page breaks for
	 * @param teamResultSheet
	 *            team result sheet to set page breaks for
	 */
	public PageBreakFile(File file, ArrayList<IndividualResultSheet> listIndividualResultSheets, TeamResultSheet teamResultSheet)
			throws BiffException, IOException, WriteException
	{
		this.file = file;
		this.listIndividualResultSheets = listIndividualResultSheets;
		this.teamResultSheet = teamResultSheet;
	}

	/**
	 * Writes the page break file by adding page breaks to the sheets
	 */
	public void write() throws IOException
	{
		ArrayList<PageBreak> pageBreaks = new ArrayList<PageBreak>();

		// Using same file as input and output files
		POIExcelFileProcessor.initialize(file, file);

		// Iterate over all the individual result sheets
		for (int i = 0; i < listIndividualResultSheets.size(); i++)
		{
			PageBreak individualResultPageBreak = new PageBreak(listIndividualResultSheets.get(i).getName());

			// Iterate over each type of result
			for (int j = 0; j < StandingsCreationHelper.TypeOfResult.values().length; j++)
			{
				individualResultPageBreak.addColumnPageBreak(IndividualResultSheet.FIRSTCOLUMN
						+ (j * listIndividualResultSheets.get(i).getNumberOfColumnsForStandings() - 1));
				individualResultPageBreak.setRowPageBreaks(null);
			}

			pageBreaks.add(individualResultPageBreak);
		}

		pageBreaks.add(new PageBreak(teamResultSheet.getName(), Collections.singletonList(teamResultSheet.getRowPageBreak()), null));

		POIExcelFileProcessor.addPageBreaks(pageBreaks);

		POIExcelFileProcessor.close();
	}
}
