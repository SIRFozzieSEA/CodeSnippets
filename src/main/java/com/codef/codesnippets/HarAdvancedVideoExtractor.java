package com.codef.codesnippets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HarAdvancedVideoExtractor {
    public static void main(String[] args) {

        String harFilePath = "/home/sirfozzie/GitRepos/CodeSnippets/src/main/resources/other/app.kululu.com2.har";
        String outputDir = "/home/sirfozzie/Downloads/steph/";
        
        new File(outputDir).mkdirs();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode rootNode = mapper.readTree(new File(harFilePath));
            JsonNode entries = rootNode.path("log").path("entries");

            for (JsonNode entry : entries) {
                // 1. Extract URL and check filenames
                String urlStr = entry.path("request").path("url").asText("");
                if (urlStr.isEmpty()) continue;

                String originalFileName = getFileNameFromUrl(urlStr);
                String lowerName = originalFileName.toLowerCase();
                String lowerUrl = urlStr.toLowerCase();

                // Completely ignore thumbnails
                if (lowerName.contains("thumb")) {
                    continue; 
                }

                // 2. Identify modern streaming fragments & standard video formats
                boolean isVideoFile = lowerName.contains("mp4") || lowerUrl.contains(".mp4") ||
                                      lowerName.contains("m4s") || lowerUrl.contains(".m4s") ||
                                      lowerName.contains("ts")  || lowerUrl.contains(".ts")  ||
                                      lowerName.contains("m3u8")|| lowerUrl.contains(".m3u8");

                JsonNode response = entry.path("response");
                JsonNode content = response.path("content");
                String mimeType = content.path("mimeType").asText("").toLowerCase();

                if (mimeType.startsWith("video/") || mimeType.contains("mp2t") || mimeType.contains("mpegurl")) {
                    isVideoFile = true;
                }

                if (!isVideoFile) continue;

                // 3. Fallback: Content is missing from HAR entirely, attempt live download
                String text = content.path("text").asText("");
                if (text.isEmpty()) {
                    System.out.println("⚠️ Content missing from HAR for: " + originalFileName);
                    System.out.println("   -> Attempting live network download from: " + urlStr);
                    downloadFileFromUrl(urlStr, outputDir + originalFileName);
                    continue;
                }

                // 4. Extract data handling both Base64 and Raw Text environments
                byte[] fileData;
                String encoding = content.path("encoding").asText("");
                
                if ("base64".equalsIgnoreCase(encoding)) {
                    fileData = Base64.getDecoder().decode(text.trim());
                } else {
                    // Raw string fall-back for unindexed media bodies
                    fileData = text.getBytes(StandardCharsets.ISO_8859_1);
                }

                // 5. Save the file
                String finalSavePath = outputDir + originalFileName;
                System.out.println("💾 Saved from HAR: " + originalFileName + " (" + mimeType + ")");
                try (FileOutputStream fos = new FileOutputStream(finalSavePath)) {
                    fos.write(fileData);
                }
            }
            System.out.println("Processing complete.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFileNameFromUrl(String urlStr) {
        try {
            URI uri = new URI(urlStr);
            String path = uri.getPath();
            if (path == null || path.isEmpty()) return "unknown_video_" + System.currentTimeMillis();
            
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            if (fileName.contains("?")) {
                fileName = fileName.substring(0, fileName.indexOf('?'));
            }
            return fileName.isEmpty() ? "video_" + System.currentTimeMillis() : fileName;
        } catch (URISyntaxException e) {
            return "video_" + System.currentTimeMillis();
        }
    }

    @SuppressWarnings("deprecation")
	private static void downloadFileFromUrl(String urlStr, String targetPath) {
        try (InputStream in = new URL(urlStr).openStream();
             FileOutputStream fos = new FileOutputStream(targetPath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            System.out.println("   ✅ Successfully downloaded live video asset.");
        } catch (Exception e) {
            System.out.println("   ❌ Live download failed (Token expired or asset unavailable): " + e.getMessage());
        }
    }
}
