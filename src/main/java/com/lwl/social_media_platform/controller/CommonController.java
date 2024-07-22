package com.lwl.social_media_platform.controller;

import com.lwl.social_media_platform.utils.AliOSSUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/common")
public class CommonController {
    private final AliOSSUtils aliOSSUtils;
    @PostMapping("/image")
    public void saveImage(@RequestBody MultipartFile multipartFile) throws IOException {
        aliOSSUtils.upload(multipartFile);
    }
}
