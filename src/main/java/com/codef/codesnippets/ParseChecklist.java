package com.codef.codesnippets;

import java.io.IOException;

public class ParseChecklist {

	private static final boolean enableMainMethod = false;

	public static String startXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><checklist>";
	public static String endXml = "</checklist>";

	public static String startArea = "<areas area=\"XXX1\">";
	public static String endArea = "</areas>";

	public static String entryArea = "<area><name>XXX2</name><condition>XXX3</condition><reference/><cautions/></area>";

	public static String inFileName = "C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\Robinson22Checklist.txt";
	public static String outFileName = "C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\Robinson22Checklist.xml";

	public static void main(String[] args) throws IOException {

		if (enableMainMethod) {

			boolean firstIteration = true;
			StringBuilder sb = new StringBuilder(startXml);
			String[] entryLines = MiscUtilities.readFile(inFileName).split("\n");

			for (int i = 0; i < entryLines.length; i++) {
				String entry = entryLines[i].trim();

				if (entry.contains("\t")) {
					String[] entries = entry.split("\t");
					sb.append(entryArea.replaceAll("XXX2", entries[0]).replaceAll("XXX3", entries[1]));
				} else {
					if (firstIteration) {
						sb.append(startArea.replaceAll("XXX1", entry));
						firstIteration = false;
					} else {
						sb.append(endArea);
						sb.append(startArea.replaceAll("XXX1", entry));
					}
				}

			}

			sb.append(endArea);
			sb.append(endXml);
			MiscUtilities.stringToFile(outFileName, sb.toString());

		}

	}

}
