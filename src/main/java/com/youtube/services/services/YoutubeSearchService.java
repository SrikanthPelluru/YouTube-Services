package com.youtube.services.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtube.services.dto.response.CompactYoutubeVideoData;
import com.youtube.services.dto.response.YoutubeVideoData;
import org.springframework.http.HttpStatusCode;
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
    public ResponseEntity<List<YoutubeVideoData>> getYoutubeDataByQuery(String query) throws IOException {
        query = query.replace(" ", "+");
        URL url = new URL("https://www.youtube.com/results?search_query=" + query);
        return getYoutubeData(url, "\\{\"videoRenderer\":\\{\"videoId\":(.*?)\"searchVideoResultEntityKey\":\"[A-Za-z0-9]+\"\\}\\}");
    }

    public ResponseEntity<List<CompactYoutubeVideoData>> getYoutubeDataByVideoId(String videoId) throws IOException {
        URL url = new URL("https://www.youtube.com/watch?v=" + videoId);
        return getCompactYoutubeData(url, "\\{\"compactVideoRenderer\":\\{\"videoId\":(.*?)],\"accessibility\":\\{\"accessibilityData\":\\{[^}]*\\}\\}\\}\\}");
    }

    private ResponseEntity<List<YoutubeVideoData>> getYoutubeData(URL url, String pattern) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );
        String inputLine;
        List<YoutubeVideoData> youtubeVideoData = new ArrayList<>();
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.contains("watch?v=")) {
                Matcher matcher = Pattern.compile(pattern).matcher(inputLine);
                while (matcher.find()) {
                    YoutubeVideoData data = objectMapper.readValue(matcher.group(), YoutubeVideoData.class);
                    youtubeVideoData.add(data);
                }
            }
            if (!youtubeVideoData.isEmpty()) break;
        }
        in.close();

        return new ResponseEntity<>(youtubeVideoData, HttpStatusCode.valueOf(status));
    }

    private ResponseEntity<List<CompactYoutubeVideoData>> getCompactYoutubeData(URL url, String pattern) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );
        String inputLine;
        List<CompactYoutubeVideoData> youtubeVideoData = new ArrayList<>();
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.contains("watch?v=")) {
                Matcher matcher = Pattern.compile(pattern).matcher(inputLine);
                while (matcher.find()) {
                    CompactYoutubeVideoData data = objectMapper.readValue(matcher.group(), CompactYoutubeVideoData.class);
                    youtubeVideoData.add(data);
                }
            }
            if (!youtubeVideoData.isEmpty()) break;
        }
        in.close();

        return new ResponseEntity<>(youtubeVideoData, HttpStatusCode.valueOf(status));
    }
}
