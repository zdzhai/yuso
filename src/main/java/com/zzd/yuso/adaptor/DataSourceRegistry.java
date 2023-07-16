package com.zzd.yuso.adaptor;

import com.zzd.yuso.model.enums.SearchTypeEnum;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源注册器
 * @author dongdong
 * @Date 2023/7/15 17:48
 */
@Component
public class DataSourceRegistry {

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    Map<String, Datasource<T>> datasourceMap;

    /**
     * 在该类初始化后就加载bean
     */
    @PostConstruct
    public void doInit(){
        datasourceMap = new HashMap(){{
            put(SearchTypeEnum.POST.getValue(),postDataSource);
            put(SearchTypeEnum.USER.getValue(),userDataSource);
            put(SearchTypeEnum.PICTURE.getValue(),pictureDataSource);

        }};
    }

   /* *//**
     * 使用静态代码块进行加载
     *//*
    static {
        Map<String, Datasource<T>> datasourceMap = new HashMap(){{
            put(SearchTypeEnum.POST.getValue(),postDataSource);
            put(SearchTypeEnum.USER.getValue(),userDataSource);
            put(SearchTypeEnum.PICTURE.getValue(),pictureDataSource);

        }};
    }*/

    /**
     * 获取对应模块的数据源
     * @param type
     * @return
     */
    public Datasource getDataSource(String type){
        if (datasourceMap == null){
            return  null;
        }
        // todo 这里创建的是可以使用一下单例模式
        return datasourceMap.get(type);
    }
}
