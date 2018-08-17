package com.codef.codesnippets;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class PhotoHistoryFileVisitor extends SimpleFileVisitor<Path> {

	private static final Logger LOGGER = Logger.getLogger(PhotoHistoryFileVisitor.class.getName());

	Connection conn = null;
	Statement stmt = null;

	Set<String> extensions = new HashSet<>();

	public void setConnection(Connection conn) throws SQLException {

		this.conn = conn;
		this.stmt = conn.createStatement();

		this.extensions.add(".jpg");
		this.extensions.add(".jpeg");

	}

	@Override
	public FileVisitResult visitFile(Path filepath, BasicFileAttributes attr) {

		String name = filepath.getFileName().toString().toLowerCase();

		try {
			String fileExtension = name.substring(name.lastIndexOf('.'));
			if (extensions.contains(fileExtension)) {
				Metadata metadata = ImageMetadataReader.readMetadata(new File(filepath.toAbsolutePath().toString()));
				for (Directory directory : metadata.getDirectories()) {
					for (Tag tag : directory.getTags()) {
						doTag(tag, filepath);
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error(name + ":" + e.toString(), e);
		}

		return CONTINUE;
	}

	private void doTag(Tag tag, Path filepath) throws SQLException {
		if (tag.getTagName().contains("Date/Time Original")) {
			String photoTakenDate = tag.getDescription().replaceAll(":", "-");
			if (!photoTakenDate.substring(0, 10).equals("0000-00-00")) {
				String insert = "insert into test (path, createdate, createday) values (?, ?, ?)";
				try (PreparedStatement ps = conn.prepareStatement(insert);) {
					ps.setString(1, filepath.toAbsolutePath().toString());
					ps.setString(2, photoTakenDate.substring(0, 10));
					ps.setString(3, photoTakenDate.substring(5, 10));
					ps.executeUpdate();
				} catch (Exception e) {
					LOGGER.error(e.toString(), e);
				}
			}

		}

	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
		LOGGER.info("Directory " + dir, exc);
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		LOGGER.error(exc.toString(), exc);
		return CONTINUE;
	}

}
