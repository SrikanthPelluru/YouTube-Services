package com.youtube.services.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtube.services.dto.response.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Modernized YouTube Search Service using yt-dlp CLI
 * yt-dlp must be installed in system PATH or your Docker image
 * Example install (Linux): apt install yt-dlp
 * Example install (Windows): pip install yt-dlp
 */
@Service
public class YoutubeSearchService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Search YouTube videos by query using yt-dlp JSON output
     */
    public ResponseEntity<YoutubeSearchData> getYoutubeDataByQuery(String query) throws IOException {
        String json = runYtDlpCommand("ytsearch10:" + query);
        List<YoutubeVideoData> videos = objectMapper.readValue(json, new TypeReference<List<YoutubeVideoData>>() {});
        YoutubeSearchData searchData = new YoutubeSearchData();

        ItemSectionRenderer renderer = new ItemSectionRenderer();
        List<ItemSectionRendererContents> contents = new ArrayList<>();
        for (YoutubeVideoData v : videos) {
            ItemSectionRendererContents c = new ItemSectionRendererContents();
            c.setVideoRenderer(v.getVideoRenderer());
            contents.add(c);
        }
        renderer.setContents(contents);
        searchData.setItemSectionRenderer(renderer);

        return new ResponseEntity<>(searchData, HttpStatus.OK);
    }

    /**
     * Get related videos for a given YouTube video ID
     */
    public ResponseEntity<List<CompactYoutubeVideoData>> getYoutubeDataByVideoId(String videoId) throws IOException {
        String json = runYtDlpCommand("https://www.youtube.com/watch?v=" + videoId + " --flat-playlist --dump-json");
        List<CompactYoutubeVideoData> videos = objectMapper.readValue(json, new TypeReference<List<CompactYoutubeVideoData>>() {});
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }

    /**
     * Fetch trending videos
     */
    public ResponseEntity<List<YoutubeVideoData>> getTrendingVideos() throws IOException {
        String json = runYtDlpCommand("https://www.youtube.com/feed/trending --dump-json");
        List<YoutubeVideoData> videos = objectMapper.readValue(json, new TypeReference<List<YoutubeVideoData>>() {});
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }

    /**
     * Get initial homepage data (trending/recommended)
     */
    public ResponseEntity<YoutubeInitialData> getYoutubeInitialData() throws IOException {
        String json = runYtDlpCommand("https://www.youtube.com --dump-json");
        YoutubeInitialData data = objectMapper.readValue(json, YoutubeInitialData.class);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    /**
     * Get Shorts related to a video ID
     */
    public ResponseEntity<List<String>> getRelatedShorts(String videoId) throws IOException {
        String json = runYtDlpCommand("https://www.youtube.com/shorts/" + videoId + " --flat-playlist --dump-json");
        List<String> ids = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new java.io.ByteArrayInputStream(json.getBytes())));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("\"id\"")) {
                try {
                    var node = objectMapper.readTree(line);
                    ids.add(node.get("id").asText());
                } catch (Exception ignored) {}
            }
        }
        return new ResponseEntity<>(ids, HttpStatus.OK);
    }

    /**
     * Get search suggestions using Google Suggest API (still valid)
     */
    public ResponseEntity<List<String>> getSuggestedTextByInput(String input) throws IOException {
        input = input.replace(" ", "%20");
        java.net.URL url = new java.net.URL("https://suggestqueries.google.com/complete/search?client=firefox&ds=yt&q=" + input);
        java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestMethod("GET");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        List<String> response = new ArrayList<>();
        while ((inputLine = bufferedReader.readLine()) != null) {
            if (inputLine != null && !inputLine.isEmpty()) {
                inputLine = inputLine.replace("\"", "");
                int startInd = inputLine.indexOf("[", inputLine.indexOf("[") + 1) + 1;
                int lastInd = inputLine.indexOf("]");
                if (startInd > 0 && lastInd > 0 && lastInd > startInd) {
                    String temp = inputLine.substring(startInd, lastInd);
                    response = new ArrayList<>(List.of(temp.split(",")));
                }
            }
        }
        bufferedReader.close();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Utility: Runs yt-dlp command and captures JSON output
     */
    private String runYtDlpCommand(String args) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("yt-dlp", "--dump-json", args);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("yt-dlp failed with exit code " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("yt-dlp command interrupted", e);
        }

        return output.toString();
    }
}
