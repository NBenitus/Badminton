package utilities;

import java.io.*;
import java.time.LocalDate;
import standings.StandingsCreationHelper.Category;

public class Utilities
{
	public enum DatesCutoff {
		BENJAMIN_START(LocalDate.of(2002, 10, 1)), BENJAMIN_END(LocalDate.of(2004, 9, 30)), CADET_START(
				LocalDate.of(2000, 10, 1)), CADET_END(LocalDate.of(2002, 9, 30)), JUVENILE_START(
						LocalDate.of(1998, 7, 1)), JUVENILE_END(LocalDate.of(2000, 9, 30));

		private LocalDate date;

		DatesCutoff(LocalDate date)
		{
			this.date = date;
		}

		public LocalDate date()
		{
			return date;
		}
	}

	public static String getFileExtension(File file)
	{
		String filename = file.getName();
		String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());

		return extension;
	}

	public static Category getPreviousCategory(Category category)
	{
		Category previousCategory = null;

		switch (category)
		{
		case BENJAMIN:
			previousCategory = null;
			break;
		case CADET:
			previousCategory = Category.BENJAMIN;
			break;
		case JUVÉNILE:
			previousCategory = Category.CADET;
			break;
		}

		return previousCategory;
	}
}