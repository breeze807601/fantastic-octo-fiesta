package com.lwl.social_media_platform.pojo.dto;

import com.lwl.social_media_platform.pojo.Image;
import com.lwl.social_media_platform.pojo.Treads;
import com.lwl.social_media_platform.pojo.TreadsTag;
import lombok.Data;

import java.util.List;

@Data
public class TreadsDTO extends Treads {
    private List<TreadsTag> treadsTagList;
    private List<Image> imageList;
}
