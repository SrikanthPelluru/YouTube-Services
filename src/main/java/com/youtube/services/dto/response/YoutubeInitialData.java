package com.youtube.services.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutubeInitialData {
    private RichGridRenderer richGridRenderer;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class RichGridRenderer {
    private List<Contents> contents;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class Contents {
    private RichSectionRenderer richSectionRenderer;
    private RichItemRenderer richItemRenderer;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class RichSectionRenderer {
    private Content content;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class Content {
    private RichShelfRenderer richShelfRenderer;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class RichShelfRenderer {
    private Title title;
    private List<RichShelfRendererContents> contents;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class RichShelfRendererContents {
    private RichItemRenderer richItemRenderer;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class RichItemRenderer {
    private RichItemRendererContent content;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class RichItemRendererContent {
    private ReelItemRenderer reelItemRenderer;

    private VideoRenderer videoRenderer;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class ReelItemRenderer {
    private String videoId;
    private Headline headline;

    private Thumbnail thumbnail;
    private ViewCountText viewCountText;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class Headline {
    private String simpleText;
}



