package com.codef.codesnippets;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class MemeFolderRenamer {

	private static final boolean enableMainMethod = true;

	private static final Logger LOGGER = Logger.getLogger(MemeFolderRenamer.class.getName());

	private static Set<String> extensionsFound = new HashSet<String>();

	private static int fileCount = 1;
	private static Set<String> folderSet = new TreeSet<>();

	public static void main(String[] args) {

		if (enableMainMethod) {

			try {
				Path path = Paths.get("E:\\Music");
				traverseDir(path);
			} catch (Exception e) {
				LOGGER.error(e.toString(), e);
			}

		}

		System.out.println(extensionsFound);

	}

	public static void traverseDir(Path path) throws SQLException {

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {

			for (Path entry : stream) {
				if (entry.toFile().isDirectory()) {
					traverseDir(entry);
				} else {

					String filePath = entry.toString();

					doRename(filePath);
//					getExtensions(filePath);

					fileCount++;

				}
			}
		} catch (IOException e) {
			LOGGER.error(e.toString(), e);
		}
	}

	public static void doRename(String filePath) {

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
		String newFileNameToUse = filePrefixNew + " index q" + padNo + "." + nFileExtension.toLowerCase();
		String renameFile = "rename \"" + filePath + "\" \"" + newFileNameToUse + "\"";

		System.out.println(renameFile);

	}

	public static void getExtensions(String filePath) {

		String nFileExtension = filePath.substring(filePath.length() - 4, filePath.length());
//		System.out.println(nFileExtension);

		if (nFileExtension.startsWith(".")) {
			if (!extensionsFound.contains(nFileExtension.toLowerCase())) {
				extensionsFound.add(nFileExtension.toLowerCase());
			}
		}

	}

}
