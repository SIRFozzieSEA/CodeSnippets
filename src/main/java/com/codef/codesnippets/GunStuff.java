package com.codef.codesnippets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import com.codef.xsalt.utils.XSaLTDataUtils;

public class GunStuff {

	public static void main(String[] args)
			throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, IOException {

		Connection connAccess = XSaLTDataUtils.getAccessConnection("E:\\Documents\\Personal\\Gun Stuff\\GunData.accdb");
		Connection connMySQL = XSaLTDataUtils.getMySQLConnection("localhost", "gun_app", "root", "boboshan69!");

		makeTables(connMySQL);
		loadTablesFromAccess(connAccess, connMySQL);
		buildCleaningReport(connMySQL);

		connAccess.close();
		connMySQL.close();

		System.out.println("DONE!");

	}

	public static void verifyData(Connection connMySQL) {

	}

	public static void loadTablesFromAccess(Connection connAccess, Connection connMySQL) throws SQLException {

		HashMap<String, Long> generatedKeysForGunNames = new HashMap<String, Long>();
		HashMap<Long, String> gunIdTogunCalibers = new HashMap<Long, String>();

		// main info
		String sql = "SELECT * FROM GunInfo ORDER BY BoughtDate";
		Statement statement = connAccess.createStatement();
		ResultSet result = statement.executeQuery(sql);

		while (result.next()) {
			PreparedStatement ps = XSaLTDataUtils.createPreparedStatementForReturnKeys(connMySQL,
					"insert into gun_registry (SERIAL, NICKNAME, MAKE, MODEL, CALIBER, BOUGHT_DATE, ORIGINAL_COST) values (?,?,?,?,?,?,?)");
			ps.setString(1, result.getString("SerialNo"));
			ps.setString(2, result.getString("GunName"));
			ps.setString(3, result.getString("GunMake"));
			ps.setString(4, result.getString("GunModel"));
			ps.setString(5, result.getString("Caliber"));
			ps.setString(6, result.getString("BoughtDate"));
			ps.setString(7, result.getString("Cost"));

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

		sql = "SELECT r.NICKNAME, r.CALIBER, SUM(p.NO_OF_ROUNDS) as TOTAL_ROUNDS, MAX(p.DATE_FIRED) AS LAST_DATE_FIRED "
				+ "FROM gun_cleaning_reporting p LEFT JOIN gun_registry r on p.GUN_PK = r.GUN_PK "
				+ "group by r.NICKNAME order by r.NICKNAME ;";
		
		XSaLTDataUtils.exportSQLAsTabDelimitedDataFile(connMySQL, sql, "E:\\bobo.tab");

	}

	public static void makeTables(Connection connMySQL) throws SQLException {

		Set<String> tableNames = new LinkedHashSet<String>();
		tableNames.add("gun_registry");
		tableNames.add("gun_shooting_sessions");
		tableNames.add("gun_cleaning_sessions");
		tableNames.add("gun_cleaning_reporting");
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
		sCreateTableSQL.append("  `BOUGHT_DATE` DATETIME,");
		sCreateTableSQL.append("  `ORIGINAL_COST` DOUBLE (15,2) default '0',");
		sCreateTableSQL.append("  PRIMARY KEY  (`GUN_PK`)");
		sCreateTableSQL.append(") ENGINE=InnoDB");
		XSaLTDataUtils.executeSQL(connMySQL, sCreateTableSQL.toString());

	}

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
