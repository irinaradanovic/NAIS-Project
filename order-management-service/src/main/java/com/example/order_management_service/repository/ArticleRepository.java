package com.example.order_management_service.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.order_management_service.model.Article;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleRepository extends Neo4jRepository<Article, UUID> {
    
    @Query("MATCH (o:Order)-[:HAS_ARTICLE]->(a:Article) WHERE o.id = $orderId RETURN a")
    List<Article> findByOrderId(@Param("orderId") UUID orderId);
}