package com.youtube.services.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
public class YoutubeVideoData {
    private String videoId;
    private String title;
    private String thumbnailUrl;
    private String channelName;
    private String duration;

    public YoutubeVideoData(String videoId) {
        this.videoId = videoId;
    }

    public YoutubeVideoData() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YoutubeVideoData that)) return false;
        return Objects.equals(getVideoId(), that.getVideoId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVideoId());
    }
}
