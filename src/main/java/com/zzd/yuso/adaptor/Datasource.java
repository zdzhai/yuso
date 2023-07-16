package com.zzd.yuso.adaptor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * @author dongdong
 * @Date 2023/7/15 17:18
 */
public interface Datasource<E> {

    /**
     * 接入数据源接口规范
     * @param searchText
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<E> doSearch(String searchText, long pageNum, long pageSize);
}
