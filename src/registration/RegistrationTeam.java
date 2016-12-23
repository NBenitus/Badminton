package registration;

import java.util.ArrayList;

import standings.Player;

public class RegistrationTeam
{
	ArrayList<Registration> registrations;
	String schoolName;

	/**
	  * Constructor
	  *
	  * @param schoolName
	  *            name of the school that contains the registered players
	  */
	public RegistrationTeam(String schoolName)
	{
		this.schoolName = schoolName;
	}

	/**
	  * Adds a registration object to the list of registrations
	  *
	  * @param registration
	  *            registration object containing the list of single players, double players and category
	  */
	public void addRegistration(Registration registration)
	{
		registrations.add(registration);
	}

	// In progress
	public ArrayList<String> verify()
	{
		ArrayList<String> errors = new ArrayList<String>();

		// Iterate for the two categories that can have overanked players
		for (int i = 0; i < registrations.size() - 1; i++)
		{
			for (int j = 0; j < registrations.get(i).getSinglePlayers().size(); j++)
			{
				Player currentCategoryPlayer = registrations.get(i).getSinglePlayers().get(j);
				ArrayList<Player> nextCategoryPlayers = registrations.get(i + 1).getSinglePlayers();

				if (nextCategoryPlayers.contains(currentCategoryPlayer))
				{

				}
			}
		}

		return errors;
	}
}

