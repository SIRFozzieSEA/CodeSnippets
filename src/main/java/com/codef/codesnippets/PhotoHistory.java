package com.codef.codesnippets;

import java.awt.Desktop;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcDataSource;

public class PhotoHistory {

	private static final Logger LOGGER = Logger.getLogger(PhotoHistory.class.getName());

	private static Connection conn = null;
	private static Statement stmt = null;

	private static String dbFilePath = "C:/_SORT/testdb";
	private static String imageDirectoryPath = "E:/Pictures";

	private static String dayOfInterest;
	private static String outputFolderPathAndFileName = "C:/_SORT/" + dayOfInterest + ".html";

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);

		LOGGER.info("Re-index data (y/n)?");
		boolean reIndex = scanner.next().equals("y") ? true : false;

		LOGGER.info("Enter month (01-12)?");
		String month = scanner.next();

		LOGGER.info("Enter day (01-31)?");
		String day = scanner.next();

		dayOfInterest = month + "-" + day;

		LOGGER.info("Start");

		getConnection();

		if (reIndex) {
			dropAndRecreateDb();
			loadImagesIntoDb();
			doStatisticsByDay();
		}

		generateHTMLPicsByDay();
		launchPicOfTheDay();

		closeConnection();
		scanner.close();

		LOGGER.info("End");
	}

	public static void launchPicOfTheDay() {

		try {
			Desktop desktop = java.awt.Desktop.getDesktop();
			URI oURL = new URI(outputFolderPathAndFileName);
			desktop.browse(oURL);
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
		}
	}

	public static void generateHTMLPicsByDay() {

		try {

			Path templatePath = Paths.get(ClassLoader.getSystemResource("photohistory_template.html").toURI());

			String content = new String(Files.readAllBytes(templatePath));

			StringBuilder outBuilder = new StringBuilder();
			outBuilder.append("<HTML><CENTER>");

			stmt = conn.createStatement();

			String query = "SELECT PATH, CREATEDAY, CREATEDATE FROM TEST WHERE CREATEDAY = ? ORDER BY CREATEDATE DESC";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, dayOfInterest);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				String imgContent = content;
				String realpath = rs.getString("PATH").replaceAll("\\\\", "\\\\\\\\");
				imgContent = imgContent.replaceAll("PATH", realpath);
				imgContent = imgContent.replaceAll("CREATEDATE", rs.getDate("CREATEDATE").toString());
				outBuilder.append(imgContent);

			}

			outBuilder.append("</CENTER></HTML>");

			Files.write(Paths.get(outputFolderPathAndFileName), outBuilder.toString().getBytes());

		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
		}
	}

	public static void doStatisticsByDay() {

		try {
			stmt = conn.createStatement();
			String query = "SELECT CREATEDAY, count(CREATEDAY) AS CREATEDAY_COUNT FROM TEST GROUP BY CREATEDAY ORDER BY CREATEDAY";
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				LOGGER.info(rs.getString("CREATEDAY") + " = " + rs.getInt("CREATEDAY_COUNT"));
			}
		} catch (SQLException e) {
			LOGGER.error(e.toString(), e);
		}

	}

	public static void loadImagesIntoDb() {

		try {
			Path startingDir = Paths.get(imageDirectoryPath);
			PhotoHistoryFileVisitor pf = new PhotoHistoryFileVisitor();
			pf.setConnection(conn);
			Files.walkFileTree(startingDir, pf);
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
		}

	}

	public static void dropAndRecreateDb() {

		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			LOGGER.error(e.toString(), e);
		}

		try {
			stmt.executeUpdate("DROP TABLE TEST");
		} catch (Exception e) {
			// do nothing
		}

		try {
			String sql = "CREATE TABLE TEST (id bigint auto_increment, path VARCHAR(2000), createdate DATE, createday VARCHAR(5), PRIMARY KEY ( id ))";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
				// nothing we can do
			}
		}
	}

	public static void getConnection() {
		try {
			JdbcDataSource ds = new JdbcDataSource();
			ds.setURL("jdbc:h2:file:" + dbFilePath);
			ds.setUser("sa");
			ds.setPassword("sa");
			conn = ds.getConnection();
		} catch (SQLException e) {
			LOGGER.error(e.toString(), e);
		}
	}

	public static void closeConnection() {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			LOGGER.error(e.toString(), e);
		}
	}

}
