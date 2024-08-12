package com.lwl.social_media_platform.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentPageQuery extends PageQuery{
    private Long treadsId;
    private Integer replyPageNo = DEFAULT_PAGE_NUM;
    private Integer replyPageSize = DEFAULT_PAGE_SIZE;
}
