package com.zzd.yuso.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 图片
 */
@Data
public class Picture implements Serializable {

    private String title;

    private String url;

}