package com.example.order_management_service.controller;

import com.example.order_management_service.dto.ArticleRequestDTO;
import com.example.order_management_service.dto.ArticleResponseDTO;
import com.example.order_management_service.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ArticleResponseDTO> create(@RequestBody ArticleRequestDTO dto) {
        return ResponseEntity.ok(articleService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ArticleResponseDTO>> getAll() {
        return ResponseEntity.ok(articleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(articleService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody ArticleRequestDTO dto
    ) {
        return ResponseEntity.ok(articleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}