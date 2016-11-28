package standings;

import standings.StandingsCreationHelper.TypeOfPlay;

public class IndividualCombinedResultSheet extends IndividualResultSheet
{
	private static final String TEMPLATESHEETNAME = "Classement_Individuel_Combiné";
	public static final int NUMBERCOLUMNSFORSTANDINGS = 6;

	/**
	  * Constructor.
	  *
	  * @param standingsExcelFile
	  *            standings file where to write the individual results
	  * @param typeOfPlay
	  *            type of play (either single or double)
	  */
	public IndividualCombinedResultSheet (StandingsFile standingsExcelFile, TypeOfPlay typeOfPlay)
	{
		super(standingsExcelFile, typeOfPlay.text(), typeOfPlay);
	}

	public String getTemplateSheetName()
	{
		return TEMPLATESHEETNAME;
	}

	public int getNumberOfColumnsForStandings()
	{
		return NUMBERCOLUMNSFORSTANDINGS;
	}
}
