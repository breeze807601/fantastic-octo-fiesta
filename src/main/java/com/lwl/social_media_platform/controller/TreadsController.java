package com.lwl.social_media_platform.controller;

import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.pojo.dto.TreadsDTO;
import com.lwl.social_media_platform.pojo.vo.TreadsVo;
import com.lwl.social_media_platform.service.TreadsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
