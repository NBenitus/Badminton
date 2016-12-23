package standings;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import compare.CompareFileTest;
import standings.StandingsCreationHelper.Category;
import standings.StandingsCreationHelper.Gender;

public class S1FileTest
{
	private static final String TEST_FILENAME = "/resources/Test/S1FileTest.xls";

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testRead() throws IOException
	{
		File file = folder.newFile("myfile.txt");

		Player mockPlayer;
		ArrayList<Player> mockPlayers = new ArrayList<Player>();
		ArrayList<Player> players = new ArrayList<Player>();

		mockPlayer = new Player("POOD67100407", "Dead Pool", "Harvard", Gender.MASCULIN, Category.BENJAMIN);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("WOMW67100202", "Wonder Woman", "Yale", Gender.FÉMININ, Category.BENJAMIN);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("WIDB84120103", "Black Widow", "MIT", Gender.FÉMININ, Category.CADET);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("MANSJ81090109", "Spider Man", "Columbia", Gender.MASCULIN, Category.CADET);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("AMECY92570003", "Captain America", "Princeton", Gender.MASCULIN, Category.JUVÉNILE);
		mockPlayers.add(mockPlayer);

		mockPlayer = new Player("WOMC67530106", "Cat Woman", "Sorbonne", Gender.FÉMININ, Category.JUVÉNILE);
		mockPlayers.add(mockPlayer);

		InputStream inputStream = S1FileTest.class.getResourceAsStream(TEST_FILENAME);
		OutputStream outputStream = new FileOutputStream(file);
		IOUtils.copy(inputStream, outputStream);
		outputStream.close();

		S1File s1File = new S1File(file);
		players = s1File.read();

		assertTrue(players.equals(mockPlayers));
	}

}
