package com.zzd.yuso.model.vo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zzd.yuso.model.entity.Picture;
import com.zzd.yuso.model.entity.Post;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子视图
 *

 */
@Data
public class SearchVO implements Serializable {

    /**
     * 用户分页数据
     */
    private List<UserVO> userList;
    /**
     * 帖子分页数据
     */
    private List<PostVO> postList;

    /**
     * 图片数据
     */
    private List<Picture> pictureList;

    /**
     * 总数据
     */
    private List<?> dataList;


}
