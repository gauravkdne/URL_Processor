package com.urlprocessor.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author gauravkahadane Contains application utility methods
 */
public class AppUtils {
	private static final Logger LOGGER = LogManager.getLogger(AppUtils.class);

	private static final String FILENAME = "url_statistics.txt";

	public static void writeToFile(Map<String, Integer> wordMap) {
		writeToFile(wordMap, null);
	}

	public static void writeToFile(Map<String, Integer> wordMap, String title) {
		try {

			FileWriter fw = new FileWriter(FILENAME, true);

			if (title != null) {
				fw.append("\n\t\t### " + title + " ###");
			}
			fw.append("\n\n# Total number of words: " + wordMap.size());

			fw.append("\n# Top 10 prevalent words: \n");

			int ctr = 10;
			wordMap = WordProcessor.sortByValue(wordMap);
			for (Map.Entry<String, Integer> entry : wordMap.entrySet()) {
				fw.append("\n\t" + entry.getKey() + ": " + entry.getValue());
				ctr--;
				if (ctr == 0)
					break;
			}
			fw.close();

		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	public static void writeToFile(Stack<StatisticInfo> statistics) {
		writeToFile(statistics, null);
	}

	public static void writeToFile(Stack<StatisticInfo> statistics, String title) {
		try {
			FileWriter fw = new FileWriter(FILENAME, true);

			if (title != null) {
				fw.append("\n\t\t### " + title + " ###");
			}

			int ctr = 0;
			fw.append("\n\n\n# URL Processing Statistics #");
			while (!statistics.isEmpty()) {
				StatisticInfo stat = statistics.pop();
				ctr++;
				fw.append("\n\n" + ctr + ".\tURL: " + stat.getUrl());
				fw.append("\n\tThread: " + stat.getThreadName());
				fw.append("\n\tCPU Processing Time: " + stat.getProcessingTime() + " ns");
				fw.append("\n\tWord Count: " + stat.getWordCount());
			}
			fw.append("\n\n# End of URL Processing Statistics #\n\n\n");

			fw.close();

		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	public static <K, V> void printMap(Map<K, V> map) {
		int ctr = 10;
		for (Map.Entry<K, V> entry : map.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			ctr--;
			if (ctr == 0)
				break;
		}
	}

	public static String getTime() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSSSSSS");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static boolean isValidUrl(String url) {
		String[] schemes = { "http", "https" };
		UrlValidator urlValidator = new UrlValidator(schemes);
		return urlValidator.isValid(url);
	}
}
