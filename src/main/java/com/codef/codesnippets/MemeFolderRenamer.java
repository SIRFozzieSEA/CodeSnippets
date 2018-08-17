package com.codef.codesnippets;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class MemeFolderRenamer {

	private static final Logger LOGGER = Logger.getLogger(MemeFolderRenamer.class.getName());

	private static int fileCount = 1;
	private static Set<String> folderSet = new TreeSet<>();

	public static void main(String[] args) {

		try {
			Path path = Paths.get("E:\\Pictures\\PhoneBackup\\Memes");
			traverseDir(path);
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
		}

	}

	public static void traverseDir(Path path) throws SQLException {

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {

			for (Path entry : stream) {
				if (entry.toFile().isDirectory()) {
					traverseDir(entry);
				} else {

					String filePath = entry.toString();

					// entry.

					String[] fileParts = filePath.split("\\\\");

					String folderName = fileParts[fileParts.length - 2];
					String fileName = fileParts[fileParts.length - 1];
					String filePrefixNew = folderName.toLowerCase().replaceAll(" ", "_");
					String[] fileNameNew = fileName.split("\\.");
					String nFileExtension = fileNameNew[1];

					if (!folderSet.contains(folderName)) {
						folderSet.add(folderName);
						fileCount = 1;
					}

					String padNo = String.format("%010d", fileCount);
					String newFileNameToUse = filePrefixNew + "_" + padNo + "." + nFileExtension.toLowerCase();
					LOGGER.info("rename \"" + filePath + "\" \"" + newFileNameToUse + "\"");

					fileCount++;

				}
			}
		} catch (IOException e) {
			LOGGER.error(e.toString(), e);
		}
	}

}
