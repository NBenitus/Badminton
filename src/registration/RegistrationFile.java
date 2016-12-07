package registration;

import java.io.File;
import java.util.ArrayList;

import database.PostgreSQLJDBC;
import excelHelper.ExcelFileReader;
import standings.StandingsCreationHelper;
import standings.StandingsCreationHelper.Category;
import utilities.Utilities;

public class RegistrationFile
{
//	 In progress
//	public static void main(String[] args) throws Exception
//	{
////		File file = new File("C:\\Benoit\\Work\\Java\\Badminton\\FormulairesEntrees_Tournoi3.xls");
//		String directoryPath = "C:\\Benoit\\Work\\Java\\Badminton\\Registration\\";
//		ArrayList<Registration> registrations = new ArrayList<Registration>();
//
//		File dir = new File(directoryPath);
//		  File[] directoryListing = dir.listFiles();
//		  if (directoryListing != null) {
//		    for (File child : directoryListing) {
//		    	if (Utilities.getFileExtension(child).equals("xls"))
//		    	{
//		    		for (int i = 0; i < Category.values().length; i++)
//		    		{
//		    			registrations.add(ExcelFileReader.readListPlayersFromForms(child, Category.values()[i].text()));
//		    			PostgreSQLJDBC.addRegistrationSingles(registrations.get(i).getSinglePlayers());
//		    		}
//		    	}
//		    }
//		  } else {
//		    // Handle the case where dir is not really a directory.
//		    // Checking dir.isDirectory() above would not be sufficient
//		    // to avoid race conditions with another process that deletes
//		    // directories.
//		  }
//
//		System.out.println("Finished");
//	}

	public RegistrationFile(File file)
	{

	}
}
