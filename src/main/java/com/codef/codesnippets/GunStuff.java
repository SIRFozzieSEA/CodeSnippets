package com.codef.codesnippets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.codef.xsalt.utils.XSaLTDataUtils;
import com.codef.xsalt.utils.XSaLTStringUtils;

public class GunStuff {

	public static String GUN_PHOTO_LOCATIONS = "E:\\Documents\\Personal\\Gun Stuff\\Serials Photos\\";

	public static void main(String[] args)
			throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, IOException {

		Connection connAccess = XSaLTDataUtils.getAccessConnection("E:\\Documents\\Personal\\Gun Stuff\\GunData.accdb");
		Connection connMySQL = XSaLTDataUtils.getMySQLConnection("localhost", "gun_app", "root", "boboshan69!");
		makeTablesMySQL(connMySQL);
		loadTablesFromAccess(connAccess, connMySQL);
		buildCleaningReport(connMySQL);
		buildShotReport(connMySQL);
		buildShotReportByCaliber(connMySQL);
//		showDatabaseRowSummary(connMySQL);

//		Connection connH2 = XSaLTDataUtils.getLocalH2Connection("~/GunData", "sa", "");
//		makeTablesH2(connH2);
//		loadTablesFromAccess(connAccess, connH2);
//		buildCleaningReport(connH2);
//		connH2.close();

		pickRandomQuestionsForTrivia(connMySQL, "Steve", 20);
		pickRandomQuestionsForTrivia(connMySQL, "Tenzin", 20);

		connMySQL.close();
		connAccess.close();

		System.out.println("DONE!");

	}

