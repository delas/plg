package plg.gui.util.collections;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Collection of Python scripts
 * 
 * @author Andrea Burattin
 */
public class ScriptsCollection {

	// various scripts
	public static final String TIME_SCRIPT = getFile("/plg/resources/scripts/taskTime.py");
	public static final String STRING_DATA_OBJECT = getFile("/plg/resources/scripts/dataObjectString.py");
	public static final String INTEGER_DATA_OBJECT = getFile("/plg/resources/scripts/dataObjectInteger.py");
	
	/**
	 * This method returns a string representation of the provided resource. The
	 * resource will be open using the {@link Class#getResource(String)} applied
	 * to {@link System} class.
	 * 
	 * @param resourceName the path of the resource
	 * @return the string representation of the provided resource
	 */
	public static String getFile(String resourceName) {
		StringBuilder result = new StringBuilder("");
		File file = new File(System.class.getResource(resourceName).getFile());
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
}
