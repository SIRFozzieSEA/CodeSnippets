package com.codef.codesnippets;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

public class CodefRobot {

//	private static final Logger LOGGER = Logger.getLogger(CodefRobot.class.getName());

	public static void getKeyPressKeysForString(Robot robot, String stringForKeypress, int nKeyPressDelay) {
		for (int i = 0; i < stringForKeypress.length(); i++) {
			char chary = stringForKeypress.charAt(i);
			typeCharacter(robot, chary, nKeyPressDelay);
		}

	}

	public static void typeCharacter(Robot robot, char letter, int delay) {

		if (Character.isLetter(letter) || Character.isDigit(letter)) {
			try {
				boolean upperCase = Character.isUpperCase(letter);
				String variableName = "VK_" + letter;

				Field field = KeyEvent.class.getField(variableName);
				int keyCode = field.getInt(KeyEvent.class);

				robot.delay(delay);

				if (upperCase)
					robot.keyPress(KeyEvent.VK_SHIFT);

				robot.keyPress(keyCode);
				robot.keyRelease(keyCode);

				if (upperCase)
					robot.keyRelease(KeyEvent.VK_SHIFT);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			typeSpecialCharacter(robot, letter);
		}
	}

	public static void typeSpecialCharacter(Robot robot, char letter) {

		switch (letter) {
		case '.':
			robot.keyPress(KeyEvent.VK_PERIOD);
			robot.keyRelease(KeyEvent.VK_PERIOD);
			break;
		case '!':
			robot.keyPress(KeyEvent.VK_SHIFT);
			robot.keyPress(KeyEvent.VK_1);
			robot.keyRelease(KeyEvent.VK_1);
			robot.keyRelease(KeyEvent.VK_SHIFT);
			break;
		case ' ':
			robot.keyPress(KeyEvent.VK_SPACE);
			robot.keyRelease(KeyEvent.VK_SPACE);
			break;
		case '?':
			robot.keyPress(KeyEvent.VK_SHIFT);
			robot.keyPress(KeyEvent.VK_SLASH);
			robot.keyRelease(KeyEvent.VK_SLASH);
			robot.keyRelease(KeyEvent.VK_SHIFT);
			break;
		case ',':
			robot.keyPress(KeyEvent.VK_COMMA);
			robot.keyRelease(KeyEvent.VK_COMMA);
			break;
		case '@':
			robot.keyPress(KeyEvent.VK_SHIFT);
			robot.keyPress(KeyEvent.VK_2);
			robot.keyRelease(KeyEvent.VK_2);
			robot.keyRelease(KeyEvent.VK_SHIFT);
			break;
		case '\b':
			robot.keyPress(KeyEvent.VK_BACK_SPACE);
			robot.keyRelease(KeyEvent.VK_BACK_SPACE);
			break;
		case '\t':
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyRelease(KeyEvent.VK_TAB);
			break;
		case '\r':
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			break;
		case '\\':
			robot.keyPress(KeyEvent.VK_BACK_SLASH);
			robot.keyRelease(KeyEvent.VK_BACK_SLASH);
			break;
		case '/':
			robot.keyPress(KeyEvent.VK_SLASH);
			robot.keyRelease(KeyEvent.VK_SLASH);
			break;
		case ':':
			robot.keyPress(KeyEvent.VK_SHIFT);
			robot.keyPress(KeyEvent.VK_SEMICOLON);
			robot.keyRelease(KeyEvent.VK_SEMICOLON);
			robot.keyRelease(KeyEvent.VK_SHIFT);
			break;
		case '-':
			robot.keyPress(KeyEvent.VK_MINUS);
			robot.keyRelease(KeyEvent.VK_MINUS);
			break;
		case '\'':
			robot.keyPress(KeyEvent.VK_SHIFT);
			robot.keyPress(KeyEvent.VK_QUOTE);
			robot.keyRelease(KeyEvent.VK_QUOTE);
			robot.keyRelease(KeyEvent.VK_SHIFT);
			break;
		case '_':
			robot.keyPress(KeyEvent.VK_SHIFT);
			robot.keyPress(KeyEvent.VK_MINUS);
			robot.keyRelease(KeyEvent.VK_MINUS);
			robot.keyRelease(KeyEvent.VK_SHIFT);
			break;
		default:
			// do nothing
		}

	}

}
