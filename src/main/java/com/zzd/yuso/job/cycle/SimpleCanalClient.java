package com.zzd.yuso.job.cycle;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import com.zzd.yuso.common.ErrorCode;
import com.zzd.yuso.exception.BusinessException;
import com.zzd.yuso.exception.ThrowUtils;
import com.zzd.yuso.model.dto.post.PostEsDTO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component
@Slf4j
//public class SimpleCanalClient implements InitializingBean {
public class SimpleCanalClient {

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    public static final String ID = "id";

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        incSyncPostToEs();
//    }

    /**
     *
     */
    @PostConstruct
    public void incSyncPostToEs() {
        // 创建链接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(AddressUtils.getHostIp(),
                11111), "example", "", "");
        int batchSize = 1000;
        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();
            while (true) {
                // 获取指定数量的数据
                Message message = connector.getWithoutAck(batchSize);
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"从ES中删除失败");
                    }
                } else {
                    try {
                        printEntry(message.getEntries());
                    } catch (Exception e) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                    }
                }
                // 提交确认
                connector.ack(batchId);
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }
        }
        finally {
            connector.disconnect();
        }
    }

    /**
     * 原始的示例方法
     */
    public void initial() {
        // 创建链接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(AddressUtils.getHostIp(),
                11111), "example", "", "");
        int batchSize = 1000;
        int emptyCount = 0;
        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();
            int totalEmptyCount = 120;
            while (emptyCount < totalEmptyCount) {
                // 获取指定数量的数据
                Message message = connector.getWithoutAck(batchSize);
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    System.out.println("empty count : " + emptyCount);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                } else {
                    emptyCount = 0;
                    // System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                    printEntry(message.getEntries());
                }

                connector.ack(batchId); // 提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }

            System.out.println("empty too many times, exit");
        } finally {
            connector.disconnect();
        }
    }

    private void printEntry(List<Entry> entrys) {
        for (Entry entry : entrys) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChage = null;
            try {
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            EventType eventType = rowChage.getEventType();
            System.out.println(String.format("================&gt; binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));

            for (RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                    deletePostFromEs(rowData.getBeforeColumnsList());
                } else if (eventType == EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());
                    incPostToEs(rowData.getAfterColumnsList());
                } else {
                    System.out.println("-------&gt; before");
                    printColumn(rowData.getBeforeColumnsList());
                    System.out.println("-------&gt; after");
                    printColumn(rowData.getAfterColumnsList());
                    incPostToEs(rowData.getAfterColumnsList());
                }
            }
        }
    }

    private void printColumn(List<Column> columns) {
        for (Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }

    private void incPostToEs(List<Column> columns) {
        Map<String, Object> hashMap = new HashMap<>(columns.size());
        columns.stream().forEach(column -> {
            hashMap.put(column.getName(), column.getValue());
        });
        PostEsDTO postEsDTO = BeanUtil.mapToBean(hashMap, PostEsDTO.class, true, new CopyOptions());
        elasticsearchRestTemplate.save(postEsDTO);
    }
    private void deletePostFromEs(List<Column> columns) {
        Map<String, Object> hashMap = new HashMap<>(1);
        columns.stream().forEach(column -> {
            if (ID.equals(column.getName())){
                hashMap.put(column.getName(), column.getValue());
            }
        });
        String id = (String) hashMap.get(ID);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.matchQuery(ID, id));
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .build();
        SearchHits<PostEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, PostEsDTO.class);
        if (!searchHits.hasSearchHits()){
            return;
        }
        ThrowUtils.throwIf(searchHits == null || !searchHits.hasSearchHits(), ErrorCode.SYSTEM_ERROR);
        SearchHit<PostEsDTO> searchHit = searchHits.getSearchHit(0);
        PostEsDTO postEsDTO = new PostEsDTO();
        BeanUtil.copyProperties(searchHit, postEsDTO,false);
        String deleteId = elasticsearchRestTemplate.delete(postEsDTO);
        log.info("id:{} 的记录已从elasticSearch中删除", deleteId);
    }
}