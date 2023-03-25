package com.codef.codesnippets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.codef.xsalt.utils.XSaLTDataUtils;
import com.codef.xsalt.utils.XSaLTObjectUtils;

public class HTwoDatabaseStarter {

	public static String dbPath = "C:\\\\bobo\\bobo";
	public static String dbUsername = "steve";
	public static String dbPassword = "bobo";

	public static void main(String[] args) throws SQLException, IOException {

		Connection conn = XSaLTDataUtils.getLocalH2Connection(dbPath, dbUsername, dbPassword);
		XSaLTDataUtils.importDelimitedFile(conn, "C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\sample_data\\REGISTRY.tab", "REGISTRY", null, "H2", true, false, false, false,
				false, false);

		ArrayList<HashMap<String, String>> regTable = XSaLTDataUtils.makeSQLAsArrayListHashMap(conn, "SELECT * FROM REGISTRY");


		System.out.println(XSaLTObjectUtils.printArrayListHashMap(regTable, "\t"));

	}



}
