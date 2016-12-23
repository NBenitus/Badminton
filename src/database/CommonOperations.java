package database;

import static com.ninja_squad.dbsetup.Operations.*;

import com.ninja_squad.dbsetup.operation.Operation;

public class CommonOperations
{
	public static final Operation DELETE_ALL = deleteAllFrom("Result", "Player", "School");

	public static final Operation INSERT_REFERENCE_DATA = sequenceOf(
			insertInto("School").columns("Name").values("Harvard").values("Yale").values("MIT").values("Columbia")
					.values("Princeton").values("Sorbonne").build(),
			insertInto("Player").columns("ID", "Name", "SchoolName", "Gender", "Category")
					.values("POOD67100407", "Dead Pool", "Harvard", "Masculin", "Benjamin")
					.values("WOMW67100202", "Wonder Woman", "Yale", "Féminin", "Benjamin")
					.values("WIDB84120103", "Black Widow", "MIT", "Féminin", "Cadet")
					.values("MANSJ81090109", "Spider Man", "Columbia", "Masculin", "Cadet")
					.values("AMECY92570003", "Captain America", "Princeton", "Masculin", "Juvénile")
					.values("WOMC67530106", "Cat Woman", "Sorbonne", "Féminin", "Juvénile").build());
}
