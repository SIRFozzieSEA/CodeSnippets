package com.codef.helpers;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import java.awt.Color;

public class HalfBoldWordsUI {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	    EventQueue.invokeLater(() -> {
	        try {
	        	HalfBoldWordsUI window = new HalfBoldWordsUI();
	            window.frame.setVisible(true);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });
	}

	/**
	 * Create the application.
	 */
	public HalfBoldWordsUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Bold It!");
		frame.setBounds(100, 100, 951, 600);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JTextArea inputBox = new JTextArea();
		inputBox.setBounds(10, 11, 915, 116);
		inputBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		inputBox.setLineWrap(true);
		frame.getContentPane().add(inputBox);

		JEditorPane outputBox = new JEditorPane();
		outputBox.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		outputBox.setEditable(false);
		outputBox.setBounds(10, 172, 915, 378);
		outputBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		frame.getContentPane().add(outputBox);

		JButton doItButton = new JButton("Bold It!");
		doItButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		doItButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				outputBox.setText(doBoldHalfWords(inputBox.getText()));
			}
		});
		doItButton.setBounds(10, 138, 915, 23);
		frame.getContentPane().add(doItButton);

	}

	private String doBoldHalfWords(String inputString) {
		String[] words = inputString.split(" ");
		StringBuilder returnSentence = new StringBuilder();
		returnSentence.append("<HTML><font face='Arial' size='4'>");

		for (int i = 0; i < words.length; i++) {
			String word = words[i];

			if (word.length() > 2) {
				int cutoffChar = (int) Math.ceil(Double.valueOf(i) / 2);
				returnSentence.append("<B>" + word.substring(0, cutoffChar + 1) + "</B>"
						+ word.substring(cutoffChar + 1, word.length()) + " ");
			} else {
				returnSentence.append("<B>" + word + "</B> ");
			}
		}

		returnSentence.append("</font></HTML>");
		return returnSentence.toString();
	}

}
