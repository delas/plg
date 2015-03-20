package plg.gui.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class is useful to convert milliseconds into a human readable time
 * format
 * 
 * @author Karaszi István
 * @author Andrea Burattin (minor contributions)
 * @see {@link https://github.com/raszi/java-progressbar/}
 */
public class HumanTimeFormatter {
	
	private static final List<DisplayUnit> DISPLAY_UNITS = new LinkedList<HumanTimeFormatter.DisplayUnit>();
	static {
		DISPLAY_UNITS.add(DisplayUnit.of(TimeUnit.DAYS, " days"));
		DISPLAY_UNITS.add(DisplayUnit.of(TimeUnit.HOURS, " hours"));
		DISPLAY_UNITS.add(DisplayUnit.of(TimeUnit.MINUTES, " minutes"));
		DISPLAY_UNITS.add(DisplayUnit.of(TimeUnit.SECONDS, " seconds"));
	}

	/**
	 * 
	 * @param milliseconds
	 * @return
	 */
	public static String formatTime(final long milliseconds) {
		long diff = milliseconds;
		final StringBuilder sb = new StringBuilder();
		final Iterator<DisplayUnit> iterator = DISPLAY_UNITS.iterator();
		while (iterator.hasNext()) {
			final DisplayUnit displayUnit = iterator.next();
			final long value = displayUnit.getFromMilliseconds(diff);
			if (value != 0 || (!iterator.hasNext() && sb.length() == 0)) {
				sb.append(displayUnit.getWithSuffix(value));
				sb.append(" and ");
				diff -= displayUnit.getInMilliseconds(value);
			}
		}
		String toRet = sb.toString();
		return toRet.substring(0, toRet.length() - 5);
	}

	private static final class DisplayUnit {
		private final TimeUnit timeUnit;
		private final String suffix;

		private DisplayUnit(final TimeUnit timeUnit, final String suffix) {
			this.timeUnit = timeUnit;
			this.suffix = suffix;
		}

		public static DisplayUnit of(final TimeUnit timeUnit, final String suffix) {
			return new DisplayUnit(timeUnit, suffix);
		}

		public long getFromMilliseconds(final long milliseconds) {
			return timeUnit.convert(milliseconds, TimeUnit.MILLISECONDS);
		}

		public long getInMilliseconds(final long value) {
			return TimeUnit.MILLISECONDS.convert(value, timeUnit);
		}

		public String getWithSuffix(final long value) {
			return String.format("%d%s", value, suffix);
		}
	}
}
