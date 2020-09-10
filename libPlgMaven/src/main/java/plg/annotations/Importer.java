package plg.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to identify all the available process importers
 * 
 * @author Andrea Burattin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Importer {

	/**
	 * The name of the importer
	 * 
	 * @return
	 */
	String name();

	/**
	 * The default file extension of this importer
	 * 
	 * @return
	 */
	String fileExtension();
}
