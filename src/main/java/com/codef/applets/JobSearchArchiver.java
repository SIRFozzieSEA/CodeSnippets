package com.codef.applets;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JobSearchArchiver {

	private static final boolean ENABLE_MAIN_METHOD = true;
	private static final Logger LOGGER = LogManager.getLogger(JobSearchArchiver.class.getName());

	private static final String SOURCE_FOLDER = "E:\\Documents\\Career\\Job Search Crap\\- JobLog2024";
	private static final String TARGET_FOLDER = "E:\\Documents\\Career\\Job Search Crap\\- JobLog2024\\Older";

	private static int totalFiles = 0;

	public static void main(String[] args) {

		if (ENABLE_MAIN_METHOD) {
			createFileFolder(TARGET_FOLDER);
			startVisit(SOURCE_FOLDER);
		}

		System.out.println("");
		System.out.println("total files: " + totalFiles);

	}

	public static void visitFileCode(String fileName) throws IOException {

		if (Pattern.matches("^\\d.*", fileName)) {

			String pattern = "[_.]";
			String[] newFileNameParts = fileName.split(pattern);
			copyFile(SOURCE_FOLDER + "\\" + fileName,
					TARGET_FOLDER + "\\" + newFileNameParts[1] + "_" + newFileNameParts[0] + "." + newFileNameParts[2]);
			deleteFile(SOURCE_FOLDER + "\\" + fileName);

			totalFiles++;
		}

	}

	public static void copyFile(String sourceFile, String targetFile) {

		try {
			Files.copy(new File(sourceFile).toPath(), new File(targetFile).toPath());
		} catch (IOException e) {
			LOGGER.error("Error copying {} to {}", sourceFile, targetFile);
		}

	}

	public static void deleteFile(String pathToFile) throws IOException {

		Path filePath = Paths.get(pathToFile);
		Files.delete(filePath);

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
