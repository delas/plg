package plg.gui.util;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import plg.annotations.Exporter;
import plg.annotations.Importer;
import plg.utils.RegisteredIO;

/**
 * This class contains utility methods to assign proper file filters, according
 * to the available {@link Importer}s and {@link Exporter}s.
 * 
 * <p>
 * This class uses the methods in {@link RegisteredIO} in order to retrieve the
 * available importers and exporters.
 * 
 * @author Andrea Burattin
 */
public class FileFilterHelper {

	/**
	 * This class assigns, to the provided file chooser, the {@link FileFilter}s
	 * according to the available {@link Importer}s.
	 * 
	 * @param fileChooser the file chooser that will receive the file filters
	 */
	public static void assignImportFileFilters(JFileChooser fileChooser) {
		fileChooser.setAcceptAllFileFilterUsed(false);
		for (Class<?> importer : RegisteredIO.getAllImporters()) {
			final Importer annotation = importer.getAnnotation(Importer.class);
			fileChooser.addChoosableFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					return annotation.name() + " (*." + annotation.fileExtension() + ")";
				}
				
				@Override
				public boolean accept(File f) {
					String extension = annotation.fileExtension();
					String fileName = f.getName();
					return fileName.endsWith(extension);
				}
			});
		}
	}
	
	/**
	 * This class assigns, to the provided file chooser, the {@link FileFilter}s
	 * according to the available {@link Exporter}s.
	 * 
	 * @param fileChooser the file chooser that will receive the file filters
	 */
	public static void assignExportFileFilters(JFileChooser fileChooser) {
		fileChooser.setAcceptAllFileFilterUsed(false);
		for (Class<?> exporter : RegisteredIO.getAllExporters()) {
			final Exporter annotation = exporter.getAnnotation(Exporter.class);
			fileChooser.addChoosableFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					return annotation.name() + " (*." + annotation.fileExtension() + ")";
				}
				
				@Override
				public boolean accept(File f) {
					String extension = annotation.fileExtension();
					String fileName = f.getName();
					return fileName.endsWith(extension);
				}
			});
		}
	}
}
