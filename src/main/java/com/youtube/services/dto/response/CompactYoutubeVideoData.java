package com.youtube.services.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompactYoutubeVideoData {
    private CompactVideoRenderer compactVideoRenderer;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class CompactVideoRenderer {
    private String videoId;
    private Thumbnail thumbnail;
    private SimpleTitle title;
    private LengthText lengthText;
    private PublishedTimeText publishedTimeText;
    private ViewCountText viewCountText;
    private LongBylineText longBylineText;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class SimpleTitle {
    private String simpleText;
}