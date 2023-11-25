package com.youtube.services.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtube.services.dto.response.*;
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
    public ResponseEntity<YoutubeSearchData> getYoutubeDataByQuery(String query) throws IOException {
        query = query.replace(" ", "+");
        URL url = new URL("https://www.youtube.com/results?search_query=" + query);
        ResponseEntity<YoutubeSearchData> searchData = getYoutubeSearchData(url, "\\{\"itemSectionRenderer\":\\{\"contents\":\\[(.*?)continuationItemRenderer\"");
        YoutubeSearchData data = searchData.getBody();
        if (data == null || data.getItemSectionRenderer() == null || data.getItemSectionRenderer().getContents() == null ||
            data.getItemSectionRenderer().getContents().size() <= 1) {
            ResponseEntity<List<YoutubeVideoData>> videoData = getYoutubeData(url, "\\{\"videoRenderer\":\\{\"videoId\":(.*?)\"searchVideoResultEntityKey\":\"[A-Za-z0-9]+\"\\}\\}");
            videoData.getBody().forEach(youtubeVideoData -> {
                ItemSectionRendererContents contents = new ItemSectionRendererContents();
                contents.setVideoRenderer(youtubeVideoData.getVideoRenderer());
                data.getItemSectionRenderer().getContents().add(contents);
            });
        }
        return new ResponseEntity<>(data, searchData.getStatusCode());
    }

    private ResponseEntity<YoutubeSearchData> getYoutubeSearchData(URL url, String pattern) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");


        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );
        String inputLine;
        YoutubeSearchData searchData = new YoutubeSearchData();
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.contains("itemSectionRenderer")) {
                Matcher matcher = Pattern.compile(pattern).matcher(inputLine);
                if (matcher.find()) {
                    searchData = objectMapper.readValue(matcher.group().substring(0, matcher.group().lastIndexOf(",")), YoutubeSearchData.class);
                }
            }
            if (searchData.getItemSectionRenderer() != null) break;
        }
        in.close();
        return new ResponseEntity<>(searchData, HttpStatusCode.valueOf(status));
    }

    public ResponseEntity<List<CompactYoutubeVideoData>> getYoutubeDataByVideoId(String videoId) throws IOException {
        URL url = new URL("https://www.youtube.com/watch?v=" + videoId);
        return getCompactYoutubeData(url, "\\{\"compactVideoRenderer\":\\{\"videoId\":(.*?)],\"accessibility\":\\{\"accessibilityData\":\\{[^}]*\\}\\}\\}\\}");
    }

    public ResponseEntity<YoutubeInitialData> getYoutubeInitialData() throws IOException {
        URL url = new URL("https://youtube.com");
        return getYoutubeInitialData(url, "\\{\"richGridRenderer\":\\{\"contents\":\\[(.*?)\"reflowOptions\":\\{\"minimumRowsOfVideosAtStart\":\\d,\"minimumRowsOfVideosBetweenSections\":\\d\\}\\}\\}");
    }

    private ResponseEntity<YoutubeInitialData> getYoutubeInitialData(URL url, String pattern) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");


        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );
        String inputLine;
        YoutubeInitialData initialData = new YoutubeInitialData();
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.contains("richGridRenderer")) {
                Matcher matcher = Pattern.compile(pattern).matcher(inputLine);
                if (matcher.find()) {
                    initialData = objectMapper.readValue(matcher.group(), YoutubeInitialData.class);
                }
            }
            if (initialData.getRichGridRenderer() != null) break;
        }
        in.close();
        return new ResponseEntity<>(initialData, HttpStatusCode.valueOf(status));
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

    public ResponseEntity<List<YoutubeVideoData>> getTrendingVideos() throws IOException {
        URL url = new URL("https://www.youtube.com/feed/trending");
        return getYoutubeData(url, "\\{\"videoRenderer\":\\{\"videoId\":(.*?)\\{\"thumbnailOverlayNowPlayingRenderer\":\\{\"text\":\\{\"runs\":\\[\\{\"text\":\"[^\"]*\"\\}\\]\\}\\}\\}\\]\\}\\},");
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

    public ResponseEntity<List<String>> getRelatedShorts(String videoId) throws IOException {
        URL url = new URL("https://youtube.com/shorts/"+videoId);
        return getYoutubeShortIds(url, "\\\\x7b\\\\x22videoId\\\\x22:\\\\x22(.*?)\\\\x22");
    }

    private ResponseEntity<List<String>> getYoutubeShortIds(URL url, String pattern) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );
        String inputLine;
        List<String> youtubeVideoIDs = new ArrayList<>();
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.contains("x22responseContext")) {
                Matcher matcher = Pattern.compile(pattern).matcher(inputLine);
                while (matcher.find()) {
                    youtubeVideoIDs.add(matcher.group().substring(24, 35));
                }
            }
            if (!youtubeVideoIDs.isEmpty()) break;
        }
        in.close();
        return new ResponseEntity<>(youtubeVideoIDs, HttpStatusCode.valueOf(status));
    }
}
