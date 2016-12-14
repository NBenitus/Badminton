package standings;

import standings.StandingsCreationHelper.TypeOfPlay;

public class IndividualResultSheet
{
	private String name;
	private String headerName;
	private TypeOfPlay typeOfPlay;

	public static final int[] ROW_BREAKS = {47, 84};

	public static final String TEMPLATE_SHEET_NAME = "Classement_Individuel";
	public static final String HEADER_NAME_PREFIX = "Classement Individuel - ";
	public static final int NUMBER_COLUMNS = 4;

	public static final int FIRST_COLUMN = 2;
	public static final int FIRST_ROW = 13;
	public static final int NUMBER_OF_ROWS_TO_DELETE = 150;
	public static final int HEADER_ROW = 5;

	/**
	  * Constructor.
	  *
	  * @param standingsFile
	  *            standings file where to write the individual results
	  * @param newSheetName
	  *            name of the output sheet
	  * @param typeOfPlay
	  *            type of play (either single or double)
	  */
	public IndividualResultSheet(StandingsFile standingsFile, String newSheetName, TypeOfPlay typeOfPlay)
	{
		this.name = TEMPLATE_SHEET_NAME + "_" + newSheetName;
		this.headerName = HEADER_NAME_PREFIX + newSheetName;
		this.typeOfPlay = typeOfPlay;
	}

	public int getFirstColumn()
	{
		return FIRST_COLUMN;
	}

	public String getName()
	{
		return name;
	}

	public String getHeaderName()
	{
		return headerName;
	}

	public int getNumberOfColumns()
	{
		return NUMBER_COLUMNS;
	}

	public String getTemplateSheetName()
	{
		return TEMPLATE_SHEET_NAME;
	}

	public TypeOfPlay getTypeOfPlay()
	{
		return typeOfPlay;
	}
}
