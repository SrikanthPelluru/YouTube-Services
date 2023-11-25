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
public class YoutubeSearchData {
    private ItemSectionRenderer itemSectionRenderer;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class ReelShelfRenderer {
    private List<Items> items;
}

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class Items {
    private ReelItemRenderer reelItemRenderer;
}
