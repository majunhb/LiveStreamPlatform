package com.livestream.video.service.impl;

import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;
import com.livestream.video.entity.ShortVideo;
import com.livestream.video.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest as SpringPageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐Feed服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final MongoTemplate mongoTemplate;

    @Override
    public PageResult<ShortVideo> getRecommendFeed(PageRequest request) {
        Query query = new Query(Criteria.where("status").is(1).and("audit_status").is(1));
        query.with(Sort.by(Sort.Direction.DESC, "like_count", "play_count", "create_time"));
        query.with(SpringPageRequest.of(request.getPageNum() - 1, request.getPageSize()));
        query.fields().exclude("description");
        
        List<ShortVideo> videos = mongoTemplate.find(query, ShortVideo.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), ShortVideo.class);
        
        return PageResult.of(videos, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public PageResult<ShortVideo> getFollowFeed(Long userId, PageRequest request) {
        // 实际需要查询关注列表，这里简化处理
        Query query = new Query(Criteria.where("user_id").in(new ArrayList<Long>()).and("status").is(1));
        query.with(Sort.by(Sort.Direction.DESC, "create_time"));
        query.with(SpringPageRequest.of(request.getPageNum() - 1, request.getPageSize()));
        
        List<ShortVideo> videos = mongoTemplate.find(query, ShortVideo.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), ShortVideo.class);
        
        return PageResult.of(videos, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public PageResult<ShortVideo> getHotVideos(PageRequest request) {
        Query query = new Query(Criteria.where("status").is(1).and("audit_status").is(1));
        query.with(Sort.by(Sort.Direction.DESC, "play_count"));
        query.with(SpringPageRequest.of(request.getPageNum() - 1, request.getPageSize()));
        
        List<ShortVideo> videos = mongoTemplate.find(query, ShortVideo.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), ShortVideo.class);
        
        return PageResult.of(videos, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public PageResult<ShortVideo> searchVideos(String keyword, PageRequest request) {
        Query query = new Query(
                new Criteria().orOperator(
                        Criteria.where("title").regex(keyword),
                        Criteria.where("description").regex(keyword),
                        Criteria.where("tags").regex(keyword)
                ).and("status").is(1).and("audit_status").is(1)
        );
        query.with(Sort.by(Sort.Direction.DESC, "create_time"));
        query.with(SpringPageRequest.of(request.getPageNum() - 1, request.getPageSize()));
        
        List<ShortVideo> videos = mongoTemplate.find(query, ShortVideo.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), ShortVideo.class);
        
        return PageResult.of(videos, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public List<ShortVideo> getVideosByCategory(Long categoryId, PageRequest request) {
        Query query = new Query(Criteria.where("category_id").is(categoryId)
                .and("status").is(1).and("audit_status").is(1));
        query.with(Sort.by(Sort.Direction.DESC, "create_time"));
        query.with(SpringPageRequest.of(request.getPageNum() - 1, request.getPageSize()));
        query.fields().exclude("description");
        
        return mongoTemplate.find(query, ShortVideo.class);
    }
}
