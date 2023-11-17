package com.youtube.services.controller;

import com.youtube.services.dto.response.CompactYoutubeVideoData;
import com.youtube.services.dto.response.YoutubeVideoData;
import com.youtube.services.services.YoutubeSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/search")
@CrossOrigin
public class YoutubeSearchController {

    @Autowired
    private YoutubeSearchService service;
    @GetMapping("/query")
    public ResponseEntity<List<YoutubeVideoData>> getYoutubeVideosByQuery(@RequestParam("q") String query) throws IOException {
        return service.getYoutubeDataByQuery(query);
    }

    @GetMapping("/related")
    public ResponseEntity<List<CompactYoutubeVideoData>> getYoutubeVideosByVideoId(@RequestParam("videoId") String videoId) throws IOException {
        return service.getYoutubeDataByVideoId(videoId);
    }
}
