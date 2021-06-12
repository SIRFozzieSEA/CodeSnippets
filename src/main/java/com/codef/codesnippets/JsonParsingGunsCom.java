package com.codef.codesnippets;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.codef.xsalt.utils.XSaLTFileSystemUtils;
import com.codef.xsalt.utils.XSaLTNetUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParsingGunsCom {

	/// https://attacomsian.com/blog/jackson-json-node-tree-model
	/// https://mkyong.com/tutorials/java-json-tutorials/

	public static String BASE_FACET_QUERY = "https://www.guns.com/catalog/search/listing?facets=%7B%22outlet%22:null,%22condition%22:null,%22compliance%22:%22%22,%22outletOnly%22:null%7D&facetGroup=%7B%22dealer%22:%22%22,%22product.upc%22:%22%22,%22product.rebates%22:%22%22,%22product.category%22:%22HANDGUNS%22,%22product.collections%22:%22%22,%22product.subCategory%22:%22%22,%22product.manufacturer%22:%22%22%7D&filters=[]&sortBy=listing_weight";

	public static String BASE_QUERY = "https://www.guns.com/firearms/handguns/all?";
	public static String BASE_STOCK = "availability=";
	public static String BASE_STOCK_QUERY = "In stock";
	public static String BASE_PRICING = "defaultPriceRange=";
	public static String BASE_PRICING_QUERY = "$500 - $749";
	public static String BASE_CALIBER = "product.specs.Caliber=";
	public static String BASE_MANUFACTURERS = "product.manufacturer=";

	public static String facetsContent = "";
	public static List<Pattern> notInterestedManufacturerList = new ArrayList<Pattern>();
	public static List<Pattern> notInterestedCaliberList = new ArrayList<Pattern>();

	public static void main(String[] args) {
		JsonParsingGunsCom jpgc = new JsonParsingGunsCom();
		jpgc.populateGunsDotComFacets();
		jpgc.populateNotInterestedMaps();
		jpgc.doStuff();
	}

	private void populateGunsDotComFacets() {
		try {
			facetsContent = XSaLTNetUtils.readStringFromURL(BASE_FACET_QUERY);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void populateNotInterestedMaps() {

		notInterestedManufacturerList.add(Pattern.compile("altor.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("century.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("colt.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("glock.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("high-point.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("kimber.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("luger.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("remington.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("rock.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("ruger.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("sar.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("sccy.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("smith.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("sig.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("springfield.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("tanfoglio.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("taurus.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("tara.*", Pattern.CASE_INSENSITIVE));
		notInterestedManufacturerList.add(Pattern.compile("zev.*", Pattern.CASE_INSENSITIVE));

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

	private void doStuff() {

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

			System.out.println(getFinalUrl(filteredManufacturer, filteredCaliber));

		} catch (Exception e) {
			e.printStackTrace();
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
