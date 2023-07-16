package com.zzd.yuso.model.dto.search;

import com.zzd.yuso.common.PageRequest;
import com.zzd.yuso.model.entity.Picture;
import com.zzd.yuso.model.vo.PostVO;
import com.zzd.yuso.model.vo.UserVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchQueryRequest extends PageRequest implements Serializable {

    /**
     * searchText
     */
    private String searchText;

    private String type;
}