package com.codef.codesnippets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogTester {
	
	private static final Logger LOGGER = LogManager.getLogger(LogTester.class.getName());
	
	public static void main(String[] args) {
		
		LOGGER.trace("A TRACE Message");
		LOGGER.debug("A DEBUG Message");
		LOGGER.info("An INFO Message");
		LOGGER.warn("A WARN Message");
		LOGGER.error("An ERROR Message");
		
	}

}
