package com.lwl.social_media_platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.domain.pojo.Concentration;
import com.lwl.social_media_platform.service.ConcentrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/concentration")
public class ConcentrationController {
    private final ConcentrationService concentrationService;

    @PostMapping
    public Result<String> follow(@RequestBody Concentration concentration){
        return concentrationService.saveConcentration(concentration);
    }

    @DeleteMapping("/cancel")
    public Result<String> cancel(@RequestParam("toUserId") Long toUserId){
        return concentrationService.cancelConcentration(toUserId);
    }

    @GetMapping("get")
    public Result<Page<Concentration>> getConcentration(int page, int pageSize){
        return concentrationService.getFollow(page,pageSize);
    }

}
