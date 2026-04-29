package com.example.order_management_service.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.order_management_service.model.Invoice;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends Neo4jRepository<Invoice, UUID> {

    @Query("MATCH (i:Invoice) WHERE i.id = $id DETACH DELETE i")
    void deleteById(@Param("id") UUID id);

    @Query("MATCH (o:Order)-[:HAS_INVOICE]->(i:Invoice) WHERE o.id = $orderId RETURN i")
    List<Invoice> findByOrderId(@Param("orderId") UUID orderId);
}