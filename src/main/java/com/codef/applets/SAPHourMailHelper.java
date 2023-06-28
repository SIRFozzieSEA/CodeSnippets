package com.codef.applets;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class SAPHourMailHelper {
	
	// java -cp codesnippets-0.0.1-SNAPSHOT.jar com.codef.applets.SAPHourMailHelper

	public static boolean flagExecutingAtHome = true;

	// default home settings
	public static int targetMonitorIndex = 0;
	public static int imageXStart = 220;
	public static int imageYStart = 145;
	public static int imageWidth = 855;
	public static int imageHeight = 1050;
	public static String desktopPath = "C:\\Users\\sir_f\\Desktop\\";

	public static void main(String[] args) throws IOException, AWTException, URISyntaxException {

		System.out.println("**************************************************");
		System.out.println("*                                                *");
		System.out.println("*             SAP HOURS MAIL HELPER              *");
		System.out.println("*                                                *");
		System.out.println("**************************************************");

		System.out.println("");
		System.out.println("");

		Scanner scanner = new Scanner(System.in);
		System.out.print("(H)ome or (W)ork: ");
		String homeOrWork = scanner.nextLine();

		if (homeOrWork.equalsIgnoreCase("w")) {
			// put work stuff here
			targetMonitorIndex = 1;
			imageXStart = 220;
			imageYStart = 145;
			imageWidth = 855;
			imageHeight = 1050;
			desktopPath = "C:\\Users\\sir_f\\Desktop\\";
		}

		System.out.print("Enter the period dates: ");
		String dates = scanner.nextLine();

		System.out.print("Enter the total hours: ");
		int totalHours = Integer.parseInt(scanner.nextLine());

		String imageFilePath = createCaptureHoursImage(dates);
		makeEmail(dates, totalHours, imageFilePath);

		System.out.println("");
		System.out.println("");

		System.out.println("**************************************************");
		System.out.println("*                                                *");
		System.out.println("*        DON'T FORGET TO ATTACH THE IMAGE        *");
		System.out.println("*                                                *");
		System.out.println("**************************************************");

		System.out.println("");
		scanner.nextLine();
		scanner.close();

	}

	public static String createCaptureHoursImage(String periodDate) throws AWTException, IOException {

		String periodDateFileName = desktopPath + "COSSETTE - Hours " + periodDate.replace("/", "-") + ".jpg";

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gd = ge.getScreenDevices();
		GraphicsDevice targetDevice = gd[targetMonitorIndex];

		Rectangle targetBounds = targetDevice.getDefaultConfiguration().getBounds();

		Robot robot = new Robot(targetDevice);
		BufferedImage screenshot = robot.createScreenCapture(targetBounds);
		BufferedImage subImage = screenshot.getSubimage(imageXStart, imageYStart, imageWidth, imageHeight);

		File outputfile = new File(periodDateFileName);
		ImageIO.write(subImage, "jpg", outputfile);

		return periodDateFileName;

	}

	public static void makeEmail(String periodDate, int totalHours, String imageFilePath)
			throws IOException, URISyntaxException {

		String subject = "COSSETTE - Hours " + periodDate.replace("/", "-") + ".jpg";
		subject = subject.replace(" ", "%20");
		String recipient = "aimtimesheets@addisongroup.com";
		String body = "Hello There,LINEFEEDLINEFEEDPlease find attached my hours for period "
				+ periodDate.replace("/", "-") + ". Total of " + totalHours
				+ " hours.LINEFEEDLINEFEEDThanks!LINEFEEDSteve";
		body = body.replace(" ", "%20").replace("LINEFEED", "%0A");

		String uriString = String.format("mailto:%s?subject=%s&body=%s", recipient, encodeUriComponent(subject),
				encodeUriComponent(body));

		URI mailtoUri = new URI(uriString);
		Desktop.getDesktop().mail(mailtoUri);

	}

	private static String encodeUriComponent(String component) {
		try {
			return URI.create("dummy://" + component).toASCIIString().substring(8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
