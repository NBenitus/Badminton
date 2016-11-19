package standings;

import java.util.ArrayList;

public class PageBreak
{
	private String sheetName;
	private ArrayList<Integer> columnPageBreaks;
	private ArrayList<Integer> rowPageBreaks;

	public PageBreak(String sheetName, ArrayList<Integer> rowPageBreaks, ArrayList<Integer> columnPageBreaks)
	{
		this.sheetName = sheetName;
		this.rowPageBreaks = rowPageBreaks;
		this.columnPageBreaks = columnPageBreaks;
	}

	public PageBreak(String sheetName, int rowPageBreak, ArrayList<Integer> columnPageBreaks)
	{
		this.sheetName = sheetName;
		rowPageBreaks = new ArrayList<Integer>();
		rowPageBreaks.add(rowPageBreak);
		this.columnPageBreaks = columnPageBreaks;
	}

	public PageBreak(String sheetName)
	{
		this.sheetName = sheetName;
		rowPageBreaks = new ArrayList<Integer>();
		columnPageBreaks = new ArrayList<Integer>();
	}

	public void addColumnPageBreak(int columnPageBreak)
	{
		columnPageBreaks.add(columnPageBreak);
	}

	public void addRowPageBreak(int rowPageBreak)
	{
		rowPageBreaks.add(rowPageBreak);
	}

	public ArrayList<Integer> getColumnPageBreaks()
	{
		return columnPageBreaks;
	}

	public ArrayList<Integer> getRowPageBreaks()
	{
		return rowPageBreaks;
	}

	public String getSheetName()
	{
		return sheetName;
	}

	public void setColumnPageBreaks(ArrayList<Integer> columnPageBreaks)
	{
		this.columnPageBreaks = columnPageBreaks;
	}

	public void setRowPageBreaks(ArrayList<Integer> rowPageBreaks)
	{
		this.rowPageBreaks = rowPageBreaks;
	}
	public void setSheetName(String sheetName)
	{
		this.sheetName = sheetName;
	}

}
