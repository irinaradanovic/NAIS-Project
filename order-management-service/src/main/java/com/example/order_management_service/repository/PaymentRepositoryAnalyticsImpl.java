package com.example.order_management_service.repository;
import com.example.order_management_service.dto.ArticleRevenueByPaymentMethodDto;
import com.example.order_management_service.dto.PaymentMethodRevenueDto;
import com.example.order_management_service.dto.PaymentStatusAverageDto;
import com.example.order_management_service.model.Payment;
import lombok.AllArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@Repository
public class PaymentRepositoryAnalyticsImpl implements PaymentRepositoryAnalytics {
    private final Neo4jClient neo4jClient;

    @Override
    public List<PaymentMethodRevenueDto> findRevenueByPaymentMethod(Double minAmount) {

        String query = """
            MATCH (o:Order)-[:HAS_PAYMENT]->(p:Payment)
            WHERE p.status = 'PAID' AND p.amount >= $minAmount
            WITH p.method AS method,
                 count(o) AS orderCount,
                 sum(p.amount) AS totalRevenue,
                 avg(p.amount) AS averagePayment
            RETURN method, orderCount, totalRevenue, averagePayment
            ORDER BY totalRevenue DESC
        """;

        return (List<PaymentMethodRevenueDto>) neo4jClient.query(query)
                .bind(minAmount).to("minAmount")
                .fetchAs(PaymentMethodRevenueDto.class)
                .mappedBy((ts, r) ->
                        new PaymentMethodRevenueDto(
                                r.get("method").asString(),
                                r.get("orderCount").asLong(),
                                r.get("totalRevenue").asDouble(),
                                r.get("averagePayment").asDouble()
                        ))
                .all();
    }

    @Override
    public List<PaymentStatusAverageDto> findPaidPaymentAverageByOrderStatus(Long minPayments) {

        String query = """
            MATCH (o:Order)-[:HAS_PAYMENT]->(p:Payment)
            WHERE p.status = 'PAID'
            WITH o.status AS orderStatus,
                 count(p) AS paymentCount,
                 avg(p.amount) AS averageAmount,
                 sum(p.amount) AS totalAmount
            WHERE paymentCount >= $minPayments
            RETURN orderStatus, paymentCount, averageAmount, totalAmount
            ORDER BY averageAmount DESC
        """;

        return (List<PaymentStatusAverageDto>) neo4jClient.query(query)
                .bind(minPayments).to("minPayments")
                .fetchAs(PaymentStatusAverageDto.class)
                .mappedBy((ts, r) ->
                        new PaymentStatusAverageDto(
                                r.get("orderStatus").asString(),
                                r.get("paymentCount").asLong(),
                                r.get("averageAmount").asDouble(),
                                r.get("totalAmount").asDouble()
                        ))
                .all();
    }

    @Override
    public List<ArticleRevenueByPaymentMethodDto> findArticleRevenueByPaymentMethod(String method) {

        String query = """
            MATCH (o:Order)-[item:HAS_ARTICLE]->(a:Article),
                  (o)-[:HAS_PAYMENT]->(p:Payment)
            WHERE p.method = $method AND p.status = 'PAID'
            WITH a.name AS articleName,
                 count(DISTINCT o) AS orderCount,
                 sum(a.price * item.quantity) AS articleRevenue
            RETURN articleName, orderCount, articleRevenue
            ORDER BY articleRevenue DESC
        """;

        return (List<ArticleRevenueByPaymentMethodDto>) neo4jClient.query(query)
                .bind(method).to("method")
                .fetchAs(ArticleRevenueByPaymentMethodDto.class)
                .mappedBy((ts, r) ->
                        new ArticleRevenueByPaymentMethodDto(
                                r.get("articleName").asString(),
                                r.get("orderCount").asLong(),
                                r.get("articleRevenue").asDouble()
                        ))
                .all();
    }


    @Override
    public void deleteById(UUID id) {

        neo4jClient.query("""
            MATCH (p:Payment)
            WHERE p.id = $id
            DETACH DELETE p
        """)
                .bind(id).to("id")
                .run();
    }

    @Override
    public void replacePaymentForOrder(UUID orderId, UUID paymentId) {

        neo4jClient.query("""
            MATCH (o:Order), (p:Payment)
            WHERE o.id = $orderId AND p.id = $paymentId
            OPTIONAL MATCH (o)-[oldPayment:HAS_PAYMENT]->(:Payment)
            DELETE oldPayment
            MERGE (o)-[:HAS_PAYMENT]->(p)
        """)
                .bind(orderId).to("orderId")
                .bind(paymentId).to("paymentId")
                .run();
    }

    @Override
    public Long updatePaymentStatusBeforeDate(String currentStatus,
                                              String newStatus,
                                              LocalDateTime beforeDate) {

        return neo4jClient.query("""
            MATCH (o:Order)-[:HAS_PAYMENT]->(p:Payment)
            WHERE p.status = $currentStatus
              AND p.paymentDate < $beforeDate
            SET p.status = $newStatus
            RETURN count(p) AS count
        """)
                .bind(currentStatus).to("currentStatus")
                .bind(newStatus).to("newStatus")
                .bind(beforeDate).to("beforeDate")
                .fetchAs(Long.class)
                .mappedBy((ts, r) -> r.get("count").asLong())
                .one()
                .orElse(0L);
    }
}
