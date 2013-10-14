package plg.exporter;

import plg.model.Process;

public interface AbstractExporter {

	public void exportModel(Process model, String filename);
}
