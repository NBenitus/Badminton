package standings;

public class TeamResultSheet extends ResultSheet
{
	public static final String TEMPLATE_SHEET_NAME = "Classement_Équipes";

	public static final int FIRST_ROW = 6;
	public static final int FIRST_COLUMN = 0;
	public static final int NUMBER_ROWS_BETWEEN_TYPES_OF_RESULTS = 3;
	public static final int NUMBER_TOURNAMENTS = 5;
	public static final int NUMBER_COLUMNS = 8;

	public int getFirstColumn()
	{
		return FIRST_COLUMN;
	}

	public int getNumberOfColumns()
	{
		return NUMBER_COLUMNS;
	}

	public String getTemplateSheetName()
	{
		return TEMPLATE_SHEET_NAME;
	}
}
