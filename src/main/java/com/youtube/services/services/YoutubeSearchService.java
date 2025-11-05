package com.youtube.services.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtube.services.dto.response.YoutubeVideoData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class YoutubeSearchService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Fetches search results from YouTube HTML and extracts key data
     */
    public ResponseEntity<List<YoutubeVideoData>> getYoutubeDataByQuery(String query) throws IOException {
        String url = "https://www.youtube.com/results?search_query=" + query.replace(" ", "+");
        List<YoutubeVideoData> videos = parseYoutubeSearchPage(url);
        return ResponseEntity.ok(videos);
    }

    /**
     * Fetches related videos by videoId (from watch page)
     */
    public ResponseEntity<List<YoutubeVideoData>> getYoutubeDataByVideoId(String videoId) throws IOException {
        String url = "https://www.youtube.com/watch?v=" + videoId;
        List<YoutubeVideoData> videos = parseYoutubeSearchPage(url);
        return ResponseEntity.ok(videos);
    }

    /**
     * Fetches trending videos (from trending page)
     */
    public ResponseEntity<List<YoutubeVideoData>> getTrendingVideos() throws IOException {
        String url = "https://www.youtube.com/feed/trending";
        List<YoutubeVideoData> videos = parseYoutubeSearchPage(url);
        return ResponseEntity.ok(videos);
    }

    /**
     * Parses the YouTube HTML, extracts ytInitialData JSON, and maps required fields
     */
    private List<YoutubeVideoData> parseYoutubeSearchPage(String pageUrl) throws IOException {
        // Fetch HTML
        Document doc = Jsoup.connect(pageUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36")
                .timeout(15000)
                .get();

        // Find JSON blob
        Pattern pattern = Pattern.compile("var ytInitialData = (\\{.*?\\});");
        Matcher matcher = pattern.matcher(doc.html());

        if (!matcher.find()) {
            throw new IOException("ytInitialData not found in HTML");
        }

        String jsonData = matcher.group(1);
        JsonNode root = objectMapper.readTree(jsonData);

        List<YoutubeVideoData> results = new ArrayList<>();

        // Extract main sections (handles both search & trending pages)
        JsonNode sections = root.at("/contents/twoColumnSearchResultsRenderer/primaryContents/sectionListRenderer/contents");
        if (sections.isMissingNode() || !sections.isArray()) {
            sections = root.at("/contents/twoColumnBrowseResultsRenderer/tabs/0/tabRenderer/content/sectionListRenderer/contents");
        }

        extractVideosFromSections(sections, results);
        return results;
    }

    /**
     * Helper: Extracts video info from a section list JSON node
     */
    private void extractVideosFromSections(JsonNode sections, List<YoutubeVideoData> results) {
        if (sections == null || !sections.isArray()) return;

        for (JsonNode section : sections) {
            JsonNode items = section.path("itemSectionRenderer").path("contents");
            if (!items.isArray()) continue;

            for (JsonNode item : items) {
                JsonNode video = item.path("videoRenderer");
                if (video.isMissingNode()) continue;

                YoutubeVideoData data = new YoutubeVideoData();
                data.setVideoId(video.path("videoId").asText());
                data.setTitle(video.path("title").path("runs").get(0).path("text").asText(""));
                data.setChannelName(video.path("ownerText").path("runs").get(0).path("text").asText(""));

                // Thumbnail
                JsonNode thumbs = video.path("thumbnail").path("thumbnails");
                if (thumbs.isArray() && thumbs.size() > 0) {
                    data.setThumbnailUrl(thumbs.get(thumbs.size() - 1).path("url").asText(""));
                }

                // Duration
                JsonNode len = video.path("lengthText").path("simpleText");
                if (!len.isMissingNode()) {
                    data.setDuration(len.asText());
                }

                results.add(data);
            }
        }
    }

    /**
     * Fetches search suggestions from Google Suggest API
     */
    public ResponseEntity<List<String>> getSuggestedTextByInput(String input) throws IOException {
        input = input.replace(" ", "%20");
        URL url = new URL("https://suggestqueries.google.com/complete/search?client=firefox&ds=yt&q=" + input);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) response.append(line);
        reader.close();

        String json = response.toString().replace("[[", "").replace("]]", "").replace("\"", "");
        String[] suggestions = json.split(",");
        return ResponseEntity.ok(List.of(suggestions));
    }
}
