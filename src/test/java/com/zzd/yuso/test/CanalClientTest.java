package com.zzd.yuso.test;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zzd.yuso.job.cycle.SimpleCanalClient;
import com.zzd.yuso.model.entity.Picture;
import com.zzd.yuso.model.entity.Post;
import com.zzd.yuso.service.PostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dongdong
 * @Date 2023/7/12 16:49
 */
@SpringBootTest
public class CanalClientTest {


    @Resource
    private SimpleCanalClient simpleCanalClient;

//    @Test
//    void testCanalInc(){
//        simpleCanalClient.incSyncPostToEs();
//    }

}
