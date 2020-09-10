package plg.io.importer;

import plg.generator.IProgressVisualizer;
import plg.model.Process;

/**
 * This interface represents the basic structure of a model importer.
 * 
 * @author Andrea Burattin
 */
public interface IFileImporter {

	/**
	 * General interface of a method that imports a model.
	 * 
	 * @param filename the source of the model to import
	 * @param progress the progress to notify the user
	 * @return the imported model
	 */
	public Process importModel(String filename, IProgressVisualizer progress);
	
	/**
	 * General interface of a method that imports a model without any progress.
	 * 
	 * @param filename the source of the model to import
	 * @return the imported model
	 */
	public Process importModel(String filename);
}
