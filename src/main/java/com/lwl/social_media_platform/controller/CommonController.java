package com.lwl.social_media_platform.controller;

import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.utils.AliOSSUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/common")
public class CommonController {
    private final AliOSSUtils aliOSSUtils;
    @PostMapping("/images")
    public Result<List<String>> saveImages(@RequestBody List<MultipartFile> multipartFiles) throws IOException {
        List<String> urls = multipartFiles.stream()
                .map(file -> {
                    try {
                        return aliOSSUtils.upload(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        return Result.success(urls);
    }
}
