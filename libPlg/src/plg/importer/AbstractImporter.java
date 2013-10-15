package plg.importer;

import plg.model.Process;

public interface AbstractImporter {

	public Process importModel(String filename);
}
