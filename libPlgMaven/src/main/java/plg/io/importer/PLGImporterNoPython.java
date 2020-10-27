package plg.io.importer;

import plg.annotations.Importer;

@Importer(
	name = "PLG file - Without Python script",
	fileExtension = "plg"
)
public class PLGImporterNoPython extends PLGImporter {

	public PLGImporterNoPython() {
		super.importPythonScript = false;
	}
}