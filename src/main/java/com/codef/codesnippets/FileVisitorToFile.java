package com.codef.codesnippets;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.codef.xsalt.utils.XSaLTFileSystemUtils;

public class FileVisitorToFile {

	private static final boolean ENABLE_MAIN_METHOD = true;

	private static final Logger LOGGER = LogManager.getLogger(FileVisitorToFile.class.getName());

	private static final String OPERATION = "cmdrecodetomp4"; // listfiles, countfileextensions, cmdrecodetomp4

	private static StringBuilder saveBuffer = new StringBuilder();
	private static final HashMap<String, Integer> FILE_EXTENSION_COUNT_MAP = new HashMap<String, Integer>();

	private static final String SCAN_FOLDER = "C:\\_PRIMARY_SORT";
	private static final String SAVE_FILE_NAME = "E:\\FileVisitorToFile_Results.txt";

	public static void main(String[] args) throws IOException {

		if (ENABLE_MAIN_METHOD) {

			String startFolder = SCAN_FOLDER;
			startVisit(startFolder);

			if (OPERATION.equals("countfileextensions")) {
				for (String key : FILE_EXTENSION_COUNT_MAP.keySet()) {
					saveBuffer.append(key + " : " + FILE_EXTENSION_COUNT_MAP.get(key) + "\n");
				}
			}

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

	public static void visitFileCode(String filePath) {

		if (OPERATION.equals("listfiles")) {
			saveBuffer.append(filePath + "\n");
		} else if (OPERATION.equals("countfileextensions")) {
			String fileExtension = getFileExtension(filePath);
			putInMap(fileExtension, FILE_EXTENSION_COUNT_MAP);
		} else if (OPERATION.equals("cmdrecodetomp4")) {
			String fileExtension = getFileExtension(filePath);
			if (fileExtension.equals(".webm") || fileExtension.equals(".mov")) {
				String newFilePath = filePath.replace(fileExtension, ".mp4");
				saveBuffer.append(".\\ffmpeg.exe -i \"" + filePath + "\" \"" + newFilePath + "\"\n");
			}
		}

	}

	public static String getFileExtension(String filePath) {

		String fileExtension = "";

		if (filePath.contains(".")) {
			fileExtension = filePath.substring(filePath.lastIndexOf("."));
		}

		return fileExtension;

	}

	public static void putInMap(String key, HashMap<String, Integer> map) {

		if (map.containsKey(key)) {
			map.put(key, map.get(key) + 1);
		} else {
			map.put(key, 1);
		}

	}

}
