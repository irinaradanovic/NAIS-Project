package com.example.order_management_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

import java.util.UUID;

@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderArticle {

    @Id
    @GeneratedValue
    private Long id;

    private Integer quantity;

    @TargetNode
    private Article article;
}