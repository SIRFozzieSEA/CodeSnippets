package com.codef.codesnippets;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AddAndTrimUI {

	private JFrame frame;
	private final Action action = new SwingAction();
	private JTextField addThisVal;
	private JTextField discardAfterThisVal;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AddAndTrimUI window = new AddAndTrimUI();
					window.frame.setVisible(true);
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
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(10, 42, 414, 174);
		frame.getContentPane().add(textArea);
		
		JButton btnNewButton = new JButton("Process");
		btnNewButton.setAction(action);
		btnNewButton.setBounds(10, 227, 414, 23);
		frame.getContentPane().add(btnNewButton);
		
		addThisVal = new JTextField();
		addThisVal.setBounds(10, 11, 204, 20);
		frame.getContentPane().add(addThisVal);
		addThisVal.setColumns(10);
		
		discardAfterThisVal = new JTextField();
		discardAfterThisVal.setBounds(220, 11, 204, 20);
		frame.getContentPane().add(discardAfterThisVal);
		discardAfterThisVal.setColumns(10);
		
		
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				processText(addThisVal.getText(), discardAfterThisVal.getText(), textArea.getText());
				
				
			}
		});
		
	}
	
	private void processText(String addThis, String removeAfter, String fromThis) {
		
		String lines[] = fromThis.split("\\r?\\n");
		
		for (int i = 0; i < lines.length; i++) {
			String[] indLine = lines[i].split(removeAfter);
			System.out.println(addThis + " " + indLine[0]);
		}
		
	}
	
	
	private class SwingAction extends AbstractAction {

		private static final long serialVersionUID = 7294366990678839454L;
		
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		
		public void actionPerformed(ActionEvent e) {
		}
	}
}
