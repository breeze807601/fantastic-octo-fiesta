package com.lwl.social_media_platform.domain.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Image {
    private Long id;
    private String url;
    private Long treadsId;
}
