package plg.io.exporter;

import plg.model.Process;

/**
 * This interface represents the basic structure of a model exporter.
 * 
 * @author Andrea Burattin
 */
public interface IFileExporter {

	/**
	 * General interface of a method that exports a model.
	 * 
	 * @param model the model to export
	 * @param filename the target of the model to export
	 */
	public void exportModel(Process model, String filename);
}
