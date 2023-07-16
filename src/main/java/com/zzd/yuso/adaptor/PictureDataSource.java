package com.zzd.yuso.adaptor;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzd.yuso.common.ErrorCode;
import com.zzd.yuso.exception.BusinessException;
import com.zzd.yuso.model.entity.Picture;
import com.zzd.yuso.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片数据源实现
 *

 */
@Service
@Slf4j
public class PictureDataSource implements Datasource {

    @Resource
    private PictureService pictureService;

    @Override
    public Page<Picture> doSearch(String searchText, long pageNum, long pageSize) {
        Page<Picture> picturePage = pictureService.searchPicture(searchText, pageNum, pageSize);
        return picturePage;
    }
}




