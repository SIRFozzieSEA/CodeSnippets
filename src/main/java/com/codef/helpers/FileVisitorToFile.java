package com.codef.helpers;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.codef.xsalt.utils.XSaLTFileSystemUtils;

public class FileVisitorToFile {

	private static boolean enableMainMethod = true;

	private static final Logger LOGGER = LogManager.getLogger(FileVisitorToFile.class.getName());

	private static final String targetFolder = "K:\\Backup_09052022\\E\\Pictures";
	private static final String saveFile = "d:\\kdrive.txt";

	private static StringBuffer saveBuffer = new StringBuffer();

	public static void main(String[] args) throws IOException {

		if (enableMainMethod) {

			String startFolder = targetFolder;
			startVisit(startFolder);
			XSaLTFileSystemUtils.writeStringBufferToFile(saveBuffer, saveFile);
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
