package com.youtube.services.controller;

import com.youtube.services.dto.response.CompactYoutubeVideoData;
import com.youtube.services.dto.response.YoutubeInitialData;
import com.youtube.services.dto.response.YoutubeSearchData;
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
@CrossOrigin
public class YoutubeSearchController {

    @Autowired
    private YoutubeSearchService service;
    @GetMapping("/query")
    public ResponseEntity<YoutubeSearchData> getYoutubeVideosByQuery(@RequestParam("q") String query) throws IOException {
        return service.getYoutubeDataByQuery(query);
    }

    @GetMapping("/related")
    public ResponseEntity<List<CompactYoutubeVideoData>> getYoutubeVideosByVideoId(@RequestParam("videoId") String videoId) throws IOException {
        return service.getYoutubeDataByVideoId(videoId);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<YoutubeVideoData>> getTrendingVideos() throws IOException {
        return service.getTrendingVideos();
    }

    @GetMapping("/initial")
    public ResponseEntity<YoutubeInitialData> getInitialData() throws IOException {
        return service.getYoutubeInitialData();
    }

    @GetMapping("/shorts")
    public ResponseEntity<List<String>> getRelatedShorts(@RequestParam("videoId") String videoId) throws IOException {
        return service.getRelatedShorts(videoId);
    }

    @GetMapping("/cronJob")
    public ResponseEntity<String> getCronJobResponse(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getRemoteAddr());
        System.out.println(response.getHeaderNames());
        return ResponseEntity.ok("Hitted at " + new Date());
    }

    @GetMapping("/getSuggestedText")
    public ResponseEntity<List<String>> getSuggestedTextByInput(@RequestParam("q") String q) throws IOException {
        return this.service.getSuggestedTextByInput(q);
    }
}
