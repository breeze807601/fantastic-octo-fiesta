package com.lwl.social_media_platform.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TreadsTag {
    private Long id;
    private Long treadsId;
    private Long tagId;
}
