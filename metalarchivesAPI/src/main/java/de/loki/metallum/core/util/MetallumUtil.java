package de.loki.metallum.core.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

public final class MetallumUtil {
	private final static String		lineSeperator	= System.getProperty("line.separator");
	private final static String[]	daySuffixes		=
													// 0 1 2 3 4 5 6 7 8 9
													{ "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
													// 10 11 12 13 14 15 16 17 18 19
			"th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
			// 20 21 22 23 24 25 26 27 28 29
			"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
			// 30 31
			"th", "st"								};
	private static Logger			logger			= Logger.getLogger(MetallumUtil.class);

	private MetallumUtil() {

	}

	public final static String getDayOfMonthSuffix(final int day) {
		return daySuffixes[day];
	}

	public final static Date getMetallumDate(final String possibleDate) {
		final String day = possibleDate.replaceAll("\\w+?\\s", "").replaceAll("\\w{2},\\s\\d+", "");
		final int dayLength = day.length();
		SimpleDateFormat sdf;
		if (dayLength > 2) {
			// April 1995
			sdf = new SimpleDateFormat("MMMMM yyyy");
		} else {
			sdf = new SimpleDateFormat("MMMMM " + (day.length() > 1 ? "dd" : "d") + "'" + MetallumUtil.getDayOfMonthSuffix(Integer.parseInt(day)) + ",' " + "yyyy", Locale.ENGLISH);
		}
		try {
			return sdf.parse(possibleDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public final static String removeComments(final String html) {
		final Document doc = Jsoup.parse(html);
		removeComments(doc);
		return doc.text();
	}

	private final static void removeComments(final Node node) {
		for (int i = 0; i < node.childNodes().size();) {
			Node child = node.childNode(i);
			if (child.nodeName().equals("#comment")) {
				child.remove();
			} else {
				removeComments(child);
				i++;
			}
		}
	}

	/**
	 * This method exists because {@link String.trim()} does not remove all whitespaces.
	 * So called No Break-Spaces ({@code &nbsp;})!<br>
	 * Removes all leading and trailing \\u00A0.<br>
	 * 
	 * @see http://www.fileformat.info/info/unicode/char/202f/index.htm<br>
	 * @see http://www.vineetmanohar.com/2009/06/how-to-trim-no-break-space-when-parsing-html/<br>
	 * 
	 * 
	 * @param stringWithWhiteSpaces with \\u00A0 Characters
	 * @return the clean String
	 */
	public static String trimNoBreakSpaces(final String stringWithWhiteSpaces) {
		// (\\.|!|\\?)+(\\s|\\z)
		// final String[] words = stringWithWhiteSpaces.split("\\S");
		// for (int i = 0; i < words.length; i++) {
		// if (!words[i].isEmpty()) {
		//
		// String word = words[i];
		// word = word.replaceAll("[\\s\\u00A0]+$|^[\\s\\u00A0]+", "");
		// System.out.println(word);
		// }
		// }
		return stringWithWhiteSpaces.replaceAll("[\\s\\u00A0]+$|^[\\s\\u00A0]+", "");
	}

	/**
	 * 
	 * @param html the HTML String with tags
	 * @return a HTML clean String (parsed), but with line separators
	 */
	public static String parseHtmlWithLineSeperators(final String html) {
		final StringBuffer strBuf = new StringBuffer();
		String cleanHtml = Jsoup.parse(html.replaceAll("(?i)<br[^>]*>", "br2n")).text();
		for (final String strPart : cleanHtml.split("br2n")) {
			strBuf.append(strPart.trim());
			strBuf.append(lineSeperator);
		}
		cleanHtml = strBuf.substring(0, strBuf.length() - 1);
		return strBuf.toString().trim();
	}

	public static boolean isStringInArray(final String control, final String... test) {
		for (int i = 0; i < test.length; i++) {
			if (test[i].equalsIgnoreCase(control)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isEncyclopediaMetallumOnline() {
		try {
			URL u = new URL("http://www.metal-archives.com/");
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();
			int code = huc.getResponseCode();
			if (code == 200) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			logger.error(e);
		}
		return false;
	}
	// :(( not possible because we cannot make a generic Array see
	// http://stackoverflow.com/questions/2927391/whats-the-reason-i-cant-create-generic-array-types-in-java
	// private static <E> E[] asArray(List<E> list) {
	// E[] array = new E[list.size()];
	// return list.toArray(array);
	// }

}
