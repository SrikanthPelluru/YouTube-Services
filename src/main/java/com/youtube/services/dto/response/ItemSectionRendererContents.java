package com.youtube.services.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemSectionRendererContents {
    private VideoRenderer videoRenderer;
    private ReelShelfRenderer reelShelfRenderer;
}