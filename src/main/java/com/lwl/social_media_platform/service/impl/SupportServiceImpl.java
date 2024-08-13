package com.lwl.social_media_platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwl.social_media_platform.domain.pojo.Support;
import com.lwl.social_media_platform.mapper.SupportMapper;
import com.lwl.social_media_platform.service.SupportService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SupportServiceImpl extends ServiceImpl<SupportMapper, Support> implements SupportService {
    @Override
    public Map<Long, Long> getCurrentMaxSupportNum(List<Long> treadIds) {
        QueryWrapper<Support> queryWrapper = new QueryWrapper<>();

        queryWrapper.select("treads_id","count(treads_id) as support_count")
                .in("treads_id",treadIds)
                .groupBy("treads_id")
                .orderByAsc("support_count")
                .last("limit 7");

        List<Map<String, Object>> maps = baseMapper.selectMaps(queryWrapper);
        Map<Long, Long> hashMap = new HashMap<>();

        maps.forEach(item ->{
            Long treadsId = (Long)item.get("treads_id");
            Long supportCount = (Long)item.get("support_count");
            hashMap.put(treadsId,supportCount);
        });

        return hashMap;
    }
}
