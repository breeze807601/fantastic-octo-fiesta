package com.lwl.social_media_platform.controller;

import com.lwl.social_media_platform.common.BaseContext;
import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.domain.dto.TreadsDTO;
import com.lwl.social_media_platform.domain.vo.TreadsVo;
import com.lwl.social_media_platform.service.TreadsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tread")
public class TreadsController {
    private final TreadsService treadsService;

    @PostMapping
    public Result<String> publish(@RequestBody TreadsDTO treadsDTO){
        return treadsService.publish(treadsDTO);
    }

    @DeleteMapping("/delete")
    public Result<String> deleteTread(@RequestParam("id") Long id){
        return treadsService.deleteTread(id);
    }

    @GetMapping("/get")
    public Result<TreadsVo> getTread(@RequestParam("id") Long id){
        return treadsService.getTread(id);
    }

    @PostMapping("/update")
    public Result<String> updateTread(@RequestBody TreadsDTO treadsDTO){
        return treadsService.updateTread(treadsDTO);
    }

    @GetMapping("/list")
    public Result<List<TreadsVo>> getTreadsList(@RequestParam("id") Long userId){
        return treadsService.getTreadsList(userId);
    }

    @GetMapping("/my-list")
    public Result<List<TreadsVo>> getMyTreadsList(){
        Long userId = BaseContext.getCurrentId();
        return getTreadsList(userId);
    }
}
