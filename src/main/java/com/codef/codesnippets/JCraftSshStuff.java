package com.codef.codesnippets;

import com.jcraft.jsch.JSchException;

public class JCraftSshStuff {

	private static int port;
	private static int timeout;

	private static String hostname;
	private static String username;
	private static String password;
	private static String knownHostPath;

	public static void main(String[] args) throws JSchException {

		JCraftManager instance = new JCraftManager(username, password, hostname, port, timeout, knownHostPath);

		instance.sendCommand("ls -lsa", null);

		instance.sendCommand("ls -lsa; tree", null);

		instance.sendCommand("echo 'hello'", password);

		instance.sendFile("C:/Filez/sampletextfilefromlocal.txt", "filez/sampletextfilefromlocal.txt");

		instance.sendFile("C:/Filez/samplezipfilefromlocal.zip", "filez/samplezipfilefromlocal.zip");

		instance.receiveFile("filez/sampletextfilefromserver.txt", "C:/Filez/sampletextfilefromserver.txt");

		instance.receiveFile("filez/samplezipfilefromserver.zip", "C:/Filez/samplezipfilefromserver.zip");

	}

}
