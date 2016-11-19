package utilities;

import java.io.*;
import java.time.LocalDate;
import java.util.Date;

import excelHelper.ExcelFileReader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import standings.StandingsCreationHelper.Category;

public class Utilities
{
	public static final LocalDate BENJAMIN_START = LocalDate.of(2002, 10, 1);
	public static final LocalDate BENJAMIN_END = LocalDate.of(2004, 9, 30);
	public static final LocalDate CADET_START = LocalDate.of(2000, 10, 1);
	public static final LocalDate CADET_END = LocalDate.of(2002, 9, 30);
	public static final LocalDate JUVÉNILE_START = LocalDate.of(1998, 7, 1);
	public static final LocalDate JUVÉNILE_END = LocalDate.of(2000, 9, 30);

	public static String getFileExtension(File file)
	{
		String filename = file.getName();
		String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());

		return extension;
	}

	public static Category getCategoryFromDateOfBirth(String dateString)
	{
		int firstSlash = dateString.indexOf("/");
		int secondSlash = dateString.lastIndexOf("/");

		String day = dateString.substring(firstSlash + 1, secondSlash);
		String month = dateString.substring(0, firstSlash);
		String year = "20" + dateString.substring(secondSlash + 1);
		String categoryString = new String();

		LocalDate compareDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

		if (!(compareDate.isBefore(BENJAMIN_START) || compareDate.isAfter(BENJAMIN_END)))
		{
			categoryString = Category.BENJAMIN.text();
		}
		else if (!(compareDate.isBefore(CADET_START) || compareDate.isAfter(CADET_END)))
		{
			categoryString = Category.CADET.text();
		}
		else if (!(compareDate.isBefore(JUVÉNILE_START) || compareDate.isAfter(JUVÉNILE_END)))
		{
			categoryString = Category.JUVÉNILE.text();
		}

		return Category.valueOf(categoryString);
	}

	public static String getSchoolNameFromS1File(File file)
	{
		int lastSlash = file.getAbsolutePath().lastIndexOf("\\");
		int lastDOT = file.getAbsolutePath().lastIndexOf(".");

		return file.getAbsolutePath().substring(lastSlash + 1, lastDOT - 1);
	}

	public static String getSchoolNameFromForm(String string)
	{
		int colon = string.indexOf(" ");

		return string.substring(colon + 2);

	}
}