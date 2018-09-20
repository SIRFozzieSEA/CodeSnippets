package com.codef.codesnippets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JCraftManager {

	private static final Logger LOGGER = Logger.getLogger(JCraftManager.class.getName());

	private String username;
	private String password;

	private String hostname;
	private int port;
	private int timeout;

	private JSch jschChannel;
	private Session jscSession;

	public JCraftManager(String username, String password, String hostname, int port, int timeout,
			String pathToKnownHostFile) throws JSchException {

		jschChannel = new JSch();

		try {
			jschChannel.setKnownHosts(pathToKnownHostFile);

			this.username = username;
			this.password = password;

			this.hostname = hostname;
			this.port = port;
			this.timeout = timeout;

		} catch (JSchException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}

	}

	public String connect() {

		String errorMessage = null;

		try {
			jscSession = jschChannel.getSession(username, hostname, port);
			jscSession.setPassword(password);
			// sesConnection.setConfig("StrictHostKeyChecking", "no"); // TESTING ONLY
			jscSession.connect(timeout);
		} catch (JSchException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			errorMessage = e.getMessage();
		}

		return errorMessage;

	}

	public void close() {
		jscSession.disconnect();
	}

	public String sendCommand(String command, String sudoPass) {
		StringBuilder outputBuffer = new StringBuilder();

		if (sudoPass != null) {
			command = "sudo -S -p '' " + command;
		}

		try {
			Channel channel = jscSession.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			InputStream commandInput = channel.getInputStream();
			OutputStream commandOutput = channel.getOutputStream();
			channel.connect();

			if (sudoPass != null) {
				commandOutput.write((sudoPass + "\n").getBytes());
				commandOutput.flush();
			}

			int readByte = commandInput.read();

			while (readByte != 0xffffffff) {
				outputBuffer.append((char) readByte);
				readByte = commandInput.read();
			}

			channel.disconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			return null;
		}

		return outputBuffer.toString();
	}

	public void sendFile(String lfile, String rfile) {

		FileInputStream fis = null;
		try {

			boolean ptimestamp = true;

			// exec 'scp -t rfile' remotely
			String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + rfile;
			Channel channel = jscSession.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			if (checkAck(in) != 0) {
				System.exit(0);
			}

			File lfileTwo = new File(lfile);

			if (ptimestamp) {
				command = "T" + (lfileTwo.lastModified() / 1000) + " 0";
				// The access time should be sent here,
				// but it is not accessible with JavaAPI ;-<
				command += (" " + (lfileTwo.lastModified() / 1000) + " 0\n");
				out.write(command.getBytes());
				out.flush();
				if (checkAck(in) != 0) {
					System.exit(0);
				}
			}

			// send "C0644 filesize filename", where filename should not include '/'
			long filesize = lfileTwo.length();
			command = "C0644 " + filesize + " ";
			if (lfile.lastIndexOf('/') > 0) {
				command += lfile.substring(lfile.lastIndexOf('/') + 1);
			} else {
				command += lfile;
			}
			command += "\n";
			out.write(command.getBytes());
			out.flush();
			if (checkAck(in) != 0) {
				System.exit(0);
			}

			// send a content of lfile
			fis = new FileInputStream(lfile);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len);
			}
			fis.close();
			fis = null;
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if (checkAck(in) != 0) {
				System.exit(0);
			}
			out.close();

			channel.disconnect();

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			try {
				if (fis != null)
					fis.close();
			} catch (Exception ee) {
				LOGGER.log(Level.SEVERE, ee.toString(), ee);
			}
		}

	}

	public void receiveFile(String rfile, String lfile) {

		FileOutputStream fos = null;
		try {

			String prefix = null;
			if (new File(lfile).isDirectory()) {
				prefix = lfile + File.separator;
			}

			// exec 'scp -f rfile' remotely
			String command = "scp -f " + rfile;
			Channel channel = jscSession.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			byte[] buf = new byte[1024];

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			while (true) {
				int c = checkAck(in);
				if (c != 'C') {
					break;
				}

				// read '0644 '
				in.read(buf, 0, 5);

				long filesize = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						// error
						break;
					}
					if (buf[0] == ' ')
						break;
					filesize = filesize * 10L + (long) (buf[0] - '0');
				}

				String file = null;
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						file = new String(buf, 0, i);
						break;
					}
				}

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				// read a content of lfile
				fos = new FileOutputStream(prefix == null ? lfile : prefix + file);
				int foo;
				while (true) {
					if (buf.length < filesize)
						foo = buf.length;
					else
						foo = (int) filesize;
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						// error
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L)
						break;
				}
				fos.close();
				fos = null;

				if (checkAck(in) != 0) {
					System.exit(0);
				}

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}

			channel.disconnect();

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			try {
				if (fos != null)
					fos.close();
			} catch (Exception ee) {
				LOGGER.log(Level.SEVERE, ee.toString(), ee);
			}
		}

	}

	static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success; 1 for error; 2 for fatal error, -1
		if (b == 0)
			return b;
		if (b == -1)
			return b;
		if (b == 1 || b == 2) {
			StringBuilder sb = new StringBuilder();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
		}
		return b;
	}

}
