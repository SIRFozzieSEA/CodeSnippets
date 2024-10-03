package com.codef.codesnippets;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringQuoteParser {

	public static void main(String[] args) {
		String csvLine = "value1,\"value, inside quotes\",value3";
		List<String> values = parseLine(csvLine);
		System.out.println(values); // Output: [value1, value, inside quotes, value3]
	}

	public static List<String> parseLine(String csvLine) {
		List<String> result = new ArrayList<>();
		String regex = "\"([^\"]*)\"|([^,]+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(csvLine);

		while (matcher.find()) {
			if (matcher.group(1) != null) {
				result.add("*" + matcher.group(1)); // Quoted value
			} else {
				result.add("*" + matcher.group(2)); // Regular value
			}
		}

		return result;
	}

}
