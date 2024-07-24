package com.lwl.social_media_platform.domain.vo;

import com.lwl.social_media_platform.domain.pojo.Image;
import com.lwl.social_media_platform.domain.pojo.Tag;
import com.lwl.social_media_platform.domain.pojo.Treads;
import lombok.Data;

import java.util.List;

@Data
public class TreadsVo extends Treads {
    private List<Tag> tagList;
    private List<Image>  imageList;
    private Boolean isFollow;
}
