package utilities;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import standings.StandingsCreationHelper.Category;

public class UtilitiesTest
{
	@Test
	public void testGetFileExtension_xls()
	{
		assertEquals("xls", Utilities.getFileExtension(new File("Test.xls")));
	}

	@Test
	public void testGetFileExtension_xlsx()
	{
		assertEquals("xlsx", Utilities.getFileExtension(new File("Test.xlsx")));
	}

	@Test
	public void testGetFileExtension_doubleExtension()
	{
		assertEquals("xlsx", Utilities.getFileExtension(new File("Test.abc.xlsx")));
	}

	@Test
	public void testGetPreviousCategory_Benjamin()
	{
		assertEquals(null, Utilities.getPreviousCategory(Category.BENJAMIN));
	}

	@Test
	public void testGetPreviousCategory_Cadet()
	{
		assertEquals(Category.BENJAMIN, Utilities.getPreviousCategory(Category.CADET));
	}

	@Test
	public void testGetPreviousCategory_Juvénile()
	{
		assertEquals(Category.CADET, Utilities.getPreviousCategory(Category.JUVÉNILE));
	}

}
