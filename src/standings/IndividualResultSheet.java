package standings;

import java.util.ArrayList;

import standings.StandingsCreationHelper.TypeOfPlay;

public class IndividualResultSheet
{
	private String name;
	private String headerName;
	private TypeOfPlay typeOfPlay;

	public static final int FIRSTROWBREAK = 47;
	public static final int SECONDROWBREAK = 84;

	public static final String TEMPLATESHEETNAME = "Classement_Individuel";
	public static final String OUTPUTINDIVIDUALSHEETPREFIX = "Classement_Individuel_";
	public static final String HEADERNAMEPREFIX = "Classement Individuel - ";
	public static final int NUMBERCOLUMNSFORSTANDINGS = 4;

	public static final int FIRSTCOLUMN = 2;
	public static final int FIRSTROW = 13;
	public static final int NUMBEROFROWSTODELETE = 150;
	public static final int HEADERROW = 5;

	private ArrayList<Integer> rowPageBreaks = new ArrayList<Integer>();

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
		this.name = OUTPUTINDIVIDUALSHEETPREFIX + newSheetName;
		this.headerName = HEADERNAMEPREFIX + newSheetName;
		this.typeOfPlay = typeOfPlay;

		rowPageBreaks.add(FIRSTROWBREAK);
		rowPageBreaks.add(SECONDROWBREAK);
	}

	public String getName()
	{
		return name;
	}

	public String getHeaderName()
	{
		return headerName;
	}

	public int getNumberOfColumnsForStandings()
	{
		return NUMBERCOLUMNSFORSTANDINGS;
	}

	public String getTemplateSheetName()
	{
		return TEMPLATESHEETNAME;
	}

	public ArrayList<Integer> getRowPageBreaks()
	{
		return rowPageBreaks;
	}

	public TypeOfPlay getTypeOfPlay()
	{
		return typeOfPlay;
	}
}
