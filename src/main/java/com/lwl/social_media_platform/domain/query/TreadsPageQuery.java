package com.lwl.social_media_platform.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TreadsPageQuery extends PageQuery{
    // 关键字
    private String key;

    // 用户界面下搜索动态时，携带id
    private Long userId;
}
