package com.codef.applets;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RotateVideos {
	
	
	private static final boolean ENABLE_MAIN_METHOD = true;

	private static final Logger LOGGER = LogManager.getLogger(RotateVideos.class.getName());
	
	private static final String FFMPEG_BIN_FOLDER = "C:\\Program Files\\FFMPEG\\bin\\";

	private static final String SOURCE_FOLDER = "D:\\_REVIEW_AND_FILE\\";
	private static final String TARGET_FOLDER = "D:\\_REVIEW_AND_FILE\\FINAL\\";

	public static void main(String[] args) {

		if (ENABLE_MAIN_METHOD) {
			startVisit(SOURCE_FOLDER);
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
		// “1” = 90 degrees Clockwise
		// “2” = 90 degrees Counterclockwise,
		// “3” = 90 Clockwise and Vertical Flip
		// Values van be chained together  (e.g. "transpose=2,transpose=2")
		
		String output = String.format("\"%sffmpeg.exe\" -y -i \"%s%s\" -vf \"transpose=1,transpose=1\" \"%s%s\"", FFMPEG_BIN_FOLDER, SOURCE_FOLDER, fileName, TARGET_FOLDER, fileName);
		LOGGER.info(output);
	}

}
