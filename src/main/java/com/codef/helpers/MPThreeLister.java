package com.codef.helpers;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.codef.xsalt.utils.XSaLTFileSystemUtils;
import com.codef.xsalt.utils.XSaLTFileVisitor;

public class MPThreeLister extends XSaLTFileVisitor {

	private static final Logger LOGGER = LogManager.getLogger(MPThreeLister.class.getName());

	private static final String SOURCE_FOLDER = "E:\\Music";

	public static void main(String[] args) {

		HashMap<String, String> myArgumentsMap = new HashMap<>();
		MPThreeLister myMfr = new MPThreeLister(myArgumentsMap);
		myMfr.startVisit(SOURCE_FOLDER);

	}

	public MPThreeLister(HashMap<String, String> argumentsMap) {
		super(argumentsMap);
	}

	@Override
	public void visitFileCode(String filePath) {

		if (filePath.toLowerCase().endsWith("ini") || filePath.toLowerCase().endsWith("jpg")
				|| filePath.toLowerCase().endsWith("db")) {

			try {
				XSaLTFileSystemUtils.deleteFile(filePath, false);
				LOGGER.info("Deleted: " + filePath + " to: " + filePath);
			} catch (Exception e) {
				LOGGER.info("Cannot Delete: " + filePath + " to: " + filePath);
			}

		}

	}

}
