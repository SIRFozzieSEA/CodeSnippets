package com.codef.codesnippets;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.codef.xsalt.utils.XSaLTFileSystemUtils;

public class IPhoneFileVisitor {

	private static final boolean ENABLE_MAIN_METHOD = true;

	private static final Logger LOGGER = LogManager.getLogger(IPhoneFileVisitor.class.getName());

	private static StringBuilder saveBuffer = new StringBuilder();

	private static final String SCAN_FOLDER = "E:\\TENZIN-SORT-SHIP\\jpg";
	private static final String SAVE_FILE_NAME = "D:\\IPhoneFileVisitor_Results.txt";

	public static void main(String[] args) throws IOException {

		if (ENABLE_MAIN_METHOD) {

			if (SCAN_FOLDER.contains("_")) {
				System.out.println("SCAN_FOLDER cannot contain underscores");
				return;
			}

			String startFolder = SCAN_FOLDER;
			startVisit(startFolder);

			if (saveBuffer.length() > 0) {
				XSaLTFileSystemUtils.writeStringBuilderToFile(saveBuffer, SAVE_FILE_NAME);
			}

		}

	}

	public static void startVisit(String filePath) {
		visitFiles(Paths.get(filePath));
	}

	private static void visitFiles(Path path) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path entry : stream) {
				if (entry.toFile().isDirectory()) {
					visitFiles(entry);
				} else {
					visitFileCode(entry.toString());
				}
			}
		} catch (IOException e) {
			LOGGER.error(e.toString(), e);
		}
	}

	public static void visitFileCode(String filePath) throws IOException {

		String fileExtension = getFileExtension(filePath);
		if (fileExtension.equals(".jpg") || fileExtension.equals(".jpeg") || fileExtension.equals(".mp4")
				|| fileExtension.equals(".mov")) {

			if (!filePath.contains("_E")) {

				String deleteFilePath = filePath.replaceAll("_", "_E");

				System.out.println("Looking at file: " + filePath);

				try {
					XSaLTFileSystemUtils.deleteFileNew(deleteFilePath);
				} catch (IOException e) {
					System.out.println("\tNo file " + deleteFilePath + " to delete");
				}

			}
		}

	}

	public static String getFileExtension(String filePath) {

		String fileExtension = "";

		if (filePath.contains(".")) {
			fileExtension = filePath.substring(filePath.lastIndexOf("."));
		}

		return fileExtension.toLowerCase();

	}

}
