package plg.utils;

/**
 * 
 * @author Boudewijn van Dongen
 * @author Andrea Burattin
 */
public class OSUtils {

	public static final String OS_WIN = "OS_WIN";
	public static final String OS_MACOSX = "OS_MACOSX";
	public static final String OS_MACOSCLASSIC = "OS_MACOSCLASSIC";
	public static final String OS_LINUX = "OS_LINUX";
	public static final String OS_BSD = "OS_BSD";
	public static final String OS_RISCOS = "OS_RISCOS";
	public static final String OS_BEOS = "OS_BEOS";
	public static final String OS_UNKNOWN = "OS_UNKNOWN";

	private static String currentOs = null;

	public static String determineOS() {
		if (currentOs == null) {
			String osString = System.getProperty("os.name").trim().toLowerCase();
			if (osString.startsWith("windows")) {
				currentOs = OS_WIN;
			} else if (osString.startsWith("mac os x")) {
				currentOs = OS_MACOSX;
			} else if (osString.startsWith("mac os")) {
				currentOs = OS_MACOSCLASSIC;
			} else if (osString.startsWith("risc os")) {
				currentOs = OS_RISCOS;
			} else if ((osString.indexOf("linux") >= 0) || (osString.indexOf("debian") >= 0)
					|| (osString.indexOf("redhat") >= 0) || (osString.indexOf("lindows") >= 0)) {
				currentOs = OS_LINUX;
			} else if ((osString.indexOf("freebsd") >= 0) || (osString.indexOf("openbsd") >= 0)
					|| (osString.indexOf("netbsd") >= 0) || (osString.indexOf("irix") >= 0)
					|| (osString.indexOf("solaris") >= 0) || (osString.indexOf("sunos") >= 0)
					|| (osString.indexOf("hp/ux") >= 0) || (osString.indexOf("risc ix") >= 0)
					|| (osString.indexOf("dg/ux") >= 0)) {
				currentOs = OS_BSD;
			} else if (osString.indexOf("beos") >= 0) {
				currentOs = OS_BEOS;
			} else {
				currentOs = OS_UNKNOWN;
			}
		}
		return currentOs;
	}

	public static boolean is64Bit() {
		return System.getProperty("sun.arch.data.model").equals("64");
	}

	public static boolean is32Bit() {
		return System.getProperty("sun.arch.data.model").equals("32");
	}

	public static boolean isRunningWindows() {
		return determineOS() == OS_WIN;
	}

	public static boolean isRunningMacOsX() {
		return determineOS() == OS_MACOSX;
	}

	public static boolean isRunningLinux() {
		return determineOS() == OS_LINUX;
	}

	public static boolean isRunningUnix() {
		String os = determineOS();
		return (os == OS_BSD) || (os == OS_LINUX) || (os == OS_MACOSX);
	}
}
