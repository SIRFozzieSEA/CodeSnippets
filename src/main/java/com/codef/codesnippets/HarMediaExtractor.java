package com.codef.codesnippets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

public class HarMediaExtractor {
    public static void main(String[] args) {
    	
    	String harFilePath = "/home/sirfozzie/GitRepos/CodeSnippets/src/main/resources/other/app.kululu.com2.har";
        String outputDir = "/home/sirfozzie/Downloads/steph/";
        
        new File(outputDir).mkdirs();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode rootNode = mapper.readTree(new File(harFilePath));
            JsonNode entries = rootNode.path("log").path("entries");

            for (JsonNode entry : entries) {
                // 1. Get the URL from the request
                String urlStr = entry.path("request").path("url").asText("").toLowerCase();
                if (urlStr.isEmpty()) continue;

                // 2. Extract the actual file name
                String originalFileName = getFileNameFromUrl(urlStr);
                if (originalFileName.isEmpty()) continue;

                // 3. Skip any thumbnail photo/file entirely
                if (originalFileName.contains("thumb")) {
                    continue; 
                }

                // 4. Process the response content
                JsonNode content = entry.path("response").path("content");
                String mimeType = content.path("mimeType").asText("").toLowerCase();
                String text = content.path("text").asText("");

                if (text.isEmpty() || !content.path("encoding").asText("").equals("base64")) {
                    continue;
                }

                // 5. Match by standard media types OR "mp4" extension in name/URL
                boolean isMediaMime = mimeType.startsWith("video/") || mimeType.startsWith("audio/") || mimeType.startsWith("image/");
                boolean isMp4File = originalFileName.contains("mp4") || urlStr.contains(".mp4");

                if (isMediaMime || isMp4File) {
                    byte[] decodedBytes = Base64.getDecoder().decode(text);
                    String finalSavePath = outputDir + originalFileName;

                    System.out.println("Saving: " + originalFileName);
                    try (FileOutputStream fos = new FileOutputStream(finalSavePath)) {
                        fos.write(decodedBytes);
                    }
                }
            }
            System.out.println("Extraction complete.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFileNameFromUrl(String urlStr) {
        try {
            URI uri = new URI(urlStr);
            String path = uri.getPath();
            if (path == null || path.isEmpty()) return "";
            
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            
            if (fileName.contains("?")) {
                fileName = fileName.substring(0, fileName.indexOf('?'));
            }
            return fileName;
        } catch (URISyntaxException e) {
            return "";
        }
    }
}
