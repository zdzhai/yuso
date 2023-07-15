package com.zzd.yuso.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzd.yuso.common.ErrorCode;
import com.zzd.yuso.exception.BusinessException;
import com.zzd.yuso.model.entity.*;
import com.zzd.yuso.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 图片服务实现
 *

 */
@Service
@Slf4j
public class PictureServiceImpl implements PictureService {


    @Override
    public Page<Picture> searchPicture(String searchText, long pageNum, long pageSize) {
        long current = (pageNum - 1) * pageSize;
        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%s",searchText , current);
        Document doc = null;
        try {
             doc = Jsoup.connect(url).get();
        } catch (IOException e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据获取异常");
        }
        Elements newsHeadlines = doc.select(".iuscp.isv");
        List<Picture> pictureList = new ArrayList<>();
        for (Element element : newsHeadlines) {
            //图片地址
            String m = element.select(".iusc").get(0).attr("m");
            String murl = (String) JSONUtil.toBean(m, Map.class).get("murl");
            //标题
            String title = element.select(".inflnk").get(0).attr("aria-label");
            Picture picture = new Picture();
            picture.setTitle(title);
            picture.setUrl(murl);
            pictureList.add(picture);
            if (pictureList.size() >= pageSize){
                break;
            }
        }
        Page<Picture> picturePage = new Page<>();
        picturePage.setRecords(pictureList);
        return picturePage;
    }
}




