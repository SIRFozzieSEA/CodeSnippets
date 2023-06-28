package com.codef.uis;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class NokiaTexterUI {

	protected static HashMap<String, String> letterToKeys = new HashMap<>();
	protected static HashMap<String, String> keysToLetter = new HashMap<>();

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				NokiaTexterUI window = new NokiaTexterUI();
				window.frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the application.
	 */
	public NokiaTexterUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		initializeMaps();

		frame = new JFrame();
		frame.setTitle("Nokia It!");
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
		outputBox.setBounds(10, 202, 915, 348);
		outputBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		frame.getContentPane().add(outputBox);

		JButton encodeItButton = new JButton("Encode It!");
		encodeItButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		encodeItButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				outputBox.setText(translateLetterToKeys(inputBox.getText()));
			}
		});
		encodeItButton.setBounds(10, 138, 915, 23);
		frame.getContentPane().add(encodeItButton);

		JButton decodeItButton = new JButton("Decode It!");
		decodeItButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		decodeItButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				outputBox.setText(translateKeysToLetter(inputBox.getText()));
			}
		});
		decodeItButton.setBounds(10, 168, 915, 23);
		frame.getContentPane().add(decodeItButton);

	}

	public static String translateLetterToKeys(String translateString) {
		StringBuilder sb = new StringBuilder();
		String[] translateArray = translateString.toLowerCase().split("");

		for (int i = 0; i < translateArray.length; i++) {
			sb.append((letterToKeys.get(translateArray[i]) != null ? letterToKeys.get(translateArray[i]) + " " : ""));
		}

		return sb.toString();
	}

	public static String translateKeysToLetter(String translateString) {

		StringBuilder sb = new StringBuilder();
		String[] translateArray = translateString.toLowerCase().split(" ");

		for (int i = 0; i < translateArray.length; i++) {
			sb.append((keysToLetter.get(translateArray[i]) != null ? keysToLetter.get(translateArray[i]) : " "));
		}

		return sb.toString();

	}

	public static void initializeMaps() {

		letterToKeys.put("a", "2");
		letterToKeys.put("b", "22");
		letterToKeys.put("c", "222");
		letterToKeys.put("d", "3");
		letterToKeys.put("e", "33");
		letterToKeys.put("f", "333");
		letterToKeys.put("g", "4");
		letterToKeys.put("h", "44");
		letterToKeys.put("i", "444");
		letterToKeys.put("j", "5");
		letterToKeys.put("k", "55");
		letterToKeys.put("l", "555");
		letterToKeys.put("m", "6");
		letterToKeys.put("n", "66");
		letterToKeys.put("o", "666");
		letterToKeys.put("p", "7");
		letterToKeys.put("q", "77");
		letterToKeys.put("r", "777");
		letterToKeys.put("s", "7777");
		letterToKeys.put("t", "8");
		letterToKeys.put("u", "88");
		letterToKeys.put("v", "888");
		letterToKeys.put("w", "9");
		letterToKeys.put("x", "99");
		letterToKeys.put("y", "999");
		letterToKeys.put("z", "9999");
		letterToKeys.put(" ", "0");

		keysToLetter.put("2", "a");
		keysToLetter.put("22", "b");
		keysToLetter.put("222", "c");
		keysToLetter.put("3", "d");
		keysToLetter.put("33", "e");
		keysToLetter.put("333", "f");
		keysToLetter.put("4", "g");
		keysToLetter.put("44", "h");
		keysToLetter.put("444", "i");
		keysToLetter.put("5", "j");
		keysToLetter.put("55", "k");
		keysToLetter.put("555", "l");
		keysToLetter.put("6", "m");
		keysToLetter.put("66", "n");
		keysToLetter.put("666", "o");
		keysToLetter.put("7", "p");
		keysToLetter.put("77", "q");
		keysToLetter.put("777", "r");
		keysToLetter.put("7777", "s");
		keysToLetter.put("8", "t");
		keysToLetter.put("88", "u");
		keysToLetter.put("888", "v");
		keysToLetter.put("9", "w");
		keysToLetter.put("99", "x");
		keysToLetter.put("999", "y");
		keysToLetter.put("9999", "z");
		keysToLetter.put("0", " ");

	}

}
