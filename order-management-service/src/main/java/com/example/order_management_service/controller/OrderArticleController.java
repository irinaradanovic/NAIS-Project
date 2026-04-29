package com.example.order_management_service.controller;

import com.example.order_management_service.dto.OrderArticleRequestDto;
import com.example.order_management_service.dto.OrderArticleResponseDto;
import com.example.order_management_service.service.OrderArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-articles")
@RequiredArgsConstructor
public class OrderArticleController {

    private final OrderArticleService orderArticleService;

    @PostMapping
    public ResponseEntity<OrderArticleResponseDto> create(@RequestBody OrderArticleRequestDto dto) {
        return ResponseEntity.ok(orderArticleService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<OrderArticleResponseDto>> getAll() {
        return ResponseEntity.ok(orderArticleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderArticleResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderArticleService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderArticleResponseDto> update(
            @PathVariable Long id,
            @RequestBody OrderArticleRequestDto dto
    ) {
        return ResponseEntity.ok(orderArticleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderArticleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}