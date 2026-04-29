package com.example.order_management_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID id;

    private LocalDateTime creationDate;
    private String status;
    private String orderType;
    private String address;

    @Relationship(type = "HAS_ARTICLE")
    private List<OrderArticle> articles = new ArrayList<>();

    @Relationship(type = "HAS_INVOICE")
    private Invoice invoice;
}