package com.codef.codesnippets;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DaysForCruise {

	public static void main(String[] args) {

		Calendar start = Calendar.getInstance();
		start.getTime();
		Calendar end = Calendar.getInstance();
		end.set(2018, 9, 14);
		Date startDate = start.getTime();
		Date endDate = end.getTime();
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		long diffTime = endTime - startTime;
		long diffDays = diffTime / (1000 * 60 * 60 * 24);
		DateFormat dateFormat = DateFormat.getDateInstance();
		
		System.out.println("The difference between " + dateFormat.format(startDate) + " and "
				+ dateFormat.format(endDate) + " is " + diffDays + " days.");
	}

}
