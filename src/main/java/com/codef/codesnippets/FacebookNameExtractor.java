package com.codef.codesnippets;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import com.codef.xsalt.utils.XSaLTFileSystemUtils;
import com.codef.xsalt.utils.XSaLTStringUtils;

public class FacebookNameExtractor {

	public static void main(String[] args) throws IOException {

		String namesFileName = "C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\FacebookNames.txt";
		String friendsFolder = "E:\\Documents\\Personal\\Old Social Profiles\\";

		Set<String> facebookNamesFinal = new TreeSet<String>();
		String facebookNamesRaw = XSaLTFileSystemUtils.readFile(namesFileName).trim();
		String[] facebookNames = facebookNamesRaw.split("\\r\\n");
		for (String singleName : facebookNames) {
			if (singleName.indexOf("mutual") == -1 && singleName.indexOf("Works at") == -1
					&& singleName.indexOf("University of") == -1 && singleName.indexOf("Manager") == -1
					&& singleName.indexOf("Artist at") == -1 && singleName.indexOf("Attorney") == -1
					&& singleName.indexOf("Teacher") == -1 && singleName.indexOf("Hogwarts") == -1
					&& singleName.indexOf("Hampshire") == -1 && singleName.indexOf("Research") == -1
					&& singleName.indexOf("College") == -1 && singleName.indexOf("Make Up Forever") == -1
					&& singleName.indexOf("School") == -1 && singleName.indexOf("University") == -1
					&& singleName.indexOf("Self-Employed") == -1 && singleName.indexOf("Consulting Firm") == -1) {
				facebookNamesFinal.add(singleName);
			}
		}

		StringBuffer outBuffer = new StringBuffer();
		for (String newName : facebookNamesFinal) {
			outBuffer.append(newName);
			outBuffer.append("\n");
		}

		XSaLTFileSystemUtils.writeStringBufferToFile(outBuffer,
				friendsFolder + "Friends_" + XSaLTStringUtils.getDateString() + ".txt");

	}

}
