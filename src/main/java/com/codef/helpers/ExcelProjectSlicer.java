package com.codef.helpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.codef.xsalt.utils.XSaLTFileSystemUtils;

public class ExcelProjectSlicer {

	/*
	 * This project will take a screen capture split it into individual files and
	 * then insert it into an Excel spreadsheet. This was used to annotate some code
	 * that was generated easily in a worksheet format.
	 * 
	 * See sample results in:
	 * C:\GitRepos\CodeSnippets\src\main\resources\excelprojectslicer\ExcelProjectSlicer_Sample_Worksheet.xlsx
	 */

	private static final boolean enableMainMethod = false;

	private static final String outputFolder = "E:/_SORT/";
	private static final String outputExcelFileName = "E:/_SORT/worksheet.xlsx";

	private static final Logger LOGGER = Logger.getLogger(ExcelProjectSlicer.class.getName());

	public static void main(String[] args) throws URISyntaxException {

		if (enableMainMethod) {
			Path imageFileName = Paths
					.get(ClassLoader.getSystemResource("excelprojectslicer/SampleEclipseProject.JPG").toURI());
			List<String> imageFileNames = doImages(imageFileName, outputFolder, 18, 2);
			doExcelSheet(outputExcelFileName, imageFileName, imageFileNames);
		}

	}

	public static List<String> doImages(Path imageFilePath, String imageSliceOutputFolder, int sliceSizeInPixels,
			int firstOffset) {

		List<String> imageFileNames = new ArrayList<>();

		try {

			XSaLTFileSystemUtils.cleanDirectory(imageSliceOutputFolder);

			BufferedImage originalImage = ImageIO.read(new File(imageFilePath.toString()));

			int runningY = firstOffset;
			int imageWidth = originalImage.getWidth();
			int imageHeight = originalImage.getHeight();
			int howManyTimesToRun = (imageHeight - firstOffset) / sliceSizeInPixels;

			for (int i = 0; i < howManyTimesToRun; i++) {

				if (i != 0) {
					runningY = runningY + (sliceSizeInPixels);
				}

				BufferedImage subImage = originalImage.getSubimage(0, runningY, imageWidth, sliceSizeInPixels);
				String newFilePath = imageSliceOutputFolder + "slice_" + String.format("%05d", i + 1) + ".jpg";
				File outputfile = new File(newFilePath);
				imageFileNames.add(newFilePath);
				ImageIO.write(subImage, "jpg", outputfile);

			}

		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
		}

		return imageFileNames;

	}

	public static void doExcelSheet(String fileName, Path originalImageFilePath, List<String> imageFileNames) {

		try (XSSFWorkbook workbook = new XSSFWorkbook();) {

			XSSFSheet sheet = workbook.createSheet("Sliced Sheet");

			Row header = sheet.createRow(0);
			header.createCell(0).setCellValue("");
			header.createCell(1).setCellValue("Description");

			for (int i = 0; i < imageFileNames.size(); i++) {

				makeSliceRow(workbook, sheet, imageFileNames.get(i), i + 1);
			}

			sheet.setColumnWidth(0, getImageWidth(originalImageFilePath) * 42);

			FileOutputStream out = new FileOutputStream(new File(fileName));
			workbook.write(out);
			out.close();

		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
		}

	}

	public static void makeSliceRow(XSSFWorkbook workbook, XSSFSheet sheet, String imageFileName, int row)
			throws IOException {

		CreationHelper helper = workbook.getCreationHelper();
		Drawing<?> drawing = sheet.createDrawingPatriarch();
		ClientAnchor anchor = helper.createClientAnchor();

		File fi = new File(imageFileName);
		byte[] fileContent = Files.readAllBytes(fi.toPath());
		int pictureIdx = workbook.addPicture(fileContent, Workbook.PICTURE_TYPE_JPEG);

		anchor.setCol1(0);
		anchor.setCol2(1);

		anchor.setRow1(row);
		anchor.setRow2(row + 1);

		drawing.createPicture(anchor, pictureIdx);
		sheet.createRow(row).createCell(row);

	}

	public static int getImageWidth(Path imageFilePath) throws IOException {
		BufferedImage originalImage = ImageIO.read(new File(imageFilePath.toString()));
		return originalImage.getWidth();
	}

}
