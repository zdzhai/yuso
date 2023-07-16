package com.zzd.yuso.adaptor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzd.yuso.model.dto.post.PostQueryRequest;
import com.zzd.yuso.model.entity.Post;
import com.zzd.yuso.model.vo.PostVO;
import com.zzd.yuso.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;

/**
 * 帖子数据源实现
 *
 * @author dongdong

 */
@Service
@Slf4j
public class PostDataSource implements Datasource {

    @Resource
    private PostService postService;

    @Override
    public Page<PostVO> doSearch(String searchText, long pageNum, long pageSize) {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        postQueryRequest.setCurrent(pageNum);
        postQueryRequest.setPageSize(pageSize);
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        Page<PostVO> postVOPage = postService.getPostVOPage(postPage, requestAttributes.getRequest());
        return postVOPage;
    }
}




