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

	private static final String SOURCE_FOLDER = "C:\\lala";
	private static final String TARGET_FOLDER = SOURCE_FOLDER + "\\final\\";
	private static final String FFMPEG_BIN_FOLDER = ".\\";

	private static int totalFiles = 0;

	public static void main(String[] args) {

		if (ENABLE_MAIN_METHOD) {
			createFileFolder(TARGET_FOLDER);
			startVisit(SOURCE_FOLDER);
		}

		System.out.println("");
		System.out.println("total files: " + totalFiles);

	}

	public static void visitFileCode(String fileName) {

		if (fileName.toLowerCase().endsWith(".webm") || fileName.toLowerCase().endsWith(".mp4")
				|| fileName.toLowerCase().endsWith(".mov")) {

			String output = "";

			ArrayList<String> commandList = new ArrayList<String>();

			// Calling the FFMPEG with the source file
			commandList
					.add(String.format("%sffmpeg.exe -i \"%s%s\"", FFMPEG_BIN_FOLDER, SOURCE_FOLDER + "\\", fileName));

			// Strip off so many seconds of the start of the file
			// commandList.add("-ss 00:00:06");

			// Filtergraph with scale... the file (width:height), if -1 is used for one if
			// the values, it will constrain with the original specified values
			String width = "720";
			String height = "1280";

			if (fileName.startsWith("p_")) {
				commandList.add(String.format("-vf scale=%s:%s", width, height));
			} else if (fileName.startsWith("l_")) {
				commandList.add(String.format("-vf scale=%s:%s", height, width));
			} else {
				commandList.add(String.format("-vf scale=%s:%s", height, width));
			}

			// Transpose
			// “1” = 90 degrees Clockwise
			// “2” = 90 degrees Counterclockwise,
			// “3” = 90 Clockwise and Vertical Flip
			// Values van be chained together (e.g. "transpose=2,transpose=2")
			// commandList.add("\"transpose=1,transpose=1\"");

			// Specifying the output
			commandList
					.add(String.format("\"%s%s\"", TARGET_FOLDER, fileName.toLowerCase().replaceAll(".mov", ".mp4")));

			output = String.join(" ", commandList);
			System.out.println(output);
			// LOGGER.info(output);

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
