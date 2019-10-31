package com.codef.codesnippets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

	private static final boolean enableMainMethod = false;

	public static void main(String[] args) throws IOException {

		if (enableMainMethod) {
			String sourceDirectoryPath = "C:\\Users\\sir_f\\Downloads\\input";
			String zipFilePath = "C:\\Users\\sir_f\\Downloads\\hangouts.zip";
			zipDirectory(sourceDirectoryPath, zipFilePath);
			String destDir = "C:\\Users\\sir_f\\Downloads\\output";
			zipFilePath = "C:\\Users\\sir_f\\Downloads\\hangouts.zip";
			unzip(zipFilePath, destDir);
			unzipNew(zipFilePath, destDir);
		}

	}

	public static void zipDirectory(String sourceDirectoryPath, String zipPath) throws IOException {
		Path zipFilePath = Files.createFile(Paths.get(zipPath));
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
			Path sourceDirPath = Paths.get(sourceDirectoryPath);
			Files.walk(sourceDirPath).filter(path -> !Files.isDirectory(path)).forEach(path -> {
				ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
				try {
					zipOutputStream.putNextEntry(zipEntry);
					zipOutputStream.write(Files.readAllBytes(path));
					zipOutputStream.closeEntry();
				} catch (Exception e) {
					System.err.println(e);
				}
			});
		}
	}

	public static void unzipNew(final String zipFilePath, final String unzipLocation) throws IOException {

		if (!(Files.exists(Paths.get(unzipLocation)))) {
			Files.createDirectories(Paths.get(unzipLocation));
		}
		try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
			ZipEntry entry = zipInputStream.getNextEntry();
			while (entry != null) {
				Path filePath = Paths.get(unzipLocation, entry.getName());
				if (!entry.isDirectory()) {
					unzipFiles(zipInputStream, filePath);
				} else {
					Files.createDirectories(filePath);
				}

				zipInputStream.closeEntry();
				entry = zipInputStream.getNextEntry();
			}
		}
	}

	public static void unzipFiles(final ZipInputStream zipInputStream, final Path unzipFilePath) throws IOException {

		try (BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(unzipFilePath.toAbsolutePath().toString()))) {
			byte[] bytesIn = new byte[1024];
			int read = 0;
			while ((read = zipInputStream.read(bytesIn)) != -1) {
				bos.write(bytesIn, 0, read);
			}
		}

	}

	private static void unzip(String zipFilePath, String destDir) {
		File dir = new File(destDir);
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		FileInputStream fis;
		// buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try {
			fis = new FileInputStream(zipFilePath);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();

			int fileNo = 0;

			while (ze != null) {
				String fileName = ze.getName();

				fileName = fileName.replaceAll("[^a-zA-Z0-9\\-]", "");

				File newFile = new File(destDir + File.separator + fileName);
				System.out.println("Unzipping to " + newFile.getAbsolutePath());
				// create directories for sub directories in zip
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				// close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();

				fileNo = fileNo + 1;

			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
