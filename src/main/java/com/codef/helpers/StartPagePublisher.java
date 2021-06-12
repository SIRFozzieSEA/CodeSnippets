package com.codef.helpers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.codef.xsalt.utils.XSaLTFTPClient;
import com.codef.xsalt.utils.XSaLTFileSystemUtils;
import com.codef.xsalt.utils.XSaLTNetUtils;
import com.codef.xsalt.utils.XSaLTXMLUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StartPagePublisher {

	/// https://attacomsian.com/blog/jackson-json-node-tree-model
	/// https://mkyong.com/tutorials/java-json-tutorials/

	private static final Logger LOGGER = Logger.getLogger(StartPagePublisher.class.getName());

	public static String BASE_FACET_QUERY = "https://www.guns.com/catalog/search/listing?facets=%7B%22outlet%22:null,%22condition%22:null,%22compliance%22:%22%22,%22outletOnly%22:null%7D&facetGroup=%7B%22dealer%22:%22%22,%22product.upc%22:%22%22,%22product.rebates%22:%22%22,%22product.category%22:%22HANDGUNS%22,%22product.collections%22:%22%22,%22product.subCategory%22:%22%22,%22product.manufacturer%22:%22%22%7D&filters=[]&sortBy=listing_weight";
	public static String BASE_QUERY = "https://www.guns.com/firearms/handguns/all?";
	public static String BASE_STOCK = "availability=";
	public static String BASE_STOCK_QUERY = "In stock";
	public static String BASE_PRICING = "defaultPriceRange=";
	public static String BASE_PRICING_QUERY = "$500 - $749";
	public static String BASE_CALIBER = "product.specs.Caliber=";
	public static String BASE_MANUFACTURERS = "product.manufacturer=";
	public static String BASE_SUBCATEGORY = "product.subCategory=";
	public static String BASE_SUBCATEGORY_QUERY = "SEMI AUTO,REVOLVER";

	private String BASE_FTP_HOSTNAME = "";
	private String BASE_FTP_USERNAME = "";
	private String BASE_FTP_PASSWORD = "";

	public static List<String> urlMode;
	public static String BASE_TEMPLATE_PATH = "C:\\GitRepos\\CodeSnippets\\src\\main\\resources\\";
	public static String BASE_CREDENTIALS_FILE = "E:\\Documents\\Personal\\Site Passwords.xml";
	public static String BASE_TEMPLATE_FILENAME = "StartPage_TEMPLATE.html";
	public static String TEMPLATE_REPLACE_TAG_PREF = "GUNS_PREFERRED_URL";
	public static String TEMPLATE_REPLACE_TAG_FULL = "GUNS_FULL_URL";

	private String facetsContent = "";
	private String templateContent = "";
	private List<Pattern> notInterestedManufacturerList;
	private List<Pattern> notInterestedCaliberList;
	private String preferredUrl = "";
	private String fullUrl = "";

	private String TEST_TEMPLATE_AND_PUBLISH = "";

	public static void main(String[] args) {

		StartPagePublisher spp = new StartPagePublisher();
		spp.setUpModes();
		spp.populateFacetsContent();

		if (spp.isFacetsPopulated()) {

			spp.populateTemplateContent();

			for (String mode : urlMode) {
				spp.populateInterestMaps(mode);
				spp.makeUrl(mode);
				spp.substituteTags(mode);
			}

			spp.populateCreds();
			spp.transferFileToFtp();

		} else {
			LOGGER.error("Could not download facets data from Guns.com - STOPPING!");
		}

		LOGGER.info("JsonParsingGunsCom COMPLETED!");

	}

	private boolean isFacetsPopulated() {
		return !facetsContent.equals("");
	}

	private void setUpModes() {
		urlMode = new ArrayList<String>();
		urlMode.add("PREF");
		urlMode.add("FULL");
	}

	private void populateFacetsContent() {
		try {
			if (TEST_TEMPLATE_AND_PUBLISH.equals("")) {
				facetsContent = XSaLTNetUtils.readStringFromURL(BASE_FACET_QUERY);
			} else {
				facetsContent = "{ }";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void populateTemplateContent() {
		try {
			templateContent = XSaLTFileSystemUtils.readFile(BASE_TEMPLATE_PATH + BASE_TEMPLATE_FILENAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void substituteTags(String mode) {
		if (mode.equals("PREF")) {
			templateContent = templateContent.replaceAll(TEMPLATE_REPLACE_TAG_PREF, preferredUrl);
		} else {
			templateContent = templateContent.replaceAll(TEMPLATE_REPLACE_TAG_FULL, fullUrl);
		}
	}

	private void populateCreds() {

		try {
			Document credsDoc = XSaLTXMLUtils.loadXMLDocumentFromFile(BASE_CREDENTIALS_FILE);
			Node blueGravityNode = XSaLTXMLUtils.getXPathResult(credsDoc, "//SitePasswords//BlueGravity_FTP").item(0);
			BASE_FTP_HOSTNAME = XSaLTXMLUtils.getLabelEntry(blueGravityNode, "URL");
			BASE_FTP_USERNAME = XSaLTXMLUtils.getLabelEntry(blueGravityNode, "Username");
			BASE_FTP_PASSWORD = XSaLTXMLUtils.getLabelEntry(blueGravityNode, "Password");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void transferFileToFtp() {

		if (BASE_FTP_HOSTNAME.equals("") && BASE_FTP_USERNAME.equals("") && BASE_FTP_PASSWORD.equals("")) {
			LOGGER.error("Could not fully populate FTP creds for transfer");
		} else {

			try {
				XSaLTFileSystemUtils.writeStringToFile(templateContent,
						BASE_TEMPLATE_PATH + BASE_TEMPLATE_FILENAME + ".tmp");
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				XSaLTFTPClient ftp = new XSaLTFTPClient(BASE_FTP_HOSTNAME, BASE_FTP_USERNAME, BASE_FTP_PASSWORD);
				ftp.connectFTP();
				ftp.login();
				ftp.setPassiveTranferMode(false);
				ftp.storeFile(BASE_TEMPLATE_PATH + BASE_TEMPLATE_FILENAME + ".tmp",
						"/fozden.com/www/" + BASE_TEMPLATE_FILENAME.replace("_TEMPLATE", ""));
				ftp.disconnectFTP();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				XSaLTFileSystemUtils.deleteFileNew(BASE_TEMPLATE_PATH + BASE_TEMPLATE_FILENAME + ".tmp");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void populateInterestMaps(String mode) {

		notInterestedManufacturerList = new ArrayList<Pattern>();
		notInterestedCaliberList = new ArrayList<Pattern>();

		if (TEST_TEMPLATE_AND_PUBLISH.equals("")) {

			notInterestedManufacturerList.add(Pattern.compile(".*heizer.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile(".*seecamp.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("arex.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("altor.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("american tactical.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("canik.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("century.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("cz.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("eaa.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("feg.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("fima.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("fn.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("girsan.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("glock.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("high-point.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("ifg.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("kahr.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("kimber.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("luger.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("mossberg.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("norinco.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("north american.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("polymer.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("rock.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("sar.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("sccy.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("sds.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("springfield.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("star.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("tanfoglio.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("tara.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("taurus.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("tisas.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("zastava.*", Pattern.CASE_INSENSITIVE));
			notInterestedManufacturerList.add(Pattern.compile("zev.*", Pattern.CASE_INSENSITIVE));

			if (mode.equals("PREF")) {
				notInterestedManufacturerList.add(Pattern.compile("colt.*", Pattern.CASE_INSENSITIVE));
				notInterestedManufacturerList.add(Pattern.compile("remington.*", Pattern.CASE_INSENSITIVE));
				notInterestedManufacturerList.add(Pattern.compile("ruger.*", Pattern.CASE_INSENSITIVE));
				notInterestedManufacturerList.add(Pattern.compile("sig.*", Pattern.CASE_INSENSITIVE));
				notInterestedManufacturerList.add(Pattern.compile("smith.*", Pattern.CASE_INSENSITIVE));
			}

			notInterestedCaliberList.add(Pattern.compile(".*380.*", Pattern.CASE_INSENSITIVE));
			notInterestedCaliberList.add(Pattern.compile(".*357.*", Pattern.CASE_INSENSITIVE));
			notInterestedCaliberList.add(Pattern.compile("9mm.*", Pattern.CASE_INSENSITIVE));
			notInterestedCaliberList.add(Pattern.compile("9 mm.*", Pattern.CASE_INSENSITIVE));
			notInterestedCaliberList.add(Pattern.compile(".*9x19.*", Pattern.CASE_INSENSITIVE));
			notInterestedCaliberList.add(Pattern.compile(".*9 x 19.*", Pattern.CASE_INSENSITIVE));
			notInterestedCaliberList.add(Pattern.compile("45 ACP", Pattern.CASE_INSENSITIVE));
			notInterestedCaliberList.add(Pattern.compile("45 ACP.*", Pattern.CASE_INSENSITIVE));
			notInterestedCaliberList.add(Pattern.compile(".*40 S.*", Pattern.CASE_INSENSITIVE));
			notInterestedCaliberList.add(Pattern.compile(".*40S.*", Pattern.CASE_INSENSITIVE));
			notInterestedCaliberList.add(Pattern.compile("\\.40 S.*", Pattern.CASE_INSENSITIVE));
			notInterestedCaliberList.add(Pattern.compile(".*45 AUTO.*", Pattern.CASE_INSENSITIVE));
			notInterestedCaliberList.add(Pattern.compile(".*45 ACP.*", Pattern.CASE_INSENSITIVE));
			notInterestedCaliberList.add(Pattern.compile(".*45 CAL.*", Pattern.CASE_INSENSITIVE));
			notInterestedCaliberList.add(Pattern.compile(".*45ACP.*", Pattern.CASE_INSENSITIVE));

		}

	}

	private void makeUrl(String mode) {

		if (TEST_TEMPLATE_AND_PUBLISH.equals("")) {

			try {

				ObjectMapper mapper = new ObjectMapper();
				JsonNode node = mapper.readTree(facetsContent);
				JsonNode facetsNode = node.path("facets");

				JsonNode manufacturerNodes = null;
				JsonNode caliberNodes = null;

				for (JsonNode facet : facetsNode) {
					if (facet.path("filter").path("label").asText().equalsIgnoreCase("manufacturer")) {
						manufacturerNodes = facet.path("properties");
					} else if (facet.path("filter").path("label").asText().equalsIgnoreCase("caliber")) {
						caliberNodes = facet.path("properties");
					}
				}

				Set<String> filteredManufacturer = getFilteredSetByRemoveFiltering(
						mapper.convertValue(manufacturerNodes, new TypeReference<Map<String, Integer>>() {
						}), notInterestedManufacturerList);
				Set<String> filteredCaliber = getFilteredSetByAddFiltering(
						mapper.convertValue(caliberNodes, new TypeReference<Map<String, Integer>>() {
						}), notInterestedCaliberList);

				if (mode.equals("PREF")) {
					preferredUrl = getFinalUrl(filteredManufacturer, filteredCaliber);
				} else {
					fullUrl = getFinalUrl(filteredManufacturer, filteredCaliber);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {

			if (mode.equals("PREF")) {
				preferredUrl = TEST_TEMPLATE_AND_PUBLISH;
			} else {
				fullUrl = TEST_TEMPLATE_AND_PUBLISH;
			}

		}

	}

	private String getFinalUrl(Set<String> filteredManufacturer, Set<String> filteredCaliber)
			throws UnsupportedEncodingException {

		StringBuilder sb = new StringBuilder();
		sb.append(BASE_STOCK);
		sb.append(URLEncoder.encode(BASE_STOCK_QUERY, StandardCharsets.UTF_8.toString()).replace("+", "%20"));
		sb.append("&");
		sb.append(BASE_PRICING);
		sb.append(URLEncoder.encode(BASE_PRICING_QUERY, StandardCharsets.UTF_8.toString()).replace("+", "%20"));
		sb.append("&");
		sb.append(BASE_CALIBER);
		sb.append(URLEncoder.encode(filteredCaliber.toString().replace(", ", ","), StandardCharsets.UTF_8.toString())
				.replace("+", "%20"));
		sb.append("&");
		sb.append(BASE_MANUFACTURERS);
		sb.append(
				URLEncoder.encode(filteredManufacturer.toString().replace(", ", ","), StandardCharsets.UTF_8.toString())
						.replace("+", "%20"));
		sb.append("&");
		sb.append(BASE_SUBCATEGORY);
		sb.append(URLEncoder
				.encode(BASE_SUBCATEGORY_QUERY.toString().replace(", ", ","), StandardCharsets.UTF_8.toString())
				.replace("+", "%20"));
		return BASE_QUERY + sb.toString();

	}

	private Set<String> getFilteredSetByRemoveFiltering(Map<String, Integer> mapToFilter, List<Pattern> filterList) {
		Set<String> filteredSet = new TreeSet<String>();

		for (Map.Entry<String, Integer> entry : mapToFilter.entrySet()) {
			boolean undesired = false;
			String entryKey = entry.getKey();
			for (Pattern pattern : filterList) {
				if (pattern.matcher(entryKey).matches()) {
					undesired = true;
				}
			}
			if (!undesired) {
				filteredSet.add(entryKey);
			}
		}
		return filteredSet;
	}

	private Set<String> getFilteredSetByAddFiltering(Map<String, Integer> mapToFilter, List<Pattern> filterList) {
		Set<String> filteredSet = new TreeSet<String>();

		for (Map.Entry<String, Integer> entry : mapToFilter.entrySet()) {
			boolean desired = false;
			String entryKey = entry.getKey();
			for (Pattern pattern : filterList) {
				if (pattern.matcher(entryKey).matches()) {
					desired = true;
				}
			}
			if (desired) {
				filteredSet.add(entryKey);
			}
		}
		return filteredSet;
	}

}
