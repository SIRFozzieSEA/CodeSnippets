package com.codef.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.codef.xsalt.utils.XSaLTFileVisitor;

public class WhichExtensionsFileVisitor extends XSaLTFileVisitor {

	private Set<String> extensionsFound = new HashSet<String>();

	public static void main(String[] args) {

		WhichExtensionsFileVisitor myMfr = new WhichExtensionsFileVisitor(null);
		myMfr.startVisit("E:\\Music");
		System.out.println(myMfr.extensionsFound);

	}

	public WhichExtensionsFileVisitor(HashMap<String, String> argumentsMap) {
		super(argumentsMap);
	}

	@Override
	public void visitFileCode(String filePath) {

		String nFileExtension = filePath.substring(filePath.length() - 4, filePath.length());

		if (nFileExtension.startsWith(".")) {
			if (!extensionsFound.contains(nFileExtension.toLowerCase())) {
				extensionsFound.add(nFileExtension.toLowerCase());
			}
		}

	}

}
