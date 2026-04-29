package com.example.order_management_service.repository;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderArticleRepository {

    private final Neo4jClient neo4jClient;

    public OrderArticleRepository(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public OrderArticleRecord create(UUID orderId, UUID articleId, Integer quantity) {
        return neo4jClient.query(
                        "MATCH (o:Order),(a:Article) " +
                        "WHERE o.id = $orderId AND a.id = $articleId " +
                        "CREATE (o)-[r:HAS_ARTICLE {quantity: $quantity}]->(a) " +
                        "RETURN id(r) AS id, r.quantity AS quantity, o.id AS orderId, a.id AS articleId")
                .bind(orderId.toString()).to("orderId")
                .bind(articleId.toString()).to("articleId")
                .bind(quantity).to("quantity")
                .fetchAs(OrderArticleRecord.class)
                .mappedBy((typeSystem, record) -> new OrderArticleRecordImpl(
                        record.get("id").asLong(),
                        UUID.fromString(record.get("orderId").asString()),
                        UUID.fromString(record.get("articleId").asString()),
                        record.get("quantity").asInt()
                ))
                .one()
                .orElseThrow(() -> new RuntimeException("OrderArticle not created"));
    }

    public Optional<OrderArticleRecord> findById(Long id) {
        return neo4jClient.query(
                        "MATCH (o:Order)-[r:HAS_ARTICLE]->(a:Article) " +
                        "WHERE id(r) = $id " +
                        "RETURN id(r) AS id, r.quantity AS quantity, o.id AS orderId, a.id AS articleId")
                .bind(id).to("id")
                .fetchAs(OrderArticleRecord.class)
                .mappedBy((typeSystem, record) -> new OrderArticleRecordImpl(
                        record.get("id").asLong(),
                        UUID.fromString(record.get("orderId").asString()),
                        UUID.fromString(record.get("articleId").asString()),
                        record.get("quantity").asInt()
                ))
                .one();
    }

    public List<OrderArticleRecord> findAll() {
        return new ArrayList<>(neo4jClient.query(
                        "MATCH (o:Order)-[r:HAS_ARTICLE]->(a:Article) " +
                        "RETURN id(r) AS id, r.quantity AS quantity, o.id AS orderId, a.id AS articleId")
                .fetchAs(OrderArticleRecord.class)
                .mappedBy((typeSystem, record) -> new OrderArticleRecordImpl(
                        record.get("id").asLong(),
                        UUID.fromString(record.get("orderId").asString()),
                        UUID.fromString(record.get("articleId").asString()),
                        record.get("quantity").asInt()
                ))
                .all());
    }

    public OrderArticleRecord updateQuantity(Long id, Integer quantity) {
        return neo4jClient.query(
                        "MATCH (o:Order)-[r:HAS_ARTICLE]->(a:Article) " +
                        "WHERE id(r) = $id " +
                        "SET r.quantity = $quantity " +
                        "RETURN id(r) AS id, r.quantity AS quantity, o.id AS orderId, a.id AS articleId")
                .bind(id).to("id")
                .bind(quantity).to("quantity")
                .fetchAs(OrderArticleRecord.class)
                .mappedBy((typeSystem, record) -> new OrderArticleRecordImpl(
                        record.get("id").asLong(),
                        UUID.fromString(record.get("orderId").asString()),
                        UUID.fromString(record.get("articleId").asString()),
                        record.get("quantity").asInt()
                ))
                .one()
                .orElseThrow(() -> new RuntimeException("OrderArticle not found"));
    }

    public void deleteById(Long id) {
        neo4jClient.query("MATCH ()-[r:HAS_ARTICLE]->() WHERE id(r) = $id DELETE r")
                .bind(id).to("id")
                .run();
    }

    public interface OrderArticleRecord {
        Long getId();
        UUID getOrderId();
        UUID getArticleId();
        Integer getQuantity();
    }

    private static class OrderArticleRecordImpl implements OrderArticleRecord {
        private final Long id;
        private final UUID orderId;
        private final UUID articleId;
        private final Integer quantity;

        private OrderArticleRecordImpl(Long id, UUID orderId, UUID articleId, Integer quantity) {
            this.id = id;
            this.orderId = orderId;
            this.articleId = articleId;
            this.quantity = quantity;
        }

        @Override public Long getId() { return id; }
        @Override public UUID getOrderId() { return orderId; }
        @Override public UUID getArticleId() { return articleId; }
        @Override public Integer getQuantity() { return quantity; }
    }
}