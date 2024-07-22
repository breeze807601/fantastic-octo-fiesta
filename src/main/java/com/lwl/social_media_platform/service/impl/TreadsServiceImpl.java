package com.lwl.social_media_platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwl.social_media_platform.common.BaseContext;
import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.mapper.TreadsMapper;
import com.lwl.social_media_platform.mapper.TreadsTagMapper;
import com.lwl.social_media_platform.pojo.Tag;
import com.lwl.social_media_platform.pojo.Treads;
import com.lwl.social_media_platform.pojo.TreadsTag;
import com.lwl.social_media_platform.pojo.dto.TreadsDTO;
import com.lwl.social_media_platform.pojo.vo.TreadsVo;
import com.lwl.social_media_platform.service.TagService;
import com.lwl.social_media_platform.service.TreadsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TreadsServiceImpl extends ServiceImpl<TreadsMapper, Treads> implements TreadsService {
    private final TagService tagService;
    private final TreadsTagMapper treadsTagMapper;
    @Override
    @Transactional
    public Result<String> publish(TreadsDTO treadsDTO) {
        Long userId = BaseContext.getCurrentId();

        treadsDTO.setUserId(userId);

        this.save(treadsDTO);

        Long treadsId = treadsDTO.getId();

        List<TreadsTag> treadsTagList = treadsDTO.getTreadsTagList();
        treadsTagList.stream().map(item -> item.setTreadId(treadsId)).collect(Collectors.toList());

        treadsTagMapper.insert(treadsTagList);
        return Result.success("发布成功");
    }

    @Override
    @Transactional
    public Result<String> delete(Long id) {
        this.remove(new LambdaQueryWrapper<Treads>().eq(Treads::getId,id));
        treadsTagMapper.delete(new LambdaQueryWrapper<TreadsTag>().eq(TreadsTag::getTreadId,id));
        return Result.success("删除成功");
    }

    @Override
    public Result<TreadsVo> getTread(Long id) {
        Treads treads = this.getById(id);
        List<TreadsTag> treadsTags = treadsTagMapper.selectList(new LambdaQueryWrapper<TreadsTag>().eq(TreadsTag::getTreadId, id));
        List<Long> tagsId = treadsTags.stream().map(TreadsTag::getTagId).toList();

        List<Tag> tags = tagService.listByIds(tagsId);

        TreadsVo treadsVo = BeanUtil.copyProperties(treads, TreadsVo.class);

        treadsVo.setTagList(tags);
        return Result.success(treadsVo);
    }

    @Override
    @Transactional
    public Result<String> updateTread(TreadsDTO treadsDTO) {
        LambdaUpdateWrapper<Treads> updateWrapper = new LambdaUpdateWrapper<>();
        LambdaQueryWrapper<TreadsTag> queryWrapper = new LambdaQueryWrapper<>();

        Long treadsId = treadsDTO.getId();

        this.update(updateWrapper.eq(Treads::getId,treadsId));

        treadsTagMapper.delete(queryWrapper.eq(TreadsTag::getTreadId,treadsId));

        List<TreadsTag> treadsTagList = treadsDTO.getTreadsTagList();
        treadsTagList.stream().map(item -> item.setTreadId(treadsId)).collect(Collectors.toList());

        treadsTagMapper.insert(treadsTagList);

        return Result.success("更新成功");
    }
}
