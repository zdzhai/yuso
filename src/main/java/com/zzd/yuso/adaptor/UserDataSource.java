package com.zzd.yuso.adaptor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzd.yuso.model.dto.user.UserQueryRequest;
import com.zzd.yuso.model.vo.SearchVO;
import com.zzd.yuso.model.vo.UserVO;
import com.zzd.yuso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户数据源实现
 *

 */
@Service
@Slf4j
public class UserDataSource implements Datasource {

    @Resource
    private UserService userService;

    @Override
    public Page<UserVO> doSearch(String searchText, long pageNum, long pageSize) {
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        userQueryRequest.setCurrent(pageNum);
        userQueryRequest.setPageSize(pageSize);
        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
        return userVOPage;
    }
}
