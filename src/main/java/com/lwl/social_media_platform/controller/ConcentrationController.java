package com.lwl.social_media_platform.controller;

import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.domain.dto.PageDTO;
import com.lwl.social_media_platform.domain.pojo.Concentration;
import com.lwl.social_media_platform.domain.query.ConcentrationPageQuery;
import com.lwl.social_media_platform.domain.vo.UserConcentrationVo;
import com.lwl.social_media_platform.service.ConcentrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/concentration")
public class ConcentrationController {
    private final ConcentrationService concentrationService;

    @PostMapping
    public Result<String> follow(@RequestBody Concentration concentration) {
        return concentrationService.saveConcentration(concentration);
    }

    @DeleteMapping("/cancel")
    public Result<String> cancel(@RequestParam("toUserId") Long toUserId) {
        return concentrationService.cancelConcentration(toUserId);
    }

    /**
     * 获取关注该用户的粉丝列表
     *
     * @param concentrationPageQuery 分页条件
     * @return 粉丝 userVo 分页
     */
    @GetMapping("/get-fans")
    public Result<PageDTO<UserConcentrationVo>> getFans(ConcentrationPageQuery concentrationPageQuery) {
        return concentrationService.getFans(concentrationPageQuery);
    }

    /**
     * 获取该用户的关注列表
     *
     * @param concentrationPageQuery 分页条件
     * @return 关注的 userVo 分页
     */
    @GetMapping("/get-concentration")
    public Result<PageDTO<UserConcentrationVo>> getConcentration(ConcentrationPageQuery concentrationPageQuery) {
        return concentrationService.getConcentration(concentrationPageQuery);
    }

    /**
     * 该用户的粉丝数
     *
     * @return 粉丝数
     */
    @GetMapping("/get-fans-num")
    public Result<Long> getFansNum(Long userId) {
        return Result.success(concentrationService.getFansNum(userId));
    }

    /**
     * 该用户的关注数
     *
     * @return 关注数
     */
    @GetMapping("/get-concentration-num")
    public Result<Long> getToConcentrationNum(Long userId) {
        return Result.success(concentrationService.getConcentrationNum(userId));
    }

}
