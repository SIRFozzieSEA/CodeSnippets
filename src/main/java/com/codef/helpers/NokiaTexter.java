package com.codef.helpers;

import java.util.HashMap;

public class NokiaTexter {

	public static HashMap<String, String> letterToKeys = new HashMap<String, String>();
	public static HashMap<String, String> keysToLetter = new HashMap<String, String>();

	public static void main(String[] args) {

		initializeMaps();
		System.out.println(translateLetterToKeys("Thats a pretty big word for a retard"));
//		System.out.println(translateKeysToLetter(
//				"66 666 55 444 2 0 7 44 666 66 33 0 88 7777 33 777 7777 0 9 444 555 555 0 4 33 8 0 8 44 444 7777"));

		
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
