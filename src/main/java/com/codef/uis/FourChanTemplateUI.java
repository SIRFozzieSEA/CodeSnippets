package com.codef.uis;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class FourChanTemplateUI {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	    EventQueue.invokeLater(() -> {
	        try {
	        	FourChanTemplateUI window = new FourChanTemplateUI();
	            window.frame.setVisible(true);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });
	}
	

	/**
	 * Create the application.
	 */
	public FourChanTemplateUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Chan it!");
		frame.setBounds(100, 100, 951, 600);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JTextArea inputBox = new JTextArea();
		inputBox.setBounds(10, 11, 915, 116);
		inputBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		inputBox.setLineWrap(true);
		frame.getContentPane().add(inputBox);

		JTextArea outputBox = new JTextArea();
		outputBox.setEditable(false);
		outputBox.setBounds(10, 172, 915, 378);
		outputBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		frame.getContentPane().add(outputBox);

		JButton doItButton = new JButton("Chan It!");
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

		if (words[0].equalsIgnoreCase("haha")) {
			int numOfHas = Integer.parseInt(words[1]);
			returnSentence.append("Ah");
			for (int i = 0; i < numOfHas; i++) {
				returnSentence.append(" ha");
			}
			returnSentence.append(" ... \n\n");
			returnSentence.append("[breathes in] \n\n");
			returnSentence.append("... ");
			for (int i = 0; i < numOfHas; i++) {
				returnSentence.append(" ha");
			}
			returnSentence.append("!\n\n");
			returnSentence.append("No.");
			
		} else {
			returnSentence.append(inputString);
		}

		return returnSentence.toString();
	}

}
