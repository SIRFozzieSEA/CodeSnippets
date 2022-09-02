package com.codef.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.codef.xsalt.utils.XSaLTFileSystemUtils;
import com.codef.xsalt.utils.XSaLTFileVisitor;

public class MemeRenamerFileVisitor extends XSaLTFileVisitor {

	private static final Logger LOGGER = LogManager.getLogger(MemeRenamerFileVisitor.class.getName());

	private static final String sourceFolder = "C:\\_PRIMARY_SORT\\_MEMES";
	private static final String targetFolder = "E:\\Memes";

	private static Set<String> folderSet = new TreeSet<>();

	private static int totalCount = 0;

	public static void main(String[] args) {

		HashMap<String, String> myArgumentsMap = new HashMap<String, String>();
		myArgumentsMap.put("PREFIX", " aaaa ");
		MemeRenamerFileVisitor myMfr = new MemeRenamerFileVisitor(myArgumentsMap);
		myMfr.startVisit(sourceFolder);

		LOGGER.info("total count: " + totalCount);

	}

	public MemeRenamerFileVisitor(HashMap<String, String> argumentsMap) {
		super(argumentsMap);
	}

	@Override
	public void visitFileCode(String filePath) {

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
		String newFileNameToUse = filePrefixNew + " index" + argumentsMap.get("PREFIX") + padNo + "."
				+ nFileExtension.toLowerCase();

		if (!nFileExtension.toLowerCase().equals("ini") && !nFileExtension.toLowerCase().equals("db")) {

			String targetFile = filePath.replace(sourceFolder, targetFolder).replace(fileName, newFileNameToUse);

			try {
				XSaLTFileSystemUtils.copyFile(filePath, targetFile);
				// LOGGER.info("   Copied from: " + filePath + " to: " + targetFile);
				totalCount = totalCount + 1;

				try {
					XSaLTFileSystemUtils.deleteFile(filePath, false);
					// LOGGER.info("       Deleted: " + filePath + " to: " + targetFile);
				} catch (Exception e) {
					LOGGER.info(" Cannot Delete: " + filePath + " to: " + targetFile);
				}

			} catch (IOException e) {
				LOGGER.info(" Error copying: " + filePath + " to: " + targetFile);
			}

//			LOGGER.info("");

		}

	}

}
