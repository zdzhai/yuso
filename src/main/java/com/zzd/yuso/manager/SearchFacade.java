package com.zzd.yuso.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzd.yuso.adaptor.*;
import com.zzd.yuso.common.ErrorCode;
import com.zzd.yuso.exception.ThrowUtils;
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
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 门面类根据类型调用不同模块的方法
 * @author dongdong
 * @Date 2023/7/15 17:10
 */
@Component
public class SearchFacade {

    @Resource
    private PictureService pictureService;

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    @Resource
    private DataSourceRegistry dataSourceRegistry;


    public SearchVO searchAll(@RequestBody SearchQueryRequest searchQueryRequest,
                                            HttpServletRequest request) {
        String type = searchQueryRequest.getType();
        String searchText = searchQueryRequest.getSearchText();
        long current = searchQueryRequest.getCurrent();
        long size = searchQueryRequest.getPageSize();
        SearchTypeEnum enumByValue = SearchTypeEnum.getEnumByValue(type);
        ThrowUtils.throwIf(type == null, ErrorCode.PARAMS_ERROR);
        if (enumByValue == null) {
            // 限制爬虫
            ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
            Page<Picture> picturePage = pictureService.searchPicture(searchText, current, size);
            UserQueryRequest userQueryRequest = new UserQueryRequest();
            userQueryRequest.setUserName(searchText);
            Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
            PostQueryRequest postQueryRequest = new PostQueryRequest();
            postQueryRequest.setSearchText(searchText);
            Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
            SearchVO searchVO = new SearchVO();
            searchVO.setUserList(userVOPage.getRecords());
            searchVO.setPostList(postVOPage.getRecords());
            searchVO.setPictureList(picturePage.getRecords());
            return searchVO;
        } else {
            Datasource dataSource = dataSourceRegistry.getDataSource(enumByValue.getValue());
            SearchVO searchVO = new SearchVO();
            Page<?> page = dataSource.doSearch(searchText, current, size);
            searchVO.setDataList(page.getRecords());
/*            switch (enumByValue) {
                case USER:
                    UserQueryRequest userQueryRequest = new UserQueryRequest();
                    userQueryRequest.setUserName(searchText);
                    Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
                    searchVO.setUserList(userVOPage.getRecords());
                    break;
                case POST:
                    PostQueryRequest postQueryRequest = new PostQueryRequest();
                    postQueryRequest.setSearchText(searchText);
                    Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
                    searchVO.setPostList(postVOPage.getRecords());
                    break;
                case PICTURE:
                    Page<Picture> picturePage = pictureService.searchPicture(searchText, current, size);
                    searchVO.setPictureList(picturePage.getRecords());
                    break;
                default:
            }*/
            return searchVO;
        }
    }
}
