package com.example.order_management_service.service;

import com.example.order_management_service.dto.OrderRequestDto;
import com.example.order_management_service.dto.OrderResponseDto;
import com.example.order_management_service.model.Article;
import com.example.order_management_service.model.Invoice;
import com.example.order_management_service.model.Order;
import com.example.order_management_service.repository.ArticleRepository;
import com.example.order_management_service.repository.InvoiceRepository;
import com.example.order_management_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private ArticleRepository articleRepository;
    @Autowired private InvoiceRepository invoiceRepository;

    public OrderResponseDto create(OrderRequestDto dto) {
        Order order = new Order();
        order.setCreationDate(dto.getCreationDate());
        order.setStatus(dto.getStatus());
        order.setOrderType(dto.getOrderType());
        order.setAddress(dto.getAddress());

        Order saved = orderRepository.save(order);
        return mapToDTO(saved);
    }

    public List<OrderResponseDto> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDto getById(UUID id) {
        return mapToDTO(getEntityById(id));
    }

    public OrderResponseDto update(UUID id, OrderRequestDto dto) {
        Order existing = getEntityById(id);
        if (dto.getCreationDate() != null) existing.setCreationDate(dto.getCreationDate());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getOrderType() != null) existing.setOrderType(dto.getOrderType());
        if (dto.getAddress() != null) existing.setAddress(dto.getAddress());

        Order saved = orderRepository.save(existing);
        return mapToDTO(saved);
    }

    public void delete(UUID id) {
        orderRepository.deleteById(id);
    }

    public void addArticle(UUID orderId, UUID articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        articleRepository.save(article);
        orderRepository.addArticleToOrder(orderId, articleId);
    }

    public void removeArticle(UUID orderId, UUID articleId) {
        orderRepository.removeArticleFromOrder(orderId, articleId);
    }

    public List<Article> getArticles(UUID orderId) {
        return articleRepository.findByOrderId(orderId);
    }

    public void addInvoice(UUID orderId, UUID invoiceId) {
        invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        orderRepository.addInvoiceToOrder(orderId, invoiceId);
    }

    public List<Invoice> getInvoices(UUID orderId) {
        return invoiceRepository.findByOrderId(orderId);
    }



    private Order getEntityById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    private OrderResponseDto mapToDTO(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getCreationDate(),
                order.getStatus(),
                order.getOrderType(),
                order.getAddress()
        );
    }
}