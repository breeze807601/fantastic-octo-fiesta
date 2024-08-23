package com.lwl.social_media_platform.domain.dto;

import com.lwl.social_media_platform.domain.pojo.Image;
import com.lwl.social_media_platform.domain.pojo.Treads;
import com.lwl.social_media_platform.domain.pojo.TreadsTag;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class TreadsDTO extends Treads {
    private List<TreadsTag> treadsTagList;
    private List<Image> imageList;
}
