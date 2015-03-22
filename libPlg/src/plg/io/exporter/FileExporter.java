package plg.io.exporter;

import plg.generator.ProgressAdapter;
import plg.model.Process;

/**
 * This abstract class implements the model export with no notification
 *  
 * @author Andrea Burattin
 */
public abstract class FileExporter implements IFileExporter {

	@Override
	public void exportModel(Process model, String filename) {
		exportModel(model, filename, new ProgressAdapter());
	}
}
