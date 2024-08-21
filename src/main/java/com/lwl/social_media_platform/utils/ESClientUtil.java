package com.lwl.social_media_platform.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lwl.social_media_platform.domain.pojo.PageResult;
import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ESClientUtil {
    /**
     * [描述] 查询超时分钟
     */
    private final Integer TIME_OUT = 60;
    /**
     * [描述] es中的自带id字段
     */
    private final String ID_ = "_id";
    private final RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     *
     * @param arrayField  数组的泛型是基本类型，示例： List<String> nameList 、 List<Integer> typeIdList
     * @param nestedField 数组的泛型是包装类型，示例： List<Map<String,Object>> typeList 、List<Subject> subjectList
     */
    public boolean createIndex(String indexName, List<String> arrayField, List<String> nestedField) {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        try {
            XContentBuilder contentBuilder = getContentBuilder(arrayField, nestedField);
            request.mapping(contentBuilder);

            CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            log.info("创建索引：{}", response.toString());
            return true;
        } catch (IOException e) {
            log.error("创建索引失败：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * [描述] 构建 array类型 或者  nested类型
     */
    private XContentBuilder getContentBuilder(List<String> arrayField, List<String> fieldList) throws IOException {
        // 构建索引映射（mapping）
        XContentBuilder mappingBuilder = JsonXContent.contentBuilder()
                .startObject()
                .startObject("properties");
        for (String field :
                arrayField) {
            mappingBuilder.startObject(field)
                    .field("type", "keyword")
                    .endObject();
        }
        for (String filed : fieldList) {
            // 第二个nested字段
            mappingBuilder.startObject(filed)
                    .field("type", "nested")
                    .endObject();
        }
        // 结束properties
        mappingBuilder.endObject()
                // 结束根对象
                .endObject();
        return mappingBuilder;
    }

    /**
     * 判断索引是否存在
     */
    public boolean existIndex(String indexName) {
        try {
            GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
            return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("检查索引失败：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除索引
     */
    public boolean deleteIndex(String indexName) {
        try {
            DeleteIndexRequest deleteRequest = new DeleteIndexRequest(indexName);
            AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices().delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("删除索引{}，返回结果为{}", indexName, acknowledgedResponse.isAcknowledged());
            return acknowledgedResponse.isAcknowledged();
        } catch (IOException e) {
            log.error("删除索引失败：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据id删除文档
     */
    public void deleteDoc(String indexName, String id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
            DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("删除索引{}中id为{}的文档，返回结果为{}", indexName, id, response.status().toString());
        } catch (IOException e) {
            log.error("删除文档失败：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量添加文档
     */
    public boolean multiAddDoc(String indexName, List<JSONObject> JSONObjectList) {
        try {
            BulkRequest request = new BulkRequest(indexName);
            JSONObjectList.forEach(doc -> {
                String source = JSONUtil.toJsonStr(doc);
                request.add(new IndexRequest()
                        .id(doc.getStr("id"))
                        .source(source, XContentType.JSON)
                );
            });
            BulkResponse bulkResponse = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            log.info("向索引{}中批量插入数据的结果为 {}", indexName, !bulkResponse.hasFailures());
            return !bulkResponse.hasFailures();
        } catch (IOException e) {
            log.info("向索引{}中批量插入数据错误信息 {}", indexName, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 更新文档
     */
    public boolean updateDoc(String indexName, String docId, JSONObject jsonObject) {
        try {
            UpdateRequest updateRequest = new UpdateRequest(indexName, docId).doc(JSONUtil.toJsonStr(jsonObject), XContentType.JSON);
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            int total = updateResponse.getShardInfo().getTotal();
            log.info("更新文档受影响的数量为{}", total);
            return total > 0;
        } catch (IOException e) {
            log.error("向索引{}中批量更新数据错误信息{}",indexName,e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据id查询文档
     */
    public String queryDocById(String indexName, String docId){
        try {
            GetRequest getRequest = new GetRequest(indexName, docId);
            GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            return JSONUtil.toJsonStr(getResponse.getSource());
        } catch (IOException e) {
            log.error("根据id查询文档时出现错误{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * [描述]
     * @param combinedQueryAnd 通过 getQueryBuilderAnd() 方法获取
     * @param combinedQueryOr 通过 getQueryBuilderOr() 方法获取
     * @param include 指定查询哪些字段
     * @param sortOrderMap 按照指定字段 正倒序 示例： { "age":SortOrder.DESC }
     * @param pageNum   起始位置（页码从1开始）（不分页不传）
     * @param pageSize   每页大小（不分页不传）
     */
    public PageResult<String> search(
            String indexName, BoolQueryBuilder combinedQueryAnd, BoolQueryBuilder combinedQueryOr,
            Map<String,SortOrder> sortOrderMap, List<String> highlightFields,
            String[] include, Integer pageNum , Integer pageSize) {

        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        getSearchSourceBuilder(sourceBuilder,
                combinedQueryAnd, combinedQueryOr, sortOrderMap,
                highlightFields, include);

        if (pageNum != null && pageSize != null) {
            sourceBuilder.from((pageNum-1)*pageSize);
            sourceBuilder.size(pageSize);
        }
        sourceBuilder.timeout(TimeValue.timeValueSeconds(TIME_OUT));
        searchRequest.source(sourceBuilder);
        SearchResponse response;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("索引{}查询错误：{}",indexName,e.getMessage());
            throw new RuntimeException("查询数据错误");
        }
        return getPageResult(pageNum,pageSize,response,highlightFields);
    }


    private void getSearchSourceBuilder(
            SearchSourceBuilder sourceBuilder,
            BoolQueryBuilder combinedQueryAnd, BoolQueryBuilder combinedQueryOr,
            Map<String, SortOrder> sortOrderMap, List<String> highlightFields, String[] include){
        if(combinedQueryAnd != null){
            sourceBuilder.query(combinedQueryAnd);
        }
        if(combinedQueryOr != null){
            sourceBuilder.query(combinedQueryOr);
        }
        if(sortOrderMap != null){
            List<SortBuilder<?>> sorts = new ArrayList<>();
            for (Map.Entry<String, SortOrder> entry : sortOrderMap.entrySet()) {
                sorts.add(SortBuilders.fieldSort(entry.getKey()).order(entry.getValue()));
            }
            sourceBuilder.sort(sorts);
        }else{
            sourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.DESC));
        }
        if(highlightFields != null){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            for (String field : highlightFields) {
                highlightBuilder.field(field).preTags("<em>").postTags("</em>");
            }
            sourceBuilder.highlighter(highlightBuilder);
        }
        if(include != null){
            sourceBuilder.fetchSource(include,null);
        }
    }


    /**
     * [描述]
     */
    private PageResult<String> getPageResult(Integer pageNum,Integer pageSize,SearchResponse response,List<String> highlightFields ){
        List<String> list = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if(highlightFields != null){
                for (String field : highlightFields) {
                    sourceAsMap.put(field,handleHeightFiled(hit,field));
                }
            }
            list.add(JSONUtil.toJsonStr(sourceAsMap));
        }
        PageResult<String> page = new PageResult<>();
        page.setList(list);
        page.setTotal(response.getHits().getTotalHits().value);
        if(pageSize != null){
            // 如果不分页 则不传 pageSize
            page.setPageNum(pageNum);
            page.setPageSize(pageSize);
            long totalPage = page.getTotal() % pageSize == 0 ? page.getTotal() / pageSize : (page.getTotal() / pageSize) + 1;
            page.setTotalPage((int) totalPage);
        }
        return page;
    }

    private String handleHeightFiled( SearchHit hit,String heightFiled ){
        if(hit.getHighlightFields() != null
                && hit.getHighlightFields().get(heightFiled) != null
                && hit.getHighlightFields().get(heightFiled).fragments().length > 0){
            return hit.getHighlightFields().get(heightFiled).fragments()[0].string();
        }
        return null;
    }

    /**
     * [描述] 所有条件以 and 的方式进行组合
     * @param likeMap 模糊查询 一个值对应 单/多 字段 示例：{"小明":["name","nickName","detail"]}
     * @param singleMap 精准匹配 一个值对应 单 字段 示例：{"age":"18"}
     * @param multipleMap 精准匹配 一个值对应 多 字段 示例：{"180":["age","height","weight"]}
     * @param rangeMap 指定字段 区间查询 示例：{"price":[100,500]} (注意：value值必须要两个元素)
     * @param nestedList 根据 getNestedListQuery() 传参生成的嵌套查询
     * @param idList 根据 es的idList 查询数据
     *@author Da.Pang
     */
    public BoolQueryBuilder getQueryBuilderAnd( Map<String,List<String>> likeMap,
                                                Map<String, Object> singleMap,
                                                Map<String, List<String>> multipleMap,
                                                Map<String, List<Long>> rangeMap,
                                                List<NestedQueryBuilder> nestedList,
                                                List<String> idList
    ) {
        if( likeMap == null && singleMap == null && multipleMap == null && nestedList == null){
            return null;
        }
        BoolQueryBuilder combinedQuery = new BoolQueryBuilder();

        if(likeMap != null){
            for (Map.Entry<String, List<String>> entry : likeMap.entrySet()) {
                for (String value : entry.getValue()) {
                    MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(entry.getKey(), value);
                    combinedQuery.must(multiMatchQueryBuilder);
                }
            }
        }
        if(singleMap != null){
            for (Map.Entry<String, Object> entry : singleMap.entrySet()) {
                TermQueryBuilder termMatch = QueryBuilders.termQuery(entry.getKey(),entry.getValue());
                combinedQuery.must(termMatch);
            }
        }

        if(multipleMap != null){
            for (Map.Entry<String, List<String>> entry : multipleMap.entrySet()) {
                TermsQueryBuilder termsMatch  =  QueryBuilders.termsQuery(entry.getKey(), entry.getValue());
                combinedQuery.must(termsMatch);
            }
        }
        if(nestedList != null){
            //嵌套精准查询 可用于多字段精准查询
            for (NestedQueryBuilder builder : nestedList) {
                combinedQuery.must(builder);
            }
        }
        if(rangeMap != null){
            for (Map.Entry<String, List<Long>> entry : rangeMap.entrySet()) {
                RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(entry.getKey());
                // 大于等于指定值
                rangeQuery.gte(entry.getValue().get(0));
                // 小于等于指定值
                rangeQuery.lte(entry.getValue().get(1));
            }
        }
        if(idList != null && !idList.isEmpty()){
            IdsQueryBuilder idsQuery = QueryBuilders.idsQuery();
            idsQuery.addIds(idList.toArray(new String[0]));
            combinedQuery.must(idsQuery);
        }
        return combinedQuery;
    }


    /**
     * [描述] 所有条件以 or 的方式进行组合
     * @param likeMap 模糊查询 一个值对应 单/多 字段 示例：{"小明":["name","nickName","detail"]}
     * @param singleMap 精准匹配 一个值对应 单 字段 示例：{"age":"18"}
     * @param multipleMap 精准匹配 一个值对应 多 字段 示例：{"180":["age","height","weight"]}
     * @param nestedList 根据 getNestedListQuery() 传参生成的嵌套查询
     *@author Da.Pang
     */
    public BoolQueryBuilder getQueryBuilderOr( Map<String,List<String>> likeMap,
                                               Map<String, Object> singleMap,
                                               Map<String, List<String>> multipleMap,
                                               List<NestedQueryBuilder> nestedList
    ) {
        if(likeMap == null && singleMap == null && multipleMap == null && nestedList == null){
            return null;
        }
        BoolQueryBuilder combinedQuery = new BoolQueryBuilder();

        if(likeMap != null){
            for (Map.Entry<String, List<String>> entry : likeMap.entrySet()) {
                for (String value : entry.getValue()) {
                    MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(entry.getKey(), value);
                    combinedQuery.should(multiMatchQueryBuilder);
                }
            }
        }
        if(singleMap != null){
            for (Map.Entry<String, Object> entry : singleMap.entrySet()) {
                TermQueryBuilder termMatch = QueryBuilders.termQuery(entry.getKey(),entry.getValue());
                combinedQuery.should(termMatch);
            }
        }

        if(multipleMap != null){
            for (Map.Entry<String, List<String>> entry : multipleMap.entrySet()) {
                TermsQueryBuilder termsMatch  =  QueryBuilders.termsQuery(entry.getKey(), entry.getValue());
                combinedQuery.should(termsMatch);
            }
        }
        if(nestedList != null){
            //嵌套精准查询 可用于多字段精准查询
            for (NestedQueryBuilder builder : nestedList) {
                combinedQuery.should(builder);
            }
        }
        return combinedQuery;
    }

    /**
     * [描述]
     * @param key 格式： key: 嵌套的path  value: 嵌套的某个字段.keyword
     * 示例： key: typeList value: typeList.code.keyword(如果该字段类型是keyword 可以去掉 .keyword, 比如类型是：Long、Integer  就不必带.keyword)
     */
    public List<NestedQueryBuilder> getNestedListQuery( Map<String,String>  key , Map<String,List<String>> value ){
        if(key == null || key.isEmpty()){
            return null;
        }
        List<NestedQueryBuilder> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : key.entrySet()) {
            List<String> valList = value.get(entry.getValue());
            NestedQueryBuilder nestedQueryBuilder2 = QueryBuilders.nestedQuery(entry.getKey(), QueryBuilders.termsQuery(entry.getValue(), valList), ScoreMode.None);
            result.add(nestedQueryBuilder2);
        }
        return result;
    }



    /**
     * [描述] 聚合操作
     * @param fieldName 指定的字段
     * @param nestedFiledName 嵌套属性中的某字段,一次只 add 两个值 示例： ["subjectList","subjectList.id"]
     * 可根据条件聚合指定字段,并回显一定的数据字段和数据列表
     * 类似： select id,name,sex  from t_user group by sex
     * @param include  示例：new String[]{"id","name", "productType.code1", "productType.name1"}
     * @param hitsSize 回显的list有多少条数据 （注意：include、hitsSize 必须同时传参 或 同时不传）
     *@author Da.Pang
     * @return name:聚合字段名 count:聚合字段值  list：参与聚合时的数据列表
     */
    public List<Map<String, Object>> group(
            String indexName, String fieldName,List<String> nestedFiledName,
            BoolQueryBuilder combinedQueryAnd,BoolQueryBuilder combinedQueryOr,
            String[] include,Integer hitsSize) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        FilterAggregationBuilder filterAgg = null;
        if(combinedQueryAnd == null && combinedQueryOr == null){
            filterAgg = AggregationBuilders.filter("filtered_agg", new BoolQueryBuilder());
        }else {
            if(combinedQueryAnd != null){
                filterAgg = AggregationBuilders.filter("filtered_agg", combinedQueryAnd);
            }
            if(combinedQueryOr != null){
                filterAgg = AggregationBuilders.filter("filtered_agg", combinedQueryOr);
            }
        }
        if(nestedFiledName != null){
            NestedAggregationBuilder nestedAgg = AggregationBuilders.nested("nested_agg", nestedFiledName.get(0));
            TermsAggregationBuilder termsAgg = AggregationBuilders.terms("agg_field").field(nestedFiledName.get(1));
            if(hitsSize != null && include != null){
                TopHitsAggregationBuilder topHitsBuilder = AggregationBuilders.topHits("top_hits");
                topHitsBuilder.size(hitsSize);
                topHitsBuilder.fetchSource(include,null);
                termsAgg.subAggregation(topHitsBuilder);
            }
            filterAgg.subAggregation(termsAgg);
            // 将术语聚合添加到嵌套聚合中
            nestedAgg.subAggregation(termsAgg);
            // 将聚合添加到搜索源构建器中
            sourceBuilder.aggregation(nestedAgg);
        }else{
            TermsAggregationBuilder termsAgg = AggregationBuilders.terms("agg_field").field(fieldName);
            if(hitsSize != null && include != null){
                TopHitsAggregationBuilder topHitsBuilder = AggregationBuilders.topHits("top_hits");
                // 聚合时，一条聚合信息对应几条数据 不设置则默认查全部数据
                topHitsBuilder.size(hitsSize);
                // 聚合时，需要显示哪些字段 不设置则查全部字段
                topHitsBuilder.fetchSource(include,null);
                termsAgg.subAggregation(topHitsBuilder);
            }
            filterAgg.subAggregation(termsAgg);
        }
        sourceBuilder.aggregation(filterAgg);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<? extends Terms.Bucket> buckets;
            if(nestedFiledName != null){
                buckets = getTermsByNested(searchResponse);
            }else{
                Filter filteredAggregation = searchResponse.getAggregations().get("filtered_agg");
                Terms yourFieldAggregation = filteredAggregation.getAggregations().get("agg_field");
                buckets = yourFieldAggregation.getBuckets();
            }
            List<Map<String,Object>> result = new ArrayList<>();
            for (Terms.Bucket bucket : buckets) {
                Map<String, Object> map = new HashMap<>(2);
                map.put("name", bucket.getKeyAsString());
                map.put("count", bucket.getDocCount());
                if(include != null && hitsSize != null){
                    TopHits topHits = bucket.getAggregations().get("top_hits");
                    List<Map<String, Object>> hitList = new ArrayList<>();
                    for (SearchHit hit : topHits.getHits()) {
                        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                        hitList.add(sourceAsMap);
                    }
                    map.put("list", hitList);
                }
                result.add(map);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("索引{}聚合出现错误{}",indexName,e.getMessage());
            throw new RuntimeException("聚合查询出现错误");
        }
    }

    /**
     * [描述]
     */
    private List<? extends Terms.Bucket> getTermsByNested(SearchResponse searchResponse ){
        // 解析聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        Nested nested = aggregations.get("nested_agg");
        Terms subjectIds = nested.getAggregations().get("agg_field");
        return subjectIds.getBuckets();
    }


    /**
     * [描述] 滚动检索 当不再需要检索时，最好调用 searchScrollClear() 对滚动ID进行清除
     * @param scrollId 滚动ID 传null则初始化滚动 翻页则传入指定值
     *@author Da.Pang
     * @return list:检索数据  scrollId：滚动ID, 每次翻页时候都需要传入
     */
    public PageResult<String> searchScroll(
            String indexName,BoolQueryBuilder combinedQueryAnd,BoolQueryBuilder combinedQueryOr,
            Map<String,SortOrder> sortOrderMap,List<String> highlightFields,
            String[] include,int pageSize,String scrollId
    ) throws IOException {
        SearchResponse searchResponse;
        if(scrollId == null){
            // 初始化搜索请求
            SearchRequest searchRequest = new SearchRequest(indexName);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            getSearchSourceBuilder(searchSourceBuilder,
                    combinedQueryAnd, combinedQueryOr, sortOrderMap,
                    highlightFields, include);
            // 设置滚动参数
            searchRequest.scroll(TimeValue.timeValueSeconds(TIME_OUT));
            searchRequest.source(searchSourceBuilder.size(pageSize));
            // 发起第一次搜索请求
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 获取滚动ID
            scrollId = searchResponse.getScrollId();
        }
        System.out.printf("scrollId： %s %n", scrollId );
        // 使用滚动ID发起下一次滚动请求
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(TimeValue.timeValueSeconds(TIME_OUT));
        // 执行滚动请求
        searchResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
        // 处理搜索结果
        List<String> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            list.add(hit.getSourceAsString());
        }
        // 更新滚动ID，准备下一次滚动
        scrollId = searchResponse.getScrollId();
       /* if(list.isEmpty()){
            // 正常情况 如果检索查不到数据 则清理滚动ID
            searchScrollClear(scrollId );
        }*/
        PageResult<String> result = new PageResult<>();
        result.setList(list);
        result.setScrollId(scrollId);
        result.setTotal(searchResponse.getHits().getTotalHits().value);
        return result;
    }


    /**
     * [描述] 清理指定 scrollId 值
     * 一般情况下，当滚动查询没有数据的时候，或不再进行查询时候，就需要清理滚动上下文，释放资源
     * 如果报错：ElasticsearchStatusException: Unable to parse response body 表示已经清理指定 scrollId 值了
     */
    public void searchScrollClear( String scrollId ) {
        try{
            // 清除滚动上下文，释放资源
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        }catch(Exception e){
            e.printStackTrace();
            log.error("清理scrollId错误，scrollId：{},message信息：{}",scrollId,e.getMessage());
        }
    }

    /**
     * [描述]通过 after_sort_values 进行翻页
     * @param sortOrderMap 由于通过 after 翻页 需要默认排序字段 所以该参数必填
     * @param lastSortValues 第一次传null  等拿到 resultSortValues 值以后传入可查到下一页数据
     *@author Da.Pang
     */
    public  PageResult<String> testSearchAfter(String indexName, BoolQueryBuilder combinedQueryAnd, BoolQueryBuilder combinedQueryOr,
                                               @NotNull Map<String,SortOrder> sortOrderMap, List<String> highlightFields,
                                               String[] include, int pageSize, Object[] lastSortValues) {
        // 初始化搜索请求
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        getSearchSourceBuilder(searchSourceBuilder,
                combinedQueryAnd, combinedQueryOr, sortOrderMap,
                highlightFields, include);
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(TIME_OUT));
        searchSourceBuilder.size(pageSize);

        if(lastSortValues != null){
            searchSourceBuilder.searchAfter(lastSortValues);
        }

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("检索报错，lastSortValues：{},message信息：{}",lastSortValues,e.getMessage());
        }
        SearchHit[] hits = searchResponse.getHits().getHits();

        List<String> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            list.add(hit.getSourceAsString());
        }
        if(hits.length == 0){
            PageResult<String> result = new PageResult<>();
            result.setTotalPage(0);
            return result;
        }
        Object[]  resultSortValues = hits[hits.length - 1].getSortValues();
        PageResult<String> result = new PageResult<>();
        result.setList(list);
        result.setTotal(searchResponse.getHits().getTotalHits().value);
        result.setLastSortValues(resultSortValues);
        return result;
    }

}
