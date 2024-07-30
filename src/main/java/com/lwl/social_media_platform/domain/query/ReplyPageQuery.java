package com.lwl.social_media_platform.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ReplyPageQuery extends PageQuery{
    private Long commentId;
}
