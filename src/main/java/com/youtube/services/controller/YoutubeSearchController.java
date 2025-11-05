package com.youtube.services.controller;

import com.youtube.services.dto.response.YoutubeVideoData;
import com.youtube.services.services.YoutubeSearchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/search")
public class YoutubeSearchController {

    @Autowired
    private YoutubeSearchService service;

    /**
     * Search YouTube videos by keyword
     * Example: /search/query?q=arijit+singh
     */
    @GetMapping("/query")
    public ResponseEntity<List<YoutubeVideoData>> getYoutubeVideosByQuery(@RequestParam("q") String query)
            throws IOException {
        return service.getYoutubeDataByQuery(query);
    }

    /**
     * Get related videos for a given YouTube videoId
     * Example: /search/related?videoId=abcd1234
     */
    @GetMapping("/related")
    public ResponseEntity<List<YoutubeVideoData>> getYoutubeVideosByVideoId(@RequestParam("videoId") String videoId)
            throws IOException {
        return service.getYoutubeDataByVideoId(videoId);
    }

    /**
     * Get trending videos
     * Example: /search/trending
     */
    @GetMapping("/trending")
    public ResponseEntity<List<YoutubeVideoData>> getTrendingVideos() throws IOException {
        return service.getTrendingVideos();
    }

    /**
     * Get text suggestions for search
     * Example: /search/getSuggestedText?q=love
     */
    @GetMapping("/getSuggestedText")
    public ResponseEntity<List<String>> getSuggestedTextByInput(@RequestParam("q") String q) throws IOException {
        return this.service.getSuggestedTextByInput(q);
    }

    /**
     * Simple ping endpoint for Render cron health checks
     */
    @GetMapping("/cronJob")
    public ResponseEntity<String> getCronJobResponse(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Pinged from: " + request.getRemoteAddr());
        return ResponseEntity.ok("Service active at " + new Date());
    }
}
