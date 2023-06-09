package com.codef.codesnippets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.codef.xsalt.utils.XSaLTDataUtils;
import com.codef.xsalt.utils.XSaLTObjectUtils;

public class HTwoDatabaseStarter {

	private static final Logger LOGGER = LogManager.getLogger(HTwoDatabaseStarter.class.getName());

	public static final String DB_PATH = "C:\\\\bobo\\bobo";
	public static final String DB_USERNAME = "steve";
	private static final String DB_PASSWORD = "bobo";

	public static void main(String[] args) throws SQLException, IOException {

		Connection conn = XSaLTDataUtils.getLocalH2Connection(DB_PATH, DB_USERNAME, DB_PASSWORD);
		XSaLTDataUtils.importDelimitedFile(conn,
				"C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\sample_data\\REGISTRY.tab", "REGISTRY", null, "H2",
				true, false, false, false, false, false);

		ArrayList<HashMap<String, String>> regTable = XSaLTDataUtils.makeSQLAsArrayListHashMap(conn,
				"SELECT * FROM REGISTRY");

		LOGGER.info(XSaLTObjectUtils.printArrayListHashMap(regTable, "\t"));

	}

}
