package com.codef.helpers;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import com.codef.xsalt.utils.XSaLTFileVisitor;

public class MemeRenamerFileVisitor extends XSaLTFileVisitor {

	private static Set<String> folderSet = new TreeSet<>();

	public static void main(String[] args) {
		
		HashMap<String, String> myArgumentsMap = new HashMap<String, String>();
		myArgumentsMap.put("PREFIX", "ew");
		MemeRenamerFileVisitor myMfr = new MemeRenamerFileVisitor(myArgumentsMap);
		myMfr.startVisit("E:\\PRIMARY_SORT\\Memes");
		
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
		String newFileNameToUse = filePrefixNew + " index " + argumentsMap.get("PREFIX") + padNo + "." + nFileExtension.toLowerCase();

		if (!nFileExtension.toLowerCase().equals("ini") && !nFileExtension.toLowerCase().equals("db")) {
			String renameFile = "rename \"" + filePath + "\" \"" + newFileNameToUse + "\"";
			System.out.println(renameFile);
		}

	}

}
