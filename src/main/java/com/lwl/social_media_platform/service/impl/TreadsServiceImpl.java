package com.lwl.social_media_platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwl.social_media_platform.common.BaseContext;
import com.lwl.social_media_platform.common.Result;
import com.lwl.social_media_platform.domain.dto.PageDTO;
import com.lwl.social_media_platform.domain.dto.TreadsDTO;
import com.lwl.social_media_platform.domain.pojo.*;
import com.lwl.social_media_platform.domain.query.TreadsPageQuery;
import com.lwl.social_media_platform.domain.vo.TreadsVo;
import com.lwl.social_media_platform.mapper.TreadsMapper;
import com.lwl.social_media_platform.service.*;
import com.lwl.social_media_platform.utils.BeanUtils;
import com.lwl.social_media_platform.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TreadsServiceImpl extends ServiceImpl<TreadsMapper, Treads> implements TreadsService {
    private final TagService tagService;
    private final TreadsTagService treadsTagService;
    private final ImageService imageService;
    private final ConcentrationService concentrationService;
    private final SupportService supportService;
    private final UserService userService;

    @Override
    @Transactional
    public Result<String> publish(TreadsDTO treadsDTO) {
        Long userId = BaseContext.getCurrentId();

        treadsDTO.setContent(
                treadsDTO.getContent()
                        .replace("\n", "<br/>")
                        .replace("\r", "")
        );

        treadsDTO.setUserId(userId);

        String content = treadsDTO.getContent();
        String str = content.replace("\n", "<br/>").replace("\r", "");
        treadsDTO.setContent(str);
        treadsDTO.setCreateTime(LocalDateTime.now());

        // 保存动态
        this.save(treadsDTO);

        Long treadsId = treadsDTO.getId();

        // 为 tag 设置动态id
        List<TreadsTag> treadsTagList = treadsDTO.getTreadsTagList();
        if (CollUtil.isNotEmpty(treadsTagList)) {
            treadsTagList.forEach(item -> item.setTreadsId(treadsId));
            // 保存标签
            treadsTagService.saveBatch(treadsTagList);
        }

        // 为 图片列表 设置动态id
        List<Image> imageList = treadsDTO.getImageList();
        if (CollUtil.isNotEmpty(imageList)) {
            imageList.forEach(item -> item.setTreadsId(treadsId));
            // 保存图片
            imageService.saveBatch(imageList);
        }

        return Result.success("发布成功");
    }

    @Override
    @Transactional
    public Result<String> deleteTread(Long id) {
        // 删除动态
        this.removeById(id);

        // 删除动态相关标签
        treadsTagService.remove(new LambdaQueryWrapper<TreadsTag>().eq(TreadsTag::getTreadsId, id));
        // 删除动态相关图片
        imageService.remove(new LambdaQueryWrapper<Image>().eq(Image::getTreadsId, id));

        return Result.success("删除成功");
    }

    @Override
    public Result<TreadsVo> getTread(Long id) {
        Treads treads = this.getById(id);
        return Result.success(getTreadsVo(treads));// 调用 getTreadsVo 方法 返回 TreadsVo
    }

    @Override
    public Result<PageDTO<TreadsVo>> getTreadByUserId(TreadsPageQuery treadsPageQuery) {
        Page<Treads> treadsPage = this.page(
                treadsPageQuery.toMpPageDefaultSortByCreateTimeDesc(),
                new LambdaQueryWrapper<Treads>().eq(Treads::getUserId, treadsPageQuery.getUserId()));
        List<Treads> treadsList = treadsPage.getRecords();
        List<TreadsVo> treadsVoList = treadsList.stream().map(this::getTreadsVo).toList();
        PageDTO<TreadsVo> treadsVoPage = PageUtils.of(treadsPage, treadsVoList);

        return Result.success(treadsVoPage);
    }


    @Override
    public Result<List<TreadsVo>> getTreadsList(Long userId) {

        List<Treads> treadsList;

        // 获取动态
        if (userId == null) {
            treadsList = this.list();
        } else {
            treadsList = this.list(new LambdaQueryWrapper<Treads>().eq(Treads::getUserId, userId));
        }

        // 将每个 Treads 转换成 TreadsVo
        List<TreadsVo> treadsVoList = treadsList.stream()
                .map(this::getTreadsVo)
                .toList();

        return Result.success(treadsVoList);
    }

    @Override
    public Result<PageDTO<TreadsVo>> getTreadsPage(TreadsPageQuery treadsPageQuery) {
        Page<Treads> treadsPage = this.lambdaQuery()
                .like(StrUtil.isNotEmpty(treadsPageQuery.getKey()), Treads::getContent, treadsPageQuery.getKey())
                .page(treadsPageQuery.toMpPageDefaultSortByCreateTimeDesc());

        List<Treads> records = treadsPage.getRecords();
        List<TreadsVo> treadsVos = records.stream()
                .map(this::getTreadsVo)
                .toList();

        return Result.success(PageUtils.of(treadsPage, treadsVos));
    }

    @Override
    @Transactional
    public Result<String> updateTread(TreadsDTO treadsDTO) {
        LambdaUpdateWrapper<Treads> updateWrapper = new LambdaUpdateWrapper<>();
        LambdaQueryWrapper<TreadsTag> queryWrapper = new LambdaQueryWrapper<>();

        Long treadsId = treadsDTO.getId();

        // 更新动态内容
        this.update(updateWrapper.eq(Treads::getId, treadsId));

        // 删除该动态的标签
        treadsTagService.remove(queryWrapper.eq(TreadsTag::getTreadsId, treadsId));

        // 获取该动态的新标签
        List<TreadsTag> treadsTagList = treadsDTO.getTreadsTagList();
        if (CollUtil.isNotEmpty(treadsTagList)) {
            // 设置动态id
            treadsTagList.forEach(item -> item.setTreadsId(treadsId));

            // 保存新标签
            treadsTagService.saveBatch(treadsTagList);
        }

        return Result.success("更新成功");
    }


    /**
     * 将 组合 TreadsVo 抽象出为一个方法
     *
     * @param treads 动态
     * @return treadsVo
     */
    private TreadsVo getTreadsVo(Treads treads) {
        long userId = BaseContext.getCurrentId();

        // 获取该动态的标签id
        Long id = treads.getId();
        List<TreadsTag> treadsTags = treadsTagService.list(new LambdaQueryWrapper<TreadsTag>().eq(TreadsTag::getTreadsId, id));
        // 取出标签id
        List<Long> tagsId = treadsTags.stream().map(TreadsTag::getTagId).toList();
        List<Tag> tags;
        // 根据id获取标签内容
        if (CollUtil.isNotEmpty(tagsId)) {
            tags = tagService.listByIds(tagsId);
        } else {
            tags = Collections.emptyList();
        }

        // 获取图片url
        List<Image> imageList = imageService.list(new LambdaQueryWrapper<Image>().eq(Image::getTreadsId, id));

        // 获取动态作者id
        long toUserId = treads.getUserId();
        // 是否关注
        Concentration concentration = concentrationService.getOne(
                new LambdaQueryWrapper<Concentration>()
                        .eq(Concentration::getUserId, userId)
                        .eq(Concentration::getToUserId, toUserId)
        );

        // 获取动态作者
        User user = userService.getById(toUserId);

        LambdaQueryWrapper<Support> supportLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 获取点赞数
        long supportNum = supportService.count(supportLambdaQueryWrapper.eq(Support::getTreadsId, id));
        // 是否点赞
        boolean isSupport = supportService.exists(supportLambdaQueryWrapper.eq(Support::getTreadsId, id).eq(Support::getUserId, userId));

        // 转换为vo
        TreadsVo treadsVo = BeanUtil.copyProperties(treads, TreadsVo.class);
        // 设置标签 图片url 是否关注 点赞数 已点赞
        treadsVo.setTagList(tags)
                .setImageList(imageList)
                .setIsFollow(concentration != null)
                .setSupportNum(supportNum)
                .setNickName(user.getUsername())
                .setPic(user.getPic())
                .setIsSupport(isSupport);

        return treadsVo;
    }

    @Override
    public Result<String> support(Support support) {
        supportService.save(support);
        return Result.success("点赞成功");
    }

    @Override
    public Result<String> cancelSupport(Support support) {
        supportService.remove(
                new LambdaQueryWrapper<Support>()
                        .eq(Support::getTreadsId, support.getTreadsId())
                        .eq(Support::getUserId, support.getUserId())
        );
        return Result.success("取消点赞成功");
    }

    @Override
    public List<TreadsVo> getCurrentHotTreads() {
        long timestamp = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000;
        LocalDateTime localDateTimeBefore = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();

        List<Treads> treadsList = this.lambdaQuery()
                .le(Treads::getCreateTime, LocalDateTime.now())
                .ge(Treads::getCreateTime, localDateTimeBefore)
                .list();

        Map<Long, Long> currentMaxSupportNum = supportService.getCurrentMaxSupportNum(treadsList.stream().map(Treads::getId).toList());

        List<TreadsVo> treadsVos = new ArrayList<>();

        treadsList.forEach(item -> {
            Long supportNum = currentMaxSupportNum.get(item.getId());
            if(supportNum != null){
                TreadsVo treadsVo = BeanUtils.copyProperties(item, TreadsVo.class);
                treadsVo.setSupportNum(supportNum);
                treadsVos.add(treadsVo);
            }
        });

        return treadsVos;
    }


}
