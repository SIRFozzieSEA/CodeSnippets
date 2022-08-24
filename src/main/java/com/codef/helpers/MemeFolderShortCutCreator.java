package com.codef.helpers;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.codef.xsalt.utils.XSaLTFileSystemUtils;
import com.codef.xsalt.utils.XSaLTFileVisitor;

public class MemeFolderShortCutCreator extends XSaLTFileVisitor {

	private static final boolean enableMainMethod = false;

	private static final Logger LOGGER = LogManager.getLogger(MemeFolderShortCutCreator.class.getName());

	private static final String sourceFolder = "E:\\Memes";
	private static final String sourceFolderMemes = "_MEMES";
	private static final String targetFolder = "C:\\_PRIMARY_SORT";

	public static void main(String[] args) {

		if (enableMainMethod) {

			XSaLTFileSystemUtils.deleteDirectoryAndSubdirectories(targetFolder + "\\" + sourceFolderMemes);
			XSaLTFileSystemUtils.makeDirectory(targetFolder + "\\" + sourceFolderMemes);

			HashMap<String, String> myArgumentsMap = new HashMap<String, String>();
			MemeFolderShortCutCreator myMfr = new MemeFolderShortCutCreator(myArgumentsMap);
			myMfr.startVisitFolders(sourceFolder);

		}

	}

	public MemeFolderShortCutCreator(HashMap<String, String> argumentsMap) {
		super(argumentsMap);
	}

	@Override
	public void visitFolderCode(String filePath) {
		String[] fileParts = filePath.split("\\\\");

		if (fileParts.length > 2) {

			String newDirectory = filePath.replace(sourceFolder, targetFolder + "\\" + sourceFolderMemes);
			XSaLTFileSystemUtils.makeDirectory(newDirectory);
			LOGGER.info(newDirectory);

		}
	}

}
