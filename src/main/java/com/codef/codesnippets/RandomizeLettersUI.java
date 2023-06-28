package com.codef.codesnippets;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import java.awt.Color;

public class RandomizeLettersUI {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	    EventQueue.invokeLater(() -> {
	        try {
	        	RandomizeLettersUI window = new RandomizeLettersUI();
	            window.frame.setVisible(true);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });
	}
	

	/**
	 * Create the application.
	 */
	public RandomizeLettersUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Randomize Letters!");
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

		JButton doItButton = new JButton("Randomize Letters!");
		doItButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		doItButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				outputBox.setText(randomizeLetters(inputBox.getText()));
			}
		});
		doItButton.setBounds(10, 138, 915, 23);
		frame.getContentPane().add(doItButton);

	}

	private String randomizeLetters(String inputString) {
		String[] words = inputString.split(" ");
		String[] newWords = new String[words.length];

		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (word.length() > 2) {
				char[] letters = word.substring(1, word.length() - 1).toCharArray();
				List<Character> lettersList = new ArrayList<>();
				for (char c : letters) {
					lettersList.add(c);
				}
				Collections.shuffle(lettersList);
				StringBuilder sb = new StringBuilder();
				sb.append(word.charAt(0));
				for (char c : lettersList) {
					sb.append(c);
				}
				sb.append(word.charAt(word.length() - 1));
				newWords[i] = sb.toString();
			} else {
				newWords[i] = word;
			}
		}

		return "<HTML><font face='Arial' size='4'>" + String.join(" ", newWords) + "</font></HTML>";
	}

}
