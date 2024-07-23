package com.lwl.social_media_platform.pojo.vo;

import com.lwl.social_media_platform.pojo.Image;
import com.lwl.social_media_platform.pojo.Tag;
import com.lwl.social_media_platform.pojo.Treads;
import lombok.Data;

import java.util.List;

@Data
public class TreadsVo extends Treads {
    private List<Tag> tagList;
    private List<Image>  imageList;
}
