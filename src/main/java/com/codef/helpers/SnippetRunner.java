package com.codef.helpers;

import java.io.IOException;

import com.codef.xsalt.utils.XSaLTFileSystemUtils;

public class SnippetRunner {

	
	public static void main(String[] args) throws IOException {
		
		System.out.println("67c26d18a8d8d81bec11f9a36c2101b8");
		System.out.println(XSaLTFileSystemUtils.getMD5Hash("C:\\Users\\sir_f\\Downloads\\ungoogled-chromium_96.0.4664.45-1.1_installer.exe"));
		
	}
}
