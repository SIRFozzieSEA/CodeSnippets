package com.codef.codesnippets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class BambuLogProcessor {

	public static void main(String[] args) {

		String filePath = "E://printhistory.txt";

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;

			int runningTotal = 0;

			ArrayList<String> logEntries = new ArrayList<>();

			while ((line = reader.readLine()) != null) {

				if (!line.equals("Gcode")) {
					logEntries.add(line);
				} else {

					if (logEntries.size() > 0 && logEntries.get(logEntries.size() - 1).equals("Success")) {

						int currentMinutes = decimalHoursToMinutes(logEntries.get(1));

						System.out.println(logEntries.get(0) + ", " + logEntries.get(1) + ", " + currentMinutes + ", "
								+ logEntries.get(5)); // Print
														// the
														// first
						runningTotal = runningTotal + currentMinutes; // line
						// (file
						// name)

					}
					logEntries = new ArrayList<>();
				}

			}
						
			System.out.println("\nTotal minutes: " + runningTotal);
			System.out.println("  Total hours: " + runningTotal/60);
			
		} catch (IOException e) {
			System.err.println("Error reading the file: " + e.getMessage());
			e.printStackTrace();
		}

	}

	public static int decimalHoursToMinutes(String timeString) {

		if (timeString.endsWith("min")) {
			return Integer.parseInt(timeString.replaceAll("min", ""));
		} else {
			String decimalPart = timeString.replaceAll("h", "");
			return (int) Math.round(Double.parseDouble(decimalPart) * 60);
		}

	}

}
