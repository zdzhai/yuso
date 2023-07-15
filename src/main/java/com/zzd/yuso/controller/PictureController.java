package com.zzd.yuso.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzd.yuso.common.BaseResponse;
import com.zzd.yuso.common.ErrorCode;
import com.zzd.yuso.common.ResultUtils;
import com.zzd.yuso.exception.ThrowUtils;
import com.zzd.yuso.model.dto.picture.PictureQueryRequest;
import com.zzd.yuso.model.entity.Picture;
import com.zzd.yuso.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 图片接口
 *
 */
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {

    @Resource
    private PictureService pictureService;


    /**
     * 分页获取列表（封装类）
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                        HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        String searchText = pictureQueryRequest.getSearchText();
        Page<Picture> listPage = pictureService.searchPicture(searchText, current, size);
        return ResultUtils.success(listPage);
    }
}
