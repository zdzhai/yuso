package com.zzd.yuso.controller;
import com.google.common.collect.Lists;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzd.yuso.common.BaseResponse;
import com.zzd.yuso.common.ErrorCode;
import com.zzd.yuso.common.ResultUtils;
import com.zzd.yuso.exception.BusinessException;
import com.zzd.yuso.exception.ThrowUtils;
import com.zzd.yuso.manager.SearchFacade;
import com.zzd.yuso.model.dto.picture.PictureQueryRequest;
import com.zzd.yuso.model.dto.post.PostQueryRequest;
import com.zzd.yuso.model.dto.search.SearchQueryRequest;
import com.zzd.yuso.model.dto.user.UserQueryRequest;
import com.zzd.yuso.model.entity.Picture;
import com.zzd.yuso.model.enums.SearchTypeEnum;
import com.zzd.yuso.model.vo.PostVO;
import com.zzd.yuso.model.vo.SearchVO;
import com.zzd.yuso.model.vo.UserVO;
import com.zzd.yuso.service.PictureService;
import com.zzd.yuso.service.PostService;
import com.zzd.yuso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * 图片接口
 *
 * @author zzd
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private SearchFacade searchFacade;

    /**
     * 分页获取列表（封装类）
     *
     * @param searchQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchQueryRequest searchQueryRequest,
                                            HttpServletRequest request) {
        ThrowUtils.throwIf(searchQueryRequest == null,ErrorCode.PARAMS_ERROR);
        SearchVO searchVO = searchFacade.searchAll(searchQueryRequest, request);
        return ResultUtils.success(searchVO);
    }


/*    @PostMapping("/all2")
    public BaseResponse<SearchVO> searchAll2(@RequestBody SearchQueryRequest searchQueryRequest,
                                            HttpServletRequest request) {
        long current = searchQueryRequest.getCurrent();
        long size = searchQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        String searchText = searchQueryRequest.getSearchText();
        //创建异步任务
        CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(
                () -> {
            Page<Picture> picturePage = pictureService.searchPicture(searchText, current, size);
            return picturePage;
        });
        CompletableFuture<Page<UserVO>> userVOTask = CompletableFuture.supplyAsync(
                () -> {
                    UserQueryRequest userQueryRequest = new UserQueryRequest();
                    userQueryRequest.setUserName(searchText);
                    Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
                    return userVOPage;
                }
        );
        CompletableFuture<Page<PostVO>> postVOTask = CompletableFuture.supplyAsync(
                () -> {
                    PostQueryRequest postQueryRequest = new PostQueryRequest();
                    postQueryRequest.setSearchText(searchText);
                    Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
                    return postVOPage;
                }
        );
        CompletableFuture.allOf(pictureTask, userVOTask, postVOTask).join();
        try {
            Page<Picture> picturePage = pictureTask.get();
            Page<UserVO> userVOPage = userVOTask.get();
            Page<PostVO> postVOPage = postVOTask.get();
            SearchVO searchVO = new SearchVO();
            searchVO.setUserList(userVOPage.getRecords());
            searchVO.setPostList(postVOPage.getRecords());
            searchVO.setPictureList(picturePage.getRecords());
            return ResultUtils.success(searchVO);
        } catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"查询异常");
        }
    }*/
}
