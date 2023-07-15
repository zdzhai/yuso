package com.zzd.yuso.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zzd.yuso.model.entity.Post;
import com.zzd.yuso.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 每次重启时会执行一次
 */
//@Component
@Slf4j
public class FetchInitPost implements CommandLineRunner {

    @Resource
    private PostService postService;


    @Override
    public void run(String... args) {
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
        log.info("初始化帖子列表成功", postList.size());
    }
}
