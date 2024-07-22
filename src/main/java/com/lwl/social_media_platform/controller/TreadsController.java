package com.lwl.social_media_platform.controller;

import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.pojo.dto.TreadsDTO;
import com.lwl.social_media_platform.service.TreadsService;
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
@RequestMapping("/tread")
public class TreadsController {
    private final TreadsService treadsService;

    private final AliOSSUtils aliOSSUtils;

    @PostMapping
    public Result<String> publish(@RequestBody TreadsDTO treadsDTO){
        return treadsService.publish(treadsDTO);
    }

    @PostMapping("/image")
    public void saveImage(@RequestBody MultipartFile multipartFile) throws IOException {
        aliOSSUtils.upload(multipartFile);
    }

}
