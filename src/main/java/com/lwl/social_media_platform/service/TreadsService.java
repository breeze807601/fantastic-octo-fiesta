package com.lwl.social_media_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.pojo.Treads;
import com.lwl.social_media_platform.pojo.dto.TreadsDTO;
import com.lwl.social_media_platform.pojo.vo.TreadsVo;

public interface TreadsService extends IService<Treads> {
    Result<String> publish(TreadsDTO treadsDTO);

    Result<String> delete(Long id);

    Result<TreadsVo> getTread(Long id);

    Result<String> updateTread(TreadsDTO treadsDTO);

}
