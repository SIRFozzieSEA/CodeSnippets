package com.codef.codesnippets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ParseChecklist {

	private static final boolean enableMainMethod = true;

	public static String startXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><checklist>";
	public static String endXml = "</checklist>";

	public static String startArea = "<areas area=\"XXX1\">";
	public static String endArea = "</areas>";

	public static String entryArea = "<area><name>XXX2</name><condition>XXX3</condition><reference/><cautions/></area>";

	public static String inFileName = "C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\helicopter\\Robinson22Checklist.txt";
	public static String outFileName = "C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\helicopter\\Robinson22Checklist.xml";

	public static String workbookFilePath = "C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\helicopter\\Robinson22Checklist.xlsx";
	
	public static String beginFileNameForHTML = "C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\helicopter\\RobinsonHTML_top.txt";
	public static String endFileNameForHTML = "C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\helicopter\\RobinsonHTML_bottom.txt";
	
	public static String outFileHtmlName = "C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\helicopter\\R22.html";

	public static String entryBlankHTML = "<tr><td class=\"areaitem\" colspan=\"2\">&nbsp;</strong></td></tr>";
	public static String entryAreaHTML = "<tr><td class=\"generalarea\" colspan=\"2\"><strong>&nbsp;XXX1&nbsp;</strong></td></tr>";
	public static String entryHTML = "<tr><td style=\"width: 30px\">&nbsp;<input name=\"Checkbox1\" type=\"checkbox\" /></td><td class=\"areaitem\"><span class=\"areaitem\"><strong>XXX2</strong></span><br /><span class=\"areadescription\">XXX3</span></td></tr>";

	public static void main(String[] args) throws IOException {

		if (enableMainMethod) {

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Robinson 22 Checklist");

			int rowCount = 0;

			XSSFFont font = sheet.getWorkbook().createFont();
			font.setBold(true);

			XSSFCellStyle yellowStyle = sheet.getWorkbook().createCellStyle();
			yellowStyle.setWrapText(false);
			yellowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			yellowStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
			yellowStyle.setAlignment(HorizontalAlignment.LEFT);
			yellowStyle.setFont(font);

			XSSFCellStyle cellStyleCenter = sheet.getWorkbook().createCellStyle();
			cellStyleCenter.setWrapText(false);
			cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);

			XSSFCellStyle cellStyleLeftBold = sheet.getWorkbook().createCellStyle();
			cellStyleLeftBold.setWrapText(false);
			cellStyleLeftBold.setAlignment(HorizontalAlignment.LEFT);
			cellStyleLeftBold.setFont(font);

			boolean firstIteration = true;
			StringBuilder sbXml = new StringBuilder(startXml);
			
			StringBuilder sbHtml = new StringBuilder(MiscUtilities.readFile(beginFileNameForHTML));
			
			String[] entryLines = MiscUtilities.readFile(inFileName).split("\n");

			for (int i = 0; i < entryLines.length; i++) {
				String entry = entryLines[i].trim();

				if (entry.contains("\t")) {
					String[] entries = entry.split("\t");
					sbXml.append(entryArea.replaceAll("XXX2", entries[0]).replaceAll("XXX3", entries[1]));
					sbHtml.append(entryHTML.replaceAll("XXX2", entries[0]).replaceAll("XXX3", entries[1]));

					Row row = sheet.createRow(rowCount);
					Cell cell = row.createCell(0);
					cell.setCellValue("  ");
					cell.setCellStyle(cellStyleCenter);

					cell = row.createCell(1);
					cell.setCellStyle(cellStyleLeftBold);

					cell.setCellValue(entries[0]);
					cell = row.createCell(2);
					cell.setCellValue(entries[1]);

				} else {

					Row row = sheet.createRow(rowCount);
					Cell cell = row.createCell(0);
					cell.setCellValue(entry);
					cell.setCellStyle(yellowStyle);

					sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 2));

					if (firstIteration) {
						sbXml.append(startArea.replaceAll("XXX1", entry));
						sbHtml.append(entryAreaHTML.replaceAll("XXX1", entry));
						firstIteration = false;
					} else {
						sbXml.append(endArea);
						sbXml.append(startArea.replaceAll("XXX1", entry));
						sbHtml.append(entryBlankHTML);
						sbHtml.append(entryAreaHTML.replaceAll("XXX1", entry));
					}

				}

				rowCount++;

			}

			sbXml.append(endArea);
			sbXml.append(endXml);
			MiscUtilities.stringToFile(outFileName, sbXml.toString());
			
			sbHtml.append(MiscUtilities.readFile(endFileNameForHTML));
			MiscUtilities.stringToFile(outFileHtmlName, sbHtml.toString());

			FileOutputStream out = new FileOutputStream(new File(workbookFilePath));
			workbook.write(out);
			workbook.close();
			out.close();

			
		}

	}

}
