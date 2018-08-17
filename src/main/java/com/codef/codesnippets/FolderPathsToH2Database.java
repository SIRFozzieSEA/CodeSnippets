package com.codef.codesnippets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;



public class FolderPathsToH2Database {

	private static final Logger LOGGER = Logger.getLogger(FolderPathsToH2Database.class.getName());

	public static Connection myH2Conn = null;

	public static void main(String[] args) {
		try {

			myH2Conn = getH2Connection();
			createTable();

			Path path = Paths.get("C:\\_SORT\\Memes");
			traverseDir(path);

			closeH2Connection(myH2Conn);

		} catch (SQLException e) {
			LOGGER.error(e.toString(), e);
		}
	}

	public static void traverseDir(Path path) throws SQLException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					traverseDir(entry);
				} else {

					String filePath = entry.toString();

					String md5Hashee = getMD5Hash(filePath);
					LOGGER.info(filePath + "\tMD5=" + md5Hashee);

					PreparedStatement updateemp = myH2Conn.prepareStatement("insert into STUFFS values(?,?)");
					updateemp.setString(1, filePath);
					updateemp.setString(2, md5Hashee);
					updateemp.executeUpdate();

				}
			}
		} catch (IOException e) {
			LOGGER.error(e.toString(), e);
		}
	}

	public static String getMD5Hash(String fileName) throws IOException {
		FileInputStream fis = new FileInputStream(new File(fileName));
		String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		fis.close();
		return md5;
	}

	public static void createTable() throws SQLException {
		myH2Conn.createStatement()
				.execute("CREATE TABLE " + "STUFFS(PATH VARCHAR(2000) PRIMARY KEY, MD5 VARCHAR(100))");
	}

	public static Connection getH2Connection() throws SQLException {
		return DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
	}

	public static void closeH2Connection(Connection conn) throws SQLException {
		conn.close();
	}

}
