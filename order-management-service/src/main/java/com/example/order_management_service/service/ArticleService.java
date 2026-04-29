package com.example.order_management_service.service;

import com.example.order_management_service.dto.ArticleRequestDTO;
import com.example.order_management_service.dto.ArticleResponseDTO;
import com.example.order_management_service.model.Article;
import com.example.order_management_service.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleResponseDTO create(ArticleRequestDTO dto) {
        Article article = new Article();
        article.setName(dto.getName());
        article.setPrice(dto.getPrice());
        article.setIsAvailable(dto.getIsAvailable());

        Article saved = articleRepository.save(article);

        return mapToDTO(saved);
    }

    public List<ArticleResponseDTO> getAll() {
        return articleRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ArticleResponseDTO getById(UUID id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        return mapToDTO(article);
    }

    public ArticleResponseDTO update(UUID id, ArticleRequestDTO dto) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        article.setName(dto.getName());
        article.setPrice(dto.getPrice());
        article.setIsAvailable(dto.getIsAvailable());

        Article updated = articleRepository.save(article);

        return mapToDTO(updated);
    }

    public void delete(UUID id) {
        articleRepository.deleteById(id);
    }

    private ArticleResponseDTO mapToDTO(Article article) {
        return new ArticleResponseDTO(
                article.getId(),
                article.getName(),
                article.getPrice(),
                article.getIsAvailable()
        );
    }
}