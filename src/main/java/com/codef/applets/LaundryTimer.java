package com.codef.applets;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class LaundryTimer extends JFrame {

	private static final long serialVersionUID = 1314542805586322898L;

	public LaundryTimer() {
		setTitle("Laundry Timer");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new GridLayout(2, 1, 10, 10));

		add(new MachinePanel("Washer"));
		add(new MachinePanel("Dryer"));

		pack();
		setLocationRelativeTo(null);
		setResizable(false);
	}

	static class MachinePanel extends JPanel {
		private static final long serialVersionUID = -1014401288335803419L;
		private final JTextField display;
		private final JSpinner minutesSpin;
		private final JButton startStopBtn;
		private final Timer tick;

		private int totalSeconds; // remaining
		@SuppressWarnings("unused")
		private int initialSeconds; // what was set, for restore on stop
		private boolean running = false;

		@SuppressWarnings("unused")
		MachinePanel(String label) {

			GridBagConstraints gbc = new GridBagConstraints();

			if (label.equals("Dryer")) {
				gbc.insets = new Insets(3, 10, 8, 10);
			} else {
				gbc.insets = new Insets(3, 10, 0, 10);
			}

			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;

			setLayout(new GridBagLayout());

			JLabel lbl = new JLabel(label, SwingConstants.LEFT);
			lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			add(lbl, gbc);

			display = new JTextField("00:00");
			display.setEditable(false);
			display.setHorizontalAlignment(SwingConstants.CENTER);
			display.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
			display.setBackground(Color.WHITE);
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.CENTER;
			add(display, gbc);

			int initialMinutes = 45;
			if (label.equals("Dryer")) {
				initialMinutes = 55;
			}

			minutesSpin = new JSpinner(new SpinnerNumberModel(initialMinutes, 1, 60, 1));
			startStopBtn = new JButton("Start");
			JButton setBtn = new JButton("Set");
			JButton clearBtn = new JButton("Clear");

			JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

			btnRow.add(minutesSpin);
			btnRow.add(setBtn);
			btnRow.add(clearBtn);
			btnRow.add(startStopBtn);

			gbc.gridy = 2;
			gbc.anchor = GridBagConstraints.CENTER;
			add(btnRow, gbc);

			startStopBtn.addActionListener(e -> toggleStartStop());
			setBtn.addActionListener(e -> applySet());
			clearBtn.addActionListener(e -> applyClear());

			tick = new Timer(1000, e -> {
				if (totalSeconds > 0) {
					totalSeconds--;
					display.setText(format(totalSeconds));
					if (totalSeconds == 0) {
						running = false;
						startStopBtn.setText("Start");
						Toolkit.getDefaultToolkit().beep();
						for (int i = 0; i < 3; i++)
							Toolkit.getDefaultToolkit().beep();
					}
				}
			});
		}

		private void toggleStartStop() {
			if (!running) {
				if (totalSeconds <= 0)
					return;
				running = true;
				startStopBtn.setText("Stop");
				tick.start();
			} else {
				running = false;
				startStopBtn.setText("Start");
				tick.stop();
			}
		}

		private void applySet() {
			tick.stop();
			running = false;
			startStopBtn.setText("Start");
			int mins = (Integer) minutesSpin.getValue();
			totalSeconds = initialSeconds = mins * 60;
			display.setText(format(totalSeconds));
		}

		private void applyClear() {
			tick.stop();
			running = false;
			startStopBtn.setText("Start");
			totalSeconds = initialSeconds = 0;
			display.setText("00:00");
		}

		private static String format(int secs) {
			return String.format("%02d:%02d", secs / 60, secs % 60);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new LaundryTimer().setVisible(true));
	}
}
