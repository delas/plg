package plg.gui.util;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import plg.annotations.Exporter;
import plg.annotations.Importer;
import plg.io.exporter.IFileExporter;
import plg.io.importer.IFileImporter;
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

	protected static String FILE_HELPER_DESCRIPTION = "%s (*.%s)";
	
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
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(String.format(FILE_HELPER_DESCRIPTION, annotation.name(), annotation.fileExtension()), annotation.fileExtension()));
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
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(String.format(FILE_HELPER_DESCRIPTION, annotation.name(), annotation.fileExtension()), annotation.fileExtension()));
		}
	}
	
	/**
	 * This method fixes the name of the provided file by adding the required
	 * extensions whether it is required
	 * 
	 * @param currentFileName the current file name
	 * @param fileFilter the file filter
	 * @return the fixed file name
	 */
	public static String fixFileName(String currentFileName, FileNameExtensionFilter fileFilter) {
		String extension = fileFilter.getExtensions()[0];
		if (!currentFileName.endsWith("." + extension)) {
			return currentFileName + "." + extension;
		}
		return currentFileName;
	}
	
	/**
	 * This method generates a new instance of a file exporter starting from the
	 * file extension provided
	 * 
	 * @param fileFilter the file extension to consider
	 * @return the file exporter
	 */
	public static IFileExporter getExporterFromFileName(FileNameExtensionFilter fileFilter) {
		for (Class<?> exporter : RegisteredIO.getAllExporters()) {
			Exporter annotation = exporter.getAnnotation(Exporter.class);
			String description = String.format(FILE_HELPER_DESCRIPTION, annotation.name(), annotation.fileExtension());
			if (description.equals(fileFilter.getDescription())) {
				try {
					return (IFileExporter) exporter.newInstance();
				} catch (InstantiationException | IllegalAccessException e) { }
			}
		}
		return null;
	}
	
	/**
	 * This method generates a new instance of a file importer starting from the
	 * file extension provided
	 * 
	 * @param fileFilter the file extension to consider
	 * @return the file importer
	 */
	public static IFileImporter getImporterFromFileName(FileNameExtensionFilter fileFilter) {
		for (Class<?> importer : RegisteredIO.getAllImporters()) {
			Importer annotation = importer.getAnnotation(Importer.class);
			String description = String.format(FILE_HELPER_DESCRIPTION, annotation.name(), annotation.fileExtension());
			if (description.equals(fileFilter.getDescription())) {
				try {
					return (IFileImporter) importer.newInstance();
				} catch (InstantiationException | IllegalAccessException e) { }
			}
		}
		return null;
	}
}
