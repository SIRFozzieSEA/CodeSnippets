package com.codef.codesnippets;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import com.codef.xsalt.utils.XSaLTDataUtils;
import com.codef.xsalt.utils.XSaLTStringUtils;

public class GunStuff {

	public static void main(String[] args)
			throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

//		Connection conn = XSaLTDataUtils.getAccessConnection("E:\\Documents\\Personal\\Gun Stuff\\GunData.accdb");

		Connection conn = XSaLTDataUtils.getMySQLConnection("localhost", "gun_app", "root", "boboshan69!");

		Set<String> tableNames = new LinkedHashSet<String>();
		tableNames.add("gun_registry");
		tableNames.add("gun_shooting_sessions");
		tableNames.add("gun_cleaning_sessions");
		tableNames.add("gun_cleaning_reporting");
		dropTables(conn, tableNames);

		StringBuffer sCreateTableSQL = new StringBuffer();
		sCreateTableSQL.append("CREATE TABLE `gun_cleaning_sessions` (");
		sCreateTableSQL.append("  `CLEAN_PK` bigint(20) unsigned NOT NULL auto_increment,");
		sCreateTableSQL.append("  `GUN_PK` bigint(20) unsigned,");
		sCreateTableSQL.append("  `DATE_CLEANED` DATETIME,");
		sCreateTableSQL.append("  PRIMARY KEY  (`CLEAN_PK`)");
		sCreateTableSQL.append(") ENGINE=InnoDB");
		XSaLTDataUtils.executeSQL(conn, sCreateTableSQL.toString());

		sCreateTableSQL = new StringBuffer();
		sCreateTableSQL.append("CREATE TABLE `gun_shooting_sessions` (");
		sCreateTableSQL.append("  `SHOOT_PK` bigint(20) unsigned NOT NULL auto_increment,");
		sCreateTableSQL.append("  `GUN_PK` bigint(20) unsigned,");
		sCreateTableSQL.append("  `CALIBER` varchar(10)  default '',");
		sCreateTableSQL.append("  `ROUNDS` bigint(20) unsigned default '0',");
		sCreateTableSQL.append("  `DATE_FIRED` DATETIME,");
		sCreateTableSQL.append("  PRIMARY KEY  (`SHOOT_PK`)");
		sCreateTableSQL.append(") ENGINE=InnoDB");
		XSaLTDataUtils.executeSQL(conn, sCreateTableSQL.toString());

		sCreateTableSQL = new StringBuffer();
		sCreateTableSQL.append("CREATE TABLE `gun_cleaning_reporting` (");
		sCreateTableSQL.append("  `CLEAN_PK` bigint(20) unsigned NOT NULL auto_increment,");
		sCreateTableSQL.append("  `GUN_PK` bigint(20) unsigned,");
		sCreateTableSQL.append("  `ROUNDS` bigint(20) unsigned default '0',");
		sCreateTableSQL.append("  `DATE_FIRED` DATETIME,");
		sCreateTableSQL.append("  PRIMARY KEY  (`CLEAN_PK`)");
		sCreateTableSQL.append(") ENGINE=InnoDB");
		XSaLTDataUtils.executeSQL(conn, sCreateTableSQL.toString());

		sCreateTableSQL = new StringBuffer();
		sCreateTableSQL.append("CREATE TABLE `gun_registry` (");
		sCreateTableSQL.append("  `GUN_PK` bigint(20) unsigned NOT NULL auto_increment,");
		sCreateTableSQL.append("  `SERIAL` varchar(20)  default '',");
		sCreateTableSQL.append("  `GUN_NAME` varchar(20)  default '',");
		sCreateTableSQL.append("  `GUN_MAKE` varchar(30)  default '',");
		sCreateTableSQL.append("  `GUN_MODEL` varchar(50)  default '',");
		sCreateTableSQL.append("  `CALIBER` varchar(10)  default '',");
		sCreateTableSQL.append("  `BOUGHT_DATE` DATETIME,");
		sCreateTableSQL.append("  `ORIGINAL_COST` DOUBLE (15,2) default '0',");
		sCreateTableSQL.append("  PRIMARY KEY  (`GUN_PK`)");
		sCreateTableSQL.append(") ENGINE=InnoDB");
		XSaLTDataUtils.executeSQL(conn, sCreateTableSQL.toString());

		conn.close();
		System.out.println("DONE!");

	}

	public static void dropTables(Connection conn, Set<String> tableNames) throws SQLException {
		for (String tableName : tableNames) {
			XSaLTDataUtils.executeSQL(conn, "DROP TABLE IF EXISTS `" + tableName + "`");
		}
	}

	public static void testStuff() throws ClassNotFoundException, SQLException {

		Connection conn = XSaLTDataUtils.getAccessConnection("E:\\Documents\\Personal\\Gun Stuff\\GunData.accdb");

//		dropGunCleaningTable(conn);
		createGunCleaningTable(conn);
		emptyGunCleaningTable(conn);

		String sql = "SELECT Gun, DateFired, Rounds FROM GunRounds order by Gun, DateFired";
		Statement statement = conn.createStatement();
		ResultSet result = statement.executeQuery(sql);

		while (result.next()) {
			String fullname = result.getString("Gun");
			Date dateFired = result.getDate("DateFired");
			int rounds = result.getInt("Rounds");
			String insertSql = "INSERT INTO `GunsToClean` (Gun, DateFired, Rounds) VALUES ('" + fullname + "', #"
					+ dateFired + "#, " + rounds + ")";
			XSaLTDataUtils.executeSQL(conn, insertSql);
		}

//		sql = "SELECT Gun, LAST(DateFired) as LastDateFired FROM GunRounds group by Gun order by Gun";
//		statement = conn.createStatement();
//		result = statement.executeQuery(sql);
//
//		while (result.next()) {
//			String fullname = result.getString("Gun");
//			Date lastdateFired = result.getDate("LastDateFired");
//			String insertSql = "INSERT INTO `GunsToClean` (Gun, LastDateFired) VALUES ('" + fullname + "', #" + lastdateFired + "#)";
//			System.out.println(insertSql);
//			XSaLTDataUtils.executeSQL(conn, insertSql);
//		}

		// one
		sql = "SELECT Gun, LAST(DateCleaned) as LastDateCleaned FROM GunCleanings group by Gun order by Gun";
		statement = conn.createStatement();
		result = statement.executeQuery(sql);

		while (result.next()) {
			String fullname = result.getString("Gun");
			Date lastDateCleaned = result.getDate("LastDateCleaned");
			String removeSql = "DELETE FROM `GunsToClean` WHERE Gun = '" + fullname + "' AND DateFired <= #"
					+ lastDateCleaned + "#";
			System.out.println(removeSql);
			XSaLTDataUtils.executeSQL(conn, removeSql);

		}

		conn.close();

		System.out.println("test");

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
