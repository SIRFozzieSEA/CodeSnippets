package com.codef.helpers;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import com.codef.xsalt.utils.XSaLTFileSystemUtils;
import com.codef.xsalt.utils.XSaLTStringUtils;

public class FacebookFriendParser {

	private static final Logger LOGGER = Logger.getLogger(FacebookFriendParser.class.getName());

	private static String friendsFolder = "E:/Documents/Personal/Old Social Profiles/";

	private JFrame frmFacebookFriendParser;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FacebookFriendParser window = new FacebookFriendParser();
					window.frmFacebookFriendParser.setVisible(true);
				} catch (Exception e) {
					LOGGER.error(e.toString(), e);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FacebookFriendParser() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmFacebookFriendParser = new JFrame();
		frmFacebookFriendParser.setTitle("Facebook Friend Parser");
		frmFacebookFriendParser.setBounds(100, 100, 636, 533);
		frmFacebookFriendParser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmFacebookFriendParser.getContentPane().setLayout(null);

		JTextArea textArea = new JTextArea();
		textArea.setBounds(10, 11, 600, 438);
		frmFacebookFriendParser.getContentPane().add(textArea);

		JLabel lblNewLabel = new JLabel("Save Directory:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(111, 460, 116, 23);
		frmFacebookFriendParser.getContentPane().add(lblNewLabel);

		textField = new JTextField();
		textField.setBounds(237, 460, 373, 23);
		textField.setText(friendsFolder);
		frmFacebookFriendParser.getContentPane().add(textField);
		textField.setColumns(10);

		JButton btnNewButton = new JButton("Parse");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					writeFriendsFile(textArea, textField);
				} catch (IOException e1) {
					LOGGER.error(e1.toString(), e1);
				}
			}
		});
		btnNewButton.setBounds(10, 460, 89, 23);
		frmFacebookFriendParser.getContentPane().add(btnNewButton);

	}

	private void writeFriendsFile(JTextArea copyPasteArea, JTextField friendsFolderTextField) throws IOException {

		Set<String> facebookNamesFinal = new TreeSet<String>();
		String[] facebookNames = copyPasteArea.getText().split("\\n");

		for (String singleName : facebookNames) {
			if (singleName.indexOf("mutual") == -1 && singleName.indexOf("Works at") == -1
					&& singleName.indexOf("University of") == -1 && singleName.indexOf("Manager") == -1
					&& singleName.indexOf("Artist at") == -1 && singleName.indexOf("Attorney") == -1
					&& singleName.indexOf("Teacher") == -1 && singleName.indexOf("Hogwarts") == -1
					&& singleName.indexOf("Hampshire") == -1 && singleName.indexOf("Research") == -1
					&& singleName.indexOf("College") == -1 && singleName.indexOf("Make Up Forever") == -1
					&& singleName.indexOf("School") == -1 && singleName.indexOf("University") == -1
					&& singleName.indexOf("Self-Employed") == -1 && singleName.indexOf("Seattle") == -1
					&& singleName.indexOf("Consulting Firm") == -1) {
				facebookNamesFinal.add(singleName);
			}
		}

		StringBuffer outBuffer = new StringBuffer();
		for (String newName : facebookNamesFinal) {
			outBuffer.append(newName);
			outBuffer.append("\n");
		}

		XSaLTFileSystemUtils.writeStringBufferToFile(outBuffer,
				friendsFolderTextField.getText() + "Friends_" + XSaLTStringUtils.getDateString() + ".txt");

		LOGGER.info(
				"Wrote " + friendsFolderTextField.getText() + "Friends_" + XSaLTStringUtils.getDateString() + ".txt");

		copyPasteArea.setText("");

	}

}
