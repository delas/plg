package plg.utils;

/**
 * This class contains utilities method to handle CPU-related information
 * 
 * @author Andrea Burattin
 */
public class CPUUtils {

	/**
	 * This method determines the optimal number of cores to use for the
	 * simulation
	 * 
	 * @return the optimal number of CPU cores available
	 */
	public static int CPUAvailable() {
		int CPUAvailables = Runtime.getRuntime().availableProcessors();
		if (CPUAvailables > 2) {
			return CPUAvailables - 2;
		}
		return 1;
	}
}
