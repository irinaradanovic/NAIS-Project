package com.example.order_management_service.controller;

import com.example.order_management_service.dto.OrderRequestDto;
import com.example.order_management_service.dto.OrderResponseDto;
import com.example.order_management_service.model.Article;
import com.example.order_management_service.model.Invoice;
import com.example.order_management_service.repository.OrderRepository;
import com.example.order_management_service.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    public ResponseEntity<OrderResponseDto> create(@RequestBody OrderRequestDto dto) {
        return ResponseEntity.ok(orderService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> update(@PathVariable UUID id, @RequestBody OrderRequestDto dto) {
        return ResponseEntity.ok(orderService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{orderId}/articles/{articleId}")
    public ResponseEntity<Void> addArticle(@PathVariable UUID orderId, @PathVariable UUID articleId) {
        orderService.addArticle(orderId, articleId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{orderId}/articles")
    public ResponseEntity<List<Article>> getArticles(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getArticles(orderId));
    }

    @DeleteMapping("/{orderId}/articles/{articleId}")
    public ResponseEntity<Void> removeArticle(@PathVariable UUID orderId, @PathVariable UUID articleId) {
        orderService.removeArticle(orderId, articleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{orderId}/invoices/{invoiceId}")
    public ResponseEntity<Void> addInvoice(@PathVariable UUID orderId, @PathVariable UUID invoiceId) {
        orderService.addInvoice(orderId, invoiceId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{orderId}/invoices")
    public ResponseEntity<List<Invoice>> getInvoices(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getInvoices(orderId));
    }

}