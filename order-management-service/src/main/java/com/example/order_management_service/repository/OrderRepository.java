package com.example.order_management_service.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.order_management_service.model.Order;

import java.time.LocalDateTime;
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

    @Query("MATCH (o:Order) WHERE o.id = $orderId " +
            "MERGE (m:OrderMenuItem {menuItemId: $menuItemId}) " +
            "CREATE (o)-[:HAS_MENU_ITEM {quantity: $quantity}]->(m)")
    void addMenuItemToOrder(@Param("orderId") UUID orderId,
                            @Param("menuItemId") String menuItemId,
                            @Param("quantity") Integer quantity);

    @Query("MATCH (o:Order) WHERE o.id = $orderId " +
            "SET o.status = $status RETURN o")
    Order updateStatus(@Param("orderId") UUID orderId, @Param("status") String status);

    @Query("MATCH (o:Order) WHERE o.id = $orderId " +
            "MERGE (c:Customer {id: $customerId}) " +
            "ON CREATE SET c.name = $customerId, c.email = $customerId + '@example.com', c.city = 'UNKNOWN', c.loyaltyTier = 'BRONZE' " +
            "MERGE (c)-[:PLACED_ORDER]->(o)")
    void linkCustomerToOrder(@Param("customerId") String customerId, @Param("orderId") UUID orderId);

    @Query("""
            MATCH (c:Customer)-[:PLACED_ORDER]->(o:Order)-[:HAS_INVOICE]->(i:Invoice)
            WHERE ($from IS NULL OR o.creationDate >= $from)
              AND ($to IS NULL OR o.creationDate <= $to)
            WITH c, count(o) AS orderCount, sum(i.price) AS totalRevenue, avg(i.price) AS avgOrderValue
            WHERE orderCount >= $minOrders
            RETURN c.id AS customerId,
                   c.name AS customerName,
                   c.city AS city,
                   c.loyaltyTier AS loyaltyTier,
                   orderCount AS orderCount,
                   totalRevenue AS totalRevenue,
                   avgOrderValue AS avgOrderValue
            ORDER BY totalRevenue DESC
            """)
    List<CustomerRevenueSummary> findCustomerRevenueSummary(@Param("from") LocalDateTime from,
                                                            @Param("to") LocalDateTime to,
                                                            @Param("minOrders") Long minOrders);

    @Query("""
            MATCH (o:Order)-[r:HAS_ARTICLE]->(a:Article)
            WHERE o.status = $status
              AND ($from IS NULL OR o.creationDate >= $from)
              AND ($to IS NULL OR o.creationDate <= $to)
            WITH a, sum(r.quantity) AS soldQuantity, sum(r.quantity * a.price) AS revenue, count(DISTINCT o) AS orderCount
            WHERE soldQuantity >= $minQuantity
            RETURN toString(a.id) AS articleId,
                   a.name AS articleName,
                   soldQuantity AS soldQuantity,
                   revenue AS revenue,
                   orderCount AS orderCount
            ORDER BY revenue DESC
            """)
    List<ArticleRevenueByStatus> findArticleRevenueByStatus(@Param("status") String status,
                                                            @Param("from") LocalDateTime from,
                                                            @Param("to") LocalDateTime to,
                                                            @Param("minQuantity") Long minQuantity);

    @Query("""
            MATCH (c:Customer)-[:PLACED_ORDER]->(o:Order)-[:HAS_INVOICE]->(i:Invoice)
            WHERE ($from IS NULL OR o.creationDate >= $from)
              AND ($to IS NULL OR o.creationDate <= $to)
            WITH o.status AS status,
                 count(DISTINCT c) AS customerCount,
                 count(o) AS orderCount,
                 sum(i.price) AS totalRevenue,
                 avg(i.price) AS avgOrderValue
            WHERE orderCount >= $minOrders
            RETURN status AS status,
                   customerCount AS customerCount,
                   orderCount AS orderCount,
                   totalRevenue AS totalRevenue,
                   avgOrderValue AS avgOrderValue
            ORDER BY totalRevenue DESC
            """)
    List<StatusRevenueSummary> findStatusRevenueSummary(@Param("from") LocalDateTime from,
                                                        @Param("to") LocalDateTime to,
                                                        @Param("minOrders") Long minOrders);

    @Query("""
            MATCH (o:Order)-[r:HAS_ARTICLE]->(a:Article)
            WHERE o.id = $orderId AND a.id = $articleId
            WITH o, a, r, r.quantity AS oldQuantity, r.quantity + $increment AS newQuantity
            SET r.quantity = newQuantity
            RETURN toString(o.id) AS orderId,
                   toString(a.id) AS articleId,
                   a.name AS articleName,
                   oldQuantity AS oldQuantity,
                   newQuantity AS newQuantity
            """)
    List<ArticleQuantityUpdateResult> increaseArticleQuantityIfExists(@Param("orderId") UUID orderId,
                                                                      @Param("articleId") UUID articleId,
                                                                      @Param("increment") Integer increment);

    @Query("""
            MATCH (c:Customer)-[:PLACED_ORDER]->(o:Order)-[:HAS_INVOICE]->(i:Invoice)
            WHERE c.id = $customerId
              AND o.status = 'COMPLETED'
              AND ($from IS NULL OR o.creationDate >= $from)
              AND ($to IS NULL OR o.creationDate <= $to)
            WITH c, sum(i.price) AS totalRevenue, count(o) AS orderCount
            SET c.loyaltyTier = CASE
                WHEN totalRevenue >= 5000 OR orderCount >= 5 THEN 'GOLD'
                WHEN totalRevenue >= 2500 OR orderCount >= 3 THEN 'SILVER'
                ELSE 'BRONZE'
            END
            RETURN c.id AS customerId,
                   c.name AS customerName,
                   totalRevenue AS totalRevenue,
                   orderCount AS orderCount,
                   c.loyaltyTier AS loyaltyTier
            """)
    List<CustomerLoyaltyUpdateResult> recalculateCustomerLoyaltyTier(@Param("customerId") String customerId,
                                                                     @Param("from") LocalDateTime from,
                                                                     @Param("to") LocalDateTime to);

    @Query("""
            MATCH (target:Customer)-[:PLACED_ORDER]->(:Order)-[:HAS_ARTICLE]->(owned:Article)
            WHERE target.id = $customerId
            WITH target, collect(DISTINCT owned.id) AS ownedArticleIds
            MATCH (target)-[:PLACED_ORDER]->(:Order)-[:HAS_ARTICLE]->(shared:Article)<-[:HAS_ARTICLE]-(:Order)<-[:PLACED_ORDER]-(similar:Customer)
            WHERE similar.id <> target.id
            WITH target, ownedArticleIds, similar
            MATCH (similar)-[:PLACED_ORDER]->(o:Order)-[r:HAS_ARTICLE]->(candidate:Article)
            WHERE ($from IS NULL OR o.creationDate >= $from)
              AND ($to IS NULL OR o.creationDate <= $to)
              AND NOT candidate.id IN ownedArticleIds
            WITH candidate,
                 count(DISTINCT similar) AS similarCustomerCount,
                 sum(r.quantity) AS totalRecommendedQuantity
            WHERE similarCustomerCount >= $minSharedPurchases
            RETURN toString(candidate.id) AS articleId,
                   candidate.name AS articleName,
                   similarCustomerCount AS similarCustomerCount,
                   totalRecommendedQuantity AS totalRecommendedQuantity,
                   similarCustomerCount * 10 + totalRecommendedQuantity AS score
            ORDER BY score DESC
            """)
    List<RecommendedArticle> findRecommendedArticlesForCustomer(@Param("customerId") String customerId,
                                                                @Param("from") LocalDateTime from,
                                                                @Param("to") LocalDateTime to,
                                                                @Param("minSharedPurchases") Long minSharedPurchases);

    @Query("""
            MATCH (o:Order)-[r:HAS_ARTICLE]->(a:Article)
            MATCH (o)-[:HAS_INVOICE]->(i:Invoice)
            WHERE ($from IS NULL OR o.creationDate >= $from)
              AND ($to IS NULL OR o.creationDate <= $to)
            WITH o, i, sum(r.quantity * a.price) AS calculatedTotal, count(a) AS articleCount
            WHERE abs(calculatedTotal - i.price) > $tolerance
            RETURN toString(o.id) AS orderId,
                   o.status AS status,
                   i.price AS invoicePrice,
                   calculatedTotal AS calculatedTotal,
                   abs(calculatedTotal - i.price) AS difference,
                   articleCount AS articleCount
            ORDER BY difference DESC
            """)
    List<InvoiceMismatch> findInvoiceMismatchOrders(@Param("from") LocalDateTime from,
                                                    @Param("to") LocalDateTime to,
                                                    @Param("tolerance") Double tolerance);

    interface CustomerRevenueSummary {
        String getCustomerId();
        String getCustomerName();
        String getCity();
        String getLoyaltyTier();
        Long getOrderCount();
        Double getTotalRevenue();
        Double getAvgOrderValue();
    }

    interface ArticleRevenueByStatus {
        String getArticleId();
        String getArticleName();
        Long getSoldQuantity();
        Double getRevenue();
        Long getOrderCount();
    }

    interface StatusRevenueSummary {
        String getStatus();
        Long getCustomerCount();
        Long getOrderCount();
        Double getTotalRevenue();
        Double getAvgOrderValue();
    }

    interface ArticleQuantityUpdateResult {
        String getOrderId();
        String getArticleId();
        String getArticleName();
        Integer getOldQuantity();
        Integer getNewQuantity();
    }

    interface CustomerLoyaltyUpdateResult {
        String getCustomerId();
        String getCustomerName();
        Double getTotalRevenue();
        Long getOrderCount();
        String getLoyaltyTier();
    }

    interface RecommendedArticle {
        String getArticleId();
        String getArticleName();
        Long getSimilarCustomerCount();
        Long getTotalRecommendedQuantity();
        Long getScore();
    }

    interface InvoiceMismatch {
        String getOrderId();
        String getStatus();
        Double getInvoicePrice();
        Double getCalculatedTotal();
        Double getDifference();
        Long getArticleCount();
    }
}
