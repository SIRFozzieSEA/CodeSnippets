package com.codef.helpers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.codef.xsalt.utils.XSaLTFileVisitor;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class GeoTagReaderFileVisitor extends XSaLTFileVisitor {

	private static final String sourceFolder = "E:\\Pictures\\Events\\Hiking with Tenzin\\20200419";

	public GeoTagReaderFileVisitor(HashMap<String, String> argumentsMap) {
		super(argumentsMap);
	}

	public static void main(String[] args) {

		HashMap<String, String> myArgumentsMap = new HashMap<String, String>();
		GeoTagReaderFileVisitor myGtrfv = new GeoTagReaderFileVisitor(myArgumentsMap);
		myGtrfv.startVisit(sourceFolder);

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

//			System.out.println(photoFileName);

			Metadata metadata = ImageMetadataReader.readMetadata(file);

			for (Directory directory : metadata.getDirectories()) {
				for (Tag tag : directory.getTags()) {

					if (tag.getTagName().equals("GPS Longitude")) {
//						System.out.println("\t" + tag.getTagName() + ": " + tag.getDescription());

						String[] parts = tag.getDescription().split(" ");

						double degrees = Double.valueOf(parts[0].substring(0, parts[0].length() - 1));
						double minutes = Double.valueOf(parts[1].substring(0, parts[1].length() - 1));
						double seconds = Double.valueOf(parts[2].substring(0, parts[2].length() - 1));
						lastLong = Math.signum(degrees) * (Math.abs(degrees) + (minutes / 60.0) + (seconds / 3600.0));

					}

					if (tag.getTagName().equals("GPS Latitude")) {
//						System.out.println("\t" + tag.getTagName() + ": " + tag.getDescription());

						String[] parts = tag.getDescription().split(" ");

						double degrees = Double.valueOf(parts[0].substring(0, parts[0].length() - 1));
						double minutes = Double.valueOf(parts[1].substring(0, parts[1].length() - 1));
						double seconds = Double.valueOf(parts[2].substring(0, parts[2].length() - 1));
						lastLat = Math.signum(degrees) * (Math.abs(degrees) + (minutes / 60.0) + (seconds / 3600.0));

					}

				}
				if (directory.hasErrors()) {
					for (String error : directory.getErrors()) {
						System.err.format("ERROR: %s", error);
					}
				}
			}
			
//			System.out.println("\t" + lastLat + " " + lastLong);
			System.out.format("%.4f", lastLat);
			System.out.print(" ");
			System.out.format("%.4f", lastLong);
			System.out.print("\n");
			

		} catch (ImageProcessingException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}

	}

}
