package com.zzd.yuso.test;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zzd.yuso.job.cycle.SimpleCanalClient;
import com.zzd.yuso.model.entity.Picture;
import com.zzd.yuso.model.entity.Post;
import com.zzd.yuso.service.PostService;
import org.apache.http.util.Asserts;
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
public class CrawlerTest {

    @Resource
    private PostService postService;

    @Test
    void test() {
        // 1.获取数据
        String json = "{\"current\":1,\"pageSize\":8,\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"category\":\"文章\",\"reviewStatus\":1}";
        String result = HttpRequest
                .post("https://www.code-nav.cn/api/post/search/page/vo")
                .body(json)
                .execute()
                .body();
        //2.json转对象
        Map<String,Object> map = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");
        System.out.println(records);
        List<Post> postList = new ArrayList<>();
        for (Object record : records) {
             JSONObject tempRecord = (JSONObject) record;
            Post post = new Post();
            post.setTitle(tempRecord.getStr("title"));
            post.setContent(tempRecord.getStr("content"));
            JSONArray tagsArray = (JSONArray) tempRecord.get("tags");
            List<String> tags = tagsArray.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(tags));
            post.setUserId(1L);
            postList.add(post);
        }
        //3.存入到数据库
        boolean b = postService.saveBatch(postList);
        Assertions.assertTrue(b);
    }

    @Test
    void testPicture() throws IOException {
        String url = "https://cn.bing.com/images/search?q=小黑子&qpvt=小黑子&form=IGRE&first=1&cw=1177&ch=727";
        Document doc = Jsoup.connect(url).get();
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
        }
    }
}
