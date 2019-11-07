package com.codef.codesnippets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

public class ImageCropper {

	private static final boolean enableMainMethod = false;

	public static void main(String[] args) throws IOException {

		if (enableMainMethod) {

			try (Stream<Path> walk = Files.walk(Paths.get("E:\\fly2"))) {

				List<String> myList = walk.map(x -> x.toString()).filter(f -> f.endsWith(".JPG"))
						.collect(Collectors.toList());

				for (Iterator<String> iterator = myList.iterator(); iterator.hasNext();) {

					String imageFileName = (String) iterator.next();

					File imageFile = new File(imageFileName);
					BufferedImage bufferedImage = ImageIO.read(imageFile);

					File pathFile = new File(imageFileName);

					if (imageFileName.contains("land")) {
						// landscape
						ImageIO.write(cropImage(bufferedImage, 87, 468, 600, 400), "jpg", pathFile);
					} else {
						// portrait
						ImageIO.write(cropImage(bufferedImage, 179, 466, 400, 600), "jpg", pathFile);
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private static BufferedImage cropImage(BufferedImage bufferedImage, int x, int y, int width, int height) {
		BufferedImage croppedImage = bufferedImage.getSubimage(x, y, width, height);
		return croppedImage;
	}

}
