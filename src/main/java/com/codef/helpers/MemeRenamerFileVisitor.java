package com.codef.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import com.codef.xsalt.utils.XSaLTFileSystemUtils;
import com.codef.xsalt.utils.XSaLTFileVisitor;

public class MemeRenamerFileVisitor extends XSaLTFileVisitor {

	private static final String sourceFolder = "C:\\_PRIMARY_SORT\\Memes";
	private static final String targetFolder = "E:\\Memes";

	private static Set<String> folderSet = new TreeSet<>();

	public static void main(String[] args) {

		HashMap<String, String> myArgumentsMap = new HashMap<String, String>();
		myArgumentsMap.put("PREFIX", "aaa");
		MemeRenamerFileVisitor myMfr = new MemeRenamerFileVisitor(myArgumentsMap);
		myMfr.startVisit(sourceFolder);

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
		String newFileNameToUse = filePrefixNew + " index " + argumentsMap.get("PREFIX") + padNo + "."
				+ nFileExtension.toLowerCase();

		if (!nFileExtension.toLowerCase().equals("ini") && !nFileExtension.toLowerCase().equals("db")) {

			String targetFile = filePath.replace(sourceFolder, targetFolder).replace(fileName, newFileNameToUse);

			try {
				XSaLTFileSystemUtils.copyFile(filePath, targetFile);
				System.out.println("  Copied from: " + filePath + " to: " + targetFile);
			} catch (IOException e) {
				System.out.println(" Duplicate in: " + filePath + " to: " + targetFile);
			}

			try {
				XSaLTFileSystemUtils.deleteFile(filePath, false);
				System.out.println("      Deleted: " + filePath + " to: " + targetFile);
			} catch (Exception e) {
				System.out.println("Cannot Delete: " + filePath + " to: " + targetFile);
			}
			
			System.out.println("");

		}

	}

}
