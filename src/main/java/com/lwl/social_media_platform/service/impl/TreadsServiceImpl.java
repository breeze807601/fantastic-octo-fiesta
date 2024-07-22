package com.lwl.social_media_platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
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

import java.time.LocalDateTime;
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

        treadsDTO.setUserId(1L);

        treadsDTO.setCreateTime(LocalDateTime.now());

        // 保存动态
        this.save(treadsDTO);

        Long treadsId = treadsDTO.getId();

        // 设置动态id
        List<TreadsTag> treadsTagList = treadsDTO.getTreadsTagList();
        if (CollUtil.isNotEmpty(treadsTagList)) {
            treadsTagList.stream().map(item -> item.setTreadId(treadsId)).collect(Collectors.toList());

            treadsTagMapper.insert(treadsTagList);
        }

        return Result.success("发布成功");
    }

    @Override
    @Transactional
    public Result<String> deleteTread(Long id) {
        // 删除动态
        this.remove(new LambdaQueryWrapper<Treads>().eq(Treads::getId,id));
        // 删除动态相关标签
        treadsTagMapper.delete(new LambdaQueryWrapper<TreadsTag>().eq(TreadsTag::getTreadId,id));
        return Result.success("删除成功");
    }

    @Override
    public Result<TreadsVo> getTread(Long id) {
        // 获取动态
        Treads treads = this.getById(id);

        // 获取该动态的标签id
        List<TreadsTag> treadsTags = treadsTagMapper.selectList(new LambdaQueryWrapper<TreadsTag>().eq(TreadsTag::getTreadId, id));
        // 去除标签id
        List<Long> tagsId = treadsTags.stream().map(TreadsTag::getTagId).toList();
        // 根据id获取标签内容
        List<Tag> tags = tagService.listByIds(tagsId);

        // 转换为vo
        TreadsVo treadsVo = BeanUtil.copyProperties(treads, TreadsVo.class);
        // 设置标签
        treadsVo.setTagList(tags);

        return Result.success(treadsVo);
    }

    @Override
    @Transactional
    public Result<String> updateTread(TreadsDTO treadsDTO) {
        LambdaUpdateWrapper<Treads> updateWrapper = new LambdaUpdateWrapper<>();
        LambdaQueryWrapper<TreadsTag> queryWrapper = new LambdaQueryWrapper<>();

        Long treadsId = treadsDTO.getId();

        // 更新动态内容
        this.update(updateWrapper.eq(Treads::getId,treadsId));

        // 删除该动态的标签
        treadsTagMapper.delete(queryWrapper.eq(TreadsTag::getTreadId,treadsId));

        // 获取该动态的新标签
        List<TreadsTag> treadsTagList = treadsDTO.getTreadsTagList();
        // 设置动态id
        treadsTagList.stream().map(item -> item.setTreadId(treadsId)).collect(Collectors.toList());
        // 保存新标签
        treadsTagMapper.insert(treadsTagList);

        return Result.success("更新成功");
    }
}
