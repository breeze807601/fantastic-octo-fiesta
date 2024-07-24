package com.lwl.social_media_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.domain.pojo.Treads;
import com.lwl.social_media_platform.domain.dto.TreadsDTO;
import com.lwl.social_media_platform.domain.vo.TreadsVo;

import java.util.List;

public interface TreadsService extends IService<Treads> {
    Result<String> publish(TreadsDTO treadsDTO);

    Result<String> deleteTread(Long id);

    Result<TreadsVo> getTread(Long id);

    Result<List<TreadsVo>> getTreadsList(Long userId);

    Result<String> updateTread(TreadsDTO treadsDTO);


}
