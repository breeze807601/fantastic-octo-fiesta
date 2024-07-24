package com.lwl.social_media_platform.controller;

import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.domain.pojo.Tag;
import com.lwl.social_media_platform.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    @PostMapping
    public Result<Tag> saveTag(@RequestBody Tag tag){
        return tagService.saveTag(tag);
    }

    @GetMapping("/get")
    public Result<List<Tag>> getTag(){
        return tagService.getTagList();
    }

    @DeleteMapping("/delete")
    public Result<String> deleteTag(@RequestParam("/id") Long id){
        return tagService.deleteTag(id);
    }
}
