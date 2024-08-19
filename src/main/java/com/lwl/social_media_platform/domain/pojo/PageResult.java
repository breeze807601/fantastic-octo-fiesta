package com.lwl.social_media_platform.domain.pojo;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    private List<T> list;
    private Integer totalPage;
    private String scrollId;
    private Object[] lastSortValues;

}
