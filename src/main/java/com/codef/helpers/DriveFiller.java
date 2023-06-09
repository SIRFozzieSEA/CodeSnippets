package com.codef.helpers;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DriveFiller {

	private static final boolean ENABLE_MAIN_METHOD = true;

	private static final Logger LOGGER = LogManager.getLogger(DriveFiller.class.getName());
	private static final int FILE_CHUNK_SIZE_IN_MB = 100;

	private static String driveLetterToFill = "K";
	private static double spaceToFillInGB = 0.0;

	public static void main(String[] args) {

		if (ENABLE_MAIN_METHOD) {
			Scanner keyboard = new Scanner(System.in);
			LOGGER.info("Enter a drive letter: ");
			driveLetterToFill = keyboard.nextLine();
			LOGGER.info("Enter space (in GB) to fill, '0' for auto: ");
			int userSpaceToFillInGB = keyboard.nextInt();
			if (userSpaceToFillInGB > spaceToFillInGB) {
				spaceToFillInGB = userSpaceToFillInGB;
			}
			keyboard.close();
			fillErUp();
		}

	}

	public static void fillErUp() {

		String directoryPath = driveLetterToFill + ":/DriveFillerChunks/";
		deleteDriveFillerChunksDirectory(directoryPath);
		new File(directoryPath).mkdir();

		try {

			LOGGER.info("STARTING");

			spaceToFillInGB = getUseableSpace(spaceToFillInGB, driveLetterToFill);
			Double iterationsDbl = (spaceToFillInGB * 1000) / FILE_CHUNK_SIZE_IN_MB;
			
			String message = String.format("Writing: %x files, on drive %s with chunks of %x MB to fill %d GB (+Extra)", 
					iterationsDbl.longValue(), driveLetterToFill, FILE_CHUNK_SIZE_IN_MB, spaceToFillInGB);
			LOGGER.info(message);

			StringBuilder contentBuilder = new StringBuilder();
			for (int i = 0; i < (10 * 1024 * FILE_CHUNK_SIZE_IN_MB); i++) {
				contentBuilder.append("101010101010101010101010101010101010101010101010101010"
						+ "101010101010101010101010101010101010101010101010101");
			}

			for (int j = 1; j < (iterationsDbl.longValue() + 1); j++) {
				String filePath = directoryPath + "CHUNK_" + String.format("%07d", j) + ".txt";
				Path fileP = Paths.get(filePath);
				Files.write(fileP, contentBuilder.toString().getBytes(StandardCharsets.UTF_8));
				if (j % 100 == 0) {
					LOGGER.info("     Wrote: {}", filePath);
				}
			}

		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
		}

		LOGGER.info("DONE");

	}

	public static double getUseableSpace(double spaceToFillInGB, String driveLetterToFill) {

		if (spaceToFillInGB == 0.0) {
			File file = new File(driveLetterToFill + ":");
			long usableSpace = file.getUsableSpace();
			return (usableSpace / 1024.0 / 1024.0 / 1024.0) + 5;
		} else {
			return spaceToFillInGB;
		}

	}

	public static void deleteDriveFillerChunksDirectory(String directoryPath) {

		Path rootPath = Paths.get(directoryPath);
		try (Stream<Path> deleteDir = Files.walk(rootPath)) {
			deleteDir.map(Path::toFile).forEach(File::delete);
		} catch (Exception e1) {
			LOGGER.info("  DriveFillerChunks folder not found, creating");
		}

	}

}
