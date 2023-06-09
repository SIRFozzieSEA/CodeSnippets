package com.codef.helpers;

import java.io.File;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.codef.xsalt.utils.XSaLTFileVisitor;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class GeoTagReaderFileVisitor extends XSaLTFileVisitor {

	private static final Logger LOGGER = LogManager.getLogger(GeoTagReaderFileVisitor.class.getName());

	private static final String SOURCE_FOLDER = "E:\\Pictures\\Events\\Hiking with Tenzin\\20200404";

	public GeoTagReaderFileVisitor(HashMap<String, String> argumentsMap) {
		super(argumentsMap);
	}

	public static void main(String[] args) {

		HashMap<String, String> myArgumentsMap = new HashMap<>();
		GeoTagReaderFileVisitor myGtrfv = new GeoTagReaderFileVisitor(myArgumentsMap);
		myGtrfv.startVisit(SOURCE_FOLDER);

	}

	@Override
	public void visitFileCode(String filePath) {
		if (filePath.endsWith(".jpg")) {
			printCoordinates(filePath);
		}
	}

	public void printCoordinates(String photoFileName) {

		try {
			File file = new File(photoFileName);

			double lastLat = 0;
			double lastLong = 0;

			LOGGER.info(photoFileName);

			Metadata metadata = ImageMetadataReader.readMetadata(file);

			for (Directory directory : metadata.getDirectories()) {
				for (Tag tag : directory.getTags()) {

					if (tag.getTagName().equals("GPS Longitude")) {
						String[] parts = tag.getDescription().split(" ");
						double degrees = Double.parseDouble(parts[0].substring(0, parts[0].length() - 1));
						double minutes = Double.parseDouble(parts[1].substring(0, parts[1].length() - 1));
						double seconds = Double.parseDouble(parts[2].substring(0, parts[2].length() - 1));
						lastLong = Math.signum(degrees) * (Math.abs(degrees) + (minutes / 60.0) + (seconds / 3600.0));
					}

					if (tag.getTagName().equals("GPS Latitude")) {
						String[] parts = tag.getDescription().split(" ");
						double degrees = Double.parseDouble(parts[0].substring(0, parts[0].length() - 1));
						double minutes = Double.parseDouble(parts[1].substring(0, parts[1].length() - 1));
						double seconds = Double.parseDouble(parts[2].substring(0, parts[2].length() - 1));
						lastLat = Math.signum(degrees) * (Math.abs(degrees) + (minutes / 60.0) + (seconds / 3600.0));
					}

				}
				if (directory.hasErrors()) {
					for (String error : directory.getErrors()) {
						LOGGER.error(error);
					}
				}
			}

			String latitude = String.format("%.4f", lastLat);
			String longitude = String.format("%.4f", lastLong);
			LOGGER.info("{} {}", latitude, longitude);

		} catch (Exception e) {
			LOGGER.error(e.toString());
			e.printStackTrace();
		}

	}

}