	public static void pickRandomQuestionsForTrivia(Connection connMySQL, String user, int numberOfQuestions) throws SQLException {

		PreparedStatement ps = XSaLTDataUtils.createPreparedStatementForReturnKeys(connMySQL,
				"insert into gun_trivia_rounds (ROUND_USER, ROUND_NO_OF_QUESTIONS) values (?,?)");
		ps.setString(1, user);
		ps.setLong(2, numberOfQuestions);
		String roundPk = XSaLTDataUtils.executePreparedStatementGetKey(connMySQL, ps);
		
		Long totalQuestionsAvailable = XSaLTDataUtils.getRowsCountInDataTable(connMySQL, "gun_trivia_questions");

		HashSet<Integer> randomIds = new HashSet<Integer>();
		Random random = new Random();
		while (true) {
			randomIds.add(random.nextInt(totalQuestionsAvailable.intValue()));
			if (randomIds.size() == numberOfQuestions)
				break;
		}
		
		for (int singleId : randomIds) {
			String sql = "SELECT * FROM gun_trivia_questions where TRIVIA_PK = " + singleId;
			Statement statement = connMySQL.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				ps = XSaLTDataUtils.createPreparedStatementForReturnKeys(connMySQL,
						"insert into gun_trivia_game (ROUND_PK, TRIVIA_PK, QUESTION_TYPE, QUESTION, QUESTION_RESPONSES, CORRECT_RESPONSE, IMAGE_LOCATION) values (?,?,?,?,?,?,?)");
				ps.setLong(1, Long.parseLong(roundPk));
				ps.setLong(2, result.getLong("TRIVIA_PK"));
				ps.setString(3, result.getString("QUESTION_TYPE"));
				ps.setString(4, result.getString("QUESTION"));
				ps.setString(5, result.getString("QUESTION_RESPONSES"));
				ps.setString(6, result.getString("CORRECT_RESPONSE"));
				ps.setString(7, result.getString("IMAGE_LOCATION"));
				ps.executeUpdate();
			}

		}

	}

	public static void verifyData(Connection connMySQL) {

	}

	public static void loadTablesFromAccess(Connection connAccess, Connection connMySQL) throws SQLException {

		HashMap<String, Long> generatedKeysForGunNames = new HashMap<String, Long>();
		HashMap<Long, String> gunIdTogunCalibers = new HashMap<Long, String>();
		TreeSet<String> allCaliberSet = new TreeSet<String>();
		TreeSet<String> allMakesSet = new TreeSet<String>();
		TreeSet<String> allModelSet = new TreeSet<String>();

		// main info
		String sql = "SELECT * FROM GunInfo ORDER BY GunName";
		Statement statement = connAccess.createStatement();
		ResultSet result = statement.executeQuery(sql);

		while (result.next()) {
			PreparedStatement ps = XSaLTDataUtils.createPreparedStatementForReturnKeys(connMySQL,
					"insert into gun_registry (SERIAL, NICKNAME, MAKE, MODEL, CALIBER, BOUGHT_DATE, ORIGINAL_COST, SIGHTED_DATE) values (?,?,?,?,?,?,?,?)");
			ps.setString(1, result.getString("SerialNo"));
			ps.setString(2, result.getString("GunName"));
			ps.setString(3, result.getString("GunMake"));
			ps.setString(4, result.getString("GunModel"));
			ps.setString(5, result.getString("Caliber"));
			ps.setString(6, result.getString("BoughtDate"));
			ps.setString(7, result.getString("Cost"));
			ps.setString(8, result.getString("SightedDate"));

			allCaliberSet.add(result.getString("Caliber"));
			allMakesSet.add(result.getString("GunMake"));
			allModelSet.add(result.getString("GunModel"));

			String keyForNewGun = XSaLTDataUtils.executePreparedStatementGetKey(connMySQL, ps);
			generatedKeysForGunNames.put(result.getString("GunName"), Long.parseLong(keyForNewGun));
			gunIdTogunCalibers.put(Long.parseLong(keyForNewGun), result.getString("Caliber"));

		}

		// cleaning info
		sql = "SELECT * FROM GunCleanings ORDER BY DateCleaned";
		statement = connAccess.createStatement();
		result = statement.executeQuery(sql);

		while (result.next()) {
			PreparedStatement ps = XSaLTDataUtils.createPreparedStatementForReturnKeys(connMySQL,
					"insert into gun_cleaning_sessions (GUN_PK, DATE_CLEANED) values (?,?)");
			ps.setLong(1, generatedKeysForGunNames.get(result.getString("Gun")));
			ps.setString(2, result.getString("DateCleaned"));
			ps.executeUpdate();
		}

		// shooting info
		sql = "SELECT * FROM GunRounds ORDER BY DateFired";
		statement = connAccess.createStatement();
		result = statement.executeQuery(sql);

		while (result.next()) {
			PreparedStatement ps = XSaLTDataUtils.createPreparedStatementForReturnKeys(connMySQL,
					"insert into gun_shooting_sessions (GUN_PK, CALIBER, NO_OF_ROUNDS, DATE_FIRED) values (?,?,?,?)");
			ps.setLong(1, generatedKeysForGunNames.get(result.getString("Gun")));
			if (result.getString("Caliber") == null) {
				ps.setString(2, gunIdTogunCalibers.get(generatedKeysForGunNames.get(result.getString("Gun"))));
			} else {
				ps.setString(2, result.getString("Caliber"));
			}

			ps.setLong(3, Long.parseLong(result.getString("Rounds")));
			ps.setString(4, result.getString("DateFired"));
			ps.executeUpdate();
		}

		// ---------------------------------------------------------

		String allGunNames = generatedKeysForGunNames.keySet().toString();
		allGunNames = allGunNames.substring(1, allGunNames.length() - 1).replace(", ", "|");

		String allCalibers = allCaliberSet.toString();
		allCalibers = allCalibers.substring(1, allCalibers.length() - 1).replace(", ", "|");

		String allMakes = allMakesSet.toString();
		allMakes = allMakes.substring(1, allMakes.length() - 1).replace(", ", "|");

		String allModels = allModelSet.toString();
		allModels = allModels.substring(1, allModels.length() - 1).replace(", ", "|");

		// trivia tables

		sql = "SELECT * FROM GunTrivia";
		statement = connAccess.createStatement();
		result = statement.executeQuery(sql);

		while (result.next()) {
			PreparedStatement ps = XSaLTDataUtils.createPreparedStatementForReturnKeys(connMySQL,
					"insert into gun_trivia_questions (QUESTION_TYPE, QUESTION, QUESTION_RESPONSES, CORRECT_RESPONSE, IMAGE_LOCATION) values (?,?,?,?,?)");
			ps.setString(1, result.getString("QuestionType"));
			ps.setString(2, result.getString("Question"));
			ps.setString(3, result.getString("QuestionResponses"));
			ps.setString(4, result.getString("CorrectResponse"));
			ps.setString(5, result.getString("ImageLocation"));
			ps.executeUpdate();
		}

		// put standard questions

		sql = "SELECT * FROM GunInfo ORDER BY BoughtDate";
		statement = connAccess.createStatement();
		result = statement.executeQuery(sql);

		while (result.next()) {

			PreparedStatement ps = XSaLTDataUtils.createPreparedStatementForReturnKeys(connMySQL,
					"insert into gun_trivia_questions (QUESTION_TYPE, QUESTION, QUESTION_RESPONSES, CORRECT_RESPONSE, IMAGE_LOCATION) values (?,?,?,?,?)");
			ps.setString(1, "MULTIPLE_CHOICE");
			ps.setString(2, "What is this gun's nickname?");
			ps.setString(3, allGunNames);
			ps.setString(4, result.getString("GunName"));
			ps.setString(5, GUN_PHOTO_LOCATIONS + result.getString("GunName") + ".jpg");
			ps.executeUpdate();

			ps = XSaLTDataUtils.createPreparedStatementForReturnKeys(connMySQL,
					"insert into gun_trivia_questions (QUESTION_TYPE, QUESTION, QUESTION_RESPONSES, CORRECT_RESPONSE, IMAGE_LOCATION) values (?,?,?,?,?)");
			ps.setString(1, "MULTIPLE_CHOICE");
			ps.setString(2, "Who is this gun's manufacturer?");
			ps.setString(3, allMakes);
			ps.setString(4, result.getString("GunMake"));
			ps.setString(5, GUN_PHOTO_LOCATIONS + result.getString("GunName") + ".jpg");
			ps.executeUpdate();

			ps = XSaLTDataUtils.createPreparedStatementForReturnKeys(connMySQL,
					"insert into gun_trivia_questions (QUESTION_TYPE, QUESTION, QUESTION_RESPONSES, CORRECT_RESPONSE, IMAGE_LOCATION) values (?,?,?,?,?)");
			ps.setString(1, "MULTIPLE_CHOICE");
			ps.setString(2, "What is this gun's model?");
			ps.setString(3, allModels);
			ps.setString(4, result.getString("GunModel"));
			ps.setString(5, GUN_PHOTO_LOCATIONS + result.getString("GunName") + ".jpg");
			ps.executeUpdate();

			ps = XSaLTDataUtils.createPreparedStatementForReturnKeys(connMySQL,
					"insert into gun_trivia_questions (QUESTION_TYPE, QUESTION, QUESTION_RESPONSES, CORRECT_RESPONSE, IMAGE_LOCATION) values (?,?,?,?,?)");
			ps.setString(1, "MULTIPLE_CHOICE");
			ps.setString(2, "What is this gun's caliber?");
			ps.setString(3, allCalibers);
			ps.setString(4, result.getString("Caliber"));
			ps.setString(5, GUN_PHOTO_LOCATIONS + result.getString("GunName") + ".jpg");
			ps.executeUpdate();

		}

	}

	public static void buildCleaningReport(Connection connMySQL) throws SQLException, IOException {

		String sql = "SELECT gun_pk, MAX(date_cleaned) as last_date_cleaned FROM gun_cleaning_sessions group by gun_pk order by gun_pk";
		Statement statement = connMySQL.createStatement();
		ResultSet result = statement.executeQuery(sql);

		while (result.next()) {

			long gunPk = result.getLong("gun_pk");
			Date lastCleanedDate = result.getDate("last_date_cleaned");
			String sqlSelect = "SELECT GUN_PK, NO_OF_ROUNDS, DATE_FIRED FROM gun_shooting_sessions WHERE GUN_PK = "
					+ gunPk + " AND DATE_FIRED > '" + lastCleanedDate + "' ORDER BY DATE_FIRED";
			String sqlTwo = "INSERT INTO gun_cleaning_reporting (GUN_PK, NO_OF_ROUNDS, DATE_FIRED) " + sqlSelect;
			XSaLTDataUtils.executeSQL(connMySQL, sqlTwo);

		}

		sql = "SELECT r.NICKNAME, r.CALIBER, SUM(p.NO_OF_ROUNDS) as TOTAL_ROUNDS_FIRED, MAX(p.DATE_FIRED) AS LAST_DATE_FIRED "
				+ "FROM gun_cleaning_reporting p LEFT JOIN gun_registry r on p.GUN_PK = r.GUN_PK "
				+ "group by r.NICKNAME order by r.NICKNAME ;";
		XSaLTDataUtils.exportSQLAsTabDelimitedDataFile(connMySQL, sql,
				"E:\\CleaningReport_" + XSaLTStringUtils.getDateString() + ".tab");

	}

	public static void buildShotReport(Connection connMySQL) throws SQLException, IOException {

		String sql = "SELECT r.NICKNAME, r.CALIBER, MAX(p.DATE_FIRED) AS LAST_DATE_FIRED, sum(NO_OF_ROUNDS) AS TOTAL_ROUNDS_FIRED "
				+ "FROM gun_shooting_sessions p LEFT JOIN gun_registry r on p.GUN_PK = r.GUN_PK "
				+ "group by r.NICKNAME order by r.NICKNAME ;";
		XSaLTDataUtils.exportSQLAsTabDelimitedDataFile(connMySQL, sql,
				"E:\\LastShotReport_" + XSaLTStringUtils.getDateString() + ".tab");

	}

	public static void buildShotReportByCaliber(Connection connMySQL) throws SQLException, IOException {

		String sql = "SELECT CALIBER, SUM(NO_OF_ROUNDS) AS TOTAL_ROUNDS_FIRED FROM gun_app.gun_shooting_sessions "
				+ "GROUP BY CALIBER ORDER BY CALIBER;";
		XSaLTDataUtils.exportSQLAsTabDelimitedDataFile(connMySQL, sql,
				"E:\\LastShotReportByCaliber_" + XSaLTStringUtils.getDateString() + ".tab");

	}

	public static void showDatabaseRowSummary(Connection connMySQL) throws SQLException {
		String tableName = "gun_registry";
		System.out.println("Table " + tableName + ": " + XSaLTDataUtils.getRowsCountInDataTable(connMySQL, tableName));
		tableName = "gun_shooting_sessions";
		System.out.println("Table " + tableName + ": " + XSaLTDataUtils.getRowsCountInDataTable(connMySQL, tableName));
		tableName = "gun_cleaning_sessions";
		System.out.println("Table " + tableName + ": " + XSaLTDataUtils.getRowsCountInDataTable(connMySQL, tableName));
		tableName = "gun_trivia_questions";
		System.out.println("Table " + tableName + ": " + XSaLTDataUtils.getRowsCountInDataTable(connMySQL, tableName));
	}

	public static void makeTablesMySQL(Connection connMySQL) throws SQLException {

		Set<String> tableNames = new LinkedHashSet<String>();
		tableNames.add("gun_registry");
		tableNames.add("gun_shooting_sessions");
		tableNames.add("gun_cleaning_sessions");
		tableNames.add("gun_cleaning_reporting");
		tableNames.add("gun_trivia_rounds");
		tableNames.add("gun_trivia_questions");
		tableNames.add("gun_trivia_game");
		dropTables(connMySQL, tableNames);

		StringBuffer sCreateTableSQL = new StringBuffer();
		sCreateTableSQL.append("CREATE TABLE `gun_cleaning_sessions` (");
		sCreateTableSQL.append("  `CLEAN_PK` bigint(20) unsigned NOT NULL auto_increment,");
		sCreateTableSQL.append("  `GUN_PK` bigint(20) unsigned,");
		sCreateTableSQL.append("  `DATE_CLEANED` DATETIME,");
		sCreateTableSQL.append("  PRIMARY KEY  (`CLEAN_PK`)");
		sCreateTableSQL.append(") ENGINE=InnoDB");
		XSaLTDataUtils.executeSQL(connMySQL, sCreateTableSQL.toString());

		sCreateTableSQL = new StringBuffer();
		sCreateTableSQL.append("CREATE TABLE `gun_shooting_sessions` (");
		sCreateTableSQL.append("  `SHOOT_PK` bigint(20) unsigned NOT NULL auto_increment,");
		sCreateTableSQL.append("  `GUN_PK` bigint(20) unsigned,");
		sCreateTableSQL.append("  `CALIBER` varchar(10)  default '',");
		sCreateTableSQL.append("  `NO_OF_ROUNDS` bigint(20) unsigned default '0',");
		sCreateTableSQL.append("  `DATE_FIRED` DATETIME,");
		sCreateTableSQL.append("  PRIMARY KEY  (`SHOOT_PK`)");
		sCreateTableSQL.append(") ENGINE=InnoDB");
		XSaLTDataUtils.executeSQL(connMySQL, sCreateTableSQL.toString());

		sCreateTableSQL = new StringBuffer();
		sCreateTableSQL.append("CREATE TABLE `gun_cleaning_reporting` (");
		sCreateTableSQL.append("  `CLEAN_PK` bigint(20) unsigned NOT NULL auto_increment,");
		sCreateTableSQL.append("  `GUN_PK` bigint(20) unsigned,");
		sCreateTableSQL.append("  `NO_OF_ROUNDS` bigint(20) unsigned default '0',");
		sCreateTableSQL.append("  `DATE_FIRED` DATETIME,");
		sCreateTableSQL.append("  PRIMARY KEY  (`CLEAN_PK`)");
		sCreateTableSQL.append(") ENGINE=InnoDB");
		XSaLTDataUtils.executeSQL(connMySQL, sCreateTableSQL.toString());

		sCreateTableSQL = new StringBuffer();
		sCreateTableSQL.append("CREATE TABLE `gun_registry` (");
		sCreateTableSQL.append("  `GUN_PK` bigint(20) unsigned NOT NULL auto_increment,");
		sCreateTableSQL.append("  `SERIAL` varchar(20)  default '',");
		sCreateTableSQL.append("  `NICKNAME` varchar(20)  default '',");
		sCreateTableSQL.append("  `MAKE` varchar(30)  default '',");
		sCreateTableSQL.append("  `MODEL` varchar(50)  default '',");
		sCreateTableSQL.append("  `CALIBER` varchar(10)  default '',");
		sCreateTableSQL.append("  `SIGHTED_DATE` varchar(20)  default '',");
		sCreateTableSQL.append("  `BOUGHT_DATE` DATETIME,");
		sCreateTableSQL.append("  `ORIGINAL_COST` DOUBLE (15,2) default '0',");
		sCreateTableSQL.append("  PRIMARY KEY  (`GUN_PK`)");
		sCreateTableSQL.append(") ENGINE=InnoDB");
		XSaLTDataUtils.executeSQL(connMySQL, sCreateTableSQL.toString());
		
		sCreateTableSQL = new StringBuffer();
		sCreateTableSQL.append("CREATE TABLE `gun_trivia_rounds` (");
		sCreateTableSQL.append("  `ROUND_PK` bigint(20) unsigned NOT NULL auto_increment,");
		sCreateTableSQL.append("  `ROUND_USER` varchar(20)  default '',");
		sCreateTableSQL.append("  `ROUND_NO_OF_QUESTIONS` bigint(20) default '0',");
		sCreateTableSQL.append("  `ROUND_NO_OF_QUESTIONS_CORRECT` bigint(20) default '0',");
		sCreateTableSQL.append("  `ROUND_SCORE` bigint(20) default '0',");
		sCreateTableSQL.append("  `ROUND_PLAYED` DATETIME,");
		sCreateTableSQL.append("  PRIMARY KEY  (`ROUND_PK`)");
		sCreateTableSQL.append(") ENGINE=InnoDB");
		XSaLTDataUtils.executeSQL(connMySQL, sCreateTableSQL.toString());

		sCreateTableSQL = new StringBuffer();
		sCreateTableSQL.append("CREATE TABLE `gun_trivia_questions` (");
		sCreateTableSQL.append("  `TRIVIA_PK` bigint(20) unsigned NOT NULL auto_increment,");
		sCreateTableSQL.append("  `QUESTION_TYPE` varchar(20)  default '',");
		sCreateTableSQL.append("  `QUESTION` varchar(2000)  default '',");
		sCreateTableSQL.append("  `QUESTION_RESPONSES` varchar(2000)  default '',");
		sCreateTableSQL.append("  `CORRECT_RESPONSE` varchar(50)  default '',");
		sCreateTableSQL.append("  `IMAGE_LOCATION` varchar(200)  default '',");
		sCreateTableSQL.append("  PRIMARY KEY  (`TRIVIA_PK`)");
		sCreateTableSQL.append(") ENGINE=InnoDB");
		XSaLTDataUtils.executeSQL(connMySQL, sCreateTableSQL.toString());

		sCreateTableSQL = new StringBuffer();
		sCreateTableSQL.append("CREATE TABLE `gun_trivia_game` (");
		sCreateTableSQL.append("  `ROUND_PK` bigint(20) unsigned NOT NULL,");
		sCreateTableSQL.append("  `QUESTION_PK` bigint(20) unsigned NOT NULL auto_increment,");
		sCreateTableSQL.append("  `TRIVIA_PK` bigint(20) unsigned NOT NULL,");
		sCreateTableSQL.append("  `QUESTION_IS_ANSWERED` varchar(1)  default 'N',");
		sCreateTableSQL.append("  `QUESTION_IS_CORRECT` varchar(1)  default '',");
		sCreateTableSQL.append("  `QUESTION_TYPE` varchar(20)  default '',");
		sCreateTableSQL.append("  `QUESTION` varchar(2000)  default '',");
		sCreateTableSQL.append("  `QUESTION_RESPONSES` varchar(2000)  default '',");
		sCreateTableSQL.append("  `CORRECT_RESPONSE` varchar(50)  default '',");
		sCreateTableSQL.append("  `USER_RESPONSE` varchar(50)  default '',");
		sCreateTableSQL.append("  `IMAGE_LOCATION` varchar(200)  default '',");
		sCreateTableSQL.append("  PRIMARY KEY  (`QUESTION_PK`)");
		sCreateTableSQL.append(") ENGINE=InnoDB");
		XSaLTDataUtils.executeSQL(connMySQL, sCreateTableSQL.toString());

	}

	/*
	 * 
	 * public static void makeTablesH2(Connection connH2) throws SQLException {
	 * 
	 * Set<String> tableNames = new LinkedHashSet<String>();
	 * tableNames.add("gun_registry"); tableNames.add("gun_shooting_sessions");
	 * tableNames.add("gun_cleaning_sessions");
	 * tableNames.add("gun_cleaning_reporting"); dropTables(connH2, tableNames);
	 * 
	 * StringBuffer sCreateTableSQL = new StringBuffer();
	 * sCreateTableSQL.append("CREATE TABLE `gun_cleaning_sessions` (");
	 * sCreateTableSQL.append("  `CLEAN_PK` INT AUTO_INCREMENT PRIMARY KEY,");
	 * sCreateTableSQL.append("  `GUN_PK` INT UNSIGNED,");
	 * sCreateTableSQL.append("  `DATE_CLEANED` TIMESTAMP");
	 * sCreateTableSQL.append(")"); XSaLTDataUtils.executeSQL(connH2,
	 * sCreateTableSQL.toString());
	 * 
	 * sCreateTableSQL = new StringBuffer();
	 * sCreateTableSQL.append("CREATE TABLE `gun_shooting_sessions` (");
	 * sCreateTableSQL.append("  `SHOOT_PK` INT AUTO_INCREMENT PRIMARY KEY,");
	 * sCreateTableSQL.append("  `GUN_PK` INT UNSIGNED,");
	 * sCreateTableSQL.append("  `CALIBER` varchar(10)  default '',");
	 * sCreateTableSQL.append("  `NO_OF_ROUNDS` INT UNSIGNED default '0',");
	 * sCreateTableSQL.append("  `DATE_FIRED` TIMESTAMP");
	 * sCreateTableSQL.append(")"); XSaLTDataUtils.executeSQL(connH2,
	 * sCreateTableSQL.toString());
	 * 
	 * sCreateTableSQL = new StringBuffer();
	 * sCreateTableSQL.append("CREATE TABLE `gun_cleaning_reporting` (");
	 * sCreateTableSQL.append("  `CLEAN_PK` INT AUTO_INCREMENT PRIMARY KEY,");
	 * sCreateTableSQL.append("  `GUN_PK` INT UNSIGNED,");
	 * sCreateTableSQL.append("  `NO_OF_ROUNDS` INT UNSIGNED default '0',");
	 * sCreateTableSQL.append("  `DATE_FIRED` TIMESTAMP");
	 * sCreateTableSQL.append(")"); XSaLTDataUtils.executeSQL(connH2,
	 * sCreateTableSQL.toString());
	 * 
	 * sCreateTableSQL = new StringBuffer();
	 * sCreateTableSQL.append("CREATE TABLE `gun_registry` (");
	 * sCreateTableSQL.append("  `GUN_PK` INT AUTO_INCREMENT PRIMARY KEY,");
	 * sCreateTableSQL.append("  `SERIAL` varchar(20)  default '',");
	 * sCreateTableSQL.append("  `NICKNAME` varchar(20)  default '',");
	 * sCreateTableSQL.append("  `MAKE` varchar(30)  default '',");
	 * sCreateTableSQL.append("  `MODEL` varchar(50)  default '',");
	 * sCreateTableSQL.append("  `CALIBER` varchar(10)  default '',");
	 * sCreateTableSQL.append("  `SIGHTED_DATE` varchar(20)  default '',");
	 * sCreateTableSQL.append("  `BOUGHT_DATE` TIMESTAMP,");
	 * sCreateTableSQL.append("  `ORIGINAL_COST` DECIMAL(15,2) default '0'");
	 * sCreateTableSQL.append(")"); XSaLTDataUtils.executeSQL(connH2,
	 * sCreateTableSQL.toString());
	 * 
	 * }
	 * 
	 */

	public static void dropTables(Connection conn, Set<String> tableNames) throws SQLException {
		for (String tableName : tableNames) {
			XSaLTDataUtils.executeSQL(conn, "DROP TABLE IF EXISTS `" + tableName + "`");
		}
	}

	public static void createGunCleaningTable(Connection conn) throws SQLException {
		StringBuffer sCreateTableSQL = new StringBuffer();
		sCreateTableSQL.append("CREATE TABLE `GunsToClean` (Gun CHAR, DateFired DATETIME, Rounds SMALLINT)");
		XSaLTDataUtils.executeSQL(conn, sCreateTableSQL.toString());
	}

	public static void emptyGunCleaningTable(Connection conn) throws SQLException {
		StringBuffer sEmptyTableSQL = new StringBuffer();
		sEmptyTableSQL.append("DELETE FROM `GunsToClean`");
		XSaLTDataUtils.executeSQL(conn, sEmptyTableSQL.toString());
	}

	public static void dropGunCleaningTable(Connection conn) {
		try {
			StringBuffer sDropTableSQL = new StringBuffer();
			sDropTableSQL.append("DROP TABLE IF EXISTS `GunsToClean`");
			XSaLTDataUtils.executeSQL(conn, sDropTableSQL.toString());

		} catch (SQLException e) {
			System.out.println("drop didn't work, emptying table");
			try {
				StringBuffer sEmptyTableSQL = new StringBuffer();
				sEmptyTableSQL.append("DELETE FROM `GunsToClean`");
				XSaLTDataUtils.executeSQL(conn, sEmptyTableSQL.toString());
			} catch (SQLException e1) {
				System.out.println("emptying table messed up too");
			}
		}
	}

}
