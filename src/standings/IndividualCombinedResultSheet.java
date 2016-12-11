package standings;

import standings.StandingsCreationHelper.TypeOfPlay;

public class IndividualCombinedResultSheet extends IndividualResultSheet
{
	private static final String TEMPLATE_SHEET_NAME = "Classement_Individuel_Combiné";
	public static final int NUMBER_OF_COLUMNS = 6;

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
		return TEMPLATE_SHEET_NAME;
	}

	public int getNumberOfColumns()
	{
		return NUMBER_OF_COLUMNS;
	}
}
