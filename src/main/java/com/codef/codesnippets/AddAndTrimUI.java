package com.codef.codesnippets;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AddAndTrimUI {

	private JFrame frmAddandtrim;
	private JTextField startEachLine;
	private JTextField discardAfterThisVal;
	private JTextField endEachLine;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AddAndTrimUI window = new AddAndTrimUI();
					window.frmAddandtrim.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AddAndTrimUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAddandtrim = new JFrame();
		frmAddandtrim.setTitle("Add-and-Trim");
		frmAddandtrim.setBounds(100, 100, 668, 533);
		frmAddandtrim.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAddandtrim.getContentPane().setLayout(null);

		JTextArea inputText = new JTextArea();
		inputText.setFont(new Font("Courier New", Font.PLAIN, 12));
		inputText.setBounds(10, 82, 632, 163);
		frmAddandtrim.getContentPane().add(inputText);

		startEachLine = new JTextField();
		startEachLine.setFont(new Font("Courier New", Font.PLAIN, 12));
		startEachLine.setBounds(10, 30, 204, 20);
		frmAddandtrim.getContentPane().add(startEachLine);
		startEachLine.setColumns(10);

		discardAfterThisVal = new JTextField();
		discardAfterThisVal.setFont(new Font("Courier New", Font.PLAIN, 12));
		discardAfterThisVal.setBounds(224, 30, 204, 20);
		frmAddandtrim.getContentPane().add(discardAfterThisVal);
		discardAfterThisVal.setColumns(10);

		JLabel lblNewLabel = new JLabel("Start each line with");
		lblNewLabel.setBounds(10, 11, 115, 14);
		frmAddandtrim.getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Clip line when this is found:");
		lblNewLabel_1.setBounds(224, 11, 174, 14);
		frmAddandtrim.getContentPane().add(lblNewLabel_1);

		JTextArea outputText = new JTextArea();
		outputText.setFont(new Font("Courier New", Font.PLAIN, 12));
		outputText.setBounds(8, 286, 634, 163);
		frmAddandtrim.getContentPane().add(outputText);

		JLabel lblNewLabel_2 = new JLabel("Input:");
		lblNewLabel_2.setBounds(10, 61, 46, 14);
		frmAddandtrim.getContentPane().add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("Output:");
		lblNewLabel_3.setBounds(10, 265, 46, 14);
		frmAddandtrim.getContentPane().add(lblNewLabel_3);

		endEachLine = new JTextField();
		endEachLine.setFont(new Font("Courier New", Font.PLAIN, 12));
		endEachLine.setColumns(10);
		endEachLine.setBounds(438, 30, 204, 20);
		frmAddandtrim.getContentPane().add(endEachLine);

		JLabel lblEndEachLine = new JLabel("End each line with");
		lblEndEachLine.setBounds(438, 11, 115, 14);
		frmAddandtrim.getContentPane().add(lblEndEachLine);

		JButton btnNewButton = new JButton("Process");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				outputText.setText(processText(startEachLine.getText(), discardAfterThisVal.getText(),
						endEachLine.getText(), inputText.getText()));
			}
		});
		btnNewButton.setBounds(10, 460, 632, 23);
		frmAddandtrim.getContentPane().add(btnNewButton);

	}

	private String processText(String startEachLine, String discardAfterThisVal, String endEachLine, String fromThis) {

		StringBuilder sb = new StringBuilder();
		String lines[] = fromThis.split("\\r?\\n");

		for (int i = 0; i < lines.length; i++) {
			String[] indLine = lines[i].split(discardAfterThisVal);
			sb.append(startEachLine + indLine[0].trim() + endEachLine + "\n");
		}

		return sb.toString();

	}
}
