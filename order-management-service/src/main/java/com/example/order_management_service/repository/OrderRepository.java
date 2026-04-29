package com.example.order_management_service.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.order_management_service.model.Article;
import com.example.order_management_service.model.Order;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends Neo4jRepository<Order, UUID> {

    @Query("MATCH (o:Order) WHERE o.id = $id DETACH DELETE o")
    void deleteById(@Param("id") UUID id);

    @Query("MATCH (o:Order),(a:Article) " +
            "WHERE o.id = $orderId AND a.id = $articleId " +
            "CREATE (o)-[:HAS_ARTICLE {quantity: 1}]->(a)")
    void addArticleToOrder(@Param("orderId") UUID orderId, @Param("articleId") UUID articleId);

    @Query("MATCH (o:Order)-[r:HAS_ARTICLE]->(a:Article) " +
            "WHERE o.id = $orderId AND a.id = $articleId " +
            "DELETE r")
    void removeArticleFromOrder(@Param("orderId") UUID orderId, @Param("articleId") UUID articleId);

    @Query("MATCH (o:Order),(i:Invoice) " +
            "WHERE o.id = $orderId AND i.id = $invoiceId " +
            "CREATE (o)-[:HAS_INVOICE]->(i)")
    void addInvoiceToOrder(@Param("orderId") UUID orderId, @Param("invoiceId") UUID invoiceId);


    interface OrderTypeAvgPrice { String getOrderType(); Double getAvgPrice(); }
    interface OrderStatusAvgPrice { String getStatus(); Double getAvgPrice(); }
    interface TopArticleRevenue { String getArticleName(); Double getTotalRevenue(); }
    interface StatusItemCount { String getStatus(); Long getItemCount(); }
}