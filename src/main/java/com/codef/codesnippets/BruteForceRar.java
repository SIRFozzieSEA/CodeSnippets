package com.codef.codesnippets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

public class BruteForceRar {

	
	// java -cp "C:\Users\sir_f\.m2\repository\log4j\log4j\1.2.17\log4j-1.2.17.jar;C:\GitRepos\CodeSnippets\target\classes" com.codef.codesnippets.BruteForceRar
	
	private static final Logger LOGGER = Logger.getLogger(BruteForceRar.class.getName());

	private char startChar = ' ';
	private char endChar = '~';
	private String pickUpFromString = " ";

	private long bruteIterationCount = 0;

	private String sourceFileDirectory = "";
	private String rarFileName = "";

	public static void main(String[] args) {
		BruteForceRar bfr = new BruteForceRar();
		bfr.bruteForceIt();
	}

	public void bruteForceIt() {
		try {
			String criteria = pickUpFromString;
			while ((criteria = getNextBrute(criteria)) != null) {
				String currentLine;
				StringBuffer outputBuffer = new StringBuffer();
				String sCmd = "UnRAR t -ierr -p" + criteria + " " + sourceFileDirectory + rarFileName;

				LOGGER.info(criteria + " -- Iteration = " + bruteIterationCount);

				boolean doActualCrackingFlag = false;
				if (doActualCrackingFlag) {

					Process process = Runtime.getRuntime().exec(sCmd);
					BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
					while ((currentLine = br.readLine()) != null) {
						outputBuffer.append(currentLine);
					}
					if (outputBuffer.indexOf("All OK") != -1) {

						String message = "'" + criteria + "' is the correct password!";
						Files.write(Paths.get(sourceFileDirectory + "CrackedPassword.txt"), message.getBytes());
						LOGGER.info(message);
						System.exit(0);
					}
					br.close();

				}

				bruteIterationCount = bruteIterationCount + 1;
				if (bruteIterationCount == 1 || bruteIterationCount % 1000000 == 0) {
					LOGGER.info(criteria + " <---- Iteration " + bruteIterationCount / 1000000 + " x 1000000");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getNextBrute(String seed) {
		String next = "";
		int lengthOfSeed = seed.length();
		char[] seedArray = seed.toCharArray();
		for (int j = lengthOfSeed - 1; j >= 0; j--) {
			seedArray[j] = getNextChar(seedArray[j]);
			if (!(seedArray[j] == startChar)) {
				next = new String(seedArray);
				break;
			} else {
				if (j == 0) {
					next = new String(seedArray) + startChar;
				}
			}
		}
		return next;
	}

	private char getNextChar(char ch) {
		if (ch == endChar) {
			return startChar;
		} else {
			return ++ch;
		}
	}

}
