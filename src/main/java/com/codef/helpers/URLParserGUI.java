package com.codef.helpers;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class URLParserGUI {

	private static final Logger LOGGER = LogManager.getLogger(URLParserGUI.class.getName());

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				URLParserGUI window = new URLParserGUI();
				window.frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the application.
	 */
	public URLParserGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Parse It!");
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

		JButton doItButton = new JButton("Parse It!");
		doItButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		doItButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				outputBox.setText(parseIt(inputBox.getText()));
			}
		});
		doItButton.setBounds(10, 138, 915, 23);
		frame.getContentPane().add(doItButton);

	}

	private String parseIt(String inputString) {

		SortedMap<String, String> testMap = parseAllParameters("", inputString);
		return getParamsString(testMap);
	}

	public static SortedMap<String, String> parseAllParameters(String tlTag, String inputString) {

		SortedMap<String, String> inputMap = new TreeMap<>();
		if (inputString.contains("?")) {
			inputString = inputString.substring(inputString.indexOf("?") + 1);

			String[] params = inputString.split("&");

			try {
				for (String singleParam : params) {

					singleParam = java.net.URLDecoder.decode(singleParam, StandardCharsets.UTF_8);
					if (singleParam.contains("?")) {

						String nestedKey = singleParam.substring(0, singleParam.indexOf("="));
						String nestedPostKey = singleParam.substring(singleParam.indexOf("=") + 1,
								singleParam.indexOf("?"));
						String nestedParams = singleParam.substring(singleParam.indexOf("?") + 1);

						specialParamInsert(inputMap, tlTag, null, nestedKey, nestedPostKey);

						String[] paramsNested = java.net.URLDecoder.decode(nestedParams, StandardCharsets.UTF_8)
								.split("&");
						for (String singleNestedParam : paramsNested) {
							specialParamInsert(inputMap, tlTag, nestedKey,
									singleNestedParam.substring(0, singleNestedParam.indexOf("=")),
									singleNestedParam.substring(singleNestedParam.indexOf("=") + 1));
						}

					} else {
						try {
							specialParamInsert(inputMap, tlTag, null,
									singleParam.substring(0, singleParam.indexOf("=")),
									singleParam.substring(singleParam.indexOf("=") + 1));
						} catch (Exception ee) {
							specialParamInsert(inputMap, tlTag, null, "MISSING", singleParam);
						}
					}
				}
			} catch (IllegalArgumentException | StringIndexOutOfBoundsException ee) {
				LOGGER.warn("Unparseable line: {}", java.net.URLDecoder.decode(inputString, StandardCharsets.UTF_8));
			}

		}
		return inputMap;
	}

	public static void specialParamInsert(SortedMap<String, String> inputMap, String tlTag, String nestedKey,
			String key, String value) {

		if (key.equals("_")) {
			key = "UNDERSCORE";
		}
		value = value.trim();
		String paramPrefix = tlTag + "param_";

		if (nestedKey == null) {
			if (inputMap.containsKey(paramPrefix + key)) {
				key = paramPrefix + key + "_dupe";
				putInMapNoDupe(inputMap, key, value);
			} else {
				// do normal stuff
				putInMapNoDupe(inputMap, paramPrefix + key, value);
			}
		} else {
			key = paramPrefix + nestedKey + "_" + key;
			if (inputMap.containsKey(key)) {
				throw new RuntimeException("Nested param key '" + key + "' already exists!");
			} else {
				// do normal stuff
				putInMapNoDupe(inputMap, key, value);
			}
		}

	}

	public static void putInMapNoDupe(SortedMap<String, String> inputMap, String key, String value) {
		if (inputMap.containsKey(key.toLowerCase())) {
			key = key + "_dupe";
		}
		inputMap.put(key.toLowerCase(), value);
	}
	
	public static String getParamsString(SortedMap<String, String> inputMap) {
		StringBuilder sb = new StringBuilder("<font face='Arial' size='3'>");
		for (Map.Entry<String, String> entry : inputMap.entrySet()) {
			sb.append("<B>").append(entry.getKey()).append("</B> = '").append(entry.getValue()).append("'<BR>");
		}
		sb.append("</font>");
		return sb.toString();
	}

}
