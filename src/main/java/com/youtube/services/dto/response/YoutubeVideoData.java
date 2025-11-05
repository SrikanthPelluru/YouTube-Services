package com.youtube.services.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class YoutubeVideoData {
    private String videoId;
    private String title;
    private String thumbnailUrl;
    private String channelName;
    private String duration;
}
