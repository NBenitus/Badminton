package standings;


public class TeamResultSheet
{
	private String name;

	public int rowPageBreak;

	public static final String TEMPLATETEAMSHEETPREFIX = "Moyenne par ecole PDF";
	public static final String OUTPUTTEAMSHEETPREFIX = "Classement_par_équipes";

	public static final int FIRSTROW = 6;
	public static final int FIRSTCOLUMN = 0;
	public static final int NUMBEROFROWSBETWEENTYPESOFRESULTS = 3;
	public static final int NUMBEROFTOURNAMENTS = 5;
	public static final int NUMBEROFCOLUMNSTOTAL = 8;

	/**
	  * Constructor.
	  *
	  * @param excelFile
	  *            JExcel File object that is the parent of the team result sheet
	  */
	public TeamResultSheet()
	{
		name = OUTPUTTEAMSHEETPREFIX;
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
