package com.codef.codesnippets;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Scanner;

import com.codef.xsalt.utils.XSaLTDataUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONParserFull {

	public static void main(String[] args) throws SQLException {

		ObjectMapper objectMapper = new ObjectMapper();

		try {

			Scanner scanner = new Scanner(System.in);
			System.out.println("Enter DB Path");
			String dbPath = scanner.nextLine();
			scanner.close();

			Connection conn = XSaLTDataUtils.getLocalH2Connection(dbPath, "H2", "H2");

			String dropTable = "DROP TABLE SITE_TABLE IF EXISTS";
			conn.createStatement().execute(dropTable);

			String createSQL = "CREATE TABLE SITE_TABLE ( SITE_ID VARCHAR(50), LANGUAGE VARCHAR(50), SITE VARCHAR(50))";
			conn.createStatement().execute(createSQL);

			File jsonFile = new File("E:/sites.json");
			JsonNode jsonNode = objectMapper.readTree(jsonFile);
			insertSiteKeys(conn, "SITE_TABLE", jsonNode, "");

			System.out.println("site is: " + getSiteId(conn, "hotels.kr_ko"));

			conn.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String getSiteId(Connection conn, String site) throws SQLException {

		ResultSet rs = conn.createStatement()
				.executeQuery("SELECT SITE_ID FROM SITE_TABLE WHERE SITE like '" + site + "'");
		if (rs.next()) {
			return rs.getString("SITE_ID");
		}
		return "";

	}

	private static void insertSiteKeys(Connection conn, String tableName, JsonNode jsonNode, String currentPath)
			throws SQLException {
		Iterator<String> outsideKeys = jsonNode.fieldNames();
		while (outsideKeys.hasNext()) {
			String outsideKey = outsideKeys.next();
			JsonNode insideKey = jsonNode.get(outsideKey);

			String testPath = currentPath.isEmpty() ? outsideKey : currentPath + "\t" + outsideKey;
			if (!currentPath.isEmpty()) {
				String siteId = currentPath;
				String language = outsideKey;
				String site = insideKey.asText();

				String insertSql = "INSERT INTO " + tableName + " VALUES ('" + siteId + "', '" + language + "', '"
						+ site + "')";
				conn.createStatement().execute(insertSql);
			}

			if (insideKey.isObject() || insideKey.isArray()) {
				insertSiteKeys(conn, tableName, insideKey, testPath);
			}
		}
	}

}
