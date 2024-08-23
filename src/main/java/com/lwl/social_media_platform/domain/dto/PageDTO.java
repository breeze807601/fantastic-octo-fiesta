package com.lwl.social_media_platform.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PageDTO<T> {
    protected Long total;
    protected Long pages;
    protected List<T> list;
}
