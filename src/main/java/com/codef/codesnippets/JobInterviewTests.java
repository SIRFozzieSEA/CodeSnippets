package com.codef.codesnippets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JobInterviewTests {

	/*
	 * Complete the 'minTime' function below.
	 *
	 * The function is expected to return an INTEGER. The function accepts following
	 * parameters: Example 1. INTEGER_ARRAY processorTime = [8,10] 2. INTEGER_ARRAY
	 * taskTime = [2,2,3,1,8,7,4,5] processor 1, 8+2, 8+x, 8+x, 8+x processor 2,
	 */

	public static void main(String[] args) {

		List<Integer> processorTime = new ArrayList<Integer>();
		processorTime.add(10);
		processorTime.add(20);

		List<Integer> taskTime = new ArrayList<Integer>();
		taskTime.add(2);
		taskTime.add(3);
		taskTime.add(1);
		taskTime.add(2);

		taskTime.add(5);
		taskTime.add(8);
		taskTime.add(4);
		taskTime.add(3);

		System.out.println("1:" + minTime(processorTime, taskTime));

		System.out.println(testAnagram("aab bba cac", "baa cca bab"));
		System.out.println(testAnagram("aab bba", "baa bdb"));

	}

	public static int minTime(List<Integer> processorTime, List<Integer> taskTime) {
		// Write your code here
		Collections.sort(taskTime);
		Collections.reverse(taskTime);
		Collections.sort(processorTime);

		int highestTime = 0;
		int runningTotalTime = 0;

		for (int i = 0; i < taskTime.size() / 4; i++) {

			List<Integer> taskTimeFour = taskTime.subList(i * 4, (i * 4) + 4);
			System.out.println("        set: " + taskTimeFour);
			System.out.println("    highest: " + taskTimeFour.get(0));
			System.out.println("processTime: " + processorTime.get(i));
			runningTotalTime = processorTime.get(i) + taskTimeFour.get(0);
			System.out.println("  totalTime: " + runningTotalTime);

			if (runningTotalTime > highestTime) {
				highestTime = runningTotalTime;
			}

			System.out.println("");

		}

		return highestTime;
	}

	public static boolean testAnagram(String s, String t) {

		String[] compareList = s.split(" ");
		String[] testList = t.split(" ");

		for (int i = 0; i < compareList.length; i++) {

			char[] compareChar = compareList[i].toCharArray();
			Arrays.sort(compareChar);
			boolean iterationFound = false;

			for (int j = 0; j < testList.length; j++) {
				char[] testChar = testList[j].toCharArray();
				Arrays.sort(testChar);
				if (Arrays.equals(compareChar, testChar)) {
					iterationFound = true;
					testList[j] = "";
					break;
				}
			}

			if (iterationFound == false) {
				return false;
			}

		}

		return true;

	}

}
