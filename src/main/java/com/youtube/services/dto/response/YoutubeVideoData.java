package com.youtube.services.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class YoutubeVideoData {
    private VideoRenderer videoRenderer;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class VideoRenderer {
    private String videoId;
    private Thumbnail thumbnail;
    private Title title;
    private LengthText lengthText;
    private PublishedTimeText publishedTimeText;
    private ViewCountText viewCountText;
    private LongBylineText longBylineText;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class Title {
    private List<Runs> runs;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class Runs {
    private String text;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class LengthText {
    private String simpleText;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class PublishedTimeText {
    private String simpleText;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class ViewCountText {
    private String simpleText;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class Thumbnail {
    private List<Thumbnails> thumbnails;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class Thumbnails {
    private String url;
    private int width;
    private int height;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class LongBylineText {
    private List<Runs> runs;
}