package com.codef.codesnippets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.codef.xsalt.utils.XSaLTDataUtils;

public class SGTest {

	public static void main(String[] args)
			throws SQLException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {

		Connection conn = XSaLTDataUtils.getPostgresConnection("localhost", "postgres", "postgres", "admin");

		XSaLTDataUtils.importDelimitedFile(conn, "C:\\Users\\sir_f\\Downloads\\redshift_api.txt", "INVENTORY",
				"VARCHAR", null, true, false, false, true, true, false);

		conn.close();

		System.out.println("done");

	}

}
