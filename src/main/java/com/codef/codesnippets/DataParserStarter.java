package com.codef.codesnippets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.codef.xsalt.arch.XSaLTSingleLineJsonProcess;
import com.codef.xsalt.arch.XSaLTSingleLineTextTabTableProcess;
import com.codef.xsalt.utils.XSaLTDataUtils;
import com.codef.xsalt.utils.XSaLTObjectUtils;

public class DataParserStarter {

	private static final Logger LOGGER = LogManager.getLogger(DataParserStarter.class.getName());

	private static final String DB_PATH = "E:\\bobo";
	private static final String DB_USERNAME = "steve";
	private static final String DB_PASSWORD = "bobo";
	private static final String DB_TABLENAME = "bobo";
	private static final String DB_TABLENAME_TWO = "bobo_two";

	public static void main(String[] args) throws SQLException, IOException {

		tryJson();
		tryTabFile();
	}

	public static void tryTabFile() throws SQLException, IOException {

		Connection conn = XSaLTDataUtils.getLocalH2Connection(DB_PATH, DB_USERNAME, DB_PASSWORD);

		XSaLTSingleLineTextTabTableProcess oXSaLTSingleLineTextTabTableProcess = new XSaLTSingleLineTextTabTableProcess();
		String tabFileFilePath = "C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\sample_data\\books_entries_as_tab_table.txt";

		XSaLTDataUtils.provisionTableWithColumns(conn, tabFileFilePath, DB_TABLENAME_TWO,
				oXSaLTSingleLineTextTabTableProcess::performOperation);

		ArrayList<HashMap<String, String>> regTable = XSaLTDataUtils.makeSQLAsArrayListHashMap(conn,
				"show columns from " + DB_TABLENAME_TWO);

		String returnValue = XSaLTObjectUtils.printArrayListHashMap(regTable, "\t");
		LOGGER.info(returnValue);

		oXSaLTSingleLineTextTabTableProcess.resetHeader();
		XSaLTDataUtils.writeTableWithColumns(conn, tabFileFilePath, DB_TABLENAME_TWO,
				oXSaLTSingleLineTextTabTableProcess::performOperation);

		regTable = XSaLTDataUtils.makeSQLAsArrayListHashMap(conn, "SELECT * from " + DB_TABLENAME_TWO);

		returnValue = XSaLTObjectUtils.printArrayListHashMap(regTable, "\t");
		LOGGER.info(returnValue);

//		XSaLTDataUtils.exportSQLAsTabDelimitedDataFile(conn, "SELECT * from " + DB_TABLENAME,
//		"C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\sample_data\\books_entries_as_tab_table.txt", true);

	}

	public static void tryJson() throws SQLException, IOException {

		Connection conn = XSaLTDataUtils.getLocalH2Connection(DB_PATH, DB_USERNAME, DB_PASSWORD);

		XSaLTSingleLineJsonProcess oXSaLTSingleLineJsonProcess = new XSaLTSingleLineJsonProcess();
		String jsonFilePath = "C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\sample_data\\books_entries_as_line.json";

		XSaLTDataUtils.provisionTableWithColumns(conn, jsonFilePath, DB_TABLENAME,
				oXSaLTSingleLineJsonProcess::performOperation);

		ArrayList<HashMap<String, String>> regTable = XSaLTDataUtils.makeSQLAsArrayListHashMap(conn,
				"show columns from " + DB_TABLENAME);

		String returnValue = XSaLTObjectUtils.printArrayListHashMap(regTable, "\t");
		LOGGER.info(returnValue);

		XSaLTDataUtils.writeTableWithColumns(conn, jsonFilePath, DB_TABLENAME,
				oXSaLTSingleLineJsonProcess::performOperation);

		regTable = XSaLTDataUtils.makeSQLAsArrayListHashMap(conn, "SELECT * from " + DB_TABLENAME);

		returnValue = XSaLTObjectUtils.printArrayListHashMap(regTable, "\t");
		LOGGER.info(returnValue);

//		XSaLTDataUtils.exportSQLAsTabDelimitedDataFile(conn, "SELECT * from " + DB_TABLENAME,
//		"C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\sample_data\\books_entries_as_tab_table.txt", true);

	}

}
