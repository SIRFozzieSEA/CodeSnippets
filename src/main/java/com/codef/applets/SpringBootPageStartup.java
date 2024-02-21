package com.codef.applets;

import java.awt.Desktop;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SpringBootPageStartup {

	@Value("${launch-page-url}")
	private String launchPageUrl;

	@EventListener(ApplicationReadyEvent.class)
	public void launchBrowser() {
		System.setProperty("java.awt.headless", "false");
		Desktop desktop = Desktop.getDesktop();

		try {
			desktop.browse(new URI(launchPageUrl));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
