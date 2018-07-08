package com.urlprocessor.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gauravkahadane
 *
 */
public class WordProcessor {
	/**
	 * Generates wordMap to store the details word and its occurrence in URL
	 * response
	 * 
	 * @param input
	 * @param wordMap stores word and its frequency count in the given input
	 */
	public static void processWords(String input, Map<String, Integer> wordMap) {

		Pattern pattern = Pattern.compile("[a-zA-Z]+");

		Matcher matcher = pattern.matcher(input);

		while (matcher.find()) {
			Integer word = wordMap.get(matcher.group());
			if (word != null) {
				wordMap.put(matcher.group(), ++word);
			} else {
				wordMap.put(matcher.group(), 1);
			}
		}
	}

	/**
	 * Sorts the given map by its values
	 * 
	 * @param unsortMap map with unsorted frequency count
	 * @return sorted map by decreasing order of word's frequency count
	 */
	public static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
}
