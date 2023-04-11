package com.codef.helpers;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RotateVideos {
	
	
	private static boolean enableMainMethod = true;

	private static final Logger LOGGER = LogManager.getLogger(FileVisitorToFile.class.getName());
	
	private static final String ffmpegBinFolder = "C:\\Program Files\\FFMPEG\\bin\\";

	private static final String sourceFolder = "D:\\\\_REVIEW_AND_FILE\\BoyButtXXL\\";
	private static final String targetFolder = "D:\\\\_REVIEW_AND_FILE\\FINAL\\BoyButtXXL\\";

	public static void main(String[] args) throws IOException {

		if (enableMainMethod) {
			startVisit(sourceFolder);
		}

	}

	public static void startVisit(String filePath) {
		visitFiles(Paths.get(filePath));
	}

	private static void visitFiles(Path path) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path entry : stream) {
				if (entry.toFile().isDirectory()) {
					// visitFiles(entry);
				} else {
					visitFileCode(entry.getParent().toString(), entry.getFileName().toString());
				}
			}
		} catch (IOException e) {
			LOGGER.error(e.toString(), e);
		}
	}

	public static void visitFileCode(String filePath, String fileName) {
		// “1” = 90 degrees Clockwise
		// “2” = 90 degrees Counterclockwise,
		// “3” = 90 Clockwise and Vertical Flip
		// Values van be chained together  (e.g. "transpose=2,transpose=2")
		System.out.println("\"" + ffmpegBinFolder + "ffmpeg.exe\" -y -i \"" + sourceFolder + fileName + "\" -vf \"transpose=1,transpose=1\" \"" + targetFolder + fileName + "\"");
	}

}
