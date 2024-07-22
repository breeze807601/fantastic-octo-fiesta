package com.lwl.social_media_platform.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String pic;
    private String password;
    private String nickname;
    private String sex;
    private String idCard;
    private String phone;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
