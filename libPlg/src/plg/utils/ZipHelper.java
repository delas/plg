package plg.utils;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * This class collects some methods to deal with Zip files.
 * 
 * @author Andrea Burattin
 */
public class ZipHelper {

	/**
	 * This method checks whether the provided file contains a Zip archive.
	 * 
	 * @param file the file to check
	 * @return <tt>true</tt> if the provided file is a Zip archive,
	 * <tt>false</tt> otherwise
	 */
	public static boolean isValid(final File file) {
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(file);
			return true;
		} catch (ZipException e) {
			return false;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (zipfile != null) {
					zipfile.close();
					zipfile = null;
				}
			} catch (IOException e) {
			}
		}
	}
}
