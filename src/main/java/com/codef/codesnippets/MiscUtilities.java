package com.codef.codesnippets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

public class MiscUtilities {

	private static final Logger LOGGER = Logger.getLogger(MiscUtilities.class.getName());

	public static void stringToFile(String pathToFile, String stringToWrite) throws IOException {
		Files.write(Paths.get(pathToFile), stringToWrite.getBytes());
	}

	public static void copyFile(String pathToSourceFile, String pathToDestinationFile) throws IOException {
		Files.copy(new File(pathToSourceFile).toPath(), new File(pathToDestinationFile).toPath());
	}

	public static void makeDirectories(String pathToDirectory) {
		File directory = new File(pathToDirectory);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public static void deleteFile(String pathToFile) {
		Path filePath = Paths.get(pathToFile);
		try {
			Files.delete(filePath);
		} catch (Exception e) {
			LOGGER.debug(e.toString(), e);
		}
	}

	public static void cleanDirectory(String pathToDirectory) {
		File directory = new File(pathToDirectory);
		if (directory.exists()) {
			try (Stream<Path> stream = Files.walk(Paths.get(pathToDirectory))) {
				stream.filter(Files::isRegularFile).map(Path::toFile).forEach(File::delete);
			} catch (Exception e) {
				LOGGER.error(e.toString(), e);
			}
		} else {
			directory.mkdir();
		}
	}

	public static String readFile(String pathToFile) throws IOException {
		// Path path = Paths.get(LavaballUtils.class.getResource(resourcePath).toURI());
		return new String(Files.readAllBytes(new File(pathToFile).toPath()));
	}

}
