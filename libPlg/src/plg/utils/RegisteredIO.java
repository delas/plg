package plg.utils;

import java.util.Set;

import org.reflections.Reflections;

import plg.annotations.Exporter;
import plg.annotations.Importer;

/**
 * This class contains utility methods to retrieve all the registered file
 * importer and exporters.
 * 
 * @author Andrea Burattin
 */
public class RegisteredIO {

	/**
	 * This method returns all the registered importers (i.e., classes annotated
	 * as {@link Importer})
	 * 
	 * @return a set of importers
	 */
	public static Set<Class<?>> getAllImporters() {
		Reflections reflections = new Reflections("plg");
		return reflections.getTypesAnnotatedWith(Importer.class);
	}
	
	/**
	 * This method returns all the registered exporters (i.e., classes annotated
	 * as {@link Exporter})
	 * 
	 * @return a set of exporters
	 */
	public static Set<Class<?>> getAllExporters() {
		Reflections reflections = new Reflections("plg");
		Set<Class<?>> exporters = reflections.getTypesAnnotatedWith(Exporter.class);
		return exporters;
	}
}
