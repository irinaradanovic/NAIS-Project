package com.example.order_management_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID id;

    private Double price;
    private LocalDate issueDate;

    @Relationship(type = "FOR_ORDER", direction = Relationship.Direction.INCOMING)
    @JsonIgnore
    private Order order;
}