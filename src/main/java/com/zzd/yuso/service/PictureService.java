package com.zzd.yuso.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzd.yuso.model.entity.Picture;

/**
 * 图片服务
 *
 */
public interface PictureService {

    Page<Picture> searchPicture(String searchText, long pageNum, long pageSize);
}
