package plg.io.importer;

import plg.generator.ProgressAdapter;
import plg.model.Process;

/**
 * This abstract class implements the model import with no notification
 *  
 * @author Andrea Burattin
 */
public abstract class FileImporter implements IFileImporter {

	@Override
	public Process importModel(String filename) {
		return importModel(filename, new ProgressAdapter());
	}
}
