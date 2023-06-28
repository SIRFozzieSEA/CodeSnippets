package com.codef.codesnippets;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.codef.xsalt.utils.XSaLTFileSystemUtils;

public class FileVisitorToFile {

	private static final boolean ENABLE_MAIN_METHOD = true;

	private static final Logger LOGGER = LogManager.getLogger(FileVisitorToFile.class.getName());

	private static final String SCAN_FOLDER = "K:\\Backup_09052022\\E\\Pictures";
	private static final String SAVE_FILE_NAME = "d:\\kdrive.txt";

	private static StringBuilder saveBuffer = new StringBuilder();

	public static void main(String[] args) throws IOException {

		if (ENABLE_MAIN_METHOD) {

			String startFolder = SCAN_FOLDER;
			startVisit(startFolder);
			XSaLTFileSystemUtils.writeStringBuilderToFile(saveBuffer, SAVE_FILE_NAME);
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

	public static void visitFileCode(String filePath) {
		saveBuffer.append(filePath + "\n");
	}

}
