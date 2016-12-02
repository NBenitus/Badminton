package pageBreak;

import java.util.ArrayList;

public class PageBreak
{
	private String sheetName;
	private ArrayList<Integer> columnPageBreaks;
	private ArrayList<Integer> rowPageBreaks;

	/**
	  * Constructor.
	  *
	  * @param sheetName
	  *            name of the sheet to add page breaks to
	  * @param rowPageBreaks
	  *            list of rows which to add page breaks to
	  * @param columnPageBreaks
	  *            list of columns which to add page breaks to
	  */
	public PageBreak(String sheetName, ArrayList<Integer> rowPageBreaks, ArrayList<Integer> columnPageBreaks)
	{
		this.sheetName = sheetName;
		this.rowPageBreaks = rowPageBreaks;
		this.columnPageBreaks = columnPageBreaks;
	}

	/**
	  * Constructor.
	  *
	  * @param sheetName
	  *            name of the sheet to add page breaks to
	  */
	public PageBreak(String sheetName)
	{
		this.sheetName = sheetName;
		rowPageBreaks = new ArrayList<Integer>();
		columnPageBreaks = new ArrayList<Integer>();
	}

	/**
	  * Adds a column page break to the list of column page breaks
	  *
	  * @param columnPageBreak
	  *            number of the column which to set as a page break
	  */
	public void addColumnPageBreak(int columnPageBreak)
	{
		columnPageBreaks.add(columnPageBreak);
	}

	/**
	  * Adds a row page break to the list of row page breaks
	  *
	  * @param rownPageBreak
	  *            number of the row which to set as a page break
	  */
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
