package com.codef.applets;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FFMpegTransformer {

	private static final boolean ENABLE_MAIN_METHOD = true;
	private static final Logger LOGGER = LogManager.getLogger(FFMpegTransformer.class.getName());

	private static final String SOURCE_FOLDER = "D:\\Videos\\Smut\\- Steve";
	private static final String TARGET_FOLDER = SOURCE_FOLDER + "\\";
	private static final String FFMPEG_BIN_FOLDER = ".\\";

	private static int totalFiles = 0;

	public static void main(String[] args) {

		if (ENABLE_MAIN_METHOD) {
			createFileFolder(TARGET_FOLDER);
			startVisit(SOURCE_FOLDER);
		}

		System.out.println("[console]::beep()");
		System.out.println("[console]::beep()");
		System.out.println("[console]::beep()");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("total files: " + totalFiles);

	}

	public static void visitFileCode(String fileName) {

		if ((fileName.toLowerCase().endsWith(".webm") || fileName.toLowerCase().endsWith(".mp4")
				|| fileName.toLowerCase().endsWith(".mov")) && fileName.startsWith("r_")) {

			String output = "";

			ArrayList<String> commandList = new ArrayList<String>();

			// Calling the FFMPEG with the source file
			commandList
					.add(String.format("%sffmpeg.exe -i \"%s%s\"", FFMPEG_BIN_FOLDER, SOURCE_FOLDER + "\\", fileName));

			// EXAMPLE: r_720x1280_Raaeee4200 - Black 3.mp4
			// r_ = recode
			// 720 width
			// 1280 height
			// filename after dimensions

			String[] fileNameParts = fileName.split("_");

			String dimensionPart = fileNameParts[1];
			String width = dimensionPart.split("x")[0];
			String height = dimensionPart.split("x")[1];
			String newFileName = fileNameParts[2];

			// use "20" or below (18 is visually lossless) for higher quality, "23" for lower quality
			String crfValue = "18";

			// use "ultrafast", "superfast", "veryfast", "faster", "fast", "medium" (default), "slow", "slower", "veryslow"
			String presetValue = "veryslow";
			
			// Transpose
			// “1” = 90 degrees Clockwise
			// “2” = 90 degrees Counterclockwise,
			// “3” = 90 Clockwise and Vertical Flip
			// Values van be chained together (e.g. "transpose=2,transpose=2"), needs to be after the -vf part

			commandList.add(String.format("-vf scale=%s:%s:force_original_aspect_ratio=increase,crop=%s:%s", width,
					height, width, height));

			commandList.add(String.format("-c:v libx264 -crf %s -preset %s -c:a copy", crfValue, presetValue));

			// Strip off so many seconds of the start of the file
			// commandList.add("-ss 00:00:06");



			// Specifying the output
			commandList.add(String.format("\"%s%s\"", TARGET_FOLDER, newFileName.replaceAll(".mov", ".mp4")));

			output = String.join(" ", commandList);
			System.out.println(output);

			totalFiles++;

		}

	}

	public static void createFileFolder(String filePath) {
		File oDirectory = new File(filePath);
		if (!oDirectory.exists()) {
			oDirectory.mkdirs();
		}
	}

	public static void startVisit(String filePath) {
		visitFiles(Paths.get(filePath));
	}

	private static void visitFiles(Path path) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path entry : stream) {
				if (!entry.toFile().isDirectory()) {
					visitFileCode(entry.getFileName().toString());
				}
			}
		} catch (IOException e) {
			LOGGER.error(e.toString(), e);
		}
	}

}
