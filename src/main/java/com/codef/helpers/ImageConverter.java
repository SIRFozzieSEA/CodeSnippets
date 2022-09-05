package com.codef.helpers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImageConverter {

	public static boolean enableMainMethod = false;

	private static final Logger LOGGER = LogManager.getLogger(ImageConverter.class.getName());

	public static int fileCount = 0;
	public static int filesProcessed = 0;
	public static TreeSet<String> filetypes = new TreeSet<String>();

	public static void main(String[] args) {

		if (enableMainMethod) {

			String startFolder = "E:/Memes";
			startVisit(startFolder);
			LOGGER.info("Total files visited = " + fileCount);
			LOGGER.info("Total JFIF file visited = " + filesProcessed);
			LOGGER.info("File Types = " + filetypes);

		}

	}

	public static void startVisit(String filePath) {
		visitFiles(Paths.get(filePath));
	}

	private static void visitFiles(Path path) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path entry : stream) {
				if (entry.toFile().isDirectory()) {
					visitFiles(entry);
				} else {
					visitFileCode(entry.toString());
					fileCount++;
				}
			}
		} catch (IOException e) {
			LOGGER.error(e.toString(), e);
		}
	}

	public static void visitFileCode(String filePath) {

		String[] fileParts = filePath.split("\\\\");
		String fileName = fileParts[fileParts.length - 1];
		String[] fileNameNew = fileName.split("\\.");
		String nFileExtension = fileNameNew[fileNameNew.length - 1];

		filetypes.add(nFileExtension);

		if (filePath.toLowerCase().endsWith("jfif")) {
			convertImage(filePath);
			LOGGER.info("File " + filePath + " has been visited.");
			filesProcessed++;
		}
	}

	public static void convertImage(String imageFileName) {

		try {
			File imageFile = new File(imageFileName);
			BufferedImage bufferedImage = ImageIO.read(imageFile);
			File pathFile = new File(imageFileName + ".jpg");
			ImageIO.write(bufferedImage, "jpg", pathFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	// THIS IS TO CROP IMAGES

	public static void cropImage(String imageFileName, File pathFile, BufferedImage bufferedImage) throws IOException {
		if (imageFileName.contains("land")) {
			// landscape
			ImageIO.write(cropImage(bufferedImage, 87, 468, 600, 400), "jpg", pathFile);
		} else {
			// portrait
			ImageIO.write(cropImage(bufferedImage, 179, 466, 400, 600), "jpg", pathFile);
		}
	}

	private static BufferedImage cropImage(BufferedImage bufferedImage, int x, int y, int width, int height) {
		BufferedImage croppedImage = bufferedImage.getSubimage(x, y, width, height);
		return croppedImage;
	}

}
