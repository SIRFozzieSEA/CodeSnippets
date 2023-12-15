package com.codef.applets;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FFMpegTransformer {

	private static final boolean ENABLE_MAIN_METHOD = true;
	private static final Logger LOGGER = LogManager.getLogger(FFMpegTransformer.class.getName());

	private static final String FFMPEG_BIN_FOLDER = ".\\";
	private static final String SOURCE_FOLDER = "D:\\Videos\\Converted Files\\hornyjohny66\\";
	private static final String TARGET_FOLDER = SOURCE_FOLDER + "final\\";

	public static void main(String[] args) {

		if (ENABLE_MAIN_METHOD) {
			createFileFolder(TARGET_FOLDER);
			startVisit(SOURCE_FOLDER);
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

	public static void visitFileCode(String fileName) {

		String mode = "RESAMPLE";
		String output = "";
		switch (mode) {
		case "ROTATE":

			// “1” = 90 degrees Clockwise
			// “2” = 90 degrees Counterclockwise,
			// “3” = 90 Clockwise and Vertical Flip
			// Values van be chained together (e.g. "transpose=2,transpose=2")
			output = String.format("\"%sffmpeg.exe\" -y -i \"%s%s\" -vf \"transpose=1,transpose=1\" \"%s%s\"",
					FFMPEG_BIN_FOLDER, SOURCE_FOLDER, fileName, TARGET_FOLDER, fileName);

			break;
		case "RESAMPLE":
			output = String.format("%sffmpeg.exe -i \"%s%s\" -vf scale=1280:720 \"%s%s\"", FFMPEG_BIN_FOLDER,
					SOURCE_FOLDER, fileName, TARGET_FOLDER, fileName);
			break;
		default:
			break;
		}

		System.out.println(output);
		// LOGGER.info(output);

	}

}
