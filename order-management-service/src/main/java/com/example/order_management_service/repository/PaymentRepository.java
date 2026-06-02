package com.example.order_management_service.repository;

import com.example.order_management_service.model.Payment;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends Neo4jRepository<Payment, UUID> {
    

    @Query("MATCH (p:Payment) WHERE p.id = $id DETACH DELETE p")
    void deleteById(@Param("id") UUID id);

    @Query("MATCH (o:Order)-[:HAS_PAYMENT]->(p:Payment) WHERE o.id = $orderId RETURN p")
    List<Payment> findByOrderId(@Param("orderId") UUID orderId);

    @Query("MATCH (o:Order), (p:Payment) " +
            "WHERE o.id = $orderId AND p.id = $paymentId " +
            "WITH o, p " +
            "OPTIONAL MATCH (o)-[oldPayment:HAS_PAYMENT]->(:Payment) " +
            "DELETE oldPayment " +
            "MERGE (o)-[:HAS_PAYMENT]->(p)")
    void replacePaymentForOrder(@Param("orderId") UUID orderId, @Param("paymentId") UUID paymentId);

    @Query("MATCH (o:Order)-[:HAS_PAYMENT]->(p:Payment) " +
            "WHERE p.status = $currentStatus AND p.paymentDate < $beforeDate " +
            "WITH p " +
            "SET p.status = $newStatus " +
            "RETURN count(p)")
    Long updatePaymentStatusBeforeDate(@Param("currentStatus") String currentStatus,
                                       @Param("newStatus") String newStatus,
                                       @Param("beforeDate") LocalDateTime beforeDate);

    @Query("MATCH (o:Order)-[:HAS_PAYMENT]->(p:Payment) " +
            "WHERE p.status = 'PAID' AND p.amount >= $minAmount " +
            "WITH p.method AS method, count(o) AS orderCount, sum(p.amount) AS totalRevenue, avg(p.amount) AS averagePayment " +
            "RETURN method, orderCount, totalRevenue, averagePayment " +
            "ORDER BY totalRevenue DESC")
    List<PaymentMethodRevenueView> findRevenueByPaymentMethod(@Param("minAmount") Double minAmount);

    @Query("MATCH (o:Order)-[:HAS_PAYMENT]->(p:Payment) " +
            "WHERE p.status = 'PAID' " +
            "WITH o.status AS orderStatus, count(p) AS paymentCount, avg(p.amount) AS averageAmount, sum(p.amount) AS totalAmount " +
            "WHERE paymentCount >= $minPayments " +
            "RETURN orderStatus, paymentCount, averageAmount, totalAmount " +
            "ORDER BY averageAmount DESC")
    List<PaymentStatusAverageView> findPaidPaymentAverageByOrderStatus(@Param("minPayments") Long minPayments);

    @Query("MATCH (o:Order)-[item:HAS_ARTICLE]->(a:Article), (o)-[:HAS_PAYMENT]->(p:Payment) " +
            "WHERE p.method = $method AND p.status = 'PAID' " +
            "WITH a.name AS articleName, count(DISTINCT o) AS orderCount, sum(a.price * item.quantity) AS articleRevenue " +
            "RETURN articleName, orderCount, articleRevenue " +
            "ORDER BY articleRevenue DESC")
    List<ArticleRevenueByPaymentMethodView> findArticleRevenueByPaymentMethod(@Param("method") String method);

    interface PaymentMethodRevenueView {
        String getMethod();
        Long getOrderCount();
        Double getTotalRevenue();
        Double getAveragePayment();
    }

    interface PaymentStatusAverageView {
        String getOrderStatus();
        Long getPaymentCount();
        Double getAverageAmount();
        Double getTotalAmount();
    }

    interface ArticleRevenueByPaymentMethodView {
        String getArticleName();
        Long getOrderCount();
        Double getArticleRevenue();
    }
}
