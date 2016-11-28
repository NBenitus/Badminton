package standings;


public class TeamResultSheet
{
	private String name;

	public int rowPageBreak;

	public static final String TEMPLATETEAMSHEETPREFIX = "Classement_Équipes";

	public static final int FIRSTROW = 6;
	public static final int FIRSTCOLUMN = 0;
	public static final int NUMBEROFROWSBETWEENTYPESOFRESULTS = 3;
	public static final int NUMBEROFTOURNAMENTS = 5;
	public static final int NUMBEROFCOLUMNSTOTAL = 8;

	/**
	  * Constructor.
	  */
	public TeamResultSheet()
	{
		name = TEMPLATETEAMSHEETPREFIX;
	}

	public String getName()
	{
		return name;
	}

	public int getRowPageBreak()
	{
		return rowPageBreak;
	}

	public void setRowPageBreak(int row)
	{
		rowPageBreak = row;
	}
}
